/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.bulk;

public class StopBatchProcessingException
extends RuntimeException {
    public StopBatchProcessingException(String error) {
        super(error);
    }
}

