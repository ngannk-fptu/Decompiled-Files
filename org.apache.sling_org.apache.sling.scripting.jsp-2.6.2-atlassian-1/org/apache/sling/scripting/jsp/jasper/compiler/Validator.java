/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ExpressionFactory
 *  javax.el.FunctionMapper
 *  javax.servlet.jsp.tagext.FunctionInfo
 *  javax.servlet.jsp.tagext.JspFragment
 *  javax.servlet.jsp.tagext.PageData
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagData
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  javax.servlet.jsp.tagext.ValidationMessage
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.BeanRepository;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.ELNode;
import org.apache.sling.scripting.jsp.jasper.compiler.ELParser;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageDataImpl;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.TagLibraryInfoImpl;
import org.apache.sling.scripting.jsp.jasper.el.ELContextImpl;
import org.xml.sax.Attributes;

class Validator {
    Validator() {
    }

    public static void validate(Compiler compiler, Node.Nodes page) throws JasperException {
        page.visit(new DirectiveVisitor(compiler));
        PageInfo pageInfo = compiler.getPageInfo();
        String contentType = pageInfo.getContentType();
        if (contentType == null || contentType.indexOf("charset=") < 0) {
            boolean isXml = page.getRoot().isXmlSyntax();
            String defaultType = contentType == null ? (isXml ? "text/xml" : "text/html") : contentType;
            String charset = null;
            if (isXml) {
                charset = "UTF-8";
            } else if (!page.getRoot().isDefaultPageEncoding()) {
                charset = page.getRoot().getPageEncoding();
            }
            if (charset != null) {
                pageInfo.setContentType(defaultType + ";charset=" + charset);
            } else {
                pageInfo.setContentType(defaultType);
            }
        }
        page.visit(new ValidateVisitor(compiler));
        Validator.validateXmlView(new PageDataImpl(page, compiler), compiler);
        page.visit(new TagExtraInfoVisitor(compiler));
    }

    private static void validateXmlView(PageData xmlView, Compiler compiler) throws JasperException {
        StringBuffer errMsg = null;
        ErrorDispatcher errDisp = compiler.getErrorDispatcher();
        for (Object o : compiler.getPageInfo().getTaglibs()) {
            TagLibraryInfoImpl tli;
            ValidationMessage[] errors;
            if (!(o instanceof TagLibraryInfoImpl) || (errors = (tli = (TagLibraryInfoImpl)o).validate(xmlView)) == null || errors.length == 0) continue;
            if (errMsg == null) {
                errMsg = new StringBuffer();
            }
            errMsg.append("<h3>");
            errMsg.append(Localizer.getMessage("jsp.error.tlv.invalid.page", tli.getShortName(), compiler.getPageInfo().getJspFile()));
            errMsg.append("</h3>");
            for (int i = 0; i < errors.length; ++i) {
                if (errors[i] == null) continue;
                errMsg.append("<p>");
                errMsg.append(errors[i].getId());
                errMsg.append(": ");
                errMsg.append(errors[i].getMessage());
                errMsg.append("</p>");
            }
        }
        if (errMsg != null) {
            errDisp.jspError(errMsg.toString());
        }
    }

    static class TagExtraInfoVisitor
    extends Node.Visitor {
        private ErrorDispatcher err;

        TagExtraInfoVisitor(Compiler compiler) {
            this.err = compiler.getErrorDispatcher();
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            ValidationMessage[] errors;
            TagInfo tagInfo = n.getTagInfo();
            if (tagInfo == null) {
                this.err.jspError(n, "jsp.error.missing.tagInfo", n.getQName());
            }
            if ((errors = tagInfo.validate(n.getTagData())) != null && errors.length != 0) {
                StringBuffer errMsg = new StringBuffer();
                errMsg.append("<h3>");
                errMsg.append(Localizer.getMessage("jsp.error.tei.invalid.attributes", n.getQName()));
                errMsg.append("</h3>");
                for (int i = 0; i < errors.length; ++i) {
                    errMsg.append("<p>");
                    if (errors[i].getId() != null) {
                        errMsg.append(errors[i].getId());
                        errMsg.append(": ");
                    }
                    errMsg.append(errors[i].getMessage());
                    errMsg.append("</p>");
                }
                this.err.jspError(n, errMsg.toString());
            }
            this.visitBody(n);
        }
    }

