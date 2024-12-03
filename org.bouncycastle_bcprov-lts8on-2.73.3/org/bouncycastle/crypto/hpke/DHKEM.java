/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.XDHBasicAgreement;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.hpke.HKDF;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP384R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Curve;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

class DHKEM {
    private AsymmetricCipherKeyPairGenerator kpGen;
    private BasicAgreement agreement;
    private final short kemId;
    private HKDF hkdf;
    private byte bitmask;
    private int Nsk;
    private int Nsecret;
    ECDomainParameters domainParams;

    protected DHKEM(short kemid) {
        this.kemId = kemid;
        switch (kemid) {
            case 16: {
                this.hkdf = new HKDF(1);
                SecP256R1Curve curve = new SecP256R1Curve();
                this.domainParams = new ECDomainParameters(curve, curve.createPoint(new BigInteger(1, Hex.decode("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296")), new BigInteger(1, Hex.decode("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5"))), curve.getOrder(), curve.getCofactor(), Hex.decode("c49d360886e704936a6678e1139d26b7819f7e90"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = (byte)-1;
                this.Nsk = 32;
                this.Nsecret = 32;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                break;
            }
            case 17: {
                this.hkdf = new HKDF(2);
                SecP384R1Curve curve = new SecP384R1Curve();
                this.domainParams = new ECDomainParameters(curve, curve.createPoint(new BigInteger(1, Hex.decode("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7")), new BigInteger(1, Hex.decode("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f"))), curve.getOrder(), curve.getCofactor(), Hex.decode("a335926aa319a27a1d00896a6773a4827acdac73"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = (byte)-1;
                this.Nsk = 48;
                this.Nsecret = 48;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                break;
            }
            case 18: {
                this.hkdf = new HKDF(3);
                SecP521R1Curve curve = new SecP521R1Curve();
                this.domainParams = new ECDomainParameters(curve, curve.createPoint(new BigInteger("c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16), new BigInteger("11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16)), curve.getOrder(), curve.getCofactor(), Hex.decode("d09e8800291cb85396cc6717393284aaa0da64ba"));
                this.agreement = new ECDHCBasicAgreement();
                this.bitmask = 1;
                this.Nsk = 66;
                this.Nsecret = 64;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, new SecureRandom()));
                break;
            }
            case 32: {
                this.hkdf = new HKDF(1);
                this.agreement = new XDHBasicAgreement();
                this.Nsecret = 32;
                this.Nsk = 32;
                this.kpGen = new X25519KeyPairGenerator();
                this.kpGen.init(new X25519KeyGenerationParameters(new SecureRandom()));
                break;
            }
            case 33: {
                this.hkdf = new HKDF(3);
                this.agreement = new XDHBasicAgreement();
                this.Nsecret = 64;
                this.Nsk = 56;
                this.kpGen = new X448KeyPairGenerator();
                this.kpGen.init(new X448KeyGenerationParameters(new SecureRandom()));
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid kem id");
            }
        }
    }

    public byte[] SerializePublicKey(AsymmetricKeyParameter key) {
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                return ((ECPublicKeyParameters)key).getQ().getEncoded(false);
            }
            case 33: {
                return ((X448PublicKeyParameters)key).getEncoded();
            }
            case 32: {
                return ((X25519PublicKeyParameters)key).getEncoded();
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    public byte[] SerializePrivateKey(AsymmetricKeyParameter key) {
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                return this.formatBigIntegerBytes(((ECPrivateKeyParameters)key).getD().toByteArray(), this.Nsk);
            }
            case 33: {
                return ((X448PrivateKeyParameters)key).getEncoded();
            }
            case 32: {
                return ((X25519PrivateKeyParameters)key).getEncoded();
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    public AsymmetricKeyParameter DeserializePublicKey(byte[] encoded) {
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                ECPoint G = this.domainParams.getCurve().decodePoint(encoded);
                return new ECPublicKeyParameters(G, this.domainParams);
            }
            case 33: {
                return new X448PublicKeyParameters(encoded);
            }
            case 32: {
                return new X25519PublicKeyParameters(encoded);
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    public AsymmetricCipherKeyPair DeserializePrivateKey(byte[] skEncoded, byte[] pkEncoded) {
        AsymmetricKeyParameter pubParam = this.DeserializePublicKey(pkEncoded);
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                BigInteger d = new BigInteger(1, skEncoded);
                return new AsymmetricCipherKeyPair(pubParam, new ECPrivateKeyParameters(d, ((ECPublicKeyParameters)pubParam).getParameters()));
            }
            case 33: {
                return new AsymmetricCipherKeyPair(pubParam, new X448PrivateKeyParameters(skEncoded));
            }
            case 32: {
                return new AsymmetricCipherKeyPair(pubParam, new X25519PrivateKeyParameters(skEncoded));
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    private boolean ValidateSk(BigInteger d) {
        BigInteger n = this.domainParams.getN();
        int nBitLength = n.bitLength();
        int minWeight = nBitLength >>> 2;
        if (d.compareTo(BigInteger.valueOf(1L)) < 0 || d.compareTo(n) >= 0) {
            return false;
        }
        return WNafUtil.getNafWeight(d) >= minWeight;
    }

    public AsymmetricCipherKeyPair GeneratePrivateKey() {
        return this.kpGen.generateKeyPair();
    }

    public AsymmetricCipherKeyPair DeriveKeyPair(byte[] ikm) {
        if (ikm.length < this.Nsk) {
            throw new IllegalArgumentException("input keying material should have length at least " + this.Nsk + " bytes");
        }
        byte[] suiteID = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                byte[] dkp_prk = this.hkdf.LabeledExtract(null, suiteID, "dkp_prk", ikm);
                int counter = 0;
                byte[] counterArray = new byte[1];
                while (true) {
                    if (counter > 255) {
                        throw new IllegalStateException("DeriveKeyPairError");
                    }
                    counterArray[0] = (byte)counter;
                    byte[] bytes = this.hkdf.LabeledExpand(dkp_prk, suiteID, "candidate", counterArray, this.Nsk);
                    bytes[0] = (byte)(bytes[0] & this.bitmask);
                    BigInteger d = new BigInteger(1, bytes);
                    if (this.ValidateSk(d)) {
                        ECPoint Q = new FixedPointCombMultiplier().multiply(this.domainParams.getG(), d);
                        ECPrivateKeyParameters sk = new ECPrivateKeyParameters(d, this.domainParams);
                        ECPublicKeyParameters pk = new ECPublicKeyParameters(Q, this.domainParams);
                        return new AsymmetricCipherKeyPair(pk, sk);
                    }
                    ++counter;
                }
            }
            case 33: {
                byte[] dkp_prk = this.hkdf.LabeledExtract(null, suiteID, "dkp_prk", ikm);
                byte[] x448sk = this.hkdf.LabeledExpand(dkp_prk, suiteID, "sk", null, this.Nsk);
                X448PrivateKeyParameters x448params = new X448PrivateKeyParameters(x448sk);
                return new AsymmetricCipherKeyPair(x448params.generatePublicKey(), x448params);
            }
            case 32: {
                byte[] dkp_prk = this.hkdf.LabeledExtract(null, suiteID, "dkp_prk", ikm);
                byte[] skBytes = this.hkdf.LabeledExpand(dkp_prk, suiteID, "sk", null, this.Nsk);
                X25519PrivateKeyParameters sk = new X25519PrivateKeyParameters(skBytes);
                return new AsymmetricCipherKeyPair(sk.generatePublicKey(), sk);
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    protected byte[][] Encap(AsymmetricKeyParameter pkR) {
        byte[][] output = new byte[2][];
        AsymmetricCipherKeyPair kpE = this.kpGen.generateKeyPair();
        this.agreement.init(kpE.getPrivate());
        byte[] temp = this.agreement.calculateAgreement(pkR).toByteArray();
        byte[] secret = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        byte[] enc = this.SerializePublicKey(kpE.getPublic());
        byte[] pkRm = this.SerializePublicKey(pkR);
        byte[] KEMContext = Arrays.concatenate(enc, pkRm);
        byte[] sharedSecret = this.ExtractAndExpand(secret, KEMContext);
        output[0] = sharedSecret;
        output[1] = enc;
        return output;
    }

    protected byte[] Decap(byte[] enc, AsymmetricCipherKeyPair kpR) {
        AsymmetricKeyParameter pkE = this.DeserializePublicKey(enc);
        this.agreement.init(kpR.getPrivate());
        byte[] temp = this.agreement.calculateAgreement(pkE).toByteArray();
        byte[] secret = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        byte[] pkRm = this.SerializePublicKey(kpR.getPublic());
        byte[] KEMContext = Arrays.concatenate(enc, pkRm);
        byte[] sharedSecret = this.ExtractAndExpand(secret, KEMContext);
        return sharedSecret;
    }

    protected byte[][] AuthEncap(AsymmetricKeyParameter pkR, AsymmetricCipherKeyPair kpS) {
        byte[][] output = new byte[2][];
        AsymmetricCipherKeyPair kpE = this.kpGen.generateKeyPair();
        this.agreement.init(kpE.getPrivate());
        byte[] temp = this.agreement.calculateAgreement(pkR).toByteArray();
        byte[] secret1 = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        this.agreement.init(kpS.getPrivate());
        temp = this.agreement.calculateAgreement(pkR).toByteArray();
        byte[] secret2 = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        byte[] secret = Arrays.concatenate(secret1, secret2);
        byte[] enc = this.SerializePublicKey(kpE.getPublic());
        byte[] pkRm = this.SerializePublicKey(pkR);
        byte[] pkSm = this.SerializePublicKey(kpS.getPublic());
        byte[] KEMContext = Arrays.concatenate(enc, pkRm, pkSm);
        byte[] sharedSecret = this.ExtractAndExpand(secret, KEMContext);
        output[0] = sharedSecret;
        output[1] = enc;
        return output;
    }

    protected byte[] AuthDecap(byte[] enc, AsymmetricCipherKeyPair kpR, AsymmetricKeyParameter pkS) {
        AsymmetricKeyParameter pkE = this.DeserializePublicKey(enc);
        this.agreement.init(kpR.getPrivate());
        byte[] temp = this.agreement.calculateAgreement(pkE).toByteArray();
        byte[] secret1 = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        this.agreement.init(kpR.getPrivate());
        temp = this.agreement.calculateAgreement(pkS).toByteArray();
        byte[] secret2 = this.formatBigIntegerBytes(temp, this.agreement.getFieldSize());
        byte[] secret = Arrays.concatenate(secret1, secret2);
        byte[] pkRm = this.SerializePublicKey(kpR.getPublic());
        byte[] pkSm = this.SerializePublicKey(pkS);
        byte[] KEMContext = Arrays.concatenate(enc, pkRm, pkSm);
        byte[] sharedSecret = this.ExtractAndExpand(secret, KEMContext);
        return sharedSecret;
    }

    private byte[] ExtractAndExpand(byte[] dh, byte[] kemContext) {
        byte[] suiteID = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        byte[] eae_prk = this.hkdf.LabeledExtract(null, suiteID, "eae_prk", dh);
        byte[] sharedSecret = this.hkdf.LabeledExpand(eae_prk, suiteID, "shared_secret", kemContext, this.Nsecret);
        return sharedSecret;
    }

    private byte[] formatBigIntegerBytes(byte[] bigIntBytes, int outputSize) {
        byte[] output = new byte[outputSize];
        if (bigIntBytes.length <= outputSize) {
            System.arraycopy(bigIntBytes, 0, output, outputSize - bigIntBytes.length, bigIntBytes.length);
        } else {
            System.arraycopy(bigIntBytes, bigIntBytes.length - outputSize, output, 0, outputSize);
        }
        return output;
    }
}

