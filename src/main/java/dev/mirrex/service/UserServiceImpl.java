package dev.mirrex.service;

import dev.mirrex.config.JwtTokenProvider;
import dev.mirrex.dto.response.CustomSuccessResponse;
import dev.mirrex.dto.request.LoginUserDtoRequest;
import dev.mirrex.dto.request.RegisterUserDtoRequest;
import dev.mirrex.exception.CustomException;
import dev.mirrex.mapper.UserMapper;
import dev.mirrex.model.User;
import dev.mirrex.repository.UserRepository;
import dev.mirrex.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    @Override
    public CustomSuccessResponse<LoginUserDtoRequest> registerUser(RegisterUserDtoRequest registerUserDtoRequest) {
        if (userRepository.existsByEmail(registerUserDtoRequest.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(registerUserDtoRequest);
        user.setPassword(passwordEncoder.encode(registerUserDtoRequest.getPassword()));

        User savedUser = userRepository.save(user);
        LoginUserDtoRequest loginUserDtoRequest = userMapper.toLoginUserDto(savedUser);
        loginUserDtoRequest.setToken(jwtTokenProvider.generateToken(savedUser.getEmail()));

        return new CustomSuccessResponse<>(loginUserDtoRequest);
    }
}
