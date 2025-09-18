package com.example.common.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum ApplicationStatus implements Serializable {
    RUNNING(0),
    EXIT(1),
    ERROR(2),
    SEND(3);

    private final int code;
}
