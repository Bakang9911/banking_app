package com.bankingapp.controller;
// SCRUM-3: Starting User Login API implementation
import com.bankingapp.dto.RegisterRequest;
import com.bankingapp.dto.UserResponse;
import com.bankingapp.entity.User;
import com.bankingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest request) {

        User user = userService.createUser(
                request.getName(),
                request.getSurname(),
                request.getNationalId(),
                request.getCellNumber(),
                request.getUsername(),
                request.getPassword()
        );

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}