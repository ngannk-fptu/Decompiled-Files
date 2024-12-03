/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter.interwiki;

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
import org.radeox.util.Encoder;

public class InterWiki {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$interwiki$InterWiki == null ? (class$org$radeox$filter$interwiki$InterWiki = InterWiki.class$("org.radeox.filter.interwiki.InterWiki")) : class$org$radeox$filter$interwiki$InterWiki));
    private static InterWiki instance;
    private Map interWiki = new HashMap();
    static /* synthetic */ Class class$org$radeox$filter$interwiki$InterWiki;

    public static synchronized InterWiki getInstance() {
        if (null == instance) {
            instance = new InterWiki();
        }
        return instance;
    }

    public InterWiki() {
        this.interWiki.put("LCOM", "http://www.langreiter.com/space/");
        this.interWiki.put("ESA", "http://earl.strain.at/space/");
        this.interWiki.put("C2", "http://www.c2.com/cgi/wiki?");
        this.interWiki.put("WeblogKitchen", "http://www.weblogkitchen.com/wiki.cgi?");
        this.interWiki.put("Meatball", "http://www.usemod.com/cgi-bin/mb.pl?");
        this.interWiki.put("SnipSnap", "http://snipsnap.org/space/");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("conf/intermap.txt")));
            this.addInterMap(br);
        }
        catch (IOException e) {
            log.warn((Object)"Unable to read conf/intermap.txt");
        }
    }

    public void addInterMap(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf(" ");
            this.interWiki.put(line.substring(0, index), Encoder.escape(line.substring(index + 1)));
        }
    }

    public Writer appendTo(Writer writer) throws IOException {
        Iterator iterator = this.interWiki.entrySet().iterator();
        writer.write("{table}\n");
        writer.write("Wiki|Url\n");
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
        return this.interWiki.containsKey(external);
    }

    public String getWikiUrl(String wiki, String name) {
        return (String)this.interWiki.get(wiki) + name;
    }

    public Writer expand(Writer writer, String wiki, String name, String view, String anchor) throws IOException {
        writer.write("<a href=\"");
        writer.write((String)this.interWiki.get(wiki));
        writer.write(name);
        if (!"".equals(anchor)) {
            writer.write("#");
            writer.write(anchor);
        }
        writer.write("\">");
        writer.write(view);
        writer.write("</a>");
        return writer;
    }

    public Writer expand(Writer writer, String wiki, String name, String view) throws IOException {
        return this.expand(writer, wiki, name, view, "");
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

