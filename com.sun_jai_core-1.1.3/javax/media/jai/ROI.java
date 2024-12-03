/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class ROI
implements Serializable {
    private transient RandomIter iter = null;
    transient PlanarImage theImage = null;
    int threshold = 127;

    protected static LinkedList mergeRunLengthList(LinkedList rectList) {
        if (rectList == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (rectList.size() > 1) {
            block0: for (int mergeIndex = 0; mergeIndex < rectList.size() - 1; ++mergeIndex) {
                ListIterator rectIter = rectList.listIterator(mergeIndex);
                Rectangle mergeRect = (Rectangle)rectIter.next();
                while (rectIter.hasNext()) {
                    Rectangle runRect = (Rectangle)rectIter.next();
                    int abuttingY = mergeRect.y + mergeRect.height;
                    if (runRect.y == abuttingY && runRect.x == mergeRect.x && runRect.width == mergeRect.width) {
                        mergeRect = new Rectangle(mergeRect.x, mergeRect.y, mergeRect.width, mergeRect.height + runRect.height);
                        rectIter.remove();
                        rectList.set(mergeIndex, mergeRect);
                        continue;
                    }
                    if (runRect.y <= abuttingY) continue;
                    continue block0;
                }
            }
        }
        return rectList;
    }

    protected ROI() {
    }

    public ROI(RenderedImage im) {
        this(im, 127);
    }

    public ROI(RenderedImage im, int threshold) {
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        SampleModel sm = im.getSampleModel();
        if (sm.getNumBands() != 1) {
            throw new IllegalArgumentException(JaiI18N.getString("ROI0"));
        }
        this.threshold = threshold;
        if (threshold >= 1 && ImageUtil.isBinary(sm)) {
            this.theImage = PlanarImage.wrapRenderedImage(im);
        } else {
            ParameterBlockJAI pbj = new ParameterBlockJAI("binarize");
            pbj.setSource("source0", im);
            pbj.setParameter("threshold", (double)threshold);
            this.theImage = JAI.create("binarize", pbj, null);
        }
    }

    private RandomIter getIter() {
        if (this.iter == null) {
            this.iter = RandomIterFactory.create(this.theImage, null);
        }
        return this.iter;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        ((RenderedOp)this.theImage).setParameter((double)threshold, 0);
        this.iter = null;
        this.getIter();
    }

    public Rectangle getBounds() {
        return new Rectangle(this.theImage.getMinX(), this.theImage.getMinY(), this.theImage.getWidth(), this.theImage.getHeight());
    }

    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Float(this.theImage.getMinX(), this.theImage.getMinY(), this.theImage.getWidth(), this.theImage.getHeight());
    }

    public boolean contains(Point p) {
        if (p == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.contains(p.x, p.y);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.contains((int)p.getX(), (int)p.getY());
    }

    public boolean contains(int x, int y) {
        int minX = this.theImage.getMinX();
        int minY = this.theImage.getMinY();
        return x >= minX && x < minX + this.theImage.getWidth() && y >= minY && y < minY + this.theImage.getHeight() && this.getIter().getSample(x, y, 0) >= 1;
    }

    public boolean contains(double x, double y) {
        return this.contains((int)x, (int)y);
    }

    public boolean contains(Rectangle rect) {
        if (rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!rect.equals(rect.intersection(this.getBounds()))) {
            return false;
        }
        byte[] packedData = ImageUtil.getPackedBinaryData(this.theImage.getData(), rect);
        int leftover = rect.width % 8;
        if (leftover == 0) {
            for (int i = 0; i < packedData.length; ++i) {
                if ((packedData[i] & 0xFF) == 255) continue;
                return false;
            }
        } else {
            int mask = (1 << leftover) - 1 << 8 - leftover;
            int k = 0;
            for (int y = 0; y < rect.height; ++y) {
                int x = 0;
                while (x < rect.width - leftover) {
                    if ((packedData[k] & 0xFF) != 255) {
                        return false;
                    }
                    x += 8;
                    ++k;
                }
                if ((packedData[k] & mask) != mask) {
                    return false;
                }
                ++k;
            }
        }
        return true;
    }

    public boolean contains(Rectangle2D rect) {
        if (rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Rectangle r = new Rectangle((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
        return this.contains(r);
    }

    public boolean contains(int x, int y, int w, int h) {
        Rectangle r = new Rectangle(x, y, w, h);
        return this.contains(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        Rectangle rect = new Rectangle((int)x, (int)y, (int)w, (int)h);
        return this.contains(rect);
    }

    public boolean intersects(Rectangle rect) {
        if (rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Rectangle r = rect.intersection(this.getBounds());
        if (r.isEmpty()) {
            return false;
        }
        byte[] packedData = ImageUtil.getPackedBinaryData(this.theImage.getData(), r);
        int leftover = r.width % 8;
        if (leftover == 0) {
            for (int i = 0; i < packedData.length; ++i) {
                if ((packedData[i] & 0xFF) == 0) continue;
                return true;
            }
        } else {
            int mask = (1 << leftover) - 1 << 8 - leftover;
            int k = 0;
            for (int y = 0; y < r.height; ++y) {
                int x = 0;
                while (x < r.width - leftover) {
                    if ((packedData[k] & 0xFF) != 0) {
                        return true;
                    }
                    x += 8;
                    ++k;
                }
                if ((packedData[k] & mask) != 0) {
                    return true;
                }
                ++k;
            }
        }
        return false;
    }

    public boolean intersects(Rectangle2D r) {
        if (r == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Rectangle rect = new Rectangle((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
        return this.intersects(rect);
    }

    public boolean intersects(int x, int y, int w, int h) {
        Rectangle rect = new Rectangle(x, y, w, h);
        return this.intersects(rect);
    }

    public boolean intersects(double x, double y, double w, double h) {
        Rectangle rect = new Rectangle((int)x, (int)y, (int)w, (int)h);
        return this.intersects(rect);
    }

    private static PlanarImage createBinaryImage(Rectangle r) {
        if (r.x == 0 && r.y == 0) {
            BufferedImage bi = new BufferedImage(r.width, r.height, 12);
            return PlanarImage.wrapRenderedImage(bi);
        }
        MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel(0, r.width, r.height, 1);
        return new TiledImage(r.x, r.y, r.width, r.height, r.x, r.y, sm, PlanarImage.createColorModel(sm));
    }

    private ROI createOpROI(ROI roi, String op) {
        RenderedOp imDest;
        if (roi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        PlanarImage imThis = this.getAsImage();
        PlanarImage imROI = roi.getAsImage();
        Rectangle boundsThis = imThis.getBounds();
        Rectangle boundsROI = imROI.getBounds();
        if (op.equals("and") || boundsThis.equals(boundsROI)) {
            imDest = JAI.create(op, (RenderedImage)imThis, imROI);
        } else if (op.equals("subtract") || boundsThis.contains(boundsROI)) {
            PlanarImage imBounds = ROI.createBinaryImage(boundsThis);
            imBounds = JAI.create("overlay", (RenderedImage)imBounds, imROI);
            imDest = JAI.create(op, (RenderedImage)imThis, imBounds);
        } else if (boundsROI.contains(boundsThis)) {
            PlanarImage imBounds = ROI.createBinaryImage(boundsROI);
            imBounds = JAI.create("overlay", (RenderedImage)imBounds, imThis);
            imDest = JAI.create(op, (RenderedImage)imBounds, imROI);
        } else {
            Rectangle merged = boundsThis.union(boundsROI);
            PlanarImage imBoundsThis = ROI.createBinaryImage(merged);
            PlanarImage imBoundsROI = ROI.createBinaryImage(merged);
            imBoundsThis = JAI.create("overlay", (RenderedImage)imBoundsThis, imThis);
            imBoundsROI = JAI.create("overlay", (RenderedImage)imBoundsROI, imROI);
            imDest = JAI.create(op, (RenderedImage)imBoundsThis, imBoundsROI);
        }
        return new ROI(imDest, this.threshold);
    }

    public ROI add(ROI roi) {
        return this.createOpROI(roi, "add");
    }

    public ROI subtract(ROI roi) {
        return this.createOpROI(roi, "subtract");
    }

    public ROI intersect(ROI roi) {
        return this.createOpROI(roi, "and");
    }

    public ROI exclusiveOr(ROI roi) {
        return this.createOpROI(roi, "xor");
    }

    public ROI transform(AffineTransform at, Interpolation interp) {
        if (at == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROI5"));
        }
        if (interp == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROI6"));
        }
        ParameterBlock paramBlock = new ParameterBlock();
        paramBlock.add(at);
        paramBlock.add(interp);
        return this.performImageOp("Affine", paramBlock, 0, null);
    }

    public ROI transform(AffineTransform at) {
        if (at == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.transform(at, Interpolation.getInstance(0));
    }

    public ROI performImageOp(RenderedImageFactory RIF, ParameterBlock paramBlock, int sourceIndex, RenderingHints renderHints) {
        if (RIF == null || paramBlock == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)paramBlock.clone();
        Vector<Object> sources = pb.getSources();
        sources.insertElementAt(this.getAsImage(), sourceIndex);
        RenderedImage im = RIF.create(pb, renderHints);
        return new ROI(im, this.threshold);
    }

    public ROI performImageOp(String name, ParameterBlock paramBlock, int sourceIndex, RenderingHints renderHints) {
        if (name == null || paramBlock == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)paramBlock.clone();
        Vector<Object> sources = pb.getSources();
        sources.insertElementAt(this.getAsImage(), sourceIndex);
        RenderedOp im = JAI.create(name, pb, renderHints);
        return new ROI(im, this.threshold);
    }

    public Shape getAsShape() {
        return null;
    }

    public PlanarImage getAsImage() {
        return this.theImage;
    }

    public int[][] getAsBitmask(int x, int y, int width, int height, int[][] mask) {
        int row;
        Rectangle rect = this.getBounds().intersection(new Rectangle(x, y, width, height));
        if (rect.isEmpty()) {
            return null;
        }
        int bitmaskIntWidth = (width + 31) / 32;
        if (mask == null) {
            mask = new int[height][bitmaskIntWidth];
        } else if (mask.length < height || mask[0].length < bitmaskIntWidth) {
            throw new RuntimeException(JaiI18N.getString("ROI3"));
        }
        byte[] data = ImageUtil.getPackedBinaryData(this.theImage.getData(), rect);
        int leftover = rect.width % 8;
        if (leftover != 0) {
            int datamask = (1 << leftover) - 1 << 8 - leftover;
            int linestride = (width + 7) / 8;
            for (int i = linestride - 1; i < data.length; i += linestride) {
                data[i] = (byte)(data[i] & datamask);
            }
        }
        int lineStride = (rect.width + 7) / 8;
        int leftOver = lineStride % 4;
        int ncols = (lineStride - leftOver) / 4;
        int k = 0;
        for (row = 0; row < rect.height; ++row) {
            int col;
            int[] maskRow = mask[row];
            for (col = 0; col < ncols; ++col) {
                maskRow[col] = (data[k] & 0xFF) << 24 | (data[k + 1] & 0xFF) << 16 | (data[k + 2] & 0xFF) << 8 | (data[k + 3] & 0xFF) << 0;
                k += 4;
            }
            switch (leftOver) {
                case 0: {
                    break;
                }
                case 1: {
                    maskRow[col++] = (data[k] & 0xFF) << 24;
                    break;
                }
                case 2: {
                    maskRow[col++] = (data[k] & 0xFF) << 24 | (data[k + 1] & 0xFF) << 16;
                    break;
                }
                case 3: {
                    maskRow[col++] = (data[k] & 0xFF) << 24 | (data[k + 1] & 0xFF) << 16 | (data[k + 2] & 0xFF) << 8;
                }
            }
            k += leftOver;
            Arrays.fill(maskRow, col, bitmaskIntWidth, 0);
        }
        for (row = rect.height; row < height; ++row) {
            Arrays.fill(mask[row], 0);
        }
        return mask;
    }

    public LinkedList getAsRectangleList(int x, int y, int width, int height) {
        return this.getAsRectangleList(x, y, width, height, true);
    }

    protected LinkedList getAsRectangleList(int x, int y, int width, int height, boolean mergeRectangles) {
        Rectangle rect;
        Rectangle bounds = this.getBounds();
        if (!bounds.intersects(rect = new Rectangle(x, y, width, height))) {
            return null;
        }
        if (!bounds.contains(rect)) {
            rect = bounds.intersection(rect);
            x = rect.x;
            y = rect.y;
            width = rect.width;
            height = rect.height;
        }
        byte[] data = ImageUtil.getPackedBinaryData(this.theImage.getData(), rect);
        int lineStride = (width + 7) / 8;
        int leftover = width % 8;
        int mask = leftover == 0 ? 255 : (1 << leftover) - 1 << 8 - leftover;
        LinkedList rectList = new LinkedList();
        int k = 0;
        for (int row = 0; row < height; ++row) {
            int start = -1;
            int col = 0;
            boolean cnt = false;
            while (col < lineStride) {
                int val = data[k] & (col == lineStride - 1 ? mask : 255);
                if (val == 0) {
                    if (start >= 0) {
                        rectList.addLast(new Rectangle(x + start, y + row, col * 8 - start, 1));
                        start = -1;
                    }
                } else if (val == 255) {
                    if (start < 0) {
                        start = col * 8;
                    }
                } else {
                    for (int bit = 7; bit >= 0; --bit) {
                        if ((val & 1 << bit) == 0) {
                            if (start < 0) continue;
                            rectList.addLast(new Rectangle(x + start, y + row, col * 8 + (7 - bit) - start, 1));
                            start = -1;
                            continue;
                        }
                        if (start >= 0) continue;
                        start = col * 8 + (7 - bit);
                    }
                }
                ++col;
                ++k;
            }
            if (start < 0) continue;
            rectList.addLast(new Rectangle(x + start, y + row, col * 8 - start, 1));
        }
        return mergeRectangles ? ROI.mergeRunLengthList(rectList) : rectList;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.theImage != null) {
            out.writeBoolean(true);
            RenderingHints hints = new RenderingHints(null);
            hints.put(JAI.KEY_SERIALIZE_DEEP_COPY, new Boolean(true));
            out.writeObject(SerializerFactory.getState(this.theImage, hints));
        } else {
            out.writeBoolean(false);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (in.readBoolean()) {
            SerializableState ss = (SerializableState)in.readObject();
            RenderedImage ri = (RenderedImage)ss.getObject();
            this.theImage = PlanarImage.wrapRenderedImage(ri);
        } else {
            this.theImage = null;
        }
        this.iter = null;
    }
}

