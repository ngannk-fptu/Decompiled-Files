/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jhlabs.image.SwimFilter
 */
package com.octo.captcha.engine.image.gimpy;

import com.jhlabs.image.SwimFilter;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;

public class HotmailEngine2008
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        RandomWordGenerator dictionnaryWords = new RandomWordGenerator("ABCDEGHJKLMNRSTUWXY235689");
        GlyphsPaster randomPaster = new GlyphsPaster((Integer)8, (Integer)8, (ColorGenerator)new SingleColorGenerator(new Color(0, 0, 80)), new GlyphsVisitors[]{new OverlapGlyphsUsingShapeVisitor(3.0), new TranslateAllToRandomPointVisitor(20.0, 20.0)});
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator((Integer)218, (Integer)48, new Color(238, 238, 238));
        RandomFontGenerator shearedFont = new RandomFontGenerator(30, 35, new Font[]{new Font("Caslon", 1, 30)}, false);
        SwimFilter swim = new SwimFilter();
        swim.setScale(30.0f);
        swim.setAmount(10.0f);
        swim.setEdgeAction(1);
        SwimFilter swim2 = new SwimFilter();
        swim2.setScale(30.0f);
        swim2.setAmount(10.0f);
        swim2.setTime(90.0f);
        swim2.setEdgeAction(1);
        ArrayList<ImageDeformation> def = new ArrayList<ImageDeformation>();
        def.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swim));
        def.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swim2));
        DeformedComposedWordToImage word2image = new DeformedComposedWordToImage(false, shearedFont, back, randomPaster, new ArrayList<ImageDeformation>(), def, new ArrayList<ImageDeformation>());
        this.addFactory(new GimpyFactory(dictionnaryWords, word2image, false));
    }
}

