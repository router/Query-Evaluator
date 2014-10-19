package edu.buffalo.cse562.Evaluators;

import java.util.Comparator;

import edu.buffalo.cse562.QueryHandler.SQLTable;

public class TablePriorityQueueComparator implements Comparator<SQLTable> {

    public int compare(SQLTable t1, SQLTable t2) {
        return (int) (t1.getTupleCount() - t2.getTupleCount());
    }
}
