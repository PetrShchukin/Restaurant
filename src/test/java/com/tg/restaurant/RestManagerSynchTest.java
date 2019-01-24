package com.tg.restaurant;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RestManagerSynchTest {
    class RMHolder {
        private final RestManager rm;

        public RMHolder(RestManager rm) {
            this.rm = rm;
        }

        public synchronized void onArrive(ClientsGroup group) {
            rm.onArrive(group);
        }

        public synchronized void onLeave(ClientsGroup group) {
            rm.onLeave(group);
        }

        public Set<Table> getTables() {
            return rm.getTables();
        }

        public List<ClientsGroup> getClients() {
            return rm.getClients();
        }
    }

    int MODEL_SIZE = 6;
    RMHolder rm;
    Random r = new Random();

    @Before
    public void setUp() {
        List<Table> tables = new ArrayList<>(MODEL_SIZE);
        for (int i = 0; i < MODEL_SIZE; i++) {
            tables.add(new Table(i + 1));
        }
        rm = new RMHolder(new RestManager(tables));
    }

    @Test
    public void test() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //
                    ClientsGroup cl = new ClientsGroup(r.nextInt(MODEL_SIZE) + 1);
                    rm.onArrive(cl);
                    System.out.println("------->"+cl);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //
                    ClientsGroup cl = new ClientsGroup(r.nextInt(MODEL_SIZE) + 1);
                    rm.onLeave(cl);
                    System.out.println("<-------"+cl);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //

                    System.out.println(new ArrayList<>(rm.getClients()).toString());
                    System.out.println(new ArrayList<>(rm.getTables()).toString());
                    System.out.println();
                }
            }
        }).run();

    }
}
