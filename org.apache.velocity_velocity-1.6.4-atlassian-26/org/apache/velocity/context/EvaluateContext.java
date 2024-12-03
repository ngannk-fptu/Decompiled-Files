/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.util.HashSet;
import org.apache.velocity.context.ChainedInternalContextAdapter;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.ClassUtils;

public class EvaluateContext
extends ChainedInternalContextAdapter {
    Context localContext;
    boolean allowRendering = true;

    public EvaluateContext(InternalContextAdapter inner, RuntimeServices rsvc) {
        super(inner);
        this.initContext(rsvc);
    }

    private void initContext(RuntimeServices rsvc) {
        Object o;
        String contextClass = rsvc.getString("directive.evaluate.context.class");
        if (contextClass != null && contextClass.length() > 0) {
            o = null;
            try {
                o = ClassUtils.getNewInstance(contextClass);
            }
            catch (ClassNotFoundException cnfe) {
                String err = "The specified class for #evaluate() context (" + contextClass + ") does not exist or is not accessible to the current classloader.";
                rsvc.getLog().error(err);
                throw new RuntimeException(err, cnfe);
            }
            catch (Exception e) {
                String err = "The specified class for #evaluate() context (" + contextClass + ") can not be loaded.";
                rsvc.getLog().error(err, e);
                throw new RuntimeException(err);
            }
            if (!(o instanceof Context)) {
                String err = "The specified class for #evaluate() context (" + contextClass + ") does not implement " + Context.class.getName() + ".";
                rsvc.getLog().error(err);
                throw new RuntimeException(err);
            }
        } else {
            String err = "No class specified for #evaluate() context.";
            rsvc.getLog().error(err);
            throw new RuntimeException(err);
        }
        this.localContext = (Context)o;
    }

    @Override
    public Object put(String key, Object value) {
        return this.localContext.put(key, value);
    }

    @Override
    public Object get(String key) {
        Object o = this.localContext.get(key);
        if (o == null) {
            o = super.get(key);
        }
        return o;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.localContext.containsKey(key) || super.containsKey(key);
    }

    @Override
    public Object[] getKeys() {
        HashSet<Object> keys = new HashSet<Object>();
        Object[] localKeys = this.localContext.getKeys();
        for (int i = 0; i < localKeys.length; ++i) {
            keys.add(localKeys[i]);
        }
        Object[] innerKeys = super.getKeys();
        for (int i = 0; i < innerKeys.length; ++i) {
            keys.add(innerKeys[i]);
        }
        return keys.toArray();
    }

    @Override
    public Object remove(Object key) {
        return this.localContext.remove(key);
    }

    @Override
    public Object localPut(String key, Object value) {
        return this.localContext.put(key, value);
    }

    @Override
    public boolean getAllowRendering() {
        return this.allowRendering && this.innerContext.getAllowRendering();
    }

    @Override
    public void setAllowRendering(boolean v) {
        this.allowRendering = false;
    }
}

