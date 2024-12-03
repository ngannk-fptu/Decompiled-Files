/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.SampledImageReader;

public final class PDInlineImage
implements PDImage {
    private final COSDictionary parameters;
    private final PDResources resources;
    private final byte[] rawData;
    private final byte[] decodedData;

    public PDInlineImage(COSDictionary parameters, byte[] data, PDResources resources) throws IOException {
        this.parameters = parameters;
        this.resources = resources;
        this.rawData = data;
        DecodeResult decodeResult = null;
        List<String> filters = this.getFilters();
        if (filters == null || filters.isEmpty()) {
            this.decodedData = data;
        } else {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
            byte[] ba = null;
            for (int i = 0; i < filters.size(); ++i) {
                out.reset();
                Filter filter = FilterFactory.INSTANCE.getFilter(filters.get(i));
                decodeResult = filter.decode(in, out, parameters, i);
                ba = out.toByteArray();
                in = new ByteArrayInputStream(ba);
            }
            this.decodedData = ba;
        }
        if (decodeResult != null) {
            parameters.addAll(decodeResult.getParameters());
        }
    }

    @Override
    public COSBase getCOSObject() {
        return this.parameters;
    }

    @Override
    public int getBitsPerComponent() {
        if (this.isStencil()) {
            return 1;
        }
        return this.parameters.getInt(COSName.BPC, COSName.BITS_PER_COMPONENT, -1);
    }

    @Override
    public void setBitsPerComponent(int bitsPerComponent) {
        this.parameters.setInt(COSName.BPC, bitsPerComponent);
    }

    @Override
    public PDColorSpace getColorSpace() throws IOException {
        COSBase cs = this.parameters.getDictionaryObject(COSName.CS, COSName.COLORSPACE);
        if (cs != null) {
            return this.createColorSpace(cs);
        }
        if (this.isStencil()) {
            return PDDeviceGray.INSTANCE;
        }
        throw new IOException("could not determine inline image color space");
    }

    private COSBase toLongName(COSBase cs) {
        if (COSName.RGB.equals(cs)) {
            return COSName.DEVICERGB;
        }
        if (COSName.CMYK.equals(cs)) {
            return COSName.DEVICECMYK;
        }
        if (COSName.G.equals(cs)) {
            return COSName.DEVICEGRAY;
        }
        return cs;
    }

    private PDColorSpace createColorSpace(COSBase cs) throws IOException {
        if (cs instanceof COSName) {
            return PDColorSpace.create(this.toLongName(cs), this.resources);
        }
        if (cs instanceof COSArray && ((COSArray)cs).size() > 1) {
            COSArray srcArray = (COSArray)cs;
            COSBase csType = srcArray.get(0);
            if (COSName.I.equals(csType) || COSName.INDEXED.equals(csType)) {
                COSArray dstArray = new COSArray();
                dstArray.addAll(srcArray);
                dstArray.set(0, COSName.INDEXED);
                dstArray.set(1, this.toLongName(srcArray.get(1)));
                return PDColorSpace.create(dstArray, this.resources);
            }
            throw new IOException("Illegal type of inline image color space: " + csType);
        }
        throw new IOException("Illegal type of object for inline image color space: " + cs);
    }

    @Override
    public void setColorSpace(PDColorSpace colorSpace) {
        COSBase base = null;
        if (colorSpace != null) {
            base = colorSpace.getCOSObject();
        }
        this.parameters.setItem(COSName.CS, base);
    }

    @Override
    public int getHeight() {
        return this.parameters.getInt(COSName.H, COSName.HEIGHT, -1);
    }

    @Override
    public void setHeight(int height) {
        this.parameters.setInt(COSName.H, height);
    }

    @Override
    public int getWidth() {
        return this.parameters.getInt(COSName.W, COSName.WIDTH, -1);
    }

    @Override
    public void setWidth(int width) {
        this.parameters.setInt(COSName.W, width);
    }

    @Override
    public boolean getInterpolate() {
        return this.parameters.getBoolean(COSName.I, COSName.INTERPOLATE, false);
    }

    @Override
    public void setInterpolate(boolean value) {
        this.parameters.setBoolean(COSName.I, value);
    }

    public List<String> getFilters() {
        List<String> names = null;
        COSBase filters = this.parameters.getDictionaryObject(COSName.F, COSName.FILTER);
        if (filters instanceof COSName) {
            COSName name = (COSName)filters;
            names = new COSArrayList<String>(name.getName(), name, this.parameters, COSName.FILTER);
        } else if (filters instanceof COSArray) {
            names = COSArrayList.convertCOSNameCOSArrayToList((COSArray)filters);
        }
        return names;
    }

    public void setFilters(List<String> filters) {
        COSArray obj = COSArrayList.convertStringListToCOSNameCOSArray(filters);
        this.parameters.setItem(COSName.F, (COSBase)obj);
    }

    @Override
    public void setDecode(COSArray decode) {
        this.parameters.setItem(COSName.D, (COSBase)decode);
    }

    @Override
    public COSArray getDecode() {
        return (COSArray)this.parameters.getDictionaryObject(COSName.D, COSName.DECODE);
    }

    @Override
    public boolean isStencil() {
        return this.parameters.getBoolean(COSName.IM, COSName.IMAGE_MASK, false);
    }

    @Override
    public void setStencil(boolean isStencil) {
        this.parameters.setBoolean(COSName.IM, isStencil);
    }

    @Override
    public InputStream createInputStream() throws IOException {
        return new ByteArrayInputStream(this.decodedData);
    }

    @Override
    public InputStream createInputStream(DecodeOptions options) throws IOException {
        return this.createInputStream();
    }

    @Override
    public InputStream createInputStream(List<String> stopFilters) throws IOException {
        List<String> filters = this.getFilters();
        ByteArrayInputStream in = new ByteArrayInputStream(this.rawData);
        ByteArrayOutputStream out = new ByteArrayOutputStream(this.rawData.length);
        for (int i = 0; filters != null && i < filters.size(); ++i) {
            out.reset();
            if (stopFilters.contains(filters.get(i))) break;
            Filter filter = FilterFactory.INSTANCE.getFilter(filters.get(i));
            filter.decode(in, out, this.parameters, i);
            in = new ByteArrayInputStream(out.toByteArray());
        }
        return in;
    }

    @Override
    public boolean isEmpty() {
        return this.decodedData.length == 0;
    }

    public byte[] getData() {
        return this.decodedData;
    }

    @Override
    public BufferedImage getImage() throws IOException {
        return SampledImageReader.getRGBImage(this, null);
    }

    @Override
    public BufferedImage getImage(Rectangle region, int subsampling) throws IOException {
        return SampledImageReader.getRGBImage(this, region, subsampling, null);
    }

    @Override
    public WritableRaster getRawRaster() throws IOException {
        return SampledImageReader.getRawRaster(this);
    }

    @Override
    public BufferedImage getRawImage() throws IOException {
        return this.getColorSpace().toRawImage(this.getRawRaster());
    }

    @Override
    public BufferedImage getStencilImage(Paint paint) throws IOException {
        if (!this.isStencil()) {
            throw new IllegalStateException("Image is not a stencil");
        }
        return SampledImageReader.getStencilImage(this, paint);
    }

    @Deprecated
    public COSArray getColorKeyMask() {
        COSBase mask = this.parameters.getDictionaryObject(COSName.IM, COSName.MASK);
        if (mask instanceof COSArray) {
            return (COSArray)mask;
        }
        return null;
    }

    @Override
    public String getSuffix() {
        List<String> filters = this.getFilters();
        if (filters == null || filters.isEmpty()) {
            return "png";
        }
        if (filters.contains(COSName.DCT_DECODE.getName()) || filters.contains(COSName.DCT_DECODE_ABBREVIATION.getName())) {
            return "jpg";
        }
        if (filters.contains(COSName.CCITTFAX_DECODE.getName()) || filters.contains(COSName.CCITTFAX_DECODE_ABBREVIATION.getName())) {
            return "tiff";
        }
        return "png";
    }
}

