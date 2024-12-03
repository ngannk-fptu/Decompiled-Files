/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.DeclarationImpl;

public class AssignedDeclaration
extends DeclarationImpl
implements Declaration {
    protected CombinedSelector.Specificity spec;
    protected StyleSheet.Origin origin;

    public AssignedDeclaration(Declaration d, CombinedSelector.Specificity spec, StyleSheet.Origin origin) {
        super(d);
        this.spec = spec;
        this.origin = origin;
    }

    public AssignedDeclaration(Declaration d, CombinedSelector s, StyleSheet.Origin origin) {
        this(d, s.computeSpecificity(), origin);
    }

    @Override
    public int compareTo(Declaration other) {
        if (!(other instanceof AssignedDeclaration)) {
            return super.compareTo(other);
        }
        AssignedDeclaration o = (AssignedDeclaration)other;
        int res = this.getOriginOrder() - o.getOriginOrder();
        if (res == 0) {
            return this.spec.compareTo(o.spec);
        }
        return res;
    }

    public int getOriginOrder() {
        if (this.important) {
            if (this.origin == StyleSheet.Origin.AUTHOR) {
                return 4;
            }
            if (this.origin == StyleSheet.Origin.AGENT) {
                return 1;
            }
            return 5;
        }
        if (this.origin == StyleSheet.Origin.AUTHOR) {
            return 3;
        }
        if (this.origin == StyleSheet.Origin.AGENT) {
            return 1;
        }
        return 2;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.spec == null ? 0 : this.spec.hashCode());
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
        if (!(obj instanceof AssignedDeclaration)) {
            return false;
        }
        AssignedDeclaration other = (AssignedDeclaration)obj;
        return !(this.spec == null ? other.spec != null : !this.spec.equals(other.spec));
    }
}

