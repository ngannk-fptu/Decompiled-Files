/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public class MappingParserException
extends Exception {
    public MappingParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingParserException(String message) {
        super(message);
    }
}

