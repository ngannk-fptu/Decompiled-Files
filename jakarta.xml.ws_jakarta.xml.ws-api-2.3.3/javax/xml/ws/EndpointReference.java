/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlTransient
 */
package javax.xml.ws;

import java.io.StringWriter;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Provider;

@XmlTransient
public abstract class EndpointReference {
    protected EndpointReference() {
    }

    public static EndpointReference readFrom(Source eprInfoset) {
        return Provider.provider().readEndpointReference(eprInfoset);
    }

    public abstract void writeTo(Result var1);

    public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature ... features) {
        return Provider.provider().getPort(this, serviceEndpointInterface, features);
    }

    public String toString() {
        StringWriter w = new StringWriter();
        this.writeTo(new StreamResult(w));
        return w.toString();
    }
}

