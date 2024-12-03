/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class HtmlDocumentFacade {
    protected final Element body;
    protected final Document document;
    protected final Element head;
    protected final Element html;
    private Map<String, Map<String, String>> stylesheet = new LinkedHashMap<String, Map<String, String>>();
    private Element stylesheetElement;
    protected Element title;
    protected Text titleText;

    public HtmlDocumentFacade(Document document) {
        this.document = document;
        this.html = document.createElement("html");
        document.appendChild(this.html);
        this.body = document.createElement("body");
        this.head = document.createElement("head");
        this.stylesheetElement = document.createElement("style");
        this.stylesheetElement.setAttribute("type", "text/css");
        this.html.appendChild(this.head);
        this.html.appendChild(this.body);
        this.head.appendChild(this.stylesheetElement);
        this.addStyleClass(this.body, "b", "white-space-collapsing:preserve;");
    }

    public void addAuthor(String value) {
        this.addMeta("author", value);
    }

    public void addDescription(String value) {
        this.addMeta("description", value);
    }

    public void addKeywords(String value) {
        this.addMeta("keywords", value);
    }

    public void addMeta(String name, String value) {
        Element meta = this.document.createElement("meta");
        meta.setAttribute("name", name);
        meta.setAttribute("content", value);
        this.head.appendChild(meta);
    }

    public void addStyleClass(Element element, String classNamePrefix, String style) {
        String exising = element.getAttribute("class");
        String addition = this.getOrCreateCssClass(classNamePrefix, style);
        String newClassValue = AbstractWordUtils.isEmpty(exising) ? addition : exising + " " + addition;
        element.setAttribute("class", newClassValue);
    }

    protected String buildStylesheet(Map<String, Map<String, String>> prefixToMapOfStyles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map<String, String> byPrefix : prefixToMapOfStyles.values()) {
            for (Map.Entry<String, String> byStyle : byPrefix.entrySet()) {
                String style = byStyle.getKey();
                String className = byStyle.getValue();
                stringBuilder.append(".");
                stringBuilder.append(className);
                stringBuilder.append("{");
                stringBuilder.append(style);
                stringBuilder.append("}\n");
            }
        }
        return stringBuilder.toString();
    }

    public Element createBlock() {
        return this.document.createElement("div");
    }

    public Element createBookmark(String name) {
        Element basicLink = this.document.createElement("a");
        basicLink.setAttribute("name", name);
        return basicLink;
    }

    public Element createHeader1() {
        return this.document.createElement("h1");
    }

    public Element createHeader2() {
        return this.document.createElement("h2");
    }

    public Element createHyperlink(String internalDestination) {
        Element basicLink = this.document.createElement("a");
        basicLink.setAttribute("href", internalDestination);
        return basicLink;
    }

    public Element createImage(String src) {
        Element result = this.document.createElement("img");
        result.setAttribute("src", src);
        return result;
    }

    public Element createLineBreak() {
        return this.document.createElement("br");
    }

    public Element createListItem() {
        return this.document.createElement("li");
    }

    public Element createOption(String value, boolean selected) {
        Element result = this.document.createElement("option");
        result.appendChild(this.createText(value));
        if (selected) {
            result.setAttribute("selected", "selected");
        }
        return result;
    }

    public Element createParagraph() {
        return this.document.createElement("p");
    }

    public Element createSelect() {
        return this.document.createElement("select");
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

    public Element createTableColumn() {
        return this.document.createElement("col");
    }

    public Element createTableColumnGroup() {
        return this.document.createElement("colgroup");
    }

    public Element createTableHeader() {
        return this.document.createElement("thead");
    }

    public Element createTableHeaderCell() {
        return this.document.createElement("th");
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

    public String getOrCreateCssClass(String classNamePrefix, String style) {
        Map<String, String> styleToClassName;
        String knownClass;
        if (!this.stylesheet.containsKey(classNamePrefix)) {
            this.stylesheet.put(classNamePrefix, new LinkedHashMap(1));
        }
        if ((knownClass = (styleToClassName = this.stylesheet.get(classNamePrefix)).get(style)) != null) {
            return knownClass;
        }
        String newClassName = classNamePrefix + (styleToClassName.size() + 1);
        styleToClassName.put(style, newClassName);
        return newClassName;
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

    public void updateStylesheet() {
        this.stylesheetElement.setTextContent(this.buildStylesheet(this.stylesheet));
    }
}

