/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DCONST;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.IntType;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class Variable
extends VariableBase {
    Variable() {
    }

    public int getIndex() {
        return this._local != null ? this._local.getIndex() : -1;
    }

    @Override
    public void parseContents(Parser parser) {
        super.parseContents(parser);
        SyntaxTreeNode parent = this.getParent();
        if (parent instanceof Stylesheet) {
            this._isLocal = false;
            Variable var = parser.getSymbolTable().lookupVariable(this._name);
            if (var != null) {
                int them;
                int us = this.getImportPrecedence();
                if (us == (them = var.getImportPrecedence())) {
                    String name = this._name.toString();
                    this.reportError(this, parser, "VARIABLE_REDEF_ERR", name);
                } else {
                    if (them > us) {
                        this._ignore = true;
                        return;
                    }
                    var.disable();
                }
            }
            ((Stylesheet)parent).addVariable(this);
            parser.getSymbolTable().addVariable(this);
        } else {
            this._isLocal = true;
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._select != null) {
            this._type = this._select.typeCheck(stable);
        } else if (this.hasContents()) {
            this.typeCheckContents(stable);
            this._type = Type.ResultTree;
        } else {
            this._type = Type.Reference;
        }
        return Type.Void;
    }

    public void initialize(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this.isLocal() && !this._refs.isEmpty()) {
            if (this._local == null) {
                this._local = methodGen.addLocalVariable2(this.getEscapedName(), this._type.toJCType(), null);
            }
            if (this._type instanceof IntType || this._type instanceof NodeType || this._type instanceof BooleanType) {
                il.append(new ICONST(0));
            } else if (this._type instanceof RealType) {
                il.append(new DCONST(0.0));
            } else {
                il.append(new ACONST_NULL());
            }
            this._local.setStart(il.append(this._type.STORE(this._local.getIndex())));
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._refs.isEmpty()) {
            this._ignore = true;
        }
        if (this._ignore) {
            return;
        }
        this._ignore = true;
        String name = this.getEscapedName();
        if (this.isLocal()) {
            boolean createLocal;
            this.translateValue(classGen, methodGen);
            boolean bl = createLocal = this._local == null;
            if (createLocal) {
                this.mapRegister(methodGen);
            }
            InstructionHandle storeInst = il.append(this._type.STORE(this._local.getIndex()));
            if (createLocal) {
                this._local.setStart(storeInst);
            }
        } else {
            String signature = this._type.toSignature();
            if (classGen.containsField(name) == null) {
                classGen.addField(new Field(1, cpg.addUtf8(name), cpg.addUtf8(signature), null, cpg.getConstantPool()));
                il.append(classGen.loadTranslet());
                this.translateValue(classGen, methodGen);
                il.append(new PUTFIELD(cpg.addFieldref(classGen.getClassName(), name, signature)));
            }
        }
    }
}

