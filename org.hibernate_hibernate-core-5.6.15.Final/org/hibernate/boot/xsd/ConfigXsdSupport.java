/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.xsd;

import org.hibernate.boot.xsd.LocalXsdResolver;
import org.hibernate.boot.xsd.XsdDescriptor;

public class ConfigXsdSupport {
    private static final XsdDescriptor[] xsdCache = new XsdDescriptor[6];

    public XsdDescriptor latestJpaDescriptor() {
        return this.getJPA22();
    }

    public XsdDescriptor jpaXsd(String version) {
        switch (version) {
            case "1.0": {
                return this.getJPA10();
            }
            case "2.0": {
                return this.getJPA20();
            }
            case "2.1": {
                return this.getJPA21();
            }
            case "2.2": {
                return this.getJPA22();
            }
            case "3.0": {
                return this.getJPA30();
            }
        }
        throw new IllegalArgumentException("Unrecognized JPA persistence.xml XSD version : `" + version + "`");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XsdDescriptor cfgXsd() {
        boolean index = false;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor cfgXml = xsdCache[0];
            if (cfgXml == null) {
                ConfigXsdSupport.xsdCache[0] = cfgXml = LocalXsdResolver.buildXsdDescriptor("org/hibernate/xsd/cfg/legacy-configuration-4.0.xsd", "4.0", "http://www.hibernate.org/xsd/orm/cfg");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return cfgXml;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XsdDescriptor getJPA10() {
        boolean index = true;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor jpa10 = xsdCache[1];
            if (jpa10 == null) {
                ConfigXsdSupport.xsdCache[1] = jpa10 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/persistence_1_0.xsd", "1.0", "http://java.sun.com/xml/ns/persistence");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return jpa10;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XsdDescriptor getJPA20() {
        int index = 2;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor jpa20 = xsdCache[2];
            if (jpa20 == null) {
                ConfigXsdSupport.xsdCache[2] = jpa20 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/persistence_2_0.xsd", "2.0", "http://java.sun.com/xml/ns/persistence");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return jpa20;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XsdDescriptor getJPA21() {
        int index = 3;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor jpa21 = xsdCache[3];
            if (jpa21 == null) {
                ConfigXsdSupport.xsdCache[3] = jpa21 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/persistence_2_1.xsd", "2.1", "http://xmlns.jcp.org/xml/ns/persistence");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return jpa21;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XsdDescriptor getJPA22() {
        int index = 4;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor jpa22 = xsdCache[4];
            if (jpa22 == null) {
                ConfigXsdSupport.xsdCache[4] = jpa22 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/persistence_2_2.xsd", "2.2", "http://xmlns.jcp.org/xml/ns/persistence");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return jpa22;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XsdDescriptor getJPA30() {
        int index = 5;
        XsdDescriptor[] xsdDescriptorArray = xsdCache;
        synchronized (xsdCache) {
            XsdDescriptor jpa30 = xsdCache[5];
            if (jpa30 == null) {
                ConfigXsdSupport.xsdCache[5] = jpa30 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/persistence_3_0.xsd", "3.0", "https://jakarta.ee/xml/ns/persistence");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return jpa30;
        }
    }
}

