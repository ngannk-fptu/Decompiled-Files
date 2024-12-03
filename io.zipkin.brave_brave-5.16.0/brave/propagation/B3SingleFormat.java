/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.RecyclableBuffers;
import brave.internal.codec.HexCodec;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Collections;

public final class B3SingleFormat {
    static final int FORMAT_MAX_LENGTH = 68;
    static final int FIELD_TRACE_ID_HIGH = 1;
    static final int FIELD_TRACE_ID = 2;
    static final int FIELD_SPAN_ID = 3;
    static final int FIELD_SAMPLED = 4;
    static final int FIELD_PARENT_SPAN_ID = 5;

    public static String writeB3SingleFormatWithoutParentId(TraceContext context) {
        char[] buffer = RecyclableBuffers.parseBuffer();
        int length = B3SingleFormat.writeB3SingleFormat(context, 0L, buffer);
        return new String(buffer, 0, length);
    }

    public static byte[] writeB3SingleFormatWithoutParentIdAsBytes(TraceContext context) {
        char[] buffer = RecyclableBuffers.parseBuffer();
        int length = B3SingleFormat.writeB3SingleFormat(context, 0L, buffer);
        return B3SingleFormat.asciiToNewByteArray(buffer, length);
    }

    public static String writeB3SingleFormat(TraceContext context) {
        char[] buffer = RecyclableBuffers.parseBuffer();
        int length = B3SingleFormat.writeB3SingleFormat(context, context.parentIdAsLong(), buffer);
        return new String(buffer, 0, length);
    }

    public static byte[] writeB3SingleFormatAsBytes(TraceContext context) {
        char[] buffer = RecyclableBuffers.parseBuffer();
        int length = B3SingleFormat.writeB3SingleFormat(context, context.parentIdAsLong(), buffer);
        return B3SingleFormat.asciiToNewByteArray(buffer, length);
    }

    static int writeB3SingleFormat(TraceContext context, long parentId, char[] result) {
        int pos = 0;
        long traceIdHigh = context.traceIdHigh();
        if (traceIdHigh != 0L) {
            HexCodec.writeHexLong(result, pos, traceIdHigh);
            pos += 16;
        }
        HexCodec.writeHexLong(result, pos, context.traceId());
        pos += 16;
        result[pos++] = 45;
        HexCodec.writeHexLong(result, pos, context.spanId());
        pos += 16;
        Boolean sampled = context.sampled();
        if (sampled != null) {
            result[pos++] = 45;
            int n = context.debug() ? 100 : (result[pos++] = sampled != false ? 49 : 48);
        }
        if (parentId != 0L) {
            result[pos++] = 45;
            HexCodec.writeHexLong(result, pos, parentId);
            pos += 16;
        }
        return pos;
    }

    @Nullable
    public static TraceContextOrSamplingFlags parseB3SingleFormat(CharSequence b3) {
        return B3SingleFormat.parseB3SingleFormat(b3, 0, b3.length());
    }

    @Nullable
    public static TraceContextOrSamplingFlags parseB3SingleFormat(CharSequence value, int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        if (length == 0) {
            Platform.get().log("Invalid input: empty", null);
            return null;
        }
        if (length == 1) {
            SamplingFlags flags = B3SingleFormat.tryParseSamplingFlags(value.charAt(beginIndex));
            return flags != null ? TraceContextOrSamplingFlags.create(flags) : null;
        }
        if (length > 68) {
            Platform.get().log("Invalid input: too long", null);
            return null;
        }
        long traceIdHigh = 0L;
        long traceId = 0L;
        long spanId = 0L;
        long parentId = 0L;
        int flags = 0;
        int currentField = 1;
        int currentFieldLength = 0;
        long buffer = 0L;
        for (int pos = beginIndex; pos <= endIndex; ++pos) {
            int c;
            boolean isEof = pos == endIndex;
            int n = c = isEof ? 45 : (int)value.charAt(pos);
            if (c == 45) {
                if (currentField == 4) {
                    if (isEof && currentFieldLength > 1) {
                        currentField = 5;
                    }
                } else if (currentField == 1) {
                    currentField = 2;
                }
                if (!B3SingleFormat.validateFieldLength(currentField, currentFieldLength)) {
                    return null;
                }
                switch (currentField) {
                    case 2: {
                        traceId = buffer;
                        currentField = 3;
                        break;
                    }
                    case 3: {
                        spanId = buffer;
                        currentField = 4;
                        break;
                    }
                    case 4: {
                        SamplingFlags samplingFlags = B3SingleFormat.tryParseSamplingFlags(value.charAt(pos - 1));
                        if (samplingFlags == null) {
                            return null;
                        }
                        flags = samplingFlags.flags;
                        currentField = 5;
                        break;
                    }
                    case 5: {
                        parentId = buffer;
                        if (isEof) break;
                        Platform.get().log("Invalid input: more than 4 fields exist", null);
                        return null;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                buffer = 0L;
                currentFieldLength = 0;
                continue;
            }
            if (currentField == 1 && currentFieldLength == 16) {
                traceIdHigh = buffer;
                buffer = 0L;
                currentField = 2;
                currentFieldLength = 0;
            }
            ++currentFieldLength;
            buffer <<= 4;
            if (c >= 48 && c <= 57) {
                buffer |= (long)(c - 48);
                continue;
            }
            if (c >= 97 && c <= 102) {
                buffer |= (long)(c - 97 + 10);
                continue;
            }
            B3SingleFormat.log(currentField, "Invalid input: only valid characters are lower-hex for {0}");
            return null;
        }
        if (traceIdHigh == 0L && traceId == 0L || spanId == 0L) {
            int field = spanId == 0L ? 3 : 2;
            B3SingleFormat.log(field, "Invalid input: read all zeros {0}");
            return null;
        }
        return TraceContextOrSamplingFlags.create(new TraceContext(flags, traceIdHigh, traceId, 0L, parentId, spanId, Collections.<Object>emptyList()));
    }

    @Nullable
    static SamplingFlags tryParseSamplingFlags(char sampledChar) {
        switch (sampledChar) {
            case '1': {
                return SamplingFlags.SAMPLED;
            }
            case '0': {
                return SamplingFlags.NOT_SAMPLED;
            }
            case 'd': {
                return SamplingFlags.DEBUG;
            }
        }
        B3SingleFormat.log(4, "Invalid input: expected 0, 1 or d for {0}");
        return null;
    }

    static boolean validateFieldLength(int field, int length) {
        int expectedLength;
        int n = expectedLength = field == 4 ? 1 : 16;
        if (length == 0) {
            B3SingleFormat.log(field, "Invalid input: empty {0}");
            return false;
        }
        if (length < expectedLength) {
            B3SingleFormat.log(field, "Invalid input: {0} is too short");
            return false;
        }
        if (length > expectedLength) {
            B3SingleFormat.log(field, "Invalid input: {0} is too long");
            return false;
        }
        return true;
    }

    static void log(int fieldCode, String s) {
        String field;
        switch (fieldCode) {
            case 1: 
            case 2: {
                field = "trace ID";
                break;
            }
            case 3: {
                field = "span ID";
                break;
            }
            case 4: {
                field = "sampled";
                break;
            }
            case 5: {
                field = "parent ID";
                break;
            }
            default: {
                throw new AssertionError((Object)("field code unmatched: " + fieldCode));
            }
        }
        Platform.get().log(s, field, null);
    }

    static byte[] asciiToNewByteArray(char[] buffer, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; ++i) {
            result[i] = (byte)buffer[i];
        }
        return result;
    }

    B3SingleFormat() {
    }
}

