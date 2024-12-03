/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.IndexedProperty;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class IndexedPropertyASTTransformation
extends AbstractASTTransformation {
    private static final Class MY_CLASS = IndexedProperty.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode LIST_TYPE = ClassHelper.makeWithoutCaching(List.class, false);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        if (parent instanceof FieldNode) {
            FieldNode fNode = (FieldNode)parent;
            ClassNode cNode = fNode.getDeclaringClass();
            if (cNode.getProperty(fNode.getName()) == null) {
                this.addError("Error during " + MY_TYPE_NAME + " processing. Field '" + fNode.getName() + "' doesn't appear to be a property; incorrect visibility?", fNode);
                return;
            }
            ClassNode fType = fNode.getType();
            if (fType.isArray()) {
                this.addArraySetter(fNode);
                this.addArrayGetter(fNode);
            } else if (fType.isDerivedFrom(LIST_TYPE)) {
                this.addListSetter(fNode);
                this.addListGetter(fNode);
            } else {
                this.addError("Error during " + MY_TYPE_NAME + " processing. Non-Indexable property '" + fNode.getName() + "' found. Type must be array or list but found " + fType.getName(), fNode);
            }
        }
    }

    private void addListGetter(FieldNode fNode) {
        this.addGetter(fNode, this.getComponentTypeForList(fNode.getType()));
    }

    private void addListSetter(FieldNode fNode) {
        this.addSetter(fNode, this.getComponentTypeForList(fNode.getType()));
    }

    private void addArrayGetter(FieldNode fNode) {
        this.addGetter(fNode, fNode.getType().getComponentType());
    }

    private void addArraySetter(FieldNode fNode) {
        this.addSetter(fNode, fNode.getType().getComponentType());
    }

    private void addGetter(FieldNode fNode, ClassNode componentType) {
        ClassNode cNode = fNode.getDeclaringClass();
        BlockStatement body = new BlockStatement();
        Parameter[] params = new Parameter[]{new Parameter(ClassHelper.int_TYPE, "index")};
        body.addStatement(GeneralUtils.stmt(GeneralUtils.indexX(GeneralUtils.varX(fNode), GeneralUtils.varX(params[0]))));
        cNode.addMethod(this.makeName(fNode, "get"), this.getModifiers(fNode), componentType, params, null, body);
    }

    private void addSetter(FieldNode fNode, ClassNode componentType) {
        ClassNode cNode = fNode.getDeclaringClass();
        BlockStatement body = new BlockStatement();
        Parameter[] theParams = GeneralUtils.params(new Parameter(ClassHelper.int_TYPE, "index"), new Parameter(componentType, "value"));
        body.addStatement(GeneralUtils.assignS(GeneralUtils.indexX(GeneralUtils.varX(fNode), GeneralUtils.varX(theParams[0])), GeneralUtils.varX(theParams[1])));
        cNode.addMethod(this.makeName(fNode, "set"), this.getModifiers(fNode), ClassHelper.VOID_TYPE, theParams, null, body);
    }

    private ClassNode getComponentTypeForList(ClassNode fType) {
        if (fType.isUsingGenerics() && fType.getGenericsTypes().length == 1) {
            return fType.getGenericsTypes()[0].getType();
        }
        return ClassHelper.OBJECT_TYPE;
    }

    private int getModifiers(FieldNode fNode) {
        int mods = 1;
        if (fNode.isStatic()) {
            mods |= 8;
        }
        return mods;
    }

    private String makeName(FieldNode fNode, String prefix) {
        return prefix + MetaClassHelper.capitalize(fNode.getName());
    }
}

