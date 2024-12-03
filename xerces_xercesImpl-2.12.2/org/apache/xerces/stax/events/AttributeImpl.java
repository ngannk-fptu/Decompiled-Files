/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xerces.stax.events.XMLEventImpl;

public class AttributeImpl
extends XMLEventImpl
implements Attribute {
    private final boolean fIsSpecified;
    private final QName fName;
    private final String fValue;
    private final String fDtdType;

    public AttributeImpl(QName qName, String string, String string2, boolean bl, Location location) {
        this(10, qName, string, string2, bl, location);
    }

    protected AttributeImpl(int n, QName qName, String string, String string2, boolean bl, Location location) {
        super(n, location);
        this.fName = qName;
        this.fValue = string;
        this.fDtdType = string2;
        this.fIsSpecified = bl;
    }

    @Override
    public final QName getName() {
        return this.fName;
    }

    @Override
    public final String getValue() {
        return this.fValue;
    }

    @Override
    public final String getDTDType() {
        return this.fDtdType;
    }

    @Override
    public final boolean isSpecified() {
        return this.fIsSpecified;
    }

    @Override
    public final void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            String string = this.fName.getPrefix();
            if (string != null && string.length() > 0) {
                writer.write(string);
                writer.write(58);
            }
            writer.write(this.fName.getLocalPart());
            writer.write("=\"");
            writer.write(this.fValue);
            writer.write(34);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

