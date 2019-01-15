import java.util.*;

class Table implements Comparable<Table> {
    private final int size;        // number of chairs
    private int freeSpace;        // number of free chairs

    public Table(int size) {
        this.size = size;
        this.freeSpace = size;
    }

    public int getSize() {
        return size;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(int freeSpace) {
        this.freeSpace = this.freeSpace - freeSpace;
    }

    //Tables are sorting according to the rules
    @Override
    public int compareTo(Table table) {
        boolean isEmpty1 = this.getFreeSpace() == this.getSize();
        boolean isEmpty2 = table.getFreeSpace() == table.getSize();
        //Prior to empty tables
        if (!isEmpty1 && isEmpty2) {
            return 1;
        } else if (isEmpty1 && !isEmpty2) {
            return -1;
        } else {
            //shift right table with no available places
            if (this.getFreeSpace() == 0) {
                return 1;
            } else if (table.getFreeSpace() == 0) {
                return -1;
            } else {
                //if there are two tables with the same free space choose smaller one
                if (this.getFreeSpace() - table.getFreeSpace() == 0) {
                    return this.getSize() - table.getSize();
                } else {
                    return this.getFreeSpace() - table.getFreeSpace();
                }
            }
        }
    }
}

class ClientsGroup {
    private final int size;  // number of clients
    private Table table;     // table

    public ClientsGroup(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}

//Creating singleton
class RestManager {
    private static final RestManager instance = new RestManager();

    private final List<Table> tableList = new ArrayList<>();
    public final List<ClientsGroup> clientsQueue = new ArrayList<>();
    private final List<ClientsGroup> clientsAtTables = new ArrayList<>();

    private RestManager() {
        //Create a simple table set
        for (int i = 2; i < 7; i++) {
            tableList.add(new Table(i));
        }
    }

    //Return singleton instance
    public static synchronized RestManager getInstance() {
        return instance;
    }

    //Searching a free table in the table list
    public boolean searchTable(ClientsGroup client) {
        for (Table table : tableList) {
            if (table.getFreeSpace() == 0) break;

            if (table.getFreeSpace() >= client.getSize()) {
                //add the table link to the client, decrease table's free space and add the client to the client list
                client.setTable(table);
                table.setFreeSpace(client.getSize());
                clientsAtTables.add(client);
                Collections.sort(tableList);
                return true;
            }
        }
        return false;
    }

    // new client(s) show up
    public void onArrive(ClientsGroup group) {
        //if table hasn't found add client to queue
        if (!searchTable(group)) {
            clientsQueue.add(group);
            System.out.println("There is no space for a new client =(");
        } else {
            System.out.println("Place for a new client has found");
        }
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave(ClientsGroup group) {
        boolean isFound = false;

        //removing the client from the client list and restore a table free space
        clientsAtTables.remove(group);
        group.getTable().setFreeSpace(-1 * group.getSize());
        Collections.sort(tableList);

        //Searching a client from the queue for a new space
        if (!clientsQueue.isEmpty()) {
            System.out.println("Searching a client from queue....");
            System.out.println("Before");
            showQueue();
            Iterator iterator = clientsQueue.iterator();
            while (iterator.hasNext()) {
                ClientsGroup client = (ClientsGroup)iterator.next();
                if (searchTable(client)) {
                    //Remove the client from the queue
                    iterator.remove();
                    isFound = true;
                    System.out.println("Place for " + client.getSize() + " client has found");
                }
            }
            if (!isFound) {
                System.out.println("There is no suitable client from the queue");
            }
        }
        else {
            System.out.println("Queue is empty");
        }
    }

    // return table where a given client group is seated,
    // or null if it is still queuing or has already left
    public Table lookup(ClientsGroup group) {
        return group.getTable();
    }

    //auxiliary functions
    public void showQueue() {
        System.out.print("Tables: ");
        for (Table table : tableList) {
            System.out.print("(" + table.getFreeSpace() + "\\" + table.getSize() + ") ");
        }
        System.out.print("\nQueue: ");
        for (ClientsGroup client : clientsQueue) {
            System.out.print(client.getSize() + " ");
        }
        System.out.println();
    }

    public ClientsGroup getRandomClient() {
        if (!clientsAtTables.isEmpty()) {
            return clientsAtTables.get(new Random().nextInt(clientsAtTables.size()));
        }
        return null;
    }
}

public class Restaurant {
    public static void main(String[] args) throws InterruptedException {
        //Create a queue of clients
        Thread t1 = new Thread(new ClientsQueue());
        t1.start();

        Thread.sleep(10_000);
        //Random client decides to leave
        Thread t2 = new Thread(new EnoughFun());
        t2.start();

        /*Thread.sleep(15_000);
        t1.interrupt();
        t2.interrupt();*/
    }
}

class ClientsQueue extends Thread {
    @Override
    public void run() {
        RestManager manager = RestManager.getInstance();
        while (true) {
            try {
                Thread.sleep((int) (Math.random() * 5000));
                ClientsGroup clientsGroup = new ClientsGroup(new Random().nextInt(6) + 1);
                System.out.println("\nNew client: " + clientsGroup.getSize());
                System.out.println("Before");
                manager.showQueue();
                manager.onArrive(clientsGroup);
                System.out.println("After");
                manager.showQueue();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

class EnoughFun extends Thread {
    @Override
    public void run() {
        RestManager manager = RestManager.getInstance();
        while (true) {
            try {
                Thread.sleep((int) (Math.random() * 5000));
                ClientsGroup clientsGroup;
                //Choose client from queue or from table
                if (Math.random() < 0.5) {
                    clientsGroup = manager.getRandomClient();
                    if (clientsGroup != null) {
                        System.out.println("\nClient " + clientsGroup.getSize() + " has left a table");
                        manager.onLeave(clientsGroup);
                        System.out.println("After");
                        manager.showQueue();
                    }
                } else {
                    //There is no manager work if client(s) from queue decide to leave
                    if (!manager.clientsQueue.isEmpty()) {
                        manager.clientsQueue.remove(new Random().nextInt(manager.clientsQueue.size()));
                        System.out.println("\nSomeone has left the queue...");
                    }
                }

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
