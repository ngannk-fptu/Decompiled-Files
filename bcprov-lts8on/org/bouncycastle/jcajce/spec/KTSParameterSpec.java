/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class KTSParameterSpec
implements AlgorithmParameterSpec {
    private final String wrappingKeyAlgorithm;
    private final int keySizeInBits;
    private final AlgorithmParameterSpec parameterSpec;
    private final AlgorithmIdentifier kdfAlgorithm;
    private byte[] otherInfo;

    protected KTSParameterSpec(String wrappingKeyAlgorithm, int keySizeInBits, AlgorithmParameterSpec parameterSpec, AlgorithmIdentifier kdfAlgorithm, byte[] otherInfo) {
        this.wrappingKeyAlgorithm = wrappingKeyAlgorithm;
        this.keySizeInBits = keySizeInBits;
        this.parameterSpec = parameterSpec;
        this.kdfAlgorithm = kdfAlgorithm;
        this.otherInfo = otherInfo;
    }

    public String getKeyAlgorithmName() {
        return this.wrappingKeyAlgorithm;
    }

    public int getKeySize() {
        return this.keySizeInBits;
    }

    public AlgorithmParameterSpec getParameterSpec() {
        return this.parameterSpec;
    }

    public AlgorithmIdentifier getKdfAlgorithm() {
        return this.kdfAlgorithm;
    }

    public byte[] getOtherInfo() {
        return Arrays.clone(this.otherInfo);
    }

    public static final class Builder {
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmParameterSpec parameterSpec;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        public Builder(String algorithmName, int keySizeInBits) {
            this(algorithmName, keySizeInBits, null);
        }

        public Builder(String algorithmName, int keySizeInBits, byte[] otherInfo) {
            this.algorithmName = algorithmName;
            this.keySizeInBits = keySizeInBits;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = otherInfo == null ? new byte[]{} : Arrays.clone(otherInfo);
        }

        public Builder withParameterSpec(AlgorithmParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
            return this;
        }

        public Builder withKdfAlgorithm(AlgorithmIdentifier kdfAlgorithm) {
            this.kdfAlgorithm = kdfAlgorithm;
            return this;
        }

        public KTSParameterSpec build() {
            return new KTSParameterSpec(this.algorithmName, this.keySizeInBits, this.parameterSpec, this.kdfAlgorithm, this.otherInfo);
        }
    }
}

