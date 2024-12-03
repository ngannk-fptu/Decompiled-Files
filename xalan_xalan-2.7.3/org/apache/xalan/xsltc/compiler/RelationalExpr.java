/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.VariableRefBase;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.IntType;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodType;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.ResultTreeType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.runtime.Operators;

final class RelationalExpr
extends Expression {
    private int _op;
    private Expression _left;
    private Expression _right;

    public RelationalExpr(int op, Expression left, Expression right) {
        this._op = op;
        this._left = left;
        this._left.setParent(this);
        this._right = right;
        this._right.setParent(this);
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
        this._right.setParser(parser);
    }

    @Override
    public boolean hasPositionCall() {
        if (this._left.hasPositionCall()) {
            return true;
        }
        return this._right.hasPositionCall();
    }

    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall() || this._right.hasLastCall();
    }

    public boolean hasReferenceArgs() {
        return this._left.getType() instanceof ReferenceType || this._right.getType() instanceof ReferenceType;
    }

    public boolean hasNodeArgs() {
        return this._left.getType() instanceof NodeType || this._right.getType() instanceof NodeType;
    }

    public boolean hasNodeSetArgs() {
        return this._left.getType() instanceof NodeSetType || this._right.getType() instanceof NodeSetType;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        MethodType ptype;
        Type tleft = this._left.typeCheck(stable);
        Type tright = this._right.typeCheck(stable);
        if (tleft instanceof ResultTreeType && tright instanceof ResultTreeType) {
            this._right = new CastExpr(this._right, Type.Real);
            this._left = new CastExpr(this._left, Type.Real);
            this._type = Type.Boolean;
            return this._type;
        }
        if (this.hasReferenceArgs()) {
            VariableBase var;
            VariableRefBase ref;
            Type type = null;
            Type typeL = null;
            Type typeR = null;
            if (tleft instanceof ReferenceType && this._left instanceof VariableRefBase) {
                ref = (VariableRefBase)this._left;
                var = ref.getVariable();
                typeL = var.getType();
            }
            if (tright instanceof ReferenceType && this._right instanceof VariableRefBase) {
                ref = (VariableRefBase)this._right;
                var = ref.getVariable();
                typeR = var.getType();
            }
            if ((type = typeL == null ? typeR : (typeR == null ? typeL : Type.Real)) == null) {
                type = Type.Real;
            }
            this._right = new CastExpr(this._right, type);
            this._left = new CastExpr(this._left, type);
            this._type = Type.Boolean;
            return this._type;
        }
        if (this.hasNodeSetArgs()) {
            if (tright instanceof NodeSetType) {
                Expression temp = this._right;
                this._right = this._left;
                this._left = temp;
                this._op = this._op == 2 ? 3 : (this._op == 3 ? 2 : (this._op == 4 ? 5 : 4));
                tright = this._right.getType();
            }
            if (tright instanceof NodeType) {
                this._right = new CastExpr(this._right, Type.NodeSet);
            }
            if (tright instanceof IntType) {
                this._right = new CastExpr(this._right, Type.Real);
            }
            if (tright instanceof ResultTreeType) {
                this._right = new CastExpr(this._right, Type.String);
            }
            this._type = Type.Boolean;
            return this._type;
        }
        if (this.hasNodeArgs()) {
            if (tleft instanceof BooleanType) {
                this._right = new CastExpr(this._right, Type.Boolean);
                tright = Type.Boolean;
            }
            if (tright instanceof BooleanType) {
                this._left = new CastExpr(this._left, Type.Boolean);
                tleft = Type.Boolean;
            }
        }
        if ((ptype = this.lookupPrimop(stable, Operators.getOpNames(this._op), new MethodType(Type.Void, tleft, tright))) != null) {
            Type arg2;
            Type arg1 = (Type)ptype.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            if (!(arg2 = (Type)ptype.argsType().elementAt(1)).identicalTo(tright)) {
                this._right = new CastExpr(this._right, arg1);
            }
            this._type = ptype.resultType();
            return this._type;
        }
        throw new TypeCheckError(this);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        if (this.hasNodeSetArgs() || this.hasReferenceArgs()) {
            ConstantPoolGen cpg = classGen.getConstantPool();
            InstructionList il = methodGen.getInstructionList();
            this._left.translate(classGen, methodGen);
            this._left.startIterator(classGen, methodGen);
            this._right.translate(classGen, methodGen);
            this._right.startIterator(classGen, methodGen);
            il.append(new PUSH(cpg, this._op));
            il.append(methodGen.loadDOM());
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "compare", "(" + this._left.getType().toSignature() + this._right.getType().toSignature() + "I" + "Lorg/apache/xalan/xsltc/DOM;" + ")Z");
            il.append(new INVOKESTATIC(index));
        } else {
            this.translateDesynthesized(classGen, methodGen);
            this.synthesize(classGen, methodGen);
        }
    }

    @Override
    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        if (this.hasNodeSetArgs() || this.hasReferenceArgs()) {
            this.translate(classGen, methodGen);
            this.desynthesize(classGen, methodGen);
        } else {
            BranchInstruction bi = null;
            InstructionList il = methodGen.getInstructionList();
            this._left.translate(classGen, methodGen);
            this._right.translate(classGen, methodGen);
            boolean tozero = false;
            Type tleft = this._left.getType();
            if (tleft instanceof RealType) {
                il.append(tleft.CMP(this._op == 3 || this._op == 5));
                tleft = Type.Int;
                tozero = true;
            }
            switch (this._op) {
                case 3: {
                    bi = tleft.GE(tozero);
                    break;
                }
                case 2: {
                    bi = tleft.LE(tozero);
                    break;
                }
                case 5: {
                    bi = tleft.GT(tozero);
                    break;
                }
                case 4: {
                    bi = tleft.LT(tozero);
                    break;
                }
                default: {
                    ErrorMsg msg = new ErrorMsg("ILLEGAL_RELAT_OP_ERR", this);
                    this.getParser().reportError(2, msg);
                }
            }
            this._falseList.add(il.append(bi));
        }
    }

    @Override
    public String toString() {
        return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')';
    }
}

