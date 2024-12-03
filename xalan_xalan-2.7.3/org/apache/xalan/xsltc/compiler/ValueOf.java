/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class ValueOf
extends Instruction {
    private Expression _select;
    private boolean _escaping = true;
    private boolean _isString = false;

    ValueOf() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("ValueOf");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
    }

    @Override
    public void parseContents(Parser parser) {
        this._select = parser.parseExpression(this, "select", null);
        if (this._select.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
            return;
        }
        String str = this.getAttribute("disable-output-escaping");
        if (str != null && str.equals("yes")) {
            this._escaping = false;
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type type = this._select.typeCheck(stable);
        if (type != null && !type.identicalTo(Type.Node)) {
            if (type.identicalTo(Type.NodeSet)) {
                this._select = new CastExpr(this._select, Type.Node);
            } else {
                this._isString = true;
                if (!type.identicalTo(Type.String)) {
                    this._select = new CastExpr(this._select, Type.String);
                }
                this._isString = true;
            }
        }
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int setEscaping = cpg.addInterfaceMethodref(OUTPUT_HANDLER, "setEscaping", "(Z)Z");
        if (!this._escaping) {
            il.append(methodGen.loadHandler());
            il.append(new PUSH(cpg, false));
            il.append(new INVOKEINTERFACE(setEscaping, 2));
        }
        if (this._isString) {
            int characters = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "characters", CHARACTERSW_SIG);
            il.append(classGen.loadTranslet());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEVIRTUAL(characters));
        } else {
            int characters = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "characters", CHARACTERS_SIG);
            il.append(methodGen.loadDOM());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(characters, 3));
        }
        if (!this._escaping) {
            il.append(methodGen.loadHandler());
            il.append(SWAP);
            il.append(new INVOKEINTERFACE(setEscaping, 2));
            il.append(POP);
        }
    }
}

