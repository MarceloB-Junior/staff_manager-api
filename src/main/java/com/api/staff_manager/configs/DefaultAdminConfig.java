package com.api.staff_manager.configs;

import com.api.staff_manager.enums.RoleEnum;
import com.api.staff_manager.models.UserModel;
import com.api.staff_manager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class DefaultAdminConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (!userRepository.existsByEmail("john.doe@example.com")){
                var admin = new UserModel();
                admin.setName("John Doe");
                admin.setPassword(passwordEncoder.encode("pwd123"));
                admin.setEmail("john.doe@example.com");
                admin.setRole(RoleEnum.ADMIN);

                var savedAdmin = userRepository.save(admin);
                log.info("Default admin account created with email: {}", savedAdmin.getEmail());
            }
        } catch (Exception e){
            log.error("Error occurred while creating default admin account {}", e.getMessage());
        }
    }
}
