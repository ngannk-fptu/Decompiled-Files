/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HashAdapter
extends AbstractMap
implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateHashModel model;
    private Set entrySet;

    HashAdapter(TemplateHashModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override
    public boolean isEmpty() {
        try {
            return this.model.isEmpty();
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public int size() {
        try {
            return this.getModelEx().size();
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public Object get(Object key) {
        try {
            return this.wrapper.unwrap(this.model.get(String.valueOf(key)));
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.get(key) != null) {
            return true;
        }
        return super.containsKey(key);
    }

    @Override
    public Set entrySet() {
        if (this.entrySet != null) {
            return this.entrySet;
        }
        this.entrySet = new AbstractSet(){

            @Override
            public Iterator iterator() {
                TemplateModelIterator i;
                try {
                    i = HashAdapter.this.getModelEx().keys().iterator();
                }
                catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException(e);
                }
                return new Iterator(){

                    @Override
                    public boolean hasNext() {
                        try {
                            return i.hasNext();
                        }
                        catch (TemplateModelException e) {
                            throw new UndeclaredThrowableException(e);
                        }
                    }

                    public Object next() {
                        Object key;
                        try {
                            key = HashAdapter.this.wrapper.unwrap(i.next());
                        }
                        catch (TemplateModelException e) {
                            throw new UndeclaredThrowableException(e);
                        }
                        return new Map.Entry(){

                            public Object getKey() {
                                return key;
                            }

                            public Object getValue() {
                                return HashAdapter.this.get(key);
                            }

                            public Object setValue(Object value) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public boolean equals(Object o) {
                                Object v2;
                                Object v1;
                                Object k2;
                                if (!(o instanceof Map.Entry)) {
                                    return false;
                                }
                                Map.Entry e = (Map.Entry)o;
                                Object k1 = this.getKey();
                                return (k1 == (k2 = e.getKey()) || k1 != null && k1.equals(k2)) && ((v1 = this.getValue()) == (v2 = e.getValue()) || v1 != null && v1.equals(v2));
                            }

                            @Override
                            public int hashCode() {
                                Object value = this.getValue();
                                return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                try {
                    return HashAdapter.this.getModelEx().size();
                }
                catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException(e);
                }
            }
        };
        return this.entrySet;
    }

    private TemplateHashModelEx getModelEx() {
        if (this.model instanceof TemplateHashModelEx) {
            return (TemplateHashModelEx)this.model;
        }
        throw new UnsupportedOperationException("Operation supported only on TemplateHashModelEx. " + this.model.getClass().getName() + " does not implement it though.");
    }
}

