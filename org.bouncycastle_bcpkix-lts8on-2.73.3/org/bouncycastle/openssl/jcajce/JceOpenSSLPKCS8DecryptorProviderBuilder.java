/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.EncryptionScheme
 *  org.bouncycastle.asn1.pkcs.KeyDerivationFunc
 *  org.bouncycastle.asn1.pkcs.PBEParameter
 *  org.bouncycastle.asn1.pkcs.PBES2Parameters
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CharToByteConverter
 *  org.bouncycastle.jcajce.PBKDF1KeyWithParameters
 *  org.bouncycastle.jcajce.PKCS12KeyWithParameters
 *  org.bouncycastle.jcajce.io.CipherInputStream
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.PEMUtilities;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Strings;

public class JceOpenSSLPKCS8DecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public InputDecryptorProvider build(final char[] password) throws OperatorCreationException {
        return new InputDecryptorProvider(){

            @Override
            public InputDecryptor get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
                try {
                    Cipher cipher;
                    if (PEMUtilities.isPKCS5Scheme2(algorithm.getAlgorithm())) {
                        PBES2Parameters params = PBES2Parameters.getInstance((Object)algorithm.getParameters());
                        KeyDerivationFunc func = params.getKeyDerivationFunc();
                        EncryptionScheme scheme = params.getEncryptionScheme();
                        PBKDF2Params defParams = (PBKDF2Params)func.getParameters();
                        int iterationCount = defParams.getIterationCount().intValue();
                        byte[] salt = defParams.getSalt();
                        String oid = scheme.getAlgorithm().getId();
                        SecretKey key = PEMUtilities.isHmacSHA1(defParams.getPrf()) ? PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, oid, password, salt, iterationCount) : PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, oid, password, salt, iterationCount, defParams.getPrf());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(scheme.getAlgorithm()));
                        AlgorithmParameters algParams = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(oid);
                        algParams.init(scheme.getParameters().toASN1Primitive().getEncoded());
                        cipher.init(2, (Key)key, algParams);
                    } else if (PEMUtilities.isPKCS12(algorithm.getAlgorithm())) {
                        PKCS12PBEParams params = PKCS12PBEParams.getInstance((Object)algorithm.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(algorithm.getAlgorithm()));
                        cipher.init(2, (Key)new PKCS12KeyWithParameters(password, params.getIV(), params.getIterations().intValue()));
                    } else if (PEMUtilities.isPKCS5Scheme1(algorithm.getAlgorithm())) {
                        PBEParameter params = PBEParameter.getInstance((Object)algorithm.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(algorithm.getAlgorithm()));
                        cipher.init(2, (Key)new PBKDF1KeyWithParameters(password, new CharToByteConverter(){

                            public String getType() {
                                return "ASCII";
                            }

                            public byte[] convert(char[] password) {
                                return Strings.toByteArray((char[])password);
                            }
                        }, params.getSalt(), params.getIterationCount().intValue()));
                    } else {
                        throw new PEMException("Unknown algorithm: " + algorithm.getAlgorithm());
                    }
                    return new InputDecryptor(){

                        @Override
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return algorithm;
                        }

                        @Override
                        public InputStream getInputStream(InputStream encIn) {
                            return new CipherInputStream(encIn, cipher);
                        }
                    };
                }
                catch (IOException e) {
                    throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + e.getMessage(), e);
                }
                catch (GeneralSecurityException e) {
                    throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + e.getMessage(), e);
                }
            }
        };
    }
}

