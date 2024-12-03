/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.util.ReadOnlyStringMap
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.logging.log4j.layout.json;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.logging.log4j.StackTraceCompressor;
import com.atlassian.logging.log4j.layout.json.DefaultJsonDataProvider;
import com.atlassian.logging.log4j.layout.json.JsonContextData;
import com.atlassian.logging.log4j.layout.json.JsonDataProvider;
import com.atlassian.logging.log4j.layout.json.JsonStaticData;
import com.atlassian.logging.log4j.util.CleanLogging;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class JsonLayoutHelper {
    private static final String TIME_ZONE = "UTC";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'");
    private static final Joiner STACK_TRACE_JOINER;
    private JsonDataProvider dataProvider;
    private final JsonFactory jsonFactory = new JsonFactory();
    private JsonStaticData staticData;
    private StackTraceCompressor stackTraceCompressor;
    private Set<String> suppressedFields = Collections.emptySet();
    private CleanLogging cleanLogging = new CleanLogging();
    private boolean includeLocation = false;
    private boolean filteringApplied = true;
    private int minimumLines = 6;
    private boolean showEludedSummary = false;
    private String filteredFrames;
    private Map<String, String> additionalFields = Maps.newHashMap();

    public String toString() {
        return "JsonLayoutHelper{dataProvider=" + this.dataProvider.getClass().toString() + ", suppressedFields=" + this.suppressedFields + ", includeLocation=" + this.includeLocation + ", filteringApplied=" + this.filteringApplied + ", minimumLines=" + this.minimumLines + ", showEludedSummary=" + this.showEludedSummary + ", filteredFrames='" + this.filteredFrames + '\'' + ", additionalFields=" + this.additionalFields + '}';
    }

    public String format(org.apache.logging.log4j.core.LogEvent logEvent) {
        return this.format(this.wrap(logEvent));
    }

    public String format(LogEvent event) {
        StringWriter stringWriter = new StringWriter();
        Optional<String> splitEnv = this.cleanLogging.getEnvironmentSuffixForSplit(event.getLoggerName());
        if (splitEnv.isPresent()) {
            this.formatWithEnv(event, splitEnv, stringWriter);
        } else {
            this.formatWithEnv(event, Optional.empty(), stringWriter);
            Optional<String> copyEnv = this.cleanLogging.getEnvironmentSuffixForCopy(event.getLoggerName());
            if (copyEnv.isPresent()) {
                stringWriter.append(",");
                this.formatWithEnv(event, copyEnv, stringWriter);
            }
        }
        return stringWriter.toString();
    }

    private LogEvent wrap(final org.apache.logging.log4j.core.LogEvent event) {
        return new LogEvent(){

            public org.apache.logging.log4j.core.LogEvent getNativeLogEvent() {
                return event;
            }

            @Override
            public String getLoggerName() {
                return event.getLoggerName();
            }

            @Override
            public String getMessage() {
                return event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
            }

            @Override
            public long getTimestamp() {
                return event.getTimeMillis();
            }

            @Override
            public String getLevel() {
                return event.getLevel().toString();
            }

            @Override
            public String getThreadName() {
                return event.getThreadName();
            }

            @Override
            public Throwable getThrown() {
                return event.getThrown();
            }

            @Override
            public StackTraceElement[] getStackTraceElements() {
                return event.getThrown().getStackTrace();
            }

            @Override
            public ReadOnlyStringMap getThreadContextMap() {
                return event.getContextData();
            }

            @Override
            public LogEvent.LocationInfo getLocationInformation() {
                final StackTraceElement info = event.getSource();
                return new LogEvent.LocationInfo(){

                    @Override
                    public String getClassName() {
                        return info.getClassName();
                    }

                    @Override
                    public String getMethodName() {
                        return info.getMethodName();
                    }

                    @Override
                    public String getLineNumber() {
                        return String.valueOf(info.getLineNumber());
                    }
                };
            }
        };
    }

    private void formatWithEnv(LogEvent event, Optional<String> suffix, StringWriter stringWriter) {
        try {
            JsonGenerator g = this.jsonFactory.createJsonGenerator((Writer)stringWriter);
            g.writeStartObject();
            this.writeFields(event, suffix, g);
            g.writeEndObject();
            g.close();
        }
        catch (IOException e) {
            throw new RuntimeException("JsonLayout - Failed to format", e);
        }
        stringWriter.append("\n");
    }

    public void setSuppressedFields(String suppressedFields) {
        this.suppressedFields = new HashSet<String>(Arrays.stream(suppressedFields.split(",")).map(String::trim).collect(Collectors.toList()));
    }

    public void setIncludeLocation(boolean includeLocation) {
        this.includeLocation = includeLocation;
    }

    public void setDataProvider(JsonDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setFilteringApplied(boolean filteringApplied) {
        this.filteringApplied = filteringApplied;
    }

    public void setMinimumLines(int minimumLines) {
        this.minimumLines = minimumLines;
    }

    public void setShowEludedSummary(boolean showEludedSummary) {
        this.showEludedSummary = showEludedSummary;
    }

    public void setFilteredFrames(String filteredFrames) {
        this.filteredFrames = filteredFrames;
    }

    public void setAdditionalFields(String additionalFields) {
        this.additionalFields = Pattern.compile(",").splitAsStream(additionalFields).map(s -> s.split(":")).collect(Collectors.toMap(a -> a[0], a -> ((String[])a).length > 1 ? a[1] : ""));
    }

    public void setEnvironmentConfigFilename(String filename) {
        this.cleanLogging.setEnvironmentConfigFilename(filename);
    }

    private void writeFields(LogEvent event, Optional<String> suffix, JsonGenerator g) throws IOException {
        this.writeBasicFields(event, suffix, g);
        this.writeMessageField(event, g);
        this.writeThrowableFields(event, g);
        this.writeContextFields(event, g);
        this.writeLocationFields(event, g);
        this.writeUnicornFields(g);
        this.writeAdditionalFields(g);
        this.writeExtFields(event, suffix, g);
    }

    private void writeAdditionalFields(JsonGenerator g) {
        this.additionalFields.forEach((tag, value) -> this.processField((String)tag, (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> value, false));
    }

    protected void writeMessageField(LogEvent event, JsonGenerator g) {
        this.processField("message", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> event.getMessage() == null ? "" : event.getMessage(), true);
    }

    private void writeBasicFields(LogEvent event, Optional<String> suffix, JsonGenerator g) throws IOException {
        String env = this.staticData.getEnvironment();
        Date date = new Date(event.getTimestamp());
        this.processField("timestamp", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> DATE_FORMAT.format(date), true);
        this.processField("level", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> event.getLevel(), true);
        this.processField("serviceId", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), this.staticData::getServiceId, true);
        this.processField("product", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), this.staticData::getProductName, true);
        this.processField("hostname", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), this.dataProvider::getHostName, true);
        this.processField("env", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> suffix.map(s -> env + s).orElse(env), true);
        this.processField("env_suffix", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> suffix.orElse(null), false);
        this.processField("pid", (arg_0, arg_1) -> ((JsonGenerator)g).writeNumberField(arg_0, arg_1), this.staticData::getProcessId, true);
        this.processField("thread", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), event::getThreadName, true);
        this.processField("logger", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), event::getLoggerName, true);
    }

    private void writeThrowableFields(LogEvent event, JsonGenerator g) throws IOException {
        if (event.getThrown() == null) {
            return;
        }
        StackTraceElement[] stackTraceElements = event.getStackTraceElements();
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return;
        }
        g.writeObjectFieldStart("err");
        this.processField("msg", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> event.getThrown().getMessage());
        g.writeArrayFieldStart("class");
        Throwable e = event.getThrown();
        do {
            g.writeString(e.getClass().getName());
        } while ((e = e.getCause()) != null);
        g.writeEndArray();
        if (this.filteringApplied) {
            StringBuffer stackTrace = new StringBuffer();
            this.stackTraceCompressor.filterStackTrace(stackTrace, stackTraceElements);
            this.processField("stack", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), stackTrace::toString);
        } else {
            this.processField("stack", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), () -> STACK_TRACE_JOINER.join((Object[])stackTraceElements));
        }
        g.writeEndObject();
    }

    private void writeContextFields(LogEvent event, JsonGenerator g) throws IOException {
        JsonContextData contextData = this.dataProvider.getContextData(event);
        if (!contextData.isEmpty()) {
            g.writeObjectFieldStart("ctx");
            this.processField("requestId", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), contextData::getRequestId);
            this.processField("sessionId", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), contextData::getSessionId);
            this.processField("userKey", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), contextData::getUserKey);
            g.writeEndObject();
        }
    }

    private void writeLocationFields(LogEvent event, JsonGenerator g) throws IOException {
        if (!this.includeLocation) {
            return;
        }
        LogEvent.LocationInfo location = event.getLocationInformation();
        g.writeObjectFieldStart("location");
        this.processField("class", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), location::getClassName);
        this.processField("method", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), location::getMethodName);
        this.processField("line", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), location::getLineNumber);
        g.writeEndObject();
    }

    private void writeUnicornFields(JsonGenerator g) throws IOException {
        if (this.staticData.getDataCenter() == null && this.staticData.getRack() == null) {
            return;
        }
        g.writeObjectFieldStart("unicorn");
        this.processField("dc", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), this.staticData::getDataCenter);
        this.processField("rack", (arg_0, arg_1) -> ((JsonGenerator)g).writeStringField(arg_0, arg_1), this.staticData::getRack);
        g.writeEndObject();
    }

    private void writeMap(JsonGenerator g, Map<String, ?> map) throws IOException {
        g.writeStartObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            this.writeMapEntry(g, entry.getKey(), entry.getValue());
        }
        g.writeEndObject();
    }

    private void writeMapEntry(JsonGenerator g, String key, Object value) throws IOException {
        g.writeFieldName(key);
        this.writeVal(g, value);
    }

    private void writeList(JsonGenerator g, List<?> list) throws IOException {
        g.writeStartArray();
        for (Object listElement : list) {
            this.writeVal(g, listElement);
        }
        g.writeEndArray();
    }

    private void writeJsonable(JsonGenerator g, Jsonable val) throws IOException {
        StringWriter w = new StringWriter();
        val.write((Writer)w);
        g.writeRawValue(w.toString());
    }

    private void writeVal(JsonGenerator g, Object val) throws IOException {
        if (val instanceof Map) {
            this.writeMap(g, (Map)val);
        } else if (val instanceof List) {
            this.writeList(g, (List)val);
        } else if (val instanceof Jsonable) {
            this.writeJsonable(g, (Jsonable)val);
        } else {
            g.writeObject(val);
        }
    }

    private void writeExtFields(LogEvent event, Optional<String> suffix, JsonGenerator g) throws IOException {
        Map<String, Object> dataTable = this.dataProvider.getExtraData(event);
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        boolean isCleanEnv = suffix.isPresent();
        block0: for (Map.Entry<String, Object> entry : dataTable.entrySet()) {
            if (isCleanEnv && this.cleanLogging.isSuppressed(entry.getKey())) continue;
            Object[] keySequence = entry.getKey().split("\\.");
            Object value = entry.getValue();
            Map<String, Object> currentMap = map;
            for (int i = 0; i < keySequence.length; ++i) {
                String key = keySequence[i];
                if (currentMap.containsKey(key)) {
                    if (i == keySequence.length - 1) {
                        System.err.println("Collision in MDC for key " + Arrays.toString(keySequence));
                        continue;
                    }
                    Object mapOrVal = currentMap.get(key);
                    if (mapOrVal instanceof Map) {
                        currentMap = (Map)mapOrVal;
                        continue;
                    }
                    System.err.println("Collision in MDC for key " + Arrays.toString(keySequence));
                    continue block0;
                }
                if (i == keySequence.length - 1) {
                    currentMap.put(key, value);
                    currentMap = map;
                    continue;
                }
                TreeMap newMapOrVal = new TreeMap();
                currentMap.put(key, newMapOrVal);
                currentMap = newMapOrVal;
            }
        }
        if (map.size() == 0) {
            return;
        }
        g.writeFieldName("ext");
        this.writeMap(g, map);
    }

    private <T> void processField(String fieldName, CheckedValueConsumer<T> consumer, CheckedValueProducer<T> producer, boolean keepNulls) {
        if (!this.suppressedFields.contains(fieldName)) {
            try {
                T value = producer.get();
                if (keepNulls || value != null) {
                    consumer.accept(fieldName, value);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <T> void processField(String fieldName, CheckedValueConsumer<T> consumer, CheckedValueProducer<T> producer) {
        this.processField(fieldName, consumer, producer, false);
    }

    public void initialise() {
        if (this.dataProvider == null) {
            this.dataProvider = new DefaultJsonDataProvider();
        }
        this.stackTraceCompressor = StackTraceCompressor.defaultBuilder(this.minimumLines, this.showEludedSummary).filteredFrames(this.filteredFrames).build();
        this.staticData = this.dataProvider.getStaticData();
    }

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        STACK_TRACE_JOINER = Joiner.on((String)"\n");
    }

    public static interface LogEvent {
        public String getLoggerName();

        public String getMessage();

        public long getTimestamp();

        public String getLevel();

        public String getThreadName();

        public Throwable getThrown();

        public StackTraceElement[] getStackTraceElements();

        public ReadOnlyStringMap getThreadContextMap();

        public LocationInfo getLocationInformation();

        public <T> T getNativeLogEvent();

        public static interface LocationInfo {
            public String getClassName();

            public String getMethodName();

            public String getLineNumber();
        }
    }

    private static final class JSON_KEYS {
        private static final String TIMESTAMP = "timestamp";
        private static final String LEVEL = "level";
        private static final String SERVICE_ID = "serviceId";
        private static final String PRODUCT = "product";
        private static final String HOST_NAME = "hostname";
        private static final String ENVIRONMENT = "env";
        private static final String ENV_SUFFIX = "env_suffix";
        private static final String PROCESS_ID = "pid";
        private static final String THREAD = "thread";
        private static final String LOGGER = "logger";
        private static final String MESSAGE = "message";
        private static final String ERROR = "err";
        private static final String ERROR_CLASS = "class";
        private static final String ERROR_MESSAGE = "msg";
        private static final String STACK_TRACE = "stack";
        private static final String CONTEXT = "ctx";
        private static final String REQUEST_ID = "requestId";
        private static final String SESSION_ID = "sessionId";
        private static final String USER_KEY = "userKey";
        private static final String UNICORN = "unicorn";
        private static final String DATA_CENTER = "dc";
        private static final String RACK = "rack";
        private static final String LOCATION = "location";
        private static final String CLASS = "class";
        private static final String METHOD = "method";
        private static final String LINE_NUMBER = "line";
        private static final String EXTRA = "ext";

        private JSON_KEYS() {
        }
    }

    @FunctionalInterface
    private static interface CheckedValueProducer<T> {
        public T get() throws Exception;
    }

    @FunctionalInterface
    private static interface CheckedValueConsumer<T> {
        public void accept(String var1, T var2) throws Exception;
    }
}

