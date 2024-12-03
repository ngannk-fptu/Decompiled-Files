/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.codec;

import aQute.lib.codec.Codec;
import aQute.lib.io.IO;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class HCodec
implements Codec {
    final Codec codec;

    public HCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public Object decode(Reader in, Type type) throws Exception {
        return this.codec.decode(in, type);
    }

    public <T> T decode(InputStream in, Class<T> t) throws Exception {
        return t.cast(this.decode(in, (Type)t));
    }

    public <T> T decode(Reader in, Class<T> t) throws Exception {
        return t.cast(this.decode(in, (Type)t));
    }

    public Object decode(InputStream in, Type t) throws Exception {
        InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
        return this.codec.decode(r, t);
    }

    @Override
    public void encode(Type t, Object o, Appendable out) throws Exception {
        this.codec.encode(t, o, out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void encode(Type t, Object o, OutputStream out) throws Exception {
        OutputStreamWriter wr = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        try {
            this.codec.encode(t, o, wr);
        }
        finally {
            wr.flush();
        }
    }

    /*
     * Exception decompiling
     */
    public <T> T decode(File in, Class<T> t) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void encode(Type t, Object o, File out) throws Exception {
        try (OutputStream oout = IO.outputStream(out);
             OutputStreamWriter wr = new OutputStreamWriter(oout, StandardCharsets.UTF_8);){
            this.codec.encode(t, o, wr);
        }
    }
}

