/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package com.sun.xml.ws.runtime.config;

import com.sun.xml.ws.runtime.config.MetroConfig;
import com.sun.xml.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.ws.runtime.config.TubeFactoryList;
import com.sun.xml.ws.runtime.config.TubelineDefinition;
import com.sun.xml.ws.runtime.config.TubelineMapping;
import com.sun.xml.ws.runtime.config.Tubelines;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    private static final QName _Tubelines_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubelines");
    private static final QName _TubelineMapping_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline-mapping");
    private static final QName _Tubeline_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline");

    public TubeFactoryConfig createTubeFactoryConfig() {
        return new TubeFactoryConfig();
    }

    public TubeFactoryList createTubeFactoryList() {
        return new TubeFactoryList();
    }

    public TubelineDefinition createTubelineDefinition() {
        return new TubelineDefinition();
    }

    public Tubelines createTubelines() {
        return new Tubelines();
    }

    public MetroConfig createMetroConfig() {
        return new MetroConfig();
    }

    public TubelineMapping createTubelineMapping() {
        return new TubelineMapping();
    }

    @XmlElementDecl(namespace="http://java.sun.com/xml/ns/metro/config", name="tubelines")
    public JAXBElement<Tubelines> createTubelines(Tubelines value) {
        return new JAXBElement(_Tubelines_QNAME, Tubelines.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://java.sun.com/xml/ns/metro/config", name="tubeline-mapping")
    public JAXBElement<TubelineMapping> createTubelineMapping(TubelineMapping value) {
        return new JAXBElement(_TubelineMapping_QNAME, TubelineMapping.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://java.sun.com/xml/ns/metro/config", name="tubeline")
    public JAXBElement<TubelineDefinition> createTubeline(TubelineDefinition value) {
        return new JAXBElement(_Tubeline_QNAME, TubelineDefinition.class, null, (Object)value);
    }
}

