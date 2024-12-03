/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.mapping.MetaAttribute;

public class ToolingHint {
    private final String name;
    private final boolean inheritable;
    private final MetaAttribute metaAttribute;

    public ToolingHint(String name, boolean inheritable) {
        this.name = name;
        this.inheritable = inheritable;
        this.metaAttribute = new MetaAttribute(name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isInheritable() {
        return this.inheritable;
    }

    public List getValues() {
        return this.metaAttribute.getValues();
    }

    public void addValue(String value) {
        this.metaAttribute.addValue(value);
    }

    public String getValue() {
        return this.metaAttribute.getValue();
    }

    public boolean isMultiValued() {
        return this.metaAttribute.isMultiValued();
    }

    public String toString() {
        return "ToolingHint{name='" + this.name + '\'' + ", inheritable=" + this.inheritable + ", values=" + this.metaAttribute.getValues() + '}';
    }

    public MetaAttribute asMetaAttribute() {
        return this.metaAttribute;
    }
}

