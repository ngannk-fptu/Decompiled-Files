/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.ImplicitTagLibraryInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.JspReader;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.compiler.TagConstants;
import org.apache.sling.scripting.jsp.jasper.compiler.TagLibraryInfoImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

class Parser
implements TagConstants {
    private ParserController parserController;
    private JspCompilationContext ctxt;
    private JspReader reader;
    private String currentFile;
    private Mark start;
    private ErrorDispatcher err;
    private int scriptlessCount;
    private boolean isTagFile;
    private boolean directivesOnly;
    private URL jarFileUrl;
    private PageInfo pageInfo;
    private static final String JAVAX_BODY_CONTENT_PARAM = "JAVAX_BODY_CONTENT_PARAM";
    private static final String JAVAX_BODY_CONTENT_PLUGIN = "JAVAX_BODY_CONTENT_PLUGIN";
    private static final String JAVAX_BODY_CONTENT_TEMPLATE_TEXT = "JAVAX_BODY_CONTENT_TEMPLATE_TEXT";

    private Parser(ParserController pc, JspReader reader, boolean isTagFile, boolean directivesOnly, URL jarFileUrl) {
        this.parserController = pc;
        this.ctxt = pc.getJspCompilationContext();
        this.pageInfo = pc.getCompiler().getPageInfo();
        this.err = pc.getCompiler().getErrorDispatcher();
        this.reader = reader;
        this.currentFile = reader.mark().getFile();
        this.scriptlessCount = 0;
        this.isTagFile = isTagFile;
        this.directivesOnly = directivesOnly;
        this.jarFileUrl = jarFileUrl;
        this.start = reader.mark();
    }

    public static Node.Nodes parse(ParserController pc, JspReader reader, Node parent, boolean isTagFile, boolean directivesOnly, URL jarFileUrl, String pageEnc, String jspConfigPageEnc, boolean isDefaultPageEncoding, boolean isBomPresent) throws JasperException {
        Parser parser = new Parser(pc, reader, isTagFile, directivesOnly, jarFileUrl);
        Node.Root root = new Node.Root(reader.mark(), parent, false);
        root.setPageEncoding(pageEnc);
        root.setJspConfigPageEncoding(jspConfigPageEnc);
        root.setIsDefaultPageEncoding(isDefaultPageEncoding);
        root.setIsBomPresent(isBomPresent);
        if (directivesOnly) {
            parser.parseTagFileDirectives(root);
            return new Node.Nodes(root);
        }
        PageInfo pageInfo = pc.getCompiler().getPageInfo();
        if (parent == null) {
            parser.addInclude(root, pageInfo.getIncludePrelude());
        }
        while (reader.hasMoreInput()) {
            parser.parseElements(root);
        }
        if (parent == null) {
            parser.addInclude(root, pageInfo.getIncludeCoda());
        }
        Node.Nodes page = new Node.Nodes(root);
        return page;
    }

    Attributes parseAttributes() throws JasperException {
        AttributesImpl attrs = new AttributesImpl();
        this.reader.skipSpaces();
        while (this.parseAttribute(attrs)) {
            this.reader.skipSpaces();
        }
        return attrs;
    }

    public static Attributes parseAttributes(ParserController pc, JspReader reader) throws JasperException {
        Parser tmpParser = new Parser(pc, reader, false, false, null);
        return tmpParser.parseAttributes();
    }

    private boolean parseAttribute(AttributesImpl attrs) throws JasperException {
        String qName = this.parseName();
        if (qName == null) {
            return false;
        }
        String localName = qName;
        String uri = "";
        int index = qName.indexOf(58);
        if (index != -1) {
            String prefix = qName.substring(0, index);
            uri = this.pageInfo.getURI(prefix);
            if (uri == null) {
                this.err.jspError(this.reader.mark(), "jsp.error.attribute.invalidPrefix", prefix);
            }
            localName = qName.substring(index + 1);
        }
        this.reader.skipSpaces();
        if (!this.reader.matches("=")) {
            this.err.jspError(this.reader.mark(), "jsp.error.attribute.noequal");
        }
        this.reader.skipSpaces();
        char quote = (char)this.reader.nextChar();
        if (quote != '\'' && quote != '\"') {
            this.err.jspError(this.reader.mark(), "jsp.error.attribute.noquote");
        }
        String watchString = "";
        if (this.reader.matches("<%=")) {
            watchString = "%>";
        }
        watchString = watchString + quote;
        String attrValue = this.parseAttributeValue(watchString);
        attrs.addAttribute(uri, localName, qName, "CDATA", attrValue);
        return true;
    }

    private String parseName() throws JasperException {
        char ch = (char)this.reader.peekChar();
        if (Character.isLetter(ch) || ch == '_' || ch == ':') {
            StringBuffer buf = new StringBuffer();
            buf.append(ch);
            this.reader.nextChar();
            ch = (char)this.reader.peekChar();
            while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '.' || ch == '_' || ch == '-' || ch == ':') {
                buf.append(ch);
                this.reader.nextChar();
                ch = (char)this.reader.peekChar();
            }
            return buf.toString();
        }
        return null;
    }

    private String parseAttributeValue(String watch) throws JasperException {
        Mark start = this.reader.mark();
        Mark stop = this.reader.skipUntilIgnoreEsc(watch);
        if (stop == null) {
            this.err.jspError(start, "jsp.error.attribute.unterminated", watch);
        }
        String ret = this.parseQuoted(this.reader.getText(start, stop));
        if (watch.length() == 1) {
            return ret;
        }
        return "<%=" + ret + "%>";
    }

    private String parseQuoted(String tx) {
        StringBuffer buf = new StringBuffer();
        int size = tx.length();
        int i = 0;
        while (i < size) {
            char ch = tx.charAt(i);
            if (ch == '&') {
                if (i + 5 < size && tx.charAt(i + 1) == 'a' && tx.charAt(i + 2) == 'p' && tx.charAt(i + 3) == 'o' && tx.charAt(i + 4) == 's' && tx.charAt(i + 5) == ';') {
                    buf.append('\'');
                    i += 6;
                    continue;
                }
                if (i + 5 < size && tx.charAt(i + 1) == 'q' && tx.charAt(i + 2) == 'u' && tx.charAt(i + 3) == 'o' && tx.charAt(i + 4) == 't' && tx.charAt(i + 5) == ';') {
                    buf.append('\"');
                    i += 6;
                    continue;
                }
                buf.append(ch);
                ++i;
                continue;
            }
            if (ch == '\\' && i + 1 < size) {
                ch = tx.charAt(i + 1);
                if (ch == '\\' || ch == '\"' || ch == '\'' || ch == '>') {
                    buf.append(ch);
                    i += 2;
                    continue;
                }
                if (ch == '$') {
                    buf.append('\u001b');
                    i += 2;
                    continue;
                }
                buf.append('\\');
                ++i;
                continue;
            }
            buf.append(ch);
            ++i;
        }
        return buf.toString();
    }

    private String parseScriptText(String tx) {
        CharArrayWriter cw = new CharArrayWriter();
        int size = tx.length();
        int i = 0;
        while (i < size) {
            char ch = tx.charAt(i);
            if (i + 2 < size && ch == '%' && tx.charAt(i + 1) == '\\' && tx.charAt(i + 2) == '>') {
                cw.write(37);
                cw.write(62);
                i += 3;
                continue;
            }
            cw.write(ch);
            ++i;
        }
        cw.close();
        return cw.toString();
    }

    private void processIncludeDirective(String file, Node parent) throws JasperException {
        if (file == null) {
            return;
        }
        try {
            this.parserController.parse(file, parent, this.jarFileUrl);
        }
        catch (FileNotFoundException ex) {
            this.err.jspError(this.start, "jsp.error.file.not.found", file);
        }
        catch (Exception ex) {
            this.err.jspError(this.start, ex.getMessage());
        }
    }

    private void parsePageDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.PageDirective n = new Node.PageDirective(attrs, this.start, parent);
        for (int i = 0; i < attrs.getLength(); ++i) {
            if (!"import".equals(attrs.getQName(i))) continue;
            n.addImport(attrs.getValue(i));
        }
    }

    private void parseIncludeDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.IncludeDirective includeNode = new Node.IncludeDirective(attrs, this.start, parent);
        this.processIncludeDirective(attrs.getValue("file"), includeNode);
    }

    private void addInclude(Node parent, List files) throws JasperException {
        if (files != null) {
            for (String file : files) {
                AttributesImpl attrs = new AttributesImpl();
                attrs.addAttribute("", "file", "file", "CDATA", file);
                Node.IncludeDirective includeNode = new Node.IncludeDirective(attrs, this.reader.mark(), parent);
                this.processIncludeDirective(file, includeNode);
            }
        }
    }

    private void parseTaglibDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        String uri = attrs.getValue("uri");
        String prefix = attrs.getValue("prefix");
        if (prefix != null) {
            Mark prevMark = this.pageInfo.getNonCustomTagPrefix(prefix);
            if (prevMark != null) {
                this.err.jspError(this.reader.mark(), "jsp.error.prefix.use_before_dcl", prefix, prevMark.getFile(), "" + prevMark.getLineNumber());
            }
            if (uri != null) {
                String uriPrev = this.pageInfo.getURI(prefix);
                if (uriPrev != null && !uriPrev.equals(uri)) {
                    this.err.jspError(this.reader.mark(), "jsp.error.prefix.refined", prefix, uri, uriPrev);
                }
                if (this.pageInfo.getTaglib(uri) == null) {
                    TagLibraryInfoImpl impl = null;
                    String[] location = this.ctxt.getTldLocation(uri);
                    impl = new TagLibraryInfoImpl(this.ctxt, this.parserController, this.pageInfo, prefix, uri, location, this.err);
                    this.pageInfo.addTaglib(uri, impl);
                }
                this.pageInfo.addPrefixMapping(prefix, uri);
            } else {
                String tagdir = attrs.getValue("tagdir");
                if (tagdir != null) {
                    String urnTagdir = "urn:jsptagdir:" + tagdir;
                    if (this.pageInfo.getTaglib(urnTagdir) == null) {
                        this.pageInfo.addTaglib(urnTagdir, new ImplicitTagLibraryInfo(this.ctxt, this.parserController, this.pageInfo, prefix, tagdir, this.err));
                    }
                    this.pageInfo.addPrefixMapping(prefix, urnTagdir);
                }
            }
        }
        new Node.TaglibDirective(attrs, this.start, parent);
    }

    private void parseDirective(Node parent) throws JasperException {
        this.reader.skipSpaces();
        String directive = null;
        if (this.reader.matches("page")) {
            directive = "&lt;%@ page";
            if (this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.istagfile", directive);
            }
            this.parsePageDirective(parent);
        } else if (this.reader.matches("include")) {
            directive = "&lt;%@ include";
            this.parseIncludeDirective(parent);
        } else if (this.reader.matches("taglib")) {
            if (this.directivesOnly) {
                return;
            }
            directive = "&lt;%@ taglib";
            this.parseTaglibDirective(parent);
        } else if (this.reader.matches("tag")) {
            directive = "&lt;%@ tag";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", directive);
            }
            this.parseTagDirective(parent);
        } else if (this.reader.matches("attribute")) {
            directive = "&lt;%@ attribute";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", directive);
            }
            this.parseAttributeDirective(parent);
        } else if (this.reader.matches("variable")) {
            directive = "&lt;%@ variable";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", directive);
            }
            this.parseVariableDirective(parent);
        } else {
            this.err.jspError(this.reader.mark(), "jsp.error.invalid.directive");
        }
        this.reader.skipSpaces();
        if (!this.reader.matches("%>")) {
            this.err.jspError(this.start, "jsp.error.unterminated", directive);
        }
    }

    private void parseXMLDirective(Node parent) throws JasperException {
        this.reader.skipSpaces();
        String eTag = null;
        if (this.reader.matches("page")) {
            eTag = "jsp:directive.page";
            if (this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.istagfile", "&lt;" + eTag);
            }
            this.parsePageDirective(parent);
        } else if (this.reader.matches("include")) {
            eTag = "jsp:directive.include";
            this.parseIncludeDirective(parent);
        } else if (this.reader.matches("tag")) {
            eTag = "jsp:directive.tag";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", "&lt;" + eTag);
            }
            this.parseTagDirective(parent);
        } else if (this.reader.matches("attribute")) {
            eTag = "jsp:directive.attribute";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", "&lt;" + eTag);
            }
            this.parseAttributeDirective(parent);
        } else if (this.reader.matches("variable")) {
            eTag = "jsp:directive.variable";
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.directive.isnottagfile", "&lt;" + eTag);
            }
            this.parseVariableDirective(parent);
        } else {
            this.err.jspError(this.reader.mark(), "jsp.error.invalid.directive");
        }
        this.reader.skipSpaces();
        if (this.reader.matches(">")) {
            this.reader.skipSpaces();
            if (!this.reader.matchesETag(eTag)) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;" + eTag);
            }
        } else if (!this.reader.matches("/>")) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;" + eTag);
        }
    }

    private void parseTagDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.TagDirective n = new Node.TagDirective(attrs, this.start, parent);
        for (int i = 0; i < attrs.getLength(); ++i) {
            if (!"import".equals(attrs.getQName(i))) continue;
            n.addImport(attrs.getValue(i));
        }
    }

    private void parseAttributeDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.AttributeDirective n = new Node.AttributeDirective(attrs, this.start, parent);
    }

    private void parseVariableDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.VariableDirective n = new Node.VariableDirective(attrs, this.start, parent);
    }

    private void parseComment(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("--%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%--");
        }
        new Node.Comment(this.reader.getText(this.start, stop), this.start, parent);
    }

    private void parseDeclaration(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%!");
        }
        new Node.Declaration(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
    }

    private void parseXMLDeclaration(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:declaration&gt;");
            }
            while (true) {
                this.start = this.reader.mark();
                Mark stop = this.reader.skipUntil("<");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:declaration&gt;");
                }
                String text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Declaration(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Declaration(text, this.start, parent);
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:declaration")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:declaration&gt;");
            }
        }
    }

    private void parseExpression(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%=");
        }
        new Node.Expression(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
    }

    private void parseXMLExpression(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:expression&gt;");
            }
            while (true) {
                this.start = this.reader.mark();
                Mark stop = this.reader.skipUntil("<");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:expression&gt;");
                }
                String text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Expression(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Expression(text, this.start, parent);
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:expression")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:expression&gt;");
            }
        }
    }

    private void parseELExpression(Node parent, char type) throws JasperException {
        int currentChar;
        this.start = this.reader.mark();
        Mark last = null;
        boolean singleQuoted = false;
        boolean doubleQuoted = false;
        do {
            last = this.reader.mark();
            currentChar = this.reader.nextChar();
            if (currentChar == 92 && (singleQuoted || doubleQuoted)) {
                this.reader.nextChar();
                currentChar = this.reader.nextChar();
            }
            if (currentChar == -1) {
                this.err.jspError(this.start, "jsp.error.unterminated", type + "{");
            }
            if (currentChar == 34) {
                boolean bl = doubleQuoted = !doubleQuoted;
            }
            if (currentChar != 39) continue;
            boolean bl = singleQuoted = !singleQuoted;
        } while (currentChar != 125 || singleQuoted || doubleQuoted);
        new Node.ELExpression(type, this.reader.getText(this.start, last), this.start, parent);
    }

    private void parseScriptlet(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%");
        }
        new Node.Scriptlet(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
    }

    private void parseXMLScriptlet(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:scriptlet&gt;");
            }
            while (true) {
                this.start = this.reader.mark();
                Mark stop = this.reader.skipUntil("<");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:scriptlet&gt;");
                }
                String text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Scriptlet(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                new Node.Scriptlet(text, this.start, parent);
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:scriptlet")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:scriptlet&gt;");
            }
        }
    }

    private void parseParam(Node parent) throws JasperException {
        if (!this.reader.matches("<jsp:param")) {
            this.err.jspError(this.reader.mark(), "jsp.error.paramexpected");
        }
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.ParamAction paramActionNode = new Node.ParamAction(attrs, this.start, parent);
        this.parseEmptyBody(paramActionNode, "jsp:param");
        this.reader.skipSpaces();
    }

    private void parseInclude(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.IncludeAction includeNode = new Node.IncludeAction(attrs, this.start, parent);
        this.parseOptionalBody(includeNode, "jsp:include", JAVAX_BODY_CONTENT_PARAM);
    }

    private void parseForward(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.ForwardAction forwardNode = new Node.ForwardAction(attrs, this.start, parent);
        this.parseOptionalBody(forwardNode, "jsp:forward", JAVAX_BODY_CONTENT_PARAM);
    }

    private void parseInvoke(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.InvokeAction invokeNode = new Node.InvokeAction(attrs, this.start, parent);
        this.parseEmptyBody(invokeNode, "jsp:invoke");
    }

    private void parseDoBody(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.DoBodyAction doBodyNode = new Node.DoBodyAction(attrs, this.start, parent);
        this.parseEmptyBody(doBodyNode, "jsp:doBody");
    }

    private void parseElement(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.JspElement elementNode = new Node.JspElement(attrs, this.start, parent);
        this.parseOptionalBody(elementNode, "jsp:element", "JSP");
    }

    private void parseGetProperty(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.GetProperty getPropertyNode = new Node.GetProperty(attrs, this.start, parent);
        this.parseOptionalBody(getPropertyNode, "jsp:getProperty", "empty");
    }

    private void parseSetProperty(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.SetProperty setPropertyNode = new Node.SetProperty(attrs, this.start, parent);
        this.parseOptionalBody(setPropertyNode, "jsp:setProperty", "empty");
    }

    private void parseEmptyBody(Node parent, String tag) throws JasperException {
        if (!this.reader.matches("/>")) {
            if (this.reader.matches(">")) {
                if (!this.reader.matchesETag(tag)) {
                    if (this.reader.matchesOptionalSpacesFollowedBy("<jsp:attribute")) {
                        this.parseNamedAttributes(parent);
                        if (!this.reader.matchesETag(tag)) {
                            this.err.jspError(this.reader.mark(), "jsp.error.jspbody.emptybody.only", "&lt;" + tag);
                        }
                    } else {
                        this.err.jspError(this.reader.mark(), "jsp.error.jspbody.emptybody.only", "&lt;" + tag);
                    }
                }
            } else {
                this.err.jspError(this.reader.mark(), "jsp.error.unterminated", "&lt;" + tag);
            }
        }
    }

    private void parseUseBean(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.UseBean useBeanNode = new Node.UseBean(attrs, this.start, parent);
        this.parseOptionalBody(useBeanNode, "jsp:useBean", "JSP");
    }

    private void parseOptionalBody(Node parent, String tag, String bodyType) throws JasperException {
        if (this.reader.matches("/>")) {
            return;
        }
        if (!this.reader.matches(">")) {
            this.err.jspError(this.reader.mark(), "jsp.error.unterminated", "&lt;" + tag);
        }
        if (this.reader.matchesETag(tag)) {
            return;
        }
        if (!this.parseJspAttributeAndBody(parent, tag, bodyType)) {
            this.parseBody(parent, tag, bodyType);
        }
    }

    private boolean parseJspAttributeAndBody(Node parent, String tag, String bodyType) throws JasperException {
        boolean result = false;
        if (this.reader.matchesOptionalSpacesFollowedBy("<jsp:attribute")) {
            this.parseNamedAttributes(parent);
            result = true;
        }
        if (this.reader.matchesOptionalSpacesFollowedBy("<jsp:body")) {
            this.parseJspBody(parent, bodyType);
            this.reader.skipSpaces();
            if (!this.reader.matchesETag(tag)) {
                this.err.jspError(this.reader.mark(), "jsp.error.unterminated", "&lt;" + tag);
            }
            result = true;
        } else if (result && !this.reader.matchesETag(tag)) {
            this.err.jspError(this.reader.mark(), "jsp.error.jspbody.required", "&lt;" + tag);
        }
        return result;
    }

    private void parseJspParams(Node parent) throws JasperException {
        Node.ParamsAction jspParamsNode = new Node.ParamsAction(this.start, parent);
        this.parseOptionalBody(jspParamsNode, "jsp:params", JAVAX_BODY_CONTENT_PARAM);
    }

    private void parseFallBack(Node parent) throws JasperException {
        Node.FallBackAction fallBackNode = new Node.FallBackAction(this.start, parent);
        this.parseOptionalBody(fallBackNode, "jsp:fallback", JAVAX_BODY_CONTENT_TEMPLATE_TEXT);
    }

    private void parsePlugin(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        Node.PlugIn pluginNode = new Node.PlugIn(attrs, this.start, parent);
        this.parseOptionalBody(pluginNode, "jsp:plugin", JAVAX_BODY_CONTENT_PLUGIN);
    }

    private void parsePluginTags(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (this.reader.matches("<jsp:params")) {
            this.parseJspParams(parent);
            this.reader.skipSpaces();
        }
        if (this.reader.matches("<jsp:fallback")) {
            this.parseFallBack(parent);
            this.reader.skipSpaces();
        }
    }

    private void parseStandardAction(Node parent) throws JasperException {
        Mark start = this.reader.mark();
        if (this.reader.matches("include")) {
            this.parseInclude(parent);
        } else if (this.reader.matches("forward")) {
            this.parseForward(parent);
        } else if (this.reader.matches("invoke")) {
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.action.isnottagfile", "&lt;jsp:invoke");
            }
            this.parseInvoke(parent);
        } else if (this.reader.matches("doBody")) {
            if (!this.isTagFile) {
                this.err.jspError(this.reader.mark(), "jsp.error.action.isnottagfile", "&lt;jsp:doBody");
            }
            this.parseDoBody(parent);
        } else if (this.reader.matches("getProperty")) {
            this.parseGetProperty(parent);
        } else if (this.reader.matches("setProperty")) {
            this.parseSetProperty(parent);
        } else if (this.reader.matches("useBean")) {
            this.parseUseBean(parent);
        } else if (this.reader.matches("plugin")) {
            this.parsePlugin(parent);
        } else if (this.reader.matches("element")) {
            this.parseElement(parent);
        } else if (this.reader.matches("attribute")) {
            this.err.jspError(start, "jsp.error.namedAttribute.invalidUse");
        } else if (this.reader.matches("body")) {
            this.err.jspError(start, "jsp.error.jspbody.invalidUse");
        } else if (this.reader.matches("fallback")) {
            this.err.jspError(start, "jsp.error.fallback.invalidUse");
        } else if (this.reader.matches("params")) {
            this.err.jspError(start, "jsp.error.params.invalidUse");
        } else if (this.reader.matches("param")) {
            this.err.jspError(start, "jsp.error.param.invalidUse");
        } else if (this.reader.matches("output")) {
            this.err.jspError(start, "jsp.error.jspoutput.invalidUse");
        } else {
            this.err.jspError(start, "jsp.error.badStandardAction");
        }
    }

    private boolean parseCustomTag(Node parent) throws JasperException {
        if (this.reader.peekChar() != 60) {
            return false;
        }
        this.reader.nextChar();
        String tagName = this.reader.parseToken(false);
        int i = tagName.indexOf(58);
        if (i == -1) {
            this.reader.reset(this.start);
            return false;
        }
        String prefix = tagName.substring(0, i);
        String shortTagName = tagName.substring(i + 1);
        String uri = this.pageInfo.getURI(prefix);
        if (uri == null) {
            this.reader.reset(this.start);
            this.pageInfo.putNonCustomTagPrefix(prefix, this.reader.mark());
            return false;
        }
        TagLibraryInfo tagLibInfo = this.pageInfo.getTaglib(uri);
        TagInfo tagInfo = tagLibInfo.getTag(shortTagName);
        TagFileInfo tagFileInfo = tagLibInfo.getTagFile(shortTagName);
        if (tagInfo == null && tagFileInfo == null) {
            this.err.jspError(this.start, "jsp.error.bad_tag", shortTagName, prefix);
        }
        Class<?> tagHandlerClass = null;
        if (tagInfo != null) {
            String handlerClassName = tagInfo.getTagClassName();
            try {
                tagHandlerClass = this.ctxt.getClassLoader().loadClass(handlerClassName);
            }
            catch (Exception e) {
                this.err.jspError(this.start, "jsp.error.loadclass.taghandler", handlerClassName, tagName);
            }
        }
        Attributes attrs = this.parseAttributes();
        this.reader.skipSpaces();
        if (this.reader.matches("/>")) {
            if (tagInfo != null) {
                new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagInfo, tagHandlerClass);
            } else {
                new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagFileInfo);
            }
            return true;
        }
        String bc = tagInfo != null ? tagInfo.getBodyContent() : tagFileInfo.getTagInfo().getBodyContent();
        Node.CustomTag tagNode = null;
        tagNode = tagInfo != null ? new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagInfo, tagHandlerClass) : new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagFileInfo);
        this.parseOptionalBody(tagNode, tagName, bc);
        return true;
    }

    private void parseTemplateText(Node parent) throws JasperException {
        if (!this.reader.hasMoreInput()) {
            return;
        }
        CharArrayWriter ttext = new CharArrayWriter();
        int ch = this.reader.nextChar();
        if (ch == 92) {
            this.reader.pushChar();
        } else {
            ttext.write(ch);
        }
        while (this.reader.hasMoreInput()) {
            ch = this.reader.nextChar();
            if (ch == 60) {
                this.reader.pushChar();
                break;
            }
            if (ch == 36 || ch == 35) {
                if (!this.reader.hasMoreInput()) {
                    ttext.write(ch);
                    break;
                }
                if (this.reader.nextChar() == 123) {
                    this.reader.pushChar();
                    this.reader.pushChar();
                    break;
                }
                ttext.write(ch);
                this.reader.pushChar();
                continue;
            }
            if (ch == 92) {
                if (!this.reader.hasMoreInput()) {
                    ttext.write(92);
                    break;
                }
                char next = (char)this.reader.peekChar();
                if (next == '%' || next == '$' || next == '#') {
                    ch = this.reader.nextChar();
                }
            }
            ttext.write(ch);
        }
        new Node.TemplateText(ttext.toString(), this.start, parent);
    }

    private void parseXMLTemplateText(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:text&gt;");
            }
            CharArrayWriter ttext = new CharArrayWriter();
            while (this.reader.hasMoreInput()) {
                int ch = this.reader.nextChar();
                if (ch == 60) {
                    if (!this.reader.matches("![CDATA[")) break;
                    this.start = this.reader.mark();
                    Mark stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                    }
                    String text = this.reader.getText(this.start, stop);
                    ttext.write(text, 0, text.length());
                    continue;
                }
                if (ch == 92) {
                    if (!this.reader.hasMoreInput()) {
                        ttext.write(92);
                        break;
                    }
                    ch = this.reader.nextChar();
                    if (ch != 36 && ch != 35) {
                        ttext.write(92);
                    }
                    ttext.write(ch);
                    continue;
                }
                if (ch == 36 || ch == 35) {
                    if (!this.reader.hasMoreInput()) {
                        ttext.write(ch);
                        break;
                    }
                    if (this.reader.nextChar() != 123) {
                        ttext.write(ch);
                        this.reader.pushChar();
                        continue;
                    }
                    new Node.TemplateText(ttext.toString(), this.start, parent);
                    this.start = this.reader.mark();
                    this.parseELExpression(parent, (char)ch);
                    this.start = this.reader.mark();
                    ttext = new CharArrayWriter();
                    continue;
                }
                ttext.write(ch);
            }
            new Node.TemplateText(ttext.toString(), this.start, parent);
            if (!this.reader.hasMoreInput()) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:text&gt;");
            } else if (!this.reader.matchesETagWithoutLessThan("jsp:text")) {
                this.err.jspError(this.start, "jsp.error.jsptext.badcontent");
            }
        }
    }

    private void parseElements(Node parent) throws JasperException {
        if (this.scriptlessCount > 0) {
            this.parseElementsScriptless(parent);
            return;
        }
        this.start = this.reader.mark();
        if (this.reader.matches("<%--")) {
            this.parseComment(parent);
        } else if (this.reader.matches("<%@")) {
            this.parseDirective(parent);
        } else if (this.reader.matches("<jsp:directive.")) {
            this.parseXMLDirective(parent);
        } else if (this.reader.matches("<%!")) {
            this.parseDeclaration(parent);
        } else if (this.reader.matches("<jsp:declaration")) {
            this.parseXMLDeclaration(parent);
        } else if (this.reader.matches("<%=")) {
            this.parseExpression(parent);
        } else if (this.reader.matches("<jsp:expression")) {
            this.parseXMLExpression(parent);
        } else if (this.reader.matches("<%")) {
            this.parseScriptlet(parent);
        } else if (this.reader.matches("<jsp:scriptlet")) {
            this.parseXMLScriptlet(parent);
        } else if (this.reader.matches("<jsp:text")) {
            this.parseXMLTemplateText(parent);
        } else if (this.reader.matches("${")) {
            this.parseELExpression(parent, '$');
        } else if (this.reader.matches("#{")) {
            this.parseELExpression(parent, '#');
        } else if (this.reader.matches("<jsp:")) {
            this.parseStandardAction(parent);
        } else if (!this.parseCustomTag(parent)) {
            this.checkUnbalancedEndTag();
            this.parseTemplateText(parent);
        }
    }

    private void parseElementsScriptless(Node parent) throws JasperException {
        ++this.scriptlessCount;
        this.start = this.reader.mark();
        if (this.reader.matches("<%--")) {
            this.parseComment(parent);
        } else if (this.reader.matches("<%@")) {
            this.parseDirective(parent);
        } else if (this.reader.matches("<jsp:directive.")) {
            this.parseXMLDirective(parent);
        } else if (this.reader.matches("<%!")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<jsp:declaration")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<%=")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<jsp:expression")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<%")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<jsp:scriptlet")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets");
        } else if (this.reader.matches("<jsp:text")) {
            this.parseXMLTemplateText(parent);
        } else if (this.reader.matches("${")) {
            this.parseELExpression(parent, '$');
        } else if (this.reader.matches("#{")) {
            this.parseELExpression(parent, '#');
        } else if (this.reader.matches("<jsp:")) {
            this.parseStandardAction(parent);
        } else if (!this.parseCustomTag(parent)) {
            this.checkUnbalancedEndTag();
            this.parseTemplateText(parent);
        }
        --this.scriptlessCount;
    }

    private void parseElementsTemplateText(Node parent) throws JasperException {
        this.start = this.reader.mark();
        if (this.reader.matches("<%--")) {
            this.parseComment(parent);
        } else if (this.reader.matches("<%@")) {
            this.parseDirective(parent);
        } else if (this.reader.matches("<jsp:directive.")) {
            this.parseXMLDirective(parent);
        } else if (this.reader.matches("<%!")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Declarations");
        } else if (this.reader.matches("<jsp:declaration")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Declarations");
        } else if (this.reader.matches("<%=")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Expressions");
        } else if (this.reader.matches("<jsp:expression")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Expressions");
        } else if (this.reader.matches("<%")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Scriptlets");
        } else if (this.reader.matches("<jsp:scriptlet")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Scriptlets");
        } else if (this.reader.matches("<jsp:text")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "&lt;jsp:text");
        } else if (this.reader.matches("${")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Expression language");
        } else if (this.reader.matches("#{")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Expression language");
        } else if (this.reader.matches("<jsp:")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Standard actions");
        } else if (this.parseCustomTag(parent)) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Custom actions");
        } else {
            this.checkUnbalancedEndTag();
            this.parseTemplateText(parent);
        }
    }

    private void checkUnbalancedEndTag() throws JasperException {
        String tagName;
        int i;
        if (!this.reader.matches("</")) {
            return;
        }
        if (this.reader.matches("jsp:")) {
            this.err.jspError(this.start, "jsp.error.unbalanced.endtag", "jsp:");
        }
        if ((i = (tagName = this.reader.parseToken(false)).indexOf(58)) == -1 || this.pageInfo.getURI(tagName.substring(0, i)) == null) {
            this.reader.reset(this.start);
            return;
        }
        this.err.jspError(this.start, "jsp.error.unbalanced.endtag", tagName);
    }

    private void parseTagDependentBody(Node parent, String tag) throws JasperException {
        Mark bodyStart = this.reader.mark();
        Mark bodyEnd = this.reader.skipUntilETag(tag);
        if (bodyEnd == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;" + tag);
        }
        new Node.TemplateText(this.reader.getText(bodyStart, bodyEnd), bodyStart, parent);
    }

    private void parseJspBody(Node parent, String bodyType) throws JasperException {
        Mark start = this.reader.mark();
        Node.JspBody bodyNode = new Node.JspBody(start, parent);
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                this.err.jspError(start, "jsp.error.unterminated", "&lt;jsp:body");
            }
            this.parseBody(bodyNode, "jsp:body", bodyType);
        }
    }

    private void parseBody(Node parent, String tag, String bodyType) throws JasperException {
        if (bodyType.equalsIgnoreCase("tagdependent")) {
            this.parseTagDependentBody(parent, tag);
        } else if (bodyType.equalsIgnoreCase("empty")) {
            if (!this.reader.matchesETag(tag)) {
                this.err.jspError(this.start, "jasper.error.emptybodycontent.nonempty", tag);
            }
        } else if (bodyType == JAVAX_BODY_CONTENT_PLUGIN) {
            this.parsePluginTags(parent);
            if (!this.reader.matchesETag(tag)) {
                this.err.jspError(this.reader.mark(), "jsp.error.unterminated", "&lt;" + tag);
            }
        } else if (bodyType.equalsIgnoreCase("JSP") || bodyType.equalsIgnoreCase("scriptless") || bodyType == JAVAX_BODY_CONTENT_PARAM || bodyType == JAVAX_BODY_CONTENT_TEMPLATE_TEXT) {
            while (this.reader.hasMoreInput()) {
                if (this.reader.matchesETag(tag)) {
                    return;
                }
                if (tag.equals("jsp:body") || tag.equals("jsp:attribute")) {
                    if (this.reader.matches("<jsp:attribute")) {
                        this.err.jspError(this.reader.mark(), "jsp.error.nested.jspattribute");
                    } else if (this.reader.matches("<jsp:body")) {
                        this.err.jspError(this.reader.mark(), "jsp.error.nested.jspbody");
                    }
                }
                if (bodyType.equalsIgnoreCase("JSP")) {
                    this.parseElements(parent);
                    continue;
                }
                if (bodyType.equalsIgnoreCase("scriptless")) {
                    this.parseElementsScriptless(parent);
                    continue;
                }
                if (bodyType == JAVAX_BODY_CONTENT_PARAM) {
                    this.reader.skipSpaces();
                    this.parseParam(parent);
                    continue;
                }
                if (bodyType != JAVAX_BODY_CONTENT_TEMPLATE_TEXT) continue;
                this.parseElementsTemplateText(parent);
            }
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;" + tag);
        } else {
            this.err.jspError(this.start, "jasper.error.bad.bodycontent.type");
        }
    }

    private void parseNamedAttributes(Node parent) throws JasperException {
        do {
            Mark start = this.reader.mark();
            Attributes attrs = this.parseAttributes();
            Node.NamedAttribute namedAttributeNode = new Node.NamedAttribute(attrs, start, parent);
            this.reader.skipSpaces();
            if (!this.reader.matches("/>")) {
                Node lastNode;
                Node.Nodes subElems;
                if (!this.reader.matches(">")) {
                    this.err.jspError(start, "jsp.error.unterminated", "&lt;jsp:attribute");
                }
                if (namedAttributeNode.isTrim()) {
                    this.reader.skipSpaces();
                }
                this.parseBody(namedAttributeNode, "jsp:attribute", this.getAttributeBodyType(parent, attrs.getValue("name")));
                if (namedAttributeNode.isTrim() && (subElems = namedAttributeNode.getBody()) != null && (lastNode = subElems.getNode(subElems.size() - 1)) instanceof Node.TemplateText) {
                    ((Node.TemplateText)lastNode).rtrim();
                }
            }
            this.reader.skipSpaces();
        } while (this.reader.matches("<jsp:attribute"));
    }

    private String getAttributeBodyType(Node n, String name) {
        if (n instanceof Node.CustomTag) {
            TagInfo tagInfo = ((Node.CustomTag)n).getTagInfo();
            TagAttributeInfo[] tldAttrs = tagInfo.getAttributes();
            for (int i = 0; i < tldAttrs.length; ++i) {
                if (!name.equals(tldAttrs[i].getName())) continue;
                if (tldAttrs[i].isFragment()) {
                    return "scriptless";
                }
                if (!tldAttrs[i].canBeRequestTime()) continue;
                return "JSP";
            }
            if (tagInfo.hasDynamicAttributes()) {
                return "JSP";
            }
        } else if (n instanceof Node.IncludeAction ? "page".equals(name) : (n instanceof Node.ForwardAction ? "page".equals(name) : (n instanceof Node.SetProperty ? "value".equals(name) : (n instanceof Node.UseBean ? "beanName".equals(name) : (n instanceof Node.PlugIn ? "width".equals(name) || "height".equals(name) : (n instanceof Node.ParamAction ? "value".equals(name) : n instanceof Node.JspElement)))))) {
            return "JSP";
        }
        return JAVAX_BODY_CONTENT_TEMPLATE_TEXT;
    }

    private void parseTagFileDirectives(Node parent) throws JasperException {
        this.reader.setSingleFile(true);
        this.reader.skipUntil("<");
        while (this.reader.hasMoreInput()) {
            this.start = this.reader.mark();
            if (this.reader.matches("%--")) {
                this.parseComment(parent);
            } else if (this.reader.matches("%@")) {
                this.parseDirective(parent);
            } else if (this.reader.matches("jsp:directive.")) {
                this.parseXMLDirective(parent);
            }
            this.reader.skipUntil("<");
        }
    }
}

