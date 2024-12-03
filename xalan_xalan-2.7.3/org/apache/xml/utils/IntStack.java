/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.EmptyStackException;
import org.apache.xml.utils.IntVector;

public class IntStack
extends IntVector {
    public IntStack() {
    }

    public IntStack(int blocksize) {
        super(blocksize);
    }

    public IntStack(IntStack v) {
        super(v);
    }

    public int push(int i) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = i;
        ++this.m_firstFree;
        return i;
    }

    public final int pop() {
        return this.m_map[--this.m_firstFree];
    }

    public final void quickPop(int n) {
        this.m_firstFree -= n;
    }

    public final int peek() {
        try {
            return this.m_map[this.m_firstFree - 1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public int peek(int n) {
        try {
            return this.m_map[this.m_firstFree - (1 + n)];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public void setTop(int val) {
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

    public int search(int o) {
        int i = this.lastIndexOf(o);
        if (i >= 0) {
            return this.size() - i;
        }
        return -1;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (IntStack)super.clone();
    }
}

