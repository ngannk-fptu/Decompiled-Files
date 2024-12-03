/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro.api;

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
import org.radeox.macro.api.ApiConverter;

public class ApiDoc {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$api$ApiDoc == null ? (class$org$radeox$macro$api$ApiDoc = ApiDoc.class$("org.radeox.macro.api.ApiDoc")) : class$org$radeox$macro$api$ApiDoc));
    private static ApiDoc instance;
    private Map apiDocs = new HashMap();
    static /* synthetic */ Class class$org$radeox$macro$api$ApiDoc;

    public static synchronized ApiDoc getInstance() {
        if (null == instance) {
            instance = new ApiDoc();
        }
        return instance;
    }

    public ApiDoc() {
        BufferedReader br;
        boolean fileNotFound = false;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("conf/apidocs.txt")));
            this.addApiDoc(br);
        }
        catch (IOException e) {
            log.warn((Object)"Unable to read conf/apidocs.txt");
            fileNotFound = true;
        }
        if (fileNotFound) {
            br = null;
            try {
                br = new BufferedReader(new InputStreamReader((class$org$radeox$macro$api$ApiDoc == null ? (class$org$radeox$macro$api$ApiDoc = ApiDoc.class$("org.radeox.macro.api.ApiDoc")) : class$org$radeox$macro$api$ApiDoc).getResourceAsStream("/conf/apidocs.txt")));
                this.addApiDoc(br);
            }
            catch (Exception e) {
                log.warn((Object)"Unable to read conf/apidocs.txt from jar");
            }
        }
    }

    public void addApiDoc(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String mode = tokenizer.nextToken();
            String baseUrl = tokenizer.nextToken();
            String converterName = tokenizer.nextToken();
            ApiConverter converter = null;
            try {
                converter = (ApiConverter)Class.forName("org.radeox.macro.api." + converterName + "ApiConverter").newInstance();
            }
            catch (Exception e) {
                log.warn((Object)("Unable to load converter: " + converterName + "ApiConverter"), (Throwable)e);
            }
            converter.setBaseUrl(baseUrl);
            this.apiDocs.put(mode.toLowerCase(), converter);
        }
    }

    public boolean contains(String external) {
        return this.apiDocs.containsKey(external);
    }

    public Writer expand(Writer writer, String className, String mode) throws IOException {
        if (this.apiDocs.containsKey(mode = mode.toLowerCase())) {
            writer.write("<a href=\"");
            ((ApiConverter)this.apiDocs.get(mode)).appendUrl(writer, className);
            writer.write("\">");
            writer.write(className);
            writer.write("</a>");
        } else {
            log.warn((Object)(mode + " not found"));
        }
        return writer;
    }

    public Writer appendTo(Writer writer) throws IOException {
        writer.write("{table}\n");
        writer.write("Binding|BaseUrl|Converter Name\n");
        Iterator iterator = this.apiDocs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            writer.write((String)entry.getKey());
            ApiConverter converter = (ApiConverter)entry.getValue();
            writer.write("|");
            writer.write(converter.getBaseUrl());
            writer.write("|");
            writer.write(converter.getName());
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

