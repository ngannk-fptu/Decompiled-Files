/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.lang.Nullable;

public class NonTransientDataAccessResourceException
extends NonTransientDataAccessException {
    public NonTransientDataAccessResourceException(String msg) {
        super(msg);
    }

    public NonTransientDataAccessResourceException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

