/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.spec.IESKEMParameterSpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

public class JceETSIKeyWrapper
implements ETSIKeyWrapper {
    private final ECPublicKey recipientKey;
    private final byte[] recipientHash;
    private final JcaJceHelper helper;

    private JceETSIKeyWrapper(ECPublicKey key, byte[] recipientHash, JcaJceHelper helper) {
        this.recipientKey = key;
        this.recipientHash = recipientHash;
        this.helper = helper;
    }

    @Override
    public EncryptedDataEncryptionKey wrap(byte[] secretKey) {
        try {
            Cipher etsiKem = this.helper.createCipher("ETSIKEMwithSHA256");
            etsiKem.init(3, (Key)this.recipientKey, (AlgorithmParameterSpec)new IESKEMParameterSpec(this.recipientHash, true));
            byte[] wrappedKey = etsiKem.wrap(new SecretKeySpec(secretKey, "AES"));
            int size = (this.recipientKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            size = wrappedKey[0] == 4 ? 2 * size + 1 : ++size;
            SubjectPublicKeyInfo pkInfo = SubjectPublicKeyInfo.getInstance((Object)this.recipientKey.getEncoded());
            ASN1ObjectIdentifier curveID = ASN1ObjectIdentifier.getInstance((Object)pkInfo.getAlgorithm().getParameters());
            EciesP256EncryptedKey key = EciesP256EncryptedKey.builder().setV(EccP256CurvePoint.createEncodedPoint((byte[])Arrays.copyOfRange((byte[])wrappedKey, (int)0, (int)size))).setC(Arrays.copyOfRange((byte[])wrappedKey, (int)size, (int)(size + secretKey.length))).setT(Arrays.copyOfRange((byte[])wrappedKey, (int)(size + secretKey.length), (int)wrappedKey.length)).createEciesP256EncryptedKey();
            if (curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
                return EncryptedDataEncryptionKey.eciesNistP256((EciesP256EncryptedKey)key);
            }
            if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
                return EncryptedDataEncryptionKey.eciesBrainpoolP256r1((EciesP256EncryptedKey)key);
            }
            throw new IllegalStateException("recipient key curve is not P-256 or Brainpool P256r1");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public static class Builder {
        private final ECPublicKey recipientKey;
        private final byte[] recipientHash;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder(ECPublicKey recipientKey, byte[] recipientHash) {
            this.recipientKey = recipientKey;
            this.recipientHash = recipientHash;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String providerName) {
            this.helper = new NamedJcaJceHelper(providerName);
            return this;
        }

        public JceETSIKeyWrapper build() {
            return new JceETSIKeyWrapper(this.recipientKey, this.recipientHash, this.helper);
        }
    }
}

