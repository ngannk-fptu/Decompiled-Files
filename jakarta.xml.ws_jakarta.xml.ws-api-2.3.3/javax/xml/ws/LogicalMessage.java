/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package javax.xml.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

public interface LogicalMessage {
    public Source getPayload();

    public void setPayload(Source var1);

    public Object getPayload(JAXBContext var1);

    public void setPayload(Object var1, JAXBContext var2);
}

