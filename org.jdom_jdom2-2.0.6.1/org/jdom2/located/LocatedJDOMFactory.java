/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import java.util.Map;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.located.LocatedCDATA;
import org.jdom2.located.LocatedComment;
import org.jdom2.located.LocatedDocType;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedEntityRef;
import org.jdom2.located.LocatedProcessingInstruction;
import org.jdom2.located.LocatedText;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LocatedJDOMFactory
extends DefaultJDOMFactory {
    @Override
    public CDATA cdata(int line, int col, String text) {
        LocatedCDATA ret = new LocatedCDATA(text);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Text text(int line, int col, String text) {
        LocatedText ret = new LocatedText(text);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Comment comment(int line, int col, String text) {
        LocatedComment ret = new LocatedComment(text);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public DocType docType(int line, int col, String elementName, String publicID, String systemID) {
        LocatedDocType ret = new LocatedDocType(elementName, publicID, systemID);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public DocType docType(int line, int col, String elementName, String systemID) {
        LocatedDocType ret = new LocatedDocType(elementName, systemID);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public DocType docType(int line, int col, String elementName) {
        LocatedDocType ret = new LocatedDocType(elementName);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Element element(int line, int col, String name, Namespace namespace) {
        LocatedElement ret = new LocatedElement(name, namespace);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Element element(int line, int col, String name) {
        LocatedElement ret = new LocatedElement(name);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Element element(int line, int col, String name, String uri) {
        LocatedElement ret = new LocatedElement(name, uri);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public Element element(int line, int col, String name, String prefix, String uri) {
        LocatedElement ret = new LocatedElement(name, prefix, uri);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target) {
        LocatedProcessingInstruction ret = new LocatedProcessingInstruction(target);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, Map<String, String> data) {
        LocatedProcessingInstruction ret = new LocatedProcessingInstruction(target, data);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, String data) {
        LocatedProcessingInstruction ret = new LocatedProcessingInstruction(target, data);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name) {
        LocatedEntityRef ret = new LocatedEntityRef(name);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String publicID, String systemID) {
        LocatedEntityRef ret = new LocatedEntityRef(name, publicID, systemID);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String systemID) {
        LocatedEntityRef ret = new LocatedEntityRef(name, systemID);
        ret.setLine(line);
        ret.setColumn(col);
        return ret;
    }
}

