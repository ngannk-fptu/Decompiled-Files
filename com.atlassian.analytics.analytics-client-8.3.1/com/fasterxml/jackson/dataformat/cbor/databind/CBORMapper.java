/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor.databind;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import com.fasterxml.jackson.dataformat.cbor.PackageVersion;

public class CBORMapper
extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public CBORMapper() {
        this(new CBORFactory());
    }

    public CBORMapper(CBORFactory f) {
        super(f);
    }

    protected CBORMapper(CBORMapper src) {
        super(src);
    }

    public static Builder builder() {
        return new Builder(new CBORMapper());
    }

    public static Builder builder(CBORFactory streamFactory) {
        return new Builder(new CBORMapper(streamFactory));
    }

    @Override
    public CBORMapper copy() {
        this._checkInvalidCopy(CBORMapper.class);
        return new CBORMapper(this);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public CBORFactory getFactory() {
        return (CBORFactory)this._jsonFactory;
    }

    public static class Builder
    extends MapperBuilder<CBORMapper, Builder> {
        protected final CBORFactory _streamFactory;

        public Builder(CBORMapper m) {
            super(m);
            this._streamFactory = m.getFactory();
        }

        public Builder enable(CBORGenerator.Feature ... features) {
            for (CBORGenerator.Feature f : features) {
                this._streamFactory.enable(f);
            }
            return this;
        }

        public Builder disable(CBORGenerator.Feature ... features) {
            for (CBORGenerator.Feature f : features) {
                this._streamFactory.disable(f);
            }
            return this;
        }

        public Builder configure(CBORGenerator.Feature f, boolean state) {
            if (state) {
                this._streamFactory.enable(f);
            } else {
                this._streamFactory.disable(f);
            }
            return this;
        }
    }
}

