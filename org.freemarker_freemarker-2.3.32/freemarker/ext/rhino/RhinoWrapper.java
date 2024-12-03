/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.Undefined
 *  org.mozilla.javascript.UniqueTag
 *  org.mozilla.javascript.Wrapper
 */
package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.rhino.RhinoScriptableModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.Wrapper;

public class RhinoWrapper
extends BeansWrapper {
    private static final Object UNDEFINED_INSTANCE;

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == UNDEFINED_INSTANCE || obj == UniqueTag.NOT_FOUND) {
            return null;
        }
        if (obj == UniqueTag.NULL_VALUE) {
            return super.wrap(null);
        }
        if (obj instanceof Wrapper) {
            obj = ((Wrapper)obj).unwrap();
        }
        return super.wrap(obj);
    }

    protected ModelFactory getModelFactory(Class clazz) {
        if (Scriptable.class.isAssignableFrom(clazz)) {
            return RhinoScriptableModel.FACTORY;
        }
        return super.getModelFactory(clazz);
    }

    static {
        try {
            UNDEFINED_INSTANCE = AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws Exception {
                    return Undefined.class.getField("instance").get(null);
                }
            });
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}

