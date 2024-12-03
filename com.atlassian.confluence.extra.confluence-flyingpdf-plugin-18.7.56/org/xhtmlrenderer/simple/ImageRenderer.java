/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

public class ImageRenderer {
    public static final int DEFAULT_WIDTH = 1024;

    public static BufferedImage renderToImage(String url, String path, int width) throws IOException {
        return ImageRenderer.renderImageToOutput(url, new FSImageWriter(), path, width);
    }

    public static BufferedImage renderToImage(String url, String path, int width, int height) throws IOException {
        return ImageRenderer.renderImageToOutput(url, new FSImageWriter(), path, width);
    }

    public static BufferedImage renderToImage(File inFile, String path, int width) throws IOException {
        return ImageRenderer.renderToImage(inFile.toURI().toURL().toExternalForm(), path, width);
    }

    public static BufferedImage renderToImage(File inFile, String path, int width, int height) throws IOException {
        return ImageRenderer.renderToImage(inFile.toURI().toURL().toExternalForm(), path, width, height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BufferedImage renderImageToOutput(String url, FSImageWriter fsw, String path, int width) throws IOException {
        OutputStream os = null;
        try {
            Java2DRenderer renderer = new Java2DRenderer(url, url, width);
            os = new BufferedOutputStream(new FileOutputStream(path));
            BufferedImage image = renderer.getImage();
            fsw.write(image, os);
            BufferedImage bufferedImage = image;
            return bufferedImage;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String url;
        if (args.length != 1) {
            ImageRenderer.usage("Incorrect argument list.");
        }
        if ((url = args[0]).indexOf("://") == -1) {
            File f = new File(url);
            if (f.exists()) {
                String output = f.getAbsolutePath();
                output = output.substring(0, output.lastIndexOf(".")) + ".png";
                System.out.println("Saving image to " + output);
                ImageRenderer.renderToImage(f, output, 1024);
            } else {
                ImageRenderer.usage("File to render is not found: " + url);
            }
        } else {
            File out = File.createTempFile("fs", ".png");
            System.out.println("Saving image to " + out.getAbsolutePath());
            ImageRenderer.renderToImage(url, out.getAbsolutePath(), 1024);
        }
    }

    private static void usage(String err) {
        if (err != null && err.length() > 0) {
            System.err.println("==>" + err);
        }
        System.err.println("Usage: ... [url]");
        System.exit(1);
    }
}

