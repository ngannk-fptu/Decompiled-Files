/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.spaces.SpaceGroup;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Transactional
public interface SpaceGroupManager {
    public SpaceGroup createSpaceGroup(String var1, String var2);

    public SpaceGroup createSpaceGroup(String var1, String var2, String var3);

    public void saveSpaceGroup(SpaceGroup var1);

    public void removeSpaceGroup(SpaceGroup var1, boolean var2);

    @Transactional(readOnly=true)
    public SpaceGroup getSpaceGroup(long var1);

    @Transactional(readOnly=true)
    public SpaceGroup getSpaceGroup(String var1);

    @Transactional(readOnly=true)
    public List getSpaceGroups();

    @Transactional(readOnly=true)
    public Set<SpaceGroup> getSpaceGroupsForUser(String var1);
}

