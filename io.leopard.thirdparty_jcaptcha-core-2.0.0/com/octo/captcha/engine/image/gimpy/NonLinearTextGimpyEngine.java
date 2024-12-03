/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.fontgenerator.DeformedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.NonLinearTextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;

public class NonLinearTextGimpyEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        RandomWordGenerator wordGenerator = new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        NonLinearTextPaster textPaster = new NonLinearTextPaster(new Integer(5), new Integer(7), new RandomListColorGenerator(new Color[]{Color.BLACK, Color.YELLOW, Color.WHITE}), Boolean.TRUE);
        GradientBackgroundGenerator backgroundGenerator = new GradientBackgroundGenerator(new Integer(200), new Integer(100), Color.CYAN, Color.GRAY);
        DeformedRandomFontGenerator fontGenerator = new DeformedRandomFontGenerator(new Integer(25), new Integer(30));
        ComposedWordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
        this.addFactory(new GimpyFactory(wordGenerator, wordToImage));
    }
}

