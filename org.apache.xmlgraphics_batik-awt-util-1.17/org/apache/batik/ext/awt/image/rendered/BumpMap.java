/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SinglePixelPackedSampleModel;

public final class BumpMap {
    private RenderedImage texture;
    private double surfaceScale;
    private double surfaceScaleX;
    private double surfaceScaleY;
    private double scaleX;
    private double scaleY;

    public BumpMap(RenderedImage texture, double surfaceScale, double scaleX, double scaleY) {
        this.texture = texture;
        this.surfaceScaleX = surfaceScale * scaleX;
        this.surfaceScaleY = surfaceScale * scaleY;
        this.surfaceScale = surfaceScale;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public double getSurfaceScale() {
        return this.surfaceScale;
    }

    public double[][][] getNormalArray(int x, int y, int w, int h) {
        double invNorm;
        double[] n;
        int xloc;
        int p;
        double[][] NRow;
        double[][][] N = new double[h][w][4];
        Rectangle srcRect = new Rectangle(x - 1, y - 1, w + 2, h + 2);
        Rectangle srcBound = new Rectangle(this.texture.getMinX(), this.texture.getMinY(), this.texture.getWidth(), this.texture.getHeight());
        if (!srcRect.intersects(srcBound)) {
            return N;
        }
        srcRect = srcRect.intersection(srcBound);
        Raster r = this.texture.getData(srcRect);
        srcRect = r.getBounds();
        DataBufferInt db = (DataBufferInt)r.getDataBuffer();
        int[] pixels = db.getBankData()[0];
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)r.getSampleModel();
        int scanStride = sppsm.getScanlineStride();
        int scanStridePP = scanStride + 1;
        int scanStrideMM = scanStride - 1;
        double prpc = 0.0;
        double prcc = 0.0;
        double prnc = 0.0;
        double crpc = 0.0;
        double crcc = 0.0;
        double crnc = 0.0;
        double nrpc = 0.0;
        double nrcc = 0.0;
        double nrnc = 0.0;
        double quarterSurfaceScaleX = this.surfaceScaleX / 4.0;
        double quarterSurfaceScaleY = this.surfaceScaleY / 4.0;
        double halfSurfaceScaleX = this.surfaceScaleX / 2.0;
        double halfSurfaceScaleY = this.surfaceScaleY / 2.0;
        double thirdSurfaceScaleX = this.surfaceScaleX / 3.0;
        double thirdSurfaceScaleY = this.surfaceScaleY / 3.0;
        double twoThirdSurfaceScaleX = this.surfaceScaleX * 2.0 / 3.0;
        double twoThirdSurfaceScaleY = this.surfaceScaleY * 2.0 / 3.0;
        double pixelScale = 0.00392156862745098;
        if (w <= 0) {
            return N;
        }
        if (h <= 0) {
            return N;
        }
        int xEnd = Math.min(srcRect.x + srcRect.width - 1, x + w);
        int yEnd = Math.min(srcRect.y + srcRect.height - 1, y + h);
        int offset = db.getOffset() + sppsm.getOffset(srcRect.x - r.getSampleModelTranslateX(), srcRect.y - r.getSampleModelTranslateY());
        int yloc = y;
        if (yloc < srcRect.y) {
            yloc = srcRect.y;
        }
        if (yloc == srcRect.y) {
            if (yloc == yEnd) {
                double invNorm2;
                double[] n2;
                double[][] NRow2 = N[yloc - y];
                int xloc2 = x;
                if (xloc2 < srcRect.x) {
                    xloc2 = srcRect.x;
                }
                int p2 = offset + (xloc2 - srcRect.x) + scanStride * (yloc - srcRect.y);
                crcc = (double)(pixels[p2] >>> 24) * 0.00392156862745098;
                if (xloc2 != srcRect.x) {
                    crpc = (double)(pixels[p2 - 1] >>> 24) * 0.00392156862745098;
                } else if (xloc2 < xEnd) {
                    crnc = (double)(pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                    n2 = NRow2[xloc2 - x];
                    n2[0] = 2.0 * this.surfaceScaleX * (crcc - crnc);
                    invNorm2 = 1.0 / Math.sqrt(n2[0] * n2[0] + 1.0);
                    n2[0] = n2[0] * invNorm2;
                    n2[1] = 0.0;
                    n2[2] = invNorm2;
                    n2[3] = crcc * this.surfaceScale;
                    ++p2;
                    ++xloc2;
                    crpc = crcc;
                    crcc = crnc;
                } else {
                    crpc = crcc;
                }
                while (xloc2 < xEnd) {
                    crnc = (double)(pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                    n2 = NRow2[xloc2 - x];
                    n2[0] = this.surfaceScaleX * (crpc - crnc);
                    invNorm2 = 1.0 / Math.sqrt(n2[0] * n2[0] + 1.0);
                    n2[0] = n2[0] * invNorm2;
                    n2[1] = 0.0;
                    n2[2] = invNorm2;
                    n2[3] = crcc * this.surfaceScale;
                    ++p2;
                    crpc = crcc;
                    crcc = crnc;
                    ++xloc2;
                }
                if (xloc2 < x + w && xloc2 == srcRect.x + srcRect.width - 1) {
                    n2 = NRow2[xloc2 - x];
                    n2[0] = 2.0 * this.surfaceScaleX * (crpc - crcc);
                    invNorm2 = 1.0 / Math.sqrt(n2[0] * n2[0] + n2[1] * n2[1] + 1.0);
                    n2[0] = n2[0] * invNorm2;
                    n2[1] = n2[1] * invNorm2;
                    n2[2] = invNorm2;
                    n2[3] = crcc * this.surfaceScale;
                }
                return N;
            }
            NRow = N[yloc - y];
            p = offset + scanStride * (yloc - srcRect.y);
            xloc = x;
            if (xloc < srcRect.x) {
                xloc = srcRect.x;
            }
            crcc = (double)(pixels[p += xloc - srcRect.x] >>> 24) * 0.00392156862745098;
            nrcc = (double)(pixels[p + scanStride] >>> 24) * 0.00392156862745098;
            if (xloc != srcRect.x) {
                crpc = (double)(pixels[p - 1] >>> 24) * 0.00392156862745098;
                nrpc = (double)(pixels[p + scanStrideMM] >>> 24) * 0.00392156862745098;
            } else if (xloc < xEnd) {
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                nrnc = (double)(pixels[p + scanStridePP] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crnc + nrnc - 2.0 * crcc - nrcc);
                n[1] = -twoThirdSurfaceScaleY * (2.0 * nrcc + nrnc - 2.0 * crcc - crnc);
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                ++xloc;
                crpc = crcc;
                nrpc = nrcc;
                crcc = crnc;
                nrcc = nrnc;
            } else {
                crpc = crcc;
                nrpc = nrcc;
            }
            while (xloc < xEnd) {
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                nrnc = (double)(pixels[p + scanStridePP] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -thirdSurfaceScaleX * (2.0 * crnc + nrnc - (2.0 * crpc + nrpc));
                n[1] = -halfSurfaceScaleY * (nrpc + 2.0 * nrcc + nrnc - (crpc + 2.0 * crcc + crnc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                crpc = crcc;
                nrpc = nrcc;
                crcc = crnc;
                nrcc = nrnc;
                ++xloc;
            }
            if (xloc < x + w && xloc == srcRect.x + srcRect.width - 1) {
                n = NRow[xloc - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crcc + nrcc - (2.0 * crpc + nrpc));
                n[1] = -twoThirdSurfaceScaleY * (2.0 * nrcc + nrpc - (2.0 * crcc + crpc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
            ++yloc;
        }
        while (yloc < yEnd) {
            NRow = N[yloc - y];
            p = offset + scanStride * (yloc - srcRect.y);
            xloc = x;
            if (xloc < srcRect.x) {
                xloc = srcRect.x;
            }
            prcc = (double)(pixels[(p += xloc - srcRect.x) - scanStride] >>> 24) * 0.00392156862745098;
            crcc = (double)(pixels[p] >>> 24) * 0.00392156862745098;
            nrcc = (double)(pixels[p + scanStride] >>> 24) * 0.00392156862745098;
            if (xloc != srcRect.x) {
                prpc = (double)(pixels[p - scanStridePP] >>> 24) * 0.00392156862745098;
                crpc = (double)(pixels[p - 1] >>> 24) * 0.00392156862745098;
                nrpc = (double)(pixels[p + scanStrideMM] >>> 24) * 0.00392156862745098;
            } else if (xloc < xEnd) {
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                prnc = (double)(pixels[p - scanStrideMM] >>> 24) * 0.00392156862745098;
                nrnc = (double)(pixels[p + scanStridePP] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -halfSurfaceScaleX * (prnc + 2.0 * crnc + nrnc - (prcc + 2.0 * crcc + nrcc));
                n[1] = -thirdSurfaceScaleY * (2.0 * prcc + prnc - (2.0 * crcc + crnc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                ++xloc;
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
                prcc = prnc;
                crcc = crnc;
                nrcc = nrnc;
            } else {
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
            }
            while (xloc < xEnd) {
                prnc = (double)(pixels[p - scanStrideMM] >>> 24) * 0.00392156862745098;
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                nrnc = (double)(pixels[p + scanStridePP] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -quarterSurfaceScaleX * (prnc + 2.0 * crnc + nrnc - (prpc + 2.0 * crpc + nrpc));
                n[1] = -quarterSurfaceScaleY * (nrpc + 2.0 * nrcc + nrnc - (prpc + 2.0 * prcc + prnc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
                prcc = prnc;
                crcc = crnc;
                nrcc = nrnc;
                ++xloc;
            }
            if (xloc < x + w && xloc == srcRect.x + srcRect.width - 1) {
                n = NRow[xloc - x];
                n[0] = -halfSurfaceScaleX * (prcc + 2.0 * crcc + nrcc - (prpc + 2.0 * crpc + nrpc));
                n[1] = -thirdSurfaceScaleY * (nrpc + 2.0 * nrcc - (prpc + 2.0 * prcc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
            ++yloc;
        }
        if (yloc < y + h && yloc == srcRect.y + srcRect.height - 1) {
            NRow = N[yloc - y];
            p = offset + scanStride * (yloc - srcRect.y);
            xloc = x;
            if (xloc < srcRect.x) {
                xloc = srcRect.x;
            }
            crcc = (double)(pixels[p += xloc - srcRect.x] >>> 24) * 0.00392156862745098;
            prcc = (double)(pixels[p - scanStride] >>> 24) * 0.00392156862745098;
            if (xloc != srcRect.x) {
                prpc = (double)(pixels[p - scanStridePP] >>> 24) * 0.00392156862745098;
                crpc = (double)(pixels[p - 1] >>> 24) * 0.00392156862745098;
            } else if (xloc < xEnd) {
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                prnc = (double)(pixels[p - scanStrideMM] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crnc + prnc - 2.0 * crcc - prcc);
                n[1] = -twoThirdSurfaceScaleY * (2.0 * crcc + crnc - 2.0 * prcc - prnc);
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                ++xloc;
                crpc = crcc;
                prpc = prcc;
                crcc = crnc;
                prcc = prnc;
            } else {
                crpc = crcc;
                prpc = prcc;
            }
            while (xloc < xEnd) {
                crnc = (double)(pixels[p + 1] >>> 24) * 0.00392156862745098;
                prnc = (double)(pixels[p - scanStrideMM] >>> 24) * 0.00392156862745098;
                n = NRow[xloc - x];
                n[0] = -thirdSurfaceScaleX * (2.0 * crnc + prnc - (2.0 * crpc + prpc));
                n[1] = -halfSurfaceScaleY * (crpc + 2.0 * crcc + crnc - (prpc + 2.0 * prcc + prnc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p;
                crpc = crcc;
                prpc = prcc;
                crcc = crnc;
                prcc = prnc;
                ++xloc;
            }
            if (xloc < x + w && xloc == srcRect.x + srcRect.width - 1) {
                n = NRow[xloc - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crcc + prcc - (2.0 * crpc + prpc));
                n[1] = -twoThirdSurfaceScaleY * (2.0 * crcc + crpc - (2.0 * prcc + prpc));
                invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                n[0] = n[0] * invNorm;
                n[1] = n[1] * invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
        }
        return N;
    }
}

