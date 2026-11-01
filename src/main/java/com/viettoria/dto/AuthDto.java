package com.viettoria.dto;

public class AuthDto {

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private AdminUserDto user;

        public LoginResponse() {}

        public LoginResponse(String token, AdminUserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public AdminUserDto getUser() { return user; }
        public void setUser(AdminUserDto user) { this.user = user; }
    }

    public static class AdminUserDto {
        private String id;
        private String username;
        private String fullName;
        private String role;

        public AdminUserDto() {}

        public AdminUserDto(String id, String username, String fullName, String role) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
