/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.PageData
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ListIterator;
import javax.servlet.jsp.tagext.PageData;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.TagConstants;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

class PageDataImpl
extends PageData
implements TagConstants {
    private static final String JSP_VERSION = "2.0";
    private static final String CDATA_START_SECTION = "<![CDATA[\n";
    private static final String CDATA_END_SECTION = "]]>\n";
    private StringBuffer buf;

    public PageDataImpl(Node.Nodes page, Compiler compiler) throws JasperException {
        FirstPassVisitor firstPass = new FirstPassVisitor(page.getRoot(), compiler.getPageInfo());
        page.visit(firstPass);
        this.buf = new StringBuffer();
        SecondPassVisitor secondPass = new SecondPassVisitor(page.getRoot(), this.buf, compiler, firstPass.getJspIdPrefix());
        page.visit(secondPass);
    }

    public InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(this.buf.toString().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee.toString());
        }
    }

    static class SecondPassVisitor
    extends Node.Visitor
    implements TagConstants {
        private Node.Root root;
        private StringBuffer buf;
        private Compiler compiler;
        private String jspIdPrefix;
        private boolean resetDefaultNS = false;
        private int jspId;

        public SecondPassVisitor(Node.Root root, StringBuffer buf, Compiler compiler, String jspIdPrefix) {
            this.root = root;
            this.buf = buf;
            this.compiler = compiler;
            this.jspIdPrefix = jspIdPrefix;
        }

        @Override
        public void visit(Node.Root n) throws JasperException {
            if (n == this.root) {
                this.appendXmlProlog();
                this.appendTag(n);
            } else {
                boolean resetDefaultNSSave = this.resetDefaultNS;
                if (n.isXmlSyntax()) {
                    this.resetDefaultNS = true;
                }
                this.visitBody(n);
                this.resetDefaultNS = resetDefaultNSSave;
            }
        }

        @Override
        public void visit(Node.JspRoot n) throws JasperException {
            this.visitBody(n);
        }

        @Override
        public void visit(Node.PageDirective n) throws JasperException {
            this.appendPageDirective(n);
        }

        @Override
        public void visit(Node.IncludeDirective n) throws JasperException {
            this.visitBody(n);
        }

        @Override
        public void visit(Node.Comment n) throws JasperException {
        }

        @Override
        public void visit(Node.Declaration n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.JspElement n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            if (!n.getRoot().isXmlSyntax()) {
                this.buf.append("<").append("jsp:text");
                this.buf.append(" ");
                this.buf.append(this.jspIdPrefix);
                this.buf.append(":id=\"");
                this.buf.append(this.jspId++).append("\">");
            }
            this.buf.append("${");
            this.buf.append(JspUtil.escapeXml(n.getText()));
            this.buf.append("}");
            if (!n.getRoot().isXmlSyntax()) {
                this.buf.append("</jsp:text>");
            }
            this.buf.append("\n");
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.GetProperty n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.ParamAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.ParamsAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.FallBackAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            boolean resetDefaultNSSave = this.resetDefaultNS;
            this.appendTag(n, this.resetDefaultNS);
            this.resetDefaultNS = resetDefaultNSSave;
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            boolean resetDefaultNSSave = this.resetDefaultNS;
            this.appendTag(n, this.resetDefaultNS);
            this.resetDefaultNS = resetDefaultNSSave;
        }

        @Override
        public void visit(Node.JspText n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.DoBodyAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.InvokeAction n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.TagDirective n) throws JasperException {
            this.appendTagDirective(n);
        }

        @Override
        public void visit(Node.AttributeDirective n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.VariableDirective n) throws JasperException {
            this.appendTag(n);
        }

        @Override
        public void visit(Node.TemplateText n) throws JasperException {
            this.appendText(n.getText(), !n.getRoot().isXmlSyntax());
        }

        private void appendTag(Node n) throws JasperException {
            this.appendTag(n, false);
        }

        private void appendTag(Node n, boolean addDefaultNS) throws JasperException {
            Node.Nodes body = n.getBody();
            String text = n.getText();
            this.buf.append("<").append(n.getQName());
            this.buf.append("\n");
            this.printAttributes(n, addDefaultNS);
            this.buf.append("  ").append(this.jspIdPrefix).append(":id").append("=\"");
            this.buf.append(this.jspId++).append("\"\n");
            if ("root".equals(n.getLocalName()) || body != null || text != null) {
                this.buf.append(">\n");
                if ("root".equals(n.getLocalName())) {
                    if (this.compiler.getCompilationContext().isTagFile()) {
                        this.appendTagDirective();
                    } else {
                        this.appendPageDirective();
                    }
                }
                if (body != null) {
                    body.visit(this);
                } else {
                    this.appendText(text, false);
                }
                this.buf.append("</" + n.getQName() + ">\n");
            } else {
                this.buf.append("/>\n");
            }
        }

        private void appendPageDirective(Node.PageDirective n) {
            String attrName;
            int i;
            boolean append = false;
            Attributes attrs = n.getAttributes();
            int len = attrs == null ? 0 : attrs.getLength();
            for (i = 0; i < len; ++i) {
                attrName = attrs.getQName(i);
                if ("pageEncoding".equals(attrName) || "contentType".equals(attrName)) continue;
                append = true;
                break;
            }
            if (!append) {
                return;
            }
            this.buf.append("<").append(n.getQName());
            this.buf.append("\n");
            this.buf.append("  ").append(this.jspIdPrefix).append(":id").append("=\"");
            this.buf.append(this.jspId++).append("\"\n");
            for (i = 0; i < len; ++i) {
                attrName = attrs.getQName(i);
                if ("import".equals(attrName) || "contentType".equals(attrName) || "pageEncoding".equals(attrName)) continue;
                String value = attrs.getValue(i);
                this.buf.append("  ").append(attrName).append("=\"");
                this.buf.append(JspUtil.getExprInXml(value)).append("\"\n");
            }
            if (n.getImports().size() > 0) {
                boolean first = true;
                ListIterator iter = n.getImports().listIterator();
                while (iter.hasNext()) {
                    if (first) {
                        first = false;
                        this.buf.append("  import=\"");
                    } else {
                        this.buf.append(",");
                    }
                    this.buf.append(JspUtil.getExprInXml((String)iter.next()));
                }
                this.buf.append("\"\n");
            }
            this.buf.append("/>\n");
        }

        private void appendPageDirective() {
            this.buf.append("<").append("jsp:directive.page");
            this.buf.append("\n");
            this.buf.append("  ").append(this.jspIdPrefix).append(":id").append("=\"");
            this.buf.append(this.jspId++).append("\"\n");
            this.buf.append("  ").append("pageEncoding").append("=\"UTF-8\"\n");
            this.buf.append("  ").append("contentType").append("=\"");
            this.buf.append(this.compiler.getPageInfo().getContentType()).append("\"\n");
            this.buf.append("/>\n");
        }

        private void appendTagDirective(Node.TagDirective n) throws JasperException {
            boolean append = false;
            Attributes attrs = n.getAttributes();
            int len = attrs == null ? 0 : attrs.getLength();
            for (int i = 0; i < len; ++i) {
                String attrName = attrs.getQName(i);
                if ("pageEncoding".equals(attrName)) continue;
                append = true;
                break;
            }
            if (!append) {
                return;
            }
            this.appendTag(n);
        }

        private void appendTagDirective() {
            this.buf.append("<").append("jsp:directive.tag");
            this.buf.append("\n");
            this.buf.append("  ").append(this.jspIdPrefix).append(":id").append("=\"");
            this.buf.append(this.jspId++).append("\"\n");
            this.buf.append("  ").append("pageEncoding").append("=\"UTF-8\"\n");
            this.buf.append("/>\n");
        }

        private void appendText(String text, boolean createJspTextElement) {
            if (createJspTextElement) {
                this.buf.append("<").append("jsp:text");
                this.buf.append("\n");
                this.buf.append("  ").append(this.jspIdPrefix).append(":id").append("=\"");
                this.buf.append(this.jspId++).append("\"\n");
                this.buf.append(">\n");
                this.appendCDATA(text);
                this.buf.append("</jsp:text>");
                this.buf.append("\n");
            } else {
                this.appendCDATA(text);
            }
        }

        private void appendCDATA(String text) {
            this.buf.append(PageDataImpl.CDATA_START_SECTION);
            this.buf.append(this.escapeCDATA(text));
            this.buf.append(PageDataImpl.CDATA_END_SECTION);
        }

        private String escapeCDATA(String text) {
            if (text == null) {
                return "";
            }
            int len = text.length();
            CharArrayWriter result = new CharArrayWriter(len);
            for (int i = 0; i < len; ++i) {
                if (i + 2 < len && text.charAt(i) == ']' && text.charAt(i + 1) == ']' && text.charAt(i + 2) == '>') {
                    result.write(93);
                    result.write(93);
                    result.write(38);
                    result.write(103);
                    result.write(116);
                    result.write(59);
                    i += 2;
                    continue;
                }
                result.write(text.charAt(i));
            }
            return result.toString();
        }

        private void printAttributes(Node n, boolean addDefaultNS) {
            String value;
            String name;
            int i;
            Attributes attrs = n.getTaglibAttributes();
            int len = attrs == null ? 0 : attrs.getLength();
            for (int i2 = 0; i2 < len; ++i2) {
                String name2 = attrs.getQName(i2);
                String value2 = attrs.getValue(i2);
                this.buf.append("  ").append(name2).append("=\"").append(value2).append("\"\n");
            }
            attrs = n.getNonTaglibXmlnsAttributes();
            len = attrs == null ? 0 : attrs.getLength();
            boolean defaultNSSeen = false;
            for (i = 0; i < len; ++i) {
                name = attrs.getQName(i);
                value = attrs.getValue(i);
                this.buf.append("  ").append(name).append("=\"").append(value).append("\"\n");
                defaultNSSeen |= "xmlns".equals(name);
            }
            if (addDefaultNS && !defaultNSSeen) {
                this.buf.append("  xmlns=\"\"\n");
            }
            this.resetDefaultNS = false;
            attrs = n.getAttributes();
            len = attrs == null ? 0 : attrs.getLength();
            for (i = 0; i < len; ++i) {
                name = attrs.getQName(i);
                value = attrs.getValue(i);
                this.buf.append("  ").append(name).append("=\"");
                this.buf.append(JspUtil.getExprInXml(value)).append("\"\n");
            }
        }

        private void appendXmlProlog() {
            this.buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        }
    }

    static class FirstPassVisitor
    extends Node.Visitor
    implements TagConstants {
        private Node.Root root;
        private AttributesImpl rootAttrs;
        private PageInfo pageInfo;
        private String jspIdPrefix;

        public FirstPassVisitor(Node.Root root, PageInfo pageInfo) {
            this.root = root;
            this.pageInfo = pageInfo;
            this.rootAttrs = new AttributesImpl();
            this.rootAttrs.addAttribute("", "", "version", "CDATA", PageDataImpl.JSP_VERSION);
            this.jspIdPrefix = "jsp";
        }

        @Override
        public void visit(Node.Root n) throws JasperException {
            this.visitBody(n);
            if (n == this.root) {
                if (!"http://java.sun.com/JSP/Page".equals(this.rootAttrs.getValue("xmlns:jsp"))) {
                    this.rootAttrs.addAttribute("", "", "xmlns:jsp", "CDATA", "http://java.sun.com/JSP/Page");
                }
                if (this.pageInfo.isJspPrefixHijacked()) {
                    this.jspIdPrefix = this.jspIdPrefix + "jsp";
                    while (this.pageInfo.containsPrefix(this.jspIdPrefix)) {
                        this.jspIdPrefix = this.jspIdPrefix + "jsp";
                    }
                    this.rootAttrs.addAttribute("", "", "xmlns:" + this.jspIdPrefix, "CDATA", "http://java.sun.com/JSP/Page");
                }
                this.root.setAttributes(this.rootAttrs);
            }
        }

        @Override
        public void visit(Node.JspRoot n) throws JasperException {
            this.addAttributes(n.getTaglibAttributes());
            this.addAttributes(n.getNonTaglibXmlnsAttributes());
            this.addAttributes(n.getAttributes());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.TaglibDirective n) throws JasperException {
            String qName;
            Attributes attrs = n.getAttributes();
            if (attrs != null && this.rootAttrs.getIndex(qName = "xmlns:" + attrs.getValue("prefix")) == -1) {
                String location = attrs.getValue("uri");
                if (location != null) {
                    if (location.startsWith("/")) {
                        location = "urn:jsptld:" + location;
                    }
                    this.rootAttrs.addAttribute("", "", qName, "CDATA", location);
                } else {
                    location = attrs.getValue("tagdir");
                    this.rootAttrs.addAttribute("", "", qName, "CDATA", "urn:jsptagdir:" + location);
                }
            }
        }

        public String getJspIdPrefix() {
            return this.jspIdPrefix;
        }

        private void addAttributes(Attributes attrs) {
            if (attrs != null) {
                int len = attrs.getLength();
                for (int i = 0; i < len; ++i) {
                    String qName = attrs.getQName(i);
                    if ("version".equals(qName) || this.rootAttrs.getIndex(qName) != -1) continue;
                    this.rootAttrs.addAttribute(attrs.getURI(i), attrs.getLocalName(i), qName, attrs.getType(i), attrs.getValue(i));
                }
            }
        }
    }
}

