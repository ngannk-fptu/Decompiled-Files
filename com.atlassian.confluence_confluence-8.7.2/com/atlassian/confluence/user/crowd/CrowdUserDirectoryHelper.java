/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.crowd.CrowdUserDirectoryImplementation;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.user.User;
import java.util.List;
import java.util.Optional;

public interface CrowdUserDirectoryHelper {
    public Optional<SynchronisationMode> getSynchronisationMode(long var1);

    public CrowdUserDirectoryImplementation getUserDirectoryImplementation(long var1);

    public Optional<Integer> getUserCount(long var1);

    public Optional<Integer> getGroupCount(long var1);

    public Optional<Integer> getMembershipCount(long var1);

    public List<Directory> getDirectoriesForUser(User var1);
}

