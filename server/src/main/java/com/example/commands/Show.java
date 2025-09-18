package com.example.commands;

import com.example.DB.TreeSetCollectionManager;
import com.example.common.command.Command;
import com.example.common.model.Movie;
import com.example.common.network.ApplicationStatus;
import com.example.common.network.NetworkObject;
import lombok.extern.log4j.Log4j2;

import java.util.TreeSet;

@Log4j2
public class Show extends Command {
    private final TreeSetCollectionManager manager;

    public Show(TreeSetCollectionManager manager){
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.manager = manager;
    }
    @Override
    public NetworkObject execute(NetworkObject request) {
        log.info("выполняется команда show");
        TreeSet<Movie> collection = manager.getCollection();
        String message;
        if (collection.isEmpty()) {
            message = "коллекция пуста";
            log.info(message);
            return new NetworkObject(request.id(), ApplicationStatus.RUNNING, null, null, null, null, message, null);
        } else {
            message = "список элементов коллекции успешно отображен\n";
            message += "в коллекции " + collection.size() + " элементов\n";
            for (Movie movie : collection) {
                message += movie.toString() + "\n";
            }
            NetworkObject response = new NetworkObject(request.id(), ApplicationStatus.RUNNING, null, null, null, null, message, null);

        log.info(message);
        return response;
        }
    }
}
