/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECCurve;

public class ECKeyUtil {
    public static ECPublicKey createKeyWithCompression(ECPublicKey ecPublicKey) {
        return new ECPublicKeyWithCompression(ecPublicKey);
    }

    private static class ECPublicKeyWithCompression
    implements ECPublicKey {
        private final ECPublicKey ecPublicKey;

        public ECPublicKeyWithCompression(ECPublicKey ecPublicKey) {
            this.ecPublicKey = ecPublicKey;
        }

        @Override
        public ECPoint getW() {
            return this.ecPublicKey.getW();
        }

        @Override
        public String getAlgorithm() {
            return this.ecPublicKey.getAlgorithm();
        }

        @Override
        public String getFormat() {
            return this.ecPublicKey.getFormat();
        }

        @Override
        public byte[] getEncoded() {
            ECCurve curve;
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(this.ecPublicKey.getEncoded());
            X962Parameters params = X962Parameters.getInstance(publicKeyInfo.getAlgorithm().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)params.getParameters();
                X9ECParametersHolder x9 = CustomNamedCurves.getByOIDLazy(oid);
                if (x9 == null) {
                    x9 = ECNamedCurveTable.getByOIDLazy(oid);
                }
                curve = x9.getCurve();
            } else {
                if (params.isImplicitlyCA()) {
                    throw new IllegalStateException("unable to identify implictlyCA");
                }
                X9ECParameters x9 = X9ECParameters.getInstance(params.getParameters());
                curve = x9.getCurve();
            }
            org.bouncycastle.math.ec.ECPoint p = curve.decodePoint(publicKeyInfo.getPublicKeyData().getOctets());
            ASN1OctetString pEnc = ASN1OctetString.getInstance(new X9ECPoint(p, true).toASN1Primitive());
            try {
                return new SubjectPublicKeyInfo(publicKeyInfo.getAlgorithm(), pEnc.getOctets()).getEncoded();
            }
            catch (IOException e) {
                throw new IllegalStateException("unable to encode EC public key: " + e.getMessage());
            }
        }

        @Override
        public ECParameterSpec getParams() {
            return this.ecPublicKey.getParams();
        }
    }
}

