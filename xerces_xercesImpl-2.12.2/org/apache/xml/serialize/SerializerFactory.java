/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.xml.serialize.ObjectFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.SecuritySupport;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactoryImpl;

public abstract class SerializerFactory {
    public static final String FactoriesProperty = "org.apache.xml.serialize.factories";
    private static Hashtable _factories = new Hashtable();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerSerializerFactory(SerializerFactory serializerFactory) {
        Hashtable hashtable = _factories;
        synchronized (hashtable) {
            String string = serializerFactory.getSupportedMethod();
            _factories.put(string, serializerFactory);
        }
    }

    public static SerializerFactory getSerializerFactory(String string) {
        return (SerializerFactory)_factories.get(string);
    }

    protected abstract String getSupportedMethod();

    public abstract Serializer makeSerializer(OutputFormat var1);

    public abstract Serializer makeSerializer(Writer var1, OutputFormat var2);

    public abstract Serializer makeSerializer(OutputStream var1, OutputFormat var2) throws UnsupportedEncodingException;

    static {
        SerializerFactory serializerFactory = new SerializerFactoryImpl("xml");
        SerializerFactory.registerSerializerFactory(serializerFactory);
        serializerFactory = new SerializerFactoryImpl("html");
        SerializerFactory.registerSerializerFactory(serializerFactory);
        serializerFactory = new SerializerFactoryImpl("xhtml");
        SerializerFactory.registerSerializerFactory(serializerFactory);
        serializerFactory = new SerializerFactoryImpl("text");
        SerializerFactory.registerSerializerFactory(serializerFactory);
        String string = SecuritySupport.getSystemProperty(FactoriesProperty);
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ;,:");
            while (stringTokenizer.hasMoreTokens()) {
                String string2 = stringTokenizer.nextToken();
                try {
                    serializerFactory = (SerializerFactory)ObjectFactory.newInstance(string2, SerializerFactory.class.getClassLoader(), true);
                    if (!_factories.containsKey(serializerFactory.getSupportedMethod())) continue;
                    _factories.put(serializerFactory.getSupportedMethod(), serializerFactory);
                }
                catch (Exception exception) {}
            }
        }
    }
}

