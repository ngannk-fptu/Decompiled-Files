/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.CustomEncoding;
import org.apache.avro.reflect.FieldAccess;
import org.apache.avro.reflect.FieldAccessor;
import org.apache.avro.reflect.Stringable;

class FieldAccessReflect
extends FieldAccess {
    FieldAccessReflect() {
    }

    @Override
    protected FieldAccessor getAccessor(Field field) {
        AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return new ReflectionBasesAccessorCustomEncoded(field, enc.using().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not instantiate custom Encoding");
            }
        }
        return new ReflectionBasedAccessor(field);
    }

    private static final class ReflectionBasesAccessorCustomEncoded
    extends ReflectionBasedAccessor {
        private CustomEncoding<?> encoding;

        public ReflectionBasesAccessorCustomEncoded(Field f, CustomEncoding<?> encoding) {
            super(f);
            this.encoding = encoding;
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            try {
                this.field.set(object, this.encoding.read(in));
            }
            catch (IllegalAccessException e) {
                throw new AvroRuntimeException(e);
            }
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            try {
                this.encoding.write(this.field.get(object), out);
            }
            catch (IllegalAccessException e) {
                throw new AvroRuntimeException(e);
            }
        }

        @Override
        protected boolean isCustomEncoded() {
            return true;
        }

        @Override
        protected boolean supportsIO() {
            return true;
        }
    }

    private static class ReflectionBasedAccessor
    extends FieldAccessor {
        protected final Field field;
        private boolean isStringable;
        private boolean isCustomEncoded;

        public ReflectionBasedAccessor(Field field) {
            this.field = field;
            this.field.setAccessible(true);
            this.isStringable = field.isAnnotationPresent(Stringable.class);
            this.isCustomEncoded = field.isAnnotationPresent(AvroEncode.class);
        }

        public String toString() {
            return this.field.getName();
        }

        @Override
        public Object get(Object object) throws IllegalAccessException {
            return this.field.get(object);
        }

        @Override
        public void set(Object object, Object value) throws IllegalAccessException, IOException {
            this.field.set(object, value);
        }

        @Override
        protected Field getField() {
            return this.field;
        }

        @Override
        protected boolean isStringable() {
            return this.isStringable;
        }

        @Override
        protected boolean isCustomEncoded() {
            return this.isCustomEncoded;
        }
    }
}

