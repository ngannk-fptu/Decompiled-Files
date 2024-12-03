/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.CollectionAndSequence;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassIntrospector;
import freemarker.ext.beans.FastPropertyDescriptor;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.ext.beans.OverloadedMethods;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanModel
implements TemplateHashModelEx,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport {
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    protected final Object object;
    protected final BeansWrapper wrapper;
    static final TemplateModel UNKNOWN = new SimpleScalar("UNKNOWN");
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new BeanModel(object, (BeansWrapper)wrapper);
        }
    };
    private HashMap<Object, TemplateModel> memberCache;

    public BeanModel(Object object, BeansWrapper wrapper) {
        this(object, wrapper, true);
    }

    BeanModel(Object object, BeansWrapper wrapper, boolean inrospectNow) {
        this.object = object;
        this.wrapper = wrapper;
        if (inrospectNow && object != null) {
            wrapper.getClassIntrospector().get(object.getClass());
        }
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Class<?> clazz = this.object.getClass();
        Map<Object, Object> classInfo = this.wrapper.getClassIntrospector().get(clazz);
        TemplateModel retval = null;
        try {
            if (this.wrapper.isMethodsShadowItems()) {
                Object fd = classInfo.get(key);
                retval = fd != null ? this.invokeThroughDescriptor(fd, classInfo) : this.invokeGenericGet(classInfo, clazz, key);
            } else {
                TemplateModel nullModel;
                TemplateModel model = this.invokeGenericGet(classInfo, clazz, key);
                if (model != (nullModel = this.wrapper.wrap(null)) && model != UNKNOWN) {
                    return model;
                }
                Object fd = classInfo.get(key);
                if (fd != null && (retval = this.invokeThroughDescriptor(fd, classInfo)) == UNKNOWN && model == nullModel) {
                    retval = nullModel;
                }
            }
            if (retval == UNKNOWN) {
                if (this.wrapper.isStrict()) {
                    throw new InvalidPropertyException("No such bean property: " + key);
                }
                if (LOG.isDebugEnabled()) {
                    this.logNoSuchKey(key, classInfo);
                }
                retval = this.wrapper.wrap(null);
            }
            return retval;
        }
        catch (TemplateModelException e) {
            throw e;
        }
        catch (Exception e) {
            throw new _TemplateModelException((Throwable)e, "An error has occurred when reading existing sub-variable ", new _DelayedJQuote(key), "; see cause exception! The type of the containing value was: ", new _DelayedFTLTypeDescription(this));
        }
    }

    private void logNoSuchKey(String key, Map<?, ?> keyMap) {
        LOG.debug("Key " + StringUtil.jQuoteNoXSS(key) + " was not found on instance of " + this.object.getClass().getName() + ". Introspection information for the class is: " + keyMap);
    }

    protected boolean hasPlainGetMethod() {
        return this.wrapper.getClassIntrospector().get(this.object.getClass()).get(ClassIntrospector.GENERIC_GET_KEY) != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TemplateModel invokeThroughDescriptor(Object desc, Map<Object, Object> classInfo) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
        TemplateModel cachedModel;
        BeanModel beanModel = this;
        synchronized (beanModel) {
            cachedModel = this.memberCache != null ? this.memberCache.get(desc) : null;
        }
        if (cachedModel != null) {
            return cachedModel;
        }
        TemplateModel resultModel = UNKNOWN;
        if (desc instanceof FastPropertyDescriptor) {
            FastPropertyDescriptor pd = (FastPropertyDescriptor)desc;
            Method indexedReadMethod = pd.getIndexedReadMethod();
            resultModel = indexedReadMethod != null ? (!this.wrapper.getPreferIndexedReadMethod() && pd.getReadMethod() != null ? this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null) : (cachedModel = new SimpleMethodModel(this.object, indexedReadMethod, ClassIntrospector.getArgTypes(classInfo, indexedReadMethod), this.wrapper))) : this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
        } else if (desc instanceof Field) {
            resultModel = this.wrapper.readField(this.object, (Field)desc);
        } else if (desc instanceof Method) {
            Method method = (Method)desc;
            resultModel = cachedModel = new SimpleMethodModel(this.object, method, ClassIntrospector.getArgTypes(classInfo, method), this.wrapper);
        } else if (desc instanceof OverloadedMethods) {
            resultModel = cachedModel = new OverloadedMethodsModel(this.object, (OverloadedMethods)desc, this.wrapper);
        }
        if (cachedModel != null) {
            BeanModel beanModel2 = this;
            synchronized (beanModel2) {
                if (this.memberCache == null) {
                    this.memberCache = new HashMap();
                }
                this.memberCache.put(desc, cachedModel);
            }
        }
        return resultModel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearMemberCache() {
        BeanModel beanModel = this;
        synchronized (beanModel) {
            this.memberCache = null;
        }
    }

    protected TemplateModel invokeGenericGet(Map classInfo, Class<?> clazz, String key) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
        Method genericGet = (Method)classInfo.get(ClassIntrospector.GENERIC_GET_KEY);
        if (genericGet == null) {
            return UNKNOWN;
        }
        return this.wrapper.invokeMethod(this.object, genericGet, new Object[]{key});
    }

    protected TemplateModel wrap(Object obj) throws TemplateModelException {
        return this.wrapper.getOuterIdentity().wrap(obj);
    }

    protected Object unwrap(TemplateModel model) throws TemplateModelException {
        return this.wrapper.unwrap(model);
    }

    @Override
    public boolean isEmpty() {
        if (this.object instanceof String) {
            return ((String)this.object).length() == 0;
        }
        if (this.object instanceof Collection) {
            return ((Collection)this.object).isEmpty();
        }
        if (this.object instanceof Iterator && this.wrapper.is2324Bugfixed()) {
            return !((Iterator)this.object).hasNext();
        }
        if (this.object instanceof Map) {
            return ((Map)this.object).isEmpty();
        }
        return this.object == null || Boolean.FALSE.equals(this.object);
    }

    @Override
    public Object getAdaptedObject(Class<?> hint) {
        return this.object;
    }

    @Override
    public Object getWrappedObject() {
        return this.object;
    }

    @Override
    public int size() {
        return this.wrapper.getClassIntrospector().keyCount(this.object.getClass());
    }

    @Override
    public TemplateCollectionModel keys() {
        return new CollectionAndSequence(new SimpleSequence(this.keySet(), (ObjectWrapper)this.wrapper));
    }

    @Override
    public TemplateCollectionModel values() throws TemplateModelException {
        ArrayList<TemplateModel> values = new ArrayList<TemplateModel>(this.size());
        TemplateModelIterator it = this.keys().iterator();
        while (it.hasNext()) {
            String key = ((TemplateScalarModel)it.next()).getAsString();
            values.add(this.get(key));
        }
        return new CollectionAndSequence(new SimpleSequence(values, (ObjectWrapper)this.wrapper));
    }

    String getAsClassicCompatibleString() {
        if (this.object == null) {
            return "null";
        }
        String s = this.object.toString();
        return s != null ? s : "null";
    }

    public String toString() {
        return this.object.toString();
    }

    protected Set keySet() {
        return this.wrapper.getClassIntrospector().keySet(this.object.getClass());
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return this.wrapper.wrapAsAPI(this.object);
    }
}

