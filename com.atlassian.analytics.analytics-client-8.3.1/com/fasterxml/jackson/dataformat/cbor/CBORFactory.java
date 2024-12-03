/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.cbor.CBORFactoryBuilder;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.fasterxml.jackson.dataformat.cbor.CBORParserBootstrapper;
import com.fasterxml.jackson.dataformat.cbor.PackageVersion;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

public class CBORFactory
extends JsonFactory {
    private static final long serialVersionUID = 1L;
    public static final String FORMAT_NAME = "CBOR";
    static final int DEFAULT_CBOR_PARSER_FEATURE_FLAGS = CBORParser.Feature.collectDefaults();
    static final int DEFAULT_CBOR_GENERATOR_FEATURE_FLAGS = CBORGenerator.Feature.collectDefaults();
    protected int _formatParserFeatures;
    protected int _formatGeneratorFeatures;

    public CBORFactory() {
        this((ObjectCodec)null);
    }

    public CBORFactory(ObjectCodec oc) {
        super(oc);
        this._formatParserFeatures = DEFAULT_CBOR_PARSER_FEATURE_FLAGS;
        this._formatGeneratorFeatures = DEFAULT_CBOR_GENERATOR_FEATURE_FLAGS;
    }

    public CBORFactory(CBORFactory src, ObjectCodec oc) {
        super(src, oc);
        this._formatParserFeatures = src._formatParserFeatures;
        this._formatGeneratorFeatures = src._formatGeneratorFeatures;
    }

    protected CBORFactory(CBORFactoryBuilder b) {
        super(b, false);
        this._formatParserFeatures = b.formatParserFeaturesMask();
        this._formatGeneratorFeatures = b.formatGeneratorFeaturesMask();
    }

    public CBORFactoryBuilder rebuild() {
        return new CBORFactoryBuilder(this);
    }

    public static CBORFactoryBuilder builder() {
        return new CBORFactoryBuilder();
    }

    @Override
    public CBORFactory copy() {
        this._checkInvalidCopy(CBORFactory.class);
        return new CBORFactory(this, null);
    }

    @Override
    protected Object readResolve() {
        return new CBORFactory(this, this._objectCodec);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }

    @Override
    public boolean canUseCharArrays() {
        return false;
    }

    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        return CBORParserBootstrapper.hasCBORFormat(acc);
    }

    @Override
    public boolean canHandleBinaryNatively() {
        return true;
    }

    public Class<CBORParser.Feature> getFormatReadFeatureType() {
        return CBORParser.Feature.class;
    }

    public Class<CBORGenerator.Feature> getFormatWriteFeatureType() {
        return CBORGenerator.Feature.class;
    }

    public final CBORFactory configure(CBORParser.Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    public CBORFactory enable(CBORParser.Feature f) {
        this._formatParserFeatures |= f.getMask();
        return this;
    }

    public CBORFactory disable(CBORParser.Feature f) {
        this._formatParserFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(CBORParser.Feature f) {
        return (this._formatParserFeatures & f.getMask()) != 0;
    }

    @Override
    public int getFormatParserFeatures() {
        return this._formatParserFeatures;
    }

    public final CBORFactory configure(CBORGenerator.Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    public CBORFactory enable(CBORGenerator.Feature f) {
        this._formatGeneratorFeatures |= f.getMask();
        return this;
    }

    public CBORFactory disable(CBORGenerator.Feature f) {
        this._formatGeneratorFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(CBORGenerator.Feature f) {
        return (this._formatGeneratorFeatures & f.getMask()) != 0;
    }

    @Override
    public int getFormatGeneratorFeatures() {
        return this._formatGeneratorFeatures;
    }

    @Override
    public CBORParser createParser(File f) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(f), true);
        return this._createParser(this._decorate(new FileInputStream(f), ctxt), ctxt);
    }

    @Override
    public CBORParser createParser(URL url) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(url), true);
        return this._createParser(this._decorate(this._optimizedStreamFromURL(url), ctxt), ctxt);
    }

    @Override
    public CBORParser createParser(InputStream in) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(in), false);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public CBORParser createParser(byte[] data) throws IOException {
        return this.createParser(data, 0, data.length);
    }

    @Override
    public CBORParser createParser(byte[] data, int offset, int len) throws IOException {
        InputStream in;
        IOContext ctxt = this._createContext(this._createContentReference(data, offset, len), true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, 0, data.length)) != null) {
            return this._createParser(in, ctxt);
        }
        return this._createParser(data, offset, len, ctxt);
    }

    @Override
    public CBORGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(out), false);
        return this._createCBORGenerator(ctxt, this._generatorFeatures, this._formatGeneratorFeatures, this._objectCodec, this._decorate(out, ctxt));
    }

    @Override
    public CBORGenerator createGenerator(OutputStream out) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(out), false);
        return this._createCBORGenerator(ctxt, this._generatorFeatures, this._formatGeneratorFeatures, this._objectCodec, this._decorate(out, ctxt));
    }

    @Override
    protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged) {
        return super._createContext(contentRef, resourceManaged);
    }

    @Override
    protected CBORParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        return new CBORParserBootstrapper(ctxt, in).constructParser(this._factoryFeatures, this._parserFeatures, this._formatParserFeatures, this._objectCodec, this._byteSymbolCanonicalizer);
    }

    @Override
    protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
        return (JsonParser)this._nonByteSource();
    }

    @Override
    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
        return (JsonParser)this._nonByteSource();
    }

    @Override
    protected CBORParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return new CBORParserBootstrapper(ctxt, data, offset, len).constructParser(this._factoryFeatures, this._parserFeatures, this._formatParserFeatures, this._objectCodec, this._byteSymbolCanonicalizer);
    }

    @Override
    protected CBORGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        return (CBORGenerator)this._nonByteTarget();
    }

    @Override
    protected CBORGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        return this._createCBORGenerator(ctxt, this._generatorFeatures, this._formatGeneratorFeatures, this._objectCodec, out);
    }

    @Override
    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        return (Writer)this._nonByteTarget();
    }

    private final CBORGenerator _createCBORGenerator(IOContext ctxt, int stdFeat, int formatFeat, ObjectCodec codec, OutputStream out) throws IOException {
        CBORGenerator gen = new CBORGenerator(ctxt, stdFeat, formatFeat, this._objectCodec, out);
        if (CBORGenerator.Feature.WRITE_TYPE_HEADER.enabledIn(formatFeat)) {
            gen.writeTag(55799);
        }
        return gen;
    }

    protected <T> T _nonByteSource() {
        throw new UnsupportedOperationException("Can not create parser for non-byte-based source");
    }

    protected <T> T _nonByteTarget() {
        throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
    }
}

