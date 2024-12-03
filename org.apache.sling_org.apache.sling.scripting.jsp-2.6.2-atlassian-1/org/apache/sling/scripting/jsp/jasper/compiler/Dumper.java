/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.xml.sax.Attributes;

class Dumper {
    Dumper() {
    }

    public static void dump(Node n) {
        try {
            n.accept(new DumpVisitor());
        }
        catch (JasperException e) {
            e.printStackTrace();
        }
    }

    public static void dump(Node.Nodes page) {
        try {
            page.visit(new DumpVisitor());
        }
        catch (JasperException e) {
            e.printStackTrace();
        }
    }

    static class DumpVisitor
    extends Node.Visitor {
        private int indent = 0;

        DumpVisitor() {
        }

        private String getAttributes(Attributes attrs) {
            if (attrs == null) {
                return "";
            }
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < attrs.getLength(); ++i) {
                buf.append(" " + attrs.getQName(i) + "=\"" + attrs.getValue(i) + "\"");
            }
            return buf.toString();
        }

        private void printString(String str) {
            this.printIndent();
            System.out.print(str);
        }

        private void printString(String prefix, char[] chars, String suffix) {
            String str = null;
            if (chars != null) {
                str = new String(chars);
            }
            this.printString(prefix, str, suffix);
        }

        private void printString(String prefix, String str, String suffix) {
            this.printIndent();
            if (str != null) {
                System.out.print(prefix + str + suffix);
            } else {
                System.out.print(prefix + suffix);
            }
        }

        private void printAttributes(String prefix, Attributes attrs, String suffix) {
            this.printString(prefix, this.getAttributes(attrs), suffix);
        }

        private void dumpBody(Node n) throws JasperException {
            Node.Nodes page = n.getBody();
            if (page != null) {
                page.visit(this);
            }
        }

        @Override
        public void visit(Node.PageDirective n) throws JasperException {
            this.printAttributes("<%@ page", n.getAttributes(), "%>");
        }

        @Override
        public void visit(Node.TaglibDirective n) throws JasperException {
            this.printAttributes("<%@ taglib", n.getAttributes(), "%>");
        }

        @Override
        public void visit(Node.IncludeDirective n) throws JasperException {
            this.printAttributes("<%@ include", n.getAttributes(), "%>");
            this.dumpBody(n);
        }

        @Override
        public void visit(Node.Comment n) throws JasperException {
            this.printString("<%--", n.getText(), "--%>");
        }

        @Override
        public void visit(Node.Declaration n) throws JasperException {
            this.printString("<%!", n.getText(), "%>");
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            this.printString("<%=", n.getText(), "%>");
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            this.printString("<%", n.getText(), "%>");
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            this.printAttributes("<jsp:include", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:include>");
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            this.printAttributes("<jsp:forward", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:forward>");
        }

        @Override
        public void visit(Node.GetProperty n) throws JasperException {
            this.printAttributes("<jsp:getProperty", n.getAttributes(), "/>");
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            this.printAttributes("<jsp:setProperty", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:setProperty>");
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            this.printAttributes("<jsp:useBean", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:useBean>");
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            this.printAttributes("<jsp:plugin", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:plugin>");
        }

        @Override
        public void visit(Node.ParamsAction n) throws JasperException {
            this.printAttributes("<jsp:params", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:params>");
        }

        @Override
        public void visit(Node.ParamAction n) throws JasperException {
            this.printAttributes("<jsp:param", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:param>");
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
            this.printAttributes("<jsp:attribute", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:attribute>");
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            this.printAttributes("<jsp:body", n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</jsp:body>");
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            this.printString("${" + new String(n.getText()) + "}");
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.printAttributes("<" + n.getQName(), n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</" + n.getQName() + ">");
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            String tag = n.getQName();
            this.printAttributes("<" + tag, n.getAttributes(), ">");
            this.dumpBody(n);
            this.printString("</" + tag + ">");
        }

        @Override
        public void visit(Node.TemplateText n) throws JasperException {
            this.printString(new String(n.getText()));
        }

        private void printIndent() {
            for (int i = 0; i < this.indent; ++i) {
                System.out.print("  ");
            }
        }
    }
}

