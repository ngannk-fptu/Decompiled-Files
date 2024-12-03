/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;

public class Revisions<T> {
    private List<T> objects;
    private List<Integer> revisionNumbers;

    private List<T> getObjects() {
        if (this.objects == null) {
            this.objects = new ArrayList<T>();
        }
        return this.objects;
    }

    private List<Integer> getRevisionNumbers() {
        if (this.revisionNumbers == null) {
            this.revisionNumbers = new ArrayList<Integer>();
        }
        return this.revisionNumbers;
    }

    public T getObject(int index) {
        return this.getObjects().get(index);
    }

    public int getRevisionNumber(int index) {
        return this.getRevisionNumbers().get(index);
    }

    public void addObject(T object, int revisionNumber) {
        this.getObjects().add(object);
        this.getRevisionNumbers().add(revisionNumber);
    }

    protected void setRevisionNumber(T object, int revisionNumber) {
        int index = this.getObjects().indexOf(object);
        if (index > -1) {
            this.getRevisionNumbers().set(index, revisionNumber);
        }
    }

    public int size() {
        return this.getObjects().size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getObjects().size(); ++i) {
            if (i > 0) {
                sb.append("; ");
            }
            sb.append("object=").append(this.getObjects().get(i)).append(", revisionNumber=").append(this.getRevisionNumber(i));
        }
        return sb.toString();
    }
}

