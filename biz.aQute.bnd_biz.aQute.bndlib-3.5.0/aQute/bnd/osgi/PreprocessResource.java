/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.AbstractResource;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class PreprocessResource
extends AbstractResource {
    private final Resource resource;
    private final Processor processor;

    public PreprocessResource(Processor processor, Resource r) {
        super(r.lastModified());
        this.processor = processor;
        this.resource = r;
        this.setExtra(this.resource.getExtra());
    }

    @Override
    protected byte[] getBytes() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
        PrintWriter pw = IO.writer((OutputStream)bout, Constants.DEFAULT_CHARSET);
        ByteBuffer bb = this.resource.buffer();
        BufferedReader r = bb != null ? IO.reader(bb, Constants.DEFAULT_CHARSET) : IO.reader(this.resource.openInputStream(), Constants.DEFAULT_CHARSET);
        try (BufferedReader rdr = r;){
            String line = rdr.readLine();
            while (line != null) {
                line = this.processor.getReplacer().process(line);
                pw.println(line);
                line = rdr.readLine();
            }
        }
        catch (Exception e) {
            bb = this.resource.buffer();
            if (bb != null) {
                return IO.read(bb);
            }
            return IO.read(this.resource.openInputStream());
        }
        pw.flush();
        byte[] data = bout.toByteArray();
        return data;
    }
}

