/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.EntityModificationException;
import javax.annotation.Nullable;

public class EntityCreateException
extends EntityModificationException {
    public EntityCreateException(@Nullable String message) {
        super(message);
    }

    public EntityCreateException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

