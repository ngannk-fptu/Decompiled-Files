/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class Message
extends Instruction {
    private boolean _terminate = false;

    Message() {
    }

    @Override
    public void parseContents(Parser parser) {
        String termstr = this.getAttribute("terminate");
        if (termstr != null) {
            this._terminate = termstr.equals("yes");
        }
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(classGen.loadTranslet());
        switch (this.elementCount()) {
            case 0: {
                il.append(new PUSH(cpg, ""));
                break;
            }
            case 1: {
                SyntaxTreeNode child = (SyntaxTreeNode)this.elementAt(0);
                if (child instanceof Text) {
                    il.append(new PUSH(cpg, ((Text)child).getText()));
                    break;
                }
            }
            default: {
                il.append(methodGen.loadHandler());
                il.append(new NEW(cpg.addClass(STREAM_XML_OUTPUT)));
                il.append(methodGen.storeHandler());
                il.append(new NEW(cpg.addClass("java.io.StringWriter")));
                il.append(DUP);
                il.append(DUP);
                il.append(new INVOKESPECIAL(cpg.addMethodref("java.io.StringWriter", "<init>", "()V")));
                il.append(methodGen.loadHandler());
                il.append(new INVOKESPECIAL(cpg.addMethodref(STREAM_XML_OUTPUT, "<init>", "()V")));
                il.append(methodGen.loadHandler());
                il.append(SWAP);
                il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "setWriter", "(Ljava/io/Writer;)V"), 2));
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, "UTF-8"));
                il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "setEncoding", "(Ljava/lang/String;)V"), 2));
                il.append(methodGen.loadHandler());
                il.append(ICONST_1);
                il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "setOmitXMLDeclaration", "(Z)V"), 2));
                il.append(methodGen.loadHandler());
                il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "startDocument", "()V"), 1));
                this.translateContents(classGen, methodGen);
                il.append(methodGen.loadHandler());
                il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "endDocument", "()V"), 1));
                il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.io.StringWriter", "toString", "()Ljava/lang/String;")));
                il.append(SWAP);
                il.append(methodGen.storeHandler());
            }
        }
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "displayMessage", "(Ljava/lang/String;)V")));
        if (this._terminate) {
            int einit = cpg.addMethodref("java.lang.RuntimeException", "<init>", "(Ljava/lang/String;)V");
            il.append(new NEW(cpg.addClass("java.lang.RuntimeException")));
            il.append(DUP);
            il.append(new PUSH(cpg, "Termination forced by an xsl:message instruction"));
            il.append(new INVOKESPECIAL(einit));
            il.append(ATHROW);
        }
    }
}

