/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslCertificateCompressionAlgorithm;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class OpenSslCertificateCompressionConfig
implements Iterable<AlgorithmConfig> {
    private final List<AlgorithmConfig> pairList;

    private OpenSslCertificateCompressionConfig(AlgorithmConfig ... pairs) {
        this.pairList = Collections.unmodifiableList(Arrays.asList(pairs));
    }

    @Override
    public Iterator<AlgorithmConfig> iterator() {
        return this.pairList.iterator();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static enum AlgorithmMode {
        Compress,
        Decompress,
        Both;

    }

    public static final class AlgorithmConfig {
        private final OpenSslCertificateCompressionAlgorithm algorithm;
        private final AlgorithmMode mode;

        private AlgorithmConfig(OpenSslCertificateCompressionAlgorithm algorithm, AlgorithmMode mode) {
            this.algorithm = (OpenSslCertificateCompressionAlgorithm)ObjectUtil.checkNotNull((Object)algorithm, (String)"algorithm");
            this.mode = (AlgorithmMode)((Object)ObjectUtil.checkNotNull((Object)((Object)mode), (String)"mode"));
        }

        public AlgorithmMode mode() {
            return this.mode;
        }

        public OpenSslCertificateCompressionAlgorithm algorithm() {
            return this.algorithm;
        }
    }

    public static final class Builder {
        private final List<AlgorithmConfig> algorithmList = new ArrayList<AlgorithmConfig>();

        private Builder() {
        }

        public Builder addAlgorithm(OpenSslCertificateCompressionAlgorithm algorithm, AlgorithmMode mode) {
            this.algorithmList.add(new AlgorithmConfig(algorithm, mode));
            return this;
        }

        public OpenSslCertificateCompressionConfig build() {
            return new OpenSslCertificateCompressionConfig(this.algorithmList.toArray(new AlgorithmConfig[0]));
        }
    }
}

