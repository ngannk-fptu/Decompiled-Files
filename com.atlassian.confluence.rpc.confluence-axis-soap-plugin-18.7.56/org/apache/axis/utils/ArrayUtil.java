/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;

public class ArrayUtil {
    public static final NonConvertable NON_CONVERTABLE = new NonConvertable();

    public static Object convertObjectToArray(Object obj, Class arrayType) {
        try {
            ArrayInfo arri = new ArrayInfo();
            boolean rc = ArrayUtil.internalIsConvertable(obj.getClass(), arri, arrayType);
            if (!rc) {
                return obj;
            }
            BeanPropertyDescriptor pd = null;
            pd = ArrayUtil.getArrayComponentPD(obj.getClass());
            if (pd == null) {
                return NON_CONVERTABLE;
            }
            Object comp = pd.get(obj);
            if (comp == null) {
                return null;
            }
            int arraylen = 0;
            if (!comp.getClass().isArray()) {
                return comp;
            }
            arraylen = Array.getLength(comp);
            int[] dims = new int[arri.dimension];
            dims[0] = arraylen;
            Object targetArray = Array.newInstance(arri.componentType, dims);
            for (int i = 0; i < arraylen; ++i) {
                Object subarray = Array.get(comp, i);
                Class<?> subarrayClass = arrayType.getComponentType();
                Array.set(targetArray, i, ArrayUtil.convertObjectToArray(subarray, subarrayClass));
            }
            return targetArray;
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isConvertable(Class clazz, Class arrayType) {
        ArrayInfo arrInfo = new ArrayInfo();
        return ArrayUtil.internalIsConvertable(clazz, arrInfo, arrayType);
    }

    private static boolean internalIsConvertable(Class clazz, ArrayInfo arri, Class arrayType) {
        BeanPropertyDescriptor pd = null;
        BeanPropertyDescriptor oldPd = null;
        if (!arrayType.isArray()) {
            return false;
        }
        Class<?> destArrCompType = arrayType.getComponentType();
        Class src = clazz;
        int depth = 0;
        while ((pd = ArrayUtil.getArrayComponentPD(src)) != null) {
            ++depth;
            src = pd.getType();
            oldPd = pd;
            if (!destArrCompType.isAssignableFrom(src)) continue;
        }
        if (depth == 0 || oldPd.getType() == null) {
            return false;
        }
        arri.componentType = oldPd.getType();
        arri.dimension = depth;
        Class componentType = oldPd.getType();
        int[] dims = new int[depth];
        Object array = Array.newInstance(componentType, dims);
        arri.arrayType = array.getClass();
        return arrayType.isAssignableFrom(arri.arrayType);
    }

    private static BeanPropertyDescriptor getArrayComponentPD(Class clazz) {
        BeanPropertyDescriptor bpd = null;
        int count = 0;
        Class cls = clazz;
        while (!cls.getName().equals("java.lang.Object")) {
            BeanPropertyDescriptor[] bpds = BeanUtils.getPd(clazz);
            for (int i = 0; i < bpds.length; ++i) {
                BeanPropertyDescriptor pd = bpds[i];
                if (!pd.isReadable() || !pd.isWriteable() || !pd.isIndexed()) continue;
                if (++count >= 2) {
                    return null;
                }
                bpd = pd;
            }
            cls = cls.getSuperclass();
        }
        if (count == 1) {
            return bpd;
        }
        return null;
    }

    public static int getArrayDimension(Class arrayType) {
        if (!arrayType.isArray()) {
            return 0;
        }
        int dim = 0;
        Class<?> compType = arrayType;
        do {
            ++dim;
        } while ((compType = (arrayType = compType).getComponentType()).isArray());
        return dim;
    }

    private static Object createNewInstance(Class cls) throws InstantiationException, IllegalAccessException {
        Comparable<Boolean> obj = null;
        if (!cls.isPrimitive()) {
            obj = (Comparable<Boolean>)cls.newInstance();
        } else if (Boolean.TYPE.isAssignableFrom(cls)) {
            obj = new Boolean(false);
        } else if (Byte.TYPE.isAssignableFrom(cls)) {
            obj = new Byte(0);
        } else if (Character.TYPE.isAssignableFrom(cls)) {
            obj = new Character('\u0000');
        } else if (Short.TYPE.isAssignableFrom(cls)) {
            obj = new Short(0);
        } else if (Integer.TYPE.isAssignableFrom(cls)) {
            obj = new Integer(0);
        } else if (Long.TYPE.isAssignableFrom(cls)) {
            obj = new Long(0L);
        } else if (Float.TYPE.isAssignableFrom(cls)) {
            obj = new Float(0.0f);
        } else if (Double.TYPE.isAssignableFrom(cls)) {
            obj = new Double(0.0);
        }
        return obj;
    }

    public static Object convertArrayToObject(Object array, Class destClass) {
        int dim = ArrayUtil.getArrayDimension(array.getClass());
        if (dim == 0) {
            return null;
        }
        Object dest = null;
        try {
            int i;
            int arraylen = Array.getLength(array);
            Object destArray = null;
            Class destComp = null;
            if (!destClass.isArray()) {
                dest = destClass.newInstance();
                BeanPropertyDescriptor pd = ArrayUtil.getArrayComponentPD(destClass);
                if (pd == null) {
                    return null;
                }
                destComp = pd.getType();
                destArray = Array.newInstance(destComp, arraylen);
                pd.set(dest, destArray);
            } else {
                destComp = destClass.getComponentType();
                destArray = dest = Array.newInstance(destComp, arraylen);
            }
            for (i = 0; i < arraylen; ++i) {
                Array.set(destArray, i, ArrayUtil.createNewInstance(destComp));
            }
            for (i = 0; i < arraylen; ++i) {
                Object comp = Array.get(array, i);
                if (comp == null) continue;
                if (comp.getClass().isArray()) {
                    Class<?> cls = Array.get(destArray, i).getClass();
                    Array.set(destArray, i, ArrayUtil.convertArrayToObject(comp, cls));
                    continue;
                }
                Array.set(destArray, i, comp);
            }
        }
        catch (IllegalAccessException ignore) {
            return null;
        }
        catch (InvocationTargetException ignore) {
            return null;
        }
        catch (InstantiationException ignore) {
            return null;
        }
        return dest;
    }

    public static class NonConvertable {
    }

    private static class ArrayInfo {
        public Class componentType;
        public Class arrayType;
        public int dimension;

        private ArrayInfo() {
        }
    }
}

