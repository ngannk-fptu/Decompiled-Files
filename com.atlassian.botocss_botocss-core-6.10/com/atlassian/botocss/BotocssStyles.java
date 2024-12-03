/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cz.vutbr.web.css.CSSException
 *  cz.vutbr.web.css.CSSFactory
 *  cz.vutbr.web.css.StyleSheet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.botocss;

import cz.vutbr.web.css.CSSException;
import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.StyleSheet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BotocssStyles {
    public static final BotocssStyles EMPTY = new BotocssStyles(Collections.EMPTY_LIST);
    private static final Logger log = LoggerFactory.getLogger(BotocssStyles.class);
    private final Iterable<StyleSheet> styleSheets;

    public static BotocssStyles parse(String ... css) {
        ArrayList<StyleSheet> styleSheets = new ArrayList<StyleSheet>(css.length);
        long start = System.currentTimeMillis();
        for (String stylesheet : css) {
            try {
                styleSheets.add(CSSFactory.parse((String)stylesheet));
            }
            catch (CSSException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Parsing {} stylesheets took {} ms", (Object)styleSheets.size(), (Object)(System.currentTimeMillis() - start));
        return new BotocssStyles(styleSheets);
    }

    private BotocssStyles(Iterable<StyleSheet> styleSheets) {
        this.styleSheets = styleSheets;
    }

    Iterable<StyleSheet> getStyleSheets() {
        return this.styleSheets;
    }
}

