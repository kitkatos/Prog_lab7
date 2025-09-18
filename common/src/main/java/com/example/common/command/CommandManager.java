package com.example.common.command;


import com.example.common.network.ApplicationStatus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import com.example.common.network.NetworkObject;


import java.util.ArrayDeque;
import java.util.Map;

@Log4j2
@Getter
public class CommandManager {
    private Map<String, Command> commandMap;
    private ArrayDeque<String> history;
    public CommandManager(Map<String, Command> commandMap, ArrayDeque<String> history) {
        this.commandMap = commandMap;
        this.history = history;
    }
    public void addCommand(Command command){
        commandMap.put(command.getName(), command);
    }
    public NetworkObject executeCommand(NetworkObject request){
        String message;
        try {
            Command command = commandMap.get(request.command());
            addHistory(command.getName());
            return command.execute(request);
        } catch (NullPointerException e) {
            message = "Аргумент передан неверно: " + e.getMessage();
            log.error(message);
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, request.userLogin(), "", "", "", message, null);
        } catch (Exception e){
            message = "Ошибка выполнения команды: " + e.getMessage();
            log.error(message);
            return new NetworkObject(request.id(), ApplicationStatus.ERROR, request.userLogin(), "", "", "", message, null);
        }
    }

    public void addHistory(String name) {
        if (history.size() == 8) {
            history.pollFirst();
            log.info("история переполнена, последний элемент удален");
        }
        history.addLast(name);
        log.info("команда добавлена в историю");
    }

}
