/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedShortClassName;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.APIModel;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeansModelCache;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans.BeansWrapperSingletonHolder;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.CharacterOrString;
import freemarker.ext.beans.ClassBasedModelFactory;
import freemarker.ext.beans.ClassIntrospector;
import freemarker.ext.beans.ClassIntrospectorBuilder;
import freemarker.ext.beans.CollectionAdapter;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.DateModel;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.HashAdapter;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.beans.MemberAndArguments;
import freemarker.ext.beans.MethodAppearanceFineTuner;
import freemarker.ext.beans.MethodSorter;
import freemarker.ext.beans.NonPrimitiveArrayBackedReadOnlyList;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.OverloadedMethods;
import freemarker.ext.beans.OverloadedNumberUtil;
import freemarker.ext.beans.PrimtiveArrayBackedReadOnlyList;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.SequenceAdapter;
import freemarker.ext.beans.SetAdapter;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.ext.beans.SimpleMethod;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans.StaticModels;
import freemarker.ext.beans.StringModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.ext.beans._EnumModels;
import freemarker.ext.beans._MethodUtil;
import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.RichObjectWrapper;
import freemarker.template.utility.WriteProtectable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class BeansWrapper
implements RichObjectWrapper,
WriteProtectable {
    private static final Logger LOG = Logger.getLogger("freemarker.beans");
    @Deprecated
    static final Object CAN_NOT_UNWRAP = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
    public static final int EXPOSE_ALL = 0;
    public static final int EXPOSE_SAFE = 1;
    public static final int EXPOSE_PROPERTIES_ONLY = 2;
    public static final int EXPOSE_NOTHING = 3;
    private final Object sharedIntrospectionLock;
    private ClassIntrospector classIntrospector;
    private final StaticModels staticModels;
    private final ClassBasedModelFactory enumModels;
    private final ModelCache modelCache;
    private final BooleanModel falseModel;
    private final BooleanModel trueModel;
    private volatile boolean writeProtected;
    private TemplateModel nullModel = null;
    private int defaultDateType;
    private ObjectWrapper outerIdentity = this;
    private boolean methodsShadowItems = true;
    private boolean simpleMapWrapper;
    private boolean strict;
    private boolean preferIndexedReadMethod;
    private final Version incompatibleImprovements;
    private static volatile boolean ftmaDeprecationWarnLogged;
    private final ModelFactory BOOLEAN_FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return (Boolean)object != false ? BeansWrapper.this.trueModel : BeansWrapper.this.falseModel;
        }
    };
    private static final ModelFactory ITERATOR_FACTORY;
    private static final ModelFactory ENUMERATION_FACTORY;

    @Deprecated
    public BeansWrapper() {
        this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public BeansWrapper(Version incompatibleImprovements) {
        this(new BeansWrapperConfiguration(incompatibleImprovements){}, false);
    }

    protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected) {
        this(bwConf, writeProtected, true);
    }

    protected BeansWrapper(BeansWrapperConfiguration bwConf, boolean writeProtected, boolean finalizeConstruction) {
        if (bwConf.getMethodAppearanceFineTuner() == null) {
            Class<?> thisClass = this.getClass();
            boolean overridden = false;
            boolean testFailed = false;
            try {
                while (!overridden && thisClass != DefaultObjectWrapper.class && thisClass != BeansWrapper.class && thisClass != SimpleObjectWrapper.class) {
                    try {
                        thisClass.getDeclaredMethod("finetuneMethodAppearance", Class.class, Method.class, MethodAppearanceDecision.class);
                        overridden = true;
                    }
                    catch (NoSuchMethodException e) {
                        thisClass = thisClass.getSuperclass();
                    }
                }
            }
            catch (Throwable e) {
                LOG.info("Failed to check if finetuneMethodAppearance is overidden in " + thisClass.getName() + "; acting like if it was, but this way it won't utilize the shared class introspection cache.", e);
                overridden = true;
                testFailed = true;
            }
            if (overridden) {
                if (!testFailed && !ftmaDeprecationWarnLogged) {
                    LOG.warn("Overriding " + BeansWrapper.class.getName() + ".finetuneMethodAppearance is deprecated and will be banned sometimes in the future. Use setMethodAppearanceFineTuner instead.");
                    ftmaDeprecationWarnLogged = true;
                }
                bwConf = (BeansWrapperConfiguration)bwConf.clone(false);
                bwConf.setMethodAppearanceFineTuner(new MethodAppearanceFineTuner(){

                    @Override
                    public void process(MethodAppearanceDecisionInput in, MethodAppearanceDecision out) {
                        BeansWrapper.this.finetuneMethodAppearance(in.getContainingClass(), in.getMethod(), out);
                    }
                });
            }
        }
        this.incompatibleImprovements = bwConf.getIncompatibleImprovements();
        this.simpleMapWrapper = bwConf.isSimpleMapWrapper();
        this.preferIndexedReadMethod = bwConf.getPreferIndexedReadMethod();
        this.defaultDateType = bwConf.getDefaultDateType();
        this.outerIdentity = bwConf.getOuterIdentity() != null ? bwConf.getOuterIdentity() : this;
        this.strict = bwConf.isStrict();
        if (!writeProtected) {
            this.sharedIntrospectionLock = new Object();
            this.classIntrospector = new ClassIntrospector(_BeansAPI.getClassIntrospectorBuilder(bwConf), this.sharedIntrospectionLock, false, false);
        } else {
            this.classIntrospector = _BeansAPI.getClassIntrospectorBuilder(bwConf).build();
            this.sharedIntrospectionLock = this.classIntrospector.getSharedLock();
        }
        this.falseModel = new BooleanModel(Boolean.FALSE, this);
        this.trueModel = new BooleanModel(Boolean.TRUE, this);
        this.staticModels = new StaticModels(this);
        this.enumModels = new _EnumModels(this);
        this.modelCache = new BeansModelCache(this);
        this.setUseCache(bwConf.getUseModelCache());
        this.finalizeConstruction(writeProtected);
    }

    protected void finalizeConstruction(boolean writeProtected) {
        if (writeProtected) {
            this.writeProtect();
        }
        this.registerModelFactories();
    }

    @Override
    public void writeProtect() {
        this.writeProtected = true;
    }

    @Override
    public boolean isWriteProtected() {
        return this.writeProtected;
    }

    Object getSharedIntrospectionLock() {
        return this.sharedIntrospectionLock;
    }

    protected void checkModifiable() {
        if (this.writeProtected) {
            throw new IllegalStateException("Can't modify the " + this.getClass().getName() + " object, as it was write protected.");
        }
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.checkModifiable();
        this.strict = strict;
    }

    public void setOuterIdentity(ObjectWrapper outerIdentity) {
        this.checkModifiable();
        this.outerIdentity = outerIdentity;
    }

    public ObjectWrapper getOuterIdentity() {
        return this.outerIdentity;
    }

    public void setSimpleMapWrapper(boolean simpleMapWrapper) {
        this.checkModifiable();
        this.simpleMapWrapper = simpleMapWrapper;
    }

    public boolean isSimpleMapWrapper() {
        return this.simpleMapWrapper;
    }

    public boolean getPreferIndexedReadMethod() {
        return this.preferIndexedReadMethod;
    }

    public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
        this.checkModifiable();
        this.preferIndexedReadMethod = preferIndexedReadMethod;
    }

    public void setExposureLevel(int exposureLevel) {
        this.checkModifiable();
        if (this.classIntrospector.getExposureLevel() != exposureLevel) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setExposureLevel(exposureLevel);
            this.replaceClassIntrospector(builder);
        }
    }

    public int getExposureLevel() {
        return this.classIntrospector.getExposureLevel();
    }

    public void setExposeFields(boolean exposeFields) {
        this.checkModifiable();
        if (this.classIntrospector.getExposeFields() != exposeFields) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setExposeFields(exposeFields);
            this.replaceClassIntrospector(builder);
        }
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        this.checkModifiable();
        if (this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() != treatDefaultMethodsAsBeanMembers) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
            this.replaceClassIntrospector(builder);
        }
    }

    public boolean isExposeFields() {
        return this.classIntrospector.getExposeFields();
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.classIntrospector.getTreatDefaultMethodsAsBeanMembers();
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.classIntrospector.getMethodAppearanceFineTuner();
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        this.checkModifiable();
        if (this.classIntrospector.getMethodAppearanceFineTuner() != methodAppearanceFineTuner) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
            this.replaceClassIntrospector(builder);
        }
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.classIntrospector.getMemberAccessPolicy();
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        this.checkModifiable();
        if (this.classIntrospector.getMemberAccessPolicy() != memberAccessPolicy) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMemberAccessPolicy(memberAccessPolicy);
            this.replaceClassIntrospector(builder);
        }
    }

    MethodSorter getMethodSorter() {
        return this.classIntrospector.getMethodSorter();
    }

    void setMethodSorter(MethodSorter methodSorter) {
        this.checkModifiable();
        if (this.classIntrospector.getMethodSorter() != methodSorter) {
            ClassIntrospectorBuilder builder = this.classIntrospector.createBuilder();
            builder.setMethodSorter(methodSorter);
            this.replaceClassIntrospector(builder);
        }
    }

    public boolean isClassIntrospectionCacheRestricted() {
        return this.classIntrospector.getHasSharedInstanceRestrictions();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void replaceClassIntrospector(ClassIntrospectorBuilder builder) {
        this.checkModifiable();
        ClassIntrospector newCI = new ClassIntrospector(builder, this.sharedIntrospectionLock, false, false);
        Object object = this.sharedIntrospectionLock;
        synchronized (object) {
            ClassIntrospector oldCI = this.classIntrospector;
            if (oldCI != null) {
                if (this.staticModels != null) {
                    oldCI.unregisterModelFactory(this.staticModels);
                    this.staticModels.clearCache();
                }
                if (this.enumModels != null) {
                    oldCI.unregisterModelFactory(this.enumModels);
                    this.enumModels.clearCache();
                }
                if (this.modelCache != null) {
                    oldCI.unregisterModelFactory(this.modelCache);
                    this.modelCache.clearCache();
                }
                if (this.trueModel != null) {
                    this.trueModel.clearMemberCache();
                }
                if (this.falseModel != null) {
                    this.falseModel.clearMemberCache();
                }
            }
            this.classIntrospector = newCI;
            this.registerModelFactories();
        }
    }

    private void registerModelFactories() {
        if (this.staticModels != null) {
            this.classIntrospector.registerModelFactory(this.staticModels);
        }
        if (this.enumModels != null) {
            this.classIntrospector.registerModelFactory(this.enumModels);
        }
        if (this.modelCache != null) {
            this.classIntrospector.registerModelFactory(this.modelCache);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMethodsShadowItems(boolean methodsShadowItems) {
        BeansWrapper beansWrapper = this;
        synchronized (beansWrapper) {
            this.checkModifiable();
            this.methodsShadowItems = methodsShadowItems;
        }
    }

    boolean isMethodsShadowItems() {
        return this.methodsShadowItems;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDefaultDateType(int defaultDateType) {
        BeansWrapper beansWrapper = this;
        synchronized (beansWrapper) {
            this.checkModifiable();
            this.defaultDateType = defaultDateType;
        }
    }

    public int getDefaultDateType() {
        return this.defaultDateType;
    }

    public void setUseCache(boolean useCache) {
        this.checkModifiable();
        this.modelCache.setUseCache(useCache);
    }

    public boolean getUseCache() {
        return this.modelCache.getUseCache();
    }

    @Deprecated
    public void setNullModel(TemplateModel nullModel) {
        this.checkModifiable();
        this.nullModel = nullModel;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    boolean is2321Bugfixed() {
        return BeansWrapper.is2321Bugfixed(this.getIncompatibleImprovements());
    }

    static boolean is2321Bugfixed(Version version) {
        return version.intValue() >= _VersionInts.V_2_3_21;
    }

    boolean is2324Bugfixed() {
        return BeansWrapper.is2324Bugfixed(this.getIncompatibleImprovements());
    }

    static boolean is2324Bugfixed(Version version) {
        return version.intValue() >= _VersionInts.V_2_3_24;
    }

    protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_27 ? Configuration.VERSION_2_3_27 : (incompatibleImprovements.intValue() == _VersionInts.V_2_3_26 ? Configuration.VERSION_2_3_26 : (BeansWrapper.is2324Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_24 : (BeansWrapper.is2321Bugfixed(incompatibleImprovements) ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0)));
    }

    @Deprecated
    public static final BeansWrapper getDefaultInstance() {
        return BeansWrapperSingletonHolder.INSTANCE;
    }

    @Override
    public TemplateModel wrap(Object object) throws TemplateModelException {
        if (object == null) {
            return this.nullModel;
        }
        return this.modelCache.getInstance(object);
    }

    public TemplateMethodModelEx wrap(Object object, Method method) {
        return new SimpleMethodModel(object, method, method.getParameterTypes(), this);
    }

    @Override
    public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
        return new APIModel(obj, this);
    }

    @Deprecated
    protected TemplateModel getInstance(Object object, ModelFactory factory) {
        return factory.create(object, this);
    }

    protected ModelFactory getModelFactory(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return this.simpleMapWrapper ? SimpleMapModel.FACTORY : MapModel.FACTORY;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CollectionModel.FACTORY;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return NumberModel.FACTORY;
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return DateModel.FACTORY;
        }
        if (Boolean.class == clazz) {
            return this.BOOLEAN_FACTORY;
        }
        if (ResourceBundle.class.isAssignableFrom(clazz)) {
            return ResourceBundleModel.FACTORY;
        }
        if (Iterator.class.isAssignableFrom(clazz)) {
            return ITERATOR_FACTORY;
        }
        if (Enumeration.class.isAssignableFrom(clazz)) {
            return ENUMERATION_FACTORY;
        }
        if (clazz.isArray()) {
            return ArrayModel.FACTORY;
        }
        return StringModel.FACTORY;
    }

    @Override
    public Object unwrap(TemplateModel model) throws TemplateModelException {
        return this.unwrap(model, Object.class);
    }

    public Object unwrap(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
        Object obj = this.tryUnwrapTo(model, targetClass);
        if (obj == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
            throw new TemplateModelException("Can not unwrap model of type " + model.getClass().getName() + " to type " + targetClass.getName());
        }
        return obj;
    }

    @Override
    public Object tryUnwrapTo(TemplateModel model, Class<?> targetClass) throws TemplateModelException {
        return this.tryUnwrapTo(model, targetClass, 0);
    }

    Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags) throws TemplateModelException {
        Object res = this.tryUnwrapTo(model, targetClass, typeFlags, null);
        if ((typeFlags & 1) != 0 && res instanceof Number) {
            return OverloadedNumberUtil.addFallbackType((Number)res, typeFlags);
        }
        return res;
    }

    private Object tryUnwrapTo(TemplateModel model, Class<?> targetClass, int typeFlags, Map<Object, Object> recursionStops) throws TemplateModelException {
        Number number;
        Object wrapped;
        if (model == null || model == this.nullModel) {
            return null;
        }
        boolean is2321Bugfixed = this.is2321Bugfixed();
        if (is2321Bugfixed && targetClass.isPrimitive()) {
            targetClass = ClassUtil.primitiveClassToBoxingClass(targetClass);
        }
        if (model instanceof AdapterTemplateModel) {
            wrapped = ((AdapterTemplateModel)model).getAdaptedObject(targetClass);
            if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
                return wrapped;
            }
            if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass) && (number = BeansWrapper.forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed)) != null) {
                return number;
            }
        }
        if (model instanceof WrapperTemplateModel) {
            wrapped = ((WrapperTemplateModel)model).getWrappedObject();
            if (targetClass == Object.class || targetClass.isInstance(wrapped)) {
                return wrapped;
            }
            if (targetClass != Object.class && wrapped instanceof Number && ClassUtil.isNumerical(targetClass) && (number = BeansWrapper.forceUnwrappedNumberToType((Number)wrapped, targetClass, is2321Bugfixed)) != null) {
                return number;
            }
        }
        if (targetClass != Object.class) {
            Date date;
            Number number2;
            if (String.class == targetClass) {
                if (model instanceof TemplateScalarModel) {
                    return ((TemplateScalarModel)model).getAsString();
                }
                return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
            }
            if (ClassUtil.isNumerical(targetClass) && model instanceof TemplateNumberModel && (number2 = BeansWrapper.forceUnwrappedNumberToType(((TemplateNumberModel)model).getAsNumber(), targetClass, is2321Bugfixed)) != null) {
                return number2;
            }
            if (Boolean.TYPE == targetClass || Boolean.class == targetClass) {
                if (model instanceof TemplateBooleanModel) {
                    return ((TemplateBooleanModel)model).getAsBoolean();
                }
                return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
            }
            if (Map.class == targetClass && model instanceof TemplateHashModel) {
                return new HashAdapter((TemplateHashModel)model, this);
            }
            if (List.class == targetClass && model instanceof TemplateSequenceModel) {
                return new SequenceAdapter((TemplateSequenceModel)model, this);
            }
            if (Set.class == targetClass && model instanceof TemplateCollectionModel) {
                return new SetAdapter((TemplateCollectionModel)model, this);
            }
            if (Collection.class == targetClass || Iterable.class == targetClass) {
                if (model instanceof TemplateCollectionModel) {
                    return new CollectionAdapter((TemplateCollectionModel)model, this);
                }
                if (model instanceof TemplateSequenceModel) {
                    return new SequenceAdapter((TemplateSequenceModel)model, this);
                }
            }
            if (targetClass.isArray()) {
                if (model instanceof TemplateSequenceModel) {
                    return this.unwrapSequenceToArray((TemplateSequenceModel)model, targetClass, true, recursionStops);
                }
                return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
            }
            if (Character.TYPE == targetClass || targetClass == Character.class) {
                String s;
                if (model instanceof TemplateScalarModel && (s = ((TemplateScalarModel)model).getAsString()).length() == 1) {
                    return Character.valueOf(s.charAt(0));
                }
                return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
            }
            if (Date.class.isAssignableFrom(targetClass) && model instanceof TemplateDateModel && targetClass.isInstance(date = ((TemplateDateModel)model).getAsDate())) {
                return date;
            }
        }
        int itf = typeFlags;
        while (true) {
            if ((itf == 0 || (itf & 0x800) != 0) && model instanceof TemplateNumberModel) {
                number = ((TemplateNumberModel)model).getAsNumber();
                if (itf != 0 || targetClass.isInstance(number)) {
                    return number;
                }
            }
            if ((itf == 0 || (itf & 0x1000) != 0) && model instanceof TemplateDateModel) {
                Date date = ((TemplateDateModel)model).getAsDate();
                if (itf != 0 || targetClass.isInstance(date)) {
                    return date;
                }
            }
            if ((itf == 0 || (itf & 0x82000) != 0) && model instanceof TemplateScalarModel && (itf != 0 || targetClass.isAssignableFrom(String.class))) {
                String strVal = ((TemplateScalarModel)model).getAsString();
                if (itf == 0 || (itf & 0x80000) == 0) {
                    return strVal;
                }
                if (strVal.length() == 1) {
                    if ((itf & 0x2000) != 0) {
                        return new CharacterOrString(strVal);
                    }
                    return Character.valueOf(strVal.charAt(0));
                }
                if ((itf & 0x2000) != 0) {
                    return strVal;
                }
            }
            if ((itf == 0 || (itf & 0x4000) != 0) && model instanceof TemplateBooleanModel && (itf != 0 || targetClass.isAssignableFrom(Boolean.class))) {
                return ((TemplateBooleanModel)model).getAsBoolean();
            }
            if ((itf == 0 || (itf & 0x8000) != 0) && model instanceof TemplateHashModel && (itf != 0 || targetClass.isAssignableFrom(HashAdapter.class))) {
                return new HashAdapter((TemplateHashModel)model, this);
            }
            if ((itf == 0 || (itf & 0x10000) != 0) && model instanceof TemplateSequenceModel && (itf != 0 || targetClass.isAssignableFrom(SequenceAdapter.class))) {
                return new SequenceAdapter((TemplateSequenceModel)model, this);
            }
            if ((itf == 0 || (itf & 0x20000) != 0) && model instanceof TemplateCollectionModel && (itf != 0 || targetClass.isAssignableFrom(SetAdapter.class))) {
                return new SetAdapter((TemplateCollectionModel)model, this);
            }
            if ((itf & 0x40000) != 0 && model instanceof TemplateSequenceModel) {
                return new SequenceAdapter((TemplateSequenceModel)model, this);
            }
            if (itf == 0) break;
            itf = 0;
        }
        if (targetClass.isInstance(model)) {
            return model;
        }
        return ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object unwrapSequenceToArray(TemplateSequenceModel seq, Class<?> arrayClass, boolean tryOnly, Map<Object, Object> recursionStops) throws TemplateModelException {
        if (recursionStops != null) {
            Object retval = recursionStops.get(seq);
            if (retval != null) {
                return retval;
            }
        } else {
            recursionStops = new IdentityHashMap<Object, Object>();
        }
        Class<?> componentType = arrayClass.getComponentType();
        int size = seq.size();
        Object array = Array.newInstance(componentType, size);
        recursionStops.put(seq, array);
        try {
            for (int i = 0; i < size; ++i) {
                TemplateModel seqItem = seq.get(i);
                Object val = this.tryUnwrapTo(seqItem, componentType, 0, recursionStops);
                if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                    if (tryOnly) {
                        Object object = ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS;
                        return object;
                    }
                    throw new _TemplateModelException("Failed to convert ", new _DelayedFTLTypeDescription(seq), " object to ", new _DelayedShortClassName(array.getClass()), ": Problematic sequence item at index ", i, " with value type: ", new _DelayedFTLTypeDescription(seqItem));
                }
                Array.set(array, i, val);
            }
        }
        finally {
            recursionStops.remove(seq);
        }
        return array;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object listToArray(List<?> list, Class<?> arrayClass, Map<Object, Object> recursionStops) throws TemplateModelException {
        if (list instanceof SequenceAdapter) {
            return this.unwrapSequenceToArray(((SequenceAdapter)list).getTemplateSequenceModel(), arrayClass, false, recursionStops);
        }
        if (recursionStops != null) {
            Object retval = recursionStops.get(list);
            if (retval != null) {
                return retval;
            }
        } else {
            recursionStops = new IdentityHashMap<Object, Object>();
        }
        Class<?> componentType = arrayClass.getComponentType();
        Object array = Array.newInstance(componentType, list.size());
        recursionStops.put(list, array);
        try {
            boolean isComponentTypeExamined = false;
            boolean isComponentTypeNumerical = false;
            boolean isComponentTypeList = false;
            int i = 0;
            for (Object listItem : list) {
                if (listItem != null && !componentType.isInstance(listItem)) {
                    if (!isComponentTypeExamined) {
                        isComponentTypeNumerical = ClassUtil.isNumerical(componentType);
                        isComponentTypeList = List.class.isAssignableFrom(componentType);
                        isComponentTypeExamined = true;
                    }
                    if (isComponentTypeNumerical && listItem instanceof Number) {
                        listItem = BeansWrapper.forceUnwrappedNumberToType((Number)listItem, componentType, true);
                    } else if (componentType == String.class && listItem instanceof Character) {
                        listItem = String.valueOf(((Character)listItem).charValue());
                    } else if ((componentType == Character.class || componentType == Character.TYPE) && listItem instanceof String) {
                        String listItemStr = (String)listItem;
                        if (listItemStr.length() == 1) {
                            listItem = Character.valueOf(listItemStr.charAt(0));
                        }
                    } else if (componentType.isArray()) {
                        if (listItem instanceof List) {
                            listItem = this.listToArray((List)listItem, componentType, recursionStops);
                        } else if (listItem instanceof TemplateSequenceModel) {
                            listItem = this.unwrapSequenceToArray((TemplateSequenceModel)listItem, componentType, false, recursionStops);
                        }
                    } else if (isComponentTypeList && listItem.getClass().isArray()) {
                        listItem = this.arrayToList(listItem);
                    }
                }
                try {
                    Array.set(array, i, listItem);
                }
                catch (IllegalArgumentException e) {
                    throw new TemplateModelException("Failed to convert " + ClassUtil.getShortClassNameOfObject(list) + " object to " + ClassUtil.getShortClassNameOfObject(array) + ": Problematic List item at index " + i + " with value type: " + ClassUtil.getShortClassNameOfObject(listItem), e);
                }
                ++i;
            }
        }
        finally {
            recursionStops.remove(list);
        }
        return array;
    }

    List<?> arrayToList(Object array) throws TemplateModelException {
        if (array instanceof Object[]) {
            Object[] objArray = (Object[])array;
            return objArray.length == 0 ? Collections.EMPTY_LIST : new NonPrimitiveArrayBackedReadOnlyList(objArray);
        }
        return Array.getLength(array) == 0 ? Collections.EMPTY_LIST : new PrimtiveArrayBackedReadOnlyList(array);
    }

    static Number forceUnwrappedNumberToType(Number n, Class<?> targetType, boolean bugfixed) {
        Number oriN;
        if (targetType == n.getClass()) {
            return n;
        }
        if (targetType == Integer.TYPE || targetType == Integer.class) {
            return n instanceof Integer ? (Integer)n : Integer.valueOf(n.intValue());
        }
        if (targetType == Long.TYPE || targetType == Long.class) {
            return n instanceof Long ? (Long)n : Long.valueOf(n.longValue());
        }
        if (targetType == Double.TYPE || targetType == Double.class) {
            return n instanceof Double ? (Double)n : Double.valueOf(n.doubleValue());
        }
        if (targetType == BigDecimal.class) {
            if (n instanceof BigDecimal) {
                return n;
            }
            if (n instanceof BigInteger) {
                return new BigDecimal((BigInteger)n);
            }
            if (n instanceof Long) {
                return BigDecimal.valueOf(n.longValue());
            }
            return new BigDecimal(n.doubleValue());
        }
        if (targetType == Float.TYPE || targetType == Float.class) {
            return n instanceof Float ? (Float)n : Float.valueOf(n.floatValue());
        }
        if (targetType == Byte.TYPE || targetType == Byte.class) {
            return n instanceof Byte ? (Byte)n : Byte.valueOf(n.byteValue());
        }
        if (targetType == Short.TYPE || targetType == Short.class) {
            return n instanceof Short ? (Short)n : Short.valueOf(n.shortValue());
        }
        if (targetType == BigInteger.class) {
            if (n instanceof BigInteger) {
                return n;
            }
            if (bugfixed) {
                if (n instanceof OverloadedNumberUtil.IntegerBigDecimal) {
                    return ((OverloadedNumberUtil.IntegerBigDecimal)n).bigIntegerValue();
                }
                if (n instanceof BigDecimal) {
                    return ((BigDecimal)n).toBigInteger();
                }
                return BigInteger.valueOf(n.longValue());
            }
            return new BigInteger(n.toString());
        }
        Number number = oriN = n instanceof OverloadedNumberUtil.NumberWithFallbackType ? (Number)((OverloadedNumberUtil.NumberWithFallbackType)n).getSourceNumber() : (Number)n;
        if (targetType.isInstance(oriN)) {
            return oriN;
        }
        return null;
    }

    protected TemplateModel invokeMethod(Object object, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, TemplateModelException {
        Object retval = method.invoke(object, args);
        return method.getReturnType() == Void.TYPE ? TemplateModel.NOTHING : this.getOuterIdentity().wrap(retval);
    }

    protected TemplateModel readField(Object object, Field field) throws IllegalAccessException, TemplateModelException {
        return this.getOuterIdentity().wrap(field.get(object));
    }

    public TemplateHashModel getStaticModels() {
        return this.staticModels;
    }

    public TemplateHashModel getEnumModels() {
        if (this.enumModels == null) {
            throw new UnsupportedOperationException("Enums not supported before J2SE 5.");
        }
        return this.enumModels;
    }

    ModelCache getModelCache() {
        return this.modelCache;
    }

    public Object newInstance(Class<?> clazz, List arguments) throws TemplateModelException {
        try {
            Object ctors = this.classIntrospector.get(clazz).get(ClassIntrospector.CONSTRUCTORS_KEY);
            if (ctors == null) {
                throw new TemplateModelException("Class " + clazz.getName() + " has no exposed constructors.");
            }
            Constructor ctor = null;
            if (ctors instanceof SimpleMethod) {
                SimpleMethod sm = (SimpleMethod)ctors;
                ctor = (Constructor)sm.getMember();
                Object[] objargs = sm.unwrapArguments(arguments, this);
                try {
                    return ctor.newInstance(objargs);
                }
                catch (Exception e) {
                    if (e instanceof TemplateModelException) {
                        throw (TemplateModelException)e;
                    }
                    throw _MethodUtil.newInvocationTemplateModelException(null, ctor, (Throwable)e);
                }
            }
            if (ctors instanceof OverloadedMethods) {
                MemberAndArguments mma = ((OverloadedMethods)ctors).getMemberAndArguments(arguments, this);
                try {
                    return mma.invokeConstructor(this);
                }
                catch (Exception e) {
                    if (e instanceof TemplateModelException) {
                        throw (TemplateModelException)e;
                    }
                    throw _MethodUtil.newInvocationTemplateModelException(null, mma.getCallableMemberDescriptor(), (Throwable)e);
                }
            }
            throw new BugException();
        }
        catch (TemplateModelException e) {
            throw e;
        }
        catch (Exception e) {
            throw new TemplateModelException("Error while creating new instance of class " + clazz.getName() + "; see cause exception", e);
        }
    }

    public void removeFromClassIntrospectionCache(Class<?> clazz) {
        this.classIntrospector.remove(clazz);
    }

    @Deprecated
    public void clearClassIntrospecitonCache() {
        this.classIntrospector.clearCache();
    }

    public void clearClassIntrospectionCache() {
        this.classIntrospector.clearCache();
    }

    ClassIntrospector getClassIntrospector() {
        return this.classIntrospector;
    }

    @Deprecated
    protected void finetuneMethodAppearance(Class<?> clazz, Method m, MethodAppearanceDecision decision) {
    }

    public static void coerceBigDecimals(AccessibleObject callable, Object[] args) {
        Class<?>[] formalTypes = null;
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (!(arg instanceof BigDecimal)) continue;
            if (formalTypes == null) {
                if (callable instanceof Method) {
                    formalTypes = ((Method)callable).getParameterTypes();
                } else if (callable instanceof Constructor) {
                    formalTypes = ((Constructor)callable).getParameterTypes();
                } else {
                    throw new IllegalArgumentException("Expected method or  constructor; callable is " + callable.getClass().getName());
                }
            }
            args[i] = BeansWrapper.coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
        }
    }

    public static void coerceBigDecimals(Class<?>[] formalTypes, Object[] args) {
        int typeLen = formalTypes.length;
        int argsLen = args.length;
        int min = Math.min(typeLen, argsLen);
        for (int i = 0; i < min; ++i) {
            Object arg = args[i];
            if (!(arg instanceof BigDecimal)) continue;
            args[i] = BeansWrapper.coerceBigDecimal((BigDecimal)arg, formalTypes[i]);
        }
        if (argsLen > typeLen) {
            Class<?> varArgType = formalTypes[typeLen - 1];
            for (int i = typeLen; i < argsLen; ++i) {
                Object arg = args[i];
                if (!(arg instanceof BigDecimal)) continue;
                args[i] = BeansWrapper.coerceBigDecimal((BigDecimal)arg, varArgType);
            }
        }
    }

    public static Object coerceBigDecimal(BigDecimal bd, Class<?> formalType) {
        if (formalType == Integer.TYPE || formalType == Integer.class) {
            return bd.intValue();
        }
        if (formalType == Double.TYPE || formalType == Double.class) {
            return bd.doubleValue();
        }
        if (formalType == Long.TYPE || formalType == Long.class) {
            return bd.longValue();
        }
        if (formalType == Float.TYPE || formalType == Float.class) {
            return Float.valueOf(bd.floatValue());
        }
        if (formalType == Short.TYPE || formalType == Short.class) {
            return bd.shortValue();
        }
        if (formalType == Byte.TYPE || formalType == Byte.class) {
            return bd.byteValue();
        }
        if (BigInteger.class.isAssignableFrom(formalType)) {
            return bd.toBigInteger();
        }
        return bd;
    }

    public String toString() {
        String propsStr = this.toPropertiesString();
        return ClassUtil.getShortClassNameOfObject(this) + "@" + System.identityHashCode(this) + "(" + this.incompatibleImprovements + ", " + (propsStr.length() != 0 ? propsStr + ", ..." : "") + ")";
    }

    protected String toPropertiesString() {
        return "simpleMapWrapper=" + this.simpleMapWrapper + ", exposureLevel=" + this.classIntrospector.getExposureLevel() + ", exposeFields=" + this.classIntrospector.getExposeFields() + ", preferIndexedReadMethod=" + this.preferIndexedReadMethod + ", treatDefaultMethodsAsBeanMembers=" + this.classIntrospector.getTreatDefaultMethodsAsBeanMembers() + ", sharedClassIntrospCache=" + (this.classIntrospector.isShared() ? "@" + System.identityHashCode(this.classIntrospector) : "none");
    }

    static {
        ITERATOR_FACTORY = new ModelFactory(){

            @Override
            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return new IteratorModel((Iterator)object, (BeansWrapper)wrapper);
            }
        };
        ENUMERATION_FACTORY = new ModelFactory(){

            @Override
            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return new EnumerationModel((Enumeration)object, (BeansWrapper)wrapper);
            }
        };
    }

    public static final class MethodAppearanceDecisionInput {
        private Method method;
        private Class<?> containingClass;

        void setMethod(Method method) {
            this.method = method;
        }

        void setContainingClass(Class<?> containingClass) {
            this.containingClass = containingClass;
        }

        public Method getMethod() {
            return this.method;
        }

        public Class getContainingClass() {
            return this.containingClass;
        }
    }

    public static final class MethodAppearanceDecision {
        private PropertyDescriptor exposeAsProperty;
        private boolean replaceExistingProperty;
        private String exposeMethodAs;
        private boolean methodShadowsProperty;

        void setDefaults(Method m) {
            this.exposeAsProperty = null;
            this.replaceExistingProperty = false;
            this.exposeMethodAs = m.getName();
            this.methodShadowsProperty = true;
        }

        public PropertyDescriptor getExposeAsProperty() {
            return this.exposeAsProperty;
        }

        public void setExposeAsProperty(PropertyDescriptor exposeAsProperty) {
            this.exposeAsProperty = exposeAsProperty;
        }

        public boolean getReplaceExistingProperty() {
            return this.replaceExistingProperty;
        }

        public void setReplaceExistingProperty(boolean overrideExistingProperty) {
            this.replaceExistingProperty = overrideExistingProperty;
        }

        public String getExposeMethodAs() {
            return this.exposeMethodAs;
        }

        public void setExposeMethodAs(String exposeAsMethod) {
            this.exposeMethodAs = exposeAsMethod;
        }

        public boolean getMethodShadowsProperty() {
            return this.methodShadowsProperty;
        }

        public void setMethodShadowsProperty(boolean shadowEarlierProperty) {
            this.methodShadowsProperty = shadowEarlierProperty;
        }
    }
}

