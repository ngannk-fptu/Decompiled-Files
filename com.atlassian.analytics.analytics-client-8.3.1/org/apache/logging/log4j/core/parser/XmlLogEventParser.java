/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.jackson.Log4jXmlObjectMapper;
import org.apache.logging.log4j.core.parser.AbstractJacksonLogEventParser;

public class XmlLogEventParser
extends AbstractJacksonLogEventParser {
    public XmlLogEventParser() {
        super((ObjectMapper)((Object)new Log4jXmlObjectMapper()));
    }
}

