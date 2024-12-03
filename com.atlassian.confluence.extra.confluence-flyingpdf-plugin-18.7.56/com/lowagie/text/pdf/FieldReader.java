/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FieldReader {
    @Deprecated
    public HashMap<String, String> getFields();

    public Map<String, String> getAllFields();

    public String getFieldValue(String var1);

    public List<String> getListValues(String var1);
}

