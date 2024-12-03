/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;

public class HttpMessageNotReadableException
extends HttpMessageConversionException {
    public HttpMessageNotReadableException(String msg) {
        super(msg);
    }

    public HttpMessageNotReadableException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

