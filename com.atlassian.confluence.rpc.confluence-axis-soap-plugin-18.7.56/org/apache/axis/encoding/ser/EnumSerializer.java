/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.SimpleSerializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class EnumSerializer
extends SimpleSerializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$EnumSerializer == null ? (class$org$apache$axis$encoding$ser$EnumSerializer = EnumSerializer.class$("org.apache.axis.encoding.ser.EnumSerializer")) : class$org$apache$axis$encoding$ser$EnumSerializer).getName());
    private Method toStringMethod = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumSerializer;

    public EnumSerializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        try {
            if (this.toStringMethod == null) {
                this.toStringMethod = this.javaType.getMethod("toString", null);
            }
            return (String)this.toStringMethod.invoke(value, null);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            return null;
        }
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return types.writeEnumType(this.xmlType, javaType);
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

