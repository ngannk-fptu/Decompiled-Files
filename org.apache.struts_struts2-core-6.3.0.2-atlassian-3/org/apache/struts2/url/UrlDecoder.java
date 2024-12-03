/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.url;

import java.io.Serializable;

public interface UrlDecoder
extends Serializable {
    public String decode(String var1, String var2, boolean var3);

    public String decode(String var1, boolean var2);

    public String decode(String var1);
}

