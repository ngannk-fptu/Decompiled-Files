/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Array;
import ognl.ASTAnd;
import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.ASTOr;
import ognl.ASTProperty;
import ognl.ASTSequence;
import ognl.ASTStaticField;
import ognl.ASTVarRef;
import ognl.DynamicSubscript;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.OrderedReturn;
import ognl.enhance.UnsupportedCompilationException;

public class ASTChain
extends SimpleNode
implements NodeType,
OrderedReturn {
    private Class _getterClass;
    private Class _setterClass;
    private String _lastExpression;
    private String _coreExpression;

    public ASTChain(int id) {
        super(id);
    }

    public ASTChain(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public String getLastExpression() {
        return this._lastExpression;
    }

    @Override
    public String getCoreExpression() {
        return this._coreExpression;
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = source;
        int ilast = this._children.length - 1;
        for (int i = 0; i <= ilast; ++i) {
            ASTProperty indexNode;
            ASTProperty propertyNode;
            int indexType;
            boolean handled = false;
            if (i < ilast && this._children[i] instanceof ASTProperty && (indexType = (propertyNode = (ASTProperty)this._children[i]).getIndexedPropertyType(context, result)) != OgnlRuntime.INDEXED_PROPERTY_NONE && this._children[i + 1] instanceof ASTProperty && (indexNode = (ASTProperty)this._children[i + 1]).isIndexedAccess()) {
                Object index = indexNode.getProperty(context, result);
                if (index instanceof DynamicSubscript) {
                    if (indexType == OgnlRuntime.INDEXED_PROPERTY_INT) {
                        Object array = propertyNode.getValue(context, result);
                        int len = Array.getLength(array);
                        switch (((DynamicSubscript)index).getFlag()) {
                            case 3: {
                                result = Array.newInstance(array.getClass().getComponentType(), len);
                                System.arraycopy(array, 0, result, 0, len);
                                handled = true;
                                ++i;
                                break;
                            }
                            case 0: {
                                index = new Integer(len > 0 ? 0 : -1);
                                break;
                            }
                            case 1: {
                                index = new Integer(len > 0 ? len / 2 : -1);
                                break;
                            }
                            case 2: {
                                index = new Integer(len > 0 ? len - 1 : -1);
                            }
                        }
                    } else if (indexType == OgnlRuntime.INDEXED_PROPERTY_OBJECT) {
                        throw new OgnlException("DynamicSubscript '" + indexNode + "' not allowed for object indexed property '" + propertyNode + "'");
                    }
                }
                if (!handled) {
                    result = OgnlRuntime.getIndexedProperty(context, result, propertyNode.getProperty(context, result).toString(), index);
                    handled = true;
                    ++i;
                }
            }
            if (handled) continue;
            result = this._children[i].getValue(context, result);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        boolean handled = false;
        int ilast = this._children.length - 2;
        for (int i = 0; i <= ilast; ++i) {
            ASTProperty indexNode;
            ASTProperty propertyNode;
            int indexType;
            if (i <= ilast && this._children[i] instanceof ASTProperty && (indexType = (propertyNode = (ASTProperty)this._children[i]).getIndexedPropertyType(context, target)) != OgnlRuntime.INDEXED_PROPERTY_NONE && this._children[i + 1] instanceof ASTProperty && (indexNode = (ASTProperty)this._children[i + 1]).isIndexedAccess()) {
                Object index = indexNode.getProperty(context, target);
                if (index instanceof DynamicSubscript) {
                    if (indexType == OgnlRuntime.INDEXED_PROPERTY_INT) {
                        Object array = propertyNode.getValue(context, target);
                        int len = Array.getLength(array);
                        switch (((DynamicSubscript)index).getFlag()) {
                            case 3: {
                                System.arraycopy(target, 0, value, 0, len);
                                handled = true;
                                ++i;
                                break;
                            }
                            case 0: {
                                index = new Integer(len > 0 ? 0 : -1);
                                break;
                            }
                            case 1: {
                                index = new Integer(len > 0 ? len / 2 : -1);
                                break;
                            }
                            case 2: {
                                index = new Integer(len > 0 ? len - 1 : -1);
                            }
                        }
                    } else if (indexType == OgnlRuntime.INDEXED_PROPERTY_OBJECT) {
                        throw new OgnlException("DynamicSubscript '" + indexNode + "' not allowed for object indexed property '" + propertyNode + "'");
                    }
                }
                if (!handled && i == ilast) {
                    OgnlRuntime.setIndexedProperty(context, target, propertyNode.getProperty(context, target).toString(), index, value);
                    handled = true;
                    ++i;
                } else if (!handled) {
                    target = OgnlRuntime.getIndexedProperty(context, target, propertyNode.getProperty(context, target).toString(), index);
                    ++i;
                    continue;
                }
            }
            if (handled) continue;
            target = this._children[i].getValue(context, target);
        }
        if (!handled) {
            this._children[this._children.length - 1].setValue(context, target, value);
        }
    }

    @Override
    public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
        boolean result = false;
        if (this._children != null && this._children.length > 0) {
            result = true;
            for (int i = 0; result && i < this._children.length; ++i) {
                result = this._children[i] instanceof SimpleNode ? ((SimpleNode)this._children[i]).isSimpleProperty(context) : false;
            }
        }
        return result;
    }

    @Override
    public Class getGetterClass() {
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return this._setterClass;
    }

    @Override
    public String toString() {
        String result = "";
        if (this._children != null && this._children.length > 0) {
            for (int i = 0; i < this._children.length; ++i) {
                if (!(i <= 0 || this._children[i] instanceof ASTProperty && ((ASTProperty)this._children[i]).isIndexedAccess())) {
                    result = result + ".";
                }
                result = result + this._children[i].toString();
            }
        }
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String prevChain = (String)context.get("_currentChain");
        if (target != null) {
            context.setCurrentObject(target);
            context.setCurrentType(target.getClass());
        }
        String result = "";
        NodeType _lastType = null;
        boolean ordered = false;
        boolean constructor = false;
        try {
            if (this._children != null && this._children.length > 0) {
                for (int i = 0; i < this._children.length; ++i) {
                    String value = this._children[i].toGetSourceString(context, context.getCurrentObject());
                    if (ASTCtor.class.isInstance(this._children[i])) {
                        constructor = true;
                    }
                    if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null) {
                        _lastType = (NodeType)((Object)this._children[i]);
                    }
                    if (!(ASTVarRef.class.isInstance(this._children[i]) || constructor || OrderedReturn.class.isInstance(this._children[i]) && ((OrderedReturn)((Object)this._children[i])).getLastExpression() != null || this._parent != null && ASTSequence.class.isInstance(this._parent))) {
                        value = OgnlRuntime.getCompiler().castExpression(context, this._children[i], value);
                    }
                    if (OrderedReturn.class.isInstance(this._children[i]) && ((OrderedReturn)((Object)this._children[i])).getLastExpression() != null) {
                        ordered = true;
                        OrderedReturn or = (OrderedReturn)((Object)this._children[i]);
                        result = or.getCoreExpression() == null || or.getCoreExpression().trim().length() <= 0 ? "" : result + or.getCoreExpression();
                        this._lastExpression = or.getLastExpression();
                        if (context.get("_preCast") != null) {
                            this._lastExpression = context.remove("_preCast") + this._lastExpression;
                        }
                    } else if (ASTOr.class.isInstance(this._children[i]) || ASTAnd.class.isInstance(this._children[i]) || ASTCtor.class.isInstance(this._children[i]) || ASTStaticField.class.isInstance(this._children[i]) && this._parent == null) {
                        context.put("_noRoot", "true");
                        result = value;
                    } else {
                        result = result + value;
                    }
                    context.put("_currentChain", result);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (_lastType != null) {
            this._getterClass = _lastType.getGetterClass();
            this._setterClass = _lastType.getSetterClass();
        }
        if (ordered) {
            this._coreExpression = result;
        }
        context.put("_currentChain", prevChain);
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String prevChain = (String)context.get("_currentChain");
        String prevChild = (String)context.get("_lastChild");
        if (prevChain != null) {
            throw new UnsupportedCompilationException("Can't compile nested chain expressions.");
        }
        if (target != null) {
            context.setCurrentObject(target);
            context.setCurrentType(target.getClass());
        }
        String result = "";
        NodeType _lastType = null;
        boolean constructor = false;
        try {
            if (this._children != null && this._children.length > 0) {
                if (ASTConst.class.isInstance(this._children[0])) {
                    throw new UnsupportedCompilationException("Can't modify constant values.");
                }
                for (int i = 0; i < this._children.length; ++i) {
                    if (i == this._children.length - 1) {
                        context.put("_lastChild", "true");
                    }
                    String value = this._children[i].toSetSourceString(context, context.getCurrentObject());
                    if (ASTCtor.class.isInstance(this._children[i])) {
                        constructor = true;
                    }
                    if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null) {
                        _lastType = (NodeType)((Object)this._children[i]);
                    }
                    if (!(ASTVarRef.class.isInstance(this._children[i]) || constructor || OrderedReturn.class.isInstance(this._children[i]) && ((OrderedReturn)((Object)this._children[i])).getLastExpression() != null || this._parent != null && ASTSequence.class.isInstance(this._parent))) {
                        value = OgnlRuntime.getCompiler().castExpression(context, this._children[i], value);
                    }
                    if (ASTOr.class.isInstance(this._children[i]) || ASTAnd.class.isInstance(this._children[i]) || ASTCtor.class.isInstance(this._children[i]) || ASTStaticField.class.isInstance(this._children[i])) {
                        context.put("_noRoot", "true");
                        result = value;
                    } else {
                        result = result + value;
                    }
                    context.put("_currentChain", result);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.put("_lastChild", prevChild);
        context.put("_currentChain", prevChain);
        if (_lastType != null) {
            this._setterClass = _lastType.getSetterClass();
        }
        return result;
    }

    @Override
    public boolean isChain(OgnlContext context) {
        return true;
    }
}

