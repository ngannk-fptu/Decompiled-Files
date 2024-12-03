/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.utils;

import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.SimpleTextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.DummyWordGenerator;
import com.octo.captcha.engine.image.utils.ImageToFile;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LogoGenerator {
    public static void main(String[] args) throws IOException {
        SimpleTextPaster paster = new SimpleTextPaster(new Integer(8), new Integer(8), Color.white);
        FileReaderRandomBackgroundGenerator back = new FileReaderRandomBackgroundGenerator(new Integer(200), new Integer(100), "/gimpybackgrounds");
        TwistedAndShearedRandomFontGenerator font = new TwistedAndShearedRandomFontGenerator(new Integer(30), null);
        DummyWordGenerator words = new DummyWordGenerator("JCAPTCHA");
        ComposedWordToImage word2image = new ComposedWordToImage(font, back, paster);
        GimpyFactory factory = new GimpyFactory(words, word2image);
        ImageCaptcha pix = ((ImageCaptchaFactory)factory).getImageCaptcha();
        ImageToFile.serialize(pix.getImageChallenge(), new File(args[0]));
    }
}

