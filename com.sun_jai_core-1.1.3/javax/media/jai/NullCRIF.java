/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;

public class NullCRIF
extends CRIFImpl {
    private static RenderedImage sourcelessImage = null;

    public static final synchronized void setSourcelessImage(RenderedImage im) {
        sourcelessImage = im;
    }

    public static final synchronized RenderedImage getSourcelessImage() {
        return sourcelessImage;
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        return args.getNumSources() == 0 ? NullCRIF.getSourcelessImage() : args.getRenderedSource(0);
    }
}

