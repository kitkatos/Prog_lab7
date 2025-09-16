package com.example;

import com.example.network.NetworkObject;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public abstract class Command {
    private final String name;
    private final String description;

    public NetworkObject execute(NetworkObject request) throws Exception{
        return new NetworkObject(ApplicationStatus.RUNNING, request.userLogin(), "", "", "", "Команда успешно выполнена", null);
    }

    @Override
    public String toString(){
        return getName() + ": " + getDescription();
    }
}
