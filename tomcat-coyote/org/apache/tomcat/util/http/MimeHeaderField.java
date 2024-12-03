/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.MessageBytes
 */
package org.apache.tomcat.util.http;

import org.apache.tomcat.util.buf.MessageBytes;

class MimeHeaderField {
    private final MessageBytes nameB = MessageBytes.newInstance();
    private final MessageBytes valueB = MessageBytes.newInstance();

    MimeHeaderField() {
    }

    public void recycle() {
        this.nameB.recycle();
        this.valueB.recycle();
    }

    public MessageBytes getName() {
        return this.nameB;
    }

    public MessageBytes getValue() {
        return this.valueB;
    }

    public String toString() {
        return this.nameB + ": " + this.valueB;
    }
}

