/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.saxpath;

import org.jaxen.saxpath.SAXPathEventSource;
import org.jaxen.saxpath.SAXPathException;

public interface XPathReader
extends SAXPathEventSource {
    public void parse(String var1) throws SAXPathException;
}

