/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.EvaluatorException
 *  org.mozilla.javascript.Function
 *  org.mozilla.javascript.NativeJavaObject
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.rhino.RhinoFunctionModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoScriptableModel
implements TemplateHashModelEx,
TemplateSequenceModel,
AdapterTemplateModel,
TemplateScalarModel,
TemplateBooleanModel,
TemplateNumberModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new RhinoScriptableModel((Scriptable)object, (BeansWrapper)wrapper);
        }
    };
    private final Scriptable scriptable;
    private final BeansWrapper wrapper;

    public RhinoScriptableModel(Scriptable scriptable, BeansWrapper wrapper) {
        this.scriptable = scriptable;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object retval = ScriptableObject.getProperty((Scriptable)this.scriptable, (String)key);
        if (retval instanceof Function) {
            return new RhinoFunctionModel((Function)retval, this.scriptable, this.wrapper);
        }
        return this.wrapper.wrap(retval);
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        Object retval = ScriptableObject.getProperty((Scriptable)this.scriptable, (int)index);
        if (retval instanceof Function) {
            return new RhinoFunctionModel((Function)retval, this.scriptable, this.wrapper);
        }
        return this.wrapper.wrap(retval);
    }

    @Override
    public boolean isEmpty() {
        return this.scriptable.getIds().length == 0;
    }

    @Override
    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel)this.wrapper.wrap(this.scriptable.getIds());
    }

    @Override
    public int size() {
        return this.scriptable.getIds().length;
    }

    @Override
    public TemplateCollectionModel values() throws TemplateModelException {
        Object[] ids = this.scriptable.getIds();
        Object[] values = new Object[ids.length];
        for (int i = 0; i < values.length; ++i) {
            Object id = ids[i];
            values[i] = id instanceof Number ? ScriptableObject.getProperty((Scriptable)this.scriptable, (int)((Number)id).intValue()) : ScriptableObject.getProperty((Scriptable)this.scriptable, (String)String.valueOf(id));
        }
        return (TemplateCollectionModel)this.wrapper.wrap(values);
    }

    @Override
    public boolean getAsBoolean() {
        return Context.toBoolean((Object)this.scriptable);
    }

    @Override
    public Number getAsNumber() {
        return Context.toNumber((Object)this.scriptable);
    }

    @Override
    public String getAsString() {
        return Context.toString((Object)this.scriptable);
    }

    Scriptable getScriptable() {
        return this.scriptable;
    }

    BeansWrapper getWrapper() {
        return this.wrapper;
    }

    public Object getAdaptedObject(Class hint) {
        try {
            return NativeJavaObject.coerceType((Class)hint, (Object)this.scriptable);
        }
        catch (EvaluatorException e) {
            return NativeJavaObject.coerceType(Object.class, (Object)this.scriptable);
        }
    }
}

