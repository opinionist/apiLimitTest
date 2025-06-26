package example.apilimittest.global.config;

import example.apilimittest.global.entity.UserDetails;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofSeconds(30))))
                .build();
    }

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain chain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) {
            chain.doFilter(request,response);
            return;
        }

        Object principal = auth.getPrincipal();
        String username, uid;
        if(principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
            uid = userDetails.getUId();
            logger.info("Username: {}", username);
            logger.info("User ID : {}", uid);
        }

        else {
            logger.warn("Principal is not an instance of UserDetails: {}", principal);
            response.setStatus(401);
            response.getWriter().write("Unauthorized");
            return;
        }


        Bucket bucket = buckets.computeIfAbsent(uid, k -> newBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        }
        else {
            logger.warn("Rate limit exceeded for user: {}", username);
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }
}
