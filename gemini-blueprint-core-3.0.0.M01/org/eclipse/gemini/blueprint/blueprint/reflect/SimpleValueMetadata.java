/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.StringUtils;

class SimpleValueMetadata
implements ValueMetadata {
    private final String typeName;
    private final String value;

    public SimpleValueMetadata(String typeName, String value) {
        this.typeName = StringUtils.hasText((String)typeName) ? typeName : null;
        this.value = value;
    }

    public SimpleValueMetadata(TypedStringValue typedStringValue) {
        String specifiedType = typedStringValue.getSpecifiedTypeName();
        this.typeName = StringUtils.hasText((String)specifiedType) ? specifiedType : null;
        this.value = typedStringValue.getValue();
    }

    @Override
    public String getStringValue() {
        return this.value;
    }

    @Override
    public String getType() {
        return this.typeName;
    }
}

