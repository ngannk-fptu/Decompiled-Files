/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowCallbackHandler
 *  org.springframework.jdbc.core.RowCountCallbackHandler
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowCountCallbackHandler;

public class BandanaKeyUniqueConstraintCleaner {
    private static final Logger log = LoggerFactory.getLogger(BandanaKeyUniqueConstraintCleaner.class);
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void cleanUp() {
        int duplicateRows;
        Session session = this.sessionFactory.getCurrentSession();
        JdbcTemplate template = DataAccessUtils.getJdbcTemplate(session);
        log.info("Checking for invalid entries in the Bandana table");
        int nullRows = this.cleanUpNullContextOrKeyRows(template);
        if (nullRows > 0) {
            log.info("Found " + nullRows + " bandana records missing either a context or a key or both. They have been removed.");
        }
        if ((duplicateRows = this.cleanUpDuplicateRows(template)) > 0) {
            log.info("Found " + duplicateRows + " bandana records with duplicate contexts and keys. Only the newest record has been kept.");
        }
    }

    private int cleanUpNullContextOrKeyRows(final JdbcTemplate template) {
        log.debug("Looking for bandana records with a null context or key...");
        String nullBandanaSql = "SELECT * FROM BANDANA WHERE BANDANACONTEXT IS NULL OR BANDANAKEY IS NULL";
        final AtomicInteger nullRows = new AtomicInteger(0);
        RowCountCallbackHandler handler = new RowCountCallbackHandler(){

            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                BandanaRecordRowHolder row = new BandanaRecordRowHolder();
                row.assignFrom(rs);
                nullRows.incrementAndGet();
                template.update("DELETE FROM BANDANA WHERE BANDANAID = ?", (Object[])new Long[]{row.id});
                log.info("Removed a row with null bandana context or key. You can reinsert it by running the following query:\n{}", (Object)row.getInsertSql());
            }
        };
        template.query(nullBandanaSql, (RowCallbackHandler)handler);
        log.debug("Removed {} bad bandana records (missing either context or key).", (Object)handler.getRowCount());
        return nullRows.get();
    }

    private int cleanUpDuplicateRows(final JdbcTemplate template) {
        log.debug("Looking for duplicate bandana records...");
        String duplicateBandanaSql = "SELECT b1.* FROM BANDANA b1, BANDANA b2 WHERE b1.BANDANAID <> b2.BANDANAID AND b1.BANDANACONTEXT = b2.BANDANACONTEXT AND b1.BANDANAKEY = b2.BANDANAKEY ORDER BY b1.BANDANACONTEXT, b1.BANDANAKEY, b1.BANDANAID DESC";
        final AtomicInteger duplicateRows = new AtomicInteger(0);
        final BandanaRecordRowHolder lastUniqueRow = new BandanaRecordRowHolder();
        RowCountCallbackHandler handler = new RowCountCallbackHandler(){

            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                BandanaRecordRowHolder currentRow = new BandanaRecordRowHolder();
                currentRow.assignFrom(rs);
                if (lastUniqueRow.duplicates(currentRow)) {
                    duplicateRows.incrementAndGet();
                    template.update("DELETE FROM BANDANA WHERE BANDANAID = ?", (Object[])new Long[]{currentRow.id});
                    log.info("Removed duplicate bandana record ID: {} is same as ID: {}. You can reinsert it by running the following query:\n{}", new Object[]{currentRow.id, lastUniqueRow.id, currentRow.getInsertSql()});
                } else {
                    lastUniqueRow.assignFrom(currentRow);
                }
            }
        };
        template.query(duplicateBandanaSql, (RowCallbackHandler)handler);
        log.debug("Found {} duplicate bandana records.", (Object)duplicateRows.get());
        return duplicateRows.get();
    }

    private static class BandanaRecordRowHolder {
        private long id;
        private String context;
        private String key;
        private String value;

        public void assignFrom(BandanaRecordRowHolder that) {
            this.id = that.id;
            this.context = that.context;
            this.key = that.key;
            this.value = that.value;
        }

        public void assignFrom(ResultSet rs) throws SQLException {
            this.id = rs.getInt("BANDANAID");
            this.context = rs.getString("BANDANACONTEXT");
            this.key = rs.getString("BANDANAKEY");
            this.value = rs.getString("BANDANAVALUE");
        }

        public boolean duplicates(BandanaRecordRowHolder that) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            return ObjectUtils.equals((Object)this.context, (Object)that.context) && ObjectUtils.equals((Object)this.key, (Object)that.key);
        }

        public String getInsertSql() {
            return "insert into BANDANA (BANDANAID, BANDANACONTEXT, BANDANAKEY, BANDANAVALUE) values (" + this.id + ", " + BandanaRecordRowHolder.escapeSqlStr(this.context) + ", " + BandanaRecordRowHolder.escapeSqlStr(this.key) + ", " + BandanaRecordRowHolder.escapeSqlStr(this.value) + ")";
        }

        private static String escapeSqlStr(String str) {
            if (str == null) {
                return "null";
            }
            return "'" + str.replaceAll("'", "\\'") + "'";
        }
    }
}

