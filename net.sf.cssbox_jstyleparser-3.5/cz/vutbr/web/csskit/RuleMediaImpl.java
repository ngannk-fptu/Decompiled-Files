/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleMedia;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.Collections;
import java.util.List;

public class RuleMediaImpl
extends AbstractRuleBlock<RuleSet>
implements RuleMedia {
    protected List<MediaQuery> media = Collections.emptyList();

    protected RuleMediaImpl() {
    }

    @Override
    public List<MediaQuery> getMediaQueries() {
        return this.media;
    }

    @Override
    public RuleMedia setMediaQueries(List<MediaQuery> medias) {
        this.media = medias;
        return this;
    }

    @Override
    public void setStyleSheet(StyleSheet stylesheet) {
        super.setStyleSheet(stylesheet);
        for (RuleSet set : this.list) {
            set.setStyleSheet(stylesheet);
        }
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append("@media ");
        sb = OutputUtil.appendList(sb, this.media, ", ");
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "\n", depth + 1);
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.media == null ? 0 : this.media.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof RuleMediaImpl)) {
            return false;
        }
        RuleMediaImpl other = (RuleMediaImpl)obj;
        return !(this.media == null ? other.media != null : !this.media.equals(other.media));
    }
}

