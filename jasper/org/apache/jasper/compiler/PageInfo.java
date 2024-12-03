/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ExpressionFactory
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ExpressionFactory;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.BeanRepository;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;

class PageInfo {
    private final List<String> imports;
    private final Map<String, Long> dependants;
    private final BeanRepository beanRepository;
    private final Set<String> varInfoNames;
    private final HashMap<String, TagLibraryInfo> taglibsMap;
    private final HashMap<String, String> jspPrefixMapper;
    private final HashMap<String, Deque<String>> xmlPrefixMapper;
    private final HashMap<String, Mark> nonCustomTagPrefixMap;
    private final String jspFile;
    private static final String defaultLanguage = "java";
    private String language;
    private final String defaultExtends = Constants.JSP_SERVLET_BASE;
    private String xtends;
    private String contentType = null;
    private String session;
    private boolean isSession = true;
    private String bufferValue;
    private int buffer = 8192;
    private String autoFlush;
    private boolean isAutoFlush = true;
    private String isThreadSafeValue;
    private boolean isThreadSafe = true;
    private String isErrorPageValue;
    private boolean isErrorPage = false;
    private String errorPage = null;
    private String info;
    private boolean scriptless = false;
    private boolean scriptingInvalid = false;
    private String isELIgnoredValue;
    private boolean isELIgnored = false;
    private String deferredSyntaxAllowedAsLiteralValue;
    private boolean deferredSyntaxAllowedAsLiteral = false;
    private final ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
    private String trimDirectiveWhitespacesValue;
    private boolean trimDirectiveWhitespaces = false;
    private String omitXmlDecl = null;
    private String doctypeName = null;
    private String doctypePublic = null;
    private String doctypeSystem = null;
    private boolean isJspPrefixHijacked;
    private final HashSet<String> prefixes;
    private boolean hasJspRoot = false;
    private Collection<String> includePrelude;
    private Collection<String> includeCoda;
    private final List<String> pluginDcls;
    private boolean errorOnUndeclaredNamespace = false;
    private final boolean isTagFile;

    PageInfo(BeanRepository beanRepository, String jspFile, boolean isTagFile) {
        this.isTagFile = isTagFile;
        this.jspFile = jspFile;
        this.beanRepository = beanRepository;
        this.varInfoNames = new HashSet<String>();
        this.taglibsMap = new HashMap();
        this.jspPrefixMapper = new HashMap();
        this.xmlPrefixMapper = new HashMap();
        this.nonCustomTagPrefixMap = new HashMap();
        this.dependants = new HashMap<String, Long>();
        this.includePrelude = new ArrayList<String>();
        this.includeCoda = new ArrayList<String>();
        this.pluginDcls = new ArrayList<String>();
        this.prefixes = new HashSet();
        this.imports = new ArrayList<String>(Constants.STANDARD_IMPORTS);
    }

    public boolean isTagFile() {
        return this.isTagFile;
    }

    public boolean isPluginDeclared(String id) {
        if (this.pluginDcls.contains(id)) {
            return true;
        }
        this.pluginDcls.add(id);
        return false;
    }

    public void addImports(List<String> imports) {
        this.imports.addAll(imports);
    }

    public void addImport(String imp) {
        this.imports.add(imp);
    }

    public List<String> getImports() {
        return this.imports;
    }

    public String getJspFile() {
        return this.jspFile;
    }

    public void addDependant(String d, Long lastModified) {
        if (!this.dependants.containsKey(d) && !this.jspFile.equals(d)) {
            this.dependants.put(d, lastModified);
        }
    }

    public Map<String, Long> getDependants() {
        return this.dependants;
    }

    public BeanRepository getBeanRepository() {
        return this.beanRepository;
    }

    public void setScriptless(boolean s) {
        this.scriptless = s;
    }

    public boolean isScriptless() {
        return this.scriptless;
    }

    public void setScriptingInvalid(boolean s) {
        this.scriptingInvalid = s;
    }

    public boolean isScriptingInvalid() {
        return this.scriptingInvalid;
    }

    public Collection<String> getIncludePrelude() {
        return this.includePrelude;
    }

    public void setIncludePrelude(Collection<String> prelude) {
        this.includePrelude = prelude;
    }

    public Collection<String> getIncludeCoda() {
        return this.includeCoda;
    }

    public void setIncludeCoda(Collection<String> coda) {
        this.includeCoda = coda;
    }

    public void setHasJspRoot(boolean s) {
        this.hasJspRoot = s;
    }

    public boolean hasJspRoot() {
        return this.hasJspRoot;
    }

    public String getOmitXmlDecl() {
        return this.omitXmlDecl;
    }

