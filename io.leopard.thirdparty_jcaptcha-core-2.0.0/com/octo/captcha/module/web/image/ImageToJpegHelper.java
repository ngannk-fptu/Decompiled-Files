/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaServiceException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 */
package com.octo.captcha.module.web.image;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

public class ImageToJpegHelper {
    public static void flushNewCaptchaToResponse(HttpServletRequest theRequest, HttpServletResponse theResponse, Logger log, ImageCaptchaService service, String id, Locale locale) throws IOException {
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage challenge = service.getImageChallengeForID(id, locale);
            ImageIO.write((RenderedImage)challenge, "png", jpegOutputStream);
        }
        catch (IllegalArgumentException e) {
            if (log != null && log.isWarnEnabled()) {
                log.warn("There was a try from " + theRequest.getRemoteAddr() + " to render an captcha with invalid ID :'" + id + "' or with a too long one");
                theResponse.sendError(404);
                return;
            }
        }
        catch (CaptchaServiceException e) {
            if (log != null && log.isWarnEnabled()) {
                log.warn("Error trying to generate a captcha and render its challenge as JPEG", (Throwable)e);
            }
            theResponse.sendError(404);
            return;
        }
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        theResponse.setHeader("Cache-Control", "no-store");
        theResponse.setHeader("Pragma", "no-cache");
        theResponse.setDateHeader("Expires", 0L);
        theResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = theResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}

