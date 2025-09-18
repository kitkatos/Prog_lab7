package com.example.common.command.commands;

import com.example.common.network.ApplicationStatus;
import com.example.DB.TreeSetCollectionManager;
import com.example.common.command.Command;
import com.example.common.model.Movie;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Add extends Command {
    private final TreeSetCollectionManager manager;


    public Add(TreeSetCollectionManager manager){
        super("add", "добавить новый элемент в коллекцию");
        this.manager = manager;
    }

    @Override
    public NetworkObject execute(NetworkObject request) {
        log.info("выполняется команда add");
        Movie movie = request.movie();

        manager.addElem(movie);
        String message = "фильм успешно добавлен в коллекцию";
        log.info(message);
        return new NetworkObject(ApplicationStatus.RUNNING, request.userLogin(), "", "", "", message, null);
    }
}
