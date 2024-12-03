/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import groovy.lang.DelegatesTo;
import groovy.lang.IntRange;
import groovy.lang.Range;
import groovy.transform.TypeChecked;
import groovy.transform.TypeCheckingMode;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.ClosureSignatureHint;
import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.ReturnAdder;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.TokenUtil;
import org.codehaus.groovy.transform.stc.DefaultTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.DelegationMetadata;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.PropertyLookupVisitor;
import org.codehaus.groovy.transform.stc.Receiver;
import org.codehaus.groovy.transform.stc.SecondPassExpression;
import org.codehaus.groovy.transform.stc.SharedVariableCollector;
import org.codehaus.groovy.transform.stc.SignatureCodec;
import org.codehaus.groovy.transform.stc.SignatureCodecVersion1;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;
import org.codehaus.groovy.transform.stc.TraitTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.TypeCheckingContext;
import org.codehaus.groovy.transform.stc.TypeCheckingExtension;
import org.codehaus.groovy.transform.stc.UnionTypeClassNode;
import org.codehaus.groovy.transform.trait.Traits;
import org.codehaus.groovy.util.ListHashMap;

public class StaticTypeCheckingVisitor
extends ClassCodeVisitorSupport {
    private static final boolean DEBUG_GENERATED_CODE = Boolean.valueOf(System.getProperty("groovy.stc.debug", "false"));
    private static final AtomicLong UNIQUE_LONG = new AtomicLong();
    protected static final Object ERROR_COLLECTOR = ErrorCollector.class;
    protected static final ClassNode ITERABLE_TYPE = ClassHelper.make(Iterable.class);
    protected static final List<MethodNode> EMPTY_METHODNODE_LIST = Collections.emptyList();
    protected static final ClassNode TYPECHECKED_CLASSNODE = ClassHelper.make(TypeChecked.class);
    protected static final ClassNode[] TYPECHECKING_ANNOTATIONS = new ClassNode[]{TYPECHECKED_CLASSNODE};
    protected static final ClassNode TYPECHECKING_INFO_NODE = ClassHelper.make(TypeChecked.TypeCheckingInfo.class);
    protected static final ClassNode DGM_CLASSNODE = ClassHelper.make(DefaultGroovyMethods.class);
    protected static final int CURRENT_SIGNATURE_PROTOCOL_VERSION = 1;
    protected static final Expression CURRENT_SIGNATURE_PROTOCOL = new ConstantExpression(1, true);
    protected static final MethodNode GET_DELEGATE = ClassHelper.CLOSURE_TYPE.getGetterMethod("getDelegate");
    protected static final MethodNode GET_OWNER = ClassHelper.CLOSURE_TYPE.getGetterMethod("getOwner");
    protected static final MethodNode GET_THISOBJECT = ClassHelper.CLOSURE_TYPE.getGetterMethod("getThisObject");
    protected static final ClassNode DELEGATES_TO = ClassHelper.make(DelegatesTo.class);
    protected static final ClassNode DELEGATES_TO_TARGET = ClassHelper.make(DelegatesTo.Target.class);
    protected static final ClassNode LINKEDHASHMAP_CLASSNODE = ClassHelper.make(LinkedHashMap.class);
    protected static final ClassNode CLOSUREPARAMS_CLASSNODE = ClassHelper.make(ClosureParams.class);
    protected static final ClassNode MAP_ENTRY_TYPE = ClassHelper.make(Map.Entry.class);
    protected static final ClassNode ENUMERATION_TYPE = ClassHelper.make(Enumeration.class);
    public static final Statement GENERATED_EMPTY_STATEMENT = new EmptyStatement();
    public static final MethodNode CLOSURE_CALL_NO_ARG = ClassHelper.CLOSURE_TYPE.getDeclaredMethod("call", Parameter.EMPTY_ARRAY);
    public static final MethodNode CLOSURE_CALL_ONE_ARG = ClassHelper.CLOSURE_TYPE.getDeclaredMethod("call", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "arg")});
    public static final MethodNode CLOSURE_CALL_VARGS = ClassHelper.CLOSURE_TYPE.getDeclaredMethod("call", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE.makeArray(), "args")});
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    protected final ReturnAdder.ReturnStatementListener returnListener = new ReturnAdder.ReturnStatementListener(){

        @Override
        public void returnStatementAdded(ReturnStatement returnStatement) {
            if (returnStatement.getExpression() == ConstantExpression.NULL) {
                return;
            }
            if (StaticTypeCheckingVisitor.isNullConstant(returnStatement.getExpression())) {
                return;
            }
            StaticTypeCheckingVisitor.this.checkReturnType(returnStatement);
            if (StaticTypeCheckingVisitor.this.typeCheckingContext.getEnclosingClosure() != null) {
                StaticTypeCheckingVisitor.this.addClosureReturnType(StaticTypeCheckingVisitor.this.getType(returnStatement.getExpression()));
            } else if (StaticTypeCheckingVisitor.this.typeCheckingContext.getEnclosingMethod() == null) {
                throw new GroovyBugError("Unexpected return statement at " + returnStatement.getLineNumber() + ":" + returnStatement.getColumnNumber() + " " + returnStatement.getText());
            }
        }
    };
    protected final ReturnAdder returnAdder = new ReturnAdder(this.returnListener);
    protected TypeCheckingContext typeCheckingContext = new TypeCheckingContext(this);
    protected DefaultTypeCheckingExtension extension = this.createDefaultTypeCheckingExtension();
    protected FieldNode currentField;
    protected PropertyNode currentProperty;

    public StaticTypeCheckingVisitor(SourceUnit source, ClassNode cn) {
        this.typeCheckingContext.source = source;
        this.typeCheckingContext.pushEnclosingClassNode(cn);
        this.typeCheckingContext.pushErrorCollector(source.getErrorCollector());
        this.typeCheckingContext.pushTemporaryTypeInfo();
    }

    private DefaultTypeCheckingExtension createDefaultTypeCheckingExtension() {
        DefaultTypeCheckingExtension ext = new DefaultTypeCheckingExtension(this);
        ext.addHandler(new TraitTypeCheckingExtension(this));
        return ext;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.typeCheckingContext.source;
    }

    public void initialize() {
        this.extension.setup();
    }

    public TypeCheckingContext getTypeCheckingContext() {
        return this.typeCheckingContext;
    }

    public void addTypeCheckingExtension(TypeCheckingExtension extension) {
        this.extension.addHandler(extension);
    }

    public void setCompilationUnit(CompilationUnit cu) {
        this.typeCheckingContext.setCompilationUnit(cu);
    }

    @Override
    public void visitClass(ClassNode node) {
        if (this.shouldSkipClassNode(node)) {
            return;
        }
        if (this.extension.beforeVisitClass(node)) {
            this.extension.afterVisitClass(node);
            return;
        }
        Object type = node.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        if (type != null) {
            this.typeCheckingContext.pushErrorCollector();
        }
        this.typeCheckingContext.pushEnclosingClassNode(node);
        Set<MethodNode> oldVisitedMethod = this.typeCheckingContext.alreadyVisitedMethods;
        this.typeCheckingContext.alreadyVisitedMethods = new LinkedHashSet<MethodNode>();
        super.visitClass(node);
        Iterator<InnerClassNode> innerClasses = node.getInnerClasses();
        while (innerClasses.hasNext()) {
            InnerClassNode innerClassNode = innerClasses.next();
            this.visitClass(innerClassNode);
        }
        this.typeCheckingContext.alreadyVisitedMethods = oldVisitedMethod;
        node.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, node);
        for (MethodNode methodNode : node.getMethods()) {
            methodNode.putNodeMetaData(StaticTypeCheckingVisitor.class, Boolean.TRUE);
        }
        for (ConstructorNode constructorNode : node.getDeclaredConstructors()) {
            constructorNode.putNodeMetaData(StaticTypeCheckingVisitor.class, Boolean.TRUE);
        }
        this.extension.afterVisitClass(node);
    }

    protected boolean shouldSkipClassNode(ClassNode node) {
        return this.isSkipMode(node);
    }

    protected ClassNode[] getTypeCheckingAnnotations() {
        return TYPECHECKING_ANNOTATIONS;
    }

    public boolean isSkipMode(AnnotatedNode node) {
        if (node == null) {
            return false;
        }
        for (ClassNode tca : this.getTypeCheckingAnnotations()) {
            List<AnnotationNode> annotations = node.getAnnotations(tca);
            if (annotations == null) continue;
            for (AnnotationNode annotation : annotations) {
                Expression value = annotation.getMember("value");
                if (value == null) continue;
                if (value instanceof ConstantExpression) {
                    ConstantExpression ce = (ConstantExpression)value;
                    if (!TypeCheckingMode.SKIP.toString().equals(ce.getValue().toString())) continue;
                    return true;
                }
                if (!(value instanceof PropertyExpression)) continue;
                PropertyExpression pe = (PropertyExpression)value;
                if (!TypeCheckingMode.SKIP.toString().equals(pe.getPropertyAsString())) continue;
                return true;
            }
        }
        if (node instanceof MethodNode) {
            return this.isSkipMode(node.getDeclaringClass());
        }
        return this.isSkippedInnerClass(node);
    }

    protected boolean isSkippedInnerClass(AnnotatedNode node) {
        if (!(node instanceof InnerClassNode)) {
            return false;
        }
        MethodNode enclosingMethod = ((InnerClassNode)node).getEnclosingMethod();
        return enclosingMethod != null && this.isSkipMode(enclosingMethod);
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
        ClassNode cn = (ClassNode)expression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        if (cn == null) {
            this.storeType(expression, this.getType(expression));
        }
    }

    private static void addPrivateFieldOrMethodAccess(Expression source, ClassNode cn, StaticTypesMarker type, ASTNode accessedMember) {
        LinkedHashSet<ASTNode> set = (LinkedHashSet<ASTNode>)cn.getNodeMetaData((Object)type);
        if (set == null) {
            set = new LinkedHashSet<ASTNode>();
            cn.putNodeMetaData((Object)type, set);
        }
        set.add(accessedMember);
        source.putNodeMetaData((Object)type, accessedMember);
    }

    private void checkOrMarkPrivateAccess(Expression source, FieldNode fn, boolean lhsOfAssignment) {
        if (fn != null && Modifier.isPrivate(fn.getModifiers()) && (fn.getDeclaringClass() != this.typeCheckingContext.getEnclosingClassNode() || this.typeCheckingContext.getEnclosingClosure() != null) && fn.getDeclaringClass().getModule() == this.typeCheckingContext.getEnclosingClassNode().getModule()) {
            StaticTypesMarker marker = lhsOfAssignment ? StaticTypesMarker.PV_FIELDS_MUTATION : StaticTypesMarker.PV_FIELDS_ACCESS;
            StaticTypeCheckingVisitor.addPrivateFieldOrMethodAccess(source, fn.getDeclaringClass(), marker, fn);
        }
    }

    private void checkOrMarkPrivateAccess(Expression source, MethodNode mn) {
        ClassNode enclosingClassNode;
        if (mn == null) {
            return;
        }
        ClassNode declaringClass = mn.getDeclaringClass();
        if (declaringClass != (enclosingClassNode = this.typeCheckingContext.getEnclosingClassNode()) || this.typeCheckingContext.getEnclosingClosure() != null) {
            int mods = mn.getModifiers();
            boolean sameModule = declaringClass.getModule() == enclosingClassNode.getModule();
            String packageName = declaringClass.getPackageName();
            if (packageName == null) {
                packageName = "";
            }
            if (Modifier.isPrivate(mods) && sameModule || Modifier.isProtected(mods) && !packageName.equals(enclosingClassNode.getPackageName())) {
                StaticTypeCheckingVisitor.addPrivateFieldOrMethodAccess(source, sameModule ? declaringClass : enclosingClassNode, StaticTypesMarker.PV_METHODS_ACCESS, mn);
            }
        }
    }

    private void checkSuperCallFromClosure(Expression call, MethodNode directCallTarget) {
        VariableExpression var;
        Expression objectExpression;
        if (call instanceof MethodCallExpression && this.typeCheckingContext.getEnclosingClosure() != null && (objectExpression = ((MethodCallExpression)call).getObjectExpression()) instanceof VariableExpression && (var = (VariableExpression)objectExpression).isSuperExpression()) {
            ClassNode current = this.typeCheckingContext.getEnclosingClassNode();
            LinkedList<MethodNode> list = (LinkedList<MethodNode>)current.getNodeMetaData((Object)StaticTypesMarker.SUPER_MOP_METHOD_REQUIRED);
            if (list == null) {
                list = new LinkedList<MethodNode>();
                current.putNodeMetaData((Object)StaticTypesMarker.SUPER_MOP_METHOD_REQUIRED, list);
            }
            list.add(directCallTarget);
            call.putNodeMetaData((Object)StaticTypesMarker.SUPER_MOP_METHOD_REQUIRED, current);
        }
    }

    private static ClassNode makeType(ClassNode cn, boolean usingClass) {
        if (usingClass) {
            ClassNode clazzType = ClassHelper.CLASS_Type.getPlainNodeReference();
            clazzType.setGenericsTypes(new GenericsType[]{new GenericsType(cn)});
            return clazzType;
        }
        return cn;
    }

    private boolean storeTypeForThis(VariableExpression vexp) {
        if (vexp == VariableExpression.THIS_EXPRESSION) {
            return true;
        }
        if (!vexp.isThisExpression()) {
            return false;
        }
        ClassNode enclosingClassNode = this.typeCheckingContext.getEnclosingClassNode();
        this.storeType(vexp, StaticTypeCheckingVisitor.makeType(enclosingClassNode, this.typeCheckingContext.isInStaticContext));
        return true;
    }

    private boolean storeTypeForSuper(VariableExpression vexp) {
        if (vexp == VariableExpression.SUPER_EXPRESSION) {
            return true;
        }
        if (!vexp.isSuperExpression()) {
            return false;
        }
        ClassNode superClassNode = this.typeCheckingContext.getEnclosingClassNode().getSuperClass();
        this.storeType(vexp, StaticTypeCheckingVisitor.makeType(superClassNode, this.typeCheckingContext.isInStaticContext));
        return true;
    }

    @Override
    public void visitVariableExpression(VariableExpression vexp) {
        TypeCheckingContext.EnclosingClosure enclosingClosure;
        BinaryExpression enclosingBinaryExpression;
        super.visitVariableExpression(vexp);
        if (this.storeTypeForThis(vexp)) {
            return;
        }
        if (this.storeTypeForSuper(vexp)) {
            return;
        }
        if (vexp.getAccessedVariable() instanceof PropertyNode && this.tryVariableExpressionAsProperty(vexp, vexp.getName()) && (enclosingBinaryExpression = this.typeCheckingContext.getEnclosingBinaryExpression()) != null) {
            Expression leftExpression = enclosingBinaryExpression.getLeftExpression();
            Expression rightExpression = enclosingBinaryExpression.getRightExpression();
            SetterInfo setterInfo = StaticTypeCheckingVisitor.removeSetterInfo(leftExpression);
            if (setterInfo != null && !this.ensureValidSetter(vexp, leftExpression, rightExpression, setterInfo)) {
                return;
            }
        }
        if ((enclosingClosure = this.typeCheckingContext.getEnclosingClosure()) != null) {
            String name = vexp.getName();
            if (name.equals("owner") || name.equals("thisObject")) {
                this.storeType(vexp, this.typeCheckingContext.getEnclosingClassNode());
                return;
            }
            if ("delegate".equals(name)) {
                DelegationMetadata md = this.getDelegationMetadata(enclosingClosure.getClosureExpression());
                ClassNode type = this.typeCheckingContext.getEnclosingClassNode();
                if (md != null) {
                    type = md.getType();
                }
                this.storeType(vexp, type);
                return;
            }
        }
        if (!(vexp.getAccessedVariable() instanceof DynamicVariable)) {
            if (this.typeCheckingContext.getEnclosingClosure() == null) {
                ClassNode inferredType;
                VariableExpression variable = null;
                if (vexp.getAccessedVariable() instanceof Parameter) {
                    variable = new ParameterVariableExpression((Parameter)vexp.getAccessedVariable());
                } else if (vexp.getAccessedVariable() instanceof VariableExpression) {
                    variable = (VariableExpression)vexp.getAccessedVariable();
                }
                if (variable != null && (inferredType = this.getInferredTypeFromTempInfo(variable, (ClassNode)variable.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE))) != null && !inferredType.getName().equals("java.lang.Object")) {
                    vexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE, inferredType);
                }
            }
            return;
        }
        DynamicVariable dyn = (DynamicVariable)vexp.getAccessedVariable();
        String dynName = dyn.getName();
        if (this.tryVariableExpressionAsProperty(vexp, dynName)) {
            return;
        }
        if (!this.extension.handleUnresolvedVariableExpression(vexp)) {
            this.addStaticTypeError("The variable [" + vexp.getName() + "] is undeclared.", vexp);
        }
    }

    private boolean tryVariableExpressionAsProperty(VariableExpression vexp, String dynName) {
        VariableExpression implicitThis = GeneralUtils.varX("this");
        PropertyExpression pe = new PropertyExpression((Expression)implicitThis, dynName);
        pe.setImplicitThis(true);
        if (this.visitPropertyExpressionSilent(pe, vexp)) {
            ClassNode previousIt = (ClassNode)vexp.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
            vexp.copyNodeMetaData(implicitThis);
            vexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, previousIt);
            this.storeType(vexp, this.getType(pe));
            Object val = pe.getNodeMetaData((Object)StaticTypesMarker.READONLY_PROPERTY);
            if (val != null) {
                vexp.putNodeMetaData((Object)StaticTypesMarker.READONLY_PROPERTY, val);
            }
            if ((val = pe.getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER)) != null) {
                vexp.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, val);
            }
            return true;
        }
        return false;
    }

    private boolean visitPropertyExpressionSilent(PropertyExpression pe, Expression lhsPart) {
        return this.existsProperty(pe, !this.isLHSOfEnclosingAssignment(lhsPart));
    }

    @Override
    public void visitPropertyExpression(PropertyExpression pexp) {
        if (this.visitPropertyExpressionSilent(pexp, pexp)) {
            return;
        }
        if (!this.extension.handleUnresolvedProperty(pexp)) {
            Expression objectExpression = pexp.getObjectExpression();
            this.addStaticTypeError("No such property: " + pexp.getPropertyAsString() + " for class: " + this.findCurrentInstanceOfClass(objectExpression, this.getType(objectExpression)).toString(false), pexp);
        }
    }

    private boolean isLHSOfEnclosingAssignment(Expression expression) {
        BinaryExpression ec = this.typeCheckingContext.getEnclosingBinaryExpression();
        return ec != null && ec.getLeftExpression() == expression && StaticTypeCheckingSupport.isAssignment(ec.getOperation().getType());
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        super.visitAttributeExpression(expression);
        if (!this.existsProperty(expression, true) && !this.extension.handleUnresolvedAttribute(expression)) {
            Expression objectExpression = expression.getObjectExpression();
            this.addStaticTypeError("No such property: " + expression.getPropertyAsString() + " for class: " + this.findCurrentInstanceOfClass(objectExpression, objectExpression.getType()), expression);
        }
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression);
        ClassNode fromType = ClassHelper.getWrapper(this.getType(expression.getFrom()));
        ClassNode toType = ClassHelper.getWrapper(this.getType(expression.getTo()));
        if (ClassHelper.Integer_TYPE.equals(fromType) && ClassHelper.Integer_TYPE.equals(toType)) {
            this.storeType(expression, ClassHelper.make(IntRange.class));
        } else {
            ClassNode rangeType = ClassHelper.make(Range.class).getPlainNodeReference();
            rangeType.setGenericsTypes(new GenericsType[]{new GenericsType(WideningCategories.lowestUpperBound(fromType, toType))});
            this.storeType(expression, rangeType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        int op = expression.getOperation().getType();
        if (op == 121 || op == 122) {
            return;
        }
        BinaryExpression enclosingBinaryExpression = this.typeCheckingContext.getEnclosingBinaryExpression();
        this.typeCheckingContext.pushEnclosingBinaryExpression(expression);
        try {
            boolean isEmptyDeclaration;
            VariableExpression leftVar;
            ClassNode resultType;
            Expression leftExpression = expression.getLeftExpression();
            Expression rightExpression = expression.getRightExpression();
            leftExpression.visit(this);
            SetterInfo setterInfo = StaticTypeCheckingVisitor.removeSetterInfo(leftExpression);
            if (setterInfo != null) {
                if (this.ensureValidSetter(expression, leftExpression, rightExpression, setterInfo)) {
                    return;
                }
            } else {
                rightExpression.visit(this);
            }
            ClassNode lType = this.getType(leftExpression);
            ClassNode rType = this.getType(rightExpression);
            if (StaticTypeCheckingVisitor.isNullConstant(rightExpression) && !ClassHelper.isPrimitiveType(lType)) {
                rType = StaticTypeCheckingSupport.UNKNOWN_PARAMETER_TYPE;
            }
            BinaryExpression reversedBinaryExpression = GeneralUtils.binX(rightExpression, expression.getOperation(), leftExpression);
            ClassNode classNode = resultType = op == 573 ? this.getResultType(rType, op, lType, reversedBinaryExpression) : this.getResultType(lType, op, rType, expression);
            if (op == 573) {
                this.storeTargetMethod(expression, (MethodNode)reversedBinaryExpression.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET));
            } else if (op == 30 && leftExpression instanceof VariableExpression && leftExpression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE) == null) {
                this.storeType(leftExpression, lType);
            }
            if (resultType == null) {
                resultType = lType;
            }
            if (leftExpression instanceof VariableExpression && (leftVar = (VariableExpression)leftExpression).isClosureSharedVariable()) {
                this.typeCheckingContext.secondPassExpressions.add(new SecondPassExpression(expression));
            }
            if (lType.isUsingGenerics() && StaticTypeCheckingSupport.missesGenericsTypes(resultType) && StaticTypeCheckingSupport.isAssignment(op)) {
                ClassNode completedType;
                resultType = completedType = GenericsUtils.parameterizeType(lType, resultType.getPlainNodeReference());
            }
            if (StaticTypeCheckingSupport.isArrayOp(op) && enclosingBinaryExpression != null && enclosingBinaryExpression.getLeftExpression() == expression && StaticTypeCheckingSupport.isAssignment(enclosingBinaryExpression.getOperation().getType()) && !lType.isArray()) {
                Expression enclosingBE_rightExpr = enclosingBinaryExpression.getRightExpression();
                if (!(enclosingBE_rightExpr instanceof ClosureExpression)) {
                    enclosingBE_rightExpr.visit(this);
                }
                ClassNode[] arguments = new ClassNode[]{rType, this.getType(enclosingBE_rightExpr)};
                List<MethodNode> nodes = this.findMethod(lType.redirect(), "putAt", arguments);
                if (nodes.size() == 1) {
                    this.typeCheckMethodsWithGenericsOrFail(lType, arguments, nodes.get(0), enclosingBE_rightExpr);
                } else if (nodes.isEmpty()) {
                    this.addNoMatchingMethodError(lType, "putAt", arguments, enclosingBinaryExpression);
                }
            }
            boolean bl = isEmptyDeclaration = expression instanceof DeclarationExpression && rightExpression instanceof EmptyExpression;
            if (!isEmptyDeclaration && StaticTypeCheckingSupport.isAssignment(op)) {
                if (rightExpression instanceof ConstructorCallExpression) {
                    this.inferDiamondType((ConstructorCallExpression)rightExpression, lType);
                }
                ClassNode originType = this.getOriginalDeclarationType(leftExpression);
                this.typeCheckAssignment(expression, leftExpression, originType, rightExpression, resultType);
                if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(ClassHelper.getWrapper(resultType), ClassHelper.getWrapper(originType))) {
                    resultType = originType;
                } else if (lType.isUsingGenerics() && !lType.isEnum() && StaticTypeCheckingVisitor.hasRHSIncompleteGenericTypeInfo(resultType)) {
                    resultType = lType;
                }
                if (ClassHelper.isPrimitiveType(originType) && resultType.equals(ClassHelper.getWrapper(originType))) {
                    resultType = originType;
                }
                if (this.typeCheckingContext.ifElseForWhileAssignmentTracker != null && leftExpression instanceof VariableExpression && !StaticTypeCheckingVisitor.isNullConstant(rightExpression)) {
                    Variable accessedVariable = ((VariableExpression)leftExpression).getAccessedVariable();
                    if (accessedVariable instanceof Parameter) {
                        accessedVariable = new ParameterVariableExpression((Parameter)accessedVariable);
                    }
                    if (accessedVariable instanceof VariableExpression) {
                        VariableExpression var = (VariableExpression)accessedVariable;
                        List<ClassNode> types = this.typeCheckingContext.ifElseForWhileAssignmentTracker.get(var);
                        if (types == null) {
                            types = new LinkedList<ClassNode>();
                            ClassNode type = (ClassNode)var.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
                            types.add(type);
                            this.typeCheckingContext.ifElseForWhileAssignmentTracker.put(var, types);
                        }
                        types.add(resultType);
                    }
                }
                this.storeType(leftExpression, resultType);
                if (leftExpression instanceof VariableExpression) {
                    Variable targetVariable;
                    if (rightExpression instanceof ClosureExpression) {
                        Parameter[] parameters = ((ClosureExpression)rightExpression).getParameters();
                        leftExpression.putNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS, parameters);
                    } else if (rightExpression instanceof VariableExpression && ((VariableExpression)rightExpression).getAccessedVariable() instanceof Expression && ((Expression)((Object)((VariableExpression)rightExpression).getAccessedVariable())).getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS) != null && (targetVariable = StaticTypeCheckingSupport.findTargetVariable((VariableExpression)leftExpression)) instanceof ASTNode) {
                        ((ASTNode)((Object)targetVariable)).putNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS, ((Expression)((Object)((VariableExpression)rightExpression).getAccessedVariable())).getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS));
                    }
                }
            } else if (op == 544) {
                this.pushInstanceOfTypeInfo(leftExpression, rightExpression);
            }
            if (!isEmptyDeclaration) {
                this.storeType(expression, resultType);
            }
        }
        finally {
            this.typeCheckingContext.popEnclosingBinaryExpression();
        }
    }

    private boolean ensureValidSetter(Expression expression, Expression leftExpression, Expression rightExpression, SetterInfo setterInfo) {
        VariableExpression ve = GeneralUtils.varX("%", setterInfo.receiverType);
        Expression newRightExpression = this.isCompoundAssignment(expression) ? GeneralUtils.binX(leftExpression, this.getOpWithoutEqual(expression), rightExpression) : rightExpression;
        MethodCallExpression call = GeneralUtils.callX((Expression)ve, setterInfo.name, newRightExpression);
        call.setImplicitThis(false);
        this.visitMethodCallExpression(call);
        MethodNode directSetterCandidate = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (directSetterCandidate == null) {
            for (MethodNode setter : setterInfo.setters) {
                ClassNode type = ClassHelper.getWrapper(setter.getParameters()[0].getOriginType());
                if (!ClassHelper.Boolean_TYPE.equals(type) && !ClassHelper.STRING_TYPE.equals(type) && !ClassHelper.CLASS_Type.equals(type)) continue;
                call = GeneralUtils.callX((Expression)ve, setterInfo.name, (Expression)GeneralUtils.castX(type, newRightExpression));
                call.setImplicitThis(false);
                this.visitMethodCallExpression(call);
                directSetterCandidate = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
                if (directSetterCandidate == null) continue;
                break;
            }
        }
        if (directSetterCandidate != null) {
            for (MethodNode setter : setterInfo.setters) {
                if (setter != directSetterCandidate) continue;
                leftExpression.putNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, directSetterCandidate);
                this.storeType(leftExpression, this.getType(newRightExpression));
                break;
            }
        } else {
            ClassNode firstSetterType = setterInfo.setters.iterator().next().getParameters()[0].getOriginType();
            this.addAssignmentError(firstSetterType, this.getType(newRightExpression), expression);
            return true;
        }
        return false;
    }

    private boolean isCompoundAssignment(Expression exp) {
        if (!(exp instanceof BinaryExpression)) {
            return false;
        }
        int type = ((BinaryExpression)exp).getOperation().getType();
        return StaticTypeCheckingSupport.isAssignment(type) && type != 100;
    }

    private Token getOpWithoutEqual(Expression exp) {
        if (!(exp instanceof BinaryExpression)) {
            return null;
        }
        Token op = ((BinaryExpression)exp).getOperation();
        int typeWithoutEqual = TokenUtil.removeAssignment(op.getType());
        return new Token(typeWithoutEqual, op.getText(), op.getStartLine(), op.getStartColumn());
    }

    protected ClassNode getOriginalDeclarationType(Expression lhs) {
        if (lhs instanceof VariableExpression) {
            Variable var = StaticTypeCheckingSupport.findTargetVariable((VariableExpression)lhs);
            if (var instanceof PropertyNode) {
                return this.getType(lhs);
            }
            if (var instanceof DynamicVariable) {
                return this.getType(lhs);
            }
            return var.getOriginType();
        }
        if (lhs instanceof FieldExpression) {
            return ((FieldExpression)lhs).getField().getOriginType();
        }
        return this.getType(lhs);
    }

    protected void inferDiamondType(ConstructorCallExpression cce, ClassNode lType) {
        ClassNode node = cce.getType();
        if (node.isUsingGenerics() && node instanceof InnerClassNode && ((InnerClassNode)node).isAnonymous()) {
            ClassNode[] interfaces = node.getInterfaces();
            ClassNode classNode = node = interfaces != null && interfaces.length == 1 ? interfaces[0] : node.getUnresolvedSuperClass(false);
            if ((node.getGenericsTypes() == null || node.getGenericsTypes().length == 0) && lType.isUsingGenerics()) {
                this.addStaticTypeError("Cannot use diamond <> with anonymous inner classes", cce);
            }
        } else if (node.isUsingGenerics() && node.getGenericsTypes() != null && node.getGenericsTypes().length == 0) {
            ArgumentListExpression argumentListExpression = InvocationWriter.makeArgumentList(cce.getArguments());
            if (argumentListExpression.getExpressions().isEmpty()) {
                this.adjustGenerics(lType, node);
            } else {
                ClassNode type = this.getType(argumentListExpression.getExpression(0));
                if (type.isUsingGenerics()) {
                    this.adjustGenerics(type, node);
                }
            }
            this.storeType(cce, node);
        }
    }

    private void adjustGenerics(ClassNode from, ClassNode to) {
        GenericsType[] genericsTypes = from.getGenericsTypes();
        if (genericsTypes == null) {
            genericsTypes = to.redirect().getGenericsTypes();
        }
        GenericsType[] copy = new GenericsType[genericsTypes.length];
        for (int i = 0; i < genericsTypes.length; ++i) {
            GenericsType genericsType = genericsTypes[i];
            copy[i] = new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(genericsType.getType()), genericsType.getUpperBounds(), genericsType.getLowerBound());
        }
        to.setGenericsTypes(copy);
    }

    protected void pushInstanceOfTypeInfo(Expression objectOfInstanceOf, Expression typeExpression) {
        Object key;
        Map<Object, List<ClassNode>> tempo = this.typeCheckingContext.temporaryIfBranchTypeInformation.peek();
        List<ClassNode> potentialTypes = tempo.get(key = this.extractTemporaryTypeInfoKey(objectOfInstanceOf));
        if (potentialTypes == null) {
            potentialTypes = new LinkedList<ClassNode>();
            tempo.put(key, potentialTypes);
        }
        potentialTypes.add(typeExpression.getType());
    }

    private boolean typeCheckMultipleAssignmentAndContinue(Expression leftExpression, Expression rightExpression) {
        if (!(leftExpression instanceof TupleExpression)) {
            return true;
        }
        if (!(rightExpression instanceof ListExpression)) {
            this.addStaticTypeError("Multiple assignments without list expressions on the right hand side are unsupported in static type checking mode", rightExpression);
            return false;
        }
        TupleExpression tuple = (TupleExpression)leftExpression;
        ListExpression list = (ListExpression)rightExpression;
        List<Expression> listExpressions = list.getExpressions();
        List<Expression> tupleExpressions = tuple.getExpressions();
        if (listExpressions.size() < tupleExpressions.size()) {
            this.addStaticTypeError("Incorrect number of values. Expected:" + tupleExpressions.size() + " Was:" + listExpressions.size(), list);
            return false;
        }
        int tupleExpressionsSize = tupleExpressions.size();
        for (int i = 0; i < tupleExpressionsSize; ++i) {
            ClassNode tupleType;
            Expression tupleExpression = tupleExpressions.get(i);
            Expression listExpression = listExpressions.get(i);
            ClassNode elemType = this.getType(listExpression);
            if (!StaticTypeCheckingSupport.isAssignableTo(elemType, tupleType = this.getType(tupleExpression))) {
                this.addStaticTypeError("Cannot assign value of type " + elemType.toString(false) + " to variable of type " + tupleType.toString(false), rightExpression);
                return false;
            }
            this.storeType(tupleExpression, elemType);
        }
        return true;
    }

    private static ClassNode adjustTypeForSpreading(ClassNode inferredRightExpressionType, Expression leftExpression) {
        ClassNode wrappedRHS = inferredRightExpressionType;
        if (leftExpression instanceof PropertyExpression && ((PropertyExpression)leftExpression).isSpreadSafe()) {
            wrappedRHS = ClassHelper.LIST_TYPE.getPlainNodeReference();
            wrappedRHS.setGenericsTypes(new GenericsType[]{new GenericsType(ClassHelper.getWrapper(inferredRightExpressionType))});
        }
        return wrappedRHS;
    }

    private boolean addedReadOnlyPropertyError(Expression expr) {
        if (expr.getNodeMetaData((Object)StaticTypesMarker.READONLY_PROPERTY) == null) {
            return false;
        }
        String name = expr instanceof VariableExpression ? ((VariableExpression)expr).getName() : ((PropertyExpression)expr).getPropertyAsString();
        this.addStaticTypeError("Cannot set read-only property: " + name, expr);
        return true;
    }

    private void addPrecisionErrors(ClassNode leftRedirect, ClassNode lhsType, ClassNode inferredrhsType, Expression rightExpression) {
        if (ClassHelper.isNumberType(leftRedirect) && ClassHelper.isNumberType(inferredrhsType) && StaticTypeCheckingSupport.checkPossibleLossOfPrecision(leftRedirect, inferredrhsType, rightExpression)) {
            this.addStaticTypeError("Possible loss of precision from " + inferredrhsType + " to " + leftRedirect, rightExpression);
            return;
        }
        if (!lhsType.isArray()) {
            return;
        }
        ClassNode leftComponentType = lhsType.getComponentType();
        ClassNode rightRedirect = rightExpression.getType().redirect();
        if (rightRedirect.isArray()) {
            ClassNode rightComponentType = rightRedirect.getComponentType();
            if (!StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(leftComponentType, rightComponentType)) {
                this.addStaticTypeError("Cannot assign value of type " + rightComponentType.toString(false) + " into array of type " + lhsType.toString(false), rightExpression);
            }
        } else if (rightExpression instanceof ListExpression) {
            for (Expression element : ((ListExpression)rightExpression).getExpressions()) {
                ClassNode rightComponentType = this.getType(element);
                if (StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(leftComponentType, rightComponentType) || StaticTypeCheckingVisitor.isNullConstant(element) && !ClassHelper.isPrimitiveType(leftComponentType)) continue;
                this.addStaticTypeError("Cannot assign value of type " + rightComponentType.toString(false) + " into array of type " + lhsType.toString(false), rightExpression);
            }
        }
    }

    private void addListAssignmentConstructorErrors(ClassNode leftRedirect, ClassNode leftExpressionType, ClassNode inferredRightExpressionType, Expression rightExpression, Expression assignmentExpression) {
        if (rightExpression instanceof ListExpression && !StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(ClassHelper.LIST_TYPE, leftRedirect)) {
            ArgumentListExpression argList = GeneralUtils.args(((ListExpression)rightExpression).getExpressions());
            ClassNode[] args = this.getArgumentTypes(argList);
            MethodNode methodNode = this.checkGroovyStyleConstructor(leftRedirect, args, assignmentExpression);
            if (methodNode != null) {
                rightExpression.putNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode);
            }
        } else if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(inferredRightExpressionType, leftRedirect) && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(inferredRightExpressionType, ClassHelper.LIST_TYPE) && !StaticTypeCheckingSupport.isWildcardLeftHandSide(leftExpressionType) && !this.extension.handleIncompatibleAssignment(leftExpressionType, inferredRightExpressionType, assignmentExpression)) {
            this.addAssignmentError(leftExpressionType, inferredRightExpressionType, assignmentExpression);
        }
    }

    private void addMapAssignmentConstructorErrors(ClassNode leftRedirect, Expression leftExpression, Expression rightExpression) {
        if (!(StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(leftRedirect, ClassHelper.MAP_TYPE) || !(rightExpression instanceof MapExpression) || leftExpression instanceof VariableExpression && ((VariableExpression)leftExpression).isDynamicTyped())) {
            ArgumentListExpression argList = GeneralUtils.args(rightExpression);
            ClassNode[] argTypes = this.getArgumentTypes(argList);
            this.checkGroovyStyleConstructor(leftRedirect, argTypes, rightExpression);
            MapExpression mapExpression = (MapExpression)rightExpression;
            this.checkGroovyConstructorMap(leftExpression, leftRedirect, mapExpression);
        }
    }

    private void checkTypeGenerics(ClassNode leftExpressionType, ClassNode wrappedRHS, Expression rightExpression) {
        if (!leftExpressionType.isUsingGenerics()) {
            return;
        }
        if (StaticTypeCheckingVisitor.hasRHSIncompleteGenericTypeInfo(wrappedRHS)) {
            return;
        }
        GenericsType gt = GenericsUtils.buildWildcardType(leftExpressionType);
        if (StaticTypeCheckingSupport.UNKNOWN_PARAMETER_TYPE.equals(wrappedRHS) || gt.isCompatibleWith(wrappedRHS) || StaticTypeCheckingVisitor.isNullConstant(rightExpression)) {
            return;
        }
        this.addStaticTypeError("Incompatible generic argument types. Cannot assign " + wrappedRHS.toString(false) + " to: " + leftExpressionType.toString(false), rightExpression);
    }

    private boolean hasGStringStringError(ClassNode leftExpressionType, ClassNode wrappedRHS, Expression rightExpression) {
        if (StaticTypeCheckingSupport.isParameterizedWithString(leftExpressionType) && StaticTypeCheckingSupport.isParameterizedWithGStringOrGStringString(wrappedRHS)) {
            this.addStaticTypeError("You are trying to use a GString in place of a String in a type which explicitly declares accepting String. Make sure to call toString() on all GString values.", rightExpression);
            return true;
        }
        return false;
    }

    protected void typeCheckAssignment(BinaryExpression assignmentExpression, Expression leftExpression, ClassNode leftExpressionType, Expression rightExpression, ClassNode inferredRightExpressionTypeOrig) {
        ClassNode wrappedRHS;
        boolean compatible;
        ClassNode inferredRightExpressionType = inferredRightExpressionTypeOrig;
        if (!this.typeCheckMultipleAssignmentAndContinue(leftExpression, rightExpression)) {
            return;
        }
        if (leftExpression instanceof VariableExpression && ((VariableExpression)leftExpression).getAccessedVariable() instanceof FieldNode) {
            this.checkOrMarkPrivateAccess(leftExpression, (FieldNode)((VariableExpression)leftExpression).getAccessedVariable(), true);
        }
        if (this.addedReadOnlyPropertyError(leftExpression)) {
            return;
        }
        ClassNode leftRedirect = leftExpressionType.redirect();
        if (rightExpression instanceof VariableExpression && StaticTypeCheckingVisitor.hasInferredReturnType(rightExpression) && assignmentExpression.getOperation().getType() == 100) {
            inferredRightExpressionType = (ClassNode)rightExpression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
        }
        if (!(compatible = StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(leftRedirect, wrappedRHS = StaticTypeCheckingVisitor.adjustTypeForSpreading(inferredRightExpressionType, leftExpression), rightExpression))) {
            if (!this.extension.handleIncompatibleAssignment(leftExpressionType, inferredRightExpressionType, assignmentExpression)) {
                this.addAssignmentError(leftExpressionType, inferredRightExpressionType, assignmentExpression.getRightExpression());
            }
        } else {
            this.addPrecisionErrors(leftRedirect, leftExpressionType, inferredRightExpressionType, rightExpression);
            this.addListAssignmentConstructorErrors(leftRedirect, leftExpressionType, inferredRightExpressionType, rightExpression, assignmentExpression);
            this.addMapAssignmentConstructorErrors(leftRedirect, leftExpression, rightExpression);
            if (this.hasGStringStringError(leftExpressionType, wrappedRHS, rightExpression)) {
                return;
            }
            this.checkTypeGenerics(leftExpressionType, wrappedRHS, rightExpression);
        }
    }

    protected void checkGroovyConstructorMap(Expression receiver, ClassNode receiverType, MapExpression mapExpression) {
        this.typeCheckingContext.pushEnclosingBinaryExpression(null);
        for (MapEntryExpression entryExpression : mapExpression.getMapEntryExpressions()) {
            MethodNode setter;
            ClassNode toBeAssignedTo;
            Expression keyExpr = entryExpression.getKeyExpression();
            if (!(keyExpr instanceof ConstantExpression)) {
                this.addStaticTypeError("Dynamic keys in map-style constructors are unsupported in static type checking", keyExpr);
                continue;
            }
            AtomicReference<ClassNode> lookup = new AtomicReference<ClassNode>();
            PropertyExpression pexp = new PropertyExpression((Expression)GeneralUtils.varX("_", receiverType), keyExpr.getText());
            boolean hasProperty = this.existsProperty(pexp, false, new PropertyLookupVisitor(lookup));
            if (!hasProperty) {
                this.addStaticTypeError("No such property: " + keyExpr.getText() + " for class: " + receiverType.getName(), receiver);
                continue;
            }
            ClassNode valueType = this.getType(entryExpression.getValueExpression());
            if (StaticTypeCheckingSupport.isAssignableTo(valueType, toBeAssignedTo = (setter = receiverType.getSetterMethod("set" + MetaClassHelper.capitalize(pexp.getPropertyAsString()), false)) == null ? lookup.get() : setter.getParameters()[0].getType()) || this.extension.handleIncompatibleAssignment(toBeAssignedTo, valueType, entryExpression)) continue;
            this.addAssignmentError(toBeAssignedTo, valueType, entryExpression);
        }
        this.typeCheckingContext.popEnclosingBinaryExpression();
    }

    protected static boolean hasRHSIncompleteGenericTypeInfo(ClassNode inferredRightExpressionType) {
        boolean replaceType = false;
        GenericsType[] genericsTypes = inferredRightExpressionType.getGenericsTypes();
        if (genericsTypes != null) {
            for (GenericsType genericsType : genericsTypes) {
                if (!genericsType.isPlaceholder()) continue;
                replaceType = true;
                break;
            }
        }
        return replaceType;
    }

    @Deprecated
    protected void checkGroovyStyleConstructor(ClassNode node, ClassNode[] arguments) {
        this.checkGroovyStyleConstructor(node, arguments, this.typeCheckingContext.getEnclosingClassNode());
    }

    protected MethodNode checkGroovyStyleConstructor(ClassNode node, ClassNode[] arguments, ASTNode source) {
        if (node.equals(ClassHelper.OBJECT_TYPE) || node.equals(ClassHelper.DYNAMIC_TYPE)) {
            return null;
        }
        List<ConstructorNode> constructors = node.getDeclaredConstructors();
        if (constructors.isEmpty() && arguments.length == 0) {
            return null;
        }
        List<MethodNode> constructorList = this.findMethod(node, "<init>", arguments);
        if (constructorList.isEmpty()) {
            if (StaticTypeCheckingSupport.isBeingCompiled(node) && arguments.length == 1 && LINKEDHASHMAP_CLASSNODE.equals(arguments[0])) {
                ConstructorNode cn = new ConstructorNode(1, new Parameter[]{new Parameter(LINKEDHASHMAP_CLASSNODE, "args")}, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
                return cn;
            }
            this.addStaticTypeError("No matching constructor found: " + node + StaticTypeCheckingSupport.toMethodParametersString("<init>", arguments), source);
            return null;
        }
        if (constructorList.size() > 1) {
            this.addStaticTypeError("Ambiguous constructor call " + node + StaticTypeCheckingSupport.toMethodParametersString("<init>", arguments), source);
            return null;
        }
        return constructorList.get(0);
    }

    protected Object extractTemporaryTypeInfoKey(Expression expression) {
        return expression instanceof VariableExpression ? StaticTypeCheckingSupport.findTargetVariable((VariableExpression)expression) : expression.getText();
    }

    protected ClassNode findCurrentInstanceOfClass(Expression expr, ClassNode type) {
        List<ClassNode> nodes;
        if (!this.typeCheckingContext.temporaryIfBranchTypeInformation.empty() && (nodes = this.getTemporaryTypesForExpression(expr)) != null && nodes.size() == 1) {
            return nodes.get(0);
        }
        return type;
    }

    protected boolean existsProperty(PropertyExpression pexp, boolean checkForReadOnly) {
        return this.existsProperty(pexp, checkForReadOnly, null);
    }

    protected boolean existsProperty(PropertyExpression pexp, boolean readMode, ClassCodeVisitorSupport visitor) {
        ClassNode testClass;
        super.visitPropertyExpression(pexp);
        String propertyName = pexp.getPropertyAsString();
        if (propertyName == null) {
            return false;
        }
        Expression objectExpression = pexp.getObjectExpression();
        ClassNode objectExpressionType = this.getType(objectExpression);
        boolean staticOnlyAccess = StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(objectExpressionType);
        if ("this".equals(propertyName) && staticOnlyAccess) {
            ClassNode outerNode = objectExpressionType.getGenericsTypes()[0].getType();
            List<ClassNode> candidates = this.typeCheckingContext.getEnclosingClassNodes();
            ClassNode found = null;
            for (ClassNode current : candidates) {
                if (current.isStaticClass() || !(current instanceof InnerClassNode) || !outerNode.equals(current.getOuterClass())) continue;
                found = current;
                break;
            }
            if (found != null) {
                this.storeType(pexp, outerNode);
                return true;
            }
        }
        if (objectExpressionType.isArray() && "length".equals(pexp.getPropertyAsString())) {
            this.storeType(pexp, ClassHelper.int_TYPE);
            if (visitor != null) {
                PropertyNode node = new PropertyNode("length", 17, ClassHelper.int_TYPE, objectExpressionType, null, null, null);
                visitor.visitProperty(node);
            }
            return true;
        }
        boolean foundGetterOrSetter = false;
        LinkedList<Receiver<String>> receivers = new LinkedList<Receiver<String>>();
        List<Receiver<String>> owners = this.makeOwnerList(objectExpression);
        this.addReceivers(receivers, owners, pexp.isImplicitThis());
        String capName = MetaClassHelper.capitalize(propertyName);
        boolean isAttributeExpression = pexp instanceof AttributeExpression;
        HashSet<ClassNode> handledNodes = new HashSet<ClassNode>();
        for (Receiver receiver : receivers) {
            MethodNode getter;
            testClass = receiver.getType();
            LinkedList<ClassNode> queue = new LinkedList<ClassNode>();
            queue.add(testClass);
            if (ClassHelper.isPrimitiveType(testClass)) {
                queue.add(ClassHelper.getWrapper(testClass));
            }
            while (!queue.isEmpty()) {
                boolean checkGetterOrSetter;
                ClassNode current = (ClassNode)queue.removeFirst();
                if (handledNodes.contains(current)) continue;
                handledNodes.add(current);
                Set<ClassNode> allInterfaces = current.getAllInterfaces();
                for (ClassNode intf : allInterfaces) {
                    queue.add(GenericsUtils.parameterizeType(current, intf));
                }
                boolean staticOnly = StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(current) ? false : staticOnlyAccess;
                FieldNode field = current.getDeclaredField(propertyName);
                if (this.storeField(field = this.allowStaticAccessToMember(field, staticOnly), isAttributeExpression, pexp, current, visitor, (String)receiver.getData(), !readMode)) {
                    return true;
                }
                boolean isThisExpression = objectExpression instanceof VariableExpression && ((VariableExpression)objectExpression).isThisExpression() && objectExpressionType.equals(current);
                if (this.storeField(field, isThisExpression, pexp, receiver.getType(), visitor, (String)receiver.getData(), !readMode)) {
                    return true;
                }
                getter = this.findGetter(current, "get" + capName, pexp.isImplicitThis());
                if ((getter = this.allowStaticAccessToMember(getter, staticOnly)) == null) {
                    getter = this.findGetter(current, "is" + capName, pexp.isImplicitThis());
                }
                getter = this.allowStaticAccessToMember(getter, staticOnly);
                String setterName = "set" + capName;
                List<MethodNode> setters = StaticTypeCheckingSupport.findSetters(current, setterName, false);
                setters = this.allowStaticAccessToMember(setters, staticOnly);
                if (visitor != null && getter != null) {
                    visitor.visitMethod(getter);
                }
                PropertyNode propertyNode = current.getProperty(propertyName);
                propertyNode = this.allowStaticAccessToMember(propertyNode, staticOnly);
                boolean bl = checkGetterOrSetter = !isThisExpression || propertyNode == null;
                if (readMode && checkGetterOrSetter) {
                    if (getter != null) {
                        ClassNode cn = this.inferReturnTypeGenerics(current, getter, ArgumentListExpression.EMPTY_ARGUMENTS);
                        this.storeInferredTypeForPropertyExpression(pexp, cn);
                        pexp.removeNodeMetaData((Object)StaticTypesMarker.READONLY_PROPERTY);
                        String delegationData = (String)receiver.getData();
                        if (delegationData != null) {
                            pexp.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, delegationData);
                        }
                        return true;
                    }
                } else if (!readMode && checkGetterOrSetter) {
                    if (!setters.isEmpty()) {
                        String delegationData;
                        if (visitor != null) {
                            if (field != null) {
                                visitor.visitField(field);
                            } else {
                                for (MethodNode setter : setters) {
                                    ClassNode setterType = setter.getParameters()[0].getOriginType();
                                    FieldNode virtual = new FieldNode(propertyName, 0, setterType, current, EmptyExpression.INSTANCE);
                                    visitor.visitField(virtual);
                                }
                            }
                        }
                        SetterInfo info = new SetterInfo(current, setterName, setters);
                        BinaryExpression enclosingBinaryExpression = this.typeCheckingContext.getEnclosingBinaryExpression();
                        if (enclosingBinaryExpression != null) {
                            StaticTypeCheckingVisitor.putSetterInfo(enclosingBinaryExpression.getLeftExpression(), info);
                        }
                        if ((delegationData = (String)receiver.getData()) != null) {
                            pexp.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, delegationData);
                        }
                        return true;
                    }
                    if (getter != null && propertyNode == null) {
                        pexp.putNodeMetaData((Object)StaticTypesMarker.READONLY_PROPERTY, true);
                    }
                }
                boolean bl2 = foundGetterOrSetter = foundGetterOrSetter || !setters.isEmpty() || getter != null;
                if (this.storeProperty(propertyNode, pexp, current, visitor, (String)receiver.getData())) {
                    return true;
                }
                if (this.storeField(field, true, pexp, current, visitor, (String)receiver.getData(), !readMode)) {
                    return true;
                }
                if (current.getSuperClass() == null) continue;
                queue.add(current.getUnresolvedSuperClass());
            }
            ArrayList<ClassNode> dgmReceivers = new ArrayList<ClassNode>(2);
            dgmReceivers.add(testClass);
            if (ClassHelper.isPrimitiveType(testClass)) {
                dgmReceivers.add(ClassHelper.getWrapper(testClass));
            }
            for (ClassNode dgmReceiver : dgmReceivers) {
                List<MethodNode> methodNodes;
                List<MethodNode> methods = StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.getTransformLoader(), dgmReceiver, "get" + capName, ClassNode.EMPTY_ARRAY);
                for (MethodNode m : StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.getTransformLoader(), dgmReceiver, "is" + capName, ClassNode.EMPTY_ARRAY)) {
                    if (!ClassHelper.Boolean_TYPE.equals(ClassHelper.getWrapper(m.getReturnType()))) continue;
                    methods.add(m);
                }
                if (methods.isEmpty() || (methodNodes = StaticTypeCheckingSupport.chooseBestMethod(dgmReceiver, methods, ClassNode.EMPTY_ARRAY)).size() != 1) continue;
                getter = methodNodes.get(0);
                if (visitor != null) {
                    visitor.visitMethod(getter);
                }
                ClassNode cn = this.inferReturnTypeGenerics(dgmReceiver, getter, ArgumentListExpression.EMPTY_ARGUMENTS);
                this.storeInferredTypeForPropertyExpression(pexp, cn);
                return true;
            }
        }
        for (Receiver receiver : receivers) {
            testClass = receiver.getType();
            ClassNode propertyType = this.getTypeForMapPropertyExpression(testClass, objectExpressionType, pexp);
            if (propertyType == null) {
                propertyType = this.getTypeForListPropertyExpression(testClass, objectExpressionType, pexp);
            }
            if (propertyType == null) {
                propertyType = this.getTypeForSpreadExpression(testClass, objectExpressionType, pexp);
            }
            if (propertyType == null) continue;
            if (visitor != null) {
                PropertyNode node = new PropertyNode(propertyName, 1, propertyType, receiver.getType(), null, null, null);
                node.setDeclaringClass(receiver.getType());
                visitor.visitProperty(node);
            }
            this.storeType(pexp, propertyType);
            String delegationData = (String)receiver.getData();
            if (delegationData != null) {
                pexp.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, delegationData);
            }
            return true;
        }
        return foundGetterOrSetter;
    }

    private MethodNode findGetter(ClassNode current, String name, boolean searchOuterClasses) {
        MethodNode getterMethod = current.getGetterMethod(name);
        if (getterMethod == null && searchOuterClasses && current instanceof InnerClassNode) {
            return this.findGetter(current.getOuterClass(), name, true);
        }
        return getterMethod;
    }

    private ClassNode getTypeForSpreadExpression(ClassNode testClass, ClassNode objectExpressionType, PropertyExpression pexp) {
        AtomicReference<ClassNode> result;
        PropertyExpression subExp;
        if (!pexp.isSpreadSafe()) {
            return null;
        }
        MethodCallExpression mce = GeneralUtils.callX((Expression)GeneralUtils.varX("_", testClass), "iterator", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        mce.setImplicitThis(false);
        mce.visit(this);
        ClassNode callType = this.getType(mce);
        if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(callType, ClassHelper.Iterator_TYPE)) {
            return null;
        }
        GenericsType[] types = callType.getGenericsTypes();
        ClassNode contentType = ClassHelper.OBJECT_TYPE;
        if (types != null && types.length == 1) {
            contentType = types[0].getType();
        }
        if (this.existsProperty(subExp = new PropertyExpression((Expression)GeneralUtils.varX("{}", contentType), pexp.getPropertyAsString()), true, new PropertyLookupVisitor(result = new AtomicReference<ClassNode>()))) {
            ClassNode intf = ClassHelper.LIST_TYPE.getPlainNodeReference();
            intf.setGenericsTypes(new GenericsType[]{new GenericsType(ClassHelper.getWrapper(result.get()))});
            return intf;
        }
        return null;
    }

    private ClassNode getTypeForListPropertyExpression(ClassNode testClass, ClassNode objectExpressionType, PropertyExpression pexp) {
        AtomicReference<ClassNode> result;
        if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(testClass, ClassHelper.LIST_TYPE)) {
            return null;
        }
        ClassNode intf = GenericsUtils.parameterizeType(objectExpressionType, ClassHelper.LIST_TYPE.getPlainNodeReference());
        GenericsType[] types = intf.getGenericsTypes();
        if (types == null || types.length != 1) {
            return ClassHelper.OBJECT_TYPE;
        }
        PropertyExpression subExp = new PropertyExpression((Expression)GeneralUtils.varX("{}", types[0].getType()), pexp.getPropertyAsString());
        if (this.existsProperty(subExp, true, new PropertyLookupVisitor(result = new AtomicReference<ClassNode>()))) {
            intf = ClassHelper.LIST_TYPE.getPlainNodeReference();
            ClassNode itemType = result.get();
            intf.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(itemType))});
            return intf;
        }
        return null;
    }

    private ClassNode getTypeForMapPropertyExpression(ClassNode testClass, ClassNode objectExpressionType, PropertyExpression pexp) {
        if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(testClass, ClassHelper.MAP_TYPE)) {
            return null;
        }
        ClassNode intf = objectExpressionType.getGenericsTypes() != null ? GenericsUtils.parameterizeType(objectExpressionType, ClassHelper.MAP_TYPE.getPlainNodeReference()) : ClassHelper.MAP_TYPE.getPlainNodeReference();
        GenericsType[] types = intf.getGenericsTypes();
        if (types == null || types.length != 2) {
            return ClassHelper.OBJECT_TYPE;
        }
        if (pexp.isSpreadSafe()) {
            if ("key".equals(pexp.getPropertyAsString())) {
                ClassNode listKey = ClassHelper.LIST_TYPE.getPlainNodeReference();
                listKey.setGenericsTypes(new GenericsType[]{types[0]});
                return listKey;
            }
            if ("value".equals(pexp.getPropertyAsString())) {
                ClassNode listValue = ClassHelper.LIST_TYPE.getPlainNodeReference();
                listValue.setGenericsTypes(new GenericsType[]{types[1]});
                return listValue;
            }
        } else {
            return types[1].getType();
        }
        this.addStaticTypeError("Spread operator on map only allows one of [key,value]", pexp);
        return null;
    }

    private <T> T allowStaticAccessToMember(T member, boolean staticOnly) {
        boolean isStatic;
        if (member == null) {
            return null;
        }
        if (!staticOnly) {
            return member;
        }
        if (member instanceof Variable) {
            Variable v = (Variable)member;
            isStatic = Modifier.isStatic(v.getModifiers());
        } else {
            if (member instanceof List) {
                List list = (List)member;
                if (list.size() == 1) {
                    return (T)Arrays.asList((MethodNode)this.allowStaticAccessToMember(list.get(0), staticOnly));
                }
                return (T)Collections.emptyList();
            }
            MethodNode mn = (MethodNode)member;
            isStatic = mn.isStatic();
        }
        if (staticOnly && !isStatic) {
            return null;
        }
        return member;
    }

    private void storeWithResolve(ClassNode typeToResolve, ClassNode receiver, ClassNode declaringClass, boolean isStatic, PropertyExpression expressionToStoreOn) {
        ClassNode type = typeToResolve;
        if (StaticTypeCheckingSupport.getGenericsWithoutArray(type) != null) {
            Map<String, GenericsType> resolvedPlaceholders = this.resolvePlaceHoldersFromDeclaration(receiver, declaringClass, null, isStatic);
            type = this.resolveGenericsWithContext(resolvedPlaceholders, type);
        }
        this.storeInferredTypeForPropertyExpression(expressionToStoreOn, type);
        this.storeType(expressionToStoreOn, type);
    }

    private boolean storeField(FieldNode field, boolean returnTrueIfFieldExists, PropertyExpression expressionToStoreOn, ClassNode receiver, ClassCodeVisitorSupport visitor, String delegationData, boolean lhsOfAssignment) {
        if (field == null || !returnTrueIfFieldExists) {
            return false;
        }
        if (visitor != null) {
            visitor.visitField(field);
        }
        this.storeWithResolve(field.getOriginType(), receiver, field.getDeclaringClass(), field.isStatic(), expressionToStoreOn);
        this.checkOrMarkPrivateAccess(expressionToStoreOn, field, lhsOfAssignment);
        if (delegationData != null) {
            expressionToStoreOn.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, delegationData);
        }
        return true;
    }

    private boolean storeProperty(PropertyNode propertyNode, PropertyExpression expressionToStoreOn, ClassNode receiver, ClassCodeVisitorSupport visitor, String delegationData) {
        if (propertyNode == null) {
            return false;
        }
        if (visitor != null) {
            visitor.visitProperty(propertyNode);
        }
        this.storeWithResolve(propertyNode.getOriginType(), receiver, propertyNode.getDeclaringClass(), propertyNode.isStatic(), expressionToStoreOn);
        if (delegationData != null) {
            expressionToStoreOn.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, delegationData);
        }
        return true;
    }

    protected void storeInferredTypeForPropertyExpression(PropertyExpression pexp, ClassNode flatInferredType) {
        if (pexp.isSpreadSafe()) {
            ClassNode list = ClassHelper.LIST_TYPE.getPlainNodeReference();
            list.setGenericsTypes(new GenericsType[]{new GenericsType(flatInferredType)});
            this.storeType(pexp, list);
        } else {
            this.storeType(pexp, flatInferredType);
        }
    }

    @Deprecated
    protected SetterInfo hasSetter(PropertyExpression pexp) {
        String propertyName = pexp.getPropertyAsString();
        if (propertyName == null) {
            return null;
        }
        Expression objectExpression = pexp.getObjectExpression();
        LinkedList<Receiver<String>> receivers = new LinkedList<Receiver<String>>();
        List<Receiver<String>> owners = this.makeOwnerList(objectExpression);
        this.addReceivers(receivers, owners, pexp.isImplicitThis());
        String capName = MetaClassHelper.capitalize(propertyName);
        boolean isAttributeExpression = pexp instanceof AttributeExpression;
        for (Receiver receiver : receivers) {
            ClassNode testClass = receiver.getType();
            LinkedList<ClassNode> queue = new LinkedList<ClassNode>();
            queue.add(testClass);
            if (testClass.isInterface()) {
                queue.addAll(testClass.getAllInterfaces());
            }
            while (!queue.isEmpty()) {
                String setterName;
                ClassNode current = (ClassNode)queue.removeFirst();
                List<MethodNode> setterMethods = StaticTypeCheckingSupport.findSetters(current = current.redirect(), setterName = "set" + capName, false);
                if (!setterMethods.isEmpty()) {
                    return new SetterInfo(current, setterName, setterMethods);
                }
                if (isAttributeExpression || current.getSuperClass() == null) continue;
                queue.add(current.getSuperClass());
            }
        }
        return null;
    }

    @Override
    public void visitProperty(PropertyNode node) {
        boolean osc = this.typeCheckingContext.isInStaticContext;
        try {
            this.typeCheckingContext.isInStaticContext = node.isInStaticContext();
            this.currentProperty = node;
            super.visitProperty(node);
        }
        finally {
            this.currentProperty = null;
            this.typeCheckingContext.isInStaticContext = osc;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitField(FieldNode node) {
        boolean osc = this.typeCheckingContext.isInStaticContext;
        try {
            this.typeCheckingContext.isInStaticContext = node.isInStaticContext();
            this.currentField = node;
            super.visitField(node);
            Expression init = node.getInitialExpression();
            if (init != null) {
                FieldExpression left = new FieldExpression(node);
                BinaryExpression bexp = GeneralUtils.binX(left, Token.newSymbol("=", node.getLineNumber(), node.getColumnNumber()), init);
                bexp.setSourcePosition(init);
                this.typeCheckAssignment(bexp, left, node.getOriginType(), init, this.getType(init));
                if (init instanceof ConstructorCallExpression) {
                    this.inferDiamondType((ConstructorCallExpression)init, node.getOriginType());
                }
            }
        }
        finally {
            this.currentField = null;
            this.typeCheckingContext.isInStaticContext = osc;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitForLoop(ForStatement forLoop) {
        HashMap<VariableExpression, ClassNode> varOrigType = new HashMap<VariableExpression, ClassNode>();
        forLoop.getLoopBlock().visit(new VariableExpressionTypeMemoizer(varOrigType));
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        Expression collectionExpression = forLoop.getCollectionExpression();
        if (collectionExpression instanceof ClosureListExpression) {
            super.visitForLoop(forLoop);
        } else {
            collectionExpression.visit(this);
            ClassNode collectionType = this.getType(collectionExpression);
            ClassNode componentType = StaticTypeCheckingVisitor.inferLoopElementType(collectionType);
            ClassNode forLoopVariableType = forLoop.getVariableType();
            if (ClassHelper.getUnwrapper(componentType) == forLoopVariableType) {
                componentType = forLoopVariableType;
            }
            if (!StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(forLoopVariableType, componentType)) {
                this.addStaticTypeError("Cannot loop with element of type " + forLoopVariableType.toString(false) + " with collection of type " + collectionType.toString(false), forLoop);
            }
            if (forLoopVariableType != ClassHelper.DYNAMIC_TYPE) {
                componentType = forLoopVariableType;
            }
            this.typeCheckingContext.controlStructureVariables.put(forLoop.getVariable(), componentType);
            try {
                super.visitForLoop(forLoop);
            }
            finally {
                this.typeCheckingContext.controlStructureVariables.remove(forLoop.getVariable());
            }
        }
        boolean typeChanged = this.isSecondPassNeededForControlStructure(varOrigType, oldTracker);
        if (typeChanged) {
            this.visitForLoop(forLoop);
        }
    }

    public static ClassNode inferLoopElementType(ClassNode collectionType) {
        ClassNode componentType = collectionType.getComponentType();
        if (componentType == null) {
            if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(collectionType, ITERABLE_TYPE)) {
                ClassNode intf = GenericsUtils.parameterizeType(collectionType, ITERABLE_TYPE);
                GenericsType[] genericsTypes = intf.getGenericsTypes();
                componentType = genericsTypes[0].getType();
            } else if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(collectionType, ClassHelper.MAP_TYPE)) {
                ClassNode intf = GenericsUtils.parameterizeType(collectionType, ClassHelper.MAP_TYPE);
                GenericsType[] genericsTypes = intf.getGenericsTypes();
                componentType = MAP_ENTRY_TYPE.getPlainNodeReference();
                componentType.setGenericsTypes(genericsTypes);
            } else if (ClassHelper.STRING_TYPE.equals(collectionType)) {
                componentType = ClassHelper.Character_TYPE;
            } else if (ENUMERATION_TYPE.equals(collectionType)) {
                ClassNode intf = GenericsUtils.parameterizeType(collectionType, ENUMERATION_TYPE);
                GenericsType[] genericsTypes = intf.getGenericsTypes();
                componentType = genericsTypes[0].getType();
            } else {
                componentType = ClassHelper.OBJECT_TYPE;
            }
        }
        return componentType;
    }

    protected boolean isSecondPassNeededForControlStructure(Map<VariableExpression, ClassNode> varOrigType, Map<VariableExpression, List<ClassNode>> oldTracker) {
        Map<VariableExpression, ClassNode> assignedVars = this.popAssignmentTracking(oldTracker);
        for (Map.Entry<VariableExpression, ClassNode> entry : assignedVars.entrySet()) {
            Variable key = StaticTypeCheckingSupport.findTargetVariable(entry.getKey());
            if (!(key instanceof VariableExpression)) continue;
            ClassNode origType = varOrigType.get(key);
            ClassNode newType = entry.getValue();
            if (!varOrigType.containsKey(key) || origType != null && newType.equals(origType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        super.visitWhileLoop(loop);
        this.popAssignmentTracking(oldTracker);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        MethodNode mn;
        super.visitBitwiseNegationExpression(expression);
        ClassNode type = this.getType(expression);
        ClassNode typeRe = type.redirect();
        ClassNode resultType = WideningCategories.isBigIntCategory(typeRe) ? type : (typeRe == ClassHelper.STRING_TYPE || typeRe == ClassHelper.GSTRING_TYPE ? ClassHelper.PATTERN_TYPE : (typeRe == StaticTypeCheckingSupport.ArrayList_TYPE ? StaticTypeCheckingSupport.ArrayList_TYPE : (typeRe.equals(ClassHelper.PATTERN_TYPE) ? ClassHelper.PATTERN_TYPE : ((mn = this.findMethodOrFail(expression, type, "bitwiseNegate", new ClassNode[0])) != null ? mn.getReturnType() : ClassHelper.OBJECT_TYPE))));
        this.storeType(expression, resultType);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression);
        this.negativeOrPositiveUnary(expression, "positive");
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression);
        this.negativeOrPositiveUnary(expression, "negative");
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression);
        Expression inner = expression.getExpression();
        int op = expression.getOperation().getType();
        this.visitPrefixOrPostifExpression(expression, inner, op);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression);
        Expression inner = expression.getExpression();
        int type = expression.getOperation().getType();
        this.visitPrefixOrPostifExpression(expression, inner, type);
    }

    private static ClassNode getMathWideningClassNode(ClassNode type) {
        if (ClassHelper.byte_TYPE.equals(type) || ClassHelper.short_TYPE.equals(type) || ClassHelper.int_TYPE.equals(type)) {
            return ClassHelper.int_TYPE;
        }
        if (ClassHelper.Byte_TYPE.equals(type) || ClassHelper.Short_TYPE.equals(type) || ClassHelper.Integer_TYPE.equals(type)) {
            return ClassHelper.Integer_TYPE;
        }
        if (ClassHelper.float_TYPE.equals(type)) {
            return ClassHelper.double_TYPE;
        }
        if (ClassHelper.Float_TYPE.equals(type)) {
            return ClassHelper.Double_TYPE;
        }
        return type;
    }

    private void visitPrefixOrPostifExpression(Expression origin, Expression innerExpression, int operationType) {
        MethodNode node;
        String name;
        boolean isPostfix = origin instanceof PostfixExpression;
        ClassNode exprType = this.getType(innerExpression);
        String string = operationType == 250 ? "next" : (name = operationType == 260 ? "previous" : null);
        if (ClassHelper.isPrimitiveType(exprType) || ClassHelper.isPrimitiveType(ClassHelper.getUnwrapper(exprType))) {
            if (operationType == 250 || operationType == 260) {
                MethodNode node2;
                if (!ClassHelper.isPrimitiveType(exprType) && (node2 = this.findMethodOrFail(GeneralUtils.varX("_dummy_", exprType), exprType, name, new ClassNode[0])) != null) {
                    this.storeTargetMethod(origin, node2);
                    this.storeType(origin, isPostfix ? exprType : StaticTypeCheckingVisitor.getMathWideningClassNode(exprType));
                    return;
                }
                this.storeType(origin, exprType);
                return;
            }
            this.addUnsupportedPreOrPostfixExpressionError(origin);
            return;
        }
        if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(exprType, ClassHelper.Number_TYPE) && (operationType == 250 || operationType == 260) && (node = this.findMethodOrFail(innerExpression, exprType, name, new ClassNode[0])) != null) {
            this.storeTargetMethod(origin, node);
            this.storeType(origin, StaticTypeCheckingVisitor.getMathWideningClassNode(exprType));
            return;
        }
        if (name == null) {
            this.addUnsupportedPreOrPostfixExpressionError(origin);
            return;
        }
        node = this.findMethodOrFail(innerExpression, exprType, name, new ClassNode[0]);
        if (node != null) {
            this.storeTargetMethod(origin, node);
            this.storeType(origin, isPostfix ? exprType : this.inferReturnTypeGenerics(exprType, node, ArgumentListExpression.EMPTY_ARGUMENTS));
        }
    }

    private void negativeOrPositiveUnary(Expression expression, String name) {
        MethodNode mn;
        ClassNode type = this.getType(expression);
        ClassNode typeRe = type.redirect();
        ClassNode resultType = WideningCategories.isDoubleCategory(ClassHelper.getUnwrapper(typeRe)) ? type : (typeRe == StaticTypeCheckingSupport.ArrayList_TYPE ? StaticTypeCheckingSupport.ArrayList_TYPE : ((mn = this.findMethodOrFail(expression, type, name, new ClassNode[0])) != null ? mn.getReturnType() : type));
        this.storeType(expression, resultType);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        this.typeCheckingContext.pushEnclosingMethod(node);
        if (!this.isSkipMode(node) && !this.shouldSkipMethodNode(node)) {
            super.visitConstructorOrMethod(node, isConstructor);
        }
        if (!isConstructor) {
            this.returnAdder.visitMethod(node);
        }
        this.typeCheckingContext.popEnclosingMethod();
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement);
        this.returnListener.returnStatementAdded(statement);
    }

    protected ClassNode checkReturnType(ReturnStatement statement) {
        MethodNode enclosingMethod;
        Expression expression = statement.getExpression();
        ClassNode type = this.getType(expression);
        if (this.typeCheckingContext.getEnclosingClosure() != null) {
            return type;
        }
        if (expression instanceof VariableExpression && StaticTypeCheckingVisitor.hasInferredReturnType(expression)) {
            type = (ClassNode)expression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
        }
        if ((enclosingMethod = this.typeCheckingContext.getEnclosingMethod()) != null && this.typeCheckingContext.getEnclosingClosure() == null) {
            if (!(enclosingMethod.isVoidMethod() || type.equals(ClassHelper.void_WRAPPER_TYPE) || type.equals(ClassHelper.VOID_TYPE) || StaticTypeCheckingSupport.checkCompatibleAssignmentTypes(enclosingMethod.getReturnType(), type, null, false) || StaticTypeCheckingVisitor.isNullConstant(expression))) {
                if (!this.extension.handleIncompatibleReturnType(statement, type)) {
                    this.addStaticTypeError("Cannot return value of type " + type.toString(false) + " on method returning type " + enclosingMethod.getReturnType().toString(false), expression);
                }
            } else if (!enclosingMethod.isVoidMethod()) {
                ClassNode inferred;
                ClassNode previousType = this.getInferredReturnType(enclosingMethod);
                ClassNode classNode = inferred = previousType == null ? type : WideningCategories.lowestUpperBound(type, previousType);
                if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(inferred, enclosingMethod.getReturnType())) {
                    if (StaticTypeCheckingSupport.missesGenericsTypes(inferred)) {
                        DeclarationExpression virtualDecl = new DeclarationExpression(GeneralUtils.varX("{target}", enclosingMethod.getReturnType()), Token.newSymbol(100, -1, -1), (Expression)GeneralUtils.varX("{source}", type));
                        virtualDecl.setSourcePosition(statement);
                        virtualDecl.visit(this);
                        ClassNode newlyInferred = (ClassNode)virtualDecl.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
                        if (!StaticTypeCheckingSupport.missesGenericsTypes(newlyInferred)) {
                            type = newlyInferred;
                        }
                    } else {
                        this.checkTypeGenerics(enclosingMethod.getReturnType(), inferred, expression);
                    }
                    return type;
                }
                return enclosingMethod.getReturnType();
            }
        }
        return type;
    }

    protected void addClosureReturnType(ClassNode returnType) {
        this.typeCheckingContext.getEnclosingClosure().addReturnType(returnType);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        MethodNode node;
        super.visitConstructorCallExpression(call);
        if (this.extension.beforeMethodCall(call)) {
            this.extension.afterMethodCall(call);
            return;
        }
        ClassNode receiver = call.isThisCall() ? this.typeCheckingContext.getEnclosingClassNode() : (call.isSuperCall() ? this.typeCheckingContext.getEnclosingClassNode().getSuperClass() : call.getType());
        Expression arguments = call.getArguments();
        ArgumentListExpression argumentList = InvocationWriter.makeArgumentList(arguments);
        this.checkForbiddenSpreadArgument(argumentList);
        ClassNode[] args = this.getArgumentTypes(argumentList);
        if (args.length > 0 && this.typeCheckingContext.getEnclosingClosure() != null && argumentList.getExpression(0) instanceof VariableExpression && ((VariableExpression)argumentList.getExpression(0)).isThisExpression() && call.getType() instanceof InnerClassNode && call.getType().getOuterClass().equals(args[0]) && !call.getType().isStaticClass()) {
            args[0] = ClassHelper.CLOSURE_TYPE;
        }
        if (this.looksLikeNamedArgConstructor(receiver, args) && this.findMethod(receiver, "<init>", DefaultGroovyMethods.init(args)).size() == 1 && this.findMethod(receiver, "<init>", args).isEmpty() && (node = this.typeCheckMapConstructor(call, receiver, arguments)) != null) {
            this.storeTargetMethod(call, node);
            this.extension.afterMethodCall(call);
            return;
        }
        node = this.findMethodOrFail(call, receiver, "<init>", args);
        if (node != null) {
            if (this.looksLikeNamedArgConstructor(receiver, args) && node.getParameters().length + 1 == args.length) {
                node = this.typeCheckMapConstructor(call, receiver, arguments);
            } else {
                this.typeCheckMethodsWithGenericsOrFail(receiver, args, node, call);
            }
            if (node != null) {
                this.storeTargetMethod(call, node);
            }
        }
        this.extension.afterMethodCall(call);
    }

    private boolean looksLikeNamedArgConstructor(ClassNode receiver, ClassNode[] args) {
        return (args.length == 1 || args.length == 2 && this.isInnerConstructor(receiver, args[0])) && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(args[args.length - 1], ClassHelper.MAP_TYPE);
    }

    private boolean isInnerConstructor(ClassNode receiver, ClassNode parent) {
        return receiver.isRedirectNode() && receiver.redirect() instanceof InnerClassNode && receiver.redirect().getOuterClass().equals(parent);
    }

    protected MethodNode typeCheckMapConstructor(ConstructorCallExpression call, ClassNode receiver, Expression arguments) {
        Expression expression;
        TupleExpression texp;
        List<Expression> expressions;
        ConstructorNode node = null;
        if (arguments instanceof TupleExpression && ((expressions = (texp = (TupleExpression)arguments).getExpressions()).size() == 1 || expressions.size() == 2) && (expression = expressions.get(expressions.size() - 1)) instanceof MapExpression) {
            Parameter[] parameterArray;
            MapExpression argList = (MapExpression)expression;
            this.checkGroovyConstructorMap(call, receiver, argList);
            if (expressions.size() == 1) {
                Parameter[] parameterArray2 = new Parameter[1];
                parameterArray = parameterArray2;
                parameterArray2[0] = new Parameter(ClassHelper.MAP_TYPE, "map");
            } else {
                Parameter[] parameterArray3 = new Parameter[2];
                parameterArray3[0] = new Parameter(receiver.redirect().getOuterClass(), "$p$");
                parameterArray = parameterArray3;
                parameterArray3[1] = new Parameter(ClassHelper.MAP_TYPE, "map");
            }
            Parameter[] params = parameterArray;
            node = new ConstructorNode(1, params, ClassNode.EMPTY_ARRAY, GENERATED_EMPTY_STATEMENT);
            node.setDeclaringClass(receiver);
        }
        return node;
    }

    protected ClassNode[] getArgumentTypes(ArgumentListExpression args) {
        List<Expression> arglist = args.getExpressions();
        ClassNode[] ret = new ClassNode[arglist.size()];
        for (int i = 0; i < arglist.size(); ++i) {
            Expression exp = arglist.get(i);
            ret[i] = StaticTypeCheckingVisitor.isNullConstant(exp) ? StaticTypeCheckingSupport.UNKNOWN_PARAMETER_TYPE : this.getInferredTypeFromTempInfo(exp, this.getType(exp));
        }
        return ret;
    }

    private ClassNode getInferredTypeFromTempInfo(Expression exp, ClassNode result) {
        List<ClassNode> classNodes;
        Map<Object, List<ClassNode>> info;
        Map<Object, List<ClassNode>> map = info = this.typeCheckingContext.temporaryIfBranchTypeInformation.empty() ? null : this.typeCheckingContext.temporaryIfBranchTypeInformation.peek();
        if (exp instanceof VariableExpression && info != null && (classNodes = this.getTemporaryTypesForExpression(exp)) != null && !classNodes.isEmpty()) {
            ArrayList<ClassNode> arr = new ArrayList<ClassNode>(classNodes.size() + 1);
            if (result != null && !classNodes.contains(result)) {
                arr.add(result);
            }
            arr.addAll(classNodes);
            Iterator iterator = arr.iterator();
            while (iterator.hasNext()) {
                ClassNode next = (ClassNode)iterator.next();
                if (!ClassHelper.OBJECT_TYPE.equals(next)) continue;
                iterator.remove();
            }
            result = arr.isEmpty() ? ClassHelper.OBJECT_TYPE.getPlainNodeReference() : (arr.size() == 1 ? (ClassNode)arr.get(0) : new UnionTypeClassNode(arr.toArray(new ClassNode[arr.size()])));
        }
        return result;
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        boolean oldStaticContext = this.typeCheckingContext.isInStaticContext;
        this.typeCheckingContext.isInStaticContext = false;
        HashMap<VariableExpression, ClassNode> varOrigType = new HashMap<VariableExpression, ClassNode>();
        Statement code = expression.getCode();
        code.visit(new VariableExpressionTypeMemoizer(varOrigType));
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        SharedVariableCollector collector = new SharedVariableCollector(this.getSourceUnit());
        collector.visitClosureExpression(expression);
        Set<VariableExpression> closureSharedExpressions = collector.getClosureSharedExpressions();
        HashMap<VariableExpression, ListHashMap> typesBeforeVisit = null;
        if (!closureSharedExpressions.isEmpty()) {
            typesBeforeVisit = new HashMap<VariableExpression, ListHashMap>();
            this.saveVariableExpressionMetadata(closureSharedExpressions, typesBeforeVisit);
        }
        this.typeCheckingContext.pushEnclosingClosureExpression(expression);
        DelegationMetadata dmd = this.getDelegationMetadata(expression);
        this.typeCheckingContext.delegationMetadata = dmd == null ? new DelegationMetadata(this.typeCheckingContext.getEnclosingClassNode(), 0, this.typeCheckingContext.delegationMetadata) : new DelegationMetadata(dmd.getType(), dmd.getStrategy(), this.typeCheckingContext.delegationMetadata);
        super.visitClosureExpression(expression);
        this.typeCheckingContext.delegationMetadata = this.typeCheckingContext.delegationMetadata.getParent();
        MethodNode node = new MethodNode("dummy", 0, ClassHelper.OBJECT_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, code);
        this.returnAdder.visitMethod(node);
        TypeCheckingContext.EnclosingClosure enclosingClosure = this.typeCheckingContext.getEnclosingClosure();
        if (!enclosingClosure.getReturnTypes().isEmpty()) {
            ClassNode returnType = WideningCategories.lowestUpperBound(enclosingClosure.getReturnTypes());
            this.storeInferredReturnType(expression, returnType);
            ClassNode inferredType = StaticTypeCheckingVisitor.wrapClosureType(returnType);
            this.storeType(enclosingClosure.getClosureExpression(), inferredType);
        }
        this.typeCheckingContext.popEnclosingClosure();
        boolean typeChanged = this.isSecondPassNeededForControlStructure(varOrigType, oldTracker);
        if (typeChanged) {
            this.visitClosureExpression(expression);
        }
        this.restoreVariableExpressionMetadata(typesBeforeVisit);
        this.typeCheckingContext.isInStaticContext = oldStaticContext;
        Parameter[] parameters = expression.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                this.typeCheckingContext.controlStructureVariables.remove(parameter);
            }
        }
    }

    private static ClassNode wrapClosureType(ClassNode returnType) {
        ClassNode inferredType = ClassHelper.CLOSURE_TYPE.getPlainNodeReference();
        inferredType.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(returnType))});
        return inferredType;
    }

    protected DelegationMetadata getDelegationMetadata(ClosureExpression expression) {
        return (DelegationMetadata)expression.getNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA);
    }

    protected void restoreVariableExpressionMetadata(Map<VariableExpression, ListHashMap> typesBeforeVisit) {
        if (typesBeforeVisit != null) {
            for (Map.Entry<VariableExpression, ListHashMap> entry : typesBeforeVisit.entrySet()) {
                VariableExpression ve = entry.getKey();
                ListHashMap metadata = entry.getValue();
                for (StaticTypesMarker marker : StaticTypesMarker.values()) {
                    ve.removeNodeMetaData((Object)marker);
                    Object value = metadata.get((Object)marker);
                    if (value == null) continue;
                    ve.setNodeMetaData((Object)marker, value);
                }
            }
        }
    }

    protected void saveVariableExpressionMetadata(Set<VariableExpression> closureSharedExpressions, Map<VariableExpression, ListHashMap> typesBeforeVisit) {
        for (VariableExpression ve : closureSharedExpressions) {
            this.getType(ve);
            ListHashMap metadata = new ListHashMap();
            for (StaticTypesMarker marker : StaticTypesMarker.values()) {
                Object value = ve.getNodeMetaData((Object)marker);
                if (value == null) continue;
                metadata.put(marker, value);
            }
            typesBeforeVisit.put(ve, metadata);
            Variable accessedVariable = ve.getAccessedVariable();
            if (accessedVariable == ve || !(accessedVariable instanceof VariableExpression)) continue;
            this.saveVariableExpressionMetadata(Collections.singleton((VariableExpression)accessedVariable), typesBeforeVisit);
        }
    }

    protected boolean shouldSkipMethodNode(MethodNode node) {
        Object type = node.getNodeMetaData(StaticTypeCheckingVisitor.class);
        return Boolean.TRUE.equals(type);
    }

    @Override
    public void visitMethod(MethodNode node) {
        if (this.shouldSkipMethodNode(node)) {
            return;
        }
        if (!this.extension.beforeVisitMethod(node)) {
            ErrorCollector collector = (ErrorCollector)node.getNodeMetaData(ERROR_COLLECTOR);
            if (collector != null) {
                this.typeCheckingContext.getErrorCollector().addCollectorContents(collector);
            } else {
                this.startMethodInference(node, this.typeCheckingContext.getErrorCollector());
            }
            node.removeNodeMetaData(ERROR_COLLECTOR);
        }
        this.extension.afterVisitMethod(node);
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        if (this.shouldSkipMethodNode(node)) {
            return;
        }
        for (Parameter parameter : node.getParameters()) {
            if (parameter.getInitialExpression() == null) continue;
            parameter.getInitialExpression().visit(this);
        }
        super.visitConstructor(node);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void startMethodInference(MethodNode node, ErrorCollector collector) {
        if (this.isSkipMode(node)) {
            return;
        }
        if (!this.typeCheckingContext.methodsToBeVisited.isEmpty() && !this.typeCheckingContext.methodsToBeVisited.contains(node)) {
            return;
        }
        if (this.typeCheckingContext.alreadyVisitedMethods.contains(node)) {
            return;
        }
        this.typeCheckingContext.alreadyVisitedMethods.add(node);
        this.typeCheckingContext.pushErrorCollector(collector);
        boolean osc = this.typeCheckingContext.isInStaticContext;
        try {
            this.typeCheckingContext.isInStaticContext = node.isStatic();
            super.visitMethod(node);
            for (Parameter parameter : node.getParameters()) {
                if (parameter.getInitialExpression() == null) continue;
                parameter.getInitialExpression().visit(this);
            }
        }
        finally {
            this.typeCheckingContext.isInStaticContext = osc;
        }
        this.typeCheckingContext.popErrorCollector();
        node.putNodeMetaData(ERROR_COLLECTOR, collector);
    }

    protected void addTypeCheckingInfoAnnotation(MethodNode node) {
        if (node instanceof ConstructorNode) {
            return;
        }
        ClassNode rtype = this.getInferredReturnType(node);
        if (rtype != null && node.getAnnotations(TYPECHECKING_INFO_NODE).isEmpty()) {
            AnnotationNode anno = new AnnotationNode(TYPECHECKING_INFO_NODE);
            anno.setMember("version", CURRENT_SIGNATURE_PROTOCOL);
            SignatureCodec codec = SignatureCodecFactory.getCodec(1, this.getTransformLoader());
            String genericsSignature = codec.encode(rtype);
            if (genericsSignature != null) {
                ConstantExpression signature = new ConstantExpression(genericsSignature);
                signature.setType(ClassHelper.STRING_TYPE);
                anno.setMember("inferredType", signature);
                node.addAnnotation(anno);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        String name = call.getMethod();
        if (name == null) {
            this.addStaticTypeError("cannot resolve dynamic method name at compile time.", call);
            return;
        }
        if (this.extension.beforeMethodCall(call)) {
            this.extension.afterMethodCall(call);
            return;
        }
        Expression callArguments = call.getArguments();
        ArgumentListExpression argumentList = InvocationWriter.makeArgumentList(callArguments);
        this.checkForbiddenSpreadArgument(argumentList);
        ClassNode receiver = call.getOwnerType();
        this.visitMethodCallArguments(receiver, argumentList, false, null);
        ClassNode[] args = this.getArgumentTypes(argumentList);
        try {
            LinkedList<Receiver<String>> receivers = new LinkedList<Receiver<String>>();
            this.addReceivers(receivers, this.makeOwnerList(new ClassExpression(receiver)), false);
            List<MethodNode> mn = null;
            Receiver chosenReceiver = null;
            for (Receiver receiver2 : receivers) {
                mn = this.findMethod(receiver2.getType(), name, args);
                if (mn.isEmpty()) continue;
                if (mn.size() == 1) {
                    this.typeCheckMethodsWithGenericsOrFail(receiver2.getType(), args, mn.get(0), call);
                }
                chosenReceiver = receiver2;
                break;
            }
            if (mn.isEmpty()) {
                mn = this.extension.handleMissingMethod(receiver, name, argumentList, args, call);
            }
            boolean callArgsVisited = false;
            if (mn.isEmpty()) {
                this.addNoMatchingMethodError(receiver, name, args, call);
            } else {
                if ((mn = this.disambiguateMethods(mn, receiver, args, call)).size() == 1) {
                    MethodNode methodNode = mn.get(0);
                    ClassNode returnType = this.getType(methodNode);
                    if (returnType.isUsingGenerics() && !returnType.isEnum()) {
                        this.visitMethodCallArguments(receiver, argumentList, true, methodNode);
                        ClassNode irtg = this.inferReturnTypeGenerics(chosenReceiver.getType(), methodNode, callArguments);
                        returnType = irtg != null && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(irtg, returnType) ? irtg : returnType;
                        callArgsVisited = true;
                    }
                    this.storeType(call, returnType);
                    this.storeTargetMethod(call, methodNode);
                } else {
                    this.addAmbiguousErrorMessage(mn, name, args, call);
                }
                if (!callArgsVisited) {
                    this.visitMethodCallArguments(receiver, argumentList, true, (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET));
                }
            }
        }
        finally {
            this.extension.afterMethodCall(call);
        }
    }

    @Deprecated
    protected void checkClosureParameters(Expression callArguments, ClassNode receiver) {
        if (callArguments instanceof ArgumentListExpression) {
            Parameter param;
            ArgumentListExpression argList = (ArgumentListExpression)callArguments;
            ClosureExpression closure = (ClosureExpression)argList.getExpression(0);
            Parameter[] parameters = closure.getParameters();
            if (parameters.length > 1) {
                this.addStaticTypeError("Unexpected number of parameters for a with call", argList);
            } else if (parameters.length == 1 && !(param = parameters[0]).isDynamicTyped() && !StaticTypeCheckingSupport.isAssignableTo(receiver, param.getType().redirect())) {
                this.addStaticTypeError("Expected parameter type: " + receiver.toString(false) + " but was: " + param.getType().redirect().toString(false), param);
            }
            closure.putNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA, new DelegationMetadata(receiver, 1, this.typeCheckingContext.delegationMetadata));
        }
    }

    protected void silentlyVisitMethodNode(MethodNode directMethodCallCandidate) {
        ErrorCollector collector = new ErrorCollector(this.typeCheckingContext.getErrorCollector().getConfiguration());
        this.startMethodInference(directMethodCallCandidate, collector);
    }

    protected void visitMethodCallArguments(ClassNode receiver, ArgumentListExpression arguments, boolean visitClosures, MethodNode selectedMethod) {
        Parameter[] params = selectedMethod != null ? selectedMethod.getParameters() : Parameter.EMPTY_ARRAY;
        LinkedList<Expression> expressions = new LinkedList<Expression>(arguments.getExpressions());
        if (selectedMethod instanceof ExtensionMethodNode) {
            params = ((ExtensionMethodNode)selectedMethod).getExtensionMethodNode().getParameters();
            expressions.add(0, GeneralUtils.varX("$self", receiver));
        }
        ArgumentListExpression newArgs = GeneralUtils.args(expressions);
        int expressionsSize = expressions.size();
        for (int i = 0; i < expressionsSize; ++i) {
            Expression expression = (Expression)expressions.get(i);
            if ((!visitClosures || !(expression instanceof ClosureExpression)) && (visitClosures || expression instanceof ClosureExpression)) continue;
            if (i < params.length && visitClosures) {
                Parameter param = params[i];
                this.checkClosureWithDelegatesTo(receiver, selectedMethod, newArgs, params, expression, param);
                if (selectedMethod instanceof ExtensionMethodNode) {
                    if (i > 0) {
                        this.inferClosureParameterTypes(receiver, arguments, (ClosureExpression)expression, param, selectedMethod);
                    }
                } else {
                    this.inferClosureParameterTypes(receiver, newArgs, (ClosureExpression)expression, param, selectedMethod);
                }
            }
            expression.visit(this);
            if (expression.getNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA) == null) continue;
            expression.removeNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA);
        }
    }

    protected void inferClosureParameterTypes(ClassNode receiver, Expression arguments, ClosureExpression expression, Parameter param, MethodNode selectedMethod) {
        List<AnnotationNode> annotations = param.getAnnotations(CLOSUREPARAMS_CLASSNODE);
        if (annotations != null && !annotations.isEmpty()) {
            for (AnnotationNode annotation : annotations) {
                Expression hintClass = annotation.getMember("value");
                Expression options = annotation.getMember("options");
                if (!(hintClass instanceof ClassExpression)) continue;
                this.doInferClosureParameterTypes(receiver, arguments, expression, selectedMethod, hintClass, options);
            }
        } else if (ClassHelper.isSAMType(param.getOriginType())) {
            this.inferSAMType(param, receiver, selectedMethod, InvocationWriter.makeArgumentList(arguments), expression);
        }
    }

    private void inferSAMType(Parameter param, ClassNode receiver, MethodNode methodWithSAMParameter, ArgumentListExpression originalMethodCallArguments, ClosureExpression openBlock) {
        int i;
        HashMap<String, GenericsType> targetMethodDeclarationClassConnections = new HashMap<String, GenericsType>();
        StaticTypeCheckingSupport.extractGenericsConnections(targetMethodDeclarationClassConnections, receiver, receiver.redirect());
        Parameter[] parametersOfMethodContainingSAM = methodWithSAMParameter.getParameters();
        for (int i2 = 0; i2 < parametersOfMethodContainingSAM.length; ++i2) {
            Expression callArg;
            if (i2 == parametersOfMethodContainingSAM.length - 1 && i2 == originalMethodCallArguments.getExpressions().size() && parametersOfMethodContainingSAM[i2].getType().isArray() || (callArg = originalMethodCallArguments.getExpression(i2)) == openBlock) continue;
            ClassNode parameterType = parametersOfMethodContainingSAM[i2].getType();
            StaticTypeCheckingSupport.extractGenericsConnections(targetMethodDeclarationClassConnections, this.getType(callArg), parameterType);
        }
        ClassNode paramTypeWithReceiverInformation = StaticTypeCheckingSupport.applyGenericsContext(targetMethodDeclarationClassConnections, param.getOriginType());
        HashMap<String, GenericsType> SAMTypeConnections = new HashMap<String, GenericsType>();
        ClassNode classForSAM = paramTypeWithReceiverInformation.redirect();
        StaticTypeCheckingSupport.extractGenericsConnections(SAMTypeConnections, paramTypeWithReceiverInformation, classForSAM);
        MethodNode methodForSAM = ClassHelper.findSAM(classForSAM);
        ClassNode[] parameterTypesForSAM = StaticTypeCheckingVisitor.extractTypesFromParameters(methodForSAM.getParameters());
        ClassNode[] blockParameterTypes = (ClassNode[])openBlock.getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS);
        if (blockParameterTypes == null) {
            Parameter[] p = openBlock.getParameters();
            if (p == null) {
                blockParameterTypes = ClassNode.EMPTY_ARRAY;
            } else if (p.length == 0 && parameterTypesForSAM.length != 0) {
                blockParameterTypes = parameterTypesForSAM;
            } else {
                blockParameterTypes = new ClassNode[p.length];
                for (int i3 = 0; i3 < p.length; ++i3) {
                    blockParameterTypes[i3] = p[i3] != null && !p[i3].isDynamicTyped() ? p[i3].getType() : this.typeOrNull(parameterTypesForSAM, i3);
                }
            }
        }
        for (i = 0; i < blockParameterTypes.length; ++i) {
            StaticTypeCheckingSupport.extractGenericsConnections(SAMTypeConnections, blockParameterTypes[i], this.typeOrNull(parameterTypesForSAM, i));
        }
        for (i = 0; i < blockParameterTypes.length; ++i) {
            ClassNode resolvedParameter;
            blockParameterTypes[i] = resolvedParameter = StaticTypeCheckingSupport.applyGenericsContext(SAMTypeConnections, this.typeOrNull(parameterTypesForSAM, i));
        }
        openBlock.putNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS, blockParameterTypes);
    }

    private ClassNode typeOrNull(ClassNode[] parameterTypesForSAM, int i) {
        return i < parameterTypesForSAM.length ? parameterTypesForSAM[i] : null;
    }

    private List<ClassNode[]> getSignaturesFromHint(ClosureExpression expression, MethodNode selectedMethod, Expression hintClass, Expression options) {
        List<ClassNode[]> closureSignatures;
        try {
            ClassLoader transformLoader = this.getTransformLoader();
            Class<?> hint = transformLoader.loadClass(hintClass.getText());
            ClosureSignatureHint hintInstance = (ClosureSignatureHint)hint.newInstance();
            closureSignatures = hintInstance.getClosureSignatures(selectedMethod instanceof ExtensionMethodNode ? ((ExtensionMethodNode)selectedMethod).getExtensionMethodNode() : selectedMethod, this.typeCheckingContext.source, this.typeCheckingContext.compilationUnit, StaticTypeCheckingVisitor.convertToStringArray(options), expression);
        }
        catch (ClassNotFoundException e) {
            throw new GroovyBugError(e);
        }
        catch (InstantiationException e) {
            throw new GroovyBugError(e);
        }
        catch (IllegalAccessException e) {
            throw new GroovyBugError(e);
        }
        return closureSignatures;
    }

    private ClassLoader getTransformLoader() {
        CompilationUnit compilationUnit = this.typeCheckingContext.getCompilationUnit();
        return compilationUnit != null ? compilationUnit.getTransformLoader() : this.getSourceUnit().getClassLoader();
    }

    private void doInferClosureParameterTypes(ClassNode receiver, Expression arguments, ClosureExpression expression, MethodNode selectedMethod, Expression hintClass, Expression options) {
        ClassNode[] inferred;
        List<ClassNode[]> closureSignatures = this.getSignaturesFromHint(expression, selectedMethod, hintClass, options);
        LinkedList<ClassNode[]> candidates = new LinkedList<ClassNode[]>();
        for (ClassNode[] signature : closureSignatures) {
            inferred = this.resolveGenericsFromTypeHint(receiver, arguments, selectedMethod, signature);
            Parameter[] closureParams = expression.getParameters();
            if (signature.length != closureParams.length && (signature.length != 1 || closureParams.length != 0) && (closureParams.length <= signature.length || !inferred[inferred.length - 1].isArray())) continue;
            candidates.add(inferred);
        }
        Parameter[] closureParams = expression.getParameters();
        if (candidates.size() > 1) {
            Iterator candIt = candidates.iterator();
            while (candIt.hasNext()) {
                inferred = (ClassNode[])candIt.next();
                int length = closureParams.length;
                for (int i = 0; i < length; ++i) {
                    ClassNode inferredType;
                    Parameter closureParam = closureParams[i];
                    ClassNode originType = closureParam.getOriginType();
                    if (i < inferred.length - 1 || inferred.length == closureParams.length) {
                        inferredType = inferred[i];
                    } else {
                        ClassNode lastArgInferred = inferred[inferred.length - 1];
                        if (lastArgInferred.isArray()) {
                            inferredType = lastArgInferred.getComponentType();
                        } else {
                            candIt.remove();
                            continue;
                        }
                    }
                    if (StaticTypeCheckingSupport.typeCheckMethodArgumentWithGenerics(originType, inferredType, i == length - 1)) continue;
                    candIt.remove();
                }
            }
            if (candidates.size() > 1) {
                this.addError("Ambiguous prototypes for closure. More than one target method matches. Please use explicit argument types.", expression);
            }
        }
        if (candidates.size() == 1) {
            ClassNode[] inferred2 = (ClassNode[])candidates.get(0);
            if (closureParams.length == 0 && inferred2.length == 1) {
                expression.putNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS, inferred2);
            } else {
                int length = closureParams.length;
                for (int i = 0; i < length; ++i) {
                    boolean lastArg;
                    Parameter closureParam = closureParams[i];
                    ClassNode originType = closureParam.getOriginType();
                    ClassNode inferredType = ClassHelper.OBJECT_TYPE;
                    if (i < inferred2.length - 1 || inferred2.length == closureParams.length) {
                        inferredType = inferred2[i];
                    } else {
                        ClassNode lastArgInferred = inferred2[inferred2.length - 1];
                        if (lastArgInferred.isArray()) {
                            inferredType = lastArgInferred.getComponentType();
                        } else {
                            this.addError("Incorrect number of parameters. Expected " + inferred2.length + " but found " + closureParams.length, expression);
                        }
                    }
                    boolean bl = lastArg = i == length - 1;
                    if (lastArg && inferredType.isArray()) {
                        if (inferredType.getComponentType().equals(originType)) {
                            inferredType = originType;
                        }
                    } else if (!StaticTypeCheckingSupport.typeCheckMethodArgumentWithGenerics(originType, inferredType, lastArg)) {
                        this.addError("Expected parameter of type " + inferredType.toString(false) + " but got " + originType.toString(false), closureParam.getType());
                    }
                    this.typeCheckingContext.controlStructureVariables.put(closureParam, inferredType);
                }
            }
        }
    }

    private ClassNode[] resolveGenericsFromTypeHint(ClassNode receiver, Expression arguments, MethodNode selectedMethod, ClassNode[] signature) {
        ClassNode dummyResultNode = new ClassNode("ClForInference$" + UNIQUE_LONG.incrementAndGet(), 0, ClassHelper.OBJECT_TYPE).getPlainNodeReference();
        GenericsType[] genericTypes = new GenericsType[signature.length];
        for (int i = 0; i < signature.length; ++i) {
            genericTypes[i] = new GenericsType(signature[i]);
        }
        dummyResultNode.setGenericsTypes(genericTypes);
        MethodNode dummyMN = selectedMethod instanceof ExtensionMethodNode ? ((ExtensionMethodNode)selectedMethod).getExtensionMethodNode() : selectedMethod;
        dummyMN = new MethodNode(dummyMN.getName(), dummyMN.getModifiers(), dummyResultNode, dummyMN.getParameters(), dummyMN.getExceptions(), EmptyStatement.INSTANCE);
        dummyMN.setDeclaringClass(selectedMethod.getDeclaringClass());
        dummyMN.setGenericsTypes(selectedMethod.getGenericsTypes());
        if (selectedMethod instanceof ExtensionMethodNode) {
            ExtensionMethodNode orig = (ExtensionMethodNode)selectedMethod;
            dummyMN = new ExtensionMethodNode(dummyMN, dummyMN.getName(), dummyMN.getModifiers(), dummyResultNode, orig.getParameters(), orig.getExceptions(), EmptyStatement.INSTANCE, orig.isStaticExtension());
            dummyMN.setDeclaringClass(orig.getDeclaringClass());
            dummyMN.setGenericsTypes(orig.getGenericsTypes());
        }
        ClassNode classNode = this.inferReturnTypeGenerics(receiver, dummyMN, arguments);
        ClassNode[] inferred = new ClassNode[classNode.getGenericsTypes().length];
        for (int i = 0; i < classNode.getGenericsTypes().length; ++i) {
            ClassNode value;
            GenericsType genericsType = classNode.getGenericsTypes()[i];
            inferred[i] = value = StaticTypeCheckingVisitor.createUsableClassNodeFromGenericsType(genericsType);
        }
        return inferred;
    }

    private static ClassNode createUsableClassNodeFromGenericsType(GenericsType genericsType) {
        ClassNode lowerBound;
        ClassNode value = genericsType.getType();
        if (genericsType.isPlaceholder()) {
            value = ClassHelper.OBJECT_TYPE;
        }
        if ((lowerBound = genericsType.getLowerBound()) != null) {
            value = lowerBound;
        } else {
            ClassNode[] upperBounds = genericsType.getUpperBounds();
            if (upperBounds != null) {
                value = WideningCategories.lowestUpperBound(Arrays.asList(upperBounds));
            }
        }
        return value;
    }

    private static String[] convertToStringArray(Expression options) {
        if (options == null) {
            return EMPTY_STRING_ARRAY;
        }
        if (options instanceof ConstantExpression) {
            return new String[]{options.getText()};
        }
        if (options instanceof ListExpression) {
            List<Expression> list = ((ListExpression)options).getExpressions();
            ArrayList<String> result = new ArrayList<String>(list.size());
            for (Expression expression : list) {
                result.add(expression.getText());
            }
            return result.toArray(new String[result.size()]);
        }
        throw new IllegalArgumentException("Unexpected options for @ClosureParams:" + options);
    }

    private void checkClosureWithDelegatesTo(ClassNode receiver, MethodNode mn, ArgumentListExpression arguments, Parameter[] params, Expression expression, Parameter param) {
        List<AnnotationNode> annotations = param.getAnnotations(DELEGATES_TO);
        if (annotations != null && !annotations.isEmpty()) {
            for (AnnotationNode annotation : annotations) {
                Expression value = annotation.getMember("value");
                Expression strategy = annotation.getMember("strategy");
                Expression genericTypeIndex = annotation.getMember("genericTypeIndex");
                Expression type = annotation.getMember("type");
                Integer stInt = 0;
                if (strategy != null) {
                    stInt = (Integer)StaticTypeCheckingSupport.evaluateExpression(GeneralUtils.castX(ClassHelper.Integer_TYPE, strategy), this.typeCheckingContext.source.getConfiguration());
                }
                if (value instanceof ClassExpression && !value.getType().equals(DELEGATES_TO_TARGET)) {
                    if (genericTypeIndex != null) {
                        this.addStaticTypeError("Cannot use @DelegatesTo(genericTypeIndex=" + genericTypeIndex.getText() + ") without @DelegatesTo.Target because generic argument types are not available at runtime", value);
                    }
                    expression.putNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA, new DelegationMetadata(value.getType(), stInt, this.typeCheckingContext.delegationMetadata));
                    continue;
                }
                if (type != null && !"".equals(type.getText()) && type instanceof ConstantExpression) {
                    String typeString = type.getText();
                    ClassNode[] resolved = GenericsUtils.parseClassNodesFromString(typeString, this.getSourceUnit(), this.typeCheckingContext.compilationUnit, mn, type);
                    if (resolved == null) continue;
                    if (resolved.length == 1) {
                        resolved = this.resolveGenericsFromTypeHint(receiver, arguments, mn, resolved);
                        expression.putNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA, new DelegationMetadata(resolved[0], stInt, this.typeCheckingContext.delegationMetadata));
                        continue;
                    }
                    this.addStaticTypeError("Incorrect type hint found in method " + mn, type);
                    continue;
                }
                List<Expression> expressions = arguments.getExpressions();
                int expressionsSize = expressions.size();
                Expression parameter = annotation.getMember("target");
                String parameterName = parameter != null && parameter instanceof ConstantExpression ? parameter.getText() : "";
                int paramsLength = params.length;
                for (int j = 0; j < paramsLength; ++j) {
                    String id;
                    Parameter methodParam = params[j];
                    List<AnnotationNode> targets = methodParam.getAnnotations(DELEGATES_TO_TARGET);
                    if (targets == null || targets.size() != 1) continue;
                    AnnotationNode targetAnnotation = targets.get(0);
                    Expression idMember = targetAnnotation.getMember("value");
                    String string = id = idMember != null && idMember instanceof ConstantExpression ? idMember.getText() : "";
                    if (!id.equals(parameterName) || j >= expressionsSize) continue;
                    Expression actualArgument = expressions.get(j);
                    ClassNode actualType = this.getType(actualArgument);
                    if (genericTypeIndex != null && genericTypeIndex instanceof ConstantExpression) {
                        int gti = Integer.parseInt(genericTypeIndex.getText());
                        ClassNode paramType = methodParam.getType();
                        GenericsType[] genericsTypes = paramType.getGenericsTypes();
                        if (genericsTypes == null) {
                            this.addStaticTypeError("Cannot use @DelegatesTo(genericTypeIndex=" + genericTypeIndex.getText() + ") with a type that doesn't use generics", methodParam);
                        } else if (gti < 0 || gti >= genericsTypes.length) {
                            this.addStaticTypeError("Index of generic type @DelegatesTo(genericTypeIndex=" + genericTypeIndex.getText() + ") " + (gti < 0 ? "lower" : "greater") + " than those of the selected type", methodParam);
                        } else {
                            ClassNode pType = GenericsUtils.parameterizeType(actualType, paramType);
                            GenericsType[] pTypeGenerics = pType.getGenericsTypes();
                            if (pTypeGenerics != null && pTypeGenerics.length > gti) {
                                actualType = pTypeGenerics[gti].getType();
                            } else {
                                this.addStaticTypeError("Unable to map actual type [" + actualType.toString(false) + "] onto " + paramType.toString(false), methodParam);
                            }
                        }
                    }
                    expression.putNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA, new DelegationMetadata(actualType, stInt, this.typeCheckingContext.delegationMetadata));
                    break;
                }
                if (expression.getNodeMetaData((Object)StaticTypesMarker.DELEGATION_METADATA) != null) continue;
                this.addError("Not enough arguments found for a @DelegatesTo method call. Please check that you either use an explicit class or @DelegatesTo.Target with a correct id", arguments);
            }
        }
    }

    private static boolean isTraitHelper(ClassNode node) {
        return node instanceof InnerClassNode && Traits.isTrait(node.getOuterClass());
    }

    protected void addReceivers(List<Receiver<String>> receivers, Collection<Receiver<String>> owners, boolean implicitThis) {
        if (this.typeCheckingContext.delegationMetadata == null || !implicitThis) {
            receivers.addAll(owners);
            return;
        }
        DelegationMetadata dmd = this.typeCheckingContext.delegationMetadata;
        StringBuilder path = new StringBuilder();
        while (dmd != null) {
            int strategy = dmd.getStrategy();
            ClassNode delegate = dmd.getType();
            dmd = dmd.getParent();
            switch (strategy) {
                case 0: {
                    receivers.addAll(owners);
                    path.append("delegate");
                    StaticTypeCheckingVisitor.doAddDelegateReceiver(receivers, path, delegate);
                    break;
                }
                case 1: {
                    path.append("delegate");
                    StaticTypeCheckingVisitor.doAddDelegateReceiver(receivers, path, delegate);
                    receivers.addAll(owners);
                    break;
                }
                case 2: {
                    receivers.addAll(owners);
                    dmd = null;
                    break;
                }
                case 3: {
                    path.append("delegate");
                    StaticTypeCheckingVisitor.doAddDelegateReceiver(receivers, path, delegate);
                    dmd = null;
                }
            }
            path.append('.');
        }
    }

    private static void doAddDelegateReceiver(List<Receiver<String>> receivers, StringBuilder path, ClassNode delegate) {
        receivers.add(new Receiver<String>(delegate, path.toString()));
        if (StaticTypeCheckingVisitor.isTraitHelper(delegate)) {
            receivers.add(new Receiver<String>(delegate.getOuterClass(), path.toString()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        String name = call.getMethodAsString();
        if (name == null) {
            this.addStaticTypeError("cannot resolve dynamic method name at compile time.", call.getMethod());
            return;
        }
        if (this.extension.beforeMethodCall(call)) {
            this.extension.afterMethodCall(call);
            return;
        }
        this.typeCheckingContext.pushEnclosingMethodCall(call);
        Expression objectExpression = call.getObjectExpression();
        objectExpression.visit(this);
        call.getMethod().visit(this);
        if (call.isSpreadSafe()) {
            ClassNode expressionType = this.getType(objectExpression);
            if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(expressionType, StaticTypeCheckingSupport.Collection_TYPE) && !expressionType.isArray()) {
                this.addStaticTypeError("Spread operator can only be used on collection types", objectExpression);
                return;
            }
            ClassNode componentType = this.inferComponentType(expressionType, ClassHelper.int_TYPE);
            MethodCallExpression subcall = GeneralUtils.callX((Expression)GeneralUtils.castX(componentType, EmptyExpression.INSTANCE), name, call.getArguments());
            subcall.setLineNumber(call.getLineNumber());
            subcall.setColumnNumber(call.getColumnNumber());
            subcall.setImplicitThis(call.isImplicitThis());
            this.visitMethodCallExpression(subcall);
            ClassNode subcallReturnType = this.getType(subcall);
            ClassNode listNode = ClassHelper.LIST_TYPE.getPlainNodeReference();
            listNode.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(subcallReturnType))});
            this.storeType(call, listNode);
            this.storeTargetMethod(call, (MethodNode)subcall.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET));
            this.typeCheckingContext.popEnclosingMethodCall();
            return;
        }
        Expression callArguments = call.getArguments();
        ArgumentListExpression argumentList = InvocationWriter.makeArgumentList(callArguments);
        this.checkForbiddenSpreadArgument(argumentList);
        ClassNode receiver = this.getType(objectExpression);
        this.visitMethodCallArguments(receiver, argumentList, false, null);
        ClassNode[] args = this.getArgumentTypes(argumentList);
        boolean isCallOnClosure = this.isClosureCall(name, objectExpression, callArguments);
        try {
            Parameter[] parameters;
            boolean callArgsVisited = false;
            if (isCallOnClosure) {
                int nbOfArgs;
                Object data;
                if (objectExpression == VariableExpression.THIS_EXPRESSION) {
                    FieldNode field = this.typeCheckingContext.getEnclosingClassNode().getDeclaredField(name);
                    GenericsType[] genericsTypes = field.getType().getGenericsTypes();
                    if (genericsTypes != null) {
                        ClassNode closureReturnType = genericsTypes[0].getType();
                        Object data2 = field.getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS);
                        if (data2 != null) {
                            Parameter[] parameters2 = (Parameter[])data2;
                            this.typeCheckClosureCall(callArguments, args, parameters2);
                        }
                        this.storeType(call, closureReturnType);
                    }
                } else if (objectExpression instanceof VariableExpression) {
                    Variable variable = StaticTypeCheckingSupport.findTargetVariable((VariableExpression)objectExpression);
                    if (variable instanceof ASTNode) {
                        ClassNode type;
                        data = ((ASTNode)((Object)variable)).getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS);
                        if (data != null) {
                            parameters = (Parameter[])data;
                            this.typeCheckClosureCall(callArguments, args, parameters);
                        }
                        if ((type = this.getType((ASTNode)((Object)variable))) != null && type.equals(ClassHelper.CLOSURE_TYPE)) {
                            GenericsType[] genericsTypes = type.getGenericsTypes();
                            type = ClassHelper.OBJECT_TYPE;
                            if (genericsTypes != null && !genericsTypes[0].isPlaceholder()) {
                                type = genericsTypes[0].getType();
                            }
                        }
                        if (type != null) {
                            this.storeType(call, type);
                        }
                    }
                } else if (objectExpression instanceof ClosureExpression) {
                    Parameter[] parameters3 = ((ClosureExpression)objectExpression).getParameters();
                    this.typeCheckClosureCall(callArguments, args, parameters3);
                    data = this.getInferredReturnType(objectExpression);
                    if (data != null) {
                        this.storeType(call, (ClassNode)data);
                    }
                }
                if (callArguments instanceof ArgumentListExpression) {
                    ArgumentListExpression list = (ArgumentListExpression)callArguments;
                    nbOfArgs = list.getExpressions().size();
                } else {
                    nbOfArgs = 0;
                }
                this.storeTargetMethod(call, nbOfArgs == 0 ? CLOSURE_CALL_NO_ARG : (nbOfArgs == 1 ? CLOSURE_CALL_ONE_ARG : CLOSURE_CALL_VARGS));
            } else {
                LinkedList<Receiver<String>> receivers = new LinkedList<Receiver<String>>();
                List<Receiver<String>> owners = this.makeOwnerList(objectExpression);
                this.addReceivers(receivers, owners, call.isImplicitThis());
                List<MethodNode> mn = null;
                Receiver<Object> chosenReceiver = null;
                for (Receiver receiver2 : receivers) {
                    ClassNode receiverType = receiver2.getType();
                    mn = this.findMethod(receiverType, name, args);
                    if (!mn.isEmpty() && (this.typeCheckingContext.isInStaticContext || (receiverType.getModifiers() & 8) != 0) && (call.isImplicitThis() || objectExpression instanceof VariableExpression && ((VariableExpression)objectExpression).isThisExpression())) {
                        LinkedList<MethodNode> accessibleMethods = new LinkedList<MethodNode>();
                        LinkedList<MethodNode> inaccessibleMethods = new LinkedList<MethodNode>();
                        for (MethodNode node : mn) {
                            if (node.isStatic() || !this.typeCheckingContext.isInStaticContext && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(receiverType, node.getDeclaringClass())) {
                                accessibleMethods.add(node);
                                continue;
                            }
                            inaccessibleMethods.add(node);
                        }
                        mn = accessibleMethods;
                        if (accessibleMethods.isEmpty()) {
                            MethodNode node = (MethodNode)inaccessibleMethods.get(0);
                            ClassNode owner = node.getDeclaringClass();
                            this.addStaticTypeError("Non static method " + owner.getName() + "#" + node.getName() + " cannot be called from static context", call);
                        }
                    }
                    if (mn.isEmpty()) continue;
                    chosenReceiver = receiver2;
                    break;
                }
                if (mn.isEmpty() && this.typeCheckingContext.getEnclosingClosure() != null && args.length == 0) {
                    if ("getDelegate".equals(name)) {
                        mn = Collections.singletonList(GET_DELEGATE);
                    } else if ("getOwner".equals(name)) {
                        mn = Collections.singletonList(GET_OWNER);
                    } else if ("getThisObject".equals(name)) {
                        mn = Collections.singletonList(GET_THISOBJECT);
                    }
                }
                if (mn.isEmpty()) {
                    mn = this.extension.handleMissingMethod(receiver, name, argumentList, args, call);
                }
                if (mn.isEmpty()) {
                    this.addNoMatchingMethodError(receiver, name, args, call);
                } else {
                    if (this.areCategoryMethodCalls(mn, name, args)) {
                        this.addCategoryMethodCallError(call);
                    }
                    if ((mn = this.disambiguateMethods(mn, chosenReceiver != null ? chosenReceiver.getType() : null, args, call)).size() == 1) {
                        void var15_30;
                        Object var15_28;
                        MethodNode directMethodCallCandidate = mn.get(0);
                        if (call.getNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION) == null && !directMethodCallCandidate.isStatic() && objectExpression instanceof ClassExpression && !"java.lang.Class".equals(directMethodCallCandidate.getDeclaringClass().getName())) {
                            ClassNode classNode = directMethodCallCandidate.getDeclaringClass();
                            this.addStaticTypeError("Non static method " + classNode.getName() + "#" + directMethodCallCandidate.getName() + " cannot be called from static context", call);
                        }
                        if (chosenReceiver == null && (chosenReceiver = Receiver.make(directMethodCallCandidate.getDeclaringClass())) == null) {
                            chosenReceiver = owners.get(0);
                        }
                        if ((var15_28 = null) == null) {
                            ClassNode classNode = this.getType(directMethodCallCandidate);
                        }
                        if (StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics((ClassNode)var15_30)) {
                            this.visitMethodCallArguments(chosenReceiver.getType(), argumentList, true, directMethodCallCandidate);
                            ClassNode irtg = this.inferReturnTypeGenerics(chosenReceiver.getType(), directMethodCallCandidate, callArguments, call.getGenericsTypes());
                            void var15_31 = irtg != null && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(irtg, (ClassNode)var15_30) ? irtg : var15_30;
                            callArgsVisited = true;
                        }
                        if (directMethodCallCandidate == GET_DELEGATE && this.typeCheckingContext.getEnclosingClosure() != null) {
                            DelegationMetadata md = this.getDelegationMetadata(this.typeCheckingContext.getEnclosingClosure().getClosureExpression());
                            ClassNode classNode = this.typeCheckingContext.getEnclosingClassNode();
                            if (md != null) {
                                ClassNode classNode2 = md.getType();
                            }
                        }
                        if (this.typeCheckMethodsWithGenericsOrFail(chosenReceiver.getType(), args, mn.get(0), call)) {
                            VariableExpression var;
                            String data;
                            void var15_35;
                            ClassNode classNode = StaticTypeCheckingVisitor.adjustWithTraits(directMethodCallCandidate, chosenReceiver.getType(), args, (ClassNode)var15_35);
                            this.storeType(call, classNode);
                            this.storeTargetMethod(call, directMethodCallCandidate);
                            String string = data = chosenReceiver != null ? (String)chosenReceiver.getData() : null;
                            if (data != null) {
                                call.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, data);
                            }
                            if (objectExpression instanceof VariableExpression && (var = (VariableExpression)objectExpression).isClosureSharedVariable()) {
                                SecondPassExpression<ClassNode[]> wrapper = new SecondPassExpression<ClassNode[]>(call, args);
                                this.typeCheckingContext.secondPassExpressions.add(wrapper);
                            }
                        }
                    } else {
                        this.addAmbiguousErrorMessage(mn, name, args, call);
                    }
                }
            }
            if (StaticTypeCheckingSupport.NUMBER_OPS.containsKey(name) && ClassHelper.isNumberType(receiver) && argumentList.getExpressions().size() == 1 && ClassHelper.isNumberType(this.getType(argumentList.getExpression(0)))) {
                ClassNode right = this.getType(argumentList.getExpression(0));
                ClassNode resultType = this.getMathResultType(StaticTypeCheckingSupport.NUMBER_OPS.get(name), receiver, right, name);
                if (resultType != null) {
                    this.storeType(call, resultType);
                }
            }
            if (!callArgsVisited) {
                MethodNode mn = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
                this.visitMethodCallArguments(receiver, argumentList, true, mn);
                if (mn != null) {
                    List<Expression> argExpressions = argumentList.getExpressions();
                    parameters = mn.getParameters();
                    for (int i = 0; i < argExpressions.size() && i < parameters.length; ++i) {
                        Expression arg = argExpressions.get(i);
                        ClassNode classNode = parameters[i].getType();
                        ClassNode aType = this.getType(arg);
                        if (!ClassHelper.CLOSURE_TYPE.equals(classNode) || !ClassHelper.CLOSURE_TYPE.equals(aType) || StaticTypeCheckingSupport.isAssignableTo(aType, classNode)) continue;
                        this.addNoMatchingMethodError(receiver, name, this.getArgumentTypes(argumentList), call);
                        call.removeNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
                    }
                }
            }
        }
        finally {
            this.typeCheckingContext.popEnclosingMethodCall();
            this.extension.afterMethodCall(call);
        }
    }

    private static ClassNode adjustWithTraits(MethodNode directMethodCallCandidate, ClassNode receiver, ClassNode[] args, ClassNode returnType) {
        ExtensionMethodNode emn;
        if (directMethodCallCandidate instanceof ExtensionMethodNode && "withTraits".equals((emn = (ExtensionMethodNode)directMethodCallCandidate).getName()) && "DefaultGroovyMethods".equals(emn.getExtensionMethodNode().getDeclaringClass().getNameWithoutPackage())) {
            LinkedList<ClassNode> nodes = new LinkedList<ClassNode>();
            Collections.addAll(nodes, receiver.getInterfaces());
            for (ClassNode arg : args) {
                if (StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(arg)) {
                    nodes.add(arg.getGenericsTypes()[0].getType());
                    continue;
                }
                nodes.add(arg);
            }
            return new WideningCategories.LowestUpperBoundClassNode(returnType.getName() + "Composed", ClassHelper.OBJECT_TYPE, nodes.toArray(new ClassNode[nodes.size()]));
        }
        return returnType;
    }

    private static void addArrayMethods(List<MethodNode> methods, ClassNode receiver, String name, ClassNode[] args) {
        if (args.length != 1) {
            return;
        }
        if (!receiver.isArray()) {
            return;
        }
        if (!WideningCategories.isIntCategory(ClassHelper.getUnwrapper(args[0]))) {
            return;
        }
        if ("getAt".equals(name)) {
            MethodNode node = new MethodNode(name, 1, receiver.getComponentType(), new Parameter[]{new Parameter(args[0], "arg")}, null, null);
            node.setDeclaringClass(receiver.redirect());
            methods.add(node);
        } else if ("setAt".equals(name)) {
            MethodNode node = new MethodNode(name, 1, ClassHelper.VOID_TYPE, new Parameter[]{new Parameter(args[0], "arg")}, null, null);
            node.setDeclaringClass(receiver.redirect());
            methods.add(node);
        }
    }

    protected ClassNode getInferredReturnTypeFromWithClosureArgument(Expression callArguments) {
        if (!(callArguments instanceof ArgumentListExpression)) {
            return null;
        }
        ArgumentListExpression argList = (ArgumentListExpression)callArguments;
        ClosureExpression closure = (ClosureExpression)argList.getExpression(0);
        this.visitClosureExpression(closure);
        if (this.getInferredReturnType(closure) != null) {
            return this.getInferredReturnType(closure);
        }
        return null;
    }

    protected List<Receiver<String>> makeOwnerList(Expression objectExpression) {
        List<ClassNode> potentialReceiverType;
        ClassNode receiver = this.getType(objectExpression);
        LinkedList<Receiver<String>> owners = new LinkedList<Receiver<String>>();
        owners.add(Receiver.make(receiver));
        if (StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(receiver)) {
            GenericsType clazzGT = receiver.getGenericsTypes()[0];
            owners.add(0, Receiver.make(clazzGT.getType()));
        }
        if (receiver.isInterface()) {
            owners.add(Receiver.make(ClassHelper.OBJECT_TYPE));
        }
        StaticTypeCheckingVisitor.addSelfTypes(receiver, owners);
        if (!this.typeCheckingContext.temporaryIfBranchTypeInformation.empty() && (potentialReceiverType = this.getTemporaryTypesForExpression(objectExpression)) != null) {
            for (ClassNode node : potentialReceiverType) {
                owners.add(Receiver.make(node));
            }
        }
        if (this.typeCheckingContext.lastImplicitItType != null && objectExpression instanceof VariableExpression && ((VariableExpression)objectExpression).getName().equals("it")) {
            owners.add(Receiver.make(this.typeCheckingContext.lastImplicitItType));
        }
        return owners;
    }

    private static void addSelfTypes(ClassNode receiver, List<Receiver<String>> owners) {
        LinkedHashSet<ClassNode> selfTypes = new LinkedHashSet<ClassNode>();
        for (ClassNode selfType : Traits.collectSelfTypes(receiver, selfTypes)) {
            owners.add(Receiver.make(selfType));
        }
    }

    protected void checkForbiddenSpreadArgument(ArgumentListExpression argumentList) {
        for (Expression arg : argumentList.getExpressions()) {
            if (!(arg instanceof SpreadExpression)) continue;
            this.addStaticTypeError("The spread operator cannot be used as argument of method or closure calls with static type checking because the number of arguments cannot be determined at compile time", arg);
        }
    }

    protected List<ClassNode> getTemporaryTypesForExpression(Expression objectExpression) {
        List classNodes = null;
        int depth = this.typeCheckingContext.temporaryIfBranchTypeInformation.size();
        while (classNodes == null && depth > 0) {
            Map tempo = (Map)this.typeCheckingContext.temporaryIfBranchTypeInformation.get(--depth);
            Object key = objectExpression instanceof ParameterVariableExpression ? ((ParameterVariableExpression)objectExpression).parameter : this.extractTemporaryTypeInfoKey(objectExpression);
            classNodes = (List)tempo.get(key);
        }
        return classNodes;
    }

    protected void storeTargetMethod(Expression call, MethodNode directMethodCallCandidate) {
        call.putNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, directMethodCallCandidate);
        this.checkOrMarkPrivateAccess(call, directMethodCallCandidate);
        this.checkSuperCallFromClosure(call, directMethodCallCandidate);
        this.extension.onMethodSelection(call, directMethodCallCandidate);
    }

    protected boolean isClosureCall(String name, Expression objectExpression, Expression arguments) {
        if (objectExpression instanceof ClosureExpression && ("call".equals(name) || "doCall".equals(name))) {
            return true;
        }
        if (objectExpression == VariableExpression.THIS_EXPRESSION) {
            ClassNode type;
            FieldNode fieldNode = this.typeCheckingContext.getEnclosingClassNode().getDeclaredField(name);
            if (fieldNode != null && ClassHelper.CLOSURE_TYPE.equals(type = fieldNode.getType()) && !this.typeCheckingContext.getEnclosingClassNode().hasPossibleMethod(name, arguments)) {
                return true;
            }
        } else if (!"call".equals(name) && !"doCall".equals(name)) {
            return false;
        }
        return this.getType(objectExpression).equals(ClassHelper.CLOSURE_TYPE);
    }

    protected void typeCheckClosureCall(Expression callArguments, ClassNode[] args, Parameter[] parameters) {
        if (StaticTypeCheckingSupport.allParametersAndArgumentsMatch(parameters, args) < 0 && StaticTypeCheckingSupport.lastArgMatchesVarg(parameters, args) < 0) {
            StringBuilder sb = new StringBuilder("[");
            int parametersLength = parameters.length;
            for (int i = 0; i < parametersLength; ++i) {
                Parameter parameter = parameters[i];
                sb.append(parameter.getType().getName());
                if (i >= parametersLength - 1) continue;
                sb.append(", ");
            }
            sb.append("]");
            this.addStaticTypeError("Closure argument types: " + sb + " do not match with parameter types: " + StaticTypeCheckingVisitor.formatArgumentList(args), callArguments);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitIfElse(IfStatement ifElse) {
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        try {
            this.typeCheckingContext.pushTemporaryTypeInfo();
            this.visitStatement(ifElse);
            ifElse.getBooleanExpression().visit(this);
            ifElse.getIfBlock().visit(this);
            this.typeCheckingContext.popTemporaryTypeInfo();
            this.restoreTypeBeforeConditional();
            Statement elseBlock = ifElse.getElseBlock();
            if (elseBlock instanceof EmptyStatement) {
                this.visitEmptyStatement((EmptyStatement)elseBlock);
            } else {
                elseBlock.visit(this);
            }
        }
        finally {
            this.popAssignmentTracking(oldTracker);
        }
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        try {
            super.visitSwitch(statement);
        }
        finally {
            this.popAssignmentTracking(oldTracker);
        }
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement);
        this.restoreTypeBeforeConditional();
    }

    private void restoreTypeBeforeConditional() {
        Set<Map.Entry<VariableExpression, List<ClassNode>>> entries = this.typeCheckingContext.ifElseForWhileAssignmentTracker.entrySet();
        for (Map.Entry<VariableExpression, List<ClassNode>> entry : entries) {
            VariableExpression var = entry.getKey();
            List<ClassNode> items = entry.getValue();
            ClassNode originValue = items.get(0);
            this.storeType(var, originValue);
        }
    }

    protected Map<VariableExpression, ClassNode> popAssignmentTracking(Map<VariableExpression, List<ClassNode>> oldTracker) {
        HashMap<VariableExpression, ClassNode> assignments = new HashMap<VariableExpression, ClassNode>();
        if (!this.typeCheckingContext.ifElseForWhileAssignmentTracker.isEmpty()) {
            for (Map.Entry<VariableExpression, List<ClassNode>> entry : this.typeCheckingContext.ifElseForWhileAssignmentTracker.entrySet()) {
                VariableExpression key = entry.getKey();
                List<ClassNode> allValues = entry.getValue();
                ArrayList<ClassNode> nonNullValues = new ArrayList<ClassNode>(allValues.size());
                for (ClassNode value : allValues) {
                    if (value == null) continue;
                    nonNullValues.add(value);
                }
                ClassNode cn = WideningCategories.lowestUpperBound(nonNullValues);
                this.storeType(key, cn);
                assignments.put(key, cn);
            }
        }
        this.typeCheckingContext.ifElseForWhileAssignmentTracker = oldTracker;
        return assignments;
    }

    protected Map<VariableExpression, List<ClassNode>> pushAssignmentTracking() {
        Map<VariableExpression, List<ClassNode>> oldTracker = this.typeCheckingContext.ifElseForWhileAssignmentTracker;
        this.typeCheckingContext.ifElseForWhileAssignmentTracker = new HashMap<VariableExpression, List<ClassNode>>();
        return oldTracker;
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
        if (!expression.isCoerce()) {
            ClassNode targetType = expression.getType();
            Expression source = expression.getExpression();
            ClassNode expressionType = this.getType(source);
            if (!this.checkCast(targetType, source) && !this.isDelegateOrOwnerInClosure(source)) {
                this.addStaticTypeError("Inconvertible types: cannot cast " + expressionType.toString(false) + " to " + targetType.toString(false), expression);
            }
        }
        this.storeType(expression, expression.getType());
    }

    private boolean isDelegateOrOwnerInClosure(Expression exp) {
        return this.typeCheckingContext.getEnclosingClosure() != null && exp instanceof VariableExpression && ("delegate".equals(((VariableExpression)exp).getName()) || "owner".equals(((VariableExpression)exp).getName()));
    }

    protected boolean checkCast(ClassNode targetType, Expression source) {
        boolean sourceIsNull = StaticTypeCheckingVisitor.isNullConstant(source);
        ClassNode expressionType = this.getType(source);
        if (targetType.isArray() && expressionType.isArray()) {
            return this.checkCast(targetType.getComponentType(), GeneralUtils.varX("foo", expressionType.getComponentType()));
        }
        if (!(targetType.equals(ClassHelper.char_TYPE) && expressionType == ClassHelper.STRING_TYPE && source instanceof ConstantExpression && source.getText().length() == 1 || targetType.equals(ClassHelper.Character_TYPE) && (expressionType == ClassHelper.STRING_TYPE || sourceIsNull) && (sourceIsNull || source instanceof ConstantExpression && source.getText().length() == 1) || WideningCategories.isNumberCategory(ClassHelper.getWrapper(targetType)) && (WideningCategories.isNumberCategory(ClassHelper.getWrapper(expressionType)) || ClassHelper.char_TYPE == expressionType) || sourceIsNull && !ClassHelper.isPrimitiveType(targetType) || ClassHelper.char_TYPE == targetType && ClassHelper.isPrimitiveType(expressionType) && ClassHelper.isNumberType(expressionType))) {
            if (sourceIsNull && ClassHelper.isPrimitiveType(targetType) && !ClassHelper.boolean_TYPE.equals(targetType)) {
                return false;
            }
            if ((expressionType.getModifiers() & 0x10) == 0 && targetType.isInterface()) {
                return true;
            }
            if ((targetType.getModifiers() & 0x10) == 0 && expressionType.isInterface()) {
                return true;
            }
            if (!StaticTypeCheckingSupport.isAssignableTo(targetType, expressionType) && !StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(expressionType, targetType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        BinaryExpression enclosingBinaryExpression;
        Map<VariableExpression, List<ClassNode>> oldTracker = this.pushAssignmentTracking();
        this.typeCheckingContext.pushTemporaryTypeInfo();
        expression.getBooleanExpression().visit(this);
        Expression trueExpression = expression.getTrueExpression();
        Expression falseExpression = expression.getFalseExpression();
        trueExpression.visit(this);
        this.typeCheckingContext.popTemporaryTypeInfo();
        falseExpression.visit(this);
        ClassNode typeOfFalse = this.getType(falseExpression);
        ClassNode typeOfTrue = this.getType(trueExpression);
        if (StaticTypeCheckingVisitor.hasInferredReturnType(falseExpression)) {
            typeOfFalse = (ClassNode)falseExpression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
        }
        if (StaticTypeCheckingVisitor.hasInferredReturnType(trueExpression)) {
            typeOfTrue = (ClassNode)trueExpression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
        }
        typeOfFalse = this.checkForTargetType(falseExpression, typeOfFalse);
        typeOfTrue = this.checkForTargetType(trueExpression, typeOfTrue);
        ClassNode resultType = StaticTypeCheckingVisitor.isNullConstant(trueExpression) || StaticTypeCheckingVisitor.isNullConstant(falseExpression) ? ((enclosingBinaryExpression = this.typeCheckingContext.getEnclosingBinaryExpression()) != null && enclosingBinaryExpression.getRightExpression() == expression ? this.getType(enclosingBinaryExpression.getLeftExpression()) : (StaticTypeCheckingVisitor.isNullConstant(trueExpression) && StaticTypeCheckingVisitor.isNullConstant(falseExpression) ? ClassHelper.OBJECT_TYPE : (StaticTypeCheckingVisitor.isNullConstant(trueExpression) ? StaticTypeCheckingVisitor.wrapTypeIfNecessary(typeOfFalse) : StaticTypeCheckingVisitor.wrapTypeIfNecessary(typeOfTrue)))) : WideningCategories.lowestUpperBound(typeOfTrue, typeOfFalse);
        this.storeType(expression, resultType);
        this.popAssignmentTracking(oldTracker);
    }

    private ClassNode checkForTargetType(Expression expr, ClassNode type) {
        BinaryExpression enclosingBinaryExpression = this.typeCheckingContext.getEnclosingBinaryExpression();
        if (enclosingBinaryExpression != null && enclosingBinaryExpression instanceof DeclarationExpression && StaticTypeCheckingVisitor.isEmptyCollection(expr) && StaticTypeCheckingSupport.isAssignment(enclosingBinaryExpression.getOperation().getType())) {
            VariableExpression target = (VariableExpression)enclosingBinaryExpression.getLeftExpression();
            return StaticTypeCheckingVisitor.adjustForTargetType(target.getType(), type);
        }
        if (this.currentField != null) {
            return StaticTypeCheckingVisitor.adjustForTargetType(this.currentField.getType(), type);
        }
        if (this.currentProperty != null) {
            return StaticTypeCheckingVisitor.adjustForTargetType(this.currentProperty.getType(), type);
        }
        return type;
    }

    private static ClassNode adjustForTargetType(ClassNode targetType, ClassNode resultType) {
        if (targetType.isUsingGenerics() && StaticTypeCheckingSupport.missesGenericsTypes(resultType)) {
            return GenericsUtils.parameterizeType(targetType, resultType.getPlainNodeReference());
        }
        return resultType;
    }

    private static boolean isEmptyCollection(Expression expr) {
        return expr instanceof ListExpression && ((ListExpression)expr).getExpressions().size() == 0 || expr instanceof MapExpression && ((MapExpression)expr).getMapEntryExpressions().size() == 0;
    }

    private static boolean hasInferredReturnType(Expression expression) {
        ClassNode type = (ClassNode)expression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
        return type != null && !type.getName().equals("java.lang.Object");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        List<CatchStatement> catchStatements = statement.getCatchStatements();
        for (CatchStatement catchStatement : catchStatements) {
            ClassNode exceptionType = catchStatement.getExceptionType();
            this.typeCheckingContext.controlStructureVariables.put(catchStatement.getVariable(), exceptionType);
        }
        try {
            super.visitTryCatchFinally(statement);
        }
        finally {
            for (CatchStatement catchStatement : catchStatements) {
                this.typeCheckingContext.controlStructureVariables.remove(catchStatement.getVariable());
            }
        }
    }

    protected void storeType(Expression exp, ClassNode cn) {
        if (exp instanceof VariableExpression && ((VariableExpression)exp).isClosureSharedVariable() && ClassHelper.isPrimitiveType(cn)) {
            cn = ClassHelper.getWrapper(cn);
        } else if (exp instanceof MethodCallExpression && ((MethodCallExpression)exp).isSafe() && ClassHelper.isPrimitiveType(cn)) {
            cn = ClassHelper.getWrapper(cn);
        } else if (exp instanceof PropertyExpression && ((PropertyExpression)exp).isSafe() && ClassHelper.isPrimitiveType(cn)) {
            cn = ClassHelper.getWrapper(cn);
        }
        if (cn == StaticTypeCheckingSupport.UNKNOWN_PARAMETER_TYPE) {
            this.storeType(exp, this.getOriginalDeclarationType(exp));
            return;
        }
        ClassNode oldValue = (ClassNode)exp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, cn);
        if (oldValue != null) {
            ClassNode oldDIT = (ClassNode)exp.getNodeMetaData((Object)StaticTypesMarker.DECLARATION_INFERRED_TYPE);
            if (oldDIT != null) {
                exp.putNodeMetaData((Object)StaticTypesMarker.DECLARATION_INFERRED_TYPE, cn == null ? oldDIT : WideningCategories.lowestUpperBound(oldDIT, cn));
            } else {
                exp.putNodeMetaData((Object)StaticTypesMarker.DECLARATION_INFERRED_TYPE, cn == null ? null : WideningCategories.lowestUpperBound(oldValue, cn));
            }
        }
        if (exp instanceof VariableExpression) {
            List<ClassNode> temporaryTypesForExpression;
            VariableExpression var = (VariableExpression)exp;
            Variable accessedVariable = var.getAccessedVariable();
            if (accessedVariable != null && accessedVariable != exp && accessedVariable instanceof VariableExpression) {
                this.storeType((Expression)((Object)accessedVariable), cn);
            }
            if (accessedVariable instanceof Parameter) {
                ((Parameter)accessedVariable).putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, cn);
            }
            if (var.isClosureSharedVariable() && cn != null) {
                List<ClassNode> assignedTypes = this.typeCheckingContext.closureSharedVariablesAssignmentTypes.get(var);
                if (assignedTypes == null) {
                    assignedTypes = new LinkedList<ClassNode>();
                    this.typeCheckingContext.closureSharedVariablesAssignmentTypes.put(var, assignedTypes);
                }
                assignedTypes.add(cn);
            }
            if (!this.typeCheckingContext.temporaryIfBranchTypeInformation.empty() && (temporaryTypesForExpression = this.getTemporaryTypesForExpression(exp)) != null && !temporaryTypesForExpression.isEmpty()) {
                temporaryTypesForExpression.clear();
            }
        }
    }

    protected ClassNode getResultType(ClassNode left, int op, ClassNode right, BinaryExpression expr) {
        MethodNode method;
        ClassNode leftRedirect = left.redirect();
        ClassNode rightRedirect = right.redirect();
        Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();
        if (op == 100 || op == 1100) {
            if (leftRedirect.isArray() && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(rightRedirect, StaticTypeCheckingSupport.Collection_TYPE)) {
                return leftRedirect;
            }
            if (leftRedirect.implementsInterface(StaticTypeCheckingSupport.Collection_TYPE) && rightRedirect.implementsInterface(StaticTypeCheckingSupport.Collection_TYPE)) {
                List<Expression> list;
                if (rightExpression instanceof ListExpression && (list = ((ListExpression)rightExpression).getExpressions()).isEmpty()) {
                    return left;
                }
                return right;
            }
            if (rightRedirect.implementsInterface(StaticTypeCheckingSupport.Collection_TYPE) && rightRedirect.isDerivedFrom(leftRedirect)) {
                return right;
            }
            if (rightRedirect.isDerivedFrom(ClassHelper.CLOSURE_TYPE) && ClassHelper.isSAMType(leftRedirect) && rightExpression instanceof ClosureExpression) {
                return this.inferSAMTypeGenericsInAssignment(left, ClassHelper.findSAM(left), right, (ClosureExpression)rightExpression);
            }
            if (leftExpression instanceof VariableExpression) {
                ClassNode initialType = this.getOriginalDeclarationType(leftExpression).redirect();
                if (ClassHelper.isPrimitiveType(right) && initialType.isDerivedFrom(ClassHelper.Number_TYPE)) {
                    return ClassHelper.getWrapper(right);
                }
                if (ClassHelper.isPrimitiveType(initialType) && rightRedirect.isDerivedFrom(ClassHelper.Number_TYPE)) {
                    return ClassHelper.getUnwrapper(right);
                }
                if (ClassHelper.STRING_TYPE.equals(initialType) || ClassHelper.CLASS_Type.equals(initialType) || ClassHelper.Boolean_TYPE.equals(initialType) || ClassHelper.boolean_TYPE.equals(initialType)) {
                    return initialType;
                }
            }
            return right;
        }
        if (StaticTypeCheckingSupport.isBoolIntrinsicOp(op)) {
            return ClassHelper.boolean_TYPE;
        }
        if (StaticTypeCheckingSupport.isArrayOp(op)) {
            BinaryExpression newExpr = GeneralUtils.binX(expr.getLeftExpression(), expr.getOperation(), rightExpression);
            newExpr.setSourcePosition(expr);
            MethodNode method2 = this.findMethodOrFail(newExpr, left.getPlainNodeReference(), "getAt", right.getPlainNodeReference());
            if (method2 != null && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(right, ClassHelper.RANGE_TYPE)) {
                return this.inferReturnTypeGenerics(left, method2, rightExpression);
            }
            return method2 != null ? this.inferComponentType(left, right) : null;
        }
        if (op == 90) {
            return StaticTypeCheckingSupport.Matcher_TYPE;
        }
        String operationName = StaticTypeCheckingSupport.getOperationName(op);
        ClassNode mathResultType = this.getMathResultType(op, leftRedirect, rightRedirect, operationName);
        if (mathResultType != null) {
            return mathResultType;
        }
        if (leftExpression instanceof ClassExpression) {
            left = ClassHelper.CLASS_Type.getPlainNodeReference();
        }
        if ((method = this.findMethodOrFail(expr, left, operationName, right)) != null) {
            this.storeTargetMethod(expr, method);
            this.typeCheckMethodsWithGenericsOrFail(left, new ClassNode[]{right}, method, expr);
            if (StaticTypeCheckingSupport.isAssignment(op)) {
                return left;
            }
            if (StaticTypeCheckingSupport.isCompareToBoolean(op)) {
                return ClassHelper.boolean_TYPE;
            }
            if (op == 128) {
                return ClassHelper.int_TYPE;
            }
            return this.inferReturnTypeGenerics(left, method, GeneralUtils.args(rightExpression));
        }
        return null;
    }

    private ClassNode getMathResultType(int op, ClassNode leftRedirect, ClassNode rightRedirect, String operationName) {
        if (ClassHelper.isNumberType(leftRedirect) && ClassHelper.isNumberType(rightRedirect)) {
            if (StaticTypeCheckingSupport.isOperationInGroup(op)) {
                if (WideningCategories.isIntCategory(leftRedirect) && WideningCategories.isIntCategory(rightRedirect)) {
                    return ClassHelper.int_TYPE;
                }
                if (WideningCategories.isLongCategory(leftRedirect) && WideningCategories.isLongCategory(rightRedirect)) {
                    return ClassHelper.long_TYPE;
                }
                if (WideningCategories.isFloat(leftRedirect) && WideningCategories.isFloat(rightRedirect)) {
                    return ClassHelper.float_TYPE;
                }
                if (WideningCategories.isDouble(leftRedirect) && WideningCategories.isDouble(rightRedirect)) {
                    return ClassHelper.double_TYPE;
                }
            } else {
                if (StaticTypeCheckingSupport.isPowerOperator(op)) {
                    return ClassHelper.Number_TYPE;
                }
                if (StaticTypeCheckingSupport.isBitOperator(op) || op == 204 || op == 214) {
                    if (WideningCategories.isIntCategory(ClassHelper.getUnwrapper(leftRedirect)) && WideningCategories.isIntCategory(ClassHelper.getUnwrapper(rightRedirect))) {
                        return ClassHelper.int_TYPE;
                    }
                    if (WideningCategories.isLongCategory(ClassHelper.getUnwrapper(leftRedirect)) && WideningCategories.isLongCategory(ClassHelper.getUnwrapper(rightRedirect))) {
                        return ClassHelper.long_TYPE;
                    }
                    if (WideningCategories.isBigIntCategory(ClassHelper.getUnwrapper(leftRedirect)) && WideningCategories.isBigIntCategory(ClassHelper.getUnwrapper(rightRedirect))) {
                        return ClassHelper.BigInteger_TYPE;
                    }
                } else if (StaticTypeCheckingSupport.isCompareToBoolean(op) || op == 123 || op == 120) {
                    return ClassHelper.boolean_TYPE;
                }
            }
        } else if (ClassHelper.char_TYPE.equals(leftRedirect) && ClassHelper.char_TYPE.equals(rightRedirect) && (StaticTypeCheckingSupport.isCompareToBoolean(op) || op == 123 || op == 120)) {
            return ClassHelper.boolean_TYPE;
        }
        if (StaticTypeCheckingSupport.isShiftOperation(operationName) && WideningCategories.isNumberCategory(leftRedirect) && (WideningCategories.isIntCategory(rightRedirect) || WideningCategories.isLongCategory(rightRedirect))) {
            return leftRedirect;
        }
        if (WideningCategories.isNumberCategory(ClassHelper.getWrapper(rightRedirect)) && WideningCategories.isNumberCategory(ClassHelper.getWrapper(leftRedirect)) && (203 == op || 213 == op)) {
            if (WideningCategories.isFloatingCategory(leftRedirect) || WideningCategories.isFloatingCategory(rightRedirect)) {
                if (!ClassHelper.isPrimitiveType(leftRedirect) || !ClassHelper.isPrimitiveType(rightRedirect)) {
                    return ClassHelper.Double_TYPE;
                }
                return ClassHelper.double_TYPE;
            }
            if (203 == op) {
                return ClassHelper.BigDecimal_TYPE;
            }
            return leftRedirect;
        }
        if (StaticTypeCheckingSupport.isOperationInGroup(op) && WideningCategories.isNumberCategory(ClassHelper.getWrapper(leftRedirect)) && WideningCategories.isNumberCategory(ClassHelper.getWrapper(rightRedirect))) {
            return StaticTypeCheckingVisitor.getGroupOperationResultType(leftRedirect, rightRedirect);
        }
        if (WideningCategories.isNumberCategory(ClassHelper.getWrapper(rightRedirect)) && WideningCategories.isNumberCategory(ClassHelper.getWrapper(leftRedirect)) && (205 == op || 215 == op)) {
            return leftRedirect;
        }
        return null;
    }

    private ClassNode inferSAMTypeGenericsInAssignment(ClassNode samUsage, MethodNode sam, ClassNode closureType, ClosureExpression closureExpression) {
        GenericsType[] samGt = samUsage.getGenericsTypes();
        GenericsType[] closureGt = closureType.getGenericsTypes();
        if (samGt == null || closureGt == null) {
            return samUsage;
        }
        HashMap<String, GenericsType> connections = new HashMap<String, GenericsType>();
        StaticTypeCheckingSupport.extractGenericsConnections(connections, this.getInferredReturnType(closureExpression), sam.getReturnType());
        Parameter[] closureParams = closureExpression.getParameters();
        Parameter[] methodParams = sam.getParameters();
        for (int i = 0; i < closureParams.length; ++i) {
            ClassNode fromClosure = closureParams[i].getType();
            ClassNode fromMethod = methodParams[i].getType();
            StaticTypeCheckingSupport.extractGenericsConnections(connections, fromClosure, fromMethod);
        }
        ClassNode result = StaticTypeCheckingSupport.applyGenericsContext(connections, samUsage.redirect());
        return result;
    }

    protected static ClassNode getGroupOperationResultType(ClassNode a, ClassNode b) {
        if (WideningCategories.isBigIntCategory(a) && WideningCategories.isBigIntCategory(b)) {
            return ClassHelper.BigInteger_TYPE;
        }
        if (WideningCategories.isBigDecCategory(a) && WideningCategories.isBigDecCategory(b)) {
            return ClassHelper.BigDecimal_TYPE;
        }
        if (ClassHelper.BigDecimal_TYPE.equals(a) || ClassHelper.BigDecimal_TYPE.equals(b)) {
            return ClassHelper.BigDecimal_TYPE;
        }
        if (ClassHelper.BigInteger_TYPE.equals(a) || ClassHelper.BigInteger_TYPE.equals(b)) {
            if (WideningCategories.isBigIntCategory(a) && WideningCategories.isBigIntCategory(b)) {
                return ClassHelper.BigInteger_TYPE;
            }
            return ClassHelper.BigDecimal_TYPE;
        }
        if (ClassHelper.double_TYPE.equals(a) || ClassHelper.double_TYPE.equals(b)) {
            return ClassHelper.double_TYPE;
        }
        if (ClassHelper.Double_TYPE.equals(a) || ClassHelper.Double_TYPE.equals(b)) {
            return ClassHelper.Double_TYPE;
        }
        if (ClassHelper.float_TYPE.equals(a) || ClassHelper.float_TYPE.equals(b)) {
            return ClassHelper.float_TYPE;
        }
        if (ClassHelper.Float_TYPE.equals(a) || ClassHelper.Float_TYPE.equals(b)) {
            return ClassHelper.Float_TYPE;
        }
        if (ClassHelper.long_TYPE.equals(a) || ClassHelper.long_TYPE.equals(b)) {
            return ClassHelper.long_TYPE;
        }
        if (ClassHelper.Long_TYPE.equals(a) || ClassHelper.Long_TYPE.equals(b)) {
            return ClassHelper.Long_TYPE;
        }
        if (ClassHelper.int_TYPE.equals(a) || ClassHelper.int_TYPE.equals(b)) {
            return ClassHelper.int_TYPE;
        }
        if (ClassHelper.Integer_TYPE.equals(a) || ClassHelper.Integer_TYPE.equals(b)) {
            return ClassHelper.Integer_TYPE;
        }
        if (ClassHelper.short_TYPE.equals(a) || ClassHelper.short_TYPE.equals(b)) {
            return ClassHelper.short_TYPE;
        }
        if (ClassHelper.Short_TYPE.equals(a) || ClassHelper.Short_TYPE.equals(b)) {
            return ClassHelper.Short_TYPE;
        }
        if (ClassHelper.byte_TYPE.equals(a) || ClassHelper.byte_TYPE.equals(b)) {
            return ClassHelper.byte_TYPE;
        }
        if (ClassHelper.Byte_TYPE.equals(a) || ClassHelper.Byte_TYPE.equals(b)) {
            return ClassHelper.Byte_TYPE;
        }
        if (ClassHelper.char_TYPE.equals(a) || ClassHelper.char_TYPE.equals(b)) {
            return ClassHelper.char_TYPE;
        }
        if (ClassHelper.Character_TYPE.equals(a) || ClassHelper.Character_TYPE.equals(b)) {
            return ClassHelper.Character_TYPE;
        }
        return ClassHelper.Number_TYPE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ClassNode inferComponentType(ClassNode containerType, ClassNode indexType) {
        ClassNode componentType = containerType.getComponentType();
        if (componentType == null) {
            this.typeCheckingContext.pushErrorCollector();
            MethodCallExpression vcall = GeneralUtils.callX((Expression)GeneralUtils.varX("_hash_", containerType), "getAt", (Expression)GeneralUtils.varX("_index_", indexType));
            try {
                this.visitMethodCallExpression(vcall);
            }
            finally {
                this.typeCheckingContext.popErrorCollector();
            }
            return this.getType(vcall);
        }
        return componentType;
    }

    protected MethodNode findMethodOrFail(Expression expr, ClassNode receiver, String name, ClassNode ... args) {
        List<MethodNode> methods = this.findMethod(receiver, name, args);
        if (methods.isEmpty() && expr instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression)expr;
            MethodCallExpression call = GeneralUtils.callX(be.getLeftExpression(), name, be.getRightExpression());
            methods = this.extension.handleMissingMethod(receiver, name, GeneralUtils.args(be.getLeftExpression()), args, call);
        }
        if (methods.isEmpty()) {
            this.addNoMatchingMethodError(receiver, name, args, expr);
        } else {
            if (this.areCategoryMethodCalls(methods, name, args)) {
                this.addCategoryMethodCallError(expr);
            }
            if ((methods = this.disambiguateMethods(methods, receiver, args, expr)).size() == 1) {
                return methods.get(0);
            }
            this.addAmbiguousErrorMessage(methods, name, args, expr);
        }
        return null;
    }

    private List<MethodNode> disambiguateMethods(List<MethodNode> methods, ClassNode receiver, ClassNode[] argTypes, Expression expr) {
        if (methods.size() > 1 && receiver != null && argTypes != null) {
            LinkedList<MethodNode> filteredWithGenerics = new LinkedList<MethodNode>();
            for (MethodNode methodNode : methods) {
                if (!StaticTypeCheckingSupport.typeCheckMethodsWithGenerics(receiver, argTypes, methodNode)) continue;
                filteredWithGenerics.add(methodNode);
            }
            if (filteredWithGenerics.size() == 1) {
                return filteredWithGenerics;
            }
            methods = this.extension.handleAmbiguousMethods(methods, expr);
        }
        return methods;
    }

    protected static String prettyPrintMethodList(List<MethodNode> nodes) {
        StringBuilder sb = new StringBuilder("[");
        int nodesSize = nodes.size();
        for (int i = 0; i < nodesSize; ++i) {
            MethodNode node = nodes.get(i);
            sb.append(node.getReturnType().toString(false));
            sb.append(" ");
            sb.append(node.getDeclaringClass().toString(false));
            sb.append("#");
            sb.append(StaticTypeCheckingSupport.toMethodParametersString(node.getName(), StaticTypeCheckingVisitor.extractTypesFromParameters(node.getParameters())));
            if (i >= nodesSize - 1) continue;
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    protected boolean areCategoryMethodCalls(List<MethodNode> foundMethods, String name, ClassNode[] args) {
        boolean category = false;
        if ("use".equals(name) && args != null && args.length == 2 && args[1].equals(ClassHelper.CLOSURE_TYPE)) {
            category = true;
            for (MethodNode method : foundMethods) {
                if (method instanceof ExtensionMethodNode && ((ExtensionMethodNode)method).getExtensionMethodNode().getDeclaringClass().equals(DGM_CLASSNODE)) continue;
                category = false;
                break;
            }
        }
        return category;
    }

    protected List<MethodNode> findMethodsWithGenerated(ClassNode receiver, String name) {
        List<MethodNode> methods = receiver.getMethods(name);
        if (methods.isEmpty() || receiver.isResolved()) {
            return methods;
        }
        List<MethodNode> result = StaticTypeCheckingVisitor.addGeneratedMethods(receiver, methods);
        return result;
    }

    private static List<MethodNode> addGeneratedMethods(ClassNode receiver, List<MethodNode> methods) {
        LinkedList<MethodNode> result = new LinkedList<MethodNode>();
        for (MethodNode method : methods) {
            result.add(method);
            Parameter[] parameters = method.getParameters();
            int counter = 0;
            int size = parameters.length;
            for (int i = size - 1; i >= 0; --i) {
                Parameter parameter = parameters[i];
                if (parameter == null || !parameter.hasInitialExpression()) continue;
                ++counter;
            }
            for (int j = 1; j <= counter; ++j) {
                MethodNode stubbed;
                Parameter[] newParams = new Parameter[parameters.length - j];
                int index = 0;
                int k = 1;
                for (Parameter parameter : parameters) {
                    if (k > counter - j && parameter != null && parameter.hasInitialExpression()) {
                        ++k;
                        continue;
                    }
                    if (parameter != null && parameter.hasInitialExpression()) {
                        newParams[index++] = parameter;
                        ++k;
                        continue;
                    }
                    newParams[index++] = parameter;
                }
                if ("<init>".equals(method.getName())) {
                    stubbed = new ConstructorNode(method.getModifiers(), newParams, method.getExceptions(), GENERATED_EMPTY_STATEMENT);
                } else {
                    stubbed = new MethodNode(method.getName(), method.getModifiers(), method.getReturnType(), newParams, method.getExceptions(), GENERATED_EMPTY_STATEMENT);
                    stubbed.setGenericsTypes(method.getGenericsTypes());
                }
                stubbed.setDeclaringClass(method.getDeclaringClass());
                result.add(stubbed);
            }
        }
        return result;
    }

    protected List<MethodNode> findMethod(ClassNode receiver, String name, ClassNode ... args) {
        List<MethodNode> result;
        MethodNode constructor;
        List<MethodNode> methods;
        if (ClassHelper.isPrimitiveType(receiver)) {
            receiver = ClassHelper.getWrapper(receiver);
        }
        if (!receiver.isInterface() && "<init>".equals(name)) {
            methods = StaticTypeCheckingVisitor.addGeneratedMethods(receiver, new ArrayList<MethodNode>(receiver.getDeclaredConstructors()));
            if (methods.isEmpty()) {
                ConstructorNode node = new ConstructorNode(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GENERATED_EMPTY_STATEMENT);
                node.setDeclaringClass(receiver);
                methods = Collections.singletonList(node);
                if (receiver.isArray()) {
                    return methods;
                }
            }
        } else {
            String pname;
            methods = this.findMethodsWithGenerated(receiver, name);
            if (receiver.isInterface()) {
                this.collectAllInterfaceMethodsByName(receiver, name, methods);
                methods.addAll(ClassHelper.OBJECT_TYPE.getMethods(name));
            }
            if (this.typeCheckingContext.getEnclosingClosure() == null) {
                ClassNode parent = receiver;
                while (parent instanceof InnerClassNode && !parent.isStaticClass()) {
                    parent = parent.getOuterClass();
                    methods.addAll(this.findMethodsWithGenerated(parent, name));
                }
            }
            if (methods.isEmpty()) {
                StaticTypeCheckingVisitor.addArrayMethods(methods, receiver, name, args);
            }
            if (methods.isEmpty() && (args == null || args.length == 0)) {
                pname = StaticTypeCheckingVisitor.extractPropertyNameFromMethodName("get", name);
                if (pname == null) {
                    pname = StaticTypeCheckingVisitor.extractPropertyNameFromMethodName("is", name);
                }
                if (pname != null) {
                    PropertyNode property = null;
                    block1: for (ClassNode curNode = receiver; property == null && curNode != null; curNode = curNode.getSuperClass()) {
                        property = curNode.getProperty(pname);
                        ClassNode svCur = curNode;
                        while (property == null && svCur instanceof InnerClassNode && !svCur.isStaticClass()) {
                            property = (svCur = svCur.getOuterClass()).getProperty(pname);
                            if (property == null) continue;
                            receiver = svCur;
                            continue block1;
                        }
                    }
                    if (property != null) {
                        MethodNode node = new MethodNode(name, 1, property.getType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GENERATED_EMPTY_STATEMENT);
                        if (property.isStatic()) {
                            node.setModifiers(9);
                        }
                        node.setDeclaringClass(receiver);
                        return Collections.singletonList(node);
                    }
                }
            } else if (methods.isEmpty() && args != null && args.length == 1 && (pname = StaticTypeCheckingVisitor.extractPropertyNameFromMethodName("set", name)) != null) {
                PropertyNode property = null;
                for (ClassNode curNode = receiver; property == null && curNode != null; curNode = curNode.getSuperClass()) {
                    property = curNode.getProperty(pname);
                }
                if (property != null) {
                    ClassNode type = property.getOriginType();
                    if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(StaticTypeCheckingVisitor.wrapTypeIfNecessary(args[0]), StaticTypeCheckingVisitor.wrapTypeIfNecessary(type))) {
                        MethodNode node = new MethodNode(name, 1, ClassHelper.VOID_TYPE, new Parameter[]{new Parameter(type, "arg")}, ClassNode.EMPTY_ARRAY, GENERATED_EMPTY_STATEMENT);
                        if (property.isStatic()) {
                            node.setModifiers(9);
                        }
                        node.setDeclaringClass(receiver);
                        return Collections.singletonList(node);
                    }
                }
            }
        }
        if (methods.isEmpty()) {
            this.collectAllInterfaceMethodsByName(receiver, name, methods);
        }
        StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.getTransformLoader(), receiver, name, args, methods);
        List<MethodNode> chosen = StaticTypeCheckingSupport.chooseBestMethod(receiver, methods, args);
        if (!chosen.isEmpty()) {
            return chosen;
        }
        if (receiver instanceof InnerClassNode && ((InnerClassNode)receiver).isAnonymous() && methods.size() == 1 && args != null && "<init>".equals(name) && (constructor = methods.get(0)).getParameters().length == args.length) {
            return methods;
        }
        if (receiver.equals(ClassHelper.CLASS_Type) && receiver.getGenericsTypes() != null && !(result = this.findMethod(receiver.getGenericsTypes()[0].getType(), name, args)).isEmpty()) {
            return result;
        }
        if (ClassHelper.GSTRING_TYPE.equals(receiver)) {
            return this.findMethod(ClassHelper.STRING_TYPE, name, args);
        }
        if (StaticTypeCheckingSupport.isBeingCompiled(receiver) && !(chosen = this.findMethod(ClassHelper.GROOVY_OBJECT_TYPE, name, args)).isEmpty()) {
            return chosen;
        }
        return EMPTY_METHODNODE_LIST;
    }

    public static String extractPropertyNameFromMethodName(String prefix, String methodName) {
        String propertyName;
        String result;
        if (prefix == null || methodName == null) {
            return null;
        }
        if (methodName.startsWith(prefix) && prefix.length() < methodName.length() && (result = methodName.substring(prefix.length())).equals(MetaClassHelper.capitalize(propertyName = Introspector.decapitalize(result)))) {
            return propertyName;
        }
        return null;
    }

    protected void collectAllInterfaceMethodsByName(ClassNode receiver, String name, List<MethodNode> methods) {
        for (ClassNode cNode = receiver; cNode != null; cNode = cNode.getSuperClass()) {
            ClassNode[] interfaces = cNode.getInterfaces();
            if (interfaces == null || interfaces.length <= 0) continue;
            for (ClassNode node : interfaces) {
                List<MethodNode> intfMethods = node.getMethods(name);
                methods.addAll(intfMethods);
                this.collectAllInterfaceMethodsByName(node, name, methods);
            }
        }
    }

    protected ClassNode getType(ASTNode exp) {
        MethodNode target;
        ClassNode cn = (ClassNode)exp.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        if (cn != null) {
            return cn;
        }
        if (exp instanceof ClassExpression) {
            ClassNode node = ClassHelper.CLASS_Type.getPlainNodeReference();
            node.setGenericsTypes(new GenericsType[]{new GenericsType(((ClassExpression)exp).getType())});
            return node;
        }
        if (exp instanceof VariableExpression) {
            VariableExpression vexp = (VariableExpression)exp;
            ClassNode selfTrait = StaticTypeCheckingSupport.isTraitSelf(vexp);
            if (selfTrait != null) {
                return StaticTypeCheckingVisitor.makeSelf(selfTrait);
            }
            if (vexp == VariableExpression.THIS_EXPRESSION) {
                return this.makeThis();
            }
            if (vexp == VariableExpression.SUPER_EXPRESSION) {
                return this.makeSuper();
            }
            Variable variable = vexp.getAccessedVariable();
            if (variable instanceof FieldNode) {
                this.checkOrMarkPrivateAccess(vexp, (FieldNode)variable, this.isLHSOfEnclosingAssignment(vexp));
                return this.getType((FieldNode)variable);
            }
            if (variable != null && variable != vexp && variable instanceof VariableExpression) {
                return this.getType((Expression)((Object)variable));
            }
            if (variable instanceof Parameter) {
                Parameter parameter = (Parameter)variable;
                ClassNode type = null;
                List<ClassNode> temporaryTypesForExpression = this.getTemporaryTypesForExpression(vexp);
                if (temporaryTypesForExpression == null || temporaryTypesForExpression.isEmpty()) {
                    type = this.typeCheckingContext.controlStructureVariables.get(parameter);
                }
                TypeCheckingContext.EnclosingClosure enclosingClosure = this.typeCheckingContext.getEnclosingClosure();
                if (type == null && enclosingClosure != null && temporaryTypesForExpression == null) {
                    type = this.getTypeFromClosureArguments(parameter, enclosingClosure);
                }
                if (type != null) {
                    this.storeType(vexp, type);
                    return type;
                }
                return this.getType((Parameter)variable);
            }
            return vexp.getOriginType();
        }
        if (exp instanceof ListExpression) {
            return this.inferListExpressionType((ListExpression)exp);
        }
        if (exp instanceof MapExpression) {
            return this.inferMapExpressionType((MapExpression)exp);
        }
        if (exp instanceof ConstructorCallExpression) {
            return ((ConstructorCallExpression)exp).getType();
        }
        if (exp instanceof MethodNode) {
            if ((exp == GET_DELEGATE || exp == GET_OWNER || exp == GET_THISOBJECT) && this.typeCheckingContext.getEnclosingClosure() != null) {
                return this.typeCheckingContext.getEnclosingClassNode();
            }
            ClassNode ret = this.getInferredReturnType(exp);
            return ret != null ? ret : ((MethodNode)exp).getReturnType();
        }
        if (exp instanceof RangeExpression) {
            ClassNode toType;
            ClassNode plain = ClassHelper.RANGE_TYPE.getPlainNodeReference();
            RangeExpression re = (RangeExpression)exp;
            ClassNode fromType = this.getType(re.getFrom());
            if (fromType.equals(toType = this.getType(re.getTo()))) {
                plain.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(fromType))});
            } else {
                plain.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(WideningCategories.lowestUpperBound(fromType, toType)))});
            }
            return plain;
        }
        if (exp instanceof UnaryPlusExpression) {
            return this.getType(((UnaryPlusExpression)exp).getExpression());
        }
        if (exp instanceof UnaryMinusExpression) {
            return this.getType(((UnaryMinusExpression)exp).getExpression());
        }
        if (exp instanceof BitwiseNegationExpression) {
            return this.getType(((BitwiseNegationExpression)exp).getExpression());
        }
        if (exp instanceof Parameter) {
            return ((Parameter)exp).getOriginType();
        }
        if (exp instanceof FieldNode) {
            FieldNode fn = (FieldNode)exp;
            return this.getGenericsResolvedTypeOfFieldOrProperty(fn, fn.getOriginType());
        }
        if (exp instanceof PropertyNode) {
            PropertyNode pn = (PropertyNode)exp;
            return this.getGenericsResolvedTypeOfFieldOrProperty(pn, pn.getOriginType());
        }
        if (exp instanceof ClosureExpression) {
            ClassNode irt = this.getInferredReturnType(exp);
            if (irt != null) {
                irt = StaticTypeCheckingVisitor.wrapTypeIfNecessary(irt);
                ClassNode result = ClassHelper.CLOSURE_TYPE.getPlainNodeReference();
                result.setGenericsTypes(new GenericsType[]{new GenericsType(irt)});
                return result;
            }
        } else if (exp instanceof MethodCall && (target = (MethodNode)exp.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET)) != null) {
            return this.getType(target);
        }
        return ((Expression)exp).getType();
    }

    private ClassNode getTypeFromClosureArguments(Parameter parameter, TypeCheckingContext.EnclosingClosure enclosingClosure) {
        ClosureExpression closureExpression = enclosingClosure.getClosureExpression();
        ClassNode[] closureParamTypes = (ClassNode[])closureExpression.getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS);
        if (closureParamTypes == null) {
            return null;
        }
        Parameter[] parameters = closureExpression.getParameters();
        String name = parameter.getName();
        if (parameters.length == 0) {
            return "it".equals(name) && closureParamTypes.length != 0 ? closureParamTypes[0] : null;
        }
        for (int index = 0; index < parameters.length; ++index) {
            if (!name.equals(parameters[index].getName())) continue;
            return closureParamTypes.length > index ? closureParamTypes[index] : null;
        }
        return null;
    }

    private ClassNode getGenericsResolvedTypeOfFieldOrProperty(AnnotatedNode an, ClassNode type) {
        if (!type.isUsingGenerics()) {
            return type;
        }
        HashMap<String, GenericsType> connections = new HashMap<String, GenericsType>();
        StaticTypeCheckingSupport.extractGenericsConnections(connections, this.typeCheckingContext.getEnclosingClassNode(), an.getDeclaringClass());
        type = StaticTypeCheckingSupport.applyGenericsContext(connections, type);
        return type;
    }

    private ClassNode makeSuper() {
        ClassNode ret = this.typeCheckingContext.getEnclosingClassNode().getSuperClass();
        if (this.typeCheckingContext.isInStaticContext) {
            ClassNode staticRet = ClassHelper.CLASS_Type.getPlainNodeReference();
            GenericsType gt = new GenericsType(ret);
            staticRet.setGenericsTypes(new GenericsType[]{gt});
            ret = staticRet;
        }
        return ret;
    }

    private ClassNode makeThis() {
        ClassNode ret = this.typeCheckingContext.getEnclosingClassNode();
        if (this.typeCheckingContext.isInStaticContext) {
            ClassNode staticRet = ClassHelper.CLASS_Type.getPlainNodeReference();
            GenericsType gt = new GenericsType(ret);
            staticRet.setGenericsTypes(new GenericsType[]{gt});
            ret = staticRet;
        }
        return ret;
    }

    private static ClassNode makeSelf(ClassNode trait) {
        ClassNode ret = trait;
        LinkedHashSet<ClassNode> selfTypes = new LinkedHashSet<ClassNode>();
        Traits.collectSelfTypes(ret, selfTypes);
        if (!selfTypes.isEmpty()) {
            selfTypes.add(ret);
            ret = new UnionTypeClassNode(selfTypes.toArray(new ClassNode[selfTypes.size()]));
        }
        return ret;
    }

    protected ClassNode storeInferredReturnType(ASTNode node, ClassNode type) {
        if (!(node instanceof ClosureExpression)) {
            throw new IllegalArgumentException("Storing inferred return type is only allowed on closures but found " + node.getClass());
        }
        return (ClassNode)node.putNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE, type);
    }

    protected ClassNode getInferredReturnType(ASTNode exp) {
        return (ClassNode)exp.getNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE);
    }

    protected ClassNode inferListExpressionType(ListExpression list) {
        List<Expression> expressions = list.getExpressions();
        if (expressions.isEmpty()) {
            return list.getType();
        }
        ClassNode listType = list.getType();
        GenericsType[] genericsTypes = listType.getGenericsTypes();
        if ((genericsTypes == null || genericsTypes.length == 0 || genericsTypes.length == 1 && ClassHelper.OBJECT_TYPE.equals(genericsTypes[0].getType())) && !expressions.isEmpty()) {
            LinkedList<ClassNode> nodes = new LinkedList<ClassNode>();
            for (Expression expression : expressions) {
                if (StaticTypeCheckingVisitor.isNullConstant(expression)) continue;
                nodes.add(this.getType(expression));
            }
            if (nodes.isEmpty()) {
                return listType;
            }
            ClassNode superType = ClassHelper.getWrapper(WideningCategories.lowestUpperBound(nodes));
            ClassNode inferred = listType.getPlainNodeReference();
            inferred.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(superType))});
            return inferred;
        }
        return listType;
    }

    protected static boolean isNullConstant(Expression expression) {
        return expression instanceof ConstantExpression && ((ConstantExpression)expression).isNullExpression();
    }

    protected ClassNode inferMapExpressionType(MapExpression map) {
        ClassNode mapType = LINKEDHASHMAP_CLASSNODE.getPlainNodeReference();
        List<MapEntryExpression> entryExpressions = map.getMapEntryExpressions();
        if (entryExpressions.isEmpty()) {
            return mapType;
        }
        GenericsType[] genericsTypes = mapType.getGenericsTypes();
        if (genericsTypes == null || genericsTypes.length < 2 || genericsTypes.length == 2 && ClassHelper.OBJECT_TYPE.equals(genericsTypes[0].getType()) && ClassHelper.OBJECT_TYPE.equals(genericsTypes[1].getType())) {
            LinkedList<ClassNode> keyTypes = new LinkedList<ClassNode>();
            LinkedList<ClassNode> valueTypes = new LinkedList<ClassNode>();
            for (MapEntryExpression entryExpression : entryExpressions) {
                keyTypes.add(this.getType(entryExpression.getKeyExpression()));
                valueTypes.add(this.getType(entryExpression.getValueExpression()));
            }
            ClassNode keyType = ClassHelper.getWrapper(WideningCategories.lowestUpperBound(keyTypes));
            ClassNode valueType = ClassHelper.getWrapper(WideningCategories.lowestUpperBound(valueTypes));
            if (!ClassHelper.OBJECT_TYPE.equals(keyType) || !ClassHelper.OBJECT_TYPE.equals(valueType)) {
                ClassNode inferred = mapType.getPlainNodeReference();
                inferred.setGenericsTypes(new GenericsType[]{new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(keyType)), new GenericsType(StaticTypeCheckingVisitor.wrapTypeIfNecessary(valueType))});
                return inferred;
            }
        }
        return mapType;
    }

    protected ClassNode inferReturnTypeGenerics(ClassNode receiver, MethodNode method, Expression arguments) {
        return this.inferReturnTypeGenerics(receiver, method, arguments, null);
    }

    protected ClassNode inferReturnTypeGenerics(ClassNode receiver, MethodNode method, Expression arguments, GenericsType[] explicitTypeHints) {
        ClassNode returnType = method.getReturnType();
        if (method instanceof ExtensionMethodNode && StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(returnType)) {
            ExtensionMethodNode emn = (ExtensionMethodNode)method;
            MethodNode dgmMethod = emn.getExtensionMethodNode();
            ClassNode dc = emn.getDeclaringClass();
            ArgumentListExpression argList = new ArgumentListExpression();
            VariableExpression vexp = GeneralUtils.varX("$foo", receiver);
            vexp.setNodeMetaData(ExtensionMethodDeclaringClass.class, dc);
            argList.addExpression(vexp);
            if (arguments instanceof ArgumentListExpression) {
                List<Expression> expressions = ((ArgumentListExpression)arguments).getExpressions();
                for (Expression arg : expressions) {
                    argList.addExpression(arg);
                }
            } else {
                argList.addExpression(arguments);
            }
            return this.inferReturnTypeGenerics(receiver, dgmMethod, argList);
        }
        if (!StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(returnType)) {
            return returnType;
        }
        if (StaticTypeCheckingSupport.getGenericsWithoutArray(returnType) == null) {
            return returnType;
        }
        Map<String, GenericsType> resolvedPlaceholders = this.resolvePlaceHoldersFromDeclaration(receiver, StaticTypeCheckingVisitor.getDeclaringClass(method, arguments), method, method.isStatic());
        if (!receiver.isGenericsPlaceHolder()) {
            GenericsUtils.extractPlaceholders(receiver, resolvedPlaceholders);
        }
        StaticTypeCheckingVisitor.resolvePlaceholdersFromExplicitTypeHints(method, explicitTypeHints, resolvedPlaceholders);
        if (resolvedPlaceholders.isEmpty()) {
            return StaticTypeCheckingSupport.boundUnboundedWildcards(returnType);
        }
        Map<String, GenericsType> placeholdersFromContext = StaticTypeCheckingSupport.extractGenericsParameterMapOfThis(this.typeCheckingContext.getEnclosingMethod());
        StaticTypeCheckingSupport.applyGenericsConnections(placeholdersFromContext, resolvedPlaceholders);
        Parameter[] parameters = method.getParameters();
        boolean isVargs = StaticTypeCheckingSupport.isVargs(parameters);
        ArgumentListExpression argList = InvocationWriter.makeArgumentList(arguments);
        List<Expression> expressions = argList.getExpressions();
        int paramLength = parameters.length;
        if (expressions.size() >= paramLength) {
            for (int i = 0; i < paramLength; ++i) {
                boolean lastArg = i == paramLength - 1;
                ClassNode type = parameters[i].getType();
                ClassNode actualType = this.getType(expressions.get(i));
                while (!type.isUsingGenerics() && type.isArray() && actualType.isArray()) {
                    type = type.getComponentType();
                    actualType = actualType.getComponentType();
                }
                if (!StaticTypeCheckingSupport.isUsingGenericsOrIsArrayUsingGenerics(type)) continue;
                if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(actualType, ClassHelper.CLOSURE_TYPE) && ClassHelper.isSAMType(type)) {
                    Map<String, GenericsType> pholders = StaticTypeCheckingSupport.applyGenericsContextToParameterClass(resolvedPlaceholders, type);
                    actualType = StaticTypeCheckingVisitor.convertClosureTypeToSAMType(expressions.get(i), actualType, type, pholders);
                }
                if (isVargs && lastArg && actualType.isArray()) {
                    actualType = actualType.getComponentType();
                }
                if (isVargs && lastArg && type.isArray()) {
                    type = type.getComponentType();
                }
                actualType = StaticTypeCheckingVisitor.wrapTypeIfNecessary(actualType);
                HashMap<String, GenericsType> connections = new HashMap<String, GenericsType>();
                StaticTypeCheckingSupport.extractGenericsConnections(connections, actualType, type);
                StaticTypeCheckingVisitor.extractGenericsConnectionsForSuperClassAndInterfaces(resolvedPlaceholders, connections);
                StaticTypeCheckingSupport.applyGenericsConnections(connections, resolvedPlaceholders);
            }
        }
        return StaticTypeCheckingSupport.applyGenericsContext(resolvedPlaceholders, returnType);
    }

    private static void resolvePlaceholdersFromExplicitTypeHints(MethodNode method, GenericsType[] explicitTypeHints, Map<String, GenericsType> resolvedPlaceholders) {
        GenericsType[] methodGenericTypes;
        if (explicitTypeHints != null && (methodGenericTypes = method.getGenericsTypes()) != null && methodGenericTypes.length == explicitTypeHints.length) {
            for (int i = 0; i < explicitTypeHints.length; ++i) {
                GenericsType methodGenericType = methodGenericTypes[i];
                GenericsType explicitTypeHint = explicitTypeHints[i];
                resolvedPlaceholders.put(methodGenericType.getName(), explicitTypeHint);
            }
        }
    }

    private static void extractGenericsConnectionsForSuperClassAndInterfaces(Map<String, GenericsType> resolvedPlaceholders, Map<String, GenericsType> connections) {
        for (GenericsType value : new HashSet<GenericsType>(connections.values())) {
            if (value.isPlaceholder() || value.isWildcard()) continue;
            ClassNode valueType = value.getType();
            LinkedList<ClassNode> deepNodes = new LinkedList<ClassNode>();
            ClassNode unresolvedSuperClass = valueType.getUnresolvedSuperClass();
            if (unresolvedSuperClass != null && unresolvedSuperClass.isUsingGenerics()) {
                deepNodes.add(unresolvedSuperClass);
            }
            for (ClassNode classNode : valueType.getUnresolvedInterfaces()) {
                if (!classNode.isUsingGenerics()) continue;
                deepNodes.add(classNode);
            }
            if (deepNodes.isEmpty()) continue;
            for (GenericsType genericsType : resolvedPlaceholders.values()) {
                ClassNode[] classNodeArray;
                ClassNode lowerBound = genericsType.getLowerBound();
                if (lowerBound != null) {
                    for (ClassNode deepNode : deepNodes) {
                        if (!lowerBound.equals(deepNode)) continue;
                        StaticTypeCheckingSupport.extractGenericsConnections(connections, deepNode, lowerBound);
                    }
                }
                if ((classNodeArray = genericsType.getUpperBounds()) == null) continue;
                for (ClassNode upperBound : classNodeArray) {
                    for (ClassNode deepNode : deepNodes) {
                        if (!upperBound.equals(deepNode)) continue;
                        StaticTypeCheckingSupport.extractGenericsConnections(connections, deepNode, upperBound);
                    }
                }
            }
        }
    }

    private static ClassNode convertClosureTypeToSAMType(Expression expression, ClassNode closureType, ClassNode samType, Map<String, GenericsType> placeholders) {
        if (!samType.isUsingGenerics()) {
            return samType;
        }
        MethodNode sam = ClassHelper.findSAM(samType);
        if (closureType.isUsingGenerics() && sam != null) {
            ClassNode samReturnType = sam.getReturnType();
            ClassNode closureReturnType = (ClassNode)expression.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
            if (closureReturnType != null && closureReturnType.isUsingGenerics()) {
                ClassNode unwrapped = closureReturnType.getGenericsTypes()[0].getType();
                StaticTypeCheckingSupport.extractGenericsConnections(placeholders, unwrapped, samReturnType);
            } else if (samReturnType.isGenericsPlaceHolder()) {
                placeholders.put(samReturnType.getGenericsTypes()[0].getName(), closureType.getGenericsTypes()[0]);
            }
            if (expression instanceof ClosureExpression && sam.getParameters().length > 0) {
                LinkedList<ClassNode[]> genericsToConnect = new LinkedList<ClassNode[]>();
                Parameter[] closureParams = ((ClosureExpression)expression).getParameters();
                ClassNode[] closureParamTypes = StaticTypeCheckingVisitor.extractTypesFromParameters(closureParams);
                if (expression.getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS) != null) {
                    closureParamTypes = (ClassNode[])expression.getNodeMetaData((Object)StaticTypesMarker.CLOSURE_ARGUMENTS);
                }
                Parameter[] parameters = sam.getParameters();
                for (int i = 0; i < parameters.length; ++i) {
                    Parameter parameter = parameters[i];
                    if (!parameter.getOriginType().isUsingGenerics() || closureParamTypes.length <= i) continue;
                    genericsToConnect.add(new ClassNode[]{closureParamTypes[i], parameter.getOriginType()});
                }
                for (ClassNode[] classNodes : genericsToConnect) {
                    ClassNode expected;
                    ClassNode found = classNodes[0];
                    if (!StaticTypeCheckingSupport.isAssignableTo(found, expected = classNodes[1])) continue;
                    ClassNode generifiedType = GenericsUtils.parameterizeType(found, expected);
                    while (expected.isArray()) {
                        expected = expected.getComponentType();
                        generifiedType = generifiedType.getComponentType();
                    }
                    if (expected.isGenericsPlaceHolder()) {
                        placeholders.put(expected.getGenericsTypes()[0].getName(), new GenericsType(generifiedType));
                        continue;
                    }
                    GenericsType[] expectedGenericsTypes = expected.getGenericsTypes();
                    GenericsType[] foundGenericsTypes = generifiedType.getGenericsTypes();
                    for (int i = 0; i < expectedGenericsTypes.length; ++i) {
                        GenericsType type = expectedGenericsTypes[i];
                        if (!type.isPlaceholder()) continue;
                        String name = type.getName();
                        placeholders.put(name, foundGenericsTypes[i]);
                    }
                }
            }
        }
        ClassNode result = StaticTypeCheckingSupport.applyGenericsContext(placeholders, samType.redirect());
        return result;
    }

    private ClassNode resolveGenericsWithContext(Map<String, GenericsType> resolvedPlaceholders, ClassNode currentType) {
        Map<String, GenericsType> placeholdersFromContext = StaticTypeCheckingSupport.extractGenericsParameterMapOfThis(this.typeCheckingContext.getEnclosingMethod());
        return StaticTypeCheckingSupport.resolveClassNodeGenerics(resolvedPlaceholders, placeholdersFromContext, currentType);
    }

    private static ClassNode getDeclaringClass(MethodNode method, Expression arguments) {
        ClassNode declaringClass = method.getDeclaringClass();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression al = (ArgumentListExpression)arguments;
            List<Expression> list = al.getExpressions();
            if (list.isEmpty()) {
                return declaringClass;
            }
            Expression exp = list.get(0);
            ClassNode cn = (ClassNode)exp.getNodeMetaData(ExtensionMethodDeclaringClass.class);
            if (cn != null) {
                return cn;
            }
        }
        return declaringClass;
    }

    private Map<String, GenericsType> resolvePlaceHoldersFromDeclaration(ClassNode receiver, ClassNode declaration, MethodNode method, boolean isStaticTarget) {
        if (isStaticTarget && ClassHelper.CLASS_Type.equals(receiver) && receiver.isUsingGenerics() && receiver.getGenericsTypes().length > 0 && !ClassHelper.OBJECT_TYPE.equals(receiver.getGenericsTypes()[0].getType())) {
            return this.resolvePlaceHoldersFromDeclaration(receiver.getGenericsTypes()[0].getType(), declaration, method, isStaticTarget);
        }
        Map<String, GenericsType> resolvedPlaceholders = StaticTypeCheckingVisitor.extractPlaceHolders(method, receiver, declaration);
        return resolvedPlaceholders;
    }

    private static boolean isGenericsPlaceHolderOrArrayOf(ClassNode cn) {
        if (cn.isArray()) {
            return StaticTypeCheckingVisitor.isGenericsPlaceHolderOrArrayOf(cn.getComponentType());
        }
        return cn.isGenericsPlaceHolder();
    }

    private static Map<String, GenericsType> extractPlaceHolders(MethodNode method, ClassNode receiver, ClassNode declaringClass) {
        if (declaringClass.equals(ClassHelper.OBJECT_TYPE)) {
            HashMap<String, GenericsType> resolvedPlaceholders = new HashMap<String, GenericsType>();
            if (method != null) {
                StaticTypeCheckingSupport.addMethodLevelDeclaredGenerics(method, resolvedPlaceholders);
            }
            return resolvedPlaceholders;
        }
        HashMap<String, GenericsType> resolvedPlaceholders = null;
        if (ClassHelper.isPrimitiveType(receiver) && !ClassHelper.isPrimitiveType(declaringClass)) {
            receiver = ClassHelper.getWrapper(receiver);
        }
        List<ClassNode> queue = receiver instanceof UnionTypeClassNode ? Arrays.asList(((UnionTypeClassNode)receiver).getDelegates()) : Collections.singletonList(receiver);
        Iterator<ClassNode> iterator = queue.iterator();
        block0: while (iterator.hasNext()) {
            ClassNode item;
            ClassNode current = item = iterator.next();
            while (current != null) {
                boolean continueLoop = true;
                HashMap<String, GenericsType> currentPlaceHolders = new HashMap<String, GenericsType>();
                if (StaticTypeCheckingVisitor.isGenericsPlaceHolderOrArrayOf(declaringClass) || declaringClass.equals(current)) {
                    StaticTypeCheckingSupport.extractGenericsConnections(currentPlaceHolders, current, declaringClass);
                    if (method != null) {
                        StaticTypeCheckingSupport.addMethodLevelDeclaredGenerics(method, currentPlaceHolders);
                    }
                    continueLoop = false;
                } else {
                    GenericsUtils.extractPlaceholders(current, currentPlaceHolders);
                }
                if (resolvedPlaceholders != null) {
                    Set entries = currentPlaceHolders.entrySet();
                    for (Map.Entry entry : entries) {
                        GenericsType referenced;
                        GenericsType gt = (GenericsType)entry.getValue();
                        if (!gt.isPlaceholder() || (referenced = (GenericsType)resolvedPlaceholders.get(gt.getName())) == null) continue;
                        entry.setValue(referenced);
                    }
                }
                resolvedPlaceholders = currentPlaceHolders;
                if (!continueLoop) continue block0;
                if ((current = ClassHelper.getNextSuperClass(current, declaringClass)) != null || !ClassHelper.CLASS_Type.equals(declaringClass)) continue;
                current = declaringClass;
            }
        }
        if (resolvedPlaceholders == null) {
            String descriptor = "<>";
            if (method != null) {
                descriptor = method.getTypeDescriptor();
            }
            throw new GroovyBugError("Declaring class for method call to '" + descriptor + "' declared in " + declaringClass.getName() + " was not matched with found receiver " + receiver.getName() + ". This should not have happened!");
        }
        return resolvedPlaceholders;
    }

    protected boolean typeCheckMethodsWithGenericsOrFail(ClassNode receiver, ClassNode[] arguments, MethodNode candidateMethod, Expression location) {
        if (!StaticTypeCheckingSupport.typeCheckMethodsWithGenerics(receiver, arguments, candidateMethod)) {
            Map<String, GenericsType> classGTs = GenericsUtils.extractPlaceholders(receiver);
            ClassNode[] ptypes = new ClassNode[candidateMethod.getParameters().length];
            Parameter[] parameters = candidateMethod.getParameters();
            for (int i = 0; i < parameters.length; ++i) {
                Parameter parameter = parameters[i];
                ClassNode type = parameter.getType();
                ptypes[i] = StaticTypeCheckingSupport.fullyResolveType(type, classGTs);
            }
            this.addStaticTypeError("Cannot call " + StaticTypeCheckingVisitor.toMethodGenericTypesString(candidateMethod) + receiver.toString(false) + "#" + StaticTypeCheckingSupport.toMethodParametersString(candidateMethod.getName(), ptypes) + " with arguments " + StaticTypeCheckingVisitor.formatArgumentList(arguments), location);
            return false;
        }
        return true;
    }

    private static String toMethodGenericTypesString(MethodNode node) {
        GenericsType[] genericsTypes = node.getGenericsTypes();
        if (genericsTypes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("<");
        for (int i = 0; i < genericsTypes.length; ++i) {
            GenericsType genericsType = genericsTypes[i];
            sb.append(genericsType.toString());
            if (i >= genericsTypes.length - 1) continue;
            sb.append(",");
        }
        sb.append("> ");
        return sb.toString();
    }

    protected static String formatArgumentList(ClassNode[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(24 * nodes.length);
        sb.append("[");
        for (ClassNode node : nodes) {
            sb.append(StaticTypeCheckingSupport.prettyPrintType(node));
            sb.append(", ");
        }
        if (sb.length() > 1) {
            sb.setCharAt(sb.length() - 2, ']');
        }
        return sb.toString();
    }

    private static void putSetterInfo(Expression exp, SetterInfo info) {
        exp.putNodeMetaData(SetterInfo.class, info);
    }

    private static SetterInfo removeSetterInfo(Expression exp) {
        Object nodeMetaData = exp.getNodeMetaData(SetterInfo.class);
        if (nodeMetaData != null) {
            exp.removeNodeMetaData(SetterInfo.class);
            return (SetterInfo)nodeMetaData;
        }
        return null;
    }

    @Override
    protected void addError(String msg, ASTNode expr) {
        Long err = (long)expr.getLineNumber() << 16 + expr.getColumnNumber();
        if (DEBUG_GENERATED_CODE && expr.getLineNumber() < 0 || !this.typeCheckingContext.reportedErrors.contains(err)) {
            this.typeCheckingContext.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), this.typeCheckingContext.source));
            this.typeCheckingContext.reportedErrors.add(err);
        }
    }

    protected void addStaticTypeError(String msg, ASTNode expr) {
        if (expr.getColumnNumber() > 0 && expr.getLineNumber() > 0) {
            this.addError("[Static type checking] - " + msg, expr);
        } else if (DEBUG_GENERATED_CODE) {
            this.addError("[Static type checking] - Error in generated code [" + expr.getText() + "] - " + msg, expr);
        }
    }

    protected void addNoMatchingMethodError(ClassNode receiver, String name, ClassNode[] args, Expression call) {
        if (StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(receiver)) {
            receiver = receiver.getGenericsTypes()[0].getType();
        }
        this.addStaticTypeError("Cannot find matching method " + receiver.getText() + "#" + StaticTypeCheckingSupport.toMethodParametersString(name, args) + ". Please check if the declared type is correct and if the method exists.", call);
    }

    protected void addAmbiguousErrorMessage(List<MethodNode> foundMethods, String name, ClassNode[] args, Expression expr) {
        this.addStaticTypeError("Reference to method is ambiguous. Cannot choose between " + StaticTypeCheckingVisitor.prettyPrintMethodList(foundMethods), expr);
    }

    protected void addCategoryMethodCallError(Expression call) {
        this.addStaticTypeError("Due to their dynamic nature, usage of categories is not possible with static type checking active", call);
    }

    protected void addAssignmentError(ClassNode leftType, ClassNode rightType, Expression assignmentExpression) {
        this.addStaticTypeError("Cannot assign value of type " + rightType.toString(false) + " to variable of type " + leftType.toString(false), assignmentExpression);
    }

    protected void addUnsupportedPreOrPostfixExpressionError(Expression expression) {
        if (expression instanceof PostfixExpression) {
            this.addStaticTypeError("Unsupported postfix operation type [" + ((PostfixExpression)expression).getOperation() + "]", expression);
        } else if (expression instanceof PrefixExpression) {
            this.addStaticTypeError("Unsupported prefix operation type [" + ((PrefixExpression)expression).getOperation() + "]", expression);
        } else {
            throw new IllegalArgumentException("Method should be called with a PostfixExpression or a PrefixExpression");
        }
    }

    public void setMethodsToBeVisited(Set<MethodNode> methodsToBeVisited) {
        this.typeCheckingContext.methodsToBeVisited = methodsToBeVisited;
    }

    public void performSecondPass() {
        for (SecondPassExpression wrapper : this.typeCheckingContext.secondPassExpressions) {
            VariableExpression var;
            List<ClassNode> classNodes;
            Variable target;
            MethodCallExpression call;
            Expression objectExpression;
            Expression expression = wrapper.getExpression();
            if (expression instanceof BinaryExpression) {
                List<MethodNode> method;
                VariableExpression var2;
                List<ClassNode> classNodes2;
                Variable target2;
                Expression left = ((BinaryExpression)expression).getLeftExpression();
                if (!(left instanceof VariableExpression) || !((target2 = StaticTypeCheckingSupport.findTargetVariable((VariableExpression)left)) instanceof VariableExpression) || (classNodes2 = this.typeCheckingContext.closureSharedVariablesAssignmentTypes.get(var2 = (VariableExpression)target2)) == null || classNodes2.size() <= 1) continue;
                ClassNode lub = WideningCategories.lowestUpperBound(classNodes2);
                String message = StaticTypeCheckingSupport.getOperationName(((BinaryExpression)expression).getOperation().getType());
                if (message == null || !(method = this.findMethod(lub, message, this.getType(((BinaryExpression)expression).getRightExpression()))).isEmpty()) continue;
                this.addStaticTypeError("A closure shared variable [" + target2.getName() + "] has been assigned with various types and the method [" + StaticTypeCheckingSupport.toMethodParametersString(message, this.getType(((BinaryExpression)expression).getRightExpression())) + "] does not exist in the lowest upper bound of those types: [" + lub.toString(false) + "]. In general, this is a bad practice (variable reuse) because the compiler cannot determine safely what is the type of the variable at the moment of the call in a multithreaded context.", expression);
                continue;
            }
            if (!(expression instanceof MethodCallExpression) || !((objectExpression = (call = (MethodCallExpression)expression).getObjectExpression()) instanceof VariableExpression) || !((target = StaticTypeCheckingSupport.findTargetVariable((VariableExpression)objectExpression)) instanceof VariableExpression) || (classNodes = this.typeCheckingContext.closureSharedVariablesAssignmentTypes.get(var = (VariableExpression)target)) == null || classNodes.size() <= 1) continue;
            ClassNode lub = WideningCategories.lowestUpperBound(classNodes);
            MethodNode methodNode = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
            Parameter[] parameters = methodNode.getParameters();
            ClassNode[] params = StaticTypeCheckingVisitor.extractTypesFromParameters(parameters);
            ClassNode[] argTypes = (ClassNode[])wrapper.getData();
            List<MethodNode> method = this.findMethod(lub, methodNode.getName(), argTypes);
            if (method.size() == 1) continue;
            this.addStaticTypeError("A closure shared variable [" + target.getName() + "] has been assigned with various types and the method [" + StaticTypeCheckingSupport.toMethodParametersString(methodNode.getName(), params) + "] does not exist in the lowest upper bound of those types: [" + lub.toString(false) + "]. In general, this is a bad practice (variable reuse) because the compiler cannot determine safely what is the type of the variable at the moment of the call in a multithreaded context.", call);
        }
        this.extension.finish();
    }

    protected static ClassNode[] extractTypesFromParameters(Parameter[] parameters) {
        ClassNode[] params = new ClassNode[parameters.length];
        for (int i = 0; i < params.length; ++i) {
            params[i] = parameters[i].getType();
        }
        return params;
    }

    protected static ClassNode wrapTypeIfNecessary(ClassNode type) {
        if (ClassHelper.isPrimitiveType(type)) {
            return ClassHelper.getWrapper(type);
        }
        return type;
    }

    protected static boolean isClassInnerClassOrEqualTo(ClassNode toBeChecked, ClassNode start) {
        if (start == toBeChecked) {
            return true;
        }
        if (start instanceof InnerClassNode) {
            return StaticTypeCheckingVisitor.isClassInnerClassOrEqualTo(toBeChecked, start.getOuterClass());
        }
        return false;
    }

    private static class ParameterVariableExpression
    extends VariableExpression {
        private final Parameter parameter;

        ParameterVariableExpression(Parameter parameter) {
            super(parameter);
            this.parameter = parameter;
            ClassNode inferred = (ClassNode)parameter.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
            if (inferred == null) {
                parameter.setNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, parameter.getOriginType());
            }
        }

        @Override
        public void copyNodeMetaData(ASTNode other) {
            this.parameter.copyNodeMetaData(other);
        }

        @Override
        public Object putNodeMetaData(Object key, Object value) {
            return this.parameter.putNodeMetaData(key, value);
        }

        @Override
        public void removeNodeMetaData(Object key) {
            this.parameter.removeNodeMetaData(key);
        }

        @Override
        public Map<?, ?> getNodeMetaData() {
            return this.parameter.getNodeMetaData();
        }

        @Override
        public <T> T getNodeMetaData(Object key) {
            return this.parameter.getNodeMetaData(key);
        }

        @Override
        public void setNodeMetaData(Object key, Object value) {
            this.parameter.setNodeMetaData(key, value);
        }

        public int hashCode() {
            return this.parameter.hashCode();
        }

        public boolean equals(Object other) {
            return this.parameter.equals(other);
        }
    }

    private static class SetterInfo {
        final ClassNode receiverType;
        final String name;
        final List<MethodNode> setters;

        private SetterInfo(ClassNode receiverType, String name, List<MethodNode> setters) {
            this.receiverType = receiverType;
            this.setters = setters;
            this.name = name;
        }
    }

    public static class SignatureCodecFactory {
        public static SignatureCodec getCodec(int version, ClassLoader classLoader) {
            switch (version) {
                case 1: {
                    return new SignatureCodecVersion1(classLoader);
                }
            }
            return null;
        }
    }

    protected class VariableExpressionTypeMemoizer
    extends ClassCodeVisitorSupport {
        private final Map<VariableExpression, ClassNode> varOrigType;

        public VariableExpressionTypeMemoizer(Map<VariableExpression, ClassNode> varOrigType) {
            this.varOrigType = varOrigType;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return StaticTypeCheckingVisitor.this.typeCheckingContext.source;
        }

        @Override
        public void visitVariableExpression(VariableExpression expression) {
            super.visitVariableExpression(expression);
            Variable var = StaticTypeCheckingSupport.findTargetVariable(expression);
            if (var instanceof VariableExpression) {
                VariableExpression ve = (VariableExpression)var;
                this.varOrigType.put(ve, (ClassNode)ve.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE));
            }
        }
    }

    private static class ExtensionMethodDeclaringClass {
        private ExtensionMethodDeclaringClass() {
        }
    }
}

