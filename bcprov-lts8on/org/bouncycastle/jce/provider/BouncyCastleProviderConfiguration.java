/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.Permission;
import java.security.spec.DSAParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.config.ProviderConfigurationPermission;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class BouncyCastleProviderConfiguration
implements ProviderConfiguration {
    private static Permission BC_EC_LOCAL_PERMISSION = new ProviderConfigurationPermission("BC", "threadLocalEcImplicitlyCa");
    private static Permission BC_EC_PERMISSION = new ProviderConfigurationPermission("BC", "ecImplicitlyCa");
    private static Permission BC_DH_LOCAL_PERMISSION = new ProviderConfigurationPermission("BC", "threadLocalDhDefaultParams");
    private static Permission BC_DH_PERMISSION = new ProviderConfigurationPermission("BC", "DhDefaultParams");
    private static Permission BC_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("BC", "acceptableEcCurves");
    private static Permission BC_ADDITIONAL_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("BC", "additionalEcParameters");
    private ThreadLocal ecThreadSpec = new ThreadLocal();
    private ThreadLocal dhThreadSpec = new ThreadLocal();
    private volatile org.bouncycastle.jce.spec.ECParameterSpec ecImplicitCaParams;
    private volatile Object dhDefaultParams;
    private volatile Set acceptableNamedCurves = new HashSet();
    private volatile Map additionalECParameters = new HashMap();

    BouncyCastleProviderConfiguration() {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void setParameter(String parameterName, Object parameter) {
        SecurityManager securityManager = System.getSecurityManager();
        if (parameterName.equals("threadLocalEcImplicitlyCa")) {
            org.bouncycastle.jce.spec.ECParameterSpec curveSpec;
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_LOCAL_PERMISSION);
            }
            if ((curveSpec = parameter instanceof org.bouncycastle.jce.spec.ECParameterSpec || parameter == null ? (org.bouncycastle.jce.spec.ECParameterSpec)parameter : EC5Util.convertSpec((ECParameterSpec)parameter)) == null) {
                this.ecThreadSpec.remove();
                return;
            } else {
                this.ecThreadSpec.set(curveSpec);
            }
            return;
        } else if (parameterName.equals("ecImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_PERMISSION);
            }
            this.ecImplicitCaParams = parameter instanceof org.bouncycastle.jce.spec.ECParameterSpec || parameter == null ? (org.bouncycastle.jce.spec.ECParameterSpec)parameter : EC5Util.convertSpec((ECParameterSpec)parameter);
            return;
        } else if (parameterName.equals("threadLocalDhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_LOCAL_PERMISSION);
            }
            if (!(parameter instanceof DHParameterSpec) && !(parameter instanceof DHParameterSpec[]) && parameter != null) {
                throw new IllegalArgumentException("not a valid DHParameterSpec");
            }
            Object dhSpec = parameter;
            if (dhSpec == null) {
                this.dhThreadSpec.remove();
                return;
            } else {
                this.dhThreadSpec.set(dhSpec);
            }
            return;
        } else if (parameterName.equals("DhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_PERMISSION);
            }
            if (!(parameter instanceof DHParameterSpec) && !(parameter instanceof DHParameterSpec[]) && parameter != null) throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
            this.dhDefaultParams = parameter;
            return;
        } else if (parameterName.equals("acceptableEcCurves")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_CURVE_PERMISSION);
            }
            this.acceptableNamedCurves = (Set)parameter;
            return;
        } else {
            if (!parameterName.equals("additionalEcParameters")) return;
            if (securityManager != null) {
                securityManager.checkPermission(BC_ADDITIONAL_EC_CURVE_PERMISSION);
            }
            this.additionalECParameters = (Map)parameter;
        }
    }

    @Override
    public org.bouncycastle.jce.spec.ECParameterSpec getEcImplicitlyCa() {
        org.bouncycastle.jce.spec.ECParameterSpec spec = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecThreadSpec.get();
        if (spec != null) {
            return spec;
        }
        return this.ecImplicitCaParams;
    }

    @Override
    public DHParameterSpec getDHDefaultParameters(int keySize) {
        DHParameters dhParams;
        Object params = this.dhThreadSpec.get();
        if (params == null) {
            params = this.dhDefaultParams;
        }
        if (params instanceof DHParameterSpec) {
            DHParameterSpec spec = (DHParameterSpec)params;
            if (spec.getP().bitLength() == keySize) {
                return spec;
            }
        } else if (params instanceof DHParameterSpec[]) {
            DHParameterSpec[] specs = (DHParameterSpec[])params;
            for (int i = 0; i != specs.length; ++i) {
                if (specs[i].getP().bitLength() != keySize) continue;
                return specs[i];
            }
        }
        if ((dhParams = (DHParameters)CryptoServicesRegistrar.getSizedProperty(CryptoServicesRegistrar.Property.DH_DEFAULT_PARAMS, keySize)) != null) {
            return new DHDomainParameterSpec(dhParams);
        }
        return null;
    }

    @Override
    public DSAParameterSpec getDSADefaultParameters(int keySize) {
        DSAParameters dsaParams = (DSAParameters)CryptoServicesRegistrar.getSizedProperty(CryptoServicesRegistrar.Property.DSA_DEFAULT_PARAMS, keySize);
        if (dsaParams != null) {
            return new DSAParameterSpec(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
        }
        return null;
    }

    @Override
    public Set getAcceptableNamedCurves() {
        return Collections.unmodifiableSet(this.acceptableNamedCurves);
    }

    @Override
    public Map getAdditionalECParameters() {
        return Collections.unmodifiableMap(this.additionalECParameters);
    }
}

