/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence;

import com.addonengine.addons.analytics.service.confluence.model.Attachment;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.model.ContentVersion;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006H&J\u0012\u0010\n\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\u0006H&J\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u0006\u0010\t\u001a\u00020\u0006H&\u00a8\u0006\r"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "", "getAttachments", "", "Lcom/addonengine/addons/analytics/service/confluence/model/Attachment;", "containerId", "", "getById", "Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "id", "getByIdOrNull", "getVersions", "Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;", "analytics"})
public interface ContentService {
    @NotNull
    public Content getById(long var1);

    @Nullable
    public Content getByIdOrNull(long var1);

    @NotNull
    public List<ContentVersion> getVersions(long var1);

    @NotNull
    public List<Attachment> getAttachments(long var1);
}

