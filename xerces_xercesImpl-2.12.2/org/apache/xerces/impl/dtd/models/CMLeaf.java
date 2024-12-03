/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.xni.QName;

public class CMLeaf
extends CMNode {
    private final QName fElement = new QName();
    private int fPosition = -1;

    public CMLeaf(QName qName, int n) {
        super(0);
        this.fElement.setValues(qName);
        this.fPosition = n;
    }

    public CMLeaf(QName qName) {
        super(0);
        this.fElement.setValues(qName);
    }

    final QName getElement() {
        return this.fElement;
    }

    final int getPosition() {
        return this.fPosition;
    }

    final void setPosition(int n) {
        this.fPosition = n;
    }

    @Override
    public boolean isNullable() {
        return this.fPosition == -1;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.fElement.toString());
        stringBuffer.append(" (");
        stringBuffer.append(this.fElement.uri);
        stringBuffer.append(',');
        stringBuffer.append(this.fElement.localpart);
        stringBuffer.append(')');
        if (this.fPosition >= 0) {
            stringBuffer.append(" (Pos:").append(Integer.toString(this.fPosition)).append(')');
        }
        return stringBuffer.toString();
    }

    @Override
    protected void calcFirstPos(CMStateSet cMStateSet) {
        if (this.fPosition == -1) {
            cMStateSet.zeroBits();
        } else {
            cMStateSet.setBit(this.fPosition);
        }
    }

    @Override
    protected void calcLastPos(CMStateSet cMStateSet) {
        if (this.fPosition == -1) {
            cMStateSet.zeroBits();
        } else {
            cMStateSet.setBit(this.fPosition);
        }
    }
}

