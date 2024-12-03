/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.plugins.jira.ChartProxyServlet;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChartFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageGeneratorServlet
extends ChartProxyServlet {
    private static final Logger log = LoggerFactory.getLogger(ImageGeneratorServlet.class);
    private static final String IMAGE_JIM_PATH = "jira/jira-logo.png";
    private static final String PLUGIN_KEY = "confluence.extra.jira";
    private static final int FONT_SIZE = 13;
    private static final int ADDED_IMAGE_SIZE = 5;
    private static final int THUMB_JIRA_CHART_WIDTH = 420;
    private static final int THUMB_JIRA_CHART_HEIGHT = 300;
    private static final int PADDING_TOP_CHART = 12;
    private static final int PADDING_TOP_TEXT = 8;
    private final PluginAccessor pluginAccessor;

    public ImageGeneratorServlet(ReadOnlyApplicationLinkService appLinkService, PluginAccessor pluginAccessor, I18nResolver i18nResolver, JiraChartFactory jiraChartFactory, PermissionManager permissionManager) {
        super(appLinkService, jiraChartFactory, i18nResolver, permissionManager);
        this.pluginAccessor = pluginAccessor;
    }

    private String getText(String key, String totalIssuesText) {
        return this.getI18nResolver().getText(key, (Serializable[])new String[]{totalIssuesText});
    }

    private String getText(String key) {
        return this.getI18nResolver().getText(key);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("jirachart".equals(req.getParameter("macro"))) {
            try {
                this.doProxy(req, resp, Request.MethodType.GET);
            }
            catch (ServletException e) {
                log.error("error render jira chart macro", (Throwable)e);
                throw new IOException();
            }
        } else {
            if (!this.getPermissionManager().hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
                resp.sendError(403, this.getI18nResolver().getText("jiraissues.error.notpermitted"));
                return;
            }
            BufferedImage bufferedImage = this.renderImageJiraIssuesMacro(req);
            resp.setContentType("image/png");
            ImageIO.write((RenderedImage)bufferedImage, "png", (OutputStream)resp.getOutputStream());
        }
    }

    @Override
    protected URI getApplinkURL(ReadOnlyApplicationLink applicationLink) {
        return applicationLink.getRpcUrl();
    }

    private BufferedImage renderImageJiraIssuesMacro(HttpServletRequest req) throws IOException {
        String totalIssuesText = this.getTotalIssueText(req.getParameter("totalIssues"));
        BufferedImage atlassianIcon = this.getIconBufferImage();
        Font font = new Font("Arial", 0, 13);
        Graphics2D originalGraphic = atlassianIcon.createGraphics();
        originalGraphic.setFont(font);
        FontMetrics fm = originalGraphic.getFontMetrics(font);
        int bufferedImageSize = atlassianIcon.getWidth() + fm.stringWidth(totalIssuesText) + 5;
        BufferedImage bufferedImage = new BufferedImage(bufferedImageSize, atlassianIcon.getHeight(), atlassianIcon.getType());
        Graphics2D graphics = this.drawImage(bufferedImage, atlassianIcon, 0, 0, atlassianIcon.getWidth(), atlassianIcon.getHeight());
        int textYPosition = (bufferedImage.getHeight() + fm.getAscent()) / 2;
        graphics.drawString(totalIssuesText, atlassianIcon.getWidth(), textYPosition);
        return bufferedImage;
    }

    private String getTotalIssueText(String totalIssuesParamValue) {
        if (StringUtils.isNumeric((CharSequence)totalIssuesParamValue)) {
            int totalIssues = Integer.parseInt(totalIssuesParamValue);
            if (totalIssues == 1) {
                return this.getText("jiraissues.static.issue.word", totalIssuesParamValue);
            }
            return this.getText("jiraissues.static.issues.word", totalIssuesParamValue);
        }
        return this.getText("jiraissues.static.issues.word", "x");
    }

    private BufferedImage renderImageJiraChartMacro(String imgLink) throws IOException {
        BufferedImage chart = ImageIO.read(new URL(imgLink));
        int chartWidth = chart.getWidth();
        int chartHeight = chart.getHeight();
        int chartPadX = (420 - chartWidth) / 2;
        int chartPadY = (300 - chartHeight) / 2 + 12;
        BufferedImage placeholder = new BufferedImage(420, 300, 2);
        Graphics2D g = this.drawImage(placeholder, chart, chartPadX, chartPadY, chartWidth, chartHeight);
        BufferedImage iconBufferImage = this.getIconBufferImage();
        int iconWidth = iconBufferImage.getWidth();
        int iconHeight = iconBufferImage.getHeight();
        g.drawImage(iconBufferImage, 5, 0, iconWidth, iconHeight, null);
        g.drawString(this.getText("jirachart.macro.placeholder.title.name"), 5 + iconWidth, iconHeight / 2 + 8);
        g.dispose();
        return placeholder;
    }

    private Graphics2D drawImage(BufferedImage placeholder, BufferedImage imageChart, int imagePosX, int imagePosY, int chartWidth, int chartHeight) {
        Graphics2D g = placeholder.createGraphics();
        Font font = new Font("Arial", 0, 13);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(Color.DARK_GRAY);
        g.drawImage(imageChart, imagePosX, imagePosY, chartWidth, chartHeight, null);
        return g;
    }

    private BufferedImage getIconBufferImage() throws IOException {
        InputStream in = this.pluginAccessor.getPlugin(PLUGIN_KEY).getClassLoader().getResourceAsStream(IMAGE_JIM_PATH);
        return ImageIO.read(in);
    }

    @Override
    protected void handleResponse(ApplicationLinkRequestFactory requestFactory, HttpServletRequest req, HttpServletResponse resp, ApplicationLinkRequest request, ReadOnlyApplicationLink appLink) throws ResponseException {
        String imgLink = this.getRedirectImgLink(request, req, requestFactory, resp, appLink);
        try {
            BufferedImage bufferedImage = this.renderImageJiraChartMacro(imgLink);
            resp.setContentType("image/png");
            ImageIO.write((RenderedImage)bufferedImage, "png", (OutputStream)resp.getOutputStream());
        }
        catch (IOException e) {
            throw new ResponseException();
        }
    }
}

