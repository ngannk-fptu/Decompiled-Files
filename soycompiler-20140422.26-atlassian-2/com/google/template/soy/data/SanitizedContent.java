/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.data;

import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SoyData;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

@ParametersAreNonnullByDefault
@Immutable
public final class SanitizedContent
extends SoyData {
    private final String content;
    private final ContentKind contentKind;
    private final Dir contentDir;

    SanitizedContent(String content, ContentKind contentKind, @Nullable Dir contentDir) {
        this.content = content;
        this.contentKind = contentKind;
        this.contentDir = contentDir;
    }

    public String getContent() {
        return this.content;
    }

    public ContentKind getContentKind() {
        return this.contentKind;
    }

    @Nullable
    public Dir getContentDirection() {
        return this.contentDir;
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return this.content.length() != 0;
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public String stringValue() {
        return this.content;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return other instanceof SanitizedContent && this.contentKind == ((SanitizedContent)other).contentKind && this.contentDir == ((SanitizedContent)other).contentDir && this.content.equals(((SanitizedContent)other).content);
    }

    public int hashCode() {
        return this.content.hashCode() + 31 * this.contentKind.hashCode();
    }

    public static enum ContentKind {
        HTML,
        JS,
        JS_STR_CHARS,
        URI,
        ATTRIBUTES,
        CSS,
        TEXT;

    }
}

