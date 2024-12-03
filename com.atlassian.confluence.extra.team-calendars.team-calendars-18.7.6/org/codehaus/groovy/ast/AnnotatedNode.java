/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;

public class AnnotatedNode
extends ASTNode {
    private List<AnnotationNode> annotations = Collections.emptyList();
    private boolean synthetic;
    ClassNode declaringClass;
    private boolean hasNoRealSourcePositionFlag;

    public List<AnnotationNode> getAnnotations() {
        return this.annotations;
    }

    public List<AnnotationNode> getAnnotations(ClassNode type) {
        ArrayList<AnnotationNode> ret = new ArrayList<AnnotationNode>(this.annotations.size());
        for (AnnotationNode node : this.annotations) {
            if (!type.equals(node.getClassNode())) continue;
            ret.add(node);
        }
        return ret;
    }

    public void addAnnotation(AnnotationNode value) {
        this.checkInit();
        this.annotations.add(value);
    }

    private void checkInit() {
        if (this.annotations == Collections.EMPTY_LIST) {
            this.annotations = new ArrayList<AnnotationNode>(3);
        }
    }

    public void addAnnotations(List<AnnotationNode> annotations) {
        for (AnnotationNode node : annotations) {
            this.addAnnotation(node);
        }
    }

    public boolean isSynthetic() {
        return this.synthetic;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public ClassNode getDeclaringClass() {
        return this.declaringClass;
    }

    public void setDeclaringClass(ClassNode declaringClass) {
        this.declaringClass = declaringClass;
    }

    public boolean hasNoRealSourcePosition() {
        return this.hasNoRealSourcePositionFlag;
    }

    public void setHasNoRealSourcePosition(boolean value) {
        this.hasNoRealSourcePositionFlag = value;
    }
}

