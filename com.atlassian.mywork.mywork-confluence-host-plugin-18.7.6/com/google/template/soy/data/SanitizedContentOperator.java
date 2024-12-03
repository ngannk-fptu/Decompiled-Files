/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SanitizedContent;
import javax.annotation.Nonnull;

public interface SanitizedContentOperator {
    @Nonnull
    public SanitizedContent.ContentKind getContentKind();
}

