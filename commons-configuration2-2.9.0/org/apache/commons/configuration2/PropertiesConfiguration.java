/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.commons.text.translate.AggregateTranslator
 *  org.apache.commons.text.translate.CharSequenceTranslator
 *  org.apache.commons.text.translate.EntityArrays
 *  org.apache.commons.text.translate.LookupTranslator
 *  org.apache.commons.text.translate.UnicodeEscaper
 */
package org.apache.commons.configuration2;

import java.io.FileNotFoundException;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.ConfigurationConsumer;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.UnicodeEscaper;

public class PropertiesConfiguration
extends BaseConfiguration
implements FileBasedConfiguration,
FileLocatorAware {
    public static final ConfigurationConsumer<ConfigurationException> DEFAULT_INCLUDE_LISTENER = e -> {
        throw e;
    };
    public static final ConfigurationConsumer<ConfigurationException> NOOP_INCLUDE_LISTENER = e -> {};
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    static final String COMMENT_CHARS = "#!";
    static final String DEFAULT_SEPARATOR = " = ";
    private static final String UNESCAPE_CHARACTERS = ":#=!\\'\"";
    private static String include = "include";
    private static String includeOptional = "includeoptional";
    private static final char[] SEPARATORS = new char[]{'=', ':'};
    private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int HEX_RADIX = 16;
    private static final int UNICODE_LEN = 4;
    private PropertiesConfigurationLayout layout;
    private ConfigurationConsumer<ConfigurationException> includeListener;
    private IOFactory ioFactory;
    private FileLocator locator;
    private boolean includesAllowed = true;

    public PropertiesConfiguration() {
        this.installLayout(this.createLayout());
    }

    private static int countTrailingBS(String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
            ++bsCount;
        }
        return bsCount;
    }

    public static String getInclude() {
        return include;
    }

    public static String getIncludeOptional() {
        return includeOptional;
    }

    static boolean isCommentLine(String line) {
        String s = line.trim();
        return s.isEmpty() || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    private static boolean needsUnescape(char ch) {
        return UNESCAPE_CHARACTERS.indexOf(ch) >= 0;
    }

    public static void setInclude(String inc) {
        include = inc;
    }

    public static void setIncludeOptional(String inc) {
        includeOptional = inc;
    }

    protected static String unescapeJava(String str) {
        return PropertiesConfiguration.unescapeJava(str, false);
    }

    protected static String unescapeJava(String str, boolean jupCompatible) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.append((char)value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new ConfigurationRuntimeException("Unable to parse unicode value: " + unicode, nfe);
                }
            }
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case 'r': {
                        out.append('\r');
                        break;
                    }
                    case 'f': {
                        out.append('\f');
                        break;
                    }
                    case 't': {
                        out.append('\t');
                        break;
                    }
                    case 'n': {
                        out.append('\n');
                        break;
                    }
                    default: {
                        if (!jupCompatible && ch == 'b') {
                            out.append('\b');
                            break;
                        }
                        if (ch == 'u') {
                            inUnicode = true;
                            break;
                        }
                        if (!PropertiesConfiguration.needsUnescape(ch) && !jupCompatible) {
                            out.append('\\');
                        }
                        out.append(ch);
                        break;
                    }
                }
                continue;
            }
            if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }
        if (hadSlash) {
            out.append('\\');
        }
        return out.toString();
    }

    @Override
    public Object clone() {
        PropertiesConfiguration copy = (PropertiesConfiguration)super.clone();
        if (this.layout != null) {
            copy.setLayout(new PropertiesConfigurationLayout(this.layout));
        }
        return copy;
    }

    private PropertiesConfigurationLayout createLayout() {
        return new PropertiesConfigurationLayout();
    }

    public String getFooter() {
        this.beginRead(false);
        try {
            String string = this.getLayout().getFooterComment();
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public String getHeader() {
        this.beginRead(false);
        try {
            String string = this.getLayout().getHeaderComment();
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public ConfigurationConsumer<ConfigurationException> getIncludeListener() {
        return this.includeListener != null ? this.includeListener : DEFAULT_INCLUDE_LISTENER;
    }

    public IOFactory getIOFactory() {
        return this.ioFactory != null ? this.ioFactory : DefaultIOFactory.INSTANCE;
    }

    public PropertiesConfigurationLayout getLayout() {
        return this.layout;
    }

    @Override
    public void initFileLocator(FileLocator locator) {
        this.locator = locator;
    }

    private void installLayout(PropertiesConfigurationLayout layout) {
        if (this.layout != null) {
            this.removeEventListener(ConfigurationEvent.ANY, this.layout);
        }
        this.layout = layout == null ? this.createLayout() : layout;
        this.addEventListener(ConfigurationEvent.ANY, this.layout);
    }

    public boolean isIncludesAllowed() {
        return this.includesAllowed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadIncludeFile(String fileName, boolean optional, Deque<URL> seenStack) throws ConfigurationException {
        URL baseURL;
        if (this.locator == null) {
            throw new ConfigurationException("Load operation not properly initialized! Do not call read(InputStream) directly, but use a FileHandler to load a configuration.");
        }
        URL url = this.locateIncludeFile(this.locator.getBasePath(), fileName);
        if (url == null && (baseURL = this.locator.getSourceURL()) != null) {
            url = this.locateIncludeFile(baseURL.toString(), fileName);
        }
        if (optional && url == null) {
            return;
        }
        if (url == null) {
            this.getIncludeListener().accept(new ConfigurationException("Cannot resolve include file " + fileName, new FileNotFoundException(fileName)));
        } else {
            FileHandler fh = new FileHandler(this);
            fh.setFileLocator(this.locator);
            FileLocator orgLocator = this.locator;
            try {
                try {
                    if (seenStack.contains(url)) {
                        throw new ConfigurationException(String.format("Cycle detected loading %s, seen stack: %s", url, seenStack));
                    }
                    seenStack.add(url);
                    try {
                        fh.load(url);
                    }
                    finally {
                        seenStack.pop();
                    }
                }
                catch (ConfigurationException e) {
                    this.getIncludeListener().accept(e);
                }
            }
            finally {
                this.locator = orgLocator;
            }
        }
    }

    private URL locateIncludeFile(String basePath, String fileName) {
        FileLocator includeLocator = FileLocatorUtils.fileLocator(this.locator).sourceURL(null).basePath(basePath).fileName(fileName).create();
        return FileLocatorUtils.locate(includeLocator);
    }

    boolean propertyLoaded(String key, String value, Deque<URL> seenStack) throws ConfigurationException {
        boolean result;
        if (StringUtils.isNotEmpty((CharSequence)PropertiesConfiguration.getInclude()) && key.equalsIgnoreCase(PropertiesConfiguration.getInclude())) {
            if (this.isIncludesAllowed()) {
                Collection<String> files = this.getListDelimiterHandler().split(value, true);
                for (String f : files) {
                    this.loadIncludeFile(this.interpolate(f), false, seenStack);
                }
            }
            result = false;
        } else if (StringUtils.isNotEmpty((CharSequence)PropertiesConfiguration.getIncludeOptional()) && key.equalsIgnoreCase(PropertiesConfiguration.getIncludeOptional())) {
            if (this.isIncludesAllowed()) {
                Collection<String> files = this.getListDelimiterHandler().split(value, true);
                for (String f : files) {
                    this.loadIncludeFile(this.interpolate(f), true, seenStack);
                }
            }
            result = false;
        } else {
            this.addPropertyInternal(key, value);
            result = true;
        }
        return result;
    }

    @Override
    public void read(Reader in) throws ConfigurationException, IOException {
        this.getLayout().load(this, in);
    }

    public void setFooter(String footer) {
        this.beginWrite(false);
        try {
            this.getLayout().setFooterComment(footer);
        }
        finally {
            this.endWrite();
        }
    }

    public void setHeader(String header) {
        this.beginWrite(false);
        try {
            this.getLayout().setHeaderComment(header);
        }
        finally {
            this.endWrite();
        }
    }

    public void setIncludeListener(ConfigurationConsumer<ConfigurationException> includeListener) {
        if (includeListener == null) {
            throw new IllegalArgumentException("includeListener must not be null.");
        }
        this.includeListener = includeListener;
    }

    public void setIncludesAllowed(boolean includesAllowed) {
        this.includesAllowed = includesAllowed;
    }

    public void setIOFactory(IOFactory ioFactory) {
        if (ioFactory == null) {
            throw new IllegalArgumentException("IOFactory must not be null.");
        }
        this.ioFactory = ioFactory;
    }

    public void setLayout(PropertiesConfigurationLayout layout) {
        this.installLayout(layout);
    }

    @Override
    public void write(Writer out) throws ConfigurationException, IOException {
        this.getLayout().save(this, out);
    }

    public static class PropertiesWriter
    extends FilterWriter {
        private static final Map<CharSequence, CharSequence> PROPERTIES_CHARS_ESCAPE;
        private static final CharSequenceTranslator ESCAPE_PROPERTIES;
        private static final ValueTransformer DEFAULT_TRANSFORMER;
        private final ValueTransformer valueTransformer;
        private final ListDelimiterHandler delimiterHandler;
        private String currentSeparator;
        private String globalSeparator;
        private String lineSeparator;

        public PropertiesWriter(Writer writer, ListDelimiterHandler delHandler) {
            this(writer, delHandler, DEFAULT_TRANSFORMER);
        }

        public PropertiesWriter(Writer writer, ListDelimiterHandler delHandler, ValueTransformer valueTransformer) {
            super(writer);
            this.delimiterHandler = delHandler;
            this.valueTransformer = valueTransformer;
        }

        protected String escapeKey(String key) {
            StringBuilder newkey = new StringBuilder();
            for (int i = 0; i < key.length(); ++i) {
                char c = key.charAt(i);
                if (ArrayUtils.contains((char[])SEPARATORS, (char)c) || ArrayUtils.contains((char[])WHITE_SPACE, (char)c) || c == '\\') {
                    newkey.append('\\');
                }
                newkey.append(c);
            }
            return newkey.toString();
        }

        protected String fetchSeparator(String key, Object value) {
            return this.getGlobalSeparator() != null ? this.getGlobalSeparator() : StringUtils.defaultString((String)this.getCurrentSeparator());
        }

        public String getCurrentSeparator() {
            return this.currentSeparator;
        }

        public ListDelimiterHandler getDelimiterHandler() {
            return this.delimiterHandler;
        }

        public String getGlobalSeparator() {
            return this.globalSeparator;
        }

        public String getLineSeparator() {
            return this.lineSeparator != null ? this.lineSeparator : LINE_SEPARATOR;
        }

        public void setCurrentSeparator(String currentSeparator) {
            this.currentSeparator = currentSeparator;
        }

        public void setGlobalSeparator(String globalSeparator) {
            this.globalSeparator = globalSeparator;
        }

        public void setLineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }

        public void writeComment(String comment) throws IOException {
            this.writeln("# " + comment);
        }

        public void writeln(String s) throws IOException {
            if (s != null) {
                this.write(s);
            }
            this.write(this.getLineSeparator());
        }

        public void writeProperty(String key, List<?> values) throws IOException {
            for (Object value : values) {
                this.writeProperty(key, value);
            }
        }

        public void writeProperty(String key, Object value) throws IOException {
            this.writeProperty(key, value, false);
        }

        public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
            String v;
            if (value instanceof List) {
                v = null;
                List values = (List)value;
                if (forceSingleLine) {
                    try {
                        v = String.valueOf(this.getDelimiterHandler().escapeList(values, this.valueTransformer));
                    }
                    catch (UnsupportedOperationException unsupportedOperationException) {
                        // empty catch block
                    }
                }
                if (v == null) {
                    this.writeProperty(key, values);
                    return;
                }
            } else {
                v = String.valueOf(this.getDelimiterHandler().escape(value, this.valueTransformer));
            }
            this.write(this.escapeKey(key));
            this.write(this.fetchSeparator(key, value));
            this.write(v);
            this.writeln(null);
        }

        static {
            HashMap<String, String> initialMap = new HashMap<String, String>();
            initialMap.put("\\", "\\\\");
            PROPERTIES_CHARS_ESCAPE = Collections.unmodifiableMap(initialMap);
            ESCAPE_PROPERTIES = new AggregateTranslator(new CharSequenceTranslator[]{new LookupTranslator(PROPERTIES_CHARS_ESCAPE), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), UnicodeEscaper.outsideOf((int)32, (int)127)});
            DEFAULT_TRANSFORMER = value -> {
                String strVal = String.valueOf(value);
                return ESCAPE_PROPERTIES.translate((CharSequence)strVal);
            };
        }
    }

    public static class PropertiesReader
    extends LineNumberReader {
        private static final Pattern PROPERTY_PATTERN = Pattern.compile("(([\\S&&[^\\\\" + new String(PropertiesConfiguration.access$000()) + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(PropertiesConfiguration.access$000()) + "])\\s*)?(.*)");
        private static final int IDX_KEY = 1;
        private static final int IDX_VALUE = 5;
        private static final int IDX_SEPARATOR = 3;
        private final List<String> commentLines = new ArrayList<String>();
        private String propertyName;
        private String propertyValue;
        private String propertySeparator = " = ";

        public PropertiesReader(Reader reader) {
            super(reader);
        }

        static boolean checkCombineLines(String line) {
            return PropertiesConfiguration.countTrailingBS(line) % 2 != 0;
        }

        static String[] doParseProperty(String line, boolean trimValue) {
            Matcher matcher = PROPERTY_PATTERN.matcher(line);
            String[] result = new String[]{"", "", ""};
            if (matcher.matches()) {
                result[0] = matcher.group(1).trim();
                String value = matcher.group(5);
                if (trimValue) {
                    value = value.trim();
                }
                result[1] = value;
                result[2] = matcher.group(3);
            }
            return result;
        }

        public List<String> getCommentLines() {
            return this.commentLines;
        }

        public String getPropertyName() {
            return this.propertyName;
        }

        public String getPropertySeparator() {
            return this.propertySeparator;
        }

        public String getPropertyValue() {
            return this.propertyValue;
        }

        protected void initPropertyName(String name) {
            this.propertyName = this.unescapePropertyName(name);
        }

        protected void initPropertySeparator(String value) {
            this.propertySeparator = value;
        }

        protected void initPropertyValue(String value) {
            this.propertyValue = this.unescapePropertyValue(value);
        }

        public boolean nextProperty() throws IOException {
            String line = this.readProperty();
            if (line == null) {
                return false;
            }
            this.parseProperty(line);
            return true;
        }

        protected void parseProperty(String line) {
            String[] property = PropertiesReader.doParseProperty(line, true);
            this.initPropertyName(property[0]);
            this.initPropertyValue(property[1]);
            this.initPropertySeparator(property[2]);
        }

        public String readProperty() throws IOException {
            String line;
            this.commentLines.clear();
            StringBuilder buffer = new StringBuilder();
            while (true) {
                if ((line = this.readLine()) == null) {
                    return null;
                }
                if (PropertiesConfiguration.isCommentLine(line)) {
                    this.commentLines.add(line);
                    continue;
                }
                if (!PropertiesReader.checkCombineLines(line = line.trim())) break;
                line = line.substring(0, line.length() - 1);
                buffer.append(line);
            }
            buffer.append(line);
            return buffer.toString();
        }

        protected String unescapePropertyName(String name) {
            return StringEscapeUtils.unescapeJava((String)name);
        }

        protected String unescapePropertyValue(String value) {
            return PropertiesConfiguration.unescapeJava(value);
        }
    }

    public static class JupPropertiesWriter
    extends PropertiesWriter {
        private static final int PRINTABLE_INDEX_END = 126;
        private static final int PRINTABLE_INDEX_START = 32;
        private static final UnicodeEscaper ESCAPER = UnicodeEscaper.outsideOf((int)32, (int)126);
        private static final Map<CharSequence, CharSequence> JUP_CHARS_ESCAPE;

        public JupPropertiesWriter(Writer writer, ListDelimiterHandler delHandler, boolean escapeUnicode) {
            super(writer, delHandler, value -> {
                String valueString = String.valueOf(value);
                AggregateTranslator translator = escapeUnicode ? new AggregateTranslator(new CharSequenceTranslator[]{new LookupTranslator(JUP_CHARS_ESCAPE), ESCAPER}) : new AggregateTranslator(new CharSequenceTranslator[]{new LookupTranslator(JUP_CHARS_ESCAPE)});
                valueString = translator.translate((CharSequence)valueString);
                if (valueString.startsWith(" ")) {
                    valueString = "\\" + valueString;
                }
                return valueString;
            });
        }

        static {
            HashMap<String, String> initialMap = new HashMap<String, String>();
            initialMap.put("\\", "\\\\");
            initialMap.put("\n", "\\n");
            initialMap.put("\t", "\\t");
            initialMap.put("\f", "\\f");
            initialMap.put("\r", "\\r");
            JUP_CHARS_ESCAPE = Collections.unmodifiableMap(initialMap);
        }
    }

    public static class JupPropertiesReader
    extends PropertiesReader {
        public JupPropertiesReader(Reader reader) {
            super(reader);
        }

        @Override
        protected void parseProperty(String line) {
            String[] property = JupPropertiesReader.doParseProperty(line, false);
            this.initPropertyName(property[0]);
            this.initPropertyValue(property[1]);
            this.initPropertySeparator(property[2]);
        }

        @Override
        public String readProperty() throws IOException {
            this.getCommentLines().clear();
            StringBuilder buffer = new StringBuilder();
            while (true) {
                String line;
                if ((line = this.readLine()) == null) {
                    if (buffer.length() > 0) break;
                    return null;
                }
                if (PropertiesConfiguration.isCommentLine(line) && buffer.length() == 0) {
                    this.getCommentLines().add(line);
                    continue;
                }
                if (buffer.length() > 0) {
                    int i;
                    for (i = 0; i < line.length() && Character.isWhitespace(line.charAt(i)); ++i) {
                    }
                    line = line.substring(i);
                }
                if (!JupPropertiesReader.checkCombineLines(line)) {
                    buffer.append(line);
                    break;
                }
                line = line.substring(0, line.length() - 1);
                buffer.append(line);
            }
            return buffer.toString();
        }

        @Override
        protected String unescapePropertyValue(String value) {
            return PropertiesConfiguration.unescapeJava(value, true);
        }
    }

    public static class JupIOFactory
    implements IOFactory {
        private final boolean escapeUnicode;

        public JupIOFactory() {
            this(true);
        }

        public JupIOFactory(boolean escapeUnicode) {
            this.escapeUnicode = escapeUnicode;
        }

        @Override
        public PropertiesReader createPropertiesReader(Reader in) {
            return new JupPropertiesReader(in);
        }

        @Override
        public PropertiesWriter createPropertiesWriter(Writer out, ListDelimiterHandler handler) {
            return new JupPropertiesWriter(out, handler, this.escapeUnicode);
        }
    }

    public static interface IOFactory {
        public PropertiesReader createPropertiesReader(Reader var1);

        public PropertiesWriter createPropertiesWriter(Writer var1, ListDelimiterHandler var2);
    }

    public static class DefaultIOFactory
    implements IOFactory {
        static final DefaultIOFactory INSTANCE = new DefaultIOFactory();

        @Override
        public PropertiesReader createPropertiesReader(Reader in) {
            return new PropertiesReader(in);
        }

        @Override
        public PropertiesWriter createPropertiesWriter(Writer out, ListDelimiterHandler handler) {
            return new PropertiesWriter(out, handler);
        }
    }
}

