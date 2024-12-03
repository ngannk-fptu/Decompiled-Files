/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class TypedXMLStreamException
extends XMLStreamException {
    private static final long serialVersionUID = 1L;
    protected String mLexical;

    public TypedXMLStreamException(String string, String string2) {
        super(string2);
        this.mLexical = string;
    }

    public TypedXMLStreamException(String string, IllegalArgumentException illegalArgumentException) {
        super(illegalArgumentException);
        this.mLexical = string;
    }

    public TypedXMLStreamException(String string, String string2, IllegalArgumentException illegalArgumentException) {
        super(string2, illegalArgumentException);
        this.mLexical = string;
    }

    public TypedXMLStreamException(String string, String string2, Location location, IllegalArgumentException illegalArgumentException) {
        super(string2, location, illegalArgumentException);
        this.mLexical = string;
    }

    public TypedXMLStreamException(String string, String string2, Location location) {
        super(string2, location);
        this.mLexical = string;
    }

    public String getLexical() {
        return this.mLexical;
    }
}

