/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

public class DeformedComposedWordToImage
extends ComposedWordToImage {
    private List<ImageDeformation> backgroundDeformations = new ArrayList<ImageDeformation>();
    private List<ImageDeformation> textDeformations = new ArrayList<ImageDeformation>();
    private List<ImageDeformation> finalDeformations = new ArrayList<ImageDeformation>();

    public DeformedComposedWordToImage(FontGenerator fontGenerator, BackgroundGenerator background, TextPaster textPaster, ImageDeformation backgroundDeformation, ImageDeformation textDeformation, ImageDeformation finalDeformation) {
        super(fontGenerator, background, textPaster);
        if (backgroundDeformation != null) {
            this.backgroundDeformations.add(backgroundDeformation);
        }
        if (textDeformation != null) {
            this.textDeformations.add(textDeformation);
        }
        if (finalDeformation != null) {
            this.finalDeformations.add(finalDeformation);
        }
    }

    public DeformedComposedWordToImage(FontGenerator fontGenerator, BackgroundGenerator background, TextPaster textPaster, List<ImageDeformation> backgroundDeformations, List<ImageDeformation> textDeformations, List<ImageDeformation> finalDeformations) {
        super(fontGenerator, background, textPaster);
        this.backgroundDeformations = backgroundDeformations;
        this.textDeformations = textDeformations;
        this.finalDeformations = finalDeformations;
    }

    public DeformedComposedWordToImage(boolean manageFontByCharacter, FontGenerator fontGenerator, BackgroundGenerator background, TextPaster textPaster, List<ImageDeformation> backgroundDeformations, List<ImageDeformation> textDeformations, List<ImageDeformation> finalDeformations) {
        super(manageFontByCharacter, fontGenerator, background, textPaster);
        this.backgroundDeformations = backgroundDeformations;
        this.textDeformations = textDeformations;
        this.finalDeformations = finalDeformations;
    }

    @Override
    public BufferedImage getImage(String word) throws CaptchaException {
        BufferedImage background = this.getBackground();
        AttributedString aword = this.getAttributedString(word, this.checkWordLength(word));
        BufferedImage out = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        Graphics2D g2 = (Graphics2D)out.getGraphics();
        g2.drawImage(background, 0, 0, out.getWidth(), out.getHeight(), null);
        g2.dispose();
        for (ImageDeformation deformation : this.backgroundDeformations) {
            out = deformation.deformImage(out);
        }
        BufferedImage transparent = new BufferedImage(out.getWidth(), out.getHeight(), 2);
        transparent = this.pasteText(transparent, aword);
        for (ImageDeformation deformation : this.textDeformations) {
            transparent = deformation.deformImage(transparent);
        }
        Graphics2D g3 = (Graphics2D)out.getGraphics();
        g3.drawImage((Image)transparent, 0, 0, null);
        g3.dispose();
        for (ImageDeformation deformation : this.finalDeformations) {
            out = deformation.deformImage(out);
        }
        return out;
    }
}

