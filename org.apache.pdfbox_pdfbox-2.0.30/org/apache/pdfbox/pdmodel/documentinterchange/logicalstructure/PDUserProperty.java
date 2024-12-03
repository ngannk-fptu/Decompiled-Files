/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDUserAttributeObject;

public class PDUserProperty
extends PDDictionaryWrapper {
    private final PDUserAttributeObject userAttributeObject;

    public PDUserProperty(PDUserAttributeObject userAttributeObject) {
        this.userAttributeObject = userAttributeObject;
    }

    public PDUserProperty(COSDictionary dictionary, PDUserAttributeObject userAttributeObject) {
        super(dictionary);
        this.userAttributeObject = userAttributeObject;
    }

    public String getName() {
        return this.getCOSObject().getNameAsString(COSName.N);
    }

    public void setName(String name) {
        this.potentiallyNotifyChanged(this.getName(), name);
        this.getCOSObject().setName(COSName.N, name);
    }

    public COSBase getValue() {
        return this.getCOSObject().getDictionaryObject(COSName.V);
    }

    public void setValue(COSBase value) {
        this.potentiallyNotifyChanged(this.getValue(), value);
        this.getCOSObject().setItem(COSName.V, value);
    }

    public String getFormattedValue() {
        return this.getCOSObject().getString(COSName.F);
    }

    public void setFormattedValue(String formattedValue) {
        this.potentiallyNotifyChanged(this.getFormattedValue(), formattedValue);
        this.getCOSObject().setString(COSName.F, formattedValue);
    }

    public boolean isHidden() {
        return this.getCOSObject().getBoolean(COSName.H, false);
    }

    public void setHidden(boolean hidden) {
        this.potentiallyNotifyChanged(this.isHidden(), hidden);
        this.getCOSObject().setBoolean(COSName.H, hidden);
    }

    public String toString() {
        return "Name=" + this.getName() + ", Value=" + this.getValue() + ", FormattedValue=" + this.getFormattedValue() + ", Hidden=" + this.isHidden();
    }

    private void potentiallyNotifyChanged(Object oldEntry, Object newEntry) {
        if (this.isEntryChanged(oldEntry, newEntry)) {
            this.userAttributeObject.userPropertyChanged(this);
        }
    }

    private boolean isEntryChanged(Object oldEntry, Object newEntry) {
        if (oldEntry == null) {
            return newEntry != null;
        }
        return !oldEntry.equals(newEntry);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.userAttributeObject == null ? 0 : this.userAttributeObject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PDUserProperty other = (PDUserProperty)obj;
        return !(this.userAttributeObject == null ? other.userAttributeObject != null : !this.userAttributeObject.equals(other.userAttributeObject));
    }
}

