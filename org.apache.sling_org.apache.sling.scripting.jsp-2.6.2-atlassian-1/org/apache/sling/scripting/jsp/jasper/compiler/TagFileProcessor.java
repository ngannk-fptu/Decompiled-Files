/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.MethodExpression
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  javax.servlet.jsp.tagext.TagVariableInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JasperTagInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.runtime.JspSourceDependent;
import org.apache.sling.scripting.jsp.jasper.servlet.JspServletWrapper;

class TagFileProcessor {
    private Vector<Compiler> tempVector;

    TagFileProcessor() {
    }

    public static TagInfo parseTagFileDirectives(ParserController pc, String name, String path, TagLibraryInfo tagLibInfo) throws JasperException {
        ErrorDispatcher err = pc.getCompiler().getErrorDispatcher();
        Node.Nodes page = null;
        try {
            page = pc.parseTagFileDirectives(path);
        }
        catch (FileNotFoundException e) {
            err.jspError("jsp.error.file.not.found", path);
        }
        catch (IOException e) {
            err.jspError("jsp.error.file.not.found", path);
        }
        TagFileDirectiveVisitor tagFileVisitor = new TagFileDirectiveVisitor(pc.getCompiler(), tagLibInfo, name, path);
        page.visit(tagFileVisitor);
        tagFileVisitor.postCheck();
        return tagFileVisitor.getTagInfo();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadTagFile(Compiler compiler, String tagFilePath, Node.CustomTag n, PageInfo parentPageInfo) throws JasperException {
        JspCompilationContext ctxt = compiler.getCompilationContext();
        JspRuntimeContext rctxt = ctxt.getRuntimeContext();
        rctxt.lockTagFileLoading(tagFilePath);
        try {
            Class<?> tagClazz;
            JspServletWrapper wrapper = rctxt.getWrapper(tagFilePath);
            if (wrapper == null) {
                wrapper = new JspServletWrapper(ctxt.getServletContext(), ctxt.getOptions(), tagFilePath, n.getTagInfo(), ctxt.getRuntimeContext(), ctxt.getTagFileJarUrl(tagFilePath));
                wrapper.getJspEngineContext().setTagFileUrls(ctxt);
                wrapper = rctxt.addWrapper(tagFilePath, wrapper);
            } else {
                wrapper.getJspEngineContext().setTagInfo(n.getTagInfo());
            }
            int tripCount = wrapper.incTripCount();
            try {
                if (tripCount > 0) {
                    String postfix = "_" + String.valueOf(tripCount);
                    String tempTagFilePath = tagFilePath + postfix;
                    JasperTagInfo tempTagInfo = new JasperTagInfo(n.getTagInfo().getTagName(), n.getTagInfo().getTagClassName() + postfix, n.getTagInfo().getBodyContent(), n.getTagInfo().getInfoString(), n.getTagInfo().getTagLibrary(), n.getTagInfo().getTagExtraInfo(), n.getTagInfo().getAttributes(), n.getTagInfo().getDisplayName(), n.getTagInfo().getSmallIcon(), n.getTagInfo().getLargeIcon(), n.getTagInfo().getTagVariableInfos(), ((JasperTagInfo)n.getTagInfo()).getDynamicAttributesMapName());
                    JspServletWrapper tempWrapper = new JspServletWrapper(ctxt.getServletContext(), ctxt.getOptions(), tagFilePath, tempTagInfo, ctxt.getRuntimeContext(), ctxt.getTagFileJarUrl(tempTagFilePath));
                    tempWrapper.getJspEngineContext().setTagFileUrls(ctxt);
                    tagClazz = tempWrapper.loadTagFilePrototype();
                    this.tempVector.add(tempWrapper.getJspEngineContext().getCompiler());
                    String name = JspUtil.getCanonicalName(tagClazz);
                    int underscorePos = name.lastIndexOf(postfix);
                    if (underscorePos > -1) {
                        n.setTagHandlerClassName(name.substring(0, underscorePos));
                    }
                } else {
                    tagClazz = wrapper.loadTagFile();
                }
            }
            finally {
                wrapper.decTripCount();
            }
            try {
                Object tagIns = tagClazz.newInstance();
                if (tagIns instanceof JspSourceDependent) {
                    Iterator iter = ((List)((JspSourceDependent)tagIns).getDependants()).iterator();
                    while (iter.hasNext()) {
                        parentPageInfo.addDependant((String)iter.next());
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            n.setTagHandlerClass(tagClazz);
        }
        finally {
            rctxt.unlockTagFileLoading(tagFilePath);
        }
    }

    public void loadTagFiles(Compiler compiler, Node.Nodes page) throws JasperException {
        this.tempVector = new Vector();
        page.visit(new TagFileLoaderVisitor(compiler));
    }

    public void removeProtoTypeFiles() {
        for (Compiler c : this.tempVector) {
            c.removeGeneratedFiles();
        }
        this.tempVector.clear();
    }

    private class TagFileLoaderVisitor
    extends Node.Visitor {
        private Compiler compiler;
        private PageInfo pageInfo;

        TagFileLoaderVisitor(Compiler compiler) {
            this.compiler = compiler;
            this.pageInfo = compiler.getPageInfo();
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            TagFileInfo tagFileInfo = n.getTagFileInfo();
            if (tagFileInfo != null) {
                String tagFilePath = tagFileInfo.getPath();
                JspCompilationContext ctxt = this.compiler.getCompilationContext();
                if (ctxt.getTagFileJarUrl(tagFilePath) == null) {
                    this.pageInfo.addDependant(tagFilePath);
                }
                TagFileProcessor.this.loadTagFile(this.compiler, tagFilePath, n, this.pageInfo);
            }
            this.visitBody(n);
        }
    }

    private static class TagFileDirectiveVisitor
    extends Node.Visitor {
        private static final JspUtil.ValidAttribute[] tagDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("display-name"), new JspUtil.ValidAttribute("body-content"), new JspUtil.ValidAttribute("dynamic-attributes"), new JspUtil.ValidAttribute("small-icon"), new JspUtil.ValidAttribute("large-icon"), new JspUtil.ValidAttribute("description"), new JspUtil.ValidAttribute("example"), new JspUtil.ValidAttribute("pageEncoding"), new JspUtil.ValidAttribute("language"), new JspUtil.ValidAttribute("import"), new JspUtil.ValidAttribute("deferredSyntaxAllowedAsLiteral"), new JspUtil.ValidAttribute("trimDirectiveWhitespaces"), new JspUtil.ValidAttribute("isELIgnored")};
        private static final JspUtil.ValidAttribute[] attributeDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name", true), new JspUtil.ValidAttribute("required"), new JspUtil.ValidAttribute("fragment"), new JspUtil.ValidAttribute("rtexprvalue"), new JspUtil.ValidAttribute("type"), new JspUtil.ValidAttribute("deferredValue"), new JspUtil.ValidAttribute("deferredValueType"), new JspUtil.ValidAttribute("deferredMethod"), new JspUtil.ValidAttribute("deferredMethodSignature"), new JspUtil.ValidAttribute("description")};
        private static final JspUtil.ValidAttribute[] variableDirectiveAttrs = new JspUtil.ValidAttribute[]{new JspUtil.ValidAttribute("name-given"), new JspUtil.ValidAttribute("name-from-attribute"), new JspUtil.ValidAttribute("alias"), new JspUtil.ValidAttribute("variable-class"), new JspUtil.ValidAttribute("scope"), new JspUtil.ValidAttribute("declare"), new JspUtil.ValidAttribute("description")};
        private ErrorDispatcher err;
        private TagLibraryInfo tagLibInfo;
        private String name = null;
        private String path = null;
        private TagExtraInfo tei = null;
        private String bodycontent = null;
        private String description = null;
        private String displayName = null;
        private String smallIcon = null;
        private String largeIcon = null;
        private String dynamicAttrsMapName;
        private String example = null;
        private Vector attributeVector;
        private Vector variableVector;
        private static final String ATTR_NAME = "the name attribute of the attribute directive";
        private static final String VAR_NAME_GIVEN = "the name-given attribute of the variable directive";
        private static final String VAR_NAME_FROM = "the name-from-attribute attribute of the variable directive";
        private static final String VAR_ALIAS = "the alias attribute of the variable directive";
        private static final String TAG_DYNAMIC = "the dynamic-attributes attribute of the tag directive";
        private HashMap nameTable = new HashMap();
        private HashMap nameFromTable = new HashMap();

        public TagFileDirectiveVisitor(Compiler compiler, TagLibraryInfo tagLibInfo, String name, String path) {
            this.err = compiler.getErrorDispatcher();
            this.tagLibInfo = tagLibInfo;
            this.name = name;
            this.path = path;
            this.attributeVector = new Vector();
            this.variableVector = new Vector();
        }

        @Override
        public void visit(Node.TagDirective n) throws JasperException {
            JspUtil.checkAttributes("Tag directive", n, tagDirectiveAttrs, this.err);
            this.bodycontent = this.checkConflict(n, this.bodycontent, "body-content");
            if (!(this.bodycontent == null || this.bodycontent.equalsIgnoreCase("empty") || this.bodycontent.equalsIgnoreCase("tagdependent") || this.bodycontent.equalsIgnoreCase("scriptless"))) {
                this.err.jspError(n, "jsp.error.tagdirective.badbodycontent", this.bodycontent);
            }
            this.dynamicAttrsMapName = this.checkConflict(n, this.dynamicAttrsMapName, "dynamic-attributes");
            if (this.dynamicAttrsMapName != null) {
                this.checkUniqueName(this.dynamicAttrsMapName, TAG_DYNAMIC, n);
            }
            this.smallIcon = this.checkConflict(n, this.smallIcon, "small-icon");
            this.largeIcon = this.checkConflict(n, this.largeIcon, "large-icon");
            this.description = this.checkConflict(n, this.description, "description");
            this.displayName = this.checkConflict(n, this.displayName, "display-name");
            this.example = this.checkConflict(n, this.example, "example");
        }

        private String checkConflict(Node n, String oldAttrValue, String attr) throws JasperException {
            String result = oldAttrValue;
            String attrValue = n.getAttributeValue(attr);
            if (attrValue != null) {
                if (oldAttrValue != null && !oldAttrValue.equals(attrValue)) {
                    this.err.jspError(n, "jsp.error.tag.conflict.attr", attr, oldAttrValue, attrValue);
                }
                result = attrValue;
            }
            return result;
        }

        @Override
        public void visit(Node.AttributeDirective n) throws JasperException {
            String deferredMethodSignature;
            String deferredValueType;
            JspUtil.checkAttributes("Attribute directive", n, attributeDirectiveAttrs, this.err);
            boolean deferredValue = false;
            boolean deferredValueSpecified = false;
            String deferredValueString = n.getAttributeValue("deferredValue");
            if (deferredValueString != null) {
                deferredValueSpecified = true;
                deferredValue = JspUtil.booleanValue(deferredValueString);
            }
            if ((deferredValueType = n.getAttributeValue("deferredValueType")) != null) {
                if (deferredValueSpecified && !deferredValue) {
                    this.err.jspError(n, "jsp.error.deferredvaluetypewithoutdeferredvalue");
                } else {
                    deferredValue = true;
                }
            } else {
                deferredValueType = deferredValue ? "java.lang.Object" : "java.lang.String";
            }
            boolean deferredMethod = false;
            boolean deferredMethodSpecified = false;
            String deferredMethodString = n.getAttributeValue("deferredMethod");
            if (deferredMethodString != null) {
                deferredMethodSpecified = true;
                deferredMethod = JspUtil.booleanValue(deferredMethodString);
            }
            if ((deferredMethodSignature = n.getAttributeValue("deferredMethodSignature")) != null) {
                if (deferredMethodSpecified && !deferredMethod) {
                    this.err.jspError(n, "jsp.error.deferredmethodsignaturewithoutdeferredmethod");
                } else {
                    deferredMethod = true;
                }
            } else if (deferredMethod) {
                deferredMethodSignature = "void methodname()";
            }
            if (deferredMethod && deferredValue) {
                this.err.jspError(n, "jsp.error.deferredmethodandvalue");
            }
            String attrName = n.getAttributeValue("name");
            boolean required = JspUtil.booleanValue(n.getAttributeValue("required"));
            boolean rtexprvalue = true;
            String rtexprvalueString = n.getAttributeValue("rtexprvalue");
            if (rtexprvalueString != null) {
                rtexprvalue = JspUtil.booleanValue(rtexprvalueString);
            }
            boolean fragment = JspUtil.booleanValue(n.getAttributeValue("fragment"));
            String type = n.getAttributeValue("type");
            if (fragment) {
                if (type != null) {
                    this.err.jspError(n, "jsp.error.fragmentwithtype");
                }
                rtexprvalue = true;
                if (rtexprvalueString != null) {
                    this.err.jspError(n, "jsp.error.frgmentwithrtexprvalue");
                }
            } else {
                if (type == null) {
                    type = "java.lang.String";
                }
                if (deferredValue) {
                    type = ValueExpression.class.getName();
                } else if (deferredMethod) {
                    type = MethodExpression.class.getName();
                }
            }
            if (("2.0".equals(this.tagLibInfo.getRequiredVersion()) || "1.2".equals(this.tagLibInfo.getRequiredVersion())) && (deferredMethodSpecified || deferredMethod || deferredValueSpecified || deferredValue)) {
                this.err.jspError("jsp.error.invalid.version", this.path);
            }
            TagAttributeInfo tagAttributeInfo = new TagAttributeInfo(attrName, required, type, rtexprvalue, fragment, null, deferredValue, deferredMethod, deferredValueType, deferredMethodSignature);
            this.attributeVector.addElement(tagAttributeInfo);
            this.checkUniqueName(attrName, ATTR_NAME, n, tagAttributeInfo);
        }

        @Override
        public void visit(Node.VariableDirective n) throws JasperException {
            String className;
            JspUtil.checkAttributes("Variable directive", n, variableDirectiveAttrs, this.err);
            String nameGiven = n.getAttributeValue("name-given");
            String nameFromAttribute = n.getAttributeValue("name-from-attribute");
            if (nameGiven == null && nameFromAttribute == null) {
                this.err.jspError("jsp.error.variable.either.name");
            }
            if (nameGiven != null && nameFromAttribute != null) {
                this.err.jspError("jsp.error.variable.both.name");
            }
            String alias = n.getAttributeValue("alias");
            if (nameFromAttribute != null && alias == null || nameFromAttribute == null && alias != null) {
                this.err.jspError("jsp.error.variable.alias");
            }
            if ((className = n.getAttributeValue("variable-class")) == null) {
                className = "java.lang.String";
            }
            String declareStr = n.getAttributeValue("declare");
            boolean declare = true;
            if (declareStr != null) {
                declare = JspUtil.booleanValue(declareStr);
            }
            int scope = 0;
            String scopeStr = n.getAttributeValue("scope");
            if (scopeStr != null && !"NESTED".equals(scopeStr)) {
                if ("AT_BEGIN".equals(scopeStr)) {
                    scope = 1;
                } else if ("AT_END".equals(scopeStr)) {
                    scope = 2;
                }
            }
            if (nameFromAttribute != null) {
                nameGiven = alias;
                this.checkUniqueName(nameFromAttribute, VAR_NAME_FROM, n);
                this.checkUniqueName(alias, VAR_ALIAS, n);
            } else {
                this.checkUniqueName(nameGiven, VAR_NAME_GIVEN, n);
            }
            this.variableVector.addElement(new TagVariableInfo(nameGiven, nameFromAttribute, className, declare, scope));
        }

        public Vector getAttributesVector() {
            return this.attributeVector;
        }

        public Vector getVariablesVector() {
            return this.variableVector;
        }

        public String getDynamicAttributesMapName() {
            return this.dynamicAttrsMapName;
        }

        public TagInfo getTagInfo() throws JasperException {
            if (this.name == null) {
                // empty if block
            }
            if (this.bodycontent == null) {
                this.bodycontent = "scriptless";
            }
            String tagClassName = JspUtil.getTagHandlerClassName(this.path, this.err);
            Object[] tagVariableInfos = new TagVariableInfo[this.variableVector.size()];
            this.variableVector.copyInto(tagVariableInfos);
            Object[] tagAttributeInfo = new TagAttributeInfo[this.attributeVector.size()];
            this.attributeVector.copyInto(tagAttributeInfo);
            return new JasperTagInfo(this.name, tagClassName, this.bodycontent, this.description, this.tagLibInfo, this.tei, (TagAttributeInfo[])tagAttributeInfo, this.displayName, this.smallIcon, this.largeIcon, (TagVariableInfo[])tagVariableInfos, this.dynamicAttrsMapName);
        }

        private void checkUniqueName(String name, String type, Node n) throws JasperException {
            this.checkUniqueName(name, type, n, null);
        }

        private void checkUniqueName(String name, String type, Node n, TagAttributeInfo attr) throws JasperException {
            HashMap table = type == VAR_NAME_FROM ? this.nameFromTable : this.nameTable;
            NameEntry nameEntry = (NameEntry)table.get(name);
            if (nameEntry != null) {
                if (type != TAG_DYNAMIC || nameEntry.getType() != TAG_DYNAMIC) {
                    int line = nameEntry.getNode().getStart().getLineNumber();
                    this.err.jspError(n, "jsp.error.tagfile.nameNotUnique", type, nameEntry.getType(), Integer.toString(line));
                }
            } else {
                table.put(name, new NameEntry(type, n, attr));
            }
        }

        void postCheck() throws JasperException {
            for (String nameFrom : this.nameFromTable.keySet()) {
                NameEntry nameEntry = (NameEntry)this.nameTable.get(nameFrom);
                NameEntry nameFromEntry = (NameEntry)this.nameFromTable.get(nameFrom);
                Node nameFromNode = nameFromEntry.getNode();
                if (nameEntry == null) {
                    this.err.jspError(nameFromNode, "jsp.error.tagfile.nameFrom.noAttribute", nameFrom);
                    continue;
                }
                Node node = nameEntry.getNode();
                TagAttributeInfo tagAttr = nameEntry.getTagAttributeInfo();
                if ("java.lang.String".equals(tagAttr.getTypeName()) && tagAttr.isRequired() && !tagAttr.canBeRequestTime()) continue;
                this.err.jspError(nameFromNode, "jsp.error.tagfile.nameFrom.badAttribute", nameFrom, Integer.toString(node.getStart().getLineNumber()));
            }
        }

        static class NameEntry {
            private String type;
            private Node node;
            private TagAttributeInfo attr;

            NameEntry(String type, Node node, TagAttributeInfo attr) {
                this.type = type;
                this.node = node;
                this.attr = attr;
            }

            String getType() {
                return this.type;
            }

            Node getNode() {
                return this.node;
            }

            TagAttributeInfo getTagAttributeInfo() {
                return this.attr;
            }
        }
    }
}

