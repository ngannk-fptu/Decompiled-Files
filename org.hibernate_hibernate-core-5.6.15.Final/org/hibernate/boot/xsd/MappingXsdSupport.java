/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.xsd;

import org.hibernate.boot.xsd.LocalXsdResolver;
import org.hibernate.boot.xsd.XsdDescriptor;

public class MappingXsdSupport {
    public static final MappingXsdSupport INSTANCE = new MappingXsdSupport();
    private final XsdDescriptor jpa10 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/orm_1_0.xsd", "1.0", "http://java.sun.com/xml/ns/persistence/orm");
    private final XsdDescriptor jpa20 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/orm_2_0.xsd", "2.0", "http://java.sun.com/xml/ns/persistence/orm");
    private final XsdDescriptor jpa21 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/orm_2_1.xsd", "2.1", "http://xmlns.jcp.org/xml/ns/persistence/orm");
    private final XsdDescriptor jpa22 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/orm_2_2.xsd", "2.2", "http://xmlns.jcp.org/xml/ns/persistence/orm");
    private final XsdDescriptor jpa30 = LocalXsdResolver.buildXsdDescriptor("org/hibernate/jpa/orm_3_0.xsd", "3.0", "https://jakarta.ee/xml/ns/persistence/orm");
    private final XsdDescriptor hbmXml = LocalXsdResolver.buildXsdDescriptor("org/hibernate/xsd/mapping/legacy-mapping-4.0.xsd", "4.0", "http://www.hibernate.org/xsd/orm/hbm");

    private MappingXsdSupport() {
    }

    public XsdDescriptor latestJpaDescriptor() {
        return this.jpa22;
    }

    public XsdDescriptor jpaXsd(String version) {
        switch (version) {
            case "1.0": {
                return this.jpa10;
            }
            case "2.0": {
                return this.jpa20;
            }
            case "2.1": {
                return this.jpa21;
            }
            case "2.2": {
                return this.jpa22;
            }
            case "3.0:": {
                return this.jpa30;
            }
        }
        throw new IllegalArgumentException("Unrecognized JPA orm.xml XSD version : `" + version + "`");
    }

    public XsdDescriptor hbmXsd() {
        return this.hbmXml;
    }
}

