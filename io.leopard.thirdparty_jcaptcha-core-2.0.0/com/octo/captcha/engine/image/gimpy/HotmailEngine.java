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
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.RandomLinesGlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.HorizontalSpaceGlyphsVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.RotateGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.ShearGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;

public class HotmailEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        RandomWordGenerator dictionnaryWords = new RandomWordGenerator("ABCDEGHJKLMNRSTUWXY235689");
        GlyphsPaster randomPaster = new GlyphsPaster((Integer)8, (Integer)8, (ColorGenerator)new SingleColorGenerator(new Color(0, 0, 80)), new GlyphsVisitors[]{new TranslateGlyphsVerticalRandomVisitor(5.0), new RotateGlyphsRandomVisitor(0.09817477042468103), new ShearGlyphsRandomVisitor(0.2, 0.2), new HorizontalSpaceGlyphsVisitor(4), new TranslateAllToRandomPointVisitor()}, new GlyphsDecorator[]{new RandomLinesGlyphsDecorator(1.2, new SingleColorGenerator(new Color(0, 0, 80)), 2.0, 25.0), new RandomLinesGlyphsDecorator(1.0, new SingleColorGenerator(new Color(238, 238, 238)), 1.0, 25.0)});
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator((Integer)218, (Integer)48, new Color(238, 238, 238));
        RandomFontGenerator shearedFont = new RandomFontGenerator(30, 35, new Font[]{new Font("Caslon", 1, 30)}, false);
        SwimFilter swim = new SwimFilter();
        swim.setScale(30.0f);
        swim.setStretch(1.0f);
        swim.setTurbulence(1.0f);
        swim.setAmount(2.0f);
        swim.setTime(0.0f);
        swim.setEdgeAction(1);
        ArrayList<ImageDeformation> def = new ArrayList<ImageDeformation>();
        def.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swim));
        DeformedComposedWordToImage word2image = new DeformedComposedWordToImage(false, shearedFont, back, randomPaster, new ArrayList<ImageDeformation>(), new ArrayList<ImageDeformation>(), def);
        this.addFactory(new GimpyFactory(dictionnaryWords, word2image, false));
    }
}

