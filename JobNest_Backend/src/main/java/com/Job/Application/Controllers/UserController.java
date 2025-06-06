package com.Job.Application.Controllers;

import com.Job.Application.Mappers.UserMapper;
import com.Job.Application.Model.User;
import com.Job.Application.Response.UserResponse;
import com.Job.Application.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Get the current user profile (available to any authenticated user)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    /**
     * Update the current user's profile (available to any authenticated user)
     */
    @PutMapping("/me")
    public ResponseEntity<Void> updateCurrentUser(@RequestBody User userUpdate) {
        User currentUser = userService.getCurrentUser();

        // Only update fields that can be modified by the user
        if (userUpdate.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(userUpdate.getPhoneNumber());
        }

        // Update job seeker specific fields if the user is a job seeker
        if (currentUser.isJobSeeker()) {
            if (userUpdate.getResume() != null) {
                currentUser.setResume(userUpdate.getResume());
            }
            if (userUpdate.getSkills() != null) {
                currentUser.setSkills(userUpdate.getSkills());
            }
            if (userUpdate.getExperience() != null) {
                currentUser.setExperience(userUpdate.getExperience());
            }
        }

        // Update recruiter specific fields if the user is a recruiter
        if (currentUser.isRecruiter()) {
            if (userUpdate.getPosition() != null) {
                currentUser.setPosition(userUpdate.getPosition());
            }
            // Company association should be handled separately
        }
        
        userService.updateUser(currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all users (available only to administrators)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Get job seekers (available only to recruiters and administrators)
     */
    @GetMapping("/job-seekers")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getJobSeekers() {
        List<UserResponse> jobSeekers = userService.getAllUsers().stream()
                .filter(User::isJobSeeker)
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobSeekers);
    }

    /**
     * Get recruiters (available only to administrators)
     */
    @GetMapping("/recruiters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getRecruiters() {
        List<UserResponse> recruiters = userService.getAllUsers().stream()
                .filter(User::isRecruiter)
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recruiters);
    }

    /**
     * Associate a recruiter with a company (available only to recruiters)
     */
    @PutMapping("/recruiters/company/{companyId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> associateRecruiterWithCompany(@PathVariable Long companyId) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isRecruiter()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can be associated with companies");
        }
        
        // Here you would look up the company and associate it with the recruiter
        // This is just a placeholder for the actual implementation
        // Companies company = companyService.getCompanyById(companyId);
        // currentUser.setCompany(company);
        // userService.updateUser(currentUser);
        
        return ResponseEntity.ok().build();
    }
} 