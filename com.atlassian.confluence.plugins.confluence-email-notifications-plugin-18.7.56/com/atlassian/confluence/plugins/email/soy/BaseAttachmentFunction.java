/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public abstract class BaseAttachmentFunction<T>
implements SoyServerFunction<T> {
    private static final ImmutableSet<Integer> SIZE = ImmutableSet.of((Object)1);
    private final AttachmentManager attachmentManager;

    public BaseAttachmentFunction(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    protected Content toContent(Object arg) {
        if (arg == null) {
            throw new NullPointerException("argument 0 must not be null in '" + this.getName() + "' soy function");
        }
        if (!(arg instanceof Content)) {
            throw new ClassCastException("argument 0 is not of type '" + Content.class.getName() + "' in '" + this.getName() + "' soy function : " + arg.getClass().getName());
        }
        Content content = (Content)arg;
        if (!content.getType().equals((Object)ContentType.ATTACHMENT)) {
            throw new IllegalArgumentException("argument 0 is not an attachment content object in '" + this.getName() + "' soy function : " + content.getType());
        }
        return content;
    }

    protected Attachment toAttachment(Content content) {
        Attachment r = this.attachmentManager.getAttachment(content.getId().asLong());
        if (r == null) {
            throw new IllegalArgumentException("the content object [" + content + "] is not found");
        }
        return r;
    }

    protected abstract T applyTo(Attachment var1);

    public T apply(Object ... args) {
        return this.applyTo(this.toAttachment(this.toContent(args[0])));
    }

    public Set<Integer> validArgSizes() {
        return SIZE;
    }
}

