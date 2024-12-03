/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.gson.Gson
 *  org.apache.commons.codec.binary.Base64
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jiracharts;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.confluence.plugins.jiracharts.model.JiraImageChartModel;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64JiraChartImageService {
    private static final Logger LOG = LoggerFactory.getLogger(Base64JiraChartImageService.class);
    private static final String PNG_IMAGE_FORMAT_NAME = "PNG";
    private ReadOnlyApplicationLinkService applicationLinkService;

    public Base64JiraChartImageService(ReadOnlyApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    public JiraImageChartModel getBase64JiraChartImageModel(String serverId, String gadgetURL) throws ResponseException {
        try {
            ReadOnlyApplicationLink applicationLink = JiraConnectorUtils.getApplicationLink(this.applicationLinkService, serverId);
            ApplicationLinkRequest request = JiraConnectorUtils.getApplicationLinkRequest(applicationLink, Request.MethodType.GET, gadgetURL);
            return (JiraImageChartModel)request.execute((ApplicationLinkResponseHandler)new Base64ImageResponseHandler(applicationLink.getRpcUrl().toString()));
        }
        catch (TypeNotInstalledException e) {
            throw new ResponseException("Can not get application link", (Throwable)e);
        }
        catch (Exception e) {
            throw new ResponseException("Can not retrieve jira chart image", (Throwable)e);
        }
    }

    static class Base64ImageResponseHandler
    implements ApplicationLinkResponseHandler {
        private String baseUrl;

        Base64ImageResponseHandler(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Object credentialsRequired(Response response) throws ResponseException {
            throw new ResponseException("Required Credentials");
        }

        public Object handle(Response response) throws ResponseException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                JiraImageChartModel chartModel = (JiraImageChartModel)new Gson().fromJson(response.getResponseBodyAsString(), JiraImageChartModel.class);
                BufferedImage bufferedImage = ImageIO.read(new URL(this.baseUrl + "/charts?filename=" + chartModel.getLocation()));
                ImageIO.write((RenderedImage)bufferedImage, Base64JiraChartImageService.PNG_IMAGE_FORMAT_NAME, os);
                chartModel.setBase64Image("data:image/png;base64," + Base64.encodeBase64String((byte[])os.toByteArray()));
                JiraImageChartModel jiraImageChartModel = chartModel;
                return jiraImageChartModel;
            }
            catch (Exception e) {
                throw new ResponseException("Can not retrieve jira chart image", (Throwable)e);
            }
            finally {
                try {
                    os.close();
                }
                catch (IOException e) {
                    LOG.debug("Can not close output stream");
                }
            }
        }
    }
}

