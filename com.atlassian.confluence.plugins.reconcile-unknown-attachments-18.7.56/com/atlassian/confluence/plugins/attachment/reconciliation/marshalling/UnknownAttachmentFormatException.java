/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;

public class UnknownAttachmentFormatException
extends XhtmlException {
    public UnknownAttachmentFormatException(String message) {
        super(message);
    }

    public UnknownAttachmentFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

