/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.pattern.PatternLayoutBase
 *  ch.qos.logback.core.pattern.color.BlackCompositeConverter
 *  ch.qos.logback.core.pattern.color.BlueCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldRedCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter
 *  ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter
 *  ch.qos.logback.core.pattern.color.CyanCompositeConverter
 *  ch.qos.logback.core.pattern.color.GrayCompositeConverter
 *  ch.qos.logback.core.pattern.color.GreenCompositeConverter
 *  ch.qos.logback.core.pattern.color.MagentaCompositeConverter
 *  ch.qos.logback.core.pattern.color.RedCompositeConverter
 *  ch.qos.logback.core.pattern.color.WhiteCompositeConverter
 *  ch.qos.logback.core.pattern.color.YellowCompositeConverter
 *  ch.qos.logback.core.pattern.parser.Parser
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.pattern.ContextNameConverter;
import ch.qos.logback.classic.pattern.DateConverter;
import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.FileOfCallerConverter;
import ch.qos.logback.classic.pattern.KeyValuePairConverter;
import ch.qos.logback.classic.pattern.LevelConverter;
import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.pattern.LineSeparatorConverter;
import ch.qos.logback.classic.pattern.LocalSequenceNumberConverter;
import ch.qos.logback.classic.pattern.LoggerConverter;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.pattern.MarkerConverter;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.pattern.MethodOfCallerConverter;
import ch.qos.logback.classic.pattern.MicrosecondConverter;
import ch.qos.logback.classic.pattern.NopThrowableInformationConverter;
import ch.qos.logback.classic.pattern.PrefixCompositeConverter;
import ch.qos.logback.classic.pattern.PropertyConverter;
import ch.qos.logback.classic.pattern.RelativeTimeConverter;
import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.SequenceNumberConverter;
import ch.qos.logback.classic.pattern.ThreadConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.pattern.color.BlackCompositeConverter;
import ch.qos.logback.core.pattern.color.BlueCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldRedCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import ch.qos.logback.core.pattern.color.CyanCompositeConverter;
import ch.qos.logback.core.pattern.color.GrayCompositeConverter;
import ch.qos.logback.core.pattern.color.GreenCompositeConverter;
import ch.qos.logback.core.pattern.color.MagentaCompositeConverter;
import ch.qos.logback.core.pattern.color.RedCompositeConverter;
import ch.qos.logback.core.pattern.color.WhiteCompositeConverter;
import ch.qos.logback.core.pattern.color.YellowCompositeConverter;
import ch.qos.logback.core.pattern.parser.Parser;
import java.util.HashMap;
import java.util.Map;

public class PatternLayout
extends PatternLayoutBase<ILoggingEvent> {
    public static final Map<String, String> DEFAULT_CONVERTER_MAP = new HashMap<String, String>();
    public static final Map<String, String> CONVERTER_CLASS_TO_KEY_MAP = new HashMap<String, String>();
    public static final Map<String, String> defaultConverterMap = DEFAULT_CONVERTER_MAP;
    public static final String HEADER_PREFIX = "#logback.classic pattern: ";

    public PatternLayout() {
        this.postCompileProcessor = new EnsureExceptionHandling();
    }

    public Map<String, String> getDefaultConverterMap() {
        return DEFAULT_CONVERTER_MAP;
    }

    public String doLayout(ILoggingEvent event) {
        if (!this.isStarted()) {
            return "";
        }
        return this.writeLoopOnConverters(event);
    }

    protected String getPresentationHeaderPrefix() {
        return HEADER_PREFIX;
    }

    static {
        DEFAULT_CONVERTER_MAP.putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);
        DEFAULT_CONVERTER_MAP.put("d", DateConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("date", DateConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(DateConverter.class.getName(), "date");
        DEFAULT_CONVERTER_MAP.put("ms", MicrosecondConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("micros", MicrosecondConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(MicrosecondConverter.class.getName(), "micros");
        DEFAULT_CONVERTER_MAP.put("r", RelativeTimeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("relative", RelativeTimeConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(RelativeTimeConverter.class.getName(), "relative");
        DEFAULT_CONVERTER_MAP.put("level", LevelConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("le", LevelConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("p", LevelConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(LevelConverter.class.getName(), "level");
        DEFAULT_CONVERTER_MAP.put("t", ThreadConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("thread", ThreadConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(ThreadConverter.class.getName(), "thread");
        DEFAULT_CONVERTER_MAP.put("lo", LoggerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("logger", LoggerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("c", LoggerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(LoggerConverter.class.getName(), "logger");
        DEFAULT_CONVERTER_MAP.put("m", MessageConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("msg", MessageConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("message", MessageConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(MessageConverter.class.getName(), "message");
        DEFAULT_CONVERTER_MAP.put("C", ClassOfCallerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("class", ClassOfCallerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(ClassOfCallerConverter.class.getName(), "class");
        DEFAULT_CONVERTER_MAP.put("M", MethodOfCallerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("method", MethodOfCallerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(MethodOfCallerConverter.class.getName(), "method");
        DEFAULT_CONVERTER_MAP.put("L", LineOfCallerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("line", LineOfCallerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(LineOfCallerConverter.class.getName(), "line");
        DEFAULT_CONVERTER_MAP.put("F", FileOfCallerConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("file", FileOfCallerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(FileOfCallerConverter.class.getName(), "file");
        DEFAULT_CONVERTER_MAP.put("X", MDCConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("mdc", MDCConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("ex", ThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("exception", ThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("rEx", RootCauseFirstThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("rootException", RootCauseFirstThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("throwable", ThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("xEx", ExtendedThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("xException", ExtendedThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("xThrowable", ExtendedThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("nopex", NopThrowableInformationConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("nopexception", NopThrowableInformationConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("cn", ContextNameConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("contextName", ContextNameConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(ContextNameConverter.class.getName(), "contextName");
        DEFAULT_CONVERTER_MAP.put("caller", CallerDataConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(CallerDataConverter.class.getName(), "caller");
        DEFAULT_CONVERTER_MAP.put("marker", MarkerConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(MarkerConverter.class.getName(), "marker");
        DEFAULT_CONVERTER_MAP.put("kvp", KeyValuePairConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(KeyValuePairConverter.class.getName(), "kvp");
        DEFAULT_CONVERTER_MAP.put("property", PropertyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("n", LineSeparatorConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("black", BlackCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("red", RedCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("green", GreenCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("yellow", YellowCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("blue", BlueCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("magenta", MagentaCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("cyan", CyanCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("white", WhiteCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("gray", GrayCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldRed", BoldRedCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldGreen", BoldGreenCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldYellow", BoldYellowCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldBlue", BoldBlueCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldMagenta", BoldMagentaCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldCyan", BoldCyanCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("boldWhite", BoldWhiteCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("highlight", HighlightingCompositeConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("lsn", LocalSequenceNumberConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(LocalSequenceNumberConverter.class.getName(), "lsn");
        DEFAULT_CONVERTER_MAP.put("sn", SequenceNumberConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("sequenceNumber", SequenceNumberConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(SequenceNumberConverter.class.getName(), "sequenceNumber");
        DEFAULT_CONVERTER_MAP.put("prefix", PrefixCompositeConverter.class.getName());
    }
}

