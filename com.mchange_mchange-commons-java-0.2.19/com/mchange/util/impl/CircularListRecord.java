/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

class CircularListRecord {
    Object object;
    CircularListRecord next;
    CircularListRecord prev;

    CircularListRecord(Object object, CircularListRecord circularListRecord, CircularListRecord circularListRecord2) {
        this.object = object;
        this.prev = circularListRecord;
        this.next = circularListRecord2;
    }

    CircularListRecord(Object object) {
        this.object = object;
        this.prev = this;
        this.next = this;
    }
}

