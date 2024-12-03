/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.notifications.content;

import com.google.common.base.Supplier;

public class ContentException
extends Exception {
    public ContentException(String message, Object ... elements) {
        super(String.format(message, elements));
    }

    public static Supplier<ContentException> contentExceptionSupplier(String message, Object ... elements) {
        return () -> new ContentException(message, elements);
    }
}

