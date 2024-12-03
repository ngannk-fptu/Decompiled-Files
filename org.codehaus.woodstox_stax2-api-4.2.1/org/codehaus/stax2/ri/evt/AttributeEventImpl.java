/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class AttributeEventImpl
extends BaseEventImpl
implements Attribute {
    final QName mName;
    final String mValue;
    final boolean mWasSpecified;

    public AttributeEventImpl(Location loc, String localName, String uri, String prefix, String value, boolean wasSpecified) {
        super(loc);
        this.mValue = value;
        if (prefix == null) {
            this.mName = uri == null ? new QName(localName) : new QName(uri, localName);
        } else {
            if (uri == null) {
                uri = "";
            }
            this.mName = new QName(uri, localName, prefix);
        }
        this.mWasSpecified = wasSpecified;
    }

    public AttributeEventImpl(Location loc, QName name, String value, boolean wasSpecified) {
        super(loc);
        this.mName = name;
        this.mValue = value;
        this.mWasSpecified = wasSpecified;
    }

    @Override
    public int getEventType() {
        return 10;
    }

    @Override
    public boolean isAttribute() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        String prefix = this.mName.getPrefix();
        try {
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this.mName.getLocalPart());
            w.write(61);
            w.write(34);
            AttributeEventImpl.writeEscapedAttrValue(w, this.mValue);
            w.write(34);
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        QName n = this.mName;
        w.writeAttribute(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI(), this.mValue);
    }

    @Override
    public String getDTDType() {
        return "CDATA";
    }

    @Override
    public QName getName() {
        return this.mName;
    }

    @Override
    public String getValue() {
        return this.mValue;
    }

    @Override
    public boolean isSpecified() {
        return this.mWasSpecified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute)o;
        if (this.mName.equals(other.getName()) && this.mValue.equals(other.getValue()) && this.isSpecified() == other.isSpecified()) {
            return AttributeEventImpl.stringsWithNullsEqual(this.getDTDType(), other.getDTDType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.mName.hashCode() ^ this.mValue.hashCode();
    }

    protected static void writeEscapedAttrValue(Writer w, String value) throws IOException {
        int i = 0;
        int len = value.length();
        do {
            int start = i;
            char c = '\u0000';
            while (i < len && (c = value.charAt(i)) != '<' && c != '&' && c != '\"') {
                ++i;
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(value, start, outLen);
            }
            if (i >= len) continue;
            if (c == '<') {
                w.write("&lt;");
                continue;
            }
            if (c == '&') {
                w.write("&amp;");
                continue;
            }
            if (c != '\"') continue;
            w.write("&quot;");
        } while (++i < len);
    }
}

