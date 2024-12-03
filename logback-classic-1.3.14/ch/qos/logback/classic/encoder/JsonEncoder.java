/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.CoreConstants
 *  ch.qos.logback.core.encoder.EncoderBase
 *  ch.qos.logback.core.encoder.JsonEscapeUtil
 *  org.slf4j.Marker
 *  org.slf4j.event.KeyValuePair
 */
package ch.qos.logback.classic.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.encoder.JsonEscapeUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;

public class JsonEncoder
extends EncoderBase<ILoggingEvent> {
    static final boolean DO_NOT_ADD_QUOTE_KEY = false;
    static final boolean ADD_QUOTE_KEY = true;
    static int DEFAULT_SIZE = 1024;
    static int DEFAULT_SIZE_WITH_THROWABLE = DEFAULT_SIZE * 8;
    static byte[] EMPTY_BYTES = new byte[0];
    public static final String CONTEXT_ATTR_NAME = "context";
    public static final String NAME_ATTR_NAME = "name";
    public static final String BIRTHDATE_ATTR_NAME = "birthdate";
    public static final String CONTEXT_PROPERTIES_ATTR_NAME = "properties";
    public static final String TIMESTAMP_ATTR_NAME = "timestamp";
    public static final String NANOSECONDS_ATTR_NAME = "nanoseconds";
    public static final String SEQUENCE_NUMBER_ATTR_NAME = "sequenceNumber";
    public static final String LEVEL_ATTR_NAME = "level";
    public static final String MARKERS_ATTR_NAME = "markers";
    public static final String THREAD_NAME_ATTR_NAME = "threadName";
    public static final String MDC_ATTR_NAME = "mdc";
    public static final String LOGGER_ATTR_NAME = "loggerName";
    public static final String MESSAGE_ATTR_NAME = "message";
    public static final String ARGUMENT_ARRAY_ATTR_NAME = "arguments";
    public static final String KEY_VALUE_PAIRS_ATTR_NAME = "kvpList";
    public static final String THROWABLE_ATTR_NAME = "throwable";
    private static final String CYCLIC_THROWABLE_ATTR_NAME = "cyclic";
    public static final String CAUSE_ATTR_NAME = "cause";
    public static final String SUPPRESSED_ATTR_NAME = "suppressed";
    public static final String COMMON_FRAMES_COUNT_ATTR_NAME = "commonFramesCount";
    public static final String CLASS_NAME_ATTR_NAME = "className";
    public static final String METHOD_NAME_ATTR_NAME = "methodName";
    private static final String FILE_NAME_ATTR_NAME = "fileName";
    private static final String LINE_NUMBER_ATTR_NAME = "lineNumber";
    public static final String STEP_ARRAY_NAME_ATTRIBUTE = "stepArray";
    private static final char OPEN_OBJ = '{';
    private static final char CLOSE_OBJ = '}';
    private static final char OPEN_ARRAY = '[';
    private static final char CLOSE_ARRAY = ']';
    private static final char QUOTE = '\"';
    private static final char SP = ' ';
    private static final char ENTRY_SEPARATOR = ':';
    private static final String COL_SP = ": ";
    private static final String QUOTE_COL = "\":";
    private static final char VALUE_SEPARATOR = ',';

    public byte[] headerBytes() {
        return EMPTY_BYTES;
    }

    public byte[] encode(ILoggingEvent event) {
        int initialCapacity = event.getThrowableProxy() == null ? DEFAULT_SIZE : DEFAULT_SIZE_WITH_THROWABLE;
        StringBuilder sb = new StringBuilder(initialCapacity);
        sb.append('{');
        this.appenderMemberWithLongValue(sb, SEQUENCE_NUMBER_ATTR_NAME, event.getSequenceNumber());
        sb.append(',');
        this.appenderMemberWithLongValue(sb, TIMESTAMP_ATTR_NAME, event.getTimeStamp());
        sb.append(',');
        this.appenderMemberWithLongValue(sb, NANOSECONDS_ATTR_NAME, event.getNanoseconds());
        sb.append(',');
        String levelStr = event.getLevel() != null ? event.getLevel().levelStr : "null";
        this.appenderMember(sb, LEVEL_ATTR_NAME, levelStr);
        sb.append(',');
        this.appenderMember(sb, THREAD_NAME_ATTR_NAME, this.jsonEscape(event.getThreadName()));
        sb.append(',');
        this.appenderMember(sb, LOGGER_ATTR_NAME, event.getLoggerName());
        sb.append(',');
        this.appendLoggerContext(sb, event.getLoggerContextVO());
        sb.append(',');
        this.appendMarkers(sb, event);
        this.appendMDC(sb, event);
        this.appendKeyValuePairs(sb, event);
        this.appenderMember(sb, MESSAGE_ATTR_NAME, this.jsonEscape(event.getMessage()));
        sb.append(',');
        this.appendArgumentArray(sb, event);
        this.appendThrowableProxy(sb, THROWABLE_ATTR_NAME, event.getThrowableProxy());
        sb.append('}');
        sb.append('\n');
        return sb.toString().getBytes(CoreConstants.UTF_8_CHARSET);
    }

    private void appendLoggerContext(StringBuilder sb, LoggerContextVO loggerContextVO) {
        sb.append('\"').append(CONTEXT_ATTR_NAME).append(QUOTE_COL);
        if (loggerContextVO == null) {
            sb.append("null");
            return;
        }
        sb.append('{');
        this.appenderMember(sb, NAME_ATTR_NAME, this.nullSafeStr(loggerContextVO.getName()));
        sb.append(',');
        this.appenderMemberWithLongValue(sb, BIRTHDATE_ATTR_NAME, loggerContextVO.getBirthTime());
        sb.append(',');
        this.appendMap(sb, CONTEXT_PROPERTIES_ATTR_NAME, loggerContextVO.getPropertyMap());
        sb.append('}');
    }

    private void appendMap(StringBuilder sb, String attrName, Map<String, String> map) {
        sb.append('\"').append(attrName).append(QUOTE_COL);
        if (map == null) {
            sb.append("null");
            return;
        }
        sb.append('{');
        boolean addComma = false;
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (addComma) {
                sb.append(',');
            }
            addComma = true;
            this.appenderMember(sb, this.jsonEscapedToString(entry.getKey()), this.jsonEscapedToString(entry.getValue()));
        }
        sb.append('}');
    }

    private void appendThrowableProxy(StringBuilder sb, String attributeName, IThrowableProxy itp) {
        IThrowableProxy[] suppressedArray;
        IThrowableProxy cause;
        if (attributeName != null) {
            sb.append('\"').append(attributeName).append(QUOTE_COL);
            if (itp == null) {
                sb.append("null");
                return;
            }
        }
        sb.append('{');
        this.appenderMember(sb, CLASS_NAME_ATTR_NAME, this.nullSafeStr(itp.getClassName()));
        sb.append(',');
        this.appenderMember(sb, MESSAGE_ATTR_NAME, this.jsonEscape(itp.getMessage()));
        if (itp.isCyclic()) {
            sb.append(',');
            this.appenderMember(sb, CYCLIC_THROWABLE_ATTR_NAME, this.jsonEscape("true"));
        }
        sb.append(',');
        this.appendSTEPArray(sb, itp.getStackTraceElementProxyArray(), itp.getCommonFrames());
        if (itp.getCommonFrames() != 0) {
            sb.append(',');
            this.appenderMemberWithIntValue(sb, COMMON_FRAMES_COUNT_ATTR_NAME, itp.getCommonFrames());
        }
        if ((cause = itp.getCause()) != null) {
            sb.append(',');
            this.appendThrowableProxy(sb, CAUSE_ATTR_NAME, cause);
        }
        if ((suppressedArray = itp.getSuppressed()) != null && suppressedArray.length != 0) {
            sb.append(',');
            sb.append('\"').append(SUPPRESSED_ATTR_NAME).append(QUOTE_COL);
            sb.append('[');
            boolean first = true;
            for (IThrowableProxy suppressedITP : suppressedArray) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                this.appendThrowableProxy(sb, null, suppressedITP);
            }
            sb.append(']');
        }
        sb.append('}');
    }

    private void appendSTEPArray(StringBuilder sb, StackTraceElementProxy[] stepArray, int commonFrames) {
        int len;
        sb.append('\"').append(STEP_ARRAY_NAME_ATTRIBUTE).append(QUOTE_COL).append('[');
        int n = len = stepArray != null ? stepArray.length : 0;
        if (commonFrames >= len) {
            commonFrames = 0;
        }
        for (int i = 0; i < len - commonFrames; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            StackTraceElementProxy step = stepArray[i];
            sb.append('{');
            StackTraceElement ste = step.getStackTraceElement();
            this.appenderMember(sb, CLASS_NAME_ATTR_NAME, this.nullSafeStr(ste.getClassName()));
            sb.append(',');
            this.appenderMember(sb, METHOD_NAME_ATTR_NAME, this.nullSafeStr(ste.getMethodName()));
            sb.append(',');
            this.appenderMember(sb, FILE_NAME_ATTR_NAME, this.nullSafeStr(ste.getFileName()));
            sb.append(',');
            this.appenderMemberWithIntValue(sb, LINE_NUMBER_ATTR_NAME, ste.getLineNumber());
            sb.append('}');
        }
        sb.append(']');
    }

    private void appenderMember(StringBuilder sb, String key, String value) {
        sb.append('\"').append(key).append(QUOTE_COL).append('\"').append(value).append('\"');
    }

    private void appenderMemberWithIntValue(StringBuilder sb, String key, int value) {
        sb.append('\"').append(key).append(QUOTE_COL).append(value);
    }

    private void appenderMemberWithLongValue(StringBuilder sb, String key, long value) {
        sb.append('\"').append(key).append(QUOTE_COL).append(value);
    }

    private void appendKeyValuePairs(StringBuilder sb, ILoggingEvent event) {
        List<KeyValuePair> kvpList = event.getKeyValuePairs();
        if (kvpList == null || kvpList.isEmpty()) {
            return;
        }
        sb.append('\"').append(KEY_VALUE_PAIRS_ATTR_NAME).append(QUOTE_COL).append(' ').append('[');
        int len = kvpList.size();
        for (int i = 0; i < len; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            KeyValuePair kvp = kvpList.get(i);
            sb.append('{');
            this.appenderMember(sb, this.jsonEscapedToString(kvp.key), this.jsonEscapedToString(kvp.value));
            sb.append('}');
        }
        sb.append(']');
        sb.append(',');
    }

    private void appendArgumentArray(StringBuilder sb, ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (argumentArray == null) {
            return;
        }
        sb.append('\"').append(ARGUMENT_ARRAY_ATTR_NAME).append(QUOTE_COL).append(' ').append('[');
        int len = argumentArray.length;
        for (int i = 0; i < len; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append('\"').append(this.jsonEscapedToString(argumentArray[i])).append('\"');
        }
        sb.append(']');
        sb.append(',');
    }

    private void appendMarkers(StringBuilder sb, ILoggingEvent event) {
        List<Marker> markerList = event.getMarkerList();
        if (markerList == null) {
            return;
        }
        sb.append('\"').append(MARKERS_ATTR_NAME).append(QUOTE_COL).append(' ').append('[');
        int len = markerList.size();
        for (int i = 0; i < len; ++i) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append('\"').append(this.jsonEscapedToString(markerList.get(i))).append('\"');
        }
        sb.append(']');
        sb.append(',');
    }

    private String jsonEscapedToString(Object o) {
        if (o == null) {
            return "null";
        }
        return JsonEscapeUtil.jsonEscapeString((String)o.toString());
    }

    private String nullSafeStr(String s) {
        if (s == null) {
            return "null";
        }
        return s;
    }

    private String jsonEscape(String s) {
        if (s == null) {
            return "null";
        }
        return JsonEscapeUtil.jsonEscapeString((String)s);
    }

    private void appendMDC(StringBuilder sb, ILoggingEvent event) {
        Map<String, String> map = event.getMDCPropertyMap();
        sb.append('\"').append(MDC_ATTR_NAME).append(QUOTE_COL).append(' ').append('{');
        if (this.isNotEmptyMap(map)) {
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            int i = 0;
            for (Map.Entry<String, String> entry : entrySet) {
                if (i != 0) {
                    sb.append(',');
                }
                this.appenderMember(sb, this.jsonEscapedToString(entry.getKey()), this.jsonEscapedToString(entry.getValue()));
                ++i;
            }
        }
        sb.append('}');
        sb.append(',');
    }

    boolean isNotEmptyMap(Map map) {
        if (map == null) {
            return false;
        }
        return !map.isEmpty();
    }

    public byte[] footerBytes() {
        return EMPTY_BYTES;
    }
}

