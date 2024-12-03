/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.io.PrintWriter;
import java.io.Serializable;
import ognl.Evaluation;
import ognl.EvaluationPool;
import ognl.InappropriateExpressionException;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.OgnlParserTreeConstants;
import ognl.OgnlRuntime;
import ognl.enhance.ExpressionAccessor;

public abstract class SimpleNode
implements Node,
Serializable {
    protected Node _parent;
    protected Node[] _children;
    protected int _id;
    protected OgnlParser _parser;
    private boolean _constantValueCalculated;
    private volatile boolean _hasConstantValue;
    private Object _constantValue;
    private ExpressionAccessor _accessor;

    public SimpleNode(int i) {
        this._id = i;
    }

    public SimpleNode(OgnlParser p, int i) {
        this(i);
        this._parser = p;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        this._parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return this._parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (this._children == null) {
            this._children = new Node[i + 1];
        } else if (i >= this._children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(this._children, 0, c, 0, this._children.length);
            this._children = c;
        }
        this._children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return this._children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return this._children == null ? 0 : this._children.length;
    }

    public String toString() {
        return OgnlParserTreeConstants.jjtNodeName[this._id];
    }

    public String toString(String prefix) {
        return prefix + OgnlParserTreeConstants.jjtNodeName[this._id] + " " + this.toString();
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        return this.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toString();
    }

    public void dump(PrintWriter writer, String prefix) {
        writer.println(this.toString(prefix));
        if (this._children != null) {
            for (int i = 0; i < this._children.length; ++i) {
                SimpleNode n = (SimpleNode)this._children[i];
                if (n == null) continue;
                n.dump(writer, prefix + "  ");
            }
        }
    }

    public int getIndexInParent() {
        int result = -1;
        if (this._parent != null) {
            int icount = this._parent.jjtGetNumChildren();
            for (int i = 0; i < icount; ++i) {
                if (this._parent.jjtGetChild(i) != this) continue;
                result = i;
                break;
            }
        }
        return result;
    }

    public Node getNextSibling() {
        int icount;
        Node result = null;
        int i = this.getIndexInParent();
        if (i >= 0 && i < (icount = this._parent.jjtGetNumChildren())) {
            result = this._parent.jjtGetChild(i + 1);
        }
        return result;
    }

    protected Object evaluateGetValueBody(OgnlContext context, Object source) throws OgnlException {
        context.setCurrentObject(source);
        context.setCurrentNode(this);
        if (!this._constantValueCalculated) {
            this._constantValueCalculated = true;
            boolean constant = this.isConstant(context);
            if (constant) {
                this._constantValue = this.getValueBody(context, source);
            }
            this._hasConstantValue = constant;
        }
        return this._hasConstantValue ? this._constantValue : this.getValueBody(context, source);
    }

    protected void evaluateSetValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setCurrentObject(target);
        context.setCurrentNode(this);
        this.setValueBody(context, target, value);
    }

    @Override
    public final Object getValue(OgnlContext context, Object source) throws OgnlException {
        Object result = null;
        if (context.getTraceEvaluations()) {
            EvaluationPool pool = OgnlRuntime.getEvaluationPool();
            Exception evalException = null;
            Evaluation evaluation = pool.create(this, source);
            context.pushEvaluation(evaluation);
            try {
                result = this.evaluateGetValueBody(context, source);
            }
            catch (OgnlException ex) {
                evalException = ex;
                throw ex;
            }
            catch (RuntimeException ex) {
                evalException = ex;
                throw ex;
            }
            finally {
                Evaluation eval = context.popEvaluation();
                eval.setResult(result);
                if (evalException != null) {
                    eval.setException(evalException);
                }
                if (evalException == null && context.getRootEvaluation() == null && !context.getKeepLastEvaluation()) {
                    pool.recycleAll(eval);
                }
            }
        } else {
            result = this.evaluateGetValueBody(context, source);
        }
        return result;
    }

    protected abstract Object getValueBody(OgnlContext var1, Object var2) throws OgnlException;

    @Override
    public final void setValue(OgnlContext context, Object target, Object value) throws OgnlException {
        if (context.getTraceEvaluations()) {
            EvaluationPool pool = OgnlRuntime.getEvaluationPool();
            Exception evalException = null;
            Evaluation evaluation = pool.create(this, target, true);
            context.pushEvaluation(evaluation);
            try {
                this.evaluateSetValueBody(context, target, value);
            }
            catch (OgnlException ex) {
                evalException = ex;
                ex.setEvaluation(evaluation);
                throw ex;
            }
            catch (RuntimeException ex) {
                evalException = ex;
                throw ex;
            }
            finally {
                Evaluation eval = context.popEvaluation();
                if (evalException != null) {
                    eval.setException(evalException);
                }
                if (evalException == null && context.getRootEvaluation() == null && !context.getKeepLastEvaluation()) {
                    pool.recycleAll(eval);
                }
            }
        } else {
            this.evaluateSetValueBody(context, target, value);
        }
    }

    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        throw new InappropriateExpressionException(this);
    }

    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return false;
    }

    public boolean isConstant(OgnlContext context) throws OgnlException {
        return this.isNodeConstant(context);
    }

    public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
        return false;
    }

    public boolean isSimpleProperty(OgnlContext context) throws OgnlException {
        return this.isNodeSimpleProperty(context);
    }

    public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
        return this.isSimpleProperty(context);
    }

    public boolean isEvalChain(OgnlContext context) throws OgnlException {
        if (this._children == null) {
            return false;
        }
        for (Node child : this._children) {
            if (!(child instanceof SimpleNode) || !((SimpleNode)child).isEvalChain(context)) continue;
            return true;
        }
        return false;
    }

    public boolean isSequence(OgnlContext context) throws OgnlException {
        if (this._children == null) {
            return false;
        }
        for (Node child : this._children) {
            if (!(child instanceof SimpleNode) || !((SimpleNode)child).isSequence(context)) continue;
            return true;
        }
        return false;
    }

    public boolean isOperation(OgnlContext context) throws OgnlException {
        if (this._children == null) {
            return false;
        }
        for (Node child : this._children) {
            if (!(child instanceof SimpleNode) || !((SimpleNode)child).isOperation(context)) continue;
            return true;
        }
        return false;
    }

    public boolean isChain(OgnlContext context) throws OgnlException {
        if (this._children == null) {
            return false;
        }
        for (Node child : this._children) {
            if (!(child instanceof SimpleNode) || !((SimpleNode)child).isChain(context)) continue;
            return true;
        }
        return false;
    }

    public boolean isSimpleMethod(OgnlContext context) throws OgnlException {
        return false;
    }

    protected boolean lastChild(OgnlContext context) {
        return this._parent == null || context.get("_lastChild") != null;
    }

    protected void flattenTree() {
        boolean shouldFlatten = false;
        int newSize = 0;
        for (int i = 0; i < this._children.length; ++i) {
            if (this._children[i].getClass() == this.getClass()) {
                shouldFlatten = true;
                newSize += this._children[i].jjtGetNumChildren();
                continue;
            }
            ++newSize;
        }
        if (shouldFlatten) {
            Node[] newChildren = new Node[newSize];
            int j = 0;
            for (int i = 0; i < this._children.length; ++i) {
                Node c = this._children[i];
                if (c.getClass() == this.getClass()) {
                    for (int k = 0; k < c.jjtGetNumChildren(); ++k) {
                        newChildren[j++] = c.jjtGetChild(k);
                    }
                    continue;
                }
                newChildren[j++] = c;
            }
            if (j != newSize) {
                throw new Error("Assertion error: " + j + " != " + newSize);
            }
            this._children = newChildren;
        }
    }

    @Override
    public ExpressionAccessor getAccessor() {
        return this._accessor;
    }

    @Override
    public void setAccessor(ExpressionAccessor accessor) {
        this._accessor = accessor;
    }
}

