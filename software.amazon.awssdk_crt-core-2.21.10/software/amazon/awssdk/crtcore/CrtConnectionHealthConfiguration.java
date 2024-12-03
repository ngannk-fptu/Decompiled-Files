/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.crtcore;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class CrtConnectionHealthConfiguration {
    private final long minimumThroughputInBps;
    private final Duration minimumThroughputTimeout;

    protected CrtConnectionHealthConfiguration(DefaultBuilder<?> builder) {
        this.minimumThroughputInBps = (Long)Validate.paramNotNull((Object)((DefaultBuilder)builder).minimumThroughputInBps, (String)"minimumThroughputInBps");
        this.minimumThroughputTimeout = Validate.isPositive((Duration)((DefaultBuilder)builder).minimumThroughputTimeout, (String)"minimumThroughputTimeout");
    }

    public final long minimumThroughputInBps() {
        return this.minimumThroughputInBps;
    }

    public final Duration minimumThroughputTimeout() {
        return this.minimumThroughputTimeout;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CrtConnectionHealthConfiguration that = (CrtConnectionHealthConfiguration)o;
        if (this.minimumThroughputInBps != that.minimumThroughputInBps) {
            return false;
        }
        return this.minimumThroughputTimeout.equals(that.minimumThroughputTimeout);
    }

    public int hashCode() {
        int result = (int)(this.minimumThroughputInBps ^ this.minimumThroughputInBps >>> 32);
        result = 31 * result + this.minimumThroughputTimeout.hashCode();
        return result;
    }

    protected static abstract class DefaultBuilder<B extends Builder>
    implements Builder {
        private Long minimumThroughputInBps;
        private Duration minimumThroughputTimeout;

        protected DefaultBuilder() {
        }

        protected DefaultBuilder(CrtConnectionHealthConfiguration configuration) {
            this.minimumThroughputInBps = configuration.minimumThroughputInBps;
            this.minimumThroughputTimeout = configuration.minimumThroughputTimeout;
        }

        public B minimumThroughputInBps(Long minimumThroughputInBps) {
            this.minimumThroughputInBps = minimumThroughputInBps;
            return (B)this;
        }

        public B minimumThroughputTimeout(Duration minimumThroughputTimeout) {
            this.minimumThroughputTimeout = minimumThroughputTimeout;
            return (B)this;
        }
    }

    public static interface Builder {
        public Builder minimumThroughputInBps(Long var1);

        public Builder minimumThroughputTimeout(Duration var1);

        public CrtConnectionHealthConfiguration build();
    }
}

