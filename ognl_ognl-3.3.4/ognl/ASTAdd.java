/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTMethod;
import ognl.ASTProperty;
import ognl.ASTSequence;
import ognl.ASTStaticField;
import ognl.ASTStaticMethod;
import ognl.ASTTest;
import ognl.ASTVarRef;
import ognl.ExpressionNode;
import ognl.NodeType;
import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.enhance.ExpressionCompiler;

public class ASTAdd
extends NumericExpression {
    public ASTAdd(int id) {
        super(id);
    }

    public ASTAdd(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = this._children[0].getValue(context, source);
        for (int i = 1; i < this._children.length; ++i) {
            result = OgnlOps.add(result, this._children[i].getValue(context, source));
        }
        return result;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "+";
    }

    boolean isWider(NodeType type, NodeType lastType) {
        if (lastType == null) {
            return true;
        }
        if (String.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (String.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (this._parent != null && String.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (String.class.isAssignableFrom(lastType.getGetterClass()) && Object.class == type.getGetterClass()) {
            return false;
        }
        if (this._parent != null && String.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (this._parent == null && String.class.isAssignableFrom(lastType.getGetterClass())) {
            return true;
        }
        if (this._parent == null && String.class.isAssignableFrom(type.getGetterClass())) {
            return false;
        }
        if (BigDecimal.class.isAssignableFrom(type.getGetterClass()) || BigInteger.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (BigDecimal.class.isAssignableFrom(lastType.getGetterClass()) || BigInteger.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (Double.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (Integer.class.isAssignableFrom(type.getGetterClass()) && Double.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (Float.class.isAssignableFrom(type.getGetterClass()) && Integer.class.isAssignableFrom(lastType.getGetterClass())) {
            return true;
        }
        return true;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String result = "";
            NodeType lastType = null;
            if (this._children != null && this._children.length > 0) {
                Class currType = context.getCurrentType();
                Class currAccessor = context.getCurrentAccessor();
                Object cast = context.get("_preCast");
                for (int i = 0; i < this._children.length; ++i) {
                    this._children[i].toGetSourceString(context, target);
                    if (!NodeType.class.isInstance(this._children[i]) || ((NodeType)((Object)this._children[i])).getGetterClass() == null || !this.isWider((NodeType)((Object)this._children[i]), lastType)) continue;
                    lastType = (NodeType)((Object)this._children[i]);
                }
                context.put("_preCast", cast);
                context.setCurrentType(currType);
                context.setCurrentAccessor(currAccessor);
            }
            context.setCurrentObject(target);
            if (this._children != null && this._children.length > 0) {
                for (int i = 0; i < this._children.length; ++i) {
                    String expr;
                    if (i > 0) {
                        result = result + " " + this.getExpressionOperator(i) + " ";
                    }
                    if ((expr = this._children[i].toGetSourceString(context, target)) != null && "null".equals(expr) || !ASTConst.class.isInstance(this._children[i]) && (expr == null || expr.trim().length() <= 0)) {
                        expr = "null";
                    }
                    if (ASTProperty.class.isInstance(this._children[i])) {
                        expr = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context) + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                    } else if (ASTMethod.class.isInstance(this._children[i])) {
                        String chain = (String)context.get("_currentChain");
                        String rootExpr = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context);
                        if (rootExpr.endsWith(".") && chain != null && chain.startsWith(").")) {
                            chain = chain.substring(1, chain.length());
                        }
                        expr = rootExpr + (chain != null ? chain + "." : "") + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                    } else if (ExpressionNode.class.isInstance(this._children[i])) {
                        expr = "(" + expr + ")";
                    } else if ((this._parent == null || !ASTChain.class.isInstance(this._parent)) && ASTChain.class.isInstance(this._children[i])) {
                        String rootExpr = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context);
                        if (!ASTProperty.class.isInstance(this._children[i].jjtGetChild(0)) && rootExpr.endsWith(")") && expr.startsWith(")")) {
                            expr = expr.substring(1, expr.length());
                        }
                        expr = rootExpr + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                        String cast = (String)context.remove("_preCast");
                        if (cast == null) {
                            cast = "";
                        }
                        expr = cast + expr;
                    }
                    if (context.getCurrentType() != null && context.getCurrentType() == Character.class && ASTConst.class.isInstance(this._children[i])) {
                        if (expr.indexOf(39) >= 0) {
                            expr = expr.replaceAll("'", "\"");
                        }
                        context.setCurrentType(String.class);
                    } else if (!(ASTVarRef.class.isAssignableFrom(this._children[i].getClass()) || ASTProperty.class.isInstance(this._children[i]) || ASTMethod.class.isInstance(this._children[i]) || ASTSequence.class.isInstance(this._children[i]) || ASTChain.class.isInstance(this._children[i]) || NumericExpression.class.isAssignableFrom(this._children[i].getClass()) || ASTStaticField.class.isInstance(this._children[i]) || ASTStaticMethod.class.isInstance(this._children[i]) || ASTTest.class.isInstance(this._children[i]) || lastType == null || !String.class.isAssignableFrom(lastType.getGetterClass()))) {
                        if (expr.indexOf("&quot;") >= 0) {
                            expr = expr.replaceAll("&quot;", "\"");
                        }
                        if (expr.indexOf(34) >= 0) {
                            expr = expr.replaceAll("\"", "'");
                        }
                        expr = "\"" + expr + "\"";
                    }
                    result = result + expr;
                    if (!(lastType != null && String.class.isAssignableFrom(lastType.getGetterClass()) || ASTConst.class.isAssignableFrom(this._children[i].getClass()) || NumericExpression.class.isAssignableFrom(this._children[i].getClass()) || context.getCurrentType() == null || !Number.class.isAssignableFrom(context.getCurrentType()) || ASTMethod.class.isInstance(this._children[i]))) {
                        if (ASTVarRef.class.isInstance(this._children[i]) || ASTProperty.class.isInstance(this._children[i]) || ASTChain.class.isInstance(this._children[i])) {
                            result = result + ".";
                        }
                        result = result + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
                        context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentType()));
                    }
                    if (lastType == null) continue;
                    context.setCurrentAccessor(lastType.getGetterClass());
                }
            }
            if (this._parent == null || ASTSequence.class.isAssignableFrom(this._parent.getClass())) {
                if (this._getterClass != null && String.class.isAssignableFrom(this._getterClass)) {
                    this._getterClass = Object.class;
                }
            } else {
                context.setCurrentType(this._getterClass);
            }
            try {
                Object contextObj = this.getValueBody(context, target);
                context.setCurrentObject(contextObj);
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
            return result;
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

