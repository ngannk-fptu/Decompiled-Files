/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;
import org.bouncycastle.cert.crmf.jcajce.CRMFHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JcePKMACValuesCalculator
implements PKMACValuesCalculator {
    private MessageDigest digest;
    private Mac mac;
    private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());

    public JcePKMACValuesCalculator setProvider(Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcePKMACValuesCalculator setProvider(String providerName) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    @Override
    public void setup(AlgorithmIdentifier digAlg, AlgorithmIdentifier macAlg) throws CRMFException {
        this.digest = this.helper.createDigest(digAlg.getAlgorithm());
        this.mac = this.helper.createMac(macAlg.getAlgorithm());
    }

    @Override
    public byte[] calculateDigest(byte[] data) {
        return this.digest.digest(data);
    }

    @Override
    public byte[] calculateMac(byte[] pwd, byte[] data) throws CRMFException {
        try {
            this.mac.init(new SecretKeySpec(pwd, this.mac.getAlgorithm()));
            return this.mac.doFinal(data);
        }
        catch (GeneralSecurityException e) {
            throw new CRMFException("failure in setup: " + e.getMessage(), e);
        }
    }
}

