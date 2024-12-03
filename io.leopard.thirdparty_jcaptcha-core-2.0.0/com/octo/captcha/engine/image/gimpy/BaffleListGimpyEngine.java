/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;

public class BaffleListGimpyEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        ComposeDictionaryWordGenerator dictionnaryWords = new ComposeDictionaryWordGenerator(new FileDictionary("toddlist"));
        DecoratedRandomTextPaster randomPaster = new DecoratedRandomTextPaster(new Integer(8), new Integer(15), (ColorGenerator)new SingleColorGenerator(Color.BLACK), new TextDecorator[]{new BaffleTextDecorator((Integer)2, Color.black)});
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator(new Integer(200), new Integer(100), Color.white);
        RandomFontGenerator shearedFont = new RandomFontGenerator(new Integer(20), new Integer(25));
        ComposedWordToImage word2image = new ComposedWordToImage(shearedFont, back, randomPaster);
        this.addFactory(new GimpyFactory(dictionnaryWords, word2image));
    }
}

