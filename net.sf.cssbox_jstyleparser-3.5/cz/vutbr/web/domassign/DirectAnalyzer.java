/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.domassign.Analyzer;
import cz.vutbr.web.domassign.AnalyzerUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DirectAnalyzer
extends Analyzer {
    private static final Logger log = LoggerFactory.getLogger(DirectAnalyzer.class);

    public DirectAnalyzer(StyleSheet sheet) {
        super(sheet);
    }

    public DirectAnalyzer(List<StyleSheet> sheets) {
        super(sheets);
    }

    public NodeData getElementStyle(Element el, Selector.PseudoElementType pseudo, MediaSpec media) {
        Analyzer.OrderedRule[] applicableRules = AnalyzerUtil.getApplicableRules(this.sheets, el, media);
        return AnalyzerUtil.getElementStyle(el, pseudo, this.getElementMatcher(), this.getMatchCondition(), applicableRules);
    }

    public NodeData getElementStyle(Element el, Selector.PseudoElementType pseudo, String media) {
        return this.getElementStyle(el, pseudo, new MediaSpec(media));
    }
}

