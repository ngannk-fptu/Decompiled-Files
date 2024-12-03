/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.exolab.castor.xml.MarshalException
 *  org.exolab.castor.xml.Marshaller
 *  org.exolab.castor.xml.ValidationException
 */
package org.apache.axis.encoding.ser.castor;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.castor.AxisContentHandler;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

public class CastorSerializer
implements Serializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$castor$CastorSerializer == null ? (class$org$apache$axis$encoding$ser$castor$CastorSerializer = CastorSerializer.class$("org.apache.axis.encoding.ser.castor.CastorSerializer")) : class$org$apache$axis$encoding$ser$castor$CastorSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$castor$CastorSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        try {
            AxisContentHandler hand = new AxisContentHandler(context);
            Marshaller marshaller = new Marshaller((ContentHandler)hand);
            marshaller.setMarshalAsDocument(false);
            marshaller.setRootElement(name.getLocalPart());
            marshaller.marshal(value);
        }
        catch (MarshalException me) {
            log.error((Object)Messages.getMessage("castorMarshalException00"), (Throwable)me);
            throw new IOException(Messages.getMessage("castorMarshalException00") + me.getLocalizedMessage());
        }
        catch (ValidationException ve) {
            log.error((Object)Messages.getMessage("castorValidationException00"), (Throwable)ve);
            throw new IOException(Messages.getMessage("castorValidationException00") + ve.getLocation() + ": " + ve.getLocalizedMessage());
        }
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
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

