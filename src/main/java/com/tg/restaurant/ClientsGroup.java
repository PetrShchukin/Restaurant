package com.tg.restaurant;

import java.util.Objects;

public class ClientsGroup {

    public final int size; // number of clients

    public ClientsGroup(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientsGroup that = (ClientsGroup) o;
        return size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }

    @Override
    public String toString() {
        return "c{" + size + '}';
    }
}
