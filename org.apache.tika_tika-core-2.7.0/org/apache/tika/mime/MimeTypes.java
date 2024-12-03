/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.tika.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.detect.XmlRootExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.Magic;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypesFactory;
import org.apache.tika.mime.Patterns;

public final class MimeTypes
implements Detector,
Serializable {
    public static final String OCTET_STREAM = "application/octet-stream";
    public static final String PLAIN_TEXT = "text/plain";
    public static final String XML = "application/xml";
    private static final long serialVersionUID = -1350863170146349036L;
    private static final Map<ClassLoader, MimeTypes> CLASSLOADER_SPECIFIC_DEFAULT_TYPES = new HashMap<ClassLoader, MimeTypes>();
    private static MimeTypes DEFAULT_TYPES = null;
    private final MimeType rootMimeType;
    private final List<MimeType> rootMimeTypeL;
    private final MimeType textMimeType;
    private final MimeType htmlMimeType;
    private final MimeType xmlMimeType;
    private final MediaTypeRegistry registry = new MediaTypeRegistry();
    private final Map<MediaType, MimeType> types = new HashMap<MediaType, MimeType>();
    private final Patterns patterns = new Patterns(this.registry);
    private final List<Magic> magics = new ArrayList<Magic>();
    private final List<MimeType> xmls = new ArrayList<MimeType>();

    public MimeTypes() {
        this.rootMimeType = new MimeType(MediaType.OCTET_STREAM);
        this.textMimeType = new MimeType(MediaType.TEXT_PLAIN);
        this.htmlMimeType = new MimeType(MediaType.TEXT_HTML);
        this.xmlMimeType = new MimeType(MediaType.APPLICATION_XML);
        this.rootMimeTypeL = Collections.singletonList(this.rootMimeType);
        this.add(this.rootMimeType);
        this.add(this.textMimeType);
        this.add(this.xmlMimeType);
    }

    public static synchronized MimeTypes getDefaultMimeTypes() {
        return MimeTypes.getDefaultMimeTypes(null);
    }

    public static synchronized MimeTypes getDefaultMimeTypes(ClassLoader classLoader) {
        MimeTypes types = DEFAULT_TYPES;
        if (classLoader != null) {
            types = CLASSLOADER_SPECIFIC_DEFAULT_TYPES.get(classLoader);
        }
        if (types == null) {
            try {
                types = MimeTypesFactory.create("tika-mimetypes.xml", "custom-mimetypes.xml", classLoader);
            }
            catch (MimeTypeException e) {
                throw new RuntimeException("Unable to parse the default media type registry", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to read the default media type registry", e);
            }
            if (classLoader == null) {
                DEFAULT_TYPES = types;
            } else {
                CLASSLOADER_SPECIFIC_DEFAULT_TYPES.put(classLoader, types);
            }
        }
        return types;
    }

    public MimeType getMimeType(String name) {
        MimeType type = this.patterns.matches(name);
        if (type != null) {
            return type;
        }
        type = this.patterns.matches(name.toLowerCase(Locale.ENGLISH));
        if (type != null) {
            return type;
        }
        return this.rootMimeType;
    }

    public MimeType getMimeType(File file) throws MimeTypeException, IOException {
        return this.forName(new Tika(this).detect(file));
    }

    List<MimeType> getMimeType(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is missing");
        }
        if (data.length == 0) {
            return this.rootMimeTypeL;
        }
        ArrayList<MimeType> result = new ArrayList<MimeType>(1);
        int currentPriority = -1;
        for (Magic magic : this.magics) {
            if (currentPriority > 0 && currentPriority > magic.getPriority()) break;
            if (!magic.eval(data)) continue;
            result.add(magic.getType());
            currentPriority = magic.getPriority();
        }
        if (!result.isEmpty()) {
            block3: for (int i = 0; i < result.size(); ++i) {
                MimeType matched = (MimeType)result.get(i);
                if (!XML.equals(matched.getName()) && !"text/html".equals(matched.getName())) continue;
                XmlRootExtractor extractor = new XmlRootExtractor();
                QName rootElement = extractor.extractRootElement(data);
                if (rootElement != null) {
                    for (MimeType type : this.xmls) {
                        if (!type.matchesXML(rootElement.getNamespaceURI(), rootElement.getLocalPart())) continue;
                        result.set(i, type);
                        continue block3;
                    }
                    continue;
                }
                if (!XML.equals(matched.getName())) continue;
                boolean isHTML = false;
                for (Magic magic : this.magics) {
                    if (!magic.getType().equals(this.htmlMimeType) || !magic.eval(data)) continue;
                    isHTML = true;
                    break;
                }
                if (isHTML) {
                    result.set(i, this.htmlMimeType);
                    continue;
                }
                result.set(i, this.textMimeType);
            }
            return result;
        }
        try {
            TextDetector detector = new TextDetector(this.getMinLength());
            UnsynchronizedByteArrayInputStream stream = new UnsynchronizedByteArrayInputStream(data);
            MimeType type = this.forName(detector.detect((InputStream)stream, new Metadata()).toString());
            return Collections.singletonList(type);
        }
        catch (Exception e) {
            return this.rootMimeTypeL;
        }
    }

    byte[] readMagicHeader(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("InputStream is missing");
        }
        byte[] bytes = new byte[this.getMinLength()];
        int totalRead = 0;
        int lastRead = stream.read(bytes);
        while (lastRead != -1) {
            if ((totalRead += lastRead) == bytes.length) {
                return bytes;
            }
            lastRead = stream.read(bytes, totalRead, bytes.length - totalRead);
        }
        byte[] shorter = new byte[totalRead];
        System.arraycopy(bytes, 0, shorter, 0, totalRead);
        return shorter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MimeType forName(String name) throws MimeTypeException {
        MediaType type = MediaType.parse(name);
        if (type != null) {
            MediaType normalisedType = this.registry.normalize(type);
            MimeType mime = this.types.get(normalisedType);
            if (mime == null) {
                MimeTypes mimeTypes = this;
                synchronized (mimeTypes) {
                    mime = this.types.get(normalisedType);
                    if (mime == null) {
                        mime = new MimeType(type);
                        this.add(mime);
                        this.types.put(type, mime);
                    }
                }
            }
            return mime;
        }
        throw new MimeTypeException("Invalid media type name: " + name);
    }

    public MimeType getRegisteredMimeType(String name) throws MimeTypeException {
        MediaType type = MediaType.parse(name);
        if (type != null) {
            MediaType normalisedType = this.registry.normalize(type);
            MimeType candidate = this.types.get(normalisedType);
            if (candidate != null) {
                return candidate;
            }
            if (normalisedType.hasParameters()) {
                return this.types.get(normalisedType.getBaseType());
            }
            return null;
        }
        throw new MimeTypeException("Invalid media type name: " + name);
    }

    public synchronized void setSuperType(MimeType type, MediaType parent) {
        this.registry.addSuperType(type.getType(), parent);
    }

    synchronized void addAlias(MimeType type, MediaType alias) {
        this.registry.addAlias(type.getType(), alias);
    }

    public void addPattern(MimeType type, String pattern) throws MimeTypeException {
        this.addPattern(type, pattern, false);
    }

    public void addPattern(MimeType type, String pattern, boolean isRegex) throws MimeTypeException {
        this.patterns.add(pattern, isRegex, type);
    }

    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.registry;
    }

    public int getMinLength() {
        return 65536;
    }

    void add(MimeType type) {
        this.registry.addType(type.getType());
        this.types.put(type.getType(), type);
        if (type.hasMagic()) {
            this.magics.addAll(type.getMagics());
        }
        if (type.hasRootXML()) {
            this.xmls.add(type);
        }
    }

    void init() {
        for (MimeType type : this.types.values()) {
            this.magics.addAll(type.getMagics());
            if (!type.hasRootXML()) continue;
            this.xmls.add(type);
        }
        Collections.sort(this.magics);
        Collections.sort(this.xmls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        String typeName;
        String resourceName;
        List<MimeType> possibleTypes = null;
        if (input != null) {
            input.mark(this.getMinLength());
            try {
                byte[] prefix = this.readMagicHeader(input);
                possibleTypes = this.getMimeType(prefix);
            }
            finally {
                input.reset();
            }
        }
        if ((resourceName = metadata.get("resourceName")) != null) {
            String name = null;
            boolean isHttp = false;
            try {
                int slash;
                URI uri = new URI(resourceName);
                String scheme = uri.getScheme();
                isHttp = scheme != null && scheme.startsWith("http");
                String path = uri.getPath();
                if (path != null && (slash = path.lastIndexOf(47)) + 1 < path.length()) {
                    name = path.substring(slash + 1);
                }
            }
            catch (URISyntaxException e) {
                name = resourceName;
            }
            if (name != null) {
                MimeType hint = this.getMimeType(name);
                if (!isHttp || !hint.isInterpreted()) {
                    possibleTypes = this.applyHint(possibleTypes, hint);
                }
            }
        }
        if ((typeName = metadata.get("Content-Type")) != null) {
            try {
                MimeType hint = this.forName(typeName);
                possibleTypes = this.applyHint(possibleTypes, hint);
            }
            catch (MimeTypeException mimeTypeException) {
                // empty catch block
            }
        }
        if (possibleTypes == null || possibleTypes.isEmpty()) {
            return MediaType.OCTET_STREAM;
        }
        return possibleTypes.get(0).getType();
    }

    private List<MimeType> applyHint(List<MimeType> possibleTypes, MimeType hint) {
        if (possibleTypes == null || possibleTypes.isEmpty()) {
            return Collections.singletonList(hint);
        }
        for (MimeType type : possibleTypes) {
            if (!hint.equals(type) && !this.registry.isSpecializationOf(hint.getType(), type.getType())) continue;
            return Collections.singletonList(hint);
        }
        return possibleTypes;
    }
}

