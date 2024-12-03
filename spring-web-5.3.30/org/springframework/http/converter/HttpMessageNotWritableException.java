/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.http.converter;

import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;

public class HttpMessageNotWritableException
extends HttpMessageConversionException {
    public HttpMessageNotWritableException(String msg) {
        super(msg);
    }

    public HttpMessageNotWritableException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

