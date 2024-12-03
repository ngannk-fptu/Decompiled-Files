/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.MessageDigestUtils;

public abstract class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(paramSpec);
    }

    protected abstract AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException;

    public static class OAEP
    extends AlgorithmParametersSpi {
        OAEPParameterSpec currentSpec;

        @Override
        protected byte[] engineGetEncoded() {
            AlgorithmIdentifier hashAlgorithm = new AlgorithmIdentifier(DigestFactory.getOID(this.currentSpec.getDigestAlgorithm()), DERNull.INSTANCE);
            MGF1ParameterSpec mgfSpec = (MGF1ParameterSpec)this.currentSpec.getMGFParameters();
            AlgorithmIdentifier maskGenAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, new AlgorithmIdentifier(DigestFactory.getOID(mgfSpec.getDigestAlgorithm()), DERNull.INSTANCE));
            PSource.PSpecified pSource = (PSource.PSpecified)this.currentSpec.getPSource();
            AlgorithmIdentifier pSourceAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(pSource.getValue()));
            RSAESOAEPparams oaepP = new RSAESOAEPparams(hashAlgorithm, maskGenAlgorithm, pSourceAlgorithm);
            try {
                return oaepP.getEncoded("DER");
            }
            catch (IOException e) {
                throw new RuntimeException("Error encoding OAEPParameters");
            }
        }

        @Override
        protected byte[] engineGetEncoded(String format) {
            if (this.isASN1FormatString(format) || format.equalsIgnoreCase("X.509")) {
                return this.engineGetEncoded();
            }
            return null;
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == OAEPParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
                return this.currentSpec;
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to OAEP parameters object.");
        }

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof OAEPParameterSpec)) {
                throw new InvalidParameterSpecException("OAEPParameterSpec required to initialise an OAEP algorithm parameters object");
            }
            this.currentSpec = (OAEPParameterSpec)paramSpec;
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            try {
                RSAESOAEPparams oaepP = RSAESOAEPparams.getInstance(params);
                if (!oaepP.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
                    throw new IOException("unknown mask generation function: " + oaepP.getMaskGenAlgorithm().getAlgorithm());
                }
                this.currentSpec = new OAEPParameterSpec(MessageDigestUtils.getDigestName(oaepP.getHashAlgorithm().getAlgorithm()), OAEPParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(oaepP.getMaskGenAlgorithm().getParameters()).getAlgorithm())), new PSource.PSpecified(ASN1OctetString.getInstance(oaepP.getPSourceAlgorithm().getParameters()).getOctets()));
            }
            catch (ClassCastException e) {
                throw new IOException("Not a valid OAEP Parameter encoding.");
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new IOException("Not a valid OAEP Parameter encoding.");
            }
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!format.equalsIgnoreCase("X.509") && !format.equalsIgnoreCase("ASN.1")) {
                throw new IOException("Unknown parameter format " + format);
            }
            this.engineInit(params);
        }

        @Override
        protected String engineToString() {
            return "OAEP Parameters";
        }
    }

    public static class PSS
    extends AlgorithmParametersSpi {
        PSSParameterSpec currentSpec;

        @Override
        protected byte[] engineGetEncoded() throws IOException {
            PSSParameterSpec pssSpec = this.currentSpec;
            ASN1ObjectIdentifier digOid = DigestFactory.getOID(pssSpec.getDigestAlgorithm());
            AlgorithmIdentifier hashAlgorithm = NISTObjectIdentifiers.id_shake128.equals(digOid) || NISTObjectIdentifiers.id_shake256.equals(digOid) ? new AlgorithmIdentifier(digOid) : new AlgorithmIdentifier(digOid, DERNull.INSTANCE);
            MGF1ParameterSpec mgfSpec = (MGF1ParameterSpec)pssSpec.getMGFParameters();
            if (mgfSpec != null) {
                AlgorithmIdentifier maskGenAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, new AlgorithmIdentifier(DigestFactory.getOID(mgfSpec.getDigestAlgorithm()), DERNull.INSTANCE));
                RSASSAPSSparams pssP = new RSASSAPSSparams(hashAlgorithm, maskGenAlgorithm, new ASN1Integer(pssSpec.getSaltLength()), new ASN1Integer(pssSpec.getTrailerField()));
                return pssP.getEncoded("DER");
            }
            AlgorithmIdentifier maskGenAlgorithm = new AlgorithmIdentifier(pssSpec.getMGFAlgorithm().equals("SHAKE128") ? NISTObjectIdentifiers.id_shake128 : NISTObjectIdentifiers.id_shake256);
            RSASSAPSSparams pssP = new RSASSAPSSparams(hashAlgorithm, maskGenAlgorithm, new ASN1Integer(pssSpec.getSaltLength()), new ASN1Integer(pssSpec.getTrailerField()));
            return pssP.getEncoded("DER");
        }

        @Override
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (format.equalsIgnoreCase("X.509") || format.equalsIgnoreCase("ASN.1")) {
                return this.engineGetEncoded();
            }
            return null;
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == PSSParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
                return this.currentSpec;
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PSS parameters object.");
        }

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof PSSParameterSpec)) {
                throw new InvalidParameterSpecException("PSSParameterSpec required to initialise an PSS algorithm parameters object");
            }
            this.currentSpec = (PSSParameterSpec)paramSpec;
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            block5: {
                try {
                    RSASSAPSSparams pssP = RSASSAPSSparams.getInstance(params);
                    ASN1ObjectIdentifier mgfOid = pssP.getMaskGenAlgorithm().getAlgorithm();
                    if (mgfOid.equals(PKCSObjectIdentifiers.id_mgf1)) {
                        this.currentSpec = new PSSParameterSpec(MessageDigestUtils.getDigestName(pssP.getHashAlgorithm().getAlgorithm()), PSSParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(pssP.getMaskGenAlgorithm().getParameters()).getAlgorithm())), pssP.getSaltLength().intValue(), pssP.getTrailerField().intValue());
                        break block5;
                    }
                    if (mgfOid.equals(NISTObjectIdentifiers.id_shake128) || mgfOid.equals(NISTObjectIdentifiers.id_shake256)) {
                        this.currentSpec = new PSSParameterSpec(MessageDigestUtils.getDigestName(pssP.getHashAlgorithm().getAlgorithm()), mgfOid.equals(NISTObjectIdentifiers.id_shake128) ? "SHAKE128" : "SHAKE256", null, pssP.getSaltLength().intValue(), pssP.getTrailerField().intValue());
                        break block5;
                    }
                    throw new IOException("unknown mask generation function: " + pssP.getMaskGenAlgorithm().getAlgorithm());
                }
                catch (ClassCastException e) {
                    throw new IOException("Not a valid PSS Parameter encoding.");
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new IOException("Not a valid PSS Parameter encoding.");
                }
            }
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!this.isASN1FormatString(format) && !format.equalsIgnoreCase("X.509")) {
                throw new IOException("Unknown parameter format " + format);
            }
            this.engineInit(params);
        }

        @Override
        protected String engineToString() {
            return "PSS Parameters";
        }
    }
}

