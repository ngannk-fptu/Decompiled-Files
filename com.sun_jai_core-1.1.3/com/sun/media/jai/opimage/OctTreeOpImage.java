/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ColorQuantizerOpImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.UnpackedImageData;

public class OctTreeOpImage
extends ColorQuantizerOpImage {
    private int treeSize;
    private int maxTreeDepth = 8;
    private int[] squares = new int[(this.maxColorNum << 1) + 1];

    public OctTreeOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod) {
        super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
        for (int i = -this.maxColorNum; i <= this.maxColorNum; ++i) {
            this.squares[i + this.maxColorNum] = i * i;
        }
        this.colorMap = null;
        this.treeSize = upperBound;
    }

    protected synchronized void train() {
        Cube cube = new Cube(this.getSourceImage(0), this.maxColorNum);
        cube.constructTree();
        cube.reduction();
        cube.assignment();
        this.colorMap = new LookupTableJAI(cube.colormap);
        this.setProperty("LUT", this.colorMap);
        this.setProperty("JAI.LookupTable", this.colorMap);
    }

    class Cube {
        PlanarImage source;
        int max_colors;
        byte[][] colormap = new byte[3][];
        Node root;
        int depth;
        int colors;
        int nodes;

        Cube(PlanarImage source, int max_colors) {
            this.source = source;
            this.max_colors = max_colors;
            int i = max_colors;
            this.depth = 0;
            while (i != 0) {
                i >>>= 1;
                ++this.depth;
            }
            if (this.depth > OctTreeOpImage.this.maxTreeDepth) {
                this.depth = OctTreeOpImage.this.maxTreeDepth;
            } else if (this.depth < 2) {
                this.depth = 2;
            }
            this.root = new Node(this);
        }

        void constructTree() {
            if (OctTreeOpImage.this.roi == null) {
                OctTreeOpImage.this.roi = new ROIShape(this.source.getBounds());
            }
            int minTileX = this.source.getMinTileX();
            int maxTileX = this.source.getMaxTileX();
            int minTileY = this.source.getMinTileY();
            int maxTileY = this.source.getMaxTileY();
            int xStart = this.source.getMinX();
            int yStart = this.source.getMinY();
            for (int y = minTileY; y <= maxTileY; ++y) {
                for (int x = minTileX; x <= maxTileX; ++x) {
                    Rectangle tileRect = this.source.getTileRect(x, y);
                    if (!OctTreeOpImage.this.roi.intersects(tileRect)) continue;
                    if (OctTreeOpImage.this.checkForSkippedTiles && tileRect.x >= xStart && tileRect.y >= yStart) {
                        int offsetX = (OctTreeOpImage.this.xPeriod - (tileRect.x - xStart) % OctTreeOpImage.this.xPeriod) % OctTreeOpImage.this.xPeriod;
                        int offsetY = (OctTreeOpImage.this.yPeriod - (tileRect.y - yStart) % OctTreeOpImage.this.yPeriod) % OctTreeOpImage.this.yPeriod;
                        if (offsetX >= tileRect.width || offsetY >= tileRect.height) continue;
                    }
                    this.constructTree(this.source.getData(tileRect));
                }
            }
        }

        private void constructTree(Raster source) {
            LinkedList rectList;
            if (!OctTreeOpImage.this.isInitialized) {
                OctTreeOpImage.this.srcPA = new PixelAccessor(OctTreeOpImage.this.getSourceImage(0));
                OctTreeOpImage.this.srcSampleType = OctTreeOpImage.this.srcPA.sampleType == -1 ? 0 : OctTreeOpImage.this.srcPA.sampleType;
                OctTreeOpImage.this.isInitialized = true;
            }
            Rectangle srcBounds = OctTreeOpImage.this.getSourceImage(0).getBounds().intersection(source.getBounds());
            if (OctTreeOpImage.this.roi == null) {
                rectList = new LinkedList();
                rectList.addLast(srcBounds);
            } else {
                rectList = OctTreeOpImage.this.roi.getAsRectangleList(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
                if (rectList == null) {
                    return;
                }
            }
            ListIterator iterator = rectList.listIterator(0);
            int xStart = source.getMinX();
            int yStart = source.getMinY();
            while (iterator.hasNext()) {
                Rectangle rect = srcBounds.intersection((Rectangle)iterator.next());
                int tx = rect.x;
                int ty = rect.y;
                rect.x = ColorQuantizerOpImage.startPosition(tx, xStart, OctTreeOpImage.this.xPeriod);
                rect.y = ColorQuantizerOpImage.startPosition(ty, yStart, OctTreeOpImage.this.yPeriod);
                rect.width = tx + rect.width - rect.x;
                rect.height = ty + rect.height - rect.y;
                if (rect.isEmpty()) continue;
                UnpackedImageData uid = OctTreeOpImage.this.srcPA.getPixels(source, rect, OctTreeOpImage.this.srcSampleType, false);
                switch (uid.type) {
                    case 0: {
                        this.constructTreeByte(uid);
                    }
                }
            }
        }

        private void constructTreeByte(UnpackedImageData uid) {
            Rectangle rect = uid.rect;
            byte[][] data = uid.getByteData();
            int lineStride = uid.lineStride;
            int pixelStride = uid.pixelStride;
            byte[] rBand = data[0];
            byte[] gBand = data[1];
            byte[] bBand = data[2];
            int lineInc = lineStride * OctTreeOpImage.this.yPeriod;
            int pixelInc = pixelStride * OctTreeOpImage.this.xPeriod;
            int lastLine = rect.height * lineStride;
            for (int lo = 0; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int red = rBand[po + uid.bandOffsets[0]] & 0xFF;
                    int green = gBand[po + uid.bandOffsets[1]] & 0xFF;
                    int blue = bBand[po + uid.bandOffsets[2]] & 0xFF;
                    if (this.nodes > OctTreeOpImage.this.treeSize) {
                        this.root.pruneLevel();
                        --this.depth;
                    }
                    Node node = this.root;
                    for (int level = 1; level <= this.depth; ++level) {
                        int id = (red > node.mid_red ? 1 : 0) | (green > node.mid_green ? 1 : 0) << 1 | (blue > node.mid_blue ? 1 : 0) << 2;
                        node = node.child[id] == null ? new Node(node, id, level) : node.child[id];
                        ++node.number_pixels;
                    }
                    ++node.unique;
                    node.total_red += red;
                    node.total_green += green;
                    node.total_blue += blue;
                }
            }
        }

        void reduction() {
            int totalSamples = (this.source.getWidth() + OctTreeOpImage.this.xPeriod - 1) / OctTreeOpImage.this.xPeriod * (this.source.getHeight() + OctTreeOpImage.this.yPeriod - 1) / OctTreeOpImage.this.yPeriod;
            int threshold = Math.max(1, totalSamples / (this.max_colors * 8));
            while (this.colors > this.max_colors) {
                this.colors = 0;
                threshold = this.root.reduce(threshold, Integer.MAX_VALUE);
            }
        }

        void assignment() {
            this.colormap = new byte[3][this.colors];
            this.colors = 0;
            this.root.colormap();
        }

        class Node {
            Cube cube;
            Node parent;
            Node[] child;
            int nchild;
            int id;
            int level;
            int mid_red;
            int mid_green;
            int mid_blue;
            int number_pixels;
            int unique;
            int total_red;
            int total_green;
            int total_blue;
            int color_number;

            Node(Cube cube) {
                this.cube = cube;
                this.parent = this;
                this.child = new Node[8];
                this.id = 0;
                this.level = 0;
                this.number_pixels = Integer.MAX_VALUE;
                this.mid_red = ((Cube)Cube.this).OctTreeOpImage.this.maxColorNum + 1 >> 1;
                this.mid_green = ((Cube)Cube.this).OctTreeOpImage.this.maxColorNum + 1 >> 1;
                this.mid_blue = ((Cube)Cube.this).OctTreeOpImage.this.maxColorNum + 1 >> 1;
            }

            Node(Node parent, int id, int level) {
                this.cube = parent.cube;
                this.parent = parent;
                this.child = new Node[8];
                this.id = id;
                this.level = level;
                ++this.cube.nodes;
                if (level == this.cube.depth) {
                    ++this.cube.colors;
                }
                ++parent.nchild;
                parent.child[id] = this;
                int bi = 1 << OctTreeOpImage.this.maxTreeDepth - level >> 1;
                this.mid_red = parent.mid_red + ((id & 1) > 0 ? bi : -bi);
                this.mid_green = parent.mid_green + ((id & 2) > 0 ? bi : -bi);
                this.mid_blue = parent.mid_blue + ((id & 4) > 0 ? bi : -bi);
            }

            void pruneChild() {
                --this.parent.nchild;
                this.parent.unique += this.unique;
                this.parent.total_red += this.total_red;
                this.parent.total_green += this.total_green;
                this.parent.total_blue += this.total_blue;
                this.parent.child[this.id] = null;
                --this.cube.nodes;
                this.cube = null;
                this.parent = null;
            }

            void pruneLevel() {
                if (this.nchild != 0) {
                    for (int id = 0; id < 8; ++id) {
                        if (this.child[id] == null) continue;
                        this.child[id].pruneLevel();
                    }
                }
                if (this.level == this.cube.depth) {
                    this.pruneChild();
                }
            }

            int reduce(int threshold, int next_threshold) {
                if (this.nchild != 0) {
                    for (int id = 0; id < 8; ++id) {
                        if (this.child[id] == null) continue;
                        next_threshold = this.child[id].reduce(threshold, next_threshold);
                    }
                }
                if (this.number_pixels <= threshold) {
                    this.pruneChild();
                } else {
                    if (this.unique != 0) {
                        ++this.cube.colors;
                    }
                    if (this.number_pixels < next_threshold) {
                        next_threshold = this.number_pixels;
                    }
                }
                return next_threshold;
            }

            void colormap() {
                if (this.nchild != 0) {
                    for (int id = 0; id < 8; ++id) {
                        if (this.child[id] == null) continue;
                        this.child[id].colormap();
                    }
                }
                if (this.unique != 0) {
                    this.cube.colormap[0][this.cube.colors] = (byte)((this.total_red + (this.unique >> 1)) / this.unique);
                    this.cube.colormap[1][this.cube.colors] = (byte)((this.total_green + (this.unique >> 1)) / this.unique);
                    this.cube.colormap[2][this.cube.colors] = (byte)((this.total_blue + (this.unique >> 1)) / this.unique);
                    this.color_number = this.cube.colors++;
                }
            }
        }
    }
}

