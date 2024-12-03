/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import org.jvnet.fastinfoset.FastInfosetException;

public class EncodingAlgorithmException
extends FastInfosetException {
    public EncodingAlgorithmException(String message) {
        super(message);
    }

    public EncodingAlgorithmException(String message, Exception e) {
        super(message, e);
    }

    public EncodingAlgorithmException(Exception e) {
        super(e);
    }
}

