/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import org.apache.xmlgraphics.util.io.Finalizable;

public class FlateEncodeOutputStream
extends DeflaterOutputStream
implements Finalizable {
    public FlateEncodeOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void finalizeStream() throws IOException {
        this.finish();
        this.flush();
        this.def.end();
        if (this.out instanceof Finalizable) {
            ((Finalizable)((Object)this.out)).finalizeStream();
        }
    }
}

