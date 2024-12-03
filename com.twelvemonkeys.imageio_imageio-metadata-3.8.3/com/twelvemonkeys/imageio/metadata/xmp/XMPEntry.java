/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import com.twelvemonkeys.imageio.metadata.xmp.XMP;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class XMPEntry
extends AbstractEntry {
    private final String fieldName;

    public XMPEntry(String string, Object object) {
        this(string, null, object);
    }

    public XMPEntry(String string, String string2, Object object) {
        super(string, object);
        this.fieldName = string2;
    }

    @Override
    protected String getNativeIdentifier() {
        String string = (String)this.getIdentifier();
        String string2 = this.fieldName != null && string.endsWith(this.fieldName) ? XMP.DEFAULT_NS_MAPPING.get(string.substring(0, string.length() - this.fieldName.length())) : null;
        return string2 != null ? string2 + ":" + this.fieldName : string;
    }

    @Override
    public String getFieldName() {
        return this.fieldName != null ? this.fieldName : XMP.DEFAULT_NS_MAPPING.get(this.getIdentifier());
    }

    @Override
    public String getTypeName() {
        Object object = this.getValue();
        if (object instanceof List) {
            return "List";
        }
        if (object instanceof Set) {
            return "Set";
        }
        if (object instanceof Map) {
            return "Map";
        }
        return super.getTypeName();
    }

    @Override
    public String toString() {
        String string = this.getTypeName();
        String string2 = string != null ? " (" + string + ")" : "";
        return String.format("%s: %s%s", this.getNativeIdentifier(), this.getValueAsString(), string2);
    }
}

