/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.io.FileUtil
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.io.FileUtil;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

final class SipsJP2Reader {
    private static final File SIPS_COMMAND = new File("/usr/bin/sips");
    private static final boolean SIPS_EXISTS_AND_EXECUTES = SipsJP2Reader.existsAndExecutes(SIPS_COMMAND);
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.icns.debug"));
    private ImageInputStream input;

    SipsJP2Reader() {
    }

    private static boolean existsAndExecutes(File file) {
        try {
            return file.exists() && file.canExecute();
        }
        catch (SecurityException securityException) {
            if (DEBUG) {
                securityException.printStackTrace();
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        File file;
        if (SIPS_EXISTS_AND_EXECUTES && SipsJP2Reader.convertToPNG(file = SipsJP2Reader.dumpToFile(this.input))) {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            while (iterator.hasNext()) {
                ImageReader imageReader = iterator.next();
                imageReader.setInput(imageInputStream);
                try {
                    BufferedImage bufferedImage = imageReader.read(n, imageReadParam);
                    return bufferedImage;
                }
                catch (IOException iOException) {
                    if (imageInputStream.getFlushedPosition() <= 0L) {
                        imageInputStream.seek(0L);
                        continue;
                    }
                    imageInputStream.close();
                    imageInputStream = ImageIO.createImageInputStream(file);
                }
                finally {
                    imageReader.dispose();
                }
            }
        }
        return null;
    }

    public void setInput(ImageInputStream imageInputStream) {
        this.input = imageInputStream;
    }

    private static boolean convertToPNG(File file) throws IIOException {
        try {
            Process process = Runtime.getRuntime().exec(SipsJP2Reader.buildCommand(SIPS_COMMAND, file));
            int n = process.waitFor();
            String string = SipsJP2Reader.checkErrorMessage(process);
            if (n == 0 && string == null) {
                return true;
            }
            throw new IOException(string);
        }
        catch (InterruptedException interruptedException) {
            throw new IIOException("Interrupted converting JPEG 2000 format", interruptedException);
        }
        catch (SecurityException securityException) {
            throw new IIOException("Cannot convert JPEG 2000 format without file permissions", securityException);
        }
        catch (IOException iOException) {
            throw new IIOException("Error converting JPEG 2000 format: " + iOException.getMessage(), iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String checkErrorMessage(Process process) throws IOException {
        try (InputStream inputStream = process.getErrorStream();){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String string = bufferedReader.readLine();
            String string2 = string != null && string.startsWith("Error: ") ? string.substring(7) : null;
            return string2;
        }
    }

    private static String[] buildCommand(File file, File file2) {
        return new String[]{file.getAbsolutePath(), "-s", "format", "png", file2.getAbsolutePath()};
    }

    private static File dumpToFile(ImageInputStream imageInputStream) throws IOException {
        File file = File.createTempFile("imageio-icns-", ".png");
        file.deleteOnExit();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
            FileUtil.copy((InputStream)IIOUtil.createStreamAdapter((ImageInputStream)imageInputStream), (OutputStream)fileOutputStream);
        }
        return file;
    }

    static boolean isAvailable() {
        return SIPS_EXISTS_AND_EXECUTES;
    }
}

