/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.MethodExpression
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.tagext.JspFragment
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagFileInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JasperTagInfo;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;

class TagFileProcessor {
    private List<Compiler> tempVector;

    TagFileProcessor() {
    }

    public static TagInfo parseTagFileDirectives(ParserController pc, String name, String path, Jar jar, TagLibraryInfo tagLibInfo) throws JasperException {
        ErrorDispatcher err = pc.getCompiler().getErrorDispatcher();
        Node.Nodes page = null;
        try {
            page = pc.parseTagFileDirectives(path, jar);
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
    private Class<?> loadTagFile(Compiler compiler, String tagFilePath, TagInfo tagInfo, PageInfo parentPageInfo) throws JasperException {
        Jar tagJarOriginal = null;
        try (Jar tagJar = null;){
            JspRuntimeContext rctxt;
            if (tagFilePath.startsWith("/META-INF/")) {
                try {
                    tagJar = compiler.getCompilationContext().getTldResourcePath(tagInfo.getTagLibrary().getURI()).openJar();
                }
                catch (IOException ioe) {
                    throw new JasperException(ioe);
                }
            }
            String wrapperUri = tagJar == null ? tagFilePath : tagJar.getURL(tagFilePath);
            JspCompilationContext ctxt = compiler.getCompilationContext();
            JspRuntimeContext jspRuntimeContext = rctxt = ctxt.getRuntimeContext();
            synchronized (jspRuntimeContext) {
                Class<?> clazz;
                block27: {
                    JspServletWrapper wrapper = null;
                    try {
                        Class<?> tagClazz;
                        wrapper = rctxt.getWrapper(wrapperUri);
                        if (wrapper == null) {
                            wrapper = new JspServletWrapper(ctxt.getServletContext(), ctxt.getOptions(), tagFilePath, tagInfo, ctxt.getRuntimeContext(), tagJar);
                            wrapper.getJspEngineContext().setClassLoader(ctxt.getClassLoader());
                            wrapper.getJspEngineContext().setClassPath(ctxt.getClassPath());
                            rctxt.addWrapper(wrapperUri, wrapper);
                        } else {
                            wrapper.getJspEngineContext().setTagInfo(tagInfo);
                            tagJarOriginal = wrapper.getJspEngineContext().getTagFileJar();
                            wrapper.getJspEngineContext().setTagFileJar(tagJar);
                        }
                        int tripCount = wrapper.incTripCount();
                        try {
                            if (tripCount > 0) {
                                JspServletWrapper tempWrapper = new JspServletWrapper(ctxt.getServletContext(), ctxt.getOptions(), tagFilePath, tagInfo, ctxt.getRuntimeContext(), tagJar);
                                tempWrapper.getJspEngineContext().setClassLoader(ctxt.getClassLoader());
                                tempWrapper.getJspEngineContext().setClassPath(ctxt.getClassPath());
                                tagClazz = tempWrapper.loadTagFilePrototype();
                                this.tempVector.add(tempWrapper.getJspEngineContext().getCompiler());
                            } else {
                                tagClazz = wrapper.loadTagFile();
                            }
                        }
                        finally {
                            wrapper.decTripCount();
                        }
                        try {
                            Object tagIns = tagClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                            if (tagIns instanceof JspSourceDependent) {
                                for (Map.Entry<String, Long> entry : ((JspSourceDependent)tagIns).getDependants().entrySet()) {
                                    parentPageInfo.addDependant(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                        catch (ReflectiveOperationException | RuntimeException exception) {
                            // empty catch block
                        }
                        clazz = tagClazz;
                        if (wrapper == null || tagJarOriginal == null) break block27;
                        wrapper.getJspEngineContext().setTagFileJar(tagJarOriginal);
                    }
                    catch (Throwable throwable) {
                        if (wrapper != null && tagJarOriginal != null) {
                            wrapper.getJspEngineContext().setTagFileJar(tagJarOriginal);
                        }
                        throw throwable;
                    }
                }
                return clazz;
            }
        }
    }

    public void loadTagFiles(Compiler compiler, Node.Nodes page) throws JasperException {
        this.tempVector = new ArrayList<Compiler>();
        page.visit(new TagFileLoaderVisitor(compiler));
    }

    public void removeProtoTypeFiles(String classFileName) {
        for (Compiler c : this.tempVector) {
            if (classFileName == null) {
                c.removeGeneratedClassFiles();
                continue;
            }
            if (!classFileName.equals(c.getCompilationContext().getClassFileName())) continue;
            c.removeGeneratedClassFiles();
            this.tempVector.remove(c);
            return;
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
        private String bodycontent = null;
        private String description = null;
        private String displayName = null;
        private String smallIcon = null;
        private String largeIcon = null;
        private String dynamicAttrsMapName;
        private String example = null;
        private List<TagAttributeInfo> attributeList;
        private List<TagVariableInfo> variableList;
        private static final String ATTR_NAME = "the name attribute of the attribute directive";
        private static final String VAR_NAME_GIVEN = "the name-given attribute of the variable directive";
        private static final String VAR_NAME_FROM = "the name-from-attribute attribute of the variable directive";
        private static final String VAR_ALIAS = "the alias attribute of the variable directive";
        private static final String TAG_DYNAMIC = "the dynamic-attributes attribute of the tag directive";
        private Map<String, NameEntry> nameTable = new HashMap<String, NameEntry>();
        private Map<String, NameEntry> nameFromTable = new HashMap<String, NameEntry>();

        TagFileDirectiveVisitor(Compiler compiler, TagLibraryInfo tagLibInfo, String name, String path) {
            this.err = compiler.getErrorDispatcher();
            this.tagLibInfo = tagLibInfo;
            this.name = name;
            this.path = path;
            this.attributeList = new ArrayList<TagAttributeInfo>();
            this.variableList = new ArrayList<TagVariableInfo>();
        }

        @Override
        public void visit(Node.TagDirective n) throws JasperException {
            JspUtil.checkAttributes("Tag directive", n, tagDirectiveAttrs, this.err);
            this.bodycontent = this.checkConflict(n, this.bodycontent, "body-content");
            if (!(this.bodycontent == null || this.bodycontent.equalsIgnoreCase("empty") || this.bodycontent.equalsIgnoreCase("tagdependent") || this.bodycontent.equalsIgnoreCase("scriptless"))) {
                this.err.jspError((Node)n, "jsp.error.tagdirective.badbodycontent", this.bodycontent);
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
                    this.err.jspError((Node)n, "jsp.error.deferredvaluetypewithoutdeferredvalue", new String[0]);
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
                    this.err.jspError((Node)n, "jsp.error.deferredmethodsignaturewithoutdeferredmethod", new String[0]);
                } else {
                    deferredMethod = true;
                }
            } else if (deferredMethod) {
                deferredMethodSignature = "void methodname()";
            }
            if (deferredMethod && deferredValue) {
                this.err.jspError((Node)n, "jsp.error.deferredmethodandvalue", new String[0]);
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
                    this.err.jspError((Node)n, "jsp.error.fragmentwithtype", JspFragment.class.getName());
                }
                rtexprvalue = true;
                if (rtexprvalueString != null) {
                    this.err.jspError((Node)n, "jsp.error.frgmentwithrtexprvalue", new String[0]);
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
            this.attributeList.add(tagAttributeInfo);
            this.checkUniqueName(attrName, ATTR_NAME, n, tagAttributeInfo);
        }

        @Override
        public void visit(Node.VariableDirective n) throws JasperException {
            String className;
            JspUtil.checkAttributes("Variable directive", n, variableDirectiveAttrs, this.err);
            String nameGiven = n.getAttributeValue("name-given");
            String nameFromAttribute = n.getAttributeValue("name-from-attribute");
            if (nameGiven == null && nameFromAttribute == null) {
                this.err.jspError("jsp.error.variable.either.name", new String[0]);
            }
            if (nameGiven != null && nameFromAttribute != null) {
                this.err.jspError("jsp.error.variable.both.name", new String[0]);
            }
            String alias = n.getAttributeValue("alias");
            if (nameFromAttribute != null && alias == null || nameFromAttribute == null && alias != null) {
                this.err.jspError("jsp.error.variable.alias", new String[0]);
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
            this.variableList.add(new TagVariableInfo(nameGiven, nameFromAttribute, className, declare, scope));
        }

        public TagInfo getTagInfo() throws JasperException {
            if (this.name == null) {
                // empty if block
            }
            if (this.bodycontent == null) {
                this.bodycontent = "scriptless";
            }
            String tagClassName = JspUtil.getTagHandlerClassName(this.path, this.tagLibInfo.getReliableURN(), this.err);
            TagVariableInfo[] tagVariableInfos = this.variableList.toArray(new TagVariableInfo[0]);
            TagAttributeInfo[] tagAttributeInfo = this.attributeList.toArray(new TagAttributeInfo[0]);
            return new JasperTagInfo(this.name, tagClassName, this.bodycontent, this.description, this.tagLibInfo, null, tagAttributeInfo, this.displayName, this.smallIcon, this.largeIcon, tagVariableInfos, this.dynamicAttrsMapName);
        }

        private void checkUniqueName(String name, String type, Node n) throws JasperException {
            this.checkUniqueName(name, type, n, null);
        }

        private void checkUniqueName(String name, String type, Node n, TagAttributeInfo attr) throws JasperException {
            Map<String, NameEntry> table = VAR_NAME_FROM.equals(type) ? this.nameFromTable : this.nameTable;
            NameEntry nameEntry = table.get(name);
            if (nameEntry != null) {
                if (!TAG_DYNAMIC.equals(type) || !TAG_DYNAMIC.equals(nameEntry.getType())) {
                    int line = nameEntry.getNode().getStart().getLineNumber();
                    this.err.jspError(n, "jsp.error.tagfile.nameNotUnique", type, nameEntry.getType(), Integer.toString(line));
                }
            } else {
                table.put(name, new NameEntry(type, n, attr));
            }
        }

        void postCheck() throws JasperException {
            for (Map.Entry<String, NameEntry> entry : this.nameFromTable.entrySet()) {
                String key = entry.getKey();
                NameEntry nameEntry = this.nameTable.get(key);
                NameEntry nameFromEntry = entry.getValue();
                Node nameFromNode = nameFromEntry.getNode();
                if (nameEntry == null) {
                    this.err.jspError(nameFromNode, "jsp.error.tagfile.nameFrom.noAttribute", key);
                    continue;
                }
                Node node = nameEntry.getNode();
                TagAttributeInfo tagAttr = nameEntry.getTagAttributeInfo();
                if ("java.lang.String".equals(tagAttr.getTypeName()) && tagAttr.isRequired() && !tagAttr.canBeRequestTime()) continue;
                this.err.jspError(nameFromNode, "jsp.error.tagfile.nameFrom.badAttribute", key, Integer.toString(node.getStart().getLineNumber()));
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
                if (tagFilePath.startsWith("/META-INF/")) {
                    TldResourcePath tldResourcePath = this.compiler.getCompilationContext().getTldResourcePath(tagFileInfo.getTagInfo().getTagLibrary().getURI());
                    try (Jar jar = tldResourcePath.openJar();){
                        if (jar != null) {
                            this.pageInfo.addDependant(jar.getURL(tldResourcePath.getEntryName()), jar.getLastModified(tldResourcePath.getEntryName()));
                            this.pageInfo.addDependant(jar.getURL(tagFilePath.substring(1)), jar.getLastModified(tagFilePath.substring(1)));
                        }
                        this.pageInfo.addDependant(tagFilePath, this.compiler.getCompilationContext().getLastModified(tagFilePath));
                    }
                    catch (IOException ioe) {
                        throw new JasperException(ioe);
                    }
                } else {
                    this.pageInfo.addDependant(tagFilePath, this.compiler.getCompilationContext().getLastModified(tagFilePath));
                }
                Class c = TagFileProcessor.this.loadTagFile(this.compiler, tagFilePath, n.getTagInfo(), this.pageInfo);
                n.setTagHandlerClass(c);
            }
            this.visitBody(n);
        }
    }
}

