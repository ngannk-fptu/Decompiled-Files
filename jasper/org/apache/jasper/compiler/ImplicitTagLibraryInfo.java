/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.tagext.FunctionInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  org.apache.tomcat.util.descriptor.tld.ImplicitTldRuleSet
 *  org.apache.tomcat.util.descriptor.tld.TaglibXml
 *  org.apache.tomcat.util.descriptor.tld.TldParser
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.jasper.compiler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.compiler.TagFileProcessor;
import org.apache.tomcat.util.descriptor.tld.ImplicitTldRuleSet;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.digester.RuleSet;
import org.xml.sax.SAXException;

class ImplicitTagLibraryInfo
extends TagLibraryInfo {
    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private static final String TAG_FILE_SUFFIX = ".tag";
    private static final String TAGX_FILE_SUFFIX = ".tagx";
    private static final String TAGS_SHORTNAME = "tags";
    private static final String TLIB_VERSION = "1.0";
    private static final String JSP_VERSION = "2.0";
    private static final String IMPLICIT_TLD = "implicit.tld";
    private final Map<String, String> tagFileMap;
    private final ParserController pc;
    private final PageInfo pi;
    private final List<TagFileInfo> list;

    ImplicitTagLibraryInfo(JspCompilationContext ctxt, ParserController pc, PageInfo pi, String prefix, String tagdir, ErrorDispatcher err) throws JasperException {
        super(prefix, null);
        this.pc = pc;
        this.pi = pi;
        this.tagFileMap = new ConcurrentHashMap<String, String>();
        this.list = Collections.synchronizedList(new ArrayList());
        this.functions = new FunctionInfo[0];
        this.tlibversion = TLIB_VERSION;
        this.jspversion = JSP_VERSION;
        if (!tagdir.startsWith(WEB_INF_TAGS)) {
            err.jspError("jsp.error.invalid.tagdir", tagdir);
        }
        if (tagdir.equals(WEB_INF_TAGS) || tagdir.equals("/WEB-INF/tags/")) {
            this.shortname = TAGS_SHORTNAME;
        } else {
            this.shortname = tagdir.substring(WEB_INF_TAGS.length());
            this.shortname = this.shortname.replace('/', '-');
        }
        Set<String> dirList = ctxt.getResourcePaths(tagdir);
        if (dirList != null) {
            for (String path : dirList) {
                TaglibXml taglibXml;
                if (path.endsWith(TAG_FILE_SUFFIX) || path.endsWith(TAGX_FILE_SUFFIX)) {
                    String suffix = path.endsWith(TAG_FILE_SUFFIX) ? TAG_FILE_SUFFIX : TAGX_FILE_SUFFIX;
                    String tagName = path.substring(path.lastIndexOf(47) + 1);
                    tagName = tagName.substring(0, tagName.lastIndexOf(suffix));
                    this.tagFileMap.put(tagName, path);
                    continue;
                }
                if (!path.endsWith(IMPLICIT_TLD)) continue;
                try {
                    URL url = ctxt.getResource(path);
                    TldResourcePath resourcePath = new TldResourcePath(url, path);
                    ServletContext servletContext = ctxt.getServletContext();
                    boolean validate = Boolean.parseBoolean(servletContext.getInitParameter("org.apache.jasper.XML_VALIDATE_TLD"));
                    String blockExternalString = servletContext.getInitParameter("org.apache.jasper.XML_BLOCK_EXTERNAL");
                    boolean blockExternal = blockExternalString == null ? true : Boolean.parseBoolean(blockExternalString);
                    TldParser parser = new TldParser(true, validate, (RuleSet)new ImplicitTldRuleSet(), blockExternal);
                    taglibXml = parser.parse(resourcePath);
                }
                catch (IOException | SAXException e) {
                    err.jspError(e);
                    throw new JasperException(e);
                }
                this.tlibversion = taglibXml.getTlibVersion();
                this.jspversion = taglibXml.getJspVersion();
                try {
                    double version = Double.parseDouble(this.jspversion);
                    if (version < 2.0) {
                        err.jspError("jsp.error.invalid.implicit.version", path);
                    }
                }
                catch (NumberFormatException e) {
                    err.jspError("jsp.error.invalid.implicit.version", path);
                }
                if (pi == null) continue;
                pi.addDependant(path, ctxt.getLastModified(path));
            }
        }
    }

    public TagFileInfo getTagFile(String shortName) {
        TagFileInfo tagFile = super.getTagFile(shortName);
        if (tagFile == null) {
            String path = this.tagFileMap.get(shortName);
            if (path == null) {
                return null;
            }
            TagInfo tagInfo = null;
            try {
                tagInfo = TagFileProcessor.parseTagFileDirectives(this.pc, shortName, path, null, this);
            }
            catch (JasperException je) {
                throw new RuntimeException(je.toString(), (Throwable)((Object)je));
            }
            tagFile = new TagFileInfo(shortName, path, tagInfo);
            this.list.add(tagFile);
            this.tagFiles = this.list.toArray(new TagFileInfo[0]);
        }
        return tagFile;
    }

    public TagLibraryInfo[] getTagLibraryInfos() {
        Collection<TagLibraryInfo> coll = this.pi.getTaglibs();
        return coll.toArray(new TagLibraryInfo[0]);
    }
}

