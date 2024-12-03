/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserFactory;
import org.xml.sax.SAXException;

public class AutoDetectParserFactory
extends ParserFactory {
    public static final String TIKA_CONFIG_PATH = "tika_config_path";

    public AutoDetectParserFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public Parser build() throws IOException, SAXException, TikaException {
        String tikaConfigPath = (String)this.args.remove(TIKA_CONFIG_PATH);
        TikaConfig tikaConfig = null;
        if (tikaConfigPath != null) {
            if (Files.isReadable(Paths.get(tikaConfigPath, new String[0]))) {
                tikaConfig = new TikaConfig(Paths.get(tikaConfigPath, new String[0]));
            } else if (this.getClass().getResource(tikaConfigPath) != null) {
                try (InputStream is = this.getClass().getResourceAsStream(tikaConfigPath);){
                    tikaConfig = new TikaConfig(is);
                }
            }
        }
        if (tikaConfig == null) {
            tikaConfig = TikaConfig.getDefaultConfig();
        }
        return new AutoDetectParser(tikaConfig);
    }
}

