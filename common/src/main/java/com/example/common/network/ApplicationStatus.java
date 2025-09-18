package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum ApplicationStatus implements Serializable {
    RUNNING(0),
    EXIT(1),
    ERROR(2);

    private final int code;
}
