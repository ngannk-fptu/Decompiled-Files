/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.backgroundgenerator.AbstractBackgroundGenerator;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

public class FileReaderRandomBackgroundGenerator
extends AbstractBackgroundGenerator {
    private List images = new ArrayList();
    private String rootPath = ".";
    protected static final Map cachedDirectories = new HashMap();

    public FileReaderRandomBackgroundGenerator(Integer width, Integer height, String rootPath) {
        super(width, height);
        File dir;
        File[] files;
        if (rootPath != null) {
            this.rootPath = rootPath;
        }
        if ((files = (dir = this.findDirectory(this.rootPath)).listFiles()) != null) {
            for (File file : files) {
                BufferedImage out = null;
                if (file.isFile()) {
                    out = FileReaderRandomBackgroundGenerator.getImage(file);
                }
                if (out == null) continue;
                this.images.add(this.images.size(), out);
            }
            if (this.images.size() != 0) {
                for (int i = 0; i < this.images.size(); ++i) {
                    BufferedImage bufferedImage = (BufferedImage)this.images.get(i);
                    this.images.set(i, this.tile(bufferedImage));
                }
            } else {
                throw new CaptchaException("Root path directory is valid but does not contains any image (jpg) files");
            }
        }
    }

    protected File findDirectory(String rootPath) {
        if (cachedDirectories.containsKey(rootPath)) {
            return (File)cachedDirectories.get(rootPath);
        }
        File dir = new File(rootPath);
        StringBuffer triedPath = new StringBuffer();
        this.appendFilePath(triedPath, dir);
        if (this.isNotReadable(dir)) {
            dir = new File(".", rootPath);
            this.appendFilePath(triedPath, dir);
            if (this.isNotReadable(dir)) {
                dir = new File("/", rootPath);
                this.appendFilePath(triedPath, dir);
                if (this.isNotReadable(dir)) {
                    URL url = FileReaderRandomBackgroundGenerator.class.getClassLoader().getResource(rootPath);
                    if (url != null) {
                        dir = new File(this.getFilePath(url));
                        this.appendFilePath(triedPath, dir);
                    } else {
                        url = ClassLoader.getSystemClassLoader().getResource(rootPath);
                        if (url != null) {
                            dir = new File(this.getFilePath(url));
                            this.appendFilePath(triedPath, dir);
                        }
                    }
                }
            }
        }
        if (this.isNotReadable(dir)) {
            StringTokenizer token = this.getClasspathFromSystemProperty();
            while (token.hasMoreElements()) {
                String path = token.nextToken();
                if (path.endsWith(".jar")) continue;
                dir = new File(path, rootPath);
                this.appendFilePath(triedPath, dir);
                if (!dir.canRead() || !dir.isDirectory()) continue;
                break;
            }
        }
        if (this.isNotReadable(dir)) {
            throw new CaptchaException("All tried paths :'" + triedPath.toString() + "' is not" + " a directory or cannot be read");
        }
        cachedDirectories.put(rootPath, dir);
        return dir;
    }

    private String getFilePath(URL url) {
        String file = null;
        try {
            file = URLDecoder.decode(url.getFile(), "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return file;
    }

    private boolean isNotReadable(File dir) {
        return !dir.canRead() || !dir.isDirectory();
    }

    private StringTokenizer getClasspathFromSystemProperty() {
        String classpath = System.getProperty("java.class.path");
        return new StringTokenizer(classpath, File.pathSeparator);
    }

    private void appendFilePath(StringBuffer triedPath, File dir) {
        triedPath.append(dir.getAbsolutePath());
        triedPath.append("\n");
    }

    private BufferedImage tile(BufferedImage tileImage) {
        BufferedImage image = new BufferedImage(this.getImageWidth(), this.getImageHeight(), tileImage.getType());
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        int NumberX = this.getImageWidth() / tileImage.getWidth();
        int NumberY = this.getImageHeight() / tileImage.getHeight();
        for (int k = 0; k <= NumberY; ++k) {
            for (int l = 0; l <= NumberX; ++l) {
                g2.drawImage(tileImage, l * tileImage.getWidth(), k * tileImage.getHeight(), Math.min(tileImage.getWidth(), this.getImageWidth()), Math.min(tileImage.getHeight(), this.getImageHeight()), null);
            }
        }
        g2.dispose();
        return image;
    }

    private static BufferedImage getImage(File o) {
        try {
            FileInputStream fis = new FileInputStream(o);
            BufferedImage out = ImageIO.read(fis);
            fis.close();
            return out;
        }
        catch (IOException e) {
            throw new CaptchaException("Unknown error during file reading ", (Throwable)e);
        }
    }

    @Override
    public BufferedImage getBackground() {
        return (BufferedImage)this.images.get(this.myRandom.nextInt(this.images.size()));
    }
}

