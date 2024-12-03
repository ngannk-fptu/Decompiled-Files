/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.actions.TemporaryUploadedPicture
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.exception.FailedPredicateException
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.user.User
 *  com.google.gson.Gson
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.user.profile.rest;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.user.profile.UserAvatarService;
import com.atlassian.confluence.plugins.user.profile.rest.TempAvatar;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.actions.TemporaryUploadedPicture;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.exception.FailedPredicateException;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.user.User;
import com.google.gson.Gson;
import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/avatar/uploadFallback")
@Produces(value={"text/html"})
public class UploadFallbackResource {
    private static final String TEMP_USER_AVATAR = "temp_user_avatar";
    private final UserAvatarService userAvatarService;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final HttpContext httpContext;

    public UploadFallbackResource(UserAvatarService userAvatarService, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, HttpContext httpContext) {
        this.userAvatarService = userAvatarService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.httpContext = httpContext;
    }

    @POST
    public Response postNewLogo(@MultipartFormParam(value="upload-logo-input") FilePart filePart) {
        Gson gson = new Gson();
        String outputFormat = "<html><body><textarea id=\"json-response\">%s</textarea></body></html>";
        try {
            TemporaryUploadedPicture pic = this.userAvatarService.createTempLogoFile(filePart);
            if (pic != null) {
                this.httpContext.getSession(true).setAttribute(TEMP_USER_AVATAR, (Object)pic);
                return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempAvatar(pic)))).build();
            }
            I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
            return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempAvatar(i18n.getText("upload.not.a.picture"))))).build();
        }
        catch (FailedPredicateException e) {
            I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
            return Response.ok((Object)String.format(outputFormat, gson.toJson((Object)new TempAvatar(i18n.getText("upload.too.large"))))).build();
        }
        catch (IOException e) {
            return Response.serverError().build();
        }
    }
}

