/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
abstract class ASN1Type {
    final Class javaClass;

    ASN1Type(Class javaClass) {
        this.javaClass = javaClass;
    }

    final Class getJavaClass() {
        return this.javaClass;
    }

    public final boolean equals(Object that) {
        return this == that;
    }

    public final int hashCode() {
        return super.hashCode();
    }
}

