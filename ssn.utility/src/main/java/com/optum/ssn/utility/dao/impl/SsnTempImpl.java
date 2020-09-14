package com.optum.ssn.utility.dao.impl;

import com.EncryptionUtil;
import com.optum.ssn.utility.SsnTempTable;
import com.optum.ssn.utility.dao.SsnTempDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@PropertySource("classpath:application.properties")
@Repository
public class SsnTempImpl implements SsnTempDao {

    @Value("${table_name}" )
    private String tableName;
    @Value("${table_name}"+"_TMP" )
    private String tempTableName;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private EncryptionUtil encryptionUtil;
    @Transactional(rollbackOn = { IllegalStateException.class ,SQLException.class, Error.class,RuntimeException.class})
    public void runSsnProcess(){
        createTempTable(tempTableName);
        LinkedList<SsnTempTable> ssnResults=getSsnValues(tableName);
        ssnResults.stream().forEach(e->
                System.out.println("SSN Value : " + e.getSsn() ));
        insertSsnTemp(ssnResults,tempTableName);
        mergeSsn(tableName,ssnResults);
    }

    @Override
    public void createTempTable(String tableName) {
        System.out.println("Creating table " + tableName);

        String sql="CREATE TABLE " +tableName +" (ID NUMBER(30), SSN VARCHAR2(100))";
        jdbcTemplate.update(sql);
     /*   String ins1="INSERT INTO SSN_CARD (id,ssn) VALUES (123,'4534545')";
        jdbcTemplate.update(ins1);
        String ins2="INSERT INTO SSN_CARD (id,ssn) VALUES (543,'76546')";
        jdbcTemplate.update(ins2);*/

        System.out.println("Created table successfully");
    }

    public LinkedList<SsnTempTable> getSsnValues(String tableName) {

        LinkedList<SsnTempTable> ssnResults=new LinkedList<>();


        jdbcTemplate.query("select distinct id,ssn from " + tableName+" where ssn is not null", (ResultSet rs) -> {
            do {
                ssnResults.add(new SsnTempTable(rs.getLong("id"), encryptionUtil.encryptString(rs.getString("ssn"))));
                //results.put(rs.getLong("id"), encryptionUtil.encryptString(rs.getString("ssn")));
            }while (rs.next());
        });

        return ssnResults;

    }
    @Override
    public int[] insertSsnTemp(LinkedList<SsnTempTable> ssnResults,String tempTableName)  {
        //LinkedList<SsnTempTable> results=ssnResults;
        String sqlQuery="INSERT INTO " + tempTableName + " (ID,SSN) VALUES (?,?)";
        System.out.println("Inserting data for table " + tempTableName);
       return this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                    ps.setLong(1, ssnResults.get(i).getId());
                    ps.setString(2, ssnResults.get(i).getSsn());
                }

                @Override
                public int getBatchSize() {
                    return ssnResults.size();
                }

            });
    }
    public void mergeSsn(String tableName,LinkedList<SsnTempTable> ssnResults){
        String sql="MERGE INTO " + tableName +" USING "
                + "(SELECT CAST(? AS LONG) AS ID,CAST( ? AS VARCHAR) AS SSN  FROM "
                + tempTableName
                + " )SSN_NEW ON ("+tableName+".ID)"
                + " WHEN MATCHED THEN UPDATE SET "+tableName+".SSN"+" = SSN_NEW.SSN";


            this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                    ps.setLong(1, ssnResults.get(i).getId());
                    ps.setString(2, ssnResults.get(i).getSsn());
                }

                @Override
                public int getBatchSize() {
                    return ssnResults.size();
                }

        });
    }
}
