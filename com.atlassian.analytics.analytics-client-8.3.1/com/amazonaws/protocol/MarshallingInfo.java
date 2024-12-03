/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.DefaultValueSupplier;
import com.amazonaws.protocol.MarshallLocation;
import com.amazonaws.protocol.MarshallingType;
import com.amazonaws.util.TimestampFormat;

@SdkProtectedApi
public class MarshallingInfo<T> {
    private final MarshallingType<T> marshallingType;
    private final String marshallLocationName;
    private final MarshallLocation marshallLocation;
    private final boolean isExplicitPayloadMember;
    private final boolean isBinary;
    private final DefaultValueSupplier<T> defaultValueSupplier;
    private final TimestampFormat timestampFormat;

    private MarshallingInfo(Builder<T> builder) {
        this.marshallingType = ((Builder)builder).marshallingType;
        this.marshallLocationName = ((Builder)builder).marshallLocationName;
        this.marshallLocation = ((Builder)builder).marshallLocation;
        this.isExplicitPayloadMember = ((Builder)builder).isExplicitPayloadMember;
        this.isBinary = ((Builder)builder).isBinary;
        this.defaultValueSupplier = ((Builder)builder).defaultValueSupplier;
        this.timestampFormat = TimestampFormat.fromValue(((Builder)builder).timestampFormat);
    }

    public MarshallingType<T> marshallingType() {
        return this.marshallingType;
    }

    public String marshallLocationName() {
        return this.marshallLocationName;
    }

    public MarshallLocation marshallLocation() {
        return this.marshallLocation;
    }

    public boolean isExplicitPayloadMember() {
        return this.isExplicitPayloadMember;
    }

    public boolean isBinary() {
        return this.isBinary;
    }

    public DefaultValueSupplier<T> defaultValueSupplier() {
        return this.defaultValueSupplier;
    }

    public TimestampFormat timestampFormat() {
        return this.timestampFormat;
    }

    public static <T> Builder<T> builder(MarshallingType<T> marshallingType) {
        return new Builder(marshallingType);
    }

    public static final class Builder<T> {
        private final MarshallingType<T> marshallingType;
        private String marshallLocationName;
        private MarshallLocation marshallLocation;
        private boolean isExplicitPayloadMember;
        private boolean isBinary;
        private DefaultValueSupplier<T> defaultValueSupplier;
        private String timestampFormat;

        private Builder(MarshallingType<T> marshallingType) {
            this.marshallingType = marshallingType;
        }

        public Builder<T> marshallLocationName(String marshallLocationName) {
            this.marshallLocationName = marshallLocationName;
            return this;
        }

        public Builder<T> marshallLocation(MarshallLocation marshallLocation) {
            this.marshallLocation = marshallLocation;
            return this;
        }

        public Builder<T> isExplicitPayloadMember(boolean isExplicitPayloadMember) {
            this.isExplicitPayloadMember = isExplicitPayloadMember;
            return this;
        }

        public Builder<T> isBinary(boolean isBinary) {
            this.isBinary = isBinary;
            return this;
        }

        public Builder<T> defaultValueSupplier(DefaultValueSupplier<T> defaultValueSupplier) {
            this.defaultValueSupplier = defaultValueSupplier;
            return this;
        }

        public Builder<T> timestampFormat(String timestampFormat) {
            this.timestampFormat = timestampFormat;
            return this;
        }

        public MarshallingInfo<T> build() {
            return new MarshallingInfo(this);
        }
    }
}

