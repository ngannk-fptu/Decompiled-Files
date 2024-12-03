/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.IgnoredSchema;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class NamespaceState
extends State {
    private boolean inlineModuleExpected = false;
    private boolean bailOut = false;
    private String namespace;
    IslandSchemaReader moduleReader;

    protected void startSelf() {
        super.startSelf();
        this.namespace = this.startTag.getAttribute("name");
        if (this.namespace == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"namespace", (Object)"name");
            return;
        }
        if (this.getReader().grammar.moduleMap.containsKey(this.namespace)) {
            this.reader.reportError("RELAXNSReader.NamespaceCollision", (Object)this.namespace);
            return;
        }
        String validation = this.startTag.getAttribute("validation");
        if ("false".equals(validation)) {
            this.getReader().grammar.moduleMap.put(this.namespace, new IgnoredSchema());
            return;
        }
        String language = this.startTag.getAttribute("language");
        if (language == null) {
            language = "http://www.xml.gr.jp/xmlns/relaxCore";
        }
        this.moduleReader = this.getReader().getIslandSchemaReader(language, this.namespace);
        if (this.moduleReader == null) {
            this.reader.reportError("RELAXNSReader.UnknownLanguage", (Object)language);
            this.bailOut = true;
            return;
        }
        String moduleLocation = this.startTag.getAttribute("moduleLocation");
        if (moduleLocation != null) {
            try {
                InputSource is = this.reader.resolveLocation(this, moduleLocation);
                XMLReader parser = this.reader.parserFactory.newSAXParser().getXMLReader();
                parser.setContentHandler(this.moduleReader);
                parser.parse(is);
            }
            catch (ParserConfigurationException e) {
                this.reader.controller.error(e, this.getLocation());
            }
            catch (IOException e) {
                this.reader.controller.error(e, this.getLocation());
            }
            catch (SAXException e) {
                this.reader.controller.error(e, this.getLocation());
            }
            catch (AbortException e) {
                // empty catch block
            }
            this.getSchema(this.moduleReader);
            return;
        }
        this.inlineModuleExpected = true;
    }

    private void getSchema(IslandSchemaReader moduleReader) {
        IslandSchema schema = moduleReader.getSchema();
        if (schema == null) {
            this.reader.controller.setErrorFlag();
            schema = new IgnoredSchema();
        }
        this.getReader().grammar.moduleMap.put(this.namespace, schema);
    }

    public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException {
        if (this.bailOut) {
            this.reader.pushState(new IgnoreState(), this, new StartTagInfo(namespace, localName, qName, atts, this.reader));
            return;
        }
        if (!this.inlineModuleExpected) {
            this.reader.reportError("GrammarReader.MalplacedElement", (Object)qName);
            this.bailOut = true;
            return;
        }
        this.moduleReader.startDocument();
        this.moduleReader.setDocumentLocator(this.reader.getLocator());
        GrammarReader.PrefixResolver resolver = this.reader.prefixResolver;
        Vector<String> prefixes = new Vector<String>();
        while (resolver instanceof GrammarReader.ChainPrefixResolver) {
            GrammarReader.ChainPrefixResolver ch = (GrammarReader.ChainPrefixResolver)resolver;
            prefixes.add(ch.prefix);
            resolver = ch.previous;
        }
        for (int i = 0; i < prefixes.size(); ++i) {
            String p = (String)prefixes.get(i);
            this.moduleReader.startPrefixMapping(p, this.reader.prefixResolver.resolve(p));
        }
        this.moduleReader.startElement(namespace, localName, qName, atts);
        CutInFilter cutInFilter = new CutInFilter();
        cutInFilter.setContentHandler(this.moduleReader);
        this.reader.setContentHandler(cutInFilter);
        this.inlineModuleExpected = false;
    }

    public void endElement(String namespace, String localName, String qName) {
        if (this.inlineModuleExpected) {
            this.reader.reportError("RELAXNSReader.InlineModuleNotFound");
        }
        this.reader.popState();
    }

    public void endDocument() {
        throw new Error();
    }

    protected RELAXNSReader getReader() {
        return (RELAXNSReader)this.reader;
    }

    private class CutInFilter
    extends XMLFilterImpl {
        private int depth = 0;

        private CutInFilter() {
        }

        public void startElement(String a, String b, String c, Attributes d) throws SAXException {
            ++this.depth;
            super.startElement(a, b, c, d);
        }

        public void endElement(String a, String b, String c) throws SAXException {
            super.endElement(a, b, c);
            if (this.depth == 0) {
                super.endDocument();
                NamespaceState.this.getReader().setContentHandler(NamespaceState.this);
                NamespaceState.this.getSchema(NamespaceState.this.moduleReader);
                return;
            }
            --this.depth;
        }
    }
}

