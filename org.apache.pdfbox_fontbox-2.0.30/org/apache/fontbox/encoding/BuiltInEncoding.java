/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.encoding;

import java.util.Map;
import org.apache.fontbox.encoding.Encoding;

public class BuiltInEncoding
extends Encoding {
    public BuiltInEncoding(Map<Integer, String> codeToName) {
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            this.addCharacterEncoding(entry.getKey(), entry.getValue());
        }
    }
}

