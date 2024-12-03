/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.impl.CircularListEnumeration;
import com.mchange.util.impl.CircularListRecord;
import java.util.Enumeration;

public class CircularList
implements Cloneable {
    CircularListRecord firstRecord = null;
    int size = 0;

    private void addElement(Object object, boolean bl) {
        if (this.firstRecord == null) {
            this.firstRecord = new CircularListRecord(object);
        } else {
            CircularListRecord circularListRecord;
            this.firstRecord.prev.next = circularListRecord = new CircularListRecord(object, this.firstRecord.prev, this.firstRecord);
            this.firstRecord.prev = circularListRecord;
            if (bl) {
                this.firstRecord = circularListRecord;
            }
        }
        ++this.size;
    }

    private void removeElement(boolean bl) {
        if (this.size == 1) {
            this.firstRecord = null;
        } else {
            if (bl) {
                this.firstRecord = this.firstRecord.next;
            }
            this.zap(this.firstRecord.prev);
        }
        --this.size;
    }

    private void zap(CircularListRecord circularListRecord) {
        circularListRecord.next.prev = circularListRecord.prev;
        circularListRecord.prev.next = circularListRecord.next;
    }

    public void appendElement(Object object) {
        this.addElement(object, false);
    }

    public void addElementToFront(Object object) {
        this.addElement(object, true);
    }

    public void removeFirstElement() {
        this.removeElement(true);
    }

    public void removeLastElement() {
        this.removeElement(false);
    }

    public void removeFromFront(int n) {
        if (n > this.size) {
            throw new IndexOutOfBoundsException(n + ">" + this.size);
        }
        for (int i = 0; i < n; ++i) {
            this.removeElement(true);
        }
    }

    public void removeFromBack(int n) {
        if (n > this.size) {
            throw new IndexOutOfBoundsException(n + ">" + this.size);
        }
        for (int i = 0; i < n; ++i) {
            this.removeElement(false);
        }
    }

    public void removeAllElements() {
        this.size = 0;
        this.firstRecord = null;
    }

    public Object getElementFromFront(int n) {
        if (n >= this.size) {
            throw new IndexOutOfBoundsException(n + ">=" + this.size);
        }
        CircularListRecord circularListRecord = this.firstRecord;
        for (int i = 0; i < n; ++i) {
            circularListRecord = circularListRecord.next;
        }
        return circularListRecord.object;
    }

    public Object getElementFromBack(int n) {
        if (n >= this.size) {
            throw new IndexOutOfBoundsException(n + ">=" + this.size);
        }
        CircularListRecord circularListRecord = this.firstRecord.prev;
        for (int i = 0; i < n; ++i) {
            circularListRecord = circularListRecord.prev;
        }
        return circularListRecord.object;
    }

    public Object getFirstElement() {
        try {
            return this.firstRecord.object;
        }
        catch (NullPointerException nullPointerException) {
            throw new IndexOutOfBoundsException("CircularList is empty.");
        }
    }

    public Object getLastElement() {
        try {
            return this.firstRecord.prev.object;
        }
        catch (NullPointerException nullPointerException) {
            throw new IndexOutOfBoundsException("CircularList is empty.");
        }
    }

    public Enumeration elements(boolean bl, boolean bl2) {
        return new CircularListEnumeration(this, bl, bl2);
    }

    public Enumeration elements(boolean bl) {
        return this.elements(bl, true);
    }

    public Enumeration elements() {
        return this.elements(true, true);
    }

    public int size() {
        return this.size;
    }

    public Object clone() {
        CircularList circularList = new CircularList();
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            circularList.appendElement(this.getElementFromFront(i));
        }
        return circularList;
    }

    public static void main(String[] stringArray) {
        CircularList circularList = new CircularList();
        circularList.appendElement("Hello");
        circularList.appendElement("There");
        circularList.appendElement("Joe.");
        Enumeration enumeration = circularList.elements();
        while (enumeration.hasMoreElements()) {
            System.out.println("x " + enumeration.nextElement());
        }
    }
}

