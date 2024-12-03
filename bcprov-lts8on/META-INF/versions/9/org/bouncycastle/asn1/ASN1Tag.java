/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
final class ASN1Tag {
    private final int tagClass;
    private final int tagNumber;

    static ASN1Tag create(int tagClass, int tagNumber) {
        return new ASN1Tag(tagClass, tagNumber);
    }

    private ASN1Tag(int tagClass, int tagNumber) {
        this.tagClass = tagClass;
        this.tagNumber = tagNumber;
    }

    int getTagClass() {
        return this.tagClass;
    }

    int getTagNumber() {
        return this.tagNumber;
    }
}

