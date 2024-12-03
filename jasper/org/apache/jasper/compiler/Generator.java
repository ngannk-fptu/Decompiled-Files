/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.MethodExpression
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagVariableInfo
 *  javax.servlet.jsp.tagext.VariableInfo
 *  org.apache.el.util.JreCompat
 */
package org.apache.jasper.compiler;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import org.apache.el.util.JreCompat;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.BeanRepository;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ELInterpreter;
import org.apache.jasper.compiler.ELInterpreterFactory;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JasperTagInfo;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ServletWriter;
import org.apache.jasper.compiler.StringInterpreter;
import org.apache.jasper.compiler.StringInterpreterFactory;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.xml.sax.Attributes;

class Generator {
    private static final Class<?>[] OBJECT_CLASS = new Class[]{Object.class};
    private static final String VAR_EXPRESSIONFACTORY = System.getProperty("org.apache.jasper.compiler.Generator.VAR_EXPRESSIONFACTORY", "_el_expressionfactory");
    private static final String VAR_INSTANCEMANAGER = System.getProperty("org.apache.jasper.compiler.Generator.VAR_INSTANCEMANAGER", "_jsp_instancemanager");
    private static final boolean POOL_TAGS_WITH_EXTENDS = Boolean.getBoolean("org.apache.jasper.compiler.Generator.POOL_TAGS_WITH_EXTENDS");
    private static final boolean STRICT_GET_PROPERTY = Boolean.parseBoolean(System.getProperty("org.apache.jasper.compiler.Generator.STRICT_GET_PROPERTY", "true"));
    private static final Pattern PRE_TAG_PATTERN = Pattern.compile("(?s).*(<pre>|</pre>).*");
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile("(\\s*(\\n|\\r)+\\s*)");
    private final ServletWriter out;
    private final ArrayList<GenBuffer> methodsBuffered;
    private final FragmentHelperClass fragmentHelperClass;
    private final ErrorDispatcher err;
    private final BeanRepository beanInfo;
    private final Set<String> varInfoNames;
    private final JspCompilationContext ctxt;
    private final boolean isPoolingEnabled;
    private final boolean breakAtLF;
    private String jspIdPrefix;
    private int jspId;
    private final PageInfo pageInfo;
    private final List<String> tagHandlerPoolNames;
    private GenBuffer charArrayBuffer;
    private final DateFormat timestampFormat;
    private final ELInterpreter elInterpreter;
    private final StringInterpreter stringInterpreter;

    static String quote(String s) {
        if (s == null) {
            return "null";
        }
        return '\"' + Generator.escape(s) + '\"';
    }

