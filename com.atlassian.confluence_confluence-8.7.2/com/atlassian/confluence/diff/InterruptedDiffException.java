/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.diff.DiffException;
import java.text.MessageFormat;

public class InterruptedDiffException
extends DiffException {
    private static final String message = "Diff generation for ({0} vs {1}) was interrupted or already failed, returning zero changes.";
    private final int timeout;

    public InterruptedDiffException(ContentEntityObject leftContent, ContentEntityObject rightContent, int timeout) {
        this(MessageFormat.format(message, leftContent, rightContent), timeout);
    }

    public InterruptedDiffException(String message, int timeout) {
        super(message);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }
}

