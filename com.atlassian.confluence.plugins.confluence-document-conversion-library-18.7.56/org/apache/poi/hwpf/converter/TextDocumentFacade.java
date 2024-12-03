/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class TextDocumentFacade {
    protected final Element body;
    protected final Document document;
    protected final Element head;
    protected final Element root;
    protected Element title;
    protected Text titleText;

    public TextDocumentFacade(Document document) {
        this.document = document;
        this.root = document.createElement("html");
        document.appendChild(this.root);
        this.body = document.createElement("body");
        this.head = document.createElement("head");
        this.root.appendChild(this.head);
        this.root.appendChild(this.body);
        this.title = document.createElement("title");
        this.titleText = document.createTextNode("");
        this.head.appendChild(this.title);
    }

    public void addAuthor(String value) {
        this.addMeta("Author", value);
    }

    public void addDescription(String value) {
        this.addMeta("Description", value);
    }

    public void addKeywords(String value) {
        this.addMeta("Keywords", value);
    }

    public void addMeta(String name, String value) {
        Element meta = this.document.createElement("meta");
        Element metaName = this.document.createElement("name");
        metaName.appendChild(this.document.createTextNode(name + ": "));
        meta.appendChild(metaName);
        Element metaValue = this.document.createElement("value");
        metaValue.appendChild(this.document.createTextNode(value + "\n"));
        meta.appendChild(metaValue);
        this.head.appendChild(meta);
    }

    public Element createBlock() {
        return this.document.createElement("div");
    }

    public Element createHeader1() {
        Element result = this.document.createElement("h1");
        result.appendChild(this.document.createTextNode("        "));
        return result;
    }

    public Element createHeader2() {
        Element result = this.document.createElement("h2");
        result.appendChild(this.document.createTextNode("    "));
        return result;
    }

    public Element createParagraph() {
        return this.document.createElement("p");
    }

    public Element createTable() {
        return this.document.createElement("table");
    }

    public Element createTableBody() {
        return this.document.createElement("tbody");
    }

    public Element createTableCell() {
        return this.document.createElement("td");
    }

    public Element createTableRow() {
        return this.document.createElement("tr");
    }

    public Text createText(String data) {
        return this.document.createTextNode(data);
    }

    public Element createUnorderedList() {
        return this.document.createElement("ul");
    }

    public Element getBody() {
        return this.body;
    }

    public Document getDocument() {
        return this.document;
    }

    public Element getHead() {
        return this.head;
    }

    public String getTitle() {
        if (this.title == null) {
            return null;
        }
        return this.titleText.getTextContent();
    }

    public void setTitle(String titleText) {
        if (AbstractWordUtils.isEmpty(titleText) && this.title != null) {
            this.head.removeChild(this.title);
            this.title = null;
            this.titleText = null;
        }
        if (this.title == null) {
            this.title = this.document.createElement("title");
            this.titleText = this.document.createTextNode(titleText);
            this.title.appendChild(this.titleText);
            this.head.appendChild(this.title);
        }
        this.titleText.setData(titleText);
    }
}

