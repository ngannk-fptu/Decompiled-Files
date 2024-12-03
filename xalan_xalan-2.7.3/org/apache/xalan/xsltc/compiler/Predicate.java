/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.ArrayList;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.xalan.xsltc.compiler.AbsoluteLocationPath;
import org.apache.xalan.xsltc.compiler.BooleanExpr;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Closure;
import org.apache.xalan.xsltc.compiler.EqualityExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.PositionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.Step;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.VariableRefBase;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.FilterGenerator;
import org.apache.xalan.xsltc.compiler.util.IntType;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NumberType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.ResultTreeType;
import org.apache.xalan.xsltc.compiler.util.TestGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Predicate
extends Expression
implements Closure {
    private Expression _exp = null;
    private boolean _canOptimize = true;
    private boolean _nthPositionFilter = false;
    private boolean _nthDescendant = false;
    int _ptype = -1;
    private String _className = null;
    private ArrayList _closureVars = null;
    private Closure _parentClosure = null;
    private Expression _value = null;
    private Step _step = null;

    public Predicate(Expression exp) {
        this._exp = exp;
        this._exp.setParent(this);
    }

    @Override
    public void setParser(Parser parser) {
        super.setParser(parser);
        this._exp.setParser(parser);
    }

    public boolean isNthPositionFilter() {
        return this._nthPositionFilter;
    }

    public boolean isNthDescendant() {
        return this._nthDescendant;
    }

    public void dontOptimize() {
        this._canOptimize = false;
    }

    @Override
    public boolean hasPositionCall() {
        return this._exp.hasPositionCall();
    }

    @Override
    public boolean hasLastCall() {
        return this._exp.hasLastCall();
    }

    @Override
    public boolean inInnerClass() {
        return this._className != null;
    }

    @Override
    public Closure getParentClosure() {
        if (this._parentClosure == null) {
            SyntaxTreeNode node = this.getParent();
            do {
                if (!(node instanceof Closure)) continue;
                this._parentClosure = (Closure)((Object)node);
                break;
            } while (!(node instanceof TopLevelElement) && (node = node.getParent()) != null);
        }
        return this._parentClosure;
    }

    @Override
    public String getInnerClassName() {
        return this._className;
    }

    @Override
    public void addVariable(VariableRefBase variableRef) {
        if (this._closureVars == null) {
            this._closureVars = new ArrayList();
        }
        if (!this._closureVars.contains(variableRef)) {
            this._closureVars.add(variableRef);
            Closure parentClosure = this.getParentClosure();
            if (parentClosure != null) {
                parentClosure.addVariable(variableRef);
            }
        }
    }

    public int getPosType() {
        if (this._ptype == -1) {
            SyntaxTreeNode parent = this.getParent();
            if (parent instanceof StepPattern) {
                this._ptype = ((StepPattern)parent).getNodeType();
            } else if (parent instanceof AbsoluteLocationPath) {
                AbsoluteLocationPath path = (AbsoluteLocationPath)parent;
                Expression exp = path.getPath();
                if (exp instanceof Step) {
                    this._ptype = ((Step)exp).getNodeType();
                }
            } else if (parent instanceof VariableRefBase) {
                VariableRefBase ref = (VariableRefBase)parent;
                VariableBase var = ref.getVariable();
                Expression exp = var.getExpression();
                if (exp instanceof Step) {
                    this._ptype = ((Step)exp).getNodeType();
                }
            } else if (parent instanceof Step) {
                this._ptype = ((Step)parent).getNodeType();
            }
        }
        return this._ptype;
    }

    public boolean parentIsPattern() {
        return this.getParent() instanceof Pattern;
    }

    public Expression getExpr() {
        return this._exp;
    }

    @Override
    public String toString() {
        return "pred(" + this._exp + ')';
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type texp = this._exp.typeCheck(stable);
        if (texp instanceof ReferenceType) {
            texp = Type.Real;
            this._exp = new CastExpr(this._exp, texp);
        }
        if (texp instanceof ResultTreeType) {
            this._exp = new CastExpr(this._exp, Type.Boolean);
            this._exp = new CastExpr(this._exp, Type.Real);
            texp = this._exp.typeCheck(stable);
        }
        if (texp instanceof NumberType) {
            if (!(texp instanceof IntType)) {
                this._exp = new CastExpr(this._exp, Type.Int);
            }
            if (this._canOptimize) {
                boolean bl = this._nthPositionFilter = !this._exp.hasLastCall() && !this._exp.hasPositionCall();
                if (this._nthPositionFilter) {
                    SyntaxTreeNode parent = this.getParent();
                    this._nthDescendant = parent instanceof Step && parent.getParent() instanceof AbsoluteLocationPath;
                    this._type = Type.NodeSet;
                    return this._type;
                }
            }
            this._nthDescendant = false;
            this._nthPositionFilter = false;
            QName position = this.getParser().getQNameIgnoreDefaultNs("position");
            PositionCall positionCall = new PositionCall(position);
            positionCall.setParser(this.getParser());
            positionCall.setParent(this);
            this._exp = new EqualityExpr(0, positionCall, this._exp);
            if (this._exp.typeCheck(stable) != Type.Boolean) {
                this._exp = new CastExpr(this._exp, Type.Boolean);
            }
            this._type = Type.Boolean;
            return this._type;
        }
        if (!(texp instanceof BooleanType)) {
            this._exp = new CastExpr(this._exp, Type.Boolean);
        }
        this._type = Type.Boolean;
        return this._type;
    }

    private void compileFilter(ClassGenerator classGen, MethodGenerator methodGen) {
        this._className = this.getXSLTC().getHelperClassName();
        FilterGenerator filterGen = new FilterGenerator(this._className, "java.lang.Object", this.toString(), 33, new String[]{"org.apache.xalan.xsltc.dom.CurrentNodeListFilter"}, classGen.getStylesheet());
        ConstantPoolGen cpg = filterGen.getConstantPool();
        int length = this._closureVars == null ? 0 : this._closureVars.size();
        for (int i = 0; i < length; ++i) {
            VariableBase var = ((VariableRefBase)this._closureVars.get(i)).getVariable();
            filterGen.addField(new Field(1, cpg.addUtf8(var.getEscapedName()), cpg.addUtf8(var.getType().toSignature()), null, cpg.getConstantPool()));
        }
        InstructionList il = new InstructionList();
        TestGenerator testGen = new TestGenerator(17, org.apache.bcel.generic.Type.BOOLEAN, new org.apache.bcel.generic.Type[]{org.apache.bcel.generic.Type.INT, org.apache.bcel.generic.Type.INT, org.apache.bcel.generic.Type.INT, org.apache.bcel.generic.Type.INT, Util.getJCRefType("Lorg/apache/xalan/xsltc/runtime/AbstractTranslet;"), Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;")}, new String[]{"node", "position", "last", "current", "translet", "iterator"}, "test", this._className, il, cpg);
        LocalVariableGen local = testGen.addLocalVariable("document", Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), null, null);
        String className = classGen.getClassName();
        il.append(filterGen.loadTranslet());
        il.append(new CHECKCAST(cpg.addClass(className)));
        il.append(new GETFIELD(cpg.addFieldref(className, "_dom", "Lorg/apache/xalan/xsltc/DOM;")));
        local.setStart(il.append(new ASTORE(local.getIndex())));
        testGen.setDomIndex(local.getIndex());
        this._exp.translate(filterGen, testGen);
        il.append(IRETURN);
        filterGen.addEmptyConstructor(1);
        filterGen.addMethod(testGen);
        this.getXSLTC().dumpClass(filterGen.getJavaClass());
    }

    public boolean isBooleanTest() {
        return this._exp instanceof BooleanExpr;
    }

    public boolean isNodeValueTest() {
        if (!this._canOptimize) {
            return false;
        }
        return this.getStep() != null && this.getCompareValue() != null;
    }

    public Step getStep() {
        if (this._step != null) {
            return this._step;
        }
        if (this._exp == null) {
            return null;
        }
        if (this._exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr)this._exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();
            if (left instanceof CastExpr) {
                left = ((CastExpr)left).getExpr();
            }
            if (left instanceof Step) {
                this._step = (Step)left;
            }
            if (right instanceof CastExpr) {
                right = ((CastExpr)right).getExpr();
            }
            if (right instanceof Step) {
                this._step = (Step)right;
            }
        }
        return this._step;
    }

    public Expression getCompareValue() {
        if (this._value != null) {
            return this._value;
        }
        if (this._exp == null) {
            return null;
        }
        if (this._exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr)this._exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();
            if (left instanceof LiteralExpr) {
                this._value = left;
                return this._value;
            }
            if (left instanceof VariableRefBase && left.getType() == Type.String) {
                this._value = left;
                return this._value;
            }
            if (right instanceof LiteralExpr) {
                this._value = right;
                return this._value;
            }
            if (right instanceof VariableRefBase && right.getType() == Type.String) {
                this._value = right;
                return this._value;
            }
        }
        return null;
    }

    public void translateFilter(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this.compileFilter(classGen, methodGen);
        il.append(new NEW(cpg.addClass(this._className)));
        il.append(DUP);
        il.append(new INVOKESPECIAL(cpg.addMethodref(this._className, "<init>", "()V")));
        int length = this._closureVars == null ? 0 : this._closureVars.size();
        for (int i = 0; i < length; ++i) {
            Closure variableClosure;
            VariableRefBase varRef = (VariableRefBase)this._closureVars.get(i);
            VariableBase var = varRef.getVariable();
            Type varType = var.getType();
            il.append(DUP);
            for (variableClosure = this._parentClosure; variableClosure != null && !variableClosure.inInnerClass(); variableClosure = variableClosure.getParentClosure()) {
            }
            if (variableClosure != null) {
                il.append(ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref(variableClosure.getInnerClassName(), var.getEscapedName(), varType.toSignature())));
            } else {
                il.append(var.loadInstruction());
            }
            il.append(new PUTFIELD(cpg.addFieldref(this._className, var.getEscapedName(), varType.toSignature())));
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._nthPositionFilter || this._nthDescendant) {
            this._exp.translate(classGen, methodGen);
        } else if (this.isNodeValueTest() && this.getParent() instanceof Step) {
            this._value.translate(classGen, methodGen);
            il.append(new CHECKCAST(cpg.addClass("java.lang.String")));
            il.append(new PUSH(cpg, ((EqualityExpr)this._exp).getOp()));
        } else {
            this.translateFilter(classGen, methodGen);
        }
    }
}

