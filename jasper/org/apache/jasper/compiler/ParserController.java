/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 */
package org.apache.jasper.compiler;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.EncodingDetector;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.JspDocumentParser;
import org.apache.jasper.compiler.JspReader;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.Parser;
import org.apache.jasper.compiler.TagConstants;
import org.apache.tomcat.Jar;
import org.xml.sax.Attributes;

class ParserController
implements TagConstants {
    private static final String CHARSET = "charset=";
    private final JspCompilationContext ctxt;
    private final Compiler compiler;
    private final ErrorDispatcher err;
    private boolean isXml;
    private final Deque<String> baseDirStack = new ArrayDeque<String>();
    private boolean isEncodingSpecifiedInProlog;
    private boolean isBomPresent;
    private int skip;
    private String sourceEnc;
    private boolean isDefaultPageEncoding;
    private boolean isTagFile;
    private boolean directiveOnly;

    ParserController(JspCompilationContext ctxt, Compiler compiler) {
        this.ctxt = ctxt;
        this.compiler = compiler;
        this.err = compiler.getErrorDispatcher();
    }

    public JspCompilationContext getJspCompilationContext() {
        return this.ctxt;
    }

    public Compiler getCompiler() {
        return this.compiler;
    }

    public Node.Nodes parse(String inFileName) throws JasperException, IOException {
        this.isTagFile = this.ctxt.isTagFile();
        this.directiveOnly = false;
        return this.doParse(inFileName, null, this.ctxt.getTagFileJar());
    }

    public Node.Nodes parseDirectives(String inFileName) throws JasperException, IOException {
        this.isTagFile = this.ctxt.isTagFile();
        this.directiveOnly = true;
        return this.doParse(inFileName, null, this.ctxt.getTagFileJar());
    }

    public Node.Nodes parse(String inFileName, Node parent, Jar jar) throws JasperException, IOException {
        return this.doParse(inFileName, parent, jar);
    }

    public Node.Nodes parseTagFileDirectives(String inFileName, Jar jar) throws JasperException, IOException {
        boolean isTagFileSave = this.isTagFile;
        boolean directiveOnlySave = this.directiveOnly;
        this.isTagFile = true;
        this.directiveOnly = true;
        Node.Nodes page = this.doParse(inFileName, null, jar);
        this.directiveOnly = directiveOnlySave;
        this.isTagFile = isTagFileSave;
        return page;
    }

    private Node.Nodes doParse(String inFileName, Node parent, Jar jar) throws FileNotFoundException, JasperException, IOException {
        Node.Nodes parsedPage = null;
        this.isEncodingSpecifiedInProlog = false;
        this.isBomPresent = false;
        this.isDefaultPageEncoding = false;
        String absFileName = this.resolveFileName(inFileName);
        String jspConfigPageEnc = this.getJspConfigPageEncoding(absFileName);
        this.determineSyntaxAndEncoding(absFileName, jar, jspConfigPageEnc);
        if (parent != null) {
            if (jar == null) {
                this.compiler.getPageInfo().addDependant(absFileName, this.ctxt.getLastModified(absFileName));
            } else {
                String entry = absFileName.substring(1);
                this.compiler.getPageInfo().addDependant(jar.getURL(entry), jar.getLastModified(entry));
            }
        }
        if ((this.isXml && this.isEncodingSpecifiedInProlog || this.isBomPresent) && jspConfigPageEnc != null && !jspConfigPageEnc.equals(this.sourceEnc) && (!jspConfigPageEnc.startsWith("UTF-16") || !this.sourceEnc.startsWith("UTF-16"))) {
            this.err.jspError("jsp.error.prolog_config_encoding_mismatch", this.sourceEnc, jspConfigPageEnc);
        }
        if (this.isXml) {
            parsedPage = JspDocumentParser.parse(this, absFileName, jar, parent, this.isTagFile, this.directiveOnly, this.sourceEnc, jspConfigPageEnc, this.isEncodingSpecifiedInProlog, this.isBomPresent);
        } else {
            try (InputStreamReader inStreamReader = JspUtil.getReader(absFileName, this.sourceEnc, jar, this.ctxt, this.err, this.skip);){
                JspReader jspReader = new JspReader(this.ctxt, absFileName, inStreamReader, this.err);
                parsedPage = Parser.parse(this, jspReader, parent, this.isTagFile, this.directiveOnly, jar, this.sourceEnc, jspConfigPageEnc, this.isDefaultPageEncoding, this.isBomPresent);
            }
        }
        this.baseDirStack.remove();
        return parsedPage;
    }

    private String getJspConfigPageEncoding(String absFileName) {
        JspConfig jspConfig = this.ctxt.getOptions().getJspConfig();
        JspConfig.JspProperty jspProperty = jspConfig.findJspProperty(absFileName);
        return jspProperty.getPageEncoding();
    }

    private void determineSyntaxAndEncoding(String absFileName, Jar jar, String jspConfigPageEnc) throws JasperException, IOException {
        this.isXml = false;
        boolean isExternal = false;
        boolean revert = false;
        JspConfig jspConfig = this.ctxt.getOptions().getJspConfig();
        JspConfig.JspProperty jspProperty = jspConfig.findJspProperty(absFileName);
        if (jspProperty.isXml() != null) {
            this.isXml = JspUtil.booleanValue(jspProperty.isXml());
            isExternal = true;
        } else if (absFileName.endsWith(".jspx") || absFileName.endsWith(".tagx")) {
            this.isXml = true;
            isExternal = true;
        }
        if (isExternal && !this.isXml) {
            this.sourceEnc = jspConfigPageEnc;
            if (this.sourceEnc != null) {
                return;
            }
            this.sourceEnc = "ISO-8859-1";
        } else {
            EncodingDetector encodingDetector;
            try (BufferedInputStream bis = JspUtil.getInputStream(absFileName, jar, this.ctxt);){
                encodingDetector = new EncodingDetector(bis);
            }
            this.sourceEnc = encodingDetector.getEncoding();
            this.isEncodingSpecifiedInProlog = encodingDetector.isEncodingSpecifiedInProlog();
            this.isBomPresent = encodingDetector.getSkip() > 0;
            this.skip = encodingDetector.getSkip();
            if (!this.isXml && this.sourceEnc.equals("UTF-8")) {
                this.sourceEnc = "ISO-8859-1";
                revert = true;
            }
        }
        if (this.isXml) {
            return;
        }
        JspReader jspReader = null;
        try {
            jspReader = new JspReader(this.ctxt, absFileName, this.sourceEnc, jar, this.err);
        }
        catch (FileNotFoundException ex) {
            throw new JasperException(ex);
        }
        Mark startMark = jspReader.mark();
        if (!isExternal) {
            jspReader.reset(startMark);
            if (this.hasJspRoot(jspReader)) {
                if (revert) {
                    this.sourceEnc = "UTF-8";
                }
                this.isXml = true;
                return;
            }
            if (revert && this.isBomPresent) {
                this.sourceEnc = "UTF-8";
            }
            this.isXml = false;
        }
        if (!this.isBomPresent) {
            this.sourceEnc = jspConfigPageEnc;
            if (this.sourceEnc == null) {
                this.sourceEnc = this.getPageEncodingForJspSyntax(jspReader, startMark);
                if (this.sourceEnc == null) {
                    this.sourceEnc = "ISO-8859-1";
                    this.isDefaultPageEncoding = true;
                }
            }
        }
    }

    private String getPageEncodingForJspSyntax(JspReader jspReader, Mark startMark) throws JasperException {
        String encoding = null;
        String saveEncoding = null;
        jspReader.reset(startMark);
        while (jspReader.skipUntil("<") != null) {
            if (jspReader.matches("%--")) {
                if (jspReader.skipUntil("--%>") != null) continue;
                break;
            }
            boolean isDirective = jspReader.matches("%@");
            if (isDirective) {
                jspReader.skipSpaces();
            } else {
                isDirective = jspReader.matches("jsp:directive.");
            }
            if (!isDirective || (!jspReader.matches("tag") || jspReader.matches("lib")) && !jspReader.matches("page")) continue;
            jspReader.skipSpaces();
            Attributes attrs = Parser.parseAttributes(this, jspReader);
            encoding = this.getPageEncodingFromDirective(attrs, "pageEncoding");
            if (encoding != null) break;
            encoding = this.getPageEncodingFromDirective(attrs, "contentType");
            if (encoding == null) continue;
            saveEncoding = encoding;
        }
        if (encoding == null) {
            encoding = saveEncoding;
        }
        return encoding;
    }

    private String getPageEncodingFromDirective(Attributes attrs, String attrName) {
        int loc;
        String value = attrs.getValue(attrName);
        if (attrName.equals("pageEncoding")) {
            return value;
        }
        String contentType = value;
        String encoding = null;
        if (contentType != null && (loc = contentType.indexOf(CHARSET)) != -1) {
            encoding = contentType.substring(loc + CHARSET.length());
        }
        return encoding;
    }

    private String resolveFileName(String inFileName) {
        String fileName = inFileName.replace('\\', '/');
        boolean isAbsolute = fileName.startsWith("/");
        fileName = isAbsolute ? fileName : this.baseDirStack.peekFirst() + fileName;
        String baseDir = fileName.substring(0, fileName.lastIndexOf(47) + 1);
        this.baseDirStack.addFirst(baseDir);
        return fileName;
    }

    private boolean hasJspRoot(JspReader reader) {
        String xmlnsDecl;
        int c;
        Mark start = null;
        while ((start = reader.skipUntil("<")) != null && ((c = reader.nextChar()) == 33 || c == 63)) {
        }
        if (start == null) {
            return false;
        }
        Mark stop = reader.skipUntil(":root");
        if (stop == null) {
            return false;
        }
        String prefix = reader.getText(start, stop).substring(1);
        start = stop;
        stop = reader.skipUntil(">");
        if (stop == null) {
            return false;
        }
        String root = reader.getText(start, stop);
        int index = root.indexOf(xmlnsDecl = "xmlns:" + prefix);
        if (index == -1) {
            return false;
        }
        index += xmlnsDecl.length();
        while (index < root.length() && Character.isWhitespace(root.charAt(index))) {
            ++index;
        }
        if (index < root.length() && root.charAt(index) == '=') {
            ++index;
            while (index < root.length() && Character.isWhitespace(root.charAt(index))) {
                ++index;
            }
            if (index < root.length() && (root.charAt(index) == '\"' || root.charAt(index) == '\'') && root.regionMatches(++index, "http://java.sun.com/JSP/Page", 0, "http://java.sun.com/JSP/Page".length())) {
                return true;
            }
        }
        return false;
    }
}

