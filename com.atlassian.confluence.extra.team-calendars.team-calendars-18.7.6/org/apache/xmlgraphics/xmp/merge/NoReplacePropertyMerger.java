/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.merge;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.merge.PropertyMerger;

public class NoReplacePropertyMerger
implements PropertyMerger {
    @Override
    public void merge(XMPProperty sourceProp, Metadata target) {
        XMPProperty prop = target.getProperty(sourceProp.getName());
        if (prop == null) {
            target.setProperty(sourceProp);
        }
    }
}

