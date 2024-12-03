/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.confluence.setup.bandana.BandanaXmlParser;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class FileBandanaXmlParser
implements BandanaXmlParser {
    private final File bandanaDirectory;

    public FileBandanaXmlParser(File bandanaDirectory) {
        this.bandanaDirectory = bandanaDirectory;
    }

    @Override
    public Map getEntries(ConfluenceBandanaContext context) {
        File xmlFile = context.isGlobal() ? new File(this.bandanaDirectory, "confluence-global.bandana.xml") : new File(this.bandanaDirectory + "/" + context.getSpaceKey(), "confluence-space.bandana.xml");
        if (!xmlFile.exists()) {
            return new HashMap();
        }
        return this.getEntries(xmlFile);
    }

    private Map getEntries(File xmlFile) {
        Document document;
        HashMap<String, String> result = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(xmlFile);
        }
        catch (Exception e) {
            throw new RuntimeException("Error reading Bandana XML file");
        }
        List list = document.selectNodes("//values/entry");
        for (Node node : list) {
            if (!(node instanceof Element)) continue;
            Element entry = (Element)node;
            Iterator children = entry.elementIterator();
            Element key = (Element)children.next();
            Element value = (Element)children.next();
            result.put(key.getText(), value.asXML());
        }
        return result;
    }
}

