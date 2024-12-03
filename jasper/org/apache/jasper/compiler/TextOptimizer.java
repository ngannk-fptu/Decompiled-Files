/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;
import org.apache.jasper.Options;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;

public class TextOptimizer {
    public static void concatenate(Compiler compiler, Node.Nodes page) throws JasperException {
        TextCatVisitor v = new TextCatVisitor(compiler);
        page.visit(v);
        v.collectText();
    }

    private static class TextCatVisitor
    extends Node.Visitor {
        private static final String EMPTY_TEXT = "";
        private static final String SINGLE_SPACE = " ";
        private final Options options;
        private final PageInfo pageInfo;
        private int textNodeCount = 0;
        private Node.TemplateText firstTextNode = null;
        private StringBuilder textBuffer;

        TextCatVisitor(Compiler compiler) {
            this.options = compiler.getCompilationContext().getOptions();
            this.pageInfo = compiler.getPageInfo();
        }

        @Override
        public void doVisit(Node n) throws JasperException {
            this.collectText();
        }

        @Override
        public void visit(Node.PageDirective n) throws JasperException {
        }

        @Override
        public void visit(Node.TagDirective n) throws JasperException {
        }

        @Override
        public void visit(Node.TaglibDirective n) throws JasperException {
        }

        @Override
        public void visit(Node.AttributeDirective n) throws JasperException {
        }

        @Override
        public void visit(Node.VariableDirective n) throws JasperException {
        }

        @Override
        public void visitBody(Node n) throws JasperException {
            super.visitBody(n);
            this.collectText();
        }

        @Override
        public void visit(Node.TemplateText n) throws JasperException {
            if (n.isAllSpace()) {
                if (this.options.getTrimSpaces() == TrimSpacesOption.TRUE || this.pageInfo.isTrimDirectiveWhitespaces()) {
                    n.setText(EMPTY_TEXT);
                    return;
                }
                if (this.options.getTrimSpaces() == TrimSpacesOption.SINGLE) {
                    n.setText(SINGLE_SPACE);
                    return;
                }
            }
            if (this.textNodeCount++ == 0) {
                this.firstTextNode = n;
                this.textBuffer = new StringBuilder(n.getText());
            } else {
                this.textBuffer.append(n.getText());
                n.setText(EMPTY_TEXT);
            }
        }

        private void collectText() {
            if (this.textNodeCount > 1) {
                this.firstTextNode.setText(this.textBuffer.toString());
            }
            this.textNodeCount = 0;
        }
    }
}

