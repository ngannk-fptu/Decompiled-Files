/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

class Cell {
    int ref = -1;
    int cmd = -1;
    int cnt = 0;
    int skip = 0;

    Cell() {
    }

    Cell(Cell a) {
        this.ref = a.ref;
        this.cmd = a.cmd;
        this.cnt = a.cnt;
        this.skip = a.skip;
    }

    public String toString() {
        return "ref(" + this.ref + ")cmd(" + this.cmd + ")cnt(" + this.cnt + ")skp(" + this.skip + ")";
    }
}

