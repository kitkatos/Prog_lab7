package com.example.common.IO.console;

import com.example.common.IO.InputArgs;
import com.example.common.IO.interfaces.ParserManager;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;


/**
 * Класс для парсинга строки из консоли.
 */

@Log4j2
public class ConsoleParserManager implements ParserManager {
    /**
     * Преобразует строку в NetworkObject.
     * @param input введенная строка
     * @return NetworkObject
     */
    public NetworkObject getNetworkObject(String input){
        log.trace("Начало метод getNetworkObject()");

        NetworkObject ans;
        String validatedInput = validate(input.trim());
        if (validatedInput.isEmpty()) {
            ans = new NetworkObject(ApplicationStatus.RUNNING, "8888", "8888", "", "", "", null);
        } else {
            String[] args = validatedInput.split("\\s+");
            String commandName = args[0];
            String argument = args.length > 1 ? args[1] : "";
            ans = new NetworkObject(ApplicationStatus.RUNNING, "8888", "8888", commandName, argument, "", null);
        }
        log.debug("Полученный объект: {}", ans.toString());
        return ans;
    }

    private String validate(String trimInput){
        log.trace("Начало метод validate()");

        String ans = "";
        String regex = "^[a-zA-Z_]+(\\s+[\\w!а-яА-Я@#$%^&*()+_=№;:?.,]+)?$";

        if (trimInput.matches(regex)){
            ans = trimInput.toLowerCase();
        }
        log.debug("Изначальная строка: {}", trimInput);
        log.debug("Полученные команда и аргументы: {}", ans);
        return ans;
    }
}
