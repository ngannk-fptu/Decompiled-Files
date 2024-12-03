/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import org.xerial.snappy.BitShuffleNative;
import org.xerial.snappy.OSInfo;
import org.xerial.snappy.SnappyApi;
import org.xerial.snappy.SnappyError;
import org.xerial.snappy.SnappyErrorCode;
import org.xerial.snappy.SnappyNative;

public class SnappyLoader {
    public static final String SNAPPY_SYSTEM_PROPERTIES_FILE = "org-xerial-snappy.properties";
    public static final String KEY_SNAPPY_LIB_PATH = "org.xerial.snappy.lib.path";
    public static final String KEY_SNAPPY_LIB_NAME = "org.xerial.snappy.lib.name";
    public static final String KEY_SNAPPY_PUREJAVA = "org.xerial.snappy.purejava";
    public static final String KEY_SNAPPY_TEMPDIR = "org.xerial.snappy.tempdir";
    public static final String KEY_SNAPPY_USE_SYSTEMLIB = "org.xerial.snappy.use.systemlib";
    public static final String KEY_SNAPPY_DISABLE_BUNDLED_LIBS = "org.xerial.snappy.disable.bundled.libs";
    private static boolean isLoaded = false;
    private static volatile SnappyApi snappyApi = null;
    private static volatile BitShuffleNative bitshuffleApi = null;
    private static File nativeLibFile = null;

    static void cleanUpExtractedNativeLib() {
        if (nativeLibFile != null && nativeLibFile.exists()) {
            boolean bl = nativeLibFile.delete();
            if (!bl) {
                // empty if block
            }
            snappyApi = null;
            bitshuffleApi = null;
        }
    }

    static synchronized void setSnappyApi(SnappyApi snappyApi) {
        SnappyLoader.snappyApi = snappyApi;
    }

