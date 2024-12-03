/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.DOMUtil
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.model.CellModification;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.xml.SelectionTransformer;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.xerces.util.DOMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Component
public class TableSelectionTransformer
extends SelectionTransformer<TableModification> {
    @Autowired
    public TableSelectionTransformer(XMLParserHelper xmlParserHelper) {
        super(xmlParserHelper);
    }

    @Override
    public boolean transform(Document document, TextMatch textMatch, TableModification tableModification) throws SAXException {
        Node tableNode = this.findAncestorByName(textMatch.getLastMatchingItem().getNode(), "table");
        if (tableNode == null) {
            return false;
        }
        List<Node> columnTableCellNodes = this.findColumnTableCellNodesForUpdate(tableNode, tableModification.getTableColumnIndex());
        if (columnTableCellNodes.size() == 0) {
            return false;
        }
        for (int i = 0; i < tableModification.getCellModifications().size(); ++i) {
            CellModification cellXmlInsertion = tableModification.getCellModifications().get(i);
            int rowIndex = cellXmlInsertion.getRow();
            if (rowIndex >= columnTableCellNodes.size()) {
                return false;
            }
            DocumentFragment fragment = this.xmlParserHelper.parseDocumentFragment(document, tableModification.getCellModifications().get(i).getXml());
            Node cellNode = columnTableCellNodes.get(rowIndex);
            cellNode.appendChild(fragment);
        }
        return true;
    }

    private List<Node> findColumnTableCellNodesForUpdate(Node tableNode, int tableColumnIndex) {
        ArrayList<Node> tableCellNodes = new ArrayList<Node>();
        Element tableBodyNode = DOMUtil.getFirstChildElement((Node)tableNode, (String)"tbody");
        List<Node> rowNodes = this.findChildrenByName(tableBodyNode, "tr");
        for (Node rowNode : rowNodes) {
            List<Node> cellNodes = this.findChildrenByName(rowNode, "td");
            if (cellNodes.size() <= 0) continue;
            if (cellNodes.size() <= tableColumnIndex) {
                return new ArrayList<Node>();
            }
            tableCellNodes.add(cellNodes.get(tableColumnIndex));
        }
        return tableCellNodes;
    }
}

