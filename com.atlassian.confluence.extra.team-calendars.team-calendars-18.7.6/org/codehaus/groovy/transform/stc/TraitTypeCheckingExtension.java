/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.transform.stc.AbstractTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.UnionTypeClassNode;
import org.codehaus.groovy.transform.trait.TraitASTTransformation;
import org.codehaus.groovy.transform.trait.Traits;

public class TraitTypeCheckingExtension
extends AbstractTypeCheckingExtension {
    private static final List<MethodNode> NOTFOUND = Collections.emptyList();

    public TraitTypeCheckingExtension(StaticTypeCheckingVisitor typeCheckingVisitor) {
        super(typeCheckingVisitor);
    }

    @Override
    public void setup() {
    }

    @Override
    public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList, ClassNode[] argumentTypes, MethodCall call) {
        String[] decomposed = Traits.decomposeSuperCallName(name);
        if (decomposed != null) {
            return this.convertToDynamicCall(call, receiver, decomposed, argumentTypes);
        }
        if (call instanceof MethodCallExpression) {
            ClassNode dynamic;
            MethodCallExpression mce = (MethodCallExpression)call;
            if (mce.getReceiver() instanceof VariableExpression) {
                VariableExpression var = (VariableExpression)mce.getReceiver();
                ClassNode type = null;
                if (TraitTypeCheckingExtension.isStaticTraitReceiver(receiver, var)) {
                    type = receiver.getGenericsTypes()[0].getType();
                } else if (TraitTypeCheckingExtension.isThisTraitReceiver(var)) {
                    type = receiver;
                }
                if (type != null && Traits.isTrait(type) && !(type instanceof UnionTypeClassNode)) {
                    ClassNode helper = Traits.findHelper(type);
                    Parameter[] params = new Parameter[argumentTypes.length + 1];
                    params[0] = new Parameter(ClassHelper.CLASS_Type.getPlainNodeReference(), "staticSelf");
                    for (int i = 1; i < params.length; ++i) {
                        params[i] = new Parameter(argumentTypes[i - 1], "p" + i);
                    }
                    MethodNode method = helper.getDeclaredMethod(name, params);
                    if (method != null) {
                        return Collections.singletonList(this.makeDynamic(call, method.getReturnType()));
                    }
                }
            }
            if ((dynamic = (ClassNode)mce.getNodeMetaData(TraitASTTransformation.DO_DYNAMIC)) != null) {
                return Collections.singletonList(this.makeDynamic(call, dynamic));
            }
        }
        return NOTFOUND;
    }

    private static boolean isStaticTraitReceiver(ClassNode receiver, VariableExpression var) {
        return "$static$self".equals(var.getName()) && StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(receiver);
    }

    private static boolean isThisTraitReceiver(VariableExpression var) {
        return "$self".equals(var.getName());
    }

    private List<MethodNode> convertToDynamicCall(MethodCall call, ClassNode receiver, String[] decomposed, ClassNode[] argumentTypes) {
        String traitName = decomposed[0];
        String name = decomposed[1];
        LinkedHashSet<ClassNode> traitsAsList = Traits.collectAllInterfacesReverseOrder(receiver, new LinkedHashSet<ClassNode>());
        ClassNode[] implementedTraits = traitsAsList.toArray(new ClassNode[traitsAsList.size()]);
        ClassNode nextTrait = null;
        for (int i = 0; i < implementedTraits.length - 1; ++i) {
            ClassNode implementedTrait = implementedTraits[i];
            if (!implementedTrait.getName().equals(traitName)) continue;
            nextTrait = implementedTraits[i + 1];
        }
        ClassNode[] newArgs = new ClassNode[argumentTypes.length];
        System.arraycopy(argumentTypes, 0, newArgs, 0, newArgs.length);
        ClassNode inferredReturnType = this.inferTraitMethodReturnType(nextTrait, name, newArgs);
        return Arrays.asList(this.makeDynamic(call, inferredReturnType));
    }

    private ClassNode inferTraitMethodReturnType(ClassNode nextTrait, String methodName, ClassNode[] paramTypes) {
        List<MethodNode> candidates;
        ClassNode result = ClassHelper.OBJECT_TYPE;
        if (nextTrait != null && (candidates = this.typeCheckingVisitor.findMethod(nextTrait, methodName, paramTypes)).size() == 1) {
            result = candidates.get(0).getReturnType();
        }
        return result;
    }
}

