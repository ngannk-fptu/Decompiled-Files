/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jhlabs.image.EmbossFilter
 *  com.jhlabs.image.RippleFilter
 *  com.jhlabs.image.SphereFilter
 *  com.jhlabs.image.TwirlFilter
 *  com.jhlabs.image.WaterFilter
 *  com.jhlabs.image.WeaveFilter
 */
package com.octo.captcha.engine.image.gimpy;

import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.SphereFilter;
import com.jhlabs.image.TwirlFilter;
import com.jhlabs.image.WaterFilter;
import com.jhlabs.image.WeaveFilter;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.word.wordgenerator.DictionaryWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.image.ImageFilter;

public class DeformedBaffleListGimpyEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        EmbossFilter emboss = new EmbossFilter();
        SphereFilter sphere = new SphereFilter();
        RippleFilter rippleBack = new RippleFilter();
        RippleFilter ripple = new RippleFilter();
        TwirlFilter twirl = new TwirlFilter();
        WaterFilter water = new WaterFilter();
        WeaveFilter weaves = new WeaveFilter();
        ripple.setWaveType(3);
        ripple.setXAmplitude(3.0f);
        ripple.setYAmplitude(3.0f);
        ripple.setXWavelength(20.0f);
        ripple.setYWavelength(10.0f);
        ripple.setEdgeAction(1);
        rippleBack.setWaveType(3);
        rippleBack.setXAmplitude(5.0f);
        rippleBack.setYAmplitude(5.0f);
        rippleBack.setXWavelength(10.0f);
        rippleBack.setYWavelength(10.0f);
        rippleBack.setEdgeAction(1);
        water.setAmplitude(1.0f);
        water.setWavelength(20.0f);
        twirl.setAngle(0.0f);
        sphere.setRefractionIndex(1.0f);
        weaves.setUseImageColors(true);
        ImageDeformationByFilters rippleDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters waterDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters embossDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters rippleDefBack = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters weavesDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters none = new ImageDeformationByFilters(null);
        DictionaryWordGenerator words = new DictionaryWordGenerator(new FileDictionary("toddlist"));
        DecoratedRandomTextPaster paster = new DecoratedRandomTextPaster(new Integer(6), new Integer(7), (ColorGenerator)new SingleColorGenerator(Color.black), new TextDecorator[]{new BaffleTextDecorator(new Integer(1), Color.white)});
        UniColorBackgroundGenerator back = new UniColorBackgroundGenerator(new Integer(200), new Integer(100), Color.white);
        TwistedAndShearedRandomFontGenerator font = new TwistedAndShearedRandomFontGenerator(new Integer(30), new Integer(40));
        ComposedWordToImage word2image = new ComposedWordToImage(font, back, paster);
        this.addFactory(new GimpyFactory(words, word2image));
        word2image = new DeformedComposedWordToImage((FontGenerator)font, (BackgroundGenerator)back, (TextPaster)paster, rippleDef, waterDef, embossDef);
        this.addFactory(new GimpyFactory(words, word2image));
        word2image = new DeformedComposedWordToImage((FontGenerator)font, (BackgroundGenerator)back, (TextPaster)paster, rippleDefBack, null, rippleDef);
        this.addFactory(new GimpyFactory(words, word2image));
        word2image = new DeformedComposedWordToImage((FontGenerator)font, (BackgroundGenerator)back, (TextPaster)paster, rippleDefBack, none, weavesDef);
        this.addFactory(new GimpyFactory(words, word2image));
    }
}

