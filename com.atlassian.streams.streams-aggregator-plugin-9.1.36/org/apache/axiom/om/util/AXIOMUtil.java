/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import java.io.Reader;
import java.io.StringReader;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;

public class AXIOMUtil {
    public static OMElement stringToOM(String xmlFragment) throws XMLStreamException {
        return AXIOMUtil.stringToOM(OMAbstractFactory.getOMFactory(), xmlFragment);
    }

    public static OMElement stringToOM(OMFactory omFactory, String xmlFragment) throws XMLStreamException {
        if (xmlFragment != null) {
            return OMXMLBuilderFactory.createOMBuilder(omFactory, (Reader)new StringReader(xmlFragment)).getDocumentElement();
        }
        return null;
    }
}

