/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.upm.core.install;

import com.atlassian.sal.api.net.ResponseException;

public class RelativeURIException
extends ResponseException {
    public RelativeURIException(String message) {
        super(message);
    }
}

