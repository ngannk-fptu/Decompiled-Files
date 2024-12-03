/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._JavaVersions;
import freemarker.ext.beans.AllowAllMemberAccessPolicy;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassBasedModelFactory;
import freemarker.ext.beans.ClassChangeNotifier;
import freemarker.ext.beans.ClassIntrospectorBuilder;
import freemarker.ext.beans.ClassMemberAccessPolicy;
import freemarker.ext.beans.ExecutableMemberSignature;
import freemarker.ext.beans.FastPropertyDescriptor;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.beans.MethodAppearanceFineTuner;
import freemarker.ext.beans.MethodSorter;
import freemarker.ext.beans.OverloadedMethods;
import freemarker.ext.beans.SimpleMethod;
import freemarker.ext.beans._MethodUtil;
import freemarker.ext.util.ModelCache;
import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ClassIntrospector {
    private static final Logger LOG;
    private static final String JREBEL_SDK_CLASS_NAME = "org.zeroturnaround.javarebel.ClassEventListener";
    private static final String JREBEL_INTEGRATION_ERROR_MSG = "Error initializing JRebel integration. JRebel integration disabled.";
    private static final ExecutableMemberSignature GET_STRING_SIGNATURE;
    private static final ExecutableMemberSignature GET_OBJECT_SIGNATURE;
    private static final ExecutableMemberSignature TO_STRING_SIGNATURE;
    static final boolean DEVELOPMENT_MODE;
    private static final ClassChangeNotifier CLASS_CHANGE_NOTIFIER;
    private static final Object ARG_TYPES_BY_METHOD_KEY;
    static final Object CONSTRUCTORS_KEY;
    static final Object GENERIC_GET_KEY;
    static final Object TO_STRING_HIDDEN_FLAG_KEY;
    final int exposureLevel;
    final boolean exposeFields;
    final MemberAccessPolicy memberAccessPolicy;
    final MethodAppearanceFineTuner methodAppearanceFineTuner;
    final MethodSorter methodSorter;
    final boolean treatDefaultMethodsAsBeanMembers;
    final Version incompatibleImprovements;
    private final boolean hasSharedInstanceRestrictions;
    private final boolean shared;
    private final Object sharedLock;
    private final Map<Class<?>, Map<Object, Object>> cache = new ConcurrentHashMap(0, 0.75f, 16);
    private final Set<String> cacheClassNames = new HashSet<String>(0);
    private final Set<Class<?>> classIntrospectionsInProgress = new HashSet(0);
    private final List<WeakReference<Object>> modelFactories = new LinkedList<WeakReference<Object>>();
    private final ReferenceQueue<Object> modelFactoriesRefQueue = new ReferenceQueue();
    private int clearingCounter;

    ClassIntrospector(ClassIntrospectorBuilder builder, Object sharedLock, boolean hasSharedInstanceRestrictions, boolean shared) {
        NullArgumentException.check("sharedLock", sharedLock);
        this.exposureLevel = builder.getExposureLevel();
        this.exposeFields = builder.getExposeFields();
        this.memberAccessPolicy = builder.getMemberAccessPolicy();
        this.methodAppearanceFineTuner = builder.getMethodAppearanceFineTuner();
        this.methodSorter = builder.getMethodSorter();
        this.treatDefaultMethodsAsBeanMembers = builder.getTreatDefaultMethodsAsBeanMembers();
        this.incompatibleImprovements = builder.getIncompatibleImprovements();
        this.sharedLock = sharedLock;
        this.hasSharedInstanceRestrictions = hasSharedInstanceRestrictions;
        this.shared = shared;
        if (CLASS_CHANGE_NOTIFIER != null) {
            CLASS_CHANGE_NOTIFIER.subscribe(this);
        }
    }

    ClassIntrospectorBuilder createBuilder() {
        return new ClassIntrospectorBuilder(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Map<Object, Object> get(Class<?> clazz) {
        String className;
        Map<Object, Object> introspData = this.cache.get(clazz);
        if (introspData != null) {
            return introspData;
        }
        Object object = this.sharedLock;
        synchronized (object) {
            Map<Object, Object> introspData2 = this.cache.get(clazz);
            if (introspData2 != null) {
                return introspData2;
            }
            className = clazz.getName();
            if (this.cacheClassNames.contains(className)) {
                this.onSameNameClassesDetected(className);
            }
            while (introspData2 == null && this.classIntrospectionsInProgress.contains(clazz)) {
                try {
                    this.sharedLock.wait();
                    introspData2 = this.cache.get(clazz);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException("Class inrospection data lookup aborded: " + e);
                }
            }
            if (introspData2 != null) {
                return introspData2;
            }
            this.classIntrospectionsInProgress.add(clazz);
        }
        try {
            Map<Object, Object> introspData3 = this.createClassIntrospectionData(clazz);
            Map<Object, Object> map = this.sharedLock;
            synchronized (map) {
                this.cache.put(clazz, introspData3);
                this.cacheClassNames.add(className);
            }
            map = introspData3;
            return map;
        }
        finally {
            Object object2 = this.sharedLock;
            synchronized (object2) {
                this.classIntrospectionsInProgress.remove(clazz);
                this.sharedLock.notifyAll();
            }
        }
    }

    private Map<Object, Object> createClassIntrospectionData(Class<?> clazz) {
        HashMap<Object, Object> introspData = new HashMap<Object, Object>();
        MemberAccessPolicy effMemberAccessPolicy = this.getEffectiveMemberAccessPolicy();
        ClassMemberAccessPolicy effClassMemberAccessPolicy = effMemberAccessPolicy.forClass(clazz);
        if (this.exposeFields) {
            this.addFieldsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
        }
        Map<ExecutableMemberSignature, List<Method>> accessibleMethods = ClassIntrospector.discoverAccessibleMethods(clazz);
        if (!effMemberAccessPolicy.isToStringAlwaysExposed()) {
            this.addToStringHiddenFlagToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
        }
        this.addGenericGetToClassIntrospectionData(introspData, accessibleMethods, effClassMemberAccessPolicy);
        if (this.exposureLevel != 3) {
            try {
                this.addBeanInfoToClassIntrospectionData(introspData, clazz, accessibleMethods, effClassMemberAccessPolicy);
            }
            catch (IntrospectionException e) {
                LOG.warn("Couldn't properly perform introspection for class " + clazz, e);
                introspData.clear();
            }
        }
        this.addConstructorsToClassIntrospectionData(introspData, clazz, effClassMemberAccessPolicy);
        if (introspData.size() > 1) {
            return introspData;
        }
        if (introspData.size() == 0) {
            return Collections.emptyMap();
        }
        Map.Entry e = introspData.entrySet().iterator().next();
        return Collections.singletonMap(e.getKey(), e.getValue());
    }

    private void addFieldsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws SecurityException {
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if ((field.getModifiers() & 8) != 0 || !effClassMemberAccessPolicy.isFieldExposed(field)) continue;
            introspData.put(field.getName(), field);
        }
    }

    private void addBeanInfoToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        List<PropertyDescriptor> pdas = this.getPropertyDescriptors(beanInfo, clazz);
        int pdasLength = pdas.size();
        for (int i = pdasLength - 1; i >= 0; --i) {
            this.addPropertyDescriptorToClassIntrospectionData(introspData, pdas.get(i), accessibleMethods, effClassMemberAccessPolicy);
        }
        if (this.exposureLevel < 2) {
            BeansWrapper.MethodAppearanceDecision decision = new BeansWrapper.MethodAppearanceDecision();
            BeansWrapper.MethodAppearanceDecisionInput decisionInput = null;
            List<MethodDescriptor> mds = this.getMethodDescriptors(beanInfo, clazz);
            this.sortMethodDescriptors(mds);
            int mdsSize = mds.size();
            IdentityHashMap<Method, Object> argTypesUsedByIndexerPropReaders = null;
            for (int i = mdsSize - 1; i >= 0; --i) {
                String methodKey;
                PropertyDescriptor propDesc;
                Method method = ClassIntrospector.getMatchingAccessibleMethod(mds.get(i).getMethod(), accessibleMethods);
                if (method == null || !effClassMemberAccessPolicy.isMethodExposed(method)) continue;
                decision.setDefaults(method);
                if (this.methodAppearanceFineTuner != null) {
                    if (decisionInput == null) {
                        decisionInput = new BeansWrapper.MethodAppearanceDecisionInput();
                    }
                    decisionInput.setContainingClass(clazz);
                    decisionInput.setMethod(method);
                    this.methodAppearanceFineTuner.process(decisionInput, decision);
                }
                if ((propDesc = decision.getExposeAsProperty()) != null && (decision.getReplaceExistingProperty() || !(introspData.get(propDesc.getName()) instanceof FastPropertyDescriptor))) {
                    this.addPropertyDescriptorToClassIntrospectionData(introspData, propDesc, accessibleMethods, effClassMemberAccessPolicy);
                }
                if ((methodKey = decision.getExposeMethodAs()) == null) continue;
                Object previous = introspData.get(methodKey);
                if (previous instanceof Method) {
                    OverloadedMethods overloadedMethods = new OverloadedMethods(this.is2321Bugfixed());
                    overloadedMethods.addMethod((Method)previous);
                    overloadedMethods.addMethod(method);
                    introspData.put(methodKey, overloadedMethods);
                    if (argTypesUsedByIndexerPropReaders != null && argTypesUsedByIndexerPropReaders.containsKey(previous)) continue;
                    ClassIntrospector.getArgTypesByMethod(introspData).remove(previous);
                    continue;
                }
                if (previous instanceof OverloadedMethods) {
                    ((OverloadedMethods)previous).addMethod(method);
                    continue;
                }
                if (!decision.getMethodShadowsProperty() && previous instanceof FastPropertyDescriptor) continue;
                introspData.put(methodKey, method);
                Class<?>[] replaced = ClassIntrospector.getArgTypesByMethod(introspData).put(method, method.getParameterTypes());
                if (replaced == null) continue;
                if (argTypesUsedByIndexerPropReaders == null) {
                    argTypesUsedByIndexerPropReaders = new IdentityHashMap<Method, Object>();
                }
                argTypesUsedByIndexerPropReaders.put(method, null);
            }
        }
    }

    private List<PropertyDescriptor> getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) {
        List<PropertyDescriptor> introspectorPDs;
        PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> list = introspectorPDs = introspectorPDsArray != null ? Arrays.asList(introspectorPDsArray) : Collections.emptyList();
        if (!this.treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null) {
            return introspectorPDs;
        }
        LinkedHashMap<String, Object> mergedPRMPs = null;
        for (Method method : clazz.getMethods()) {
            String propName;
            Class<?>[] paramTypes;
            if (!_JavaVersions.JAVA_8.isDefaultMethod(method) || method.getReturnType() == Void.TYPE || method.isBridge() || (paramTypes = method.getParameterTypes()).length != 0 && (paramTypes.length != 1 || paramTypes[0] != Integer.TYPE) || (propName = _MethodUtil.getBeanPropertyNameFromReaderMethodName(method.getName(), method.getReturnType())) == null) continue;
            if (mergedPRMPs == null) {
                mergedPRMPs = new LinkedHashMap<String, Object>();
            }
            if (paramTypes.length == 0) {
                this.mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
                continue;
            }
            this.mergeInPropertyReaderMethodPair(mergedPRMPs, propName, new PropertyReaderMethodPair(null, method));
        }
        if (mergedPRMPs == null) {
            return introspectorPDs;
        }
        for (PropertyDescriptor introspectorPD : introspectorPDs) {
            this.mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
        }
        ArrayList<PropertyDescriptor> mergedPDs = new ArrayList<PropertyDescriptor>(mergedPRMPs.size());
        for (Map.Entry entry : mergedPRMPs.entrySet()) {
            Method indexedReadMethod;
            Method readMethod;
            String propName = (String)entry.getKey();
            Object propDescObj = entry.getValue();
            if (propDescObj instanceof PropertyDescriptor) {
                mergedPDs.add((PropertyDescriptor)propDescObj);
                continue;
            }
            if (propDescObj instanceof Method) {
                readMethod = (Method)propDescObj;
                indexedReadMethod = null;
            } else if (propDescObj instanceof PropertyReaderMethodPair) {
                PropertyReaderMethodPair prmp = (PropertyReaderMethodPair)propDescObj;
                readMethod = prmp.readMethod;
                indexedReadMethod = prmp.indexedReadMethod;
                if (readMethod != null && indexedReadMethod != null && indexedReadMethod.getReturnType() != readMethod.getReturnType().getComponentType()) {
                    indexedReadMethod = null;
                }
            } else {
                throw new BugException();
            }
            try {
                mergedPDs.add(indexedReadMethod != null ? new IndexedPropertyDescriptor(propName, readMethod, null, indexedReadMethod, null) : new PropertyDescriptor(propName, readMethod, null));
            }
            catch (IntrospectionException e) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn("Failed creating property descriptor for " + clazz.getName() + " property " + propName, e);
            }
        }
        return mergedPDs;
    }

    private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
        String propName = pd.getName();
        Object replaced = mergedPRMPs.put(propName, pd);
        if (replaced != null) {
            PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
            this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
        }
    }

    private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs, String propName, PropertyReaderMethodPair newPRM) {
        Object replaced = mergedPRMPs.put(propName, newPRM);
        if (replaced != null) {
            this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
        }
    }

    private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs, String propName, Method readerMethod) {
        Object replaced = mergedPRMPs.put(propName, readerMethod);
        if (replaced != null) {
            this.putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, new PropertyReaderMethodPair(readerMethod, null));
        }
    }

    private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs, String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
        PropertyReaderMethodPair replacedPRMP = PropertyReaderMethodPair.from(replaced);
        PropertyReaderMethodPair mergedPRMP = PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
        if (!mergedPRMP.equals(newPRMP)) {
            mergedPRMPs.put(propName, mergedPRMP);
        }
    }

    /*
     * WARNING - void declaration
     */
    private List<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo, Class<?> clazz) {
        void var8_10;
        List<MethodDescriptor> introspectionMDs;
        MethodDescriptor[] introspectorMDArray = beanInfo.getMethodDescriptors();
        List<MethodDescriptor> list = introspectionMDs = introspectorMDArray != null && introspectorMDArray.length != 0 ? Arrays.asList(introspectorMDArray) : Collections.emptyList();
        if (!this.treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null) {
            return introspectionMDs;
        }
        HashMap<String, ArrayList<Method>> defaultMethodsToAddByName = null;
        Method[] methodArray = clazz.getMethods();
        int n = methodArray.length;
        boolean bl = false;
        while (var8_10 < n) {
            Method method = methodArray[var8_10];
            if (_JavaVersions.JAVA_8.isDefaultMethod(method) && !method.isBridge()) {
                ArrayList<Method> overloads;
                if (defaultMethodsToAddByName == null) {
                    defaultMethodsToAddByName = new HashMap<String, ArrayList<Method>>();
                }
                if ((overloads = (ArrayList<Method>)defaultMethodsToAddByName.get(method.getName())) == null) {
                    overloads = new ArrayList<Method>(0);
                    defaultMethodsToAddByName.put(method.getName(), overloads);
                }
                overloads.add(method);
            }
            ++var8_10;
        }
        if (defaultMethodsToAddByName == null) {
            return introspectionMDs;
        }
        ArrayList<MethodDescriptor> newIntrospectionMDs = new ArrayList<MethodDescriptor>(introspectionMDs.size() + 16);
        for (MethodDescriptor methodDescriptor : introspectionMDs) {
            Method introspectorM = methodDescriptor.getMethod();
            if (this.containsMethodWithSameParameterTypes((List)defaultMethodsToAddByName.get(introspectorM.getName()), introspectorM)) continue;
            newIntrospectionMDs.add(methodDescriptor);
        }
        introspectionMDs = newIntrospectionMDs;
        for (Map.Entry entry : defaultMethodsToAddByName.entrySet()) {
            for (Method method : (List)entry.getValue()) {
                introspectionMDs.add(new MethodDescriptor(method));
            }
        }
        return introspectionMDs;
    }

    private boolean containsMethodWithSameParameterTypes(List<Method> overloads, Method m) {
        if (overloads == null) {
            return false;
        }
        Object[] paramTypes = m.getParameterTypes();
        for (Method overload : overloads) {
            if (!Arrays.equals(overload.getParameterTypes(), paramTypes)) continue;
            return true;
        }
        return false;
    }

    private void addPropertyDescriptorToClassIntrospectionData(Map<Object, Object> introspData, PropertyDescriptor pd, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method indexedReadMethod;
        Method readMethod = ClassIntrospector.getMatchingAccessibleMethod(pd.getReadMethod(), accessibleMethods);
        if (readMethod != null && !effClassMemberAccessPolicy.isMethodExposed(readMethod)) {
            readMethod = null;
        }
        if (pd instanceof IndexedPropertyDescriptor) {
            indexedReadMethod = ClassIntrospector.getMatchingAccessibleMethod(((IndexedPropertyDescriptor)pd).getIndexedReadMethod(), accessibleMethods);
            if (indexedReadMethod != null && !effClassMemberAccessPolicy.isMethodExposed(indexedReadMethod)) {
                indexedReadMethod = null;
            }
            if (indexedReadMethod != null) {
                ClassIntrospector.getArgTypesByMethod(introspData).put(indexedReadMethod, indexedReadMethod.getParameterTypes());
            }
        } else {
            indexedReadMethod = null;
        }
        if (readMethod != null || indexedReadMethod != null) {
            introspData.put(pd.getName(), new FastPropertyDescriptor(readMethod, indexedReadMethod));
        }
    }

    private void addGenericGetToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method genericGet = ClassIntrospector.getFirstAccessibleMethod(GET_STRING_SIGNATURE, accessibleMethods);
        if (genericGet == null) {
            genericGet = ClassIntrospector.getFirstAccessibleMethod(GET_OBJECT_SIGNATURE, accessibleMethods);
        }
        if (genericGet != null && effClassMemberAccessPolicy.isMethodExposed(genericGet)) {
            introspData.put(GENERIC_GET_KEY, genericGet);
        }
    }

    private void addToStringHiddenFlagToClassIntrospectionData(Map<Object, Object> introspData, Map<ExecutableMemberSignature, List<Method>> accessibleMethods, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        Method toStringMethod = ClassIntrospector.getFirstAccessibleMethod(TO_STRING_SIGNATURE, accessibleMethods);
        if (toStringMethod == null) {
            throw new BugException("toString() method not found");
        }
        if (!effClassMemberAccessPolicy.isMethodExposed(toStringMethod)) {
            introspData.put(TO_STRING_HIDDEN_FLAG_KEY, true);
        }
    }

    private void addConstructorsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz, ClassMemberAccessPolicy effClassMemberAccessPolicy) {
        try {
            Constructor<?>[] ctorsUnfiltered = clazz.getConstructors();
            ArrayList ctors = new ArrayList(ctorsUnfiltered.length);
            for (Constructor<?> constructor : ctorsUnfiltered) {
                if (!effClassMemberAccessPolicy.isConstructorExposed(constructor)) continue;
                ctors.add(constructor);
            }
            if (!ctors.isEmpty()) {
                Object ctorsIntrospData;
                if (ctors.size() == 1) {
                    Constructor ctor = (Constructor)ctors.get(0);
                    ctorsIntrospData = new SimpleMethod(ctor, ctor.getParameterTypes());
                } else {
                    OverloadedMethods overloadedCtors = new OverloadedMethods(this.is2321Bugfixed());
                    for (Constructor constructor : ctors) {
                        overloadedCtors.addConstructor(constructor);
                    }
                    ctorsIntrospData = overloadedCtors;
                }
                introspData.put(CONSTRUCTORS_KEY, ctorsIntrospData);
            }
        }
        catch (SecurityException e) {
            LOG.warn("Can't discover constructors for class " + clazz.getName(), e);
        }
    }

    private static Map<ExecutableMemberSignature, List<Method>> discoverAccessibleMethods(Class<?> clazz) {
        HashMap<ExecutableMemberSignature, List<Method>> accessibles = new HashMap<ExecutableMemberSignature, List<Method>>();
        ClassIntrospector.discoverAccessibleMethods(clazz, accessibles);
        return accessibles;
    }

    private static void discoverAccessibleMethods(Class<?> clazz, Map<ExecutableMemberSignature, List<Method>> accessibles) {
        if (Modifier.isPublic(clazz.getModifiers())) {
            try {
                Method[] methods = clazz.getMethods();
                for (int i = 0; i < methods.length; ++i) {
                    Method method = methods[i];
                    if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) continue;
                    ExecutableMemberSignature sig = new ExecutableMemberSignature(method);
                    List<Method> methodList = accessibles.get(sig);
                    if (methodList == null) {
                        methodList = new LinkedList<Method>();
                        accessibles.put(sig, methodList);
                    }
                    methodList.add(method);
                }
                return;
            }
            catch (SecurityException e) {
                LOG.warn("Could not discover accessible methods of class " + clazz.getName() + ", attemping superclasses/interfaces.", e);
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            ClassIntrospector.discoverAccessibleMethods(interfaces[i], accessibles);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            ClassIntrospector.discoverAccessibleMethods(superclass, accessibles);
        }
    }

    private static Method getMatchingAccessibleMethod(Method m, Map<ExecutableMemberSignature, List<Method>> accessibles) {
        if (m == null) {
            return null;
        }
        List<Method> ams = accessibles.get(new ExecutableMemberSignature(m));
        return ams != null ? _MethodUtil.getMethodWithClosestNonSubReturnType(m.getReturnType(), ams) : null;
    }

    private static Method getFirstAccessibleMethod(ExecutableMemberSignature sig, Map<ExecutableMemberSignature, List<Method>> accessibles) {
        List<Method> ams = accessibles.get(sig);
        if (ams == null || ams.isEmpty()) {
            return null;
        }
        return ams.get(0);
    }

    private void sortMethodDescriptors(List<MethodDescriptor> methodDescriptors) {
        if (this.methodSorter != null) {
            this.methodSorter.sortMethodDescriptors(methodDescriptors);
        }
    }

    MemberAccessPolicy getEffectiveMemberAccessPolicy() {
        return this.exposureLevel < 1 ? AllowAllMemberAccessPolicy.INSTANCE : this.memberAccessPolicy;
    }

    private boolean is2321Bugfixed() {
        return BeansWrapper.is2321Bugfixed(this.incompatibleImprovements);
    }

    private static Map<Method, Class<?>[]> getArgTypesByMethod(Map<Object, Object> classInfo) {
        HashMap argTypes = (HashMap)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        if (argTypes == null) {
            argTypes = new HashMap();
            classInfo.put(ARG_TYPES_BY_METHOD_KEY, argTypes);
        }
        return argTypes;
    }

    void clearCache() {
        if (this.getHasSharedInstanceRestrictions()) {
            throw new IllegalStateException("It's not allowed to clear the whole cache in a read-only " + this.getClass().getName() + "instance. Use removeFromClassIntrospectionCache(String prefix) instead.");
        }
        this.forcedClearCache();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void forcedClearCache() {
        Object object = this.sharedLock;
        synchronized (object) {
            this.cache.clear();
            this.cacheClassNames.clear();
            ++this.clearingCounter;
            for (WeakReference<Object> regedMfREf : this.modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf == null) continue;
                if (regedMf instanceof ClassBasedModelFactory) {
                    ((ClassBasedModelFactory)regedMf).clearCache();
                    continue;
                }
                if (regedMf instanceof ModelCache) {
                    ((ModelCache)regedMf).clearCache();
                    continue;
                }
                throw new BugException();
            }
            this.removeClearedModelFactoryReferences();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void remove(Class<?> clazz) {
        Object object = this.sharedLock;
        synchronized (object) {
            this.cache.remove(clazz);
            this.cacheClassNames.remove(clazz.getName());
            ++this.clearingCounter;
            for (WeakReference<Object> regedMfREf : this.modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf == null) continue;
                if (regedMf instanceof ClassBasedModelFactory) {
                    ((ClassBasedModelFactory)regedMf).removeFromCache(clazz);
                    continue;
                }
                if (regedMf instanceof ModelCache) {
                    ((ModelCache)regedMf).clearCache();
                    continue;
                }
                throw new BugException();
            }
            this.removeClearedModelFactoryReferences();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getClearingCounter() {
        Object object = this.sharedLock;
        synchronized (object) {
            return this.clearingCounter;
        }
    }

    private void onSameNameClassesDetected(String className) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Detected multiple classes with the same name, \"" + className + "\". Assuming it was a class-reloading. Clearing class introspection caches to release old data.");
        }
        this.forcedClearCache();
    }

    void registerModelFactory(ClassBasedModelFactory mf) {
        this.registerModelFactory((Object)mf);
    }

    void registerModelFactory(ModelCache mf) {
        this.registerModelFactory((Object)mf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerModelFactory(Object mf) {
        Object object = this.sharedLock;
        synchronized (object) {
            this.modelFactories.add(new WeakReference<Object>(mf, this.modelFactoriesRefQueue));
            this.removeClearedModelFactoryReferences();
        }
    }

    void unregisterModelFactory(ClassBasedModelFactory mf) {
        this.unregisterModelFactory((Object)mf);
    }

    void unregisterModelFactory(ModelCache mf) {
        this.unregisterModelFactory((Object)mf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void unregisterModelFactory(Object mf) {
        Object object = this.sharedLock;
        synchronized (object) {
            Iterator<WeakReference<Object>> it = this.modelFactories.iterator();
            while (it.hasNext()) {
                Object regedMf = it.next().get();
                if (regedMf != mf) continue;
                it.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeClearedModelFactoryReferences() {
        Reference<Object> cleardRef;
        while ((cleardRef = this.modelFactoriesRefQueue.poll()) != null) {
            Object object = this.sharedLock;
            synchronized (object) {
                Iterator<WeakReference<Object>> it = this.modelFactories.iterator();
                while (it.hasNext()) {
                    if (it.next() != cleardRef) continue;
                    it.remove();
                    break;
                }
            }
        }
    }

    static Class<?>[] getArgTypes(Map<Object, Object> classInfo, Method method) {
        Map argTypesByMethod = (Map)classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        return (Class[])argTypesByMethod.get(method);
    }

    int keyCount(Class<?> clazz) {
        Map<Object, Object> map = this.get(clazz);
        int count = map.size();
        if (map.containsKey(CONSTRUCTORS_KEY)) {
            --count;
        }
        if (map.containsKey(GENERIC_GET_KEY)) {
            --count;
        }
        if (map.containsKey(ARG_TYPES_BY_METHOD_KEY)) {
            --count;
        }
        return count;
    }

    Set<Object> keySet(Class<?> clazz) {
        HashSet<Object> set = new HashSet<Object>(this.get(clazz).keySet());
        set.remove(CONSTRUCTORS_KEY);
        set.remove(GENERIC_GET_KEY);
        set.remove(ARG_TYPES_BY_METHOD_KEY);
        return set;
    }

    int getExposureLevel() {
        return this.exposureLevel;
    }

    boolean getExposeFields() {
        return this.exposeFields;
    }

    MemberAccessPolicy getMemberAccessPolicy() {
        return this.memberAccessPolicy;
    }

    boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.treatDefaultMethodsAsBeanMembers;
    }

    MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.methodAppearanceFineTuner;
    }

    MethodSorter getMethodSorter() {
        return this.methodSorter;
    }

    boolean getHasSharedInstanceRestrictions() {
        return this.hasSharedInstanceRestrictions;
    }

    boolean isShared() {
        return this.shared;
    }

    Object getSharedLock() {
        return this.sharedLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object[] getRegisteredModelFactoriesSnapshot() {
        Object object = this.sharedLock;
        synchronized (object) {
            return this.modelFactories.toArray();
        }
    }

    static {
        ClassChangeNotifier classChangeNotifier;
        boolean jRebelAvailable;
        LOG = Logger.getLogger("freemarker.beans");
        GET_STRING_SIGNATURE = new ExecutableMemberSignature("get", new Class[]{String.class});
        GET_OBJECT_SIGNATURE = new ExecutableMemberSignature("get", new Class[]{Object.class});
        TO_STRING_SIGNATURE = new ExecutableMemberSignature("toString", new Class[0]);
        DEVELOPMENT_MODE = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development", "false"));
        try {
            Class.forName(JREBEL_SDK_CLASS_NAME);
            jRebelAvailable = true;
        }
        catch (Throwable e) {
            jRebelAvailable = false;
            try {
                if (!(e instanceof ClassNotFoundException)) {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (jRebelAvailable) {
            try {
                classChangeNotifier = (ClassChangeNotifier)Class.forName("freemarker.ext.beans.JRebelClassChangeNotifier").newInstance();
            }
            catch (Throwable e) {
                classChangeNotifier = null;
                try {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e);
                }
                catch (Throwable throwable) {}
            }
        } else {
            classChangeNotifier = null;
        }
        CLASS_CHANGE_NOTIFIER = classChangeNotifier;
        ARG_TYPES_BY_METHOD_KEY = new Object();
        CONSTRUCTORS_KEY = new Object();
        GENERIC_GET_KEY = new Object();
        TO_STRING_HIDDEN_FLAG_KEY = new Object();
    }

    private static class PropertyReaderMethodPair {
        private final Method readMethod;
        private final Method indexedReadMethod;

        PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
            this.readMethod = readerMethod;
            this.indexedReadMethod = indexedReaderMethod;
        }

        PropertyReaderMethodPair(PropertyDescriptor pd) {
            this(pd.getReadMethod(), pd instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor)pd).getIndexedReadMethod() : null);
        }

        static PropertyReaderMethodPair from(Object obj) {
            if (obj instanceof PropertyReaderMethodPair) {
                return (PropertyReaderMethodPair)obj;
            }
            if (obj instanceof PropertyDescriptor) {
                return new PropertyReaderMethodPair((PropertyDescriptor)obj);
            }
            if (obj instanceof Method) {
                return new PropertyReaderMethodPair((Method)obj, null);
            }
            throw new BugException("Unexpected obj type: " + obj.getClass().getName());
        }

        static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
            return new PropertyReaderMethodPair(newMethods.readMethod != null ? newMethods.readMethod : oldMethods.readMethod, newMethods.indexedReadMethod != null ? newMethods.indexedReadMethod : oldMethods.indexedReadMethod);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.indexedReadMethod == null ? 0 : this.indexedReadMethod.hashCode());
            result = 31 * result + (this.readMethod == null ? 0 : this.readMethod.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            PropertyReaderMethodPair other = (PropertyReaderMethodPair)obj;
            return other.readMethod == this.readMethod && other.indexedReadMethod == this.indexedReadMethod;
        }
    }
}

