/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 */
package com.atlassian.confluence.content.render.xhtml.links;

import java.util.regex.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class WebLink {
    private static final Pattern ON_SITE_URL_PATTERN = Pattern.compile("^(?![\\p{Zs}\\p{S}\\p{P}\\p{L}\\p{N}&&[^':\"<>]]*(&amp;colon))[\\p{Zs}\\p{S}\\p{P}\\p{L}\\p{N}&&[^':\"<>]]*");
    private static final Pattern OFF_SITE_URL_PATTERN = Pattern.compile("^\\s*(([a-zA-Z]*(?<!(script|data))://)|mailto:|skype:|callto:|facetime:|git:|irc:|irc6:|news:|nntp:|feed:|cvs:|svn:|mvn:|ssh:|itms:|notes:|smb:|sourcetree:|urn:|tel:|xmpp:|telnet:|vnc:|rdp:|whatsapp:|slack:|sip:|sips:|magnet:)[\\p{L}\\p{N}/]+([\\p{Zs}\\p{S}\\p{P}\\p{L}\\p{N}&&[^'\"<>]])*(\\s)*");
    private String href;
    private String anchor;

    public WebLink(String href) {
        this.href = href;
        int anchorPos = href.indexOf(35);
        this.anchor = anchorPos > 0 && anchorPos + 1 < href.length() ? href.substring(anchorPos + 1) : "";
    }

    public static boolean isValidURL(String url) {
        if (url == null) {
            return false;
        }
        return ON_SITE_URL_PATTERN.matcher(url).matches() || OFF_SITE_URL_PATTERN.matcher(url).matches();
    }

    public boolean isRelative() {
        return ON_SITE_URL_PATTERN.matcher(this.href).matches();
    }

    public String getHref() {
        return this.href;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        WebLink rhs = (WebLink)obj;
        return new EqualsBuilder().append((Object)this.href, (Object)rhs.href).append((Object)this.anchor, (Object)rhs.anchor).isEquals();
    }

    public int hashCode() {
        int result = this.href != null ? this.href.hashCode() : 0;
        result = 31 * result + (this.anchor != null ? this.anchor.hashCode() : 0);
        return result;
    }
}

