/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.Canonical;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.EqualsAndHashCodeASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.ImmutableASTTransformation;
import org.codehaus.groovy.transform.ToStringASTTransformation;
import org.codehaus.groovy.transform.TupleConstructorASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class CanonicalASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = Canonical.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            List<String> includes;
            ClassNode cNode = (ClassNode)parent;
            if (this.hasAnnotation(cNode, ImmutableASTTransformation.MY_TYPE)) {
                this.addError(MY_TYPE_NAME + " class '" + cNode.getName() + "' can't also be " + ImmutableASTTransformation.MY_TYPE_NAME, parent);
            }
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            List<String> excludes = CanonicalASTTransformation.getMemberList(anno, "excludes");
            if (!this.checkIncludeExclude(anno, excludes, includes = CanonicalASTTransformation.getMemberList(anno, "includes"), MY_TYPE_NAME)) {
                return;
            }
            if (!this.hasAnnotation(cNode, TupleConstructorASTTransformation.MY_TYPE)) {
                TupleConstructorASTTransformation.createConstructor(cNode, false, true, false, false, false, false, excludes, includes);
            }
            if (!this.hasAnnotation(cNode, EqualsAndHashCodeASTTransformation.MY_TYPE)) {
                EqualsAndHashCodeASTTransformation.createHashCode(cNode, false, false, false, excludes, includes);
                EqualsAndHashCodeASTTransformation.createEquals(cNode, false, false, true, excludes, includes);
            }
            if (!this.hasAnnotation(cNode, ToStringASTTransformation.MY_TYPE)) {
                ToStringASTTransformation.createToString(cNode, false, false, excludes, includes, false);
            }
        }
    }
}

