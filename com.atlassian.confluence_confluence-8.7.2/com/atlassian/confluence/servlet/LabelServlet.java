/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LabelServlet.class);
    private static final String DISPLAY_LABEL_PATH = "/labels/viewlabel.action?ids={0}&spaceKey={1}";
    private static final String DISPLAY_PERSONAL_LABEL_PATH = "/users/viewmylabels.action?labelId={0}";
    private LabelManager labelManager;

    private LabelManager getLabelManager() {
        if (this.labelManager == null) {
            this.labelManager = (LabelManager)ContainerManager.getComponent((String)"labelManager");
        }
        return this.labelManager;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Label label;
        if (!GeneralUtil.isSetupComplete()) {
            httpServletResponse.sendError(503);
            return;
        }
        String requestPath = httpServletRequest.getPathInfo();
        if (requestPath == null) {
            httpServletResponse.sendError(404);
            return;
        }
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        String spaceKey = "";
        if (requestPath.indexOf(47) != -1) {
            spaceKey = requestPath.substring(0, requestPath.indexOf(47));
        }
        String labelNamesStr = requestPath.substring(requestPath.indexOf(47) + 1);
        String[] labelNames = labelNamesStr.split("\\+");
        ArrayList<String> ids = new ArrayList<String>(labelNames.length);
        boolean hasPersonal = false;
        for (int i = 0; i < labelNames.length; ++i) {
            Label label2 = this.getLabelManager().getLabel(labelNames[i]);
            if (label2 == null) continue;
            ids.add(Long.toString(label2.getId()));
            if (!Namespace.PERSONAL.equals(label2.getNamespace())) continue;
            hasPersonal = true;
        }
        if (labelNames.length > 1 && (label = this.getLabelManager().getLabel(labelNamesStr)) != null) {
            ids.add(Long.toString(label.getId()));
            if (Namespace.PERSONAL.equals(label.getNamespace())) {
                hasPersonal = true;
            }
        }
        if (ids.size() == 0) {
            httpServletResponse.sendError(404);
            return;
        }
        String pathToForward = "";
        String idStr = StringUtils.join(ids.iterator(), (String)"&ids=");
        pathToForward = hasPersonal ? MessageFormat.format(DISPLAY_PERSONAL_LABEL_PATH, idStr) : MessageFormat.format(DISPLAY_LABEL_PATH, idStr, HtmlUtil.urlEncode(spaceKey));
        if (log.isDebugEnabled()) {
            log.debug("Forwarding label request to: " + pathToForward);
        }
        httpServletRequest.getRequestDispatcher(pathToForward).forward((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    }
}

