/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Namespace {
    private static Logger log = LoggerFactory.getLogger(Namespace.class);
    public static final Namespace EMPTY_NAMESPACE = new Namespace("", "");
    public static final Namespace XML_NAMESPACE = new Namespace("xml", "http://www.w3.org/XML/1998/namespace");
    public static final Namespace XMLNS_NAMESPACE = new Namespace("xmlns", "http://www.w3.org/2000/xmlns/");
    private final String prefix;
    private final String uri;

    private Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public static Namespace getNamespace(String prefix, String uri) {
        if (prefix == null) {
            prefix = EMPTY_NAMESPACE.getPrefix();
        }
        if (uri == null) {
            uri = EMPTY_NAMESPACE.getURI();
        }
        return new Namespace(prefix, uri);
    }

    public static Namespace getNamespace(String uri) {
        return Namespace.getNamespace("", uri);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getURI() {
        return this.uri;
    }

    public boolean isSame(String namespaceURI) {
        Namespace other = Namespace.getNamespace(namespaceURI);
        return this.equals(other);
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Namespace) {
            return this.uri.equals(((Namespace)obj).uri);
        }
        return false;
    }
}

