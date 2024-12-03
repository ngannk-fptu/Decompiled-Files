/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.datatype;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;

final class SerializedXMLGregorianCalendar
implements Serializable {
    private static final long serialVersionUID = -7752272381890705397L;
    private final String lexicalValue;

    public SerializedXMLGregorianCalendar(String string) {
        this.lexicalValue = string;
    }

    private Object readResolve() throws ObjectStreamException {
        return new DatatypeFactoryImpl().newXMLGregorianCalendar(this.lexicalValue);
    }
}

