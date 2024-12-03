/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.exception;

public class InvalidStateTransitionException
extends RuntimeException {
    public InvalidStateTransitionException(String transitionName) {
        super("Invalid ZDU transition: " + transitionName);
    }
}

