/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.property;

import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LengthsProperty
extends AbstractDavProperty<long[]>
implements ItemResourceConstants {
    private final long[] value;

    public LengthsProperty(long[] lengths) {
        super(JCR_LENGTHS, true);
        this.value = lengths;
    }

    @Override
    public long[] getValue() {
        return this.value;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (long length : this.value) {
            String txtContent = String.valueOf(length);
            DomUtil.addChildElement(elem, "length", ItemResourceConstants.NAMESPACE, txtContent);
        }
        return elem;
    }
}

