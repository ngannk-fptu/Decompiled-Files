/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.definition;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RichTextMacroBody
implements MacroBody {
    private final Streamable storageBody;
    private final Streamable transformedBody;

    public static RichTextMacroBody withStorage(Streamable storageBody) {
        Preconditions.checkNotNull((Object)storageBody, (Object)"Missing storage body");
        return new RichTextMacroBody(storageBody, null);
    }

    public static RichTextMacroBody withStorageAndTransform(Streamable storageBody, Streamable transformedBody) {
        Preconditions.checkNotNull((Object)storageBody, (Object)"Missing storage body");
        Preconditions.checkNotNull((Object)transformedBody, (Object)"Missing transformed body");
        return new RichTextMacroBody(storageBody, transformedBody);
    }

    @Deprecated
    public RichTextMacroBody(String body) {
        this(null, Streamables.from(body));
    }

    @Deprecated
    public RichTextMacroBody(Streamable body) {
        this(null, body);
    }

    private RichTextMacroBody(Streamable storageBody, Streamable transformedBody) {
        this.transformedBody = transformedBody;
        this.storageBody = storageBody;
    }

    @Override
    public Streamable getBodyStream() {
        if (this.transformedBody != null) {
            return this.transformedBody;
        }
        return this.storageBody;
    }

    @Override
    public String getBody() {
        if (this.transformedBody != null) {
            return Streamables.writeToString(this.transformedBody);
        }
        return Streamables.writeToString(this.storageBody);
    }

    @Override
    public Streamable getTransformedBodyStream() {
        return this.transformedBody;
    }

    @Override
    public Streamable getStorageBodyStream() {
        return this.storageBody;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RichTextMacroBody that = (RichTextMacroBody)o;
        if (this.storageBody != null ? !this.storageBody.equals(that.storageBody) : that.storageBody != null) {
            return false;
        }
        return !(this.transformedBody != null ? !this.transformedBody.equals(that.transformedBody) : that.transformedBody != null);
    }

    public int hashCode() {
        int result = this.storageBody != null ? this.storageBody.hashCode() : 0;
        result = 31 * result + (this.transformedBody != null ? this.transformedBody.hashCode() : 0);
        return result;
    }
}

