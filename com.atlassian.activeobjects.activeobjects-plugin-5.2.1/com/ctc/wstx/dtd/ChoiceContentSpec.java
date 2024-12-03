/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ChoiceModel;
import com.ctc.wstx.dtd.ConcatModel;
import com.ctc.wstx.dtd.ContentSpec;
import com.ctc.wstx.dtd.LargePrefixedNameSet;
import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.OptionalModel;
import com.ctc.wstx.dtd.PrefixedNameSet;
import com.ctc.wstx.dtd.SmallPrefixedNameSet;
import com.ctc.wstx.dtd.StarModel;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.dtd.TokenContentSpec;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

public class ChoiceContentSpec
extends ContentSpec {
    final boolean mNsAware;
    final boolean mHasMixed;
    final ContentSpec[] mContentSpecs;

    private ChoiceContentSpec(boolean nsAware, char arity, boolean mixed, ContentSpec[] specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        this.mContentSpecs = specs;
    }

    private ChoiceContentSpec(boolean nsAware, char arity, boolean mixed, Collection specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        this.mContentSpecs = new ContentSpec[specs.size()];
        specs.toArray(this.mContentSpecs);
    }

    public static ChoiceContentSpec constructChoice(boolean nsAware, char arity, Collection specs) {
        return new ChoiceContentSpec(nsAware, arity, false, specs);
    }

    public static ChoiceContentSpec constructMixed(boolean nsAware, Collection specs) {
        return new ChoiceContentSpec(nsAware, '*', true, specs);
    }

    public StructValidator getSimpleValidator() {
        int i;
        ContentSpec[] specs = this.mContentSpecs;
        int len = specs.length;
        if (this.mHasMixed) {
            i = len;
        } else {
            for (i = 0; i < len && specs[i].isLeaf(); ++i) {
            }
        }
        if (i == len) {
            PrefixedNameSet keyset = ChoiceContentSpec.namesetFromSpecs(this.mNsAware, specs);
            return new Validator(this.mArity, keyset);
        }
        return null;
    }

    public ModelNode rewrite() {
        ContentSpec[] specs = this.mContentSpecs;
        int len = specs.length;
        ModelNode[] models = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            models[i] = specs[i].rewrite();
        }
        ChoiceModel model = new ChoiceModel(models);
        if (this.mArity == '*') {
            return new StarModel(model);
        }
        if (this.mArity == '?') {
            return new OptionalModel(model);
        }
        if (this.mArity == '+') {
            return new ConcatModel(model, new StarModel(model.cloneModel()));
        }
        return model;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.mHasMixed) {
            sb.append("(#PCDATA | ");
        } else {
            sb.append('(');
        }
        for (int i = 0; i < this.mContentSpecs.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }

    protected static PrefixedNameSet namesetFromSpecs(boolean nsAware, ContentSpec[] specs) {
        int len = specs.length;
        PrefixedName[] nameArray = new PrefixedName[len];
        for (int i = 0; i < len; ++i) {
            nameArray[i] = ((TokenContentSpec)specs[i]).getName();
        }
        if (len < 5) {
            return new SmallPrefixedNameSet(nsAware, nameArray);
        }
        return new LargePrefixedNameSet(nsAware, nameArray);
    }

    static final class Validator
    extends StructValidator {
        final char mArity;
        final PrefixedNameSet mNames;
        int mCount = 0;

        public Validator(char arity, PrefixedNameSet names) {
            this.mArity = arity;
            this.mNames = names;
        }

        public StructValidator newInstance() {
            return this.mArity == '*' ? this : new Validator(this.mArity, this.mNames);
        }

        public String tryToValidate(PrefixedName elemName) {
            if (!this.mNames.contains(elemName)) {
                if (this.mNames.hasMultiple()) {
                    return "Expected one of (" + this.mNames.toString(" | ") + ")";
                }
                return "Expected <" + this.mNames.toString("") + ">";
            }
            if (++this.mCount > 1 && (this.mArity == '?' || this.mArity == ' ')) {
                if (this.mNames.hasMultiple()) {
                    return "Expected $END (already had one of [" + this.mNames.toString(" | ") + "]";
                }
                return "Expected $END (already had one <" + this.mNames.toString("") + ">]";
            }
            return null;
        }

        public String fullyValid() {
            switch (this.mArity) {
                case '*': 
                case '?': {
                    return null;
                }
                case ' ': 
                case '+': {
                    if (this.mCount > 0) {
                        return null;
                    }
                    return "Expected " + (this.mArity == '+' ? "at least" : "") + " one of elements (" + this.mNames + ")";
                }
            }
            ExceptionUtil.throwGenericInternal();
            return null;
        }
    }
}

