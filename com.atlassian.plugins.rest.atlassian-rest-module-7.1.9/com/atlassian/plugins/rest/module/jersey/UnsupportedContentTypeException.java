/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugins.rest.module.jersey.EntityConversionException;

public class UnsupportedContentTypeException
extends EntityConversionException {
    public UnsupportedContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

