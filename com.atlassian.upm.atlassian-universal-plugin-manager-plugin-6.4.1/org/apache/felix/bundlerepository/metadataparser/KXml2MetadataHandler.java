/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.metadataparser.MetadataHandler;
import org.apache.felix.bundlerepository.metadataparser.kxmlsax.KXml2SAXParser;

public class KXml2MetadataHandler
extends MetadataHandler {
    public KXml2MetadataHandler(Logger logger) {
        super(logger);
    }

    public void parse(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        KXml2SAXParser parser = new KXml2SAXParser(br);
        parser.parseXML(this.m_handler);
    }
}

