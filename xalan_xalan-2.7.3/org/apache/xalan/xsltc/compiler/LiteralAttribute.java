/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.ElemDesc
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.AttributeValue;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.LiteralElement;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SimpleAttributeValue;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.serializer.ElemDesc;

final class LiteralAttribute
extends Instruction {
    private final String _name;
    private final AttributeValue _value;

    public LiteralAttribute(String name, String value, Parser parser, SyntaxTreeNode parent) {
        this._name = name;
        this.setParent(parent);
        this._value = AttributeValue.create(this, value, parser);
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("LiteralAttribute name=" + this._name + " value=" + this._value);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this._value.typeCheck(stable);
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    protected boolean contextDependent() {
        return this._value.contextDependent();
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadHandler());
        il.append(new PUSH(cpg, this._name));
        this._value.translate(classGen, methodGen);
        SyntaxTreeNode parent = this.getParent();
        if (parent instanceof LiteralElement && ((LiteralElement)parent).allAttributesUnique()) {
            String attrValue;
            int flags = 0;
            boolean isHTMLAttrEmpty = false;
            ElemDesc elemDesc = ((LiteralElement)parent).getElemDesc();
            if (elemDesc != null) {
                if (elemDesc.isAttrFlagSet(this._name, 4)) {
                    flags |= 2;
                    isHTMLAttrEmpty = true;
                } else if (elemDesc.isAttrFlagSet(this._name, 2)) {
                    flags |= 4;
                }
            }
            if (this._value instanceof SimpleAttributeValue && !this.hasBadChars(attrValue = ((SimpleAttributeValue)this._value).toString()) && !isHTMLAttrEmpty) {
                flags |= 1;
            }
            il.append(new PUSH(cpg, flags));
            il.append(methodGen.uniqueAttribute());
        } else {
            il.append(methodGen.attribute());
        }
    }

    private boolean hasBadChars(String value) {
        for (char ch : value.toCharArray()) {
            if (ch >= ' ' && '~' >= ch && ch != '<' && ch != '>' && ch != '&' && ch != '\"') continue;
            return true;
        }
        return false;
    }

    public String getName() {
        return this._name;
    }

    public AttributeValue getValue() {
        return this._value;
    }
}

