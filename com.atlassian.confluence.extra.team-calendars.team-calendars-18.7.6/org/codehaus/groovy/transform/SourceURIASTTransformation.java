/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.SourceURI;
import java.io.File;
import java.net.URI;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class SourceURIASTTransformation
extends AbstractASTTransformation {
    private static final Class<SourceURI> MY_CLASS = SourceURI.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode URI_TYPE = ClassHelper.make(URI.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        if (parent instanceof DeclarationExpression) {
            this.setScriptURIOnDeclaration((DeclarationExpression)parent, node);
        } else if (parent instanceof FieldNode) {
            this.setScriptURIOnField((FieldNode)parent, node);
        } else {
            this.addError("Expected to find the annotation " + MY_TYPE_NAME + " on an declaration statement.", parent);
        }
    }

    private void setScriptURIOnDeclaration(DeclarationExpression de, AnnotationNode node) {
        if (de.isMultipleAssignmentDeclaration()) {
            this.addError("Annotation " + MY_TYPE_NAME + " not supported with multiple assignment notation.", de);
            return;
        }
        if (!(de.getRightExpression() instanceof EmptyExpression)) {
            this.addError("Annotation " + MY_TYPE_NAME + " not supported with variable assignment.", de);
            return;
        }
        URI uri = this.getSourceURI(node);
        if (uri == null) {
            this.addError("Unable to get the URI for the source of this script!", de);
        } else {
            de.setRightExpression(this.getExpression(uri));
        }
    }

    private void setScriptURIOnField(FieldNode fieldNode, AnnotationNode node) {
        if (fieldNode.hasInitialExpression()) {
            this.addError("Annotation " + MY_TYPE_NAME + " not supported with variable assignment.", fieldNode);
            return;
        }
        URI uri = this.getSourceURI(node);
        if (uri == null) {
            this.addError("Unable to get the URI for the source of this class!", fieldNode);
        } else {
            fieldNode.setInitialValueExpression(this.getExpression(uri));
        }
    }

    private Expression getExpression(URI uri) {
        return GeneralUtils.callX(URI_TYPE, "create", (Expression)GeneralUtils.args(GeneralUtils.constX(uri.toString())));
    }

    protected URI getSourceURI(AnnotationNode node) {
        URI uri = this.sourceUnit.getSource().getURI();
        if (uri != null && !uri.isAbsolute() && !this.memberHasValue(node, "allowRelative", true)) {
            URI baseURI = new File(".").toURI();
            uri = uri.resolve(baseURI);
        }
        return uri;
    }
}

