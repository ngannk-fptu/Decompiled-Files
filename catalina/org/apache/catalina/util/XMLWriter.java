/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.util;

import java.io.IOException;
import java.io.Writer;
import org.apache.tomcat.util.security.Escape;

public class XMLWriter {
    public static final int OPENING = 0;
    public static final int CLOSING = 1;
    public static final int NO_CONTENT = 2;
    protected StringBuilder buffer = new StringBuilder();
    protected final Writer writer;
    protected boolean lastWriteWasOpen;

    public XMLWriter() {
        this(null);
    }

    public XMLWriter(Writer writer) {
        this.writer = writer;
    }

    public String toString() {
        return this.buffer.toString();
    }

    public void writeProperty(String namespace, String name, String value) {
        this.writeElement(namespace, name, 0);
        this.buffer.append(value);
        this.writeElement(namespace, name, 1);
    }

    public void writeElement(String namespace, String name, int type) {
        this.writeElement(namespace, null, name, type);
    }

    public void writeElement(String namespace, String namespaceInfo, String name, int type) {
        if (namespace != null && namespace.length() > 0) {
            switch (type) {
                case 0: {
                    if (this.lastWriteWasOpen) {
                        this.buffer.append('\n');
                    }
                    if (namespaceInfo != null) {
                        this.buffer.append("<" + namespace + ":" + name + " xmlns:" + namespace + "=\"" + namespaceInfo + "\">");
                    } else {
                        this.buffer.append("<" + namespace + ":" + name + ">");
                    }
                    this.lastWriteWasOpen = true;
                    break;
                }
                case 1: {
                    this.buffer.append("</" + namespace + ":" + name + ">\n");
                    this.lastWriteWasOpen = false;
                    break;
                }
                default: {
                    if (this.lastWriteWasOpen) {
                        this.buffer.append('\n');
                    }
                    if (namespaceInfo != null) {
                        this.buffer.append("<" + namespace + ":" + name + " xmlns:" + namespace + "=\"" + namespaceInfo + "\"/>\n");
                    } else {
                        this.buffer.append("<" + namespace + ":" + name + "/>\n");
                    }
                    this.lastWriteWasOpen = false;
                    break;
                }
            }
        } else {
            switch (type) {
                case 0: {
                    if (this.lastWriteWasOpen) {
                        this.buffer.append('\n');
                    }
                    this.buffer.append("<" + name + ">");
                    this.lastWriteWasOpen = true;
                    break;
                }
                case 1: {
                    this.buffer.append("</" + name + ">\n");
                    this.lastWriteWasOpen = false;
                    break;
                }
                default: {
                    if (this.lastWriteWasOpen) {
                        this.buffer.append('\n');
                    }
                    this.buffer.append("<" + name + "/>\n");
                    this.lastWriteWasOpen = false;
                }
            }
        }
    }

    public void writeText(String text) {
        this.buffer.append(Escape.xml((String)text));
    }

    public void writeData(String data) {
        this.buffer.append("<![CDATA[" + data + "]]>");
    }

    public void writeXMLHeader() {
        this.buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
    }

    public void sendData() throws IOException {
        if (this.writer != null) {
            this.writer.write(this.buffer.toString());
            this.buffer = new StringBuilder();
        }
    }
}

