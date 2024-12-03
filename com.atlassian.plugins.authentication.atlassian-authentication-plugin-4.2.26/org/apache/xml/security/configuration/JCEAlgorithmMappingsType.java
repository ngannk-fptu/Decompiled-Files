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
import org.apache.xml.security.configuration.AlgorithmType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="JCEAlgorithmMappingsType", namespace="http://www.xmlsecurity.org/NS/configuration", propOrder={"algorithm"})
public class JCEAlgorithmMappingsType {
    @XmlElement(name="Algorithm", namespace="http://www.xmlsecurity.org/NS/configuration")
    protected List<AlgorithmType> algorithm;

    public List<AlgorithmType> getAlgorithm() {
        if (this.algorithm == null) {
            this.algorithm = new ArrayList<AlgorithmType>();
        }
        return this.algorithm;
    }
}

