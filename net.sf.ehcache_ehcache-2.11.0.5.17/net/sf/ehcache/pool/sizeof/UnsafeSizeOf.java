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
import net.sf.ehcache.pool.sizeof.JvmInformation;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

public class UnsafeSizeOf
extends SizeOf {
    private static final Logger LOGGER;
    private static final Unsafe UNSAFE;

    public UnsafeSizeOf() throws UnsupportedOperationException {
        this(new PassThroughFilter());
    }

    public UnsafeSizeOf(SizeOfFilter filter) throws UnsupportedOperationException {
        this(filter, true);
    }

    public UnsafeSizeOf(SizeOfFilter filter, boolean caching) throws UnsupportedOperationException {
        super(filter, caching);
        if (UNSAFE == null) {
            throw new UnsupportedOperationException("sun.misc.Unsafe instance not accessible");
        }
        if (!JvmInformation.CURRENT_JVM_INFORMATION.supportsUnsafeSizeOf()) {
            LOGGER.warn("UnsafeSizeOf is not always accurate on the JVM (" + JvmInformation.CURRENT_JVM_INFORMATION.getJvmDescription() + ").  Please consider enabling AgentSizeOf.");
        }
    }

    @Override
    public long sizeOf(Object obj) {
        if (obj.getClass().isArray()) {
            Class<?> klazz = obj.getClass();
            int base = UNSAFE.arrayBaseOffset(klazz);
            int scale = UNSAFE.arrayIndexScale(klazz);
            long size = base + scale * Array.getLength(obj);
            if ((size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getFieldOffsetAdjustment()) % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() != 0L) {
                size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() - size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment();
            }
            return Math.max((long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize(), size);
        }
        for (Class<?> klazz = obj.getClass(); klazz != null; klazz = klazz.getSuperclass()) {
            long lastFieldOffset = -1L;
            for (Field f : klazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                lastFieldOffset = Math.max(lastFieldOffset, UNSAFE.objectFieldOffset(f));
            }
            if (lastFieldOffset <= 0L) continue;
            lastFieldOffset += (long)JvmInformation.CURRENT_JVM_INFORMATION.getFieldOffsetAdjustment();
            if (++lastFieldOffset % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() != 0L) {
                lastFieldOffset += (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() - lastFieldOffset % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment();
            }
            return Math.max((long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize(), lastFieldOffset);
        }
        long size = JvmInformation.CURRENT_JVM_INFORMATION.getObjectHeaderSize();
        if (size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() != 0L) {
            size += (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment() - size % (long)JvmInformation.CURRENT_JVM_INFORMATION.getObjectAlignment();
        }
        return Math.max((long)JvmInformation.CURRENT_JVM_INFORMATION.getMinimumObjectSize(), size);
    }

    static {
        Unsafe unsafe;
        LOGGER = LoggerFactory.getLogger(UnsafeSizeOf.class);
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe)unsafeField.get(null);
        }
        catch (Throwable t) {
            unsafe = null;
        }
        UNSAFE = unsafe;
    }
}

