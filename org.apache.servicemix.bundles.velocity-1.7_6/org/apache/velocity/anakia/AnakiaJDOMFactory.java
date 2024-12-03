/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.DefaultJDOMFactory
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package org.apache.velocity.anakia;

import org.apache.velocity.anakia.AnakiaElement;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.Namespace;

public class AnakiaJDOMFactory
extends DefaultJDOMFactory {
    public Element element(String name, Namespace namespace) {
        return new AnakiaElement(name, namespace);
    }

    public Element element(String name) {
        return new AnakiaElement(name);
    }

    public Element element(String name, String uri) {
        return new AnakiaElement(name, uri);
    }

    public Element element(String name, String prefix, String uri) {
        return new AnakiaElement(name, prefix, uri);
    }
}

