/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser.castor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class CastorEnumTypeSerializer
implements Serializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$castor$CastorEnumTypeSerializer == null ? (class$org$apache$axis$encoding$ser$castor$CastorEnumTypeSerializer = CastorEnumTypeSerializer.class$("org.apache.axis.encoding.ser.castor.CastorEnumTypeSerializer")) : class$org$apache$axis$encoding$ser$castor$CastorEnumTypeSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$castor$CastorEnumTypeSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        try {
            Method method = value.getClass().getMethod("toString", new Class[0]);
            String string = (String)method.invoke(value, new Object[0]);
            context.writeString(string);
        }
        catch (Exception me) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)me);
            throw new IOException("Castor object error: " + me.getLocalizedMessage());
        }
        finally {
            context.endElement();
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element simpleType = types.createElement("simpleType");
        Element restriction = types.createElement("restriction");
        simpleType.appendChild(restriction);
        restriction.setAttribute("base", "xsd:string");
        Method enumerateMethod = javaType.getMethod("enumerate", new Class[0]);
        Enumeration en = (Enumeration)enumerateMethod.invoke(null, new Object[0]);
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            Method toStringMethod = obj.getClass().getMethod("toString", new Class[0]);
            String value = (String)toStringMethod.invoke(obj, new Object[0]);
            Element enumeration = types.createElement("enumeration");
            restriction.appendChild(enumeration);
            enumeration.setAttribute("value", value);
        }
        return simpleType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

