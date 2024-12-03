/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import java.io.IOException;

public class ContentTooLongException
extends IOException {
    public ContentTooLongException(String message) {
        super(message);
    }
}

