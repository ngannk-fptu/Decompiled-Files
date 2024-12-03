/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.lookup;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;

final class UrlStringLookup
extends AbstractStringLookup {
    static final UrlStringLookup INSTANCE = new UrlStringLookup();

    private UrlStringLookup() {
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(SPLIT_STR);
        int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad URL key format [%s]; expected format is DocumentPath:Key.", key);
        }
        String charsetName = keys[0];
        String urlStr = StringUtils.substringAfter((String)key, (int)58);
        try {
            URL url = new URL(urlStr);
            int size = 8192;
            StringWriter writer = new StringWriter(8192);
            char[] buffer = new char[8192];
            try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
                 InputStreamReader reader = new InputStreamReader((InputStream)bis, charsetName);){
                int n;
                while (-1 != (n = reader.read(buffer))) {
                    writer.write(buffer, 0, n);
                }
            }
            return writer.toString();
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up URL [%s] with Charset [%s].", urlStr, charsetName);
        }
    }
}

