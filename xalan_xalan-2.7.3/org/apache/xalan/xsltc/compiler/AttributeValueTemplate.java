/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.AttributeValue;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class AttributeValueTemplate
extends AttributeValue {
    static final int OUT_EXPR = 0;
    static final int IN_EXPR = 1;
    static final int IN_EXPR_SQUOTES = 2;
    static final int IN_EXPR_DQUOTES = 3;
    static final String DELIMITER = "\ufffe";

    public AttributeValueTemplate(String value, Parser parser, SyntaxTreeNode parent) {
        this.setParent(parent);
        this.setParser(parser);
        try {
            this.parseAVTemplate(value, parser);
        }
        catch (NoSuchElementException e) {
            this.reportError(parent, parser, "ATTR_VAL_TEMPLATE_ERR", value);
        }
    }

    private void parseAVTemplate(String text, Parser parser) {
        StringTokenizer tokenizer = new StringTokenizer(text, "{}\"'", true);
        String t = null;
        String lookahead = null;
        StringBuffer buffer = new StringBuffer();
        int state = 0;
        block23: while (tokenizer.hasMoreTokens()) {
            if (lookahead != null) {
                t = lookahead;
                lookahead = null;
            } else {
                t = tokenizer.nextToken();
            }
            if (t.length() == 1) {
                switch (t.charAt(0)) {
                    case '{': {
                        switch (state) {
                            case 0: {
                                lookahead = tokenizer.nextToken();
                                if (lookahead.equals("{")) {
                                    buffer.append(lookahead);
                                    lookahead = null;
                                    break;
                                }
                                buffer.append(DELIMITER);
                                state = 1;
                                break;
                            }
                            case 1: 
                            case 2: 
                            case 3: {
                                this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
                            }
                        }
                        continue block23;
                    }
                    case '}': {
                        switch (state) {
                            case 0: {
                                lookahead = tokenizer.nextToken();
                                if (lookahead.equals("}")) {
                                    buffer.append(lookahead);
                                    lookahead = null;
                                    break;
                                }
                                this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
                                break;
                            }
                            case 1: {
                                buffer.append(DELIMITER);
                                state = 0;
                                break;
                            }
                            case 2: 
                            case 3: {
                                buffer.append(t);
                            }
                        }
                        continue block23;
                    }
                    case '\'': {
                        switch (state) {
                            case 1: {
                                state = 2;
                                break;
                            }
                            case 2: {
                                state = 1;
                                break;
                            }
                        }
                        buffer.append(t);
                        continue block23;
                    }
                    case '\"': {
                        switch (state) {
                            case 1: {
                                state = 3;
                                break;
                            }
                            case 3: {
                                state = 1;
                                break;
                            }
                        }
                        buffer.append(t);
                        continue block23;
                    }
                }
                buffer.append(t);
                continue;
            }
            buffer.append(t);
        }
        if (state != 0) {
            this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
        }
        tokenizer = new StringTokenizer(buffer.toString(), DELIMITER, true);
        while (tokenizer.hasMoreTokens()) {
            t = tokenizer.nextToken();
            if (t.equals(DELIMITER)) {
                this.addElement(parser.parseExpression(this, tokenizer.nextToken()));
                tokenizer.nextToken();
                continue;
            }
            this.addElement(new LiteralExpr(t));
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Vector contents = this.getContents();
        int n = contents.size();
        for (int i = 0; i < n; ++i) {
            Expression exp = (Expression)contents.elementAt(i);
            if (exp.typeCheck(stable).identicalTo(Type.String)) continue;
            contents.setElementAt(new CastExpr(exp, Type.String), i);
        }
        this._type = Type.String;
        return this._type;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("AVT:[");
        int count = this.elementCount();
        for (int i = 0; i < count; ++i) {
            buffer.append(this.elementAt(i).toString());
            if (i >= count - 1) continue;
            buffer.append(' ');
        }
        return buffer.append(']').toString();
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        if (this.elementCount() == 1) {
            Expression exp = (Expression)this.elementAt(0);
            exp.translate(classGen, methodGen);
        } else {
            ConstantPoolGen cpg = classGen.getConstantPool();
            InstructionList il = methodGen.getInstructionList();
            int initBuffer = cpg.addMethodref("java.lang.StringBuffer", "<init>", "()V");
            INVOKEVIRTUAL append = new INVOKEVIRTUAL(cpg.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
            int toString = cpg.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
            il.append(new NEW(cpg.addClass("java.lang.StringBuffer")));
            il.append(DUP);
            il.append(new INVOKESPECIAL(initBuffer));
            Enumeration elements = this.elements();
            while (elements.hasMoreElements()) {
                Expression exp = (Expression)elements.nextElement();
                exp.translate(classGen, methodGen);
                il.append(append);
            }
            il.append(new INVOKEVIRTUAL(toString));
        }
    }
}

