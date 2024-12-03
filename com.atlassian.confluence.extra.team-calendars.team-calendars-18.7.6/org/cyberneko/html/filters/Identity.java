/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XNIException
 */
package org.cyberneko.html.filters;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.filters.DefaultFilter;

public class Identity
extends DefaultFilter {
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (augs == null || !Identity.synthesized(augs)) {
            super.startElement(element, attributes, augs);
        }
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (augs == null || !Identity.synthesized(augs)) {
            super.emptyElement(element, attributes, augs);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (augs == null || !Identity.synthesized(augs)) {
            super.endElement(element, augs);
        }
    }

    protected static boolean synthesized(Augmentations augs) {
        HTMLEventInfo info = (HTMLEventInfo)augs.getItem(AUGMENTATIONS);
        return info != null ? info.isSynthesized() : false;
    }
}

