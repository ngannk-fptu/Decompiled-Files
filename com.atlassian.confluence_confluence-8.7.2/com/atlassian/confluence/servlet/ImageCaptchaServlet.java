/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.image.ImageCaptchaService
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.spring.container.ContainerManager;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageCaptchaServlet
extends HttpServlet {
    public static final String CAPTCHA_ID = "id";
    private CaptchaManager captchaManager;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        byte[] captchaChallengeAsPng;
        String captchaId = httpServletRequest.getParameter(CAPTCHA_ID);
        if (captchaId == null || captchaId.isEmpty()) {
            return;
        }
        try {
            ImageCaptchaService imageCaptchaService = this.getCaptchaManager().getImageCaptchaService();
            if (imageCaptchaService == null) {
                throw new RuntimeException("There was a problem creating the CAPTCHA service. This probably indicates a problem with Java's image subsystem. You may need to configure your application server to supply the system property java.awt.headless=true.");
            }
            BufferedImage challenge = imageCaptchaService.getImageChallengeForID(captchaId, httpServletRequest.getLocale());
            ByteArrayOutputStream fout = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)challenge, "png", fout);
            captchaChallengeAsPng = fout.toByteArray();
        }
        catch (IllegalArgumentException e) {
            httpServletResponse.sendError(404);
            return;
        }
        catch (CaptchaServiceException e) {
            httpServletResponse.sendError(500);
            return;
        }
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setContentType("image/png");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsPng);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    public CaptchaManager getCaptchaManager() {
        if (this.captchaManager == null) {
            this.captchaManager = (CaptchaManager)ContainerManager.getComponent((String)"captchaManager");
        }
        return this.captchaManager;
    }
}

