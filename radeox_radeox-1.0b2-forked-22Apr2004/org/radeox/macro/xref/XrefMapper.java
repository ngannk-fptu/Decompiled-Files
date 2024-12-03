/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro.xref;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XrefMapper {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$xref$XrefMapper == null ? (class$org$radeox$macro$xref$XrefMapper = XrefMapper.class$("org.radeox.macro.xref.XrefMapper")) : class$org$radeox$macro$xref$XrefMapper));
    private static final String FILENAME = "conf/xref.txt";
    private static XrefMapper instance;
    private Map xrefMap = new HashMap();
    static /* synthetic */ Class class$org$radeox$macro$xref$XrefMapper;

    public static synchronized XrefMapper getInstance() {
        if (null == instance) {
            instance = new XrefMapper();
        }
        return instance;
    }

    public XrefMapper() {
        BufferedReader br;
        boolean fileNotFound = false;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME)));
            this.addXref(br);
        }
        catch (IOException e) {
            log.warn((Object)"Unable to read conf/xref.txt");
            fileNotFound = true;
        }
        if (fileNotFound) {
            br = null;
            try {
                br = new BufferedReader(new InputStreamReader((class$org$radeox$macro$xref$XrefMapper == null ? (class$org$radeox$macro$xref$XrefMapper = XrefMapper.class$("org.radeox.macro.xref.XrefMapper")) : class$org$radeox$macro$xref$XrefMapper).getResourceAsStream("/conf/xref.txt")));
                this.addXref(br);
            }
            catch (Exception e) {
                log.warn((Object)"Unable to read conf/xref.txt");
            }
        }
    }

    public void addXref(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String site = tokenizer.nextToken();
            String baseUrl = tokenizer.nextToken();
            this.xrefMap.put(site.toLowerCase(), baseUrl);
        }
    }

    public boolean contains(String external) {
        return this.xrefMap.containsKey(external);
    }

    public Writer expand(Writer writer, String className, String site, int lineNumber) throws IOException {
        if (this.xrefMap.containsKey(site = site.toLowerCase())) {
            writer.write("<a href=\"");
            writer.write((String)this.xrefMap.get(site));
            writer.write("/");
            writer.write(className.replace('.', '/'));
            writer.write(".html");
            if (lineNumber > 0) {
                writer.write("#");
                writer.write("" + lineNumber);
            }
            writer.write("\">");
            writer.write(className);
            writer.write("</a>");
        } else {
            log.debug((Object)("Xrefs : " + this.xrefMap));
            log.warn((Object)(site + " not found"));
        }
        return writer;
    }

    public Writer appendTo(Writer writer) throws IOException {
        writer.write("{table}\n");
        writer.write("Binding|Site\n");
        Iterator iterator = this.xrefMap.entrySet().iterator();
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

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

