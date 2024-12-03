/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;

public class SimpleListImageCaptchaEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        RandomWordGenerator wordGenerator = new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        RandomTextPaster textPaster = new RandomTextPaster(new Integer(5), new Integer(8), Color.white);
        FunkyBackgroundGenerator backgroundGenerator = new FunkyBackgroundGenerator(new Integer(200), new Integer(100));
        TwistedAndShearedRandomFontGenerator fontGenerator = new TwistedAndShearedRandomFontGenerator(new Integer(25), new Integer(30));
        ComposedWordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
        this.addFactory(new GimpyFactory(wordGenerator, wordToImage));
    }
}