    private static void loadSnappySystemProperties() {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SNAPPY_SYSTEM_PROPERTIES_FILE);
            if (inputStream == null) {
                return;
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String string = (String)enumeration.nextElement();
                if (!string.startsWith("org.xerial.snappy.") || System.getProperty(string) != null) continue;
                System.setProperty(string, properties.getProperty(string));
            }
        }
        catch (Throwable throwable) {
            System.err.println("Could not load 'org-xerial-snappy.properties' from classpath: " + throwable.toString());
        }
    }

    static synchronized SnappyApi loadSnappyApi() {
        if (snappyApi != null) {
            return snappyApi;
        }
        SnappyLoader.loadNativeLibrary();
        SnappyLoader.setSnappyApi(new SnappyNative());
        return snappyApi;
    }

    static synchronized BitShuffleNative loadBitShuffleApi() {
        if (bitshuffleApi != null) {
            return bitshuffleApi;
        }
        SnappyLoader.loadNativeLibrary();
        bitshuffleApi = new BitShuffleNative();
        return bitshuffleApi;
    }

    private static synchronized void loadNativeLibrary() {
        if (!isLoaded) {
            try {
                nativeLibFile = SnappyLoader.findNativeLibrary();
                if (nativeLibFile != null) {
                    System.load(nativeLibFile.getAbsolutePath());
                } else {
                    System.loadLibrary("snappyjava");
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, exception.getMessage());
            }
            isLoaded = true;
        }
    }

    private static boolean contentsEquals(InputStream inputStream, InputStream inputStream2) throws IOException {
        int n;
        if (!(inputStream instanceof BufferedInputStream)) {
            inputStream = new BufferedInputStream(inputStream);
        }
        if (!(inputStream2 instanceof BufferedInputStream)) {
            inputStream2 = new BufferedInputStream(inputStream2);
        }
        int n2 = inputStream.read();
        while (n2 != -1) {
            n = inputStream2.read();
            if (n2 != n) {
                return false;
            }
            n2 = inputStream.read();
        }
        n = inputStream2.read();
        return n == -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File extractLibraryFile(String string, String string2, String string3) {
        String string4 = string + "/" + string2;
        String string5 = UUID.randomUUID().toString();
        String string6 = String.format("snappy-%s-%s-%s", SnappyLoader.getVersion(), string5, string2);
        File file = new File(string3, string6);
        try {
            boolean bl;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                inputStream = SnappyLoader.getResourceAsInputStream(string4);
                try {
                    fileOutputStream = new FileOutputStream(file);
                    byte[] byArray = new byte[8192];
                    int n = 0;
                    while ((n = inputStream.read(byArray)) != -1) {
                        fileOutputStream.write(byArray, 0, n);
                    }
                }
                finally {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
            }
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                file.deleteOnExit();
            }
            boolean bl2 = bl = file.setReadable(true) && file.setWritable(true, true) && file.setExecutable(true);
            if (!bl) {
                // empty if block
            }
            InputStream inputStream2 = null;
            FileInputStream fileInputStream = null;
            try {
                inputStream2 = SnappyLoader.getResourceAsInputStream(string4);
                fileInputStream = new FileInputStream(file);
                if (!SnappyLoader.contentsEquals(inputStream2, fileInputStream)) {
                    throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, String.format("Failed to write a native library file at %s", file));
                }
            }
            finally {
                if (inputStream2 != null) {
                    inputStream2.close();
                }
                if (fileInputStream != null) {
                    ((InputStream)fileInputStream).close();
                }
            }
            return new File(string3, string6);
        }
        catch (IOException iOException) {
            iOException.printStackTrace(System.err);
            return null;
        }
    }

    static File findNativeLibrary() {
        boolean bl;
        Object object;
        File file;
        boolean bl2 = Boolean.parseBoolean(System.getProperty(KEY_SNAPPY_USE_SYSTEMLIB, "false"));
        boolean bl3 = Boolean.parseBoolean(System.getProperty(KEY_SNAPPY_DISABLE_BUNDLED_LIBS, "false"));
        if (bl2 || bl3) {
            return null;
        }
        String string = System.getProperty(KEY_SNAPPY_LIB_PATH);
        Object object2 = System.getProperty(KEY_SNAPPY_LIB_NAME);
        if (object2 == null) {
            object2 = System.mapLibraryName("snappyjava");
        }
        if (string != null && (file = new File(string, (String)object2)).exists()) {
            return file;
        }
        string = "/org/xerial/snappy/native/" + OSInfo.getNativeLibFolderPathForCurrentOS();
        boolean bl4 = SnappyLoader.hasResource(string + "/" + (String)object2);
        if (!bl4 && OSInfo.getOSName().equals("Mac")) {
            object = "libsnappyjava.dylib";
            if (SnappyLoader.hasResource(string + "/" + (String)object)) {
                object2 = object;
                bl4 = true;
            }
        }
        if (!bl4) {
            object = String.format("no native library is found for os.name=%s and os.arch=%s", OSInfo.getOSName(), OSInfo.getArchName());
            throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, (String)object);
        }
        object = new File(System.getProperty(KEY_SNAPPY_TEMPDIR, System.getProperty("java.io.tmpdir")));
        if (((File)object).exists() || !(bl = ((File)object).mkdirs())) {
            // empty if block
        }
        return SnappyLoader.extractLibraryFile(string, (String)object2, ((File)object).getAbsolutePath());
    }

    private static boolean hasResource(String string) {
        return SnappyLoader.class.getResource(string) != null;
    }

    public static String getVersion() {
        URL uRL = SnappyLoader.class.getResource("/META-INF/maven/org.xerial.snappy/snappy-java/pom.properties");
        if (uRL == null) {
            uRL = SnappyLoader.class.getResource("/org/xerial/snappy/VERSION");
        }
        String string = "unknown";
        try {
            if (uRL != null) {
                Properties properties = new Properties();
                properties.load(uRL.openStream());
                string = properties.getProperty("version", string);
                if (string.equals("unknown")) {
                    string = properties.getProperty("SNAPPY_VERSION", string);
                }
                string = string.trim().replaceAll("[^0-9M\\.]", "");
            }
        }
        catch (IOException iOException) {
            System.err.println(iOException);
        }
        return string;
    }

    private static InputStream getResourceAsInputStream(String string) throws IOException {
        URL uRL = SnappyLoader.class.getResource(string);
        URLConnection uRLConnection = uRL.openConnection();
        if (uRLConnection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection)uRLConnection;
            jarURLConnection.setUseCaches(false);
            return jarURLConnection.getInputStream();
        }
        return uRLConnection.getInputStream();
    }

    static {
        SnappyLoader.loadSnappySystemProperties();
    }
}

