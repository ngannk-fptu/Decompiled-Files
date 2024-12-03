/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.deformation;

import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.utils.ToolkitFactory;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;

public class ImageDeformationByFilters
implements ImageDeformation {
    private ImageFilter[] filters;

    public ImageDeformationByFilters(ImageFilter[] filters) {
        this.filters = filters;
    }

    @Override
    public BufferedImage deformImage(BufferedImage image) {
        if (this.filters != null && this.filters.length > 0) {
            BufferedImage clone = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Image temp = null;
            for (int i = 0; i < this.filters.length; ++i) {
                ImageFilter filter = this.filters[i];
                FilteredImageSource filtered = new FilteredImageSource(image.getSource(), filter);
                temp = ToolkitFactory.getToolkit().createImage(filtered);
            }
            clone.getGraphics().drawImage(temp, 0, 0, null);
            clone.getGraphics().dispose();
            return clone;
        }
        return image;
    }
}

