/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class LdapRdnComponent
implements Comparable,
Serializable {
    private static final long serialVersionUID = -3296747972616243038L;
    private static final Logger LOG = LoggerFactory.getLogger(LdapRdnComponent.class);
    public static final boolean DONT_DECODE_VALUE = false;
    private String key;
    private String value;

    public LdapRdnComponent(String key, String value) {
        this(key, value, false);
    }

    public LdapRdnComponent(String key, String value, boolean decodeValue) {
        Assert.hasText((String)key, (String)"Key must not be empty");
        Assert.hasText((String)value, (String)"Value must not be empty");
        String caseFold = System.getProperty("org.springframework.ldap.core.keyCaseFold");
        if (!StringUtils.hasText((String)caseFold) || caseFold.equals("lower")) {
            this.key = key.toLowerCase();
        } else if (caseFold.equals("upper")) {
            this.key = key.toUpperCase();
        } else if (caseFold.equals("none")) {
            this.key = key;
        } else {
            LOG.warn("\"" + caseFold + "\" invalid property value for " + "org.springframework.ldap.core.keyCaseFold" + "; expected \"" + "lower" + "\", \"" + "upper" + "\", or \"" + "none" + "\"");
            this.key = key.toLowerCase();
        }
        this.value = decodeValue ? LdapEncoder.nameDecode(value) : value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        Assert.hasText((String)key, (String)"Key must not be empty");
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        Assert.hasText((String)value, (String)"Value must not be empty");
        this.value = value;
    }

    protected String encodeLdap() {
        StringBuffer buff = new StringBuffer(this.key.length() + this.value.length() * 2);
        buff.append(this.key);
        buff.append('=');
        buff.append(LdapEncoder.nameEncode(this.value));
        return buff.toString();
    }

    public String toString() {
        return this.getLdapEncoded();
    }

    public String getLdapEncoded() {
        return this.encodeLdap();
    }

    public String encodeUrl() {
        try {
            URI valueUri = new URI(null, null, this.value, null);
            return this.key + "=" + valueUri.toString();
        }
        catch (URISyntaxException e) {
            return this.key + "=value";
        }
    }

    public int hashCode() {
        return this.key.toUpperCase().hashCode() ^ this.value.toUpperCase().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof LdapRdnComponent) {
            LdapRdnComponent that = (LdapRdnComponent)obj;
            return this.key.equalsIgnoreCase(that.key) && this.value.equalsIgnoreCase(that.value);
        }
        return false;
    }

    public int compareTo(Object obj) {
        LdapRdnComponent that = (LdapRdnComponent)obj;
        int keyCompare = this.key.toLowerCase().compareTo(that.key.toLowerCase());
        if (keyCompare == 0) {
            return this.value.toLowerCase().compareTo(that.value.toLowerCase());
        }
        return keyCompare;
    }

    public LdapRdnComponent immutableLdapRdnComponent() {
        return new ImmutableLdapRdnComponent(this.key, this.value);
    }

    private static class ImmutableLdapRdnComponent
    extends LdapRdnComponent {
        private static final long serialVersionUID = -7099970046426346567L;

        public ImmutableLdapRdnComponent(String key, String value) {
            super(key, value);
        }

        @Override
        public void setKey(String key) {
            throw new UnsupportedOperationException("SetValue not supported for this immutable LdapRdnComponent");
        }

        @Override
        public void setValue(String value) {
            throw new UnsupportedOperationException("SetKey not supported for this immutable LdapRdnComponent");
        }
    }
}

