/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class RegexCaptureParser
extends AbstractParser
implements Initializable {
    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.TEXT_PLAIN);
    private Map<String, Pattern> regexMap = new HashMap<String, Pattern>();

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
            String line = reader.readLine();
            HashMap<String, Matcher> matchers = new HashMap<String, Matcher>();
            for (Map.Entry<String, Pattern> entry : this.regexMap.entrySet()) {
                matchers.put(entry.getKey(), entry.getValue().matcher(""));
            }
            while (line != null) {
                for (Map.Entry<String, Pattern> entry : matchers.entrySet()) {
                    Matcher m = (Matcher)((Object)entry.getValue());
                    if (!m.reset(line).find()) continue;
                    String val = m.group(1);
                    metadata.set(entry.getKey(), val);
                }
                line = reader.readLine();
            }
        }
    }

    @Field
    public void setRegexMap(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            String field = e.getKey();
            Pattern pattern = Pattern.compile(e.getValue());
            this.regexMap.put(field, pattern);
        }
    }
}

