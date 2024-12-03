/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.exolab.castor.xml.MarshalException
 *  org.exolab.castor.xml.Unmarshaller
 *  org.exolab.castor.xml.ValidationException
 */
package org.apache.axis.encoding.ser.castor;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class CastorDeserializer
extends DeserializerImpl
implements Deserializer {
    public QName xmlType;
    public Class javaType;

    public CastorDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        try {
            MessageElement msgElem = context.getCurElement();
            if (msgElem != null) {
                this.value = Unmarshaller.unmarshal((Class)this.javaType, (Node)msgElem.getAsDOM());
            }
        }
        catch (MarshalException me) {
            log.error((Object)Messages.getMessage("castorMarshalException00"), (Throwable)me);
            throw new SAXException(Messages.getMessage("castorMarshalException00") + me.getLocalizedMessage());
        }
        catch (ValidationException ve) {
            log.error((Object)Messages.getMessage("castorValidationException00"), (Throwable)ve);
            throw new SAXException(Messages.getMessage("castorValidationException00") + ve.getLocation() + ": " + ve.getLocalizedMessage());
        }
        catch (Exception exp) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)exp);
            throw new SAXException(exp);
        }
    }
}

