/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jhlabs.image.WeaveFilter
 *  com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator
 *  com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator
 *  com.octo.captcha.component.image.color.ColorGenerator
 *  com.octo.captcha.component.image.color.SingleColorGenerator
 *  com.octo.captcha.component.image.deformation.ImageDeformation
 *  com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp
 *  com.octo.captcha.component.image.deformation.ImageDeformationByFilters
 *  com.octo.captcha.component.image.fontgenerator.FontGenerator
 *  com.octo.captcha.component.image.fontgenerator.RandomFontGenerator
 *  com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster
 *  com.octo.captcha.component.image.textpaster.TextPaster
 *  com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage
 *  com.octo.captcha.component.image.wordtoimage.WordToImage
 *  com.octo.captcha.component.word.DictionaryReader
 *  com.octo.captcha.component.word.FileDictionary
 *  com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator
 *  com.octo.captcha.component.word.wordgenerator.WordGenerator
 *  com.octo.captcha.engine.image.ListImageCaptchaEngine
 *  com.octo.captcha.image.ImageCaptchaFactory
 *  com.octo.captcha.image.gimpy.GimpyFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.internal.security.captcha.CensoringWordGenerator;
import com.jhlabs.image.WeaveFilter;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageFilter;

public class DistortedCaptchaEngine
extends ListImageCaptchaEngine {
    public static final Color BACKGROUND_COLOR = Color.white;

    protected void buildInitialFactories() {
        WeaveFilter weave = new WeaveFilter();
        ImageDeformationByFilters backDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters textDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByBufferedImageOp postDef = new ImageDeformationByBufferedImageOp((BufferedImageOp)weave);
        CensoringWordGenerator dictionaryWords = new CensoringWordGenerator((WordGenerator)new ComposeDictionaryWordGenerator((DictionaryReader)new FileDictionary("toddlist")));
        DecoratedRandomTextPaster randomPaster = new DecoratedRandomTextPaster(Integer.valueOf(6), Integer.valueOf(7), (ColorGenerator)new SingleColorGenerator(Color.black), null);
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator(Integer.valueOf(200), Integer.valueOf(50), BACKGROUND_COLOR);
        Font[] fonts = new Font[]{Font.decode("Serif"), Font.decode("SansSerif"), Font.decode("Monospaced"), Font.decode("Dialog"), Font.decode("DialogInput")};
        RandomFontGenerator shearedFont = new RandomFontGenerator(Integer.valueOf(30), Integer.valueOf(35), fonts);
        DeformedComposedWordToImage word2image = new DeformedComposedWordToImage((FontGenerator)shearedFont, (BackgroundGenerator)back, (TextPaster)randomPaster, (ImageDeformation)backDef, (ImageDeformation)textDef, (ImageDeformation)postDef);
        this.addFactory((ImageCaptchaFactory)new GimpyFactory((WordGenerator)dictionaryWords, (WordToImage)word2image));
    }
}

