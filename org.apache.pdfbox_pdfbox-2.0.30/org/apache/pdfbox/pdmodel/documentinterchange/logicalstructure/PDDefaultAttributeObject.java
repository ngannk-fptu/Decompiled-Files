/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject;

public class PDDefaultAttributeObject
extends PDAttributeObject {
    public PDDefaultAttributeObject() {
    }

    public PDDefaultAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public List<String> getAttributeNames() {
        ArrayList<String> attrNames = new ArrayList<String>();
        for (Map.Entry<COSName, COSBase> entry : this.getCOSObject().entrySet()) {
            COSName key = entry.getKey();
            if (COSName.O.equals(key)) continue;
            attrNames.add(key.getName());
        }
        return attrNames;
    }

    public COSBase getAttributeValue(String attrName) {
        return this.getCOSObject().getDictionaryObject(attrName);
    }

    protected COSBase getAttributeValue(String attrName, COSBase defaultValue) {
        COSBase value = this.getCOSObject().getDictionaryObject(attrName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void setAttribute(String attrName, COSBase attrValue) {
        COSBase old = this.getAttributeValue(attrName);
        this.getCOSObject().setItem(COSName.getPDFName(attrName), attrValue);
        this.potentiallyNotifyChanged(old, attrValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString()).append(", attributes={");
        Iterator<String> it = this.getAttributeNames().iterator();
        while (it.hasNext()) {
            String name = it.next();
            sb.append(name).append('=').append(this.getAttributeValue(name));
            if (!it.hasNext()) continue;
            sb.append(", ");
        }
        return sb.append('}').toString();
    }
}

