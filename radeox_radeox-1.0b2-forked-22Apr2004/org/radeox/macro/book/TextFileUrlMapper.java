/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro.book;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.book.UrlMapper;
import org.radeox.util.Encoder;

public abstract class TextFileUrlMapper
implements UrlMapper {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$book$TextFileUrlMapper == null ? (class$org$radeox$macro$book$TextFileUrlMapper = TextFileUrlMapper.class$("org.radeox.macro.book.TextFileUrlMapper")) : class$org$radeox$macro$book$TextFileUrlMapper));
    private Map services = new HashMap();
    static /* synthetic */ Class class$org$radeox$macro$book$TextFileUrlMapper;

    public abstract String getFileName();

    public abstract String getKeyName();

    public TextFileUrlMapper(Class klass) {
        BufferedReader br;
        boolean fileNotFound = false;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(this.getFileName())));
            this.addMapping(br);
        }
        catch (IOException e) {
            log.warn((Object)("Unable to read " + this.getFileName()));
            fileNotFound = true;
        }
        if (fileNotFound) {
            br = null;
            try {
                br = new BufferedReader(new InputStreamReader(klass.getResourceAsStream("/" + this.getFileName())));
                this.addMapping(br);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to read /" + this.getFileName() + " from jar"));
            }
        }
    }

    public void addMapping(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) continue;
            int index = line.indexOf(" ");
            this.services.put(line.substring(0, index), Encoder.escape(line.substring(index + 1)));
        }
    }

    public Writer appendTo(Writer writer) throws IOException {
        Iterator iterator = this.services.entrySet().iterator();
        writer.write("{table}\n");
        writer.write("Service|Url\n");
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            writer.write((String)entry.getKey());
            writer.write("|");
            writer.write((String)entry.getValue());
            writer.write("\n");
        }
        writer.write("{table}");
        return writer;
    }

    public boolean contains(String external) {
        return this.services.containsKey(external);
    }

    public Writer appendUrl(Writer writer, String key) throws IOException {
        if (this.services.size() == 0) {
            writer.write(this.getKeyName());
            writer.write(":");
            writer.write(key);
        } else {
            writer.write("(");
            Iterator iterator = this.services.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                writer.write("<a href=\"");
                writer.write((String)entry.getValue());
                writer.write(key);
                writer.write("\">");
                writer.write((String)entry.getKey());
                writer.write("</a>");
                if (!iterator.hasNext()) continue;
                writer.write(" &#x7c; ");
            }
            writer.write(")");
        }
        return writer;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

