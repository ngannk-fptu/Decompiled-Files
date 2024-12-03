/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.applinks.internal.common.web.data;

import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class EntityTypesDataProvider
implements WebResourceDataProvider {
    private final I18nResolver i18nResolver;
    private final TypeAccessor typeAccessor;

    public EntityTypesDataProvider(I18nResolver i18nResolver, TypeAccessor typeAccessor) {
        this.i18nResolver = i18nResolver;
        this.typeAccessor = typeAccessor;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal((Object)this.getAllTypes());
    }

    private BaseRestEntity getAllTypes() {
        BaseRestEntity.Builder singularTypes = new BaseRestEntity.Builder();
        BaseRestEntity.Builder pluralTypes = new BaseRestEntity.Builder();
        for (EntityType entityType : this.typeAccessor.getEnabledEntityTypes()) {
            String id = ApplicationTypes.resolveEntityTypeId(entityType);
            singularTypes.add(id, this.i18nResolver.getText(entityType.getI18nKey()));
            pluralTypes.add(id, this.i18nResolver.getText(entityType.getPluralizedI18nKey()));
        }
        return new BaseRestEntity.Builder().add("singular", singularTypes.build()).add("plural", pluralTypes.build()).build();
    }
}

