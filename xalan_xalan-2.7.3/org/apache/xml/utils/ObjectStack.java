/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.EmptyStackException;
import org.apache.xml.utils.ObjectVector;

public class ObjectStack
extends ObjectVector {
    public ObjectStack() {
    }

    public ObjectStack(int blocksize) {
        super(blocksize);
    }

    public ObjectStack(ObjectStack v) {
        super(v);
    }

    public Object push(Object i) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            Object[] newMap = new Object[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = i;
        ++this.m_firstFree;
        return i;
    }

    public Object pop() {
        Object val = this.m_map[--this.m_firstFree];
        this.m_map[this.m_firstFree] = null;
        return val;
    }

    public void quickPop(int n) {
        this.m_firstFree -= n;
    }

    public Object peek() {
        try {
            return this.m_map[this.m_firstFree - 1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public Object peek(int n) {
        try {
            return this.m_map[this.m_firstFree - (1 + n)];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public void setTop(Object val) {
        try {
            this.m_map[this.m_firstFree - 1] = val;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public boolean empty() {
        return this.m_firstFree == 0;
    }

    public int search(Object o) {
        int i = this.lastIndexOf(o);
        if (i >= 0) {
            return this.size() - i;
        }
        return -1;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (ObjectStack)super.clone();
    }
}

