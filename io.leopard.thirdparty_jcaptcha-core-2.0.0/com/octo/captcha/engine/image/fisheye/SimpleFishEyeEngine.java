/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jhlabs.image.RippleFilter
 *  com.jhlabs.image.SphereFilter
 *  com.jhlabs.image.TwirlFilter
 *  com.jhlabs.image.WaterFilter
 */
package com.octo.captcha.engine.image.fisheye;

import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.SphereFilter;
import com.jhlabs.image.TwirlFilter;
import com.jhlabs.image.WaterFilter;
import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.fisheye.FishEyeFactory;
import java.awt.image.ImageFilter;

public class SimpleFishEyeEngine
extends ListImageCaptchaEngine {
    @Override
    protected void buildInitialFactories() {
        SphereFilter sphere = new SphereFilter();
        RippleFilter ripple = new RippleFilter();
        TwirlFilter twirl = new TwirlFilter();
        WaterFilter water = new WaterFilter();
        ripple.setWaveType(3);
        ripple.setXAmplitude(10.0f);
        ripple.setYAmplitude(10.0f);
        ripple.setXWavelength(10.0f);
        ripple.setYWavelength(10.0f);
        ripple.setEdgeAction(1);
        water.setAmplitude(10.0f);
        water.setWavelength(20.0f);
        twirl.setAngle(4.0f);
        sphere.setRefractionIndex(2.0f);
        ImageDeformationByFilters rippleDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters sphereDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters waterDef = new ImageDeformationByFilters(new ImageFilter[0]);
        ImageDeformationByFilters twirlDef = new ImageDeformationByFilters(new ImageFilter[0]);
        FileReaderRandomBackgroundGenerator generator = new FileReaderRandomBackgroundGenerator(new Integer(250), new Integer(250), "./fisheyebackgrounds");
        this.addFactory(new FishEyeFactory(generator, sphereDef, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(generator, rippleDef, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(generator, waterDef, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(generator, twirlDef, new Integer(10), new Integer(5)));
    }
}

