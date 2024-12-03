/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.configuration;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.configuration.AlgorithmType;
import org.apache.xml.security.configuration.ConfigurationType;
import org.apache.xml.security.configuration.HandlerType;
import org.apache.xml.security.configuration.JCEAlgorithmMappingsType;
import org.apache.xml.security.configuration.PropertiesType;
import org.apache.xml.security.configuration.PropertyType;
import org.apache.xml.security.configuration.ResolverType;
import org.apache.xml.security.configuration.ResourceResolversType;
import org.apache.xml.security.configuration.SecurityHeaderHandlersType;
import org.apache.xml.security.configuration.TransformAlgorithmType;
import org.apache.xml.security.configuration.TransformAlgorithmsType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _Configuration_QNAME = new QName("http://www.xmlsecurity.org/NS/configuration", "Configuration");

    public ConfigurationType createConfigurationType() {
        return new ConfigurationType();
    }

    public AlgorithmType createAlgorithmType() {
        return new AlgorithmType();
    }

    public TransformAlgorithmType createTransformAlgorithmType() {
        return new TransformAlgorithmType();
    }

    public ResolverType createResolverType() {
        return new ResolverType();
    }

    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    public TransformAlgorithmsType createTransformAlgorithmsType() {
        return new TransformAlgorithmsType();
    }

    public HandlerType createHandlerType() {
        return new HandlerType();
    }

    public SecurityHeaderHandlersType createSecurityHeaderHandlersType() {
        return new SecurityHeaderHandlersType();
    }

    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    public JCEAlgorithmMappingsType createJCEAlgorithmMappingsType() {
        return new JCEAlgorithmMappingsType();
    }

    public ResourceResolversType createResourceResolversType() {
        return new ResourceResolversType();
    }

    @XmlElementDecl(namespace="http://www.xmlsecurity.org/NS/configuration", name="Configuration")
    public JAXBElement<ConfigurationType> createConfiguration(ConfigurationType value) {
        return new JAXBElement(_Configuration_QNAME, ConfigurationType.class, null, (Object)value);
    }
}

