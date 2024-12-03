/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.Mac
 *  org.bouncycastle.crypto.engines.DESedeEngine
 *  org.bouncycastle.crypto.engines.RC2Engine
 *  org.bouncycastle.crypto.generators.PKCS12ParametersGenerator
 *  org.bouncycastle.crypto.io.MacOutputStream
 *  org.bouncycastle.crypto.macs.HMac
 *  org.bouncycastle.crypto.modes.CBCBlockCipher
 *  org.bouncycastle.crypto.paddings.BlockCipherPadding
 *  org.bouncycastle.crypto.paddings.PKCS7Padding
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 *  org.bouncycastle.crypto.params.DESedeParameters
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithIV
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Integers;

class PKCS12PBEUtils {
    private static Map keySizes = new HashMap();
    private static Set noIvAlgs = new HashSet();
    private static Set desAlgs = new HashSet();

    PKCS12PBEUtils() {
    }

    static int getKeySize(ASN1ObjectIdentifier algorithm) {
        return (Integer)keySizes.get(algorithm);
    }

    static boolean hasNoIv(ASN1ObjectIdentifier algorithm) {
        return noIvAlgs.contains(algorithm);
    }

    static boolean isDesAlg(ASN1ObjectIdentifier algorithm) {
        return desAlgs.contains(algorithm);
    }

    static PaddedBufferedBlockCipher getEngine(ASN1ObjectIdentifier algorithm) {
        DESedeEngine engine;
        if (algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC) || algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC)) {
            engine = new DESedeEngine();
        } else if (algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC) || algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)) {
            engine = new RC2Engine();
        } else {
            throw new IllegalStateException("unknown algorithm");
        }
        return new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)engine), (BlockCipherPadding)new PKCS7Padding());
    }

    static MacCalculator createMacCalculator(final ASN1ObjectIdentifier digestAlgorithm, ExtendedDigest digest, final PKCS12PBEParams pbeParams, final char[] password) {
        PKCS12ParametersGenerator pGen = new PKCS12ParametersGenerator((Digest)digest);
        pGen.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes((char[])password), pbeParams.getIV(), pbeParams.getIterations().intValue());
        KeyParameter keyParam = (KeyParameter)pGen.generateDerivedMacParameters(digest.getDigestSize() * 8);
        final HMac hMac = new HMac((Digest)digest);
        hMac.init((CipherParameters)keyParam);
        return new MacCalculator(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(digestAlgorithm, (ASN1Encodable)pbeParams);
            }

            @Override
            public OutputStream getOutputStream() {
                return new MacOutputStream((Mac)hMac);
            }

            @Override
            public byte[] getMac() {
                byte[] res = new byte[hMac.getMacSize()];
                hMac.doFinal(res, 0);
                return res;
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), PKCS12ParametersGenerator.PKCS12PasswordToBytes((char[])password));
            }
        };
    }

    static CipherParameters createCipherParameters(ASN1ObjectIdentifier algorithm, ExtendedDigest digest, int blockSize, PKCS12PBEParams pbeParams, char[] password) {
        CipherParameters params;
        PKCS12ParametersGenerator pGen = new PKCS12ParametersGenerator((Digest)digest);
        pGen.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes((char[])password), pbeParams.getIV(), pbeParams.getIterations().intValue());
        if (PKCS12PBEUtils.hasNoIv(algorithm)) {
            params = pGen.generateDerivedParameters(PKCS12PBEUtils.getKeySize(algorithm));
        } else {
            params = pGen.generateDerivedParameters(PKCS12PBEUtils.getKeySize(algorithm), blockSize * 8);
            if (PKCS12PBEUtils.isDesAlg(algorithm)) {
                DESedeParameters.setOddParity((byte[])((KeyParameter)((ParametersWithIV)params).getParameters()).getKey());
            }
        }
        return params;
    }

    static {
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, Integers.valueOf((int)128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf((int)40));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf((int)192));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf((int)128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf((int)128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf((int)40));
        noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
        noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
        desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC);
        desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
    }
}

