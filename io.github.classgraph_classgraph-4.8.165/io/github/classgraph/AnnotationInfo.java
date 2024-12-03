/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.HasName;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ScanResultObject;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class AnnotationInfo
extends ScanResultObject
implements Comparable<AnnotationInfo>,
HasName {
    private String name;
    private AnnotationParameterValueList annotationParamValues;
    private transient boolean annotationParamValuesHasBeenConvertedToPrimitive;
    private transient AnnotationParameterValueList annotationParamValuesWithDefaults;

    AnnotationInfo() {
    }

    AnnotationInfo(String name, AnnotationParameterValueList annotationParamValues) {
        this.name = name;
        this.annotationParamValues = annotationParamValues;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean isInherited() {
        return this.getClassInfo().isInherited;
    }

    public AnnotationParameterValueList getDefaultParameterValues() {
        return this.getClassInfo().getAnnotationDefaultParameterValues();
    }

    public AnnotationParameterValueList getParameterValues(boolean includeDefaultValues) {
        ClassInfo classInfo = this.getClassInfo();
        if (classInfo == null) {
            return this.annotationParamValues == null ? AnnotationParameterValueList.EMPTY_LIST : this.annotationParamValues;
        }
        if (this.annotationParamValues != null && !this.annotationParamValuesHasBeenConvertedToPrimitive) {
            this.annotationParamValues.convertWrapperArraysToPrimitiveArrays(classInfo);
            this.annotationParamValuesHasBeenConvertedToPrimitive = true;
        }
        if (!includeDefaultValues) {
            return this.annotationParamValues == null ? AnnotationParameterValueList.EMPTY_LIST : this.annotationParamValues;
        }
        if (this.annotationParamValuesWithDefaults == null) {
            AnnotationParameterValueList defaultParamValues;
            if (classInfo.annotationDefaultParamValues != null && !classInfo.annotationDefaultParamValuesHasBeenConvertedToPrimitive) {
                classInfo.annotationDefaultParamValues.convertWrapperArraysToPrimitiveArrays(classInfo);
                classInfo.annotationDefaultParamValuesHasBeenConvertedToPrimitive = true;
            }
            if ((defaultParamValues = classInfo.annotationDefaultParamValues) == null && this.annotationParamValues == null) {
                return AnnotationParameterValueList.EMPTY_LIST;
            }
            if (defaultParamValues == null) {
                return this.annotationParamValues;
            }
            if (this.annotationParamValues == null) {
                return defaultParamValues;
            }
            HashMap<String, Object> allParamValues = new HashMap<String, Object>();
            for (AnnotationParameterValue defaultParamValue : defaultParamValues) {
                allParamValues.put(defaultParamValue.getName(), defaultParamValue.getValue());
            }
            for (AnnotationParameterValue annotationParamValue : this.annotationParamValues) {
                allParamValues.put(annotationParamValue.getName(), annotationParamValue.getValue());
            }
            if (classInfo.methodInfo == null) {
                throw new IllegalArgumentException("Could not find methods for annotation " + classInfo.getName());
            }
            this.annotationParamValuesWithDefaults = new AnnotationParameterValueList();
            block13: for (MethodInfo mi : classInfo.methodInfo) {
                String paramName;
                switch (paramName = mi.getName()) {
                    case "<init>": 
                    case "<clinit>": 
                    case "hashCode": 
                    case "equals": 
                    case "toString": 
                    case "annotationType": {
                        continue block13;
                    }
                }
                Object paramValue = allParamValues.get(paramName);
                if (paramValue == null) continue;
                this.annotationParamValuesWithDefaults.add(new AnnotationParameterValue(paramName, paramValue));
            }
        }
        return this.annotationParamValuesWithDefaults;
    }

    public AnnotationParameterValueList getParameterValues() {
        return this.getParameterValues(true);
    }

    @Override
    protected String getClassName() {
        return this.name;
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.annotationParamValues != null) {
            for (AnnotationParameterValue a : this.annotationParamValues) {
                a.setScanResult(scanResult);
            }
        }
    }

    @Override
    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        super.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
        if (this.annotationParamValues != null) {
            for (AnnotationParameterValue annotationParamValue : this.annotationParamValues) {
                annotationParamValue.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            }
        }
    }

    @Override
    public ClassInfo getClassInfo() {
        return super.getClassInfo();
    }

    public Annotation loadClassAndInstantiate() {
        Class<Annotation> annotationClass = this.getClassInfo().loadClass(Annotation.class);
        return (Annotation)Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass}, (InvocationHandler)new AnnotationInvocationHandler(annotationClass, this));
    }

    void convertWrapperArraysToPrimitiveArrays() {
        if (this.annotationParamValues != null) {
            this.annotationParamValues.convertWrapperArraysToPrimitiveArrays(this.getClassInfo());
        }
    }

    @Override
    public int compareTo(AnnotationInfo o) {
        int diff = this.name.compareTo(o.name);
        if (diff != 0) {
            return diff;
        }
        if (this.annotationParamValues == null && o.annotationParamValues == null) {
            return 0;
        }
        if (this.annotationParamValues == null) {
            return -1;
        }
        if (o.annotationParamValues == null) {
            return 1;
        }
        int max = Math.max(this.annotationParamValues.size(), o.annotationParamValues.size());
        for (int i = 0; i < max; ++i) {
            if (i >= this.annotationParamValues.size()) {
                return -1;
            }
            if (i >= o.annotationParamValues.size()) {
                return 1;
            }
            int diff2 = ((AnnotationParameterValue)this.annotationParamValues.get(i)).compareTo((AnnotationParameterValue)o.annotationParamValues.get(i));
            if (diff2 == 0) continue;
            return diff2;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AnnotationInfo)) {
            return false;
        }
        AnnotationInfo other = (AnnotationInfo)obj;
        return this.compareTo(other) == 0;
    }

    public int hashCode() {
        int h = this.name.hashCode();
        if (this.annotationParamValues != null) {
            for (AnnotationParameterValue e : this.annotationParamValues) {
                h = h * 7 + e.getName().hashCode() * 3 + e.getValue().hashCode();
            }
        }
        return h;
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        buf.append('@').append(useSimpleNames ? ClassInfo.getSimpleName(this.name) : this.name);
        AnnotationParameterValueList paramVals = this.getParameterValues();
        if (!paramVals.isEmpty()) {
            buf.append('(');
            for (int i = 0; i < paramVals.size(); ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                AnnotationParameterValue paramVal = (AnnotationParameterValue)paramVals.get(i);
                if (paramVals.size() > 1 || !"value".equals(paramVal.getName())) {
                    paramVal.toString(useSimpleNames, buf);
                    continue;
                }
                paramVal.toStringParamValueOnly(useSimpleNames, buf);
            }
            buf.append(')');
        }
    }

    private static class AnnotationInvocationHandler
    implements InvocationHandler {
        private final Class<? extends Annotation> annotationClass;
        private final AnnotationInfo annotationInfo;
        private final Map<String, Object> annotationParameterValuesInstantiated = new HashMap<String, Object>();

        AnnotationInvocationHandler(Class<? extends Annotation> annotationClass, AnnotationInfo annotationInfo) {
            this.annotationClass = annotationClass;
            this.annotationInfo = annotationInfo;
            for (AnnotationParameterValue apv : annotationInfo.getParameterValues()) {
                Object instantiatedValue = apv.instantiate(annotationInfo.getClassInfo());
                if (instantiatedValue == null) {
                    throw new IllegalArgumentException("Got null value for annotation parameter " + apv.getName() + " of annotation " + annotationInfo.name);
                }
                this.annotationParameterValuesInstantiated.put(apv.getName(), instantiatedValue);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Class<?>[] paramTypes;
            String methodName = method.getName();
            if ((args == null ? 0 : args.length) != (paramTypes = method.getParameterTypes()).length) {
                throw new IllegalArgumentException("Wrong number of arguments for " + this.annotationClass.getName() + "." + methodName + ": got " + (args == null ? 0 : args.length) + ", expected " + paramTypes.length);
            }
            if (args != null && paramTypes.length == 1) {
                if ("equals".equals(methodName) && paramTypes[0] == Object.class) {
                    if (this == args[0]) {
                        return true;
                    }
                    if (!this.annotationClass.isInstance(args[0])) {
                        return false;
                    }
                    ReflectionUtils reflectionUtils = this.annotationInfo.scanResult == null ? new ReflectionUtils() : this.annotationInfo.scanResult.reflectionUtils;
                    for (Map.Entry<String, Object> ent : this.annotationParameterValuesInstantiated.entrySet()) {
                        Object otherParamVal;
                        String paramName = ent.getKey();
                        Object paramVal = ent.getValue();
                        if (paramVal == null != ((otherParamVal = reflectionUtils.invokeMethod(false, args[0], paramName)) == null)) {
                            return false;
                        }
                        if (paramVal == null && otherParamVal == null) {
                            return true;
                        }
                        if (paramVal != null && paramVal.equals(otherParamVal)) continue;
                        return false;
                    }
                    return true;
                }
                throw new IllegalArgumentException();
            }
            if (paramTypes.length == 0) {
                switch (methodName) {
                    case "toString": {
                        return this.annotationInfo.toString();
                    }
                    case "hashCode": {
                        int result = 0;
                        for (Map.Entry<String, Object> ent : this.annotationParameterValuesInstantiated.entrySet()) {
                            Class<?> type;
                            String paramName = ent.getKey();
                            Object paramVal = ent.getValue();
                            int paramValHashCode = paramVal == null ? 0 : (!(type = paramVal.getClass()).isArray() ? paramVal.hashCode() : (type == byte[].class ? Arrays.hashCode((byte[])paramVal) : (type == char[].class ? Arrays.hashCode((char[])paramVal) : (type == double[].class ? Arrays.hashCode((double[])paramVal) : (type == float[].class ? Arrays.hashCode((float[])paramVal) : (type == int[].class ? Arrays.hashCode((int[])paramVal) : (type == long[].class ? Arrays.hashCode((long[])paramVal) : (type == short[].class ? Arrays.hashCode((short[])paramVal) : (type == boolean[].class ? Arrays.hashCode((boolean[])paramVal) : Arrays.hashCode((Object[])paramVal))))))))));
                            result += 127 * paramName.hashCode() ^ paramValHashCode;
                        }
                        return result;
                    }
                    case "annotationType": {
                        return this.annotationClass;
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
            Object annotationParameterValue = this.annotationParameterValuesInstantiated.get(methodName);
            if (annotationParameterValue == null) {
                throw new IncompleteAnnotationException(this.annotationClass, methodName);
            }
            Class<?> annotationParameterValueClass = annotationParameterValue.getClass();
            if (annotationParameterValueClass.isArray()) {
                if (annotationParameterValueClass == String[].class) {
                    return ((String[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == byte[].class) {
                    return ((byte[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == char[].class) {
                    return ((char[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == double[].class) {
                    return ((double[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == float[].class) {
                    return ((float[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == int[].class) {
                    return ((int[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == long[].class) {
                    return ((long[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == short[].class) {
                    return ((short[])annotationParameterValue).clone();
                }
                if (annotationParameterValueClass == boolean[].class) {
                    return ((boolean[])annotationParameterValue).clone();
                }
                Object[] arr = (Object[])annotationParameterValue;
                return arr.clone();
            }
            return annotationParameterValue;
        }
    }
}

