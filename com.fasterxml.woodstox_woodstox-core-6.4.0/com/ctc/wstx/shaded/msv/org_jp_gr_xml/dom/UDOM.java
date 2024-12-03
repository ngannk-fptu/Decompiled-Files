/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.UDOMVisitor;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.XMLMaker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class UDOM {
    public static String getXMLText(Document document) {
        return UDOM.getXMLText(document, "UTF-8");
    }

    public static String getXMLText(Document document, String s) {
        XMLMaker xmlmaker = new XMLMaker();
        UDOMVisitor.traverse(document, xmlmaker);
        return xmlmaker.getText();
    }

    public static String getXMLText(Node node) {
        XMLMaker xmlmaker = new XMLMaker();
        UDOMVisitor.traverse(node, xmlmaker);
        return xmlmaker.getText();
    }
}

