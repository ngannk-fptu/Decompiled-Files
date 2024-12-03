/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.AttributeTable
 */
package org.bouncycastle.cms;

import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSAttributeTableGenerator;

public class SimpleAttributeTableGenerator
implements CMSAttributeTableGenerator {
    private final AttributeTable attributes;

    public SimpleAttributeTableGenerator(AttributeTable attributes) {
        this.attributes = attributes;
    }

    @Override
    public AttributeTable getAttributes(Map parameters) {
        return this.attributes;
    }
}

