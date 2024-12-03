/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.JsonRootEatingInputStreamFilter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;

public class RootElementWrapper {
    public static InputStream wrapInput(InputStream inputStream, String rootName) throws UnsupportedEncodingException {
        SequenceInputStream sis = new SequenceInputStream(new ByteArrayInputStream(String.format("{\"%s\":", rootName).getBytes("UTF-8")), inputStream);
        return new SequenceInputStream(sis, new ByteArrayInputStream("}".getBytes("UTF-8")));
    }

    public static InputStream unwrapInput(InputStream inputStream) throws IOException {
        return new JsonRootEatingInputStreamFilter(inputStream);
    }

    public static OutputStream unwrapOutput(OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException("to be implemented yet");
    }

    public static OutputStream wrapOutput(OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException("to be implemented yet");
    }
}

