/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.AbstractFilter;

public class ElementFilter
extends AbstractFilter {
    private static final String CVS_ID = "@(#) $RCSfile: ElementFilter.java,v $ $Revision: 1.20 $ $Date: 2007/11/10 05:29:00 $ $Name:  $";
    private String name;
    private transient Namespace namespace;

    public ElementFilter() {
    }

    public ElementFilter(String name) {
        this.name = name;
    }

    public ElementFilter(Namespace namespace) {
        this.namespace = namespace;
    }

    public ElementFilter(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    @Override
    public boolean matches(Object obj) {
        if (obj instanceof Element) {
            Element el = (Element)obj;
            return !(this.name != null && !this.name.equals(el.getName()) || this.namespace != null && !this.namespace.equals(el.getNamespace()));
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ElementFilter)) {
            return false;
        }
        ElementFilter filter = (ElementFilter)obj;
        if (this.name != null ? !this.name.equals(filter.name) : filter.name != null) {
            return false;
        }
        return !(this.namespace != null ? !this.namespace.equals(filter.namespace) : filter.namespace != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 29 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        return result;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.namespace != null) {
            out.writeObject(this.namespace.getPrefix());
            out.writeObject(this.namespace.getURI());
        } else {
            out.writeObject(null);
            out.writeObject(null);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Object prefix = in.readObject();
        Object uri = in.readObject();
        if (prefix != null) {
            this.namespace = Namespace.getNamespace((String)prefix, (String)uri);
        }
    }
}

