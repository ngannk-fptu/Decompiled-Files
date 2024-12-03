/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class Mimetype {
    public static final String MIMETYPE_XML = "application/xml";
    public static final String MIMETYPE_HTML = "text/html";
    public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    public static final String MIMETYPE_GZIP = "application/x-gzip";
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_EVENT_STREAM = "application/vnd.amazon.eventstream";
    private static final Logger LOG = LoggerFactory.getLogger(Mimetype.class);
    private static final String MIME_TYPE_PATH = "software/amazon/awssdk/core/util/mime.types";
    private static final ClassLoader CLASS_LOADER = ClassLoaderHelper.classLoader(Mimetype.class);
    private static volatile Mimetype mimetype;
    private final Map<String, String> extensionToMimetype = new HashMap<String, String>();

    private Mimetype() {
        Optional.ofNullable(CLASS_LOADER).map(loader -> loader.getResourceAsStream(MIME_TYPE_PATH)).ifPresent(stream -> {
            try {
                this.loadAndReplaceMimetypes((InputStream)stream);
            }
            catch (IOException e) {
                LOG.debug("Failed to load mime types from file in the classpath: mime.types", (Throwable)e);
            }
            finally {
                IoUtils.closeQuietly((AutoCloseable)stream, null);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Mimetype getInstance() {
        if (mimetype != null) return mimetype;
        Class<Mimetype> clazz = Mimetype.class;
        synchronized (Mimetype.class) {
            if (mimetype != null) return mimetype;
            mimetype = new Mimetype();
            // ** MonitorExit[var0] (shouldn't be in output)
            return mimetype;
        }
    }

    public String getMimetype(Path path) {
        Validate.notNull((Object)path, (String)"path", (Object[])new Object[0]);
        Path file = path.getFileName();
        if (file != null) {
            return this.getMimetype(file.toString());
        }
        return MIMETYPE_OCTET_STREAM;
    }

    public String getMimetype(File file) {
        return this.getMimetype(file.toPath());
    }

    String getMimetype(String fileName) {
        String ext;
        int lastPeriodIndex = fileName.lastIndexOf(46);
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length() && this.extensionToMimetype.containsKey(ext = StringUtils.lowerCase((String)fileName.substring(lastPeriodIndex + 1)))) {
            return this.extensionToMimetype.get(ext);
        }
        return MIMETYPE_OCTET_STREAM;
    }

    private void loadAndReplaceMimetypes(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        br.lines().filter(line -> !line.startsWith("#")).forEach(line -> {
            StringTokenizer st = new StringTokenizer(line = line.trim(), " \t");
            if (st.countTokens() > 1) {
                String mimetype = st.nextToken();
                while (st.hasMoreTokens()) {
                    String extension = st.nextToken();
                    this.extensionToMimetype.put(StringUtils.lowerCase((String)extension), mimetype);
                }
            }
        });
    }
}

