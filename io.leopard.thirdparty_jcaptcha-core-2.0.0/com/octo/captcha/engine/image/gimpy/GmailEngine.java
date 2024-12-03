/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jhlabs.image.PinchFilter
 */
package com.octo.captcha.engine.image.gimpy;

import com.jhlabs.image.PinchFilter;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;

public class GmailEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        ComposeDictionaryWordGenerator dictionnaryWords = new ComposeDictionaryWordGenerator(new FileDictionary("toddlist"));
        GlyphsPaster randomPaster = new GlyphsPaster((Integer)7, (Integer)7, (ColorGenerator)new RandomListColorGenerator(new Color[]{new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172)}), new GlyphsVisitors[]{new TranslateGlyphsVerticalRandomVisitor(1.0), new OverlapGlyphsUsingShapeVisitor(3.0), new TranslateAllToRandomPointVisitor()});
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator((Integer)200, (Integer)70, Color.white);
        RandomFontGenerator shearedFont = new RandomFontGenerator(50, 50, new Font[]{new Font("nyala", 1, 50), new Font("Bell MT", 0, 50), new Font("Credit valley", 1, 50)}, false);
        PinchFilter pinch = new PinchFilter();
        pinch.setAmount(-0.5f);
        pinch.setRadius(30.0f);
        pinch.setAngle(0.19634955f);
        pinch.setCentreX(0.5f);
        pinch.setCentreY(-0.01f);
        pinch.setEdgeAction(1);
        PinchFilter pinch2 = new PinchFilter();
        pinch2.setAmount(-0.6f);
        pinch2.setRadius(70.0f);
        pinch2.setAngle(0.19634955f);
        pinch2.setCentreX(0.3f);
        pinch2.setCentreY(1.01f);
        pinch2.setEdgeAction(1);
        PinchFilter pinch3 = new PinchFilter();
        pinch3.setAmount(-0.6f);
        pinch3.setRadius(70.0f);
        pinch3.setAngle(0.19634955f);
        pinch3.setCentreX(0.8f);
        pinch3.setCentreY(-0.01f);
        pinch3.setEdgeAction(1);
        ArrayList<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
        textDef.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch));
        textDef.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch2));
        textDef.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch3));
        DeformedComposedWordToImage word2image = new DeformedComposedWordToImage(false, shearedFont, back, randomPaster, new ArrayList<ImageDeformation>(), new ArrayList<ImageDeformation>(), textDef);
        this.addFactory(new GimpyFactory(dictionnaryWords, word2image, false));
    }
}

