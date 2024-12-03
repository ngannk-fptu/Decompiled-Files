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
package com.octo.captcha.module.web.sound;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.sound.SoundCaptchaService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.slf4j.Logger;

public class SoundToWavHelper {
    public static void flushNewCaptchaToResponse(HttpServletRequest theRequest, HttpServletResponse theResponse, Logger log, SoundCaptchaService service, String id, Locale locale) throws IOException {
        byte[] captchaChallengeAsWav = null;
        ByteArrayOutputStream wavOutputStream = new ByteArrayOutputStream();
        try {
            AudioInputStream stream = service.getSoundChallengeForID(id, locale);
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, wavOutputStream);
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
        captchaChallengeAsWav = wavOutputStream.toByteArray();
        theResponse.setHeader("Cache-Control", "no-store");
        theResponse.setHeader("Pragma", "no-cache");
        theResponse.setDateHeader("Expires", 0L);
        theResponse.setContentType("audio/x-wav");
        ServletOutputStream responseOutputStream = theResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsWav);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}

