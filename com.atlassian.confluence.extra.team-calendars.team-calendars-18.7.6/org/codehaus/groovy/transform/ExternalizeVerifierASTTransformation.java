/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.ExternalizeVerifier;
import java.io.Externalizable;
import java.io.Serializable;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CLASS_GENERATION)
public class ExternalizeVerifierASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = ExternalizeVerifier.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode EXTERNALIZABLE_TYPE = ClassHelper.make(Externalizable.class);
    private static final ClassNode SERIALIZABLE_TYPE = ClassHelper.make(Serializable.class);

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
            if (!this.hasNoargConstructor(cNode)) {
                this.addError(MY_TYPE_NAME + ": An Externalizable class requires a no-arg constructor but none found", cNode);
            }
            if (!this.implementsExternalizable(cNode)) {
                this.addError(MY_TYPE_NAME + ": An Externalizable class must implement the Externalizable interface", cNode);
            }
            boolean includeFields = this.memberHasValue(anno, "includeFields", true);
            boolean checkPropertyTypes = this.memberHasValue(anno, "checkPropertyTypes", true);
            List<String> excludes = ExternalizeVerifierASTTransformation.getMemberList(anno, "excludes");
            List<FieldNode> list = GeneralUtils.getInstancePropertyFields(cNode);
            if (includeFields) {
                list.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
            }
            this.checkProps(list, excludes, checkPropertyTypes);
        }
    }

    private void checkProps(List<FieldNode> list, List<String> excludes, boolean checkPropertyTypes) {
        for (FieldNode fNode : list) {
            if (excludes.contains(fNode.getName()) || (fNode.getModifiers() & 0x80) != 0) continue;
            if ((fNode.getModifiers() & 0x10) != 0) {
                this.addError(MY_TYPE_NAME + ": The Externalizable property (or field) '" + fNode.getName() + "' cannot be final", fNode);
            }
            ClassNode propType = fNode.getType();
            if (!checkPropertyTypes || ClassHelper.isPrimitiveType(propType) || this.implementsExternalizable(propType) || this.implementsSerializable(propType)) continue;
            this.addError(MY_TYPE_NAME + ": strict type checking is enabled and the non-primitive property (or field) '" + fNode.getName() + "' in an Externalizable class has the type '" + propType.getName() + "' which isn't Externalizable or Serializable", fNode);
        }
    }

    private boolean implementsExternalizable(ClassNode cNode) {
        return cNode.implementsInterface(EXTERNALIZABLE_TYPE);
    }

    private boolean implementsSerializable(ClassNode cNode) {
        return cNode.implementsInterface(SERIALIZABLE_TYPE);
    }

    private boolean hasNoargConstructor(ClassNode cNode) {
        List<ConstructorNode> constructors = cNode.getDeclaredConstructors();
        for (ConstructorNode next : constructors) {
            if (next.getParameters().length != 0) continue;
            return true;
        }
        return false;
    }
}

