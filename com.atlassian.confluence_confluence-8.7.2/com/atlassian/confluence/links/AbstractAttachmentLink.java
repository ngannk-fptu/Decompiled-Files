/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.BaseLink
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.renderer.links.BaseLink;
import com.atlassian.renderer.links.GenericLinkParser;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractAttachmentLink
extends BaseLink {
    protected Attachment attachment;

    protected AbstractAttachmentLink(GenericLinkParser genericLinkParser) {
        super(genericLinkParser);
    }

    public @Nullable Attachment getAttachment() {
        return this.attachment;
    }

    public String getLinkBody() {
        if (this.isNoLinkBodyProvided()) {
            return Objects.requireNonNull(this.getAttachment()).getDisplayTitle();
        }
        return super.getLinkBody();
    }

    @EnsuresNonNullIf(expression={"getAttachment()"}, result=true)
    private boolean isNoLinkBodyProvided() {
        return this.getOriginalParser().getLinkBody() == null && this.getAttachment() != null;
    }
}

