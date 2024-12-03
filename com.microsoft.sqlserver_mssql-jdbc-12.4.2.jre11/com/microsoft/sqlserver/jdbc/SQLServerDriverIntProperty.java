/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverIntProperty {
    PACKET_SIZE("packetSize", 8000),
    LOCK_TIMEOUT("lockTimeout", -1),
    LOGIN_TIMEOUT("loginTimeout", 30, 0, 65535),
    QUERY_TIMEOUT("queryTimeout", -1),
    PORT_NUMBER("portNumber", 1433),
    SOCKET_TIMEOUT("socketTimeout", 0),
    SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD("serverPreparedStatementDiscardThreshold", 10),
    STATEMENT_POOLING_CACHE_SIZE("statementPoolingCacheSize", 0),
    CANCEL_QUERY_TIMEOUT("cancelQueryTimeout", -1),
    CONNECT_RETRY_COUNT("connectRetryCount", 1, 0, 255),
    CONNECT_RETRY_INTERVAL("connectRetryInterval", 10, 1, 60);

    private final String name;
    private final int defaultValue;
    private int minValue = -1;
    private int maxValue = -1;

    private SQLServerDriverIntProperty(String name, int defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    private SQLServerDriverIntProperty(String name, int defaultValue, int minValue, int maxValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    int getDefaultValue() {
        return this.defaultValue;
    }

    boolean isValidValue(int value) {
        return this.minValue == -1 && this.maxValue == -1 || value >= this.minValue && value <= this.maxValue;
    }

    public String toString() {
        return this.name;
    }
}

