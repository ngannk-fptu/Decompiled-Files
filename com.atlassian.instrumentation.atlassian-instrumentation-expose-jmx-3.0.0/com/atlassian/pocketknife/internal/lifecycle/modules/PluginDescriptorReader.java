/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.plugin.PluginParseException;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Nullable;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PluginDescriptorReader {
    static final String RESOURCE = "resource";
    static final String PLUGIN_INFO = "plugin-info";
    private final Document descriptor;

    public static PluginDescriptorReader createDescriptorReader(InputStream source) throws PluginParseException {
        SAXReader reader = new SAXReader();
        reader.setMergeAdjacentText(true);
        try {
            Document doc = reader.read(source);
            return new PluginDescriptorReader(doc);
        }
        catch (DocumentException e) {
            throw new PluginParseException("Cannot parse XML plugin descriptor", (Throwable)e);
        }
    }

    public PluginDescriptorReader(Document descriptor) {
        this.descriptor = (Document)Preconditions.checkNotNull((Object)descriptor);
    }

    private Element getPluginElement() {
        return this.descriptor.getRootElement();
    }

    public Iterable<Element> getModules() {
        return Iterables.filter((Iterable)Iterables.filter(PluginDescriptorReader.elements(this.getPluginElement()), (Predicate)Predicates.not((Predicate)Predicates.or((Predicate)new ElementWithName(PLUGIN_INFO), (Predicate)new ElementWithName(RESOURCE)))), (Predicate)new Predicate<Element>(){

            public boolean apply(Element module) {
                return true;
            }
        });
    }

    static List<Element> elements(Element e) {
        return e.elements();
    }

    private static final class ElementWithName
    implements Predicate<Element> {
        private final String name;

        private ElementWithName(String name) {
            this.name = (String)Preconditions.checkNotNull((Object)name);
        }

        public boolean apply(@Nullable Element element) {
            return element != null && this.name.equalsIgnoreCase(element.getName());
        }
    }
}

