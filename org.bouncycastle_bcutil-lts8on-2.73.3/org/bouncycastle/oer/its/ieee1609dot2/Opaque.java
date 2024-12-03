/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.util.Arrays;

public class Opaque
extends ASN1Object {
    private final byte[] content;

    public Opaque(byte[] content) {
        this.content = Arrays.clone((byte[])content);
    }

    private Opaque(ASN1OctetString value) {
        this(value.getOctets());
    }

    public static Opaque getInstance(Object src) {
        if (src instanceof Opaque) {
            return (Opaque)((Object)src);
        }
        if (src != null) {
            return new Opaque(ASN1OctetString.getInstance((Object)src));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.content);
    }

    public byte[] getContent() {
        return this.content;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public static <T> T getValue(final Class<T> type, final Element definition, final Opaque src) {
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                try {
                    ASN1Encodable value = OERInputStream.parse(src.content, definition);
                    Method m = type.getMethod("getInstance", Object.class);
                    return type.cast(m.invoke(null, value));
                }
                catch (Exception ex) {
                    throw new IllegalStateException("could not invoke getInstance on type " + ex.getMessage(), ex);
                }
            }
        });
    }
}

