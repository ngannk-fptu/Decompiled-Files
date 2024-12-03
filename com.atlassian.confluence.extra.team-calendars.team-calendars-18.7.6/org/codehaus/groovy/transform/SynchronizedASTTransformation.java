/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.Synchronized;
import java.util.Arrays;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class SynchronizedASTTransformation
extends AbstractASTTransformation {
    private static final Class MY_CLASS = Synchronized.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        String value = SynchronizedASTTransformation.getMemberStringValue(node, "value");
        if (parent instanceof MethodNode) {
            MethodNode mNode = (MethodNode)parent;
            if (mNode.isAbstract()) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: annotation not allowed on abstract method '" + mNode.getName() + "'", mNode);
                return;
            }
            ClassNode cNode = mNode.getDeclaringClass();
            String lockExpr = this.determineLock(value, cNode, mNode);
            if (lockExpr == null) {
                return;
            }
            Statement origCode = mNode.getCode();
            SynchronizedStatement newCode = new SynchronizedStatement(GeneralUtils.varX(lockExpr), origCode);
            mNode.setCode(newCode);
        }
    }

    private String determineLock(String value, ClassNode cNode, MethodNode mNode) {
        boolean isStatic = mNode.isStatic();
        if (value != null && value.length() > 0 && !value.equalsIgnoreCase("$lock")) {
            if (cNode.getDeclaredField(value) == null) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: lock field with name '" + value + "' not found in class " + cNode.getName(), mNode);
                return null;
            }
            FieldNode field = cNode.getDeclaredField(value);
            if (isStatic && !field.isStatic()) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: lock field with name '" + value + "' must be static for static method '" + mNode.getName() + "'", field);
                return null;
            }
            return value;
        }
        if (isStatic) {
            FieldNode field = cNode.getDeclaredField("$LOCK");
            if (field == null) {
                int visibility = 26;
                cNode.addField("$LOCK", visibility, ClassHelper.OBJECT_TYPE, this.zeroLengthObjectArray());
            } else if (!field.isStatic()) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: $LOCK field must be static", field);
            }
            return "$LOCK";
        }
        FieldNode field = cNode.getDeclaredField("$lock");
        if (field == null) {
            int visibility = 18;
            cNode.addField("$lock", visibility, ClassHelper.OBJECT_TYPE, this.zeroLengthObjectArray());
        } else if (field.isStatic()) {
            this.addError("Error during " + MY_TYPE_NAME + " processing: $lock field must not be static", field);
        }
        return "$lock";
    }

    private Expression zeroLengthObjectArray() {
        return new ArrayExpression(ClassHelper.OBJECT_TYPE, null, Arrays.asList(GeneralUtils.constX(0)));
    }
}

