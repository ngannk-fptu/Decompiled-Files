/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.merge;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPArray;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.merge.PropertyMerger;

public class ArrayAddPropertyMerger
implements PropertyMerger {
    @Override
    public void merge(XMPProperty sourceProp, Metadata target) {
        XMPProperty existing = target.getProperty(sourceProp.getName());
        if (existing == null) {
            target.setProperty(sourceProp);
        } else {
            XMPArray array = existing.convertSimpleValueToArray(XMPArrayType.SEQ);
            XMPArray otherArray = sourceProp.getArrayValue();
            if (otherArray == null) {
                if (sourceProp.getXMLLang() != null) {
                    array.add(sourceProp.getValue().toString(), sourceProp.getXMLLang());
                } else {
                    array.add(sourceProp.getValue());
                }
            } else {
                int c = otherArray.getSize();
                for (int i = 0; i < c; ++i) {
                    array.add(otherArray.getValue(i));
                }
            }
        }
    }
}

