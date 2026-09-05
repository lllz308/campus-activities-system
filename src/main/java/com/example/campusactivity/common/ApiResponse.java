package com.example.campusactivity.common;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, "success", data);
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(0, message, null);
    }
}

