/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.url;

import java.io.Serializable;

public interface UrlEncoder
extends Serializable {
    public String encode(String var1, String var2);

    public String encode(String var1);
}

