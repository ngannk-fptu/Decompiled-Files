/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.url;

import java.io.Serializable;
import java.util.Map;

public interface QueryStringBuilder
extends Serializable {
    public void build(Map<String, Object> var1, StringBuilder var2, String var3);
}

