/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.PBEParametersGenerator
 *  org.bouncycastle.crypto.Wrapper
 *  org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithIV
 */
package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;
import org.bouncycastle.cms.bc.CMSUtils;
import org.bouncycastle.cms.bc.EnvelopedDataHelper;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;

public class BcPasswordRecipientInfoGenerator
extends PasswordRecipientInfoGenerator {
    public BcPasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password) {
        super(kekAlgorithm, password);
    }

    @Override
    protected byte[] calculateDerivedKey(int schemeID, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
        PBKDF2Params params = PBKDF2Params.getInstance((Object)derivationAlgorithm.getParameters());
        byte[] encodedPassword = schemeID == 0 ? PBEParametersGenerator.PKCS5PasswordToBytes((char[])this.password) : PBEParametersGenerator.PKCS5PasswordToUTF8Bytes((char[])this.password);
        try {
            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator((Digest)EnvelopedDataHelper.getPRF(params.getPrf()));
            gen.init(encodedPassword, params.getSalt(), params.getIterationCount().intValue());
            return ((KeyParameter)gen.generateDerivedParameters(keySize)).getKey();
        }
        catch (Exception e) {
            throw new CMSException("exception creating derived key: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateEncryptedBytes(AlgorithmIdentifier keyEncryptionAlgorithm, byte[] derivedKey, GenericKey contentEncryptionKey) throws CMSException {
        byte[] contentEncryptionKeySpec = ((KeyParameter)CMSUtils.getBcKey(contentEncryptionKey)).getKey();
        Wrapper keyEncryptionCipher = EnvelopedDataHelper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());
        keyEncryptionCipher.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(derivedKey), ASN1OctetString.getInstance((Object)keyEncryptionAlgorithm.getParameters()).getOctets()));
        return keyEncryptionCipher.wrap(contentEncryptionKeySpec, 0, contentEncryptionKeySpec.length);
    }
}

