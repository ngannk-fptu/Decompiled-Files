/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import com.sun.xml.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import java.io.OutputStream;

public class BEncoderStream
extends BASE64EncoderStream {
    public BEncoderStream(OutputStream out) {
        super(out, Integer.MAX_VALUE);
    }

    public static int encodedLength(byte[] b) {
        return (b.length + 2) / 3 * 4;
    }
}

