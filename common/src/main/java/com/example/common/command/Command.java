package com.example.common.command;

import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Абстрактный класс для команд клиента и сервера.
 * Содержит поля имя и описание команды.
 * Умеет выполнять и выдавать описание команды.
 */
@Getter
@AllArgsConstructor
public abstract class Command {
    private final String name;
    private final String description;

    public NetworkObject execute(NetworkObject request) throws Exception{
        return new NetworkObject(request.id(), ApplicationStatus.RUNNING, request.userLogin(), "", "", "", "Команда успешно выполнена", null);
    }

    @Override
    public String toString(){
        return getName() + ": " + getDescription();
    }
}
