/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.base64.Base64;
import aQute.lib.io.IO;
import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;

public class FileHandler
extends Handler {
    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        File f = (File)object;
        if (!f.isFile()) {
            throw new RuntimeException("Encoding a file requires the file to exist and to be a normal file " + f);
        }
        try (InputStream in = IO.stream(f);){
            app.append('\"');
            Base64.encode(in, (Appendable)app);
            app.append('\"');
        }
    }

    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        File tmp = File.createTempFile("json", ".bin");
        try (OutputStream fout = IO.outputStream(tmp);){
            Base64.decode(new StringReader(s), fout);
        }
        return tmp;
    }
}

