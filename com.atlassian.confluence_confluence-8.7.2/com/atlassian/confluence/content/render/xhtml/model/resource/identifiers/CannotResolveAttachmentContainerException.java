/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;

public class CannotResolveAttachmentContainerException
extends CannotResolveResourceIdentifierException {
    public CannotResolveAttachmentContainerException(AttachmentResourceIdentifier attachmentResourceIdentifier, String message, Throwable cause) {
        super(attachmentResourceIdentifier, message, cause);
    }
}

