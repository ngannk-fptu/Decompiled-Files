/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import org.glassfish.json.JsonTokenizer;

final class JsonMessages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.glassfish.json.messages");

    JsonMessages() {
    }

    static String INTERNAL_ERROR() {
        return JsonMessages.localize("internal.error", new Object[0]);
    }

    static String TOKENIZER_UNEXPECTED_CHAR(int unexpected, JsonLocation location) {
        return JsonMessages.localize("tokenizer.unexpected.char", unexpected, location);
    }

    static String TOKENIZER_EXPECTED_CHAR(int unexpected, JsonLocation location, char expected) {
        return JsonMessages.localize("tokenizer.expected.char", unexpected, location, Character.valueOf(expected));
    }

    static String TOKENIZER_IO_ERR() {
        return JsonMessages.localize("tokenizer.io.err", new Object[0]);
    }

    static String PARSER_GETSTRING_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getString.err", new Object[]{event});
    }

    static String PARSER_ISINTEGRALNUMBER_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.isIntegralNumber.err", new Object[]{event});
    }

    static String PARSER_GETINT_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getInt.err", new Object[]{event});
    }

    static String PARSER_GETLONG_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getLong.err", new Object[]{event});
    }

    static String PARSER_GETBIGDECIMAL_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getBigDecimal.err", new Object[]{event});
    }

    static String PARSER_GETARRAY_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getArray.err", new Object[]{event});
    }

    static String PARSER_GETOBJECT_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getObject.err", new Object[]{event});
    }

    static String PARSER_GETVALUE_ERR(JsonParser.Event event) {
        return JsonMessages.localize("parser.getValue.err", new Object[]{event});
    }

    static String PARSER_GETVALUESTREAM_ERR() {
        return JsonMessages.localize("parser.getValueStream.err", new Object[0]);
    }

    static String PARSER_EXPECTED_EOF(JsonTokenizer.JsonToken token) {
        return JsonMessages.localize("parser.expected.eof", new Object[]{token});
    }

    static String PARSER_TOKENIZER_CLOSE_IO() {
        return JsonMessages.localize("parser.tokenizer.close.io", new Object[0]);
    }

    static String PARSER_INVALID_TOKEN(JsonTokenizer.JsonToken token, JsonLocation location, String expectedTokens) {
        return JsonMessages.localize("parser.invalid.token", new Object[]{token, location, expectedTokens});
    }

    static String PARSER_STATE_ERR(JsonValue.ValueType type) {
        return JsonMessages.localize("parser.state.err", new Object[]{type});
    }

    static String PARSER_SCOPE_ERR(JsonValue value) {
        return JsonMessages.localize("parser.scope.err", value);
    }

    static String PARSER_INPUT_ENC_DETECT_FAILED() {
        return JsonMessages.localize("parser.input.enc.detect.failed", new Object[0]);
    }

    static String PARSER_INPUT_ENC_DETECT_IOERR() {
        return JsonMessages.localize("parser.input.enc.detect.ioerr", new Object[0]);
    }

    static String GENERATOR_FLUSH_IO_ERR() {
        return JsonMessages.localize("generator.flush.io.err", new Object[0]);
    }

    static String GENERATOR_CLOSE_IO_ERR() {
        return JsonMessages.localize("generator.close.io.err", new Object[0]);
    }

    static String GENERATOR_WRITE_IO_ERR() {
        return JsonMessages.localize("generator.write.io.err", new Object[0]);
    }

    static String GENERATOR_ILLEGAL_METHOD(Object scope) {
        return JsonMessages.localize("generator.illegal.method", scope);
    }

    static String GENERATOR_DOUBLE_INFINITE_NAN() {
        return JsonMessages.localize("generator.double.infinite.nan", new Object[0]);
    }

    static String GENERATOR_INCOMPLETE_JSON() {
        return JsonMessages.localize("generator.incomplete.json", new Object[0]);
    }

    static String GENERATOR_ILLEGAL_MULTIPLE_TEXT() {
        return JsonMessages.localize("generator.illegal.multiple.text", new Object[0]);
    }

    static String WRITER_WRITE_ALREADY_CALLED() {
        return JsonMessages.localize("writer.write.already.called", new Object[0]);
    }

    static String READER_READ_ALREADY_CALLED() {
        return JsonMessages.localize("reader.read.already.called", new Object[0]);
    }

    static String OBJBUILDER_NAME_NULL() {
        return JsonMessages.localize("objbuilder.name.null", new Object[0]);
    }

    static String OBJBUILDER_VALUE_NULL() {
        return JsonMessages.localize("objbuilder.value.null", new Object[0]);
    }

    static String OBJBUILDER_OBJECT_BUILDER_NULL() {
        return JsonMessages.localize("objbuilder.object.builder.null", new Object[0]);
    }

    static String OBJBUILDER_ARRAY_BUILDER_NULL() {
        return JsonMessages.localize("objbuilder.array.builder.null", new Object[0]);
    }

    static String ARRBUILDER_VALUE_NULL() {
        return JsonMessages.localize("arrbuilder.value.null", new Object[0]);
    }

    static String ARRBUILDER_OBJECT_BUILDER_NULL() {
        return JsonMessages.localize("arrbuilder.object.builder.null", new Object[0]);
    }

    static String ARRBUILDER_ARRAY_BUILDER_NULL() {
        return JsonMessages.localize("arrbuilder.array.builder.null", new Object[0]);
    }

    static String ARRBUILDER_VALUELIST_NULL(int index, int size) {
        return JsonMessages.localize("arrbuilder.valuelist.null", index, size);
    }

    static String POINTER_FORMAT_INVALID() {
        return JsonMessages.localize("pointer.format.invalid", new Object[0]);
    }

    static String POINTER_MAPPING_MISSING(JsonObject object, String key) {
        return JsonMessages.localize("pointer.mapping.missing", object, key);
    }

    static String POINTER_REFERENCE_INVALID(JsonValue.ValueType type) {
        return JsonMessages.localize("pointer.reference.invalid", type.name());
    }

    static String POINTER_ARRAY_INDEX_ERR(String token) {
        return JsonMessages.localize("pointer.array.index.err", token);
    }

    static String POINTER_ARRAY_INDEX_ILLEGAL(String token) {
        return JsonMessages.localize("pointer.array.index.illegal", token);
    }

    static String NODEREF_VALUE_ADD_ERR() {
        return JsonMessages.localize("noderef.value.add.err", new Object[0]);
    }

    static String NODEREF_VALUE_CANNOT_REMOVE() {
        return JsonMessages.localize("noderef.value.cannot.remove", new Object[0]);
    }

    static String NODEREF_OBJECT_MISSING(String key) {
        return JsonMessages.localize("noderef.object.missing", key);
    }

    static String NODEREF_ARRAY_INDEX_ERR(int index, int size) {
        return JsonMessages.localize("noderef.array.index.err", index, size);
    }

    static String PATCH_MUST_BE_ARRAY() {
        return JsonMessages.localize("patch.must.be.array", new Object[0]);
    }

    static String PATCH_MOVE_PROPER_PREFIX(String from, String path) {
        return JsonMessages.localize("patch.move.proper.prefix", from, path);
    }

    static String PATCH_MOVE_TARGET_NULL(String from) {
        return JsonMessages.localize("patch.move.target.null", from);
    }

    static String PATCH_TEST_FAILED(String path, String value) {
        return JsonMessages.localize("patch.test.failed", path, value);
    }

    static String PATCH_ILLEGAL_OPERATION(String operation) {
        return JsonMessages.localize("patch.illegal.operation", operation);
    }

    static String PATCH_MEMBER_MISSING(String operation, String member) {
        return JsonMessages.localize("patch.member.missing", operation, member);
    }

    private static String localize(String key, Object ... args) {
        try {
            String msg = BUNDLE.getString(key);
            return MessageFormat.format(msg, args);
        }
        catch (Exception e) {
            return JsonMessages.getDefaultMessage(key, args);
        }
    }

    private static String getDefaultMessage(String key, Object ... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[failed to localize] ");
        sb.append(key);
        if (args != null) {
            sb.append('(');
            for (int i = 0; i < args.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(String.valueOf(args[i]));
            }
            sb.append(')');
        }
        return sb.toString();
    }
}

