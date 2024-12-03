/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer;

public class XMLStreamBufferException
extends Exception {
    public XMLStreamBufferException(String message) {
        super(message);
    }

    public XMLStreamBufferException(String message, Exception e) {
        super(message, e);
    }

    public XMLStreamBufferException(Exception e) {
        super(e);
    }
}

