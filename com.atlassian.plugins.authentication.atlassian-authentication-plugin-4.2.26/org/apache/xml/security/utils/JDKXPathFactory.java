/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils;

import org.apache.xml.security.utils.JDKXPathAPI;
import org.apache.xml.security.utils.XPathAPI;
import org.apache.xml.security.utils.XPathFactory;

public class JDKXPathFactory
extends XPathFactory {
    @Override
    public XPathAPI newXPathAPI() {
        return new JDKXPathAPI();
    }
}

