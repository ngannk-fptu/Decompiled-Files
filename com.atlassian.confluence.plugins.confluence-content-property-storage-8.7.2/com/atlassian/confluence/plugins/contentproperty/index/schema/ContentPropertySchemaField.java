/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.querylang.fields.UISupport
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.SchemaFieldType;
import com.atlassian.fugue.Option;
import com.atlassian.querylang.fields.UISupport;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ContentPropertySchemaField {
    private final String jsonExpression;
    private final SchemaFieldType fieldType;
    private final String fieldName;
    private final String owningPlugin;
    private final String owningModule;
    private final String alias;
    private final Option<UISupport> uiSupport;

    public ContentPropertySchemaField(String jsonExpression, SchemaFieldType fieldType, String fieldName, String owningPlugin, String owningModule, String alias, Option<UISupport> uiSupport) {
        Preconditions.checkNotNull(uiSupport);
        this.jsonExpression = jsonExpression;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.owningPlugin = owningPlugin;
        this.owningModule = owningModule;
        this.alias = alias;
        this.uiSupport = uiSupport;
    }

    public String getJsonExpression() {
        return this.jsonExpression;
    }

    public SchemaFieldType getFieldType() {
        return this.fieldType;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getOwningPlugin() {
        return this.owningPlugin;
    }

    public String getOwningModule() {
        return this.owningModule;
    }

    public String getAlias() {
        return this.alias;
    }

    public Option<UISupport> getUiSupport() {
        return this.uiSupport;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.fieldName).add("type", (Object)this.fieldType).add("expression", (Object)this.jsonExpression).add("owningPlugin", (Object)this.owningPlugin).add("owningModule", (Object)this.owningModule).add("alias", (Object)this.alias).add("uiSupport", this.uiSupport).toString();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.fieldName).append((Object)this.fieldType).append((Object)this.jsonExpression).append((Object)this.owningPlugin).append((Object)this.owningModule).append((Object)this.alias).append(this.uiSupport).hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        ContentPropertySchemaField rhs = (ContentPropertySchemaField)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append((Object)this.fieldName, (Object)rhs.fieldName).append((Object)this.fieldType, (Object)rhs.fieldType).append((Object)this.jsonExpression, (Object)rhs.jsonExpression).append((Object)this.owningPlugin, (Object)rhs.owningPlugin).append((Object)this.owningModule, (Object)rhs.owningModule).append((Object)this.alias, (Object)rhs.alias).append(this.uiSupport, rhs.uiSupport).isEquals();
    }
}

