/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

class LOHRecElem {
    long num;
    Object obj;
    LOHRecElem next;

    LOHRecElem(long l, Object object, LOHRecElem lOHRecElem) {
        this.num = l;
        this.obj = object;
        this.next = lOHRecElem;
    }
}

