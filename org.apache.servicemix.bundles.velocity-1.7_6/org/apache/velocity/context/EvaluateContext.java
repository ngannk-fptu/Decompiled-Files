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

    public EvaluateContext(InternalContextAdapter inner, RuntimeServices rsvc) {
        super(inner);
        this.initContext(rsvc);
    }

    private void initContext(RuntimeServices rsvc) {
        String contextClass = rsvc.getString("directive.evaluate.context.class");
        if (contextClass != null && contextClass.length() > 0) {
            rsvc.getLog().warn("The directive.evaluate.context.class property has been deprecated. It will be removed in Velocity 2.0.  Instead, please use the automatically provided $evaluate namespace to get and set local references (e.g. #set($evaluate.foo = 'bar') and $evaluate.foo).");
            Object o = null;
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
            this.localContext = (Context)o;
        } else if (rsvc.getLog().isDebugEnabled()) {
            rsvc.getLog().debug("No class specified for #evaluate() context, so #set calls will now alter the global context and no longer be local.  This is a change from earlier versions due to VELOCITY-704.  If you need references within #evaluate to stay local, please use the automatically provided $evaluate namespace instead (e.g. #set($evaluate.foo = 'bar') and $evaluate.foo).");
        }
    }

    public Object put(String key, Object value) {
        if (this.localContext != null) {
            return this.localContext.put(key, value);
        }
        return super.put(key, value);
    }

    public Object get(String key) {
        Object o = null;
        if (this.localContext != null) {
            o = this.localContext.get(key);
        }
        if (o == null) {
            o = super.get(key);
        }
        return o;
    }

    public boolean containsKey(Object key) {
        return this.localContext != null && this.localContext.containsKey(key) || super.containsKey(key);
    }

    public Object[] getKeys() {
        if (this.localContext != null) {
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
        return super.getKeys();
    }

    public Object remove(Object key) {
        if (this.localContext != null) {
            return this.localContext.remove(key);
        }
        return super.remove(key);
    }

    public Object localPut(String key, Object value) {
        if (this.localContext != null) {
            return this.localContext.put(key, value);
        }
        return super.localPut(key, value);
    }
}

