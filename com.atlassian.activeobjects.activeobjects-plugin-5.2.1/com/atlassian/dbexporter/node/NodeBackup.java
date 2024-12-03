/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node;

import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.node.NodeStreamReader;
import com.atlassian.dbexporter.node.NodeStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

public final class NodeBackup {

    public static final class RowDataNode {
        public static final String NAME = "row";
        private static final String STRING = "string";
        private static final String BOOLEAN = "boolean";
        private static final String INTEGER = "integer";
        private static final String DOUBLE = "double";
        private static final String DATE = "timestamp";
        private static final String BINARY = "binary";

        public static NodeCreator add(NodeCreator node) {
            return node.addNode(NAME);
        }

        public static NodeCreator append(NodeCreator node, BigInteger value) {
            return node.addNode(INTEGER).setContentAsBigInteger(value == null ? null : value).closeEntity();
        }

        public static NodeCreator append(NodeCreator node, BigDecimal value) {
            return node.addNode(DOUBLE).setContentAsBigDecimal(value == null ? null : value).closeEntity();
        }

        public static NodeCreator append(NodeCreator node, String value) {
            return node.addNode(STRING).setContentAsString(value).closeEntity();
        }

        public static NodeCreator append(NodeCreator node, Boolean value) {
            return node.addNode(BOOLEAN).setContentAsBoolean(value).closeEntity();
        }

        public static NodeCreator append(NodeCreator node, Timestamp value) {
            return node.addNode(DATE).setContentAsDate(value).closeEntity();
        }

        public static NodeCreator append(NodeCreator node, byte[] value) {
            return node.addNode(BINARY).setContentAsBinary(value).closeEntity();
        }

        public static boolean isString(NodeParser node) {
            return STRING.equals(node.getName());
        }

        public static boolean isBoolean(NodeParser node) {
            return BOOLEAN.equals(node.getName());
        }

        public static boolean isInteger(NodeParser node) {
            return INTEGER.equals(node.getName());
        }

        public static boolean isDouble(NodeParser node) {
            return DOUBLE.equals(node.getName());
        }

        public static boolean isDate(NodeParser node) {
            return DATE.equals(node.getName());
        }

        public static boolean isBinary(NodeParser node) {
            return BINARY.equals(node.getName());
        }
    }

    public static final class ColumnDataNode {
        public static final String NAME = "column";
        private static final String NAME_ATTR = "name";

        public static NodeCreator add(NodeCreator node, String columnName) {
            return node.addNode(NAME).addAttribute(NAME_ATTR, columnName);
        }

        public static String getName(NodeParser node) {
            return node.getRequiredAttribute(NAME_ATTR);
        }
    }

    public static final class TableDataNode {
        public static final String NAME = "data";
        private static final String NAME_ATTR = "tableName";

        public static NodeCreator add(NodeCreator node, String tableName) {
            return node.addNode(NAME).addAttribute(NAME_ATTR, tableName);
        }

        public static String getName(NodeParser node) {
            return node.getRequiredAttribute(NAME_ATTR);
        }
    }

    public static final class ForeignKeyDefinitionNode {
        public static final String NAME = "foreignKey";
        private static final String NAME_ATTR = "name";
        private static final String FROM_TABLE = "fromTable";
        private static final String FROM_COLUMN = "fromColumn";
        private static final String TO_TABLE = "toTable";
        private static final String TO_COLUMN = "toColumn";

        public static NodeCreator add(NodeCreator node) {
            return node.addNode(NAME);
        }

        public static String getName(NodeParser node) {
            return node.getRequiredAttribute(NAME_ATTR);
        }

        public static String getFromTable(NodeParser node) {
            return node.getRequiredAttribute(FROM_TABLE);
        }

        public static NodeCreator setFromTable(NodeCreator node, String fromTable) {
            return node.addAttribute(FROM_TABLE, fromTable);
        }

        public static String getFromColumn(NodeParser node) {
            return node.getRequiredAttribute(FROM_COLUMN);
        }

        public static NodeCreator setFromColumn(NodeCreator node, String fromColumn) {
            return node.addAttribute(FROM_COLUMN, fromColumn);
        }

        public static String getToTable(NodeParser node) {
            return node.getRequiredAttribute(TO_TABLE);
        }

