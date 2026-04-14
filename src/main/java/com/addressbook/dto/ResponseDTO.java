package com.addressbook.dto;

public class ResponseDTO<T> {

    private String message;
    private T data;

    // No-arg constructor
    public ResponseDTO() {
    }

   
    public ResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Static factory helper — optional but handy
    public static <T> ResponseDTO<T> of(String message, T data) {
        return new ResponseDTO<>(message, data);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    @Override
    public String toString() {
        return "ResponseDTO{message='" + message + "', data=" + data + "}";
    }
}
