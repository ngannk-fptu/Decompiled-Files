/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharQueue;
import antlr.CharStreamException;

public abstract class InputBuffer {
    protected int nMarkers = 0;
    protected int markerOffset = 0;
    protected int numToConsume = 0;
    protected CharQueue queue = new CharQueue(1);

    public void commit() {
        --this.nMarkers;
    }

    public void consume() {
        ++this.numToConsume;
    }

    public abstract void fill(int var1) throws CharStreamException;

    public String getLAChars() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = this.markerOffset; i < this.queue.nbrEntries; ++i) {
            stringBuffer.append(this.queue.elementAt(i));
        }
        return stringBuffer.toString();
    }

    public String getMarkedChars() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.markerOffset; ++i) {
            stringBuffer.append(this.queue.elementAt(i));
        }
        return stringBuffer.toString();
    }

    public boolean isMarked() {
        return this.nMarkers != 0;
    }

    public char LA(int n) throws CharStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1);
    }

    public int mark() {
        this.syncConsume();
        ++this.nMarkers;
        return this.markerOffset;
    }

    public void rewind(int n) {
        this.syncConsume();
        this.markerOffset = n;
        --this.nMarkers;
    }

    public void reset() {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.queue.reset();
    }

    protected void syncConsume() {
        while (this.numToConsume > 0) {
            if (this.nMarkers > 0) {
                ++this.markerOffset;
            } else {
                this.queue.removeFirst();
            }
            --this.numToConsume;
        }
    }
}

