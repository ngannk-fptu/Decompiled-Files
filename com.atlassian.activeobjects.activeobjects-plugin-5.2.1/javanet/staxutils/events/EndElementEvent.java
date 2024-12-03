/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent
extends AbstractXMLEvent
implements EndElement {
    protected QName name;
    protected Collection namespaces;

    public EndElementEvent(QName name, Iterator namespaces) {
        this(name, namespaces, null, null);
    }

    public EndElementEvent(QName name, Iterator namespaces, Location location) {
        this(name, namespaces, location, null);
    }

    public EndElementEvent(QName name, Iterator namespaces, Location location, QName schemaType) {
        super(location, schemaType);
        this.name = name;
        if (namespaces != null && namespaces.hasNext()) {
            ArrayList<Namespace> nsList = new ArrayList<Namespace>();
            do {
                Namespace ns = (Namespace)namespaces.next();
                nsList.add(ns);
            } while (namespaces.hasNext());
        }
    }

    public EndElementEvent(EndElement that) {
        super(that);
        this.name = that.getName();
        Iterator<Namespace> namespaces = that.getNamespaces();
        if (namespaces != null && namespaces.hasNext()) {
            ArrayList<Namespace> nsList = new ArrayList<Namespace>();
            do {
                Namespace ns = namespaces.next();
                nsList.add(ns);
            } while (namespaces.hasNext());
        }
    }

    public int getEventType() {
        return 2;
    }

    public QName getName() {
        return this.name;
    }

    public Iterator getNamespaces() {
        if (this.namespaces != null) {
            return this.namespaces.iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }
}

