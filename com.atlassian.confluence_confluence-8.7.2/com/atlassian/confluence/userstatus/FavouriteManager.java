/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.userstatus;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import org.springframework.dao.DataAccessException;

public interface FavouriteManager {
    public boolean isUserFavourite(User var1, Space var2);

    public boolean hasPermission(User var1, Space var2);

    public void addSpaceToFavourites(User var1, Space var2) throws NotAuthorizedException, DataAccessException;

    public void removeSpaceFromFavourites(User var1, Space var2) throws NotAuthorizedException, DataAccessException;

    public boolean isUserFavourite(User var1, AbstractPage var2);

    public boolean hasPermission(User var1, AbstractPage var2);

    public void addPageToFavourites(User var1, AbstractPage var2) throws NotAuthorizedException, DataAccessException;

    public void removePageFromFavourites(User var1, AbstractPage var2) throws NotAuthorizedException, DataAccessException;
}

