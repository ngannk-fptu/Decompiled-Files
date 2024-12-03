/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PNGConverter;
import org.apache.pdfbox.pdmodel.graphics.image.SampledImageReader;
import org.apache.pdfbox.util.filetypedetector.FileType;
import org.apache.pdfbox.util.filetypedetector.FileTypeDetector;

public final class PDImageXObject
extends PDXObject
implements PDImage {
    private static final Log LOG = LogFactory.getLog(PDImageXObject.class);
    private SoftReference<BufferedImage> cachedImage;
    private PDColorSpace colorSpace;
    private int cachedImageSubsampling = Integer.MAX_VALUE;
    private final PDResources resources;

    public PDImageXObject(PDDocument document) throws IOException {
        this(new PDStream(document), (PDResources)null);
    }

    public PDImageXObject(PDDocument document, InputStream encodedStream, COSBase cosFilter, int width, int height, int bitsPerComponent, PDColorSpace initColorSpace) throws IOException {
        super(PDImageXObject.createRawStream(document, encodedStream), COSName.IMAGE);
        this.getCOSObject().setItem(COSName.FILTER, cosFilter);
        this.resources = null;
        this.colorSpace = null;
        this.setBitsPerComponent(bitsPerComponent);
        this.setWidth(width);
        this.setHeight(height);
        this.setColorSpace(initColorSpace);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PDImageXObject(PDStream stream, PDResources resources) throws IOException {
        super(stream, COSName.IMAGE);
        this.resources = resources;
        List<COSName> filters = stream.getFilters();
        if (filters != null && !filters.isEmpty() && COSName.JPX_DECODE.equals(filters.get(filters.size() - 1))) {
            COSInputStream is = null;
            try {
                is = stream.createInputStream();
                DecodeResult decodeResult = is.getDecodeResult();
                stream.getCOSObject().addAll(decodeResult.getParameters());
                this.colorSpace = decodeResult.getJPXColorSpace();
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public static PDImageXObject createThumbnail(COSStream cosStream) throws IOException {
        PDStream pdStream = new PDStream(cosStream);
        return new PDImageXObject(pdStream, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static COSStream createRawStream(PDDocument document, InputStream rawInput) throws IOException {
        COSStream stream = document.getDocument().createCOSStream();
        OutputStream output = null;
        try {
            output = stream.createRawOutputStream();
            IOUtils.copy(rawInput, output);
        }
        finally {
            if (output != null) {
                output.close();
            }
        }
        return stream;
    }

    public static PDImageXObject createFromFile(String imagePath, PDDocument doc) throws IOException {
        return PDImageXObject.createFromFileByExtension(new File(imagePath), doc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PDImageXObject createFromFileByExtension(File file, PDDocument doc) throws IOException {
        String name = file.getName();
        int dot = name.lastIndexOf(46);
        if (dot == -1) {
            throw new IllegalArgumentException("Image type not supported: " + name);
        }
        String ext = name.substring(dot + 1).toLowerCase();
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            PDImageXObject pDImageXObject;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                pDImageXObject = JPEGFactory.createFromStream(doc, fis);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fis);
                throw throwable;
            }
            IOUtils.closeQuietly(fis);
            return pDImageXObject;
        }
        if ("tif".equals(ext) || "tiff".equals(ext)) {
            try {
                return CCITTFactory.createFromFile(doc, file);
            }
            catch (IOException ex) {
                LOG.debug((Object)"Reading as TIFF failed, setting fileType to PNG", (Throwable)ex);
                ext = "png";
            }
        }
        if ("gif".equals(ext) || "bmp".equals(ext) || "png".equals(ext)) {
            BufferedImage bim = ImageIO.read(file);
            return LosslessFactory.createFromImage(doc, bim);
        }
        throw new IllegalArgumentException("Image type not supported: " + name);
    }

    public static PDImageXObject createFromFileByContent(File file, PDDocument doc) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        FileType fileType = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            fileType = FileTypeDetector.detectFileType(bufferedInputStream);
        }
        catch (IOException e) {
            try {
                throw new IOException("Could not determine file type: " + file.getName(), e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(bufferedInputStream);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(fileInputStream);
        IOUtils.closeQuietly(bufferedInputStream);
        if (fileType == null) {
            throw new IllegalArgumentException("Image type not supported: " + file.getName());
        }
        if (fileType.equals((Object)FileType.JPEG)) {
            FileInputStream fis = new FileInputStream(file);
            PDImageXObject imageXObject = JPEGFactory.createFromStream(doc, fis);
            fis.close();
            return imageXObject;
        }
        if (fileType.equals((Object)FileType.TIFF)) {
            try {
                return CCITTFactory.createFromFile(doc, file);
            }
            catch (IOException ex) {
                LOG.debug((Object)"Reading as TIFF failed, setting fileType to PNG", (Throwable)ex);
                fileType = FileType.PNG;
            }
        }
        if (fileType.equals((Object)FileType.BMP) || fileType.equals((Object)FileType.GIF) || fileType.equals((Object)FileType.PNG)) {
            BufferedImage bim = ImageIO.read(file);
            return LosslessFactory.createFromImage(doc, bim);
        }
        throw new IllegalArgumentException("Image type " + (Object)((Object)fileType) + " not supported: " + file.getName());
    }

    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray, String name) throws IOException {
        PDImageXObject image;
        FileType fileType;
        try {
            fileType = FileTypeDetector.detectFileType(byteArray);
        }
        catch (IOException e) {
            throw new IOException("Could not determine file type: " + name, e);
        }
        if (fileType == null) {
            throw new IllegalArgumentException("Image type not supported: " + name);
        }
        if (fileType.equals((Object)FileType.JPEG)) {
            return JPEGFactory.createFromByteArray(document, byteArray);
        }
        if (fileType.equals((Object)FileType.PNG) && (image = PNGConverter.convertPNGImage(document, byteArray)) != null) {
            return image;
        }
        if (fileType.equals((Object)FileType.TIFF)) {
            try {
                return CCITTFactory.createFromByteArray(document, byteArray);
            }
            catch (IOException ex) {
                LOG.debug((Object)"Reading as TIFF failed, setting fileType to PNG", (Throwable)ex);
                fileType = FileType.PNG;
            }
        }
        if (fileType.equals((Object)FileType.BMP) || fileType.equals((Object)FileType.GIF) || fileType.equals((Object)FileType.PNG)) {
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            BufferedImage bim = ImageIO.read(bais);
            return LosslessFactory.createFromImage(document, bim);
        }
        throw new IllegalArgumentException("Image type " + (Object)((Object)fileType) + " not supported: " + name);
    }

    public PDMetadata getMetadata() {
        COSStream cosStream = this.getCOSObject().getCOSStream(COSName.METADATA);
        if (cosStream != null) {
            return new PDMetadata(cosStream);
        }
        return null;
    }

    public void setMetadata(PDMetadata meta) {
        this.getCOSObject().setItem(COSName.METADATA, (COSObjectable)meta);
    }

    public int getStructParent() {
        return this.getCOSObject().getInt(COSName.STRUCT_PARENT);
    }

    public void setStructParent(int key) {
        this.getCOSObject().setInt(COSName.STRUCT_PARENT, key);
    }

    @Override
    public BufferedImage getImage() throws IOException {
        return this.getImage(null, 1);
    }

    @Override
    public BufferedImage getImage(Rectangle region, int subsampling) throws IOException {
        BufferedImage cached;
        if (region == null && subsampling == this.cachedImageSubsampling && this.cachedImage != null && (cached = this.cachedImage.get()) != null) {
            return cached;
        }
        PDImageXObject softMask = this.getSoftMask();
        PDImageXObject mask = this.getMask();
        BufferedImage image = softMask != null ? this.applyMask(SampledImageReader.getRGBImage(this, region, subsampling, this.getColorKeyMask()), softMask.getOpaqueImage(region, subsampling), softMask.getInterpolate(), true, this.extractMatte(softMask)) : (mask != null && mask.isStencil() ? this.applyMask(SampledImageReader.getRGBImage(this, region, subsampling, this.getColorKeyMask()), mask.getOpaqueImage(region, subsampling), mask.getInterpolate(), false, null) : SampledImageReader.getRGBImage(this, region, subsampling, this.getColorKeyMask()));
        if (region == null && subsampling <= this.cachedImageSubsampling) {
            this.cachedImageSubsampling = subsampling;
            this.cachedImage = new SoftReference<BufferedImage>(image);
        }
        return image;
    }

    @Override
    public BufferedImage getRawImage() throws IOException {
        return this.getColorSpace().toRawImage(this.getRawRaster());
    }

    @Override
    public WritableRaster getRawRaster() throws IOException {
        return SampledImageReader.getRawRaster(this);
    }

    private float[] extractMatte(PDImageXObject softMask) throws IOException {
        COSBase base = softMask.getCOSObject().getItem(COSName.MATTE);
        float[] matte = null;
        if (base instanceof COSArray) {
            matte = ((COSArray)base).toFloatArray();
            if (matte.length < this.getColorSpace().getNumberOfComponents()) {
                LOG.error((Object)"Image /Matte entry not long enough for colorspace, skipped");
                return null;
            }
            matte = this.getColorSpace().toRGB(matte);
        }
        return matte;
    }

    @Override
    public BufferedImage getStencilImage(Paint paint) throws IOException {
        if (!this.isStencil()) {
            throw new IllegalStateException("Image is not a stencil");
        }
        return SampledImageReader.getStencilImage(this, paint);
    }

    public BufferedImage getOpaqueImage() throws IOException {
        return this.getOpaqueImage(null, 1);
    }

    public BufferedImage getOpaqueImage(Rectangle region, int subsampling) throws IOException {
        return SampledImageReader.getRGBImage(this, region, subsampling, null);
    }

    private BufferedImage applyMask(BufferedImage image, BufferedImage mask, boolean interpolateMask, boolean isSoft, float[] matte) {
        if (mask == null) {
            return image;
        }
        int width = Math.max(image.getWidth(), mask.getWidth());
        int height = Math.max(image.getHeight(), mask.getHeight());
        if (mask.getWidth() < width || mask.getHeight() < height) {
            mask = PDImageXObject.scaleImage(mask, width, height, 10, interpolateMask);
        } else if (mask.getType() != 10) {
            mask = PDImageXObject.scaleImage(mask, width, height, 10, false);
        }
        if (image.getWidth() < width || image.getHeight() < height) {
            image = PDImageXObject.scaleImage(image, width, height, 2, this.getInterpolate());
        } else if (image.getType() != 2) {
            image = PDImageXObject.scaleImage(image, width, height, 2, false);
        }
        WritableRaster raster = image.getRaster();
        WritableRaster alpha = mask.getRaster();
        if (!isSoft && raster.getDataBuffer().getSize() == alpha.getDataBuffer().getSize()) {
            DataBuffer dst = raster.getDataBuffer();
            DataBuffer src = alpha.getDataBuffer();
            int i = 0;
            for (int c = dst.getSize(); c > 0; --c) {
                dst.setElem(i, dst.getElem(i) & 0xFFFFFF | ~src.getElem(i) << 24);
                ++i;
            }
        } else if (matte == null) {
            int[] samples = new int[width];
            for (int y = 0; y < height; ++y) {
                alpha.getSamples(0, y, width, 1, 0, samples);
                if (!isSoft) {
                    int x = 0;
                    while (x < width) {
                        int n = x++;
                        samples[n] = ~samples[n];
                    }
                }
                raster.setSamples(0, y, width, 1, 3, samples);
            }
        } else {
            int[] alphas = new int[width];
            int[] pixels = new int[4 * width];
            int fraction = 15;
            int factor = 8355840;
            int m0 = Math.round(8355840.0f * matte[0]) * 255;
            int m1 = Math.round(8355840.0f * matte[1]) * 255;
            int m2 = Math.round(8355840.0f * matte[2]) * 255;
            int m0h = m0 / 255 + 16384;
            int m1h = m1 / 255 + 16384;
            int m2h = m2 / 255 + 16384;
            for (int y = 0; y < height; ++y) {
                raster.getPixels(0, y, width, 1, pixels);
                alpha.getSamples(0, y, width, 1, 0, alphas);
                int offset = 0;
                for (int x = 0; x < width; ++x) {
                    int a = alphas[x];
                    if (a == 0) {
                        offset += 3;
                    } else {
                        pixels[offset] = PDImageXObject.clampColor((pixels[offset++] * 8355840 - m0) / a + m0h >> 15);
                        pixels[offset] = PDImageXObject.clampColor((pixels[offset++] * 8355840 - m1) / a + m1h >> 15);
                        pixels[offset] = PDImageXObject.clampColor((pixels[offset++] * 8355840 - m2) / a + m2h >> 15);
                    }
                    pixels[offset++] = a;
                }
                raster.setPixels(0, y, width, 1, pixels);
            }
        }
        return image;
    }

    private static int clampColor(int color) {
        return color < 0 ? 0 : (color > 255 ? 255 : color);
    }

    private static BufferedImage scaleImage(BufferedImage image, int width, int height, int type, boolean interpolate) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        boolean largeScale = width * height > 9000000 * (type == 10 ? 3 : 1);
        boolean bl = imgWidth != width || imgHeight != height;
        BufferedImage image2 = new BufferedImage(width, height, type);
        if (interpolate &= bl) {
            AffineTransform af = AffineTransform.getScaleInstance((double)width / (double)imgWidth, (double)height / (double)imgHeight);
            AffineTransformOp afo = new AffineTransformOp(af, largeScale ? 2 : 3);
            try {
                afo.filter(image, image2);
                return image2;
            }
            catch (ImagingOpException e) {
                LOG.warn((Object)e.getMessage(), (Throwable)e);
            }
        }
        Graphics2D g = image2.createGraphics();
        if (interpolate) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, largeScale ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, largeScale ? RenderingHints.VALUE_RENDER_DEFAULT : RenderingHints.VALUE_RENDER_QUALITY);
        }
        g.drawImage(image, 0, 0, width, height, 0, 0, imgWidth, imgHeight, null);
        g.dispose();
        return image2;
    }

    public PDImageXObject getMask() throws IOException {
        COSBase mask = this.getCOSObject().getDictionaryObject(COSName.MASK);
        if (mask instanceof COSArray) {
            return null;
        }
        COSStream cosStream = this.getCOSObject().getCOSStream(COSName.MASK);
        if (cosStream != null) {
            return new PDImageXObject(new PDStream(cosStream), null);
        }
        return null;
    }

    public COSArray getColorKeyMask() {
        COSBase mask = this.getCOSObject().getDictionaryObject(COSName.MASK);
        if (mask instanceof COSArray) {
            return (COSArray)mask;
        }
        return null;
    }

    public PDImageXObject getSoftMask() throws IOException {
        COSStream cosStream = this.getCOSObject().getCOSStream(COSName.SMASK);
        if (cosStream != null) {
            return new PDImageXObject(new PDStream(cosStream), null);
        }
        return null;
    }

    @Override
    public int getBitsPerComponent() {
        if (this.isStencil()) {
            return 1;
        }
        return this.getCOSObject().getInt(COSName.BITS_PER_COMPONENT, COSName.BPC);
    }

    @Override
    public void setBitsPerComponent(int bpc) {
        this.getCOSObject().setInt(COSName.BITS_PER_COMPONENT, bpc);
    }

    @Override
    public PDColorSpace getColorSpace() throws IOException {
        if (this.colorSpace == null) {
            COSBase cosBase = this.getCOSObject().getItem(COSName.COLORSPACE, COSName.CS);
            if (cosBase != null) {
                COSObject indirect = null;
                if (cosBase instanceof COSObject && this.resources != null && this.resources.getResourceCache() != null) {
                    indirect = (COSObject)cosBase;
                    this.colorSpace = this.resources.getResourceCache().getColorSpace(indirect);
                    if (this.colorSpace != null) {
                        return this.colorSpace;
                    }
                }
                this.colorSpace = PDColorSpace.create(cosBase, this.resources);
                if (indirect != null) {
                    this.resources.getResourceCache().put(indirect, this.colorSpace);
                }
            } else {
                if (this.isStencil()) {
                    return PDDeviceGray.INSTANCE;
                }
                throw new IOException("could not determine color space");
            }
        }
        return this.colorSpace;
    }

    @Override
    public InputStream createInputStream() throws IOException {
        return this.getStream().createInputStream();
    }

    @Override
    public InputStream createInputStream(DecodeOptions options) throws IOException {
        return this.getStream().createInputStream(options);
    }

    @Override
    public InputStream createInputStream(List<String> stopFilters) throws IOException {
        return this.getStream().createInputStream(stopFilters);
    }

    @Override
    public boolean isEmpty() {
        return this.getStream().getCOSObject().getLength() == 0L;
    }

    @Override
    public void setColorSpace(PDColorSpace cs) {
        this.getCOSObject().setItem(COSName.COLORSPACE, cs != null ? cs.getCOSObject() : null);
        this.colorSpace = null;
        this.cachedImage = null;
    }

    @Override
    public int getHeight() {
        return this.getCOSObject().getInt(COSName.HEIGHT);
    }

    @Override
    public void setHeight(int h) {
        this.getCOSObject().setInt(COSName.HEIGHT, h);
    }

    @Override
    public int getWidth() {
        return this.getCOSObject().getInt(COSName.WIDTH);
    }

    @Override
    public void setWidth(int w) {
        this.getCOSObject().setInt(COSName.WIDTH, w);
    }

    @Override
    public boolean getInterpolate() {
        return this.getCOSObject().getBoolean(COSName.INTERPOLATE, false);
    }

    @Override
    public void setInterpolate(boolean value) {
        this.getCOSObject().setBoolean(COSName.INTERPOLATE, value);
    }

    @Override
    public void setDecode(COSArray decode) {
        this.getCOSObject().setItem(COSName.DECODE, (COSBase)decode);
    }

    @Override
    public COSArray getDecode() {
        COSBase decode = this.getCOSObject().getDictionaryObject(COSName.DECODE);
        if (decode instanceof COSArray) {
            return (COSArray)decode;
        }
        return null;
    }

    @Override
    public boolean isStencil() {
        return this.getCOSObject().getBoolean(COSName.IMAGE_MASK, false);
    }

    @Override
    public void setStencil(boolean isStencil) {
        this.getCOSObject().setBoolean(COSName.IMAGE_MASK, isStencil);
    }

    @Override
    public String getSuffix() {
        List<COSName> filters = this.getStream().getFilters();
        if (filters == null) {
            return "png";
        }
        if (filters.contains(COSName.DCT_DECODE)) {
            return "jpg";
        }
        if (filters.contains(COSName.JPX_DECODE)) {
            return "jpx";
        }
        if (filters.contains(COSName.CCITTFAX_DECODE)) {
            return "tiff";
        }
        if (filters.contains(COSName.FLATE_DECODE) || filters.contains(COSName.LZW_DECODE) || filters.contains(COSName.RUN_LENGTH_DECODE)) {
            return "png";
        }
        if (filters.contains(COSName.JBIG2_DECODE)) {
            return "jb2";
        }
        LOG.warn((Object)("getSuffix() returns null, filters: " + filters));
        return null;
    }

    public PDPropertyList getOptionalContent() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.OC);
        if (base instanceof COSDictionary) {
            return PDPropertyList.create((COSDictionary)base);
        }
        return null;
    }

    public void setOptionalContent(PDPropertyList oc) {
        this.getCOSObject().setItem(COSName.OC, (COSObjectable)oc);
    }
}

