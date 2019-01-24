package com.tg.restaurant;

import java.util.*;

public class RestManager {

    private final Set<Table> tables;
    private final Queue<ClientsGroup> clients;

    public RestManager(Collection<Table> tables) {
        this.tables = new HashSet<>(tables);
        this.clients = new LinkedList<>();
    }

    // new client(s) show up
    public void onArrive(ClientsGroup group) {
        Table table = null;
        for (Table t : tables) {
            if (t.size == group.size && t.isFree()) {
                table = t;
                break;
            }
        }
        if (table == null) {
            for (Table t : tables) {
                if (t.canAcceptGroup(group)) {
                    table = t;
                    break;
                }
            }
        }

        if (table != null) {
            table.acceptGroup(group);
        } else {
            clients.offer(group);
        }
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave(ClientsGroup group) {
        if (!clients.remove(group)) {
            Table groupTable = lookup(group);
            if (groupTable != null) {
                groupTable.leaveGroup(group);
            }

            ClientsGroup groupFromQueue;
            while ((groupFromQueue = clients.poll()) != null) {
                onArrive(groupFromQueue);
            }
        }
    }

    // return table where a given client group is seated,
    // or null if it is still queuing or has already left
    public Table lookup(ClientsGroup group) {
        for (Table table : tables) {
            if (table.isGroupAtTable(group)) {
                return table;
            }
        }
        return null;
    }

    public Set<Table> getTables() {
        return tables;
    }

    public Queue<ClientsGroup> getClients() {
        return clients;
    }
}