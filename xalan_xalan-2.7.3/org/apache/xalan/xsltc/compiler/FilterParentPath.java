/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.CurrentCall;
import org.apache.xalan.xsltc.compiler.DocumentCall;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.KeyCall;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.RelativeLocationPath;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class FilterParentPath
extends Expression {
    private Expression _filterExpr;
    private Expression _path;
    private boolean _hasDescendantAxis = false;

    public FilterParentPath(Expression filterExpr, Expression path) {
        this._path = path;
        this._path.setParent(this);
        this._filterExpr = filterExpr;
        this._filterExpr.setParent(this);
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._filterExpr.setParser(parser);
        this._path.setParser(parser);
    }

    @Override
    public String toString() {
        return "FilterParentPath(" + this._filterExpr + ", " + this._path + ')';
    }

    public void setDescendantAxis() {
        this._hasDescendantAxis = true;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type ptype;
        Type ftype = this._filterExpr.typeCheck(stable);
        if (!(ftype instanceof NodeSetType)) {
            if (ftype instanceof ReferenceType) {
                this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
            } else if (ftype instanceof NodeType) {
                this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
            } else {
                throw new TypeCheckError(this);
            }
        }
        if (!((ptype = this._path.typeCheck(stable)) instanceof NodeSetType)) {
            this._path = new CastExpr(this._path, Type.NodeSet);
        }
        this._type = Type.NodeSet;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        SyntaxTreeNode parent;
        boolean parentAlreadyOrdered;
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int initSI = cpg.addMethodref("org.apache.xalan.xsltc.dom.StepIterator", "<init>", "(Lorg/apache/xml/dtm/DTMAxisIterator;Lorg/apache/xml/dtm/DTMAxisIterator;)V");
        this._filterExpr.translate(classGen, methodGen);
        LocalVariableGen filterTemp = methodGen.addLocalVariable("filter_parent_path_tmp1", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));
        this._path.translate(classGen, methodGen);
        LocalVariableGen pathTemp = methodGen.addLocalVariable("filter_parent_path_tmp2", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));
        il.append(new NEW(cpg.addClass("org.apache.xalan.xsltc.dom.StepIterator")));
        il.append(DUP);
        filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));
        il.append(new INVOKESPECIAL(initSI));
        if (this._hasDescendantAxis) {
            int incl = cpg.addMethodref("org.apache.xml.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(new INVOKEVIRTUAL(incl));
        }
        boolean bl = parentAlreadyOrdered = (parent = this.getParent()) instanceof RelativeLocationPath || parent instanceof FilterParentPath || parent instanceof KeyCall || parent instanceof CurrentCall || parent instanceof DocumentCall;
        if (!parentAlreadyOrdered) {
            int order = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "orderNodes", "(Lorg/apache/xml/dtm/DTMAxisIterator;I)Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}

