/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.filter.ListParseFilter;
import org.apache.abdera.util.filter.AbstractParseFilter;

public abstract class AbstractListParseFilter
extends AbstractParseFilter
implements Cloneable,
ListParseFilter {
    private static final long serialVersionUID = -758691949740569208L;
    private transient List<QName> qnames = new ArrayList<QName>();
    private transient Map<QName, List<QName>> attributes = new HashMap<QName, List<QName>>();

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListParseFilter add(QName qname) {
        List<QName> list = this.qnames;
        synchronized (list) {
            if (!this.contains(qname)) {
                this.qnames.add(qname);
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean contains(QName qname) {
        List<QName> list = this.qnames;
        synchronized (list) {
            return this.qnames.contains(qname);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListParseFilter add(QName parent, QName attribute) {
        Map<QName, List<QName>> map = this.attributes;
        synchronized (map) {
            if (this.attributes.containsKey(parent)) {
                List<QName> attrs = this.attributes.get(parent);
                if (!attrs.contains(attribute)) {
                    attrs.add(attribute);
                }
            } else {
                ArrayList<QName> attrs = new ArrayList<QName>();
                attrs.add(attribute);
                this.attributes.put(parent, attrs);
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean contains(QName qname, QName attribute) {
        Map<QName, List<QName>> map = this.attributes;
        synchronized (map) {
            if (this.attributes.containsKey(qname)) {
                List<QName> attrs = this.attributes.get(qname);
                return attrs.contains(attribute);
            }
            return false;
        }
    }

    public abstract boolean acceptable(QName var1);

    public abstract boolean acceptable(QName var1, QName var2);

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        assert (this.qnames != null);
        out.writeInt(this.qnames.size());
        for (QName qName : this.qnames) {
            out.writeObject(qName);
        }
        assert (this.attributes != null);
        out.writeInt(this.attributes.size());
        for (Map.Entry entry : this.attributes.entrySet()) {
            out.writeObject(entry.getKey());
            List v = (List)entry.getValue();
            assert (v != null);
            out.writeInt(v.size());
            for (QName q : v) {
                out.writeObject(q);
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int qnamesSize = in.readInt();
        this.qnames = new ArrayList<QName>(qnamesSize);
        for (int i = 0; i < qnamesSize; ++i) {
            this.qnames.add((QName)in.readObject());
        }
        int attributesSize = in.readInt();
        this.attributes = new HashMap<QName, List<QName>>(attributesSize);
        for (int i = 0; i < attributesSize; ++i) {
            QName k = (QName)in.readObject();
            int vSize = in.readInt();
            ArrayList<QName> v = new ArrayList<QName>(vSize);
            for (int j = 0; j < vSize; ++j) {
                v.add((QName)in.readObject());
            }
            this.attributes.put(k, v);
        }
    }
}

