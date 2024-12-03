/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.user.Group;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface GroupMembershipAccessor {
    public Pager<String> getMemberNames(Group var1);

    default public List<String> getMemberNamesAsList(Group group) {
        return PagerUtils.toList(this.getMemberNames(group));
    }
}

