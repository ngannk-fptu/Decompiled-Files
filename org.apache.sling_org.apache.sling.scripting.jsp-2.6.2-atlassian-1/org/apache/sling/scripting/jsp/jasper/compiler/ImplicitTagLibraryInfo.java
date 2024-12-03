/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.FunctionInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.InputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.compiler.TagFileProcessor;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ParserUtils;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;

class ImplicitTagLibraryInfo
extends TagLibraryInfo {
    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private static final String TAG_FILE_SUFFIX = ".tag";
    private static final String TAGX_FILE_SUFFIX = ".tagx";
    private static final String TAGS_SHORTNAME = "tags";
    private static final String TLIB_VERSION = "1.0";
    private static final String JSP_VERSION = "2.0";
    private static final String IMPLICIT_TLD = "implicit.tld";
    private Hashtable tagFileMap;
    private ParserController pc;
    private PageInfo pi;
    private Vector vec;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ImplicitTagLibraryInfo(JspCompilationContext ctxt, ParserController pc, PageInfo pi, String prefix, String tagdir, ErrorDispatcher err) throws JasperException {
        super(prefix, null);
        this.pc = pc;
        this.pi = pi;
        this.tagFileMap = new Hashtable();
        this.vec = new Vector();
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
                if (path.endsWith(TAG_FILE_SUFFIX) || path.endsWith(TAGX_FILE_SUFFIX)) {
                    String suffix = path.endsWith(TAG_FILE_SUFFIX) ? TAG_FILE_SUFFIX : TAGX_FILE_SUFFIX;
                    String tagName = path.substring(path.lastIndexOf("/") + 1);
                    tagName = tagName.substring(0, tagName.lastIndexOf(suffix));
                    this.tagFileMap.put(tagName, path);
                    continue;
                }
                if (!path.endsWith(IMPLICIT_TLD)) continue;
                InputStream in = null;
                try {
                    ParserUtils pu;
                    TreeNode tld;
                    in = ctxt.getResourceAsStream(path);
                    if (in == null) continue;
                    if (pi != null) {
                        pi.addDependant(path);
                    }
                    if ((tld = (pu = new ParserUtils()).parseXMLDocument(this.uri, in)).findAttribute("version") != null) {
                        this.jspversion = tld.findAttribute("version");
                    }
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
                        if ("shortname".equals(tname) || "short-name".equals(tname)) continue;
                        err.jspError("jsp.error.invalid.implicit", path);
                    }
                    try {
                        double version = Double.parseDouble(this.jspversion);
                        if (!(version < 2.0)) continue;
                        err.jspError("jsp.error.invalid.implicit.version", path);
                    }
                    catch (NumberFormatException e) {
                        err.jspError("jsp.error.invalid.implicit.version", path);
                    }
                }
                finally {
                    if (in == null) continue;
                    try {
                        in.close();
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
    }

    public TagFileInfo getTagFile(String shortName) {
        TagFileInfo tagFile = super.getTagFile(shortName);
        if (tagFile == null) {
            String path = (String)this.tagFileMap.get(shortName);
            if (path == null) {
                return null;
            }
            TagInfo tagInfo = null;
            try {
                tagInfo = TagFileProcessor.parseTagFileDirectives(this.pc, shortName, path, this);
            }
            catch (JasperException je) {
                throw new RuntimeException(je.toString(), (Throwable)((Object)je));
            }
            tagFile = new TagFileInfo(shortName, path, tagInfo);
            this.vec.addElement(tagFile);
            this.tagFiles = new TagFileInfo[this.vec.size()];
            this.vec.copyInto(this.tagFiles);
        }
        return tagFile;
    }

    public TagLibraryInfo[] getTagLibraryInfos() {
        Collection coll = this.pi.getTaglibs();
        return coll.toArray(new TagLibraryInfo[0]);
    }
}

