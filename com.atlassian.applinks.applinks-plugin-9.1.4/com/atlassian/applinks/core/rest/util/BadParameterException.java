/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core.rest.util;

public class BadParameterException
extends RuntimeException {
    public BadParameterException(String parameter) {
        super(parameter + " is a required parameter");
    }
}

