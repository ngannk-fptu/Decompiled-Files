/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfwriter;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSObjectKey;

public class COSWriterXRefEntry
implements Comparable<COSWriterXRefEntry> {
    private long offset;
    private COSBase object;
    private COSObjectKey key;
    private boolean free = false;
    private static final COSWriterXRefEntry NULLENTRY = new COSWriterXRefEntry(0L, null, new COSObjectKey(0L, 65535));

    public COSWriterXRefEntry(long start, COSBase obj, COSObjectKey keyValue) {
        this.setOffset(start);
        this.setObject(obj);
        this.setKey(keyValue);
    }

    @Override
    public int compareTo(COSWriterXRefEntry obj) {
        if (obj != null) {
            if (this.getKey().getNumber() < obj.getKey().getNumber()) {
                return -1;
            }
            if (this.getKey().getNumber() > obj.getKey().getNumber()) {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    public static COSWriterXRefEntry getNullEntry() {
        return NULLENTRY;
    }

    public COSObjectKey getKey() {
        return this.key;
    }

    public long getOffset() {
        return this.offset;
    }

    public boolean isFree() {
        return this.free;
    }

    public void setFree(boolean newFree) {
        this.free = newFree;
    }

    private void setKey(COSObjectKey newKey) {
        this.key = newKey;
    }

    public final void setOffset(long newOffset) {
        this.offset = newOffset;
    }

    public COSBase getObject() {
        return this.object;
    }

    private void setObject(COSBase newObject) {
        this.object = newObject;
    }

    static {
        NULLENTRY.setFree(true);
    }
}

