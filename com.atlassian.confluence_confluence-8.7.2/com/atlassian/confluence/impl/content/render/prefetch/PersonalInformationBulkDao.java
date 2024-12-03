/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;

public interface PersonalInformationBulkDao {
    public Collection<PersonalInformation> bulkFetchPersonalInformation(Collection<UserKey> var1);
}

