/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageToFile {
    private static ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

    public static void serialize(BufferedImage image, File file) throws IOException {
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ImageToFile.encodeJPG(fos, image);
        fos.flush();
        fos.close();
    }

    public static void encodeJPG(OutputStream sos, BufferedImage image) throws IOException {
        ImageOutputStream ios = ImageIO.createImageOutputStream(sos);
        writer.setOutput(ios);
        writer.write(image);
    }
}

