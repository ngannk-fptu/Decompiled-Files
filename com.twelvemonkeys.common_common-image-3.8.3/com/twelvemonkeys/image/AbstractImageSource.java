/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractImageSource
implements ImageProducer {
    private List<ImageConsumer> consumers = new ArrayList<ImageConsumer>();
    protected int width;
    protected int height;
    protected int xOff;
    protected int yOff;

    @Override
    public void addConsumer(ImageConsumer imageConsumer) {
        block5: {
            if (this.consumers.contains(imageConsumer)) {
                return;
            }
            this.consumers.add(imageConsumer);
            try {
                this.initConsumer(imageConsumer);
                this.sendPixels(imageConsumer);
                if (this.isConsumer(imageConsumer)) {
                    imageConsumer.imageComplete(3);
                    if (this.isConsumer(imageConsumer)) {
                        imageConsumer.imageComplete(1);
                        this.removeConsumer(imageConsumer);
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                if (!this.isConsumer(imageConsumer)) break block5;
                imageConsumer.imageComplete(1);
            }
        }
    }

    @Override
    public void removeConsumer(ImageConsumer imageConsumer) {
        this.consumers.remove(imageConsumer);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer imageConsumer) {
    }

    @Override
    public void startProduction(ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer);
    }

    @Override
    public boolean isConsumer(ImageConsumer imageConsumer) {
        return this.consumers.contains(imageConsumer);
    }

    protected abstract void initConsumer(ImageConsumer var1);

    protected abstract void sendPixels(ImageConsumer var1);
}

