/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

public enum MetricLabel {
    NUMBER_OF_BYTES_MARSHALLED("Number of bytes marshalled"),
    NUMBER_OF_BYTES_UNMARSHALLED("Number of bytes unmarshalled"),
    NUMBER_OF_FAILED_GET("Number of failed 'get' operations on an external cache"),
    NUMBER_OF_FAILED_IDENTIFIED_GET("Number of failed 'identified get' operations on an external cache"),
    NUMBER_OF_FAILED_MARSHALL("Number of failed 'marshall' operations on a Marshaller"),
    NUMBER_OF_FAILED_UNMARSHALL("Number of failed 'unmarshall' operations on a Marshaller"),
    NUMBER_OF_FAILED_IDENTIFIED_REMOVE("Number of failed 'identified remove' operations on an external cache"),
    NUMBER_OF_FAILED_IDENTIFIED_REPLACE("Number of failed 'identified replace' operations on an external cache"),
    NUMBER_OF_FAILED_PUT("Number of failed 'put' operations on an external cache"),
    NUMBER_OF_FAILED_REMOVE("Number of failed 'remove' operations on an external cache"),
    NUMBER_OF_FAILED_REMOVE_ALL("Number of failed 'removeAll' operations on an external cache"),
    NUMBER_OF_FACTORY_KEYS("Number of keys passed to a factory"),
    NUMBER_OF_HITS("Number of successful hits to a cache"),
    NUMBER_OF_MISSES("Number of missed lookups to a cache"),
    NUMBER_OF_REMOTE_GET("Number of remote 'get' operations performed to non-direct external cache"),
    TIMED_FACTORY_CALL("Calls to a factory to create values"),
    TIMED_SUPPLIER_CALL("Calls to Supplier<> to create values"),
    TIMED_GET_CALL("Calls to 'get' operations"),
    TIMED_GET_KEYS_CALL("Calls to 'getKeys' operations"),
    TIMED_IDENTIFIED_GET_CALL("Calls to 'identified get' operations"),
    TIMED_IDENTIFIED_REMOVE_CALL("Calls to 'identified remove' operations"),
    TIMED_IDENTIFIED_REPLACE_CALL("Calls to 'identified replace' operations"),
    TIMED_MARSHALL_CALL("Calls to 'marshall' operations"),
    TIMED_PUT_CALL("Calls to 'put' operations"),
    TIMED_REMOVE_CALL("Calls to 'remove' operations"),
    TIMED_REMOVE_ALL_CALL("Calls to 'remove all' operations"),
    TIMED_UNMARSHALL_CALL("Calls to 'unmarshall' operations"),
    TIMED_TRANSACTION_DISCARD_CALL("Calls to 'transaction discard' operations"),
    TIMED_TRANSACTION_SYNC_CALL("Calls to 'transaction sync' operations");

    private final String description;

    private MetricLabel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