    public void setOmitXmlDecl(String omit) {
        this.omitXmlDecl = omit;
    }

    public String getDoctypeName() {
        return this.doctypeName;
    }

    public void setDoctypeName(String doctypeName) {
        this.doctypeName = doctypeName;
    }

    public String getDoctypeSystem() {
        return this.doctypeSystem;
    }

    public void setDoctypeSystem(String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }

    public String getDoctypePublic() {
        return this.doctypePublic;
    }

    public void setDoctypePublic(String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }

    public void setIsJspPrefixHijacked(boolean isHijacked) {
        this.isJspPrefixHijacked = isHijacked;
    }

    public boolean isJspPrefixHijacked() {
        return this.isJspPrefixHijacked;
    }

    public void addPrefix(String prefix) {
        this.prefixes.add(prefix);
    }

    public boolean containsPrefix(String prefix) {
        return this.prefixes.contains(prefix);
    }

    public void addTaglib(String uri, TagLibraryInfo info) {
        this.taglibsMap.put(uri, info);
    }

    public TagLibraryInfo getTaglib(String uri) {
        return this.taglibsMap.get(uri);
    }

    public Collection<TagLibraryInfo> getTaglibs() {
        return this.taglibsMap.values();
    }

    public boolean hasTaglib(String uri) {
        return this.taglibsMap.containsKey(uri);
    }

    public void addPrefixMapping(String prefix, String uri) {
        this.jspPrefixMapper.put(prefix, uri);
    }

    public void pushPrefixMapping(String prefix, String uri) {
        this.xmlPrefixMapper.computeIfAbsent(prefix, k -> new LinkedList()).addFirst(uri);
    }

    public void popPrefixMapping(String prefix) {
        Deque<String> stack = this.xmlPrefixMapper.get(prefix);
        stack.removeFirst();
    }

    public String getURI(String prefix) {
        String uri = null;
        Deque<String> stack = this.xmlPrefixMapper.get(prefix);
        uri = stack == null || stack.size() == 0 ? this.jspPrefixMapper.get(prefix) : stack.getFirst();
        return uri;
    }

    public void setLanguage(String value, Node n, ErrorDispatcher err, boolean pagedir) throws JasperException {
        if (!defaultLanguage.equalsIgnoreCase(value)) {
            if (pagedir) {
                err.jspError(n, "jsp.error.page.language.nonjava", new String[0]);
            } else {
                err.jspError(n, "jsp.error.tag.language.nonjava", new String[0]);
            }
        }
        this.language = value;
    }

    public String getLanguage(boolean useDefault) {
        return this.language == null && useDefault ? defaultLanguage : this.language;
    }

    public void setExtends(String value) {
        this.xtends = value;
    }

    public String getExtends(boolean useDefault) {
        return this.xtends == null && useDefault ? this.defaultExtends : this.xtends;
    }

    public String getExtends() {
        return this.getExtends(true);
    }

