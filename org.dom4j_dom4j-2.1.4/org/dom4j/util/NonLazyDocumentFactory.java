/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.util;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.util.NonLazyElement;

public class NonLazyDocumentFactory
extends DocumentFactory {
    protected static transient NonLazyDocumentFactory singleton = new NonLazyDocumentFactory();

    public static DocumentFactory getInstance() {
        return singleton;
    }

    @Override
    public Element createElement(QName qname) {
        return new NonLazyElement(qname);
    }
}

