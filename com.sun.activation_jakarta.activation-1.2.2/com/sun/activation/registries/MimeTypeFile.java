/*
 * Decompiled with CFR 0.152.
 */
package com.sun.activation.registries;

import com.sun.activation.registries.LineTokenizer;
import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MimeTypeEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class MimeTypeFile {
    private String fname = null;
    private Hashtable type_hash = new Hashtable();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MimeTypeFile(String new_fname) throws IOException {
        File mime_file = null;
        FileReader fr = null;
        this.fname = new_fname;
        mime_file = new File(this.fname);
        fr = new FileReader(mime_file);
        try {
            this.parse(new BufferedReader(fr));
        }
        finally {
            try {
                fr.close();
            }
            catch (IOException iOException) {}
        }
    }

    public MimeTypeFile(InputStream is) throws IOException {
        this.parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
    }

    public MimeTypeFile() {
    }

    public MimeTypeEntry getMimeTypeEntry(String file_ext) {
        return (MimeTypeEntry)this.type_hash.get(file_ext);
    }

    public String getMIMETypeString(String file_ext) {
        MimeTypeEntry entry = this.getMimeTypeEntry(file_ext);
        if (entry != null) {
            return entry.getMIMEType();
        }
        return null;
    }

    public void appendToRegistry(String mime_types) {
        try {
            this.parse(new BufferedReader(new StringReader(mime_types)));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void parse(BufferedReader buf_reader) throws IOException {
        String line = null;
        String prev = null;
        while ((line = buf_reader.readLine()) != null) {
            prev = prev == null ? line : prev + line;
            int end = prev.length();
            if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
                prev = prev.substring(0, end - 1);
                continue;
            }
            this.parseEntry(prev);
            prev = null;
        }
        if (prev != null) {
            this.parseEntry(prev);
        }
    }

    private void parseEntry(String line) {
        String mime_type = null;
        String file_ext = null;
        if ((line = line.trim()).length() == 0) {
            return;
        }
        if (line.charAt(0) == '#') {
            return;
        }
        if (line.indexOf(61) > 0) {
            LineTokenizer lt = new LineTokenizer(line);
            while (lt.hasMoreTokens()) {
                String name = lt.nextToken();
                String value = null;
                if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens()) {
                    value = lt.nextToken();
                }
                if (value == null) {
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("Bad .mime.types entry: " + line);
                    }
                    return;
                }
                if (name.equals("type")) {
                    mime_type = value;
                    continue;
                }
                if (!name.equals("exts")) continue;
                StringTokenizer st = new StringTokenizer(value, ",");
                while (st.hasMoreTokens()) {
                    file_ext = st.nextToken();
                    MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
                    this.type_hash.put(file_ext, entry);
                    if (!LogSupport.isLoggable()) continue;
                    LogSupport.log("Added: " + entry.toString());
                }
            }
        } else {
            StringTokenizer strtok = new StringTokenizer(line);
            int num_tok = strtok.countTokens();
            if (num_tok == 0) {
                return;
            }
            mime_type = strtok.nextToken();
            while (strtok.hasMoreTokens()) {
                MimeTypeEntry entry = null;
                file_ext = strtok.nextToken();
                entry = new MimeTypeEntry(mime_type, file_ext);
                this.type_hash.put(file_ext, entry);
                if (!LogSupport.isLoggable()) continue;
                LogSupport.log("Added: " + entry.toString());
            }
        }
    }
}

