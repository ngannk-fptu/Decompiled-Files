/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.jaxp;

import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.serialize.OMXMLReader;
import org.xml.sax.InputSource;

public class OMSource
extends SAXSource {
    public OMSource(OMElement element) {
        this((OMContainer)element);
    }

    public OMSource(OMContainer node) {
        super(new OMXMLReader(node), new InputSource());
    }
}

