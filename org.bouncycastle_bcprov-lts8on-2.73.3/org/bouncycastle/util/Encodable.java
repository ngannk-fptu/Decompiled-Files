/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.io.IOException;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface Encodable {
    public byte[] getEncoded() throws IOException;
}

