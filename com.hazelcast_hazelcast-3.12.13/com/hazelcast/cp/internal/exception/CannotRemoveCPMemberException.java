/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.exception;

import com.hazelcast.core.HazelcastException;

public class CannotRemoveCPMemberException
extends HazelcastException {
    private static final long serialVersionUID = -3631327013406551312L;

    public CannotRemoveCPMemberException(String message) {
        super(message, null);
    }
}

