/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.TransientDataAccessException;

public class TransientDataAccessResourceException
extends TransientDataAccessException {
    public TransientDataAccessResourceException(String msg) {
        super(msg);
    }

    public TransientDataAccessResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

