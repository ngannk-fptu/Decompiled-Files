/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.user.ConfluenceUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ParsedLabelName {
    private final String owner;
    private final String name;
    private String prefix;

    public ParsedLabelName(String name) {
        this(name, null);
    }

    public ParsedLabelName(String name, @Nullable String owner) {
        this(name, owner, null);
    }

    public ParsedLabelName(String name, @Nullable String owner, String prefix) {
        this.name = name == null ? null : name.toLowerCase();
        this.owner = owner;
        this.prefix = prefix;
    }

    public String getName() {
        return this.name;
    }

    public @Nullable String getOwner() {
        return this.owner;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @EnsuresNonNullIf(expression={"getOwner()"}, result=true)
    public boolean isOwnerSpecified() {
        return StringUtils.isNotEmpty((CharSequence)this.getOwner());
    }

    public Label addLabel(Labelable labelable, LabelManager labelManager) {
        Label label = this.toLabel();
        labelManager.addLabel((ContentEntityObject)labelable, label);
        return label;
    }

    public Label toLabel() {
        if (StringUtils.isNotEmpty((CharSequence)this.owner) && LabelParser.PERSONAL_LABEL_PREFIX.equals(this.prefix)) {
            return new Label(this.name, Namespace.PERSONAL, this.owner);
        }
        if (LabelParser.TEAM_LABEL_PREFIX.equals(this.prefix)) {
            return new Label(this.name, Namespace.TEAM);
        }
        return new Label(this.name, Namespace.GLOBAL, this.owner);
    }

    public Label toLabel(@Nullable ConfluenceUser user) {
        if (StringUtils.isNotEmpty((CharSequence)this.owner) && LabelParser.PERSONAL_LABEL_PREFIX.equals(this.prefix)) {
            return new Label(this.name, Namespace.PERSONAL, user);
        }
        if (LabelParser.TEAM_LABEL_PREFIX.equals(this.prefix)) {
            return new Label(this.name, Namespace.TEAM);
        }
        return new Label(this.name, Namespace.GLOBAL, user);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ParsedLabelName other = (ParsedLabelName)obj;
        return new EqualsBuilder().append((Object)this.name, (Object)other.name).append((Object)this.owner, (Object)other.owner).append((Object)this.prefix, (Object)other.prefix).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(301, 37).append((Object)this.name).append((Object)this.owner).append((Object)this.prefix).toHashCode();
    }
}

