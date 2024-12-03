/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermFloatValue;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.css.TermOperator;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.csskit.OutputUtil;
import cz.vutbr.web.csskit.TermListImpl;
import java.util.ArrayList;
import java.util.List;
import org.unbescape.css.CssEscape;

public class TermFunctionImpl
extends TermListImpl
implements TermFunction {
    protected static final TermOperator DEFAULT_ARG_SEP = CSSFactory.getTermFactory().createOperator(',');
    protected String functionName;
    protected boolean valid = true;

    protected TermFunctionImpl() {
    }

    @Override
    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public TermFunction setFunctionName(String functionName) {
        if (functionName == null) {
            throw new IllegalArgumentException("Invalid functionName in function (null)");
        }
        this.functionName = functionName;
        return this;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        this.value = new ArrayList();
        boolean prevMinus = false;
        for (Term<?> term : value) {
            if (term instanceof TermOperator && ((Character)((TermOperator)term).getValue()).charValue() == '-') {
                prevMinus = true;
            } else if (prevMinus) {
                if (this.prependMinus(term)) {
                    this.value.remove(this.value.size() - 1);
                }
                prevMinus = false;
            }
            this.value.add(term);
        }
        return this;
    }

    protected boolean prependMinus(Term<?> term) {
        boolean merged = false;
        if (term instanceof TermFloatValue) {
            TermFloatValue floatT = (TermFloatValue)term;
            floatT.setValue(Float.valueOf(-1.0f * ((Float)floatT.getValue()).floatValue()));
            merged = true;
        } else if (term instanceof TermIdent) {
            TermIdent ident = (TermIdent)term;
            ident.setValue("-" + (String)ident.getValue());
            merged = true;
        } else if (term instanceof TermFunction) {
            TermFunction func = (TermFunction)term;
            func.setFunctionName("-" + func.getFunctionName());
            merged = true;
        }
        return merged;
    }

    @Override
    public List<List<Term<?>>> getSeparatedArgs(Term<?> separator) {
        ArrayList ret = new ArrayList();
        ArrayList cur = new ArrayList();
        for (Term<?> t : this) {
            if (t.equals(separator)) {
                ret.add(cur);
                cur = new ArrayList();
                continue;
            }
            cur.add(t);
        }
        if (!cur.isEmpty()) {
            ret.add(cur);
        }
        return ret;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public List<Term<?>> getSeparatedValues(Term<?> separator, boolean allowKeywords) {
        ArrayList ret = new ArrayList();
        Term curOp = null;
        TermFloatValue curVal = null;
        for (TermFloatValue t : this) {
            if (t.equals(separator)) {
                if (curVal == null) return null;
                if (curOp != null) {
                    if (!(curVal instanceof TermFloatValue)) return null;
                    if (((Character)curOp.getValue()).charValue() == '-') {
                        Float newVal = Float.valueOf(-((Float)((TermFloatValue)curVal).getValue()).floatValue());
                        curVal = (TermFloatValue)curVal.shallowClone();
                        curVal.setValue(newVal);
                    } else if (((Character)curOp.getValue()).charValue() != '+') {
                        return null;
                    }
                }
                ret.add(curVal);
                curVal = null;
                curOp = null;
                continue;
            }
            if (t instanceof TermOperator) {
                if (curOp != null || curVal != null) return null;
                curOp = (TermOperator)((Object)t);
                continue;
            }
            if (t instanceof TermFloatValue || t instanceof TermString) {
                if (curVal != null) return null;
                curVal = t;
                continue;
            }
            if (!allowKeywords || !(t instanceof TermIdent)) return null;
            if (curVal != null) return null;
            curVal = t;
        }
        if (curVal == null) return null;
        if (curOp != null) {
            if (!(curVal instanceof TermFloatValue)) return null;
            if (((Character)curOp.getValue()).charValue() == '-') {
                Float newVal = Float.valueOf(-((Float)((TermFloatValue)curVal).getValue()).floatValue());
                curVal = (TermFloatValue)curVal.shallowClone();
                curVal.setValue(newVal);
            } else if (((Character)curOp.getValue()).charValue() != '+') {
                return null;
            }
        }
        ret.add(curVal);
        return ret;
    }

    @Override
    public List<Term<?>> getValues(boolean allowKeywords) {
        ArrayList ret = new ArrayList();
        TermOperator curOp = null;
        for (Term<?> t : this) {
            if (t instanceof TermOperator) {
                if (curOp == null) {
                    curOp = (TermOperator)t;
                    continue;
                }
                return null;
            }
            if (t instanceof TermFloatValue) {
                TermFloatValue curVal = (TermFloatValue)t;
                if (curOp != null) {
                    if (((Character)curOp.getValue()).charValue() == '-') {
                        Float newVal = Float.valueOf(-((Float)curVal.getValue()).floatValue());
                        curVal = (TermFloatValue)curVal.shallowClone();
                        curVal.setValue(newVal);
                    } else if (((Character)curOp.getValue()).charValue() != '+') {
                        return null;
                    }
                }
                ret.add(curVal);
                curVal = null;
                curOp = null;
                continue;
            }
            if (t instanceof TermIdent) {
                if (allowKeywords && curOp == null) {
                    ret.add(t);
                    continue;
                }
                return null;
            }
            if (curOp == null) {
                ret.add(t);
                continue;
            }
            return null;
        }
        if (curOp != null) {
            return null;
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append(CssEscape.escapeCssIdentifier((String)this.functionName)).append("(");
        sb = OutputUtil.appendFunctionArgs(sb, this.value).append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.functionName == null ? 0 : this.functionName.hashCode());
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
        if (!(obj instanceof TermFunctionImpl)) {
            return false;
        }
        TermFunctionImpl other = (TermFunctionImpl)obj;
        return !(this.functionName == null ? other.functionName != null : !this.functionName.equals(other.functionName));
    }

    protected boolean isNumberArg(Term<?> term) {
        return term instanceof TermNumber || term instanceof TermInteger;
    }

    protected float getNumberArg(Term<?> term) {
        if (term instanceof TermNumber) {
            return ((Float)((TermNumber)term).getValue()).floatValue();
        }
        return ((Float)((TermInteger)term).getValue()).floatValue();
    }

    protected TermAngle getAngleArg(Term<?> term) {
        if (term instanceof TermAngle) {
            return (TermAngle)term;
        }
        if (this.isNumberArg(term) && this.getNumberArg(term) == 0.0f) {
            return CSSFactory.getTermFactory().createAngle(Float.valueOf(0.0f));
        }
        return null;
    }

    protected TermLength getLengthArg(Term<?> term) {
        if (term instanceof TermLength) {
            return (TermLength)term;
        }
        if (this.isNumberArg(term) && this.getNumberArg(term) == 0.0f) {
            return CSSFactory.getTermFactory().createLength(Float.valueOf(0.0f));
        }
        return null;
    }

    protected TermLengthOrPercent getLengthOrPercentArg(Term<?> term) {
        if (term instanceof TermLengthOrPercent) {
            return (TermLengthOrPercent)term;
        }
        if (this.isNumberArg(term) && this.getNumberArg(term) == 0.0f) {
            return CSSFactory.getTermFactory().createLength(Float.valueOf(0.0f));
        }
        return null;
    }

    protected TermAngle convertSideOrCorner(List<Term<?>> aarg) {
        if (aarg.size() > 1 && aarg.size() <= 3) {
            Term<?> dir2;
            TermAngle angle = null;
            Term<?> toTerm = aarg.get(0);
            Term<?> dir1 = aarg.get(1);
            Term<?> term = dir2 = aarg.size() == 3 ? aarg.get(2) : null;
            if (toTerm instanceof TermIdent && toTerm.toString().equals("to") && dir1 instanceof TermIdent && (dir2 == null || dir2 instanceof TermIdent)) {
                TermFactory tf = CSSFactory.getTermFactory();
                switch (dir1.toString()) {
                    case "top": {
                        if (dir2 == null) {
                            angle = tf.createAngle("0", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (dir2.toString().equals("left")) {
                            angle = tf.createAngle("315", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (!dir2.toString().equals("right")) break;
                        angle = tf.createAngle("45", TermNumeric.Unit.deg, 1);
                        break;
                    }
                    case "right": {
                        if (dir2 == null) {
                            angle = tf.createAngle("90", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (dir2.toString().equals("top")) {
                            angle = tf.createAngle("45", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (!dir2.toString().equals("bottom")) break;
                        angle = tf.createAngle("135", TermNumeric.Unit.deg, 1);
                        break;
                    }
                    case "bottom": {
                        if (dir2 == null) {
                            angle = tf.createAngle("180", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (dir2.toString().equals("left")) {
                            angle = tf.createAngle("225", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (!dir2.toString().equals("right")) break;
                        angle = tf.createAngle("135", TermNumeric.Unit.deg, 1);
                        break;
                    }
                    case "left": {
                        if (dir2 == null) {
                            angle = tf.createAngle("270", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (dir2.toString().equals("top")) {
                            angle = tf.createAngle("315", TermNumeric.Unit.deg, 1);
                            break;
                        }
                        if (!dir2.toString().equals("bottom")) break;
                        angle = tf.createAngle("225", TermNumeric.Unit.deg, 1);
                    }
                }
            }
            return angle;
        }
        return null;
    }
}

