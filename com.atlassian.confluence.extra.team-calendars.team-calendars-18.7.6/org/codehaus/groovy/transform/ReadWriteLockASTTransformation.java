/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.WithReadLock;
import groovy.transform.WithWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ReadWriteLockASTTransformation
extends AbstractASTTransformation {
    private static final ClassNode READ_LOCK_TYPE = ClassHelper.make(WithReadLock.class);
    private static final ClassNode WRITE_LOCK_TYPE = ClassHelper.make(WithWriteLock.class);
    private static final ClassNode LOCK_TYPE = ClassHelper.make(ReentrantReadWriteLock.class);
    public static final String DEFAULT_STATIC_LOCKNAME = "$REENTRANTLOCK";
    public static final String DEFAULT_INSTANCE_LOCKNAME = "$reentrantlock";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        boolean isWriteLock;
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (READ_LOCK_TYPE.equals(node.getClassNode())) {
            isWriteLock = false;
        } else if (WRITE_LOCK_TYPE.equals(node.getClassNode())) {
            isWriteLock = true;
        } else {
            throw new GroovyBugError("Internal error: expecting [" + READ_LOCK_TYPE.getName() + ", " + WRITE_LOCK_TYPE.getName() + "] but got: " + node.getClassNode().getName());
        }
        String myTypeName = "@" + node.getClassNode().getNameWithoutPackage();
        String value = ReadWriteLockASTTransformation.getMemberStringValue(node, "value");
        if (parent instanceof MethodNode) {
            MethodNode mNode = (MethodNode)parent;
            ClassNode cNode = mNode.getDeclaringClass();
            String lockExpr = this.determineLock(value, cNode, mNode.isStatic(), myTypeName);
            if (lockExpr == null) {
                return;
            }
            MethodCallExpression lockType = isWriteLock ? GeneralUtils.callX(GeneralUtils.varX(lockExpr, LOCK_TYPE), "writeLock") : GeneralUtils.callX(GeneralUtils.varX(lockExpr, LOCK_TYPE), "readLock");
            MethodCallExpression acquireLock = GeneralUtils.callX(lockType, "lock");
            MethodCallExpression releaseLock = GeneralUtils.callX(lockType, "unlock");
            Statement originalCode = mNode.getCode();
            mNode.setCode(GeneralUtils.block(GeneralUtils.stmt(acquireLock), new TryCatchStatement(originalCode, GeneralUtils.stmt(releaseLock))));
        }
    }

    private String determineLock(String value, ClassNode targetClass, boolean isStatic, String myTypeName) {
        if (value != null && value.length() > 0 && !value.equalsIgnoreCase(DEFAULT_INSTANCE_LOCKNAME)) {
            FieldNode existingLockField = targetClass.getDeclaredField(value);
            if (existingLockField == null) {
                this.addError("Error during " + myTypeName + " processing: lock field with name '" + value + "' not found in class " + targetClass.getName(), targetClass);
                return null;
            }
            if (existingLockField.isStatic() != isStatic) {
                this.addError("Error during " + myTypeName + " processing: lock field with name '" + value + "' should " + (isStatic ? "" : "not ") + "be static", existingLockField);
                return null;
            }
            return value;
        }
        if (isStatic) {
            FieldNode field = targetClass.getDeclaredField(DEFAULT_STATIC_LOCKNAME);
            if (field == null) {
                int visibility = 26;
                targetClass.addField(DEFAULT_STATIC_LOCKNAME, visibility, LOCK_TYPE, this.createLockObject());
            } else if (!field.isStatic()) {
                this.addError("Error during " + myTypeName + " processing: " + DEFAULT_STATIC_LOCKNAME + " field must be static", field);
                return null;
            }
            return DEFAULT_STATIC_LOCKNAME;
        }
        FieldNode field = targetClass.getDeclaredField(DEFAULT_INSTANCE_LOCKNAME);
        if (field == null) {
            int visibility = 18;
            targetClass.addField(DEFAULT_INSTANCE_LOCKNAME, visibility, LOCK_TYPE, this.createLockObject());
        } else if (field.isStatic()) {
            this.addError("Error during " + myTypeName + " processing: " + DEFAULT_INSTANCE_LOCKNAME + " field must not be static", field);
            return null;
        }
        return DEFAULT_INSTANCE_LOCKNAME;
    }

    private Expression createLockObject() {
        return GeneralUtils.ctorX(LOCK_TYPE);
    }
}

