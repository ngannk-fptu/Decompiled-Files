/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.highlight.rest;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugins.highlight.DefaultSelectionStorageFormatModifier;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.rest.TableModificationBean;
import com.atlassian.confluence.plugins.highlight.rest.WebItemModuleDescriptorEntity;
import com.atlassian.confluence.plugins.highlight.rest.XMLModificationBean;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@Path(value="/")
public class SelectionModificationResource {
    private static final Logger log = LoggerFactory.getLogger(SelectionModificationResource.class);
    private final DefaultSelectionStorageFormatModifier formatFinder;
    private final WebInterfaceManager webInterfaceManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final PageManager pageManager;

    public SelectionModificationResource(DefaultSelectionStorageFormatModifier formatFinder, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport PageManager pageManager) {
        this.formatFinder = formatFinder;
        this.webInterfaceManager = webInterfaceManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.pageManager = pageManager;
    }

    @GET
    @Path(value="panel-items")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response panelItems(@QueryParam(value="pageId") @DefaultValue(value="0") long pageId) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        DefaultWebInterfaceContext context = this.getContext(pageId);
        List displayableItems = this.webInterfaceManager.getDisplayableItems("page.view.selection/action-panel", context.toMap());
        ArrayList<WebItemModuleDescriptorEntity> entities = new ArrayList<WebItemModuleDescriptorEntity>();
        for (WebItemModuleDescriptor displayableItem : displayableItems) {
            entities.add(new WebItemModuleDescriptorEntity(displayableItem, i18NBean));
        }
        return Response.ok(entities).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="insert-storage-fragment")
    public Response insertStorageFragment(XMLModificationBean modificationBean) throws SAXException {
        try {
            long pageId = modificationBean.getPageId();
            long lastFetchTime = modificationBean.getLastFetchTime();
            TextSearch selection = new TextSearch(modificationBean.getSelectedText(), modificationBean.getNumMatches(), modificationBean.getIndex());
            XMLModification xmlModification = modificationBean.getXmlModification();
            boolean inserted = this.formatFinder.appendToSelection(pageId, lastFetchTime, selection, xmlModification);
            return Response.ok((Object)inserted).build();
        }
        catch (SelectionModificationException e) {
            return this.createResponse(e);
        }
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="insert-storage-column-table")
    public Response insertStorageTableColumnCells(TableModificationBean tableModificationBean) throws SAXException {
        try {
            long pageId = tableModificationBean.getPageId();
            long lastFetchTime = tableModificationBean.getLastFetchTime();
            TextSearch selection = new TextSearch(tableModificationBean.getSelectedText(), tableModificationBean.getNumMatches(), tableModificationBean.getIndex());
            TableModification tableModification = new TableModification(tableModificationBean.getTableColumnIndex(), tableModificationBean.getCellModifications());
            boolean inserted = this.formatFinder.appendToColumnTableCells(pageId, lastFetchTime, selection, tableModification);
            return Response.ok((Object)inserted).build();
        }
        catch (SelectionModificationException e) {
            return this.createResponse(e);
        }
    }

    private Response createResponse(SelectionModificationException e) {
        Response.Status status;
        switch (e.getType()) {
            case NO_OBJECT_TO_MODIFY: {
                status = Response.Status.NOT_FOUND;
                break;
            }
            case NO_PERMISSION: {
                status = Response.Status.UNAUTHORIZED;
                break;
            }
            case STALE_OBJECT_TO_MODIFY: {
                status = Response.Status.CONFLICT;
                break;
            }
            default: {
                status = Response.Status.BAD_REQUEST;
            }
        }
        return Response.status((Response.Status)status).entity((Object)e.getMessage()).build();
    }

    private DefaultWebInterfaceContext getContext(long pageId) {
        DefaultWebInterfaceContext context = new DefaultWebInterfaceContext();
        context.setCurrentUser(AuthenticatedUserThreadLocal.get());
        if (pageId != 0L) {
            AbstractPage page = this.pageManager.getAbstractPage(pageId);
            if (page != null) {
                context.setPage(page);
                context.setSpace(page.getSpace());
            } else {
                log.warn("Could not find page with id : {}, unable to set page in context", (Object)pageId);
            }
        }
        return context;
    }
}

