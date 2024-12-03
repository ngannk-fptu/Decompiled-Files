/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.lookup;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;
import org.xml.sax.InputSource;

final class XmlStringLookup
extends AbstractStringLookup {
    static final XmlStringLookup INSTANCE = new XmlStringLookup();

    private XmlStringLookup() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(SPLIT_STR);
        int keyLen = keys.length;
        if (keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad XML key format [%s]; expected format is DocumentPath:XPath.", key);
        }
        String documentPath = keys[0];
        String xpath = StringUtils.substringAfter((String)key, (int)58);
        try (InputStream inputStream = Files.newInputStream(Paths.get(documentPath, new String[0]), new OpenOption[0]);){
            String string = XPathFactory.newInstance().newXPath().evaluate(xpath, new InputSource(inputStream));
            return string;
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up XML document [%s] and XPath [%s].", documentPath, xpath);
        }
    }
}

