/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChartFactory;
import com.atlassian.confluence.plugins.jiracharts.render.JiraImageChart;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

public class GenericImagePlaceholderServlet
extends HttpServlet {
    private final PermissionManager permissionManager;
    private final PluginAccessor pluginAccessor;
    private final JiraChartFactory jiraChartFactory;
    private final I18nResolver i18nResolver;
    private final SettingsManager settingsManager;
    private static final String PNG_IMAGE_FORMAT_NAME = "PNG";

    public GenericImagePlaceholderServlet(PermissionManager permissionManager, PluginAccessor pluginAccessor, JiraChartFactory jiraChartFactory, I18nResolver i18nResolver, SettingsManager settingsManager) {
        this.permissionManager = permissionManager;
        this.pluginAccessor = pluginAccessor;
        this.jiraChartFactory = jiraChartFactory;
        this.i18nResolver = i18nResolver;
        this.settingsManager = settingsManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, this.i18nResolver.getText("jiraissues.error.notpermitted"));
            return;
        }
        if (!JiraChartHelper.isRequiredParamValid(req)) {
            resp.sendError(400, "Missing required \"chartType\" paramter");
            return;
        }
        String chartType = req.getParameter("chartType");
        try {
            JiraImageChart jiraChart = (JiraImageChart)this.jiraChartFactory.getJiraChartRenderer(chartType);
            resp.setContentType("image/png");
            ImageIO.write((RenderedImage)this.getDefaultPlaceHolder(jiraChart), PNG_IMAGE_FORMAT_NAME, (OutputStream)resp.getOutputStream());
        }
        catch (Exception e) {
            resp.sendError(400, "Could not process chart of type " + StringEscapeUtils.escapeHtml4((String)chartType));
        }
    }

    private BufferedImage getDefaultPlaceHolder(JiraImageChart jiraChart) throws IOException {
        return ImageIO.read(new URL(this.settingsManager.getGlobalSettings().getBaseUrl() + jiraChart.getDefaultImagePlaceholderUrl()));
    }
}

