/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.benryan.components.OcSettingsManager
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.conversion;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.benryan.components.ContentResolver;
import com.benryan.components.OcSettingsManager;
import com.benryan.conversion.ConverterHelper;
import com.benryan.conversion.WebDavUtil;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultConverterHelper
implements ConverterHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultConverterHelper.class);
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final WebResourceManager webResourceManager;
    private final SettingsManager settingsManager;
    private final OcSettingsManager ocSettingsManager;
    private final ContentResolver contentResolver;
    private static final String OC_PLUGIN_KEY = "com.atlassian.confluence.extra.officeconnector:pptslideservlet";

    @Autowired
    public DefaultConverterHelper(@ComponentImport AttachmentManager attachmentManager, @ComponentImport PermissionManager permissionManager, @ComponentImport WebResourceManager webResourceManager, @ComponentImport SettingsManager settingsManager, OcSettingsManager ocSettingsManager, ContentResolver contentResolver) {
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.webResourceManager = webResourceManager;
        this.settingsManager = settingsManager;
        this.ocSettingsManager = ocSettingsManager;
        this.contentResolver = contentResolver;
    }

    @Override
    public Map<String, Object> validateArguments(Map args, ConversionContext context) throws MacroExecutionException {
        String date;
        String space;
        String file = this.getFileName(args);
        String pageName = (String)args.get("page");
        ContentEntityObject page = this.resolveContent(pageName, space = (String)args.get("space"), date = (String)args.get("date"), context);
        Attachment attachment = this.attachmentManager.getAttachment(page, file);
        if (attachment == null) {
            throw new MacroExecutionException("The viewfile macro is unable to locate the attachment \"" + file + "\" on " + (String)(pageName == null ? "this page" : "the page \"" + pageName + "\" in space \"" + space + "\""));
        }
        if (log.isDebugEnabled()) {
            ContentEntityObject content;
            String message = "Executing converter macro with attachment: '" + attachment + "' that belongs to page: '" + page + "'. ";
            if (context instanceof PageContext && (content = ((PageContext)context).getEntity()) != null) {
                message = message + "This macro was added to the following piece of content: " + content;
            }
            log.debug(message);
        }
        this.checkPermissions(page, space, attachment);
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        String contextPath = request != null ? request.getContextPath() : "";
        String typeName = this.getTypeName(args, file);
        if (typeName != null) {
            args.putIfAbsent("type", typeName);
        }
        HashMap<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.putAll(args);
        if (page instanceof Page) {
            WebDavUtil util = new WebDavUtil((AbstractPage)page);
            argsMap.put("editUrl", util.getRelWebDavUrl(file));
        }
        boolean isPreview = context.getOutputType().equals("preview");
        argsMap.put("isNews", page instanceof Page && !isPreview && this.permissionManager.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.EDIT, (Object)page));
        argsMap.put("attachmentObj", attachment);
        argsMap.put("pageID", String.valueOf(page.getId()));
        argsMap.put("context", contextPath);
        argsMap.put("attachment", file);
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        argsMap.put("baseUrl", baseUrl);
        argsMap.put("servletBaseUrl", contextPath);
        argsMap.put("useJavascript", !isPreview);
        argsMap.put("resourcePath", this.getResourcePath());
        argsMap.put("usePathAuth", this.ocSettingsManager.getPathAuth());
        if (context.getOutputType().equals("page_gadget")) {
            argsMap.put("servletBaseUrl", baseUrl);
            argsMap.put("context", baseUrl);
            argsMap.put("resourcePath", baseUrl.substring(0, baseUrl.length() - contextPath.length()) + this.getResourcePath());
            argsMap.put("isNews", false);
        }
        return argsMap;
    }

    private ContentEntityObject resolveContent(String pageName, String space, String date, ConversionContext context) throws MacroExecutionException {
        ContentEntityObject page;
        try {
            page = this.contentResolver.getContent(pageName, space, date, this.getContentObject(context));
        }
        catch (ParseException ex) {
            throw new MacroExecutionException("Unrecognized date string, please use mm/dd/yyyy");
        }
        catch (IllegalArgumentException ex) {
            throw new MacroExecutionException("The space key could not be found.");
        }
        if (page == null) {
            throw new MacroExecutionException("The viewfile macro is unable to locate the page \"" + pageName + "\" in space \"" + space + "\"");
        }
        return page;
    }

    private ContentEntityObject getContentObject(ConversionContext conversionContext) {
        RenderContext renderContext = conversionContext.getRenderContext();
        if (!(renderContext instanceof PageContext)) {
            return null;
        }
        return ((PageContext)renderContext).getEntity();
    }

    private String getFileName(Map args) throws MacroExecutionException {
        String file = (String)args.get("0");
        if (file == null && (file = (String)args.get("filename")) == null && ((file = (String)args.get("name")) == null || file.trim().length() == 0)) {
            throw new MacroExecutionException("No attachment name specified");
        }
        return file;
    }

    private String getTypeName(Map args, String file) {
        int dotIdx;
        String typeName = (String)args.get("type");
        if (typeName == null && (dotIdx = file.lastIndexOf(46)) != -1) {
            typeName = file.substring(dotIdx + 1);
        }
        return typeName;
    }

    private void checkPermissions(ContentEntityObject page, String space, Attachment attachment) throws MacroExecutionException {
        User user = AuthenticatedUserThreadLocal.getUser();
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)attachment)) {
            throw new MacroExecutionException("You don't have sufficient privileges to view the attachment '" + attachment.getFileName() + "' from page '" + page.getTitle() + "' in space '" + space + "'");
        }
    }

    private String getResourcePath() {
        String path = this.webResourceManager.getStaticPluginResource(OC_PLUGIN_KEY, "conversion");
        if (path != null && path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}

