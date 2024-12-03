/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Mode;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class ApplyImports
extends Instruction {
    private QName _modeName;
    private int _precedence;

    ApplyImports() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("ApplyTemplates");
        this.indent(indent + 4);
        if (this._modeName != null) {
            this.indent(indent + 4);
            Util.println("mode " + this._modeName);
        }
    }

    public boolean hasWithParams() {
        return this.hasContents();
    }

    private int getMinPrecedence(int max) {
        Stylesheet includeRoot = this.getStylesheet();
        while (includeRoot._includedFrom != null) {
            includeRoot = includeRoot._includedFrom;
        }
        return includeRoot.getMinimumDescendantPrecedence();
    }

    @Override
    public void parseContents(Parser parser) {
        Stylesheet stylesheet = this.getStylesheet();
        stylesheet.setTemplateInlining(false);
        Template template = this.getTemplate();
        this._modeName = template.getModeName();
        this._precedence = template.getImportPrecedence();
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Stylesheet stylesheet = classGen.getStylesheet();
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int current = methodGen.getLocalIndex("current");
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadHandler());
        il.append(methodGen.loadCurrentNode());
        if (stylesheet.hasLocalParams()) {
            il.append(classGen.loadTranslet());
            int pushFrame = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(pushFrame));
        }
        int maxPrecedence = this._precedence;
        int minPrecedence = this.getMinPrecedence(maxPrecedence);
        Mode mode = stylesheet.getMode(this._modeName);
        String functionName = mode.functionName(minPrecedence, maxPrecedence);
        String className = classGen.getStylesheet().getClassName();
        String signature = classGen.getApplyTemplatesSigForImport();
        int applyTemplates = cpg.addMethodref(className, functionName, signature);
        il.append(new INVOKEVIRTUAL(applyTemplates));
        if (stylesheet.hasLocalParams()) {
            il.append(classGen.loadTranslet());
            int pushFrame = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(pushFrame));
        }
    }
}

