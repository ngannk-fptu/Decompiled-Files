/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.TNEFAttribute;

public final class TNEFMAPIAttribute
extends TNEFAttribute {
    private final List<MAPIAttribute> attributes = MAPIAttribute.create(this);

    protected TNEFMAPIAttribute(int id, int type, InputStream inp) throws IOException {
        super(id, type, inp);
    }

    public List<MAPIAttribute> getMAPIAttributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "Attribute " + this.getProperty() + ", type=" + this.getType() + ", " + this.attributes.size() + " MAPI Attributes";
    }
}

