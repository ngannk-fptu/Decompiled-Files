/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.CryptoServicePurpose;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface CryptoServiceProperties {
    public int bitsOfSecurity();

    public String getServiceName();

    public CryptoServicePurpose getPurpose();

    public Object getParams();
}

