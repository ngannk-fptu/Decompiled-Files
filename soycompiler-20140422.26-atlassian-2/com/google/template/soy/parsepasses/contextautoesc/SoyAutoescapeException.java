/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Preconditions;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import javax.annotation.Nullable;

public final class SoyAutoescapeException
extends SoySyntaxException {
    public static SoyAutoescapeException createWithoutMetaInfo(String message) {
        return new SoyAutoescapeException(message);
    }

    public static SoyAutoescapeException createCausedWithoutMetaInfo(@Nullable String message, Throwable cause) {
        Preconditions.checkNotNull((Object)cause);
        if (message != null) {
            return new SoyAutoescapeException(message, cause);
        }
        return new SoyAutoescapeException(cause);
    }

    public static SoyAutoescapeException createWithNode(String message, SoyNode node) {
        return SoyAutoescapeException.createWithoutMetaInfo(message).associateNode(node);
    }

    public static SoyAutoescapeException createCausedWithNode(@Nullable String message, Throwable cause, SoyNode node) {
        return SoyAutoescapeException.createCausedWithoutMetaInfo(message, cause).associateNode(node);
    }

    private SoyAutoescapeException(String message) {
        super(message);
    }

    private SoyAutoescapeException(String message, Throwable cause) {
        super(message, cause);
    }

    private SoyAutoescapeException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public SoyAutoescapeException associateNode(SoyNode node) {
        SoySyntaxExceptionUtils.associateNode(this, node);
        return this;
    }

    public SoyAutoescapeException maybeAssociateNode(SoyNode node) {
        if (this.getSourceLocation() == SourceLocation.UNKNOWN) {
            this.associateNode(node);
        }
        return this;
    }
}

