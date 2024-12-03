/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;

public final class ApiPreconditions {
    public static void checkRequestArgs(boolean condition, String reason) {
        if (!condition) {
            throw new BadRequestException(reason);
        }
    }

    public static void checkPermission(boolean condition, String reason) {
        if (!condition) {
            throw new PermissionException(reason);
        }
    }
}

