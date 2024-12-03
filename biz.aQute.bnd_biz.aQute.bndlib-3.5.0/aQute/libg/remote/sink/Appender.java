/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.sink;

import aQute.libg.remote.Source;
import java.io.IOException;

class Appender
implements Appendable {
    final Source[] sources;
    final String areaId;
    final boolean err;

    Appender(Source[] sources, String areaId, boolean err) {
        this.sources = sources;
        this.err = err;
        this.areaId = areaId;
    }

    @Override
    public Appendable append(char ch) throws IOException {
        return this.append(Character.toString(ch));
    }

    @Override
    public Appendable append(CharSequence text) throws IOException {
        for (Source source : this.sources) {
            source.output(this.areaId, text, this.err);
        }
        return this;
    }

    @Override
    public Appendable append(CharSequence text, int start, int end) throws IOException {
        return this.append(text.subSequence(start, end));
    }
}

