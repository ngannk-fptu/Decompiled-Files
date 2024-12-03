/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.ext.beans.ArgumentTypes;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans.CallableMemberDescriptor;
import freemarker.ext.beans.ClassIntrospectorBuilder;
import freemarker.ext.beans.EmptyCallableMemberDescriptor;
import freemarker.ext.beans.MaybeEmptyCallableMemberDescriptor;
import freemarker.ext.beans.ReflectionCallableMemberDescriptor;
import freemarker.ext.beans._MethodUtil;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.CollectionUtils;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class _BeansAPI {
    private _BeansAPI() {
    }

    public static String getAsClassicCompatibleString(BeanModel bm) {
        return bm.getAsClassicCompatibleString();
    }

    public static Object newInstance(Class<?> pClass, Object[] args, BeansWrapper bw) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
        return _BeansAPI.newInstance(_BeansAPI.getConstructorDescriptor(pClass, args), args, bw);
    }

    private static CallableMemberDescriptor getConstructorDescriptor(Class<?> pClass, Object[] args) throws NoSuchMethodException {
        if (args == null) {
            args = CollectionUtils.EMPTY_OBJECT_ARRAY;
        }
        ArgumentTypes argTypes = new ArgumentTypes(args, true);
        ArrayList<ReflectionCallableMemberDescriptor> fixedArgMemberDescs = new ArrayList<ReflectionCallableMemberDescriptor>();
        ArrayList<ReflectionCallableMemberDescriptor> varArgsMemberDescs = new ArrayList<ReflectionCallableMemberDescriptor>();
        Constructor<?>[] constrs = pClass.getConstructors();
        for (int i = 0; i < constrs.length; ++i) {
            Constructor<?> constr = constrs[i];
            ReflectionCallableMemberDescriptor memberDesc = new ReflectionCallableMemberDescriptor(constr, (Class[])constr.getParameterTypes());
            if (!_MethodUtil.isVarargs(constr)) {
                fixedArgMemberDescs.add(memberDesc);
                continue;
            }
            varArgsMemberDescs.add(memberDesc);
        }
        MaybeEmptyCallableMemberDescriptor contrDesc = argTypes.getMostSpecific(fixedArgMemberDescs, false);
        if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
            contrDesc = argTypes.getMostSpecific(varArgsMemberDescs, true);
        }
        if (contrDesc instanceof EmptyCallableMemberDescriptor) {
            if (contrDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
                throw new NoSuchMethodException("There's no public " + pClass.getName() + " constructor with compatible parameter list.");
            }
            if (contrDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
                throw new NoSuchMethodException("There are multiple public " + pClass.getName() + " constructors that match the compatible parameter list with the same preferability.");
            }
            throw new NoSuchMethodException();
        }
        return (CallableMemberDescriptor)contrDesc;
    }

    private static Object newInstance(CallableMemberDescriptor constrDesc, Object[] args, BeansWrapper bw) throws InstantiationException, IllegalAccessException, InvocationTargetException, IllegalArgumentException, TemplateModelException {
        Object[] packedArgs;
        if (args == null) {
            args = CollectionUtils.EMPTY_OBJECT_ARRAY;
        }
        if (constrDesc.isVarargs()) {
            Class[] paramTypes = constrDesc.getParamTypes();
            int fixedArgCnt = paramTypes.length - 1;
            packedArgs = new Object[fixedArgCnt + 1];
            for (int i = 0; i < fixedArgCnt; ++i) {
                packedArgs[i] = args[i];
            }
            Class<?> compType = paramTypes[fixedArgCnt].getComponentType();
            int varArgCnt = args.length - fixedArgCnt;
            Object varArgsArray = Array.newInstance(compType, varArgCnt);
            for (int i = 0; i < varArgCnt; ++i) {
                Array.set(varArgsArray, i, args[fixedArgCnt + i]);
            }
            packedArgs[fixedArgCnt] = varArgsArray;
        } else {
            packedArgs = args;
        }
        return constrDesc.invokeConstructor(bw, packedArgs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> BW getBeansWrapperSubclassSingleton(BWC settings, Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue, _BeansWrapperSubclassFactory<BW, BWC> beansWrapperSubclassFactory) {
        BeansWrapper instance;
        Reference instanceRef;
        Map<BWC, WeakReference<BW>> tcclScopedCache;
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Map<ClassLoader, Map<BWC, WeakReference<BW>>> map = instanceCache;
        synchronized (map) {
            tcclScopedCache = instanceCache.get(tccl);
            if (tcclScopedCache == null) {
                tcclScopedCache = new HashMap<BWC, WeakReference<BW>>();
                instanceCache.put(tccl, tcclScopedCache);
                instanceRef = null;
            } else {
                instanceRef = tcclScopedCache.get(settings);
            }
        }
        BeansWrapper beansWrapper = instance = instanceRef != null ? (BeansWrapper)instanceRef.get() : null;
        if (instance != null) {
            return (BW)instance;
        }
        instance = beansWrapperSubclassFactory.create(settings = _BeansAPI.clone(settings));
        if (!instance.isWriteProtected()) {
            throw new BugException();
        }
        Map<ClassLoader, Map<BWC, WeakReference<BW>>> map2 = instanceCache;
        synchronized (map2) {
            BeansWrapper concurrentInstance;
            instanceRef = tcclScopedCache.get(settings);
            BeansWrapper beansWrapper2 = concurrentInstance = instanceRef != null ? (BeansWrapper)instanceRef.get() : null;
            if (concurrentInstance == null) {
                tcclScopedCache.put(settings, new WeakReference<BeansWrapper>(instance, instanceCacheRefQue));
            } else {
                instance = concurrentInstance;
            }
        }
        _BeansAPI.removeClearedReferencesFromCache(instanceCache, instanceCacheRefQue);
        return (BW)instance;
    }

    private static <BWC extends BeansWrapperConfiguration> BWC clone(BWC settings) {
        return (BWC)((BeansWrapperConfiguration)settings.clone(true));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> void removeClearedReferencesFromCache(Map<ClassLoader, Map<BWC, WeakReference<BW>>> instanceCache, ReferenceQueue<BW> instanceCacheRefQue) {
        Reference<BW> clearedRef;
        while ((clearedRef = instanceCacheRefQue.poll()) != null) {
            Map<ClassLoader, Map<BWC, WeakReference<BW>>> map = instanceCache;
            synchronized (map) {
                block4: for (Map<BWC, WeakReference<BW>> tcclScopedCache : instanceCache.values()) {
                    Iterator<WeakReference<BW>> it2 = tcclScopedCache.values().iterator();
                    while (it2.hasNext()) {
                        if (it2.next() != clearedRef) continue;
                        it2.remove();
                        break block4;
                    }
                }
            }
        }
    }

    public static ClassIntrospectorBuilder getClassIntrospectorBuilder(BeansWrapperConfiguration bwc) {
        return bwc.getClassIntrospectorBuilder();
    }

    public static interface _BeansWrapperSubclassFactory<BW extends BeansWrapper, BWC extends BeansWrapperConfiguration> {
        public BW create(BWC var1);
    }
}

