/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;

public class NamespaceEventImpl
extends AttributeEventImpl
implements Namespace {
    final String mPrefix;
    final String mURI;

    protected NamespaceEventImpl(Location loc, String nsURI) {
        super(loc, "xmlns", "http://www.w3.org/2000/xmlns/", null, nsURI, true);
        this.mPrefix = "";
        this.mURI = nsURI;
    }

    protected NamespaceEventImpl(Location loc, String nsPrefix, String nsURI) {
        super(loc, nsPrefix, "http://www.w3.org/2000/xmlns/", "xmlns", nsURI, true);
        this.mPrefix = nsPrefix;
        this.mURI = nsURI;
    }

    public static NamespaceEventImpl constructDefaultNamespace(Location loc, String nsURI) {
        return new NamespaceEventImpl(loc, nsURI);
    }

    public static NamespaceEventImpl constructNamespace(Location loc, String nsPrefix, String nsURI) {
        if (nsPrefix == null || nsPrefix.length() == 0) {
            return new NamespaceEventImpl(loc, nsURI);
        }
        return new NamespaceEventImpl(loc, nsPrefix, nsURI);
    }

    @Override
    public String getNamespaceURI() {
        return this.mURI;
    }

    @Override
    public String getPrefix() {
        return this.mPrefix;
    }

    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return this.mPrefix.length() == 0;
    }

    @Override
    public int getEventType() {
        return 13;
    }

    @Override
    public boolean isNamespace() {
        return true;
    }
}

