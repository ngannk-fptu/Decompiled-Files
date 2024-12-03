/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DefaultServiceProperties
implements CryptoServiceProperties {
    private final String algorithm;
    private final int bitsOfSecurity;
    private final Object params;
    private final CryptoServicePurpose purpose;

    public DefaultServiceProperties(String algorithm, int bitsOfSecurity) {
        this(algorithm, bitsOfSecurity, null, CryptoServicePurpose.ANY);
    }

    public DefaultServiceProperties(String algorithm, int bitsOfSecurity, Object params) {
        this(algorithm, bitsOfSecurity, params, CryptoServicePurpose.ANY);
    }

    public DefaultServiceProperties(String algorithm, int bitsOfSecurity, Object params, CryptoServicePurpose purpose) {
        this.algorithm = algorithm;
        this.bitsOfSecurity = bitsOfSecurity;
        this.params = params;
        if (params instanceof CryptoServicePurpose) {
            throw new IllegalArgumentException("params should not be CryptoServicePurpose");
        }
        this.purpose = purpose;
    }

    @Override
    public int bitsOfSecurity() {
        return this.bitsOfSecurity;
    }

    @Override
    public String getServiceName() {
        return this.algorithm;
    }

    @Override
    public CryptoServicePurpose getPurpose() {
        return this.purpose;
    }

    @Override
    public Object getParams() {
        return this.params;
    }
}

