/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;

public class TextOptimizer {
    public static void concatenate(Compiler compiler, Node.Nodes page) throws JasperException {
        TextCatVisitor v = new TextCatVisitor(compiler);
        page.visit(v);
        v.collectText();
    }

    static class TextCatVisitor
    extends Node.Visitor {
        private Options options;
        private PageInfo pageInfo;
        private int textNodeCount = 0;
        private Node.TemplateText firstTextNode = null;
        private StringBuffer textBuffer;
        private final String emptyText = new String("");

        public TextCatVisitor(Compiler compiler) {
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
            if ((this.options.getTrimSpaces() || this.pageInfo.isTrimDirectiveWhitespaces()) && n.isAllSpace()) {
                n.setText(this.emptyText);
                return;
            }
            if (this.textNodeCount++ == 0) {
                this.firstTextNode = n;
                this.textBuffer = new StringBuffer(n.getText());
            } else {
                this.textBuffer.append(n.getText());
                n.setText(this.emptyText);
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

