/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.configuration.HandlerType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SecurityHeaderHandlersType", namespace="http://www.xmlsecurity.org/NS/configuration", propOrder={"handler"})
public class SecurityHeaderHandlersType {
    @XmlElement(name="Handler", namespace="http://www.xmlsecurity.org/NS/configuration")
    protected List<HandlerType> handler;

    public List<HandlerType> getHandler() {
        if (this.handler == null) {
            this.handler = new ArrayList<HandlerType>();
        }
        return this.handler;
    }
}

