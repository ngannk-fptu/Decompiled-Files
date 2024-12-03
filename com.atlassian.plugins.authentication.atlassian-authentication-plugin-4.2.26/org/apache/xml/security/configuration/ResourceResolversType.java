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
import org.apache.xml.security.configuration.ResolverType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ResourceResolversType", namespace="http://www.xmlsecurity.org/NS/configuration", propOrder={"resolver"})
public class ResourceResolversType {
    @XmlElement(name="Resolver", namespace="http://www.xmlsecurity.org/NS/configuration")
    protected List<ResolverType> resolver;

    public List<ResolverType> getResolver() {
        if (this.resolver == null) {
            this.resolver = new ArrayList<ResolverType>();
        }
        return this.resolver;
    }
}

