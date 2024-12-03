/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.util.Vector;
import javax.servlet.ServletContext;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ParserUtils;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;
import org.xml.sax.InputSource;

public class JspConfig {
    private static final String WEB_XML = "/WEB-INF/web.xml";
    private Log log = LogFactory.getLog(JspConfig.class);
    private Vector jspProperties = null;
    private ServletContext ctxt;
    private boolean initialized = false;
    private String defaultIsXml = null;
    private String defaultIsELIgnored = null;
    private String defaultIsScriptingInvalid = null;
    private String defaultDeferedSyntaxAllowedAsLiteral = null;
    private String defaultTrimDirectiveWhitespaces = null;
    private JspProperty defaultJspProperty;

    public JspConfig(ServletContext ctxt) {
        this.ctxt = ctxt;
    }

    private double getVersion(TreeNode webApp) {
        String v = webApp.findAttribute("version");
        if (v != null) {
            try {
                return Double.parseDouble(v);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return 2.3;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void processWebDotXml(ServletContext ctxt) throws JasperException {
        is = null;
        try {
            uri = ctxt.getResource("/WEB-INF/web.xml");
            if (uri == null) {
                return;
            }
            is = uri.openStream();
            ip = new InputSource(is);
            ip.setSystemId(uri.toExternalForm());
            pu = new ParserUtils();
            webApp = pu.parseXMLDocument("/WEB-INF/web.xml", ip);
            if (webApp == null || this.getVersion(webApp) < 2.4) {
                this.defaultIsELIgnored = "true";
                return;
            }
            jspConfig = webApp.findChild("jsp-config");
            if (jspConfig == null) {
                return;
            }
            this.jspProperties = new Vector<E>();
            jspPropertyList = jspConfig.findChildren("jsp-property-group");
            block18: while (true) {
                if (jspPropertyList.hasNext() == false) return;
                element = (TreeNode)jspPropertyList.next();
                list = element.findChildren();
                urlPatterns = new Vector<String>();
                pageEncoding = null;
                scriptingInvalid = null;
                elIgnored = null;
                isXml = null;
                includePrelude = new Vector<String>();
                includeCoda = new Vector<String>();
                deferredSyntaxAllowedAsLiteral = null;
                trimDirectiveWhitespaces = null;
                while (list.hasNext()) {
                    element = (TreeNode)list.next();
                    tname = element.getName();
                    if ("url-pattern".equals(tname)) {
                        urlPatterns.addElement(element.getBody());
                        continue;
                    }
                    if ("page-encoding".equals(tname)) {
                        pageEncoding = element.getBody();
                        continue;
                    }
                    if ("is-xml".equals(tname)) {
                        isXml = element.getBody();
                        continue;
                    }
                    if ("el-ignored".equals(tname)) {
                        elIgnored = element.getBody();
                        continue;
                    }
                    if ("scripting-invalid".equals(tname)) {
                        scriptingInvalid = element.getBody();
                        continue;
                    }
                    if ("include-prelude".equals(tname)) {
                        includePrelude.addElement(element.getBody());
                        continue;
                    }
                    if ("include-coda".equals(tname)) {
                        includeCoda.addElement(element.getBody());
                        continue;
                    }
                    if ("deferred-syntax-allowed-as-literal".equals(tname)) {
                        deferredSyntaxAllowedAsLiteral = element.getBody();
                        continue;
                    }
                    if (!"trim-directive-whitespaces".equals(tname)) continue;
                    trimDirectiveWhitespaces = element.getBody();
                }
                if (urlPatterns.size() == 0) continue;
                p = 0;
                while (true) {
                    block41: {
                        if (p < urlPatterns.size()) ** break;
                        continue block18;
                        urlPattern = (String)urlPatterns.elementAt(p);
                        path = null;
                        extension = null;
                        if (urlPattern.indexOf(42) >= 0) break block41;
                        path = urlPattern;
                        ** GOTO lbl-1000
                    }
                    i = urlPattern.lastIndexOf(47);
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
                    isStar = "*".equals(extension);
                    if (path == null && (extension == null || isStar) || path != null && !isStar) {
                        if (this.log.isWarnEnabled()) {
                            this.log.warn(Localizer.getMessage("jsp.warning.bad.urlpattern.propertygroup", urlPattern));
                        }
                    } else lbl-1000:
                    // 2 sources

                    {
                        property = new JspProperty(isXml, elIgnored, scriptingInvalid, pageEncoding, includePrelude, includeCoda, deferredSyntaxAllowedAsLiteral, trimDirectiveWhitespaces);
                        propertyGroup = new JspPropertyGroup(path, extension, property);
                        this.jspProperties.addElement(propertyGroup);
                    }
                    ++p;
                }
                break;
            }
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Throwable var28_35) {}
            }
        }
    }

    private void init() throws JasperException {
        if (!this.initialized) {
            this.processWebDotXml(this.ctxt);
            this.defaultJspProperty = new JspProperty(this.defaultIsXml, this.defaultIsELIgnored, this.defaultIsScriptingInvalid, null, null, null, this.defaultDeferedSyntaxAllowedAsLiteral, this.defaultTrimDirectiveWhitespaces);
            this.initialized = true;
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

    public JspProperty findJspProperty(String uri) throws JasperException {
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
        Vector includePreludes = new Vector();
        Vector includeCodas = new Vector();
        JspPropertyGroup isXmlMatch = null;
        JspPropertyGroup elIgnoredMatch = null;
        JspPropertyGroup scriptingInvalidMatch = null;
        JspPropertyGroup pageEncodingMatch = null;
        JspPropertyGroup deferedSyntaxAllowedAsLiteralMatch = null;
        JspPropertyGroup trimDirectiveWhitespacesMatch = null;
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
            if (jp.isTrimDirectiveWhitespaces() == null) continue;
            trimDirectiveWhitespacesMatch = this.selectProperty(trimDirectiveWhitespacesMatch, jpg);
        }
        String isXml = this.defaultIsXml;
        String isELIgnored = this.defaultIsELIgnored;
        String isScriptingInvalid = this.defaultIsScriptingInvalid;
        String pageEncoding = null;
        String isDeferedSyntaxAllowedAsLiteral = this.defaultDeferedSyntaxAllowedAsLiteral;
        String isTrimDirectiveWhitespaces = this.defaultTrimDirectiveWhitespaces;
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
        return new JspProperty(isXml, isELIgnored, isScriptingInvalid, pageEncoding, includePreludes, includeCodas, isDeferedSyntaxAllowedAsLiteral, isTrimDirectiveWhitespaces);
    }

    public boolean isJspPage(String uri) throws JasperException {
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
            JspProperty jp = jpg.getJspProperty();
            String extension = jpg.getExtension();
            String path = jpg.getPath();
            if (!(extension == null ? uri.equals(path) : !(path != null && !path.equals(uriPath) || !extension.equals("*") && !extension.equals(uriExtension)))) continue;
            return true;
        }
        return false;
    }

    public static class JspProperty {
        private String isXml;
        private String elIgnored;
        private String scriptingInvalid;
        private String pageEncoding;
        private Vector includePrelude;
        private Vector includeCoda;
        private String deferedSyntaxAllowedAsLiteral;
        private String trimDirectiveWhitespaces;

        public JspProperty(String isXml, String elIgnored, String scriptingInvalid, String pageEncoding, Vector includePrelude, Vector includeCoda, String deferedSyntaxAllowedAsLiteral, String trimDirectiveWhitespaces) {
            this.isXml = isXml;
            this.elIgnored = elIgnored;
            this.scriptingInvalid = scriptingInvalid;
            this.pageEncoding = pageEncoding;
            this.includePrelude = includePrelude;
            this.includeCoda = includeCoda;
            this.deferedSyntaxAllowedAsLiteral = deferedSyntaxAllowedAsLiteral;
            this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;
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

        public Vector getIncludePrelude() {
            return this.includePrelude;
        }

        public Vector getIncludeCoda() {
            return this.includeCoda;
        }

        public String isDeferedSyntaxAllowedAsLiteral() {
            return this.deferedSyntaxAllowedAsLiteral;
        }

        public String isTrimDirectiveWhitespaces() {
            return this.trimDirectiveWhitespaces;
        }
    }

    static class JspPropertyGroup {
        private String path;
        private String extension;
        private JspProperty jspProperty;

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

