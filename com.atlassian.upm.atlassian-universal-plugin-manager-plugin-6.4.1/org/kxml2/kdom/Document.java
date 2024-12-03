/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.kdom;

import java.io.IOException;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class Document
extends Node {
    protected int rootIndex = -1;
    String encoding;
    Boolean standalone;

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public void setStandalone(Boolean bl) {
        this.standalone = bl;
    }

    public Boolean getStandalone() {
        return this.standalone;
    }

    public String getName() {
        return "#document";
    }

    public void addChild(int n, int n2, Object object) {
        if (n2 == 2) {
            this.rootIndex = n;
        } else if (this.rootIndex >= n) {
            ++this.rootIndex;
        }
        super.addChild(n, n2, object);
    }

    public void parse(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(0, null, null);
        xmlPullParser.nextToken();
        this.encoding = xmlPullParser.getInputEncoding();
        this.standalone = (Boolean)xmlPullParser.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone");
        super.parse(xmlPullParser);
        if (xmlPullParser.getEventType() != 1) {
            throw new RuntimeException("Document end expected!");
        }
    }

    public void removeChild(int n) {
        if (n == this.rootIndex) {
            this.rootIndex = -1;
        } else if (n < this.rootIndex) {
            --this.rootIndex;
        }
        super.removeChild(n);
    }

    public Element getRootElement() {
        if (this.rootIndex == -1) {
            throw new RuntimeException("Document has no root element!");
        }
        return (Element)this.getChild(this.rootIndex);
    }

    public void write(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startDocument(this.encoding, this.standalone);
        this.writeChildren(xmlSerializer);
        xmlSerializer.endDocument();
    }
}

