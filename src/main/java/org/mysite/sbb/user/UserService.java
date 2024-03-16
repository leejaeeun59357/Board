package org.mysite.sbb.user;

import lombok.RequiredArgsConstructor;
import org.mysite.sbb.exception.DataNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));

        return siteUser;
    }
}
