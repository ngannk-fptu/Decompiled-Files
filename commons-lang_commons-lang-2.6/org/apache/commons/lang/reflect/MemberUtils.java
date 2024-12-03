/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SystemUtils;

abstract class MemberUtils {
    private static final int ACCESS_TEST = 7;
    private static final Method IS_SYNTHETIC;
    private static final Class[] ORDERED_PRIMITIVE_TYPES;
    static /* synthetic */ Class class$java$lang$reflect$Member;

    MemberUtils() {
    }

    static void setAccessibleWorkaround(AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return;
        }
        Member m = (Member)((Object)o);
        if (Modifier.isPublic(m.getModifiers()) && MemberUtils.isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
    }

    static boolean isPackageAccess(int modifiers) {
        return (modifiers & 7) == 0;
    }

    static boolean isAccessible(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !MemberUtils.isSynthetic(m);
    }

    static boolean isSynthetic(Member m) {
        if (IS_SYNTHETIC != null) {
            try {
                return (Boolean)IS_SYNTHETIC.invoke((Object)m, null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }

    static int compareParameterTypes(Class[] left, Class[] right, Class[] actual) {
        float rightCost;
        float leftCost = MemberUtils.getTotalTransformationCost(actual, left);
        return leftCost < (rightCost = MemberUtils.getTotalTransformationCost(actual, right)) ? -1 : (rightCost < leftCost ? 1 : 0);
    }

    private static float getTotalTransformationCost(Class[] srcArgs, Class[] destArgs) {
        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; ++i) {
            Class srcClass = srcArgs[i];
            Class destClass = destArgs[i];
            totalCost += MemberUtils.getObjectTransformationCost(srcClass, destClass);
        }
        return totalCost;
    }

    private static float getObjectTransformationCost(Class srcClass, Class destClass) {
        if (destClass.isPrimitive()) {
            return MemberUtils.getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
                cost += 0.25f;
                break;
            }
            cost += 1.0f;
            srcClass = srcClass.getSuperclass();
        }
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }

    private static float getPrimitivePromotionCost(Class srcClass, Class destClass) {
        float cost = 0.0f;
        Class cls = srcClass;
        if (!cls.isPrimitive()) {
            cost += 0.1f;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; ++i) {
            if (cls != ORDERED_PRIMITIVE_TYPES[i]) continue;
            cost += 0.1f;
            if (i >= ORDERED_PRIMITIVE_TYPES.length - 1) continue;
            cls = ORDERED_PRIMITIVE_TYPES[i + 1];
        }
        return cost;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Method isSynthetic = null;
        if (SystemUtils.isJavaVersionAtLeast(1.5f)) {
            try {
                isSynthetic = (class$java$lang$reflect$Member == null ? (class$java$lang$reflect$Member = MemberUtils.class$("java.lang.reflect.Member")) : class$java$lang$reflect$Member).getMethod("isSynthetic", ArrayUtils.EMPTY_CLASS_ARRAY);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        IS_SYNTHETIC = isSynthetic;
        ORDERED_PRIMITIVE_TYPES = new Class[]{Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
    }
}

