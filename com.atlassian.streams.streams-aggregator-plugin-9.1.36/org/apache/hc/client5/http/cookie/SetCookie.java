/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.cookie;

import java.util.Date;
import org.apache.hc.client5.http.cookie.Cookie;

public interface SetCookie
extends Cookie {
    public void setValue(String var1);

    public void setExpiryDate(Date var1);

    public void setDomain(String var1);

    public void setPath(String var1);

    public void setSecure(boolean var1);
}

