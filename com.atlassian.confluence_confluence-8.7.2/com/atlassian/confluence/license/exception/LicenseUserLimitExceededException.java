/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.core.exception.InfrastructureException;

public class LicenseUserLimitExceededException
extends InfrastructureException {
    public LicenseUserLimitExceededException(String msg) {
        super(msg);
    }
}

