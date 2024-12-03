/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

public class ExtendedModelMap
extends ModelMap
implements Model {
    @Override
    public ExtendedModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
        return this;
    }

    @Override
    public ExtendedModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    @Override
    public ExtendedModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    @Override
    public ExtendedModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        super.addAllAttributes(attributes);
        return this;
    }

    @Override
    public ExtendedModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        super.mergeAttributes(attributes);
        return this;
    }

    @Override
    public Map<String, Object> asMap() {
        return this;
    }
}

