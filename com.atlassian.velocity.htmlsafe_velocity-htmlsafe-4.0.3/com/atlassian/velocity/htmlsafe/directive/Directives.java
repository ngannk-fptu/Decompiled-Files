/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.Template
 *  org.apache.velocity.runtime.parser.node.ASTDirective
 *  org.apache.velocity.runtime.parser.node.ParserVisitor
 *  org.apache.velocity.runtime.parser.node.SimpleNode
 *  org.apache.velocity.runtime.visitor.BaseVisitor
 */
package com.atlassian.velocity.htmlsafe.directive;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.visitor.BaseVisitor;

public class Directives {
    public static boolean isPresent(String directiveName, Template template) {
        SimpleNode dataAsSimpleNode = (SimpleNode)template.getData();
        DirectiveDetectionVisitor isDirectivePresentVisitor = new DirectiveDetectionVisitor(directiveName);
        dataAsSimpleNode.jjtAccept((ParserVisitor)isDirectivePresentVisitor, new Object());
        return isDirectivePresentVisitor.isPresent();
    }

    private static class DirectiveDetectionVisitor
    extends BaseVisitor {
        private final String directiveName;
        private boolean present = false;

        public DirectiveDetectionVisitor(String directiveName) {
            this.directiveName = directiveName;
        }

        public Object visit(ASTDirective node, Object data) {
            if (!this.present && node.getDirectiveName().equals(this.directiveName)) {
                this.present = true;
            }
            return true;
        }

        public boolean isPresent() {
            return this.present;
        }
    }
}

