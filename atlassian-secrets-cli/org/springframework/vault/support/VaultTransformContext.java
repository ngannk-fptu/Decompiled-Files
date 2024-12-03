/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Arrays;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class VaultTransformContext {
    private static final VaultTransformContext EMPTY = new VaultTransformContext("", new byte[0]);
    private final String transformation;
    private final byte[] tweak;

    private VaultTransformContext(String transformation, byte[] tweak) {
        this.transformation = transformation;
        this.tweak = tweak;
    }

    public static VaultTransformRequestBuilder builder() {
        return new VaultTransformRequestBuilder();
    }

    public static VaultTransformContext empty() {
        return EMPTY;
    }

    public static VaultTransformContext fromTransformation(String transformation) {
        return VaultTransformContext.builder().transformation(transformation).build();
    }

    public static VaultTransformContext fromTweak(byte[] tweak) {
        return VaultTransformContext.builder().tweak(tweak).build();
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(this.transformation) && ObjectUtils.isEmpty((Object)this.tweak);
    }

    public String getTransformation() {
        return this.transformation;
    }

    public byte[] getTweak() {
        return this.tweak;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VaultTransformContext)) {
            return false;
        }
        VaultTransformContext that = (VaultTransformContext)o;
        return this.transformation.equals(that.transformation) && Arrays.equals(this.tweak, that.tweak);
    }

    public int hashCode() {
        int result = this.transformation.hashCode();
        result = 31 * result + Arrays.hashCode(this.tweak);
        return result;
    }

    public static class VaultTransformRequestBuilder {
        private String transformation = "";
        private byte[] tweak = new byte[0];

        private VaultTransformRequestBuilder() {
        }

        public VaultTransformRequestBuilder transformation(String transformation) {
            Assert.notNull((Object)transformation, "Transformation must not be null");
            this.transformation = transformation;
            return this;
        }

        public VaultTransformRequestBuilder tweak(byte[] tweak) {
            Assert.notNull((Object)tweak, "Tweak must not be null");
            this.tweak = tweak;
            return this;
        }

        public VaultTransformContext build() {
            return new VaultTransformContext(this.transformation, this.tweak);
        }
    }
}

