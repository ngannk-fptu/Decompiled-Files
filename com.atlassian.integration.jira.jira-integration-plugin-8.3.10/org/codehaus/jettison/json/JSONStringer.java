/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.json;

import java.io.StringWriter;
import org.codehaus.jettison.json.JSONWriter;

public class JSONStringer
extends JSONWriter {
    public JSONStringer() {
        super(new StringWriter());
    }

    public String toString() {
        return this.mode == 'd' ? this.writer.toString() : null;
    }
}

