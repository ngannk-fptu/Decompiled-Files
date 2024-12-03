/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 */
package com.atlassian.crowd.dao.licensing;

import com.atlassian.crowd.dao.licensing.LicensedUsersQuery;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.licensing.LicensedUser;
import com.atlassian.crowd.model.licensing.LicensingSummary;
import java.util.List;

public interface LicensedUserDao {
    public LicensedUser findById(long var1) throws ObjectNotFoundException;

    public void update(LicensedUser var1);

    public void save(List<LicensedUser> var1);

    public void remove(LicensedUser var1);

    public List<LicensedUser> findLicensedUsers(LicensedUsersQuery var1);

    public Long countLicensedUsers(LicensedUsersQuery var1);

    public void removeAll(LicensingSummary var1);

    public void removeByDirectoryId(Long var1);
}

