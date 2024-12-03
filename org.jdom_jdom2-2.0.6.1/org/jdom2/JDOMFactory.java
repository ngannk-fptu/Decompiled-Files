/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface JDOMFactory {
    public Attribute attribute(String var1, String var2, Namespace var3);

    @Deprecated
    public Attribute attribute(String var1, String var2, int var3, Namespace var4);

    public Attribute attribute(String var1, String var2, AttributeType var3, Namespace var4);

    public Attribute attribute(String var1, String var2);

    @Deprecated
    public Attribute attribute(String var1, String var2, int var3);

    public Attribute attribute(String var1, String var2, AttributeType var3);

    public CDATA cdata(String var1);

    public CDATA cdata(int var1, int var2, String var3);

    public Text text(int var1, int var2, String var3);

    public Text text(String var1);

    public Comment comment(String var1);

    public Comment comment(int var1, int var2, String var3);

    public DocType docType(String var1, String var2, String var3);

    public DocType docType(String var1, String var2);

    public DocType docType(String var1);

    public DocType docType(int var1, int var2, String var3, String var4, String var5);

    public DocType docType(int var1, int var2, String var3, String var4);

    public DocType docType(int var1, int var2, String var3);

    public Document document(Element var1, DocType var2);

    public Document document(Element var1, DocType var2, String var3);

    public Document document(Element var1);

    public Element element(String var1, Namespace var2);

    public Element element(String var1);

    public Element element(String var1, String var2);

    public Element element(String var1, String var2, String var3);

    public Element element(int var1, int var2, String var3, Namespace var4);

    public Element element(int var1, int var2, String var3);

    public Element element(int var1, int var2, String var3, String var4);

    public Element element(int var1, int var2, String var3, String var4, String var5);

    public ProcessingInstruction processingInstruction(String var1, Map<String, String> var2);

    public ProcessingInstruction processingInstruction(String var1, String var2);

    public ProcessingInstruction processingInstruction(String var1);

    public ProcessingInstruction processingInstruction(int var1, int var2, String var3, Map<String, String> var4);

    public ProcessingInstruction processingInstruction(int var1, int var2, String var3, String var4);

    public ProcessingInstruction processingInstruction(int var1, int var2, String var3);

    public EntityRef entityRef(String var1);

    public EntityRef entityRef(String var1, String var2, String var3);

    public EntityRef entityRef(String var1, String var2);

    public EntityRef entityRef(int var1, int var2, String var3);

    public EntityRef entityRef(int var1, int var2, String var3, String var4, String var5);

    public EntityRef entityRef(int var1, int var2, String var3, String var4);

    public void addContent(Parent var1, Content var2);

    public void setAttribute(Element var1, Attribute var2);

    public void addNamespaceDeclaration(Element var1, Namespace var2);

    public void setRoot(Document var1, Element var2);
}

