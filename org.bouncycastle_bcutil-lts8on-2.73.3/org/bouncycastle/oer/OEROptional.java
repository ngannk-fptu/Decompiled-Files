/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Absent
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.oer;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Absent;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OEROptional
extends ASN1Object {
    public static final OEROptional ABSENT = new OEROptional(false, null);
    private final boolean defined;
    private final ASN1Encodable value;

    private OEROptional(boolean defined, ASN1Encodable value) {
        this.defined = defined;
        this.value = value;
    }

    public static OEROptional getInstance(Object o) {
        if (o instanceof OEROptional) {
            return (OEROptional)((Object)o);
        }
        if (o instanceof ASN1Encodable) {
            return new OEROptional(true, (ASN1Encodable)o);
        }
        return ABSENT;
    }

    public static <T> T getValue(Class<T> type, Object src) {
        OEROptional o = OEROptional.getInstance(src);
        if (!o.defined) {
            return null;
        }
        return o.getObject(type);
    }

    public <T> T getObject(final Class<T> type) {
        if (this.defined) {
            if (this.value.getClass().isInstance(type)) {
                return type.cast(this.value);
            }
            return AccessController.doPrivileged(new PrivilegedAction<T>(){

                @Override
                public T run() {
                    try {
                        Method m = type.getMethod("getInstance", Object.class);
                        return type.cast(m.invoke(null, OEROptional.this.value));
                    }
                    catch (Exception ex) {
                        throw new IllegalStateException("could not invoke getInstance on type " + ex.getMessage(), ex);
                    }
                }
            });
        }
        return null;
    }

    public ASN1Encodable get() {
        if (!this.defined) {
            return ABSENT;
        }
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        if (!this.defined) {
            return ASN1Absent.INSTANCE;
        }
        return this.get().toASN1Primitive();
    }

    public boolean isDefined() {
        return this.defined;
    }

    public String toString() {
        if (this.defined) {
            return "OPTIONAL(" + this.value + ")";
        }
        return "ABSENT";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OEROptional that = (OEROptional)((Object)o);
        if (this.defined != that.defined) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.defined ? 1 : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}

