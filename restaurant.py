"""
https://habr.com/ru/post/436282/

RESTAURANT EXERCISE (please use JAVA 7 syntax)

Your restaurant has a set of tables of different sizes: each table can
accommodate 2, 3, 4, 5 or 6 persons. Clients arrive alone or in groups, up to 6
persons. Clients within a given group must be seated together at one table,
hence you can direct a group only to a table, which can accommodate them all.
If there is no table with the required number of empty chairs, the group has
to wait in the queue.

Once seated, the group cannot change the table, i.e. you cannot move a group
from one table to another to make room for new clients.

Client groups must be served in the order of arrival with one exception: if
there is enough room at a table for a smaller group arriving later, you can
seat them before the larger group(s) in the queue. For example, if there is a
six-person group waiting for a six-seat table and there is a two-person group
queuing or arriving you can send them directly to a table with two empty
chairs.

Groups may share tables, however if at the same time you have an empty table
with the required number of chairs and enough empty chairs at a larger one,
you must always seat your client(s) at an empty table and not any partly seated
one, even if the empty table is bigger than the size of the group.

Of course the system assumes that any bigger group may get bored of seeing
smaller groups arrive and get their tables ahead of them, and then decide to
leave, which would mean that they abandon the queue without being served.

Please fill RestManager class with appropriate data structures and implement
its constructor and three public methods. You are encouraged modify other
classes too (to help us test them) and add new methods at your will.

Java base template:

public class Table
{
    public final int size; // number of chairs
}

public class ClientsGroup
{
    public final int size; // number of clients
}

public class RestManager
{
    public RestManager (List<Table> tables)
    {
        // TODO
    }

    // new client(s) show up
    public void onArrive (ClientsGroup group)
    {
        // TODO
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave (ClientsGroup group)
    {
        // TODO
    }

    // return table where a given client group is seated,
    // or null if it is still queuing or has already left
    public Table lookup (ClientsGroup group)
    {
        // TODO
    }
}
"""


class Table():

    def __init__(self, size):
        self.size = size
        self.freeSize = size
        self.groups = set()

    def attach(self, group):
        self.groups.add(group)
        group.attach(self)
        self.freeSize -= group.size

    def detach(self, group):
        self.groups.remove(group)
        group.detach()
        self.freeSize += group.size

    def __str__(self):
        return("Table (%d)" % self.size)


class ClientsGroup():

    def __init__(self, size):
        self.size = size
        self.table = None

    def attach(self, table):
        self.table = table

    def detach(self):
        self.table = None

    def __str__(self):
        return("Group (%d)" % self.size)

class RestManager():

    def __init__(self, tables):
        self.tables = tables
        self.queue = []
        self.groups = []  # groups with table
        self.sort()

    def sort(self):
        self.tables = sorted(self.tables, key=lambda x: len(x.groups) * 10 + x.size - x.freeSize)

    def onArrive(self, group):
        """new client(s) show up
        """
        print('Arrived:', group)
        for table in self.tables:
            if table.freeSize >= group.size:
                table.attach(group)
                self.groups.append(group)
                if group in self.queue:
                    self.queue.remove(group)
                break
        self.sort()
        if group.table is None:
            if group not in self.queue:
                self.queue.append(group)

    def onLeave(self, group):
        """client(s) leave, either served or simply abandoning the queue
        """
        print('Leaved:', group)
        if group.table is not None:
            group.table.detach(group)
            self.groups.remove(group)
        elif group in self.queue:
            self.queue.remove(group)

        for qg in self.queue:
            self.onArrive(qg)

    def lookup(self, group):
        """return table where a given client group is seated,
        or null if it is still queuing or has already left
        """
        return group.table

    def __str__(self):
        output = ""
        for table in self.tables:
            output += str(table) + ': ' + ', '.join([str(g) for g in table.groups]) + '\n'
        output += "Queue: " + ', '.join([str(g) for g in self.queue]) + '\n'
        output += '\n'
        return output


if __name__ == "__main__":
    tables = [2, 2, 3, 4, 4, 5, 6, 6]
    rm = RestManager([Table(s) for s in tables])
    print(rm)

    groups = []
    groups += [ClientsGroup(3)]  # 0
    groups += [ClientsGroup(5)]  # 1
    groups += [ClientsGroup(5)]  # 2
    groups += [ClientsGroup(5)]  # 3
    groups += [ClientsGroup(6)]  # 4
    groups += [ClientsGroup(3)]  # 5
    groups += [ClientsGroup(2)]  # 6
    groups += [ClientsGroup(4)]  # 7
    groups += [ClientsGroup(3)]  # 8
    groups += [ClientsGroup(6)]  # 9
    for g in groups:
        rm.onArrive(g)
    print(rm)

    # free table for first group(6) in queue
    rm.onLeave(groups[2])
    print(rm)

    # group(3) from queue leaved
    rm.onLeave(groups[8])
    print(rm)

    # arrived new group of 2 clients
    groups += [ClientsGroup(2)]
    rm.onArrive(groups[10])
    print(rm)

    # group(3) on table(4) leaved
    rm.onLeave(groups[5])
    print(rm)

    # arrived new group of 2 clients
    groups += [ClientsGroup(2)]
    rm.onArrive(groups[11])
    print(rm)

    # arrived new group of 2 clients
    groups += [ClientsGroup(2)]
    rm.onArrive(groups[12])
    print(rm)
