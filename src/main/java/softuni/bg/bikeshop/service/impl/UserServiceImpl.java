package softuni.bg.bikeshop.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import softuni.bg.bikeshop.models.Role;
import softuni.bg.bikeshop.models.User;
import softuni.bg.bikeshop.models.UserRole;
import softuni.bg.bikeshop.models.dto.UserRegisterDto;
import softuni.bg.bikeshop.repository.RoleRepository;
import softuni.bg.bikeshop.repository.UserRepository;
import softuni.bg.bikeshop.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public void initFirstUserAsAdmin() {
        if(userRepository.count() > 0){
            return;
        }

        User firstUser = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(UserRole.ADMIN));

        firstUser.setUsername("admin");
        firstUser.setPassword(passwordEncoder.encode("admin"));
        firstUser.setAge(35);
        firstUser.setEmail("admin@example.com");
        firstUser.setFullName("admin adminov");
        firstUser.setRoles(roles);

        userRepository.saveAndFlush(firstUser);
    }

    @Override
    public boolean register(UserRegisterDto userRegisterDto) {
        if(!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())){
            return false;
        }
        Optional<User> byUsername = userRepository.findByUsername(userRegisterDto.getUsername());
        if(byUsername.isPresent()){
            return false;
        }
        Optional<User> byEmail = userRepository.findByEmail(userRegisterDto.getEmail());
        if (byEmail.isPresent()){
            return false;
        }
        User user = modelMapper.map(userRegisterDto,User.class);
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        Role role = roleRepository.findByName(UserRole.USER);
        user.getRoles().add(role);

        userRepository.saveAndFlush(user);
        return true;
    }
}
