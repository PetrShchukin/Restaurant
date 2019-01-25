package com.tg.restaurant;

import java.util.*;

public class RestManager {

    private final Set<Table> tables;
    private final List<ClientsGroup> clients;

    public RestManager(Collection<Table> tables) {
        this.tables = new HashSet<>(tables);
        this.clients = new ArrayList<>();
    }

    // new client(s) show up
    public void onArrive(ClientsGroup group) {

        Table table = lookupFreeTable(group);
        if (table != null) {
            table.acceptGroup(group);
        } else {
            clients.add(group);
        }
    }

    private Table lookupFreeTable(ClientsGroup group) {
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
        return table;
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave(ClientsGroup group) {
        if (!clients.remove(group)) {
            Table groupTable = lookup(group);
            if (groupTable != null) {
                groupTable.leaveGroup(group);
            }

            for (int i = clients.size() - 1; i >= 0; i--) {
                ClientsGroup clientFromQueue = clients.get(i);
                Table table = lookupFreeTable(clientFromQueue);
                if (table != null) {
                    table.acceptGroup(clientFromQueue);
                    clients.remove(clientFromQueue);
                }
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

    public List<ClientsGroup> getClients() {
        return clients;
    }
}