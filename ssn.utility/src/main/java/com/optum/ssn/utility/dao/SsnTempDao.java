package com.optum.ssn.utility.dao;

import com.optum.ssn.utility.SsnTempTable;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public interface SsnTempDao {
    public void createTempTable(String tableName);
    public int[] insertSsnTemp(LinkedList<SsnTempTable> ssnTempTable, String tempTableName) throws InterruptedException;
    public LinkedList<SsnTempTable> getSsnValues(String tableName);
}
