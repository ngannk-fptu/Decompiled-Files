/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdentifierGeneratorDefinitionType;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.jboss.logging.Logger;

public class IdentifierGeneratorDefinitionBinder {
    private static final Logger log = Logger.getLogger(IdentifierGeneratorDefinitionBinder.class);

    public static void processIdentifierGeneratorDefinition(HbmLocalMetadataBuildingContext context, JaxbHbmIdentifierGeneratorDefinitionType identifierGenerator) {
        log.debugf("Processing <identifier-generator/> : %s", (Object)identifierGenerator.getName());
        context.getMetadataCollector().addIdentifierGenerator(new IdentifierGeneratorDefinition(identifierGenerator.getName(), identifierGenerator.getClazz()));
    }
}

