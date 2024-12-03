/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.json;

import java.io.IOException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.ObjectCodec;
import software.amazon.awssdk.thirdparty.jackson.core.SerializableString;
import software.amazon.awssdk.thirdparty.jackson.core.StreamWriteCapability;
import software.amazon.awssdk.thirdparty.jackson.core.Version;
import software.amazon.awssdk.thirdparty.jackson.core.base.GeneratorBase;
import software.amazon.awssdk.thirdparty.jackson.core.io.CharTypes;
import software.amazon.awssdk.thirdparty.jackson.core.io.CharacterEscapes;
import software.amazon.awssdk.thirdparty.jackson.core.io.IOContext;
import software.amazon.awssdk.thirdparty.jackson.core.util.DefaultPrettyPrinter;
import software.amazon.awssdk.thirdparty.jackson.core.util.JacksonFeatureSet;
import software.amazon.awssdk.thirdparty.jackson.core.util.VersionUtil;

public abstract class JsonGeneratorImpl
extends GeneratorBase {
    protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
    protected static final JacksonFeatureSet<StreamWriteCapability> JSON_WRITE_CAPABILITIES = DEFAULT_TEXTUAL_WRITE_CAPABILITIES;
    protected final IOContext _ioContext;
    protected int[] _outputEscapes = sOutputEscapes;
    protected int _maximumNonEscapedChar;
    protected CharacterEscapes _characterEscapes;
    protected SerializableString _rootValueSeparator = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
    protected boolean _cfgUnqNames;
    protected boolean _cfgWriteHexUppercase;

    public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec) {
        super(features, codec);
        this._ioContext = ctxt;
        if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features)) {
            this._maximumNonEscapedChar = 127;
        }
        this._cfgWriteHexUppercase = JsonGenerator.Feature.WRITE_HEX_UPPER_CASE.enabledIn(features);
        this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features);
    }

    @Override
    public Version version() {
        return VersionUtil.versionFor(this.getClass());
    }

    @Override
    public JsonGenerator enable(JsonGenerator.Feature f) {
        super.enable(f);
        if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
            this._cfgUnqNames = false;
        } else if (f == JsonGenerator.Feature.WRITE_HEX_UPPER_CASE) {
            this._cfgWriteHexUppercase = true;
        }
        return this;
    }

    @Override
    public JsonGenerator disable(JsonGenerator.Feature f) {
        super.disable(f);
        if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
            this._cfgUnqNames = true;
        } else if (f == JsonGenerator.Feature.WRITE_HEX_UPPER_CASE) {
            this._cfgWriteHexUppercase = false;
        }
        return this;
    }

    @Override
    protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
        super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
        this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags);
        this._cfgWriteHexUppercase = JsonGenerator.Feature.WRITE_HEX_UPPER_CASE.enabledIn(newFeatureFlags);
    }

    @Override
    public JsonGenerator setHighestNonEscapedChar(int charCode) {
        this._maximumNonEscapedChar = charCode < 0 ? 0 : charCode;
        return this;
    }

    @Override
    public int getHighestEscapedChar() {
        return this._maximumNonEscapedChar;
    }

    @Override
    public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        this._outputEscapes = esc == null ? sOutputEscapes : esc.getEscapeCodesForAscii();
        return this;
    }

    @Override
    public CharacterEscapes getCharacterEscapes() {
        return this._characterEscapes;
    }

    @Override
    public JsonGenerator setRootValueSeparator(SerializableString sep) {
        this._rootValueSeparator = sep;
        return this;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
        return JSON_WRITE_CAPABILITIES;
    }

    protected void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
        switch (status) {
            case 1: {
                this._cfgPrettyPrinter.writeArrayValueSeparator(this);
                break;
            }
            case 2: {
                this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
                break;
            }
            case 3: {
                this._cfgPrettyPrinter.writeRootValueSeparator(this);
                break;
            }
            case 0: {
                if (this._writeContext.inArray()) {
                    this._cfgPrettyPrinter.beforeArrayValues(this);
                    break;
                }
                if (!this._writeContext.inObject()) break;
                this._cfgPrettyPrinter.beforeObjectEntries(this);
                break;
            }
            case 5: {
                this._reportCantWriteValueExpectName(typeMsg);
                break;
            }
            default: {
                this._throwInternal();
            }
        }
    }

    protected void _reportCantWriteValueExpectName(String typeMsg) throws IOException {
        this._reportError(String.format("Can not %s, expecting field name (context: %s)", typeMsg, this._writeContext.typeDesc()));
    }
}

