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
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.tld.TagFileXml
 *  org.apache.tomcat.util.descriptor.tld.TagXml
 *  org.apache.tomcat.util.descriptor.tld.TaglibXml
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 *  org.apache.tomcat.util.descriptor.tld.ValidatorXml
 */
package org.apache.jasper.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.compiler.TagConstants;
import org.apache.jasper.compiler.TagFileProcessor;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.tld.TagFileXml;
import org.apache.tomcat.util.descriptor.tld.TagXml;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.descriptor.tld.ValidatorXml;

class TagLibraryInfoImpl
extends TagLibraryInfo
implements TagConstants {
    private final JspCompilationContext ctxt;
    private final PageInfo pi;
    private final ErrorDispatcher err;
    private final ParserController parserController;
    private TagLibraryValidator tagLibraryValidator;

    private static void print(String name, String value, PrintWriter w) {
        if (value != null) {
            w.print(name + " = {\n\t");
            w.print(value);
            w.print("\n}\n");
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        TagLibraryInfoImpl.print("tlibversion", this.tlibversion, out);
        TagLibraryInfoImpl.print("jspversion", this.jspversion, out);
        TagLibraryInfoImpl.print("shortname", this.shortname, out);
        TagLibraryInfoImpl.print("urn", this.urn, out);
        TagLibraryInfoImpl.print("info", this.info, out);
        TagLibraryInfoImpl.print("uri", this.uri, out);
        TagLibraryInfoImpl.print("tagLibraryValidator", "" + this.tagLibraryValidator, out);
        for (TagInfo tagInfo : this.tags) {
            out.println(tagInfo.toString());
        }
        for (TagInfo tagInfo : this.tagFiles) {
            out.println(tagInfo.toString());
        }
        for (TagInfo tagInfo : this.functions) {
            out.println(tagInfo.toString());
        }
        return sw.toString();
    }

    TagLibraryInfoImpl(JspCompilationContext ctxt, ParserController pc, PageInfo pi, String prefix, String uriIn, TldResourcePath tldResourcePath, ErrorDispatcher err) throws JasperException {
        super(prefix, uriIn);
        this.ctxt = ctxt;
        this.parserController = pc;
        this.pi = pi;
        this.err = err;
        if (tldResourcePath == null) {
            tldResourcePath = this.generateTldResourcePath(this.uri, ctxt);
        }
        try (Jar jar = tldResourcePath.openJar();){
            String v;
            TaglibXml taglibXml;
            PageInfo pageInfo = ctxt.createCompiler().getPageInfo();
            if (pageInfo != null) {
                String path = tldResourcePath.getWebappPath();
                if (path != null) {
                    pageInfo.addDependant(path, ctxt.getLastModified(path, null));
                }
                if (jar != null) {
                    if (path == null) {
                        URL jarUrl = jar.getJarFileURL();
                        long lastMod = -1L;
                        URLConnection urlConn = null;
                        try {
                            urlConn = jarUrl.openConnection();
                            lastMod = urlConn.getLastModified();
                        }
                        catch (IOException ioe) {
                            throw new JasperException(ioe);
                        }
                        finally {
                            if (urlConn != null) {
                                try {
                                    urlConn.getInputStream().close();
                                }
                                catch (IOException iOException) {}
                            }
                        }
                        pageInfo.addDependant(jarUrl.toExternalForm(), lastMod);
                    }
                    String entryName = tldResourcePath.getEntryName();
                    try {
                        pageInfo.addDependant(jar.getURL(entryName), jar.getLastModified(entryName));
                    }
                    catch (IOException ioe) {
                        throw new JasperException(ioe);
                    }
                }
            }
            if (tldResourcePath.getUrl() == null) {
                err.jspError("jsp.error.tld.missing", prefix, this.uri);
            }
            if ((taglibXml = ctxt.getOptions().getTldCache().getTaglibXml(tldResourcePath)) == null) {
                err.jspError("jsp.error.tld.missing", prefix, this.uri);
            }
            this.jspversion = v = taglibXml.getJspVersion();
            this.tlibversion = taglibXml.getTlibVersion();
            this.shortname = taglibXml.getShortName();
            this.urn = taglibXml.getUri();
            this.info = taglibXml.getInfo();
            this.tagLibraryValidator = this.createValidator(taglibXml.getValidator());
            ArrayList<TagInfo> tagInfos = new ArrayList<TagInfo>();
            for (Object tagXml : taglibXml.getTags()) {
                tagInfos.add(this.createTagInfo((TagXml)tagXml));
            }
            ArrayList<TagFileInfo> tagFileInfos = new ArrayList<TagFileInfo>();
            for (TagFileXml tagFileXml : taglibXml.getTagFiles()) {
                tagFileInfos.add(this.createTagFileInfo(tagFileXml, jar));
            }
            HashSet<String> names = new HashSet<String>();
            List functionInfos = taglibXml.getFunctions();
            for (FunctionInfo functionInfo : functionInfos) {
                String name = functionInfo.getName();
                if (names.add(name)) continue;
                err.jspError("jsp.error.tld.fn.duplicate.name", name, this.uri);
            }
            if (this.tlibversion == null) {
                err.jspError("jsp.error.tld.mandatory.element.missing", "tlib-version", this.uri);
            }
            if (this.jspversion == null) {
                err.jspError("jsp.error.tld.mandatory.element.missing", "jsp-version", this.uri);
            }
            this.tags = tagInfos.toArray(new TagInfo[0]);
            this.tagFiles = tagFileInfos.toArray(new TagFileInfo[0]);
            this.functions = functionInfos.toArray(new FunctionInfo[0]);
        }
        catch (IOException ioe) {
            throw new JasperException(ioe);
        }
    }

    public TagLibraryInfo[] getTagLibraryInfos() {
        Collection<TagLibraryInfo> coll = this.pi.getTaglibs();
        return coll.toArray(new TagLibraryInfo[0]);
    }

    private TldResourcePath generateTldResourcePath(String uri, JspCompilationContext ctxt) throws JasperException {
        if (uri.indexOf(58) != -1) {
            this.err.jspError("jsp.error.taglibDirective.absUriCannotBeResolved", uri);
        } else if (uri.charAt(0) != '/') {
            uri = ctxt.resolveRelativeUri(uri);
            try {
                uri = new URI(uri).normalize().toString();
                if (uri.startsWith("../")) {
                    this.err.jspError("jsp.error.taglibDirective.uriInvalid", uri);
                }
            }
            catch (URISyntaxException e) {
                this.err.jspError("jsp.error.taglibDirective.uriInvalid", uri);
            }
        }
        URL url = null;
        try {
            url = ctxt.getResource(uri);
        }
        catch (Exception ex) {
            this.err.jspError("jsp.error.tld.unable_to_get_jar", uri, ex.toString());
        }
        if (uri.endsWith(".jar")) {
            if (url == null) {
                this.err.jspError("jsp.error.tld.missing_jar", uri);
            }
            return new TldResourcePath(url, uri, "META-INF/taglib.tld");
        }
        if (uri.startsWith("/WEB-INF/lib/") || uri.startsWith("/WEB-INF/classes/") || uri.startsWith("/WEB-INF/tags/") && uri.endsWith(".tld") && !uri.endsWith("implicit.tld")) {
            this.err.jspError("jsp.error.tld.invalid_tld_file", uri);
        }
        return new TldResourcePath(url, uri);
    }

    private TagInfo createTagInfo(TagXml tagXml) throws JasperException {
        String teiClassName = tagXml.getTeiClass();
        TagExtraInfo tei = null;
        if (teiClassName != null && !teiClassName.isEmpty()) {
            try {
                Class<?> teiClass = this.ctxt.getClassLoader().loadClass(teiClassName);
                tei = (TagExtraInfo)teiClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                this.err.jspError(e, "jsp.error.teiclass.instantiation", teiClassName);
            }
        }
        List attributeInfos = tagXml.getAttributes();
        List variableInfos = tagXml.getVariables();
        return new TagInfo(tagXml.getName(), tagXml.getTagClass(), tagXml.getBodyContent(), tagXml.getInfo(), (TagLibraryInfo)this, tei, attributeInfos.toArray(new TagAttributeInfo[0]), tagXml.getDisplayName(), tagXml.getSmallIcon(), tagXml.getLargeIcon(), variableInfos.toArray(new TagVariableInfo[0]), tagXml.hasDynamicAttributes());
    }

    private TagFileInfo createTagFileInfo(TagFileXml tagFileXml, Jar jar) throws JasperException {
        String name = tagFileXml.getName();
        String path = tagFileXml.getPath();
        if (path == null) {
            this.err.jspError("jsp.error.tagfile.missingPath", new String[0]);
        } else if (!path.startsWith("/META-INF/tags") && !path.startsWith("/WEB-INF/tags")) {
            this.err.jspError("jsp.error.tagfile.illegalPath", path);
        }
        if (jar == null && path.startsWith("/META-INF/tags")) {
            path = "/WEB-INF/classes" + path;
        }
        TagInfo tagInfo = TagFileProcessor.parseTagFileDirectives(this.parserController, name, path, jar, this);
        return new TagFileInfo(name, path, tagInfo);
    }

    private TagLibraryValidator createValidator(ValidatorXml validatorXml) throws JasperException {
        if (validatorXml == null) {
            return null;
        }
        String validatorClass = validatorXml.getValidatorClass();
        if (validatorClass == null || validatorClass.isEmpty()) {
            return null;
        }
        HashMap initParams = new HashMap(validatorXml.getInitParams());
        try {
            Class<?> tlvClass = this.ctxt.getClassLoader().loadClass(validatorClass);
            TagLibraryValidator tlv = (TagLibraryValidator)tlvClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            tlv.setInitParameters(initParams);
            return tlv;
        }
        catch (Exception e) {
            this.err.jspError(e, "jsp.error.tlvclass.instantiation", validatorClass);
            return null;
        }
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

