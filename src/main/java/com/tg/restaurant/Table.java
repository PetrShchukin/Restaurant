package com.tg.restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table {
    public final int size; // number of chairs
    private int freeChairs; // number of free chairs
    private final List<ClientsGroup> clientsGroups;

    public Table(int size) {
        this.size = size;
        this.freeChairs = size;
        this.clientsGroups = new ArrayList<>();
    }

    public int getFreeChairs() {
        return freeChairs;
    }

    public boolean isFree() {
        return this.freeChairs == this.size;
    }

    public boolean isGroupAtTable(ClientsGroup group) {
        return clientsGroups.contains(group);
    }

    public boolean canAcceptGroup(ClientsGroup group) {
        return this.freeChairs >= group.size;
    }

    public int acceptGroup(ClientsGroup group) {
        if (group.size > this.freeChairs) {
            throw new IllegalArgumentException("Group size > free chairs");
        }
        clientsGroups.add(group);
        this.freeChairs -= group.size;
        return this.freeChairs;
    }

    public void leaveGroup(ClientsGroup group) {
        if (clientsGroups.remove(group)) {
            this.freeChairs += group.size;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return size == table.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }

    @Override
    public String toString() {
        if (clientsGroups.isEmpty()) {
            return "t{" + size + ":" + freeChairs + '}';
        } else {
            StringBuilder sb = new StringBuilder(" c[");
            for (ClientsGroup c : clientsGroups) {
                sb.append(c.size)
                        .append(",");
            }
            sb.append("]");
            return "t{" + size + ":" + freeChairs + sb.toString() + '}';
        }
    }
}
