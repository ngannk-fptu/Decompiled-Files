/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.actions.TemporaryUploadedPicture
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.exception.FailedPredicateException
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.user.User
 *  com.google.gson.Gson
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.ia.impl.SpaceLogoService;
import com.atlassian.confluence.plugins.ia.rest.SpaceDetailsBean;
import com.atlassian.confluence.plugins.ia.rest.TempLogoBean;
import com.atlassian.confluence.plugins.ia.service.SidebarPageService;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.plugins.ia.service.SpaceBeanFactory;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.TemporaryUploadedPicture;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.exception.FailedPredicateException;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.user.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

@Path(value="space")
@AnonymousAllowed
@Produces(value={"application/json"})
@InterceptorChain(value={TransactionInterceptor.class})
public class SpaceSidebarResource {
    private static final String TEMP_SPACE_LOGO = "temp_space_logo";
    private final SpaceManager spaceManager;
    private final SpaceBeanFactory spaceBeanFactory;
    private final SpaceLogoService spaceLogoService;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final SidebarService sidebarService;
    private final SidebarPageService sidebarPageService;
    private final SpacePermissionManager spacePermissionManager;
    private final HttpContext httpContext;

    public SpaceSidebarResource(SpaceManager spaceManager, SpaceBeanFactory spaceBeanFactory, SpaceLogoService spaceLogoService, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, SidebarService sidebarService, SidebarPageService sidebarPageService, SpacePermissionManager spacePermissionManager, HttpContext httpContext) {
        this.spaceManager = spaceManager;
        this.spaceBeanFactory = spaceBeanFactory;
        this.spaceLogoService = spaceLogoService;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.sidebarService = sidebarService;
        this.sidebarPageService = sidebarPageService;
        this.spacePermissionManager = spacePermissionManager;
        this.httpContext = httpContext;
    }

    @GET
    @Path(value="childPagesContextualNav")
    public Response getContextualNav(@QueryParam(value="pageId") long pageId) {
        return Response.ok((Object)this.sidebarPageService.getPageContextualNav(pageId)).build();
    }

    @GET
    public Response getSpaceData(@QueryParam(value="spaceKey") String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)user)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)this.spaceBeanFactory.createSpaceBean(space, this.getCurrentUser())).build();
    }

    @POST
    @Path(value="option")
    public Response setOption(Map<String, String> data) {
        try {
            this.sidebarService.setOption(data.get("spaceKey"), data.get("option"), data.get("value"));
            return Response.ok().build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
    }

    @GET
    @Path(value="defaultLogo")
    public Response getDefaultLogo() {
        return Response.ok((Object)new SpaceDetailsBean(null, this.spaceManager.getLogoForGlobalcontext())).build();
    }

    @POST
    @Consumes(value={"multipart/form-data"})
    @Produces(value={"text/html"})
    @Path(value="uploadLogo")
    public Response postNewLogo(@MultipartFormParam(value="upload-logo-input") FilePart filePart) {
        Gson gson = new Gson();
        String outputFormat = "<html><body><textarea id=\"json-response\">%s</textarea></body></html>";
        try {
            TemporaryUploadedPicture pic = this.spaceLogoService.createTempLogoFile(filePart);
            if (pic != null) {
                this.httpContext.getSession(true).setAttribute(TEMP_SPACE_LOGO, (Object)pic);
                return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempLogoBean(pic)))).build();
            }
            I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(this.getCurrentUser()));
            return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempLogoBean(i18n.getText("upload.not.a.picture"))))).build();
        }
        catch (FailedPredicateException e) {
            I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(this.getCurrentUser()));
            return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempLogoBean(i18n.getText("upload.too.large"))))).build();
        }
        catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path(value="setLogo")
    @Consumes(value={"application/json"})
    public Response setLogo(SpaceSettingsInformation spaceSettings) {
        try {
            Space space = this.spaceManager.getSpace(spaceSettings.getSpaceKey());
            this.spaceLogoService.changeSpaceName(space, spaceSettings.getSpaceName());
            if (StringUtils.isNotEmpty((CharSequence)spaceSettings.getLogoDataURI())) {
                this.spaceLogoService.saveLogo(space, spaceSettings.getLogoDataURI());
            }
            return Response.ok((Object)new SpaceDetailsBean(space.getName(), this.spaceManager.getLogoForSpace(spaceSettings.getSpaceKey()))).build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
    }

    protected User getCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    @XmlRootElement
    private static class SpaceSettingsInformation {
        @XmlElement
        private String spaceKey;
        @XmlElement
        private String spaceName;
        @XmlElement
        private String logoDataURI;

        private SpaceSettingsInformation() {
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public void setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
        }

        public String getSpaceName() {
            return this.spaceName;
        }

        public void setSpaceName(String spaceName) {
            this.spaceName = spaceName;
        }

        public String getLogoDataURI() {
            return this.logoDataURI;
        }

        public void setLogoDataURI(String logoDataURI) {
            this.logoDataURI = logoDataURI;
        }
    }
}

