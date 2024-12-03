/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.utils;

import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.engine.image.utils.ImageToFile;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SimpleImageCaptchaToJPEG {
    public static void main(String[] args) throws IOException {
        DefaultGimpyEngine bge = new DefaultGimpyEngine();
        System.out.println("got gimpy");
        ImageCaptchaFactory factory = bge.getImageCaptchaFactory();
        System.out.println("got factory");
        ImageCaptcha pixCaptcha = factory.getImageCaptcha();
        System.out.println("got image");
        System.out.println(pixCaptcha.getQuestion());
        BufferedImage bi = pixCaptcha.getImageChallenge();
        File f = new File("foo.jpg");
        ImageToFile.serialize(bi, f);
    }
}

