/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.applinks.internal.common.web.data;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class ApplicationTypesDataProvider
implements WebResourceDataProvider {
    private final I18nResolver i18nResolver;
    private final TypeAccessor typeAccessor;

    public ApplicationTypesDataProvider(I18nResolver i18nResolver, TypeAccessor typeAccessor) {
        this.i18nResolver = i18nResolver;
        this.typeAccessor = typeAccessor;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal((Object)this.getAllTypes());
    }

    private BaseRestEntity getAllTypes() {
        BaseRestEntity.Builder allTypes = new BaseRestEntity.Builder();
        for (ApplicationType applicationType : this.typeAccessor.getEnabledApplicationTypes()) {
            allTypes.add(ApplicationTypes.resolveApplicationTypeId(applicationType), this.i18nResolver.getText(applicationType.getI18nKey()));
        }
        return allTypes.build();
    }
}

