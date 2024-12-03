/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.util.Arrays;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.expression.EqualTo;
import net.sf.ehcache.search.expression.GreaterThan;
import net.sf.ehcache.search.expression.GreaterThanOrEqual;
import net.sf.ehcache.search.expression.IsNull;
import net.sf.ehcache.search.expression.LessThan;
import net.sf.ehcache.search.expression.LessThanOrEqual;
import net.sf.ehcache.search.expression.NotEqualTo;
import net.sf.ehcache.search.expression.NotNull;
import net.sf.ehcache.search.parser.MAttribute;
import net.sf.ehcache.search.parser.ModelElement;

public interface MCriteria
extends ModelElement<Criteria> {

    public static class Like
    implements MCriteria {
        private final MAttribute attr;
        private final String originalRegexp;
        private final String sanitizedRegex;

        public Like(MAttribute attr, String regexp) {
            this.attr = attr;
            this.originalRegexp = regexp;
            this.sanitizedRegex = regexp.replace('%', '*').replace('_', '?');
        }

        public MAttribute getAttribute() {
            return this.attr;
        }

        public String getLikeRegex() {
            return this.originalRegexp;
        }

        public String getILikeRegex() {
            return this.sanitizedRegex;
        }

        public String toString() {
            return this.attr + " like " + this.originalRegexp;
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            return new net.sf.ehcache.search.expression.ILike(this.attr.asEhcacheAttributeString(), this.getILikeRegex());
        }

        public int hashCode() {
            int prime = 19;
            int result = 1;
            result = 19 * result + (this.attr == null ? 0 : this.attr.hashCode());
            result = 19 * result + (this.originalRegexp == null ? 0 : this.originalRegexp.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Like other = (Like)obj;
            if (this.attr == null ? other.attr != null : !this.attr.equals(other.attr)) {
                return false;
            }
            return !(this.originalRegexp == null ? other.originalRegexp != null : !this.originalRegexp.equals(other.originalRegexp));
        }
    }

    public static class Not
    implements MCriteria {
        private final MCriteria crit;

        public Not(MCriteria crit1) {
            this.crit = crit1;
        }

        public String toString() {
            return "(not " + this.crit + ")";
        }

        public MCriteria getCriterium() {
            return this.crit;
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            return new net.sf.ehcache.search.expression.Not((Criteria)this.crit.asEhcacheObject(loader));
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.crit == null ? 0 : this.crit.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Not other = (Not)obj;
            return !(this.crit == null ? other.crit != null : !this.crit.equals(other.crit));
        }
    }

    public static class And
    implements MCriteria {
        private final MCriteria[] crits;

        public And(MCriteria ... crits) {
            this.crits = crits;
        }

        public MCriteria[] getCriteria() {
            return this.crits;
        }

        public String toString() {
            Object s = this.crits[0].toString();
            for (int i = 1; i < this.crits.length; ++i) {
                s = (String)s + " and " + this.crits[i];
            }
            return "(" + (String)s + ")";
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            net.sf.ehcache.search.expression.And crit = new net.sf.ehcache.search.expression.And((Criteria)this.crits[this.crits.length - 2].asEhcacheObject(loader), (Criteria)this.crits[this.crits.length - 1].asEhcacheObject(loader));
            if (this.crits.length > 2) {
                for (int i = this.crits.length - 3; i >= 0; --i) {
                    crit = new net.sf.ehcache.search.expression.And((Criteria)this.crits[i].asEhcacheObject(loader), (Criteria)crit);
                }
            }
            return crit;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.hashCode(this.crits);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            And other = (And)obj;
            return Arrays.equals(this.crits, other.crits);
        }
    }

    public static class Or
    implements MCriteria {
        private MCriteria[] crits;

        public Or(MCriteria ... crits) {
            this.crits = crits;
        }

        public MCriteria[] getCrits() {
            return this.crits;
        }

        public String toString() {
            Object s = this.crits[0].toString();
            for (int i = 1; i < this.crits.length; ++i) {
                s = (String)s + " or " + this.crits[i];
            }
            return "(" + (String)s + ")";
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            net.sf.ehcache.search.expression.Or crit = new net.sf.ehcache.search.expression.Or((Criteria)this.crits[this.crits.length - 2].asEhcacheObject(loader), (Criteria)this.crits[this.crits.length - 1].asEhcacheObject(loader));
            if (this.crits.length > 2) {
                for (int i = this.crits.length - 3; i >= 0; --i) {
                    crit = new net.sf.ehcache.search.expression.Or((Criteria)this.crits[i].asEhcacheObject(loader), (Criteria)crit);
                }
            }
            return crit;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.hashCode(this.crits);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Or other = (Or)obj;
            return Arrays.equals(this.crits, other.crits);
        }
    }

    public static class ILike
    implements MCriteria {
        private final MAttribute attr;
        private final String regexp;

        public ILike(MAttribute attr, String regexp) {
            this.attr = attr;
            this.regexp = regexp;
        }

        public MAttribute getAttribute() {
            return this.attr;
        }

        public String getRegexp() {
            return this.regexp;
        }

        public String toString() {
            return this.attr + " ilike " + this.regexp;
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            return new net.sf.ehcache.search.expression.ILike(this.attr.asEhcacheAttributeString(), this.getRegexp());
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.attr == null ? 0 : this.attr.hashCode());
            result = 31 * result + (this.regexp == null ? 0 : this.regexp.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ILike other = (ILike)obj;
            if (this.attr == null ? other.attr != null : !this.attr.equals(other.attr)) {
                return false;
            }
            return !(this.regexp == null ? other.regexp != null : !this.regexp.equals(other.regexp));
        }
    }

    public static class Between
    implements MCriteria {
        private final MAttribute attr;
        private final ModelElement<?> min;
        private final boolean includeMin;
        private final ModelElement<?> max;
        private final boolean includeMax;

        public Between(MAttribute attr, ModelElement<?> min, boolean includeMin, ModelElement<?> max, boolean includeMax) {
            this.attr = attr;
            this.min = min;
            this.includeMin = includeMin;
            this.max = max;
            this.includeMax = includeMax;
        }

        public MAttribute getAttribute() {
            return this.attr;
        }

        public ModelElement<?> getMin() {
            return this.min;
        }

        public boolean isIncludeMin() {
            return this.includeMin;
        }

        public ModelElement<?> getMax() {
            return this.max;
        }

        public boolean isIncludeMax() {
            return this.includeMax;
        }

        public String toString() {
            return this.attr + " between " + (this.includeMin ? "[" : "") + this.min + " and " + this.max + (this.includeMax ? "]" : "");
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            return new net.sf.ehcache.search.expression.Between(this.attr.asEhcacheAttributeString(), this.getMin().asEhcacheObject(loader), this.getMax().asEhcacheObject(loader), this.isIncludeMin(), this.isIncludeMax());
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.attr == null ? 0 : this.attr.hashCode());
            result = 31 * result + (this.includeMax ? 1231 : 1237);
            result = 31 * result + (this.includeMin ? 1231 : 1237);
            result = 31 * result + (this.max == null ? 0 : this.max.hashCode());
            result = 31 * result + (this.min == null ? 0 : this.min.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Between other = (Between)obj;
            if (this.attr == null ? other.attr != null : !this.attr.equals(other.attr)) {
                return false;
            }
            if (this.includeMax != other.includeMax) {
                return false;
            }
            if (this.includeMin != other.includeMin) {
                return false;
            }
            if (this.max == null ? other.max != null : !this.max.equals(other.max)) {
                return false;
            }
            return !(this.min == null ? other.min != null : !this.min.equals(other.min));
        }
    }

    public static final class Simple
    implements MCriteria {
        private final MAttribute attr;
        private final SimpleOp op;
        private final ModelElement<?> rhs;

        public Simple(MAttribute attr, SimpleOp op, ModelElement<?> rhs) {
            this.attr = attr;
            this.op = op;
            this.rhs = rhs;
        }

        public MAttribute getAttribute() {
            return this.attr;
        }

        public SimpleOp getOp() {
            return this.op;
        }

        public ModelElement<?> getRhs() {
            return this.rhs;
        }

        public String toString() {
            return this.attr + " " + this.op.getSymbol() + " " + this.rhs;
        }

        @Override
        public Criteria asEhcacheObject(ClassLoader loader) {
            switch (this.op) {
                case EQ: {
                    return new EqualTo(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case NE: {
                    return new NotEqualTo(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case GT: {
                    return new GreaterThan(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case LE: {
                    return new LessThanOrEqual(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case LT: {
                    return new LessThan(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case GE: {
                    return new GreaterThanOrEqual(this.attr.asEhcacheAttributeString(), this.getRhs().asEhcacheObject(loader));
                }
                case NULL: {
                    return new IsNull(this.attr.asEhcacheAttributeString());
                }
                case NOT_NULL: {
                    return new NotNull(this.attr.asEhcacheAttributeString());
                }
            }
            throw new IllegalStateException("Unrecognized op: " + this.op);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.attr == null ? 0 : this.attr.hashCode());
            result = 31 * result + (this.op == null ? 0 : this.op.hashCode());
            result = 31 * result + (this.rhs == null ? 0 : this.rhs.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Simple other = (Simple)obj;
            if (this.attr == null ? other.attr != null : !this.attr.equals(other.attr)) {
                return false;
            }
            if (this.op != other.op) {
                return false;
            }
            return !(this.rhs == null ? other.rhs != null : !this.rhs.equals(other.rhs));
        }
    }

    public static enum SimpleOp {
        GE(">="),
        LT("<"),
        GT(">"),
        LE("<"),
        NULL("IS NULL"),
        NOT_NULL("IS NOT NULL"),
        EQ("="),
        NE("!=");

        private String symbol;

        private SimpleOp(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return this.symbol;
        }
    }
}

