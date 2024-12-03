/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.header.HttpDateFormat;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.text.ParseException;
import java.util.Date;
import javax.ws.rs.WebApplicationException;

public class StringReaderProviders {

    public static class DateProvider
    implements StringReaderProvider {
        public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
            if (type != Date.class) {
                return null;
            }
            return new StringReader(){

                public Object fromString(String value) {
                    try {
                        return HttpDateFormat.readDate(value);
                    }
                    catch (ParseException ex) {
                        throw new ExtractorContainerException(ex);
                    }
                }
            };
        }
    }

    public static class TypeFromStringEnum
    extends TypeFromString {
        @Override
        public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
            if (!Enum.class.isAssignableFrom(type)) {
                return null;
            }
            return super.getStringReader(type, genericType, annotations);
        }
    }

    public static class TypeFromString
    implements StringReaderProvider {
        public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
            final Method fromString = AccessController.doPrivileged(ReflectionHelper.getFromStringStringMethodPA(type));
            if (fromString == null) {
                return null;
            }
            return new AbstractStringReader(){

                @Override
                public Object _fromString(String value) throws Exception {
                    return fromString.invoke(null, value);
                }
            };
        }
    }

    public static class TypeValueOf
    implements StringReaderProvider {
        public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
            final Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(type));
            if (valueOf == null) {
                return null;
            }
            return new AbstractStringReader(){

                @Override
                public Object _fromString(String value) throws Exception {
                    return valueOf.invoke(null, value);
                }
            };
        }
    }

    public static class StringConstructor
    implements StringReaderProvider {
        public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
            final Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
            if (constructor == null) {
                return null;
            }
            return new AbstractStringReader(){

                @Override
                protected Object _fromString(String value) throws Exception {
                    return constructor.newInstance(value);
                }
            };
        }
    }

    private static abstract class AbstractStringReader
    implements StringReader {
        private AbstractStringReader() {
        }

        public Object fromString(String value) {
            try {
                return this._fromString(value);
            }
            catch (InvocationTargetException ex) {
                if (value.length() == 0) {
                    return null;
                }
                Throwable target = ex.getTargetException();
                if (target instanceof WebApplicationException) {
                    throw (WebApplicationException)target;
                }
                throw new ExtractorContainerException(target);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }

        protected abstract Object _fromString(String var1) throws Exception;
    }
}

