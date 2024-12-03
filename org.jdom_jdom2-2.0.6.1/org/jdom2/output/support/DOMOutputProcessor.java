/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface DOMOutputProcessor {
    public org.w3c.dom.Document process(org.w3c.dom.Document var1, Format var2, Document var3);

    public org.w3c.dom.Element process(org.w3c.dom.Document var1, Format var2, Element var3);

    public List<Node> process(org.w3c.dom.Document var1, Format var2, List<? extends Content> var3);

    public CDATASection process(org.w3c.dom.Document var1, Format var2, CDATA var3);

    public org.w3c.dom.Text process(org.w3c.dom.Document var1, Format var2, Text var3);

    public org.w3c.dom.Comment process(org.w3c.dom.Document var1, Format var2, Comment var3);

    public org.w3c.dom.ProcessingInstruction process(org.w3c.dom.Document var1, Format var2, ProcessingInstruction var3);

    public EntityReference process(org.w3c.dom.Document var1, Format var2, EntityRef var3);

    public Attr process(org.w3c.dom.Document var1, Format var2, Attribute var3);
}

