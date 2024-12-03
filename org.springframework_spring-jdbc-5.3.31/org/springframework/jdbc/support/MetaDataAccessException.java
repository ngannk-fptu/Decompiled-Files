/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedCheckedException
 */
package org.springframework.jdbc.support;

import org.springframework.core.NestedCheckedException;

public class MetaDataAccessException
extends NestedCheckedException {
    public MetaDataAccessException(String msg) {
        super(msg);
    }

    public MetaDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

