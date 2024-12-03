/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.DOMVisitorException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public interface IDOMVisitor {
    public boolean enter(Element var1) throws DOMVisitorException;

    public boolean enter(Attr var1) throws DOMVisitorException;

    public boolean enter(Text var1) throws DOMVisitorException;

    public boolean enter(CDATASection var1) throws DOMVisitorException;

    public boolean enter(EntityReference var1) throws DOMVisitorException;

    public boolean enter(Entity var1) throws DOMVisitorException;

    public boolean enter(ProcessingInstruction var1) throws DOMVisitorException;

    public boolean enter(Comment var1) throws DOMVisitorException;

    public boolean enter(Document var1) throws DOMVisitorException;

    public boolean enter(DocumentType var1) throws DOMVisitorException;

    public boolean enter(DocumentFragment var1) throws DOMVisitorException;

    public boolean enter(Notation var1) throws DOMVisitorException;

    public boolean enter(Node var1) throws DOMVisitorException;

    public void leave(Element var1) throws DOMVisitorException;

    public void leave(Attr var1) throws DOMVisitorException;

    public void leave(Text var1) throws DOMVisitorException;

    public void leave(CDATASection var1) throws DOMVisitorException;

    public void leave(EntityReference var1) throws DOMVisitorException;

    public void leave(Entity var1) throws DOMVisitorException;

    public void leave(ProcessingInstruction var1) throws DOMVisitorException;

    public void leave(Comment var1) throws DOMVisitorException;

    public void leave(Document var1) throws DOMVisitorException;

    public void leave(DocumentType var1) throws DOMVisitorException;

    public void leave(DocumentFragment var1) throws DOMVisitorException;

    public void leave(Notation var1) throws DOMVisitorException;

    public void leave(Node var1) throws DOMVisitorException;
}

