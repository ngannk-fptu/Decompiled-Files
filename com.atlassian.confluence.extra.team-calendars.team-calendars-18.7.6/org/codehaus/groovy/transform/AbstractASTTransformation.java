/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.Undefined;
import groovyjarjarasm.asm.Opcodes;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;

public abstract class AbstractASTTransformation
implements Opcodes,
ASTTransformation {
    public static final ClassNode RETENTION_CLASSNODE = ClassHelper.makeWithoutCaching(Retention.class);
    protected SourceUnit sourceUnit;

    protected List<AnnotationNode> copyAnnotatedNodeAnnotations(AnnotatedNode annotatedNode, String myTypeName) {
        ArrayList<AnnotationNode> copiedAnnotations = new ArrayList<AnnotationNode>();
        ArrayList<AnnotationNode> notCopied = new ArrayList<AnnotationNode>();
        GeneralUtils.copyAnnotatedNodeAnnotations(annotatedNode, copiedAnnotations, notCopied);
        for (AnnotationNode annotation : notCopied) {
            this.addError(myTypeName + " does not support keeping Closure annotation members.", annotation);
        }
        return copiedAnnotations;
    }

    protected void init(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (nodes == null || nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new GroovyBugError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + (nodes == null ? null : Arrays.asList(nodes)));
        }
        this.sourceUnit = sourceUnit;
    }

    public boolean memberHasValue(AnnotationNode node, String name, Object value) {
        Expression member = node.getMember(name);
        return member != null && member instanceof ConstantExpression && ((ConstantExpression)member).getValue().equals(value);
    }

    public Object getMemberValue(AnnotationNode node, String name) {
        Expression member = node.getMember(name);
        if (member != null && member instanceof ConstantExpression) {
            return ((ConstantExpression)member).getValue();
        }
        return null;
    }

    public static String getMemberStringValue(AnnotationNode node, String name, String defaultValue) {
        Expression member = node.getMember(name);
        if (member != null && member instanceof ConstantExpression) {
            Object result = ((ConstantExpression)member).getValue();
            if (result != null && result instanceof String && Undefined.isUndefined((String)result)) {
                result = null;
            }
            if (result != null) {
                return result.toString();
            }
        }
        return defaultValue;
    }

    public static String getMemberStringValue(AnnotationNode node, String name) {
        return AbstractASTTransformation.getMemberStringValue(node, name, null);
    }

    public int getMemberIntValue(AnnotationNode node, String name) {
        Object value = this.getMemberValue(node, name);
        if (value != null && value instanceof Integer) {
            return (Integer)value;
        }
        return 0;
    }

    public ClassNode getMemberClassValue(AnnotationNode node, String name) {
        return this.getMemberClassValue(node, name, null);
    }

    public ClassNode getMemberClassValue(AnnotationNode node, String name, ClassNode defaultValue) {
        Expression member = node.getMember(name);
        if (member != null) {
            if (member instanceof ClassExpression) {
                if (!Undefined.isUndefined(member.getType())) {
                    return member.getType();
                }
            } else {
                if (member instanceof VariableExpression) {
                    this.addError("Error expecting to find class value for '" + name + "' but found variable: " + member.getText() + ". Missing import?", node);
                    return null;
                }
                if (member instanceof ConstantExpression) {
                    this.addError("Error expecting to find class value for '" + name + "' but found constant: " + member.getText() + "!", node);
                    return null;
                }
            }
        }
        return defaultValue;
    }

    public static List<String> getMemberList(AnnotationNode anno, String name) {
        List<String> list;
        Expression expr = anno.getMember(name);
        if (expr != null && expr instanceof ListExpression) {
            list = new ArrayList<String>();
            ListExpression listExpression = (ListExpression)expr;
            for (Expression itemExpr : listExpression.getExpressions()) {
                Object value;
                if (itemExpr == null || !(itemExpr instanceof ConstantExpression) || (value = ((ConstantExpression)itemExpr).getValue()) == null) continue;
                list.add(value.toString());
            }
        } else {
            list = AbstractASTTransformation.tokenize(AbstractASTTransformation.getMemberStringValue(anno, name));
        }
        return list;
    }

    public List<ClassNode> getClassList(AnnotationNode anno, String name) {
        ClassNode cn;
        ArrayList<ClassNode> list = new ArrayList<ClassNode>();
        Expression expr = anno.getMember(name);
        if (expr != null && expr instanceof ListExpression) {
            ListExpression listExpression = (ListExpression)expr;
            for (Expression itemExpr : listExpression.getExpressions()) {
                ClassNode cn2;
                if (itemExpr == null || !(itemExpr instanceof ClassExpression) || (cn2 = itemExpr.getType()) == null) continue;
                list.add(cn2);
            }
        } else if (expr != null && expr instanceof ClassExpression && (cn = expr.getType()) != null) {
            list.add(cn);
        }
        return list;
    }

    public void addError(String msg, ASTNode expr) {
        this.sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), this.sourceUnit));
    }

    protected boolean checkNotInterface(ClassNode cNode, String annotationName) {
        if (cNode.isInterface()) {
            this.addError("Error processing interface '" + cNode.getName() + "'. " + annotationName + " not allowed for interfaces.", cNode);
            return false;
        }
        return true;
    }

    public boolean hasAnnotation(ClassNode cNode, ClassNode annotation) {
        List<AnnotationNode> annots = cNode.getAnnotations(annotation);
        return annots != null && !annots.isEmpty();
    }

    public static List<String> tokenize(String rawExcludes) {
        return rawExcludes == null ? new ArrayList() : StringGroovyMethods.tokenize(rawExcludes, ", ");
    }

    public static boolean deemedInternalName(String name) {
        return name.contains("$");
    }

    public static boolean shouldSkip(String name, List<String> excludes, List<String> includes) {
        return excludes != null && excludes.contains(name) || AbstractASTTransformation.deemedInternalName(name) || includes != null && !includes.isEmpty() && !includes.contains(name);
    }

    public static boolean shouldSkipOnDescriptor(boolean checkReturn, Map genericsSpec, MethodNode mNode, List<ClassNode> excludeTypes, List<ClassNode> includeTypes) {
        String md;
        MethodNode correctedMethodNode;
        ClassNode next;
        Map<String, ClassNode> updatedGenericsSpec;
        LinkedList<ClassNode> remaining;
        String descriptor = mNode.getTypeDescriptor();
        String descriptorNoReturn = GeneralUtils.makeDescriptorWithoutReturnType(mNode);
        for (ClassNode cn : excludeTypes) {
            remaining = new LinkedList<ClassNode>();
            remaining.add(cn);
            updatedGenericsSpec = new HashMap<String, ClassNode>(genericsSpec);
            while (!remaining.isEmpty()) {
                next = (ClassNode)remaining.remove(0);
                if (next.equals(ClassHelper.OBJECT_TYPE)) continue;
                updatedGenericsSpec = GenericsUtils.createGenericsSpec(next, updatedGenericsSpec);
                for (MethodNode mn : next.getMethods()) {
                    correctedMethodNode = GenericsUtils.correctToGenericsSpec(updatedGenericsSpec, mn);
                    if (!(checkReturn ? (md = correctedMethodNode.getTypeDescriptor()).equals(descriptor) : (md = GeneralUtils.makeDescriptorWithoutReturnType(correctedMethodNode)).equals(descriptorNoReturn))) continue;
                    return true;
                }
                remaining.addAll(Arrays.asList(next.getInterfaces()));
            }
        }
        if (includeTypes.isEmpty()) {
            return false;
        }
        for (ClassNode cn : includeTypes) {
            remaining = new LinkedList();
            remaining.add(cn);
            updatedGenericsSpec = new HashMap(genericsSpec);
            while (!remaining.isEmpty()) {
                next = (ClassNode)remaining.remove(0);
                if (next.equals(ClassHelper.OBJECT_TYPE)) continue;
                updatedGenericsSpec = GenericsUtils.createGenericsSpec(next, updatedGenericsSpec);
                for (MethodNode mn : next.getMethods()) {
                    correctedMethodNode = GenericsUtils.correctToGenericsSpec(updatedGenericsSpec, mn);
                    if (!(checkReturn ? (md = correctedMethodNode.getTypeDescriptor()).equals(descriptor) : (md = GeneralUtils.makeDescriptorWithoutReturnType(correctedMethodNode)).equals(descriptorNoReturn))) continue;
                    return false;
                }
                remaining.addAll(Arrays.asList(next.getInterfaces()));
            }
        }
        return true;
    }

    protected boolean checkIncludeExclude(AnnotationNode node, List<String> excludes, List<String> includes, String typeName) {
        if (includes != null && !includes.isEmpty() && excludes != null && !excludes.isEmpty()) {
            this.addError("Error during " + typeName + " processing: Only one of 'includes' and 'excludes' should be supplied not both.", node);
            return false;
        }
        return true;
    }

    protected void checkIncludeExclude(AnnotationNode node, List<String> excludes, List<String> includes, List<ClassNode> excludeTypes, List<ClassNode> includeTypes, String typeName) {
        int found = 0;
        if (includes != null && !includes.isEmpty()) {
            ++found;
        }
        if (excludes != null && !excludes.isEmpty()) {
            ++found;
        }
        if (includeTypes != null && !includeTypes.isEmpty()) {
            ++found;
        }
        if (excludeTypes != null && !excludeTypes.isEmpty()) {
            ++found;
        }
        if (found > 1) {
            this.addError("Error during " + typeName + " processing: Only one of 'includes', 'excludes', 'includeTypes' and 'excludeTypes' should be supplied.", node);
        }
    }

    @Deprecated
    public static ClassNode nonGeneric(ClassNode type) {
        return GenericsUtils.nonGeneric(type);
    }
}

