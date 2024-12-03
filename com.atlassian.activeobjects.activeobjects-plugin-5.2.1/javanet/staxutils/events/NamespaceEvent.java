/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AttributeEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;

public class NamespaceEvent
extends AttributeEvent
implements Namespace {
    public static final QName DEFAULT_NS_DECL = new QName("http://www.w3.org/2000/xmlns/", "xmlns");

    public NamespaceEvent(String prefix, String nsURI) {
        this(prefix, nsURI, null);
    }

    public NamespaceEvent(String prefix, String nsURI, Location location) {
        super(prefix != null ? new QName("http://www.w3.org/2000/xmlns/", prefix, "xmlns") : DEFAULT_NS_DECL, nsURI, location);
    }

    public int getEventType() {
        return 13;
    }

    public String getNamespaceURI() {
        return this.getValue();
    }

    public String getPrefix() {
        String prefix = this.getName().getLocalPart();
        if (!"xmlns".equals(prefix)) {
            return prefix;
        }
        return "";
    }

    public boolean isDefaultNamespaceDeclaration() {
        return "".equals(this.getPrefix());
    }
}