    static String escape(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\"') {
                b.append('\\').append('\"');
                continue;
            }
            if (c == '\\') {
                b.append('\\').append('\\');
                continue;
            }
            if (c == '\n') {
                b.append('\\').append('n');
                continue;
            }
            if (c == '\r') {
                b.append('\\').append('r');
                continue;
            }
            b.append(c);
        }
        return b.toString();
    }

    static String quote(char c) {
        StringBuilder b = new StringBuilder();
        b.append('\'');
        if (c == '\'') {
            b.append('\\').append('\'');
        } else if (c == '\\') {
            b.append('\\').append('\\');
        } else if (c == '\n') {
            b.append('\\').append('n');
        } else if (c == '\r') {
            b.append('\\').append('r');
        } else {
            b.append(c);
        }
        b.append('\'');
        return b.toString();
    }

    private String createJspId() {
        if (this.jspIdPrefix == null) {
            StringBuilder sb = new StringBuilder(32);
            String name = this.ctxt.getServletJavaFileName();
            sb.append("jsp_");
            sb.append(Math.abs((long)name.hashCode()));
            sb.append('_');
            this.jspIdPrefix = sb.toString();
        }
        return this.jspIdPrefix + this.jspId++;
    }

    private void generateDeclarations(Node.Nodes page) throws JasperException {
        this.out.println();
        class DeclarationVisitor
        extends Node.Visitor {
            private boolean getServletInfoGenerated = false;

            DeclarationVisitor() {
            }

            @Override
            public void visit(Node.PageDirective n) throws JasperException {
                if (this.getServletInfoGenerated) {
                    return;
                }
                String info = n.getAttributeValue("info");
                if (info == null) {
                    return;
                }
                this.getServletInfoGenerated = true;
                Generator.this.out.printil("public java.lang.String getServletInfo() {");
                Generator.this.out.pushIndent();
                Generator.this.out.printin("return ");
                Generator.this.out.print(Generator.quote(info));
                Generator.this.out.println(";");
                Generator.this.out.popIndent();
                Generator.this.out.printil("}");
                Generator.this.out.println();
            }

            @Override
            public void visit(Node.Declaration n) throws JasperException {
                n.setBeginJavaLine(Generator.this.out.getJavaLine());
                Generator.this.out.printMultiLn(n.getText());
                Generator.this.out.println();
                n.setEndJavaLine(Generator.this.out.getJavaLine());
            }

            @Override
            public void visit(Node.CustomTag n) throws JasperException {
                if (n.useTagPlugin()) {
                    n.getAtSTag().visit(this);
                    this.visitBody(n);
                    n.getAtETag().visit(this);
                } else {
                    this.visitBody(n);
                }
            }
        }
        page.visit(new DeclarationVisitor());
    }

    private void compileTagHandlerPoolList(Node.Nodes page) throws JasperException {
        class TagHandlerPoolVisitor
        extends Node.Visitor {
            private final List<String> names;

            TagHandlerPoolVisitor(List<String> v) {
                this.names = v;
            }

            @Override
            public void visit(Node.CustomTag n) throws JasperException {
                if (!n.implementsSimpleTag()) {
                    String name = this.createTagHandlerPoolName(n.getPrefix(), n.getLocalName(), n.getAttributes(), n.getNamedAttributeNodes(), n.hasEmptyBody());
                    n.setTagHandlerPoolName(name);
                    if (!this.names.contains(name)) {
                        this.names.add(name);
                    }
                }
                this.visitBody(n);
            }

            private String createTagHandlerPoolName(String prefix, String shortName, Attributes attrs, Node.Nodes namedAttrs, boolean hasEmptyBody) {
                StringBuilder poolName = new StringBuilder(64);
                poolName.append("_jspx_tagPool_").append(prefix).append('_').append(shortName);
                if (attrs != null) {
                    int i;
                    String[] attrNames = new String[attrs.getLength() + namedAttrs.size()];
                    for (i = 0; i < attrNames.length; ++i) {
                        attrNames[i] = attrs.getQName(i);
                    }
                    for (i = 0; i < namedAttrs.size(); ++i) {
                        attrNames[attrs.getLength() + i] = namedAttrs.getNode(i).getQName();
                    }
                    Arrays.sort(attrNames, Collections.reverseOrder());
                    if (attrNames.length > 0) {
                        poolName.append('&');
                    }
                    for (String attrName : attrNames) {
                        poolName.append('_');
                        poolName.append(attrName);
                    }
                }
                if (hasEmptyBody) {
                    poolName.append("_nobody");
                }
                return JspUtil.makeJavaIdentifier(poolName.toString());
            }
        }
        page.visit(new TagHandlerPoolVisitor(this.tagHandlerPoolNames));
    }

    private void declareTemporaryScriptingVars(Node.Nodes page) throws JasperException {
        class ScriptingVarVisitor
        extends Node.Visitor {
            private final List<String> vars = new ArrayList<String>();

            ScriptingVarVisitor() {
            }

            @Override
            public void visit(Node.CustomTag n) throws JasperException {
                if (n.getCustomNestingLevel() > 0) {
                    TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
                    VariableInfo[] varInfos = n.getVariableInfos();
                    if (varInfos.length > 0) {
                        for (VariableInfo varInfo : varInfos) {
                            String varName = varInfo.getVarName();
                            String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                            if (this.vars.contains(tmpVarName)) continue;
                            this.vars.add(tmpVarName);
                            Generator.this.out.printin(varInfo.getClassName());
                            Generator.this.out.print(" ");
                            Generator.this.out.print(tmpVarName);
                            Generator.this.out.print(" = ");
                            Generator.this.out.print(null);
                            Generator.this.out.println(";");
                        }
                    } else {
                        for (TagVariableInfo tagVarInfo : tagVarInfos) {
                            String varName = tagVarInfo.getNameGiven();
                            if (varName == null) {
                                varName = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute());
                            } else if (tagVarInfo.getNameFromAttribute() != null) continue;
                            String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                            if (this.vars.contains(tmpVarName)) continue;
                            this.vars.add(tmpVarName);
                            Generator.this.out.printin(tagVarInfo.getClassName());
                            Generator.this.out.print(" ");
                            Generator.this.out.print(tmpVarName);
                            Generator.this.out.print(" = ");
                            Generator.this.out.print(null);
                            Generator.this.out.println(";");
                        }
                    }
                }
                this.visitBody(n);
            }
        }
        page.visit(new ScriptingVarVisitor());
    }

    private void generateGetters() {
        this.out.printil("public javax.el.ExpressionFactory _jsp_getExpressionFactory() {");
        this.out.pushIndent();
        if (!this.ctxt.isTagFile()) {
            this.out.printin("if (");
            this.out.print(VAR_EXPRESSIONFACTORY);
            this.out.println(" == null) {");
            this.out.pushIndent();
            this.out.printil("synchronized (this) {");
            this.out.pushIndent();
            this.out.printin("if (");
            this.out.print(VAR_EXPRESSIONFACTORY);
            this.out.println(" == null) {");
            this.out.pushIndent();
            this.out.printin(VAR_EXPRESSIONFACTORY);
            this.out.println(" = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();");
            this.out.popIndent();
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
        }
        this.out.printin("return ");
        this.out.print(VAR_EXPRESSIONFACTORY);
        this.out.println(";");
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
        this.out.printil("public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {");
        this.out.pushIndent();
        if (!this.ctxt.isTagFile()) {
            this.out.printin("if (");
            this.out.print(VAR_INSTANCEMANAGER);
            this.out.println(" == null) {");
            this.out.pushIndent();
            this.out.printil("synchronized (this) {");
            this.out.pushIndent();
            this.out.printin("if (");
            this.out.print(VAR_INSTANCEMANAGER);
            this.out.println(" == null) {");
            this.out.pushIndent();
            this.out.printin(VAR_INSTANCEMANAGER);
            this.out.println(" = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());");
            this.out.popIndent();
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
        }
        this.out.printin("return ");
        this.out.print(VAR_INSTANCEMANAGER);
        this.out.println(";");
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
    }

    private void generateInit() {
        if (this.ctxt.isTagFile()) {
            this.out.printil("private void _jspInit(javax.servlet.ServletConfig config) {");
        } else {
            this.out.printil("public void _jspInit() {");
        }
        this.out.pushIndent();
        if (this.isPoolingEnabled) {
            for (int i = 0; i < this.tagHandlerPoolNames.size(); ++i) {
                this.out.printin(this.tagHandlerPoolNames.get(i));
                this.out.print(" = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(");
                if (this.ctxt.isTagFile()) {
                    this.out.print("config");
                } else {
                    this.out.print("getServletConfig()");
                }
                this.out.println(");");
            }
        }
        if (this.ctxt.isTagFile()) {
            this.out.printin(VAR_EXPRESSIONFACTORY);
            this.out.println(" = _jspxFactory.getJspApplicationContext(config.getServletContext()).getExpressionFactory();");
            this.out.printin(VAR_INSTANCEMANAGER);
            this.out.println(" = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(config);");
        }
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
    }

    private void generateDestroy() {
        this.out.printil("public void _jspDestroy() {");
        this.out.pushIndent();
        if (this.isPoolingEnabled) {
            for (int i = 0; i < this.tagHandlerPoolNames.size(); ++i) {
                this.out.printin(this.tagHandlerPoolNames.get(i));
                this.out.println(".release();");
            }
        }
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
    }

    private void genPreamblePackage(String packageName) {
        this.out.printil("package " + packageName + ";");
        this.out.println();
    }

    private void genPreambleImports() {
        for (String i : this.pageInfo.getImports()) {
            this.out.printin("import ");
            this.out.print(i);
            this.out.println(";");
        }
        this.out.println();
    }

    private void genPreambleStaticInitializers() {
        this.out.printil("private static final javax.servlet.jsp.JspFactory _jspxFactory =");
        this.out.printil("        javax.servlet.jsp.JspFactory.getDefaultFactory();");
        this.out.println();
        this.out.printil("private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;");
        this.out.println();
        Map<String, Long> dependants = this.pageInfo.getDependants();
        if (!dependants.isEmpty()) {
            this.out.printil("static {");
            this.out.pushIndent();
            this.out.printin("_jspx_dependants = new java.util.HashMap<java.lang.String,java.lang.Long>(");
            this.out.print("" + dependants.size());
            this.out.println(");");
            for (Map.Entry<String, Long> entry : dependants.entrySet()) {
                this.out.printin("_jspx_dependants.put(\"");
                this.out.print(entry.getKey());
                this.out.print("\", Long.valueOf(");
                this.out.print(entry.getValue().toString());
                this.out.println("L));");
            }
            this.out.popIndent();
            this.out.printil("}");
            this.out.println();
        }
        List<String> imports = this.pageInfo.getImports();
        HashSet<String> packages = new HashSet<String>();
        HashSet<String> classes = new HashSet<String>();
        for (String importName : imports) {
            String trimmed = importName.trim();
            if (trimmed.endsWith(".*")) {
                packages.add(trimmed.substring(0, trimmed.length() - 2));
                continue;
            }
            classes.add(trimmed);
        }
        this.out.printil("private static final java.util.Set<java.lang.String> _jspx_imports_packages;");
        this.out.println();
        this.out.printil("private static final java.util.Set<java.lang.String> _jspx_imports_classes;");
        this.out.println();
        this.out.printil("static {");
        this.out.pushIndent();
        this.out.printin("_jspx_imports_packages = new java.util.HashSet<>();");
        this.out.println();
        for (String packageName : packages) {
            this.out.printin("_jspx_imports_packages.add(\"");
            this.out.print(packageName);
            this.out.println("\");");
        }
        if (classes.size() == 0) {
            this.out.printin("_jspx_imports_classes = null;");
            this.out.println();
        } else {
            this.out.printin("_jspx_imports_classes = new java.util.HashSet<>();");
            this.out.println();
            for (String className : classes) {
                this.out.printin("_jspx_imports_classes.add(\"");
                this.out.print(className);
                this.out.println("\");");
            }
        }
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
    }

    private void genPreambleClassVariableDeclarations() {
        if (this.isPoolingEnabled && !this.tagHandlerPoolNames.isEmpty()) {
            for (int i = 0; i < this.tagHandlerPoolNames.size(); ++i) {
                this.out.printil("private org.apache.jasper.runtime.TagHandlerPool " + this.tagHandlerPoolNames.get(i) + ";");
            }
            this.out.println();
        }
        this.out.printin("private volatile javax.el.ExpressionFactory ");
        this.out.print(VAR_EXPRESSIONFACTORY);
        this.out.println(";");
        this.out.printin("private volatile org.apache.tomcat.InstanceManager ");
        this.out.print(VAR_INSTANCEMANAGER);
        this.out.println(";");
        this.out.println();
    }

    private void genPreambleMethods() {
        this.out.printil("public java.util.Map<java.lang.String,java.lang.Long> getDependants() {");
        this.out.pushIndent();
        this.out.printil("return _jspx_dependants;");
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
        this.out.printil("public java.util.Set<java.lang.String> getPackageImports() {");
        this.out.pushIndent();
        this.out.printil("return _jspx_imports_packages;");
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
        this.out.printil("public java.util.Set<java.lang.String> getClassImports() {");
        this.out.pushIndent();
        this.out.printil("return _jspx_imports_classes;");
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
        this.generateGetters();
        this.generateInit();
        this.generateDestroy();
    }

    private void generatePreamble(Node.Nodes page) throws JasperException {
        String servletPackageName = this.ctxt.getServletPackageName();
        String servletClassName = this.ctxt.getServletClassName();
        String serviceMethodName = Constants.SERVICE_METHOD_NAME;
        this.genPreamblePackage(servletPackageName);
        this.genPreambleImports();
        this.out.printin("public final class ");
        this.out.print(servletClassName);
        this.out.print(" extends ");
        this.out.println(this.pageInfo.getExtends());
        this.out.printin("    implements org.apache.jasper.runtime.JspSourceDependent,");
        this.out.println();
        this.out.printin("                 org.apache.jasper.runtime.JspSourceImports");
        if (!this.pageInfo.isThreadSafe()) {
            this.out.println(",");
            this.out.printin("                 javax.servlet.SingleThreadModel");
        }
        this.out.println(" {");
        this.out.pushIndent();
        this.generateDeclarations(page);
        this.genPreambleStaticInitializers();
        this.genPreambleClassVariableDeclarations();
        this.genPreambleMethods();
        this.out.printin("public void ");
        this.out.print(serviceMethodName);
        this.out.println("(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)");
        this.out.pushIndent();
        this.out.pushIndent();
        this.out.printil("throws java.io.IOException, javax.servlet.ServletException {");
        this.out.popIndent();
        this.out.println();
        if (!this.pageInfo.isErrorPage()) {
            this.out.printil("if (!javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {");
            this.out.pushIndent();
            this.out.printil("final java.lang.String _jspx_method = request.getMethod();");
            this.out.printil("if (\"OPTIONS\".equals(_jspx_method)) {");
            this.out.pushIndent();
            this.out.printil("response.setHeader(\"Allow\",\"GET, HEAD, POST, OPTIONS\");");
            this.out.printil("return;");
            this.out.popIndent();
            this.out.printil("}");
            this.out.printil("if (!\"GET\".equals(_jspx_method) && !\"POST\".equals(_jspx_method) && !\"HEAD\".equals(_jspx_method)) {");
            this.out.pushIndent();
            this.out.printil("response.setHeader(\"Allow\",\"GET, HEAD, POST, OPTIONS\");");
            this.out.printin("response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ");
            this.out.println("\"" + Localizer.getMessage("jsp.error.servlet.invalid.method") + "\");");
            this.out.printil("return;");
            this.out.popIndent();
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
            this.out.println();
        }
        this.out.printil("final javax.servlet.jsp.PageContext pageContext;");
        if (this.pageInfo.isSession()) {
            this.out.printil("javax.servlet.http.HttpSession session = null;");
        }
        if (this.pageInfo.isErrorPage()) {
            this.out.printil("java.lang.Throwable exception = org.apache.jasper.runtime.JspRuntimeLibrary.getThrowable(request);");
            this.out.printil("if (exception != null) {");
            this.out.pushIndent();
            this.out.printil("response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);");
            this.out.popIndent();
            this.out.printil("}");
        }
        this.out.printil("final javax.servlet.ServletContext application;");
        this.out.printil("final javax.servlet.ServletConfig config;");
        this.out.printil("javax.servlet.jsp.JspWriter out = null;");
        this.out.printil("final java.lang.Object page = this;");
        this.out.printil("javax.servlet.jsp.JspWriter _jspx_out = null;");
        this.out.printil("javax.servlet.jsp.PageContext _jspx_page_context = null;");
        this.out.println();
        this.declareTemporaryScriptingVars(page);
        this.out.println();
        this.out.printil("try {");
        this.out.pushIndent();
        this.out.printin("response.setContentType(");
        this.out.print(Generator.quote(this.pageInfo.getContentType()));
        this.out.println(");");
        if (this.ctxt.getOptions().isXpoweredBy()) {
            this.out.printil("response.addHeader(\"X-Powered-By\", \"JSP/2.3\");");
        }
        this.out.printil("pageContext = _jspxFactory.getPageContext(this, request, response,");
        this.out.printin("\t\t\t");
        this.out.print(Generator.quote(this.pageInfo.getErrorPage()));
        this.out.print(", " + this.pageInfo.isSession());
        this.out.print(", " + this.pageInfo.getBuffer());
        this.out.print(", " + this.pageInfo.isAutoFlush());
        this.out.println(");");
        this.out.printil("_jspx_page_context = pageContext;");
        this.out.printil("application = pageContext.getServletContext();");
        this.out.printil("config = pageContext.getServletConfig();");
        if (this.pageInfo.isSession()) {
            this.out.printil("session = pageContext.getSession();");
        }
        this.out.printil("out = pageContext.getOut();");
        this.out.printil("_jspx_out = out;");
        this.out.println();
    }

    private void generateXmlProlog(Node.Nodes page) {
        String doctypeName;
        String omitXmlDecl = this.pageInfo.getOmitXmlDecl();
        if (omitXmlDecl != null && !JspUtil.booleanValue(omitXmlDecl) || omitXmlDecl == null && page.getRoot().isXmlSyntax() && !this.pageInfo.hasJspRoot() && !this.ctxt.isTagFile()) {
            String cType = this.pageInfo.getContentType();
            String charSet = cType.substring(cType.indexOf("charset=") + 8);
            this.out.printil("out.write(\"<?xml version=\\\"1.0\\\" encoding=\\\"" + charSet + "\\\"?>\\n\");");
        }
        if ((doctypeName = this.pageInfo.getDoctypeName()) != null) {
            String doctypePublic = this.pageInfo.getDoctypePublic();
            String doctypeSystem = this.pageInfo.getDoctypeSystem();
            this.out.printin("out.write(\"<!DOCTYPE ");
            this.out.print(doctypeName);
            if (doctypePublic == null) {
                this.out.print(" SYSTEM \\\"");
            } else {
                this.out.print(" PUBLIC \\\"");
                this.out.print(doctypePublic);
                this.out.print("\\\" \\\"");
            }
            this.out.print(doctypeSystem);
            this.out.println("\\\">\\n\");");
        }
    }

    private static void generateLocalVariables(ServletWriter out, Node.ChildInfoBase n) {
        Node.ChildInfo ci = n.getChildInfo();
        if (ci.hasUseBean()) {
            out.printil("javax.servlet.http.HttpSession session = _jspx_page_context.getSession();");
            out.printil("javax.servlet.ServletContext application = _jspx_page_context.getServletContext();");
        }
        if (ci.hasUseBean() || ci.hasIncludeAction() || ci.hasSetProperty() || ci.hasParamAction()) {
            out.printil("javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)_jspx_page_context.getRequest();");
        }
        if (ci.hasIncludeAction()) {
            out.printil("javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)_jspx_page_context.getResponse();");
        }
    }

    private void genCommonPostamble() {
        for (GenBuffer methodBuffer : this.methodsBuffered) {
            methodBuffer.adjustJavaLines(this.out.getJavaLine() - 1);
            this.out.printMultiLn(methodBuffer.toString());
        }
        if (this.fragmentHelperClass.isUsed()) {
            this.fragmentHelperClass.generatePostamble();
            this.fragmentHelperClass.adjustJavaLines(this.out.getJavaLine() - 1);
            this.out.printMultiLn(this.fragmentHelperClass.toString());
        }
        if (this.charArrayBuffer != null) {
            this.out.printMultiLn(this.charArrayBuffer.toString());
        }
        this.out.popIndent();
        this.out.printil("}");
    }

    private void generatePostamble() {
        this.out.popIndent();
        this.out.printil("} catch (java.lang.Throwable t) {");
        this.out.pushIndent();
        this.out.printil("if (!(t instanceof javax.servlet.jsp.SkipPageException)){");
        this.out.pushIndent();
        this.out.printil("out = _jspx_out;");
        this.out.printil("if (out != null && out.getBufferSize() != 0)");
        this.out.pushIndent();
        this.out.printil("try {");
        this.out.pushIndent();
        this.out.printil("if (response.isCommitted()) {");
        this.out.pushIndent();
        this.out.printil("out.flush();");
        this.out.popIndent();
        this.out.printil("} else {");
        this.out.pushIndent();
        this.out.printil("out.clearBuffer();");
        this.out.popIndent();
        this.out.printil("}");
        this.out.popIndent();
        this.out.printil("} catch (java.io.IOException e) {}");
        this.out.popIndent();
        this.out.printil("if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);");
        this.out.printil("else throw new ServletException(t);");
        this.out.popIndent();
        this.out.printil("}");
        this.out.popIndent();
        this.out.printil("} finally {");
        this.out.pushIndent();
        this.out.printil("_jspxFactory.releasePageContext(_jspx_page_context);");
        this.out.popIndent();
        this.out.printil("}");
        this.out.popIndent();
        this.out.printil("}");
        this.genCommonPostamble();
    }

    Generator(ServletWriter out, Compiler compiler) throws JasperException {
        this.out = out;
        this.methodsBuffered = new ArrayList();
        this.charArrayBuffer = null;
        this.err = compiler.getErrorDispatcher();
        this.ctxt = compiler.getCompilationContext();
        this.fragmentHelperClass = new FragmentHelperClass("Helper");
        this.pageInfo = compiler.getPageInfo();
        ELInterpreter elInterpreter = null;
        try {
            elInterpreter = ELInterpreterFactory.getELInterpreter(compiler.getCompilationContext().getServletContext());
        }
        catch (Exception e) {
            this.err.jspError("jsp.error.el_interpreter_class.instantiation", e.getMessage());
        }
        this.elInterpreter = elInterpreter;
        StringInterpreter stringInterpreter = null;
        try {
            stringInterpreter = StringInterpreterFactory.getStringInterpreter(compiler.getCompilationContext().getServletContext());
        }
        catch (Exception e) {
            this.err.jspError("jsp.error.string_interpreter_class.instantiation", e.getMessage());
        }
        this.stringInterpreter = stringInterpreter;
        this.isPoolingEnabled = this.pageInfo.getExtends(false) == null || POOL_TAGS_WITH_EXTENDS ? this.ctxt.getOptions().isPoolingEnabled() : false;
        this.beanInfo = this.pageInfo.getBeanRepository();
        this.varInfoNames = this.pageInfo.getVarInfoNames();
        this.breakAtLF = this.ctxt.getOptions().getMappedFile();
        this.tagHandlerPoolNames = this.isPoolingEnabled ? new ArrayList<String>() : null;
        this.timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timestampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void generate(ServletWriter out, Compiler compiler, Node.Nodes page) throws JasperException {
        Generator gen = new Generator(out, compiler);
        if (gen.isPoolingEnabled) {
            gen.compileTagHandlerPoolList(page);
        }
        gen.generateCommentHeader();
        if (gen.ctxt.isTagFile()) {
            JasperTagInfo tagInfo = (JasperTagInfo)gen.ctxt.getTagInfo();
            gen.generateTagHandlerPreamble(tagInfo, page);
            if (gen.ctxt.isPrototypeMode()) {
                return;
            }
            gen.generateXmlProlog(page);
            gen.fragmentHelperClass.generatePreamble();
            Generator generator = gen;
            Objects.requireNonNull(generator);
            page.visit(generator.new GenerateVisitor(gen.ctxt.isTagFile(), out, gen.methodsBuffered, gen.fragmentHelperClass));
            gen.generateTagHandlerPostamble(tagInfo);
        } else {
            gen.generatePreamble(page);
            gen.generateXmlProlog(page);
            gen.fragmentHelperClass.generatePreamble();
            Generator generator = gen;
            Objects.requireNonNull(generator);
            page.visit(generator.new GenerateVisitor(gen.ctxt.isTagFile(), out, gen.methodsBuffered, gen.fragmentHelperClass));
            gen.generatePostamble();
        }
    }

    private void generateCommentHeader() {
        this.out.println("/*");
        this.out.println(" * Generated by the Jasper component of Apache Tomcat");
        this.out.println(" * Version: " + this.ctxt.getServletContext().getServerInfo());
        if (this.ctxt.getOptions().getGeneratedJavaAddTimestamp()) {
            this.out.println(" * Generated at: " + this.timestampFormat.format(new Date()) + " UTC");
        }
        this.out.println(" * Note: The last modified time of this file was set to");
        this.out.println(" *       the last modified time of the source file after");
        this.out.println(" *       generation to assist with modification tracking.");
        this.out.println(" */");
    }

    private void generateTagHandlerPreamble(JasperTagInfo tagInfo, Node.Nodes tag) throws JasperException {
        String className = tagInfo.getTagClassName();
        int lastIndex = className.lastIndexOf(46);
        if (lastIndex != -1) {
            String packageName = className.substring(0, lastIndex);
            this.genPreamblePackage(packageName);
            className = className.substring(lastIndex + 1);
        }
        this.genPreambleImports();
        this.out.printin("public final class ");
        this.out.println(className);
        this.out.printil("    extends javax.servlet.jsp.tagext.SimpleTagSupport");
        this.out.printin("    implements org.apache.jasper.runtime.JspSourceDependent,");
        this.out.println();
        this.out.printin("                 org.apache.jasper.runtime.JspSourceImports");
        if (tagInfo.hasDynamicAttributes()) {
            this.out.println(",");
            this.out.printin("               javax.servlet.jsp.tagext.DynamicAttributes");
        }
        this.out.println(" {");
        this.out.pushIndent();
        this.generateDeclarations(tag);
        this.genPreambleStaticInitializers();
        this.out.printil("private javax.servlet.jsp.JspContext jspContext;");
        this.out.printil("private java.io.Writer _jspx_sout;");
        this.genPreambleClassVariableDeclarations();
        this.generateSetJspContext(tagInfo);
        this.generateTagHandlerAttributes(tagInfo);
        if (tagInfo.hasDynamicAttributes()) {
            this.generateSetDynamicAttribute();
        }
        this.genPreambleMethods();
        this.out.printil("public void doTag() throws javax.servlet.jsp.JspException, java.io.IOException {");
        if (this.ctxt.isPrototypeMode()) {
            this.out.printil("}");
            this.out.popIndent();
            this.out.printil("}");
            return;
        }
        this.out.pushIndent();
        this.out.printil("javax.servlet.jsp.PageContext _jspx_page_context = (javax.servlet.jsp.PageContext)jspContext;");
        this.out.printil("javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) _jspx_page_context.getRequest();");
        this.out.printil("javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse) _jspx_page_context.getResponse();");
        this.out.printil("javax.servlet.http.HttpSession session = _jspx_page_context.getSession();");
        this.out.printil("javax.servlet.ServletContext application = _jspx_page_context.getServletContext();");
        this.out.printil("javax.servlet.ServletConfig config = _jspx_page_context.getServletConfig();");
        this.out.printil("javax.servlet.jsp.JspWriter out = jspContext.getOut();");
        this.out.printil("_jspInit(config);");
        this.out.printil("jspContext.getELContext().putContext(javax.servlet.jsp.JspContext.class,jspContext);");
        this.generatePageScopedVariables(tagInfo);
        this.declareTemporaryScriptingVars(tag);
        this.out.println();
        this.out.printil("try {");
        this.out.pushIndent();
    }

    private void generateTagHandlerPostamble(TagInfo tagInfo) {
        this.out.popIndent();
        this.out.printil("} catch( java.lang.Throwable t ) {");
        this.out.pushIndent();
        this.out.printil("if( t instanceof javax.servlet.jsp.SkipPageException )");
        this.out.printil("    throw (javax.servlet.jsp.SkipPageException) t;");
        this.out.printil("if( t instanceof java.io.IOException )");
        this.out.printil("    throw (java.io.IOException) t;");
        this.out.printil("if( t instanceof java.lang.IllegalStateException )");
        this.out.printil("    throw (java.lang.IllegalStateException) t;");
        this.out.printil("if( t instanceof javax.servlet.jsp.JspException )");
        this.out.printil("    throw (javax.servlet.jsp.JspException) t;");
        this.out.printil("throw new javax.servlet.jsp.JspException(t);");
        this.out.popIndent();
        this.out.printil("} finally {");
        this.out.pushIndent();
        TagAttributeInfo[] attrInfos = tagInfo.getAttributes();
        for (int i = 0; i < attrInfos.length; ++i) {
            if (!attrInfos[i].isDeferredMethod() && !attrInfos[i].isDeferredValue()) continue;
            this.out.printin("_el_variablemapper.setVariable(");
            this.out.print(Generator.quote(attrInfos[i].getName()));
            this.out.print(",_el_ve");
            this.out.print(i);
            this.out.println(");");
        }
        this.out.printil("jspContext.getELContext().putContext(javax.servlet.jsp.JspContext.class,super.getJspContext());");
        this.out.printil("((org.apache.jasper.runtime.JspContextWrapper) jspContext).syncEndTagFile();");
        if (this.isPoolingEnabled && !this.tagHandlerPoolNames.isEmpty()) {
            this.out.printil("_jspDestroy();");
        }
        this.out.popIndent();
        this.out.printil("}");
        this.out.popIndent();
        this.out.printil("}");
        this.genCommonPostamble();
    }

    private void generateTagHandlerAttributes(TagInfo tagInfo) {
        TagAttributeInfo[] attrInfos;
        if (tagInfo.hasDynamicAttributes()) {
            this.out.printil("private java.util.HashMap _jspx_dynamic_attrs = new java.util.HashMap();");
        }
        for (TagAttributeInfo info : attrInfos = tagInfo.getAttributes()) {
            this.out.printin("private ");
            if (info.isFragment()) {
                this.out.print("javax.servlet.jsp.tagext.JspFragment ");
            } else {
                this.out.print(JspUtil.toJavaSourceType(info.getTypeName()));
                this.out.print(" ");
            }
            this.out.print(JspUtil.makeJavaIdentifierForAttribute(info.getName()));
            this.out.println(";");
        }
        this.out.println();
        for (TagAttributeInfo attrInfo : attrInfos) {
            String javaName = JspUtil.makeJavaIdentifierForAttribute(attrInfo.getName());
            this.out.printin("public ");
            if (attrInfo.isFragment()) {
                this.out.print("javax.servlet.jsp.tagext.JspFragment ");
            } else {
                this.out.print(JspUtil.toJavaSourceType(attrInfo.getTypeName()));
                this.out.print(" ");
            }
            this.out.print(this.toGetterMethod(attrInfo.getName()));
            this.out.println(" {");
            this.out.pushIndent();
            this.out.printin("return this.");
            this.out.print(javaName);
            this.out.println(";");
            this.out.popIndent();
            this.out.printil("}");
            this.out.println();
            this.out.printin("public void ");
            this.out.print(this.toSetterMethodName(attrInfo.getName()));
            if (attrInfo.isFragment()) {
                this.out.print("(javax.servlet.jsp.tagext.JspFragment ");
            } else {
                this.out.print("(");
                this.out.print(JspUtil.toJavaSourceType(attrInfo.getTypeName()));
                this.out.print(" ");
            }
            this.out.print(javaName);
            this.out.println(") {");
            this.out.pushIndent();
            this.out.printin("this.");
            this.out.print(javaName);
            this.out.print(" = ");
            this.out.print(javaName);
            this.out.println(";");
            this.out.printin("jspContext.setAttribute(\"");
            this.out.print(attrInfo.getName());
            this.out.print("\", ");
            this.out.print(javaName);
            this.out.println(");");
            this.out.popIndent();
            this.out.printil("}");
            this.out.println();
        }
    }

    private void generateSetJspContext(TagInfo tagInfo) {
        TagVariableInfo[] tagVars;
        boolean nestedSeen = false;
        boolean atBeginSeen = false;
        boolean atEndSeen = false;
        boolean aliasSeen = false;
        for (TagVariableInfo var : tagVars = tagInfo.getTagVariableInfos()) {
            if (var.getNameFromAttribute() == null) continue;
            aliasSeen = true;
            break;
        }
        if (aliasSeen) {
            this.out.printil("public void setJspContext(javax.servlet.jsp.JspContext ctx, java.util.Map aliasMap) {");
        } else {
            this.out.printil("public void setJspContext(javax.servlet.jsp.JspContext ctx) {");
        }
        this.out.pushIndent();
        this.out.printil("super.setJspContext(ctx);");
        this.out.printil("java.util.ArrayList _jspx_nested = null;");
        this.out.printil("java.util.ArrayList _jspx_at_begin = null;");
        this.out.printil("java.util.ArrayList _jspx_at_end = null;");
        for (TagVariableInfo tagVar : tagVars) {
            switch (tagVar.getScope()) {
                case 0: {
                    if (!nestedSeen) {
                        this.out.printil("_jspx_nested = new java.util.ArrayList();");
                        nestedSeen = true;
                    }
                    this.out.printin("_jspx_nested.add(");
                    break;
                }
                case 1: {
                    if (!atBeginSeen) {
                        this.out.printil("_jspx_at_begin = new java.util.ArrayList();");
                        atBeginSeen = true;
                    }
                    this.out.printin("_jspx_at_begin.add(");
                    break;
                }
                case 2: {
                    if (!atEndSeen) {
                        this.out.printil("_jspx_at_end = new java.util.ArrayList();");
                        atEndSeen = true;
                    }
                    this.out.printin("_jspx_at_end.add(");
                }
            }
            this.out.print(Generator.quote(tagVar.getNameGiven()));
            this.out.println(");");
        }
        if (aliasSeen) {
            this.out.printil("this.jspContext = new org.apache.jasper.runtime.JspContextWrapper(this, ctx, _jspx_nested, _jspx_at_begin, _jspx_at_end, aliasMap);");
        } else {
            this.out.printil("this.jspContext = new org.apache.jasper.runtime.JspContextWrapper(this, ctx, _jspx_nested, _jspx_at_begin, _jspx_at_end, null);");
        }
        this.out.popIndent();
        this.out.printil("}");
        this.out.println();
        this.out.printil("public javax.servlet.jsp.JspContext getJspContext() {");
        this.out.pushIndent();
        this.out.printil("return this.jspContext;");
        this.out.popIndent();
        this.out.printil("}");
    }

    public void generateSetDynamicAttribute() {
        this.out.printil("public void setDynamicAttribute(java.lang.String uri, java.lang.String localName, java.lang.Object value) throws javax.servlet.jsp.JspException {");
        this.out.pushIndent();
        this.out.printil("if (uri == null)");
        this.out.pushIndent();
        this.out.printil("_jspx_dynamic_attrs.put(localName, value);");
        this.out.popIndent();
        this.out.popIndent();
        this.out.printil("}");
    }

    private void generatePageScopedVariables(JasperTagInfo tagInfo) {
        TagAttributeInfo[] attrInfos = tagInfo.getAttributes();
        boolean variableMapperVar = false;
        for (int i = 0; i < attrInfos.length; ++i) {
            String attrName = attrInfos[i].getName();
            if (attrInfos[i].isDeferredValue() || attrInfos[i].isDeferredMethod()) {
                if (!variableMapperVar) {
                    this.out.printil("javax.el.VariableMapper _el_variablemapper = jspContext.getELContext().getVariableMapper();");
                    variableMapperVar = true;
                }
                this.out.printin("javax.el.ValueExpression _el_ve");
                this.out.print(i);
                this.out.print(" = _el_variablemapper.setVariable(");
                this.out.print(Generator.quote(attrName));
                this.out.print(',');
                if (attrInfos[i].isDeferredMethod()) {
                    this.out.print("_jsp_getExpressionFactory().createValueExpression(");
                    this.out.print(this.toGetterMethod(attrName));
                    this.out.print(",javax.el.MethodExpression.class)");
                } else {
                    this.out.print(this.toGetterMethod(attrName));
                }
                this.out.println(");");
                continue;
            }
            this.out.printil("if( " + this.toGetterMethod(attrName) + " != null ) ");
            this.out.pushIndent();
            this.out.printin("_jspx_page_context.setAttribute(");
            this.out.print(Generator.quote(attrName));
            this.out.print(", ");
            this.out.print(this.toGetterMethod(attrName));
            this.out.println(");");
            this.out.popIndent();
        }
        if (tagInfo.hasDynamicAttributes()) {
            this.out.printin("_jspx_page_context.setAttribute(\"");
            this.out.print(tagInfo.getDynamicAttributesMapName());
            this.out.print("\", _jspx_dynamic_attrs);");
        }
    }

    private String toGetterMethod(String attrName) {
        char[] attrChars = attrName.toCharArray();
        attrChars[0] = Character.toUpperCase(attrChars[0]);
        return "get" + new String(attrChars) + "()";
    }

    private String toSetterMethodName(String attrName) {
        char[] attrChars = attrName.toCharArray();
        attrChars[0] = Character.toUpperCase(attrChars[0]);
        return "set" + new String(attrChars);
    }

    private static class GenBuffer {
        private Node node;
        private Node.Nodes body;
        private CharArrayWriter charWriter;
        protected ServletWriter out;

        GenBuffer() {
            this(null, null);
        }

        GenBuffer(Node n, Node.Nodes b) {
            this.node = n;
            this.body = b;
            if (this.body != null) {
                this.body.setGeneratedInBuffer(true);
            }
            this.charWriter = new CharArrayWriter();
            this.out = new ServletWriter(new PrintWriter(this.charWriter));
        }

        public ServletWriter getOut() {
            return this.out;
        }

        public String toString() {
            return this.charWriter.toString();
        }

        public void adjustJavaLines(final int offset) {
            if (this.node != null) {
                GenBuffer.adjustJavaLine(this.node, offset);
            }
            if (this.body != null) {
                try {
                    this.body.visit(new Node.Visitor(){

                        @Override
                        public void doVisit(Node n) {
                            GenBuffer.adjustJavaLine(n, offset);
                        }

                        @Override
                        public void visit(Node.CustomTag n) throws JasperException {
                            Node.Nodes b = n.getBody();
                            if (b != null && !b.isGeneratedInBuffer()) {
                                b.visit(this);
                            }
                        }
                    });
                }
                catch (JasperException jasperException) {
                    // empty catch block
                }
            }
        }

        private static void adjustJavaLine(Node n, int offset) {
            if (n.getBeginJavaLine() > 0) {
                n.setBeginJavaLine(n.getBeginJavaLine() + offset);
                n.setEndJavaLine(n.getEndJavaLine() + offset);
            }
        }
    }

    private static class FragmentHelperClass {
        private boolean used = false;
        private List<Fragment> fragments = new ArrayList<Fragment>();
        private String className;
        private GenBuffer classBuffer = new GenBuffer();

        FragmentHelperClass(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }

        public boolean isUsed() {
            return this.used;
        }

        public void generatePreamble() {
            ServletWriter out = this.classBuffer.getOut();
            out.println();
            out.pushIndent();
            out.printil("private class " + this.className);
            out.printil("    extends org.apache.jasper.runtime.JspFragmentHelper");
            out.printil("{");
            out.pushIndent();
            out.printil("private javax.servlet.jsp.tagext.JspTag _jspx_parent;");
            out.printil("private int[] _jspx_push_body_count;");
            out.println();
            out.printil("public " + this.className + "( int discriminator, javax.servlet.jsp.JspContext jspContext, javax.servlet.jsp.tagext.JspTag _jspx_parent, int[] _jspx_push_body_count ) {");
            out.pushIndent();
            out.printil("super( discriminator, jspContext, _jspx_parent );");
            out.printil("this._jspx_parent = _jspx_parent;");
            out.printil("this._jspx_push_body_count = _jspx_push_body_count;");
            out.popIndent();
            out.printil("}");
        }

        public Fragment openFragment(Node.ChildInfoBase parent, int methodNesting) {
            Fragment result = new Fragment(this.fragments.size(), parent);
            this.fragments.add(result);
            this.used = true;
            parent.setInnerClassName(this.className);
            ServletWriter out = result.getGenBuffer().getOut();
            out.pushIndent();
            out.pushIndent();
            if (methodNesting > 0) {
                out.printin("public boolean invoke");
            } else {
                out.printin("public void invoke");
            }
            out.println(result.getId() + "( javax.servlet.jsp.JspWriter out ) ");
            out.pushIndent();
            out.printil("throws java.lang.Throwable");
            out.popIndent();
            out.printil("{");
            out.pushIndent();
            Generator.generateLocalVariables(out, parent);
            return result;
        }

        public void closeFragment(Fragment fragment, int methodNesting) {
            ServletWriter out = fragment.getGenBuffer().getOut();
            if (methodNesting > 0) {
                out.printil("return false;");
            } else {
                out.printil("return;");
            }
            out.popIndent();
            out.printil("}");
        }

        public void generatePostamble() {
            ServletWriter out = this.classBuffer.getOut();
            for (Fragment fragment : this.fragments) {
                fragment.getGenBuffer().adjustJavaLines(out.getJavaLine() - 1);
                out.printMultiLn(fragment.getGenBuffer().toString());
            }
            out.printil("public void invoke( java.io.Writer writer )");
            out.pushIndent();
            out.printil("throws javax.servlet.jsp.JspException");
            out.popIndent();
            out.printil("{");
            out.pushIndent();
            out.printil("javax.servlet.jsp.JspWriter out = null;");
            out.printil("if( writer != null ) {");
            out.pushIndent();
            out.printil("out = this.jspContext.pushBody(writer);");
            out.popIndent();
            out.printil("} else {");
            out.pushIndent();
            out.printil("out = this.jspContext.getOut();");
            out.popIndent();
            out.printil("}");
            out.printil("try {");
            out.pushIndent();
            out.printil("Object _jspx_saved_JspContext = this.jspContext.getELContext().getContext(javax.servlet.jsp.JspContext.class);");
            out.printil("this.jspContext.getELContext().putContext(javax.servlet.jsp.JspContext.class,this.jspContext);");
            out.printil("switch( this.discriminator ) {");
            out.pushIndent();
            for (int i = 0; i < this.fragments.size(); ++i) {
                out.printil("case " + i + ":");
                out.pushIndent();
                out.printil("invoke" + i + "( out );");
                out.printil("break;");
                out.popIndent();
            }
            out.popIndent();
            out.printil("}");
            out.printil("jspContext.getELContext().putContext(javax.servlet.jsp.JspContext.class,_jspx_saved_JspContext);");
            out.popIndent();
            out.printil("}");
            out.printil("catch( java.lang.Throwable e ) {");
            out.pushIndent();
            out.printil("if (e instanceof javax.servlet.jsp.SkipPageException)");
            out.printil("    throw (javax.servlet.jsp.SkipPageException) e;");
            out.printil("throw new javax.servlet.jsp.JspException( e );");
            out.popIndent();
            out.printil("}");
            out.printil("finally {");
            out.pushIndent();
            out.printil("if( writer != null ) {");
            out.pushIndent();
            out.printil("this.jspContext.popBody();");
            out.popIndent();
            out.printil("}");
            out.popIndent();
            out.printil("}");
            out.popIndent();
            out.printil("}");
            out.popIndent();
            out.printil("}");
            out.popIndent();
        }

        public String toString() {
            return this.classBuffer.toString();
        }

        public void adjustJavaLines(int offset) {
            for (Fragment fragment : this.fragments) {
                fragment.getGenBuffer().adjustJavaLines(offset);
            }
        }

        private static class Fragment {
            private GenBuffer genBuffer;
            private int id;

            Fragment(int id, Node node) {
                this.id = id;
                this.genBuffer = new GenBuffer(null, node.getBody());
            }

            public GenBuffer getGenBuffer() {
                return this.genBuffer;
            }

            public int getId() {
                return this.id;
            }
        }
    }

    private class GenerateVisitor
    extends Node.Visitor {
        private final Map<String, Map<String, TagHandlerInfo>> handlerInfos;
        private final Map<String, Integer> tagVarNumbers;
        private String parent;
        private boolean isSimpleTagParent;
        private String pushBodyCountVar;
        private String simpleTagHandlerVar;
        private boolean isSimpleTagHandler;
        private boolean isFragment;
        private final boolean isTagFile;
        private ServletWriter out;
        private final ArrayList<GenBuffer> methodsBuffered;
        private final FragmentHelperClass fragmentHelperClass;
        private int methodNesting;
        private int charArrayCount;
        private HashMap<String, String> textMap;
        private static final String DOUBLE_QUOTE = "\\\"";

        GenerateVisitor(boolean isTagFile, ServletWriter out, ArrayList<GenBuffer> methodsBuffered, FragmentHelperClass fragmentHelperClass) {
            this.isTagFile = isTagFile;
            this.out = out;
            this.methodsBuffered = methodsBuffered;
            this.fragmentHelperClass = fragmentHelperClass;
            this.methodNesting = 0;
            this.handlerInfos = new HashMap<String, Map<String, TagHandlerInfo>>();
            this.tagVarNumbers = new HashMap<String, Integer>();
            this.textMap = new HashMap();
        }

        private String attributeValue(Node.JspAttribute attr, boolean encode, Class<?> expectedType) {
            String v = attr.getValue();
            if (attr.isExpression()) {
                if (encode) {
                    return "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(String.valueOf(" + v + "), request.getCharacterEncoding())";
                }
                return v;
            }
            if (attr.isELInterpreterInput()) {
                v = Generator.this.elInterpreter.interpreterCall(Generator.this.ctxt, this.isTagFile, v, expectedType, attr.getEL().getMapName());
                if (encode) {
                    return "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(" + v + ", request.getCharacterEncoding())";
                }
                return v;
            }
            if (attr.isNamedAttribute()) {
                return attr.getNamedAttributeNode().getTemporaryVariableName();
            }
            if (encode) {
                return "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(" + Generator.quote(v) + ", request.getCharacterEncoding())";
            }
            return Generator.quote(v);
        }

        private void printParams(Node n, String pageParam, boolean literal) throws JasperException {
            String sep = literal ? (pageParam.indexOf(63) > 0 ? "\"&\"" : "\"?\"") : "((" + pageParam + ").indexOf('?')>0? '&': '?')";
            if (n.getBody() != null) {
                class ParamVisitor
                extends Node.Visitor {
                    private String separator;

                    ParamVisitor(String separator) {
                        this.separator = separator;
                    }

                    @Override
                    public void visit(Node.ParamAction n) throws JasperException {
                        GenerateVisitor.this.out.print(" + ");
                        GenerateVisitor.this.out.print(this.separator);
                        GenerateVisitor.this.out.print(" + ");
                        GenerateVisitor.this.out.print("org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(" + Generator.quote(n.getTextAttribute("name")) + ", request.getCharacterEncoding())");
                        GenerateVisitor.this.out.print("+ \"=\" + ");
                        GenerateVisitor.this.out.print(GenerateVisitor.this.attributeValue(n.getValue(), true, String.class));
                        this.separator = "\"&\"";
                    }
                }
                n.getBody().visit(new ParamVisitor(sep));
            }
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printin("out.print(");
            this.out.printMultiLn(n.getText());
            this.out.println(");");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printMultiLn(n.getText());
            this.out.println();
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printil("out.write(" + Generator.this.elInterpreter.interpreterCall(Generator.this.ctxt, this.isTagFile, n.getType() + "{" + n.getText() + "}", String.class, n.getEL().getMapName()) + ");");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            String flush = n.getTextAttribute("flush");
            Node.JspAttribute page = n.getPage();
            boolean isFlush = "true".equals(flush);
            n.setBeginJavaLine(this.out.getJavaLine());
            String pageParam = page.isNamedAttribute() ? this.generateNamedAttributeValue(page.getNamedAttributeNode()) : this.attributeValue(page, false, String.class);
            Node.JspBody jspBody = this.findJspBody(n);
            if (jspBody != null) {
                this.prepareParams(jspBody);
            } else {
                this.prepareParams(n);
            }
            this.out.printin("org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, " + pageParam);
            this.printParams(n, pageParam, page.isLiteral());
            this.out.println(", out, " + isFlush + ");");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        private void prepareParams(Node parent) throws JasperException {
            Node.Nodes subelements = parent.getBody();
            if (subelements != null) {
                for (int i = 0; i < subelements.size(); ++i) {
                    Node n = subelements.getNode(i);
                    Node.Nodes paramSubElements = n.getBody();
                    for (int j = 0; paramSubElements != null && j < paramSubElements.size(); ++j) {
                        Node m = paramSubElements.getNode(j);
                        if (!(m instanceof Node.NamedAttribute)) continue;
                        this.generateNamedAttributeValue((Node.NamedAttribute)m);
                    }
                }
            }
        }

        private Node.JspBody findJspBody(Node parent) {
            Node.JspBody result = null;
            Node.Nodes subelements = parent.getBody();
            for (int i = 0; subelements != null && i < subelements.size(); ++i) {
                Node n = subelements.getNode(i);
                if (!(n instanceof Node.JspBody)) continue;
                result = (Node.JspBody)n;
                break;
            }
            return result;
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            Node.JspAttribute page = n.getPage();
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printil("if (true) {");
            this.out.pushIndent();
            String pageParam = page.isNamedAttribute() ? this.generateNamedAttributeValue(page.getNamedAttributeNode()) : this.attributeValue(page, false, String.class);
            Node.JspBody jspBody = this.findJspBody(n);
            if (jspBody != null) {
                this.prepareParams(jspBody);
            } else {
                this.prepareParams(n);
            }
            this.out.printin("_jspx_page_context.forward(");
            this.out.print(pageParam);
            this.printParams(n, pageParam, page.isLiteral());
            this.out.println(");");
            if (this.isTagFile || this.isFragment) {
                this.out.printil("throw new javax.servlet.jsp.SkipPageException();");
            } else {
                this.out.printil(this.methodNesting > 0 ? "return true;" : "return;");
            }
            this.out.popIndent();
            this.out.printil("}");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.GetProperty n) throws JasperException {
            String name = n.getTextAttribute("name");
            String property = n.getTextAttribute("property");
            n.setBeginJavaLine(this.out.getJavaLine());
            if (Generator.this.beanInfo.checkVariable(name)) {
                Class<?> bean = Generator.this.beanInfo.getBeanType(name);
                String beanName = bean.getCanonicalName();
                Method meth = JspRuntimeLibrary.getReadMethod(bean, property);
                String methodName = meth.getName();
                this.out.printil("out.write(org.apache.jasper.runtime.JspRuntimeLibrary.toString((((" + beanName + ")_jspx_page_context.findAttribute(\"" + name + "\"))." + methodName + "())));");
            } else if (!STRICT_GET_PROPERTY || Generator.this.varInfoNames.contains(name)) {
                this.out.printil("out.write(org.apache.jasper.runtime.JspRuntimeLibrary.toString(org.apache.jasper.runtime.JspRuntimeLibrary.handleGetProperty(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\")));");
            } else {
                throw new JasperException(Localizer.getMessage("jsp.error.invalid.name", n.getStart(), name));
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            String name = n.getTextAttribute("name");
            String property = n.getTextAttribute("property");
            String param = n.getTextAttribute("param");
            Node.JspAttribute value = n.getValue();
            n.setBeginJavaLine(this.out.getJavaLine());
            if ("*".equals(property)) {
                this.out.printil("org.apache.jasper.runtime.JspRuntimeLibrary.introspect(_jspx_page_context.findAttribute(\"" + name + "\"), request);");
            } else if (value == null) {
                if (param == null) {
                    param = property;
                }
                this.out.printil("org.apache.jasper.runtime.JspRuntimeLibrary.introspecthelper(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\", request.getParameter(\"" + param + "\"), request, \"" + param + "\", false);");
            } else if (value.isExpression()) {
                this.out.printil("org.apache.jasper.runtime.JspRuntimeLibrary.handleSetProperty(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\",");
                this.out.print(this.attributeValue(value, false, null));
                this.out.println(");");
            } else if (value.isELInterpreterInput()) {
                this.out.printil("org.apache.jasper.runtime.JspRuntimeLibrary.handleSetPropertyExpression(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\", " + Generator.quote(value.getValue()) + ", _jspx_page_context, " + value.getEL().getMapName() + ");");
            } else if (value.isNamedAttribute()) {
                String valueVarName = this.generateNamedAttributeValue(value.getNamedAttributeNode());
                this.out.printil("org.apache.jasper.runtime.JspRuntimeLibrary.introspecthelper(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\", " + valueVarName + ", null, null, false);");
            } else {
                this.out.printin("org.apache.jasper.runtime.JspRuntimeLibrary.introspecthelper(_jspx_page_context.findAttribute(\"" + name + "\"), \"" + property + "\", ");
                this.out.print(this.attributeValue(value, false, null));
                this.out.println(", null, null, false);");
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            String name = n.getTextAttribute("id");
            String scope = n.getTextAttribute("scope");
            String klass = n.getTextAttribute("class");
            String type = n.getTextAttribute("type");
            Node.JspAttribute beanName = n.getBeanName();
            boolean generateNew = false;
            String canonicalName = null;
            if (klass != null) {
                block17: {
                    try {
                        Class<?> bean = Generator.this.ctxt.getClassLoader().loadClass(klass);
                        canonicalName = klass.indexOf(36) >= 0 ? bean.getCanonicalName() : klass;
                        Constructor<?> constructor = bean.getConstructor(new Class[0]);
                        int modifiers = bean.getModifiers();
                        JreCompat jreCompat = JreCompat.getInstance();
                        if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || !jreCompat.canAccess(null, constructor)) {
                            throw new Exception(Localizer.getMessage("jsp.error.invalid.bean", modifiers));
                        }
                        generateNew = true;
                    }
                    catch (Exception e) {
                        if (Generator.this.ctxt.getOptions().getErrorOnUseBeanInvalidClassAttribute()) {
                            Generator.this.err.jspError((Node)n, "jsp.error.invalid.bean", klass);
                        }
                        if (canonicalName != null) break block17;
                        canonicalName = klass.replace('$', '.');
                    }
                }
                if (type == null) {
                    type = canonicalName;
                }
            }
            String scopename = "javax.servlet.jsp.PageContext.PAGE_SCOPE";
            String lock = null;
            if ("request".equals(scope)) {
                scopename = "javax.servlet.jsp.PageContext.REQUEST_SCOPE";
            } else if ("session".equals(scope)) {
                scopename = "javax.servlet.jsp.PageContext.SESSION_SCOPE";
                lock = "session";
            } else if ("application".equals(scope)) {
                scopename = "javax.servlet.jsp.PageContext.APPLICATION_SCOPE";
                lock = "application";
            }
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printin(type);
            this.out.print(' ');
            this.out.print(name);
            this.out.println(" = null;");
            if (lock != null) {
                this.out.printin("synchronized (");
                this.out.print(lock);
                this.out.println(") {");
                this.out.pushIndent();
            }
            this.out.printin(name);
            this.out.print(" = (");
            this.out.print(type);
            this.out.print(") _jspx_page_context.getAttribute(");
            this.out.print(Generator.quote(name));
            this.out.print(", ");
            this.out.print(scopename);
            this.out.println(");");
            this.out.printin("if (");
            this.out.print(name);
            this.out.println(" == null){");
            this.out.pushIndent();
            if (klass == null && beanName == null) {
                this.out.printin("throw new java.lang.InstantiationException(\"bean ");
                this.out.print(name);
                this.out.println(" not found within scope\");");
            } else {
                if (!generateNew) {
                    String binaryName = beanName != null ? (beanName.isNamedAttribute() ? this.generateNamedAttributeValue(beanName.getNamedAttributeNode()) : this.attributeValue(beanName, false, String.class)) : Generator.quote(klass);
                    this.out.printil("try {");
                    this.out.pushIndent();
                    this.out.printin(name);
                    this.out.print(" = (");
                    this.out.print(type);
                    this.out.print(") java.beans.Beans.instantiate(");
                    this.out.print("this.getClass().getClassLoader(), ");
                    this.out.print(binaryName);
                    this.out.println(");");
                    this.out.popIndent();
                    this.out.printil("} catch (java.lang.ClassNotFoundException exc) {");
                    this.out.pushIndent();
                    this.out.printil("throw new InstantiationException(exc.getMessage());");
                    this.out.popIndent();
                    this.out.printil("} catch (java.lang.Exception exc) {");
                    this.out.pushIndent();
                    this.out.printin("throw new javax.servlet.ServletException(");
                    this.out.print("\"Cannot create bean of class \" + ");
                    this.out.print(binaryName);
                    this.out.println(", exc);");
                    this.out.popIndent();
                    this.out.printil("}");
                } else {
                    this.out.printin(name);
                    this.out.print(" = new ");
                    this.out.print(canonicalName);
                    this.out.println("();");
                }
                this.out.printin("_jspx_page_context.setAttribute(");
                this.out.print(Generator.quote(name));
                this.out.print(", ");
                this.out.print(name);
                this.out.print(", ");
                this.out.print(scopename);
                this.out.println(");");
                this.visitBody(n);
            }
            this.out.popIndent();
            this.out.printil("}");
            if (lock != null) {
                this.out.popIndent();
                this.out.printil("}");
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        private String makeAttr(String attr, String value) {
            if (value == null) {
                return "";
            }
            return " " + attr + "=\"" + value + '\"';
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            class ParamVisitor
            extends Node.Visitor {
                private final boolean ie;

                ParamVisitor(boolean ie) {
                    this.ie = ie;
                }

                @Override
                public void visit(Node.ParamAction n) throws JasperException {
                    String name = n.getTextAttribute("name");
                    if (name.equalsIgnoreCase("object")) {
                        name = "java_object";
                    } else if (name.equalsIgnoreCase("type")) {
                        name = "java_type";
                    }
                    n.setBeginJavaLine(GenerateVisitor.this.out.getJavaLine());
                    if (this.ie) {
                        GenerateVisitor.this.out.printil("out.write( \"<param name=\\\"" + Generator.escape(name) + "\\\" value=\\\"\" + " + GenerateVisitor.this.attributeValue(n.getValue(), false, String.class) + " + \"\\\">\" );");
                        GenerateVisitor.this.out.printil("out.write(\"\\n\");");
                    } else {
                        GenerateVisitor.this.out.printil("out.write( \" " + Generator.escape(name) + "=\\\"\" + " + GenerateVisitor.this.attributeValue(n.getValue(), false, String.class) + " + \"\\\"\" );");
                    }
                    n.setEndJavaLine(GenerateVisitor.this.out.getJavaLine());
                }
            }
            Node.Nodes subelements;
            String type = n.getTextAttribute("type");
            String code = n.getTextAttribute("code");
            String name = n.getTextAttribute("name");
            Node.JspAttribute height = n.getHeight();
            Node.JspAttribute width = n.getWidth();
            String hspace = n.getTextAttribute("hspace");
            String vspace = n.getTextAttribute("vspace");
            String align = n.getTextAttribute("align");
            String iepluginurl = n.getTextAttribute("iepluginurl");
            String nspluginurl = n.getTextAttribute("nspluginurl");
            String codebase = n.getTextAttribute("codebase");
            String archive = n.getTextAttribute("archive");
            String jreversion = n.getTextAttribute("jreversion");
            String widthStr = null;
            if (width != null) {
                widthStr = width.isNamedAttribute() ? this.generateNamedAttributeValue(width.getNamedAttributeNode()) : this.attributeValue(width, false, String.class);
            }
            String heightStr = null;
            if (height != null) {
                heightStr = height.isNamedAttribute() ? this.generateNamedAttributeValue(height.getNamedAttributeNode()) : this.attributeValue(height, false, String.class);
            }
            if (iepluginurl == null) {
                iepluginurl = "http://java.sun.com/products/plugin/1.2.2/jinstall-1_2_2-win.cab#Version=1,2,2,0";
            }
            if (nspluginurl == null) {
                nspluginurl = "http://java.sun.com/products/plugin/";
            }
            n.setBeginJavaLine(this.out.getJavaLine());
            Node.JspBody jspBody = this.findJspBody(n);
            if (jspBody != null && (subelements = jspBody.getBody()) != null) {
                for (int i = 0; i < subelements.size(); ++i) {
                    Node m = subelements.getNode(i);
                    if (!(m instanceof Node.ParamsAction)) continue;
                    this.prepareParams(m);
                    break;
                }
            }
            String s0 = "<object" + this.makeAttr("classid", Generator.this.ctxt.getOptions().getIeClassId()) + this.makeAttr("name", name);
            String s1 = "";
            if (width != null) {
                s1 = " + \" width=\\\"\" + " + widthStr + " + \"\\\"\"";
            }
            String s2 = "";
            if (height != null) {
                s2 = " + \" height=\\\"\" + " + heightStr + " + \"\\\"\"";
            }
            String s3 = this.makeAttr("hspace", hspace) + this.makeAttr("vspace", vspace) + this.makeAttr("align", align) + this.makeAttr("codebase", iepluginurl) + '>';
            this.out.printil("out.write(" + Generator.quote(s0) + s1 + s2 + " + " + Generator.quote(s3) + ");");
            this.out.printil("out.write(\"\\n\");");
            s0 = "<param name=\"java_code\"" + this.makeAttr("value", code) + '>';
            this.out.printil("out.write(" + Generator.quote(s0) + ");");
            this.out.printil("out.write(\"\\n\");");
            if (codebase != null) {
                s0 = "<param name=\"java_codebase\"" + this.makeAttr("value", codebase) + '>';
                this.out.printil("out.write(" + Generator.quote(s0) + ");");
                this.out.printil("out.write(\"\\n\");");
            }
            if (archive != null) {
                s0 = "<param name=\"java_archive\"" + this.makeAttr("value", archive) + '>';
                this.out.printil("out.write(" + Generator.quote(s0) + ");");
                this.out.printil("out.write(\"\\n\");");
            }
            s0 = "<param name=\"type\"" + this.makeAttr("value", "application/x-java-" + type + (jreversion == null ? "" : ";version=" + jreversion)) + '>';
            this.out.printil("out.write(" + Generator.quote(s0) + ");");
            this.out.printil("out.write(\"\\n\");");
            if (n.getBody() != null) {
                n.getBody().visit(new ParamVisitor(true));
            }
            this.out.printil("out.write(" + Generator.quote("<comment>") + ");");
            this.out.printil("out.write(\"\\n\");");
            s0 = "<EMBED" + this.makeAttr("type", "application/x-java-" + type + (jreversion == null ? "" : ";version=" + jreversion)) + this.makeAttr("name", name);
            s3 = this.makeAttr("hspace", hspace) + this.makeAttr("vspace", vspace) + this.makeAttr("align", align) + this.makeAttr("pluginspage", nspluginurl) + this.makeAttr("java_code", code) + this.makeAttr("java_codebase", codebase) + this.makeAttr("java_archive", archive);
            this.out.printil("out.write(" + Generator.quote(s0) + s1 + s2 + " + " + Generator.quote(s3) + ");");
            if (n.getBody() != null) {
                n.getBody().visit(new ParamVisitor(false));
            }
            this.out.printil("out.write(" + Generator.quote("/>") + ");");
            this.out.printil("out.write(\"\\n\");");
            this.out.printil("out.write(" + Generator.quote("<noembed>") + ");");
            this.out.printil("out.write(\"\\n\");");
            if (n.getBody() != null) {
                this.visitBody(n);
                this.out.printil("out.write(\"\\n\");");
            }
            this.out.printil("out.write(" + Generator.quote("</noembed>") + ");");
            this.out.printil("out.write(\"\\n\");");
            this.out.printil("out.write(" + Generator.quote("</comment>") + ");");
            this.out.printil("out.write(\"\\n\");");
            this.out.printil("out.write(" + Generator.quote("</object>") + ");");
            this.out.printil("out.write(\"\\n\");");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            TagVariableInfo[] tagInfos;
            VariableInfo[] infos;
            if (n.useTagPlugin()) {
                this.generateTagPlugin(n);
                return;
            }
            TagHandlerInfo handlerInfo = this.getTagHandlerInfo(n);
            String baseVar = this.createTagVarName(n.getQName(), n.getPrefix(), n.getLocalName());
            String tagEvalVar = "_jspx_eval_" + baseVar;
            String tagHandlerVar = "_jspx_th_" + baseVar;
            String tagPushBodyCountVar = "_jspx_push_body_count_" + baseVar;
            ServletWriter outSave = null;
            Node.ChildInfo ci = n.getChildInfo();
            if (ci.isScriptless() && !ci.hasScriptingVars()) {
                String tagMethod = "_jspx_meth_" + baseVar;
                this.out.printin("if (");
                this.out.print(tagMethod);
                this.out.print("(");
                if (this.parent != null) {
                    this.out.print(this.parent);
                    this.out.print(", ");
                }
                this.out.print("_jspx_page_context");
                if (this.pushBodyCountVar != null) {
                    this.out.print(", ");
                    this.out.print(this.pushBodyCountVar);
                }
                this.out.println("))");
                this.out.pushIndent();
                this.out.printil(this.methodNesting > 0 ? "return true;" : "return;");
                this.out.popIndent();
                outSave = this.out;
                GenBuffer genBuffer = new GenBuffer(n, n.implementsSimpleTag() ? null : n.getBody());
                this.methodsBuffered.add(genBuffer);
                this.out = genBuffer.getOut();
                ++this.methodNesting;
                this.out.println();
                this.out.pushIndent();
                this.out.printin("private boolean ");
                this.out.print(tagMethod);
                this.out.print("(");
                if (this.parent != null) {
                    this.out.print("javax.servlet.jsp.tagext.JspTag ");
                    this.out.print(this.parent);
                    this.out.print(", ");
                }
                this.out.print("javax.servlet.jsp.PageContext _jspx_page_context");
                if (this.pushBodyCountVar != null) {
                    this.out.print(", int[] ");
                    this.out.print(this.pushBodyCountVar);
                }
                this.out.println(")");
                this.out.printil("        throws java.lang.Throwable {");
                this.out.pushIndent();
                if (!this.isTagFile) {
                    this.out.printil("javax.servlet.jsp.PageContext pageContext = _jspx_page_context;");
                }
                this.out.printil("javax.servlet.jsp.JspWriter out = _jspx_page_context.getOut();");
                Generator.generateLocalVariables(this.out, n);
            }
            if ((infos = n.getVariableInfos()).length > 0) {
                for (VariableInfo info : infos) {
                    Generator.this.pageInfo.getVarInfoNames().add(info.getVarName());
                }
            }
            if ((tagInfos = n.getTagVariableInfos()).length > 0) {
                for (TagVariableInfo tagInfo : tagInfos) {
                    String name = tagInfo.getNameGiven();
                    if (name == null) {
                        String nameFromAttribute = tagInfo.getNameFromAttribute();
                        name = n.getAttributeValue(nameFromAttribute);
                    }
                    Generator.this.pageInfo.getVarInfoNames().add(name);
                }
            }
            if (n.implementsSimpleTag()) {
                this.generateCustomDoTag(n, handlerInfo, tagHandlerVar);
            } else {
                this.generateCustomStart(n, handlerInfo, tagHandlerVar, tagEvalVar, tagPushBodyCountVar);
                String tmpParent = this.parent;
                this.parent = tagHandlerVar;
                boolean isSimpleTagParentSave = this.isSimpleTagParent;
                this.isSimpleTagParent = false;
                String tmpPushBodyCountVar = null;
                if (n.implementsTryCatchFinally()) {
                    tmpPushBodyCountVar = this.pushBodyCountVar;
                    this.pushBodyCountVar = tagPushBodyCountVar;
                }
                boolean tmpIsSimpleTagHandler = this.isSimpleTagHandler;
                this.isSimpleTagHandler = false;
                this.visitBody(n);
                this.parent = tmpParent;
                this.isSimpleTagParent = isSimpleTagParentSave;
                if (n.implementsTryCatchFinally()) {
                    this.pushBodyCountVar = tmpPushBodyCountVar;
                }
                this.isSimpleTagHandler = tmpIsSimpleTagHandler;
                this.generateCustomEnd(n, tagHandlerVar, tagEvalVar, tagPushBodyCountVar);
            }
            if (ci.isScriptless() && !ci.hasScriptingVars()) {
                this.out.printil("return false;");
                this.out.popIndent();
                this.out.printil("}");
                this.out.popIndent();
                --this.methodNesting;
                this.out = outSave;
            }
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printin("out.write(\"<");
            this.out.print(n.getQName());
            Attributes attrs = n.getNonTaglibXmlnsAttributes();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this.out.print(" ");
                    this.out.print(attrs.getQName(i));
                    this.out.print("=");
                    this.out.print(DOUBLE_QUOTE);
                    this.out.print(Generator.escape(attrs.getValue(i).replace("\"", "&quot;")));
                    this.out.print(DOUBLE_QUOTE);
                }
            }
            if ((attrs = n.getAttributes()) != null) {
                Node.JspAttribute[] jspAttrs = n.getJspAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this.out.print(" ");
                    this.out.print(attrs.getQName(i));
                    this.out.print("=");
                    if (jspAttrs[i].isELInterpreterInput()) {
                        this.out.print("\\\"\" + ");
                        String debug = this.attributeValue(jspAttrs[i], false, String.class);
                        this.out.print(debug);
                        this.out.print(" + \"\\\"");
                        continue;
                    }
                    this.out.print(DOUBLE_QUOTE);
                    this.out.print(Generator.escape(jspAttrs[i].getValue().replace("\"", "&quot;")));
                    this.out.print(DOUBLE_QUOTE);
                }
            }
            if (n.getBody() != null) {
                this.out.println(">\");");
                this.visitBody(n);
                this.out.printin("out.write(\"</");
                this.out.print(n.getQName());
                this.out.println(">\");");
            } else {
                this.out.println("/>\");");
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        /*
         * WARNING - void declaration
         */
        @Override
        public void visit(Node.JspElement n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            HashMap<String, void> map = new HashMap<String, void>();
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; i < attrs.length; ++i) {
                void var6_14;
                Object value = null;
                Object var6_9 = null;
                if (attrs[i].isNamedAttribute()) {
                    String omit;
                    Node.NamedAttribute attr = attrs[i].getNamedAttributeNode();
                    Node.JspAttribute omitAttr = attr.getOmit();
                    if (omitAttr == null) {
                        omit = "false";
                    } else {
                        omit = this.attributeValue(omitAttr, false, Boolean.TYPE);
                        if ("\"true\"".equals(omit)) continue;
                    }
                    value = this.generateNamedAttributeValue(attrs[i].getNamedAttributeNode());
                    if ("\"false\"".equals(omit)) {
                        String string = " + \" " + attrs[i].getName() + "=\\\"\" + " + (String)value + " + \"\\\"\"";
                    } else {
                        String string = " + (java.lang.Boolean.valueOf(" + omit + ")?\"\":\" " + attrs[i].getName() + "=\\\"\" + " + (String)value + " + \"\\\"\")";
                    }
                } else {
                    value = this.attributeValue(attrs[i], false, Object.class);
                    String string = " + \" " + attrs[i].getName() + "=\\\"\" + " + (String)value + " + \"\\\"\"";
                }
                map.put(attrs[i].getName(), var6_14);
            }
            String elemName = this.attributeValue(n.getNameAttribute(), false, String.class);
            this.out.printin("out.write(\"<\"");
            this.out.print(" + " + elemName);
            for (Map.Entry entry : map.entrySet()) {
                this.out.print((String)entry.getValue());
            }
            boolean hasBody = false;
            Node.Nodes nodes = n.getBody();
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); ++i) {
                    Node subelem = nodes.getNode(i);
                    if (subelem instanceof Node.NamedAttribute) continue;
                    hasBody = true;
                    break;
                }
            }
            if (hasBody) {
                this.out.println(" + \">\");");
                n.setEndJavaLine(this.out.getJavaLine());
                this.visitBody(n);
                this.out.printin("out.write(\"</\"");
                this.out.print(" + " + elemName);
                this.out.println(" + \">\");");
            } else {
                this.out.println(" + \"/>\");");
                n.setEndJavaLine(this.out.getJavaLine());
            }
        }

        @Override
        public void visit(Node.TemplateText n) throws JasperException {
            int textSize;
            Matcher preMatcher;
            String text = n.getText();
            if (Generator.this.ctxt.getOptions().getTrimSpaces().equals((Object)TrimSpacesOption.EXTENDED) && text.contains("\n") && !(preMatcher = PRE_TAG_PATTERN.matcher(text)).matches()) {
                Matcher matcher = BLANK_LINE_PATTERN.matcher(text);
                String revisedText = matcher.replaceAll("\n");
                text = revisedText.trim();
            }
            if ((textSize = text.length()) == 0) {
                return;
            }
            if (textSize <= 3) {
                n.setBeginJavaLine(this.out.getJavaLine());
                int lineInc = 0;
                for (int i = 0; i < textSize; ++i) {
                    char ch = text.charAt(i);
                    this.out.printil("out.write(" + Generator.quote(ch) + ");");
                    if (i > 0) {
                        n.addSmap(lineInc);
                    }
                    if (ch != '\n') continue;
                    ++lineInc;
                }
                n.setEndJavaLine(this.out.getJavaLine());
                return;
            }
            if (Generator.this.ctxt.getOptions().genStringAsCharArray()) {
                int len;
                ServletWriter caOut;
                if (Generator.this.charArrayBuffer == null) {
                    Generator.this.charArrayBuffer = new GenBuffer();
                    caOut = Generator.this.charArrayBuffer.getOut();
                    caOut.pushIndent();
                    this.textMap = new HashMap();
                } else {
                    caOut = Generator.this.charArrayBuffer.getOut();
                }
                int textLength = text.length();
                for (int textIndex = 0; textIndex < textLength; textIndex += len) {
                    len = 0;
                    len = textLength - textIndex > 16384 ? 16384 : textLength - textIndex;
                    String output = text.substring(textIndex, textIndex + len);
                    String charArrayName = this.textMap.get(output);
                    if (charArrayName == null) {
                        charArrayName = "_jspx_char_array_" + this.charArrayCount++;
                        this.textMap.put(output, charArrayName);
                        caOut.printin("static char[] ");
                        caOut.print(charArrayName);
                        caOut.print(" = ");
                        caOut.print(Generator.quote(output));
                        caOut.println(".toCharArray();");
                    }
                    n.setBeginJavaLine(this.out.getJavaLine());
                    this.out.printil("out.write(" + charArrayName + ");");
                    n.setEndJavaLine(this.out.getJavaLine());
                }
                return;
            }
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printin();
            StringBuilder sb = new StringBuilder("out.write(\"");
            int initLength = sb.length();
            int count = 1024;
            int srcLine = 0;
            block8: for (int i = 0; i < text.length(); ++i) {
                char ch = text.charAt(i);
                --count;
                switch (ch) {
                    case '\"': {
                        sb.append('\\').append('\"');
                        continue block8;
                    }
                    case '\\': {
                        sb.append('\\').append('\\');
                        continue block8;
                    }
                    case '\r': {
                        sb.append('\\').append('r');
                        continue block8;
                    }
                    case '\n': {
                        sb.append('\\').append('n');
                        ++srcLine;
                        if (Generator.this.breakAtLF || count < 0) {
                            sb.append("\");");
                            this.out.println(sb.toString());
                            if (i < text.length() - 1) {
                                this.out.printin();
                            }
                            sb.setLength(initLength);
                            count = 1024;
                        }
                        n.addSmap(srcLine);
                        continue block8;
                    }
                    default: {
                        sb.append(ch);
                    }
                }
            }
            if (sb.length() > initLength) {
                sb.append("\");");
                this.out.println(sb.toString());
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            if (n.getBody() != null) {
                if (this.isSimpleTagHandler) {
                    this.out.printin(this.simpleTagHandlerVar);
                    this.out.print(".setJspBody(");
                    this.generateJspFragment(n, this.simpleTagHandlerVar);
                    this.out.println(");");
                } else {
                    this.visitBody(n);
                }
            }
        }

        @Override
        public void visit(Node.InvokeAction n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printil("((org.apache.jasper.runtime.JspContextWrapper) this.jspContext).syncBeforeInvoke();");
            String varReaderAttr = n.getTextAttribute("varReader");
            String varAttr = n.getTextAttribute("var");
            if (varReaderAttr != null || varAttr != null) {
                this.out.printil("_jspx_sout = new java.io.StringWriter();");
            } else {
                this.out.printil("_jspx_sout = null;");
            }
            this.out.printin("if (");
            this.out.print(Generator.this.toGetterMethod(n.getTextAttribute("fragment")));
            this.out.println(" != null) {");
            this.out.pushIndent();
            this.out.printin(Generator.this.toGetterMethod(n.getTextAttribute("fragment")));
            this.out.println(".invoke(_jspx_sout);");
            this.out.popIndent();
            this.out.printil("}");
            if (varReaderAttr != null || varAttr != null) {
                String scopeName = n.getTextAttribute("scope");
                this.out.printin("_jspx_page_context.setAttribute(");
                if (varReaderAttr != null) {
                    this.out.print(Generator.quote(varReaderAttr));
                    this.out.print(", new java.io.StringReader(_jspx_sout.toString())");
                } else {
                    this.out.print(Generator.quote(varAttr));
                    this.out.print(", _jspx_sout.toString()");
                }
                if (scopeName != null) {
                    this.out.print(", ");
                    this.out.print(this.getScopeConstant(scopeName));
                }
                this.out.println(");");
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.DoBodyAction n) throws JasperException {
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printil("((org.apache.jasper.runtime.JspContextWrapper) this.jspContext).syncBeforeInvoke();");
            String varReaderAttr = n.getTextAttribute("varReader");
            String varAttr = n.getTextAttribute("var");
            if (varReaderAttr != null || varAttr != null) {
                this.out.printil("_jspx_sout = new java.io.StringWriter();");
            } else {
                this.out.printil("_jspx_sout = null;");
            }
            this.out.printil("if (getJspBody() != null)");
            this.out.pushIndent();
            this.out.printil("getJspBody().invoke(_jspx_sout);");
            this.out.popIndent();
            if (varReaderAttr != null || varAttr != null) {
                String scopeName = n.getTextAttribute("scope");
                this.out.printin("_jspx_page_context.setAttribute(");
                if (varReaderAttr != null) {
                    this.out.print(Generator.quote(varReaderAttr));
                    this.out.print(", new java.io.StringReader(_jspx_sout.toString())");
                } else {
                    this.out.print(Generator.quote(varAttr));
                    this.out.print(", _jspx_sout.toString()");
                }
                if (scopeName != null) {
                    this.out.print(", ");
                    this.out.print(this.getScopeConstant(scopeName));
                }
                this.out.println(");");
            }
            this.out.printil("jspContext.getELContext().putContext(javax.servlet.jsp.JspContext.class,getJspContext());");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        @Override
        public void visit(Node.AttributeGenerator n) throws JasperException {
            Node.CustomTag tag = n.getTag();
            Node.JspAttribute[] attrs = tag.getJspAttributes();
            for (int i = 0; i < attrs.length; ++i) {
                if (!attrs[i].getName().equals(n.getName())) continue;
                this.out.print(this.evaluateAttribute(this.getTagHandlerInfo(tag), attrs[i], tag, null));
                break;
            }
        }

        private TagHandlerInfo getTagHandlerInfo(Node.CustomTag n) throws JasperException {
            Map handlerInfosByShortName = this.handlerInfos.computeIfAbsent(n.getPrefix(), k -> new HashMap());
            TagHandlerInfo handlerInfo = (TagHandlerInfo)handlerInfosByShortName.get(n.getLocalName());
            if (handlerInfo == null) {
                handlerInfo = new TagHandlerInfo(n, n.getTagHandlerClass(), Generator.this.err);
                handlerInfosByShortName.put(n.getLocalName(), handlerInfo);
            }
            return handlerInfo;
        }

        private void generateTagPlugin(Node.CustomTag n) throws JasperException {
            n.getAtSTag().visit(this);
            this.visitBody(n);
            n.getAtETag().visit(this);
        }

        private void generateCustomStart(Node.CustomTag n, TagHandlerInfo handlerInfo, String tagHandlerVar, String tagEvalVar, String tagPushBodyCountVar) throws JasperException {
            Class<?> tagHandlerClass = handlerInfo.getTagHandlerClass();
            this.out.printin("//  ");
            this.out.println(n.getQName());
            n.setBeginJavaLine(this.out.getJavaLine());
            this.declareScriptingVars(n, 1);
            this.saveScriptingVars(n, 1);
            String tagHandlerClassName = tagHandlerClass.getCanonicalName();
            if (Generator.this.isPoolingEnabled && !n.implementsJspIdConsumer()) {
                this.out.printin(tagHandlerClassName);
                this.out.print(" ");
                this.out.print(tagHandlerVar);
                this.out.print(" = ");
                this.out.print("(");
                this.out.print(tagHandlerClassName);
                this.out.print(") ");
                this.out.print(n.getTagHandlerPoolName());
                this.out.print(".get(");
                this.out.print(tagHandlerClassName);
                this.out.println(".class);");
                this.out.printin("boolean ");
                this.out.print(tagHandlerVar);
                this.out.println("_reused = false;");
            } else {
                this.writeNewInstance(tagHandlerVar, tagHandlerClass);
            }
            this.out.printil("try {");
            this.out.pushIndent();
            this.generateSetters(n, tagHandlerVar, handlerInfo, false);
            if (n.implementsTryCatchFinally()) {
                this.out.printin("int[] ");
                this.out.print(tagPushBodyCountVar);
                this.out.println(" = new int[] { 0 };");
                this.out.printil("try {");
                this.out.pushIndent();
            }
            this.out.printin("int ");
            this.out.print(tagEvalVar);
            this.out.print(" = ");
            this.out.print(tagHandlerVar);
            this.out.println(".doStartTag();");
            if (!n.implementsBodyTag()) {
                this.syncScriptingVars(n, 1);
            }
            if (!n.hasEmptyBody()) {
                this.out.printin("if (");
                this.out.print(tagEvalVar);
                this.out.println(" != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {");
                this.out.pushIndent();
                this.declareScriptingVars(n, 0);
                this.saveScriptingVars(n, 0);
                if (n.implementsBodyTag()) {
                    this.out.printin("if (");
                    this.out.print(tagEvalVar);
                    this.out.println(" != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {");
                    this.out.pushIndent();
                    if (n.implementsTryCatchFinally()) {
                        this.out.printin(tagPushBodyCountVar);
                        this.out.println("[0]++;");
                    } else if (this.pushBodyCountVar != null) {
                        this.out.printin(this.pushBodyCountVar);
                        this.out.println("[0]++;");
                    }
                    this.out.printin("out = org.apache.jasper.runtime.JspRuntimeLibrary.startBufferedBody(");
                    this.out.print("_jspx_page_context, ");
                    this.out.print(tagHandlerVar);
                    this.out.println(");");
                    this.out.popIndent();
                    this.out.printil("}");
                    this.syncScriptingVars(n, 1);
                    this.syncScriptingVars(n, 0);
                } else {
                    this.syncScriptingVars(n, 0);
                }
                if (n.implementsIterationTag()) {
                    this.out.printil("do {");
                    this.out.pushIndent();
                }
            }
            n.setEndJavaLine(this.out.getJavaLine());
        }

        private void writeNewInstance(String tagHandlerVar, Class<?> tagHandlerClass) {
            String tagHandlerClassName = tagHandlerClass.getCanonicalName();
            this.out.printin(tagHandlerClassName);
            this.out.print(" ");
            this.out.print(tagHandlerVar);
            this.out.print(" = ");
            if (Constants.USE_INSTANCE_MANAGER_FOR_TAGS) {
                this.out.print("(");
                this.out.print(tagHandlerClassName);
                this.out.print(")");
                this.out.print("_jsp_getInstanceManager().newInstance(\"");
                this.out.print(tagHandlerClass.getName());
                this.out.println("\", this.getClass().getClassLoader());");
            } else {
                this.out.print("new ");
                this.out.print(tagHandlerClassName);
                this.out.println("();");
                this.out.printin("_jsp_getInstanceManager().newInstance(");
                this.out.print(tagHandlerVar);
                this.out.println(");");
            }
        }

        private void writeDestroyInstance(String tagHandlerVar) {
            this.out.printin("_jsp_getInstanceManager().destroyInstance(");
            this.out.print(tagHandlerVar);
            this.out.println(");");
        }

        private void generateCustomEnd(Node.CustomTag n, String tagHandlerVar, String tagEvalVar, String tagPushBodyCountVar) {
            if (!n.hasEmptyBody()) {
                if (n.implementsIterationTag()) {
                    this.out.printin("int evalDoAfterBody = ");
                    this.out.print(tagHandlerVar);
                    this.out.println(".doAfterBody();");
                    this.syncScriptingVars(n, 1);
                    this.syncScriptingVars(n, 0);
                    this.out.printil("if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)");
                    this.out.pushIndent();
                    this.out.printil("break;");
                    this.out.popIndent();
                    this.out.popIndent();
                    this.out.printil("} while (true);");
                }
                this.restoreScriptingVars(n, 0);
                if (n.implementsBodyTag()) {
                    this.out.printin("if (");
                    this.out.print(tagEvalVar);
                    this.out.println(" != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {");
                    this.out.pushIndent();
                    this.out.printil("out = _jspx_page_context.popBody();");
                    if (n.implementsTryCatchFinally()) {
                        this.out.printin(tagPushBodyCountVar);
                        this.out.println("[0]--;");
                    } else if (this.pushBodyCountVar != null) {
                        this.out.printin(this.pushBodyCountVar);
                        this.out.println("[0]--;");
                    }
                    this.out.popIndent();
                    this.out.printil("}");
                }
                this.out.popIndent();
                this.out.printil("}");
            }
            this.out.printin("if (");
            this.out.print(tagHandlerVar);
            this.out.println(".doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {");
            this.out.pushIndent();
            if (this.isTagFile || this.isFragment) {
                this.out.printil("throw new javax.servlet.jsp.SkipPageException();");
            } else {
                this.out.printil(this.methodNesting > 0 ? "return true;" : "return;");
            }
            this.out.popIndent();
            this.out.printil("}");
            this.syncScriptingVars(n, 1);
            if (n.implementsTryCatchFinally()) {
                this.out.popIndent();
                this.out.printil("} catch (java.lang.Throwable _jspx_exception) {");
                this.out.pushIndent();
                this.out.printin("while (");
                this.out.print(tagPushBodyCountVar);
                this.out.println("[0]-- > 0)");
                this.out.pushIndent();
                this.out.printil("out = _jspx_page_context.popBody();");
                this.out.popIndent();
                this.out.printin(tagHandlerVar);
                this.out.println(".doCatch(_jspx_exception);");
                this.out.popIndent();
                this.out.printil("} finally {");
                this.out.pushIndent();
                this.out.printin(tagHandlerVar);
                this.out.println(".doFinally();");
            }
            if (n.implementsTryCatchFinally()) {
                this.out.popIndent();
                this.out.printil("}");
            }
            if (Generator.this.isPoolingEnabled && !n.implementsJspIdConsumer()) {
                this.out.printin(n.getTagHandlerPoolName());
                this.out.print(".reuse(");
                this.out.print(tagHandlerVar);
                this.out.println(");");
                this.out.printin(tagHandlerVar);
                this.out.println("_reused = true;");
            }
            this.out.popIndent();
            this.out.printil("} finally {");
            this.out.pushIndent();
            this.out.printin("org.apache.jasper.runtime.JspRuntimeLibrary.releaseTag(");
            this.out.print(tagHandlerVar);
            this.out.print(", _jsp_getInstanceManager(), ");
            if (Generator.this.isPoolingEnabled && !n.implementsJspIdConsumer()) {
                this.out.print(tagHandlerVar);
                this.out.println("_reused);");
            } else {
                this.out.println("false);");
            }
            this.out.popIndent();
            this.out.printil("}");
            this.declareScriptingVars(n, 2);
            this.syncScriptingVars(n, 2);
            this.restoreScriptingVars(n, 1);
        }

        private void generateCustomDoTag(Node.CustomTag n, TagHandlerInfo handlerInfo, String tagHandlerVar) throws JasperException {
            Class<?> tagHandlerClass = handlerInfo.getTagHandlerClass();
            n.setBeginJavaLine(this.out.getJavaLine());
            this.out.printin("//  ");
            this.out.println(n.getQName());
            this.declareScriptingVars(n, 1);
            this.saveScriptingVars(n, 1);
            this.declareScriptingVars(n, 2);
            this.writeNewInstance(tagHandlerVar, tagHandlerClass);
            this.out.printil("try {");
            this.out.pushIndent();
            this.generateSetters(n, tagHandlerVar, handlerInfo, true);
            if (this.findJspBody(n) == null) {
                if (!n.hasEmptyBody()) {
                    this.out.printin(tagHandlerVar);
                    this.out.print(".setJspBody(");
                    this.generateJspFragment(n, tagHandlerVar);
                    this.out.println(");");
                }
            } else {
                String tmpTagHandlerVar = this.simpleTagHandlerVar;
                this.simpleTagHandlerVar = tagHandlerVar;
                boolean tmpIsSimpleTagHandler = this.isSimpleTagHandler;
                this.isSimpleTagHandler = true;
                this.visitBody(n);
                this.simpleTagHandlerVar = tmpTagHandlerVar;
                this.isSimpleTagHandler = tmpIsSimpleTagHandler;
            }
            this.out.printin(tagHandlerVar);
            this.out.println(".doTag();");
            this.restoreScriptingVars(n, 1);
            this.syncScriptingVars(n, 1);
            this.syncScriptingVars(n, 2);
            this.out.popIndent();
            this.out.printil("} finally {");
            this.out.pushIndent();
            this.writeDestroyInstance(tagHandlerVar);
            this.out.popIndent();
            this.out.printil("}");
            n.setEndJavaLine(this.out.getJavaLine());
        }

        private void declareScriptingVars(Node.CustomTag n, int scope) {
            if (this.isFragment) {
                return;
            }
            List<Object> vec = n.getScriptingVars(scope);
            if (vec != null) {
                for (Object elem : vec) {
                    if (elem instanceof VariableInfo) {
                        VariableInfo varInfo = (VariableInfo)elem;
                        this.out.printin(varInfo.getClassName());
                        this.out.print(" ");
                        this.out.print(varInfo.getVarName());
                        this.out.println(" = null;");
                        continue;
                    }
                    TagVariableInfo tagVarInfo = (TagVariableInfo)elem;
                    String varName = tagVarInfo.getNameGiven();
                    if (varName == null) {
                        varName = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute());
                    } else if (tagVarInfo.getNameFromAttribute() != null) continue;
                    this.out.printin(tagVarInfo.getClassName());
                    this.out.print(" ");
                    this.out.print(varName);
                    this.out.println(" = null;");
                }
            }
        }

        private void saveScriptingVars(Node.CustomTag n, int scope) {
            if (n.getCustomNestingLevel() == 0) {
                return;
            }
            if (this.isFragment) {
                return;
            }
            TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
            VariableInfo[] varInfos = n.getVariableInfos();
            if (varInfos.length == 0 && tagVarInfos.length == 0) {
                return;
            }
            List<Object> declaredVariables = n.getScriptingVars(scope);
            if (varInfos.length > 0) {
                for (VariableInfo varInfo : varInfos) {
                    if (varInfo.getScope() != scope || declaredVariables.contains(varInfo)) continue;
                    String varName = varInfo.getVarName();
                    String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                    this.out.printin(tmpVarName);
                    this.out.print(" = ");
                    this.out.print(varName);
                    this.out.println(";");
                }
            } else {
                for (TagVariableInfo tagVarInfo : tagVarInfos) {
                    if (tagVarInfo.getScope() != scope || declaredVariables.contains(tagVarInfo)) continue;
                    String varName = tagVarInfo.getNameGiven();
                    if (varName == null) {
                        varName = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute());
                    }
                    String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                    this.out.printin(tmpVarName);
                    this.out.print(" = ");
                    this.out.print(varName);
                    this.out.println(";");
                }
            }
        }

        private void restoreScriptingVars(Node.CustomTag n, int scope) {
            if (n.getCustomNestingLevel() == 0) {
                return;
            }
            if (this.isFragment) {
                return;
            }
            TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
            VariableInfo[] varInfos = n.getVariableInfos();
            if (varInfos.length == 0 && tagVarInfos.length == 0) {
                return;
            }
            List<Object> declaredVariables = n.getScriptingVars(scope);
            if (varInfos.length > 0) {
                for (VariableInfo varInfo : varInfos) {
                    if (varInfo.getScope() != scope || declaredVariables.contains(varInfo)) continue;
                    String varName = varInfo.getVarName();
                    String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                    this.out.printin(varName);
                    this.out.print(" = ");
                    this.out.print(tmpVarName);
                    this.out.println(";");
                }
            } else {
                for (TagVariableInfo tagVarInfo : tagVarInfos) {
                    if (tagVarInfo.getScope() != scope || declaredVariables.contains(tagVarInfo)) continue;
                    String varName = tagVarInfo.getNameGiven();
                    if (varName == null) {
                        varName = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute());
                    }
                    String tmpVarName = "_jspx_" + varName + "_" + n.getCustomNestingLevel();
                    this.out.printin(varName);
                    this.out.print(" = ");
                    this.out.print(tmpVarName);
                    this.out.println(";");
                }
            }
        }

        private void syncScriptingVars(Node.CustomTag n, int scope) {
            if (this.isFragment) {
                return;
            }
            TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
            VariableInfo[] varInfos = n.getVariableInfos();
            if (varInfos.length == 0 && tagVarInfos.length == 0) {
                return;
            }
            if (varInfos.length > 0) {
                for (VariableInfo varInfo : varInfos) {
                    if (varInfo.getScope() != scope) continue;
                    this.out.printin(varInfo.getVarName());
                    this.out.print(" = (");
                    this.out.print(varInfo.getClassName());
                    this.out.print(") _jspx_page_context.findAttribute(");
                    this.out.print(Generator.quote(varInfo.getVarName()));
                    this.out.println(");");
                }
            } else {
                for (TagVariableInfo tagVarInfo : tagVarInfos) {
                    if (tagVarInfo.getScope() != scope) continue;
                    String name = tagVarInfo.getNameGiven();
                    if (name == null) {
                        name = n.getTagData().getAttributeString(tagVarInfo.getNameFromAttribute());
                    } else if (tagVarInfo.getNameFromAttribute() != null) continue;
                    this.out.printin(name);
                    this.out.print(" = (");
                    this.out.print(tagVarInfo.getClassName());
                    this.out.print(") _jspx_page_context.findAttribute(");
                    this.out.print(Generator.quote(name));
                    this.out.println(");");
                }
            }
        }

        private String getJspContextVar() {
            if (this.isTagFile) {
                return "this.getJspContext()";
            }
            return "_jspx_page_context";
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private String createTagVarName(String fullName, String prefix, String shortName) {
            String varName;
            Map<String, Integer> map = this.tagVarNumbers;
            synchronized (map) {
                varName = prefix + "_" + shortName + "_";
                if (this.tagVarNumbers.get(fullName) != null) {
                    Integer i = this.tagVarNumbers.get(fullName);
                    varName = varName + i;
                    this.tagVarNumbers.put(fullName, i + 1);
                } else {
                    this.tagVarNumbers.put(fullName, 1);
                    varName = varName + "0";
                }
            }
            return JspUtil.makeJavaIdentifier(varName);
        }

        private String evaluateAttribute(TagHandlerInfo handlerInfo, Node.JspAttribute attr, Node.CustomTag n, String tagHandlerVar) throws JasperException {
            String attrValue = attr.getValue();
            if (attrValue == null) {
                attrValue = n.checkIfAttributeIsJspFragment(attr.getName()) ? this.generateNamedAttributeJspFragment(attr.getNamedAttributeNode(), tagHandlerVar) : this.generateNamedAttributeValue(attr.getNamedAttributeNode());
            }
            String localName = attr.getLocalName();
            Method m = null;
            Class[] c = null;
            if (attr.isDynamic()) {
                c = OBJECT_CLASS;
            } else {
                m = handlerInfo.getSetterMethod(localName);
                if (m == null) {
                    Generator.this.err.jspError((Node)n, "jsp.error.unable.to_find_method", attr.getName());
                }
                c = m.getParameterTypes();
            }
            if (!attr.isExpression()) {
                if (attr.isNamedAttribute()) {
                    if (!n.checkIfAttributeIsJspFragment(attr.getName()) && !attr.isDynamic()) {
                        attrValue = Generator.this.stringInterpreter.convertString(c[0], attrValue, localName, handlerInfo.getPropertyEditorClass(localName), true);
                    }
                } else if (attr.isELInterpreterInput()) {
                    StringBuilder sb = new StringBuilder(64);
                    TagAttributeInfo tai = attr.getTagAttributeInfo();
                    sb.append(this.getJspContextVar());
                    sb.append(".getELContext()");
                    String elContext = sb.toString();
                    if (attr.getEL() != null && attr.getEL().getMapName() != null) {
                        sb.setLength(0);
                        sb.append("new org.apache.jasper.el.ELContextWrapper(");
                        sb.append(elContext);
                        sb.append(',');
                        sb.append(attr.getEL().getMapName());
                        sb.append(')');
                        elContext = sb.toString();
                    }
                    sb.setLength(0);
                    sb.append(n.getStart().toString());
                    sb.append(" '");
                    sb.append(attrValue);
                    sb.append('\'');
                    String mark = sb.toString();
                    sb.setLength(0);
                    if (attr.isDeferredInput() || tai != null && ValueExpression.class.getName().equals(tai.getTypeName())) {
                        sb.append("new org.apache.jasper.el.JspValueExpression(");
                        sb.append(Generator.quote(mark));
                        sb.append(",_jsp_getExpressionFactory().createValueExpression(");
                        if (attr.getEL() != null) {
                            sb.append(elContext);
                            sb.append(',');
                        }
                        sb.append(Generator.quote(attrValue));
                        sb.append(',');
                        sb.append(JspUtil.toJavaSourceTypeFromTld(attr.getExpectedTypeName()));
                        sb.append("))");
                        boolean evaluate = false;
                        if (tai.canBeRequestTime()) {
                            evaluate = true;
                        }
                        if (attr.isDeferredInput()) {
                            evaluate = false;
                        }
                        if (attr.isDeferredInput() && tai.canBeRequestTime()) {
                            boolean bl = evaluate = !attrValue.contains("#{");
                        }
                        if (evaluate) {
                            sb.append(".getValue(");
                            sb.append(this.getJspContextVar());
                            sb.append(".getELContext()");
                            sb.append(')');
                        }
                        attrValue = sb.toString();
                    } else if (attr.isDeferredMethodInput() || tai != null && MethodExpression.class.getName().equals(tai.getTypeName())) {
                        String[] p;
                        sb.append("new org.apache.jasper.el.JspMethodExpression(");
                        sb.append(Generator.quote(mark));
                        sb.append(",_jsp_getExpressionFactory().createMethodExpression(");
                        sb.append(elContext);
                        sb.append(',');
                        sb.append(Generator.quote(attrValue));
                        sb.append(',');
                        sb.append(JspUtil.toJavaSourceTypeFromTld(attr.getExpectedTypeName()));
                        sb.append(',');
                        sb.append("new java.lang.Class[] {");
                        for (String s : p = attr.getParameterTypeNames()) {
                            sb.append(JspUtil.toJavaSourceTypeFromTld(s));
                            sb.append(',');
                        }
                        if (p.length > 0) {
                            sb.setLength(sb.length() - 1);
                        }
                        sb.append("}))");
                        attrValue = sb.toString();
                    } else {
                        String mapName = attr.getEL().getMapName();
                        attrValue = Generator.this.elInterpreter.interpreterCall(Generator.this.ctxt, this.isTagFile, attrValue, c[0], mapName);
                    }
                } else {
                    attrValue = Generator.this.stringInterpreter.convertString(c[0], attrValue, localName, handlerInfo.getPropertyEditorClass(localName), false);
                }
            }
            return attrValue;
        }

        private String generateAliasMap(Node.CustomTag n, String tagHandlerVar) {
            TagVariableInfo[] tagVars = n.getTagVariableInfos();
            String aliasMapVar = null;
            boolean aliasSeen = false;
            for (TagVariableInfo tagVar : tagVars) {
                String nameFrom = tagVar.getNameFromAttribute();
                if (nameFrom == null) continue;
                String aliasedName = n.getAttributeValue(nameFrom);
                if (!aliasSeen) {
                    this.out.printin("java.util.HashMap ");
                    aliasMapVar = tagHandlerVar + "_aliasMap";
                    this.out.print(aliasMapVar);
                    this.out.println(" = new java.util.HashMap();");
                    aliasSeen = true;
                }
                this.out.printin(aliasMapVar);
                this.out.print(".put(");
                this.out.print(Generator.quote(tagVar.getNameGiven()));
                this.out.print(", ");
                this.out.print(Generator.quote(aliasedName));
                this.out.println(");");
            }
            return aliasMapVar;
        }

        private void generateSetters(Node.CustomTag n, String tagHandlerVar, TagHandlerInfo handlerInfo, boolean simpleTag) throws JasperException {
            if (simpleTag) {
                String aliasMapVar = null;
                if (n.isTagFile()) {
                    aliasMapVar = this.generateAliasMap(n, tagHandlerVar);
                }
                this.out.printin(tagHandlerVar);
                if (aliasMapVar == null) {
                    this.out.println(".setJspContext(_jspx_page_context);");
                } else {
                    this.out.print(".setJspContext(_jspx_page_context, ");
                    this.out.print(aliasMapVar);
                    this.out.println(");");
                }
            } else {
                this.out.printin(tagHandlerVar);
                this.out.println(".setPageContext(_jspx_page_context);");
            }
            if (this.isTagFile && this.parent == null) {
                this.out.printin(tagHandlerVar);
                this.out.print(".setParent(");
                this.out.print("new javax.servlet.jsp.tagext.TagAdapter(");
                this.out.println("(javax.servlet.jsp.tagext.SimpleTag) this ));");
            } else if (!simpleTag) {
                this.out.printin(tagHandlerVar);
                this.out.print(".setParent(");
                if (this.parent != null) {
                    if (this.isSimpleTagParent) {
                        this.out.print("new javax.servlet.jsp.tagext.TagAdapter(");
                        this.out.print("(javax.servlet.jsp.tagext.SimpleTag) ");
                        this.out.print(this.parent);
                        this.out.println("));");
                    } else {
                        this.out.print("(javax.servlet.jsp.tagext.Tag) ");
                        this.out.print(this.parent);
                        this.out.println(");");
                    }
                } else {
                    this.out.println("null);");
                }
            } else if (this.parent != null) {
                this.out.printin(tagHandlerVar);
                this.out.print(".setParent(");
                this.out.print(this.parent);
                this.out.println(");");
            }
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; attrs != null && i < attrs.length; ++i) {
                String attrValue = this.evaluateAttribute(handlerInfo, attrs[i], n, tagHandlerVar);
                Mark m = n.getStart();
                this.out.printil("// " + m.getFile() + "(" + m.getLineNumber() + "," + m.getColumnNumber() + ") " + attrs[i].getTagAttributeInfo());
                if (attrs[i].isDynamic()) {
                    this.out.printin(tagHandlerVar);
                    this.out.print(".");
                    this.out.print("setDynamicAttribute(");
                    String uri = attrs[i].getURI();
                    if ("".equals(uri) || uri == null) {
                        this.out.print("null");
                    } else {
                        this.out.print("\"" + attrs[i].getURI() + "\"");
                    }
                    this.out.print(", \"");
                    this.out.print(attrs[i].getLocalName());
                    this.out.print("\", ");
                    this.out.print(attrValue);
                    this.out.println(");");
                    continue;
                }
                this.out.printin(tagHandlerVar);
                this.out.print(".");
                this.out.print(handlerInfo.getSetterMethod(attrs[i].getLocalName()).getName());
                this.out.print("(");
                this.out.print(attrValue);
                this.out.println(");");
            }
            if (n.implementsJspIdConsumer()) {
                this.out.printin(tagHandlerVar);
                this.out.print(".setJspId(\"");
                this.out.print(Generator.this.createJspId());
                this.out.println("\");");
            }
        }

        private String getScopeConstant(String scope) {
            String scopeName = "javax.servlet.jsp.PageContext.PAGE_SCOPE";
            if ("request".equals(scope)) {
                scopeName = "javax.servlet.jsp.PageContext.REQUEST_SCOPE";
            } else if ("session".equals(scope)) {
                scopeName = "javax.servlet.jsp.PageContext.SESSION_SCOPE";
            } else if ("application".equals(scope)) {
                scopeName = "javax.servlet.jsp.PageContext.APPLICATION_SCOPE";
            }
            return scopeName;
        }

        private void generateJspFragment(Node.ChildInfoBase n, String tagHandlerVar) throws JasperException {
            FragmentHelperClass.Fragment fragment = this.fragmentHelperClass.openFragment(n, this.methodNesting);
            ServletWriter outSave = this.out;
            this.out = fragment.getGenBuffer().getOut();
            String tmpParent = this.parent;
            this.parent = "_jspx_parent";
            boolean isSimpleTagParentSave = this.isSimpleTagParent;
            this.isSimpleTagParent = true;
            boolean tmpIsFragment = this.isFragment;
            this.isFragment = true;
            String pushBodyCountVarSave = this.pushBodyCountVar;
            if (this.pushBodyCountVar != null) {
                this.pushBodyCountVar = "_jspx_push_body_count";
            }
            this.visitBody(n);
            this.out = outSave;
            this.parent = tmpParent;
            this.isSimpleTagParent = isSimpleTagParentSave;
            this.isFragment = tmpIsFragment;
            this.pushBodyCountVar = pushBodyCountVarSave;
            this.fragmentHelperClass.closeFragment(fragment, this.methodNesting);
            this.out.print("new " + this.fragmentHelperClass.getClassName() + "( " + fragment.getId() + ", _jspx_page_context, " + tagHandlerVar + ", " + this.pushBodyCountVar + ")");
        }

        public String generateNamedAttributeValue(Node.NamedAttribute n) throws JasperException {
            String varName = n.getTemporaryVariableName();
            Node.Nodes body = n.getBody();
            if (body != null) {
                Node bodyElement;
                boolean templateTextOptimization = false;
                if (body.size() == 1 && (bodyElement = body.getNode(0)) instanceof Node.TemplateText) {
                    templateTextOptimization = true;
                    this.out.printil("java.lang.String " + varName + " = " + Generator.quote(bodyElement.getText()) + ";");
                }
                if (!templateTextOptimization) {
                    this.out.printil("out = _jspx_page_context.pushBody();");
                    this.visitBody(n);
                    this.out.printil("java.lang.String " + varName + " = ((javax.servlet.jsp.tagext.BodyContent)out).getString();");
                    this.out.printil("out = _jspx_page_context.popBody();");
                }
            } else {
                this.out.printil("java.lang.String " + varName + " = \"\";");
            }
            return varName;
        }

        public String generateNamedAttributeJspFragment(Node.NamedAttribute n, String tagHandlerVar) throws JasperException {
            String varName = n.getTemporaryVariableName();
            this.out.printin("javax.servlet.jsp.tagext.JspFragment " + varName + " = ");
            this.generateJspFragment(n, tagHandlerVar);
            this.out.println(";");
            return varName;
        }
    }

    private static class TagHandlerInfo {
        private Map<String, Method> methodMaps;
        private Map<String, Class<?>> propertyEditorMaps;
        private Class<?> tagHandlerClass;

        TagHandlerInfo(Node n, Class<?> tagHandlerClass, ErrorDispatcher err) throws JasperException {
            this.tagHandlerClass = tagHandlerClass;
            this.methodMaps = new HashMap<String, Method>();
            this.propertyEditorMaps = new HashMap();
            try {
                PropertyDescriptor[] pd;
                BeanInfo tagClassInfo = Introspector.getBeanInfo(tagHandlerClass);
                for (PropertyDescriptor propertyDescriptor : pd = tagClassInfo.getPropertyDescriptors()) {
                    if (propertyDescriptor.getWriteMethod() != null) {
                        this.methodMaps.put(propertyDescriptor.getName(), propertyDescriptor.getWriteMethod());
                    }
                    if (propertyDescriptor.getPropertyEditorClass() == null) continue;
                    this.propertyEditorMaps.put(propertyDescriptor.getName(), propertyDescriptor.getPropertyEditorClass());
                }
            }
            catch (IntrospectionException ie) {
                err.jspError(n, (Exception)ie, "jsp.error.introspect.taghandler", tagHandlerClass.getName());
            }
        }

        public Method getSetterMethod(String attrName) {
            return this.methodMaps.get(attrName);
        }

        public Class<?> getPropertyEditorClass(String attrName) {
            return this.propertyEditorMaps.get(attrName);
        }

        public Class<?> getTagHandlerClass() {
            return this.tagHandlerClass;
        }
    }
}

