/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.config;

import java.security.spec.DSAParameterSpec;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ProviderConfiguration {
    public ECParameterSpec getEcImplicitlyCa();

    public DHParameterSpec getDHDefaultParameters(int var1);

    public DSAParameterSpec getDSADefaultParameters(int var1);

    public Set getAcceptableNamedCurves();

    public Map getAdditionalECParameters();
}

