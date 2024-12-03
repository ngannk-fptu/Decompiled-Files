/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ParsingException;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class LazyConstructionEnumeration
implements Enumeration {
    private ASN1InputStream aIn;
    private Object nextObj;

    public LazyConstructionEnumeration(byte[] encoded) {
        this.aIn = new ASN1InputStream(encoded, true);
        this.nextObj = this.readObject();
    }

    @Override
    public boolean hasMoreElements() {
        return this.nextObj != null;
    }

    public Object nextElement() {
        if (this.nextObj != null) {
            Object o = this.nextObj;
            this.nextObj = this.readObject();
            return o;
        }
        throw new NoSuchElementException();
    }

    private Object readObject() {
        try {
            return this.aIn.readObject();
        }
        catch (IOException e) {
            throw new ASN1ParsingException("malformed ASN.1: " + e, e);
        }
    }
}

