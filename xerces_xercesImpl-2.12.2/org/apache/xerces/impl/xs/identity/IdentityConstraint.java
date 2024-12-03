/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;

public abstract class IdentityConstraint
implements XSIDCDefinition {
    protected short type;
    protected final String fNamespace;
    protected final String fIdentityConstraintName;
    protected final String fElementName;
    protected Selector fSelector;
    protected int fFieldCount;
    protected Field[] fFields;
    protected XSAnnotationImpl[] fAnnotations = null;
    protected int fNumAnnotations;

    protected IdentityConstraint(String string, String string2, String string3) {
        this.fNamespace = string;
        this.fIdentityConstraintName = string2;
        this.fElementName = string3;
    }

    public String getIdentityConstraintName() {
        return this.fIdentityConstraintName;
    }

    public void setSelector(Selector selector) {
        this.fSelector = selector;
    }

    public Selector getSelector() {
        return this.fSelector;
    }

    public void addField(Field field) {
        if (this.fFields == null) {
            this.fFields = new Field[4];
        } else if (this.fFieldCount == this.fFields.length) {
            this.fFields = IdentityConstraint.resize(this.fFields, this.fFieldCount * 2);
        }
        this.fFields[this.fFieldCount++] = field;
    }

    public int getFieldCount() {
        return this.fFieldCount;
    }

    public Field getFieldAt(int n) {
        return this.fFields[n];
    }

    public String getElementName() {
        return this.fElementName;
    }

    public String toString() {
        String string = super.toString();
        int n = string.lastIndexOf(36);
        if (n != -1) {
            return string.substring(n + 1);
        }
        int n2 = string.lastIndexOf(46);
        if (n2 != -1) {
            return string.substring(n2 + 1);
        }
        return string;
    }

    public boolean equals(IdentityConstraint identityConstraint) {
        boolean bl = this.fIdentityConstraintName.equals(identityConstraint.fIdentityConstraintName);
        if (!bl) {
            return false;
        }
        bl = this.fSelector.toString().equals(identityConstraint.fSelector.toString());
        if (!bl) {
            return false;
        }
        boolean bl2 = bl = this.fFieldCount == identityConstraint.fFieldCount;
        if (!bl) {
            return false;
        }
        for (int i = 0; i < this.fFieldCount; ++i) {
            if (this.fFields[i].toString().equals(identityConstraint.fFields[i].toString())) continue;
            return false;
        }
        return true;
    }

    static final Field[] resize(Field[] fieldArray, int n) {
        Field[] fieldArray2 = new Field[n];
        System.arraycopy(fieldArray, 0, fieldArray2, 0, fieldArray.length);
        return fieldArray2;
    }

    @Override
    public short getType() {
        return 10;
    }

    @Override
    public String getName() {
        return this.fIdentityConstraintName;
    }

    @Override
    public String getNamespace() {
        return this.fNamespace;
    }

    @Override
    public short getCategory() {
        return this.type;
    }

    @Override
    public String getSelectorStr() {
        return this.fSelector != null ? this.fSelector.toString() : null;
    }

    @Override
    public StringList getFieldStrs() {
        String[] stringArray = new String[this.fFieldCount];
        for (int i = 0; i < this.fFieldCount; ++i) {
            stringArray[i] = this.fFields[i].toString();
        }
        return new StringListImpl(stringArray, this.fFieldCount);
    }

    @Override
    public XSIDCDefinition getRefKey() {
        return null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    public void addAnnotation(XSAnnotationImpl xSAnnotationImpl) {
        if (xSAnnotationImpl == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
        } else if (this.fNumAnnotations == this.fAnnotations.length) {
            XSAnnotationImpl[] xSAnnotationImplArray = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, xSAnnotationImplArray, 0, this.fNumAnnotations);
            this.fAnnotations = xSAnnotationImplArray;
        }
        this.fAnnotations[this.fNumAnnotations++] = xSAnnotationImpl;
    }
}

