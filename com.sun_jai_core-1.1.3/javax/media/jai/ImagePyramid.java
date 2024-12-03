/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.RenderedImage;
import java.util.Vector;
import javax.media.jai.ImageMIPMap;
import javax.media.jai.JaiI18N;
import javax.media.jai.RenderedOp;

public class ImagePyramid
extends ImageMIPMap {
    protected RenderedOp upSampler;
    protected RenderedOp differencer;
    protected RenderedOp combiner;
    private Vector diffImages = new Vector();

    protected ImagePyramid() {
    }

    public ImagePyramid(RenderedImage image, RenderedOp downSampler, RenderedOp upSampler, RenderedOp differencer, RenderedOp combiner) {
        super(image, downSampler);
        if (upSampler == null || differencer == null || combiner == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.upSampler = upSampler;
        this.differencer = differencer;
        this.combiner = combiner;
    }

    public ImagePyramid(RenderedOp downSampler, RenderedOp upSampler, RenderedOp differencer, RenderedOp combiner) {
        super(downSampler);
        if (upSampler == null || differencer == null || combiner == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.upSampler = upSampler;
        this.differencer = differencer;
        this.combiner = combiner;
    }

    public RenderedImage getImage(int level) {
        if (level < 0) {
            return null;
        }
        while (this.currentLevel < level) {
            this.getDownImage();
        }
        while (this.currentLevel > level) {
            this.getUpImage();
        }
        return this.currentImage;
    }

    public RenderedImage getDownImage() {
        ++this.currentLevel;
        RenderedOp downOp = this.duplicate(this.downSampler, this.vectorize(this.currentImage));
        RenderedOp upOp = this.duplicate(this.upSampler, this.vectorize(downOp.getRendering()));
        RenderedOp diffOp = this.duplicate(this.differencer, this.vectorize(this.currentImage, upOp.getRendering()));
        this.diffImages.add(diffOp.getRendering());
        this.currentImage = downOp.getRendering();
        return this.currentImage;
    }

    public RenderedImage getUpImage() {
        if (this.currentLevel > 0) {
            --this.currentLevel;
            RenderedOp upOp = this.duplicate(this.upSampler, this.vectorize(this.currentImage));
            RenderedImage diffImage = (RenderedImage)this.diffImages.elementAt(this.currentLevel);
            this.diffImages.removeElementAt(this.currentLevel);
            RenderedOp combOp = this.duplicate(this.combiner, this.vectorize(upOp.getRendering(), diffImage));
            this.currentImage = combOp.getRendering();
        }
        return this.currentImage;
    }

    public RenderedImage getDiffImage() {
        RenderedOp downOp = this.duplicate(this.downSampler, this.vectorize(this.currentImage));
        RenderedOp upOp = this.duplicate(this.upSampler, this.vectorize(downOp.getRendering()));
        RenderedOp diffOp = this.duplicate(this.differencer, this.vectorize(this.currentImage, upOp.getRendering()));
        return diffOp.getRendering();
    }
}

