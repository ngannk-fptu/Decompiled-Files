/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.internal.integration.jira.rest;

import java.util.LinkedHashMap;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class RestErrorMessage
extends LinkedHashMap<String, Object> {
    public static final String CONTEXT = "context";
    public static final String MESSAGE = "message";
    public static final String EXCEPTION_NAME = "exceptionName";

    public RestErrorMessage(String context, String message, String exceptionName) {
        this.put(CONTEXT, context);
        this.put(MESSAGE, message);
        this.put(EXCEPTION_NAME, exceptionName);
    }

    public RestErrorMessage(Exception e) {
        this(null, e.getLocalizedMessage(), e.getClass().getCanonicalName());
    }
}

