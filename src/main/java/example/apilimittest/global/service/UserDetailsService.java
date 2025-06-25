package example.apilimittest.global.service;

import example.apilimittest.global.entity.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    UserDetails loadUserByUserID(String userId) throws UsernameNotFoundException;
}
