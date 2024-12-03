/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.velocity.util.MapFactory;
import org.apache.velocity.util.introspection.IntrospectionUtils;

public class MethodMap {
    private static final int MORE_SPECIFIC = 0;
    private static final int LESS_SPECIFIC = 1;
    private static final int INCOMPARABLE = 2;
    Map methodByNameMap = MapFactory.create(false);

    public void add(Method method) {
        String methodName = method.getName();
        ArrayList<Method> l = this.get(methodName);
        if (l == null) {
            l = new ArrayList<Method>();
            this.methodByNameMap.put(methodName, l);
        }
        l.add(method);
    }

    public List get(String key) {
        return (List)this.methodByNameMap.get(key);
    }

    public Method find(String methodName, Object[] args) throws AmbiguousException {
        List methodList = this.get(methodName);
        if (methodList == null) {
            return null;
        }
        int l = args.length;
        Class[] classes = new Class[l];
        for (int i = 0; i < l; ++i) {
            Object arg = args[i];
            classes[i] = arg == null ? null : arg.getClass();
        }
        return MethodMap.getBestMatch(methodList, classes);
    }

    private static Method getBestMatch(List methods, Class[] args) {
        ArrayList<Method> equivalentMatches = null;
        Method bestMatch = null;
        Class[] bestMatchTypes = null;
        block8: for (Method method : methods) {
            if (!MethodMap.isApplicable(method, args)) continue;
            if (bestMatch == null) {
                bestMatch = method;
                bestMatchTypes = method.getParameterTypes();
                continue;
            }
            Class[] methodTypes = method.getParameterTypes();
            switch (MethodMap.compare(methodTypes, bestMatchTypes)) {
                case 0: {
                    if (equivalentMatches == null) {
                        bestMatch = method;
                        bestMatchTypes = methodTypes;
                        break;
                    }
                    int ambiguities = equivalentMatches.size();
                    block9: for (int a = 0; a < ambiguities; ++a) {
                        Method other = (Method)equivalentMatches.get(a);
                        switch (MethodMap.compare(methodTypes, other.getParameterTypes())) {
                            case 0: {
                                bestMatch = method;
                                bestMatchTypes = methodTypes;
                                equivalentMatches = null;
                                ambiguities = 0;
                                continue block9;
                            }
                            case 2: {
                                equivalentMatches.add(method);
                                continue block9;
                            }
                        }
                    }
                    continue block8;
                }
                case 2: {
                    if (equivalentMatches == null) {
                        equivalentMatches = new ArrayList<Method>(bestMatchTypes.length);
                    }
                    equivalentMatches.add(method);
                    break;
                }
            }
        }
        if (equivalentMatches != null) {
            throw new AmbiguousException();
        }
        return bestMatch;
    }

    private static int compare(Class[] c1, Class[] c2) {
        boolean c1MoreSpecific = false;
        boolean c2MoreSpecific = false;
        if (c1.length > c2.length) {
            return 0;
        }
        if (c2.length > c1.length) {
            return 1;
        }
        for (int i = 0; i < c1.length; ++i) {
            if (c1[i] == c2[i]) continue;
            boolean last = i == c1.length - 1;
            c1MoreSpecific = c1MoreSpecific || MethodMap.isStrictConvertible(c2[i], c1[i], last);
            c2MoreSpecific = c2MoreSpecific || MethodMap.isStrictConvertible(c1[i], c2[i], last);
        }
        if (c1MoreSpecific) {
            if (c2MoreSpecific) {
                boolean last1Array = c1[c1.length - 1].isArray();
                boolean last2Array = c2[c2.length - 1].isArray();
                if (last1Array && !last2Array) {
                    return 1;
                }
                if (!last1Array && last2Array) {
                    return 0;
                }
                return 2;
            }
            return 0;
        }
        if (c2MoreSpecific) {
            return 1;
        }
        return 2;
    }

    private static boolean isApplicable(Method method, Class[] classes) {
        block9: {
            Class<?>[] methodArgs;
            block8: {
                methodArgs = method.getParameterTypes();
                if (methodArgs.length > classes.length) {
                    if (methodArgs.length == classes.length + 1 && methodArgs[methodArgs.length - 1].isArray()) {
                        for (int i = 0; i < classes.length; ++i) {
                            if (MethodMap.isConvertible(methodArgs[i], classes[i], false)) continue;
                            return false;
                        }
                        return true;
                    }
                    return false;
                }
                if (methodArgs.length != classes.length) break block8;
                for (int i = 0; i < classes.length; ++i) {
                    if (MethodMap.isConvertible(methodArgs[i], classes[i], false)) continue;
                    if (i == classes.length - 1 && methodArgs[i].isArray()) {
                        return MethodMap.isConvertible(methodArgs[i], classes[i], true);
                    }
                    return false;
                }
                break block9;
            }
            if (methodArgs.length <= 0) break block9;
            Class<?> lastarg = methodArgs[methodArgs.length - 1];
            if (!lastarg.isArray()) {
                return false;
            }
            for (int i = 0; i < methodArgs.length - 1; ++i) {
                if (MethodMap.isConvertible(methodArgs[i], classes[i], false)) continue;
                return false;
            }
            Class<?> vararg = lastarg.getComponentType();
            for (int i = methodArgs.length - 1; i < classes.length; ++i) {
                if (MethodMap.isConvertible(vararg, classes[i], false)) continue;
                return false;
            }
        }
        return true;
    }

    private static boolean isConvertible(Class formal, Class actual, boolean possibleVarArg) {
        return IntrospectionUtils.isMethodInvocationConvertible(formal, actual, possibleVarArg);
    }

    private static boolean isStrictConvertible(Class formal, Class actual, boolean possibleVarArg) {
        return IntrospectionUtils.isStrictMethodInvocationConvertible(formal, actual, possibleVarArg);
    }

    public static class AmbiguousException
    extends RuntimeException {
        private static final long serialVersionUID = -2314636505414551663L;
    }
}

