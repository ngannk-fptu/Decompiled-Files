/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.descriptor.JspPropertyGroupDescriptor
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import org.apache.jasper.compiler.Localizer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JspConfig {
    private final Log log = LogFactory.getLog(JspConfig.class);
    private List<JspPropertyGroup> jspProperties = null;
    private final ServletContext ctxt;
    private volatile boolean initialized = false;
    private static final String defaultIsXml = null;
    private String defaultIsELIgnored = null;
    private static final String defaultIsScriptingInvalid = null;
    private String defaultDeferedSyntaxAllowedAsLiteral = null;
    private static final String defaultTrimDirectiveWhitespaces = null;
    private static final String defaultDefaultContentType = null;
    private static final String defaultBuffer = null;
    private static final String defaultErrorOnUndeclaredNamespace = "false";
    private JspProperty defaultJspProperty;

    public JspConfig(ServletContext ctxt) {
        this.ctxt = ctxt;
    }

    private void processWebDotXml() {
        JspConfigDescriptor jspConfig;
        if (this.ctxt.getEffectiveMajorVersion() < 2) {
            this.defaultIsELIgnored = "true";
            this.defaultDeferedSyntaxAllowedAsLiteral = "true";
            return;
        }
        if (this.ctxt.getEffectiveMajorVersion() == 2) {
            if (this.ctxt.getEffectiveMinorVersion() < 5) {
                this.defaultDeferedSyntaxAllowedAsLiteral = "true";
            }
            if (this.ctxt.getEffectiveMinorVersion() < 4) {
                this.defaultIsELIgnored = "true";
                return;
            }
        }
        if ((jspConfig = this.ctxt.getJspConfigDescriptor()) == null) {
            return;
        }
        this.jspProperties = new ArrayList<JspPropertyGroup>();
        Collection jspPropertyGroups = jspConfig.getJspPropertyGroups();
        for (JspPropertyGroupDescriptor jspPropertyGroup : jspPropertyGroups) {
            Collection urlPatterns = jspPropertyGroup.getUrlPatterns();
            if (urlPatterns.size() == 0) continue;
            JspProperty property = new JspProperty(jspPropertyGroup.getIsXml(), jspPropertyGroup.getElIgnored(), jspPropertyGroup.getScriptingInvalid(), jspPropertyGroup.getPageEncoding(), jspPropertyGroup.getIncludePreludes(), jspPropertyGroup.getIncludeCodas(), jspPropertyGroup.getDeferredSyntaxAllowedAsLiteral(), jspPropertyGroup.getTrimDirectiveWhitespaces(), jspPropertyGroup.getDefaultContentType(), jspPropertyGroup.getBuffer(), jspPropertyGroup.getErrorOnUndeclaredNamespace());
            for (String urlPattern : urlPatterns) {
                String path = null;
                String extension = null;
                if (urlPattern.indexOf(42) < 0) {
                    path = urlPattern;
                } else {
                    String file;
                    int i = urlPattern.lastIndexOf(47);
                    if (i >= 0) {
                        path = urlPattern.substring(0, i + 1);
                        file = urlPattern.substring(i + 1);
                    } else {
                        file = urlPattern;
                    }
                    if (file.equals("*")) {
                        extension = "*";
                    } else if (file.startsWith("*.")) {
                        extension = file.substring(file.indexOf(46) + 1);
                    }
                    boolean isStar = "*".equals(extension);
                    if (path == null && (extension == null || isStar) || path != null && !isStar) {
                        if (!this.log.isWarnEnabled()) continue;
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.bad.urlpattern.propertygroup", urlPattern));
                        continue;
                    }
                }
                JspPropertyGroup propertyGroup = new JspPropertyGroup(path, extension, property);
                this.jspProperties.add(propertyGroup);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init() {
        if (!this.initialized) {
            JspConfig jspConfig = this;
            synchronized (jspConfig) {
                if (!this.initialized) {
                    this.processWebDotXml();
                    this.defaultJspProperty = new JspProperty(defaultIsXml, this.defaultIsELIgnored, defaultIsScriptingInvalid, null, null, null, this.defaultDeferedSyntaxAllowedAsLiteral, defaultTrimDirectiveWhitespaces, defaultDefaultContentType, defaultBuffer, defaultErrorOnUndeclaredNamespace);
                    this.initialized = true;
                }
            }
        }
    }

    private JspPropertyGroup selectProperty(JspPropertyGroup prev, JspPropertyGroup curr) {
        if (prev == null) {
            return curr;
        }
        if (prev.getExtension() == null) {
            return prev;
        }
        if (curr.getExtension() == null) {
            return curr;
        }
        String prevPath = prev.getPath();
        String currPath = curr.getPath();
        if (prevPath == null && currPath == null) {
            return prev;
        }
        if (prevPath == null && currPath != null) {
            return curr;
        }
        if (prevPath != null && currPath == null) {
            return prev;
        }
        if (prevPath.length() >= currPath.length()) {
            return prev;
        }
        return curr;
    }

    public JspProperty findJspProperty(String uri) {
        this.init();
        if (this.jspProperties == null || uri.endsWith(".tag") || uri.endsWith(".tagx")) {
            return this.defaultJspProperty;
        }
        String uriPath = null;
        int index = uri.lastIndexOf(47);
        if (index >= 0) {
            uriPath = uri.substring(0, index + 1);
        }
        String uriExtension = null;
        index = uri.lastIndexOf(46);
        if (index >= 0) {
            uriExtension = uri.substring(index + 1);
        }
        ArrayList<String> includePreludes = new ArrayList<String>();
        ArrayList<String> includeCodas = new ArrayList<String>();
        JspPropertyGroup isXmlMatch = null;
        JspPropertyGroup elIgnoredMatch = null;
        JspPropertyGroup scriptingInvalidMatch = null;
        JspPropertyGroup pageEncodingMatch = null;
        JspPropertyGroup deferedSyntaxAllowedAsLiteralMatch = null;
        JspPropertyGroup trimDirectiveWhitespacesMatch = null;
        JspPropertyGroup defaultContentTypeMatch = null;
        JspPropertyGroup bufferMatch = null;
        JspPropertyGroup errorOnUndeclaredNamespaceMatch = null;
        for (JspPropertyGroup jpg : this.jspProperties) {
            JspProperty jp = jpg.getJspProperty();
            String extension = jpg.getExtension();
            String path = jpg.getPath();
            if (extension != null ? path != null && uriPath != null && !uriPath.startsWith(path) || !extension.equals("*") && !extension.equals(uriExtension) : !uri.equals(path)) continue;
            if (jp.getIncludePrelude() != null) {
                includePreludes.addAll(jp.getIncludePrelude());
            }
            if (jp.getIncludeCoda() != null) {
                includeCodas.addAll(jp.getIncludeCoda());
            }
            if (jp.isXml() != null) {
                isXmlMatch = this.selectProperty(isXmlMatch, jpg);
            }
            if (jp.isELIgnored() != null) {
                elIgnoredMatch = this.selectProperty(elIgnoredMatch, jpg);
            }
            if (jp.isScriptingInvalid() != null) {
                scriptingInvalidMatch = this.selectProperty(scriptingInvalidMatch, jpg);
            }
            if (jp.getPageEncoding() != null) {
                pageEncodingMatch = this.selectProperty(pageEncodingMatch, jpg);
            }
            if (jp.isDeferedSyntaxAllowedAsLiteral() != null) {
                deferedSyntaxAllowedAsLiteralMatch = this.selectProperty(deferedSyntaxAllowedAsLiteralMatch, jpg);
            }
            if (jp.isTrimDirectiveWhitespaces() != null) {
                trimDirectiveWhitespacesMatch = this.selectProperty(trimDirectiveWhitespacesMatch, jpg);
            }
            if (jp.getDefaultContentType() != null) {
                defaultContentTypeMatch = this.selectProperty(defaultContentTypeMatch, jpg);
            }
            if (jp.getBuffer() != null) {
                bufferMatch = this.selectProperty(bufferMatch, jpg);
            }
            if (jp.isErrorOnUndeclaredNamespace() == null) continue;
            errorOnUndeclaredNamespaceMatch = this.selectProperty(errorOnUndeclaredNamespaceMatch, jpg);
        }
        String isXml = defaultIsXml;
        String isELIgnored = this.defaultIsELIgnored;
        String isScriptingInvalid = defaultIsScriptingInvalid;
        String pageEncoding = null;
        String isDeferedSyntaxAllowedAsLiteral = this.defaultDeferedSyntaxAllowedAsLiteral;
        String isTrimDirectiveWhitespaces = defaultTrimDirectiveWhitespaces;
        String defaultContentType = defaultDefaultContentType;
        String buffer = defaultBuffer;
        String errorOnUndeclaredNamespace = defaultErrorOnUndeclaredNamespace;
        if (isXmlMatch != null) {
            isXml = isXmlMatch.getJspProperty().isXml();
        }
        if (elIgnoredMatch != null) {
            isELIgnored = elIgnoredMatch.getJspProperty().isELIgnored();
        }
        if (scriptingInvalidMatch != null) {
            isScriptingInvalid = scriptingInvalidMatch.getJspProperty().isScriptingInvalid();
        }
        if (pageEncodingMatch != null) {
            pageEncoding = pageEncodingMatch.getJspProperty().getPageEncoding();
        }
        if (deferedSyntaxAllowedAsLiteralMatch != null) {
            isDeferedSyntaxAllowedAsLiteral = deferedSyntaxAllowedAsLiteralMatch.getJspProperty().isDeferedSyntaxAllowedAsLiteral();
        }
        if (trimDirectiveWhitespacesMatch != null) {
            isTrimDirectiveWhitespaces = trimDirectiveWhitespacesMatch.getJspProperty().isTrimDirectiveWhitespaces();
        }
        if (defaultContentTypeMatch != null) {
            defaultContentType = defaultContentTypeMatch.getJspProperty().getDefaultContentType();
        }
        if (bufferMatch != null) {
            buffer = bufferMatch.getJspProperty().getBuffer();
        }
        if (errorOnUndeclaredNamespaceMatch != null) {
            errorOnUndeclaredNamespace = errorOnUndeclaredNamespaceMatch.getJspProperty().isErrorOnUndeclaredNamespace();
        }
        return new JspProperty(isXml, isELIgnored, isScriptingInvalid, pageEncoding, includePreludes, includeCodas, isDeferedSyntaxAllowedAsLiteral, isTrimDirectiveWhitespaces, defaultContentType, buffer, errorOnUndeclaredNamespace);
    }

    public boolean isJspPage(String uri) {
        this.init();
        if (this.jspProperties == null) {
            return false;
        }
        String uriPath = null;
        int index = uri.lastIndexOf(47);
        if (index >= 0) {
            uriPath = uri.substring(0, index + 1);
        }
        String uriExtension = null;
        index = uri.lastIndexOf(46);
        if (index >= 0) {
            uriExtension = uri.substring(index + 1);
        }
        for (JspPropertyGroup jpg : this.jspProperties) {
            String extension = jpg.getExtension();
            String path = jpg.getPath();
            if (!(extension == null ? uri.equals(path) : !(path != null && !path.equals(uriPath) || !extension.equals("*") && !extension.equals(uriExtension)))) continue;
            return true;
        }
        return false;
    }

    public static class JspProperty {
        private final String isXml;
        private final String elIgnored;
        private final String scriptingInvalid;
        private final String pageEncoding;
        private final Collection<String> includePrelude;
        private final Collection<String> includeCoda;
        private final String deferedSyntaxAllowedAsLiteral;
        private final String trimDirectiveWhitespaces;
        private final String defaultContentType;
        private final String buffer;
        private final String errorOnUndeclaredNamespace;

        public JspProperty(String isXml, String elIgnored, String scriptingInvalid, String pageEncoding, Collection<String> includePrelude, Collection<String> includeCoda, String deferedSyntaxAllowedAsLiteral, String trimDirectiveWhitespaces, String defaultContentType, String buffer, String errorOnUndeclaredNamespace) {
            this.isXml = isXml;
            this.elIgnored = elIgnored;
            this.scriptingInvalid = scriptingInvalid;
            this.pageEncoding = pageEncoding;
            this.includePrelude = includePrelude;
            this.includeCoda = includeCoda;
            this.deferedSyntaxAllowedAsLiteral = deferedSyntaxAllowedAsLiteral;
            this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;
            this.defaultContentType = defaultContentType;
            this.buffer = buffer;
            this.errorOnUndeclaredNamespace = errorOnUndeclaredNamespace;
        }

        public String isXml() {
            return this.isXml;
        }

        public String isELIgnored() {
            return this.elIgnored;
        }

        public String isScriptingInvalid() {
            return this.scriptingInvalid;
        }

        public String getPageEncoding() {
            return this.pageEncoding;
        }

        public Collection<String> getIncludePrelude() {
            return this.includePrelude;
        }

        public Collection<String> getIncludeCoda() {
            return this.includeCoda;
        }

        public String isDeferedSyntaxAllowedAsLiteral() {
            return this.deferedSyntaxAllowedAsLiteral;
        }

        public String isTrimDirectiveWhitespaces() {
            return this.trimDirectiveWhitespaces;
        }

        public String getDefaultContentType() {
            return this.defaultContentType;
        }

        public String getBuffer() {
            return this.buffer;
        }

        public String isErrorOnUndeclaredNamespace() {
            return this.errorOnUndeclaredNamespace;
        }
    }

    public static class JspPropertyGroup {
        private final String path;
        private final String extension;
        private final JspProperty jspProperty;

        JspPropertyGroup(String path, String extension, JspProperty jspProperty) {
            this.path = path;
            this.extension = extension;
            this.jspProperty = jspProperty;
        }

        public String getPath() {
            return this.path;
        }

        public String getExtension() {
            return this.extension;
        }

        public JspProperty getJspProperty() {
            return this.jspProperty;
        }
    }
}

