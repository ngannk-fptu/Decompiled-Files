/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.LiteralElement;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Text
extends Instruction {
    private String _text;
    private boolean _escaping = true;
    private boolean _ignore = false;
    private boolean _textElement = false;

    public Text() {
        this._textElement = true;
    }

    public Text(String text) {
        this._text = text;
    }

    protected String getText() {
        return this._text;
    }

    protected void setText(String text) {
        this._text = this._text == null ? text : this._text + text;
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("Text");
        this.indent(indent + 4);
        Util.println(this._text);
    }

    @Override
    public void parseContents(Parser parser) {
        String str = this.getAttribute("disable-output-escaping");
        if (str != null && str.equals("yes")) {
            this._escaping = false;
        }
        this.parseChildren(parser);
        if (this._text == null) {
            if (this._textElement) {
                this._text = "";
            } else {
                this._ignore = true;
            }
        } else if (this._textElement) {
            if (this._text.length() == 0) {
                this._ignore = true;
            }
        } else if (this.getParent() instanceof LiteralElement) {
            LiteralElement element = (LiteralElement)this.getParent();
            String space = element.getAttribute("xml:space");
            if (space == null || !space.equals("preserve")) {
                char c;
                int i;
                int textLength = this._text.length();
                for (i = 0; i < textLength && Text.isWhitespace(c = this._text.charAt(i)); ++i) {
                }
                if (i == textLength) {
                    this._ignore = true;
                }
            }
        } else {
            char c;
            int i;
            int textLength = this._text.length();
            for (i = 0; i < textLength && Text.isWhitespace(c = this._text.charAt(i)); ++i) {
            }
            if (i == textLength) {
                this._ignore = true;
            }
        }
    }

    public void ignore() {
        this._ignore = true;
    }

    public boolean isIgnore() {
        return this._ignore;
    }

    public boolean isTextElement() {
        return this._textElement;
    }

    @Override
    protected boolean contextDependent() {
        return false;
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (!this._ignore) {
            int esc = cpg.addInterfaceMethodref(OUTPUT_HANDLER, "setEscaping", "(Z)Z");
            if (!this._escaping) {
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, false));
                il.append(new INVOKEINTERFACE(esc, 2));
            }
            il.append(methodGen.loadHandler());
            if (!this.canLoadAsArrayOffsetLength()) {
                int characters = cpg.addInterfaceMethodref(OUTPUT_HANDLER, "characters", "(Ljava/lang/String;)V");
                il.append(new PUSH(cpg, this._text));
                il.append(new INVOKEINTERFACE(characters, 2));
            } else {
                int characters = cpg.addInterfaceMethodref(OUTPUT_HANDLER, "characters", "([CII)V");
                this.loadAsArrayOffsetLength(classGen, methodGen);
                il.append(new INVOKEINTERFACE(characters, 4));
            }
            if (!this._escaping) {
                il.append(methodGen.loadHandler());
                il.append(SWAP);
                il.append(new INVOKEINTERFACE(esc, 2));
                il.append(POP);
            }
        }
        this.translateContents(classGen, methodGen);
    }

    public boolean canLoadAsArrayOffsetLength() {
        return this._text.length() <= 21845;
    }

    public void loadAsArrayOffsetLength(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        XSLTC xsltc = classGen.getParser().getXSLTC();
        int offset = xsltc.addCharacterData(this._text);
        int length = this._text.length();
        String charDataFieldName = "_scharData" + (xsltc.getCharacterDataCount() - 1);
        il.append(new GETSTATIC(cpg.addFieldref(xsltc.getClassName(), charDataFieldName, "[C")));
        il.append(new PUSH(cpg, offset));
        il.append(new PUSH(cpg, this._text.length()));
    }
}

