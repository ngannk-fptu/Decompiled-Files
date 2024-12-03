/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.FieldDesc;

public class ElementDesc
extends FieldDesc
implements Serializable {
    private int minOccurs = 1;
    private int maxOccurs = 1;
    private boolean nillable = false;
    private boolean unbounded = false;
    private QName arrayType;
    private QName itemQName;

    public ElementDesc() {
        super(true);
    }

    public boolean isMinOccursZero() {
        return this.minOccurs == 0;
    }

    public int getMinOccurs() {
        return this.minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return this.maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public void setMaxOccursUnbounded(boolean ubnd) {
        this.unbounded = ubnd;
    }

    public boolean isMaxOccursUnbounded() {
        return this.unbounded;
    }

    public boolean isNillable() {
        return this.nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public QName getArrayType() {
        return this.arrayType;
    }

    public void setArrayType(QName arrayType) {
        this.arrayType = arrayType;
    }

    public QName getItemQName() {
        return this.itemQName;
    }

    public void setItemQName(QName itemQName) {
        this.itemQName = itemQName;
    }
}

