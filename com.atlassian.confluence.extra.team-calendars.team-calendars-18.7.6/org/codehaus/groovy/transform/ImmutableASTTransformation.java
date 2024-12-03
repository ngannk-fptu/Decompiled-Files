/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import groovy.transform.Immutable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ReflectionMethodInvoker;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.EqualsAndHashCodeASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.ToStringASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ImmutableASTTransformation
extends AbstractASTTransformation {
    private static List<String> immutableList = Arrays.asList("java.lang.Class", "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Double", "java.lang.Float", "java.lang.Integer", "java.lang.Long", "java.lang.Short", "java.lang.String", "java.math.BigInteger", "java.math.BigDecimal", "java.awt.Color", "java.net.URI", "java.util.UUID");
    private static final Class MY_CLASS = Immutable.class;
    public static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    static final String MEMBER_KNOWN_IMMUTABLE_CLASSES = "knownImmutableClasses";
    static final String MEMBER_KNOWN_IMMUTABLES = "knownImmutables";
    static final String MEMBER_ADD_COPY_WITH = "copyWith";
    static final String COPY_WITH_METHOD = "copyWith";
    private static final ClassNode DATE_TYPE = ClassHelper.make(Date.class);
    private static final ClassNode CLONEABLE_TYPE = ClassHelper.make(Cloneable.class);
    private static final ClassNode COLLECTION_TYPE = ClassHelper.makeWithoutCaching(Collection.class, false);
    private static final ClassNode READONLYEXCEPTION_TYPE = ClassHelper.make(ReadOnlyPropertyException.class);
    private static final ClassNode DGM_TYPE = ClassHelper.make(DefaultGroovyMethods.class);
    private static final ClassNode SELF_TYPE = ClassHelper.make(ImmutableASTTransformation.class);
    private static final ClassNode HASHMAP_TYPE = ClassHelper.makeWithoutCaching(HashMap.class, false);
    private static final ClassNode MAP_TYPE = ClassHelper.makeWithoutCaching(Map.class, false);
    private static final ClassNode REFLECTION_INVOKER_TYPE = ClassHelper.make(ReflectionMethodInvoker.class);
    private static final ClassNode SORTEDSET_CLASSNODE = ClassHelper.make(SortedSet.class);
    private static final ClassNode SORTEDMAP_CLASSNODE = ClassHelper.make(SortedMap.class);
    private static final ClassNode SET_CLASSNODE = ClassHelper.make(Set.class);
    private static final ClassNode MAP_CLASSNODE = ClassHelper.make(Map.class);
    public static final String IMMUTABLE_SAFE_FLAG = "Immutable.Safe";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!node.getClassNode().getName().endsWith(".Immutable")) {
            return;
        }
        ArrayList<PropertyNode> newProperties = new ArrayList<PropertyNode>();
        if (parent instanceof ClassNode) {
            List<String> knownImmutableClasses = this.getKnownImmutableClasses(node);
            List<String> knownImmutables = this.getKnownImmutables(node);
            ClassNode cNode = (ClassNode)parent;
            String cName = cNode.getName();
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            this.makeClassFinal(cNode);
            List<PropertyNode> pList = GeneralUtils.getInstanceProperties(cNode);
            for (PropertyNode pNode : pList) {
                this.adjustPropertyForImmutability(pNode, newProperties);
            }
            for (PropertyNode pNode : newProperties) {
                cNode.getProperties().remove(pNode);
                this.addProperty(cNode, pNode);
            }
            List<FieldNode> fList = cNode.getFields();
            for (FieldNode fNode : fList) {
                this.ensureNotPublic(cName, fNode);
            }
            this.createConstructors(cNode, knownImmutableClasses, knownImmutables);
            if (!this.hasAnnotation(cNode, EqualsAndHashCodeASTTransformation.MY_TYPE)) {
                EqualsAndHashCodeASTTransformation.createHashCode(cNode, true, false, false, null, null);
                EqualsAndHashCodeASTTransformation.createEquals(cNode, false, false, false, null, null);
            }
            if (!this.hasAnnotation(cNode, ToStringASTTransformation.MY_TYPE)) {
                ToStringASTTransformation.createToString(cNode, false, false, null, null, false, true);
            }
            if (this.memberHasValue(node, "copyWith", true) && !pList.isEmpty() && !GeneralUtils.hasDeclaredMethod(cNode, "copyWith", 1)) {
                this.createCopyWith(cNode, pList);
            }
        }
    }

    private void doAddConstructor(final ClassNode cNode, ConstructorNode constructorNode) {
        cNode.addConstructor(constructorNode);
        Parameter argsParam = null;
        for (Parameter p : constructorNode.getParameters()) {
            if (!"args".equals(p.getName())) continue;
            argsParam = p;
            break;
        }
        if (argsParam != null) {
            final Parameter arg = argsParam;
            ClassCodeVisitorSupport variableExpressionFix = new ClassCodeVisitorSupport(){

                @Override
                protected SourceUnit getSourceUnit() {
                    return cNode.getModule().getContext();
                }

                @Override
                public void visitVariableExpression(VariableExpression expression) {
                    super.visitVariableExpression(expression);
                    if ("args".equals(expression.getName())) {
                        expression.setAccessedVariable(arg);
                    }
                }
            };
            variableExpressionFix.visitConstructor(constructorNode);
        }
    }

    private List<String> getKnownImmutableClasses(AnnotationNode node) {
        ArrayList<String> immutableClasses = new ArrayList<String>();
        Expression expression = node.getMember(MEMBER_KNOWN_IMMUTABLE_CLASSES);
        if (expression == null) {
            return immutableClasses;
        }
        if (!(expression instanceof ListExpression)) {
            this.addError("Use the Groovy list notation [el1, el2] to specify known immutable classes via \"knownImmutableClasses\"", node);
            return immutableClasses;
        }
        ListExpression listExpression = (ListExpression)expression;
        for (Expression listItemExpression : listExpression.getExpressions()) {
            if (!(listItemExpression instanceof ClassExpression)) continue;
            immutableClasses.add(listItemExpression.getType().getName());
        }
        return immutableClasses;
    }

    private List<String> getKnownImmutables(AnnotationNode node) {
        ArrayList<String> immutables = new ArrayList<String>();
        Expression expression = node.getMember(MEMBER_KNOWN_IMMUTABLES);
        if (expression == null) {
            return immutables;
        }
        if (!(expression instanceof ListExpression)) {
            this.addError("Use the Groovy list notation [el1, el2] to specify known immutable property names via \"knownImmutables\"", node);
            return immutables;
        }
        ListExpression listExpression = (ListExpression)expression;
        for (Expression listItemExpression : listExpression.getExpressions()) {
            if (!(listItemExpression instanceof ConstantExpression)) continue;
            immutables.add((String)((ConstantExpression)listItemExpression).getValue());
        }
        return immutables;
    }

    private void makeClassFinal(ClassNode cNode) {
        int modifiers = cNode.getModifiers();
        if ((modifiers & 0x10) == 0) {
            if ((modifiers & 0x1400) == 5120) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: annotation found on inappropriate class " + cNode.getName(), cNode);
                return;
            }
            cNode.setModifiers(modifiers | 0x10);
        }
    }

    private void createConstructors(ClassNode cNode, List<String> knownImmutableClasses, List<String> knownImmutables) {
        boolean specialHashMapCase;
        if (!this.validateConstructors(cNode)) {
            return;
        }
        List<PropertyNode> list = GeneralUtils.getInstanceProperties(cNode);
        boolean bl = specialHashMapCase = list.size() == 1 && list.get(0).getField().getType().equals(HASHMAP_TYPE);
        if (specialHashMapCase) {
            this.createConstructorMapSpecial(cNode, list);
        } else {
            this.createConstructorMap(cNode, list, knownImmutableClasses, knownImmutables);
            this.createConstructorOrdered(cNode, list);
        }
    }

    private void createConstructorOrdered(ClassNode cNode, List<PropertyNode> list) {
        MapExpression argMap = new MapExpression();
        Parameter[] orderedParams = new Parameter[list.size()];
        int index = 0;
        for (PropertyNode pNode : list) {
            Parameter param = new Parameter(pNode.getField().getType(), pNode.getField().getName());
            orderedParams[index++] = param;
            argMap.addMapEntryExpression(GeneralUtils.constX(pNode.getName()), GeneralUtils.varX(pNode.getName()));
        }
        BlockStatement orderedBody = new BlockStatement();
        orderedBody.addStatement(GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.THIS, GeneralUtils.args(GeneralUtils.castX(HASHMAP_TYPE, argMap)))));
        this.doAddConstructor(cNode, new ConstructorNode(1, orderedParams, ClassNode.EMPTY_ARRAY, orderedBody));
    }

    private Statement createGetterBodyDefault(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        return GeneralUtils.stmt(fieldExpr);
    }

    private Expression cloneCollectionExpr(Expression fieldExpr, ClassNode type) {
        return GeneralUtils.castX(type, this.createIfInstanceOfAsImmutableS(fieldExpr, SORTEDSET_CLASSNODE, this.createIfInstanceOfAsImmutableS(fieldExpr, SORTEDMAP_CLASSNODE, this.createIfInstanceOfAsImmutableS(fieldExpr, SET_CLASSNODE, this.createIfInstanceOfAsImmutableS(fieldExpr, MAP_CLASSNODE, this.createIfInstanceOfAsImmutableS(fieldExpr, ClassHelper.LIST_TYPE, this.createAsImmutableX(fieldExpr, COLLECTION_TYPE)))))));
    }

    private Expression createIfInstanceOfAsImmutableS(Expression expr, ClassNode type, Expression elseStatement) {
        return GeneralUtils.ternaryX(GeneralUtils.isInstanceOfX(expr, type), this.createAsImmutableX(expr, type), elseStatement);
    }

    private Expression createAsImmutableX(Expression expr, ClassNode type) {
        return GeneralUtils.callX(DGM_TYPE, "asImmutable", (Expression)GeneralUtils.castX(type, expr));
    }

    private Expression cloneArrayOrCloneableExpr(Expression fieldExpr, ClassNode type) {
        StaticMethodCallExpression smce = GeneralUtils.callX(REFLECTION_INVOKER_TYPE, "invoke", (Expression)GeneralUtils.args(fieldExpr, GeneralUtils.constX("clone"), new ArrayExpression(ClassHelper.OBJECT_TYPE.makeArray(), Collections.<Expression>emptyList())));
        return GeneralUtils.castX(type, smce);
    }

    private void createConstructorMapSpecial(ClassNode cNode, List<PropertyNode> list) {
        BlockStatement body = new BlockStatement();
        body.addStatement(this.createConstructorStatementMapSpecial(list.get(0).getField()));
        this.createConstructorMapCommon(cNode, body);
    }

    private void createConstructorMap(ClassNode cNode, List<PropertyNode> list, List<String> knownImmutableClasses, List<String> knownImmutables) {
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.equalsNullX(GeneralUtils.varX("args")), GeneralUtils.assignS(GeneralUtils.varX("args"), new MapExpression())));
        for (PropertyNode pNode : list) {
            body.addStatement(this.createConstructorStatement(cNode, pNode, knownImmutableClasses, knownImmutables));
        }
        body.addStatement(GeneralUtils.stmt(GeneralUtils.callX(SELF_TYPE, "checkPropNames", (Expression)GeneralUtils.args("this", "args"))));
        this.createConstructorMapCommon(cNode, body);
        if (!list.isEmpty()) {
            this.createNoArgConstructor(cNode);
        }
    }

    private void createNoArgConstructor(ClassNode cNode) {
        Statement body = GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.THIS, GeneralUtils.args(new MapExpression())));
        this.doAddConstructor(cNode, new ConstructorNode(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body));
    }

    private void createConstructorMapCommon(ClassNode cNode, BlockStatement body) {
        List<FieldNode> fList = cNode.getFields();
        for (FieldNode fNode : fList) {
            if (fNode.isPublic() || cNode.getProperty(fNode.getName()) != null || fNode.isFinal() && fNode.isStatic() || fNode.getName().contains("$") || fNode.isSynthetic()) continue;
            if (fNode.isFinal() && fNode.getInitialExpression() != null) {
                body.addStatement(this.checkFinalArgNotOverridden(cNode, fNode));
            }
            body.addStatement(GeneralUtils.createConstructorStatementDefault(fNode));
        }
        this.doAddConstructor(cNode, new ConstructorNode(1, GeneralUtils.params(new Parameter(HASHMAP_TYPE, "args")), ClassNode.EMPTY_ARRAY, body));
    }

    private Statement checkFinalArgNotOverridden(ClassNode cNode, FieldNode fNode) {
        String name = fNode.getName();
        Expression value = GeneralUtils.findArg(name);
        return GeneralUtils.ifS((Expression)GeneralUtils.notX(GeneralUtils.equalsNullX(value)), GeneralUtils.throwS(GeneralUtils.ctorX(READONLYEXCEPTION_TYPE, GeneralUtils.args(GeneralUtils.constX(name), GeneralUtils.constX(cNode.getName())))));
    }

    private Statement createConstructorStatementMapSpecial(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        ClassNode fieldType = ((Expression)fieldExpr).getType();
        Expression initExpr = fNode.getInitialValueExpression();
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION) : GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(initExpr, fieldType));
        Expression namedArgs = GeneralUtils.findArg(fNode.getName());
        VariableExpression baseArgs = GeneralUtils.varX("args");
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(baseArgs), assignInit, GeneralUtils.ifElseS(GeneralUtils.equalsNullX(namedArgs), GeneralUtils.ifElseS(GeneralUtils.isTrueX(GeneralUtils.callX((Expression)baseArgs, "containsKey", (Expression)GeneralUtils.constX(fNode.getName()))), GeneralUtils.assignS(fieldExpr, namedArgs), GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(baseArgs, fieldType))), GeneralUtils.ifElseS(GeneralUtils.isOneX(GeneralUtils.callX(baseArgs, "size")), GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(namedArgs, fieldType)), GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(baseArgs, fieldType)))));
    }

    private void ensureNotPublic(String cNode, FieldNode fNode) {
        String fName = fNode.getName();
        if (!(!fNode.isPublic() || fName.contains("$") || fNode.isStatic() && fNode.isFinal())) {
            this.addError("Public field '" + fName + "' not allowed for " + MY_TYPE_NAME + " class '" + cNode + "'.", fNode);
        }
    }

    private void addProperty(ClassNode cNode, PropertyNode pNode) {
        FieldNode fn = pNode.getField();
        cNode.getFields().remove(fn);
        cNode.addProperty(pNode.getName(), pNode.getModifiers() | 0x10, pNode.getType(), pNode.getInitialExpression(), pNode.getGetterBlock(), pNode.getSetterBlock());
        FieldNode newfn = cNode.getField(fn.getName());
        cNode.getFields().remove(newfn);
        cNode.addField(fn);
    }

    private boolean validateConstructors(ClassNode cNode) {
        List<ConstructorNode> declaredConstructors = cNode.getDeclaredConstructors();
        for (ConstructorNode constructorNode : declaredConstructors) {
            Object nodeMetaData = constructorNode.getNodeMetaData(IMMUTABLE_SAFE_FLAG);
            if (nodeMetaData != null && ((Boolean)nodeMetaData).booleanValue()) continue;
            this.addError("Explicit constructors not allowed for " + MY_TYPE_NAME + " class: " + cNode.getNameWithoutPackage(), constructorNode);
            return false;
        }
        return true;
    }

    private Statement createConstructorStatement(ClassNode cNode, PropertyNode pNode, List<String> knownImmutableClasses, List<String> knownImmutables) {
        FieldNode fNode = pNode.getField();
        ClassNode fieldType = fNode.getType();
        Statement statement = null;
        if (fieldType.isArray() || GeneralUtils.isOrImplements(fieldType, CLONEABLE_TYPE)) {
            statement = this.createConstructorStatementArrayOrCloneable(fNode);
        } else if (this.isKnownImmutableClass(fieldType, knownImmutableClasses) || this.isKnownImmutable(pNode.getName(), knownImmutables)) {
            statement = GeneralUtils.createConstructorStatementDefault(fNode);
        } else if (fieldType.isDerivedFrom(DATE_TYPE)) {
            statement = this.createConstructorStatementDate(fNode);
        } else if (GeneralUtils.isOrImplements(fieldType, COLLECTION_TYPE) || fieldType.isDerivedFrom(COLLECTION_TYPE) || GeneralUtils.isOrImplements(fieldType, MAP_TYPE) || fieldType.isDerivedFrom(MAP_TYPE)) {
            statement = this.createConstructorStatementCollection(fNode);
        } else if (fieldType.isResolved()) {
            this.addError(ImmutableASTTransformation.createErrorMessage(cNode.getName(), fNode.getName(), fieldType.getName(), "compiling"), fNode);
            statement = EmptyStatement.INSTANCE;
        } else {
            statement = this.createConstructorStatementGuarded(cNode, fNode);
        }
        return statement;
    }

    private Statement createConstructorStatementGuarded(ClassNode cNode, FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression initExpr = fNode.getInitialValueExpression();
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION) : GeneralUtils.assignS(fieldExpr, this.checkUnresolved(fNode, initExpr));
        Expression unknown = GeneralUtils.findArg(fNode.getName());
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(unknown), assignInit, GeneralUtils.assignS(fieldExpr, this.checkUnresolved(fNode, unknown)));
    }

    private Expression checkUnresolved(FieldNode fNode, Expression value) {
        ArgumentListExpression args = GeneralUtils.args(GeneralUtils.callThisX("getClass"), GeneralUtils.constX(fNode.getName()), value);
        return GeneralUtils.callX(SELF_TYPE, "checkImmutable", (Expression)args);
    }

    private Statement createConstructorStatementCollection(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        ClassNode fieldType = ((Expression)fieldExpr).getType();
        Expression initExpr = fNode.getInitialValueExpression();
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION) : GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(initExpr, fieldType));
        Expression collection = GeneralUtils.findArg(fNode.getName());
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(collection), assignInit, GeneralUtils.ifElseS(GeneralUtils.isInstanceOfX(collection, CLONEABLE_TYPE), GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(this.cloneArrayOrCloneableExpr(collection, fieldType), fieldType)), GeneralUtils.assignS(fieldExpr, this.cloneCollectionExpr(collection, fieldType))));
    }

    private boolean isKnownImmutableClass(ClassNode fieldType, List<String> knownImmutableClasses) {
        if (ImmutableASTTransformation.inImmutableList(fieldType.getName()) || knownImmutableClasses.contains(fieldType.getName())) {
            return true;
        }
        if (!fieldType.isResolved()) {
            return false;
        }
        return fieldType.isEnum() || ClassHelper.isPrimitiveType(fieldType) || !fieldType.getAnnotations(MY_TYPE).isEmpty();
    }

    private boolean isKnownImmutable(String fieldName, List<String> knownImmutables) {
        return knownImmutables.contains(fieldName);
    }

    private static boolean inImmutableList(String typeName) {
        return immutableList.contains(typeName);
    }

    private Statement createConstructorStatementArrayOrCloneable(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression initExpr = fNode.getInitialValueExpression();
        ClassNode fieldType = fNode.getType();
        Expression array = GeneralUtils.findArg(fNode.getName());
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION) : GeneralUtils.assignS(fieldExpr, this.cloneArrayOrCloneableExpr(initExpr, fieldType));
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(array), assignInit, GeneralUtils.assignS(fieldExpr, this.cloneArrayOrCloneableExpr(array, fieldType)));
    }

    private Statement createConstructorStatementDate(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression initExpr = fNode.getInitialValueExpression();
        Statement assignInit = initExpr == null || initExpr instanceof ConstantExpression && ((ConstantExpression)initExpr).isNullExpression() ? GeneralUtils.assignS(fieldExpr, ConstantExpression.EMPTY_EXPRESSION) : GeneralUtils.assignS(fieldExpr, this.cloneDateExpr(initExpr));
        Expression date = GeneralUtils.findArg(fNode.getName());
        return GeneralUtils.ifElseS(GeneralUtils.equalsNullX(date), assignInit, GeneralUtils.assignS(fieldExpr, this.cloneDateExpr(date)));
    }

    private Expression cloneDateExpr(Expression origDate) {
        return GeneralUtils.ctorX(DATE_TYPE, GeneralUtils.callX(origDate, "getTime"));
    }

    private void adjustPropertyForImmutability(PropertyNode pNode, List<PropertyNode> newNodes) {
        FieldNode fNode = pNode.getField();
        fNode.setModifiers(pNode.getModifiers() & 0xFFFFFFFE | 0x10 | 2);
        this.adjustPropertyNode(pNode, this.createGetterBody(fNode));
        newNodes.add(pNode);
    }

    private void adjustPropertyNode(PropertyNode pNode, Statement getterBody) {
        pNode.setSetterBlock(null);
        pNode.setGetterBlock(getterBody);
    }

    private Statement createGetterBody(FieldNode fNode) {
        BlockStatement body = new BlockStatement();
        ClassNode fieldType = fNode.getType();
        Statement statement = fieldType.isArray() || GeneralUtils.isOrImplements(fieldType, CLONEABLE_TYPE) ? this.createGetterBodyArrayOrCloneable(fNode) : (fieldType.isDerivedFrom(DATE_TYPE) ? this.createGetterBodyDate(fNode) : this.createGetterBodyDefault(fNode));
        body.addStatement(statement);
        return body;
    }

    private static String createErrorMessage(String className, String fieldName, String typeName, String mode) {
        return MY_TYPE_NAME + " processor doesn't know how to handle field '" + fieldName + "' of type '" + ImmutableASTTransformation.prettyTypeName(typeName) + "' while " + mode + " class " + className + ".\n" + MY_TYPE_NAME + " classes only support properties with effectively immutable types including:\n- Strings, primitive types, wrapper types, Class, BigInteger and BigDecimal, enums\n- other " + MY_TYPE_NAME + " classes and known immutables (java.awt.Color, java.net.URI)\n- Cloneable classes, collections, maps and arrays, and other classes with special handling (java.util.Date)\nOther restrictions apply, please see the groovydoc for " + MY_TYPE_NAME + " for further details";
    }

    private static String prettyTypeName(String name) {
        return name.equals("java.lang.Object") ? name + " or def" : name;
    }

    private Statement createGetterBodyArrayOrCloneable(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression expression = this.cloneArrayOrCloneableExpr(fieldExpr, fNode.getType());
        return GeneralUtils.safeExpression(fieldExpr, expression);
    }

    private Statement createGetterBodyDate(FieldNode fNode) {
        VariableExpression fieldExpr = GeneralUtils.varX(fNode);
        Expression expression = this.cloneDateExpr(fieldExpr);
        return GeneralUtils.safeExpression(fieldExpr, expression);
    }

    private Statement createCheckForProperty(PropertyNode pNode) {
        return GeneralUtils.block(new VariableScope(), GeneralUtils.ifElseS(GeneralUtils.callX((Expression)GeneralUtils.varX("map", HASHMAP_TYPE), "containsKey", (Expression)GeneralUtils.args(GeneralUtils.constX(pNode.getName()))), GeneralUtils.block(new VariableScope(), GeneralUtils.declS(GeneralUtils.varX("newValue", ClassHelper.OBJECT_TYPE), GeneralUtils.callX((Expression)GeneralUtils.varX("map", HASHMAP_TYPE), "get", (Expression)GeneralUtils.args(GeneralUtils.constX(pNode.getName())))), GeneralUtils.declS(GeneralUtils.varX("oldValue", ClassHelper.OBJECT_TYPE), GeneralUtils.callThisX(GeneralUtils.getGetterName(pNode))), GeneralUtils.ifS((Expression)GeneralUtils.neX(GeneralUtils.varX("newValue", ClassHelper.OBJECT_TYPE), GeneralUtils.varX("oldValue", ClassHelper.OBJECT_TYPE)), GeneralUtils.block(new VariableScope(), GeneralUtils.assignS(GeneralUtils.varX("oldValue", ClassHelper.OBJECT_TYPE), GeneralUtils.varX("newValue", ClassHelper.OBJECT_TYPE)), GeneralUtils.assignS(GeneralUtils.varX("dirty", ClassHelper.boolean_TYPE), ConstantExpression.TRUE))), GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.varX("construct", HASHMAP_TYPE), "put", (Expression)GeneralUtils.args(GeneralUtils.constX(pNode.getName()), GeneralUtils.varX("oldValue", ClassHelper.OBJECT_TYPE))))), GeneralUtils.block(new VariableScope(), GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.varX("construct", HASHMAP_TYPE), "put", (Expression)GeneralUtils.args(GeneralUtils.constX(pNode.getName()), GeneralUtils.callThisX(GeneralUtils.getGetterName(pNode))))))));
    }

    private void createCopyWith(ClassNode cNode, List<PropertyNode> pList) {
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.orX(GeneralUtils.equalsNullX(GeneralUtils.varX("map", ClassHelper.MAP_TYPE)), GeneralUtils.eqX(GeneralUtils.callX(GeneralUtils.varX("map", HASHMAP_TYPE), "size"), GeneralUtils.constX(0))), GeneralUtils.returnS(GeneralUtils.varX("this", cNode))));
        body.addStatement(GeneralUtils.declS(GeneralUtils.varX("dirty", ClassHelper.boolean_TYPE), ConstantExpression.PRIM_FALSE));
        body.addStatement(GeneralUtils.declS(GeneralUtils.varX("construct", HASHMAP_TYPE), GeneralUtils.ctorX(HASHMAP_TYPE)));
        for (PropertyNode pNode : pList) {
            body.addStatement(this.createCheckForProperty(pNode));
        }
        body.addStatement(GeneralUtils.returnS(GeneralUtils.ternaryX(GeneralUtils.isTrueX(GeneralUtils.varX("dirty", ClassHelper.boolean_TYPE)), GeneralUtils.ctorX(cNode, GeneralUtils.args(GeneralUtils.varX("construct", HASHMAP_TYPE))), GeneralUtils.varX("this", cNode))));
        ClassNode clonedNode = cNode.getPlainNodeReference();
        cNode.addMethod("copyWith", 17, clonedNode, GeneralUtils.params(new Parameter(new ClassNode(Map.class), "map")), null, body);
    }

    public static Object checkImmutable(String className, String fieldName, Object field) {
        if (field == null || field instanceof Enum || ImmutableASTTransformation.inImmutableList(field.getClass().getName())) {
            return field;
        }
        if (field instanceof Collection) {
            return DefaultGroovyMethods.asImmutable((Collection)field);
        }
        if (field.getClass().getAnnotation(MY_CLASS) != null) {
            return field;
        }
        String typeName = field.getClass().getName();
        throw new RuntimeException(ImmutableASTTransformation.createErrorMessage(className, fieldName, typeName, "constructing"));
    }

    public static Object checkImmutable(Class<?> clazz, String fieldName, Object field) {
        Immutable immutable = (Immutable)clazz.getAnnotation(MY_CLASS);
        List<Object> knownImmutableClasses = new ArrayList();
        if (immutable != null && immutable.knownImmutableClasses().length > 0) {
            knownImmutableClasses = Arrays.asList(immutable.knownImmutableClasses());
        }
        if (field == null || field instanceof Enum || ImmutableASTTransformation.inImmutableList(field.getClass().getName()) || knownImmutableClasses.contains(field.getClass())) {
            return field;
        }
        if (field.getClass().getAnnotation(MY_CLASS) != null) {
            return field;
        }
        if (field instanceof Collection) {
            try {
                Field declaredField = clazz.getDeclaredField(fieldName);
                Class<?> fieldType = declaredField.getType();
                if (Collection.class.isAssignableFrom(fieldType)) {
                    return DefaultGroovyMethods.asImmutable((Collection)field);
                }
                if (fieldType.getAnnotation(MY_CLASS) != null) {
                    return field;
                }
                if (ImmutableASTTransformation.inImmutableList(fieldType.getName()) || knownImmutableClasses.contains(fieldType)) {
                    return field;
                }
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        String typeName = field.getClass().getName();
        throw new RuntimeException(ImmutableASTTransformation.createErrorMessage(clazz.getName(), fieldName, typeName, "constructing"));
    }

    public static void checkPropNames(Object instance, Map<String, Object> args) {
        MetaClass metaClass = InvokerHelper.getMetaClass(instance);
        for (String k : args.keySet()) {
            if (metaClass.hasProperty(instance, k) != null) continue;
            throw new MissingPropertyException(k, instance.getClass());
        }
    }
}

