/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.Jpeg2000;
import com.lowagie.text.Utilities;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageLoader {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getPngImage(URL url) {
        try (InputStream is = url.openStream();){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getGifImage(URL url) {
        try (InputStream is = url.openStream();){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getTiffImage(URL url) {
        try (InputStream is = url.openStream();){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getBmpImage(URL url) {
        try (InputStream is = url.openStream();){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getJpegImage(URL url) {
        try (InputStream is = url.openStream();){
            byte[] imageBytes = Utilities.toByteArray(is);
            Jpeg jpeg = new Jpeg(imageBytes);
            return jpeg;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getJpeg2000Image(URL url) {
        try (InputStream is = url.openStream();){
            byte[] imageBytes = Utilities.toByteArray(is);
            Jpeg2000 jpeg2000 = new Jpeg2000(imageBytes);
            return jpeg2000;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getGifImage(byte[] imageData) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(imageData);){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getPngImage(byte[] imageData) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(imageData);){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getBmpImage(byte[] imageData) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(imageData);){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Image getTiffImage(byte[] imageData) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(imageData);){
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = Image.getInstance(bufferedImage, null, false);
            return image;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static Image getJpegImage(byte[] imageData) {
        try {
            return new Jpeg(imageData);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static Image getJpeg2000Image(byte[] imageData) {
        try {
            return new Jpeg2000(imageData);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}

