/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.evt.BaseStartElement;
import com.ctc.wstx.evt.MergedNsContext;
import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

public class SimpleStartElement
extends BaseStartElement {
    final Map<QName, Attribute> mAttrs;

    protected SimpleStartElement(Location loc, QName name, BaseNsContext nsCtxt, Map<QName, Attribute> attr) {
        super(loc, name, nsCtxt);
        this.mAttrs = attr;
    }

    public static SimpleStartElement construct(Location loc, QName name, Map<QName, Attribute> attrs, List<Namespace> ns, NamespaceContext nsCtxt) {
        BaseNsContext myCtxt = MergedNsContext.construct(nsCtxt, ns);
        return new SimpleStartElement(loc, name, myCtxt, attrs);
    }

    public static SimpleStartElement construct(Location loc, QName name, Iterator<Attribute> attrs, Iterator<Namespace> ns, NamespaceContext nsCtxt) {
        BaseNsContext myCtxt;
        LinkedHashMap<QName, Attribute> attrMap;
        if (attrs == null || !attrs.hasNext()) {
            attrMap = null;
        } else {
            attrMap = new LinkedHashMap<QName, Attribute>();
            do {
                Attribute attr = attrs.next();
                attrMap.put(attr.getName(), attr);
            } while (attrs.hasNext());
        }
        if (ns != null && ns.hasNext()) {
            ArrayList<Namespace> l = new ArrayList<Namespace>();
            do {
                l.add(ns.next());
            } while (ns.hasNext());
            myCtxt = MergedNsContext.construct(nsCtxt, l);
        } else {
            myCtxt = nsCtxt == null ? null : (nsCtxt instanceof BaseNsContext ? (BaseNsContext)nsCtxt : MergedNsContext.construct(nsCtxt, null));
        }
        return new SimpleStartElement(loc, name, myCtxt, attrMap);
    }

    @Override
    public Attribute getAttributeByName(QName name) {
        if (this.mAttrs == null) {
            return null;
        }
        return this.mAttrs.get(name);
    }

    @Override
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrs == null) {
            return DataUtil.emptyIterator();
        }
        return this.mAttrs.values().iterator();
    }

    @Override
    protected void outputNsAndAttr(Writer w) throws IOException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (Attribute attr : this.mAttrs.values()) {
                if (!attr.isSpecified()) continue;
                w.write(32);
                QName name = attr.getName();
                String prefix = name.getPrefix();
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
                w.write(name.getLocalPart());
                w.write("=\"");
                String val = attr.getValue();
                if (val != null && val.length() > 0) {
                    TextEscaper.writeEscapedAttrValue(w, val);
                }
                w.write(34);
            }
        }
    }

    @Override
    protected void outputNsAndAttr(XMLStreamWriter w) throws XMLStreamException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (Attribute attr : this.mAttrs.values()) {
                if (!attr.isSpecified()) continue;
                QName name = attr.getName();
                String prefix = name.getPrefix();
                String ln = name.getLocalPart();
                String nsURI = name.getNamespaceURI();
                w.writeAttribute(prefix, nsURI, ln, attr.getValue());
            }
        }
    }
}

