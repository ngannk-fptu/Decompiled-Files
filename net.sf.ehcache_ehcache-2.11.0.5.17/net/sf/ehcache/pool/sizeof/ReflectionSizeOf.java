/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.pool.sizeof;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Stack;
import net.sf.ehcache.pool.sizeof.JvmInformation;
import net.sf.ehcache.pool.sizeof.PrimitiveType;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionSizeOf
extends SizeOf {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionSizeOf.class);

    public ReflectionSizeOf() {
        this(new PassThroughFilter());
    }

    public ReflectionSizeOf(SizeOfFilter fieldFilter) {
        this(fieldFilter, true);
    }

    public ReflectionSizeOf(SizeOfFilter fieldFilter, boolean caching) {
        super(fieldFilter, caching);
        if (!JvmInformation.CURRENT_JVM_INFORMATION.supportsReflectionSizeOf()) {
            LOGGER.warn("ReflectionSizeOf is not always accurate on the JVM (" + JvmInformation.CURRENT_JVM_INFORMATION.getJvmDescription() + ").  Please consider enabling AgentSizeOf.");
        }
    }

    @Override
    public long sizeOf(Object obj) {
        Class klazz;
        if (obj == null) {
            return 0L;
        }
        Class aClass = obj.getClass();
        if (aClass.isArray()) {
            return this.guessArraySize(obj);
        }
        long size = JvmInformation.CURRENT_JVM_INFORMATION.getObjectHeaderSize();
        Stack<Class> classStack = new Stack<Class>();
        for (klazz = aClass; klazz != null; klazz = klazz.getSuperclass()) {
            classStack.push(klazz);
        }
        while (!classStack.isEmpty()) {
            klazz = (Class)classStack.pop();
            int oops = 0;
            int doubles = 0;
            int words = 0;
            int shorts = 0;
            int bytes = 0;
            block8: for (Field f : klazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                if (f.getType().isPrimitive()) {
                    switch (PrimitiveType.forType(f.getType())) {
                        case BOOLEAN: 
                        case BYTE: {
                            ++bytes;
                            continue block8;
                        }
                        case SHORT: 
                        case CHAR: {
                            ++shorts;
                            continue block8;
                        }
                        case INT: 
                        case FLOAT: {
                            ++words;
                            continue block8;
                        }
                        case DOUBLE: 
                        case LONG: {
                            ++doubles;
                            continue block8;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                }
                ++oops;
            }
            if (doubles > 0 && size % (long)PrimitiveType.LONG.getSize() != 0L) {
                long length = (long)PrimitiveType.LONG.getSize() - size % (long)PrimitiveType.LONG.getSize();
                size += (long)PrimitiveType.LONG.getSize() - size % (long)PrimitiveType.LONG.getSize();
                while (length >= (long)PrimitiveType.INT.getSize() && words > 0) {
                    length -= (long)PrimitiveType.INT.getSize();
                    --words;
                }
                while (length >= (long)PrimitiveType.SHORT.getSize() && shorts > 0) {
                    length -= (long)PrimitiveType.SHORT.getSize();
                    --shorts;
                }
                while (length >= (long)PrimitiveType.BYTE.getSize() && bytes > 0) {
                    length -= (long)PrimitiveType.BYTE.getSize();
                    --bytes;
                }
                while (length >= (long)PrimitiveType.getReferenceSize() && oops > 0) {
                    length -= (long)PrimitiveType.getReferenceSize();
                    --oops;
                }
            }
            size += (long)(PrimitiveType.DOUBLE.getSize() * doubles);
            size += (long)(PrimitiveType.INT.getSize() * words);
            size += (long)(PrimitiveType.SHORT.getSize() * shorts);
            size += (long)(PrimitiveType.BYTE.getSize() * bytes);
            if (oops > 0) {
                if (size % (long)PrimitiveType.getReferenceSize() != 0L) {
                    size += (long)PrimitiveType.getReferenceSize() - size % (long)PrimitiveType.getReferenceSize();
                }
                size += (long)(oops * PrimitiveType.getReferenceSize());
            }
            if (doubles + words + shorts + bytes + oops <= 0 || size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getPointerSize() == 0L) continue;
            size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getPointerSize() - size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getPointerSize();
        }
        if (size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() != 0L) {
            size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() - size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment();
        }
        return Math.max(size, (long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize());
    }

    private long guessArraySize(Object obj) {
        long size = PrimitiveType.getArraySize();
        int length = Array.getLength(obj);
        if (length != 0) {
            Class<?> arrayElementClazz = obj.getClass().getComponentType();
            size = arrayElementClazz.isPrimitive() ? (size += (long)(length * PrimitiveType.forType(arrayElementClazz).getSize())) : (size += (long)(length * PrimitiveType.getReferenceSize()));
        }
        if (size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() != 0L) {
            size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() - size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment();
        }
        return Math.max(size, (long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize());
    }
}

