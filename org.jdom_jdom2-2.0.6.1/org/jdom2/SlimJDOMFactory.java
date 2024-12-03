/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.StringBin;
import org.jdom2.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SlimJDOMFactory
extends DefaultJDOMFactory {
    private StringBin cache = new StringBin();
    private final boolean cachetext;

    public SlimJDOMFactory() {
        this(true);
    }

    public SlimJDOMFactory(boolean cachetext) {
        this.cachetext = cachetext;
    }

    public void clearCache() {
        this.cache = new StringBin();
    }

    @Override
    public Attribute attribute(String name, String value, Namespace namespace) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value, namespace);
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value, type, namespace);
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type, Namespace namespace) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value, type, namespace);
    }

    @Override
    public Attribute attribute(String name, String value) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value);
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value, type);
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type) {
        return super.attribute(this.cache.reuse(name), this.cachetext ? this.cache.reuse(value) : value, type);
    }

    @Override
    public CDATA cdata(int line, int col, String str) {
        return super.cdata(line, col, this.cachetext ? this.cache.reuse(str) : str);
    }

    @Override
    public Text text(int line, int col, String str) {
        return super.text(line, col, this.cachetext ? this.cache.reuse(str) : str);
    }

    @Override
    public Comment comment(int line, int col, String text) {
        return super.comment(line, col, this.cachetext ? this.cache.reuse(text) : text);
    }

    @Override
    public DocType docType(int line, int col, String elementName, String publicID, String systemID) {
        return super.docType(line, col, this.cache.reuse(elementName), publicID, systemID);
    }

    @Override
    public DocType docType(int line, int col, String elementName, String systemID) {
        return super.docType(line, col, this.cache.reuse(elementName), systemID);
    }

    @Override
    public DocType docType(int line, int col, String elementName) {
        return super.docType(line, col, this.cache.reuse(elementName));
    }

    @Override
    public Element element(int line, int col, String name, Namespace namespace) {
        return super.element(line, col, this.cache.reuse(name), namespace);
    }

    @Override
    public Element element(int line, int col, String name) {
        return super.element(line, col, this.cache.reuse(name));
    }

    @Override
    public Element element(int line, int col, String name, String uri) {
        return super.element(line, col, this.cache.reuse(name), uri);
    }

    @Override
    public Element element(int line, int col, String name, String prefix, String uri) {
        return super.element(line, col, this.cache.reuse(name), prefix, uri);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, Map<String, String> data) {
        return super.processingInstruction(line, col, this.cache.reuse(target), data);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, String data) {
        return super.processingInstruction(line, col, this.cache.reuse(target), data);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target) {
        return super.processingInstruction(line, col, this.cache.reuse(target));
    }

    @Override
    public EntityRef entityRef(int line, int col, String name) {
        return super.entityRef(line, col, this.cache.reuse(name));
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String publicID, String systemID) {
        return super.entityRef(line, col, this.cache.reuse(name), publicID, systemID);
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String systemID) {
        return super.entityRef(line, col, this.cache.reuse(name), systemID);
    }
}

