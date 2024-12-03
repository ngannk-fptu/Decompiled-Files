/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.BindableASTTransformation;
import groovy.beans.Vetoable;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.PropertyNodeUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class VetoableASTTransformation
extends BindableASTTransformation {
    protected static ClassNode constrainedClassNode = ClassHelper.make(Vetoable.class);

    public static boolean hasVetoableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (!constrainedClassNode.equals(annotation.getClassNode())) continue;
            return true;
        }
        return false;
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
        }
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (nodes[1] instanceof ClassNode) {
            this.addListenerToClass(source, (ClassNode)nodes[1]);
        } else {
            if ((((FieldNode)nodes[1]).getModifiers() & 0x10) != 0) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Vetoable cannot annotate a final property.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
            }
            this.addListenerToProperty(source, node, (AnnotatedNode)nodes[1]);
        }
    }

    private void addListenerToProperty(SourceUnit source, AnnotationNode node, AnnotatedNode parent) {
        ClassNode declaringClass = parent.getDeclaringClass();
        FieldNode field = (FieldNode)parent;
        String fieldName = field.getName();
        for (PropertyNode propertyNode : declaringClass.getProperties()) {
            boolean bindable;
            boolean bl = bindable = BindableASTTransformation.hasBindableAnnotation(parent) || BindableASTTransformation.hasBindableAnnotation(parent.getDeclaringClass());
            if (!propertyNode.getName().equals(fieldName)) continue;
            if (field.isStatic()) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Vetoable cannot annotate a static property.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
            } else {
                this.createListenerSetter(source, bindable, declaringClass, propertyNode);
            }
            return;
        }
        source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Vetoable must be on a property, not a field.  Try removing the private, protected, or public modifier.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
    }

    private void addListenerToClass(SourceUnit source, ClassNode classNode) {
        boolean bindable = BindableASTTransformation.hasBindableAnnotation(classNode);
        for (PropertyNode propertyNode : classNode.getProperties()) {
            if (VetoableASTTransformation.hasVetoableAnnotation(propertyNode.getField()) || propertyNode.getField().isFinal() || propertyNode.getField().isStatic()) continue;
            this.createListenerSetter(source, bindable || BindableASTTransformation.hasBindableAnnotation(propertyNode.getField()), classNode, propertyNode);
        }
    }

    private static void wrapSetterMethod(ClassNode classNode, boolean bindable, String propertyName) {
        String getterName = "get" + MetaClassHelper.capitalize(propertyName);
        MethodNode setter = classNode.getSetterMethod("set" + MetaClassHelper.capitalize(propertyName));
        if (setter != null) {
            Statement code = setter.getCode();
            VariableExpression oldValue = GeneralUtils.varX("$oldValue");
            VariableExpression newValue = GeneralUtils.varX("$newValue");
            VariableExpression proposedValue = GeneralUtils.varX(setter.getParameters()[0].getName());
            BlockStatement block = new BlockStatement();
            block.addStatement(GeneralUtils.declS(oldValue, GeneralUtils.callThisX(getterName)));
            block.addStatement(GeneralUtils.stmt(GeneralUtils.callThisX("fireVetoableChange", GeneralUtils.args(GeneralUtils.constX(propertyName), oldValue, proposedValue))));
            block.addStatement(code);
            if (bindable) {
                block.addStatement(GeneralUtils.declS(newValue, GeneralUtils.callThisX(getterName)));
                block.addStatement(GeneralUtils.stmt(GeneralUtils.callThisX("firePropertyChange", GeneralUtils.args(GeneralUtils.constX(propertyName), oldValue, newValue))));
            }
            setter.setCode(block);
        }
    }

    private void createListenerSetter(SourceUnit source, boolean bindable, ClassNode declaringClass, PropertyNode propertyNode) {
        String setterName;
        if (bindable && this.needsPropertyChangeSupport(declaringClass, source)) {
            this.addPropertyChangeSupport(declaringClass);
        }
        if (this.needsVetoableChangeSupport(declaringClass, source)) {
            this.addVetoableChangeSupport(declaringClass);
        }
        if (declaringClass.getMethods(setterName = "set" + MetaClassHelper.capitalize(propertyNode.getName())).isEmpty()) {
            FieldExpression fieldExpression = GeneralUtils.fieldX(propertyNode.getField());
            BlockStatement setterBlock = new BlockStatement();
            setterBlock.addStatement(this.createConstrainedStatement(propertyNode, fieldExpression));
            if (bindable) {
                setterBlock.addStatement(this.createBindableStatement(propertyNode, fieldExpression));
            } else {
                setterBlock.addStatement(this.createSetStatement(fieldExpression));
            }
            this.createSetterMethod(declaringClass, propertyNode, setterName, setterBlock);
        } else {
            VetoableASTTransformation.wrapSetterMethod(declaringClass, bindable, propertyNode.getName());
        }
    }

    protected Statement createConstrainedStatement(PropertyNode propertyNode, Expression fieldExpression) {
        return GeneralUtils.stmt(GeneralUtils.callThisX("fireVetoableChange", GeneralUtils.args(GeneralUtils.constX(propertyNode.getName()), fieldExpression, GeneralUtils.varX("value"))));
    }

    protected Statement createSetStatement(Expression fieldExpression) {
        return GeneralUtils.assignS(fieldExpression, GeneralUtils.varX("value"));
    }

    protected boolean needsVetoableChangeSupport(ClassNode declaringClass, SourceUnit sourceUnit) {
        ClassNode consideredClass;
        boolean foundAdd = false;
        boolean foundRemove = false;
        boolean foundFire = false;
        for (consideredClass = declaringClass; consideredClass != null; consideredClass = consideredClass.getSuperClass()) {
            for (MethodNode method : consideredClass.getMethods()) {
                foundAdd = foundAdd || method.getName().equals("addVetoableChangeListener") && method.getParameters().length == 1;
                foundRemove = foundRemove || method.getName().equals("removeVetoableChangeListener") && method.getParameters().length == 1;
                boolean bl = foundFire = foundFire || method.getName().equals("fireVetoableChange") && method.getParameters().length == 3;
                if (!foundAdd || !foundRemove || !foundFire) continue;
                return false;
            }
        }
        for (consideredClass = declaringClass.getSuperClass(); consideredClass != null; consideredClass = consideredClass.getSuperClass()) {
            if (VetoableASTTransformation.hasVetoableAnnotation(consideredClass)) {
                return false;
            }
            for (FieldNode field : consideredClass.getFields()) {
                if (!VetoableASTTransformation.hasVetoableAnnotation(field)) continue;
                return false;
            }
        }
        if (foundAdd || foundRemove || foundFire) {
            sourceUnit.getErrorCollector().addErrorAndContinue(new SimpleMessage("@Vetoable cannot be processed on " + declaringClass.getName() + " because some but not all of addVetoableChangeListener, removeVetoableChange, and fireVetoableChange were declared in the current or super classes.", sourceUnit));
            return false;
        }
        return true;
    }

    @Override
    protected void createSetterMethod(ClassNode declaringClass, PropertyNode propertyNode, String setterName, Statement setterBlock) {
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(PropertyVetoException.class)};
        MethodNode setter = new MethodNode(setterName, PropertyNodeUtils.adjustPropertyModifiersForMethod(propertyNode), ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(propertyNode.getType(), "value")), exceptions, setterBlock);
        setter.setSynthetic(true);
        declaringClass.addMethod(setter);
    }

    protected void addVetoableChangeSupport(ClassNode declaringClass) {
        ClassNode vcsClassNode = ClassHelper.make(VetoableChangeSupport.class);
        ClassNode vclClassNode = ClassHelper.make(VetoableChangeListener.class);
        FieldNode vcsField = declaringClass.addField("this$vetoableChangeSupport", 4114, vcsClassNode, GeneralUtils.ctorX(vcsClassNode, GeneralUtils.args(GeneralUtils.varX("this"))));
        declaringClass.addMethod(new MethodNode("addVetoableChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(vclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "addVetoableChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("listener", vclClassNode))))));
        declaringClass.addMethod(new MethodNode("addVetoableChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(vclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "addVetoableChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("listener", vclClassNode))))));
        declaringClass.addMethod(new MethodNode("removeVetoableChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(vclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "removeVetoableChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("listener", vclClassNode))))));
        declaringClass.addMethod(new MethodNode("removeVetoableChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(vclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "removeVetoableChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("listener", vclClassNode))))));
        declaringClass.addMethod(new MethodNode("fireVetoableChange", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(ClassHelper.OBJECT_TYPE, "oldValue"), GeneralUtils.param(ClassHelper.OBJECT_TYPE, "newValue")), new ClassNode[]{ClassHelper.make(PropertyVetoException.class)}, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "fireVetoableChange", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("oldValue"), GeneralUtils.varX("newValue"))))));
        declaringClass.addMethod(new MethodNode("getVetoableChangeListeners", 1, vclClassNode.makeArray(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(GeneralUtils.callX(GeneralUtils.fieldX(vcsField), "getVetoableChangeListeners"))));
        declaringClass.addMethod(new MethodNode("getVetoableChangeListeners", 1, vclClassNode.makeArray(), GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name")), ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(GeneralUtils.callX((Expression)GeneralUtils.fieldX(vcsField), "getVetoableChangeListeners", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE))))));
    }
}

