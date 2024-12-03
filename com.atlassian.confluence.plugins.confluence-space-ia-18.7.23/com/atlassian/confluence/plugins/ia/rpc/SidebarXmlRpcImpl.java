/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.ia.rpc;

import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.plugins.ia.rpc.SidebarXmlRpc;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collection;
import java.util.List;

public class SidebarXmlRpcImpl
implements SidebarXmlRpc {
    private final SidebarLinkService sidebarLinkService;
    private final TransactionTemplate transactionTemplate;
    private final SidebarService sidebarService;

    public SidebarXmlRpcImpl(SidebarLinkService sidebarLinkService, TransactionTemplate transactionTemplate, SidebarService sidebarService) {
        this.sidebarLinkService = sidebarLinkService;
        this.transactionTemplate = transactionTemplate;
        this.sidebarService = sidebarService;
    }

    @Override
    public boolean addSidebarQuickLink(String token, final String spaceKey, final String pageId, final String title, final String customClass) throws RemoteException {
        return (Boolean)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    if (SidebarXmlRpcImpl.this.sidebarLinkService.hasQuickLink(spaceKey, Long.parseLong(pageId))) {
                        return false;
                    }
                    SidebarXmlRpcImpl.this.sidebarLinkService.create(spaceKey, Long.parseLong(pageId), title, null, customClass);
                }
                catch (NotPermittedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
    }

    @Override
    public boolean addSidebarQuickLink(String token, final String spaceKey, final String resourceType, final String resourceId, final String title, final String customClass) throws RemoteException {
        return (Boolean)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    if (SidebarXmlRpcImpl.this.sidebarLinkService.hasQuickLink(spaceKey, Long.parseLong(resourceId))) {
                        return false;
                    }
                    SidebarXmlRpcImpl.this.sidebarLinkService.create(spaceKey, resourceType, Long.parseLong(resourceId), title, null, customClass);
                }
                catch (NotPermittedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
    }

    @Override
    public boolean removeSidebarQuickLink(String token, final String spaceKey, final String pageId) throws RemoteException {
        return (Boolean)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    Collection<SidebarLinkBean> links = SidebarXmlRpcImpl.this.sidebarLinkService.getQuickLinksForDestinationPage(spaceKey, Long.parseLong(pageId));
                    for (SidebarLinkBean link : links) {
                        SidebarXmlRpcImpl.this.sidebarLinkService.delete(spaceKey, link.getId());
                    }
                    return true;
                }
                catch (NotPermittedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public boolean removeSidebarQuickLinks(String token, final String spaceKey) throws RemoteException {
        return (Boolean)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    List<SidebarLinkBean> links = SidebarXmlRpcImpl.this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.QUICK, spaceKey, false);
                    for (SidebarLinkBean link : links) {
                        SidebarXmlRpcImpl.this.sidebarLinkService.delete(spaceKey, link.getId());
                    }
                    return true;
                }
                catch (NotPermittedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public String getOption(String token, String spaceKey, String option) throws RemoteException {
        String navType = this.sidebarService.getOption(spaceKey, option);
        return navType != null ? navType : "page-tree";
    }

    @Override
    public boolean setOption(String token, String spaceKey, String option, String value) throws RemoteException {
        try {
            this.sidebarService.setOption(spaceKey, option, value);
            return true;
        }
        catch (NotPermittedException e) {
            throw new RuntimeException(e);
        }
    }
}

