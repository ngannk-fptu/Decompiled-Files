/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.util;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.util.Internal;

@Internal
abstract class MFProxy
implements Closeable {
    boolean ignoreParse;
    boolean quiet;

    MFProxy() {
    }

    void setIgnoreParse(boolean ignoreParse) {
        this.ignoreParse = ignoreParse;
    }

    void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    abstract void parse(File var1) throws IOException;

    abstract void parse(InputStream var1) throws IOException;

    abstract Dimension2D getSize();

    void setSlideNo(int slideNo) {
    }

    abstract String getTitle();

    abstract void draw(Graphics2D var1);

    int getSlideCount() {
        return 1;
    }

    Set<Integer> slideIndexes(String range) {
        return Collections.singleton(1);
    }

    abstract GenericRecord getRoot();

    abstract Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings(int var1);

    abstract void setDefaultCharset(Charset var1);
}

