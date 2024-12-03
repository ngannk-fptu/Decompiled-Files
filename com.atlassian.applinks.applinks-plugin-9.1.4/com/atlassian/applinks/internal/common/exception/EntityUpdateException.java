/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.EntityModificationException;
import javax.annotation.Nullable;

public class EntityUpdateException
extends EntityModificationException {
    public EntityUpdateException(@Nullable String message) {
        super(message);
    }

    public EntityUpdateException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

