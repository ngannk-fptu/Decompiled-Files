/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.definition;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PlainTextMacroBody
implements MacroBody {
    private final Streamable body;

    public PlainTextMacroBody(String body) {
        this.body = Streamables.from(body);
    }

    @Override
    public Streamable getBodyStream() {
        return this.body;
    }

    @Override
    public String getBody() {
        return Streamables.writeToString(this.body);
    }

    @Override
    public Streamable getTransformedBodyStream() {
        return this.getBodyStream();
    }

    @Override
    public Streamable getStorageBodyStream() {
        return this.getBodyStream();
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
        PlainTextMacroBody that = (PlainTextMacroBody)o;
        return !(this.body != null ? !this.body.equals(that.body) : that.body != null);
    }

    public int hashCode() {
        return this.body != null ? this.body.hashCode() : 0;
    }
}

