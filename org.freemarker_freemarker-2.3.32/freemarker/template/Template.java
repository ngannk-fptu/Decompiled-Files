/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core.BugException;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.FMParser;
import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.TemplateElement;
import freemarker.core.TextBlock;
import freemarker.core.TokenMgrError;
import freemarker.core._CoreAPI;
import freemarker.debug.impl.DebuggerService;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.TreePath;

public class Template
extends Configurable {
    public static final String DEFAULT_NAMESPACE_PREFIX = "D";
    public static final String NO_NS_PREFIX = "N";
    private static final int READER_BUFFER_SIZE = 4096;
    private Map macros = new HashMap();
    private List imports = new Vector();
    private TemplateElement rootElement;
    private String encoding;
    private String defaultNS;
    private Object customLookupCondition;
    private int interpolationSyntax;
    private int actualTagSyntax;
    private int actualNamingConvention;
    private boolean autoEscaping;
    private OutputFormat outputFormat;
    private final String name;
    private final String sourceName;
    private final ArrayList lines = new ArrayList();
    private final ParserConfiguration parserConfiguration;
    private Map prefixToNamespaceURILookup = new HashMap();
    private Map namespaceURIToPrefixLookup = new HashMap();
    private Version templateLanguageVersion;

    private Template(String name, String sourceName, Configuration cfg, ParserConfiguration customParserConfiguration) {
        super(Template.toNonNull(cfg));
        this.name = name;
        this.sourceName = sourceName;
        this.templateLanguageVersion = Template.normalizeTemplateLanguageVersion(Template.toNonNull(cfg).getIncompatibleImprovements());
        this.parserConfiguration = customParserConfiguration != null ? customParserConfiguration : this.getConfiguration();
    }

    private static Configuration toNonNull(Configuration cfg) {
        return cfg != null ? cfg : Configuration.getDefaultConfiguration();
    }

    public Template(String name, Reader reader, Configuration cfg) throws IOException {
        this(name, null, reader, cfg);
    }

    public Template(String name, String sourceCode, Configuration cfg) throws IOException {
        this(name, new StringReader(sourceCode), cfg);
    }

    public Template(String name, Reader reader, Configuration cfg, String encoding) throws IOException {
        this(name, null, reader, cfg, encoding);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg) throws IOException {
        this(name, sourceName, reader, cfg, null);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg, String encoding) throws IOException {
        this(name, sourceName, reader, cfg, null, encoding);
    }

    public Template(String name, String sourceName, Reader reader, Configuration cfg, ParserConfiguration customParserConfiguration, String encoding) throws IOException {
        this(name, sourceName, cfg, customParserConfiguration);
        LineTableBuilder ltbReader;
        this.setEncoding(encoding);
        try {
            ParserConfiguration actualParserConfiguration = this.getParserConfiguration();
            if (!(reader instanceof BufferedReader) && !(reader instanceof StringReader)) {
                reader = new BufferedReader(reader, 4096);
            }
            ltbReader = new LineTableBuilder(reader, actualParserConfiguration);
            reader = ltbReader;
            try {
                FMParser parser = new FMParser(this, reader, actualParserConfiguration);
                if (cfg != null) {
                    _CoreAPI.setPreventStrippings(parser, cfg.getPreventStrippings());
                }
                try {
                    this.rootElement = parser.Root();
                }
                catch (IndexOutOfBoundsException exc) {
                    if (!ltbReader.hasFailure()) {
                        throw exc;
                    }
                    this.rootElement = null;
                }
                this.actualTagSyntax = parser._getLastTagSyntax();
                this.interpolationSyntax = actualParserConfiguration.getInterpolationSyntax();
                this.actualNamingConvention = parser._getLastNamingConvention();
            }
            catch (TokenMgrError exc) {
                throw exc.toParseException(this);
            }
        }
        catch (ParseException e) {
            e.setTemplateName(this.getSourceName());
            throw e;
        }
        finally {
            reader.close();
        }
        ltbReader.throwFailure();
        DebuggerService.registerTemplate(this);
        this.namespaceURIToPrefixLookup = Collections.unmodifiableMap(this.namespaceURIToPrefixLookup);
        this.prefixToNamespaceURILookup = Collections.unmodifiableMap(this.prefixToNamespaceURILookup);
    }

    @Deprecated
    public Template(String name, Reader reader) throws IOException {
        this(name, reader, null);
    }

    @Deprecated
    Template(String name, TemplateElement root, Configuration cfg) {
        this(name, null, cfg, (ParserConfiguration)null);
        this.rootElement = root;
        DebuggerService.registerTemplate(this);
    }

    public static Template getPlainTextTemplate(String name, String content, Configuration config) {
        return Template.getPlainTextTemplate(name, null, content, config);
    }

    public static Template getPlainTextTemplate(String name, String sourceName, String content, Configuration config) {
        Template template;
        try {
            template = new Template(name, sourceName, new StringReader("X"), config);
        }
        catch (IOException e) {
            throw new BugException("Plain text template creation failed", e);
        }
        _CoreAPI.replaceText((TextBlock)template.rootElement, content);
        DebuggerService.registerTemplate(template);
        return template;
    }

    private static Version normalizeTemplateLanguageVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        int v = incompatibleImprovements.intValue();
        if (v < _VersionInts.V_2_3_19) {
            return Configuration.VERSION_2_3_0;
        }
        if (v > _VersionInts.V_2_3_21) {
            return Configuration.VERSION_2_3_21;
        }
        return incompatibleImprovements;
    }

    public void process(Object dataModel, Writer out) throws TemplateException, IOException {
        this.createProcessingEnvironment(dataModel, out, null).process();
    }

    public void process(Object dataModel, Writer out, ObjectWrapper wrapper, TemplateNodeModel rootNode) throws TemplateException, IOException {
        Environment env = this.createProcessingEnvironment(dataModel, out, wrapper);
        if (rootNode != null) {
            env.setCurrentVisitorNode(rootNode);
        }
        env.process();
    }

    public void process(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
        this.createProcessingEnvironment(dataModel, out, wrapper).process();
    }

    public Environment createProcessingEnvironment(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
        TemplateHashModel dataModelHash;
        if (dataModel instanceof TemplateHashModel) {
            dataModelHash = (TemplateHashModel)dataModel;
        } else {
            if (wrapper == null) {
                wrapper = this.getObjectWrapper();
            }
            if (dataModel == null) {
                dataModelHash = new SimpleHash(wrapper);
            } else {
                TemplateModel wrappedDataModel = wrapper.wrap(dataModel);
                if (wrappedDataModel instanceof TemplateHashModel) {
                    dataModelHash = (TemplateHashModel)wrappedDataModel;
                } else {
                    if (wrappedDataModel == null) {
                        throw new IllegalArgumentException(wrapper.getClass().getName() + " converted " + dataModel.getClass().getName() + " to null.");
                    }
                    throw new IllegalArgumentException(wrapper.getClass().getName() + " didn't convert " + dataModel.getClass().getName() + " to a TemplateHashModel. Generally, you want to use a Map<String, Object> or a JavaBean as the root-map (aka. data-model) parameter. The Map key-s or JavaBean property names will be the variable names in the template.");
                }
            }
        }
        return new Environment(this, dataModelHash, out);
    }

    public Environment createProcessingEnvironment(Object dataModel, Writer out) throws TemplateException, IOException {
        return this.createProcessingEnvironment(dataModel, out, null);
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            this.dump(sw);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
        return sw.toString();
    }

    public String getName() {
        return this.name;
    }

    public String getSourceName() {
        return this.sourceName != null ? this.sourceName : this.getName();
    }

    public Configuration getConfiguration() {
        return (Configuration)this.getParent();
    }

    public ParserConfiguration getParserConfiguration() {
        return this.parserConfiguration;
    }

    Version getTemplateLanguageVersion() {
        return this.templateLanguageVersion;
    }

    @Deprecated
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public Object getCustomLookupCondition() {
        return this.customLookupCondition;
    }

    public void setCustomLookupCondition(Object customLookupCondition) {
        this.customLookupCondition = customLookupCondition;
    }

    public int getActualTagSyntax() {
        return this.actualTagSyntax;
    }

    public int getInterpolationSyntax() {
        return this.interpolationSyntax;
    }

    public int getActualNamingConvention() {
        return this.actualNamingConvention;
    }

    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }

    void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean getAutoEscaping() {
        return this.autoEscaping;
    }

    void setAutoEscaping(boolean autoEscaping) {
        this.autoEscaping = autoEscaping;
    }

    public void dump(PrintStream ps) {
        ps.print(this.rootElement.getCanonicalForm());
    }

    public void dump(Writer out) throws IOException {
        out.write(this.rootElement.getCanonicalForm());
    }

    @Deprecated
    public void addMacro(Macro macro) {
        this.macros.put(macro.getName(), macro);
    }

    @Deprecated
    public void addImport(LibraryLoad ll) {
        this.imports.add(ll);
    }

    public String getSource(int beginColumn, int beginLine, int endColumn, int endLine) {
        if (beginLine < 1 || endLine < 1) {
            return null;
        }
        --beginColumn;
        --endColumn;
        --endLine;
        StringBuilder buf = new StringBuilder();
        for (int i = --beginLine; i <= endLine; ++i) {
            if (i >= this.lines.size()) continue;
            buf.append(this.lines.get(i));
        }
        int lastLineLength = this.lines.get(endLine).toString().length();
        int trailingCharsToDelete = lastLineLength - endColumn - 1;
        buf.delete(0, beginColumn);
        buf.delete(buf.length() - trailingCharsToDelete, buf.length());
        return buf.toString();
    }

    @Deprecated
    public TemplateElement getRootTreeNode() {
        return this.rootElement;
    }

    @Deprecated
    public Map getMacros() {
        return this.macros;
    }

    @Deprecated
    public List getImports() {
        return this.imports;
    }

    @Deprecated
    public void addPrefixNSMapping(String prefix, String nsURI) {
        if (nsURI.length() == 0) {
            throw new IllegalArgumentException("Cannot map empty string URI");
        }
        if (prefix.length() == 0) {
            throw new IllegalArgumentException("Cannot map empty string prefix");
        }
        if (prefix.equals(NO_NS_PREFIX)) {
            throw new IllegalArgumentException("The prefix: " + prefix + " cannot be registered, it's reserved for special internal use.");
        }
        if (this.prefixToNamespaceURILookup.containsKey(prefix)) {
            throw new IllegalArgumentException("The prefix: '" + prefix + "' was repeated. This is illegal.");
        }
        if (this.namespaceURIToPrefixLookup.containsKey(nsURI)) {
            throw new IllegalArgumentException("The namespace URI: " + nsURI + " cannot be mapped to 2 different prefixes.");
        }
        if (prefix.equals(DEFAULT_NAMESPACE_PREFIX)) {
            this.defaultNS = nsURI;
        } else {
            this.prefixToNamespaceURILookup.put(prefix, nsURI);
            this.namespaceURIToPrefixLookup.put(nsURI, prefix);
        }
    }

    public String getDefaultNS() {
        return this.defaultNS;
    }

    public String getNamespaceForPrefix(String prefix) {
        if (prefix.equals("")) {
            return this.defaultNS == null ? "" : this.defaultNS;
        }
        return (String)this.prefixToNamespaceURILookup.get(prefix);
    }

    public String getPrefixForNamespace(String nsURI) {
        if (nsURI == null) {
            return null;
        }
        if (nsURI.length() == 0) {
            return this.defaultNS == null ? "" : NO_NS_PREFIX;
        }
        if (nsURI.equals(this.defaultNS)) {
            return "";
        }
        return (String)this.namespaceURIToPrefixLookup.get(nsURI);
    }

    public String getPrefixedName(String localName, String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            if (this.defaultNS != null) {
                return "N:" + localName;
            }
            return localName;
        }
        if (nsURI.equals(this.defaultNS)) {
            return localName;
        }
        String prefix = this.getPrefixForNamespace(nsURI);
        if (prefix == null) {
            return null;
        }
        return prefix + ":" + localName;
    }

    @Deprecated
    public TreePath containingElements(int column, int line) {
        ArrayList<TemplateElement> elements = new ArrayList<TemplateElement>();
        TemplateElement element = this.rootElement;
        block0: while (element.contains(column, line)) {
            elements.add(element);
            Enumeration enumeration = element.children();
            while (enumeration.hasMoreElements()) {
                TemplateElement elem = (TemplateElement)enumeration.nextElement();
                if (!elem.contains(column, line)) continue;
                element = elem;
                continue block0;
            }
            break block0;
        }
        if (elements.isEmpty()) {
            return null;
        }
        return new TreePath(elements.toArray());
    }

    public static class WrongEncodingException
    extends ParseException {
        private static final long serialVersionUID = 1L;
        @Deprecated
        public String specifiedEncoding;
        private final String constructorSpecifiedEncoding;

        @Deprecated
        public WrongEncodingException(String templateSpecifiedEncoding) {
            this(templateSpecifiedEncoding, (String)null);
        }

        public WrongEncodingException(String templateSpecifiedEncoding, String constructorSpecifiedEncoding) {
            this.specifiedEncoding = templateSpecifiedEncoding;
            this.constructorSpecifiedEncoding = constructorSpecifiedEncoding;
        }

        @Override
        public String getMessage() {
            return "Encoding specified inside the template (" + this.specifiedEncoding + ") doesn't match the encoding specified for the Template constructor" + (this.constructorSpecifiedEncoding != null ? " (" + this.constructorSpecifiedEncoding + ")." : ".");
        }

        public String getTemplateSpecifiedEncoding() {
            return this.specifiedEncoding;
        }

        public String getConstructorSpecifiedEncoding() {
            return this.constructorSpecifiedEncoding;
        }
    }

    private class LineTableBuilder
    extends FilterReader {
        private final int tabSize;
        private final StringBuilder lineBuf;
        int lastChar;
        boolean closed;
        private Exception failure;

        LineTableBuilder(Reader r, ParserConfiguration parserConfiguration) {
            super(r);
            this.lineBuf = new StringBuilder();
            this.tabSize = parserConfiguration.getTabSize();
        }

        public boolean hasFailure() {
            return this.failure != null;
        }

        public void throwFailure() throws IOException {
            if (this.failure != null) {
                if (this.failure instanceof IOException) {
                    throw (IOException)this.failure;
                }
                if (this.failure instanceof RuntimeException) {
                    throw (RuntimeException)this.failure;
                }
                throw new UndeclaredThrowableException(this.failure);
            }
        }

        @Override
        public int read() throws IOException {
            try {
                int c = this.in.read();
                this.handleChar(c);
                return c;
            }
            catch (Exception e) {
                throw this.rememberException(e);
            }
        }

        private IOException rememberException(Exception e) throws IOException {
            if (!this.closed) {
                this.failure = e;
            }
            if (e instanceof IOException) {
                return (IOException)e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new UndeclaredThrowableException(e);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            try {
                int numchars = this.in.read(cbuf, off, len);
                for (int i = off; i < off + numchars; ++i) {
                    char c = cbuf[i];
                    this.handleChar(c);
                }
                return numchars;
            }
            catch (Exception e) {
                throw this.rememberException(e);
            }
        }

        @Override
        public void close() throws IOException {
            if (this.lineBuf.length() > 0) {
                Template.this.lines.add(this.lineBuf.toString());
                this.lineBuf.setLength(0);
            }
            super.close();
            this.closed = true;
        }

        private void handleChar(int c) {
            if (c == 10 || c == 13) {
                if (this.lastChar == 13 && c == 10) {
                    int lastIndex = Template.this.lines.size() - 1;
                    String lastLine = (String)Template.this.lines.get(lastIndex);
                    Template.this.lines.set(lastIndex, lastLine + '\n');
                } else {
                    this.lineBuf.append((char)c);
                    Template.this.lines.add(this.lineBuf.toString());
                    this.lineBuf.setLength(0);
                }
            } else if (c == 9 && this.tabSize != 1) {
                int numSpaces = this.tabSize - this.lineBuf.length() % this.tabSize;
                for (int i = 0; i < numSpaces; ++i) {
                    this.lineBuf.append(' ');
                }
            } else {
                this.lineBuf.append((char)c);
            }
            this.lastChar = c;
        }
    }
}

