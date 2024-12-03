/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.InMemoryRepresentable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ASN1OctetStringParser
extends ASN1Encodable,
InMemoryRepresentable {
    public InputStream getOctetStream();
}

