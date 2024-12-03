/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeAlgorithmSpec
implements AlgorithmParameterSpec {
    private final List<String> algorithmNames;
    private final List<AlgorithmParameterSpec> parameterSpecs;

    public CompositeAlgorithmSpec(Builder builder) {
        this.algorithmNames = Collections.unmodifiableList(new ArrayList(builder.algorithmNames));
        this.parameterSpecs = Collections.unmodifiableList(new ArrayList(builder.parameterSpecs));
    }

    public List<String> getAlgorithmNames() {
        return this.algorithmNames;
    }

    public List<AlgorithmParameterSpec> getParameterSpecs() {
        return this.parameterSpecs;
    }

    public static class Builder {
        private List<String> algorithmNames = new ArrayList<String>();
        private List<AlgorithmParameterSpec> parameterSpecs = new ArrayList<AlgorithmParameterSpec>();

        public Builder add(String algorithmName) {
            this.algorithmNames.add(algorithmName);
            this.parameterSpecs.add(null);
            return this;
        }

        public Builder add(String algorithmName, AlgorithmParameterSpec parameterSpec) {
            this.algorithmNames.add(algorithmName);
            this.parameterSpecs.add(parameterSpec);
            return this;
        }

        public CompositeAlgorithmSpec build() {
            if (this.algorithmNames.isEmpty()) {
                throw new IllegalStateException("cannot call build with no algorithm names added");
            }
            return new CompositeAlgorithmSpec(this);
        }
    }
}

