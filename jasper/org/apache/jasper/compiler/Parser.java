/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.util.Collection;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.AttributeParser;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.ImplicitTagLibraryInfo;
import org.apache.jasper.compiler.JspReader;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.compiler.TagConstants;
import org.apache.jasper.compiler.TagLibraryInfoImpl;
import org.apache.jasper.util.UniqueAttributesImpl;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

class Parser
implements TagConstants {
    private final ParserController parserController;
    private final JspCompilationContext ctxt;
    private final JspReader reader;
    private Mark start;
    private final ErrorDispatcher err;
    private int scriptlessCount;
    private final boolean isTagFile;
    private final boolean directivesOnly;
    private final Jar jar;
    private final PageInfo pageInfo;
    private static final String JAVAX_BODY_CONTENT_PARAM = "JAVAX_BODY_CONTENT_PARAM";
    private static final String JAVAX_BODY_CONTENT_PLUGIN = "JAVAX_BODY_CONTENT_PLUGIN";
    private static final String JAVAX_BODY_CONTENT_TEMPLATE_TEXT = "JAVAX_BODY_CONTENT_TEMPLATE_TEXT";
    private static final boolean STRICT_WHITESPACE = Boolean.parseBoolean(System.getProperty("org.apache.jasper.compiler.Parser.STRICT_WHITESPACE", "true"));

    private Parser(ParserController pc, JspReader reader, boolean isTagFile, boolean directivesOnly, Jar jar) {
        this.parserController = pc;
        this.ctxt = pc.getJspCompilationContext();
        this.pageInfo = pc.getCompiler().getPageInfo();
        this.err = pc.getCompiler().getErrorDispatcher();
        this.reader = reader;
        this.scriptlessCount = 0;
        this.isTagFile = isTagFile;
        this.directivesOnly = directivesOnly;
        this.jar = jar;
        this.start = reader.mark();
    }

    public static Node.Nodes parse(ParserController pc, JspReader reader, Node parent, boolean isTagFile, boolean directivesOnly, Jar jar, String pageEnc, String jspConfigPageEnc, boolean isDefaultPageEncoding, boolean isBomPresent) throws JasperException {
        Parser parser = new Parser(pc, reader, isTagFile, directivesOnly, jar);
        Node.Root root = new Node.Root(reader.mark(), parent, false);
        root.setPageEncoding(pageEnc);
        root.setJspConfigPageEncoding(jspConfigPageEnc);
        root.setIsDefaultPageEncoding(isDefaultPageEncoding);
        root.setIsBomPresent(isBomPresent);
        PageInfo pageInfo = pc.getCompiler().getPageInfo();
        if (parent == null && !isTagFile) {
            parser.addInclude(root, pageInfo.getIncludePrelude());
        }
        if (directivesOnly) {
            parser.parseFileDirectives(root);
        } else {
            while (reader.hasMoreInput()) {
                parser.parseElements(root);
            }
        }
        if (parent == null && !isTagFile) {
            parser.addInclude(root, pageInfo.getIncludeCoda());
        }
        Node.Nodes page = new Node.Nodes(root);
        return page;
    }

    Attributes parseAttributes() throws JasperException {
        return this.parseAttributes(false);
    }

    Attributes parseAttributes(boolean pageDirective) throws JasperException {
        UniqueAttributesImpl attrs = new UniqueAttributesImpl(pageDirective);
        this.reader.skipSpaces();
        int ws = 1;
        try {
            while (this.parseAttribute(attrs)) {
                if (ws == 0 && STRICT_WHITESPACE) {
                    this.err.jspError(this.reader.mark(), "jsp.error.attribute.nowhitespace", new String[0]);
                }
                ws = this.reader.skipSpaces();
            }
        }
        catch (IllegalArgumentException iae) {
            this.err.jspError(this.reader.mark(), "jsp.error.attribute.duplicate", new String[0]);
        }
        return attrs;
    }

    public static Attributes parseAttributes(ParserController pc, JspReader reader) throws JasperException {
        Parser tmpParser = new Parser(pc, reader, false, false, null);
        return tmpParser.parseAttributes(true);
    }

    private boolean parseAttribute(AttributesImpl attrs) throws JasperException {
        String qName = this.parseName();
        if (qName == null) {
            return false;
        }
        boolean ignoreEL = this.pageInfo.isELIgnored();
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
            this.err.jspError(this.reader.mark(), "jsp.error.attribute.noequal", new String[0]);
        }
        this.reader.skipSpaces();
        char quote = (char)this.reader.nextChar();
        if (quote != '\'' && quote != '\"') {
            this.err.jspError(this.reader.mark(), "jsp.error.attribute.noquote", new String[0]);
        }
        String watchString = "";
        if (this.reader.matches("<%=")) {
            watchString = "%>";
            ignoreEL = true;
        }
        watchString = watchString + quote;
        String attrValue = this.parseAttributeValue(qName, watchString, ignoreEL);
        attrs.addAttribute(uri, localName, qName, "CDATA", attrValue);
        return true;
    }

    private String parseName() {
        char ch = (char)this.reader.peekChar();
        if (Character.isLetter(ch) || ch == '_' || ch == ':') {
            StringBuilder buf = new StringBuilder();
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

    private String parseAttributeValue(String qName, String watch, boolean ignoreEL) throws JasperException {
        boolean quoteAttributeEL = this.ctxt.getOptions().getQuoteAttributeEL();
        Mark start = this.reader.mark();
        Mark stop = this.reader.skipUntilIgnoreEsc(watch, ignoreEL || quoteAttributeEL);
        if (stop == null) {
            this.err.jspError(start, "jsp.error.attribute.unterminated", qName);
        }
        String ret = null;
        try {
            char quote = watch.charAt(watch.length() - 1);
            boolean isElIgnored = this.pageInfo.isELIgnored() || watch.length() > 1;
            ret = AttributeParser.getUnquoted(this.reader.getText(start, stop), quote, isElIgnored, this.pageInfo.isDeferredSyntaxAllowedAsLiteral(), this.ctxt.getOptions().getStrictQuoteEscaping(), quoteAttributeEL);
        }
        catch (IllegalArgumentException iae) {
            this.err.jspError(start, iae.getMessage(), new String[0]);
        }
        if (watch.length() == 1) {
            return ret;
        }
        return "<%=" + ret + "%>";
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
            this.parserController.parse(file, parent, this.jar);
        }
        catch (FileNotFoundException ex) {
            this.err.jspError(this.start, "jsp.error.file.not.found", file);
        }
        catch (Exception ex) {
            this.err.jspError(this.start, ex.getMessage(), new String[0]);
        }
    }

    private void parsePageDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes(true);
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

    private void addInclude(Node parent, Collection<String> files) throws JasperException {
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
                    if (this.ctxt.getOptions().isCaching()) {
                        impl = (TagLibraryInfoImpl)this.ctxt.getOptions().getCache().get(uri);
                    }
                    if (impl == null) {
                        TldResourcePath tldResourcePath = this.ctxt.getTldResourcePath(uri);
                        impl = new TagLibraryInfoImpl(this.ctxt, this.parserController, this.pageInfo, prefix, uri, tldResourcePath, this.err);
                        if (this.ctxt.getOptions().isCaching()) {
                            this.ctxt.getOptions().getCache().put(uri, impl);
                        }
                    }
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
        Node.TaglibDirective unused = new Node.TaglibDirective(attrs, this.start, parent);
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
            this.err.jspError(this.reader.mark(), "jsp.error.invalid.directive", new String[0]);
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
            this.err.jspError(this.reader.mark(), "jsp.error.invalid.directive", new String[0]);
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
        Attributes attrs = this.parseAttributes(true);
        Node.TagDirective n = new Node.TagDirective(attrs, this.start, parent);
        for (int i = 0; i < attrs.getLength(); ++i) {
            if (!"import".equals(attrs.getQName(i))) continue;
            n.addImport(attrs.getValue(i));
        }
    }

    private void parseAttributeDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.AttributeDirective unused = new Node.AttributeDirective(attrs, this.start, parent);
    }

    private void parseVariableDirective(Node parent) throws JasperException {
        Attributes attrs = this.parseAttributes();
        Node.VariableDirective unused = new Node.VariableDirective(attrs, this.start, parent);
    }

    private void parseComment(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("--%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%--");
        }
        Node.Comment unused = new Node.Comment(this.reader.getText(this.start, stop), this.start, parent);
    }

    private void parseDeclaration(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%!");
        }
        Node.Declaration unused = new Node.Declaration(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
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
                Node.Declaration unused = new Node.Declaration(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                Node.Declaration declaration = new Node.Declaration(text, this.start, parent);
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
        Node.Expression unused = new Node.Expression(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
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
                Node.Expression unused = new Node.Expression(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                Node.Expression expression = new Node.Expression(text, this.start, parent);
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:expression")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:expression&gt;");
            }
        }
    }

    private void parseELExpression(Node parent, char type) throws JasperException {
        this.start = this.reader.mark();
        Mark last = this.reader.skipELExpression();
        if (last == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", type + "{");
        }
        Node.ELExpression unused = new Node.ELExpression(type, this.reader.getText(this.start, last), this.start, parent);
    }

    private void parseScriptlet(Node parent) throws JasperException {
        this.start = this.reader.mark();
        Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            this.err.jspError(this.start, "jsp.error.unterminated", "&lt;%");
        }
        Node.Scriptlet unused = new Node.Scriptlet(this.parseScriptText(this.reader.getText(this.start, stop)), this.start, parent);
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
                Node.Scriptlet unused = new Node.Scriptlet(text, this.start, parent);
                if (!this.reader.matches("![CDATA[")) break;
                this.start = this.reader.mark();
                stop = this.reader.skipUntil("]]>");
                if (stop == null) {
                    this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                }
                text = this.parseScriptText(this.reader.getText(this.start, stop));
                Node.Scriptlet scriptlet = new Node.Scriptlet(text, this.start, parent);
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:scriptlet")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:scriptlet&gt;");
            }
        }
    }

    private void parseParam(Node parent) throws JasperException {
        if (!this.reader.matches("<jsp:param")) {
            this.err.jspError(this.reader.mark(), "jsp.error.paramexpected", new String[0]);
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
            this.err.jspError(start, "jsp.error.namedAttribute.invalidUse", new String[0]);
        } else if (this.reader.matches("body")) {
            this.err.jspError(start, "jsp.error.jspbody.invalidUse", new String[0]);
        } else if (this.reader.matches("fallback")) {
            this.err.jspError(start, "jsp.error.fallback.invalidUse", new String[0]);
        } else if (this.reader.matches("params")) {
            this.err.jspError(start, "jsp.error.params.invalidUse", new String[0]);
        } else if (this.reader.matches("param")) {
            this.err.jspError(start, "jsp.error.param.invalidUse", new String[0]);
        } else if (this.reader.matches("output")) {
            this.err.jspError(start, "jsp.error.jspoutput.invalidUse", new String[0]);
        } else {
            this.err.jspError(start, "jsp.error.badStandardAction", new String[0]);
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
            if (this.pageInfo.isErrorOnUndeclaredNamespace()) {
                this.err.jspError(this.start, "jsp.error.undeclared_namespace", prefix);
            } else {
                this.reader.reset(this.start);
                this.pageInfo.putNonCustomTagPrefix(prefix, this.reader.mark());
                return false;
            }
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
                Node.CustomTag e = new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagInfo, tagHandlerClass);
            } else {
                Node.CustomTag e = new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagFileInfo);
            }
            return true;
        }
        String bc = tagInfo != null ? tagInfo.getBodyContent() : tagFileInfo.getTagInfo().getBodyContent();
        Node.CustomTag tagNode = null;
        tagNode = tagInfo != null ? new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagInfo, tagHandlerClass) : new Node.CustomTag(tagName, prefix, shortTagName, uri, attrs, this.start, parent, tagFileInfo);
        this.parseOptionalBody(tagNode, tagName, bc);
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private void parseTemplateText(Node parent) {
        if (!this.reader.hasMoreInput()) {
            return;
        }
        CharArrayWriter ttext = new CharArrayWriter();
        int ch = this.reader.nextChar();
        while (ch != -1) {
            block13: {
                block14: {
                    if (ch != 60) break block14;
                    if (this.reader.peekChar(0) == 92 && this.reader.peekChar(1) == 37) {
                        ttext.write(ch);
                        this.reader.nextChar();
                        ttext.write(this.reader.nextChar());
                        break block13;
                    } else if (ttext.size() == 0) {
                        ttext.write(ch);
                        break block13;
                    } else {
                        this.reader.pushChar();
                        break;
                    }
                }
                if (ch == 92 && !this.pageInfo.isELIgnored()) {
                    int next = this.reader.peekChar(0);
                    if (next == 36 || next == 35) {
                        ttext.write(this.reader.nextChar());
                    } else {
                        ttext.write(ch);
                    }
                } else if ((ch == 36 || ch == 35 && !this.pageInfo.isDeferredSyntaxAllowedAsLiteral()) && !this.pageInfo.isELIgnored()) {
                    if (this.reader.peekChar(0) == 123) {
                        this.reader.pushChar();
                        break;
                    }
                    ttext.write(ch);
                } else {
                    ttext.write(ch);
                }
            }
            ch = this.reader.nextChar();
        }
        Node.TemplateText unused = new Node.TemplateText(ttext.toString(), this.start, parent);
    }

    private void parseXMLTemplateText(Node parent) throws JasperException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            Node.TemplateText unused;
            if (!this.reader.matches(">")) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:text&gt;");
            }
            CharArrayWriter ttext = new CharArrayWriter();
            int ch = this.reader.nextChar();
            while (ch != -1) {
                if (ch == 60) {
                    if (!this.reader.matches("![CDATA[")) break;
                    this.start = this.reader.mark();
                    Mark stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        this.err.jspError(this.start, "jsp.error.unterminated", "CDATA");
                    }
                    String text = this.reader.getText(this.start, stop);
                    ttext.write(text, 0, text.length());
                } else if (ch == 92) {
                    int next = this.reader.peekChar(0);
                    if (next == 36 || next == 35) {
                        ttext.write(this.reader.nextChar());
                    } else {
                        ttext.write(92);
                    }
                } else if (ch == 36 || ch == 35) {
                    if (this.reader.peekChar(0) == 123) {
                        this.reader.nextChar();
                        unused = new Node.TemplateText(ttext.toString(), this.start, parent);
                        this.parseELExpression(parent, (char)ch);
                        this.start = this.reader.mark();
                        ttext.reset();
                    } else {
                        ttext.write(ch);
                    }
                } else {
                    ttext.write(ch);
                }
                ch = this.reader.nextChar();
            }
            unused = new Node.TemplateText(ttext.toString(), this.start, parent);
            if (!this.reader.hasMoreInput()) {
                this.err.jspError(this.start, "jsp.error.unterminated", "&lt;jsp:text&gt;");
            } else if (!this.reader.matchesETagWithoutLessThan("jsp:text")) {
                this.err.jspError(this.start, "jsp.error.jsptext.badcontent", new String[0]);
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
        } else if (!this.pageInfo.isELIgnored() && this.reader.matches("${")) {
            this.parseELExpression(parent, '$');
        } else if (!this.pageInfo.isELIgnored() && !this.pageInfo.isDeferredSyntaxAllowedAsLiteral() && this.reader.matches("#{")) {
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
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<jsp:declaration")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<%=")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<jsp:expression")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<%")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<jsp:scriptlet")) {
            this.err.jspError(this.reader.mark(), "jsp.error.no.scriptlets", new String[0]);
        } else if (this.reader.matches("<jsp:text")) {
            this.parseXMLTemplateText(parent);
        } else if (!this.pageInfo.isELIgnored() && this.reader.matches("${")) {
            this.parseELExpression(parent, '$');
        } else if (!this.pageInfo.isELIgnored() && !this.pageInfo.isDeferredSyntaxAllowedAsLiteral() && this.reader.matches("#{")) {
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
        } else if (!this.pageInfo.isELIgnored() && this.reader.matches("${")) {
            this.err.jspError(this.reader.mark(), "jsp.error.not.in.template", "Expression language");
        } else if (!this.pageInfo.isELIgnored() && !this.pageInfo.isDeferredSyntaxAllowedAsLiteral() && this.reader.matches("#{")) {
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
        Node.TemplateText unused = new Node.TemplateText(this.reader.getText(bodyStart, bodyEnd), bodyStart, parent);
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
                        this.err.jspError(this.reader.mark(), "jsp.error.nested.jspattribute", new String[0]);
                    } else if (this.reader.matches("<jsp:body")) {
                        this.err.jspError(this.reader.mark(), "jsp.error.nested.jspbody", new String[0]);
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
            this.err.jspError(this.start, "jasper.error.bad.bodycontent.type", new String[0]);
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
            TagAttributeInfo[] tldAttrs;
            TagInfo tagInfo = ((Node.CustomTag)n).getTagInfo();
            for (TagAttributeInfo tldAttr : tldAttrs = tagInfo.getAttributes()) {
                if (!name.equals(tldAttr.getName())) continue;
                if (tldAttr.isFragment()) {
                    return "scriptless";
                }
                if (!tldAttr.canBeRequestTime()) continue;
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

    private void parseFileDirectives(Node parent) throws JasperException {
        this.reader.skipUntil("<");
        while (this.reader.hasMoreInput()) {
            this.start = this.reader.mark();
            if (this.reader.matches("%--")) {
                this.reader.skipUntil("--%>");
            } else if (this.reader.matches("%@")) {
                this.parseDirective(parent);
            } else if (this.reader.matches("jsp:directive.")) {
                this.parseXMLDirective(parent);
            } else if (this.reader.matches("%!")) {
                this.reader.skipUntil("%>");
            } else if (this.reader.matches("%=")) {
                this.reader.skipUntil("%>");
            } else if (this.reader.matches("%")) {
                this.reader.skipUntil("%>");
            }
            this.reader.skipUntil("<");
        }
    }
}

