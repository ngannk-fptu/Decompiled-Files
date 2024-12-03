/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.FieldDesc;

public class AttributeDesc
extends FieldDesc
implements Serializable {
    public AttributeDesc() {
        super(false);
    }

    public void setAttributeName(String name) {
        this.setXmlName(new QName("", name));
    }
}

