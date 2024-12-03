/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.AnnotationConstantExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class AnnotationVisitor {
    private SourceUnit source;
    private ErrorCollector errorCollector;
    private AnnotationNode annotation;
    private ClassNode reportClass;

    public AnnotationVisitor(SourceUnit source, ErrorCollector errorCollector) {
        this.source = source;
        this.errorCollector = errorCollector;
    }

    public void setReportClass(ClassNode cn) {
        this.reportClass = cn;
    }

    public AnnotationNode visit(AnnotationNode node) {
        this.annotation = node;
        this.reportClass = node.getClassNode();
        if (!AnnotationVisitor.isValidAnnotationClass(node.getClassNode())) {
            this.addError("class " + node.getClassNode().getName() + " is not an annotation");
            return node;
        }
        if (!this.checkIfMandatoryAnnotationValuesPassed(node)) {
            return node;
        }
        if (!this.checkIfValidEnumConstsAreUsed(node)) {
            return node;
        }
        Map<String, Expression> attributes = node.getMembers();
        for (Map.Entry<String, Expression> entry : attributes.entrySet()) {
            String attrName = entry.getKey();
            Expression attrExpr = this.transformInlineConstants(entry.getValue());
            entry.setValue(attrExpr);
            ClassNode attrType = this.getAttributeType(node, attrName);
            this.visitExpression(attrName, attrExpr, attrType);
        }
        VMPluginFactory.getPlugin().configureAnnotation(node);
        return this.annotation;
    }

    private boolean checkIfValidEnumConstsAreUsed(AnnotationNode node) {
        Map<String, Expression> attributes = node.getMembers();
        for (Map.Entry<String, Expression> entry : attributes.entrySet()) {
            if (this.validateEnumConstant(entry.getValue())) continue;
            return false;
        }
        return true;
    }

    private boolean validateEnumConstant(Expression exp) {
        if (exp instanceof PropertyExpression) {
            ClassExpression ce;
            ClassNode type;
            PropertyExpression pe = (PropertyExpression)exp;
            String name = pe.getPropertyAsString();
            if (pe.getObjectExpression() instanceof ClassExpression && name != null && (type = (ce = (ClassExpression)pe.getObjectExpression()).getType()).isEnum()) {
                boolean ok = false;
                try {
                    FieldNode enumField = type.getDeclaredField(name);
                    ok = enumField != null && enumField.getType().equals(type);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (!ok) {
                    this.addError("No enum const " + type.getName() + "." + name, pe);
                    return false;
                }
            }
        }
        return true;
    }

    private Expression transformInlineConstants(Expression exp) {
        if (exp instanceof PropertyExpression) {
            PropertyExpression pe = (PropertyExpression)exp;
            if (pe.getObjectExpression() instanceof ClassExpression) {
                ClassExpression ce = (ClassExpression)pe.getObjectExpression();
                ClassNode type = ce.getType();
                if (type.isEnum() || !type.isResolved()) {
                    return exp;
                }
                try {
                    Field field = type.getTypeClass().getField(pe.getPropertyAsString());
                    if (field != null && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        return new ConstantExpression(field.get(null));
                    }
                }
                catch (Exception field) {}
            }
        } else if (exp instanceof ListExpression) {
            ListExpression le = (ListExpression)exp;
            ListExpression result = new ListExpression();
            for (Expression e : le.getExpressions()) {
                result.addExpression(this.transformInlineConstants(e));
            }
            return result;
        }
        return exp;
    }

    private boolean checkIfMandatoryAnnotationValuesPassed(AnnotationNode node) {
        boolean ok = true;
        Map<String, Expression> attributes = node.getMembers();
        ClassNode classNode = node.getClassNode();
        for (MethodNode mn : classNode.getMethods()) {
            String methodName = mn.getName();
            if (mn.getCode() != null || attributes.containsKey(methodName)) continue;
            this.addError("No explicit/default value found for annotation attribute '" + methodName + "'", node);
            ok = false;
        }
        return ok;
    }

    private ClassNode getAttributeType(AnnotationNode node, String attrName) {
        ClassNode classNode = node.getClassNode();
        List<MethodNode> methods = classNode.getMethods(attrName);
        if (methods.isEmpty()) {
            this.addError("'" + attrName + "'is not part of the annotation " + classNode, node);
            return ClassHelper.OBJECT_TYPE;
        }
        MethodNode method = methods.get(0);
        return method.getReturnType();
    }

    private static boolean isValidAnnotationClass(ClassNode node) {
        return node.implementsInterface(ClassHelper.Annotation_TYPE);
    }

    protected void visitExpression(String attrName, Expression attrExp, ClassNode attrType) {
        if (attrType.isArray()) {
            if (attrExp instanceof ListExpression) {
                ListExpression le = (ListExpression)attrExp;
                this.visitListExpression(attrName, le, attrType.getComponentType());
            } else if (attrExp instanceof ClosureExpression) {
                this.addError("Annotation list attributes must use Groovy notation [el1, el2]", attrExp);
            } else {
                ListExpression listExp = new ListExpression();
                listExp.addExpression(attrExp);
                if (this.annotation != null) {
                    this.annotation.setMember(attrName, listExp);
                }
                this.visitExpression(attrName, listExp, attrType);
            }
        } else if (ClassHelper.isPrimitiveType(attrType)) {
            this.visitConstantExpression(attrName, this.getConstantExpression(attrExp, attrType), ClassHelper.getWrapper(attrType));
        } else if (ClassHelper.STRING_TYPE.equals(attrType)) {
            this.visitConstantExpression(attrName, this.getConstantExpression(attrExp, attrType), ClassHelper.STRING_TYPE);
        } else if (ClassHelper.CLASS_Type.equals(attrType)) {
            if (!(attrExp instanceof ClassExpression) && !(attrExp instanceof ClosureExpression)) {
                this.addError("Only classes and closures can be used for attribute '" + attrName + "'", attrExp);
            }
        } else if (attrType.isDerivedFrom(ClassHelper.Enum_Type)) {
            if (attrExp instanceof PropertyExpression) {
                this.visitEnumExpression(attrName, (PropertyExpression)attrExp, attrType);
            } else {
                this.addError("Expected enum value for attribute " + attrName, attrExp);
            }
        } else if (AnnotationVisitor.isValidAnnotationClass(attrType)) {
            if (attrExp instanceof AnnotationConstantExpression) {
                this.visitAnnotationExpression(attrName, (AnnotationConstantExpression)attrExp, attrType);
            } else {
                this.addError("Expected annotation of type '" + attrType.getName() + "' for attribute " + attrName, attrExp);
            }
        } else {
            this.addError("Unexpected type " + attrType.getName(), attrExp);
        }
    }

    public void checkReturnType(ClassNode attrType, ASTNode node) {
        if (attrType.isArray()) {
            this.checkReturnType(attrType.getComponentType(), node);
        } else {
            if (ClassHelper.isPrimitiveType(attrType)) {
                return;
            }
            if (ClassHelper.STRING_TYPE.equals(attrType)) {
                return;
            }
            if (ClassHelper.CLASS_Type.equals(attrType)) {
                return;
            }
            if (attrType.isDerivedFrom(ClassHelper.Enum_Type)) {
                return;
            }
            if (AnnotationVisitor.isValidAnnotationClass(attrType)) {
                return;
            }
            this.addError("Unexpected return type " + attrType.getName(), node);
        }
    }

    private ConstantExpression getConstantExpression(Expression exp, ClassNode attrType) {
        if (exp instanceof ConstantExpression) {
            return (ConstantExpression)exp;
        }
        String base = "Expected '" + exp.getText() + "' to be an inline constant of type " + attrType.getName();
        if (exp instanceof PropertyExpression) {
            this.addError(base + " not a property expression", exp);
        } else if (exp instanceof VariableExpression && ((VariableExpression)exp).getAccessedVariable() instanceof FieldNode) {
            this.addError(base + " not a field expression", exp);
        } else {
            this.addError(base, exp);
        }
        return ConstantExpression.EMPTY_EXPRESSION;
    }

    protected void visitAnnotationExpression(String attrName, AnnotationConstantExpression expression, ClassNode attrType) {
        AnnotationNode annotationNode = (AnnotationNode)expression.getValue();
        AnnotationVisitor visitor = new AnnotationVisitor(this.source, this.errorCollector);
        visitor.visit(annotationNode);
    }

    protected void visitListExpression(String attrName, ListExpression listExpr, ClassNode elementType) {
        for (Expression expression : listExpr.getExpressions()) {
            this.visitExpression(attrName, expression, elementType);
        }
    }

    protected void visitConstantExpression(String attrName, ConstantExpression constExpr, ClassNode attrType) {
        ClassNode constType = constExpr.getType();
        ClassNode wrapperType = ClassHelper.getWrapper(constType);
        if (!AnnotationVisitor.hasCompatibleType(attrType, wrapperType)) {
            this.addError("Attribute '" + attrName + "' should have type '" + attrType.getName() + "'; but found type '" + constType.getName() + "'", constExpr);
        }
    }

    private static boolean hasCompatibleType(ClassNode attrType, ClassNode wrapperType) {
        return wrapperType.isDerivedFrom(ClassHelper.getWrapper(attrType));
    }

    protected void visitEnumExpression(String attrName, PropertyExpression propExpr, ClassNode attrType) {
        if (!propExpr.getObjectExpression().getType().isDerivedFrom(attrType)) {
            this.addError("Attribute '" + attrName + "' should have type '" + attrType.getName() + "' (Enum), but found " + propExpr.getObjectExpression().getType().getName(), propExpr);
        }
    }

    protected void addError(String msg) {
        this.addError(msg, this.annotation);
    }

    protected void addError(String msg, ASTNode expr) {
        this.errorCollector.addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + " in @" + this.reportClass.getName() + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), this.source));
    }

    public void checkCircularReference(ClassNode searchClass, ClassNode attrType, Expression startExp) {
        if (!AnnotationVisitor.isValidAnnotationClass(attrType)) {
            return;
        }
        if (!(startExp instanceof AnnotationConstantExpression)) {
            this.addError("Found '" + startExp.getText() + "' when expecting an Annotation Constant", startExp);
            return;
        }
        AnnotationConstantExpression ace = (AnnotationConstantExpression)startExp;
        AnnotationNode annotationNode = (AnnotationNode)ace.getValue();
        if (annotationNode.getClassNode().equals(searchClass)) {
            this.addError("Circular reference discovered in " + searchClass.getName(), startExp);
            return;
        }
        ClassNode cn = annotationNode.getClassNode();
        for (MethodNode method : cn.getMethods()) {
            ReturnStatement code;
            if (method.getReturnType().equals(searchClass)) {
                this.addError("Circular reference discovered in " + cn.getName(), startExp);
            }
            if ((code = (ReturnStatement)method.getCode()) == null) continue;
            this.checkCircularReference(searchClass, method.getReturnType(), code.getExpression());
        }
    }
}

