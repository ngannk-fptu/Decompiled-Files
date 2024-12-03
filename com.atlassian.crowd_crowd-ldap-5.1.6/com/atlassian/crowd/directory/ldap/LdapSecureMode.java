/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.crowd.directory.ldap;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(value=Adapter.class)
public enum LdapSecureMode {
    NONE("false", "ldap", 389),
    LDAPS("true", "ldaps", 636),
    START_TLS("starttls", "ldap", 389);

    private static final Splitter SPACE_SPLITTER;
    private String name;
    private String protocol;
    private int defaultPort;

    private LdapSecureMode(String name, String protocol, int defaultPort) {
        this.name = name;
        this.protocol = protocol;
        this.defaultPort = defaultPort;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public static LdapSecureMode fromString(String name) {
        for (LdapSecureMode mode : LdapSecureMode.values()) {
            if (!mode.getName().equalsIgnoreCase(name)) continue;
            return mode;
        }
        return NONE;
    }

    public static LdapSecureMode fromUrl(String url) {
        Iterable urls = SPACE_SPLITTER.split((CharSequence)Strings.nullToEmpty((String)url));
        if (urls.iterator().hasNext()) {
            try {
                URI uri = new URI((String)urls.iterator().next());
                if (LDAPS.getProtocol().equalsIgnoreCase(uri.getScheme())) {
                    return LDAPS;
                }
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return NONE;
    }

    static {
        SPACE_SPLITTER = Splitter.on((String)" ").trimResults().omitEmptyStrings();
    }

    public static class Adapter
    extends XmlAdapter<String, LdapSecureMode> {
        public String marshal(LdapSecureMode qualifier) {
            return qualifier.getName();
        }

        public LdapSecureMode unmarshal(String val) {
            return LdapSecureMode.fromString(val);
        }
    }
}

