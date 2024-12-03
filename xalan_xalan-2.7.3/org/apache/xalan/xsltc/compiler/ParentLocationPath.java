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
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.RelativeLocationPath;
import org.apache.xalan.xsltc.compiler.Step;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class ParentLocationPath
extends RelativeLocationPath {
    private Expression _step;
    private final RelativeLocationPath _path;
    private Type stype;
    private boolean _orderNodes = false;
    private boolean _axisMismatch = false;

    public ParentLocationPath(RelativeLocationPath path, Expression step) {
        this._path = path;
        this._step = step;
        this._path.setParent(this);
        this._step.setParent(this);
        if (this._step instanceof Step) {
            this._axisMismatch = this.checkAxisMismatch();
        }
    }

    @Override
    public void setAxis(int axis) {
        this._path.setAxis(axis);
    }

    @Override
    public int getAxis() {
        return this._path.getAxis();
    }

    public RelativeLocationPath getPath() {
        return this._path;
    }

    public Expression getStep() {
        return this._step;
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._step.setParser(parser);
        this._path.setParser(parser);
    }

    @Override
    public String toString() {
        return "ParentLocationPath(" + this._path + ", " + this._step + ')';
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this.stype = this._step.typeCheck(stable);
        this._path.typeCheck(stable);
        if (this._axisMismatch) {
            this.enableNodeOrdering();
        }
        this._type = Type.NodeSet;
        return this._type;
    }

    public void enableNodeOrdering() {
        SyntaxTreeNode parent = this.getParent();
        if (parent instanceof ParentLocationPath) {
            ((ParentLocationPath)parent).enableNodeOrdering();
        } else {
            this._orderNodes = true;
        }
    }

    public boolean checkAxisMismatch() {
        int type;
        int left = this._path.getAxis();
        int right = ((Step)this._step).getAxis();
        if (!(left != 0 && left != 1 || right != 3 && right != 4 && right != 5 && right != 10 && right != 11 && right != 12)) {
            return true;
        }
        if (left == 3 && right == 0 || right == 1 || right == 10 || right == 11) {
            return true;
        }
        if (left == 4 || left == 5) {
            return true;
        }
        if (!(left != 6 && left != 7 || right != 6 && right != 10 && right != 11 && right != 12)) {
            return true;
        }
        if (!(left != 11 && left != 12 || right != 4 && right != 5 && right != 6 && right != 7 && right != 10 && right != 11 && right != 12)) {
            return true;
        }
        return right == 6 && left == 3 && this._path instanceof Step && (type = ((Step)this._path).getNodeType()) == 2;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this._path.translate(classGen, methodGen);
        LocalVariableGen pathTemp = methodGen.addLocalVariable("parent_location_path_tmp1", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));
        this._step.translate(classGen, methodGen);
        LocalVariableGen stepTemp = methodGen.addLocalVariable("parent_location_path_tmp2", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        stepTemp.setStart(il.append(new ASTORE(stepTemp.getIndex())));
        int initSI = cpg.addMethodref("org.apache.xalan.xsltc.dom.StepIterator", "<init>", "(Lorg/apache/xml/dtm/DTMAxisIterator;Lorg/apache/xml/dtm/DTMAxisIterator;)V");
        il.append(new NEW(cpg.addClass("org.apache.xalan.xsltc.dom.StepIterator")));
        il.append(DUP);
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));
        stepTemp.setEnd(il.append(new ALOAD(stepTemp.getIndex())));
        il.append(new INVOKESPECIAL(initSI));
        Expression stp = this._step;
        if (stp instanceof ParentLocationPath) {
            stp = ((ParentLocationPath)stp).getStep();
        }
        if (this._path instanceof Step && stp instanceof Step) {
            int path = ((Step)this._path).getAxis();
            int step = ((Step)stp).getAxis();
            if (path == 5 && step == 3 || path == 11 && step == 10) {
                int incl = cpg.addMethodref("org.apache.xml.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lorg/apache/xml/dtm/DTMAxisIterator;");
                il.append(new INVOKEVIRTUAL(incl));
            }
        }
        if (this._orderNodes) {
            int order = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "orderNodes", "(Lorg/apache/xml/dtm/DTMAxisIterator;I)Lorg/apache/xml/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}

