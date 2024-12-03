/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.spaces;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ParametersAreNonnullByDefault
@Transactional
public interface SpaceManager {
    public static final String GLOBAL_LOGO = "global.logo";

    public @NonNull Space createSpace(String var1, String var2, @Nullable String var3, User var4);

    public @NonNull Space createPersonalSpace(String var1, @Nullable String var2, User var3);

    public @NonNull Space createPrivatePersonalSpace(String var1, @Nullable String var2, User var3);

    @Deprecated
    public @NonNull Space createSpace(Space var1);

    public @NonNull Space createPrivateSpace(String var1, String var2, @Nullable String var3, User var4);

    public void saveSpace(Space var1);

    public void saveSpace(Space var1, Space var2);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public @NonNull Boolean removeSpace(Space var1);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public @NonNull Boolean removeSpace(String var1);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public @NonNull Boolean removeSpace(String var1, ProgressMeter var2);

    @Deprecated
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void removeSpacesInGroup(SpaceGroup var1);

    @Deprecated
    @Transactional(readOnly=true)
    public @Nullable Space getSpace(long var1);

    @Deprecated
    @Transactional(readOnly=true)
    public @Nullable Space getSpace(@Nullable String var1);

    @Deprecated
    @Transactional(readOnly=true)
    public @Nullable Space getPersonalSpace(@Nullable ConfluenceUser var1);

    @Deprecated
    @Transactional(readOnly=true)
    public @NonNull List<Space> getAllSpaces();

    @Transactional(readOnly=true)
    public @NonNull List getSpacesContainingPagesEditedBy(String var1);

    @Transactional(readOnly=true)
    public @NonNull List getSpacesContainingCommentsBy(String var1);

    @Transactional(readOnly=true)
    public @NonNull List getAuthoredSpacesByUser(String var1);

    @Transactional(readOnly=true)
    public long findPageTotal(Space var1);

    @Transactional(readOnly=true)
    public int getNumberOfBlogPosts(Space var1);

    @Transactional(readOnly=true)
    public @Nullable String getSpaceFromPageId(long var1);

    @Transactional(readOnly=true)
    public @NonNull List<Space> getSpacesCreatedAfter(Date var1);

    public void ensureSpaceDescriptionExists(Space var1);

    @Transactional(readOnly=true)
    public @NonNull SpaceLogo getLogoForSpace(@Nullable String var1);

    @Transactional(readOnly=true)
    public @NonNull SpaceLogo getLogoForGlobalcontext();

    @Deprecated
    @Transactional(readOnly=true)
    public @NonNull ListBuilder<Space> getSpaces(SpacesQuery var1);

    @Deprecated
    @Transactional(readOnly=true)
    public @NonNull List<Space> getAllSpaces(SpacesQuery var1);

    @Transactional(readOnly=true)
    public @NonNull List<User> getSpaceAdmins(Space var1);

    @Transactional(readOnly=true)
    public @NonNull List<User> getSpaceAdmins(Space var1, int var2);

    public void archiveSpace(Space var1);

    public void unarchiveSpace(Space var1);

    @Transactional(readOnly=true)
    public @NonNull Collection<String> getAllSpaceKeys(SpaceStatus var1);
}

