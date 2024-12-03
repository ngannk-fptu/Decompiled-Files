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

    protected NamespaceEventImpl(Location location, String string) {
        super(location, "xmlns", "http://www.w3.org/2000/xmlns/", null, string, true);
        this.mPrefix = "";
        this.mURI = string;
    }

    protected NamespaceEventImpl(Location location, String string, String string2) {
        super(location, string, "http://www.w3.org/2000/xmlns/", "xmlns", string2, true);
        this.mPrefix = string;
        this.mURI = string2;
    }

    public static NamespaceEventImpl constructDefaultNamespace(Location location, String string) {
        return new NamespaceEventImpl(location, string);
    }

    public static NamespaceEventImpl constructNamespace(Location location, String string, String string2) {
        if (string == null || string.length() == 0) {
            return new NamespaceEventImpl(location, string2);
        }
        return new NamespaceEventImpl(location, string, string2);
    }

    public String getNamespaceURI() {
        return this.mURI;
    }

    public String getPrefix() {
        return this.mPrefix;
    }

    public boolean isDefaultNamespaceDeclaration() {
        return this.mPrefix.length() == 0;
    }

    public int getEventType() {
        return 13;
    }

    public boolean isNamespace() {
        return true;
    }
}

