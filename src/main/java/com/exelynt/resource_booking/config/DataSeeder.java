package com.exelynt.resource_booking.config;

import com.exelynt.resource_booking.entity.Role;
import com.exelynt.resource_booking.entity.User;
import com.exelynt.resource_booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(new User("user", passwordEncoder.encode("user123"), Role.ROLE_USER));
        }
    }
}