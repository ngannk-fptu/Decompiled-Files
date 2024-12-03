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
import java.util.LinkedHashSet;
import java.util.Map;

public class QuadrupleMapNodeData
extends BaseNodeDataImpl {
    private static final int COMMON_DECLARATION_SIZE = 7;
    private Map<String, CSSProperty> propertiesOwn = new HashMap<String, CSSProperty>(css.getTotalProperties(), 1.0f);
    private Map<String, CSSProperty> propertiesInh = new HashMap<String, CSSProperty>(css.getTotalProperties(), 1.0f);
    private Map<String, Term<?>> valuesOwn = new HashMap(css.getTotalProperties(), 1.0f);
    private Map<String, Term<?>> valuesInh = new HashMap(css.getTotalProperties(), 1.0f);
    private Map<String, Declaration> sourcesOwn = new HashMap<String, Declaration>(css.getTotalProperties(), 1.0f);
    private Map<String, Declaration> sourcesInh = new HashMap<String, Declaration>(css.getTotalProperties(), 1.0f);

    @Override
    public <T extends CSSProperty> T getProperty(String name) {
        return this.getProperty(name, true);
    }

    @Override
    public <T extends CSSProperty> T getProperty(String name, boolean includeInherited) {
        CSSProperty inh = null;
        CSSProperty tmp = null;
        if (includeInherited) {
            inh = this.propertiesInh.get(name);
        }
        if ((tmp = this.propertiesOwn.get(name)) == null) {
            tmp = inh;
        }
        CSSProperty retval = tmp;
        return (T)retval;
    }

    @Override
    public Term<?> getValue(String name, boolean includeInherited) {
        if (includeInherited) {
            Term<?> own = this.valuesOwn.get(name);
            if (own != null) {
                return own;
            }
            Term<?> inherited = null;
            if (!this.propertiesOwn.containsKey(name)) {
                inherited = this.valuesInh.get(name);
            }
            return inherited;
        }
        return this.valuesOwn.get(name);
    }

    @Override
    public <T extends Term<?>> T getValue(Class<T> clazz, String name, boolean includeInherited) {
        if (includeInherited) {
            Term own = (Term)clazz.cast(this.valuesOwn.get(name));
            if (own != null) {
                return (T)own;
            }
            Term inherited = null;
            if (!this.propertiesOwn.containsKey(name)) {
                inherited = (Term)clazz.cast(this.valuesInh.get(name));
            }
            return (T)inherited;
        }
        return (T)((Term)clazz.cast(this.valuesOwn.get(name)));
    }

    @Override
    public <T extends Term<?>> T getValue(Class<T> clazz, String name) {
        return this.getValue(clazz, name, true);
    }

    @Override
    public String getAsString(String name, boolean includeInherited) {
        boolean usedInherited = false;
        CSSProperty prop = this.propertiesOwn.get(name);
        if (prop == null && includeInherited) {
            prop = this.propertiesInh.get(name);
            usedInherited = true;
        }
        if (prop == null) {
            return null;
        }
        if (!prop.toString().isEmpty()) {
            return prop.toString();
        }
        Term<?> val = usedInherited ? this.valuesInh.get(name) : this.valuesOwn.get(name);
        return val == null ? null : val.toString();
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
            this.propertiesOwn.put((String)entry.getKey(), (CSSProperty)entry.getValue());
            this.sourcesOwn.put((String)entry.getKey(), d);
        }
        for (Map.Entry entry : terms.entrySet()) {
            Term t = (Term)entry.getValue();
            if (t.getOperator() != null) {
                t = t.shallowClone().setOperator(null);
            }
            this.valuesOwn.put((String)entry.getKey(), t);
        }
        return this;
    }

    @Override
    public NodeData inheritFrom(NodeData parent) throws ClassCastException {
        Declaration src;
        Term<?> term;
        CSSProperty cur;
        CSSProperty value;
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof QuadrupleMapNodeData)) {
            throw new ClassCastException("Cant't inherit from NodeData different from " + this.getClass().getName() + "(" + parent.getClass().getName() + ")");
        }
        QuadrupleMapNodeData nd = (QuadrupleMapNodeData)parent;
        for (String key : nd.propertiesInh.keySet()) {
            value = nd.propertiesInh.get(key);
            cur = this.propertiesOwn.get(key);
            if (!value.inherited() && (cur == null || !cur.equalsInherit())) continue;
            this.propertiesInh.put(key, value);
            this.valuesInh.remove(key);
            term = nd.valuesInh.get(key);
            if (term != null) {
                this.valuesInh.put(key, term);
            }
            if ((src = nd.sourcesInh.get(key)) == null) continue;
            this.sourcesInh.put(key, src);
        }
        for (String key : nd.propertiesOwn.keySet()) {
            value = nd.propertiesOwn.get(key);
            cur = this.propertiesOwn.get(key);
            if (!value.inherited() && (cur == null || !cur.equalsInherit())) continue;
            this.propertiesInh.put(key, value);
            this.valuesInh.remove(key);
            term = nd.valuesOwn.get(key);
            if (term != null) {
                this.valuesInh.put(key, term);
            }
            if ((src = nd.sourcesOwn.get(key)) == null) continue;
            this.sourcesInh.put(key, src);
        }
        return this;
    }

    @Override
    public NodeData concretize() {
        CSSProperty p;
        for (String key : this.propertiesInh.keySet()) {
            p = this.propertiesInh.get(key);
            if (!p.equalsInherit()) continue;
            this.propertiesInh.put(key, css.getDefaultProperty(key));
            Term<?> value = css.getDefaultValue(key);
            if (value == null) continue;
            this.valuesInh.put(key, value);
        }
        for (String key : this.propertiesOwn.keySet()) {
            Term<?> value;
            CSSProperty rp;
            p = this.propertiesOwn.get(key);
            if (p.equalsInherit()) {
                Declaration source;
                rp = this.propertiesInh.get(key);
                if (rp == null) {
                    rp = css.getDefaultProperty(key);
                }
                this.propertiesOwn.put(key, rp);
                value = this.valuesInh.get(key);
                if (value == null) {
                    value = css.getDefaultValue(key);
                }
                if (value != null) {
                    this.valuesOwn.put(key, value);
                }
                if ((source = this.sourcesInh.get(key)) == null) continue;
                this.sourcesOwn.put(key, source);
                continue;
            }
            if (p.equalsInitial()) {
                rp = css.getDefaultProperty(key);
                this.propertiesOwn.put(key, rp);
                value = css.getDefaultValue(key);
                if (value == null) continue;
                this.valuesOwn.put(key, value);
                continue;
            }
            if (!p.equalsUnset()) continue;
            if (p.inherited()) {
                rp = this.propertiesInh.get(key);
                if (rp == null) {
                    rp = css.getDefaultProperty(key);
                }
                this.propertiesOwn.put(key, rp);
                value = this.valuesInh.get(key);
                if (value == null) {
                    value = css.getDefaultValue(key);
                }
                if (value == null) continue;
                this.valuesOwn.put(key, value);
                continue;
            }
            rp = css.getDefaultProperty(key);
            this.propertiesOwn.put(key, rp);
            value = css.getDefaultValue(key);
            if (value == null) continue;
            this.valuesOwn.put(key, value);
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        LinkedHashSet<String> tmp = new LinkedHashSet<String>();
        tmp.addAll(this.propertiesInh.keySet());
        tmp.addAll(this.propertiesOwn.keySet());
        ArrayList keys = new ArrayList(tmp);
        Collections.sort(keys);
        for (String key : keys) {
            Term<?> value;
            CSSProperty prop = this.propertiesOwn.get(key);
            if (prop == null) {
                prop = this.propertiesInh.get(key);
            }
            if ((value = this.valuesOwn.get(key)) == null) {
                value = this.valuesInh.get(key);
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
        LinkedHashSet<String> props = new LinkedHashSet<String>();
        props.addAll(this.propertiesInh.keySet());
        props.addAll(this.propertiesOwn.keySet());
        ArrayList<String> keys = new ArrayList<String>(props);
        Collections.sort(keys);
        return keys;
    }

    @Override
    public Declaration getSourceDeclaration(String name) {
        return this.sourcesOwn.get(name);
    }

    @Override
    public Declaration getSourceDeclaration(String name, boolean includeInherited) {
        Declaration ret = this.sourcesOwn.get(name);
        if (includeInherited && ret == null) {
            ret = this.sourcesInh.get(name);
        }
        return ret;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.propertiesInh == null ? 0 : this.propertiesInh.hashCode());
        result = 31 * result + (this.propertiesOwn == null ? 0 : this.propertiesOwn.hashCode());
        result = 31 * result + (this.valuesInh == null ? 0 : this.valuesInh.hashCode());
        result = 31 * result + (this.valuesOwn == null ? 0 : this.valuesOwn.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof QuadrupleMapNodeData)) {
            return false;
        }
        QuadrupleMapNodeData other = (QuadrupleMapNodeData)obj;
        if (this.propertiesInh == null ? other.propertiesInh != null : !this.propertiesInh.equals(other.propertiesInh)) {
            return false;
        }
        if (this.propertiesOwn == null ? other.propertiesOwn != null : !this.propertiesOwn.equals(other.propertiesOwn)) {
            return false;
        }
        if (this.valuesInh == null ? other.valuesInh != null : !this.valuesInh.equals(other.valuesInh)) {
            return false;
        }
        return !(this.valuesOwn == null ? other.valuesOwn != null : !this.valuesOwn.equals(other.valuesOwn));
    }
}

