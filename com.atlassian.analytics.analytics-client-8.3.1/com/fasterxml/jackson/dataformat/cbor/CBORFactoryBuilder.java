/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

public class CBORFactoryBuilder
extends TSFBuilder<CBORFactory, CBORFactoryBuilder> {
    protected int _formatParserFeatures;
    protected int _formatGeneratorFeatures;

    protected CBORFactoryBuilder() {
        this._formatParserFeatures = CBORFactory.DEFAULT_CBOR_PARSER_FEATURE_FLAGS;
        this._formatGeneratorFeatures = CBORFactory.DEFAULT_CBOR_GENERATOR_FEATURE_FLAGS;
    }

    public CBORFactoryBuilder(CBORFactory base) {
        super(base);
        this._formatParserFeatures = base._formatParserFeatures;
        this._formatGeneratorFeatures = base._formatGeneratorFeatures;
    }

    public CBORFactoryBuilder enable(CBORParser.Feature f) {
        this._formatParserFeatures |= f.getMask();
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder enable(CBORParser.Feature first, CBORParser.Feature ... other) {
        this._formatParserFeatures |= first.getMask();
        for (CBORParser.Feature f : other) {
            this._formatParserFeatures |= f.getMask();
        }
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder disable(CBORParser.Feature f) {
        this._formatParserFeatures &= ~f.getMask();
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder disable(CBORParser.Feature first, CBORParser.Feature ... other) {
        this._formatParserFeatures &= ~first.getMask();
        for (CBORParser.Feature f : other) {
            this._formatParserFeatures &= ~f.getMask();
        }
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder configure(CBORParser.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public CBORFactoryBuilder enable(CBORGenerator.Feature f) {
        this._formatGeneratorFeatures |= f.getMask();
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder enable(CBORGenerator.Feature first, CBORGenerator.Feature ... other) {
        this._formatGeneratorFeatures |= first.getMask();
        for (CBORGenerator.Feature f : other) {
            this._formatGeneratorFeatures |= f.getMask();
        }
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder disable(CBORGenerator.Feature f) {
        this._formatGeneratorFeatures &= ~f.getMask();
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder disable(CBORGenerator.Feature first, CBORGenerator.Feature ... other) {
        this._formatGeneratorFeatures &= ~first.getMask();
        for (CBORGenerator.Feature f : other) {
            this._formatGeneratorFeatures &= ~f.getMask();
        }
        return (CBORFactoryBuilder)this._this();
    }

    public CBORFactoryBuilder configure(CBORGenerator.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public int formatParserFeaturesMask() {
        return this._formatParserFeatures;
    }

    public int formatGeneratorFeaturesMask() {
        return this._formatGeneratorFeatures;
    }

    @Override
    public CBORFactory build() {
        return new CBORFactory(this);
    }
}

