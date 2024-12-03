/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.RuleImport;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;

@Deprecated
public class RuleImportImpl
extends AbstractRuleBlock<String>
implements RuleImport {
    protected String uri = "";

    protected RuleImportImpl() {
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public RuleImport setURI(String uri) {
        if (uri == null) {
            return this;
        }
        this.uri = uri.replaceAll("^url\\(", "").replaceAll("\\)$", "").replaceAll("^'", "").replaceAll("^\"", "").replaceAll("'$", "").replaceAll("\"$", "");
        return this;
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("@import ").append("url('").append(this.uri).append("')");
        if (this.list.size() != 0) {
            sb.append(" ");
        }
        sb = OutputUtil.appendList(sb, this.list, ", ");
        sb.append(";\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.uri == null ? 0 : this.uri.hashCode());
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
        if (!(obj instanceof RuleImportImpl)) {
            return false;
        }
        RuleImportImpl other = (RuleImportImpl)obj;
        return !(this.uri == null ? other.uri != null : !this.uri.equals(other.uri));
    }
}

