/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesReader;
import org.w3c.dom.Document;

public class MimeTypesFactory {
    public static final String CUSTOM_MIMES_SYS_PROP = "tika.custom-mimetypes";

    public static MimeTypes create() {
        return new MimeTypes();
    }

    public static MimeTypes create(Document document) throws MimeTypeException {
        MimeTypes mimeTypes = new MimeTypes();
        new MimeTypesReader(mimeTypes).read(document);
        mimeTypes.init();
        return mimeTypes;
    }

    public static MimeTypes create(InputStream ... inputStreams) throws IOException, MimeTypeException {
        MimeTypes mimeTypes = new MimeTypes();
        MimeTypesReader reader = new MimeTypesReader(mimeTypes);
        for (InputStream inputStream : inputStreams) {
            reader.read(inputStream);
        }
        mimeTypes.init();
        return mimeTypes;
    }

    public static MimeTypes create(InputStream stream) throws IOException, MimeTypeException {
        return MimeTypesFactory.create(new InputStream[]{stream});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MimeTypes create(URL ... urls) throws IOException, MimeTypeException {
        InputStream[] streams = new InputStream[urls.length];
        for (int i = 0; i < streams.length; ++i) {
            streams[i] = urls[i].openStream();
        }
        try {
            MimeTypes mimeTypes = MimeTypesFactory.create(streams);
            return mimeTypes;
        }
        finally {
            for (InputStream stream : streams) {
                stream.close();
            }
        }
    }

    public static MimeTypes create(URL url) throws IOException, MimeTypeException {
        return MimeTypesFactory.create(new URL[]{url});
    }

    public static MimeTypes create(String filePath) throws IOException, MimeTypeException {
        return MimeTypesFactory.create(MimeTypesReader.class.getResource(filePath));
    }

    public static MimeTypes create(String coreFilePath, String extensionFilePath) throws IOException, MimeTypeException {
        return MimeTypesFactory.create(coreFilePath, extensionFilePath, null);
    }

    public static MimeTypes create(String coreFilePath, String extensionFilePath, ClassLoader classLoader) throws IOException, MimeTypeException {
        if (classLoader == null) {
            classLoader = MimeTypesReader.class.getClassLoader();
        }
        String classPrefix = MimeTypesReader.class.getPackage().getName().replace('.', '/') + "/";
        URL coreURL = classLoader.getResource(classPrefix + coreFilePath);
        ArrayList<URL> extensionURLs = Collections.list(classLoader.getResources(classPrefix + extensionFilePath));
        ArrayList<URL> urls = new ArrayList<URL>();
        urls.add(coreURL);
        urls.addAll(extensionURLs);
        String customMimesPath = System.getProperty(CUSTOM_MIMES_SYS_PROP);
        if (customMimesPath != null) {
            File externalFile = new File(customMimesPath);
            if (!externalFile.exists()) {
                throw new IOException("Specified custom mimetypes file not found: " + customMimesPath);
            }
            URL externalURL = externalFile.toURI().toURL();
            urls.add(externalURL);
        }
        return MimeTypesFactory.create(urls.toArray(new URL[urls.size()]));
    }
}

