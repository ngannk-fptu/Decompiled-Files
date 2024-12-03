/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.FunctionInfo
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.tagext.FunctionInfo;
import org.apache.jasper.JasperException;

abstract class ELNode {
    ELNode() {
    }

    public abstract void accept(Visitor var1) throws JasperException;

    public static class Visitor {
        public void visit(Root n) throws JasperException {
            n.getExpression().visit(this);
        }

        public void visit(Function n) throws JasperException {
        }

        public void visit(Text n) throws JasperException {
        }

        public void visit(ELText n) throws JasperException {
        }
    }

    public static class Nodes {
        private String mapName = null;
        private final List<ELNode> list = new ArrayList<ELNode>();

        Nodes() {
        }

        public void add(ELNode en) {
            this.list.add(en);
        }

        public void visit(Visitor v) throws JasperException {
            for (ELNode n : this.list) {
                n.accept(v);
            }
        }

        public Iterator<ELNode> iterator() {
            return this.list.iterator();
        }

        public boolean isEmpty() {
            return this.list.size() == 0;
        }

        public boolean containsEL() {
            for (ELNode n : this.list) {
                if (!(n instanceof Root)) continue;
                return true;
            }
            return false;
        }

        public void setMapName(String name) {
            this.mapName = name;
        }

        public String getMapName() {
            return this.mapName;
        }
    }

    public static class Function
    extends ELNode {
        private final String prefix;
        private final String name;
        private final String originalText;
        private String uri;
        private FunctionInfo functionInfo;
        private String methodName;
        private String[] parameters;

        Function(String prefix, String name, String originalText) {
            this.prefix = prefix;
            this.name = name;
            this.originalText = originalText;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getName() {
            return this.name;
        }

        public String getOriginalText() {
            return this.originalText;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return this.uri;
        }

        public void setFunctionInfo(FunctionInfo f) {
            this.functionInfo = f;
        }

        public FunctionInfo getFunctionInfo() {
            return this.functionInfo;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        public String[] getParameters() {
            return this.parameters;
        }
    }

    public static class ELText
    extends ELNode {
        private final String text;

        ELText(String text) {
            this.text = text;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getText() {
            return this.text;
        }
    }

    public static class Text
    extends ELNode {
        private final String text;

        Text(String text) {
            this.text = text;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public String getText() {
            return this.text;
        }
    }

    public static class Root
    extends ELNode {
        private final Nodes expr;
        private final char type;

        Root(Nodes expr, char type) {
            this.expr = expr;
            this.type = type;
        }

        @Override
        public void accept(Visitor v) throws JasperException {
            v.visit(this);
        }

        public Nodes getExpression() {
            return this.expr;
        }

        public char getType() {
            return this.type;
        }
    }
}

