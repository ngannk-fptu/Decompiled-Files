/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.dom;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.w3c.dom.DOMImplementation;

public interface DOMMetaFactory
extends OMMetaFactory {
    public DocumentBuilderFactory newDocumentBuilderFactory();

    public DOMImplementation getDOMImplementation();
}

