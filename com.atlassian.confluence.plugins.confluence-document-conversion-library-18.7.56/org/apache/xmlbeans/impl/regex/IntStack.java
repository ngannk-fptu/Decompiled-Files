/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.regex;

final class IntStack {
    private int fDepth;
    private int[] fData;

    IntStack() {
    }

    public int size() {
        return this.fDepth;
    }

    public void push(int value) {
        this.ensureCapacity(this.fDepth + 1);
        this.fData[this.fDepth++] = value;
    }

    public int peek() {
        return this.fData[this.fDepth - 1];
    }

    public int elementAt(int depth) {
        return this.fData[depth];
    }

    public int pop() {
        return this.fData[--this.fDepth];
    }

    public void clear() {
        this.fDepth = 0;
    }

    public void print() {
        System.out.print('(');
        System.out.print(this.fDepth);
        System.out.print(") {");
        for (int i = 0; i < this.fDepth; ++i) {
            if (i == 3) {
                System.out.print(" ...");
                break;
            }
            System.out.print(' ');
            System.out.print(this.fData[i]);
            if (i >= this.fDepth - 1) continue;
            System.out.print(',');
        }
        System.out.print(" }");
        System.out.println();
    }

    private void ensureCapacity(int size) {
        if (this.fData == null) {
            this.fData = new int[32];
        } else if (this.fData.length <= size) {
            int[] newdata = new int[this.fData.length * 2];
            System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
            this.fData = newdata;
        }
    }
}

