/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.Bindable;
import groovy.beans.VetoableASTTransformation;
import groovyjarjarasm.asm.Opcodes;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class BindableASTTransformation
implements ASTTransformation,
Opcodes {
    protected static ClassNode boundClassNode = ClassHelper.make(Bindable.class);

    public static boolean hasBindableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (!boundClassNode.equals(annotation.getClassNode())) continue;
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
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        if (VetoableASTTransformation.hasVetoableAnnotation(parent)) {
            return;
        }
        ClassNode declaringClass = parent.getDeclaringClass();
        if (parent instanceof FieldNode) {
            if ((((FieldNode)parent).getModifiers() & 0x10) != 0) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Bindable cannot annotate a final property.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
            }
            if (VetoableASTTransformation.hasVetoableAnnotation(parent.getDeclaringClass())) {
                return;
            }
            this.addListenerToProperty(source, node, declaringClass, (FieldNode)parent);
        } else if (parent instanceof ClassNode) {
            this.addListenerToClass(source, (ClassNode)parent);
        }
    }

    private void addListenerToProperty(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field) {
        String fieldName = field.getName();
        for (PropertyNode propertyNode : declaringClass.getProperties()) {
            if (!propertyNode.getName().equals(fieldName)) continue;
            if (field.isStatic()) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Bindable cannot annotate a static property.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
            } else {
                if (this.needsPropertyChangeSupport(declaringClass, source)) {
                    this.addPropertyChangeSupport(declaringClass);
                }
                this.createListenerSetter(declaringClass, propertyNode);
            }
            return;
        }
        source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.beans.Bindable must be on a property, not a field.  Try removing the private, protected, or public modifier.", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source));
    }

    private void addListenerToClass(SourceUnit source, ClassNode classNode) {
        if (this.needsPropertyChangeSupport(classNode, source)) {
            this.addPropertyChangeSupport(classNode);
        }
        for (PropertyNode propertyNode : classNode.getProperties()) {
            FieldNode field = propertyNode.getField();
            if (BindableASTTransformation.hasBindableAnnotation(field) || (field.getModifiers() & 0x10) != 0 || field.isStatic() || VetoableASTTransformation.hasVetoableAnnotation(field)) continue;
            this.createListenerSetter(classNode, propertyNode);
        }
    }

    private static void wrapSetterMethod(ClassNode classNode, String propertyName) {
        String getterName = "get" + MetaClassHelper.capitalize(propertyName);
        MethodNode setter = classNode.getSetterMethod("set" + MetaClassHelper.capitalize(propertyName));
        if (setter != null) {
            Statement code = setter.getCode();
            VariableExpression oldValue = GeneralUtils.varX("$oldValue");
            VariableExpression newValue = GeneralUtils.varX("$newValue");
            BlockStatement block = new BlockStatement();
            block.addStatement(GeneralUtils.declS(oldValue, GeneralUtils.callThisX(getterName)));
            block.addStatement(code);
            block.addStatement(GeneralUtils.declS(newValue, GeneralUtils.callThisX(getterName)));
            block.addStatement(GeneralUtils.stmt(GeneralUtils.callThisX("firePropertyChange", GeneralUtils.args(GeneralUtils.constX(propertyName), oldValue, newValue))));
            setter.setCode(block);
        }
    }

    private void createListenerSetter(ClassNode classNode, PropertyNode propertyNode) {
        String setterName = "set" + MetaClassHelper.capitalize(propertyNode.getName());
        if (classNode.getMethods(setterName).isEmpty()) {
            Statement setterBlock = this.createBindableStatement(propertyNode, GeneralUtils.fieldX(propertyNode.getField()));
            this.createSetterMethod(classNode, propertyNode, setterName, setterBlock);
        } else {
            BindableASTTransformation.wrapSetterMethod(classNode, propertyNode.getName());
        }
    }

    protected Statement createBindableStatement(PropertyNode propertyNode, Expression fieldExpression) {
        return GeneralUtils.stmt(GeneralUtils.callThisX("firePropertyChange", GeneralUtils.args(GeneralUtils.constX(propertyNode.getName()), fieldExpression, GeneralUtils.assignX(fieldExpression, GeneralUtils.varX("value")))));
    }

    protected void createSetterMethod(ClassNode declaringClass, PropertyNode propertyNode, String setterName, Statement setterBlock) {
        MethodNode setter = new MethodNode(setterName, PropertyNodeUtils.adjustPropertyModifiersForMethod(propertyNode), ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(propertyNode.getType(), "value")), ClassNode.EMPTY_ARRAY, setterBlock);
        setter.setSynthetic(true);
        declaringClass.addMethod(setter);
    }

    protected boolean needsPropertyChangeSupport(ClassNode declaringClass, SourceUnit sourceUnit) {
        ClassNode consideredClass;
        boolean foundAdd = false;
        boolean foundRemove = false;
        boolean foundFire = false;
        for (consideredClass = declaringClass; consideredClass != null; consideredClass = consideredClass.getSuperClass()) {
            for (MethodNode method : consideredClass.getMethods()) {
                foundAdd = foundAdd || method.getName().equals("addPropertyChangeListener") && method.getParameters().length == 1;
                foundRemove = foundRemove || method.getName().equals("removePropertyChangeListener") && method.getParameters().length == 1;
                boolean bl = foundFire = foundFire || method.getName().equals("firePropertyChange") && method.getParameters().length == 3;
                if (!foundAdd || !foundRemove || !foundFire) continue;
                return false;
            }
        }
        for (consideredClass = declaringClass.getSuperClass(); consideredClass != null; consideredClass = consideredClass.getSuperClass()) {
            if (BindableASTTransformation.hasBindableAnnotation(consideredClass)) {
                return false;
            }
            for (FieldNode field : consideredClass.getFields()) {
                if (!BindableASTTransformation.hasBindableAnnotation(field)) continue;
                return false;
            }
        }
        if (foundAdd || foundRemove || foundFire) {
            sourceUnit.getErrorCollector().addErrorAndContinue(new SimpleMessage("@Bindable cannot be processed on " + declaringClass.getName() + " because some but not all of addPropertyChangeListener, removePropertyChange, and firePropertyChange were declared in the current or super classes.", sourceUnit));
            return false;
        }
        return true;
    }

    protected void addPropertyChangeSupport(ClassNode declaringClass) {
        ClassNode pcsClassNode = ClassHelper.make(PropertyChangeSupport.class);
        ClassNode pclClassNode = ClassHelper.make(PropertyChangeListener.class);
        FieldNode pcsField = declaringClass.addField("this$propertyChangeSupport", 4114, pcsClassNode, GeneralUtils.ctorX(pcsClassNode, GeneralUtils.args(GeneralUtils.varX("this"))));
        declaringClass.addMethod(new MethodNode("addPropertyChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(pclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "addPropertyChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("listener", pclClassNode))))));
        declaringClass.addMethod(new MethodNode("addPropertyChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(pclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "addPropertyChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("listener", pclClassNode))))));
        declaringClass.addMethod(new MethodNode("removePropertyChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(pclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "removePropertyChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("listener", pclClassNode))))));
        declaringClass.addMethod(new MethodNode("removePropertyChangeListener", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(pclClassNode, "listener")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "removePropertyChangeListener", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("listener", pclClassNode))))));
        declaringClass.addMethod(new MethodNode("firePropertyChange", 1, ClassHelper.VOID_TYPE, GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name"), GeneralUtils.param(ClassHelper.OBJECT_TYPE, "oldValue"), GeneralUtils.param(ClassHelper.OBJECT_TYPE, "newValue")), ClassNode.EMPTY_ARRAY, GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "firePropertyChange", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE), GeneralUtils.varX("oldValue"), GeneralUtils.varX("newValue"))))));
        declaringClass.addMethod(new MethodNode("getPropertyChangeListeners", 1, pclClassNode.makeArray(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(GeneralUtils.callX(GeneralUtils.fieldX(pcsField), "getPropertyChangeListeners"))));
        declaringClass.addMethod(new MethodNode("getPropertyChangeListeners", 1, pclClassNode.makeArray(), GeneralUtils.params(GeneralUtils.param(ClassHelper.STRING_TYPE, "name")), ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(GeneralUtils.callX((Expression)GeneralUtils.fieldX(pcsField), "getPropertyChangeListeners", (Expression)GeneralUtils.args(GeneralUtils.varX("name", ClassHelper.STRING_TYPE))))));
    }
}

