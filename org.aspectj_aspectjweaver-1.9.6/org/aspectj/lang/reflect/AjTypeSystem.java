/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.internal.lang.reflect.AjTypeImpl;
import org.aspectj.lang.reflect.AjType;

public class AjTypeSystem {
    private static Map<Class, WeakReference<AjType>> ajTypes = Collections.synchronizedMap(new WeakHashMap());

    public static <T> AjType<T> getAjType(Class<T> fromClass) {
        WeakReference<AjType> weakRefToAjType = ajTypes.get(fromClass);
        if (weakRefToAjType != null) {
            AjTypeImpl<T> theAjType = (AjTypeImpl<T>)weakRefToAjType.get();
            if (theAjType != null) {
                return theAjType;
            }
            theAjType = new AjTypeImpl<T>(fromClass);
            ajTypes.put(fromClass, new WeakReference<AjTypeImpl<T>>(theAjType));
            return theAjType;
        }
        AjTypeImpl<T> theAjType = new AjTypeImpl<T>(fromClass);
        ajTypes.put(fromClass, new WeakReference<AjTypeImpl<T>>(theAjType));
        return theAjType;
    }
}

