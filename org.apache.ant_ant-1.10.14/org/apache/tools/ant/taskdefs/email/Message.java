/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.ProjectComponent;

public class Message
extends ProjectComponent {
    private File messageSource = null;
    private StringBuffer buffer = new StringBuffer();
    private String mimeType = "text/plain";
    private boolean specified = false;
    private String charset = null;
    private String inputEncoding;

    public Message() {
    }

    public Message(String text) {
        this.addText(text);
    }

    public Message(File file) {
        this.messageSource = file;
    }

    public void addText(String text) {
        this.buffer.append(text);
    }

    public void setSrc(File src) {
        this.messageSource = src;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        this.specified = true;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void print(PrintStream ps) throws IOException {
        BufferedWriter out = null;
        BufferedWriter bufferedWriter = out = this.charset == null ? new BufferedWriter(new OutputStreamWriter(ps)) : new BufferedWriter(new OutputStreamWriter((OutputStream)ps, this.charset));
        if (this.messageSource != null) {
            try (BufferedReader in = new BufferedReader(this.getReader(this.messageSource));){
                String line;
                while ((line = in.readLine()) != null) {
                    out.write(this.getProject().replaceProperties(line));
                    out.newLine();
                }
            }
        } else {
            out.write(this.getProject().replaceProperties(this.buffer.substring(0)));
            out.newLine();
        }
        out.flush();
    }

    public boolean isMimeTypeSpecified() {
        return this.specified;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setInputEncoding(String encoding) {
        this.inputEncoding = encoding;
    }

    private Reader getReader(File f) throws IOException {
        if (this.inputEncoding != null) {
            InputStream fis = Files.newInputStream(f.toPath(), new OpenOption[0]);
            try {
                return new InputStreamReader(fis, this.inputEncoding);
            }
            catch (IOException ex) {
                fis.close();
                throw ex;
            }
        }
        return new FileReader(f);
    }
}

