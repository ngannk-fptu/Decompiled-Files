/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser.castor;

import java.lang.reflect.Method;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.xml.sax.SAXException;

public class CastorEnumTypeDeserializer
extends DeserializerImpl
implements Deserializer {
    public QName xmlType;
    public Class javaType;
    static /* synthetic */ Class class$java$lang$String;

    public CastorEnumTypeDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        try {
            MessageElement msgElem = context.getCurElement();
            if (msgElem != null) {
                Method method = this.javaType.getMethod("valueOf", class$java$lang$String == null ? (class$java$lang$String = CastorEnumTypeDeserializer.class$("java.lang.String")) : class$java$lang$String);
                this.value = method.invoke(null, msgElem.getValue());
            }
        }
        catch (Exception exp) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)exp);
            throw new SAXException(exp);
        }
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

