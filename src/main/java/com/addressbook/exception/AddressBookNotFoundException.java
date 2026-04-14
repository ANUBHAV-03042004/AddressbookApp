package com.addressbook.exception;

public class AddressBookNotFoundException extends RuntimeException {
    public AddressBookNotFoundException(String message) {
        super(message);
    }
}