    static class ValidateVisitor
    extends Node.Visitor {
        private PageInfo pageInfo;
        private ErrorDispatcher err;
        private TagInfo tagInfo;
        private ClassLoader loader;
        private final StringBuffer buf = new StringBuffer(32);
        private static final JspUtil.ValidAttribute[] jspRootAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("xsi:schemaLocation"), new JspUtil.ValidAttribute("version", true)};
        private static final JspUtil.ValidAttribute[] includeDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("file", true)};
        private static final JspUtil.ValidAttribute[] taglibDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("uri"), new JspUtil.ValidAttribute("tagdir"), new JspUtil.ValidAttribute("prefix", true)};
        private static final JspUtil.ValidAttribute[] includeActionAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("page", true, true), new JspUtil.ValidAttribute("flush")};
        private static final JspUtil.ValidAttribute[] paramActionAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name", true), new JspUtil.ValidAttribute("value", true, true)};
        private static final JspUtil.ValidAttribute[] forwardActionAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("page", true, true)};
        private static final JspUtil.ValidAttribute[] getPropertyAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name", true), new JspUtil.ValidAttribute("property", true)};
        private static final JspUtil.ValidAttribute[] setPropertyAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name", true), new JspUtil.ValidAttribute("property", true), new JspUtil.ValidAttribute("value", false, true), new JspUtil.ValidAttribute("param")};
        private static final JspUtil.ValidAttribute[] useBeanAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("id", true), new JspUtil.ValidAttribute("scope"), new JspUtil.ValidAttribute("class"), new JspUtil.ValidAttribute("type"), new JspUtil.ValidAttribute("beanName", false, true)};
        private static final JspUtil.ValidAttribute[] plugInAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("type", true), new JspUtil.ValidAttribute("code", true), new JspUtil.ValidAttribute("codebase"), new JspUtil.ValidAttribute("align"), new JspUtil.ValidAttribute("archive"), new JspUtil.ValidAttribute("height", false, true), new JspUtil.ValidAttribute("hspace"), new JspUtil.ValidAttribute("jreversion"), new JspUtil.ValidAttribute("name"), new JspUtil.ValidAttribute("vspace"), new JspUtil.ValidAttribute("width", false, true), new JspUtil.ValidAttribute("nspluginurl"), new JspUtil.ValidAttribute("iepluginurl")};
        private static final JspUtil.ValidAttribute[] attributeAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name", true), new JspUtil.ValidAttribute("trim")};
        private static final JspUtil.ValidAttribute[] invokeAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("fragment", true), new JspUtil.ValidAttribute("var"), new JspUtil.ValidAttribute("varReader"), new JspUtil.ValidAttribute("scope")};
        private static final JspUtil.ValidAttribute[] doBodyAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("var"), new JspUtil.ValidAttribute("varReader"), new JspUtil.ValidAttribute("scope")};
        private static final JspUtil.ValidAttribute[] jspOutputAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("omit-xml-declaration"), new JspUtil.ValidAttribute("doctype-root-element"), new JspUtil.ValidAttribute("doctype-public"), new JspUtil.ValidAttribute("doctype-system")};

        ValidateVisitor(Compiler compiler) {
            this.pageInfo = compiler.getPageInfo();
            this.err = compiler.getErrorDispatcher();
            this.tagInfo = compiler.getCompilationContext().getTagInfo();
            this.loader = compiler.getCompilationContext().getClassLoader();
        }

        @Override
        public void visit(Node.JspRoot n) throws JasperException {
            JspUtil.checkAttributes("Jsp:root", n, jspRootAttrs, this.err);
            String version = n.getTextAttribute("version");
            if (!(version.equals("1.2") || version.equals("2.0") || version.equals("2.1"))) {
                this.err.jspError(n, "jsp.error.jsproot.version.invalid", version);
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.IncludeDirective n) throws JasperException {
            JspUtil.checkAttributes("Include directive", n, includeDirectiveAttrs, this.err);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.TaglibDirective n) throws JasperException {
            JspUtil.checkAttributes("Taglib directive", n, taglibDirectiveAttrs, this.err);
            String uri = n.getAttributeValue("uri");
            String tagdir = n.getAttributeValue("tagdir");
            if (uri == null && tagdir == null) {
                this.err.jspError(n, "jsp.error.taglibDirective.missing.location");
            }
            if (uri != null && tagdir != null) {
                this.err.jspError(n, "jsp.error.taglibDirective.both_uri_and_tagdir");
            }
        }

        @Override
        public void visit(Node.ParamAction n) throws JasperException {
            JspUtil.checkAttributes("Param action", n, paramActionAttrs, this.err);
            this.throwErrorIfExpression(n, "name", "jsp:param");
            n.setValue(this.getJspAttribute(null, "value", null, null, n.getAttributeValue("value"), String.class, n, false));
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ParamsAction n) throws JasperException {
            Node.Nodes subElems = n.getBody();
            if (subElems == null) {
                this.err.jspError(n, "jsp.error.params.emptyBody");
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            JspUtil.checkAttributes("Include action", n, includeActionAttrs, this.err);
            n.setPage(this.getJspAttribute(null, "page", null, null, n.getAttributeValue("page"), String.class, n, false));
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            JspUtil.checkAttributes("Forward", n, forwardActionAttrs, this.err);
            n.setPage(this.getJspAttribute(null, "page", null, null, n.getAttributeValue("page"), String.class, n, false));
            this.visitBody(n);
        }

        @Override
        public void visit(Node.GetProperty n) throws JasperException {
            JspUtil.checkAttributes("GetProperty", n, getPropertyAttrs, this.err);
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            boolean valueSpecified;
            JspUtil.checkAttributes("SetProperty", n, setPropertyAttrs, this.err);
            String property = n.getTextAttribute("property");
            String param = n.getTextAttribute("param");
            String value = n.getAttributeValue("value");
            n.setValue(this.getJspAttribute(null, "value", null, null, value, Object.class, n, false));
            boolean bl = valueSpecified = n.getValue() != null;
            if ("*".equals(property)) {
                if (param != null || valueSpecified) {
                    this.err.jspError(n, "jsp.error.setProperty.invalid");
                }
            } else if (param != null && valueSpecified) {
                this.err.jspError(n, "jsp.error.setProperty.invalid");
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            JspUtil.checkAttributes("UseBean", n, useBeanAttrs, this.err);
            String name = n.getTextAttribute("id");
            String scope = n.getTextAttribute("scope");
            JspUtil.checkScope(scope, n, this.err);
            String className = n.getTextAttribute("class");
            String type = n.getTextAttribute("type");
            BeanRepository beanInfo = this.pageInfo.getBeanRepository();
            if (className == null && type == null) {
                this.err.jspError(n, "jsp.error.usebean.missingType");
            }
            if (beanInfo.checkVariable(name)) {
                this.err.jspError(n, "jsp.error.usebean.duplicate");
            }
            if ("session".equals(scope) && !this.pageInfo.isSession()) {
                this.err.jspError(n, "jsp.error.usebean.noSession");
            }
            Node.JspAttribute jattr = this.getJspAttribute(null, "beanName", null, null, n.getAttributeValue("beanName"), String.class, n, false);
            n.setBeanName(jattr);
            if (className != null && jattr != null) {
                this.err.jspError(n, "jsp.error.usebean.notBoth");
            }
            if (className == null) {
                className = type;
            }
            beanInfo.addBean(n, name, className, scope);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            JspUtil.checkAttributes("Plugin", n, plugInAttrs, this.err);
            this.throwErrorIfExpression(n, "type", "jsp:plugin");
            this.throwErrorIfExpression(n, "code", "jsp:plugin");
            this.throwErrorIfExpression(n, "codebase", "jsp:plugin");
            this.throwErrorIfExpression(n, "align", "jsp:plugin");
            this.throwErrorIfExpression(n, "archive", "jsp:plugin");
            this.throwErrorIfExpression(n, "hspace", "jsp:plugin");
            this.throwErrorIfExpression(n, "jreversion", "jsp:plugin");
            this.throwErrorIfExpression(n, "name", "jsp:plugin");
            this.throwErrorIfExpression(n, "vspace", "jsp:plugin");
            this.throwErrorIfExpression(n, "nspluginurl", "jsp:plugin");
            this.throwErrorIfExpression(n, "iepluginurl", "jsp:plugin");
            String type = n.getTextAttribute("type");
            if (type == null) {
                this.err.jspError(n, "jsp.error.plugin.notype");
            }
            if (!type.equals("bean") && !type.equals("applet")) {
                this.err.jspError(n, "jsp.error.plugin.badtype");
            }
            if (n.getTextAttribute("code") == null) {
                this.err.jspError(n, "jsp.error.plugin.nocode");
            }
            Node.JspAttribute width = this.getJspAttribute(null, "width", null, null, n.getAttributeValue("width"), String.class, n, false);
            n.setWidth(width);
            Node.JspAttribute height = this.getJspAttribute(null, "height", null, null, n.getAttributeValue("height"), String.class, n, false);
            n.setHeight(height);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
            JspUtil.checkAttributes("Attribute", n, attributeAttrs, this.err);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            this.visitBody(n);
        }

        @Override
        public void visit(Node.Declaration n) throws JasperException {
            if (this.pageInfo.isScriptingInvalid()) {
                this.err.jspError(n.getStart(), "jsp.error.no.scriptlets");
            }
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            if (this.pageInfo.isScriptingInvalid()) {
                this.err.jspError(n.getStart(), "jsp.error.no.scriptlets");
            }
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            if (this.pageInfo.isScriptingInvalid()) {
                this.err.jspError(n.getStart(), "jsp.error.no.scriptlets");
            }
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            if (this.pageInfo.isELIgnored()) {
                return;
            }
            if (n.getType() == '#') {
                if (!(this.pageInfo.isDeferredSyntaxAllowedAsLiteral() || this.tagInfo != null && (this.tagInfo == null || this.tagInfo.getTagLibrary().getRequiredVersion().equals("2.0") || this.tagInfo.getTagLibrary().getRequiredVersion().equals("1.2")))) {
                    this.err.jspError(n, "jsp.error.el.template.deferred");
                } else {
                    return;
                }
            }
            StringBuffer expr = this.getBuffer();
            expr.append(n.getType()).append('{').append(n.getText()).append('}');
            ELNode.Nodes el = ELParser.parse(expr.toString());
            this.prepareExpression(el, n, expr.toString());
            n.setEL(el);
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            Attributes attrs;
            if (n.getNamedAttributeNodes().size() != 0) {
                this.err.jspError(n, "jsp.error.namedAttribute.invalidUse");
            }
            if ((attrs = n.getAttributes()) != null) {
                int attrSize = attrs.getLength();
                Node.JspAttribute[] jspAttrs = new Node.JspAttribute[attrSize];
                for (int i = 0; i < attrSize; ++i) {
                    jspAttrs[i] = this.getJspAttribute(null, attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), Object.class, n, false);
                }
                n.setJspAttributes(jspAttrs);
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            TagInfo tagInfo = n.getTagInfo();
            if (tagInfo == null) {
                this.err.jspError(n, "jsp.error.missing.tagInfo", n.getQName());
            }
            if (n.implementsSimpleTag() && tagInfo.getBodyContent().equalsIgnoreCase("JSP")) {
                this.err.jspError(n, "jsp.error.simpletag.badbodycontent", tagInfo.getTagClassName());
            }
            if (tagInfo.hasDynamicAttributes() && !n.implementsDynamicAttributes()) {
                this.err.jspError(n, "jsp.error.dynamic.attributes.not.implemented", n.getQName());
            }
            TagAttributeInfo[] tldAttrs = tagInfo.getAttributes();
            String customActionUri = n.getURI();
            Attributes attrs = n.getAttributes();
            int attrsSize = attrs == null ? 0 : attrs.getLength();
            for (int i = 0; i < tldAttrs.length; ++i) {
                String attr = null;
                if (attrs != null && (attr = attrs.getValue(tldAttrs[i].getName())) == null) {
                    attr = attrs.getValue(customActionUri, tldAttrs[i].getName());
                }
                Node.NamedAttribute na = n.getNamedAttributeNode(tldAttrs[i].getName());
                if (tldAttrs[i].isRequired() && attr == null && na == null) {
                    this.err.jspError((Node)n, "jsp.error.missing_attribute", tldAttrs[i].getName(), n.getLocalName());
                }
                if (attr == null || na == null) continue;
                this.err.jspError(n, "jsp.error.duplicate.name.jspattribute", tldAttrs[i].getName());
            }
            Node.Nodes naNodes = n.getNamedAttributeNodes();
            int jspAttrsSize = naNodes.size() + attrsSize;
            Node.JspAttribute[] jspAttrs = null;
            if (jspAttrsSize > 0) {
                jspAttrs = new Node.JspAttribute[jspAttrsSize];
            }
            Hashtable<String, Object> tagDataAttrs = new Hashtable<String, Object>(attrsSize);
            this.checkXmlAttributes(n, jspAttrs, tagDataAttrs);
            this.checkNamedAttributes(n, jspAttrs, attrsSize, tagDataAttrs);
            TagData tagData = new TagData(tagDataAttrs);
            TagExtraInfo tei = tagInfo.getTagExtraInfo();
            if (tei != null && tei.getVariableInfo(tagData) != null && tei.getVariableInfo(tagData).length > 0 && tagInfo.getTagVariableInfos().length > 0) {
                this.err.jspError("jsp.error.non_null_tei_and_var_subelems", n.getQName());
            }
            n.setTagData(tagData);
            n.setJspAttributes(jspAttrs);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspElement n) throws JasperException {
            int i;
            Attributes attrs = n.getAttributes();
            if (attrs == null) {
                this.err.jspError(n, "jsp.error.jspelement.missing.name");
            }
            int xmlAttrLen = attrs.getLength();
            Node.Nodes namedAttrs = n.getNamedAttributeNodes();
            int jspAttrSize = xmlAttrLen - 1 + namedAttrs.size();
            Node.JspAttribute[] jspAttrs = new Node.JspAttribute[jspAttrSize];
            int jspAttrIndex = 0;
            for (i = 0; i < xmlAttrLen; ++i) {
                if ("name".equals(attrs.getLocalName(i))) {
                    n.setNameAttribute(this.getJspAttribute(null, attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), String.class, n, false));
                    continue;
                }
                if (jspAttrIndex >= jspAttrSize) continue;
                jspAttrs[jspAttrIndex++] = this.getJspAttribute(null, attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), Object.class, n, false);
            }
            if (n.getNameAttribute() == null) {
                this.err.jspError(n, "jsp.error.jspelement.missing.name");
            }
            for (i = 0; i < namedAttrs.size(); ++i) {
                Node.NamedAttribute na = (Node.NamedAttribute)namedAttrs.getNode(i);
                jspAttrs[jspAttrIndex++] = new Node.JspAttribute(na, null, false);
            }
            n.setJspAttributes(jspAttrs);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspOutput n) throws JasperException {
            JspUtil.checkAttributes("jsp:output", n, jspOutputAttrs, this.err);
            if (n.getBody() != null) {
                this.err.jspError(n, "jsp.error.jspoutput.nonemptybody");
            }
            String omitXmlDecl = n.getAttributeValue("omit-xml-declaration");
            String doctypeName = n.getAttributeValue("doctype-root-element");
            String doctypePublic = n.getAttributeValue("doctype-public");
            String doctypeSystem = n.getAttributeValue("doctype-system");
            String omitXmlDeclOld = this.pageInfo.getOmitXmlDecl();
            String doctypeNameOld = this.pageInfo.getDoctypeName();
            String doctypePublicOld = this.pageInfo.getDoctypePublic();
            String doctypeSystemOld = this.pageInfo.getDoctypeSystem();
            if (omitXmlDecl != null && omitXmlDeclOld != null && !omitXmlDecl.equals(omitXmlDeclOld)) {
                this.err.jspError(n, "jsp.error.jspoutput.conflict", "omit-xml-declaration", omitXmlDeclOld, omitXmlDecl);
            }
            if (doctypeName != null && doctypeNameOld != null && !doctypeName.equals(doctypeNameOld)) {
                this.err.jspError(n, "jsp.error.jspoutput.conflict", "doctype-root-element", doctypeNameOld, doctypeName);
            }
            if (doctypePublic != null && doctypePublicOld != null && !doctypePublic.equals(doctypePublicOld)) {
                this.err.jspError(n, "jsp.error.jspoutput.conflict", "doctype-public", doctypePublicOld, doctypePublic);
            }
            if (doctypeSystem != null && doctypeSystemOld != null && !doctypeSystem.equals(doctypeSystemOld)) {
                this.err.jspError(n, "jsp.error.jspoutput.conflict", "doctype-system", doctypeSystemOld, doctypeSystem);
            }
            if (doctypeName == null && doctypeSystem != null || doctypeName != null && doctypeSystem == null) {
                this.err.jspError(n, "jsp.error.jspoutput.doctypenamesystem");
            }
            if (doctypePublic != null && doctypeSystem == null) {
                this.err.jspError(n, "jsp.error.jspoutput.doctypepulicsystem");
            }
            if (omitXmlDecl != null) {
                this.pageInfo.setOmitXmlDecl(omitXmlDecl);
            }
            if (doctypeName != null) {
                this.pageInfo.setDoctypeName(doctypeName);
            }
            if (doctypeSystem != null) {
                this.pageInfo.setDoctypeSystem(doctypeSystem);
            }
            if (doctypePublic != null) {
                this.pageInfo.setDoctypePublic(doctypePublic);
            }
        }

        @Override
        public void visit(Node.InvokeAction n) throws JasperException {
            JspUtil.checkAttributes("Invoke", n, invokeAttrs, this.err);
            String scope = n.getTextAttribute("scope");
            JspUtil.checkScope(scope, n, this.err);
            String var = n.getTextAttribute("var");
            String varReader = n.getTextAttribute("varReader");
            if (scope != null && var == null && varReader == null) {
                this.err.jspError(n, "jsp.error.missing_var_or_varReader");
            }
            if (var != null && varReader != null) {
                this.err.jspError(n, "jsp.error.var_and_varReader");
            }
        }

        @Override
        public void visit(Node.DoBodyAction n) throws JasperException {
            JspUtil.checkAttributes("DoBody", n, doBodyAttrs, this.err);
            String scope = n.getTextAttribute("scope");
            JspUtil.checkScope(scope, n, this.err);
            String var = n.getTextAttribute("var");
            String varReader = n.getTextAttribute("varReader");
            if (scope != null && var == null && varReader == null) {
                this.err.jspError(n, "jsp.error.missing_var_or_varReader");
            }
            if (var != null && varReader != null) {
                this.err.jspError(n, "jsp.error.var_and_varReader");
            }
        }

        private void checkXmlAttributes(Node.CustomTag n, Node.JspAttribute[] jspAttrs, Hashtable<String, Object> tagDataAttrs) throws JasperException {
            TagInfo tagInfo = n.getTagInfo();
            if (tagInfo == null) {
                this.err.jspError(n, "jsp.error.missing.tagInfo", n.getQName());
            }
            TagAttributeInfo[] tldAttrs = tagInfo.getAttributes();
            Attributes attrs = n.getAttributes();
            boolean checkDeferred = !this.pageInfo.isDeferredSyntaxAllowedAsLiteral() && !tagInfo.getTagLibrary().getRequiredVersion().equals("2.0") && !tagInfo.getTagLibrary().getRequiredVersion().equals("1.2");
            for (int i = 0; attrs != null && i < attrs.getLength(); ++i) {
                boolean found = false;
                boolean runtimeExpression = n.getRoot().isXmlSyntax() && attrs.getValue(i).startsWith("%=") || !n.getRoot().isXmlSyntax() && attrs.getValue(i).startsWith("<%=");
                boolean elExpression = false;
                boolean deferred = false;
                boolean deferredValueIsLiteral = false;
                ELNode.Nodes el = null;
                if (!runtimeExpression) {
                    el = ELParser.parse(attrs.getValue(i));
                    Iterator<ELNode> nodes = el.iterator();
                    while (!(!nodes.hasNext() || elExpression && deferredValueIsLiteral)) {
                        ELNode node = nodes.next();
                        if (!(node instanceof ELNode.Root)) continue;
                        if (((ELNode.Root)node).getType() == '$') {
                            elExpression = true;
                            continue;
                        }
                        if (!checkDeferred || ((ELNode.Root)node).getType() != '#') continue;
                        elExpression = true;
                        deferred = true;
                        if (!this.pageInfo.isELIgnored()) continue;
                        deferredValueIsLiteral = true;
                    }
                }
                boolean expression = runtimeExpression || elExpression && (!this.pageInfo.isELIgnored() || !"true".equalsIgnoreCase(this.pageInfo.getIsELIgnored()) && checkDeferred && deferred);
                for (int j = 0; tldAttrs != null && j < tldAttrs.length; ++j) {
                    block35: {
                        if (!attrs.getLocalName(i).equals(tldAttrs[j].getName()) || attrs.getURI(i) != null && attrs.getURI(i).length() != 0 && !attrs.getURI(i).equals(n.getURI())) continue;
                        if (tldAttrs[j].canBeRequestTime() || tldAttrs[j].isDeferredMethod() || tldAttrs[j].isDeferredValue()) {
                            Object expectedType;
                            if (!expression) {
                                if (deferredValueIsLiteral && !this.pageInfo.isDeferredSyntaxAllowedAsLiteral()) {
                                    this.err.jspError(n, "jsp.error.attribute.custom.non_rt_with_expr", tldAttrs[j].getName());
                                }
                                expectedType = null;
                                if (tldAttrs[j].isDeferredMethod()) {
                                    String m = tldAttrs[j].getMethodSignature();
                                    if (m != null) {
                                        int rti = m.trim().indexOf(32);
                                        if (rti > 0) {
                                            expectedType = m.substring(0, rti).trim();
                                        }
                                    } else {
                                        expectedType = "java.lang.Object";
                                    }
                                }
                                if (tldAttrs[j].isDeferredValue()) {
                                    expectedType = tldAttrs[j].getExpectedTypeName();
                                }
                                if (expectedType != null) {
                                    Class expectedClass = String.class;
                                    try {
                                        expectedClass = JspUtil.toClass(expectedType, this.loader);
                                    }
                                    catch (ClassNotFoundException e) {
                                        this.err.jspError((Node)n, "jsp.error.unknown_attribute_type", tldAttrs[j].getName(), (String)expectedType);
                                    }
                                    try {
                                        this.pageInfo.getExpressionFactory().coerceToType((Object)attrs.getValue(i), expectedClass);
                                    }
                                    catch (Exception e) {
                                        this.err.jspError(n, "jsp.error.coerce_to_type", tldAttrs[j].getName(), (String)expectedType, attrs.getValue(i));
                                    }
                                }
                                jspAttrs[i] = new Node.JspAttribute(tldAttrs[j], attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), false, null, false);
                            } else {
                                if (deferred && !tldAttrs[j].isDeferredMethod() && !tldAttrs[j].isDeferredValue()) {
                                    this.err.jspError(n, "jsp.error.attribute.custom.non_rt_with_expr", tldAttrs[j].getName());
                                }
                                if (!deferred && !tldAttrs[j].canBeRequestTime()) {
                                    this.err.jspError(n, "jsp.error.attribute.custom.non_rt_with_expr", tldAttrs[j].getName());
                                }
                                expectedType = String.class;
                                try {
                                    String typeStr = tldAttrs[j].getTypeName();
                                    if (tldAttrs[j].isFragment()) {
                                        expectedType = JspFragment.class;
                                    } else if (typeStr != null) {
                                        expectedType = JspUtil.toClass(typeStr, this.loader);
                                    }
                                    if (elExpression) {
                                        this.validateFunctions(el, n);
                                        jspAttrs[i] = new Node.JspAttribute(tldAttrs[j], attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), false, el, false);
                                        ELContextImpl ctx = new ELContextImpl();
                                        ctx.setFunctionMapper(this.getFunctionMapper(el));
                                        try {
                                            jspAttrs[i].validateEL(this.pageInfo.getExpressionFactory(), ctx);
                                        }
                                        catch (ELException e) {
                                            this.err.jspError(n.getStart(), "jsp.error.invalid.expression", attrs.getValue(i), e.toString());
                                        }
                                        break block35;
                                    }
                                    jspAttrs[i] = this.getJspAttribute(tldAttrs[j], attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), (Class)expectedType, n, false);
                                }
                                catch (ClassNotFoundException e) {
                                    this.err.jspError((Node)n, "jsp.error.unknown_attribute_type", tldAttrs[j].getName(), tldAttrs[j].getTypeName());
                                }
                            }
                        } else {
                            if (expression) {
                                this.err.jspError(n, "jsp.error.attribute.custom.non_rt_with_expr", tldAttrs[j].getName());
                            }
                            jspAttrs[i] = new Node.JspAttribute(tldAttrs[j], attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), false, null, false);
                        }
                    }
                    if (expression) {
                        tagDataAttrs.put(attrs.getQName(i), TagData.REQUEST_TIME_VALUE);
                    } else {
                        tagDataAttrs.put(attrs.getQName(i), attrs.getValue(i));
                    }
                    found = true;
                    break;
                }
                if (found) continue;
                if (tagInfo.hasDynamicAttributes()) {
                    jspAttrs[i] = this.getJspAttribute(null, attrs.getQName(i), attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i), Object.class, n, true);
                    continue;
                }
                this.err.jspError((Node)n, "jsp.error.bad_attribute", attrs.getQName(i), n.getLocalName());
            }
        }

        private void checkNamedAttributes(Node.CustomTag n, Node.JspAttribute[] jspAttrs, int start, Hashtable<String, Object> tagDataAttrs) throws JasperException {
            TagInfo tagInfo = n.getTagInfo();
            if (tagInfo == null) {
                this.err.jspError(n, "jsp.error.missing.tagInfo", n.getQName());
            }
            TagAttributeInfo[] tldAttrs = tagInfo.getAttributes();
            Node.Nodes naNodes = n.getNamedAttributeNodes();
            for (int i = 0; i < naNodes.size(); ++i) {
                Node.NamedAttribute na = (Node.NamedAttribute)naNodes.getNode(i);
                boolean found = false;
                for (int j = 0; j < tldAttrs.length; ++j) {
                    String attrPrefix = na.getPrefix();
                    if (!na.getLocalName().equals(tldAttrs[j].getName()) || attrPrefix != null && attrPrefix.length() != 0 && !attrPrefix.equals(n.getPrefix())) continue;
                    jspAttrs[start + i] = new Node.JspAttribute(na, tldAttrs[j], false);
                    NamedAttributeVisitor nav = null;
                    if (na.getBody() != null) {
                        nav = new NamedAttributeVisitor();
                        na.getBody().visit(nav);
                    }
                    if (nav != null && nav.hasDynamicContent()) {
                        tagDataAttrs.put(na.getName(), TagData.REQUEST_TIME_VALUE);
                    } else {
                        tagDataAttrs.put(na.getName(), na.getText());
                    }
                    found = true;
                    break;
                }
                if (found) continue;
                if (tagInfo.hasDynamicAttributes()) {
                    jspAttrs[start + i] = new Node.JspAttribute(na, null, true);
                    continue;
                }
                this.err.jspError((Node)n, "jsp.error.bad_attribute", na.getName(), n.getLocalName());
            }
        }

        private Node.JspAttribute getJspAttribute(TagAttributeInfo tai, String qName, String uri, String localName, String value, Class expectedType, Node n, boolean dynamic) throws JasperException {
            Node.JspAttribute result = null;
            if (value != null) {
                if (n.getRoot().isXmlSyntax() && value.startsWith("%=")) {
                    result = new Node.JspAttribute(tai, qName, uri, localName, value.substring(2, value.length() - 1), true, null, dynamic);
                } else if (!n.getRoot().isXmlSyntax() && value.startsWith("<%=")) {
                    result = new Node.JspAttribute(tai, qName, uri, localName, value.substring(3, value.length() - 2), true, null, dynamic);
                } else {
                    ELNode.Nodes el = ELParser.parse(value);
                    boolean deferred = false;
                    Iterator<ELNode> nodes = el.iterator();
                    while (nodes.hasNext() && !deferred) {
                        ELNode node = nodes.next();
                        if (!(node instanceof ELNode.Root) || ((ELNode.Root)node).getType() != '#') continue;
                        deferred = true;
                    }
                    if (el.containsEL() && !this.pageInfo.isELIgnored() && (!this.pageInfo.isDeferredSyntaxAllowedAsLiteral() && deferred || !deferred)) {
                        this.validateFunctions(el, n);
                        result = new Node.JspAttribute(tai, qName, uri, localName, value, false, el, dynamic);
                        ELContextImpl ctx = new ELContextImpl();
                        ctx.setFunctionMapper(this.getFunctionMapper(el));
                        try {
                            result.validateEL(this.pageInfo.getExpressionFactory(), ctx);
                        }
                        catch (ELException e) {
                            this.err.jspError(n.getStart(), "jsp.error.invalid.expression", value, e.toString());
                        }
                    } else {
                        value = value.replace('\u001b', '$');
                        result = new Node.JspAttribute(tai, qName, uri, localName, value, false, null, dynamic);
                    }
                }
            } else {
                Node.NamedAttribute namedAttributeNode = n.getNamedAttributeNode(qName);
                if (namedAttributeNode != null) {
                    result = new Node.JspAttribute(namedAttributeNode, tai, dynamic);
                }
            }
            return result;
        }

        private StringBuffer getBuffer() {
            this.buf.setLength(0);
            return this.buf;
        }

        private boolean isExpression(Node n, String value, boolean checkDeferred) {
            boolean runtimeExpression = n.getRoot().isXmlSyntax() && value.startsWith("%=") || !n.getRoot().isXmlSyntax() && value.startsWith("<%=");
            boolean elExpression = false;
            if (!runtimeExpression && !this.pageInfo.isELIgnored()) {
                Iterator<ELNode> nodes = ELParser.parse(value).iterator();
                while (nodes.hasNext() && !elExpression) {
                    ELNode node = nodes.next();
                    if (!(node instanceof ELNode.Root)) continue;
                    if (((ELNode.Root)node).getType() == '$') {
                        elExpression = true;
                        continue;
                    }
                    if (!checkDeferred || this.pageInfo.isDeferredSyntaxAllowedAsLiteral() || ((ELNode.Root)node).getType() != '#') continue;
                    elExpression = true;
                }
            }
            return runtimeExpression || elExpression;
        }

        private void throwErrorIfExpression(Node n, String attrName, String actionName) throws JasperException {
            if (n.getAttributes() != null && n.getAttributes().getValue(attrName) != null && this.isExpression(n, n.getAttributes().getValue(attrName), true)) {
                this.err.jspError(n, "jsp.error.attribute.standard.non_rt_with_expr", attrName, actionName);
            }
        }

        private String findUri(String prefix, Node n) {
            for (Node p = n; p != null; p = p.getParent()) {
                Attributes attrs = p.getTaglibAttributes();
                if (attrs == null) continue;
                for (int i = 0; i < attrs.getLength(); ++i) {
                    String name = attrs.getQName(i);
                    int k = name.indexOf(58);
                    if (prefix == null && k < 0) {
                        return attrs.getValue(i);
                    }
                    if (prefix == null || k < 0 || !prefix.equals(name.substring(k + 1))) continue;
                    return attrs.getValue(i);
                }
            }
            return null;
        }

        private void validateFunctions(ELNode.Nodes el, Node n) throws JasperException {
            class FVVisitor
            extends ELNode.Visitor {
                Node n;

                FVVisitor(Node n) {
                    this.n = n;
                }

                @Override
                public void visit(ELNode.Function func) throws JasperException {
                    String prefix = func.getPrefix();
                    String function = func.getName();
                    String uri = null;
                    if (this.n.getRoot().isXmlSyntax()) {
                        uri = ValidateVisitor.this.findUri(prefix, this.n);
                    } else if (prefix != null) {
                        uri = ValidateVisitor.this.pageInfo.getURI(prefix);
                    }
                    if (uri == null) {
                        if (prefix == null) {
                            ValidateVisitor.this.err.jspError(this.n, "jsp.error.noFunctionPrefix", function);
                        } else {
                            ValidateVisitor.this.err.jspError(this.n, "jsp.error.attribute.invalidPrefix", prefix);
                        }
                    }
                    TagLibraryInfo taglib = ValidateVisitor.this.pageInfo.getTaglib(uri);
                    FunctionInfo funcInfo = null;
                    if (taglib != null) {
                        funcInfo = taglib.getFunction(function);
                    }
                    if (funcInfo == null) {
                        ValidateVisitor.this.err.jspError(this.n, "jsp.error.noFunction", function);
                    }
                    func.setUri(uri);
                    func.setFunctionInfo(funcInfo);
                    ValidateVisitor.this.processSignature(func);
                }
            }
            el.visit(new FVVisitor(n));
        }

        private void prepareExpression(ELNode.Nodes el, Node n, String expr) throws JasperException {
            this.validateFunctions(el, n);
            ELContextImpl ctx = new ELContextImpl();
            ctx.setFunctionMapper(this.getFunctionMapper(el));
            ExpressionFactory ef = this.pageInfo.getExpressionFactory();
            try {
                ef.createValueExpression((ELContext)ctx, expr, Object.class);
            }
            catch (ELException eLException) {
                // empty catch block
            }
        }

        private void processSignature(ELNode.Function func) throws JasperException {
            func.setMethodName(this.getMethod(func));
            func.setParameters(this.getParameters(func));
        }

        private String getMethod(ELNode.Function func) throws JasperException {
            int end;
            FunctionInfo funcInfo = func.getFunctionInfo();
            String signature = funcInfo.getFunctionSignature();
            int start = signature.indexOf(32);
            if (start < 0) {
                this.err.jspError("jsp.error.tld.fn.invalid.signature", func.getPrefix(), func.getName());
            }
            if ((end = signature.indexOf(40)) < 0) {
                this.err.jspError("jsp.error.tld.fn.invalid.signature.parenexpected", func.getPrefix(), func.getName());
            }
            return signature.substring(start + 1, end).trim();
        }

        private String[] getParameters(ELNode.Function func) throws JasperException {
            FunctionInfo funcInfo = func.getFunctionInfo();
            String signature = funcInfo.getFunctionSignature();
            ArrayList<String> params = new ArrayList<String>();
            int start = signature.indexOf(40) + 1;
            boolean lastArg = false;
            while (true) {
                String arg;
                int p;
                if ((p = signature.indexOf(44, start)) < 0) {
                    p = signature.indexOf(41, start);
                    if (p < 0) {
                        this.err.jspError("jsp.error.tld.fn.invalid.signature", func.getPrefix(), func.getName());
                    }
                    lastArg = true;
                }
                if (!"".equals(arg = signature.substring(start, p).trim())) {
                    params.add(arg);
                }
                if (lastArg) break;
                start = p + 1;
            }
            return params.toArray(new String[params.size()]);
        }

        private FunctionMapper getFunctionMapper(ELNode.Nodes el) throws JasperException {
            class ValidateFunctionMapper
            extends FunctionMapper {
                private HashMap<String, Method> fnmap = new HashMap();

                ValidateFunctionMapper() {
                }

                public void mapFunction(String fnQName, Method method) {
                    this.fnmap.put(fnQName, method);
                }

                public Method resolveFunction(String prefix, String localName) {
                    return this.fnmap.get(prefix + ":" + localName);
                }
            }
            ValidateFunctionMapper fmapper = new ValidateFunctionMapper();
            class MapperELVisitor
            extends ELNode.Visitor {
                ValidateFunctionMapper fmapper;

                MapperELVisitor(ValidateFunctionMapper fmapper) {
                    this.fmapper = fmapper;
                }

                @Override
                public void visit(ELNode.Function n) throws JasperException {
                    Class<?> c = null;
                    Method method = null;
                    try {
                        c = ValidateVisitor.this.loader.loadClass(n.getFunctionInfo().getFunctionClass());
                    }
                    catch (ClassNotFoundException e) {
                        ValidateVisitor.this.err.jspError("jsp.error.function.classnotfound", n.getFunctionInfo().getFunctionClass(), n.getPrefix() + ':' + n.getName(), e.getMessage());
                    }
                    String[] paramTypes = n.getParameters();
                    int size = paramTypes.length;
                    Class[] params = new Class[size];
                    int i = 0;
                    try {
                        for (i = 0; i < size; ++i) {
                            params[i] = JspUtil.toClass(paramTypes[i], ValidateVisitor.this.loader);
                        }
                        method = c.getDeclaredMethod(n.getMethodName(), params);
                    }
                    catch (ClassNotFoundException e) {
                        ValidateVisitor.this.err.jspError("jsp.error.signature.classnotfound", paramTypes[i], n.getPrefix() + ':' + n.getName(), e.getMessage());
                    }
                    catch (NoSuchMethodException e) {
                        ValidateVisitor.this.err.jspError("jsp.error.noFunctionMethod", n.getMethodName(), n.getName(), c.getName());
                    }
                    this.fmapper.mapFunction(n.getPrefix() + ':' + n.getName(), method);
                }
            }
            el.visit(new MapperELVisitor(fmapper));
            return fmapper;
        }

        private static class NamedAttributeVisitor
        extends Node.Visitor {
            private boolean hasDynamicContent;

            private NamedAttributeVisitor() {
            }

            @Override
            public void doVisit(Node n) throws JasperException {
                if (!(n instanceof Node.JspText) && !(n instanceof Node.TemplateText)) {
                    this.hasDynamicContent = true;
                }
                this.visitBody(n);
            }

            public boolean hasDynamicContent() {
                return this.hasDynamicContent;
            }
        }
    }

    static class DirectiveVisitor
    extends Node.Visitor {
        private PageInfo pageInfo;
        private ErrorDispatcher err;
        private static final JspUtil.ValidAttribute[] pageDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("language"), new JspUtil.ValidAttribute("extends"), new JspUtil.ValidAttribute("import"), new JspUtil.ValidAttribute("session"), new JspUtil.ValidAttribute("buffer"), new JspUtil.ValidAttribute("autoFlush"), new JspUtil.ValidAttribute("isThreadSafe"), new JspUtil.ValidAttribute("info"), new JspUtil.ValidAttribute("errorPage"), new JspUtil.ValidAttribute("isErrorPage"), new JspUtil.ValidAttribute("contentType"), new JspUtil.ValidAttribute("pageEncoding"), new JspUtil.ValidAttribute("isELIgnored"), new JspUtil.ValidAttribute("deferredSyntaxAllowedAsLiteral"), new JspUtil.ValidAttribute("trimDirectiveWhitespaces")};
        private boolean pageEncodingSeen = false;

        DirectiveVisitor(Compiler compiler) throws JasperException {
            this.pageInfo = compiler.getPageInfo();
            this.err = compiler.getErrorDispatcher();
        }

        @Override
        public void visit(Node.IncludeDirective n) throws JasperException {
            boolean pageEncodingSeenSave = this.pageEncodingSeen;
            this.pageEncodingSeen = false;
            this.visitBody(n);
            this.pageEncodingSeen = pageEncodingSeenSave;
        }

        @Override
        public void visit(Node.PageDirective n) throws JasperException {
            JspUtil.checkAttributes("Page directive", n, pageDirectiveAttrs, this.err);
            Attributes attrs = n.getAttributes();
            for (int i = 0; attrs != null && i < attrs.getLength(); ++i) {
                String attr = attrs.getQName(i);
                String value = attrs.getValue(i);
                if ("language".equals(attr)) {
                    if (this.pageInfo.getLanguage(false) == null) {
                        this.pageInfo.setLanguage(value, n, this.err, true);
                        continue;
                    }
                    if (this.pageInfo.getLanguage(false).equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.language", this.pageInfo.getLanguage(false), value);
                    continue;
                }
                if ("extends".equals(attr)) {
                    if (this.pageInfo.getExtends(false) == null) {
                        this.pageInfo.setExtends(value, n);
                        continue;
                    }
                    if (this.pageInfo.getExtends(false).equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.extends", this.pageInfo.getExtends(false), value);
                    continue;
                }
                if ("contentType".equals(attr)) {
                    if (this.pageInfo.getContentType() == null) {
                        this.pageInfo.setContentType(value);
                        continue;
                    }
                    if (this.pageInfo.getContentType().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.contenttype", this.pageInfo.getContentType(), value);
                    continue;
                }
                if ("session".equals(attr)) {
                    if (this.pageInfo.getSession() == null) {
                        this.pageInfo.setSession(value, n, this.err);
                        continue;
                    }
                    if (this.pageInfo.getSession().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.session", this.pageInfo.getSession(), value);
                    continue;
                }
                if ("buffer".equals(attr)) {
                    if (this.pageInfo.getBufferValue() == null) {
                        this.pageInfo.setBufferValue(value, n, this.err);
                        continue;
                    }
                    if (this.pageInfo.getBufferValue().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.buffer", this.pageInfo.getBufferValue(), value);
                    continue;
                }
                if ("autoFlush".equals(attr)) {
                    if (this.pageInfo.getAutoFlush() == null) {
                        this.pageInfo.setAutoFlush(value, n, this.err);
                        continue;
                    }
                    if (this.pageInfo.getAutoFlush().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.autoflush", this.pageInfo.getAutoFlush(), value);
                    continue;
                }
                if ("isThreadSafe".equals(attr)) {
                    if (this.pageInfo.getIsThreadSafe() == null) {
                        this.pageInfo.setIsThreadSafe(value, n, this.err);
                        continue;
                    }
                    if (this.pageInfo.getIsThreadSafe().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.isthreadsafe", this.pageInfo.getIsThreadSafe(), value);
                    continue;
                }
                if ("isELIgnored".equals(attr)) {
                    if (this.pageInfo.getIsELIgnored() == null) {
                        this.pageInfo.setIsELIgnored(value, n, this.err, true);
                        continue;
                    }
                    if (this.pageInfo.getIsELIgnored().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.iselignored", this.pageInfo.getIsELIgnored(), value);
                    continue;
                }
                if ("isErrorPage".equals(attr)) {
                    if (this.pageInfo.getIsErrorPage() == null) {
                        this.pageInfo.setIsErrorPage(value, n, this.err);
                        continue;
                    }
                    if (this.pageInfo.getIsErrorPage().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.iserrorpage", this.pageInfo.getIsErrorPage(), value);
                    continue;
                }
                if ("errorPage".equals(attr)) {
                    if (this.pageInfo.getErrorPage() == null) {
                        this.pageInfo.setErrorPage(value);
                        continue;
                    }
                    if (this.pageInfo.getErrorPage().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.errorpage", this.pageInfo.getErrorPage(), value);
                    continue;
                }
                if ("info".equals(attr)) {
                    if (this.pageInfo.getInfo() == null) {
                        this.pageInfo.setInfo(value);
                        continue;
                    }
                    if (this.pageInfo.getInfo().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.info", this.pageInfo.getInfo(), value);
                    continue;
                }
                if ("pageEncoding".equals(attr)) {
                    if (this.pageEncodingSeen) {
                        this.err.jspError(n, "jsp.error.page.multi.pageencoding");
                    }
                    this.pageEncodingSeen = true;
                    String actual = this.comparePageEncodings(value, n);
                    n.getRoot().setPageEncoding(actual);
                    continue;
                }
                if ("deferredSyntaxAllowedAsLiteral".equals(attr)) {
                    if (this.pageInfo.getDeferredSyntaxAllowedAsLiteral() == null) {
                        this.pageInfo.setDeferredSyntaxAllowedAsLiteral(value, n, this.err, true);
                        continue;
                    }
                    if (this.pageInfo.getDeferredSyntaxAllowedAsLiteral().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.page.conflict.deferredsyntaxallowedasliteral", this.pageInfo.getDeferredSyntaxAllowedAsLiteral(), value);
                    continue;
                }
                if (!"trimDirectiveWhitespaces".equals(attr)) continue;
                if (this.pageInfo.getTrimDirectiveWhitespaces() == null) {
                    this.pageInfo.setTrimDirectiveWhitespaces(value, n, this.err, true);
                    continue;
                }
                if (this.pageInfo.getTrimDirectiveWhitespaces().equals(value)) continue;
                this.err.jspError((Node)n, "jsp.error.page.conflict.trimdirectivewhitespaces", this.pageInfo.getTrimDirectiveWhitespaces(), value);
            }
            if (this.pageInfo.getBuffer() == 0 && !this.pageInfo.isAutoFlush()) {
                this.err.jspError(n, "jsp.error.page.badCombo");
            }
            this.pageInfo.addImports(n.getImports());
        }

        @Override
        public void visit(Node.TagDirective n) throws JasperException {
            Attributes attrs = n.getAttributes();
            for (int i = 0; attrs != null && i < attrs.getLength(); ++i) {
                String attr = attrs.getQName(i);
                String value = attrs.getValue(i);
                if ("language".equals(attr)) {
                    if (this.pageInfo.getLanguage(false) == null) {
                        this.pageInfo.setLanguage(value, n, this.err, false);
                        continue;
                    }
                    if (this.pageInfo.getLanguage(false).equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.tag.conflict.language", this.pageInfo.getLanguage(false), value);
                    continue;
                }
                if ("isELIgnored".equals(attr)) {
                    if (this.pageInfo.getIsELIgnored() == null) {
                        this.pageInfo.setIsELIgnored(value, n, this.err, false);
                        continue;
                    }
                    if (this.pageInfo.getIsELIgnored().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.tag.conflict.iselignored", this.pageInfo.getIsELIgnored(), value);
                    continue;
                }
                if ("pageEncoding".equals(attr)) {
                    if (this.pageEncodingSeen) {
                        this.err.jspError(n, "jsp.error.tag.multi.pageencoding");
                    }
                    this.pageEncodingSeen = true;
                    this.compareTagEncodings(value, n);
                    n.getRoot().setPageEncoding(value);
                    continue;
                }
                if ("deferredSyntaxAllowedAsLiteral".equals(attr)) {
                    if (this.pageInfo.getDeferredSyntaxAllowedAsLiteral() == null) {
                        this.pageInfo.setDeferredSyntaxAllowedAsLiteral(value, n, this.err, false);
                        continue;
                    }
                    if (this.pageInfo.getDeferredSyntaxAllowedAsLiteral().equals(value)) continue;
                    this.err.jspError((Node)n, "jsp.error.tag.conflict.deferredsyntaxallowedasliteral", this.pageInfo.getDeferredSyntaxAllowedAsLiteral(), value);
                    continue;
                }
                if (!"trimDirectiveWhitespaces".equals(attr)) continue;
                if (this.pageInfo.getTrimDirectiveWhitespaces() == null) {
                    this.pageInfo.setTrimDirectiveWhitespaces(value, n, this.err, false);
                    continue;
                }
                if (this.pageInfo.getTrimDirectiveWhitespaces().equals(value)) continue;
                this.err.jspError((Node)n, "jsp.error.tag.conflict.trimdirectivewhitespaces", this.pageInfo.getTrimDirectiveWhitespaces(), value);
            }
            this.pageInfo.addImports(n.getImports());
        }

        @Override
        public void visit(Node.AttributeDirective n) throws JasperException {
        }

        @Override
        public void visit(Node.VariableDirective n) throws JasperException {
        }

        private String comparePageEncodings(String pageDirEnc, Node.PageDirective pageDir) throws JasperException {
            Node.Root root = pageDir.getRoot();
            String configEnc = root.getJspConfigPageEncoding();
            if (configEnc != null) {
                if (!(pageDirEnc.equals(configEnc) || pageDirEnc.startsWith("UTF-16") && configEnc.startsWith("UTF-16"))) {
                    this.err.jspError((Node)pageDir, "jsp.error.config_pagedir_encoding_mismatch", configEnc, pageDirEnc);
                } else {
                    return configEnc;
                }
            }
            if (root.isXmlSyntax() && root.isEncodingSpecifiedInProlog() || root.isBomPresent()) {
                String pageEnc = root.getPageEncoding();
                if (!(pageDirEnc.equals(pageEnc) || pageDirEnc.startsWith("UTF-16") && pageEnc.startsWith("UTF-16"))) {
                    this.err.jspError((Node)pageDir, "jsp.error.prolog_pagedir_encoding_mismatch", pageEnc, pageDirEnc);
                } else {
                    return pageEnc;
                }
            }
            return pageDirEnc;
        }

        private void compareTagEncodings(String pageDirEnc, Node.TagDirective pageDir) throws JasperException {
            String pageEnc;
            Node.Root root = pageDir.getRoot();
            if ((root.isXmlSyntax() && root.isEncodingSpecifiedInProlog() || root.isBomPresent()) && !pageDirEnc.equals(pageEnc = root.getPageEncoding()) && (!pageDirEnc.startsWith("UTF-16") || !pageEnc.startsWith("UTF-16"))) {
                this.err.jspError((Node)pageDir, "jsp.error.prolog_pagedir_encoding_mismatch", pageEnc, pageDirEnc);
            }
        }
    }
}