    public void setContentType(String value) {
        this.contentType = value;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setBufferValue(String value, Node n, ErrorDispatcher err) throws JasperException {
        if ("none".equalsIgnoreCase(value)) {
            this.buffer = 0;
        } else {
            if (value == null || !value.endsWith("kb")) {
                if (n == null) {
                    err.jspError("jsp.error.page.invalid.buffer", new String[0]);
                } else {
                    err.jspError(n, "jsp.error.page.invalid.buffer", new String[0]);
                }
            }
            try {
                int k = Integer.parseInt(value.substring(0, value.length() - 2));
                this.buffer = k * 1024;
            }
            catch (NumberFormatException e) {
                if (n == null) {
                    err.jspError("jsp.error.page.invalid.buffer", new String[0]);
                }
                err.jspError(n, "jsp.error.page.invalid.buffer", new String[0]);
            }
        }
        this.bufferValue = value;
    }

    public String getBufferValue() {
        return this.bufferValue;
    }

    public int getBuffer() {
        return this.buffer;
    }

    public void setSession(String value, Node n, ErrorDispatcher err) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.isSession = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.isSession = false;
        } else {
            err.jspError(n, "jsp.error.page.invalid.session", new String[0]);
        }
        this.session = value;
    }

    public String getSession() {
        return this.session;
    }

    public boolean isSession() {
        return this.isSession;
    }

    public void setAutoFlush(String value, Node n, ErrorDispatcher err) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.isAutoFlush = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.isAutoFlush = false;
        } else {
            err.jspError(n, "jsp.error.autoFlush.invalid", new String[0]);
        }
        this.autoFlush = value;
    }

    public String getAutoFlush() {
        return this.autoFlush;
    }

    public boolean isAutoFlush() {
        return this.isAutoFlush;
    }

    public void setIsThreadSafe(String value, Node n, ErrorDispatcher err) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.isThreadSafe = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.isThreadSafe = false;
        } else {
            err.jspError(n, "jsp.error.page.invalid.isthreadsafe", new String[0]);
        }
        this.isThreadSafeValue = value;
    }

    public String getIsThreadSafe() {
        return this.isThreadSafeValue;
    }

    public boolean isThreadSafe() {
        return this.isThreadSafe;
    }

    public void setInfo(String value) {
        this.info = value;
    }

    public String getInfo() {
        return this.info;
    }

    public void setErrorPage(String value) {
        this.errorPage = value;
    }

    public String getErrorPage() {
        return this.errorPage;
    }

    public void setIsErrorPage(String value, Node n, ErrorDispatcher err) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.isErrorPage = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.isErrorPage = false;
        } else {
            err.jspError(n, "jsp.error.page.invalid.iserrorpage", new String[0]);
        }
        this.isErrorPageValue = value;
    }

    public String getIsErrorPage() {
        return this.isErrorPageValue;
    }

    public boolean isErrorPage() {
        return this.isErrorPage;
    }

    public void setIsELIgnored(String value, Node n, ErrorDispatcher err, boolean pagedir) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.isELIgnored = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.isELIgnored = false;
        } else if (pagedir) {
            err.jspError(n, "jsp.error.page.invalid.iselignored", new String[0]);
        } else {
            err.jspError(n, "jsp.error.tag.invalid.iselignored", new String[0]);
        }
        this.isELIgnoredValue = value;
    }

    public void setDeferredSyntaxAllowedAsLiteral(String value, Node n, ErrorDispatcher err, boolean pagedir) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.deferredSyntaxAllowedAsLiteral = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.deferredSyntaxAllowedAsLiteral = false;
        } else if (pagedir) {
            err.jspError(n, "jsp.error.page.invalid.deferredsyntaxallowedasliteral", new String[0]);
        } else {
            err.jspError(n, "jsp.error.tag.invalid.deferredsyntaxallowedasliteral", new String[0]);
        }
        this.deferredSyntaxAllowedAsLiteralValue = value;
    }

    public void setTrimDirectiveWhitespaces(String value, Node n, ErrorDispatcher err, boolean pagedir) throws JasperException {
        if ("true".equalsIgnoreCase(value)) {
            this.trimDirectiveWhitespaces = true;
        } else if ("false".equalsIgnoreCase(value)) {
            this.trimDirectiveWhitespaces = false;
        } else if (pagedir) {
            err.jspError(n, "jsp.error.page.invalid.trimdirectivewhitespaces", new String[0]);
        } else {
            err.jspError(n, "jsp.error.tag.invalid.trimdirectivewhitespaces", new String[0]);
        }
        this.trimDirectiveWhitespacesValue = value;
    }

    public void setELIgnored(boolean s) {
        this.isELIgnored = s;
    }

    public String getIsELIgnored() {
        return this.isELIgnoredValue;
    }

    public boolean isELIgnored() {
        return this.isELIgnored;
    }

    public void putNonCustomTagPrefix(String prefix, Mark where) {
        this.nonCustomTagPrefixMap.put(prefix, where);
    }

    public Mark getNonCustomTagPrefix(String prefix) {
        return this.nonCustomTagPrefixMap.get(prefix);
    }

    public String getDeferredSyntaxAllowedAsLiteral() {
        return this.deferredSyntaxAllowedAsLiteralValue;
    }

    public boolean isDeferredSyntaxAllowedAsLiteral() {
        return this.deferredSyntaxAllowedAsLiteral;
    }

    public void setDeferredSyntaxAllowedAsLiteral(boolean isELDeferred) {
        this.deferredSyntaxAllowedAsLiteral = isELDeferred;
    }

    public ExpressionFactory getExpressionFactory() {
        return this.expressionFactory;
    }

    public String getTrimDirectiveWhitespaces() {
        return this.trimDirectiveWhitespacesValue;
    }

    public boolean isTrimDirectiveWhitespaces() {
        return this.trimDirectiveWhitespaces;
    }

    public void setTrimDirectiveWhitespaces(boolean trimDirectiveWhitespaces) {
        this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;
    }

    public Set<String> getVarInfoNames() {
        return this.varInfoNames;
    }

    public boolean isErrorOnUndeclaredNamespace() {
        return this.errorOnUndeclaredNamespace;
    }

    public void setErrorOnUndeclaredNamespace(boolean errorOnUndeclaredNamespace) {
        this.errorOnUndeclaredNamespace = errorOnUndeclaredNamespace;
    }
}

