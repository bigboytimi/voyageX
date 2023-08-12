package com.bigboytimi.authenticationservice.security.config.userdetails;

import com.bigboytimi.authenticationservice.exception.UserNotFoundException;
import com.bigboytimi.authenticationservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findByEmail(username)
               .map(CustomUserDetails::new)
               .orElseThrow(()-> new UserNotFoundException("User not found"));


    }
}
