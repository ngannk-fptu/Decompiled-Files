/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.fugue.Pair
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Pair
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.upgrade.impl;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateCheckConstraint {
    @Deprecated
    public static Predicate<String> createConstraintPredicate(HibernateConfig hibernateConfig, SessionFactory sessionFactory) {
        return HibernateCheckConstraint.createConstraintPredicate(HibernateDatabaseCapabilities.from(hibernateConfig), sessionFactory);
    }

    public static Predicate<String> createConstraintPredicate(HibernateDatabaseCapabilities databaseCapabilities, SessionFactory sessionFactory) {
        return constraintName -> {
            String statement = HibernateCheckConstraint.getCountSQL(databaseCapabilities, constraintName);
            Long constraintsCount = (Long)HibernateCheckConstraint.getTemplate(sessionFactory).queryForObject(statement, Long.TYPE);
            return constraintsCount > 0L;
        };
    }

    public static Map<String, List<String>> getConstraintsForTable(HibernateConfig hibernateConfig, SessionFactory sessionFactory, @NonNull String tableName) {
        io.atlassian.fugue.Pair<String, List<String>> sql = HibernateCheckConstraint.getConstraintSQL(hibernateConfig, tableName);
        List results = HibernateCheckConstraint.getTemplate(sessionFactory).queryForList((String)sql.left());
        List colNames = (List)sql.right();
        HashMap constraintsMap = Maps.newHashMap();
        for (Map row : results) {
            String constraintName = (String)row.get(colNames.get(0));
            String columnName = (String)row.get(colNames.get(1));
            List constraint = constraintsMap.getOrDefault(constraintName, new ArrayList());
            constraint.add(columnName);
            constraintsMap.put(constraintName, constraint);
        }
        return constraintsMap;
    }

    @Deprecated
    public static boolean constraintsExists(HibernateConfig hibernateConfig, SessionFactory sessionFactory, Map<String, Pair<String, List<String>>> expectedConstraints) {
        Map<String, io.atlassian.fugue.Pair<String, List<String>>> ioExpectedConstraints = expectedConstraints.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> FugueConversionUtil.toIoPair((Pair)e.getValue())));
        return HibernateCheckConstraint.checkConstraintsExists(hibernateConfig, sessionFactory, ioExpectedConstraints);
    }

    public static boolean checkConstraintsExists(HibernateConfig hibernateConfig, SessionFactory sessionFactory, Map<String, io.atlassian.fugue.Pair<String, List<String>>> expectedConstraints) {
        return expectedConstraints.entrySet().stream().allMatch(c -> {
            String tableName = (String)c.getKey();
            String expectedConstraint = (String)((io.atlassian.fugue.Pair)c.getValue()).left();
            List expectedCols = (List)((io.atlassian.fugue.Pair)c.getValue()).right();
            Map<String, List<String>> r = HibernateCheckConstraint.getConstraintsForTable(hibernateConfig, sessionFactory, tableName);
            return r.containsKey(expectedConstraint) && expectedCols.containsAll((Collection)r.get(expectedConstraint));
        });
    }

    private static JdbcTemplate getTemplate(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        return DataAccessUtils.getJdbcTemplate(session);
    }

    private static String getCountSQL(HibernateDatabaseCapabilities databaseCapabilities, String constraintName) {
        if (databaseCapabilities.isPostgreSql() || databaseCapabilities.isMySql()) {
            return String.format("SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name = '%s';", constraintName);
        }
        if (databaseCapabilities.isHSQL()) {
            return String.format("SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name = '%s';", constraintName.toUpperCase());
        }
        if (databaseCapabilities.isSqlServer()) {
            return String.format("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE constraint_name = '%s';", constraintName);
        }
        if (databaseCapabilities.isOracle()) {
            return String.format("SELECT COUNT(*) FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME = '%s'", constraintName.toUpperCase());
        }
        if (databaseCapabilities.isH2()) {
            return String.format("SELECT COUNT(*) FROM information_schema.CONSTRAINTS WHERE constraint_name = '%s';", constraintName.toUpperCase());
        }
        throw new IllegalArgumentException("Unsupported database type");
    }

    private static io.atlassian.fugue.Pair<String, List<String>> getConstraintSQL(HibernateConfig config, String tableName) {
        if (config.isMySql()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT TABLE_CONSTRAINTS.TABLE_NAME,%n    TABLE_CONSTRAINTS.CONSTRAINT_NAME,%n    KEY_COLUMN_USAGE.COLUMN_NAME %nFROM information_schema.KEY_COLUMN_USAGE, information_schema.TABLE_CONSTRAINTS %nWHERE TABLE_CONSTRAINTS.TABLE_NAME = '%s'%n    AND TABLE_CONSTRAINTS.CONSTRAINT_TYPE = 'UNIQUE'%n    AND KEY_COLUMN_USAGE.CONSTRAINT_NAME = TABLE_CONSTRAINTS.CONSTRAINT_NAME", tableName), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        if (config.isPostgreSql()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT upper(table_constraints.table_name) as TABLE_NAME,%n    constraint_column_usage.constraint_name as CONSTRAINT_NAME,%n    upper(constraint_column_usage.column_name) as COLUMN_NAME %nFROM information_schema.table_constraints%n    ,information_schema.constraint_column_usage%nWHERE %n    (table_constraints.table_name = lower('%s') OR table_constraints.table_name = '%s')%n    AND table_constraints.constraint_type = 'UNIQUE'%n    AND table_constraints.constraint_name = constraint_column_usage.constraint_name%n", tableName, tableName), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        if (config.isHSQL()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT TABLE_CONSTRAINTS.TABLE_NAME,%n    TABLE_CONSTRAINTS.CONSTRAINT_NAME,%n    KEY_COLUMN_USAGE.COLUMN_NAME %nFROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS, INFORMATION_SCHEMA.KEY_COLUMN_USAGE %nWHERE TABLE_CONSTRAINTS.TABLE_NAME = '%s' %n    AND TABLE_CONSTRAINTS.CONSTRAINT_TYPE = 'UNIQUE' %n    AND KEY_COLUMN_USAGE.CONSTRAINT_NAME = TABLE_CONSTRAINTS.CONSTRAINT_NAME", tableName.toUpperCase()), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        if (config.isSqlServer()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT TABLE_CONSTRAINTS.TABLE_NAME,%n     TABLE_CONSTRAINTS.CONSTRAINT_NAME,%n     CONSTRAINT_COLUMN_USAGE.COLUMN_NAME%nFROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE, INFORMATION_SCHEMA.TABLE_CONSTRAINTS%nWHERE TABLE_CONSTRAINTS.TABLE_NAME = '%s'%n     AND TABLE_CONSTRAINTS.CONSTRAINT_TYPE = 'UNIQUE'%n     AND CONSTRAINT_COLUMN_USAGE.CONSTRAINT_NAME = TABLE_CONSTRAINTS.CONSTRAINT_NAME", tableName), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        if (config.isOracle()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT uc.TABLE_NAME as table_name,%n    uc.CONSTRAINT_NAME as CONSTRAINT_NAME,%n    cc.COLUMN_NAME as COLUMN_NAME %nFROM USER_CONSTRAINTS uc, USER_CONS_COLUMNS cc%nWHERE uc.TABLE_NAME = '%s'%n    AND uc.CONSTRAINT_TYPE IN ('U')%n    AND uc.TABLE_NAME = cc.TABLE_NAME%n    AND uc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME ORDER BY TABLE_NAME, CONSTRAINT_NAME", tableName.toUpperCase()), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        if (config.isH2()) {
            return io.atlassian.fugue.Pair.pair((Object)String.format("SELECT CONSTRAINTS.TABLE_NAME,%n    CONSTRAINT_NAME,%n    COLUMNS.COLUMN_NAME%nFROM INFORMATION_SCHEMA.CONSTRAINTS, INFORMATION_SCHEMA.COLUMNS%nWHERE COLUMNS.TABLE_NAME = '%s'%n    AND CONSTRAINT_TYPE = 'UNIQUE'%n    AND COLUMNS.TABLE_NAME = CONSTRAINTS.TABLE_NAME %n    AND COLUMN_LIST REGEXP concat('^', COLUMNS.COLUMN_NAME, ',|,', COLUMNS.COLUMN_NAME, ',|,', COLUMNS.COLUMN_NAME, '$|^', COLUMNS.COLUMN_NAME, '$')", tableName.toUpperCase()), (Object)ImmutableList.of((Object)"CONSTRAINT_NAME", (Object)"COLUMN_NAME"));
        }
        throw new IllegalArgumentException("Unsupported database type");
    }
}

