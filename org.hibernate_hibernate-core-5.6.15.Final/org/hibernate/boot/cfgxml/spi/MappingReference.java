/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.cfgxml.spi;

import java.io.File;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgMappingReferenceType;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationException;

public class MappingReference {
    private final Type type;
    private final String reference;

    public MappingReference(Type type, String reference) {
        this.type = type;
        this.reference = reference;
    }

    public Type getType() {
        return this.type;
    }

    public String getReference() {
        return this.reference;
    }

    public static MappingReference consume(JaxbCfgMappingReferenceType jaxbMapping) {
        if (StringHelper.isNotEmpty(jaxbMapping.getClazz())) {
            return new MappingReference(Type.CLASS, jaxbMapping.getClazz());
        }
        if (StringHelper.isNotEmpty(jaxbMapping.getFile())) {
            return new MappingReference(Type.FILE, jaxbMapping.getFile());
        }
        if (StringHelper.isNotEmpty(jaxbMapping.getResource())) {
            return new MappingReference(Type.RESOURCE, jaxbMapping.getResource());
        }
        if (StringHelper.isNotEmpty(jaxbMapping.getJar())) {
            return new MappingReference(Type.JAR, jaxbMapping.getJar());
        }
        if (StringHelper.isNotEmpty(jaxbMapping.getPackage())) {
            return new MappingReference(Type.PACKAGE, jaxbMapping.getPackage());
        }
        throw new ConfigurationException("<mapping/> named unexpected reference type");
    }

    public void apply(MetadataSources metadataSources) {
        switch (this.getType()) {
            case RESOURCE: {
                metadataSources.addResource(this.getReference());
                break;
            }
            case CLASS: {
                metadataSources.addAnnotatedClassName(this.getReference());
                break;
            }
            case FILE: {
                metadataSources.addFile(this.getReference());
                break;
            }
            case PACKAGE: {
                metadataSources.addPackage(this.getReference());
                break;
            }
            case JAR: {
                metadataSources.addJar(new File(this.getReference()));
            }
        }
    }

    public static enum Type {
        RESOURCE,
        CLASS,
        FILE,
        JAR,
        PACKAGE;

    }
}

