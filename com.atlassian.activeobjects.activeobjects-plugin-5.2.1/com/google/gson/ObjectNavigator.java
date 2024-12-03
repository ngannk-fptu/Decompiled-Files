/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.NullExclusionStrategy;
import com.google.gson.ObjectTypePair;
import com.google.gson.Primitives;
import com.google.gson.ReflectingFieldNavigator;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Type;

final class ObjectNavigator {
    private final ExclusionStrategy exclusionStrategy;
    private final ReflectingFieldNavigator reflectingFieldNavigator;

    ObjectNavigator(ExclusionStrategy strategy) {
        this.exclusionStrategy = strategy == null ? new NullExclusionStrategy() : strategy;
        this.reflectingFieldNavigator = new ReflectingFieldNavigator(this.exclusionStrategy);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void accept(ObjectTypePair objTypePair, Visitor visitor) {
        if (this.exclusionStrategy.shouldSkipClass($Gson$Types.getRawType(objTypePair.type))) {
            return;
        }
        boolean visitedWithCustomHandler = visitor.visitUsingCustomHandler(objTypePair);
        if (!visitedWithCustomHandler) {
            Object objectToVisit;
            Object obj = objTypePair.getObject();
            Object object = objectToVisit = obj == null ? visitor.getTarget() : obj;
            if (objectToVisit == null) {
                return;
            }
            objTypePair.setObject(objectToVisit);
            visitor.start(objTypePair);
            try {
                if ($Gson$Types.isArray(objTypePair.type)) {
                    visitor.visitArray(objectToVisit, objTypePair.type);
                } else if (objTypePair.type == Object.class && ObjectNavigator.isPrimitiveOrString(objectToVisit)) {
                    visitor.visitPrimitive(objectToVisit);
                    visitor.getTarget();
                } else {
                    visitor.startVisitingObject(objectToVisit);
                    this.reflectingFieldNavigator.visitFieldsReflectively(objTypePair, visitor);
                }
            }
            finally {
                visitor.end(objTypePair);
            }
        }
    }

    private static boolean isPrimitiveOrString(Object objectToVisit) {
        Class<?> realClazz = objectToVisit.getClass();
        return realClazz == Object.class || realClazz == String.class || Primitives.unwrap(realClazz).isPrimitive();
    }

    public static interface Visitor {
        public void start(ObjectTypePair var1);

        public void end(ObjectTypePair var1);

        public void startVisitingObject(Object var1);

        public void visitArray(Object var1, Type var2);

        public void visitObjectField(FieldAttributes var1, Type var2, Object var3);

        public void visitArrayField(FieldAttributes var1, Type var2, Object var3);

        public boolean visitUsingCustomHandler(ObjectTypePair var1);

        public boolean visitFieldUsingCustomHandler(FieldAttributes var1, Type var2, Object var3);

        public void visitPrimitive(Object var1);

        public Object getTarget();
    }
}

