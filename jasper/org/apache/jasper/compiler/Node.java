/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ExpressionFactory
 *  javax.servlet.jsp.tagext.BodyTag
 *  javax.servlet.jsp.tagext.DynamicAttributes
 *  javax.servlet.jsp.tagext.IterationTag
 *  javax.servlet.jsp.tagext.JspIdConsumer
 *  javax.servlet.jsp.tagext.SimpleTag
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagData
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  javax.servlet.jsp.tagext.TryCatchFinally
 *  javax.servlet.jsp.tagext.VariableInfo
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.List;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.JspIdConsumer;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.VariableInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ELNode;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.TagConstants;
import org.apache.jasper.compiler.tagplugin.TagPluginContext;
import org.xml.sax.Attributes;

abstract class Node
implements TagConstants {
    private static final VariableInfo[] ZERO_VARIABLE_INFO = new VariableInfo[0];
    protected Attributes attrs;
    protected Attributes taglibAttrs;
    protected Attributes nonTaglibXmlnsAttrs;
    protected Nodes body;
    protected String text;
    protected Mark startMark;
    protected int beginJavaLine;
    protected int endJavaLine;
    protected Node parent;
    protected Nodes namedAttributeNodes;
    protected String qName;
    protected String localName;
    protected String innerClassName;

    Node() {
    }

    Node(Mark start, Node parent) {
        this.startMark = start;
        this.addToParent(parent);
    }

    Node(String qName, String localName, Attributes attrs, Mark start, Node parent) {
        this.qName = qName;
        this.localName = localName;
        this.attrs = attrs;
        this.startMark = start;
        this.addToParent(parent);
    }

    Node(String qName, String localName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
        this.qName = qName;
        this.localName = localName;
        this.attrs = attrs;
        this.nonTaglibXmlnsAttrs = nonTaglibXmlnsAttrs;
        this.taglibAttrs = taglibAttrs;
        this.startMark = start;
        this.addToParent(parent);
    }

    Node(String qName, String localName, String text, Mark start, Node parent) {
        this.qName = qName;
        this.localName = localName;
        this.text = text;
        this.startMark = start;
        this.addToParent(parent);
    }

    public String getQName() {
        return this.qName;
    }

    public String getLocalName() {
        return this.localName;
    }

    public Attributes getAttributes() {
        return this.attrs;
    }

    public Attributes getTaglibAttributes() {
        return this.taglibAttrs;
    }

    public Attributes getNonTaglibXmlnsAttributes() {
        return this.nonTaglibXmlnsAttrs;
    }

    public void setAttributes(Attributes attrs) {
        this.attrs = attrs;
    }

    public String getAttributeValue(String name) {
        return this.attrs == null ? null : this.attrs.getValue(name);
    }

    public String getTextAttribute(String name) {
        String attr = this.getAttributeValue(name);
        if (attr != null) {
            return attr;
        }
        NamedAttribute namedAttribute = this.getNamedAttributeNode(name);
        if (namedAttribute == null) {
            return null;
        }
        return namedAttribute.getText();
    }

    public NamedAttribute getNamedAttributeNode(String name) {
        NamedAttribute result = null;
        Nodes nodes = this.getNamedAttributeNodes();
        int numChildNodes = nodes.size();
        for (int i = 0; i < numChildNodes; ++i) {
            NamedAttribute na = (NamedAttribute)nodes.getNode(i);
            boolean found = false;
            int index = name.indexOf(58);
            found = index != -1 ? na.getName().equals(name) : na.getLocalName().equals(name);
            if (!found) continue;
            result = na;
            break;
        }
        return result;
    }

    public Nodes getNamedAttributeNodes() {
        if (this.namedAttributeNodes != null) {
            return this.namedAttributeNodes;
        }
        Nodes result = new Nodes();
        Nodes nodes = this.getBody();
        if (nodes != null) {
            int numChildNodes = nodes.size();
            for (int i = 0; i < numChildNodes; ++i) {
                Node n = nodes.getNode(i);
                if (n instanceof NamedAttribute) {
                    result.add(n);
                    continue;
                }
                if (!(n instanceof Comment)) break;
            }
        }
        this.namedAttributeNodes = result;
        return result;
    }

    public Nodes getBody() {
        return this.body;
    }

    public void setBody(Nodes body) {
        this.body = body;
    }

    public String getText() {
        return this.text;
    }

    public Mark getStart() {
        return this.startMark;
    }

    public Node getParent() {
        return this.parent;
    }

    public int getBeginJavaLine() {
        return this.beginJavaLine;
    }

    public void setBeginJavaLine(int begin) {
        this.beginJavaLine = begin;
    }

    public int getEndJavaLine() {
        return this.endJavaLine;
    }

    public void setEndJavaLine(int end) {
        this.endJavaLine = end;
    }

    public Root getRoot() {
        Node n = this;
        while (!(n instanceof Root)) {
            n = n.getParent();
        }
        return (Root)n;
    }

    public String getInnerClassName() {
        return this.innerClassName;
    }

    public void setInnerClassName(String icn) {
        this.innerClassName = icn;
    }

    abstract void accept(Visitor var1) throws JasperException;

    private void addToParent(Node parent) {
        if (parent != null) {
            this.parent = parent;
            Nodes parentBody = parent.getBody();
            if (parentBody == null) {
                parentBody = new Nodes();
                parent.setBody(parentBody);
            }
            parentBody.add(this);
        }
    }

    public static class NamedAttribute
    extends ChildInfoBase {
        private String temporaryVariableName;
        private boolean trim = true;
        private JspAttribute omit;
        private final String name;
        private String localName;
        private String prefix;

        NamedAttribute(Attributes attrs, Mark start, Node parent) {
            this("jsp:attribute", attrs, null, null, start, parent);
        }

        NamedAttribute(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "attribute", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
            if ("false".equals(this.getAttributeValue("trim"))) {
                this.trim = false;
            }
            this.name = this.getAttributeValue("name");
            if (this.name != null) {
                this.localName = this.name;
                int index = this.name.indexOf(58);
                if (index != -1) {
                    this.prefix = this.name.substring(0, index);
                    this.localName = this.name.substring(index + 1);
                }
            }
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getLocalName() {
            return this.localName;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public boolean isTrim() {
            return this.trim;
        }

        public void setOmit(JspAttribute omit) {
            this.omit = omit;
        }

        public JspAttribute getOmit() {
            return this.omit;
        }

        public String getTemporaryVariableName() {
            if (this.temporaryVariableName == null) {
                this.temporaryVariableName = this.getRoot().nextTemporaryVariableName();
            }
            return this.temporaryVariableName;
        }

        @Override
        public String getText() {
            String text = "";
            if (this.getBody() != null) {
                class AttributeVisitor
                extends Visitor {
                    private String attrValue = null;

                    AttributeVisitor() {
                    }

                    @Override
                    public void visit(TemplateText txt) {
                        this.attrValue = txt.getText();
                    }

                    public String getAttrValue() {
                        return this.attrValue;
                    }
                }
                AttributeVisitor attributeVisitor = new AttributeVisitor();
                try {
                    this.getBody().visit(attributeVisitor);
                }
                catch (JasperException jasperException) {
                    // empty catch block
                }
                text = attributeVisitor.getAttrValue();
            }
            return text;
        }
    }

    public static class Nodes {
        private final List<Node> list;
        private Root root;
        private boolean generatedInBuffer;

        Nodes() {
            this.list = new ArrayList<Node>();
        }

        Nodes(Root root) {
            this.root = root;
            this.list = new ArrayList<Node>();
            this.list.add(root);
        }

        public void add(Node n) {
            this.list.add(n);
            this.root = null;
        }

        public void remove(Node n) {
            this.list.remove(n);
        }

        public void visit(Visitor v) throws JasperException {
            for (Node n : this.list) {
                n.accept(v);
            }
        }

        public int size() {
            return this.list.size();
        }

        public Node getNode(int index) {
            Node n = null;
            try {
                n = this.list.get(index);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                // empty catch block
            }
            return n;
        }

        public Root getRoot() {
            return this.root;
        }

        public boolean isGeneratedInBuffer() {
            return this.generatedInBuffer;
        }

        public void setGeneratedInBuffer(boolean g) {
            this.generatedInBuffer = g;
        }
    }

    public static class Comment
    extends Node {
        Comment(String text, Mark start, Node parent) {
            super(null, null, text, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class Root
    extends Node {
        private final Root parentRoot;
        private final boolean isXmlSyntax;
        private String pageEnc;
        private String jspConfigPageEnc;
        private boolean isDefaultPageEncoding;
        private boolean isEncodingSpecifiedInProlog;
        private boolean isBomPresent;
        private int tempSequenceNumber = 0;

        Root(Mark start, Node parent, boolean isXmlSyntax) {
            super(start, parent);
            Node r;
            this.isXmlSyntax = isXmlSyntax;
            this.qName = "jsp:root";
            this.localName = "root";
            for (r = parent; r != null && !(r instanceof Root); r = r.getParent()) {
            }
            this.parentRoot = (Root)r;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public boolean isXmlSyntax() {
            return this.isXmlSyntax;
        }

        public void setJspConfigPageEncoding(String enc) {
            this.jspConfigPageEnc = enc;
        }

        public String getJspConfigPageEncoding() {
            return this.jspConfigPageEnc;
        }

        public void setPageEncoding(String enc) {
            this.pageEnc = enc;
        }

        public String getPageEncoding() {
            return this.pageEnc;
        }

        public void setIsDefaultPageEncoding(boolean isDefault) {
            this.isDefaultPageEncoding = isDefault;
        }

        public boolean isDefaultPageEncoding() {
            return this.isDefaultPageEncoding;
        }

        public void setIsEncodingSpecifiedInProlog(boolean isSpecified) {
            this.isEncodingSpecifiedInProlog = isSpecified;
        }

        public boolean isEncodingSpecifiedInProlog() {
            return this.isEncodingSpecifiedInProlog;
        }

        public void setIsBomPresent(boolean isBom) {
            this.isBomPresent = isBom;
        }

        public boolean isBomPresent() {
            return this.isBomPresent;
        }

        public String nextTemporaryVariableName() {
            if (this.parentRoot == null) {
                return Constants.TEMP_VARIABLE_NAME_PREFIX + this.tempSequenceNumber++;
            }
            return this.parentRoot.nextTemporaryVariableName();
        }
    }

    public static class Visitor {
        protected void doVisit(Node n) throws JasperException {
        }

        protected void visitBody(Node n) throws JasperException {
            if (n.getBody() != null) {
                n.getBody().visit(this);
            }
        }

        public void visit(Root n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(JspRoot n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(PageDirective n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(TagDirective n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(IncludeDirective n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(TaglibDirective n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(AttributeDirective n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(VariableDirective n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(Comment n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(Declaration n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(Expression n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(Scriptlet n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(ELExpression n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(IncludeAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(ForwardAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(GetProperty n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(SetProperty n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(ParamAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(ParamsAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(FallBackAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(UseBean n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(PlugIn n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(CustomTag n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(UninterpretedTag n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(JspElement n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(JspText n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(NamedAttribute n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(JspBody n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(InvokeAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(DoBodyAction n) throws JasperException {
            this.doVisit(n);
            this.visitBody(n);
        }

        public void visit(TemplateText n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(JspOutput n) throws JasperException {
            this.doVisit(n);
        }

        public void visit(AttributeGenerator n) throws JasperException {
            this.doVisit(n);
        }
    }

    public static class JspAttribute {
        private final String qName;
        private final String uri;
        private final String localName;
        private final String value;
        private final boolean expression;
        private final boolean dynamic;
        private final ELNode.Nodes el;
        private final TagAttributeInfo tai;
        private final boolean namedAttribute;
        private final NamedAttribute namedAttributeNode;

        JspAttribute(TagAttributeInfo tai, String qName, String uri, String localName, String value, boolean expr, ELNode.Nodes el, boolean dyn) {
            this.qName = qName;
            this.uri = uri;
            this.localName = localName;
            this.value = value;
            this.namedAttributeNode = null;
            this.expression = expr;
            this.el = el;
            this.dynamic = dyn;
            this.namedAttribute = false;
            this.tai = tai;
        }

        public void validateEL(ExpressionFactory ef, ELContext ctx) throws ELException {
            if (this.el != null) {
                ef.createValueExpression(ctx, this.value, String.class);
            }
        }

        JspAttribute(NamedAttribute na, TagAttributeInfo tai, boolean dyn) {
            this.qName = na.getName();
            this.localName = na.getLocalName();
            this.value = null;
            this.namedAttributeNode = na;
            this.expression = false;
            this.el = null;
            this.dynamic = dyn;
            this.namedAttribute = true;
            this.tai = tai;
            this.uri = null;
        }

        public String getName() {
            return this.qName;
        }

        public String getLocalName() {
            return this.localName;
        }

        public String getURI() {
            return this.uri;
        }

        public TagAttributeInfo getTagAttributeInfo() {
            return this.tai;
        }

        public boolean isDeferredInput() {
            return this.tai != null ? this.tai.isDeferredValue() : false;
        }

        public boolean isDeferredMethodInput() {
            return this.tai != null ? this.tai.isDeferredMethod() : false;
        }

        public String getExpectedTypeName() {
            if (this.tai != null) {
                int rti;
                String m;
                if (this.isDeferredInput()) {
                    return this.tai.getExpectedTypeName();
                }
                if (this.isDeferredMethodInput() && (m = this.tai.getMethodSignature()) != null && (rti = m.trim().indexOf(32)) > 0) {
                    return m.substring(0, rti).trim();
                }
            }
            return "java.lang.Object";
        }

        public String[] getParameterTypeNames() {
            String m;
            if (this.tai != null && this.isDeferredMethodInput() && (m = this.tai.getMethodSignature()) != null) {
                m = m.trim();
                m = m.substring(m.indexOf(40) + 1);
                if ((m = m.substring(0, m.length() - 1)).trim().length() > 0) {
                    String[] p = m.split(",");
                    for (int i = 0; i < p.length; ++i) {
                        p[i] = p[i].trim();
                    }
                    return p;
                }
            }
            return new String[0];
        }

        public String getValue() {
            return this.value;
        }

        public NamedAttribute getNamedAttributeNode() {
            return this.namedAttributeNode;
        }

        public boolean isExpression() {
            return this.expression;
        }

        public boolean isNamedAttribute() {
            return this.namedAttribute;
        }

        public boolean isELInterpreterInput() {
            return this.el != null || this.isDeferredInput() || this.isDeferredMethodInput();
        }

        public boolean isLiteral() {
            return !this.expression && this.el == null && !this.namedAttribute;
        }

        public boolean isDynamic() {
            return this.dynamic;
        }

        public ELNode.Nodes getEL() {
            return this.el;
        }
    }

    public static class TemplateText
    extends Node {
        private ArrayList<Integer> extraSmap = null;

        TemplateText(String text, Mark start, Node parent) {
            super(null, null, text, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void ltrim() {
            int index;
            for (index = 0; index < this.text.length() && this.text.charAt(index) <= ' '; ++index) {
            }
            this.text = this.text.substring(index);
        }

        public void setText(String text) {
            this.text = text;
        }

        public void rtrim() {
            int index;
            for (index = this.text.length(); index > 0 && this.text.charAt(index - 1) <= ' '; --index) {
            }
            this.text = this.text.substring(0, index);
        }

        public boolean isAllSpace() {
            boolean isAllSpace = true;
            for (int i = 0; i < this.text.length(); ++i) {
                if (Character.isWhitespace(this.text.charAt(i))) continue;
                isAllSpace = false;
                break;
            }
            return isAllSpace;
        }

        public void addSmap(int srcLine) {
            if (this.extraSmap == null) {
                this.extraSmap = new ArrayList();
            }
            this.extraSmap.add(srcLine);
        }

        public ArrayList<Integer> getExtraSmap() {
            return this.extraSmap;
        }
    }

    public static class JspBody
    extends ChildInfoBase {
        JspBody(Mark start, Node parent) {
            this("jsp:body", (Attributes)null, (Attributes)null, start, parent);
        }

        JspBody(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "body", null, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class JspText
    extends Node {
        JspText(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "text", null, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class AttributeGenerator
    extends Node {
        private String name;
        private CustomTag tag;

        AttributeGenerator(Mark start, String name, CustomTag tag) {
            super(start, null);
            this.name = name;
            this.tag = tag;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getName() {
            return this.name;
        }

        public CustomTag getTag() {
            return this.tag;
        }
    }

    public static class CustomTag
    extends ChildInfoBase {
        private final String uri;
        private final String prefix;
        private JspAttribute[] jspAttrs;
        private TagData tagData;
        private String tagHandlerPoolName;
        private final TagInfo tagInfo;
        private final TagFileInfo tagFileInfo;
        private Class<?> tagHandlerClass;
        private VariableInfo[] varInfos;
        private final int customNestingLevel;
        private final boolean implementsIterationTag;
        private final boolean implementsBodyTag;
        private final boolean implementsTryCatchFinally;
        private final boolean implementsJspIdConsumer;
        private final boolean implementsSimpleTag;
        private final boolean implementsDynamicAttributes;
        private List<Object> atBeginScriptingVars;
        private List<Object> atEndScriptingVars;
        private List<Object> nestedScriptingVars;
        private CustomTag customTagParent;
        private Integer numCount;
        private boolean useTagPlugin;
        private TagPluginContext tagPluginContext;
        private Nodes atSTag;
        private Nodes atETag;

        CustomTag(String qName, String prefix, String localName, String uri, Attributes attrs, Mark start, Node parent, TagInfo tagInfo, Class<?> tagHandlerClass) {
            this(qName, prefix, localName, uri, attrs, null, null, start, parent, tagInfo, tagHandlerClass);
        }

        CustomTag(String qName, String prefix, String localName, String uri, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent, TagInfo tagInfo, Class<?> tagHandlerClass) {
            super(qName, localName, attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
            this.uri = uri;
            this.prefix = prefix;
            this.tagInfo = tagInfo;
            this.tagFileInfo = null;
            this.tagHandlerClass = tagHandlerClass;
            this.customNestingLevel = this.makeCustomNestingLevel();
            this.implementsIterationTag = IterationTag.class.isAssignableFrom(tagHandlerClass);
            this.implementsBodyTag = BodyTag.class.isAssignableFrom(tagHandlerClass);
            this.implementsTryCatchFinally = TryCatchFinally.class.isAssignableFrom(tagHandlerClass);
            this.implementsSimpleTag = SimpleTag.class.isAssignableFrom(tagHandlerClass);
            this.implementsDynamicAttributes = DynamicAttributes.class.isAssignableFrom(tagHandlerClass);
            this.implementsJspIdConsumer = JspIdConsumer.class.isAssignableFrom(tagHandlerClass);
        }

        CustomTag(String qName, String prefix, String localName, String uri, Attributes attrs, Mark start, Node parent, TagFileInfo tagFileInfo) {
            this(qName, prefix, localName, uri, attrs, null, null, start, parent, tagFileInfo);
        }

        CustomTag(String qName, String prefix, String localName, String uri, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent, TagFileInfo tagFileInfo) {
            super(qName, localName, attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
            this.uri = uri;
            this.prefix = prefix;
            this.tagFileInfo = tagFileInfo;
            this.tagInfo = tagFileInfo.getTagInfo();
            this.customNestingLevel = this.makeCustomNestingLevel();
            this.implementsIterationTag = false;
            this.implementsBodyTag = false;
            this.implementsTryCatchFinally = false;
            this.implementsSimpleTag = true;
            this.implementsJspIdConsumer = false;
            this.implementsDynamicAttributes = this.tagInfo.hasDynamicAttributes();
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getURI() {
            return this.uri;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setJspAttributes(JspAttribute[] jspAttrs) {
            this.jspAttrs = jspAttrs;
        }

        public JspAttribute[] getJspAttributes() {
            return this.jspAttrs;
        }

        public void setTagData(TagData tagData) {
            this.tagData = tagData;
            this.varInfos = this.tagInfo.getVariableInfo(tagData);
            if (this.varInfos == null) {
                this.varInfos = ZERO_VARIABLE_INFO;
            }
        }

        public TagData getTagData() {
            return this.tagData;
        }

        public void setTagHandlerPoolName(String s) {
            this.tagHandlerPoolName = s;
        }

        public String getTagHandlerPoolName() {
            return this.tagHandlerPoolName;
        }

        public TagInfo getTagInfo() {
            return this.tagInfo;
        }

        public TagFileInfo getTagFileInfo() {
            return this.tagFileInfo;
        }

        public boolean isTagFile() {
            return this.tagFileInfo != null;
        }

        public Class<?> getTagHandlerClass() {
            return this.tagHandlerClass;
        }

        public void setTagHandlerClass(Class<?> hc) {
            this.tagHandlerClass = hc;
        }

        public boolean implementsIterationTag() {
            return this.implementsIterationTag;
        }

        public boolean implementsBodyTag() {
            return this.implementsBodyTag;
        }

        public boolean implementsTryCatchFinally() {
            return this.implementsTryCatchFinally;
        }

        public boolean implementsJspIdConsumer() {
            return this.implementsJspIdConsumer;
        }

        public boolean implementsSimpleTag() {
            return this.implementsSimpleTag;
        }

        public boolean implementsDynamicAttributes() {
            return this.implementsDynamicAttributes;
        }

        public TagVariableInfo[] getTagVariableInfos() {
            return this.tagInfo.getTagVariableInfos();
        }

        public VariableInfo[] getVariableInfos() {
            return this.varInfos;
        }

        public void setCustomTagParent(CustomTag n) {
            this.customTagParent = n;
        }

        public CustomTag getCustomTagParent() {
            return this.customTagParent;
        }

        public void setNumCount(Integer count) {
            this.numCount = count;
        }

        public Integer getNumCount() {
            return this.numCount;
        }

        public void setScriptingVars(List<Object> vec, int scope) {
            switch (scope) {
                case 1: {
                    this.atBeginScriptingVars = vec;
                    break;
                }
                case 2: {
                    this.atEndScriptingVars = vec;
                    break;
                }
                case 0: {
                    this.nestedScriptingVars = vec;
                    break;
                }
                default: {
                    throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.varscope", scope));
                }
            }
        }

        public List<Object> getScriptingVars(int scope) {
            List<Object> vec = null;
            switch (scope) {
                case 1: {
                    vec = this.atBeginScriptingVars;
                    break;
                }
                case 2: {
                    vec = this.atEndScriptingVars;
                    break;
                }
                case 0: {
                    vec = this.nestedScriptingVars;
                    break;
                }
                default: {
                    throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.varscope", scope));
                }
            }
            return vec;
        }

        public int getCustomNestingLevel() {
            return this.customNestingLevel;
        }

        public boolean checkIfAttributeIsJspFragment(String name) {
            TagAttributeInfo[] attributes;
            boolean result = false;
            for (TagAttributeInfo attribute : attributes = this.tagInfo.getAttributes()) {
                if (!attribute.getName().equals(name) || !attribute.isFragment()) continue;
                result = true;
                break;
            }
            return result;
        }

        public void setUseTagPlugin(boolean use) {
            this.useTagPlugin = use;
        }

        public boolean useTagPlugin() {
            return this.useTagPlugin;
        }

        public void setTagPluginContext(TagPluginContext tagPluginContext) {
            this.tagPluginContext = tagPluginContext;
        }

        public TagPluginContext getTagPluginContext() {
            return this.tagPluginContext;
        }

        public void setAtSTag(Nodes sTag) {
            this.atSTag = sTag;
        }

        public Nodes getAtSTag() {
            return this.atSTag;
        }

        public void setAtETag(Nodes eTag) {
            this.atETag = eTag;
        }

        public Nodes getAtETag() {
            return this.atETag;
        }

        private int makeCustomNestingLevel() {
            int n = 0;
            Node p = this.parent;
            while (p != null) {
                if (p instanceof CustomTag && this.qName.equals(((CustomTag)p).qName)) {
                    ++n;
                }
                p = p.parent;
            }
            return n;
        }

        public boolean hasEmptyBody() {
            boolean hasEmptyBody = true;
            Nodes nodes = this.getBody();
            if (nodes != null) {
                int numChildNodes = nodes.size();
                for (int i = 0; i < numChildNodes; ++i) {
                    Node n = nodes.getNode(i);
                    if (n instanceof NamedAttribute) continue;
                    if (n instanceof JspBody) {
                        hasEmptyBody = n.getBody() == null;
                        break;
                    }
                    hasEmptyBody = false;
                    break;
                }
            }
            return hasEmptyBody;
        }
    }

    public static abstract class ChildInfoBase
    extends Node {
        private final ChildInfo childInfo = new ChildInfo();

        ChildInfoBase(String qName, String localName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, localName, attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        public ChildInfo getChildInfo() {
            return this.childInfo;
        }
    }

    public static class ChildInfo {
        private boolean scriptless;
        private boolean hasUseBean;
        private boolean hasIncludeAction;
        private boolean hasParamAction;
        private boolean hasSetProperty;
        private boolean hasScriptingVars;

        public void setScriptless(boolean s) {
            this.scriptless = s;
        }

        public boolean isScriptless() {
            return this.scriptless;
        }

        public void setHasUseBean(boolean u) {
            this.hasUseBean = u;
        }

        public boolean hasUseBean() {
            return this.hasUseBean;
        }

        public void setHasIncludeAction(boolean i) {
            this.hasIncludeAction = i;
        }

        public boolean hasIncludeAction() {
            return this.hasIncludeAction;
        }

        public void setHasParamAction(boolean i) {
            this.hasParamAction = i;
        }

        public boolean hasParamAction() {
            return this.hasParamAction;
        }

        public void setHasSetProperty(boolean s) {
            this.hasSetProperty = s;
        }

        public boolean hasSetProperty() {
            return this.hasSetProperty;
        }

        public void setHasScriptingVars(boolean s) {
            this.hasScriptingVars = s;
        }

        public boolean hasScriptingVars() {
            return this.hasScriptingVars;
        }
    }

    public static class JspOutput
    extends Node {
        JspOutput(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "output", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class JspElement
    extends Node {
        private JspAttribute[] jspAttrs;
        private JspAttribute nameAttr;

        JspElement(Attributes attrs, Mark start, Node parent) {
            this("jsp:element", attrs, null, null, start, parent);
        }

        JspElement(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "element", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setJspAttributes(JspAttribute[] jspAttrs) {
            this.jspAttrs = jspAttrs;
        }

        public JspAttribute[] getJspAttributes() {
            return this.jspAttrs;
        }

        public void setNameAttribute(JspAttribute nameAttr) {
            this.nameAttr = nameAttr;
        }

        public JspAttribute getNameAttribute() {
            return this.nameAttr;
        }
    }

    public static class UninterpretedTag
    extends Node {
        private JspAttribute[] jspAttrs;

        UninterpretedTag(String qName, String localName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, localName, attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setJspAttributes(JspAttribute[] jspAttrs) {
            this.jspAttrs = jspAttrs;
        }

        public JspAttribute[] getJspAttributes() {
            return this.jspAttrs;
        }
    }

    public static class PlugIn
    extends Node {
        private JspAttribute width;
        private JspAttribute height;

        PlugIn(Attributes attrs, Mark start, Node parent) {
            this("jsp:plugin", attrs, null, null, start, parent);
        }

        PlugIn(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "plugin", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setHeight(JspAttribute height) {
            this.height = height;
        }

        public void setWidth(JspAttribute width) {
            this.width = width;
        }

        public JspAttribute getHeight() {
            return this.height;
        }

        public JspAttribute getWidth() {
            return this.width;
        }
    }

    public static class UseBean
    extends Node {
        private JspAttribute beanName;

        UseBean(Attributes attrs, Mark start, Node parent) {
            this("jsp:useBean", attrs, null, null, start, parent);
        }

        UseBean(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "useBean", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setBeanName(JspAttribute beanName) {
            this.beanName = beanName;
        }

        public JspAttribute getBeanName() {
            return this.beanName;
        }
    }

    public static class SetProperty
    extends Node {
        private JspAttribute value;

        SetProperty(Attributes attrs, Mark start, Node parent) {
            this("jsp:setProperty", attrs, null, null, start, parent);
        }

        SetProperty(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "setProperty", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setValue(JspAttribute value) {
            this.value = value;
        }

        public JspAttribute getValue() {
            return this.value;
        }
    }

    public static class GetProperty
    extends Node {
        GetProperty(Attributes attrs, Mark start, Node parent) {
            this("jsp:getProperty", attrs, null, null, start, parent);
        }

        GetProperty(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "getProperty", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class ForwardAction
    extends Node {
        private JspAttribute page;

        ForwardAction(Attributes attrs, Mark start, Node parent) {
            this("jsp:forward", attrs, null, null, start, parent);
        }

        ForwardAction(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "forward", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setPage(JspAttribute page) {
            this.page = page;
        }

        public JspAttribute getPage() {
            return this.page;
        }
    }

    public static class IncludeAction
    extends Node {
        private JspAttribute page;

        IncludeAction(Attributes attrs, Mark start, Node parent) {
            this("jsp:include", attrs, null, null, start, parent);
        }

        IncludeAction(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "include", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setPage(JspAttribute page) {
            this.page = page;
        }

        public JspAttribute getPage() {
            return this.page;
        }
    }

    public static class FallBackAction
    extends Node {
        FallBackAction(Mark start, Node parent) {
            this("jsp:fallback", (Attributes)null, (Attributes)null, start, parent);
        }

        FallBackAction(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "fallback", null, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class ParamsAction
    extends Node {
        ParamsAction(Mark start, Node parent) {
            this("jsp:params", (Attributes)null, (Attributes)null, start, parent);
        }

        ParamsAction(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "params", null, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class ParamAction
    extends Node {
        private JspAttribute value;

        ParamAction(Attributes attrs, Mark start, Node parent) {
            this("jsp:param", attrs, null, null, start, parent);
        }

        ParamAction(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "param", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setValue(JspAttribute value) {
            this.value = value;
        }

        public JspAttribute getValue() {
            return this.value;
        }
    }

    public static class ELExpression
    extends Node {
        private ELNode.Nodes el;
        private final char type;

        ELExpression(char type, String text, Mark start, Node parent) {
            super(null, null, text, start, parent);
            this.type = type;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void setEL(ELNode.Nodes el) {
            this.el = el;
        }

        public ELNode.Nodes getEL() {
            return this.el;
        }

        public char getType() {
            return this.type;
        }
    }

    public static class Scriptlet
    extends ScriptingElement {
        Scriptlet(String text, Mark start, Node parent) {
            super("jsp:scriptlet", "scriptlet", text, start, parent);
        }

        Scriptlet(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "scriptlet", nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class Expression
    extends ScriptingElement {
        Expression(String text, Mark start, Node parent) {
            super("jsp:expression", "expression", text, start, parent);
        }

        Expression(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "expression", nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class Declaration
    extends ScriptingElement {
        Declaration(String text, Mark start, Node parent) {
            super("jsp:declaration", "declaration", text, start, parent);
        }

        Declaration(String qName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "declaration", nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static abstract class ScriptingElement
    extends Node {
        ScriptingElement(String qName, String localName, String text, Mark start, Node parent) {
            super(qName, localName, text, start, parent);
        }

        ScriptingElement(String qName, String localName, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, localName, null, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public String getText() {
            String ret = this.text;
            if (ret == null) {
                if (this.body != null) {
                    StringBuilder buf = new StringBuilder();
                    for (int i = 0; i < this.body.size(); ++i) {
                        buf.append(this.body.getNode(i).getText());
                    }
                    ret = buf.toString();
                } else {
                    ret = "";
                }
            }
            return ret;
        }

        @Override
        public Mark getStart() {
            if (this.text == null && this.body != null && this.body.size() > 0) {
                return this.body.getNode(0).getStart();
            }
            return super.getStart();
        }
    }

    public static class DoBodyAction
    extends Node {
        DoBodyAction(Attributes attrs, Mark start, Node parent) {
            this("jsp:doBody", attrs, null, null, start, parent);
        }

        DoBodyAction(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "doBody", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class InvokeAction
    extends Node {
        InvokeAction(Attributes attrs, Mark start, Node parent) {
            this("jsp:invoke", attrs, null, null, start, parent);
        }

        InvokeAction(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "invoke", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class VariableDirective
    extends Node {
        VariableDirective(Attributes attrs, Mark start, Node parent) {
            this("jsp:directive.variable", attrs, null, null, start, parent);
        }

        VariableDirective(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "directive.variable", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class AttributeDirective
    extends Node {
        AttributeDirective(Attributes attrs, Mark start, Node parent) {
            this("jsp:directive.attribute", attrs, null, null, start, parent);
        }

        AttributeDirective(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "directive.attribute", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class TagDirective
    extends Node {
        private final List<String> imports = new ArrayList<String>();

        TagDirective(Attributes attrs, Mark start, Node parent) {
            this("jsp:directive.tag", attrs, null, null, start, parent);
        }

        TagDirective(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "directive.tag", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void addImport(String value) {
            int index;
            int start = 0;
            while ((index = value.indexOf(44, start)) != -1) {
                this.imports.add(value.substring(start, index).trim());
                start = index + 1;
            }
            if (start == 0) {
                this.imports.add(value.trim());
            } else {
                this.imports.add(value.substring(start).trim());
            }
        }

        public List<String> getImports() {
            return this.imports;
        }
    }

    public static class TaglibDirective
    extends Node {
        TaglibDirective(Attributes attrs, Mark start, Node parent) {
            super("jsp:taglib", "taglib", attrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class IncludeDirective
    extends Node {
        IncludeDirective(Attributes attrs, Mark start, Node parent) {
            this("jsp:directive.include", attrs, null, null, start, parent);
        }

        IncludeDirective(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "directive.include", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }

    public static class PageDirective
    extends Node {
        private final List<String> imports = new ArrayList<String>();

        PageDirective(Attributes attrs, Mark start, Node parent) {
            this("jsp:directive.page", attrs, null, null, start, parent);
        }

        PageDirective(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "directive.page", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public void addImport(String value) {
            int index;
            int start = 0;
            while ((index = value.indexOf(44, start)) != -1) {
                this.imports.add(this.validateImport(value.substring(start, index)));
                start = index + 1;
            }
            if (start == 0) {
                this.imports.add(this.validateImport(value));
            } else {
                this.imports.add(this.validateImport(value.substring(start)));
            }
        }

        public List<String> getImports() {
            return this.imports;
        }

        private String validateImport(String importEntry) {
            if (importEntry.indexOf(59) > -1) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.import"));
            }
            return importEntry.trim();
        }
    }

    public static class JspRoot
    extends Node {
        JspRoot(String qName, Attributes attrs, Attributes nonTaglibXmlnsAttrs, Attributes taglibAttrs, Mark start, Node parent) {
            super(qName, "root", attrs, nonTaglibXmlnsAttrs, taglibAttrs, start, parent);
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }
    }
}

