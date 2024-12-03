/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.expression.AccessException
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.TypedValue
 *  org.springframework.expression.spel.CodeFlow
 *  org.springframework.expression.spel.CompilablePropertyAccessor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.expression;

import java.util.Map;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompilablePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class MapAccessor
implements CompilablePropertyAccessor {
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{Map.class};
    }

    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return target instanceof Map && ((Map)target).containsKey(name);
    }

    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        Assert.state((boolean)(target instanceof Map), (String)"Target must be of type Map");
        Map map = (Map)target;
        Object value = map.get(name);
        if (value == null && !map.containsKey(name)) {
            throw new MapAccessException(name);
        }
        return new TypedValue(value);
    }

    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return true;
    }

    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
        Assert.state((boolean)(target instanceof Map), (String)"Target must be a Map");
        Map map = (Map)target;
        map.put(name, newValue);
    }

    public boolean isCompilable() {
        return true;
    }

    public Class<?> getPropertyType() {
        return Object.class;
    }

    public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
        String descriptor = cf.lastDescriptor();
        if (descriptor == null || !descriptor.equals("Ljava/util/Map")) {
            if (descriptor == null) {
                cf.loadTarget(mv);
            }
            CodeFlow.insertCheckCast((MethodVisitor)mv, (String)"Ljava/util/Map");
        }
        mv.visitLdcInsn((Object)propertyName);
        mv.visitMethodInsn(185, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
    }

    private static class MapAccessException
    extends AccessException {
        private final String key;

        public MapAccessException(String key) {
            super("");
            this.key = key;
        }

        public String getMessage() {
            return "Map does not contain a value for key '" + this.key + "'";
        }
    }
}

