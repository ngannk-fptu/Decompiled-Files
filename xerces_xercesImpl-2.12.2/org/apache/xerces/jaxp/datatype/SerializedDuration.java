/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.datatype;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;

final class SerializedDuration
implements Serializable {
    private static final long serialVersionUID = 3897193592341225793L;
    private final String lexicalValue;

    public SerializedDuration(String string) {
        this.lexicalValue = string;
    }

    private Object readResolve() throws ObjectStreamException {
        return new DatatypeFactoryImpl().newDuration(this.lexicalValue);
    }
}

