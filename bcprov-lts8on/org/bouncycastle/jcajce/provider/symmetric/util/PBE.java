/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;

public interface PBE {
    public static final int MD5 = 0;
    public static final int SHA1 = 1;
    public static final int RIPEMD160 = 2;
    public static final int TIGER = 3;
    public static final int SHA256 = 4;
    public static final int MD2 = 5;
    public static final int GOST3411 = 6;
    public static final int SHA224 = 7;
    public static final int SHA384 = 8;
    public static final int SHA512 = 9;
    public static final int SHA3_224 = 10;
    public static final int SHA3_256 = 11;
    public static final int SHA3_384 = 12;
    public static final int SHA3_512 = 13;
    public static final int SM3 = 14;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OPENSSL = 3;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2_UTF8 = 5;

    public static class Util {
        private static PBEParametersGenerator makePBEGenerator(int type, int hash) {
            PBEParametersGenerator generator;
            block35: {
                block37: {
                    block36: {
                        block34: {
                            if (type != 0 && type != 4) break block34;
                            switch (hash) {
                                case 5: {
                                    generator = new PKCS5S1ParametersGenerator(new MD2Digest());
                                    break block35;
                                }
                                case 0: {
                                    generator = new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
                                    break block35;
                                }
                                case 1: {
                                    generator = new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
                                    break block35;
                                }
                                default: {
                                    throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                                }
                            }
                        }
                        if (type != 1 && type != 5) break block36;
                        switch (hash) {
                            case 5: {
                                generator = new PKCS5S2ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                                break block35;
                            }
                            case 0: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createMD5PRF());
                                break block35;
                            }
                            case 1: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA1PRF());
                                break block35;
                            }
                            case 2: {
                                generator = new PKCS5S2ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                                break block35;
                            }
                            case 3: {
                                generator = new PKCS5S2ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                                break block35;
                            }
                            case 4: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA256PRF());
                                break block35;
                            }
                            case 6: {
                                generator = new PKCS5S2ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                                break block35;
                            }
                            case 7: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA224PRF());
                                break block35;
                            }
                            case 8: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA384PRF());
                                break block35;
                            }
                            case 9: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512PRF());
                                break block35;
                            }
                            case 10: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224PRF());
                                break block35;
                            }
                            case 11: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256PRF());
                                break block35;
                            }
                            case 12: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384PRF());
                                break block35;
                            }
                            case 13: {
                                generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512PRF());
                                break block35;
                            }
                            case 14: {
                                generator = new PKCS5S2ParametersGenerator(new SM3Digest(CryptoServicePurpose.PRF));
                                break block35;
                            }
                            default: {
                                throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                            }
                        }
                    }
                    if (type != 2) break block37;
                    switch (hash) {
                        case 5: {
                            generator = new PKCS12ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                            break block35;
                        }
                        case 0: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createMD5PRF());
                            break block35;
                        }
                        case 1: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createSHA1PRF());
                            break block35;
                        }
                        case 2: {
                            generator = new PKCS12ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                            break block35;
                        }
                        case 3: {
                            generator = new PKCS12ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                            break block35;
                        }
                        case 4: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createSHA256PRF());
                            break block35;
                        }
                        case 6: {
                            generator = new PKCS12ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                            break block35;
                        }
                        case 7: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createSHA224PRF());
                            break block35;
                        }
                        case 8: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createSHA384PRF());
                            break block35;
                        }
                        case 9: {
                            generator = new PKCS12ParametersGenerator(DigestFactory.createSHA512PRF());
                            break block35;
                        }
                        default: {
                            throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                        }
                    }
                }
                generator = new OpenSSLPBEParametersGenerator();
            }
            return generator;
        }

        public static CipherParameters makePBEParameters(byte[] pbeKey, int scheme, int digest, int keySize, int ivSize, AlgorithmParameterSpec spec, String targetAlgorithm) throws InvalidAlgorithmParameterException {
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec)spec;
            PBEParametersGenerator generator = Util.makePBEGenerator(scheme, digest);
            byte[] key = pbeKey;
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            CipherParameters param = ivSize != 0 ? generator.generateDerivedParameters(keySize, ivSize) : generator.generateDerivedParameters(keySize);
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter)((ParametersWithIV)param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam = (KeyParameter)param;
                    DESParameters.setOddParity(kParam.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec, String targetAlgorithm) {
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec)spec;
            PBEParametersGenerator generator = Util.makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            if (pbeKey.shouldTryWrongPKCS12()) {
                key = new byte[2];
            }
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            CipherParameters param = pbeKey.getIvSize() != 0 ? generator.generateDerivedParameters(pbeKey.getKeySize(), pbeKey.getIvSize()) : generator.generateDerivedParameters(pbeKey.getKeySize());
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter)((ParametersWithIV)param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam = (KeyParameter)param;
                    DESParameters.setOddParity(kParam.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec) {
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec)spec;
            PBEParametersGenerator generator = Util.makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(pbeKey.getKeySize());
            return param;
        }

        public static CipherParameters makePBEMacParameters(PBEKeySpec keySpec, int type, int hash, int keySize) {
            PBEParametersGenerator generator = Util.makePBEGenerator(type, hash);
            byte[] key = Util.convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != key.length; ++i) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEParameters(PBEKeySpec keySpec, int type, int hash, int keySize, int ivSize) {
            PBEParametersGenerator generator = Util.makePBEGenerator(type, hash);
            byte[] key = Util.convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            CipherParameters param = ivSize != 0 ? generator.generateDerivedParameters(keySize, ivSize) : generator.generateDerivedParameters(keySize);
            for (int i = 0; i != key.length; ++i) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(SecretKey key, int type, int hash, int keySize, PBEParameterSpec pbeSpec) {
            PBEParametersGenerator generator = Util.makePBEGenerator(type, hash);
            byte[] keyBytes = key.getEncoded();
            generator.init(key.getEncoded(), pbeSpec.getSalt(), pbeSpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != keyBytes.length; ++i) {
                keyBytes[i] = 0;
            }
            return param;
        }

        private static byte[] convertPassword(int type, PBEKeySpec keySpec) {
            byte[] key = type == 2 ? PBEParametersGenerator.PKCS12PasswordToBytes(keySpec.getPassword()) : (type == 5 || type == 4 ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(keySpec.getPassword()) : PBEParametersGenerator.PKCS5PasswordToBytes(keySpec.getPassword()));
            return key;
        }
    }
}

