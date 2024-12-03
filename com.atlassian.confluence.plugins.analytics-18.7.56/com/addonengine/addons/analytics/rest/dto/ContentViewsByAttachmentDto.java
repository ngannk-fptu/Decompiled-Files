/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto;

import com.addonengine.addons.analytics.rest.dto.AttachmentViewsDto;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ContentViewsByAttachmentDto;", "", "attachmentViews", "", "Lcom/addonengine/addons/analytics/rest/dto/AttachmentViewsDto;", "(Ljava/util/List;)V", "getAttachmentViews", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ContentViewsByAttachmentDto {
    @NotNull
    private final List<AttachmentViewsDto> attachmentViews;

    public ContentViewsByAttachmentDto(@NotNull List<AttachmentViewsDto> attachmentViews) {
        Intrinsics.checkNotNullParameter(attachmentViews, (String)"attachmentViews");
        this.attachmentViews = attachmentViews;
    }

    @NotNull
    public final List<AttachmentViewsDto> getAttachmentViews() {
        return this.attachmentViews;
    }

    @NotNull
    public final List<AttachmentViewsDto> component1() {
        return this.attachmentViews;
    }

    @NotNull
    public final ContentViewsByAttachmentDto copy(@NotNull List<AttachmentViewsDto> attachmentViews) {
        Intrinsics.checkNotNullParameter(attachmentViews, (String)"attachmentViews");
        return new ContentViewsByAttachmentDto(attachmentViews);
    }

    public static /* synthetic */ ContentViewsByAttachmentDto copy$default(ContentViewsByAttachmentDto contentViewsByAttachmentDto, List list, int n, Object object) {
        if ((n & 1) != 0) {
            list = contentViewsByAttachmentDto.attachmentViews;
        }
        return contentViewsByAttachmentDto.copy(list);
    }

    @NotNull
    public String toString() {
        return "ContentViewsByAttachmentDto(attachmentViews=" + this.attachmentViews + ')';
    }

    public int hashCode() {
        return ((Object)this.attachmentViews).hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentViewsByAttachmentDto)) {
            return false;
        }
        ContentViewsByAttachmentDto contentViewsByAttachmentDto = (ContentViewsByAttachmentDto)other;
        return Intrinsics.areEqual(this.attachmentViews, contentViewsByAttachmentDto.attachmentViews);
    }
}

