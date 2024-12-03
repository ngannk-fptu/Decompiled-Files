/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Locale;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.MetadataSource;
import org.hibernate.mapping.PersistentClass;

public class VerifyFetchProfileReferenceSecondPass
implements SecondPass {
    private String fetchProfileName;
    private FetchProfile.FetchOverride fetch;
    private MetadataBuildingContext buildingContext;

    public VerifyFetchProfileReferenceSecondPass(String fetchProfileName, FetchProfile.FetchOverride fetch, MetadataBuildingContext buildingContext) {
        this.fetchProfileName = fetchProfileName;
        this.fetch = fetch;
        this.buildingContext = buildingContext;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        FetchProfile profile = this.buildingContext.getMetadataCollector().getFetchProfile(this.fetchProfileName);
        if (profile != null) {
            if (profile.getSource() != MetadataSource.ANNOTATIONS) {
                return;
            }
        } else {
            profile = new FetchProfile(this.fetchProfileName, MetadataSource.ANNOTATIONS);
            this.buildingContext.getMetadataCollector().addFetchProfile(profile);
        }
        PersistentClass clazz = this.buildingContext.getMetadataCollector().getEntityBinding(this.fetch.entity().getName());
        clazz.getProperty(this.fetch.association());
        profile.addFetch(this.fetch.entity().getName(), this.fetch.association(), this.fetch.mode().toString().toLowerCase(Locale.ROOT));
    }
}

