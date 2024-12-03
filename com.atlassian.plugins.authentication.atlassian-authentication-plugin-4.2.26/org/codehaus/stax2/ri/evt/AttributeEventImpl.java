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

    public AttributeEventImpl(Location location, String string, String string2, String string3, String string4, boolean bl) {
        super(location);
        this.mValue = string4;
        if (string3 == null) {
            this.mName = string2 == null ? new QName(string) : new QName(string2, string);
        } else {
            if (string2 == null) {
                string2 = "";
            }
            this.mName = new QName(string2, string, string3);
        }
        this.mWasSpecified = bl;
    }

    public AttributeEventImpl(Location location, QName qName, String string, boolean bl) {
        super(location);
        this.mName = qName;
        this.mValue = string;
        this.mWasSpecified = bl;
    }

    public int getEventType() {
        return 10;
    }

    public boolean isAttribute() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        String string = this.mName.getPrefix();
        try {
            if (string != null && string.length() > 0) {
                writer.write(string);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            writer.write(61);
            writer.write(34);
            AttributeEventImpl.writeEscapedAttrValue(writer, this.mValue);
            writer.write(34);
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        QName qName = this.mName;
        xMLStreamWriter2.writeAttribute(qName.getPrefix(), qName.getLocalPart(), qName.getNamespaceURI(), this.mValue);
    }

    public String getDTDType() {
        return "CDATA";
    }

    public QName getName() {
        return this.mName;
    }

    public String getValue() {
        return this.mValue;
    }

    public boolean isSpecified() {
        return this.mWasSpecified;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute)object;
        if (this.mName.equals(attribute.getName()) && this.mValue.equals(attribute.getValue()) && this.isSpecified() == attribute.isSpecified()) {
            return AttributeEventImpl.stringsWithNullsEqual(this.getDTDType(), attribute.getDTDType());
        }
        return false;
    }

    public int hashCode() {
        return this.mName.hashCode() ^ this.mValue.hashCode();
    }

    protected static void writeEscapedAttrValue(Writer writer, String string) throws IOException {
        int n = 0;
        int n2 = string.length();
        do {
            int n3 = n;
            char c = '\u0000';
            while (n < n2 && (c = string.charAt(n)) != '<' && c != '&' && c != '\"') {
                ++n;
            }
            int n4 = n - n3;
            if (n4 > 0) {
                writer.write(string, n3, n4);
            }
            if (n >= n2) continue;
            if (c == '<') {
                writer.write("&lt;");
                continue;
            }
            if (c == '&') {
                writer.write("&amp;");
                continue;
            }
            if (c != '\"') continue;
            writer.write("&quot;");
        } while (++n < n2);
    }
}

