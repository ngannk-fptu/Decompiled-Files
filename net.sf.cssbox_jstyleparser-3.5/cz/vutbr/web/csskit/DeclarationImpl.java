/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.csskit.AbstractRule;
import cz.vutbr.web.csskit.OutputUtil;

public class DeclarationImpl
extends AbstractRule<Term<?>>
implements Declaration {
    protected String property;
    protected boolean important;
    protected Declaration.Source source;

    protected DeclarationImpl() {
        this.property = "";
        this.important = false;
        this.source = null;
    }

    protected DeclarationImpl(Declaration clone) {
        this.property = clone.getProperty();
        this.important = clone.isImportant();
        this.source = new Declaration.Source(clone.getSource());
        this.replaceAll(clone.asList());
    }

    public boolean isInherited(int level) {
        return false;
    }

    public int getInheritanceLevel() {
        return 0;
    }

    @Override
    public int compareTo(Declaration o) {
        if (this.isImportant() && !o.isImportant()) {
            return 1;
        }
        if (o.isImportant() && !this.isImportant()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String getProperty() {
        return this.property;
    }

    @Override
    public void setProperty(String property) {
        this.property = property.toLowerCase();
    }

    @Override
    public boolean isImportant() {
        return this.important;
    }

    @Override
    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public Declaration.Source getSource() {
        return this.source;
    }

    @Override
    public void setSource(Declaration.Source src) {
        this.source = src;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append(this.property).append(": ");
        sb = OutputUtil.appendList(sb, this.list, "");
        if (this.important) {
            sb.append(" ").append("!important");
        }
        sb.append(";\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.important ? 1231 : 1237);
        result = 31 * result + (this.property == null ? 0 : this.property.hashCode());
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
        if (!(obj instanceof DeclarationImpl)) {
            return false;
        }
        DeclarationImpl other = (DeclarationImpl)obj;
        if (this.important != other.important) {
            return false;
        }
        return !(this.property == null ? other.property != null : !this.property.equals(other.property));
    }
}

