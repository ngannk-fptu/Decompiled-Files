/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.DigesterFactory
 *  org.apache.tomcat.util.descriptor.LocalResolver
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 */
package org.apache.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.util.Collection;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.ImplicitTagLibraryInfo;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.compiler.TagConstants;
import org.apache.jasper.compiler.TagLibraryInfoImpl;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.LocalResolver;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

class JspDocumentParser
extends DefaultHandler2
implements TagConstants {
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String JSP_URI = "http://java.sun.com/JSP/Page";
    private final ParserController parserController;
    private final JspCompilationContext ctxt;
    private final PageInfo pageInfo;
    private final String path;
    private StringBuilder charBuffer;
    private Node current;
    private Node scriptlessBodyNode;
    private Locator locator;
    private Mark startMark;
    private boolean inDTD;
    private boolean isValidating;
    private final EntityResolver2 entityResolver;
    private final ErrorDispatcher err;
    private final boolean isTagFile;
    private final boolean directivesOnly;
    private boolean isTop;
    private int tagDependentNesting = 0;
    private boolean tagDependentPending = false;

    JspDocumentParser(ParserController pc, String path, boolean isTagFile, boolean directivesOnly) {
        this.parserController = pc;
        this.ctxt = pc.getJspCompilationContext();
        this.pageInfo = pc.getCompiler().getPageInfo();
        this.err = pc.getCompiler().getErrorDispatcher();
        this.path = path;
        this.isTagFile = isTagFile;
        this.directivesOnly = directivesOnly;
        this.isTop = true;
        String blockExternalString = this.ctxt.getServletContext().getInitParameter("org.apache.jasper.XML_BLOCK_EXTERNAL");
        boolean blockExternal = blockExternalString == null ? true : Boolean.parseBoolean(blockExternalString);
        this.entityResolver = new LocalResolver(DigesterFactory.SERVLET_API_PUBLIC_IDS, DigesterFactory.SERVLET_API_SYSTEM_IDS, blockExternal);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Node.Nodes parse(ParserController pc, String path, Jar jar, Node parent, boolean isTagFile, boolean directivesOnly, String pageEnc, String jspConfigPageEnc, boolean isEncodingSpecifiedInProlog, boolean isBomPresent) throws JasperException {
        JspDocumentParser jspDocParser = new JspDocumentParser(pc, path, isTagFile, directivesOnly);
        Node.Nodes pageNodes = null;
        try {
            Node.Root dummyRoot = new Node.Root(null, parent, true);
            dummyRoot.setPageEncoding(pageEnc);
            dummyRoot.setJspConfigPageEncoding(jspConfigPageEnc);
            dummyRoot.setIsEncodingSpecifiedInProlog(isEncodingSpecifiedInProlog);
            dummyRoot.setIsBomPresent(isBomPresent);
            jspDocParser.current = dummyRoot;
            if (parent == null) {
                jspDocParser.addInclude(dummyRoot, jspDocParser.pageInfo.getIncludePrelude());
            } else {
                jspDocParser.isTop = false;
            }
            jspDocParser.isValidating = false;
            SAXParser saxParser = JspDocumentParser.getSAXParser(false, jspDocParser);
            InputSource source = JspUtil.getInputSource(path, jar, jspDocParser.ctxt);
            try {
                saxParser.parse(source, (DefaultHandler)jspDocParser);
            }
            catch (EnableDTDValidationException e) {
                saxParser = JspDocumentParser.getSAXParser(true, jspDocParser);
                jspDocParser.isValidating = true;
                try {
                    source.getByteStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                source = JspUtil.getInputSource(path, jar, jspDocParser.ctxt);
                saxParser.parse(source, (DefaultHandler)jspDocParser);
            }
            finally {
                try {
                    source.getByteStream().close();
                }
                catch (IOException iOException) {}
            }
            if (parent == null) {
                jspDocParser.addInclude(dummyRoot, jspDocParser.pageInfo.getIncludeCoda());
            }
            pageNodes = new Node.Nodes(dummyRoot);
        }
        catch (IOException ioe) {
            jspDocParser.err.jspError(ioe, "jsp.error.data.file.read", path);
        }
        catch (SAXParseException e) {
            jspDocParser.err.jspError(new Mark(jspDocParser.ctxt, path, e.getLineNumber(), e.getColumnNumber()), (Exception)e, e.getMessage(), new String[0]);
        }
        catch (Exception e) {
            jspDocParser.err.jspError(e, "jsp.error.data.file.processing", path);
        }
        return pageNodes;
    }

    private void addInclude(Node parent, Collection<String> files) throws SAXException {
        if (files != null) {
            for (String file : files) {
                AttributesImpl attrs = new AttributesImpl();
                attrs.addAttribute("", "file", "file", "CDATA", file);
                Node.IncludeDirective includeDir = new Node.IncludeDirective(attrs, null, parent);
                this.processIncludeDirective(file, includeDir);
            }
        }
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        return this.entityResolver.getExternalSubset(name, baseURI);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return this.entityResolver.resolveEntity(publicId, systemId);
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        return this.entityResolver.resolveEntity(name, publicId, baseURI, systemId);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        AttributesImpl taglibAttrs = null;
        AttributesImpl nonTaglibAttrs = null;
        AttributesImpl nonTaglibXmlnsAttrs = null;
        this.processChars();
        this.checkPrefixes(uri, qName, attrs);
        if (!(!this.directivesOnly || JSP_URI.equals(uri) && localName.startsWith("directive."))) {
            return;
        }
        if (this.current instanceof Node.JspText) {
            throw new SAXParseException(Localizer.getMessage("jsp.error.text.has_subelement"), this.locator);
        }
        this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
        boolean isTaglib = false;
        for (int i = attrs.getLength() - 1; i >= 0; --i) {
            isTaglib = false;
            String attrQName = attrs.getQName(i);
            if (!attrQName.startsWith("xmlns")) {
                if (nonTaglibAttrs == null) {
                    nonTaglibAttrs = new AttributesImpl();
                }
                nonTaglibAttrs.addAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
                continue;
            }
            if (attrQName.startsWith("xmlns:jsp")) {
                isTaglib = true;
            } else {
                String attrUri = attrs.getValue(i);
                isTaglib = this.pageInfo.hasTaglib(attrUri);
            }
            if (isTaglib) {
                if (taglibAttrs == null) {
                    taglibAttrs = new AttributesImpl();
                }
                taglibAttrs.addAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
                continue;
            }
            if (nonTaglibXmlnsAttrs == null) {
                nonTaglibXmlnsAttrs = new AttributesImpl();
            }
            nonTaglibXmlnsAttrs.addAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
        }
        Node node = null;
        if (this.tagDependentPending && JSP_URI.equals(uri) && localName.equals("body")) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
            this.current = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark);
            return;
        }
        if (this.tagDependentPending && JSP_URI.equals(uri) && localName.equals("attribute")) {
            this.current = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark);
            return;
        }
        if (this.tagDependentPending) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
        }
        if (this.tagDependentNesting > 0) {
            node = new Node.UninterpretedTag(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
        } else if (JSP_URI.equals(uri)) {
            node = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark);
        } else {
            node = this.parseCustomAction(qName, localName, uri, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
            if (node == null) {
                node = new Node.UninterpretedTag(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
            } else {
                String bodyType = JspDocumentParser.getBodyType((Node.CustomTag)node);
                if (this.scriptlessBodyNode == null && bodyType.equalsIgnoreCase("scriptless")) {
                    this.scriptlessBodyNode = node;
                } else if ("tagdependent".equalsIgnoreCase(bodyType)) {
                    this.tagDependentPending = true;
                }
            }
        }
        this.current = node;
    }

    @Override
    public void characters(char[] buf, int offset, int len) {
        if (this.charBuffer == null) {
            this.charBuffer = new StringBuilder();
        }
        this.charBuffer.append(buf, offset, len);
    }

    private void processChars() throws SAXException {
        if (this.charBuffer == null || this.directivesOnly) {
            return;
        }
        boolean isAllSpace = true;
        if (!(this.current instanceof Node.JspText) && !(this.current instanceof Node.NamedAttribute)) {
            for (int i = 0; i < this.charBuffer.length(); ++i) {
                char ch = this.charBuffer.charAt(i);
                if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') continue;
                isAllSpace = false;
                break;
            }
        }
        if (!isAllSpace && this.tagDependentPending) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
        }
        if (this.tagDependentNesting > 0 || this.pageInfo.isELIgnored() || this.current instanceof Node.ScriptingElement) {
            if (this.charBuffer.length() > 0) {
                Node.TemplateText i = new Node.TemplateText(this.charBuffer.toString(), this.startMark, this.current);
            }
            this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
            this.charBuffer = null;
            return;
        }
        if (this.current instanceof Node.JspText || this.current instanceof Node.NamedAttribute || !isAllSpace) {
            int line = this.startMark.getLineNumber();
            int column = this.startMark.getColumnNumber();
            CharArrayWriter ttext = new CharArrayWriter();
            int lastCh = 0;
            int elType = 0;
            for (int i = 0; i < this.charBuffer.length(); ++i) {
                char ch;
                block29: {
                    ch = this.charBuffer.charAt(i);
                    if (ch == '\n') {
                        column = 1;
                        ++line;
                    } else {
                        ++column;
                    }
                    if ((lastCh == 36 || lastCh == 35) && ch == '{') {
                        elType = lastCh;
                        if (ttext.size() > 0) {
                            Node.TemplateText unused = new Node.TemplateText(ttext.toString(), this.startMark, this.current);
                            ttext.reset();
                            this.startMark = new Mark(this.ctxt, this.path, line, column - 2);
                        }
                        ++i;
                        boolean singleQ = false;
                        boolean doubleQ = false;
                        lastCh = 0;
                        while (true) {
                            if (i >= this.charBuffer.length()) {
                                throw new SAXParseException(Localizer.getMessage("jsp.error.unterminated", (char)elType + "{"), this.locator);
                            }
                            ch = this.charBuffer.charAt(i);
                            if (ch == '\n') {
                                column = 1;
                                ++line;
                            } else {
                                ++column;
                            }
                            if (lastCh == 92 && (singleQ || doubleQ)) {
                                ttext.write(ch);
                                lastCh = 0;
                            } else {
                                if (ch == '}') {
                                    Node.ELExpression unused = new Node.ELExpression((char)elType, ttext.toString(), this.startMark, this.current);
                                    ttext.reset();
                                    this.startMark = new Mark(this.ctxt, this.path, line, column);
                                    break block29;
                                }
                                if (ch == '\"') {
                                    doubleQ = !doubleQ;
                                } else if (ch == '\'') {
                                    singleQ = !singleQ;
                                }
                                ttext.write(ch);
                                lastCh = ch;
                            }
                            ++i;
                        }
                    }
                    if (lastCh == 92 && (ch == '$' || ch == '#')) {
                        if (this.pageInfo.isELIgnored()) {
                            ttext.write(92);
                        }
                        ttext.write(ch);
                        ch = '\u0000';
                    } else {
                        if (lastCh == 36 || lastCh == 35 || lastCh == 92) {
                            ttext.write(lastCh);
                        }
                        if (ch != '$' && ch != '#' && ch != '\\') {
                            ttext.write(ch);
                        }
                    }
                }
                lastCh = ch;
            }
            if (lastCh == 36 || lastCh == 35 || lastCh == 92) {
                ttext.write(lastCh);
            }
            if (ttext.size() > 0) {
                Node.TemplateText templateText = new Node.TemplateText(ttext.toString(), this.startMark, this.current);
            }
        }
        this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
        this.charBuffer = null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Node.Nodes children;
        String bodyType;
        int i;
        this.processChars();
        if (!(!this.directivesOnly || JSP_URI.equals(uri) && localName.startsWith("directive."))) {
            return;
        }
        if (this.current instanceof Node.NamedAttribute) {
            boolean isTrim = ((Node.NamedAttribute)this.current).isTrim();
            Node.Nodes subElems = this.current.getBody();
            for (i = 0; subElems != null && i < subElems.size(); ++i) {
                Node subElem = subElems.getNode(i);
                if (!(subElem instanceof Node.TemplateText)) continue;
                if (i == 0) {
                    if (!isTrim) continue;
                    ((Node.TemplateText)subElem).ltrim();
                    continue;
                }
                if (i == subElems.size() - 1) {
                    if (!isTrim) continue;
                    ((Node.TemplateText)subElem).rtrim();
                    continue;
                }
                if (!((Node.TemplateText)subElem).isAllSpace()) continue;
                subElems.remove(subElem);
            }
        } else if (this.current instanceof Node.ScriptingElement) {
            this.checkScriptingBody((Node.ScriptingElement)this.current);
        }
        if (this.isTagDependent(this.current)) {
            --this.tagDependentNesting;
        }
        if (this.scriptlessBodyNode != null && this.current.equals(this.scriptlessBodyNode)) {
            this.scriptlessBodyNode = null;
        }
        if (this.current instanceof Node.CustomTag && "empty".equalsIgnoreCase(bodyType = JspDocumentParser.getBodyType((Node.CustomTag)this.current)) && (children = this.current.getBody()) != null && children.size() > 0) {
            for (i = 0; i < children.size(); ++i) {
                Node child = children.getNode(i);
                if (child instanceof Node.NamedAttribute) continue;
                throw new SAXParseException(Localizer.getMessage("jasper.error.emptybodycontent.nonempty", this.current.qName), this.locator);
            }
        }
        if (this.current.getParent() != null) {
            this.current = this.current.getParent();
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void comment(char[] buf, int offset, int len) throws SAXException {
        this.processChars();
        if (!this.inDTD) {
            this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
            Node.Comment comment = new Node.Comment(new String(buf, offset, len), this.startMark, this.current);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        this.processChars();
        this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
    }

    @Override
    public void endCDATA() throws SAXException {
        this.processChars();
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (!this.isValidating) {
            this.fatalError(new EnableDTDValidationException("jsp.error.enable_dtd_validation", null));
        }
        this.inDTD = true;
    }

    @Override
    public void endDTD() throws SAXException {
        this.inDTD = false;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        TagLibraryInfo taglibInfo;
        if (this.directivesOnly && !JSP_URI.equals(uri)) {
            return;
        }
        try {
            taglibInfo = this.getTaglibInfo(prefix, uri);
        }
        catch (JasperException je) {
            throw new SAXParseException(Localizer.getMessage("jsp.error.could.not.add.taglibraries"), this.locator, (Exception)((Object)je));
        }
        if (taglibInfo != null) {
            if (this.pageInfo.getTaglib(uri) == null) {
                this.pageInfo.addTaglib(uri, taglibInfo);
            }
            this.pageInfo.pushPrefixMapping(prefix, uri);
        } else {
            this.pageInfo.pushPrefixMapping(prefix, null);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        String uri;
        if (this.directivesOnly && !JSP_URI.equals(uri = this.pageInfo.getURI(prefix))) {
            return;
        }
        this.pageInfo.popPrefixMapping(prefix);
    }

    private Node parseStandardAction(String qName, String localName, Attributes nonTaglibAttrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start) throws SAXException {
        Node node = null;
        if (localName.equals("root")) {
            if (!(this.current instanceof Node.Root)) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.nested_jsproot"), this.locator);
            }
            node = new Node.JspRoot(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
            if (this.isTop) {
                this.pageInfo.setHasJspRoot(true);
            }
        } else if (localName.equals("directive.page")) {
            if (this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.istagfile", localName), this.locator);
            }
            node = new Node.PageDirective(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
            String imports = nonTaglibAttrs.getValue("import");
            if (imports != null) {
                ((Node.PageDirective)node).addImport(imports);
            }
        } else if (localName.equals("directive.include")) {
            node = new Node.IncludeDirective(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
            this.processIncludeDirective(nonTaglibAttrs.getValue("file"), node);
        } else if (localName.equals("declaration")) {
            if (this.scriptlessBodyNode != null) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.no.scriptlets", localName), this.locator);
            }
            node = new Node.Declaration(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("scriptlet")) {
            if (this.scriptlessBodyNode != null) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.no.scriptlets", localName), this.locator);
            }
            node = new Node.Scriptlet(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("expression")) {
            if (this.scriptlessBodyNode != null) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.no.scriptlets", localName), this.locator);
            }
            node = new Node.Expression(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("useBean")) {
            node = new Node.UseBean(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("setProperty")) {
            node = new Node.SetProperty(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("getProperty")) {
            node = new Node.GetProperty(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("include")) {
            node = new Node.IncludeAction(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("forward")) {
            node = new Node.ForwardAction(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("param")) {
            node = new Node.ParamAction(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("params")) {
            node = new Node.ParamsAction(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("plugin")) {
            node = new Node.PlugIn(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("text")) {
            node = new Node.JspText(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("body")) {
            node = new Node.JspBody(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("attribute")) {
            node = new Node.NamedAttribute(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("output")) {
            node = new Node.JspOutput(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("directive.tag")) {
            if (!this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.isnottagfile", localName), this.locator);
            }
            node = new Node.TagDirective(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
            String imports = nonTaglibAttrs.getValue("import");
            if (imports != null) {
                ((Node.TagDirective)node).addImport(imports);
            }
        } else if (localName.equals("directive.attribute")) {
            if (!this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.isnottagfile", localName), this.locator);
            }
            node = new Node.AttributeDirective(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("directive.variable")) {
            if (!this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.isnottagfile", localName), this.locator);
            }
            node = new Node.VariableDirective(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("invoke")) {
            if (!this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.isnottagfile", localName), this.locator);
            }
            node = new Node.InvokeAction(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("doBody")) {
            if (!this.isTagFile) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.action.isnottagfile", localName), this.locator);
            }
            node = new Node.DoBodyAction(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("element")) {
            node = new Node.JspElement(qName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else if (localName.equals("fallback")) {
            node = new Node.FallBackAction(qName, nonTaglibXmlnsAttrs, taglibAttrs, start, this.current);
        } else {
            throw new SAXParseException(Localizer.getMessage("jsp.error.xml.badStandardAction", localName), this.locator);
        }
        return node;
    }

    private Node parseCustomAction(String qName, String localName, String uri, Attributes nonTaglibAttrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) throws SAXException {
        TagLibraryInfo tagLibInfo = this.pageInfo.getTaglib(uri);
        if (tagLibInfo == null) {
            return null;
        }
        TagInfo tagInfo = tagLibInfo.getTag(localName);
        TagFileInfo tagFileInfo = tagLibInfo.getTagFile(localName);
        if (tagInfo == null && tagFileInfo == null) {
            throw new SAXParseException(Localizer.getMessage("jsp.error.xml.bad_tag", localName, uri), this.locator);
        }
        Class<?> tagHandlerClass = null;
        if (tagInfo != null) {
            String handlerClassName = tagInfo.getTagClassName();
            try {
                tagHandlerClass = this.ctxt.getClassLoader().loadClass(handlerClassName);
            }
            catch (Exception e) {
                throw new SAXParseException(Localizer.getMessage("jsp.error.loadclass.taghandler", handlerClassName, qName), this.locator, e);
            }
        }
        String prefix = this.getPrefix(qName);
        Node.CustomTag ret = null;
        ret = tagInfo != null ? new Node.CustomTag(qName, prefix, localName, uri, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent, tagInfo, tagHandlerClass) : new Node.CustomTag(qName, prefix, localName, uri, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent, tagFileInfo);
        return ret;
    }

    private TagLibraryInfo getTaglibInfo(String prefix, String uri) throws JasperException {
        TagLibraryInfo result = null;
        if (uri.startsWith("urn:jsptagdir:")) {
            String tagdir = uri.substring("urn:jsptagdir:".length());
            result = new ImplicitTagLibraryInfo(this.ctxt, this.parserController, this.pageInfo, prefix, tagdir, this.err);
        } else {
            boolean isPlainUri = false;
            if (uri.startsWith("urn:jsptld:")) {
                uri = uri.substring("urn:jsptld:".length());
            } else {
                isPlainUri = true;
            }
            TldResourcePath tldResourcePath = this.ctxt.getTldResourcePath(uri);
            if (tldResourcePath != null || !isPlainUri) {
                if (this.ctxt.getOptions().isCaching()) {
                    result = this.ctxt.getOptions().getCache().get(uri);
                }
                if (result == null) {
                    result = new TagLibraryInfoImpl(this.ctxt, this.parserController, this.pageInfo, prefix, uri, tldResourcePath, this.err);
                    if (this.ctxt.getOptions().isCaching()) {
                        this.ctxt.getOptions().getCache().put(uri, result);
                    }
                }
            }
        }
        return result;
    }

    private void checkScriptingBody(Node.ScriptingElement scriptingElem) throws SAXException {
        Node.Nodes body = scriptingElem.getBody();
        if (body != null) {
            int size = body.size();
            for (int i = 0; i < size; ++i) {
                Node n = body.getNode(i);
                if (n instanceof Node.TemplateText) continue;
                String elemType = "scriptlet";
                if (scriptingElem instanceof Node.Declaration) {
                    elemType = "declaration";
                }
                if (scriptingElem instanceof Node.Expression) {
                    elemType = "expression";
                }
                String msg = Localizer.getMessage("jsp.error.parse.xml.scripting.invalid.body", elemType);
                throw new SAXParseException(msg, this.locator);
            }
        }
    }

    private void processIncludeDirective(String fname, Node parent) throws SAXException {
        if (fname == null) {
            return;
        }
        try {
            this.parserController.parse(fname, parent, null);
        }
        catch (FileNotFoundException fnfe) {
            throw new SAXParseException(Localizer.getMessage("jsp.error.file.not.found", fname), this.locator, fnfe);
        }
        catch (Exception e) {
            throw new SAXParseException(e.getMessage(), this.locator, e);
        }
    }

    private void checkPrefixes(String uri, String qName, Attributes attrs) {
        this.checkPrefix(uri, qName);
        int len = attrs.getLength();
        for (int i = 0; i < len; ++i) {
            this.checkPrefix(attrs.getURI(i), attrs.getQName(i));
        }
    }

    private void checkPrefix(String uri, String qName) {
        String prefix = this.getPrefix(qName);
        if (prefix.length() > 0) {
            this.pageInfo.addPrefix(prefix);
            if ("jsp".equals(prefix) && !JSP_URI.equals(uri)) {
                this.pageInfo.setIsJspPrefixHijacked(true);
            }
        }
    }

    private String getPrefix(String qName) {
        int index = qName.indexOf(58);
        if (index != -1) {
            return qName.substring(0, index);
        }
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static SAXParser getSAXParser(boolean validating, JspDocumentParser jspDocParser) throws Exception {
        ClassLoader original;
        PrivilegedGetTccl pa;
        Thread currentThread = Thread.currentThread();
        if (Constants.IS_SECURITY_ENABLED) {
            pa = new PrivilegedGetTccl(currentThread);
            original = (ClassLoader)AccessController.doPrivileged(pa);
        } else {
            original = currentThread.getContextClassLoader();
        }
        try {
            if (Constants.IS_SECURITY_ENABLED) {
                pa = new PrivilegedSetTccl(currentThread, JspDocumentParser.class.getClassLoader());
                AccessController.doPrivileged(pa);
            } else {
                currentThread.setContextClassLoader(JspDocumentParser.class.getClassLoader());
            }
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setValidating(validating);
            if (validating) {
                factory.setFeature("http://xml.org/sax/features/validation", true);
                factory.setFeature("http://apache.org/xml/features/validation/schema", true);
            }
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setProperty(LEXICAL_HANDLER_PROPERTY, jspDocParser);
            xmlReader.setErrorHandler(jspDocParser);
            SAXParser sAXParser = saxParser;
            return sAXParser;
        }
        finally {
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa2 = new PrivilegedSetTccl(currentThread, original);
                AccessController.doPrivileged(pa2);
            } else {
                currentThread.setContextClassLoader(original);
            }
        }
    }

    private static String getBodyType(Node.CustomTag custom) {
        if (custom.getTagInfo() != null) {
            return custom.getTagInfo().getBodyContent();
        }
        return custom.getTagFileInfo().getTagInfo().getBodyContent();
    }

    private boolean isTagDependent(Node n) {
        if (n instanceof Node.CustomTag) {
            String bodyType = JspDocumentParser.getBodyType((Node.CustomTag)n);
            return "tagdependent".equalsIgnoreCase(bodyType);
        }
        return false;
    }

    private static class EnableDTDValidationException
    extends SAXParseException {
        private static final long serialVersionUID = 1L;

        EnableDTDValidationException(String message, Locator loc) {
            super(message, loc);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

