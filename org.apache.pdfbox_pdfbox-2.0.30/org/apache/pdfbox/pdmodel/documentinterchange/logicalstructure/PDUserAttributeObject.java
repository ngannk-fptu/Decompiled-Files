/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDUserProperty;

public class PDUserAttributeObject
extends PDAttributeObject {
    public static final String OWNER_USER_PROPERTIES = "UserProperties";

    public PDUserAttributeObject() {
        this.setOwner(OWNER_USER_PROPERTIES);
    }

    public PDUserAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public List<PDUserProperty> getOwnerUserProperties() {
        COSArray p = (COSArray)this.getCOSObject().getDictionaryObject(COSName.P);
        ArrayList<PDUserProperty> properties = new ArrayList<PDUserProperty>(p.size());
        for (int i = 0; i < p.size(); ++i) {
            properties.add(new PDUserProperty((COSDictionary)p.getObject(i), this));
        }
        return properties;
    }

    public void setUserProperties(List<PDUserProperty> userProperties) {
        COSArray p = new COSArray();
        for (PDUserProperty userProperty : userProperties) {
            p.add(userProperty);
        }
        this.getCOSObject().setItem(COSName.P, (COSBase)p);
    }

    public void addUserProperty(PDUserProperty userProperty) {
        COSArray p = (COSArray)this.getCOSObject().getDictionaryObject(COSName.P);
        p.add(userProperty);
        this.notifyChanged();
    }

    public void removeUserProperty(PDUserProperty userProperty) {
        if (userProperty == null) {
            return;
        }
        COSArray p = (COSArray)this.getCOSObject().getDictionaryObject(COSName.P);
        p.remove(userProperty.getCOSObject());
        this.notifyChanged();
    }

    public void userPropertyChanged(PDUserProperty userProperty) {
    }

    @Override
    public String toString() {
        return super.toString() + ", userProperties=" + this.getOwnerUserProperties();
    }
}

