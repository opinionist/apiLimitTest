package example.apilimittest.global.config;

import example.apilimittest.global.entity.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpikeArrestFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(SpikeArrestFilter.class);

    private final Map<String , Long> lastRequestTimeMap = new ConcurrentHashMap<>();

    private static final long MIN_INTERVAL_MS = 2000;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain chain) throws ServletException , IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()){
            chain.doFilter(request, response);
        }

        Object principal = Objects.requireNonNull(auth).getPrincipal();
        String uid;

        if(principal instanceof UserDetails userDetails){
            uid = userDetails.getUId();
            logger.info("SpikeArrestFilter : doFilterInternal() - User ID: {}", uid);
        } else {
            logger.warn("SpikeArrestFilter : doFilterInternal() - Principal is not an instance of UserDetails: {}", principal);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }
        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = lastRequestTimeMap.get(uid);
        if (lastRequestTime != null && (currentTime - lastRequestTime < MIN_INTERVAL_MS)) {
            logger.warn("SpikeArrestFilter : doFilterInternal() - Too many requests from user ID: {}", uid);
            response.setStatus(492);
            response.getWriter().write("Too many requests");
            return;
        }
        lastRequestTimeMap.put(uid, currentTime);
        logger.info("SpikeArrestFilter : doFilterInternal() - Request allowed for user ID: {}", uid);
        chain.doFilter(request, response);
    }
}
