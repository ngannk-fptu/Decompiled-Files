/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.parser.AbstractPage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PartialPageParserHtmlPage
extends AbstractPage
implements HTMLPage {
    private final SitemeshBufferFragment head;
    private final SitemeshBufferFragment body;
    private final SitemeshBuffer sitemeshBuffer;

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer) {
        this(sitemeshBuffer, null, null, null, null, null, null);
    }

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties) {
        this(sitemeshBuffer, body, bodyProperties, null, null, null, null);
    }

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties, SitemeshBufferFragment head, String title, Map<String, String> metaAttributes, Map<String, String> pageProperties) {
        super(sitemeshBuffer);
        this.sitemeshBuffer = sitemeshBuffer;
        this.head = head;
        this.body = body;
        if (title == null) {
            title = "";
        }
        this.addProperty("title", title);
        this.addProperties(metaAttributes, "meta.");
        this.addProperties(bodyProperties, "body.");
        this.addProperties(pageProperties, "page.");
    }

    private void addProperties(Map<String, String> properties, String prefix) {
        if (properties != null) {
            for (Map.Entry<String, String> property : properties.entrySet()) {
                this.addProperty(prefix + property.getKey(), property.getValue());
            }
        }
    }

    @Override
    public void writeHead(Writer out) throws IOException {
        if (this.head != null) {
            this.head.writeTo(out);
        }
    }

    @Override
    public String getHead() {
        if (this.head != null) {
            StringWriter headString = new StringWriter();
            try {
                this.head.writeTo(headString);
            }
            catch (IOException e) {
                throw new RuntimeException("IOException occured while writing to buffer?", e);
            }
            return headString.toString();
        }
        return "";
    }

    @Override
    public void writeBody(Writer out) throws IOException {
        if (this.body != null) {
            if (out instanceof SitemeshWriter) {
                this.stateCheckNoSecondaryStorage();
                ((SitemeshWriter)((Object)out)).writeSitemeshBufferFragment(this.body);
            } else {
                this.body.writeTo(out);
                this.writeOutSecondaryStorage(out, this.sitemeshBuffer);
            }
        } else {
            this.sitemeshBuffer.writeTo(out, 0, this.sitemeshBuffer.getBufferLength());
            this.writeOutSecondaryStorage(out, this.sitemeshBuffer);
        }
    }

    private void writeOutSecondaryStorage(Writer out, SitemeshBuffer sitemeshBuffer) {
        if (sitemeshBuffer.hasSecondaryStorage()) {
            SecondaryStorage secondaryStorage = sitemeshBuffer.getSecondaryStorage();
            try {
                secondaryStorage.writeTo(out);
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to read from SiteMesh secondary storage", e);
            }
            finally {
                secondaryStorage.cleanUp();
            }
        }
    }

    @Override
    public String getBody() {
        this.stateCheckNoSecondaryStorage();
        return super.getBody();
    }

    @Override
    public void writePage(Writer out) throws IOException {
        this.sitemeshBuffer.writeTo(out, 0, this.sitemeshBuffer.getBufferLength());
        this.writeOutSecondaryStorage(out, this.sitemeshBuffer);
    }

    private void stateCheckNoSecondaryStorage() {
        if (this.sitemeshBuffer.hasSecondaryStorage()) {
            throw new IllegalStateException("You have asked for all the body in memory but its spilled over into secondary storage");
        }
    }

    @Override
    public boolean isFrameSet() {
        return false;
    }

    @Override
    public void setFrameSet(boolean frameset) {
        throw new UnsupportedOperationException();
    }
}

