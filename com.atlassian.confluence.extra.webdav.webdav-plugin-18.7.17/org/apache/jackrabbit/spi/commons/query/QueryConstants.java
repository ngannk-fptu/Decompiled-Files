/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.spi.commons.query.ConstantNameProvider;

public interface QueryConstants {
    public static final int TYPE_LONG = 1;
    public static final String TYPE_NAME_LONG = "LONG";
    public static final int TYPE_DOUBLE = 2;
    public static final String TYPE_NAME_DOUBLE = "DOUBLE";
    public static final int TYPE_STRING = 3;
    public static final String TYPE_NAME_STRING = "STRING";
    public static final int TYPE_DATE = 4;
    public static final String TYPE_NAME_DATE = "DATE";
    public static final int TYPE_TIMESTAMP = 5;
    public static final String TYPE_NAME_TIMESTAMP = "TIMESTAMP";
    public static final int TYPE_POSITION = 6;
    public static final String TYPE_NAME_POSITION = "POS";
    public static final String TYPE_NAME_UNKNOWN = "UNKNOWN TYPE";
    public static final int OPERATIONS = 10;
    public static final int OPERATION_EQ_VALUE = 11;
    public static final String OP_NAME_EQ_VALUE = "eq";
    public static final int OPERATION_EQ_GENERAL = 12;
    public static final String OP_NAME_EQ_GENERAL = "=";
    public static final int OPERATION_NE_VALUE = 13;
    public static final String OP_NAME_NE_VALUE = "ne";
    public static final int OPERATION_NE_GENERAL = 14;
    public static final String OP_NAME_NE_GENERAL = "<>";
    public static final int OPERATION_LT_VALUE = 15;
    public static final String OP_NAME_LT_VALUE = "lt";
    public static final int OPERATION_LT_GENERAL = 16;
    public static final String OP_NAME_LT_GENERAL = "<";
    public static final int OPERATION_GT_VALUE = 17;
    public static final String OP_NAME_GT_VALUE = "gt";
    public static final int OPERATION_GT_GENERAL = 18;
    public static final String OP_NAME_GT_GENERAL = ">";
    public static final int OPERATION_GE_VALUE = 19;
    public static final String OP_NAME_GE_VALUE = "ge";
    public static final int OPERATION_GE_GENERAL = 20;
    public static final String OP_NAME_GE_GENERAL = ">=";
    public static final int OPERATION_LE_VALUE = 21;
    public static final String OP_NAME_LE_VALUE = "le";
    public static final int OPERATION_LE_GENERAL = 22;
    public static final String OP_NAME_LE_GENERAL = "<=";
    public static final int OPERATION_LIKE = 23;
    public static final String OP_NAME_LIKE = "LIKE";
    public static final int OPERATION_BETWEEN = 24;
    public static final String OP_NAME_BETWEEN = "BETWEEN";
    public static final int OPERATION_IN = 25;
    public static final String OP_NAME_IN = "IN";
    public static final int OPERATION_NULL = 26;
    public static final String OP_NAME_NULL = "IS NULL";
    public static final int OPERATION_NOT_NULL = 27;
    public static final String OP_NAME_NOT_NULL = "NOT NULL";
    public static final int OPERATION_SIMILAR = 28;
    public static final String OP_NAME_SIMILAR = "similarity";
    public static final int OPERATION_SPELLCHECK = 29;
    public static final String OP_NAME_SPELLCHECK = "spellcheck";
    public static final String OP_NAME_UNKNOW = "UNKNOWN OPERATION";
    public static final ConstantNameProvider OPERATION_NAMES = new ConstantNameProvider(){
        private final Map operationNames;
        {
            HashMap<Integer, String> map = new HashMap<Integer, String>();
            map.put(new Integer(24), QueryConstants.OP_NAME_BETWEEN);
            map.put(new Integer(11), QueryConstants.OP_NAME_EQ_VALUE);
            map.put(new Integer(12), QueryConstants.OP_NAME_EQ_GENERAL);
            map.put(new Integer(20), QueryConstants.OP_NAME_GE_GENERAL);
            map.put(new Integer(19), QueryConstants.OP_NAME_GE_VALUE);
            map.put(new Integer(18), QueryConstants.OP_NAME_GT_GENERAL);
            map.put(new Integer(17), QueryConstants.OP_NAME_GT_VALUE);
            map.put(new Integer(25), QueryConstants.OP_NAME_IN);
            map.put(new Integer(22), QueryConstants.OP_NAME_LE_GENERAL);
            map.put(new Integer(21), QueryConstants.OP_NAME_LE_VALUE);
            map.put(new Integer(23), QueryConstants.OP_NAME_LIKE);
            map.put(new Integer(16), QueryConstants.OP_NAME_LT_GENERAL);
            map.put(new Integer(15), QueryConstants.OP_NAME_LT_VALUE);
            map.put(new Integer(14), QueryConstants.OP_NAME_NE_GENERAL);
            map.put(new Integer(13), QueryConstants.OP_NAME_NE_VALUE);
            map.put(new Integer(27), QueryConstants.OP_NAME_NOT_NULL);
            map.put(new Integer(26), QueryConstants.OP_NAME_NULL);
            map.put(new Integer(28), QueryConstants.OP_NAME_SIMILAR);
            map.put(new Integer(29), QueryConstants.OP_NAME_SPELLCHECK);
            this.operationNames = Collections.unmodifiableMap(map);
        }

        @Override
        public String getName(int constant) {
            String name = (String)this.operationNames.get(new Integer(constant));
            return name == null ? QueryConstants.OP_NAME_UNKNOW : name;
        }
    };
    public static final ConstantNameProvider TYPE_NAMES = new ConstantNameProvider(){
        private final Map typeNames;
        {
            HashMap<Integer, String> map = new HashMap<Integer, String>();
            map.put(new Integer(4), QueryConstants.TYPE_NAME_DATE);
            map.put(new Integer(2), QueryConstants.TYPE_NAME_DOUBLE);
            map.put(new Integer(1), QueryConstants.TYPE_NAME_LONG);
            map.put(new Integer(6), QueryConstants.TYPE_NAME_POSITION);
            map.put(new Integer(3), QueryConstants.TYPE_NAME_STRING);
            map.put(new Integer(5), QueryConstants.TYPE_NAME_TIMESTAMP);
            this.typeNames = Collections.unmodifiableMap(map);
        }

        @Override
        public String getName(int constant) {
            String name = (String)this.typeNames.get(new Integer(constant));
            return name == null ? QueryConstants.TYPE_NAME_UNKNOWN : name;
        }
    };
}

