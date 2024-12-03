/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

class IOHRecElem {
    int num;
    Object obj;
    IOHRecElem next;

    IOHRecElem(int n, Object object, IOHRecElem iOHRecElem) {
        this.num = n;
        this.obj = object;
        this.next = iOHRecElem;
    }
}

