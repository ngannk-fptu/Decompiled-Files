/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.fn.GenericGradient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenericRadialGradient
extends GenericGradient {
    private static final TermPercent DEFAULT_POSITION = CSSFactory.getTermFactory().createPercent(Float.valueOf(50.0f));
    private static final TermIdent DEFAULT_SHAPE = CSSFactory.getTermFactory().createIdent("ellipse");
    private static final TermIdent CIRCLE_SHAPE = CSSFactory.getTermFactory().createIdent("circle");
    private static final TermIdent DEFAULT_SIZE = CSSFactory.getTermFactory().createIdent("farthest-corner");
    private TermIdent shape;
    private TermLengthOrPercent[] size;
    private TermIdent sizeIdent;
    private TermLengthOrPercent[] position;

    public TermIdent getShape() {
        return this.shape;
    }

    public TermLengthOrPercent[] getSize() {
        return this.size;
    }

    public TermIdent getSizeIdent() {
        return this.sizeIdent;
    }

    public TermLengthOrPercent[] getPosition() {
        return this.position;
    }

    @Override
    public TermList setValue(List<Term<?>> value) {
        super.setValue(value);
        List<List<Term<?>>> args = this.getSeparatedArgs(DEFAULT_ARG_SEP);
        if (args.size() > 1) {
            int firstStop = 0;
            if (this.decodeShape(args.get(0))) {
                firstStop = 1;
            } else {
                this.sizeIdent = DEFAULT_SIZE;
                this.shape = DEFAULT_SHAPE;
                this.position = new TermLengthOrPercent[2];
                this.position[0] = this.position[1] = DEFAULT_POSITION;
            }
            this.loadColorStops(args, firstStop);
            if (this.getColorStops() != null) {
                this.setValid(true);
            }
        }
        return this;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean decodeShape(List<Term<?>> arglist) {
        Term<Object> arg;
        List<Term<Object>> args = new ArrayList(arglist);
        int atpos = -1;
        for (int i = 0; i < args.size(); ++i) {
            arg = (Term<Object>)args.get(i);
            if (!(arg instanceof TermIdent) || !((String)((TermIdent)arg).getValue()).equalsIgnoreCase("at")) continue;
            atpos = i;
            break;
        }
        if (atpos != -1) {
            List<Term<?>> posList = args.subList(atpos + 1, args.size());
            if (!this.decodePosition(posList)) {
                return false;
            }
            args = args.subList(0, atpos);
        } else {
            this.position = new TermLengthOrPercent[2];
            this.position[0] = this.position[1] = DEFAULT_POSITION;
        }
        boolean isCircle = false;
        Iterator<Term<Object>> it = args.iterator();
        while (it.hasNext()) {
            Term<Object> arg2 = it.next();
            String idval = arg2 instanceof TermIdent ? (String)((TermIdent)arg2).getValue() : null;
            if (idval == null || !idval.equalsIgnoreCase("circle") && !idval.equalsIgnoreCase("ellipse")) continue;
            this.shape = (TermIdent)arg2;
            isCircle = idval.equalsIgnoreCase("circle");
            it.remove();
            break;
        }
        if (this.shape == null) {
            if (args.size() == 0) {
                this.sizeIdent = DEFAULT_SIZE;
                this.shape = DEFAULT_SHAPE;
                return true;
            } else if (args.size() == 1) {
                arg = args.get(0);
                if (this.isExtentKeyword(arg)) {
                    this.sizeIdent = (TermIdent)arg;
                    this.shape = DEFAULT_SHAPE;
                    return true;
                } else {
                    if (!(arg instanceof TermLength)) return false;
                    this.size = new TermLengthOrPercent[1];
                    this.size[0] = (TermLength)arg;
                    this.shape = CIRCLE_SHAPE;
                }
                return true;
            } else {
                if (args.size() != 2) return false;
                this.size = new TermLengthOrPercent[2];
                int i = 0;
                for (Term<Object> arg3 : args) {
                    if (arg3 instanceof TermLengthOrPercent) {
                        this.size[i++] = (TermLengthOrPercent)arg3;
                        continue;
                    }
                    this.size = null;
                    return false;
                }
                this.shape = DEFAULT_SHAPE;
            }
            return true;
        } else if (!isCircle) {
            if (args.size() == 0) {
                this.sizeIdent = DEFAULT_SIZE;
                return true;
            } else if (args.size() == 1) {
                arg = args.get(0);
                if (!this.isExtentKeyword(arg)) return false;
                this.sizeIdent = (TermIdent)arg;
                return true;
            } else {
                if (args.size() != 2) return false;
                this.size = new TermLengthOrPercent[2];
                int i = 0;
                for (Term<Object> arg3 : args) {
                    if (arg3 instanceof TermLengthOrPercent) {
                        this.size[i++] = (TermLengthOrPercent)arg3;
                        continue;
                    }
                    this.size = null;
                    return false;
                }
            }
            return true;
        } else if (args.size() == 0) {
            this.sizeIdent = DEFAULT_SIZE;
            return true;
        } else {
            if (args.size() != 1) return false;
            arg = args.get(0);
            if (this.isExtentKeyword(arg)) {
                this.sizeIdent = (TermIdent)arg;
                return true;
            } else {
                if (!(arg instanceof TermLength)) return false;
                this.size = new TermLengthOrPercent[1];
                this.size[0] = (TermLength)arg;
            }
        }
        return true;
    }

    private boolean decodePosition(List<Term<?>> arglist) {
        if (arglist.size() == 1 || arglist.size() == 2) {
            this.position = new TermLengthOrPercent[2];
            for (Term<?> term : arglist) {
                if (term instanceof TermIdent || term instanceof TermLengthOrPercent) {
                    this.storeBackgroundPosition(this.position, term);
                    continue;
                }
                return false;
            }
            int assigned = 0;
            int valid = 0;
            for (int i = 0; i < 2; ++i) {
                if (this.position[i] == null) {
                    this.position[i] = DEFAULT_POSITION;
                    ++valid;
                    continue;
                }
                if (!(this.position[i] instanceof TermLengthOrPercent)) continue;
                ++assigned;
                ++valid;
            }
            return assigned > 0 && valid == 2;
        }
        return false;
    }

    private void storeBackgroundPosition(Term<?>[] storage, Term<?> term) {
        if (term instanceof TermIdent) {
            String idval = (String)((TermIdent)term).getValue();
            TermFactory tf = CSSFactory.getTermFactory();
            if (idval.equalsIgnoreCase("left")) {
                this.setPositionValue(storage, 0, tf.createPercent(Float.valueOf(0.0f)));
            } else if (idval.equalsIgnoreCase("right")) {
                this.setPositionValue(storage, 0, tf.createPercent(Float.valueOf(100.0f)));
            } else if (idval.equalsIgnoreCase("top")) {
                this.setPositionValue(storage, 1, tf.createPercent(Float.valueOf(0.0f)));
            } else if (idval.equalsIgnoreCase("bottom")) {
                this.setPositionValue(storage, 1, tf.createPercent(Float.valueOf(100.0f)));
            } else if (idval.equalsIgnoreCase("center")) {
                this.setPositionValue(storage, -1, tf.createPercent(Float.valueOf(50.0f)));
            }
        } else {
            this.setPositionValue(storage, -1, term);
        }
    }

    private void setPositionValue(Term<?>[] s, int index, Term<?> term) {
        switch (index) {
            case -1: {
                if (s[0] == null) {
                    s[0] = term;
                    break;
                }
                s[1] = term;
                break;
            }
            case 0: {
                if (s[0] != null) {
                    s[1] = s[0];
                }
                s[0] = term;
                break;
            }
            case 1: {
                if (s[1] != null) {
                    s[0] = s[1];
                }
                s[1] = term;
            }
        }
    }

    private boolean isExtentKeyword(Term<?> term) {
        if (term instanceof TermIdent) {
            switch ((String)((TermIdent)term).getValue()) {
                case "closest-corner": 
                case "closest-side": 
                case "farthest-corner": 
                case "farthest-side": {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}

