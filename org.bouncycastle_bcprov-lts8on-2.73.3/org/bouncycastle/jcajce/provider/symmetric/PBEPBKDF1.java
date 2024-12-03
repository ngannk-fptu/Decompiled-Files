/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class PBEPBKDF1 {
    private PBEPBKDF1() {
    }

    public static class AlgParams
    extends BaseAlgorithmParameters {
        PBEParameter params;

        @Override
        protected byte[] engineGetEncoded() {
            try {
                return this.params.getEncoded("DER");
            }
            catch (IOException e) {
                throw new RuntimeException("Oooops! " + e.toString());
            }
        }

        @Override
        protected byte[] engineGetEncoded(String format) {
            if (this.isASN1FormatString(format)) {
                return this.engineGetEncoded();
            }
            return null;
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == PBEParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF1 PBE parameters object.");
        }

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF1 PBE parameters algorithm parameters object");
            }
            PBEParameterSpec pbeSpec = (PBEParameterSpec)paramSpec;
            this.params = new PBEParameter(pbeSpec.getSalt(), pbeSpec.getIterationCount());
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            this.params = PBEParameter.getInstance(params);
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (this.isASN1FormatString(format)) {
                this.engineInit(params);
                return;
            }
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
        }

        @Override
        protected String engineToString() {
            return "PBKDF1 Parameters";
        }
    }

    public static class Mappings
    extends AlgorithmProvider {
        private static final String PREFIX = PBEPBKDF1.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.PBKDF1", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBKDF1");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBKDF1");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC, "PBKDF1");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBKDF1");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC, "PBKDF1");
        }
    }
}

