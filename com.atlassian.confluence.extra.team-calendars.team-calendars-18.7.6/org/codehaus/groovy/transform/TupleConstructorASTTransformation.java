/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.TupleConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.CanonicalASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.ImmutableASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class TupleConstructorASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = TupleConstructor.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode LHMAP_TYPE = ClassHelper.makeWithoutCaching(LinkedHashMap.class, false);
    private static final ClassNode HMAP_TYPE = ClassHelper.makeWithoutCaching(HashMap.class, false);
    private static final ClassNode CHECK_METHOD_TYPE = ClassHelper.make(ImmutableASTTransformation.class);
    private static Map<Class<?>, Expression> primitivesInitialValues;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            ClassNode cNode = (ClassNode)parent;
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            boolean includeFields = this.memberHasValue(anno, "includeFields", true);
            boolean includeProperties = !this.memberHasValue(anno, "includeProperties", false);
            boolean includeSuperFields = this.memberHasValue(anno, "includeSuperFields", true);
            boolean includeSuperProperties = this.memberHasValue(anno, "includeSuperProperties", true);
            boolean callSuper = this.memberHasValue(anno, "callSuper", true);
            boolean force = this.memberHasValue(anno, "force", true);
            List<String> excludes = TupleConstructorASTTransformation.getMemberList(anno, "excludes");
            List<String> includes = TupleConstructorASTTransformation.getMemberList(anno, "includes");
            if (this.hasAnnotation(cNode, CanonicalASTTransformation.MY_TYPE)) {
                AnnotationNode canonical = cNode.getAnnotations(CanonicalASTTransformation.MY_TYPE).get(0);
                if (excludes == null || excludes.isEmpty()) {
                    excludes = TupleConstructorASTTransformation.getMemberList(canonical, "excludes");
                }
                if (includes == null || includes.isEmpty()) {
                    includes = TupleConstructorASTTransformation.getMemberList(canonical, "includes");
                }
            }
            if (!this.checkIncludeExclude(anno, excludes, includes, MY_TYPE_NAME)) {
                return;
            }
            TupleConstructorASTTransformation.createConstructor(cNode, includeFields, includeProperties, includeSuperFields, includeSuperProperties, callSuper, force, excludes, includes);
        }
    }

    public static void createConstructor(ClassNode cNode, boolean includeFields, boolean includeProperties, boolean includeSuperFields, boolean includeSuperProperties, boolean callSuper, boolean force, List<String> excludes, List<String> includes) {
        String name;
        boolean foundEmpty;
        List<ConstructorNode> constructors = cNode.getDeclaredConstructors();
        if (constructors.size() > 1 && !force) {
            return;
        }
        boolean bl = foundEmpty = constructors.size() == 1 && constructors.get(0).getFirstStatement() == null;
        if (constructors.size() == 1 && !foundEmpty && !force) {
            return;
        }
        if (foundEmpty) {
            constructors.remove(0);
        }
        ArrayList<FieldNode> superList = new ArrayList<FieldNode>();
        if (includeSuperProperties) {
            superList.addAll(GeneralUtils.getSuperPropertyFields(cNode.getSuperClass()));
        }
        if (includeSuperFields) {
            superList.addAll(GeneralUtils.getSuperNonPropertyFields(cNode.getSuperClass()));
        }
        ArrayList<FieldNode> list = new ArrayList<FieldNode>();
        if (includeProperties) {
            list.addAll(GeneralUtils.getInstancePropertyFields(cNode));
        }
        if (includeFields) {
            list.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
        }
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        ArrayList<Expression> superParams = new ArrayList<Expression>();
        BlockStatement body = new BlockStatement();
        for (FieldNode fNode : superList) {
            name = fNode.getName();
            if (TupleConstructorASTTransformation.shouldSkip(name, excludes, includes)) continue;
            params.add(TupleConstructorASTTransformation.createParam(fNode, name));
            if (callSuper) {
                superParams.add(GeneralUtils.varX(name));
                continue;
            }
            body.addStatement(GeneralUtils.assignS(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), name), GeneralUtils.varX(name)));
        }
        if (callSuper) {
            body.addStatement(GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.SUPER, GeneralUtils.args(superParams))));
        }
        for (FieldNode fNode : list) {
            name = fNode.getName();
            if (TupleConstructorASTTransformation.shouldSkip(name, excludes, includes)) continue;
            Parameter nextParam = TupleConstructorASTTransformation.createParam(fNode, name);
            params.add(nextParam);
            body.addStatement(GeneralUtils.assignS(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), name), GeneralUtils.varX(nextParam)));
        }
        cNode.addConstructor(new ConstructorNode(1, params.toArray(new Parameter[params.size()]), ClassNode.EMPTY_ARRAY, body));
        if (!params.isEmpty()) {
            ClassNode firstParam = ((Parameter)params.get(0)).getType();
            if (params.size() > 1 || firstParam.equals(ClassHelper.OBJECT_TYPE)) {
                if (firstParam.equals(ClassHelper.MAP_TYPE)) {
                    TupleConstructorASTTransformation.addMapConstructors(cNode, true, "The class " + cNode.getName() + " was incorrectly initialized via the map constructor with null.");
                } else {
                    for (ClassNode candidate = HMAP_TYPE; candidate != null; candidate = candidate.getSuperClass()) {
                        if (!candidate.equals(firstParam)) continue;
                        TupleConstructorASTTransformation.addMapConstructors(cNode, true, "The class " + cNode.getName() + " was incorrectly initialized via the map constructor with null.");
                        break;
                    }
                }
            }
        }
    }

    private static Parameter createParam(FieldNode fNode, String name) {
        Parameter param = new Parameter(fNode.getType(), name);
        param.setInitialExpression(TupleConstructorASTTransformation.providedOrDefaultInitialValue(fNode));
        return param;
    }

    private static Expression providedOrDefaultInitialValue(FieldNode fNode) {
        Expression initialExp = fNode.getInitialExpression() != null ? fNode.getInitialExpression() : ConstantExpression.NULL;
        ClassNode paramType = fNode.getType();
        if (ClassHelper.isPrimitiveType(paramType) && initialExp.equals(ConstantExpression.NULL)) {
            initialExp = primitivesInitialValues.get(paramType.getTypeClass());
        }
        return initialExp;
    }

    public static void addMapConstructors(ClassNode cNode, boolean hasNoArg, String message) {
        Parameter[] parameters = GeneralUtils.params(new Parameter(LHMAP_TYPE, "__namedArgs"));
        BlockStatement code = new BlockStatement();
        VariableExpression namedArgs = GeneralUtils.varX("__namedArgs");
        namedArgs.setAccessedVariable(parameters[0]);
        code.addStatement(GeneralUtils.ifElseS(GeneralUtils.equalsNullX(namedArgs), TupleConstructorASTTransformation.illegalArgumentBlock(message), TupleConstructorASTTransformation.processArgsBlock(cNode, namedArgs)));
        ConstructorNode init = new ConstructorNode(1, parameters, ClassNode.EMPTY_ARRAY, code);
        cNode.addConstructor(init);
        if (!hasNoArg) {
            code = new BlockStatement();
            code.addStatement(GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.THIS, GeneralUtils.ctorX(LHMAP_TYPE))));
            init = new ConstructorNode(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, code);
            cNode.addConstructor(init);
        }
    }

    private static BlockStatement illegalArgumentBlock(String message) {
        return GeneralUtils.block(GeneralUtils.throwS(GeneralUtils.ctorX(ClassHelper.make(IllegalArgumentException.class), GeneralUtils.args(GeneralUtils.constX(message)))));
    }

    private static BlockStatement processArgsBlock(ClassNode cNode, VariableExpression namedArgs) {
        BlockStatement block = new BlockStatement();
        for (PropertyNode pNode : cNode.getProperties()) {
            if (pNode.isStatic()) continue;
            Statement ifStatement = GeneralUtils.ifS((Expression)GeneralUtils.callX((Expression)namedArgs, "containsKey", (Expression)GeneralUtils.constX(pNode.getName())), GeneralUtils.assignS(GeneralUtils.varX(pNode), GeneralUtils.propX((Expression)namedArgs, pNode.getName())));
            block.addStatement(ifStatement);
        }
        block.addStatement(GeneralUtils.stmt(GeneralUtils.callX(CHECK_METHOD_TYPE, "checkPropNames", (Expression)GeneralUtils.args(GeneralUtils.varX("this"), namedArgs))));
        return block;
    }

    static {
        ConstantExpression zero = GeneralUtils.constX(0);
        ConstantExpression zeroDecimal = GeneralUtils.constX(0.0);
        primitivesInitialValues = new HashMap();
        primitivesInitialValues.put(Integer.TYPE, zero);
        primitivesInitialValues.put(Long.TYPE, zero);
        primitivesInitialValues.put(Short.TYPE, zero);
        primitivesInitialValues.put(Byte.TYPE, zero);
        primitivesInitialValues.put(Character.TYPE, zero);
        primitivesInitialValues.put(Float.TYPE, zeroDecimal);
        primitivesInitialValues.put(Double.TYPE, zeroDecimal);
        primitivesInitialValues.put(Boolean.TYPE, ConstantExpression.FALSE);
    }
}

