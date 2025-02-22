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
    void setParameter(String string, Object object) {
        SecurityManager securityManager = System.getSecurityManager();
        if (string.equals("threadLocalEcImplicitlyCa")) {
            org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec;
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_LOCAL_PERMISSION);
            }
            if ((eCParameterSpec = object instanceof org.bouncycastle.jce.spec.ECParameterSpec || object == null ? (org.bouncycastle.jce.spec.ECParameterSpec)object : EC5Util.convertSpec((ECParameterSpec)object)) == null) {
                this.ecThreadSpec.remove();
                return;
            } else {
                this.ecThreadSpec.set(eCParameterSpec);
            }
            return;
        } else if (string.equals("ecImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_PERMISSION);
            }
            this.ecImplicitCaParams = object instanceof org.bouncycastle.jce.spec.ECParameterSpec || object == null ? (org.bouncycastle.jce.spec.ECParameterSpec)object : EC5Util.convertSpec((ECParameterSpec)object);
            return;
        } else if (string.equals("threadLocalDhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_LOCAL_PERMISSION);
            }
            if (!(object instanceof DHParameterSpec) && !(object instanceof DHParameterSpec[]) && object != null) {
                throw new IllegalArgumentException("not a valid DHParameterSpec");
            }
            Object object2 = object;
            if (object2 == null) {
                this.dhThreadSpec.remove();
                return;
            } else {
                this.dhThreadSpec.set(object2);
            }
            return;
        } else if (string.equals("DhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_PERMISSION);
            }
            if (!(object instanceof DHParameterSpec) && !(object instanceof DHParameterSpec[]) && object != null) throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
            this.dhDefaultParams = object;
            return;
        } else if (string.equals("acceptableEcCurves")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_CURVE_PERMISSION);
            }
            this.acceptableNamedCurves = (Set)object;
            return;
        } else {
            if (!string.equals("additionalEcParameters")) return;
            if (securityManager != null) {
                securityManager.checkPermission(BC_ADDITIONAL_EC_CURVE_PERMISSION);
            }
            this.additionalECParameters = (Map)object;
        }
    }

    public org.bouncycastle.jce.spec.ECParameterSpec getEcImplicitlyCa() {
        org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecThreadSpec.get();
        if (eCParameterSpec != null) {
            return eCParameterSpec;
        }
        return this.ecImplicitCaParams;
    }

    public DHParameterSpec getDHDefaultParameters(int n) {
        Object object;
        Object object2 = this.dhThreadSpec.get();
        if (object2 == null) {
            object2 = this.dhDefaultParams;
        }
        if (object2 instanceof DHParameterSpec) {
            object = (DHParameterSpec[])object2;
            if (object.getP().bitLength() == n) {
                return object;
            }
        } else if (object2 instanceof DHParameterSpec[]) {
            object = (DHParameterSpec[])object2;
            for (int i = 0; i != ((DHParameterSpec[])object).length; ++i) {
                if (object[i].getP().bitLength() != n) continue;
                return object[i];
            }
        }
        if ((object = (DHParameters)CryptoServicesRegistrar.getSizedProperty(CryptoServicesRegistrar.Property.DH_DEFAULT_PARAMS, n)) != null) {
            return new DHDomainParameterSpec((DHParameters)object);
        }
        return null;
    }

    public DSAParameterSpec getDSADefaultParameters(int n) {
        DSAParameters dSAParameters = (DSAParameters)CryptoServicesRegistrar.getSizedProperty(CryptoServicesRegistrar.Property.DSA_DEFAULT_PARAMS, n);
        if (dSAParameters != null) {
            return new DSAParameterSpec(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG());
        }
        return null;
    }

    public Set getAcceptableNamedCurves() {
        return Collections.unmodifiableSet(this.acceptableNamedCurves);
    }

    public Map getAdditionalECParameters() {
        return Collections.unmodifiableMap(this.additionalECParameters);
    }
}

