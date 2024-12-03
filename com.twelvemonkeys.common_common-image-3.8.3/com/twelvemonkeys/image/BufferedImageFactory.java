/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.ImageConversionException;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.SubsamplingFilter;
import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BufferedImageFactory {
    private List<ProgressListener> listeners;
    private int percentageDone;
    private ImageProducer producer;
    private ImageConversionException consumerException;
    private volatile boolean fetching;
    private boolean readColorModelOnly;
    private int x = 0;
    private int y = 0;
    private int width = -1;
    private int height = -1;
    private int xSub = 1;
    private int ySub = 1;
    private int offset;
    private int scanSize;
    private ColorModel sourceColorModel;
    private Hashtable sourceProperties;
    private Object sourcePixels;
    private BufferedImage buffered;
    private ColorModel colorModel;
    private final Consumer consumer = new Consumer();

    public BufferedImageFactory(Image image) {
        this(image != null ? image.getSource() : null);
    }

    public BufferedImageFactory(ImageProducer imageProducer) {
        Validate.notNull((Object)imageProducer, (String)"source");
        this.producer = imageProducer;
    }

    public BufferedImage getBufferedImage() throws ImageConversionException {
        this.doFetch(false);
        return this.buffered;
    }

    public ColorModel getColorModel() throws ImageConversionException {
        this.doFetch(true);
        return this.buffered != null ? this.buffered.getColorModel() : this.colorModel;
    }

    public void dispose() {
        this.freeResources();
        this.buffered = null;
        this.colorModel = null;
    }

    public void abort() {
        this.consumer.imageComplete(4);
    }

    public void setSourceRegion(Rectangle rectangle) {
        if (this.x != rectangle.x || this.y != rectangle.y || this.width != rectangle.width || this.height != rectangle.height) {
            this.dispose();
        }
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public void setSourceSubsampling(int n, int n2) {
        if (this.xSub != n || this.ySub != n2) {
            this.dispose();
        }
        if (n > 1) {
            this.xSub = n;
        }
        if (n2 > 1) {
            this.ySub = n2;
        }
    }

    private synchronized void doFetch(boolean bl) throws ImageConversionException {
        if (!this.fetching && (!bl && this.buffered == null || this.buffered == null && this.sourceColorModel == null)) {
            if (!(bl || this.xSub <= 1 && this.ySub <= 1)) {
                if (this.width > 0 && this.height > 0) {
                    this.width = (this.width + this.xSub - 1) / this.xSub;
                    this.height = (this.height + this.ySub - 1) / this.ySub;
                    this.x = (this.x + this.xSub - 1) / this.xSub;
                    this.y = (this.y + this.ySub - 1) / this.ySub;
                }
                this.producer = new FilteredImageSource(this.producer, new SubsamplingFilter(this.xSub, this.ySub));
            }
            this.fetching = true;
            this.readColorModelOnly = bl;
            this.producer.startProduction(this.consumer);
            while (this.fetching) {
                try {
                    this.wait(200L);
                }
                catch (InterruptedException interruptedException) {
                    throw new ImageConversionException("Image conversion aborted: " + interruptedException.getMessage(), interruptedException);
                }
            }
            if (this.consumerException != null) {
                throw new ImageConversionException("Image conversion failed: " + this.consumerException.getMessage(), this.consumerException);
            }
            if (bl) {
                this.createColorModel();
            } else {
                this.createBuffered();
            }
        }
    }

    private void createColorModel() {
        this.colorModel = this.sourceColorModel;
        this.freeResources();
    }

    private void createBuffered() {
        if (this.width > 0 && this.height > 0) {
            if (this.sourceColorModel != null && this.sourcePixels != null) {
                WritableRaster writableRaster = ImageUtil.createRaster(this.width, this.height, this.sourcePixels, this.sourceColorModel);
                this.buffered = new BufferedImage(this.sourceColorModel, writableRaster, this.sourceColorModel.isAlphaPremultiplied(), this.sourceProperties);
            } else {
                this.buffered = ImageUtil.createClear(this.width, this.height, null);
            }
        }
        this.freeResources();
    }

    private void freeResources() {
        this.sourceColorModel = null;
        this.sourcePixels = null;
        this.sourceProperties = null;
    }

    private void processProgress(int n) {
        int n2;
        if (this.listeners != null && (n2 = 100 * n / this.height) > this.percentageDone) {
            this.percentageDone = n2;
            for (ProgressListener progressListener : this.listeners) {
                progressListener.progress(this, n2);
            }
        }
    }

    public void addProgressListener(ProgressListener progressListener) {
        if (progressListener == null) {
            return;
        }
        if (this.listeners == null) {
            this.listeners = new CopyOnWriteArrayList<ProgressListener>();
        }
        this.listeners.add(progressListener);
    }

    public void removeProgressListener(ProgressListener progressListener) {
        if (progressListener == null) {
            return;
        }
        if (this.listeners == null) {
            return;
        }
        this.listeners.remove(progressListener);
    }

    public void removeAllProgressListeners() {
        if (this.listeners != null) {
            this.listeners.clear();
        }
    }

    private static short[] toShortPixels(int[] nArray) {
        short[] sArray = new short[nArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = (short)(nArray[i] & 0xFFFF);
        }
        return sArray;
    }

    private class Consumer
    implements ImageConsumer {
        private Consumer() {
        }

        private void setPixelsImpl(int n, int n2, int n3, int n4, ColorModel colorModel, Object object, int n5, int n6) {
            int n7;
            this.setColorModelOnce(colorModel);
            if (object == null) {
                return;
            }
            if (BufferedImageFactory.this.sourcePixels == null) {
                BufferedImageFactory.this.sourcePixels = Array.newInstance(object.getClass().getComponentType(), BufferedImageFactory.this.width * BufferedImageFactory.this.height);
                BufferedImageFactory.this.scanSize = BufferedImageFactory.this.width;
                BufferedImageFactory.this.offset = 0;
            } else if (BufferedImageFactory.this.sourcePixels.getClass() != object.getClass()) {
                throw new IllegalStateException("Only one pixel type allowed");
            }
            if (n2 < BufferedImageFactory.this.y) {
                n7 = BufferedImageFactory.this.y - n2;
                if (n7 >= n4) {
                    return;
                }
                n5 += n6 * n7;
                n2 += n7;
                n4 -= n7;
            }
            if (n2 + n4 > BufferedImageFactory.this.y + BufferedImageFactory.this.height && (n4 = BufferedImageFactory.this.y + BufferedImageFactory.this.height - n2) <= 0) {
                return;
            }
            if (n < BufferedImageFactory.this.x) {
                n7 = BufferedImageFactory.this.x - n;
                if (n7 >= n3) {
                    return;
                }
                n5 += n7;
                n += n7;
                n3 -= n7;
            }
            if (n + n3 > BufferedImageFactory.this.x + BufferedImageFactory.this.width && (n3 = BufferedImageFactory.this.x + BufferedImageFactory.this.width - n) <= 0) {
                return;
            }
            n7 = BufferedImageFactory.this.offset + (n2 - BufferedImageFactory.this.y) * BufferedImageFactory.this.scanSize + (n - BufferedImageFactory.this.x);
            for (int i = n4; i > 0; --i) {
                System.arraycopy(object, n5, BufferedImageFactory.this.sourcePixels, n7, n3);
                n5 += n6;
                n7 += BufferedImageFactory.this.scanSize;
            }
            BufferedImageFactory.this.processProgress(n2 + n4);
        }

        public void setPixels(int n, int n2, int n3, int n4, ColorModel colorModel, short[] sArray, int n5, int n6) {
            this.setPixelsImpl(n, n2, n3, n4, colorModel, sArray, n5, n6);
        }

        private void setColorModelOnce(ColorModel colorModel) {
            if (BufferedImageFactory.this.sourceColorModel != colorModel) {
                if (BufferedImageFactory.this.sourcePixels == null) {
                    BufferedImageFactory.this.sourceColorModel = colorModel;
                } else {
                    throw new IllegalStateException("Change of ColorModel after pixel delivery not supported");
                }
            }
            if (BufferedImageFactory.this.readColorModelOnly) {
                BufferedImageFactory.this.consumer.imageComplete(4);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void imageComplete(int n) {
            BufferedImageFactory.this.fetching = false;
            if (BufferedImageFactory.this.producer != null) {
                BufferedImageFactory.this.producer.removeConsumer(this);
            }
            switch (n) {
                case 1: {
                    BufferedImageFactory.this.consumerException = new ImageConversionException("ImageConsumer.IMAGEERROR");
                }
            }
            BufferedImageFactory bufferedImageFactory = BufferedImageFactory.this;
            synchronized (bufferedImageFactory) {
                BufferedImageFactory.this.notifyAll();
            }
        }

        @Override
        public void setColorModel(ColorModel colorModel) {
            this.setColorModelOnce(colorModel);
        }

        @Override
        public void setDimensions(int n, int n2) {
            if (BufferedImageFactory.this.width < 0) {
                BufferedImageFactory.this.width = n - BufferedImageFactory.this.x;
            }
            if (BufferedImageFactory.this.height < 0) {
                BufferedImageFactory.this.height = n2 - BufferedImageFactory.this.y;
            }
            if (BufferedImageFactory.this.width <= 0 || BufferedImageFactory.this.height <= 0) {
                this.imageComplete(3);
            }
        }

        @Override
        public void setHints(int n) {
        }

        @Override
        public void setPixels(int n, int n2, int n3, int n4, ColorModel colorModel, byte[] byArray, int n5, int n6) {
            this.setPixelsImpl(n, n2, n3, n4, colorModel, byArray, n5, n6);
        }

        @Override
        public void setPixels(int n, int n2, int n3, int n4, ColorModel colorModel, int[] nArray, int n5, int n6) {
            if (colorModel.getTransferType() == 1) {
                this.setPixelsImpl(n, n2, n3, n4, colorModel, BufferedImageFactory.toShortPixels(nArray), n5, n6);
            } else {
                this.setPixelsImpl(n, n2, n3, n4, colorModel, nArray, n5, n6);
            }
        }

        public void setProperties(Hashtable hashtable) {
            BufferedImageFactory.this.sourceProperties = hashtable;
        }
    }

    public static interface ProgressListener
    extends EventListener {
        public void progress(BufferedImageFactory var1, float var2);
    }
}

