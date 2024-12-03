/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.rpc.AuthenticationFailedException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.xmlrpc.bloggingrpc;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.List;

public interface BloggingUtils {
    public ConfluenceUser authenticateUser(String var1, String var2) throws AuthenticationFailedException;

    public List<Space> getBlogs(User var1);

    public String getText(String var1);

    public String getText(String var1, String var2);

    public String getText(String var1, Object[] var2);

    public String convertStorageFormatToView(BlogPost var1);
}

