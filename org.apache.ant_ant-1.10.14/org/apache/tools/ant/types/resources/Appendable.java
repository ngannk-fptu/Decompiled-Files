/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;

public interface Appendable {
    public OutputStream getAppendOutputStream() throws IOException;
}

