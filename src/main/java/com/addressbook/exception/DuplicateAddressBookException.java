package com.addressbook.exception;

public class DuplicateAddressBookException extends RuntimeException {
    public DuplicateAddressBookException(String message) {
        super(message);
    }
}