        public static NodeCreator setToTable(NodeCreator node, String toTable) {
            return node.addAttribute(TO_TABLE, toTable);
        }

        public static NodeCreator setToColumn(NodeCreator node, String toField) {
            return node.addAttribute(TO_COLUMN, toField);
        }

        public static String getToColumn(NodeParser node) {
            return node.getRequiredAttribute(TO_COLUMN);
        }
    }

    public static final class ColumnDefinitionNode {
        public static final String NAME = "column";
        private static final String NAME_ATTR = "name";
        private static final String PRIMARY_KEY = "primaryKey";
        private static final String AUTO_INCREMENT = "autoIncrement";
        private static final String SQL_TYPE = "sqlType";
        private static final String PRECISION = "precision";
        private static final String SCALE = "scale";

        public static NodeCreator add(NodeCreator node) {
            return node.addNode(NAME);
        }

        public static String getName(NodeParser node) {
            return node.getRequiredAttribute(NAME_ATTR);
        }

        public static NodeCreator setName(NodeCreator node, String name) {
            return node.addAttribute(NAME_ATTR, name);
        }

        public static boolean isPrimaryKey(NodeParser node) {
            return Boolean.valueOf(node.getAttribute(PRIMARY_KEY));
        }

        public static NodeCreator setPrimaryKey(NodeCreator node, Boolean primaryKey) {
            return primaryKey == null ? node : node.addAttribute(PRIMARY_KEY, String.valueOf(primaryKey));
        }

        public static boolean isAutoIncrement(NodeParser node) {
            return Boolean.valueOf(node.getAttribute(AUTO_INCREMENT));
        }

        public static NodeCreator setAutoIncrement(NodeCreator node, Boolean autoIncrement) {
            return autoIncrement == null ? node : node.addAttribute(AUTO_INCREMENT, String.valueOf(autoIncrement));
        }

        public static int getSqlType(NodeParser node) {
            return Integer.valueOf(node.getRequiredAttribute(SQL_TYPE));
        }

        public static NodeCreator setSqlType(NodeCreator node, int sqlType) {
            return node.addAttribute(SQL_TYPE, String.valueOf(sqlType));
        }

        public static Integer getPrecision(NodeParser node) {
            String stringPrecision = node.getAttribute(PRECISION);
            return stringPrecision == null ? null : Integer.valueOf(stringPrecision);
        }

        public static NodeCreator setPrecision(NodeCreator node, Integer precision) {
            return precision == null ? node : node.addAttribute(PRECISION, String.valueOf(precision));
        }

        public static Integer getScale(NodeParser node) {
            String stringScale = node.getAttribute(SCALE);
            return stringScale == null ? null : Integer.valueOf(stringScale);
        }

        public static NodeCreator setScale(NodeCreator node, Integer scale) {
            return scale == null ? node : node.addAttribute(SCALE, String.valueOf(scale));
        }
    }

    public static final class TableDefinitionNode {
        public static final String NAME = "table";
        private static final String NAME_ATTR = "name";

        public static NodeCreator add(NodeCreator node) {
            return node.addNode(NAME);
        }

        public static String getName(NodeParser node) {
            return node.getRequiredAttribute(NAME_ATTR);
        }

        public static NodeCreator setName(NodeCreator node, String name) {
            return node.addAttribute(NAME_ATTR, name);
        }
    }

    public static final class DatabaseInformationNode {
        public static final String NAME = "database";
        public static final String META = "meta";
        private static final String META_KEY = "key";
        private static final String META_VALUE = "value";

        public static NodeCreator addMeta(NodeCreator node, String key, String value) {
            return node.addNode(META).addAttribute(META_KEY, key).addAttribute(META_VALUE, value).closeEntity();
        }

        public static String getMetaKey(NodeParser node) {
            return node.getRequiredAttribute(META_KEY);
        }

        public static String getMetaValue(NodeParser node) {
            return node.getRequiredAttribute(META_VALUE);
        }
    }

    public static final class RootNode {
        public static final String NAME = "backup";

        public static NodeParser get(NodeStreamReader nsr) {
            NodeParser node = nsr.getRootNode();
            if (!NAME.equals(node.getName())) {
                throw new IllegalStateException("Root node is not 'backup'");
            }
            return node;
        }

        public static NodeCreator add(NodeStreamWriter nsw) {
            return nsw.addRootNode(NAME);
        }
    }
}

