/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.guardrails;

class InvalidDataException
extends RuntimeException {
    public InvalidDataException(String statement) {
        super(statement);
    }
}

