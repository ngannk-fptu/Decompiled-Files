/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class AnnotationMirrorImpl
implements AnnotationMirror,
InvocationHandler {
    public final BaseProcessingEnvImpl _env;
    public final AnnotationBinding _binding;

    AnnotationMirrorImpl(BaseProcessingEnvImpl env, AnnotationBinding binding) {
        this._env = env;
        this._binding = binding;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AnnotationMirrorImpl) {
            if (this._binding == null) {
                return ((AnnotationMirrorImpl)obj)._binding == null;
            }
            return AnnotationMirrorImpl.equals(this._binding, ((AnnotationMirrorImpl)obj)._binding);
        }
        return obj == null ? false : obj.equals(this);
    }

    private static boolean equals(AnnotationBinding annotationBinding, AnnotationBinding annotationBinding2) {
        ElementValuePair[] elementValuePairs2;
        if (annotationBinding.getAnnotationType() != annotationBinding2.getAnnotationType()) {
            return false;
        }
        ElementValuePair[] elementValuePairs = annotationBinding.getElementValuePairs();
        int length = elementValuePairs.length;
        if (length != (elementValuePairs2 = annotationBinding2.getElementValuePairs()).length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            block10: {
                ElementValuePair pair = elementValuePairs[i];
                int j = 0;
                while (j < length) {
                    ElementValuePair pair2 = elementValuePairs2[j];
                    if (pair.binding == pair2.binding) {
                        if (pair.value == null) {
                            if (pair2.value != null) {
                                return false;
                            }
                        } else {
                            if (pair2.value == null) {
                                return false;
                            }
                            if (pair2.value instanceof Object[] && pair.value instanceof Object[] ? !Arrays.equals((Object[])pair.value, (Object[])pair2.value) : !pair2.value.equals(pair.value)) {
                                return false;
                            }
                        }
                        break block10;
                    }
                    ++j;
                }
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public DeclaredType getAnnotationType() {
        return (DeclaredType)this._env.getFactory().newTypeMirror(this._binding.getAnnotationType());
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        if (this._binding == null) {
            return Collections.emptyMap();
        }
        ElementValuePair[] pairs = this._binding.getElementValuePairs();
        LinkedHashMap<ExecutableElementImpl, AnnotationMemberValue> valueMap = new LinkedHashMap<ExecutableElementImpl, AnnotationMemberValue>(pairs.length);
        ElementValuePair[] elementValuePairArray = pairs;
        int n = pairs.length;
        int n2 = 0;
        while (n2 < n) {
            ElementValuePair pair = elementValuePairArray[n2];
            MethodBinding method = pair.getMethodBinding();
            if (method != null) {
                ExecutableElementImpl e = new ExecutableElementImpl(this._env, method);
                AnnotationMemberValue v = new AnnotationMemberValue(this._env, pair.getValue(), method);
                valueMap.put(e, v);
            }
            ++n2;
        }
        return Collections.unmodifiableMap(valueMap);
    }

    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults() {
        if (this._binding == null) {
            return Collections.emptyMap();
        }
        ElementValuePair[] pairs = this._binding.getElementValuePairs();
        ReferenceBinding annoType = this._binding.getAnnotationType();
        LinkedHashMap<ExecutableElementImpl, AnnotationMemberValue> valueMap = new LinkedHashMap<ExecutableElementImpl, AnnotationMemberValue>();
        MethodBinding[] methodBindingArray = annoType.methods();
        int n = methodBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            Object defaultVal;
            MethodBinding method = methodBindingArray[n2];
            boolean foundExplicitValue = false;
            int i = 0;
            while (i < pairs.length) {
                MethodBinding explicitBinding = pairs[i].getMethodBinding();
                if (method == explicitBinding) {
                    ExecutableElementImpl e = new ExecutableElementImpl(this._env, explicitBinding);
                    AnnotationMemberValue v = new AnnotationMemberValue(this._env, pairs[i].getValue(), explicitBinding);
                    valueMap.put(e, v);
                    foundExplicitValue = true;
                    break;
                }
                ++i;
            }
            if (!foundExplicitValue && (defaultVal = method.getDefaultValue()) != null) {
                ExecutableElementImpl e = new ExecutableElementImpl(this._env, method);
                AnnotationMemberValue v = new AnnotationMemberValue(this._env, defaultVal, method);
                valueMap.put(e, v);
            }
            ++n2;
        }
        return Collections.unmodifiableMap(valueMap);
    }

    public int hashCode() {
        if (this._binding == null) {
            return this._env.hashCode();
        }
        return this._binding.hashCode();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ElementValuePair[] pairs;
        if (this._binding == null) {
            return null;
        }
        String methodName = method.getName();
        if (args == null || args.length == 0) {
            if (methodName.equals("hashCode")) {
                return this.hashCode();
            }
            if (methodName.equals("toString")) {
                return this.toString();
            }
            if (methodName.equals("annotationType")) {
                return proxy.getClass().getInterfaces()[0];
            }
        } else if (args.length == 1 && methodName.equals("equals")) {
            return this.equals(args[0]);
        }
        if (args != null && args.length != 0) {
            throw new NoSuchMethodException("method " + method.getName() + this.formatArgs(args) + " does not exist on annotation " + this.toString());
        }
        MethodBinding methodBinding = this.getMethodBinding(methodName);
        if (methodBinding == null) {
            throw new NoSuchMethodException("method " + method.getName() + "() does not exist on annotation" + this.toString());
        }
        Object actualValue = null;
        boolean foundMethod = false;
        ElementValuePair[] elementValuePairArray = pairs = this._binding.getElementValuePairs();
        int n = pairs.length;
        int n2 = 0;
        while (n2 < n) {
            ElementValuePair pair = elementValuePairArray[n2];
            if (methodName.equals(new String(pair.getName()))) {
                actualValue = pair.getValue();
                foundMethod = true;
                break;
            }
            ++n2;
        }
        if (!foundMethod) {
            actualValue = methodBinding.getDefaultValue();
        }
        Class<?> expectedType = method.getReturnType();
        TypeBinding actualType = methodBinding.returnType;
        return this.getReflectionValue(actualValue, actualType, expectedType);
    }

    public String toString() {
        DeclaredType decl = this.getAnnotationType();
        StringBuilder sb = new StringBuilder();
        sb.append('@');
        sb.append(decl.toString());
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = this.getElementValues();
        if (!values.isEmpty()) {
            sb.append('(');
            boolean first = true;
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(e.getKey().getSimpleName());
                sb.append(" = ");
                sb.append(((Object)e.getValue()).toString());
            }
            sb.append(')');
        }
        return sb.toString();
    }

    private String formatArgs(Object[] args) {
        StringBuilder builder = new StringBuilder(args.length * 8 + 2);
        builder.append('(');
        int i = 0;
        while (i < args.length) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(args[i].getClass().getName());
            ++i;
        }
        builder.append(')');
        return builder.toString();
    }

    private MethodBinding getMethodBinding(String name) {
        MethodBinding[] methods;
        ReferenceBinding annoType = this._binding.getAnnotationType();
        MethodBinding[] methodBindingArray = methods = annoType.getMethods(name.toCharArray());
        int n = methods.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding method = methodBindingArray[n2];
            if (method.parameters.length == 0) {
                return method;
            }
            ++n2;
        }
        return null;
    }

    private Object getReflectionValue(Object actualValue, TypeBinding actualType, Class<?> expectedType) {
        if (expectedType == null) {
            return null;
        }
        if (actualValue == null) {
            return Factory.getMatchingDummyValue(expectedType);
        }
        if (expectedType.isArray()) {
            if (Class.class.equals(expectedType.getComponentType())) {
                Object bindings;
                if (actualType.isArrayType() && ((ArrayBinding)actualType).leafComponentType.erasure().id == 16 && (bindings = actualValue instanceof Object[] ? (Object[])actualValue : (actualValue instanceof TypeBinding ? new Object[]{actualValue} : null)) != null) {
                    ArrayList<TypeMirror> mirrors = new ArrayList<TypeMirror>(((Object[])bindings).length);
                    int i = 0;
                    while (i < ((Object[])bindings).length) {
                        if (bindings[i] instanceof TypeBinding) {
                            mirrors.add(this._env.getFactory().newTypeMirror((TypeBinding)bindings[i]));
                        }
                        ++i;
                    }
                    throw new MirroredTypesException(mirrors);
                }
                return null;
            }
            return this.convertJDTArrayToReflectionArray(actualValue, actualType, expectedType);
        }
        if (Class.class.equals(expectedType)) {
            if (actualValue instanceof TypeBinding) {
                TypeMirror mirror = this._env.getFactory().newTypeMirror((TypeBinding)actualValue);
                throw new MirroredTypeException(mirror);
            }
            return null;
        }
        return this.convertJDTValueToReflectionType(actualValue, actualType, expectedType);
    }

    private Object convertJDTArrayToReflectionArray(Object jdtValue, TypeBinding jdtType, Class<?> expectedType) {
        Object[] jdtArray;
        assert (expectedType != null && expectedType.isArray());
        if (!jdtType.isArrayType()) {
            return null;
        }
        if (jdtValue != null && !(jdtValue instanceof Object[])) {
            jdtArray = (Object[])Array.newInstance(jdtValue.getClass(), 1);
            jdtArray[0] = jdtValue;
        } else {
            jdtArray = (Object[])jdtValue;
        }
        TypeBinding jdtLeafType = jdtType.leafComponentType();
        Class<?> expectedLeafType = expectedType.getComponentType();
        int length = jdtArray.length;
        Object returnArray = Array.newInstance(expectedLeafType, length);
        int i = 0;
        while (i < length) {
            Object returnVal;
            Object jdtElementValue = jdtArray[i];
            if (expectedLeafType.isPrimitive() || String.class.equals(expectedLeafType)) {
                if (jdtElementValue instanceof Constant) {
                    if (Boolean.TYPE.equals(expectedLeafType)) {
                        Array.setBoolean(returnArray, i, ((Constant)jdtElementValue).booleanValue());
                    } else if (Byte.TYPE.equals(expectedLeafType)) {
                        Array.setByte(returnArray, i, ((Constant)jdtElementValue).byteValue());
                    } else if (Character.TYPE.equals(expectedLeafType)) {
                        Array.setChar(returnArray, i, ((Constant)jdtElementValue).charValue());
                    } else if (Double.TYPE.equals(expectedLeafType)) {
                        Array.setDouble(returnArray, i, ((Constant)jdtElementValue).doubleValue());
                    } else if (Float.TYPE.equals(expectedLeafType)) {
                        Array.setFloat(returnArray, i, ((Constant)jdtElementValue).floatValue());
                    } else if (Integer.TYPE.equals(expectedLeafType)) {
                        Array.setInt(returnArray, i, ((Constant)jdtElementValue).intValue());
                    } else if (Long.TYPE.equals(expectedLeafType)) {
                        Array.setLong(returnArray, i, ((Constant)jdtElementValue).longValue());
                    } else if (Short.TYPE.equals(expectedLeafType)) {
                        Array.setShort(returnArray, i, ((Constant)jdtElementValue).shortValue());
                    } else if (String.class.equals(expectedLeafType)) {
                        Array.set(returnArray, i, ((Constant)jdtElementValue).stringValue());
                    }
                } else {
                    Factory.setArrayMatchingDummyValue(returnArray, i, expectedLeafType);
                }
            } else if (expectedLeafType.isEnum()) {
                returnVal = null;
                if (jdtLeafType != null && jdtLeafType.isEnum() && jdtElementValue instanceof FieldBinding) {
                    FieldBinding binding = (FieldBinding)jdtElementValue;
                    try {
                        Field returnedField = null;
                        returnedField = expectedLeafType.getField(new String(binding.name));
                        if (returnedField != null) {
                            returnVal = returnedField.get(null);
                        }
                    }
                    catch (NoSuchFieldException noSuchFieldException) {
                    }
                    catch (IllegalAccessException illegalAccessException) {}
                }
                Array.set(returnArray, i, returnVal);
            } else if (expectedLeafType.isAnnotation()) {
                returnVal = null;
                if (jdtLeafType.isAnnotationType() && jdtElementValue instanceof AnnotationBinding) {
                    AnnotationMirrorImpl annoMirror = (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtElementValue);
                    returnVal = Proxy.newProxyInstance(expectedLeafType.getClassLoader(), new Class[]{expectedLeafType}, (InvocationHandler)annoMirror);
                }
                Array.set(returnArray, i, returnVal);
            } else {
                Array.set(returnArray, i, null);
            }
            ++i;
        }
        return returnArray;
    }

    private Object convertJDTValueToReflectionType(Object jdtValue, TypeBinding actualType, Class<?> expectedType) {
        if (expectedType.isPrimitive() || String.class.equals(expectedType)) {
            if (jdtValue instanceof Constant) {
                if (Boolean.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).booleanValue();
                }
                if (Byte.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).byteValue();
                }
                if (Character.TYPE.equals(expectedType)) {
                    return Character.valueOf(((Constant)jdtValue).charValue());
                }
                if (Double.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).doubleValue();
                }
                if (Float.TYPE.equals(expectedType)) {
                    return Float.valueOf(((Constant)jdtValue).floatValue());
                }
                if (Integer.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).intValue();
                }
                if (Long.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).longValue();
                }
                if (Short.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).shortValue();
                }
                if (String.class.equals(expectedType)) {
                    return ((Constant)jdtValue).stringValue();
                }
            }
            return Factory.getMatchingDummyValue(expectedType);
        }
        if (expectedType.isEnum()) {
            Object returnVal = null;
            if (actualType != null && actualType.isEnum() && jdtValue instanceof FieldBinding) {
                FieldBinding binding = (FieldBinding)jdtValue;
                try {
                    Field returnedField = null;
                    returnedField = expectedType.getField(new String(binding.name));
                    if (returnedField != null) {
                        returnVal = returnedField.get(null);
                    }
                }
                catch (NoSuchFieldException noSuchFieldException) {
                }
                catch (IllegalAccessException illegalAccessException) {}
            }
            return returnVal == null ? Factory.getMatchingDummyValue(expectedType) : returnVal;
        }
        if (expectedType.isAnnotation()) {
            if (actualType.isAnnotationType() && jdtValue instanceof AnnotationBinding) {
                AnnotationMirrorImpl annoMirror = (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtValue);
                return Proxy.newProxyInstance(expectedType.getClassLoader(), new Class[]{expectedType}, (InvocationHandler)annoMirror);
            }
            return null;
        }
        return Factory.getMatchingDummyValue(expectedType);
    }
}

