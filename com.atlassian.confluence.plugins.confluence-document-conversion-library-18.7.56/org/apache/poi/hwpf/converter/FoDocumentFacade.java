/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class FoDocumentFacade {
    private static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String NS_XSLFO = "http://www.w3.org/1999/XSL/Format";
    protected final Element declarations;
    protected final Document document;
    protected final Element layoutMasterSet;
    protected Element propertiesRoot;
    protected final Element root;

    public FoDocumentFacade(Document document) {
        this.document = document;
        this.root = document.createElementNS(NS_XSLFO, "fo:root");
        document.appendChild(this.root);
        this.layoutMasterSet = document.createElementNS(NS_XSLFO, "fo:layout-master-set");
        this.root.appendChild(this.layoutMasterSet);
        this.declarations = document.createElementNS(NS_XSLFO, "fo:declarations");
        this.root.appendChild(this.declarations);
    }

    public Element addFlowToPageSequence(Element pageSequence, String flowName) {
        Element flow = this.document.createElementNS(NS_XSLFO, "fo:flow");
        flow.setAttribute("flow-name", flowName);
        pageSequence.appendChild(flow);
        return flow;
    }

    public Element addListItem(Element listBlock) {
        Element result = this.createListItem();
        listBlock.appendChild(result);
        return result;
    }

    public Element addListItemBody(Element listItem) {
        Element result = this.createListItemBody();
        listItem.appendChild(result);
        return result;
    }

    public Element addListItemLabel(Element listItem, String text) {
        Element result = this.createListItemLabel(text);
        listItem.appendChild(result);
        return result;
    }

    public void addPageSequence(Element pageSequence) {
        this.root.appendChild(pageSequence);
    }

    public Element addPageSequence(String pageMaster) {
        Element pageSequence = this.createPageSequence(pageMaster);
        this.root.appendChild(pageSequence);
        return pageSequence;
    }

    public Element addRegionBody(Element pageMaster) {
        Element regionBody = this.document.createElementNS(NS_XSLFO, "fo:region-body");
        pageMaster.appendChild(regionBody);
        return regionBody;
    }

    public Element addSimplePageMaster(String masterName) {
        Element simplePageMaster = this.document.createElementNS(NS_XSLFO, "fo:simple-page-master");
        simplePageMaster.setAttribute("master-name", masterName);
        this.layoutMasterSet.appendChild(simplePageMaster);
        return simplePageMaster;
    }

    public Element createBasicLinkExternal(String externalDestination) {
        Element basicLink = this.document.createElementNS(NS_XSLFO, "fo:basic-link");
        basicLink.setAttribute("external-destination", externalDestination);
        return basicLink;
    }

    public Element createBasicLinkInternal(String internalDestination) {
        Element basicLink = this.document.createElementNS(NS_XSLFO, "fo:basic-link");
        basicLink.setAttribute("internal-destination", internalDestination);
        return basicLink;
    }

    public Element createBlock() {
        return this.document.createElementNS(NS_XSLFO, "fo:block");
    }

    public Element createExternalGraphic(String source) {
        Element result = this.document.createElementNS(NS_XSLFO, "fo:external-graphic");
        result.setAttribute("src", "url('" + source + "')");
        return result;
    }

    public Element createFootnote() {
        return this.document.createElementNS(NS_XSLFO, "fo:footnote");
    }

    public Element createFootnoteBody() {
        return this.document.createElementNS(NS_XSLFO, "fo:footnote-body");
    }

    public Element createInline() {
        return this.document.createElementNS(NS_XSLFO, "fo:inline");
    }

    public Element createLeader() {
        return this.document.createElementNS(NS_XSLFO, "fo:leader");
    }

    public Element createListBlock() {
        return this.document.createElementNS(NS_XSLFO, "fo:list-block");
    }

    public Element createListItem() {
        return this.document.createElementNS(NS_XSLFO, "fo:list-item");
    }

    public Element createListItemBody() {
        return this.document.createElementNS(NS_XSLFO, "fo:list-item-body");
    }

    public Element createListItemLabel(String text) {
        Element result = this.document.createElementNS(NS_XSLFO, "fo:list-item-label");
        Element block = this.createBlock();
        block.appendChild(this.document.createTextNode(text));
        result.appendChild(block);
        return result;
    }

    public Element createPageSequence(String pageMaster) {
        Element pageSequence = this.document.createElementNS(NS_XSLFO, "fo:page-sequence");
        pageSequence.setAttribute("master-reference", pageMaster);
        return pageSequence;
    }

    public Element createTable() {
        return this.document.createElementNS(NS_XSLFO, "fo:table");
    }

    public Element createTableBody() {
        return this.document.createElementNS(NS_XSLFO, "fo:table-body");
    }

    public Element createTableCell() {
        return this.document.createElementNS(NS_XSLFO, "fo:table-cell");
    }

    public Element createTableColumn() {
        return this.document.createElementNS(NS_XSLFO, "fo:table-column");
    }

    public Element createTableHeader() {
        return this.document.createElementNS(NS_XSLFO, "fo:table-header");
    }

    public Element createTableRow() {
        return this.document.createElementNS(NS_XSLFO, "fo:table-row");
    }

    public Text createText(String data) {
        return this.document.createTextNode(data);
    }

    public Document getDocument() {
        return this.document;
    }

    protected Element getOrCreatePropertiesRoot() {
        if (this.propertiesRoot != null) {
            return this.propertiesRoot;
        }
        Element xmpmeta = this.document.createElementNS("adobe:ns:meta/", "x:xmpmeta");
        this.declarations.appendChild(xmpmeta);
        Element rdf = this.document.createElementNS(NS_RDF, "rdf:RDF");
        xmpmeta.appendChild(rdf);
        this.propertiesRoot = this.document.createElementNS(NS_RDF, "rdf:Description");
        this.propertiesRoot.setAttributeNS(NS_RDF, "rdf:about", "");
        rdf.appendChild(this.propertiesRoot);
        return this.propertiesRoot;
    }

    public void setCreator(String value) {
        this.setDublinCoreProperty("creator", value);
    }

    public void setCreatorTool(String value) {
        this.setXmpProperty("CreatorTool", value);
    }

    public void setDescription(String value) {
        Element element = this.setDublinCoreProperty("description", value);
        if (element != null) {
            element.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", "x-default");
        }
    }

    public Element setDublinCoreProperty(String name, String value) {
        return this.setProperty("http://purl.org/dc/elements/1.1/", "dc", name, value);
    }

    public void setKeywords(String value) {
        this.setPdfProperty("Keywords", value);
    }

    public Element setPdfProperty(String name, String value) {
        return this.setProperty("http://ns.adobe.com/pdf/1.3/", "pdf", name, value);
    }

    public void setProducer(String value) {
        this.setPdfProperty("Producer", value);
    }

    protected Element setProperty(String namespace, String prefix, String name, String value) {
        Element propertiesRoot = this.getOrCreatePropertiesRoot();
        NodeList existingChildren = propertiesRoot.getChildNodes();
        for (int i = 0; i < existingChildren.getLength(); ++i) {
            Element childElement;
            Node child = existingChildren.item(i);
            if (child.getNodeType() != 1 || !AbstractWordUtils.isNotEmpty((childElement = (Element)child).getNamespaceURI()) || !AbstractWordUtils.isNotEmpty(childElement.getLocalName()) || !namespace.equals(childElement.getNamespaceURI()) || !name.equals(childElement.getLocalName())) continue;
            propertiesRoot.removeChild(childElement);
            break;
        }
        if (AbstractWordUtils.isNotEmpty(value)) {
            Element property = this.document.createElementNS(namespace, prefix + ":" + name);
            property.appendChild(this.document.createTextNode(value));
            propertiesRoot.appendChild(property);
            return property;
        }
        return null;
    }

    public void setSubject(String value) {
        this.setDublinCoreProperty("title", value);
    }

    public void setTitle(String value) {
        this.setDublinCoreProperty("title", value);
    }

    public Element setXmpProperty(String name, String value) {
        return this.setProperty("http://ns.adobe.com/xap/1.0/", "xmp", name, value);
    }
}

