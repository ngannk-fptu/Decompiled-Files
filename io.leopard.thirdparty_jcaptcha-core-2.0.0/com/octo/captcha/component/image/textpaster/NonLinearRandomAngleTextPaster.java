/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.AbstractTextPaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class NonLinearRandomAngleTextPaster
extends AbstractTextPaster {
    private Map renderingHints = new HashMap();

    public NonLinearRandomAngleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
    }

    public NonLinearRandomAngleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
    }

    public NonLinearRandomAngleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2d = this.pasteBackgroundAndSetTextColor(out, background);
        g2d.setRenderingHints(this.renderingHints);
        g2d.translate(10, background.getHeight() / 2);
        AttributedCharacterIterator iterator = attributedWord.getIterator();
        while (iterator.getIndex() != iterator.getEndIndex()) {
            AttributedString character = new AttributedString(String.valueOf(iterator.current()));
            character.addAttribute(TextAttribute.FONT, iterator.getAttribute(TextAttribute.FONT));
            this.pasteCharacter(g2d, character);
            iterator.next();
        }
        g2d.dispose();
        return out;
    }

    protected void pasteCharacter(Graphics2D g2d, AttributedString character) {
        Font font = (Font)character.getIterator().getAttribute(TextAttribute.FONT);
        Rectangle2D rectangle = g2d.getFontMetrics(font).getStringBounds(String.valueOf(character.getIterator().current()), g2d);
        double angle = this.getRandomAngle();
        int maxTranslatedY = (int)g2d.getTransform().getTranslateY();
        double y = this.myRandom.nextBoolean() ? (double)this.myRandom.nextInt(maxTranslatedY) : (double)(-this.myRandom.nextInt(maxTranslatedY - (int)rectangle.getHeight()));
        g2d.setFont(font);
        g2d.translate(0.0, y);
        if (angle >= 1.5707963267948966 || angle <= -1.5707963267948966) {
            character.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
        }
        g2d.rotate(angle, rectangle.getX() + rectangle.getWidth() / 2.0, rectangle.getY() + rectangle.getHeight() / 2.0);
        g2d.drawString(character.getIterator(), 0, 0);
        g2d.rotate(-angle, rectangle.getX() + rectangle.getWidth() / 2.0, rectangle.getY() + rectangle.getHeight() / 2.0);
        g2d.translate(rectangle.getHeight(), -y);
    }

    protected double getRandomAngle() {
        double number = this.myRandom.nextDouble() * (double)this.myRandom.nextInt(10) + 1.0;
        double angle = Math.PI / number;
        return this.myRandom.nextBoolean() ? angle : -angle;
    }

    public void addRenderingHints(RenderingHints.Key key, Object value) {
        this.renderingHints.put(key, value);
    }
}

