/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.upm.core.install;

import com.atlassian.sal.api.net.ResponseException;

public class InvalidFileURIException
extends ResponseException {
    public InvalidFileURIException(String message) {
        super(message);
    }
}

