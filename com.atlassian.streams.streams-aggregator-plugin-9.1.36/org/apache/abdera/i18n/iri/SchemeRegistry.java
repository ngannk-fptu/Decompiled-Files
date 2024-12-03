/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.i18n.iri.DefaultScheme;
import org.apache.abdera.i18n.iri.FtpScheme;
import org.apache.abdera.i18n.iri.HttpScheme;
import org.apache.abdera.i18n.iri.HttpsScheme;
import org.apache.abdera.i18n.iri.Scheme;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SchemeRegistry {
    private static SchemeRegistry registry;
    private final Map<String, Scheme> schemes = new HashMap<String, Scheme>();

    public static synchronized SchemeRegistry getInstance() {
        if (registry == null) {
            registry = new SchemeRegistry();
        }
        return registry;
    }

    SchemeRegistry() {
        this.schemes.put("http", new HttpScheme());
        this.schemes.put("https", new HttpsScheme());
        this.schemes.put("ftp", new FtpScheme());
    }

    public synchronized boolean register(String schemeClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<Scheme> klass = Thread.currentThread().getContextClassLoader().loadClass(schemeClass);
        return this.register(klass);
    }

    public synchronized boolean register(Class<Scheme> schemeClass) throws IllegalAccessException, InstantiationException {
        Scheme scheme = schemeClass.newInstance();
        return this.register(scheme);
    }

    public synchronized boolean register(Scheme scheme) {
        String name = scheme.getName();
        if (this.schemes.get(name) == null) {
            this.schemes.put(name.toLowerCase(), scheme);
            return true;
        }
        return false;
    }

    public Scheme getScheme(String scheme) {
        if (scheme == null) {
            return null;
        }
        Scheme s = this.schemes.get(scheme.toLowerCase());
        return s != null ? s : new DefaultScheme(scheme);
    }
}

