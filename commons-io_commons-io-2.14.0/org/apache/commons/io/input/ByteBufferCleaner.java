/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

class ByteBufferCleaner {
    private static final Cleaner INSTANCE = ByteBufferCleaner.getCleaner();

    ByteBufferCleaner() {
    }

    static void clean(ByteBuffer buffer) {
        try {
            INSTANCE.clean(buffer);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to clean direct buffer.", e);
        }
    }

    private static Cleaner getCleaner() {
        try {
            return new Java8Cleaner();
        }
        catch (Exception e) {
            try {
                return new Java9Cleaner();
            }
            catch (Exception e1) {
                throw new IllegalStateException("Failed to initialize a Cleaner.", e);
            }
        }
    }

    static boolean isSupported() {
        return INSTANCE != null;
    }

    private static interface Cleaner {
        public void clean(ByteBuffer var1) throws ReflectiveOperationException;
    }

    private static class Java8Cleaner
    implements Cleaner {
        private final Method cleanerMethod;
        private final Method cleanMethod = Class.forName("sun.misc.Cleaner").getMethod("clean", new Class[0]);

        private Java8Cleaner() throws ReflectiveOperationException, SecurityException {
            this.cleanerMethod = Class.forName("sun.nio.ch.DirectBuffer").getMethod("cleaner", new Class[0]);
        }

        @Override
        public void clean(ByteBuffer buffer) throws ReflectiveOperationException {
            Object cleaner = this.cleanerMethod.invoke((Object)buffer, new Object[0]);
            if (cleaner != null) {
                this.cleanMethod.invoke(cleaner, new Object[0]);
            }
        }
    }

    private static class Java9Cleaner
    implements Cleaner {
        private final Object theUnsafe;
        private final Method invokeCleaner;

        private Java9Cleaner() throws ReflectiveOperationException, SecurityException {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            this.theUnsafe = field.get(null);
            this.invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
        }

        @Override
        public void clean(ByteBuffer buffer) throws ReflectiveOperationException {
            this.invokeCleaner.invoke(this.theUnsafe, buffer);
        }
    }
}

