/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence;

import com.addonengine.addons.analytics.service.confluence.model.Group;
import com.addonengine.addons.analytics.service.confluence.model.User;
import com.addonengine.addons.analytics.service.confluence.model.UserType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\bH&J\u0010\u0010\n\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH&J\u0018\u0010\n\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH&J\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH&J$\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000e2\u0006\u0010\u000b\u001a\u00020\fH&J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0010\u001a\u00020\bH&J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0007\u001a\u0004\u0018\u00010\bH&J\"\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00030\u00142\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH&J\u0010\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/UserService;", "", "getAnonymousUserDetails", "Lcom/addonengine/addons/analytics/service/confluence/model/User;", "getGroupsUserIsMemberOf", "", "Lcom/addonengine/addons/analytics/service/confluence/model/Group;", "userKey", "", "getUnknownUserName", "getUserDetails", "ignoreIncreasedPrivacyMode", "", "userKeys", "", "getUserKeyByUsername", "username", "getUserType", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getUsersDetailsMap", "", "isUserLicensed", "analytics"})
public interface UserService {
    @Nullable
    public String getUserKeyByUsername(@NotNull String var1);

    @NotNull
    public User getUserDetails(@NotNull String var1);

    @NotNull
    public User getUserDetails(@NotNull String var1, boolean var2);

    @NotNull
    public List<User> getUserDetails(@NotNull Set<String> var1);

    @NotNull
    public List<User> getUserDetails(@NotNull Set<String> var1, boolean var2);

    @NotNull
    public UserType getUserType(@Nullable String var1);

    @NotNull
    public User getAnonymousUserDetails();

    @NotNull
    public List<Group> getGroupsUserIsMemberOf(@NotNull String var1);

    @NotNull
    public Map<String, User> getUsersDetailsMap(@NotNull Set<String> var1);

    @NotNull
    public String getUnknownUserName();

    public boolean isUserLicensed(@NotNull String var1);
}

