/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarFile;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.ImplicitTagLibraryInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.compiler.TagConstants;
import org.apache.sling.scripting.jsp.jasper.compiler.TagLibraryInfoImpl;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

class JspDocumentParser
extends DefaultHandler
implements LexicalHandler,
TagConstants {
    private static final String JSP_VERSION = "version";
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String JSP_URI = "http://java.sun.com/JSP/Page";
    private static final EnableDTDValidationException ENABLE_DTD_VALIDATION_EXCEPTION = new EnableDTDValidationException("jsp.error.enable_dtd_validation", null);
    private ParserController parserController;
    private JspCompilationContext ctxt;
    private PageInfo pageInfo;
    private String path;
    private StringBuffer charBuffer;
    private Node current;
    private Node scriptlessBodyNode;
    private Locator locator;
    private Mark startMark;
    private boolean inDTD;
    private boolean isValidating;
    private ErrorDispatcher err;
    private boolean isTagFile;
    private boolean directivesOnly;
    private boolean isTop;
    private int tagDependentNesting = 0;
    private boolean tagDependentPending = false;

    public JspDocumentParser(ParserController pc, String path, boolean isTagFile, boolean directivesOnly) {
        this.parserController = pc;
        this.ctxt = pc.getJspCompilationContext();
        this.pageInfo = pc.getCompiler().getPageInfo();
        this.err = pc.getCompiler().getErrorDispatcher();
        this.path = path;
        this.isTagFile = isTagFile;
        this.directivesOnly = directivesOnly;
        this.isTop = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Node.Nodes parse(ParserController pc, String path, JarFile jarFile, Node parent, boolean isTagFile, boolean directivesOnly, String pageEnc, String jspConfigPageEnc, boolean isEncodingSpecifiedInProlog, boolean isBomPresent) throws JasperException {
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
            SAXParser saxParser = JspDocumentParser.getSAXParser(false, jspDocParser);
            InputStream inStream = null;
            try {
                inStream = JspUtil.getInputStream(path, jarFile, jspDocParser.ctxt, jspDocParser.err);
                saxParser.parse(new InputSource(inStream), (DefaultHandler)jspDocParser);
            }
            catch (EnableDTDValidationException e) {
                saxParser = JspDocumentParser.getSAXParser(true, jspDocParser);
                jspDocParser.isValidating = true;
                if (inStream != null) {
                    try {
                        inStream.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                inStream = JspUtil.getInputStream(path, jarFile, jspDocParser.ctxt, jspDocParser.err);
                saxParser.parse(new InputSource(inStream), (DefaultHandler)jspDocParser);
            }
            finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    }
                    catch (Exception exception) {}
                }
            }
            if (parent == null) {
                jspDocParser.addInclude(dummyRoot, jspDocParser.pageInfo.getIncludeCoda());
            }
            pageNodes = new Node.Nodes(dummyRoot);
        }
        catch (IOException ioe) {
            jspDocParser.err.jspError("jsp.error.data.file.read", path, ioe);
        }
        catch (SAXParseException e) {
            jspDocParser.err.jspError(new Mark(jspDocParser.ctxt, path, e.getLineNumber(), e.getColumnNumber()), e.getMessage());
        }
        catch (Exception e) {
            jspDocParser.err.jspError(e);
        }
        return pageNodes;
    }

    private void addInclude(Node parent, List files) throws SAXException {
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
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        AttributesImpl taglibAttrs = null;
        AttributesImpl nonTaglibAttrs = null;
        AttributesImpl nonTaglibXmlnsAttrs = null;
        this.processChars();
        this.checkPrefixes(uri, qName, attrs);
        if (!(!this.directivesOnly || JSP_URI.equals(uri) && localName.startsWith("directive."))) {
            return;
        }
        if (JSP_URI.equals(uri) && "text".equals(this.current.getLocalName())) {
            throw new SAXParseException(Localizer.getMessage("jsp.error.text.has_subelement"), this.locator);
        }
        this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
        if (attrs != null) {
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
        }
        Node node = null;
        if (this.tagDependentPending && JSP_URI.equals(uri) && localName.equals("body")) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
            this.current = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
            return;
        }
        if (this.tagDependentPending && JSP_URI.equals(uri) && localName.equals("attribute")) {
            this.current = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
            return;
        }
        if (this.tagDependentPending) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
        }
        if (this.tagDependentNesting > 0) {
            node = new Node.UninterpretedTag(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
        } else if (JSP_URI.equals(uri)) {
            node = this.parseStandardAction(qName, localName, nonTaglibAttrs, nonTaglibXmlnsAttrs, taglibAttrs, this.startMark, this.current);
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
            this.charBuffer = new StringBuffer();
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
                if (this.charBuffer.charAt(i) == ' ' || this.charBuffer.charAt(i) == '\n' || this.charBuffer.charAt(i) == '\r' || this.charBuffer.charAt(i) == '\t') continue;
                isAllSpace = false;
                break;
            }
        }
        if (!isAllSpace && this.tagDependentPending) {
            this.tagDependentPending = false;
            ++this.tagDependentNesting;
        }
        if (this.tagDependentNesting > 0) {
            if (this.charBuffer.length() > 0) {
                new Node.TemplateText(this.charBuffer.toString(), this.startMark, this.current);
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
                block28: {
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
                            new Node.TemplateText(ttext.toString(), this.startMark, this.current);
                            ttext = new CharArrayWriter();
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
                                    new Node.ELExpression((char)elType, ttext.toString(), this.startMark, this.current);
                                    ttext = new CharArrayWriter();
                                    this.startMark = new Mark(this.ctxt, this.path, line, column);
                                    break block28;
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
                new Node.TemplateText(ttext.toString(), this.startMark, this.current);
            }
        }
        this.startMark = new Mark(this.ctxt, this.path, this.locator.getLineNumber(), this.locator.getColumnNumber());
        this.charBuffer = null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.processChars();
        if (!(!this.directivesOnly || JSP_URI.equals(uri) && localName.startsWith("directive."))) {
            return;
        }
        if (this.current instanceof Node.NamedAttribute) {
            boolean isTrim = ((Node.NamedAttribute)this.current).isTrim();
            Node.Nodes subElems = ((Node.NamedAttribute)this.current).getBody();
            for (int i = 0; subElems != null && i < subElems.size(); ++i) {
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
            new Node.Comment(new String(buf, offset, len), this.startMark, this.current);
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
            this.fatalError(ENABLE_DTD_VALIDATION_EXCEPTION);
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

    private Node parseStandardAction(String qName, String localName, Attributes nonTaglibAttrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) throws SAXException {
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
            throw new SAXException(Localizer.getMessage("jsp.error.xml.bad_tag", localName, uri));
        }
        Class<?> tagHandlerClass = null;
        if (tagInfo != null) {
            String handlerClassName = tagInfo.getTagClassName();
            try {
                tagHandlerClass = this.ctxt.getClassLoader().loadClass(handlerClassName);
            }
            catch (Exception e) {
                throw new SAXException(Localizer.getMessage("jsp.error.loadclass.taghandler", handlerClassName, qName), e);
            }
        }
        String prefix = "";
        int colon = qName.indexOf(58);
        if (colon != -1) {
            prefix = qName.substring(0, colon);
        }
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
            String[] location = this.ctxt.getTldLocation(uri);
            if (location != null || !isPlainUri) {
                result = new TagLibraryInfoImpl(this.ctxt, this.parserController, this.pageInfo, prefix, uri, location, this.err);
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
                throw new SAXException(msg);
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
            throw new SAXException(e);
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
        int index = qName.indexOf(58);
        if (index != -1) {
            String prefix = qName.substring(0, index);
            this.pageInfo.addPrefix(prefix);
            if ("jsp".equals(prefix) && !JSP_URI.equals(uri)) {
                this.pageInfo.setIsJspPrefixHijacked(true);
            }
        }
    }

    private static SAXParser getSAXParser(boolean validating, JspDocumentParser jspDocParser) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        factory.setValidating(validating);
        SAXParser saxParser = factory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setProperty(LEXICAL_HANDLER_PROPERTY, jspDocParser);
        xmlReader.setErrorHandler(jspDocParser);
        return saxParser;
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
        EnableDTDValidationException(String message, Locator loc) {
            super(message, loc);
        }
    }
}

