/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.math.BigDecimal;
import java.math.BigInteger;
import ognl.ASTProperty;
import ognl.ExpressionNode;
import ognl.Node;
import ognl.NodeType;
import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.UnsupportedCompilationException;

public class ASTConst
extends SimpleNode
implements NodeType {
    private Object value;
    private Class _getterClass;

    public ASTConst(int id) {
        super(id);
    }

    public ASTConst(OgnlParser p, int id) {
        super(p, id);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return this.value;
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return true;
    }

    @Override
    public Class getGetterClass() {
        if (this._getterClass == null) {
            return null;
        }
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return null;
    }

    @Override
    public String toString() {
        String result;
        if (this.value == null) {
            result = "null";
        } else if (this.value instanceof String) {
            result = '\"' + OgnlOps.getEscapeString(this.value.toString()) + '\"';
        } else if (this.value instanceof Character) {
            result = '\'' + OgnlOps.getEscapedChar(((Character)this.value).charValue()) + '\'';
        } else {
            result = this.value.toString();
            if (this.value instanceof Long) {
                result = result + "L";
            } else if (this.value instanceof BigDecimal) {
                result = result + "B";
            } else if (this.value instanceof BigInteger) {
                result = result + "H";
            } else if (this.value instanceof Node) {
                result = ":[ " + result + " ]";
            }
        }
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (this.value == null && this._parent != null && ExpressionNode.class.isInstance(this._parent)) {
            context.setCurrentType(null);
            return "null";
        }
        if (this.value == null) {
            context.setCurrentType(null);
            return "";
        }
        this._getterClass = this.value.getClass();
        Object retval = this.value;
        if (this._parent != null && ASTProperty.class.isInstance(this._parent)) {
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        if (this.value != null && Number.class.isAssignableFrom(this.value.getClass())) {
            context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(this.value.getClass()));
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        if ((this._parent == null || this.value == null || !NumericExpression.class.isAssignableFrom(this._parent.getClass())) && String.class.isAssignableFrom(this.value.getClass())) {
            context.setCurrentType(String.class);
            retval = '\"' + OgnlOps.getEscapeString(this.value.toString()) + '\"';
            context.setCurrentObject(retval.toString());
            return retval.toString();
        }
        if (Character.class.isInstance(this.value)) {
            Character val = (Character)this.value;
            context.setCurrentType(Character.class);
            retval = Character.isLetterOrDigit(val.charValue()) ? "'" + ((Character)this.value).charValue() + "'" : "'" + OgnlOps.getEscapedChar(((Character)this.value).charValue()) + "'";
            context.setCurrentObject(retval);
            return retval.toString();
        }
        if (Boolean.class.isAssignableFrom(this.value.getClass())) {
            this._getterClass = Boolean.TYPE;
            context.setCurrentType(Boolean.TYPE);
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        return this.value.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this._parent == null) {
            throw new UnsupportedCompilationException("Can't modify constant values.");
        }
        return this.toGetSourceString(context, target);
    }
}

