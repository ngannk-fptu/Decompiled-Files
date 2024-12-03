/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class NameDetector
implements Detector {
    private final Map<Pattern, MediaType> patterns;

    public NameDetector(Map<Pattern, MediaType> patterns) {
        this.patterns = patterns;
    }

    @Override
    public MediaType detect(InputStream input, Metadata metadata) {
        String name = metadata.get("resourceName");
        if (name != null) {
            int percent;
            int backslash;
            int slash;
            int question = name.indexOf(63);
            if (question != -1) {
                name = name.substring(0, question);
            }
            if ((slash = name.lastIndexOf(47)) != -1) {
                name = name.substring(slash + 1);
            }
            if ((backslash = name.lastIndexOf(92)) != -1) {
                name = name.substring(backslash + 1);
            }
            int hash = name.lastIndexOf(35);
            int dot = name.lastIndexOf(46);
            if (hash != -1 && (dot == -1 || hash > dot)) {
                name = name.substring(0, hash);
            }
            if ((percent = name.indexOf(37)) != -1) {
                try {
                    name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
                }
                catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("UTF-8 not supported", e);
                }
            }
            if ((name = name.trim()).length() > 0) {
                for (Map.Entry<Pattern, MediaType> entry : this.patterns.entrySet()) {
                    if (!entry.getKey().matcher(name).matches()) continue;
                    return entry.getValue();
                }
            }
        }
        return MediaType.OCTET_STREAM;
    }
}

