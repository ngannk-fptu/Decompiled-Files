/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.TermURI;
import java.util.List;

public interface RuleFontFace
extends RuleBlock<Declaration>,
PrettyOutput {
    public String getFontFamily();

    public List<Source> getSources();

    public CSSProperty.FontStyle getFontStyle();

    public CSSProperty.FontWeight getFontWeight();

    public List<String> getUnicodeRanges();

    public static interface SourceURL
    extends Source {
        public TermURI getURI();

        public String getFormat();
    }

    public static interface SourceLocal
    extends Source {
        public String getName();
    }

    public static interface Source {
    }
}

