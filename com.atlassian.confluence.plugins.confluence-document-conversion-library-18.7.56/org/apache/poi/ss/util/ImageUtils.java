/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.ss.util;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.Units;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class ImageUtils {
    private static final Logger LOG = LogManager.getLogger(ImageUtils.class);
    private static final int WIDTH_UNITS = 1024;
    private static final int HEIGHT_UNITS = 256;

    private ImageUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public static Dimension getImageDimension(InputStream is, int type) {
        size = new Dimension();
        switch (type) {
            case 5: 
            case 6: 
            case 7: {
                try {
                    iis = ImageIO.createImageInputStream(is);
                    var4_5 = null;
                    i = ImageIO.getImageReaders(iis);
                    if (i.hasNext()) {
                        r = i.next();
                        try {
                            r.setInput(iis);
                            img = r.read(0);
                            dpi = ImageUtils.getResolution(r);
                            if (dpi[0] == 0) {
                                dpi[0] = 96;
                            }
                            if (dpi[1] == 0) {
                                dpi[1] = 96;
                            }
                            size.width = img.getWidth() * 96 / dpi[0];
                            size.height = img.getHeight() * 96 / dpi[1];
                        }
                        finally {
                            r.dispose();
                        }
                    } else {
                        ImageUtils.LOG.atWarn().log("ImageIO found no images");
                    }
                    if (iis == null) break;
                    if (var4_5 == null) ** GOTO lbl34
                    try {
                        iis.close();
                    }
                    catch (Throwable var5_7) {
                        var4_5.addSuppressed(var5_7);
                    }
                    break;
lbl34:
                    // 1 sources

                    iis.close();
                    ** break;
                    catch (Throwable var5_8) {
                        try {
                            var4_5 = var5_8;
                            throw var5_8;
                        }
                        catch (Throwable var10_13) {
                            if (iis != null) {
                                if (var4_5 != null) {
                                    try {
                                        iis.close();
                                    }
                                    catch (Throwable var11_14) {
                                        var4_5.addSuppressed(var11_14);
                                    }
                                } else {
                                    iis.close();
                                }
                            }
                            throw var10_13;
lbl51:
                            // 1 sources

                        }
                    }
                }
                catch (IOException e) {
                    ImageUtils.LOG.atWarn().withThrowable(e).log("Failed to determine image dimensions");
                }
                break;
            }
            default: {
                ImageUtils.LOG.atWarn().log("Only JPEG, PNG and DIB pictures can be automatically sized");
            }
        }
        return size;
    }

    public static int[] getResolution(ImageReader r) throws IOException {
        int hdpi = 96;
        int vdpi = 96;
        double mm2inch = 25.4;
        Element node = (Element)r.getImageMetadata(0).getAsTree("javax_imageio_1.0");
        NodeList lst = node.getElementsByTagName("HorizontalPixelSize");
        if (lst != null && lst.getLength() == 1) {
            hdpi = (int)(mm2inch / (double)Float.parseFloat(((Element)lst.item(0)).getAttribute("value")));
        }
        if ((lst = node.getElementsByTagName("VerticalPixelSize")) != null && lst.getLength() == 1) {
            vdpi = (int)(mm2inch / (double)Float.parseFloat(((Element)lst.item(0)).getAttribute("value")));
        }
        return new int[]{hdpi, vdpi};
    }

    public static Dimension setPreferredSize(Picture picture, double scaleX, double scaleY) {
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = anchor instanceof HSSFClientAnchor;
        PictureData data = picture.getPictureData();
        Sheet sheet = picture.getSheet();
        Dimension imgSize = scaleX == Double.MAX_VALUE || scaleY == Double.MAX_VALUE ? ImageUtils.getImageDimension((InputStream)new UnsynchronizedByteArrayInputStream(data.getData()), data.getPictureType()) : new Dimension();
        Dimension anchorSize = scaleX != Double.MAX_VALUE || scaleY != Double.MAX_VALUE ? ImageUtils.getDimensionFromAnchor(picture) : new Dimension();
        double scaledWidth = scaleX == Double.MAX_VALUE ? imgSize.getWidth() : anchorSize.getWidth() / 9525.0 * scaleX;
        double scaledHeight = scaleY == Double.MAX_VALUE ? imgSize.getHeight() : anchorSize.getHeight() / 9525.0 * scaleY;
        ImageUtils.scaleCell(scaledWidth, anchor.getCol1(), anchor.getDx1(), anchor::setCol2, anchor::setDx2, isHSSF ? 1024 : 0, sheet::getColumnWidthInPixels);
        ImageUtils.scaleCell(scaledHeight, anchor.getRow1(), anchor.getDy1(), anchor::setRow2, anchor::setDy2, isHSSF ? 256 : 0, row -> ImageUtils.getRowHeightInPixels(sheet, row));
        return new Dimension((int)Math.round(scaledWidth * 9525.0), (int)Math.round(scaledHeight * 9525.0));
    }

    public static Dimension getDimensionFromAnchor(Picture picture) {
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = anchor instanceof HSSFClientAnchor;
        Sheet sheet = picture.getSheet();
        Dimension imgSize = null;
        if (anchor.getCol2() < anchor.getCol1() || anchor.getRow2() < anchor.getRow1()) {
            PictureData data = picture.getPictureData();
            imgSize = ImageUtils.getImageDimension((InputStream)new UnsynchronizedByteArrayInputStream(data.getData()), data.getPictureType());
        }
        int w = ImageUtils.getDimFromCell(imgSize == null ? 0.0 : imgSize.getWidth(), anchor.getCol1(), anchor.getDx1(), anchor.getCol2(), anchor.getDx2(), isHSSF ? 1024 : 0, sheet::getColumnWidthInPixels);
        int h = ImageUtils.getDimFromCell(imgSize == null ? 0.0 : imgSize.getHeight(), anchor.getRow1(), anchor.getDy1(), anchor.getRow2(), anchor.getDy2(), isHSSF ? 256 : 0, row -> ImageUtils.getRowHeightInPixels(sheet, row));
        return new Dimension(w, h);
    }

    public static double getRowHeightInPixels(Sheet sheet, int rowNum) {
        Row r = sheet.getRow(rowNum);
        double points = r == null ? (double)sheet.getDefaultRowHeightInPoints() : (double)r.getHeightInPoints();
        return (double)Units.toEMU(points) / 9525.0;
    }

    private static void scaleCell(double targetSize, int startCell, int startD, Consumer<Integer> endCell, Consumer<Integer> endD, int hssfUnits, Function<Integer, Number> nextSize) {
        double delta;
        double dim;
        if (targetSize < 0.0) {
            throw new IllegalArgumentException("target size < 0");
        }
        if (Double.isInfinite(targetSize) || Double.isNaN(targetSize)) {
            throw new IllegalArgumentException("target size " + targetSize + " is not supported");
        }
        int cellIdx = startCell;
        double totalDim = 0.0;
        while (true) {
            double remDim = dim = nextSize.apply(cellIdx).doubleValue();
            if (cellIdx == startCell) {
                remDim = hssfUnits > 0 ? (remDim *= 1.0 - (double)startD / (double)hssfUnits) : (remDim -= (double)startD / 9525.0);
            }
            if ((delta = targetSize - totalDim) < remDim) break;
            ++cellIdx;
            totalDim += remDim;
        }
        double endDval = hssfUnits > 0 ? delta / dim * (double)hssfUnits : delta * 9525.0;
        if (cellIdx == startCell) {
            endDval += (double)startD;
        }
        endCell.accept(cellIdx);
        endD.accept((int)Math.rint(endDval));
    }

    private static int getDimFromCell(double imgSize, int startCell, int startD, int endCell, int endD, int hssfUnits, Function<Integer, Number> nextSize) {
        double targetSize;
        if (endCell < startCell) {
            targetSize = imgSize * 9525.0;
        } else {
            targetSize = 0.0;
            for (int cellIdx = startCell; cellIdx <= endCell; ++cellIdx) {
                double dim = nextSize.apply(cellIdx).doubleValue() * 9525.0;
                double leadSpace = 0.0;
                if (cellIdx == startCell) {
                    leadSpace = hssfUnits > 0 ? dim * (double)startD / (double)hssfUnits : (double)startD;
                }
                double trailSpace = 0.0;
                if (cellIdx == endCell) {
                    trailSpace = hssfUnits > 0 ? dim * (double)(hssfUnits - endD) / (double)hssfUnits : dim - (double)endD;
                }
                targetSize += dim - leadSpace - trailSpace;
            }
        }
        return (int)Math.rint(targetSize);
    }
}

