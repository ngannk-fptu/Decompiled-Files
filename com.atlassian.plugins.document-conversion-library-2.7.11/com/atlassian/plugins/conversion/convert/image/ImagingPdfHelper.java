/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aspose.pdf.Image
 *  aspose.pdf.ImageInfo
 *  aspose.pdf.MarginInfo
 *  aspose.pdf.PageSetup
 *  aspose.pdf.Paragraph
 *  aspose.pdf.Pdf
 *  aspose.pdf.Section
 *  com.aspose.imaging.Image
 *  com.aspose.imaging.ImageOptionsBase
 *  com.aspose.imaging.fileformats.tiff.TiffFrame
 *  com.aspose.imaging.fileformats.tiff.TiffImage
 *  com.aspose.imaging.imageoptions.PngOptions
 *  com.aspose.imaging.system.io.MemoryStream
 *  com.aspose.imaging.system.io.Stream
 *  com.aspose.psd.Image
 *  com.aspose.psd.ImageOptionsBase
 *  com.aspose.psd.imageoptions.PngOptions
 */
package com.atlassian.plugins.conversion.convert.image;

import aspose.pdf.ImageInfo;
import aspose.pdf.MarginInfo;
import aspose.pdf.PageSetup;
import aspose.pdf.Paragraph;
import aspose.pdf.Pdf;
import aspose.pdf.Section;
import com.aspose.imaging.ImageOptionsBase;
import com.aspose.imaging.fileformats.tiff.TiffFrame;
import com.aspose.imaging.fileformats.tiff.TiffImage;
import com.aspose.imaging.imageoptions.PngOptions;
import com.aspose.imaging.system.io.MemoryStream;
import com.aspose.imaging.system.io.Stream;
import com.aspose.psd.Image;
import com.atlassian.plugins.conversion.convert.FileFormat;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

public class ImagingPdfHelper {
    public static void convertToPdf(ImageReader reader, OutputStream outputStream) throws IOException {
        Pdf pdf = ImagingPdfHelper.createPdf();
        MemoryStream stream = new MemoryStream();
        int imgIdx = 0;
        try {
            while (true) {
                stream.setLength(0L);
                stream.setPosition(0L);
                BufferedImage image = reader.read(imgIdx++);
                ImageIO.write((RenderedImage)image, "png", stream.toOutputStream());
                stream.setPosition(0L);
                ImagingPdfHelper.addPdfPage(pdf, FileFormat.PNG, (Stream)stream, image.getWidth(), image.getHeight(), false);
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            pdf.save(outputStream);
            return;
        }
    }

    public static void convertToPdf(com.aspose.imaging.Image image, OutputStream outputStream) throws IOException {
        TiffFrame[] images;
        PngOptions opt = new PngOptions();
        if (image instanceof TiffImage) {
            TiffImage tifImage = (TiffImage)image;
            images = tifImage.getFrames();
        } else {
            images = new com.aspose.imaging.Image[]{image};
        }
        Pdf pdf = ImagingPdfHelper.createPdf();
        MemoryStream stream = new MemoryStream();
        for (TiffFrame img : images) {
            stream.setLength(0L);
            stream.setPosition(0L);
            img.save(stream.toOutputStream(), (ImageOptionsBase)opt);
            stream.setPosition(0L);
            ImagingPdfHelper.addPdfPage(pdf, FileFormat.PNG, (Stream)stream, img.getWidth(), img.getHeight(), false);
        }
        pdf.save(outputStream);
    }

    public static void convertToPdf(Image image, OutputStream outputStream) throws IOException {
        com.aspose.psd.imageoptions.PngOptions opt = new com.aspose.psd.imageoptions.PngOptions();
        Image[] images = new Image[]{image};
        Pdf pdf = ImagingPdfHelper.createPdf();
        MemoryStream stream = new MemoryStream();
        for (Image img : images) {
            stream.setLength(0L);
            stream.setPosition(0L);
            img.save(stream.toOutputStream(), (com.aspose.psd.ImageOptionsBase)opt);
            stream.setPosition(0L);
            ImagingPdfHelper.addPdfPage(pdf, FileFormat.PNG, (Stream)stream, img.getWidth(), img.getHeight(), false);
        }
        pdf.save(outputStream);
    }

    public static Pdf createPdf() {
        Pdf result = new Pdf(){

            public String getProducer() {
                return "Atlassian Confluence";
            }
        };
        result.setAuthor("Atlassian Confluence");
        result.setCreator("Atlassian Confluence");
        return result;
    }

    public static void addPdfPage(Pdf pdf, FileFormat imgFormat, Stream stream, double w, double h, boolean useSizeAsProvided) {
        Section sec = pdf.getSections().add();
        PageSetup pageInfo = sec.getPageInfo();
        pageInfo.setPageWidth((float)(useSizeAsProvided ? w : Math.max(64.0, w * (double)0.72f)));
        pageInfo.setPageHeight((float)(useSizeAsProvided ? h : Math.max(64.0, h * (double)0.72f)));
        MarginInfo margin = pageInfo.getMargin();
        if (!useSizeAsProvided) {
            margin.setTop(12.0f);
            margin.setBottom(12.0f);
            margin.setLeft(12.0f);
            margin.setRight(12.0f);
        } else {
            margin.setTop(0.0f);
            margin.setBottom(0.0f);
            margin.setLeft(0.0f);
            margin.setRight(0.0f);
            margin.setInner(0.0f);
            margin.setOuter(0.0f);
        }
        aspose.pdf.Image pdfImage = new aspose.pdf.Image(sec);
        sec.getParagraphs().add((Paragraph)pdfImage);
        ImageInfo imageInfo = pdfImage.getImageInfo();
        if (ImagingPdfHelper.isMac()) {
            imageInfo.getTextInfo().setFontName("Helvetica");
        }
        imageInfo.setAlignment(1);
        imageInfo.setImageStream(stream.toInputStream());
        int imageFileType = -1;
        switch (imgFormat) {
            case PNG: {
                imageFileType = 3;
                break;
            }
            case JPG: {
                imageFileType = 2;
                break;
            }
            case EMF: {
                imageFileType = 6;
            }
        }
        imageInfo.setImageFileType(imageFileType);
    }

    private static boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("mac os x");
    }
}

