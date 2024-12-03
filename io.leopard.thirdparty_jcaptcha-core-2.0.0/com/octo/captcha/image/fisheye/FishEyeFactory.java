/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.image.fisheye;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.fisheye.FishEye;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class FishEyeFactory
extends ImageCaptchaFactory {
    public static final String BUNDLE_QUESTION_KEY = FishEye.class.getName();
    private Random myRandom = new SecureRandom();
    private BackgroundGenerator generator;
    private ImageDeformation deformation;
    private Integer tolerance;
    private Integer scale;

    public FishEyeFactory(BackgroundGenerator generator, ImageDeformation deformation, Integer scale, Integer tolerance) {
        if (generator == null) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : BackgroundGenerator can't be null");
        }
        if (deformation == null) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : ImageDeformation can't be null");
        }
        this.deformation = deformation;
        this.generator = generator;
        if (scale == null || scale < 1 || scale > 99) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : scale can't be null, and must be between 1 and 99");
        }
        this.scale = scale;
        if (tolerance == null || tolerance < 0) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : tolerance can't be null, and must be positive");
        }
        this.tolerance = tolerance;
    }

    @Override
    public ImageCaptcha getImageCaptcha() {
        return this.getImageCaptcha(Locale.getDefault());
    }

    @Override
    public ImageCaptcha getImageCaptcha(Locale locale) {
        BufferedImage background = this.generator.getBackground();
        BufferedImage out = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        out.getGraphics().drawImage(background, 0, 0, null, null);
        int x = background.getWidth();
        int y = background.getHeight();
        int scaledX = Math.max(x * this.scale / 100, 1);
        int scaledY = Math.max(y * this.scale / 100, 1);
        int xPos = this.myRandom.nextInt(x - scaledX);
        int yPos = this.myRandom.nextInt(y - scaledY);
        BufferedImage clone = out.getSubimage(xPos, yPos, scaledX, scaledY);
        out.getGraphics().drawImage(this.deformation.deformImage(clone), xPos, yPos, Color.white, null);
        out.getGraphics().dispose();
        Point center = new Point(xPos + scaledX / 2, yPos + scaledY / 2);
        return new FishEye(CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY), out, center, this.tolerance);
    }
}

