/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.JaiI18N;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class ImageLayout
implements Cloneable,
Serializable {
    public static final int MIN_X_MASK = 1;
    public static final int MIN_Y_MASK = 2;
    public static final int WIDTH_MASK = 4;
    public static final int HEIGHT_MASK = 8;
    public static final int TILE_GRID_X_OFFSET_MASK = 16;
    public static final int TILE_GRID_Y_OFFSET_MASK = 32;
    public static final int TILE_WIDTH_MASK = 64;
    public static final int TILE_HEIGHT_MASK = 128;
    public static final int SAMPLE_MODEL_MASK = 256;
    public static final int COLOR_MODEL_MASK = 512;
    int minX = 0;
    int minY = 0;
    int width = 0;
    int height = 0;
    int tileGridXOffset = 0;
    int tileGridYOffset = 0;
    int tileWidth = 0;
    int tileHeight = 0;
    transient SampleModel sampleModel = null;
    transient ColorModel colorModel = null;
    protected int validMask = 0;
    static /* synthetic */ Class class$java$awt$image$SampleModel;
    static /* synthetic */ Class class$java$awt$image$ColorModel;

    public ImageLayout() {
    }

    public ImageLayout(int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, int tileWidth, int tileHeight, SampleModel sampleModel, ColorModel colorModel) {
        this.setMinX(minX);
        this.setMinY(minY);
        this.setWidth(width);
        this.setHeight(height);
        this.setTileGridXOffset(tileGridXOffset);
        this.setTileGridYOffset(tileGridYOffset);
        this.setTileWidth(tileWidth);
        this.setTileHeight(tileHeight);
        if (sampleModel != null) {
            this.setSampleModel(sampleModel);
        }
        if (colorModel != null) {
            this.setColorModel(colorModel);
        }
    }

    public ImageLayout(int minX, int minY, int width, int height) {
        this.setMinX(minX);
        this.setMinY(minY);
        this.setWidth(width);
        this.setHeight(height);
    }

    public ImageLayout(int tileGridXOffset, int tileGridYOffset, int tileWidth, int tileHeight, SampleModel sampleModel, ColorModel colorModel) {
        this.setTileGridXOffset(tileGridXOffset);
        this.setTileGridYOffset(tileGridYOffset);
        this.setTileWidth(tileWidth);
        this.setTileHeight(tileHeight);
        if (sampleModel != null) {
            this.setSampleModel(sampleModel);
        }
        if (colorModel != null) {
            this.setColorModel(colorModel);
        }
    }

    public ImageLayout(RenderedImage im) {
        this(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight(), im.getTileGridXOffset(), im.getTileGridYOffset(), im.getTileWidth(), im.getTileHeight(), im.getSampleModel(), im.getColorModel());
    }

    public int getValidMask() {
        return this.validMask;
    }

    public final boolean isValid(int mask) {
        return (this.validMask & mask) == mask;
    }

    public ImageLayout setValid(int mask) {
        this.validMask |= mask;
        return this;
    }

    public ImageLayout unsetValid(int mask) {
        this.validMask &= ~mask;
        return this;
    }

    public ImageLayout unsetImageBounds() {
        this.unsetValid(15);
        return this;
    }

    public ImageLayout unsetTileLayout() {
        this.unsetValid(240);
        return this;
    }

    public int getMinX(RenderedImage fallback) {
        if (this.isValid(1)) {
            return this.minX;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getMinX();
    }

    public ImageLayout setMinX(int minX) {
        this.minX = minX;
        this.setValid(1);
        return this;
    }

    public int getMinY(RenderedImage fallback) {
        if (this.isValid(2)) {
            return this.minY;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getMinY();
    }

    public ImageLayout setMinY(int minY) {
        this.minY = minY;
        this.setValid(2);
        return this;
    }

    public int getWidth(RenderedImage fallback) {
        if (this.isValid(4)) {
            return this.width;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getWidth();
    }

    public ImageLayout setWidth(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
        }
        this.width = width;
        this.setValid(4);
        return this;
    }

    public int getHeight(RenderedImage fallback) {
        if (this.isValid(8)) {
            return this.height;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getHeight();
    }

    public ImageLayout setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
        }
        this.height = height;
        this.setValid(8);
        return this;
    }

    public int getTileGridXOffset(RenderedImage fallback) {
        if (this.isValid(16)) {
            return this.tileGridXOffset;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getTileGridXOffset();
    }

    public ImageLayout setTileGridXOffset(int tileGridXOffset) {
        this.tileGridXOffset = tileGridXOffset;
        this.setValid(16);
        return this;
    }

    public int getTileGridYOffset(RenderedImage fallback) {
        if (this.isValid(32)) {
            return this.tileGridYOffset;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getTileGridYOffset();
    }

    public ImageLayout setTileGridYOffset(int tileGridYOffset) {
        this.tileGridYOffset = tileGridYOffset;
        this.setValid(32);
        return this;
    }

    public int getTileWidth(RenderedImage fallback) {
        if (this.isValid(64)) {
            return this.tileWidth;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getTileWidth();
    }

    public ImageLayout setTileWidth(int tileWidth) {
        if (tileWidth <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
        }
        this.tileWidth = tileWidth;
        this.setValid(64);
        return this;
    }

    public int getTileHeight(RenderedImage fallback) {
        if (this.isValid(128)) {
            return this.tileHeight;
        }
        if (fallback == null) {
            return 0;
        }
        return fallback.getTileHeight();
    }

    public ImageLayout setTileHeight(int tileHeight) {
        if (tileHeight <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("ImageLayout0"));
        }
        this.tileHeight = tileHeight;
        this.setValid(128);
        return this;
    }

    public SampleModel getSampleModel(RenderedImage fallback) {
        if (this.isValid(256)) {
            return this.sampleModel;
        }
        if (fallback == null) {
            return null;
        }
        return fallback.getSampleModel();
    }

    public ImageLayout setSampleModel(SampleModel sampleModel) {
        this.sampleModel = sampleModel;
        this.setValid(256);
        return this;
    }

    public ColorModel getColorModel(RenderedImage fallback) {
        if (this.isValid(512)) {
            return this.colorModel;
        }
        if (fallback == null) {
            return null;
        }
        return fallback.getColorModel();
    }

    public ImageLayout setColorModel(ColorModel colorModel) {
        this.colorModel = colorModel;
        this.setValid(512);
        return this;
    }

    public String toString() {
        String s = "ImageLayout[";
        boolean first = true;
        if (this.isValid(1)) {
            s = s + "MIN_X=" + this.minX;
            first = false;
        }
        if (this.isValid(2)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "MIN_Y=" + this.minY;
            first = false;
        }
        if (this.isValid(4)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "WIDTH=" + this.width;
            first = false;
        }
        if (this.isValid(8)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "HEIGHT=" + this.height;
            first = false;
        }
        if (this.isValid(16)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "TILE_GRID_X_OFFSET=" + this.tileGridXOffset;
            first = false;
        }
        if (this.isValid(32)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "TILE_GRID_Y_OFFSET=" + this.tileGridYOffset;
            first = false;
        }
        if (this.isValid(64)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "TILE_WIDTH=" + this.tileWidth;
            first = false;
        }
        if (this.isValid(128)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "TILE_HEIGHT=" + this.tileHeight;
            first = false;
        }
        if (this.isValid(256)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "SAMPLE_MODEL=" + this.sampleModel;
            first = false;
        }
        if (this.isValid(512)) {
            if (!first) {
                s = s + ", ";
            }
            s = s + "COLOR_MODEL=" + this.colorModel;
        }
        s = s + "]";
        return s;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.isValid(256)) {
            out.writeObject(SerializerFactory.getState(this.sampleModel, null));
        }
        if (this.isValid(512)) {
            out.writeObject(SerializerFactory.getState(this.colorModel, null));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Class c;
        SerializableState ss;
        Object object;
        in.defaultReadObject();
        if (this.isValid(256)) {
            object = in.readObject();
            if (!(object instanceof SerializableState)) {
                this.sampleModel = null;
            }
            ss = (SerializableState)object;
            c = ss.getObjectClass();
            this.sampleModel = (class$java$awt$image$SampleModel == null ? (class$java$awt$image$SampleModel = ImageLayout.class$("java.awt.image.SampleModel")) : class$java$awt$image$SampleModel).isAssignableFrom(c) ? (SampleModel)ss.getObject() : null;
        }
        if (this.isValid(512)) {
            object = in.readObject();
            if (!(object instanceof SerializableState)) {
                this.colorModel = null;
            }
            ss = (SerializableState)object;
            c = ss.getObjectClass();
            this.colorModel = (class$java$awt$image$ColorModel == null ? (class$java$awt$image$ColorModel = ImageLayout.class$("java.awt.image.ColorModel")) : class$java$awt$image$ColorModel).isAssignableFrom(c) ? (ColorModel)ss.getObject() : null;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImageLayout)) {
            return false;
        }
        ImageLayout il = (ImageLayout)obj;
        return this.validMask == il.validMask && this.width == il.width && this.height == il.height && this.minX == il.minX && this.minY == il.minY && this.tileHeight == il.tileHeight && this.tileWidth == il.tileWidth && this.tileGridXOffset == il.tileGridXOffset && this.tileGridYOffset == il.tileGridYOffset && this.sampleModel.equals(il.sampleModel) && this.colorModel.equals(il.colorModel);
    }

    public int hashCode() {
        int code = 0;
        int i = 1;
        code += this.width * i++;
        code += this.height * i++;
        code += this.minX * i++;
        code += this.minY * i++;
        code += this.tileHeight * i++;
        code += this.tileWidth * i++;
        code += this.tileGridXOffset * i++;
        code += this.tileGridYOffset * i++;
        code ^= this.sampleModel.hashCode();
        code ^= this.validMask;
        return code ^= this.colorModel.hashCode();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

