/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.io.UTF8Writer;
import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingByteBufferJsonParser;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.JacksonFeature;
import com.fasterxml.jackson.core.util.JsonGeneratorDecorator;
import com.fasterxml.jackson.core.util.JsonRecyclerPools;
import com.fasterxml.jackson.core.util.RecyclerPool;
import java.io.CharArrayReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonFactory
extends TokenStreamFactory
implements Versioned,
Serializable {
    private static final long serialVersionUID = 2L;
    public static final String FORMAT_NAME_JSON = "JSON";
    protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
    protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
    protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
    public static final char DEFAULT_QUOTE_CHAR = '\"';
    protected final transient CharsToNameCanonicalizer _rootCharSymbols;
    protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
    protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
    protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
    protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
    protected RecyclerPool<BufferRecycler> _recyclerPool;
    protected ObjectCodec _objectCodec;
    protected CharacterEscapes _characterEscapes;
    protected StreamReadConstraints _streamReadConstraints;
    protected ErrorReportConfiguration _errorReportConfiguration;
    protected StreamWriteConstraints _streamWriteConstraints;
    protected InputDecorator _inputDecorator;
    protected OutputDecorator _outputDecorator;
    protected final List<JsonGeneratorDecorator> _generatorDecorators;
    protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
    protected int _maximumNonEscapedChar;
    protected final char _quoteChar;

    public JsonFactory() {
        this((ObjectCodec)null);
    }

    public JsonFactory(ObjectCodec oc) {
        this._recyclerPool = JsonRecyclerPools.defaultPool();
        this._objectCodec = oc;
        this._quoteChar = (char)34;
        this._streamReadConstraints = StreamReadConstraints.defaults();
        this._streamWriteConstraints = StreamWriteConstraints.defaults();
        this._errorReportConfiguration = ErrorReportConfiguration.defaults();
        this._generatorDecorators = null;
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot(this);
    }

    protected JsonFactory(JsonFactory src, ObjectCodec codec) {
        this._recyclerPool = src._recyclerPool;
        this._objectCodec = codec;
        this._factoryFeatures = src._factoryFeatures;
        this._parserFeatures = src._parserFeatures;
        this._generatorFeatures = src._generatorFeatures;
        this._inputDecorator = src._inputDecorator;
        this._outputDecorator = src._outputDecorator;
        this._generatorDecorators = JsonFactory._copy(src._generatorDecorators);
        this._streamReadConstraints = Objects.requireNonNull(src._streamReadConstraints);
        this._streamWriteConstraints = Objects.requireNonNull(src._streamWriteConstraints);
        this._errorReportConfiguration = Objects.requireNonNull(src._errorReportConfiguration);
        this._characterEscapes = src._characterEscapes;
        this._rootValueSeparator = src._rootValueSeparator;
        this._maximumNonEscapedChar = src._maximumNonEscapedChar;
        this._quoteChar = src._quoteChar;
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot(this);
    }

    public JsonFactory(JsonFactoryBuilder b) {
        this._recyclerPool = b._recyclerPool;
        this._objectCodec = null;
        this._factoryFeatures = b._factoryFeatures;
        this._parserFeatures = b._streamReadFeatures;
        this._generatorFeatures = b._streamWriteFeatures;
        this._inputDecorator = b._inputDecorator;
        this._outputDecorator = b._outputDecorator;
        this._generatorDecorators = JsonFactory._copy(b._generatorDecorators);
        this._streamReadConstraints = Objects.requireNonNull(b._streamReadConstraints);
        this._streamWriteConstraints = Objects.requireNonNull(b._streamWriteConstraints);
        this._errorReportConfiguration = Objects.requireNonNull(b._errorReportConfiguration);
        this._characterEscapes = b._characterEscapes;
        this._rootValueSeparator = b._rootValueSeparator;
        this._maximumNonEscapedChar = b._maximumNonEscapedChar;
        this._quoteChar = b._quoteChar;
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot(this);
    }

    protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
        this._recyclerPool = b._recyclerPool;
        this._objectCodec = null;
        this._factoryFeatures = b._factoryFeatures;
        this._parserFeatures = b._streamReadFeatures;
        this._generatorFeatures = b._streamWriteFeatures;
        this._inputDecorator = b._inputDecorator;
        this._outputDecorator = b._outputDecorator;
        this._generatorDecorators = JsonFactory._copy(b._generatorDecorators);
        this._streamReadConstraints = Objects.requireNonNull(b._streamReadConstraints);
        this._streamWriteConstraints = Objects.requireNonNull(b._streamWriteConstraints);
        this._errorReportConfiguration = Objects.requireNonNull(b._errorReportConfiguration);
        this._characterEscapes = null;
        this._rootValueSeparator = null;
        this._maximumNonEscapedChar = 0;
        this._quoteChar = (char)34;
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot(this);
    }

    public TSFBuilder<?, ?> rebuild() {
        this._requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
        return new JsonFactoryBuilder(this);
    }

    public static TSFBuilder<?, ?> builder() {
        return new JsonFactoryBuilder();
    }

    public JsonFactory copy() {
        this._checkInvalidCopy(JsonFactory.class);
        return new JsonFactory(this, null);
    }

    protected void _checkInvalidCopy(Class<?> exp) {
        if (this.getClass() != exp) {
            throw new IllegalStateException("Failed copy(): " + this.getClass().getName() + " (version: " + this.version() + ") does not override copy(); it has to");
        }
    }

    protected static <T> List<T> _copy(List<T> src) {
        if (src == null) {
            return src;
        }
        return new ArrayList<T>(src);
    }

    protected Object readResolve() {
        return new JsonFactory(this, this._objectCodec);
    }

    @Override
    public boolean requiresPropertyOrdering() {
        return false;
    }

    @Override
    public boolean canHandleBinaryNatively() {
        return false;
    }

    public boolean canUseCharArrays() {
        return true;
    }

    @Override
    public boolean canParseAsync() {
        return this._isJSONFactory();
    }

    @Override
    public Class<? extends FormatFeature> getFormatReadFeatureType() {
        return null;
    }

    @Override
    public Class<? extends FormatFeature> getFormatWriteFeatureType() {
        return null;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        if (schema == null) {
            return false;
        }
        String ourFormat = this.getFormatName();
        return ourFormat != null && ourFormat.equals(schema.getSchemaType());
    }

    @Override
    public String getFormatName() {
        if (this.getClass() == JsonFactory.class) {
            return FORMAT_NAME_JSON;
        }
        return null;
    }

    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        if (this.getClass() == JsonFactory.class) {
            return this.hasJSONFormat(acc);
        }
        return null;
    }

    public boolean requiresCustomCodec() {
        return false;
    }

    protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
        return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Deprecated
    public final JsonFactory configure(Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    @Deprecated
    public JsonFactory enable(Feature f) {
        this._factoryFeatures |= f.getMask();
        return this;
    }

    @Deprecated
    public JsonFactory disable(Feature f) {
        this._factoryFeatures &= ~f.getMask();
        return this;
    }

    @Override
    public final boolean isEnabled(Feature f) {
        return (this._factoryFeatures & f.getMask()) != 0;
    }

    @Override
    public final int getFactoryFeatures() {
        return this._factoryFeatures;
    }

    @Override
    public final int getParserFeatures() {
        return this._parserFeatures;
    }

    @Override
    public final int getGeneratorFeatures() {
        return this._generatorFeatures;
    }

    @Override
    public int getFormatParserFeatures() {
        return 0;
    }

    @Override
    public int getFormatGeneratorFeatures() {
        return 0;
    }

    @Override
    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    @Override
    public StreamWriteConstraints streamWriteConstraints() {
        return this._streamWriteConstraints;
    }

    public JsonFactory setStreamReadConstraints(StreamReadConstraints src) {
        this._streamReadConstraints = Objects.requireNonNull(src);
        return this;
    }

    public JsonFactory setErrorReportConfiguration(ErrorReportConfiguration src) {
        this._errorReportConfiguration = Objects.requireNonNull(src, "Cannot pass null ErrorReportConfiguration");
        return this;
    }

    public JsonFactory setStreamWriteConstraints(StreamWriteConstraints swc) {
        this._streamWriteConstraints = Objects.requireNonNull(swc);
        return this;
    }

    public final JsonFactory configure(JsonParser.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public JsonFactory enable(JsonParser.Feature f) {
        this._parserFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonParser.Feature f) {
        this._parserFeatures &= ~f.getMask();
        return this;
    }

    @Override
    public final boolean isEnabled(JsonParser.Feature f) {
        return (this._parserFeatures & f.getMask()) != 0;
    }

    @Override
    public final boolean isEnabled(StreamReadFeature f) {
        return (this._parserFeatures & f.mappedFeature().getMask()) != 0;
    }

    public InputDecorator getInputDecorator() {
        return this._inputDecorator;
    }

    @Deprecated
    public JsonFactory setInputDecorator(InputDecorator d) {
        this._inputDecorator = d;
        return this;
    }

    public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public JsonFactory enable(JsonGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonGenerator.Feature f) {
        this._generatorFeatures &= ~f.getMask();
        return this;
    }

    @Override
    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0;
    }

    @Override
    public final boolean isEnabled(StreamWriteFeature f) {
        return (this._generatorFeatures & f.mappedFeature().getMask()) != 0;
    }

    public CharacterEscapes getCharacterEscapes() {
        return this._characterEscapes;
    }

    public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        return this;
    }

    public OutputDecorator getOutputDecorator() {
        return this._outputDecorator;
    }

    @Deprecated
    public JsonFactory setOutputDecorator(OutputDecorator d) {
        this._outputDecorator = d;
        return this;
    }

    public JsonFactory setRootValueSeparator(String sep) {
        this._rootValueSeparator = sep == null ? null : new SerializedString(sep);
        return this;
    }

    public String getRootValueSeparator() {
        return this._rootValueSeparator == null ? null : this._rootValueSeparator.getValue();
    }

    public JsonFactory setRecyclerPool(RecyclerPool<BufferRecycler> p) {
        this._recyclerPool = Objects.requireNonNull(p);
        return this;
    }

    public JsonFactory setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override
    public JsonParser createParser(File f) throws IOException, JsonParseException {
        IOContext ctxt = this._createContext(this._createContentReference(f), true);
        InputStream in = this._fileInputStream(f);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public JsonParser createParser(URL url) throws IOException, JsonParseException {
        IOContext ctxt = this._createContext(this._createContentReference(url), true);
        InputStream in = this._optimizedStreamFromURL(url);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
        IOContext ctxt = this._createContext(this._createContentReference(in), false);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public JsonParser createParser(Reader r) throws IOException, JsonParseException {
        IOContext ctxt = this._createContext(this._createContentReference(r), false);
        return this._createParser(this._decorate(r, ctxt), ctxt);
    }

    @Override
    public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
        InputStream in;
        IOContext ctxt = this._createContext(this._createContentReference(data), true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, 0, data.length)) != null) {
            return this._createParser(in, ctxt);
        }
        return this._createParser(data, 0, data.length, ctxt);
    }

    @Override
    public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
        InputStream in;
        this._checkRangeBoundsForByteArray(data, offset, len);
        IOContext ctxt = this._createContext(this._createContentReference(data, offset, len), true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, offset, len)) != null) {
            return this._createParser(in, ctxt);
        }
        return this._createParser(data, offset, len, ctxt);
    }

    @Override
    public JsonParser createParser(String content) throws IOException, JsonParseException {
        int strLen = content.length();
        if (this._inputDecorator != null || strLen > 32768 || !this.canUseCharArrays()) {
            return this.createParser(new StringReader(content));
        }
        IOContext ctxt = this._createContext(this._createContentReference(content), true);
        char[] buf = ctxt.allocTokenBuffer(strLen);
        content.getChars(0, strLen, buf, 0);
        return this._createParser(buf, 0, strLen, ctxt, true);
    }

    @Override
    public JsonParser createParser(char[] content) throws IOException {
        return this.createParser(content, 0, content.length);
    }

    @Override
    public JsonParser createParser(char[] content, int offset, int len) throws IOException {
        this._checkRangeBoundsForCharArray(content, offset, len);
        if (this._inputDecorator != null) {
            return this.createParser(new CharArrayReader(content, offset, len));
        }
        return this._createParser(content, offset, len, this._createContext(this._createContentReference(content, offset, len), true), false);
    }

    @Override
    public JsonParser createParser(DataInput in) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(in), false);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public JsonParser createNonBlockingByteArrayParser() throws IOException {
        this._requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
        IOContext ctxt = this._createNonBlockingContext(null);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChildOrPlaceholder(this._factoryFeatures);
        return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
    }

    @Override
    public JsonParser createNonBlockingByteBufferParser() throws IOException {
        this._requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
        IOContext ctxt = this._createNonBlockingContext(null);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChildOrPlaceholder(this._factoryFeatures);
        return new NonBlockingByteBufferJsonParser(ctxt, this._parserFeatures, can);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(out), false);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            return this._createUTF8Generator(this._decorate(out, ctxt), ctxt);
        }
        Writer w = this._createWriter(out, enc, ctxt);
        return this._createGenerator(this._decorate(w, ctxt), ctxt);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) throws IOException {
        return this.createGenerator(out, JsonEncoding.UTF8);
    }

    @Override
    public JsonGenerator createGenerator(Writer w) throws IOException {
        IOContext ctxt = this._createContext(this._createContentReference(w), false);
        return this._createGenerator(this._decorate(w, ctxt), ctxt);
    }

    @Override
    public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
        OutputStream out = this._fileOutputStream(f);
        IOContext ctxt = this._createContext(this._createContentReference(out), true);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            return this._createUTF8Generator(this._decorate(out, ctxt), ctxt);
        }
        Writer w = this._createWriter(out, enc, ctxt);
        return this._createGenerator(this._decorate(w, ctxt), ctxt);
    }

    @Override
    public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
        return this.createGenerator(this._createDataOutputWrapper(out), enc);
    }

    @Override
    public JsonGenerator createGenerator(DataOutput out) throws IOException {
        return this.createGenerator(this._createDataOutputWrapper(out), JsonEncoding.UTF8);
    }

    @Deprecated
    public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
        return this.createParser(f);
    }

    @Deprecated
    public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
        return this.createParser(url);
    }

    @Deprecated
    public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
        return this.createParser(in);
    }

    @Deprecated
    public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
        return this.createParser(r);
    }

    @Deprecated
    public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
        return this.createParser(data);
    }

    @Deprecated
    public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
        return this.createParser(data, offset, len);
    }

    @Deprecated
    public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
        return this.createParser(content);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        return this.createGenerator(out, enc);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(Writer out) throws IOException {
        return this.createGenerator(out);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
        return this.createGenerator(out, JsonEncoding.UTF8);
    }

    protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        try {
            return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
        }
        catch (IOException | RuntimeException e) {
            if (ctxt.isResourceManaged()) {
                try {
                    in.close();
                }
                catch (Exception e2) {
                    e.addSuppressed(e2);
                }
            }
            ctxt.close();
            throw e;
        }
    }

    protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
        return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild());
    }

    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
        return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(), data, offset, offset + len, recyclable);
    }

    protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
    }

    protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
        this._requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
        int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChildOrPlaceholder(this._factoryFeatures);
        return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
    }

    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        SerializableString rootSep;
        WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
        if (this._maximumNonEscapedChar > 0) {
            gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
        }
        if (this._characterEscapes != null) {
            gen.setCharacterEscapes(this._characterEscapes);
        }
        if ((rootSep = this._rootValueSeparator) != DEFAULT_ROOT_VALUE_SEPARATOR) {
            gen.setRootValueSeparator(rootSep);
        }
        return this._decorate(gen);
    }

    protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        SerializableString rootSep;
        UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
        if (this._maximumNonEscapedChar > 0) {
            gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
        }
        if (this._characterEscapes != null) {
            gen.setCharacterEscapes(this._characterEscapes);
        }
        if ((rootSep = this._rootValueSeparator) != DEFAULT_ROOT_VALUE_SEPARATOR) {
            gen.setRootValueSeparator(rootSep);
        }
        return this._decorate(gen);
    }

    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        if (enc == JsonEncoding.UTF8) {
            return new UTF8Writer(ctxt, out);
        }
        return new OutputStreamWriter(out, enc.getJavaName());
    }

    protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
        InputStream in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
        Reader in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
        DataInput in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
        OutputStream out2;
        if (this._outputDecorator != null && (out2 = this._outputDecorator.decorate(ctxt, out)) != null) {
            return out2;
        }
        return out;
    }

    protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
        Writer out2;
        if (this._outputDecorator != null && (out2 = this._outputDecorator.decorate(ctxt, out)) != null) {
            return out2;
        }
        return out;
    }

    protected JsonGenerator _decorate(JsonGenerator g) {
        if (this._generatorDecorators != null) {
            for (JsonGeneratorDecorator decorator : this._generatorDecorators) {
                g = decorator.decorate(this, g);
            }
        }
        return g;
    }

    public BufferRecycler _getBufferRecycler() {
        return this._getRecyclerPool().acquireAndLinkPooled();
    }

    public RecyclerPool<BufferRecycler> _getRecyclerPool() {
        if (!Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
            return JsonRecyclerPools.nonRecyclingPool();
        }
        return this._recyclerPool;
    }

    protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged) {
        if (contentRef == null) {
            contentRef = ContentReference.unknown();
        }
        return new IOContext(this._streamReadConstraints, this._streamWriteConstraints, this._errorReportConfiguration, this._getBufferRecycler(), contentRef, resourceManaged);
    }

    @Deprecated
    protected IOContext _createContext(Object rawContentRef, boolean resourceManaged) {
        return new IOContext(this._streamReadConstraints, this._streamWriteConstraints, this._errorReportConfiguration, this._getBufferRecycler(), this._createContentReference(rawContentRef), resourceManaged);
    }

    protected IOContext _createNonBlockingContext(Object srcRef) {
        return new IOContext(this._streamReadConstraints, this._streamWriteConstraints, this._errorReportConfiguration, this._getBufferRecycler(), this._createContentReference(srcRef), false);
    }

    protected ContentReference _createContentReference(Object contentAccessor) {
        return ContentReference.construct(!this.canHandleBinaryNatively(), contentAccessor, this._errorReportConfiguration);
    }

    protected ContentReference _createContentReference(Object contentAccessor, int offset, int length) {
        return ContentReference.construct(!this.canHandleBinaryNatively(), contentAccessor, offset, length, this._errorReportConfiguration);
    }

    private final void _requireJSONFactory(String msg) {
        if (!this._isJSONFactory()) {
            throw new UnsupportedOperationException(String.format(msg, this.getFormatName()));
        }
    }

    private final boolean _isJSONFactory() {
        return this.getFormatName() == FORMAT_NAME_JSON;
    }

    public static enum Feature implements JacksonFeature
    {
        INTERN_FIELD_NAMES(true),
        CANONICALIZE_FIELD_NAMES(true),
        FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
        USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true),
        CHARSET_DETECTION(true);

        private final boolean _defaultState;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : Feature.values()) {
                if (!f.enabledByDefault()) continue;
                flags |= f.getMask();
            }
            return flags;
        }

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
        }

        @Override
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override
        public boolean enabledIn(int flags) {
            return (flags & this.getMask()) != 0;
        }

        @Override
        public int getMask() {
            return 1 << this.ordinal();
        }
    }
}

