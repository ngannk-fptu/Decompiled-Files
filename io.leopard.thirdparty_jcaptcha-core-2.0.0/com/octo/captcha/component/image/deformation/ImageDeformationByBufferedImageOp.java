/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.deformation;

import com.octo.captcha.component.image.deformation.ImageDeformation;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.List;

public class ImageDeformationByBufferedImageOp
implements ImageDeformation {
    private List<BufferedImageOp> ImageOperations = new ArrayList<BufferedImageOp>();

    public void setImageOperations(List<BufferedImageOp> imageOperations) {
        this.ImageOperations = imageOperations;
    }

    public ImageDeformationByBufferedImageOp(List<BufferedImageOp> imageOperations) {
        this.ImageOperations = imageOperations;
    }

    public ImageDeformationByBufferedImageOp(BufferedImageOp imageOperation) {
        this.ImageOperations.add(imageOperation);
    }

    @Override
    public BufferedImage deformImage(BufferedImage image) {
        for (BufferedImageOp operation : this.ImageOperations) {
            image = operation.filter(image, null);
        }
        return image;
    }
}

