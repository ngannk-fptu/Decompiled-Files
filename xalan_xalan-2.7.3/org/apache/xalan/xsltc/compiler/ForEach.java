/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Sort;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.ResultTreeType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class ForEach
extends Instruction {
    private Expression _select;
    private Type _type;

    ForEach() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("ForEach");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
        this.displayContents(indent + 4);
    }

    @Override
    public void parseContents(Parser parser) {
        this._select = parser.parseExpression(this, "select", null);
        this.parseChildren(parser);
        if (this._select.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this._type = this._select.typeCheck(stable);
        if (this._type instanceof ReferenceType || this._type instanceof NodeType) {
            this._select = new CastExpr(this._select, Type.NodeSet);
            this.typeCheckContents(stable);
            return Type.Void;
        }
        if (this._type instanceof NodeSetType || this._type instanceof ResultTreeType) {
            this.typeCheckContents(stable);
            return Type.Void;
        }
        throw new TypeCheckError(this);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        Vector sortObjects = new Vector();
        Enumeration children = this.elements();
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            if (!(child instanceof Sort)) continue;
            sortObjects.addElement(child);
        }
        if (this._type != null && this._type instanceof ResultTreeType) {
            il.append(methodGen.loadDOM());
            if (sortObjects.size() > 0) {
                ErrorMsg msg = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
                this.getParser().reportError(4, msg);
            }
            this._select.translate(classGen, methodGen);
            this._type.translateTo(classGen, methodGen, Type.NodeSet);
            il.append(SWAP);
            il.append(methodGen.storeDOM());
        } else {
            if (sortObjects.size() > 0) {
                Sort.translateSortIterator(classGen, methodGen, this._select, sortObjects);
            } else {
                this._select.translate(classGen, methodGen);
            }
            if (!(this._type instanceof ReferenceType)) {
                il.append(methodGen.loadContextNode());
                il.append(methodGen.setStartNode());
            }
        }
        il.append(methodGen.storeIterator());
        this.initializeVariables(classGen, methodGen);
        BranchHandle nextNode = il.append(new GOTO(null));
        InstructionHandle loop = il.append(NOP);
        this.translateContents(classGen, methodGen);
        nextNode.setTarget(il.append(methodGen.loadIterator()));
        il.append(methodGen.nextNode());
        il.append(DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGT(loop));
        if (this._type != null && this._type instanceof ResultTreeType) {
            il.append(methodGen.storeDOM());
        }
        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
    }

    public void initializeVariables(ClassGenerator classGen, MethodGenerator methodGen) {
        int n = this.elementCount();
        for (int i = 0; i < n; ++i) {
            Object child = this.getContents().elementAt(i);
            if (!(child instanceof Variable)) continue;
            Variable var = (Variable)child;
            var.initialize(classGen, methodGen);
        }
    }
}

