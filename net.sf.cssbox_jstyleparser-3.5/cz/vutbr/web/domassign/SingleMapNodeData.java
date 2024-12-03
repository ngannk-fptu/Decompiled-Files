/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.domassign.BaseNodeDataImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SingleMapNodeData
extends BaseNodeDataImpl {
    private static final int COMMON_DECLARATION_SIZE = 7;
    private Map<String, Quadruple> map = new HashMap<String, Quadruple>(css.getTotalProperties(), 1.0f);

    @Override
    public <T extends CSSProperty> T getProperty(String name) {
        return this.getProperty(name, true);
    }

    @Override
    public <T extends CSSProperty> T getProperty(String name, boolean includeInherited) {
        Quadruple q = this.map.get(name);
        if (q == null) {
            return null;
        }
        CSSProperty tmp = includeInherited ? (q.curProp != null ? q.curProp : q.inhProp) : q.curProp;
        CSSProperty retval = tmp;
        return (T)retval;
    }

    @Override
    public Term<?> getValue(String name, boolean includeInherited) {
        Quadruple q = this.map.get(name);
        if (q == null) {
            return null;
        }
        if (includeInherited) {
            if (q.curProp != null) {
                return q.curValue;
            }
            return q.inhValue;
        }
        return q.curValue;
    }

    @Override
    public <T extends Term<?>> T getValue(Class<T> clazz, String name) {
        return this.getValue(clazz, name, true);
    }

    @Override
    public String getAsString(String name, boolean includeInherited) {
        Quadruple q = this.map.get(name);
        if (q == null) {
            return null;
        }
        CSSProperty prop = q.curProp;
        Term<?> value = q.curValue;
        if (prop == null && includeInherited) {
            prop = q.inhProp;
            value = q.inhValue;
        }
        return value == null ? prop.toString() : value.toString();
    }

    @Override
    public <T extends Term<?>> T getValue(Class<T> clazz, String name, boolean includeInherited) {
        Quadruple q = this.map.get(name);
        if (q == null) {
            return null;
        }
        if (includeInherited) {
            if (q.curProp != null) {
                return (T)((Term)clazz.cast(q.curValue));
            }
            return (T)((Term)clazz.cast(q.inhValue));
        }
        return (T)((Term)clazz.cast(q.curValue));
    }

    @Override
    public NodeData push(Declaration d) {
        HashMap<String, CSSProperty> properties = new HashMap<String, CSSProperty>(7);
        HashMap terms = new HashMap(7);
        boolean result = transformer.parseDeclaration(d, properties, terms);
        if (!result) {
            return this;
        }
        for (Map.Entry entry : properties.entrySet()) {
            String key = (String)entry.getKey();
            Quadruple q = this.map.get(key);
            if (q == null) {
                q = new Quadruple();
            }
            q.curProp = (CSSProperty)entry.getValue();
            q.curValue = (Term)terms.get(key);
            q.curSource = d;
            if (q.curValue != null && q.curValue.getOperator() != null) {
                q.curValue = q.curValue.shallowClone().setOperator(null);
            }
            this.map.put(key, q);
        }
        return this;
    }

    @Override
    public NodeData concretize() {
        for (Map.Entry<String, Quadruple> entry : this.map.entrySet()) {
            String key = entry.getKey();
            Quadruple q = entry.getValue();
            if (q.curProp == null) continue;
            if (q.curProp.equalsInherit()) {
                if (q.inhProp == null) {
                    q.curProp = css.getDefaultProperty(key);
                } else {
                    q.curProp = q.inhProp;
                    q.curSource = q.inhSource;
                }
                q.curValue = q.inhValue == null ? css.getDefaultValue(key) : q.inhValue;
                this.map.put(key, q);
                continue;
            }
            if (q.curProp.equalsInitial()) {
                q.curProp = css.getDefaultProperty(key);
                q.curValue = css.getDefaultValue(key);
                this.map.put(key, q);
                continue;
            }
            if (!q.curProp.equalsUnset()) continue;
            if (q.curProp.inherited()) {
                q.curProp = q.inhProp == null ? css.getDefaultProperty(key) : q.inhProp;
                q.curValue = q.inhValue == null ? css.getDefaultValue(key) : q.inhValue;
            } else {
                q.curProp = css.getDefaultProperty(key);
                q.curValue = css.getDefaultValue(key);
            }
            this.map.put(key, q);
        }
        return this;
    }

    @Override
    public NodeData inheritFrom(NodeData parent) throws ClassCastException {
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof SingleMapNodeData)) {
            throw new ClassCastException("Cant't inherit from NodeData different from " + this.getClass().getName() + "(" + parent.getClass().getName() + ")");
        }
        SingleMapNodeData nd = (SingleMapNodeData)parent;
        for (Map.Entry<String, Quadruple> entry : nd.map.entrySet()) {
            String key = entry.getKey();
            Quadruple qp = entry.getValue();
            Quadruple q = this.map.get(key);
            if (q == null) {
                q = new Quadruple();
            }
            boolean forceInherit = q.curProp != null && q.curProp.equalsInherit();
            boolean changed = false;
            if (qp.inhProp != null && (qp.inhProp.inherited() || forceInherit)) {
                q.inhProp = qp.inhProp;
                q.inhValue = qp.inhValue;
                q.inhSource = qp.inhSource;
                changed = true;
            }
            if (qp.curProp != null && (qp.curProp.inherited() || forceInherit)) {
                q.inhProp = qp.curProp;
                q.inhValue = qp.curValue;
                q.inhSource = qp.curSource;
                changed = true;
            }
            if (!changed || q.isEmpty()) continue;
            this.map.put(key, q);
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(this.map.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Term<?> value;
            Quadruple q = this.map.get(key);
            CSSProperty prop = q.curProp;
            if (prop == null) {
                prop = q.inhProp;
            }
            if ((value = q.curValue) == null) {
                value = q.inhValue;
            }
            sb.append(key).append(": ");
            if (value != null) {
                sb.append(value.toString());
            } else {
                sb.append(prop.toString());
            }
            sb.append(";\n");
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getPropertyNames() {
        ArrayList<String> keys = new ArrayList<String>();
        keys.addAll(this.map.keySet());
        return keys;
    }

    @Override
    public Declaration getSourceDeclaration(String name) {
        return this.getSourceDeclaration(name, true);
    }

    @Override
    public Declaration getSourceDeclaration(String name, boolean includeInherited) {
        Quadruple q = this.map.get(name);
        if (q == null) {
            return null;
        }
        if (includeInherited) {
            if (q.curSource != null) {
                return q.curSource;
            }
            return q.inhSource;
        }
        return q.curSource;
    }

    static class Quadruple {
        CSSProperty inhProp = null;
        CSSProperty curProp = null;
        Term<?> inhValue = null;
        Term<?> curValue = null;
        Declaration inhSource = null;
        Declaration curSource = null;

        public boolean isEmpty() {
            return this.inhProp == null && this.curProp == null && this.inhValue == null && this.curValue == null;
        }
    }
}

