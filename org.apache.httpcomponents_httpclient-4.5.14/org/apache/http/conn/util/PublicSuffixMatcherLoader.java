/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Consts
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.conn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.PublicSuffixList;
import org.apache.http.conn.util.PublicSuffixListParser;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public final class PublicSuffixMatcherLoader {
    private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;

    private static PublicSuffixMatcher load(InputStream in) throws IOException {
        List<PublicSuffixList> lists = new PublicSuffixListParser().parseByType(new InputStreamReader(in, Consts.UTF_8));
        return new PublicSuffixMatcher(lists);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PublicSuffixMatcher load(URL url) throws IOException {
        Args.notNull((Object)url, (String)"URL");
        InputStream in = url.openStream();
        try {
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(in);
            return publicSuffixMatcher;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PublicSuffixMatcher load(File file) throws IOException {
        Args.notNull((Object)file, (String)"File");
        FileInputStream in = new FileInputStream(file);
        try {
            PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(in);
            return publicSuffixMatcher;
        }
        finally {
            ((InputStream)in).close();
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
                    Log log = LogFactory.getLog(PublicSuffixMatcherLoader.class);
                    if (!log.isWarnEnabled()) return DEFAULT_INSTANCE;
                    log.warn((Object)"Failure loading public suffix list from default resource", (Throwable)ex);
                }
            } else {
                DEFAULT_INSTANCE = new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList("com"), null);
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return DEFAULT_INSTANCE;
        }
    }
}

