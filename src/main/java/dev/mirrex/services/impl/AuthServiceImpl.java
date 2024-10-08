package dev.mirrex.services.impl;

import dev.mirrex.security.JwtTokenProvider;
import dev.mirrex.dto.request.AuthRequest;
import dev.mirrex.dto.response.LoginUserResponse;
import dev.mirrex.dto.request.RegisterUserRequest;
import dev.mirrex.dto.response.baseResponse.CustomSuccessResponse;
import dev.mirrex.exceptionHandlers.CustomException;
import dev.mirrex.mappers.UserMapper;
import dev.mirrex.entities.User;
import dev.mirrex.repositories.UserRepository;
import dev.mirrex.services.AuthService;
import dev.mirrex.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    @Override
    public CustomSuccessResponse<LoginUserResponse> registerUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(registerUserRequest);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));

        User savedUser = userRepository.save(user);

        LoginUserResponse loginUserResponse = userMapper.toLoginUserDto(savedUser);
        loginUserResponse.setToken(jwtTokenProvider.generateToken(savedUser.getEmail()));

        return new CustomSuccessResponse<>(loginUserResponse);
    }

    @Override
    public CustomSuccessResponse<LoginUserResponse> loginUser(AuthRequest authDto) {
        User user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_VALID);
        }

        LoginUserResponse loginUserDto = userMapper.toLoginUserDto(user);
        loginUserDto.setToken(jwtTokenProvider.generateToken(user.getEmail()));

        return new CustomSuccessResponse<>(loginUserDto);
    }
}
