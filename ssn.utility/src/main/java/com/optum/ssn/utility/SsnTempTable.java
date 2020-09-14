package com.optum.ssn.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.sql.DataSourceDefinition;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
public class SsnTempTable {
    private long id;
    private String ssn;
    public SsnTempTable(long id, String ssn) {
        this.id=id;
        this.ssn=ssn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Override
    public String toString() {
        return "SsnTempTable{" +
                "id=" + id +
                ", ssn='" + ssn + '\'' +
                '}';
    }
}
