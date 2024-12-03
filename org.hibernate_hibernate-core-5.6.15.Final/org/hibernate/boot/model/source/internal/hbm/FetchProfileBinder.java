/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.MetadataSource;
import org.jboss.logging.Logger;

public class FetchProfileBinder {
    private static final Logger log = Logger.getLogger(FetchProfileBinder.class);

    public static void processFetchProfile(HbmLocalMetadataBuildingContext context, JaxbHbmFetchProfileType fetchProfileBinding) {
        FetchProfileBinder.processFetchProfile(context, fetchProfileBinding, null);
    }

    public static void processFetchProfile(HbmLocalMetadataBuildingContext context, JaxbHbmFetchProfileType fetchProfileBinding, String containingEntityName) {
        FetchProfile profile = context.getMetadataCollector().getFetchProfile(fetchProfileBinding.getName());
        if (profile == null) {
            log.debugf("Creating FetchProfile : %s", (Object)fetchProfileBinding.getName());
            profile = new FetchProfile(fetchProfileBinding.getName(), MetadataSource.HBM);
            context.getMetadataCollector().addFetchProfile(profile);
        }
        for (JaxbHbmFetchProfileType.JaxbHbmFetch fetchBinding : fetchProfileBinding.getFetch()) {
            String entityName = fetchBinding.getEntity();
            if (entityName == null) {
                entityName = containingEntityName;
            }
            if (entityName == null) {
                throw new MappingException(String.format("Unable to determine entity for fetch-profile fetch [%s:%s]", profile.getName(), fetchBinding.getAssociation()), context.getOrigin());
            }
            profile.addFetch(entityName, fetchBinding.getAssociation(), fetchBinding.getStyle().value());
        }
    }
}

