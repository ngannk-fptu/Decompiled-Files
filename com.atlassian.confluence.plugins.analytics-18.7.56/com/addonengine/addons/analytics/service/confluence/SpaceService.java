/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence;

import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.SpaceType;
import java.net.URL;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J$\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH&J\u001c\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000fH&J\u001a\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\bH&J\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000fH&\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "", "getByKey", "Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "key", "", "getByKeyOrNull", "includeCategories", "", "getDefaultSpacesLogoUrl", "Ljava/net/URL;", "getSpaceContent", "", "Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "getSpaces", "spaceTypes", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "getSpacesLogoUrl", "spaceKey", "redirectUrl", "getSpacesWithCategories", "analytics"})
public interface SpaceService {
    @NotNull
    public Space getByKey(@NotNull String var1);

    @Nullable
    public Space getByKeyOrNull(@NotNull String var1, boolean var2);

    @NotNull
    public List<Space> getSpaces(@NotNull Set<? extends SpaceType> var1);

    @NotNull
    public URL getSpacesLogoUrl(@NotNull String var1, boolean var2);

    @NotNull
    public URL getDefaultSpacesLogoUrl();

    @NotNull
    public List<Space> getSpacesWithCategories(@NotNull Set<? extends SpaceType> var1);

    @NotNull
    public List<Content> getSpaceContent(@NotNull String var1, @NotNull Set<? extends ContentType> var2);

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        public static /* synthetic */ Space getByKeyOrNull$default(SpaceService spaceService, String string, boolean bl, int n, Object object) {
            if (object != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getByKeyOrNull");
            }
            if ((n & 2) != 0) {
                bl = false;
            }
            return spaceService.getByKeyOrNull(string, bl);
        }

        public static /* synthetic */ URL getSpacesLogoUrl$default(SpaceService spaceService, String string, boolean bl, int n, Object object) {
            if (object != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getSpacesLogoUrl");
            }
            if ((n & 2) != 0) {
                bl = false;
            }
            return spaceService.getSpacesLogoUrl(string, bl);
        }
    }
}

