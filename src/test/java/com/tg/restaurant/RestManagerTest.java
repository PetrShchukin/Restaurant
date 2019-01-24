package com.tg.restaurant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RestManagerTest {

    RestManager rm;

    void replay(int[] clients) {
        for (int c : clients) {
            if (c < 0)
                rm.onLeave(new ClientsGroup(0 - c));
            else
                rm.onArrive(new ClientsGroup(c));
        }
    }

    @Before
    public void setUp() throws Exception {
        rm = new RestManager(Arrays.asList(new Table(1),
                new Table(2),
                new Table(3),
                new Table(4),
                new Table(5),
                new Table(6)));
    }

    @Test
    public void test_1() {
        int[] clients = new int[]{1, 2, 3, 4, 5, 6};
        replay(clients);
        for (Table table : rm.getTables()) {
            Assert.assertFalse(table.isFree());
            Assert.assertEquals(0, table.getFreeChairs());
        }
    }

    @Test
    public void test_2() {
        int[] clients = new int[]{1, 2, 3, 4, 5, 6,
                6, 5, 4, -5};
        replay(clients);
        List<ClientsGroup> cq = rm.getClients();
        //
        System.out.println(cq.toString());
        System.out.println(rm.getTables().toString());
        //
        Assert.assertEquals(2, cq.size());
        for (Table table : rm.getTables()) {
            Assert.assertFalse(table.isFree());
            Assert.assertEquals(table.getFreeChairs(), 0);
        }

    }

    @Test
    public void test_3() {
        int[] clients = new int[]{3, 3, 3, 3, 3, 3};
        replay(clients);
        List<ClientsGroup> cq = rm.getClients();
        //
        System.out.println(cq.toString());
        System.out.println(rm.getTables().toString());
        //
        Assert.assertEquals(1, cq.size());

    }

}
