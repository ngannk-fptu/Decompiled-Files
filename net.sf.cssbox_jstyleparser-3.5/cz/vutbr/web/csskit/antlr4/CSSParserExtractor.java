/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleList;
import java.util.List;

public interface CSSParserExtractor {
    public List<String> getImportPaths();

    public List<List<MediaQuery>> getImportMedia();

    public RuleList getRules();

    public List<MediaQuery> getMedia();
}

