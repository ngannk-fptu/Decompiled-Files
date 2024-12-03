/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.ri.evt.AttributeEventImpl
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.evt.BaseStartElement;
import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;

public class CompactStartElement
extends BaseStartElement {
    private static final int OFFSET_NS_URI = 1;
    private static final int OFFSET_NS_PREFIX = 2;
    private static final int OFFSET_VALUE = 3;
    final ElemAttrs mAttrs;
    final String[] mRawAttrs;
    private ArrayList<Attribute> mAttrList = null;

    protected CompactStartElement(Location loc, QName name, BaseNsContext nsCtxt, ElemAttrs attrs) {
        super(loc, name, nsCtxt);
        this.mAttrs = attrs;
        this.mRawAttrs = attrs == null ? null : attrs.getRawAttrs();
    }

    @Override
    public Attribute getAttributeByName(QName name) {
        if (this.mAttrs == null) {
            return null;
        }
        int ix = this.mAttrs.findIndex(name);
        if (ix < 0) {
            return null;
        }
        return this.constructAttr(this.mRawAttrs, ix, this.mAttrs.isDefault(ix));
    }

    @Override
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrList == null) {
            if (this.mAttrs == null) {
                return DataUtil.emptyIterator();
            }
            String[] rawAttrs = this.mRawAttrs;
            int rawLen = rawAttrs.length;
            int defOffset = this.mAttrs.getFirstDefaultOffset();
            if (rawLen == 4) {
                return DataUtil.singletonIterator(this.constructAttr(rawAttrs, 0, defOffset == 0));
            }
            ArrayList<Attribute> l = new ArrayList<Attribute>(rawLen >> 2);
            for (int i = 0; i < rawLen; i += 4) {
                l.add(this.constructAttr(rawAttrs, i, i >= defOffset));
            }
            this.mAttrList = l;
        }
        return this.mAttrList.iterator();
    }

    @Override
    protected void outputNsAndAttr(Writer w) throws IOException {
        String[] raw;
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if ((raw = this.mRawAttrs) != null) {
            int len = raw.length;
            for (int i = 0; i < len; i += 4) {
                w.write(32);
                String prefix = raw[i + 2];
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
                w.write(raw[i]);
                w.write("=\"");
                TextEscaper.writeEscapedAttrValue(w, raw[i + 3]);
                w.write(34);
            }
        }
    }

    @Override
    protected void outputNsAndAttr(XMLStreamWriter w) throws XMLStreamException {
        String[] raw;
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if ((raw = this.mRawAttrs) != null) {
            int len = raw.length;
            for (int i = 0; i < len; i += 4) {
                String ln = raw[i];
                String prefix = raw[i + 2];
                String nsURI = raw[i + 1];
                w.writeAttribute(prefix, nsURI, ln, raw[i + 3]);
            }
        }
    }

    protected Attribute constructAttr(String[] raw, int rawIndex, boolean isDef) {
        return new AttributeEventImpl(this.getLocation(), raw[rawIndex], raw[rawIndex + 1], raw[rawIndex + 2], raw[rawIndex + 3], !isDef);
    }
}

