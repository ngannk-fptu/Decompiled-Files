/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;

public class JcaAlgorithmParametersConverter {
    public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameters algorithmParameters) throws InvalidAlgorithmParameterException {
        try {
            ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(algorithmParameters.getEncoded());
            return new AlgorithmIdentifier(aSN1ObjectIdentifier, aSN1Primitive);
        }
        catch (IOException iOException) {
            throw new InvalidAlgorithmParameterException("unable to encode parameters object: " + iOException.getMessage());
        }
    }

    public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof OAEPParameterSpec) {
            AlgorithmIdentifier algorithmIdentifier;
            if (algorithmParameterSpec.equals(OAEPParameterSpec.DEFAULT)) {
                return new AlgorithmIdentifier(aSN1ObjectIdentifier, new RSAESOAEPparams(RSAESOAEPparams.DEFAULT_HASH_ALGORITHM, RSAESOAEPparams.DEFAULT_MASK_GEN_FUNCTION, RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM));
            }
            OAEPParameterSpec oAEPParameterSpec = (OAEPParameterSpec)algorithmParameterSpec;
            PSource pSource = oAEPParameterSpec.getPSource();
            if (!oAEPParameterSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
                throw new InvalidAlgorithmParameterException("only " + OAEPParameterSpec.DEFAULT.getMGFAlgorithm() + " mask generator supported.");
            }
            AlgorithmIdentifier algorithmIdentifier2 = new DefaultDigestAlgorithmIdentifierFinder().find(oAEPParameterSpec.getDigestAlgorithm());
            if (algorithmIdentifier2.getParameters() == null) {
                algorithmIdentifier2 = new AlgorithmIdentifier(algorithmIdentifier2.getAlgorithm(), DERNull.INSTANCE);
            }
            if ((algorithmIdentifier = new DefaultDigestAlgorithmIdentifierFinder().find(((MGF1ParameterSpec)oAEPParameterSpec.getMGFParameters()).getDigestAlgorithm())).getParameters() == null) {
                algorithmIdentifier = new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), DERNull.INSTANCE);
            }
            return new AlgorithmIdentifier(aSN1ObjectIdentifier, new RSAESOAEPparams(algorithmIdentifier2, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifier), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(((PSource.PSpecified)pSource).getValue()))));
        }
        throw new InvalidAlgorithmParameterException("unknown parameter spec passed.");
    }
}

