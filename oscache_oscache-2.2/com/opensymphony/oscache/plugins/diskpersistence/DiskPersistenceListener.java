/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.plugins.diskpersistence;

import com.opensymphony.oscache.plugins.diskpersistence.AbstractDiskPersistenceListener;

public class DiskPersistenceListener
extends AbstractDiskPersistenceListener {
    private static final String CHARS_TO_CONVERT = "./\\ :;\"'_?";

    protected char[] getCacheFileName(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("Invalid key '" + key + "' specified to getCacheFile.");
        }
        char[] chars = key.toCharArray();
        StringBuffer sb = new StringBuffer(chars.length + 8);
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            int pos = CHARS_TO_CONVERT.indexOf(c);
            if (pos >= 0) {
                sb.append('_');
                sb.append(i);
                continue;
            }
            sb.append(c);
        }
        char[] fileChars = new char[sb.length()];
        sb.getChars(0, fileChars.length, fileChars, 0);
        return fileChars;
    }
}

