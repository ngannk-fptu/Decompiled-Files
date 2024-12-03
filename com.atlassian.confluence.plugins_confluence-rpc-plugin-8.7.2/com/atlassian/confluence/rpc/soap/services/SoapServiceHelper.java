/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public interface SoapServiceHelper {
    public static final String __PARANAMER_DATA = "assertCanAdminister com.atlassian.confluence.spaces.Space space \nassertCanCreateBlogPost com.atlassian.confluence.spaces.Space space \nassertCanCreatePage com.atlassian.confluence.spaces.Space space \nassertCanExport com.atlassian.confluence.spaces.Space space \nassertCanModify com.atlassian.confluence.pages.AbstractPage page \nassertCanModifyObject java.lang.Object,java.lang.String obj,typeDescription \nassertCanRemove com.atlassian.confluence.pages.AbstractPage page \nassertCanView com.atlassian.confluence.pages.AbstractPage page \nassertCanView com.atlassian.confluence.spaces.Space space \nretrieveAbstractPage long pageId \nretrieveContent long contentId \nretrievePage java.lang.String,java.lang.String spaceKey,pageTitle \nretrieveSpace java.lang.String spaceKey \nretrieveUser java.lang.String username \n";

    public Space retrieveSpace(String var1) throws RemoteException;

    public AbstractPage retrieveAbstractPage(long var1) throws RemoteException;

    public Page retrievePage(String var1, String var2) throws RemoteException;

    public User retrieveUser(String var1) throws RemoteException;

    public void assertCanAdminister() throws RemoteException;

    public void assertHasValidWebSudoSession() throws RemoteException;

    public void assertCanModifyObject(Object var1, String var2) throws NotPermittedException;

    public void assertCanModify(AbstractPage var1) throws RemoteException;

    public void assertCanRemove(AbstractPage var1) throws RemoteException;

    public void assertCanView(AbstractPage var1) throws RemoteException;

    public void assertCanExport(Space var1) throws RemoteException;

    public void assertCanAdminister(Space var1) throws RemoteException;

    public void assertCanView(Space var1) throws RemoteException;

    public void assertCanCreateBlogPost(Space var1) throws RemoteException;

    public void assertCanCreatePage(Space var1) throws RemoteException;

    public ContentEntityObject retrieveContent(long var1) throws RemoteException;
}

