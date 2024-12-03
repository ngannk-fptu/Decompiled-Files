/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ShortcutResourceIdentifier
implements ResourceIdentifier {
    private final String shortcutKey;
    public static final String CONTENT_TYPE = "shortcut";
    private final String shortcutParameter;

    public ShortcutResourceIdentifier(String shortcutKey, String shortcutParameter) {
        if (StringUtils.isBlank((CharSequence)shortcutKey)) {
            throw new IllegalArgumentException("shortcutKey cannot be null or empty string.");
        }
        this.shortcutKey = shortcutKey;
        if (shortcutParameter != null && shortcutParameter.toLowerCase().startsWith("javascript:")) {
            shortcutParameter = "#";
        }
        this.shortcutParameter = shortcutParameter;
    }

    public String getShortcutKey() {
        return this.shortcutKey;
    }

    public String getShortcutParameter() {
        return this.shortcutParameter;
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
        ShortcutResourceIdentifier that = (ShortcutResourceIdentifier)o;
        if (!this.shortcutKey.equals(that.shortcutKey)) {
            return false;
        }
        return !(this.shortcutParameter != null ? !this.shortcutParameter.equals(that.shortcutParameter) : that.shortcutParameter != null);
    }

    public int hashCode() {
        int result = this.shortcutKey.hashCode();
        result = 31 * result + (this.shortcutParameter != null ? this.shortcutParameter.hashCode() : 0);
        return result;
    }
}

