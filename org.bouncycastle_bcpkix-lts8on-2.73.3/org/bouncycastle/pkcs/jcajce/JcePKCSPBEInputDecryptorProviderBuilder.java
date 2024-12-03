/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.cryptopro.GOST28147Parameters
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.misc.ScryptParams
 *  org.bouncycastle.asn1.pkcs.PBEParameter
 *  org.bouncycastle.asn1.pkcs.PBES2Parameters
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CharToByteConverter
 *  org.bouncycastle.crypto.PasswordConverter
 *  org.bouncycastle.jcajce.PBKDF1Key
 *  org.bouncycastle.jcajce.PKCS12KeyWithParameters
 *  org.bouncycastle.jcajce.io.CipherInputStream
 *  org.bouncycastle.jcajce.spec.GOST28147ParameterSpec
 *  org.bouncycastle.jcajce.spec.PBKDF2KeySpec
 *  org.bouncycastle.jcajce.spec.ScryptKeySpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JcePKCSPBEInputDecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private boolean wrongPKCS12Zero = false;
    private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;

    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setTryWrongPKCS12Zero(boolean tryWrong) {
        this.wrongPKCS12Zero = tryWrong;
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setKeySizeProvider(SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }

    public InputDecryptorProvider build(final char[] password) {
        return new InputDecryptorProvider(){
            private Cipher cipher;
            private AlgorithmIdentifier encryptionAlg;

            @Override
            public InputDecryptor get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                block13: {
                    ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
                    try {
                        if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                            PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters());
                            this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                            this.cipher.init(2, (Key)new PKCS12KeyWithParameters(password, JcePKCSPBEInputDecryptorProviderBuilder.this.wrongPKCS12Zero, pbeParams.getIV(), pbeParams.getIterations().intValue()));
                            this.encryptionAlg = algorithmIdentifier;
                            break block13;
                        }
                        if (algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.id_PBES2)) {
                            SecretKey key;
                            PBES2Parameters alg = PBES2Parameters.getInstance((Object)algorithmIdentifier.getParameters());
                            if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive)alg.getKeyDerivationFunc().getAlgorithm())) {
                                ScryptParams params = ScryptParams.getInstance((Object)alg.getKeyDerivationFunc().getParameters());
                                AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance((Object)alg.getEncryptionScheme());
                                SecretKeyFactory keyFact = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory("SCRYPT");
                                key = keyFact.generateSecret((KeySpec)new ScryptKeySpec(password, params.getSalt(), params.getCostParameter().intValue(), params.getBlockSize().intValue(), params.getParallelizationParameter().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(encScheme)));
                            } else {
                                SecretKeyFactory keyFact = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory(alg.getKeyDerivationFunc().getAlgorithm().getId());
                                PBKDF2Params func = PBKDF2Params.getInstance((Object)alg.getKeyDerivationFunc().getParameters());
                                AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance((Object)alg.getEncryptionScheme());
                                key = func.isDefaultPrf() ? keyFact.generateSecret(new PBEKeySpec(password, func.getSalt(), func.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(encScheme))) : keyFact.generateSecret((KeySpec)new PBKDF2KeySpec(password, func.getSalt(), func.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(encScheme), func.getPrf()));
                            }
                            this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(alg.getEncryptionScheme().getAlgorithm().getId());
                            this.encryptionAlg = AlgorithmIdentifier.getInstance((Object)alg.getEncryptionScheme());
                            ASN1Encodable encParams = alg.getEncryptionScheme().getParameters();
                            if (encParams instanceof ASN1OctetString) {
                                this.cipher.init(2, (Key)key, new IvParameterSpec(ASN1OctetString.getInstance((Object)encParams).getOctets()));
                            } else if (encParams instanceof ASN1Sequence && JcePKCSPBEInputDecryptorProviderBuilder.this.isCCMorGCM((ASN1Encodable)alg.getEncryptionScheme())) {
                                AlgorithmParameters params = AlgorithmParameters.getInstance(alg.getEncryptionScheme().getAlgorithm().getId());
                                params.init(((ASN1Sequence)encParams).getEncoded());
                                this.cipher.init(2, (Key)key, params);
                            } else if (encParams == null) {
                                this.cipher.init(2, key);
                            } else {
                                GOST28147Parameters gParams = GOST28147Parameters.getInstance((Object)encParams);
                                this.cipher.init(2, (Key)key, (AlgorithmParameterSpec)new GOST28147ParameterSpec(gParams.getEncryptionParamSet(), gParams.getIV()));
                            }
                            break block13;
                        }
                        if (algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC) || algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC)) {
                            PBEParameter pbeParams = PBEParameter.getInstance((Object)algorithmIdentifier.getParameters());
                            this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                            this.cipher.init(2, (Key)new PBKDF1Key(password, (CharToByteConverter)PasswordConverter.ASCII), new PBEParameterSpec(pbeParams.getSalt(), pbeParams.getIterationCount().intValue()));
                            break block13;
                        }
                        throw new OperatorCreationException("unable to create InputDecryptor: algorithm " + algorithm + " unknown.");
                    }
                    catch (Exception e) {
                        throw new OperatorCreationException("unable to create InputDecryptor: " + e.getMessage(), e);
                    }
                }
                return new InputDecryptor(){

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return encryptionAlg;
                    }

                    @Override
                    public InputStream getInputStream(InputStream input) {
                        return new CipherInputStream(input, cipher);
                    }
                };
            }
        };
    }

    private boolean isCCMorGCM(ASN1Encodable encParams) {
        ASN1Sequence seq;
        AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance((Object)encParams);
        ASN1Encodable params = algId.getParameters();
        if (params instanceof ASN1Sequence && (seq = ASN1Sequence.getInstance((Object)params)).size() == 2) {
            return seq.getObjectAt(1) instanceof ASN1Integer;
        }
        return false;
    }
}

