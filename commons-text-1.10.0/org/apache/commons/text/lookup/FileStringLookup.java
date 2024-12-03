/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.lookup;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;

final class FileStringLookup
extends AbstractStringLookup {
    static final AbstractStringLookup INSTANCE = new FileStringLookup();

    private FileStringLookup() {
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(String.valueOf(':'));
        int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad file key format [%s], expected format is CharsetName:DocumentPath.", key);
        }
        String charsetName = keys[0];
        String fileName = StringUtils.substringAfter((String)key, (int)58);
        try {
            return new String(Files.readAllBytes(Paths.get(fileName, new String[0])), charsetName);
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up file [%s] with charset [%s].", fileName, charsetName);
        }
    }
}

