package com.example.IO.console;

import com.example.IO.InputArgs;
import com.example.IO.interfaces.ParserManager;
import lombok.extern.log4j.Log4j2;


/**
 * Класс для парсинга строки из консоли.
 */

@Log4j2
public class ConsoleParserManager implements ParserManager {
    /**
     * Преобразует строку в специальный объект.
     * @param input введенная строка
     * @return объект с именем команды, аргументами и данными о пользователе
     */
    public InputArgs getInputArgs(String input){
        log.trace("Начало метод getInputArgs()");

        InputArgs ans;
        String validatedInput = validate(input.trim());
        if (validatedInput.isEmpty()) {
            ans = new InputArgs("", "", "");
        } else {
            String[] args = validatedInput.split("\\s+");
            String commandName = args[0];
            String argument = args.length > 1 ? args[1] : "";
            ans =  new InputArgs(commandName, argument, "");
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
