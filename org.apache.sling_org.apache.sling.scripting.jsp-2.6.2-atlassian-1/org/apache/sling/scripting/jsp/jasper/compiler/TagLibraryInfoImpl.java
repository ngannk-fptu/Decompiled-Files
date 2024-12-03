/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.FunctionInfo
 *  javax.servlet.jsp.tagext.PageData
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  javax.servlet.jsp.tagext.TagLibraryValidator
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  javax.servlet.jsp.tagext.ValidationMessage
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagLibraryValidator;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.compiler.TagConstants;
import org.apache.sling.scripting.jsp.jasper.compiler.TagFileProcessor;
import org.apache.sling.scripting.jsp.jasper.compiler.TldLocationsCache;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ParserUtils;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;

class TagLibraryInfoImpl
extends TagLibraryInfo
implements TagConstants {
    private Log log;
    private JspCompilationContext ctxt;
    private PageInfo pi;
    private ErrorDispatcher err;
    private ParserController parserController;
    protected TagLibraryValidator tagLibraryValidator;

    private final void print(String name, String value, PrintWriter w) {
        if (value != null) {
            w.print(name + " = {\n\t");
            w.print(value);
            w.print("\n}\n");
        }
    }

    public String toString() {
        int i;
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        this.print("tlibversion", this.tlibversion, out);
        this.print("jspversion", this.jspversion, out);
        this.print("shortname", this.shortname, out);
        this.print("urn", this.urn, out);
        this.print("info", this.info, out);
        this.print("uri", this.uri, out);
        this.print("tagLibraryValidator", "" + this.tagLibraryValidator, out);
        for (i = 0; i < this.tags.length; ++i) {
            out.println(this.tags[i].toString());
        }
        for (i = 0; i < this.tagFiles.length; ++i) {
            out.println(this.tagFiles[i].toString());
        }
        for (i = 0; i < this.functions.length; ++i) {
            out.println(this.functions[i].toString());
        }
        return sw.toString();
    }

    private InputStream getResourceAsStream(String uri) throws FileNotFoundException {
        try {
            String real = this.ctxt.getRealPath(uri);
            if (real == null) {
                return this.ctxt.getResourceAsStream(uri);
            }
            return this.ctxt.getRuntimeContext().getIOProvider().getInputStream(real);
        }
        catch (IOException ex) {
            return this.ctxt.getResourceAsStream(uri);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TagLibraryInfoImpl(JspCompilationContext ctxt, ParserController pc, PageInfo pi, String prefix, String uriIn, String[] location, ErrorDispatcher err) throws JasperException {
        block21: {
            super(prefix, uriIn);
            this.log = LogFactory.getLog(TagLibraryInfoImpl.class);
            this.ctxt = ctxt;
            this.parserController = pc;
            this.pi = pi;
            this.err = err;
            InputStream in = null;
            ZipFile jarFile = null;
            if (location == null) {
                location = this.generateTLDLocation(this.uri, ctxt);
            }
            try {
                if (!location[0].endsWith("jar")) {
                    try {
                        in = this.getResourceAsStream(location[0]);
                        if (in == null) {
                            throw new FileNotFoundException(location[0]);
                        }
                    }
                    catch (FileNotFoundException ex) {
                        err.jspError("jsp.error.file.not.found", location[0]);
                    }
                    this.parseTLD(ctxt, location[0], in, null);
                    PageInfo pageInfo = ctxt.activateCompiler().getPageInfo();
                    if (pageInfo != null) {
                        pageInfo.addDependant(location[0]);
                    }
                    break block21;
                }
                try {
                    URL jarFileUrl = new URL("jar:" + location[0] + "!/");
                    JarURLConnection conn = (JarURLConnection)jarFileUrl.openConnection();
                    conn.setUseCaches(false);
                    conn.connect();
                    jarFile = conn.getJarFile();
                    ZipEntry jarEntry = ((JarFile)jarFile).getEntry(location[1]);
                    in = ((JarFile)jarFile).getInputStream(jarEntry);
                    this.parseTLD(ctxt, location[0], in, jarFileUrl);
                }
                catch (Exception ex) {
                    err.jspError("jsp.error.tld.unable_to_read", location[0], location[1], ex.toString());
                }
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (Throwable throwable) {}
                }
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
    }

    public TagLibraryInfo[] getTagLibraryInfos() {
        Collection coll = this.pi.getTaglibs();
        return coll.toArray(new TagLibraryInfo[0]);
    }

    private void parseTLD(JspCompilationContext ctxt, String uri, InputStream in, URL jarFileUrl) throws JasperException {
        Vector<TagInfo> tagVector = new Vector<TagInfo>();
        Vector<TagFileInfo> tagFileVector = new Vector<TagFileInfo>();
        Hashtable<String, FunctionInfo> functionTable = new Hashtable<String, FunctionInfo>();
        ParserUtils pu = new ParserUtils();
        TreeNode tld = pu.parseXMLDocument(uri, in);
        this.jspversion = tld.findAttribute("version");
        Iterator list = tld.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("tlibversion".equals(tname) || "tlib-version".equals(tname)) {
                this.tlibversion = element.getBody();
                continue;
            }
            if ("jspversion".equals(tname) || "jsp-version".equals(tname)) {
                this.jspversion = element.getBody();
                continue;
            }
            if ("shortname".equals(tname) || "short-name".equals(tname)) {
                this.shortname = element.getBody();
                continue;
            }
            if ("uri".equals(tname)) {
                this.urn = element.getBody();
                continue;
            }
            if ("info".equals(tname) || "description".equals(tname)) {
                this.info = element.getBody();
                continue;
            }
            if ("validator".equals(tname)) {
                this.tagLibraryValidator = this.createValidator(element);
                continue;
            }
            if ("tag".equals(tname)) {
                tagVector.addElement(this.createTagInfo(element, this.jspversion));
                continue;
            }
            if ("tag-file".equals(tname)) {
                TagFileInfo tagFileInfo = this.createTagFileInfo(element, uri, jarFileUrl);
                tagFileVector.addElement(tagFileInfo);
                continue;
            }
            if ("function".equals(tname)) {
                FunctionInfo funcInfo = this.createFunctionInfo(element);
                String funcName = funcInfo.getName();
                if (functionTable.containsKey(funcName)) {
                    this.err.jspError("jsp.error.tld.fn.duplicate.name", funcName, uri);
                }
                functionTable.put(funcName, funcInfo);
                continue;
            }
            if ("display-name".equals(tname) || "small-icon".equals(tname) || "large-icon".equals(tname) || "listener".equals(tname) || "taglib-extension".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.taglib", tname));
        }
        if (this.tlibversion == null) {
            this.err.jspError("jsp.error.tld.mandatory.element.missing", "tlib-version");
        }
        if (this.jspversion == null) {
            this.err.jspError("jsp.error.tld.mandatory.element.missing", "jsp-version");
        }
        this.tags = new TagInfo[tagVector.size()];
        tagVector.copyInto(this.tags);
        this.tagFiles = new TagFileInfo[tagFileVector.size()];
        tagFileVector.copyInto(this.tagFiles);
        this.functions = new FunctionInfo[functionTable.size()];
        int i = 0;
        Enumeration enumeration = functionTable.elements();
        while (enumeration.hasMoreElements()) {
            this.functions[i++] = (FunctionInfo)enumeration.nextElement();
        }
    }

    private String[] generateTLDLocation(String uri, JspCompilationContext ctxt) throws JasperException {
        int uriType = TldLocationsCache.uriType(uri);
        if (uriType == 0) {
            this.err.jspError("jsp.error.taglibDirective.absUriCannotBeResolved", uri);
        } else if (uriType == 2) {
            uri = ctxt.resolveRelativeUri(uri);
        }
        String[] location = new String[2];
        location[0] = uri;
        if (location[0].endsWith("jar")) {
            URL url = null;
            try {
                url = ctxt.getResource(location[0]);
            }
            catch (Exception ex) {
                this.err.jspError("jsp.error.tld.unable_to_get_jar", location[0], ex.toString());
            }
            if (url == null) {
                this.err.jspError("jsp.error.tld.missing_jar", location[0]);
            }
            location[0] = url.toString();
            location[1] = "META-INF/taglib.tld";
        }
        return location;
    }

    private TagInfo createTagInfo(TreeNode elem, String jspVersion) throws JasperException {
        String tagName = null;
        String tagClassName = null;
        String teiClassName = null;
        String bodycontent = "JSP";
        String info = null;
        String displayName = null;
        String smallIcon = null;
        String largeIcon = null;
        boolean dynamicAttributes = false;
        Vector<TagAttributeInfo> attributeVector = new Vector<TagAttributeInfo>();
        Vector<TagVariableInfo> variableVector = new Vector<TagVariableInfo>();
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("name".equals(tname)) {
                tagName = element.getBody();
                continue;
            }
            if ("tagclass".equals(tname) || "tag-class".equals(tname)) {
                tagClassName = element.getBody();
                continue;
            }
            if ("teiclass".equals(tname) || "tei-class".equals(tname)) {
                teiClassName = element.getBody();
                continue;
            }
            if ("bodycontent".equals(tname) || "body-content".equals(tname)) {
                bodycontent = element.getBody();
                continue;
            }
            if ("display-name".equals(tname)) {
                displayName = element.getBody();
                continue;
            }
            if ("small-icon".equals(tname)) {
                smallIcon = element.getBody();
                continue;
            }
            if ("large-icon".equals(tname)) {
                largeIcon = element.getBody();
                continue;
            }
            if ("icon".equals(tname)) {
                TreeNode icon = element.findChild("small-icon");
                if (icon != null) {
                    smallIcon = icon.getBody();
                }
                if ((icon = element.findChild("large-icon")) == null) continue;
                largeIcon = icon.getBody();
                continue;
            }
            if ("info".equals(tname) || "description".equals(tname)) {
                info = element.getBody();
                continue;
            }
            if ("variable".equals(tname)) {
                variableVector.addElement(this.createVariable(element));
                continue;
            }
            if ("attribute".equals(tname)) {
                attributeVector.addElement(this.createAttribute(element, jspVersion));
                continue;
            }
            if ("dynamic-attributes".equals(tname)) {
                dynamicAttributes = JspUtil.booleanValue(element.getBody());
                continue;
            }
            if ("example".equals(tname) || "tag-extension".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.tag", tname));
        }
        TagExtraInfo tei = null;
        if (teiClassName != null && !teiClassName.equals("")) {
            try {
                Class<?> teiClass = this.ctxt.getClassLoader().loadClass(teiClassName);
                tei = (TagExtraInfo)teiClass.newInstance();
            }
            catch (Exception e) {
                this.err.jspError("jsp.error.teiclass.instantiation", teiClassName, e);
            }
        }
        Object[] tagAttributeInfo = new TagAttributeInfo[attributeVector.size()];
        attributeVector.copyInto(tagAttributeInfo);
        Object[] tagVariableInfos = new TagVariableInfo[variableVector.size()];
        variableVector.copyInto(tagVariableInfos);
        TagInfo taginfo = new TagInfo(tagName, tagClassName, bodycontent, info, (TagLibraryInfo)this, tei, (TagAttributeInfo[])tagAttributeInfo, displayName, smallIcon, largeIcon, (TagVariableInfo[])tagVariableInfos, dynamicAttributes);
        return taginfo;
    }

    private TagFileInfo createTagFileInfo(TreeNode elem, String uri, URL jarFileUrl) throws JasperException {
        String name = null;
        String path = null;
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode child = (TreeNode)list.next();
            String tname = child.getName();
            if ("name".equals(tname)) {
                name = child.getBody();
                continue;
            }
            if ("path".equals(tname)) {
                path = child.getBody();
                continue;
            }
            if ("example".equals(tname) || "tag-extension".equals(tname) || "icon".equals(tname) || "display-name".equals(tname) || "description".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.tagfile", tname));
        }
        if (path.startsWith("/META-INF/tags")) {
            if (jarFileUrl != null) {
                this.ctxt.setTagFileJarUrl(path, jarFileUrl);
            } else {
                String baseUrlStr;
                int index;
                URL baseUrl = this.ctxt.getOptions().getTldLocationsCache().getTldLocationURL(uri);
                if (baseUrl != null && (index = (baseUrlStr = baseUrl.toString()).indexOf("/META-INF/")) != -1) {
                    try {
                        URL finalUrl = new URL(baseUrlStr.substring(0, index) + path);
                        this.ctxt.setTagFileUrl(path, finalUrl);
                    }
                    catch (MalformedURLException malformedURLException) {}
                }
            }
        } else if (!path.startsWith("/WEB-INF/tags")) {
            this.err.jspError("jsp.error.tagfile.illegalPath", path);
        }
        TagInfo tagInfo = TagFileProcessor.parseTagFileDirectives(this.parserController, name, path, this);
        return new TagFileInfo(name, path, tagInfo);
    }

    TagAttributeInfo createAttribute(TreeNode elem, String jspVersion) {
        String name = null;
        String type = null;
        String expectedType = null;
        String methodSignature = null;
        boolean required = false;
        boolean rtexprvalue = false;
        boolean isFragment = false;
        boolean deferredValue = false;
        boolean deferredMethod = false;
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode child;
            String s;
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("name".equals(tname)) {
                name = element.getBody();
                continue;
            }
            if ("required".equals(tname)) {
                s = element.getBody();
                if (s == null) continue;
                required = JspUtil.booleanValue(s);
                continue;
            }
            if ("rtexprvalue".equals(tname)) {
                s = element.getBody();
                if (s == null) continue;
                rtexprvalue = JspUtil.booleanValue(s);
                continue;
            }
            if ("type".equals(tname)) {
                type = element.getBody();
                if (!"1.2".equals(jspVersion) || !type.equals("Boolean") && !type.equals("Byte") && !type.equals("Character") && !type.equals("Double") && !type.equals("Float") && !type.equals("Integer") && !type.equals("Long") && !type.equals("Object") && !type.equals("Short") && !type.equals("String")) continue;
                type = "java.lang." + type;
                continue;
            }
            if ("fragment".equals(tname)) {
                s = element.getBody();
                if (s == null) continue;
                isFragment = JspUtil.booleanValue(s);
                continue;
            }
            if ("deferred-value".equals(tname)) {
                deferredValue = true;
                type = "javax.el.ValueExpression";
                child = element.findChild("type");
                if (child != null) {
                    expectedType = child.getBody();
                    if (expectedType == null) continue;
                    expectedType = expectedType.trim();
                    continue;
                }
                expectedType = "java.lang.Object";
                continue;
            }
            if ("deferred-method".equals(tname)) {
                deferredMethod = true;
                type = "javax.el.MethodExpression";
                child = element.findChild("method-signature");
                if (child != null) {
                    methodSignature = child.getBody();
                    if (methodSignature == null) continue;
                    methodSignature = methodSignature.trim();
                    continue;
                }
                methodSignature = "java.lang.Object method()";
                continue;
            }
            if ("description".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.attribute", tname));
        }
        if (isFragment) {
            type = "javax.servlet.jsp.tagext.JspFragment";
            rtexprvalue = true;
        }
        if (!rtexprvalue && type == null) {
            type = "java.lang.String";
        }
        return new TagAttributeInfo(name, required, type, rtexprvalue, isFragment, null, deferredValue, deferredMethod, expectedType, methodSignature);
    }

    TagVariableInfo createVariable(TreeNode elem) {
        String nameGiven = null;
        String nameFromAttribute = null;
        String className = "java.lang.String";
        boolean declare = true;
        int scope = 0;
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            String s;
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("name-given".equals(tname)) {
                nameGiven = element.getBody();
                continue;
            }
            if ("name-from-attribute".equals(tname)) {
                nameFromAttribute = element.getBody();
                continue;
            }
            if ("variable-class".equals(tname)) {
                className = element.getBody();
                continue;
            }
            if ("declare".equals(tname)) {
                s = element.getBody();
                if (s == null) continue;
                declare = JspUtil.booleanValue(s);
                continue;
            }
            if ("scope".equals(tname)) {
                s = element.getBody();
                if (s == null) continue;
                if ("NESTED".equals(s)) {
                    scope = 0;
                    continue;
                }
                if ("AT_BEGIN".equals(s)) {
                    scope = 1;
                    continue;
                }
                if (!"AT_END".equals(s)) continue;
                scope = 2;
                continue;
            }
            if ("description".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.variable", tname));
        }
        return new TagVariableInfo(nameGiven, nameFromAttribute, className, declare, scope);
    }

    private TagLibraryValidator createValidator(TreeNode elem) throws JasperException {
        String validatorClass = null;
        Hashtable<String, String> initParams = new Hashtable<String, String>();
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("validator-class".equals(tname)) {
                validatorClass = element.getBody();
                continue;
            }
            if ("init-param".equals(tname)) {
                String[] initParam = this.createInitParam(element);
                initParams.put(initParam[0], initParam[1]);
                continue;
            }
            if ("description".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.validator", tname));
        }
        TagLibraryValidator tlv = null;
        if (validatorClass != null && !validatorClass.equals("")) {
            try {
                Class<?> tlvClass = this.ctxt.getClassLoader().loadClass(validatorClass);
                tlv = (TagLibraryValidator)tlvClass.newInstance();
            }
            catch (Exception e) {
                this.err.jspError("jsp.error.tlvclass.instantiation", validatorClass, e);
            }
        }
        if (tlv != null) {
            tlv.setInitParameters(initParams);
        }
        return tlv;
    }

    String[] createInitParam(TreeNode elem) {
        String[] initParam = new String[2];
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("param-name".equals(tname)) {
                initParam[0] = element.getBody();
                continue;
            }
            if ("param-value".equals(tname)) {
                initParam[1] = element.getBody();
                continue;
            }
            if ("description".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.initParam", tname));
        }
        return initParam;
    }

    FunctionInfo createFunctionInfo(TreeNode elem) {
        String name = null;
        String klass = null;
        String signature = null;
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode)list.next();
            String tname = element.getName();
            if ("name".equals(tname)) {
                name = element.getBody();
                continue;
            }
            if ("function-class".equals(tname)) {
                klass = element.getBody();
                continue;
            }
            if ("function-signature".equals(tname)) {
                signature = element.getBody();
                continue;
            }
            if ("display-name".equals(tname) || "small-icon".equals(tname) || "large-icon".equals(tname) || "description".equals(tname) || "example".equals(tname) || !this.log.isWarnEnabled()) continue;
            this.log.warn(Localizer.getMessage("jsp.warning.unknown.element.in.function", tname));
        }
        return new FunctionInfo(name, klass, signature);
    }

    public TagLibraryValidator getTagLibraryValidator() {
        return this.tagLibraryValidator;
    }

    public ValidationMessage[] validate(PageData thePage) {
        TagLibraryValidator tlv = this.getTagLibraryValidator();
        if (tlv == null) {
            return null;
        }
        String uri = this.getURI();
        if (uri.startsWith("/")) {
            uri = "urn:jsptld:" + uri;
        }
        return tlv.validate(this.getPrefixString(), uri, thePage);
    }
}

