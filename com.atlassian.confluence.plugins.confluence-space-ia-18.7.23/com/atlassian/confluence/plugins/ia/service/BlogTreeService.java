/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.ia.service;

import com.atlassian.confluence.plugins.ia.model.BlogNodeBean;
import com.atlassian.confluence.plugins.ia.model.DateNodeBean;
import com.atlassian.user.User;
import java.util.Calendar;
import java.util.List;

public interface BlogTreeService {
    public List<DateNodeBean> getBlogTree(User var1, long var2);

    public List<DateNodeBean> getBlogTree(User var1, String var2, Calendar var3);

    public List<DateNodeBean> getMonthsWithBlogPosts(User var1, String var2, String var3);

    public List<BlogNodeBean> getBlogsForMonth(User var1, String var2, String var3);

    public String getDefaultBlogUrl(User var1, String var2);
}

