/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.psl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.hc.client5.http.psl.DomainType;
import org.apache.hc.client5.http.psl.PublicSuffixList;
import org.apache.hc.client5.http.psl.PublicSuffixListParser;
import org.apache.hc.client5.http.psl.PublicSuffixMatcher;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE)
public final class PublicSuffixMatcherLoader {
    private static final Logger LOG = LoggerFactory.getLogger(PublicSuffixMatcherLoader.class);
    private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;

    private static PublicSuffixMatcher load(InputStream in) throws IOException {
        List<PublicSuffixList> lists = PublicSuffixListParser.INSTANCE.parseByType(new InputStreamReader(in, StandardCharsets.UTF_8));
        return new PublicSuffixMatcher(lists);
    }

    public static PublicSuffixMatcher load(URL url) throws IOException {
        Args.notNull((Object)url, (String)"URL");
        try (InputStream in = url.openStream();){
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(in);
            return publicSuffixMatcher;
        }
    }

    public static PublicSuffixMatcher load(File file) throws IOException {
        Args.notNull((Object)file, (String)"File");
        try (FileInputStream in = new FileInputStream(file);){
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(in);
            return publicSuffixMatcher;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static PublicSuffixMatcher getDefault() {
        if (DEFAULT_INSTANCE != null) return DEFAULT_INSTANCE;
        Class<PublicSuffixMatcherLoader> clazz = PublicSuffixMatcherLoader.class;
        synchronized (PublicSuffixMatcherLoader.class) {
            if (DEFAULT_INSTANCE != null) return DEFAULT_INSTANCE;
            URL url = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
            if (url != null) {
                try {
                    DEFAULT_INSTANCE = PublicSuffixMatcherLoader.load(url);
                }
                catch (IOException ex) {
                    LOG.warn("Failure loading public suffix list from default resource", (Throwable)ex);
                }
            } else {
                DEFAULT_INSTANCE = new PublicSuffixMatcher(DomainType.ICANN, Collections.singletonList("com"), null);
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return DEFAULT_INSTANCE;
        }
    }
}

