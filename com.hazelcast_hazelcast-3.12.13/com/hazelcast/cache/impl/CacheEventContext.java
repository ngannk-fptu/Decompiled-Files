/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.nio.serialization.Data;

public class CacheEventContext {
    private String cacheName;
    private CacheEventType eventType;
    private Data dataKey;
    private Data dataValue;
    private Data dataOldValue;
    private boolean isOldValueAvailable;
    private long creationTime;
    private long expirationTime;
    private long lastAccessTime;
    private long accessHit;
    private String origin;
    private int orderKey;
    private int completionId;
    private Data expiryPolicy;

    public String getCacheName() {
        return this.cacheName;
    }

    public CacheEventContext setCacheName(String cacheName) {
        this.cacheName = cacheName;
        return this;
    }

    public CacheEventType getEventType() {
        return this.eventType;
    }

    public CacheEventContext setEventType(CacheEventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public Data getDataKey() {
        return this.dataKey;
    }

    public CacheEventContext setDataKey(Data dataKey) {
        this.dataKey = dataKey;
        return this;
    }

    public Data getDataValue() {
        return this.dataValue;
    }

    public CacheEventContext setDataValue(Data dataValue) {
        this.dataValue = dataValue;
        return this;
    }

    public Data getDataOldValue() {
        return this.dataOldValue;
    }

    public CacheEventContext setDataOldValue(Data dataOldValue) {
        this.dataOldValue = dataOldValue;
        return this;
    }

    public boolean isOldValueAvailable() {
        return this.isOldValueAvailable;
    }

    public CacheEventContext setIsOldValueAvailable(boolean isOldValueAvailable) {
        this.isOldValueAvailable = isOldValueAvailable;
        return this;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public CacheEventContext setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public CacheEventContext setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getAccessHit() {
        return this.accessHit;
    }

    public CacheEventContext setAccessHit(long accessHit) {
        this.accessHit = accessHit;
        return this;
    }

    public Data getExpiryPolicy() {
        return this.expiryPolicy;
    }

    public CacheEventContext setExpiryPolicy(Data expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
        return this;
    }

    public String getOrigin() {
        return this.origin;
    }

    public CacheEventContext setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public int getOrderKey() {
        return this.orderKey;
    }

    public CacheEventContext setOrderKey(int orderKey) {
        this.orderKey = orderKey;
        return this;
    }

    public int getCompletionId() {
        return this.completionId;
    }

    public CacheEventContext setCompletionId(int completionId) {
        this.completionId = completionId;
        return this;
    }
}

