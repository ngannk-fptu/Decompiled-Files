/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassMemberAccessPolicy;
import freemarker.ext.beans.OverloadedMethods;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.log.Logger;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

final class StaticModel
implements TemplateHashModelEx {
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    private final Class<?> clazz;
    private final BeansWrapper wrapper;
    private final Map<String, Object> map = new HashMap<String, Object>();

    StaticModel(Class<?> clazz, BeansWrapper wrapper) throws TemplateModelException {
        this.clazz = clazz;
        this.wrapper = wrapper;
        this.populate();
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object model = this.map.get(key);
        if (model instanceof TemplateModel) {
            return (TemplateModel)model;
        }
        if (model instanceof Field) {
            try {
                return this.wrapper.readField(null, (Field)model);
            }
            catch (IllegalAccessException e) {
                throw new TemplateModelException("Illegal access for field " + key + " of class " + this.clazz.getName());
            }
        }
        throw new TemplateModelException("No such key: " + key + " in class " + this.clazz.getName());
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.keySet());
    }

    @Override
    public TemplateCollectionModel values() throws TemplateModelException {
        return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.values());
    }

    private void populate() throws TemplateModelException {
        Field[] fields;
        if (!Modifier.isPublic(this.clazz.getModifiers())) {
            throw new TemplateModelException("Can't wrap the non-public class " + this.clazz.getName());
        }
        if (this.wrapper.getExposureLevel() == 3) {
            return;
        }
        ClassMemberAccessPolicy effClassMemberAccessPolicy = this.wrapper.getClassIntrospector().getEffectiveMemberAccessPolicy().forClass(this.clazz);
        for (Field field : fields = this.clazz.getFields()) {
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !effClassMemberAccessPolicy.isFieldExposed(field)) continue;
            if (Modifier.isFinal(mod)) {
                try {
                    this.map.put(field.getName(), this.wrapper.readField(null, field));
                }
                catch (IllegalAccessException illegalAccessException) {}
                continue;
            }
            this.map.put(field.getName(), field);
        }
        if (this.wrapper.getExposureLevel() < 2) {
            Method[] methods = this.clazz.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                OverloadedMethods overloadedMethods;
                Method method = methods[i];
                int mod = method.getModifiers();
                if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !effClassMemberAccessPolicy.isMethodExposed(method)) continue;
                String name = method.getName();
                Object obj = this.map.get(name);
                if (obj instanceof Method) {
                    overloadedMethods = new OverloadedMethods(this.wrapper.is2321Bugfixed());
                    overloadedMethods.addMethod((Method)obj);
                    overloadedMethods.addMethod(method);
                    this.map.put(name, overloadedMethods);
                    continue;
                }
                if (obj instanceof OverloadedMethods) {
                    overloadedMethods = (OverloadedMethods)obj;
                    overloadedMethods.addMethod(method);
                    continue;
                }
                if (obj != null && LOG.isInfoEnabled()) {
                    LOG.info("Overwriting value [" + obj + "] for  key '" + name + "' with [" + method + "] in static model for " + this.clazz.getName());
                }
                this.map.put(name, method);
            }
            for (Map.Entry<String, Object> entry : this.map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Method) {
                    Method method = (Method)value;
                    entry.setValue(new SimpleMethodModel(null, method, method.getParameterTypes(), this.wrapper));
                    continue;
                }
                if (!(value instanceof OverloadedMethods)) continue;
                entry.setValue(new OverloadedMethodsModel(null, (OverloadedMethods)value, this.wrapper));
            }
        }
    }
}

