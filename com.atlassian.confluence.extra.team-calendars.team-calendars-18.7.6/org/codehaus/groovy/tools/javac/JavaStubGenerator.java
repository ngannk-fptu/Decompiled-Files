/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.tools.Utilities;
import org.codehaus.groovy.transform.trait.Traits;

public class JavaStubGenerator {
    private boolean java5 = false;
    private String encoding;
    private boolean requireSuperResolved = false;
    private File outputPath;
    private List<String> toCompile = new ArrayList<String>();
    private ArrayList<MethodNode> propertyMethods = new ArrayList();
    private Map<String, MethodNode> propertyMethodsWithSigs = new HashMap<String, MethodNode>();
    private ArrayList<ConstructorNode> constructors = new ArrayList();
    private ModuleNode currentModule;
    private static final int DEFAULT_BUFFER_SIZE = 32768;

    public JavaStubGenerator(File outputPath, boolean requireSuperResolved, boolean java5, String encoding) {
        this.outputPath = outputPath;
        this.requireSuperResolved = requireSuperResolved;
        this.java5 = java5;
        this.encoding = encoding;
        outputPath.mkdirs();
    }

    public JavaStubGenerator(File outputPath) {
        this(outputPath, false, false, Charset.defaultCharset().name());
    }

    private static void mkdirs(File parent, String relativeFile) {
        int index = relativeFile.lastIndexOf(47);
        if (index == -1) {
            return;
        }
        File dir = new File(parent, relativeFile.substring(0, index));
        dir.mkdirs();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateClass(ClassNode classNode) throws FileNotFoundException {
        if (this.requireSuperResolved && !classNode.getSuperClass().isResolved()) {
            return;
        }
        if (classNode instanceof InnerClassNode) {
            return;
        }
        if ((classNode.getModifiers() & 2) != 0) {
            return;
        }
        String fileName = classNode.getName().replace('.', '/');
        JavaStubGenerator.mkdirs(this.outputPath, fileName);
        this.toCompile.add(fileName);
        File file = new File(this.outputPath, fileName + ".java");
        Charset charset = Charset.forName(this.encoding);
        PrintWriter out = new PrintWriter(new OutputStreamWriter((OutputStream)new BufferedOutputStream(new FileOutputStream(file), 32768), charset));
        try {
            String packageName = classNode.getPackageName();
            if (packageName != null) {
                out.println("package " + packageName + ";\n");
            }
            JavaStubGenerator.printImports(out, classNode);
            this.printClassContents(out, classNode);
        }
        finally {
            try {
                out.close();
            }
            catch (Exception exception) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void printClassContents(PrintWriter out, ClassNode classNode) throws FileNotFoundException {
        if (classNode instanceof InnerClassNode && ((InnerClassNode)classNode).isAnonymous()) {
            return;
        }
        try {
            ClassNode[] interfaces;
            Verifier verifier = new Verifier(){

                @Override
                public void visitClass(ClassNode node) {
                    ArrayList<Statement> savedStatements = new ArrayList<Statement>(node.getObjectInitializerStatements());
                    super.visitClass(node);
                    node.getObjectInitializerStatements().addAll(savedStatements);
                }

                @Override
                public void addCovariantMethods(ClassNode cn) {
                }

                @Override
                protected void addTimeStamp(ClassNode node) {
                }

                @Override
                protected void addInitialization(ClassNode node) {
                }

                @Override
                protected void addPropertyMethod(MethodNode method) {
                    this.doAddMethod(method);
                }

                @Override
                protected void addReturnIfNeeded(MethodNode node) {
                }

                @Override
                protected void addMethod(ClassNode node, boolean shouldBeSynthetic, String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
                    this.doAddMethod(new MethodNode(name, modifiers, returnType, parameters, exceptions, code));
                }

                @Override
                protected void addConstructor(Parameter[] newParams, ConstructorNode ctor, Statement code, ClassNode node) {
                    if (code instanceof ExpressionStatement) {
                        Statement temp = code;
                        code = new BlockStatement();
                        ((BlockStatement)code).addStatement(temp);
                    }
                    ConstructorNode ctrNode = new ConstructorNode(ctor.getModifiers(), newParams, ctor.getExceptions(), code);
                    ctrNode.setDeclaringClass(node);
                    JavaStubGenerator.this.constructors.add(ctrNode);
                }

                @Override
                protected void addDefaultParameters(Verifier.DefaultArgsAction action, MethodNode method) {
                    int i;
                    Parameter[] parameters = method.getParameters();
                    Expression[] saved = new Expression[parameters.length];
                    for (i = 0; i < parameters.length; ++i) {
                        if (!parameters[i].hasInitialExpression()) continue;
                        saved[i] = parameters[i].getInitialExpression();
                    }
                    super.addDefaultParameters(action, method);
                    for (i = 0; i < parameters.length; ++i) {
                        if (saved[i] == null) continue;
                        parameters[i].setInitialExpression(saved[i]);
                    }
                }

                private void doAddMethod(MethodNode method) {
                    String sig = method.getTypeDescriptor();
                    if (JavaStubGenerator.this.propertyMethodsWithSigs.containsKey(sig)) {
                        return;
                    }
                    JavaStubGenerator.this.propertyMethods.add(method);
                    JavaStubGenerator.this.propertyMethodsWithSigs.put(sig, method);
                }

                @Override
                protected void addDefaultConstructor(ClassNode node) {
                }
            };
            verifier.visitClass(classNode);
            this.currentModule = classNode.getModule();
            boolean isInterface = JavaStubGenerator.isInterfaceOrTrait(classNode);
            boolean isEnum = classNode.isEnum();
            boolean isAnnotationDefinition = classNode.isAnnotationDefinition();
            this.printAnnotations(out, classNode);
            JavaStubGenerator.printModifiers(out, classNode.getModifiers() & ~(isInterface ? 1024 : 0) & ~(isEnum ? 1040 : 0));
            if (isInterface) {
                if (isAnnotationDefinition) {
                    out.print("@");
                }
                out.print("interface ");
            } else if (isEnum) {
                out.print("enum ");
            } else {
                out.print("class ");
            }
            String className = classNode.getNameWithoutPackage();
            if (classNode instanceof InnerClassNode) {
                className = className.substring(className.lastIndexOf("$") + 1);
            }
            out.println(className);
            this.printGenericsBounds(out, classNode, true);
            ClassNode superClass = classNode.getUnresolvedSuperClass(false);
            if (!isInterface && !isEnum) {
                out.print("  extends ");
                this.printType(out, superClass);
            }
            if ((interfaces = classNode.getInterfaces()) != null && interfaces.length > 0 && !isAnnotationDefinition) {
                if (isInterface) {
                    out.println("  extends");
                } else {
                    out.println("  implements");
                }
                for (int i = 0; i < interfaces.length - 1; ++i) {
                    out.print("    ");
                    this.printType(out, interfaces[i]);
                    out.print(",");
                }
                out.print("    ");
                this.printType(out, interfaces[interfaces.length - 1]);
            }
            out.println(" {");
            this.printFields(out, classNode);
            this.printMethods(out, classNode, isEnum);
            Iterator<InnerClassNode> inner = classNode.getInnerClasses();
            while (inner.hasNext()) {
                this.propertyMethods.clear();
                this.propertyMethodsWithSigs.clear();
                this.constructors.clear();
                this.printClassContents(out, inner.next());
            }
            out.println("}");
        }
        finally {
            this.propertyMethods.clear();
            this.propertyMethodsWithSigs.clear();
            this.constructors.clear();
            this.currentModule = null;
        }
    }

    private void printMethods(PrintWriter out, ClassNode classNode, boolean isEnum) {
        if (!isEnum) {
            this.printConstructors(out, classNode);
        }
        List methods = (List)this.propertyMethods.clone();
        methods.addAll(classNode.getMethods());
        for (MethodNode method : methods) {
            if (isEnum && method.isSynthetic()) {
                String name = method.getName();
                Parameter[] params = method.getParameters();
                if (name.equals("values") && params.length == 0 || name.equals("valueOf") && params.length == 1 && params[0].getType().equals(ClassHelper.STRING_TYPE)) continue;
            }
            this.printMethod(out, classNode, method);
        }
        for (ClassNode node : classNode.getAllInterfaces()) {
            if (!Traits.isTrait(node)) continue;
            List<MethodNode> traitMethods = node.getMethods();
            for (MethodNode traitMethod : traitMethods) {
                MethodNode method = classNode.getMethod(traitMethod.getName(), traitMethod.getParameters());
                if (method != null) continue;
                for (MethodNode methodNode : this.propertyMethods) {
                    boolean sameParams;
                    if (!methodNode.getName().equals(traitMethod.getName()) || !(sameParams = JavaStubGenerator.sameParameterTypes(methodNode))) continue;
                    method = methodNode;
                    break;
                }
                if (method != null) continue;
                this.printMethod(out, classNode, traitMethod);
            }
        }
    }

    private static boolean sameParameterTypes(MethodNode methodNode) {
        Parameter[] b;
        boolean sameParams;
        Parameter[] a = methodNode.getParameters();
        boolean bl = sameParams = a.length == (b = methodNode.getParameters()).length;
        if (sameParams) {
            for (int i = 0; i < a.length; ++i) {
                if (a[i].getType().equals(b[i].getType())) continue;
                sameParams = false;
                break;
            }
        }
        return sameParams;
    }

    private void printConstructors(PrintWriter out, ClassNode classNode) {
        List constrs = (List)this.constructors.clone();
        if (constrs != null) {
            constrs.addAll(classNode.getDeclaredConstructors());
            for (ConstructorNode constr : constrs) {
                this.printConstructor(out, classNode, constr);
            }
        }
    }

    private void printFields(PrintWriter out, ClassNode classNode) {
        boolean isInterface = JavaStubGenerator.isInterfaceOrTrait(classNode);
        List<FieldNode> fields = classNode.getFields();
        if (fields == null) {
            return;
        }
        ArrayList<FieldNode> enumFields = new ArrayList<FieldNode>(fields.size());
        ArrayList<FieldNode> normalFields = new ArrayList<FieldNode>(fields.size());
        for (FieldNode field : fields) {
            boolean isSynthetic;
            boolean bl = isSynthetic = (field.getModifiers() & 0x1000) != 0;
            if (field.isEnum()) {
                enumFields.add(field);
                continue;
            }
            if (isSynthetic) continue;
            normalFields.add(field);
        }
        JavaStubGenerator.printEnumFields(out, enumFields);
        for (FieldNode normalField : normalFields) {
            this.printField(out, normalField, isInterface);
        }
    }

    private static void printEnumFields(PrintWriter out, List<FieldNode> fields) {
        if (!fields.isEmpty()) {
            boolean first = true;
            for (FieldNode field : fields) {
                if (!first) {
                    out.print(", ");
                } else {
                    first = false;
                }
                out.print(field.getName());
            }
        }
        out.println(";");
    }

    private void printField(PrintWriter out, FieldNode fieldNode, boolean isInterface) {
        if ((fieldNode.getModifiers() & 2) != 0) {
            return;
        }
        this.printAnnotations(out, fieldNode);
        if (!isInterface) {
            JavaStubGenerator.printModifiers(out, fieldNode.getModifiers());
        }
        ClassNode type = fieldNode.getType();
        this.printType(out, type);
        out.print(" ");
        out.print(fieldNode.getName());
        if (isInterface || (fieldNode.getModifiers() & 0x10) != 0) {
            out.print(" = ");
            Expression valueExpr = fieldNode.getInitialValueExpression();
            if (valueExpr instanceof ConstantExpression) {
                valueExpr = Verifier.transformToPrimitiveConstantIfPossible((ConstantExpression)valueExpr);
            }
            if (valueExpr instanceof ConstantExpression && fieldNode.isStatic() && fieldNode.isFinal() && ClassHelper.isStaticConstantInitializerType(valueExpr.getType()) && valueExpr.getType().equals(fieldNode.getType())) {
                if (ClassHelper.STRING_TYPE.equals(valueExpr.getType())) {
                    out.print(JavaStubGenerator.formatString(valueExpr.getText()));
                } else if (ClassHelper.char_TYPE.equals(valueExpr.getType())) {
                    out.print("'" + valueExpr.getText() + "'");
                } else {
                    ClassNode constantType = valueExpr.getType();
                    out.print('(');
                    this.printType(out, type);
                    out.print(") ");
                    out.print(valueExpr.getText());
                    if (ClassHelper.Long_TYPE.equals(ClassHelper.getWrapper(constantType))) {
                        out.print('L');
                    }
                }
            } else if (ClassHelper.isPrimitiveType(type)) {
                String val = type == ClassHelper.boolean_TYPE ? "false" : "0";
                out.print("new " + ClassHelper.getWrapper(type) + "((" + type + ")" + val + ")");
            } else {
                out.print("null");
            }
        }
        out.println(";");
    }

    private static String formatChar(String ch) {
        return "'" + JavaStubGenerator.escapeSpecialChars("" + ch.charAt(0)) + "'";
    }

    private static String formatString(String s) {
        return "\"" + JavaStubGenerator.escapeSpecialChars(s) + "\"";
    }

    private static ConstructorCallExpression getConstructorCallExpression(ConstructorNode constructorNode) {
        Statement code = constructorNode.getCode();
        if (!(code instanceof BlockStatement)) {
            return null;
        }
        BlockStatement block = (BlockStatement)code;
        List<Statement> stats = block.getStatements();
        if (stats == null || stats.isEmpty()) {
            return null;
        }
        Statement stat = stats.get(0);
        if (!(stat instanceof ExpressionStatement)) {
            return null;
        }
        Expression expr = ((ExpressionStatement)stat).getExpression();
        if (!(expr instanceof ConstructorCallExpression)) {
            return null;
        }
        return (ConstructorCallExpression)expr;
    }

    private void printConstructor(PrintWriter out, ClassNode clazz, ConstructorNode constructorNode) {
        this.printAnnotations(out, constructorNode);
        out.print("public ");
        String className = clazz.getNameWithoutPackage();
        if (clazz instanceof InnerClassNode) {
            className = className.substring(className.lastIndexOf("$") + 1);
        }
        out.println(className);
        this.printParams(out, constructorNode);
        ConstructorCallExpression constrCall = JavaStubGenerator.getConstructorCallExpression(constructorNode);
        if (constrCall == null || !constrCall.isSpecialCall()) {
            out.println(" {}");
        } else {
            out.println(" {");
            this.printSpecialConstructorArgs(out, constructorNode, constrCall);
            out.println("}");
        }
    }

    private static Parameter[] selectAccessibleConstructorFromSuper(ConstructorNode node) {
        ClassNode type = node.getDeclaringClass();
        ClassNode superType = type.getUnresolvedSuperClass();
        Parameter[] bestMatch = null;
        for (ConstructorNode c : superType.getDeclaredConstructors()) {
            if (!c.isPublic() && !c.isProtected()) continue;
            Parameter[] parameters = c.getParameters();
            Parameter[] copy = new Parameter[parameters.length];
            for (int i = 0; i < copy.length; ++i) {
                Parameter orig = parameters[i];
                copy[i] = new Parameter(orig.getOriginType().getPlainNodeReference(), orig.getName());
            }
            if (JavaStubGenerator.noExceptionToAvoid(node, c)) {
                return copy;
            }
            if (bestMatch != null) continue;
            bestMatch = copy;
        }
        if (bestMatch != null) {
            return bestMatch;
        }
        if (superType.isPrimaryClassNode()) {
            return Parameter.EMPTY_ARRAY;
        }
        return null;
    }

    private static boolean noExceptionToAvoid(ConstructorNode fromStub, ConstructorNode fromSuper) {
        ClassNode[] superExceptions = fromSuper.getExceptions();
        if (superExceptions == null || superExceptions.length == 0) {
            return true;
        }
        ClassNode[] stubExceptions = fromStub.getExceptions();
        if (stubExceptions == null || stubExceptions.length == 0) {
            return false;
        }
        block0: for (int i = 0; i < superExceptions.length; ++i) {
            ClassNode superExc = superExceptions[i];
            for (ClassNode stub : stubExceptions) {
                if (stub.isDerivedFrom(superExc)) continue block0;
            }
            return false;
        }
        return true;
    }

    private void printSpecialConstructorArgs(PrintWriter out, ConstructorNode node, ConstructorCallExpression constrCall) {
        Parameter[] params = JavaStubGenerator.selectAccessibleConstructorFromSuper(node);
        if (params != null) {
            out.print("super (");
            for (int i = 0; i < params.length; ++i) {
                this.printDefaultValue(out, params[i].getType());
                if (i + 1 >= params.length) continue;
                out.print(", ");
            }
            out.println(");");
            return;
        }
        Expression arguments = constrCall.getArguments();
        if (constrCall.isSuperCall()) {
            out.print("super(");
        } else {
            out.print("this(");
        }
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argumentListExpression = (ArgumentListExpression)arguments;
            List<Expression> args = argumentListExpression.getExpressions();
            for (Expression arg : args) {
                if (arg instanceof ConstantExpression) {
                    ConstantExpression expression = (ConstantExpression)arg;
                    Object o = expression.getValue();
                    if (o instanceof String) {
                        out.print("(String)null");
                    } else {
                        out.print(expression.getText());
                    }
                } else {
                    ClassNode type = JavaStubGenerator.getConstructorArgumentType(arg, node);
                    this.printDefaultValue(out, type);
                }
                if (arg == args.get(args.size() - 1)) continue;
                out.print(", ");
            }
        }
        out.println(");");
    }

    private static ClassNode getConstructorArgumentType(Expression arg, ConstructorNode node) {
        if (!(arg instanceof VariableExpression)) {
            return arg.getType();
        }
        VariableExpression vexp = (VariableExpression)arg;
        String name = vexp.getName();
        for (Parameter param : node.getParameters()) {
            if (!param.getName().equals(name)) continue;
            return param.getType();
        }
        return vexp.getType();
    }

    private void printMethod(PrintWriter out, ClassNode clazz, MethodNode methodNode) {
        if (methodNode.getName().equals("<clinit>")) {
            return;
        }
        if (methodNode.isPrivate() || !Utilities.isJavaIdentifier(methodNode.getName())) {
            return;
        }
        if (methodNode.isSynthetic() && methodNode.getName().equals("$getStaticMetaClass")) {
            return;
        }
        this.printAnnotations(out, methodNode);
        if (!JavaStubGenerator.isInterfaceOrTrait(clazz)) {
            int modifiers = methodNode.getModifiers();
            if (JavaStubGenerator.isDefaultTraitImpl(methodNode)) {
                modifiers ^= 0x400;
            }
            JavaStubGenerator.printModifiers(out, modifiers & ~(clazz.isEnum() ? 1024 : 0));
        }
        JavaStubGenerator.printGenericsBounds(out, methodNode.getGenericsTypes());
        out.print(" ");
        this.printType(out, methodNode.getReturnType());
        out.print(" ");
        out.print(methodNode.getName());
        this.printParams(out, methodNode);
        ClassNode[] exceptions = methodNode.getExceptions();
        for (int i = 0; i < exceptions.length; ++i) {
            ClassNode exception = exceptions[i];
            if (i == 0) {
                out.print("throws ");
            } else {
                out.print(", ");
            }
            this.printType(out, exception);
        }
        if (Traits.isTrait(clazz)) {
            out.println(";");
        } else if (JavaStubGenerator.isAbstract(methodNode) && !clazz.isEnum()) {
            Statement fs;
            if (clazz.isAnnotationDefinition() && methodNode.hasAnnotationDefault() && (fs = methodNode.getFirstStatement()) instanceof ExpressionStatement) {
                boolean classReturn;
                ExpressionStatement es = (ExpressionStatement)fs;
                Expression re = es.getExpression();
                out.print(" default ");
                ClassNode rt = methodNode.getReturnType();
                boolean bl = classReturn = ClassHelper.CLASS_Type.equals(rt) || rt.isArray() && ClassHelper.CLASS_Type.equals(rt.getComponentType());
                if (re instanceof ListExpression) {
                    out.print("{ ");
                    ListExpression le = (ListExpression)re;
                    boolean first = true;
                    for (Expression expression : le.getExpressions()) {
                        if (first) {
                            first = false;
                        } else {
                            out.print(", ");
                        }
                        JavaStubGenerator.printValue(out, expression, classReturn);
                    }
                    out.print(" }");
                } else {
                    JavaStubGenerator.printValue(out, re, classReturn);
                }
            }
            out.println(";");
        } else {
            out.print(" { ");
            ClassNode retType = methodNode.getReturnType();
            this.printReturn(out, retType);
            out.println("}");
        }
    }

    private static boolean isAbstract(MethodNode methodNode) {
        if (JavaStubGenerator.isDefaultTraitImpl(methodNode)) {
            return false;
        }
        return (methodNode.getModifiers() & 0x400) != 0;
    }

    private static boolean isDefaultTraitImpl(MethodNode methodNode) {
        return Traits.isTrait(methodNode.getDeclaringClass()) && Traits.hasDefaultImplementation(methodNode);
    }

    private static void printValue(PrintWriter out, Expression re, boolean assumeClass) {
        if (assumeClass) {
            if (re.getType().getName().equals("groovy.lang.Closure")) {
                out.print("groovy.lang.Closure.class");
                return;
            }
            String className = re.getText();
            out.print(className);
            if (!className.endsWith(".class")) {
                out.print(".class");
            }
        } else if (re instanceof ConstantExpression) {
            ConstantExpression ce = (ConstantExpression)re;
            Object value = ce.getValue();
            if (ClassHelper.STRING_TYPE.equals(ce.getType())) {
                out.print(JavaStubGenerator.formatString((String)value));
            } else if (ClassHelper.char_TYPE.equals(ce.getType()) || ClassHelper.Character_TYPE.equals(ce.getType())) {
                out.print(JavaStubGenerator.formatChar(value.toString()));
            } else if (ClassHelper.long_TYPE.equals(ce.getType())) {
                out.print("" + value + "L");
            } else if (ClassHelper.float_TYPE.equals(ce.getType())) {
                out.print("" + value + "f");
            } else if (ClassHelper.double_TYPE.equals(ce.getType())) {
                out.print("" + value + "d");
            } else {
                out.print(re.getText());
            }
        } else {
            out.print(re.getText());
        }
    }

    private void printReturn(PrintWriter out, ClassNode retType) {
        String retName = retType.getName();
        if (!retName.equals("void")) {
            out.print("return ");
            this.printDefaultValue(out, retType);
            out.print(";");
        }
    }

    private void printDefaultValue(PrintWriter out, ClassNode type) {
        if (type.redirect() != ClassHelper.OBJECT_TYPE && type.redirect() != ClassHelper.boolean_TYPE) {
            out.print("(");
            this.printType(out, type);
            out.print(")");
        }
        if (ClassHelper.isPrimitiveType(type)) {
            if (type == ClassHelper.boolean_TYPE) {
                out.print("false");
            } else {
                out.print("0");
            }
        } else {
            out.print("null");
        }
    }

    private void printType(PrintWriter out, ClassNode type) {
        if (type.isArray()) {
            this.printType(out, type.getComponentType());
            out.print("[]");
        } else if (this.java5 && type.isGenericsPlaceHolder()) {
            out.print(type.getGenericsTypes()[0].getName());
        } else {
            this.printGenericsBounds(out, type, false);
        }
    }

    private void printTypeName(PrintWriter out, ClassNode type) {
        if (ClassHelper.isPrimitiveType(type)) {
            if (type == ClassHelper.boolean_TYPE) {
                out.print("boolean");
            } else if (type == ClassHelper.char_TYPE) {
                out.print("char");
            } else if (type == ClassHelper.int_TYPE) {
                out.print("int");
            } else if (type == ClassHelper.short_TYPE) {
                out.print("short");
            } else if (type == ClassHelper.long_TYPE) {
                out.print("long");
            } else if (type == ClassHelper.float_TYPE) {
                out.print("float");
            } else if (type == ClassHelper.double_TYPE) {
                out.print("double");
            } else if (type == ClassHelper.byte_TYPE) {
                out.print("byte");
            } else {
                out.print("void");
            }
        } else {
            String name = type.getName();
            ClassNode alias = this.currentModule.getImportType(name);
            if (alias != null) {
                name = alias.getName();
            }
            out.print(name.replace('$', '.'));
        }
    }

    private void printGenericsBounds(PrintWriter out, ClassNode type, boolean skipName) {
        if (!skipName) {
            this.printTypeName(out, type);
        }
        if (!this.java5) {
            return;
        }
        if (!ClassHelper.isCachedType(type)) {
            JavaStubGenerator.printGenericsBounds(out, type.getGenericsTypes());
        }
    }

    private static void printGenericsBounds(PrintWriter out, GenericsType[] genericsTypes) {
        if (genericsTypes == null || genericsTypes.length == 0) {
            return;
        }
        out.print('<');
        for (int i = 0; i < genericsTypes.length; ++i) {
            if (i != 0) {
                out.print(", ");
            }
            out.print(genericsTypes[i].toString().replace("$", "."));
        }
        out.print('>');
    }

    private void printParams(PrintWriter out, MethodNode methodNode) {
        out.print("(");
        Parameter[] parameters = methodNode.getParameters();
        if (parameters != null && parameters.length != 0) {
            int lastIndex = parameters.length - 1;
            boolean vararg = parameters[lastIndex].getType().isArray();
            for (int i = 0; i != parameters.length; ++i) {
                this.printAnnotations(out, parameters[i]);
                if (i == lastIndex && vararg) {
                    this.printType(out, parameters[i].getType().getComponentType());
                    out.print("...");
                } else {
                    this.printType(out, parameters[i].getType());
                }
                out.print(" ");
                out.print(parameters[i].getName());
                if (i + 1 >= parameters.length) continue;
                out.print(", ");
            }
        }
        out.print(")");
    }

    private void printAnnotations(PrintWriter out, AnnotatedNode annotated) {
        if (!this.java5) {
            return;
        }
        for (AnnotationNode annotation : annotated.getAnnotations()) {
            this.printAnnotation(out, annotation);
        }
    }

    private void printAnnotation(PrintWriter out, AnnotationNode annotation) {
        out.print("@" + annotation.getClassNode().getName().replace('$', '.') + "(");
        boolean first = true;
        Map<String, Expression> members = annotation.getMembers();
        for (Map.Entry<String, Expression> entry : members.entrySet()) {
            String key = entry.getKey();
            if (first) {
                first = false;
            } else {
                out.print(", ");
            }
            out.print(key + "=" + this.getAnnotationValue(entry.getValue()).replace('$', '.'));
        }
        out.print(") ");
    }

    private String getAnnotationValue(Object memberValue) {
        String val = "null";
        if (memberValue instanceof ListExpression) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            ListExpression le = (ListExpression)memberValue;
            for (Expression e : le.getExpressions()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(this.getAnnotationValue(e));
            }
            sb.append("}");
            val = sb.toString();
        } else if (memberValue instanceof ConstantExpression) {
            ConstantExpression ce = (ConstantExpression)memberValue;
            Object constValue = ce.getValue();
            if (constValue instanceof AnnotationNode) {
                StringWriter writer = new StringWriter();
                PrintWriter out = new PrintWriter(writer);
                this.printAnnotation(out, (AnnotationNode)constValue);
                val = writer.toString();
            } else {
                val = constValue instanceof Number || constValue instanceof Boolean ? constValue.toString() : "\"" + JavaStubGenerator.escapeSpecialChars(constValue.toString()) + "\"";
            }
        } else if (memberValue instanceof PropertyExpression) {
            val = ((Expression)memberValue).getText();
        } else if (memberValue instanceof VariableExpression) {
            val = ((Expression)memberValue).getText();
            ImportNode alias = this.currentModule.getStaticImports().get(val);
            if (alias != null) {
                val = alias.getClassName() + "." + alias.getFieldName();
            }
        } else if (memberValue instanceof ClosureExpression) {
            val = "groovy.lang.Closure.class";
        } else if (memberValue instanceof ClassExpression) {
            val = ((Expression)memberValue).getText() + ".class";
        }
        return val;
    }

    private static void printModifiers(PrintWriter out, int modifiers) {
        if ((modifiers & 1) != 0) {
            out.print("public ");
        }
        if ((modifiers & 4) != 0) {
            out.print("protected ");
        }
        if ((modifiers & 2) != 0) {
            out.print("private ");
        }
        if ((modifiers & 8) != 0) {
            out.print("static ");
        }
        if ((modifiers & 0x20) != 0) {
            out.print("synchronized ");
        }
        if ((modifiers & 0x10) != 0) {
            out.print("final ");
        }
        if ((modifiers & 0x400) != 0) {
            out.print("abstract ");
        }
    }

    private static void printImports(PrintWriter out, ClassNode classNode) {
        ArrayList<String> imports = new ArrayList<String>();
        ModuleNode moduleNode = classNode.getModule();
        for (ImportNode importNode : moduleNode.getStarImports()) {
            imports.add(importNode.getPackageName());
        }
        for (ImportNode importNode : moduleNode.getImports()) {
            if (importNode.getAlias() != null) continue;
            imports.add(importNode.getType().getName());
        }
        imports.addAll(Arrays.asList(ResolveVisitor.DEFAULT_IMPORTS));
        for (Map.Entry entry : moduleNode.getStaticImports().entrySet()) {
            if (!((String)entry.getKey()).equals(((ImportNode)entry.getValue()).getFieldName())) continue;
            imports.add("static " + ((ImportNode)entry.getValue()).getType().getName() + "." + (String)entry.getKey());
        }
        for (Map.Entry entry : moduleNode.getStaticStarImports().entrySet()) {
            imports.add("static " + ((ImportNode)entry.getValue()).getType().getName() + ".");
        }
        for (String string : imports) {
            String s = ("import " + string + (string.charAt(string.length() - 1) == '.' ? "*;" : ";")).replace('$', '.');
            out.println(s);
        }
        out.println();
    }

    public void clean() {
        for (String path : this.toCompile) {
            new File(this.outputPath, path + ".java").delete();
        }
    }

    private static String escapeSpecialChars(String value) {
        return InvokerHelper.escapeBackslashes(value).replace("\"", "\\\"");
    }

    private static boolean isInterfaceOrTrait(ClassNode cn) {
        return cn.isInterface() || Traits.isTrait(cn);
    }
}

