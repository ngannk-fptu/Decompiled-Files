/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.util.Hashtable;
import java.util.Properties;
import org.apache.xml.serializer.ObjectFactory;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerConstants;
import org.apache.xml.serializer.utils.Utils;
import org.apache.xml.serializer.utils.WrappedRuntimeException;
import org.xml.sax.ContentHandler;

public final class SerializerFactory {
    private static Hashtable m_formats = new Hashtable();

    private SerializerFactory() {
    }

    public static Serializer getSerializer(Properties format) {
        Serializer ser;
        block6: {
            try {
                Properties methodDefaults;
                String method = format.getProperty("method");
                if (method == null) {
                    String msg = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[]{"method"});
                    throw new IllegalArgumentException(msg);
                }
                String className = format.getProperty("{http://xml.apache.org/xalan}content-handler");
                if (null == className && null == (className = (methodDefaults = OutputPropertiesFactory.getDefaultMethodProperties(method)).getProperty("{http://xml.apache.org/xalan}content-handler"))) {
                    String msg = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[]{"{http://xml.apache.org/xalan}content-handler"});
                    throw new IllegalArgumentException(msg);
                }
                ClassLoader loader = ObjectFactory.findClassLoader();
                Class cls = ObjectFactory.findProviderClass(className, loader, true);
                Object obj = cls.newInstance();
                if (obj instanceof SerializationHandler) {
                    ser = (Serializer)cls.newInstance();
                    ser.setOutputFormat(format);
                    break block6;
                }
                if (obj instanceof ContentHandler) {
                    className = SerializerConstants.DEFAULT_SAX_SERIALIZER;
                    cls = ObjectFactory.findProviderClass(className, loader, true);
                    SerializationHandler sh = (SerializationHandler)cls.newInstance();
                    sh.setContentHandler((ContentHandler)obj);
                    sh.setOutputFormat(format);
                    ser = sh;
                    break block6;
                }
                throw new Exception(Utils.messages.createMessage("ER_SERIALIZER_NOT_CONTENTHANDLER", new Object[]{className}));
            }
            catch (Exception e) {
                throw new WrappedRuntimeException(e);
            }
        }
        return ser;
    }
}

