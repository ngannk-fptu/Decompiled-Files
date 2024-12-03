/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.application;

public class ApplicationAccessDeniedException
extends Exception {
    public ApplicationAccessDeniedException(String application) {
        super("User does not have access to application <" + application + ">");
    }
}

