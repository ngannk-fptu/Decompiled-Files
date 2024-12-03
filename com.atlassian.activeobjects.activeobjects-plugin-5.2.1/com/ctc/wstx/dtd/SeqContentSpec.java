/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ConcatModel;
import com.ctc.wstx.dtd.ContentSpec;
import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.OptionalModel;
import com.ctc.wstx.dtd.StarModel;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.dtd.TokenContentSpec;
import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

public class SeqContentSpec
extends ContentSpec {
    final boolean mNsAware;
    final ContentSpec[] mContentSpecs;

    public SeqContentSpec(boolean nsAware, char arity, ContentSpec[] subSpecs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mContentSpecs = subSpecs;
    }

    public static SeqContentSpec construct(boolean nsAware, char arity, Collection subSpecs) {
        ContentSpec[] specs = new ContentSpec[subSpecs.size()];
        subSpecs.toArray(specs);
        return new SeqContentSpec(nsAware, arity, specs);
    }

    public StructValidator getSimpleValidator() {
        int i;
        ContentSpec[] specs = this.mContentSpecs;
        int len = specs.length;
        for (i = 0; i < len && specs[i].isLeaf(); ++i) {
        }
        if (i == len) {
            PrefixedName[] set = new PrefixedName[len];
            for (i = 0; i < len; ++i) {
                TokenContentSpec ss = (TokenContentSpec)specs[i];
                set[i] = ss.getName();
            }
            return new Validator(this.mArity, set);
        }
        return null;
    }

    public ModelNode rewrite() {
        ModelNode model = this.rewrite(this.mContentSpecs, 0, this.mContentSpecs.length);
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

    private ModelNode rewrite(ContentSpec[] specs, int first, int last) {
        int count = last - first;
        if (count > 3) {
            int mid = last + first + 1 >> 1;
            return new ConcatModel(this.rewrite(specs, first, mid), this.rewrite(specs, mid, last));
        }
        ConcatModel model = new ConcatModel(specs[first].rewrite(), specs[first + 1].rewrite());
        if (count == 3) {
            model = new ConcatModel(model, specs[first + 2].rewrite());
        }
        return model;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (int i = 0; i < this.mContentSpecs.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }

    static final class Validator
    extends StructValidator {
        final char mArity;
        final PrefixedName[] mNames;
        int mRounds = 0;
        int mStep = 0;

        public Validator(char arity, PrefixedName[] names) {
            this.mArity = arity;
            this.mNames = names;
        }

        public StructValidator newInstance() {
            return new Validator(this.mArity, this.mNames);
        }

        public String tryToValidate(PrefixedName elemName) {
            if (this.mStep == 0 && this.mRounds == 1 && (this.mArity == '?' || this.mArity == ' ')) {
                return "was not expecting any more elements in the sequence (" + Validator.concatNames(this.mNames) + ")";
            }
            PrefixedName next = this.mNames[this.mStep];
            if (!elemName.equals(next)) {
                return this.expElem(this.mStep);
            }
            if (++this.mStep == this.mNames.length) {
                ++this.mRounds;
                this.mStep = 0;
            }
            return null;
        }

        public String fullyValid() {
            if (this.mStep != 0) {
                return this.expElem(this.mStep) + "; got end element";
            }
            switch (this.mArity) {
                case '*': 
                case '?': {
                    return null;
                }
                case ' ': 
                case '+': {
                    if (this.mRounds > 0) {
                        return null;
                    }
                    return "Expected sequence (" + Validator.concatNames(this.mNames) + "); got end element";
                }
            }
            throw new IllegalStateException("Internal error");
        }

        private String expElem(int step) {
            return "expected element <" + this.mNames[step] + "> in sequence (" + Validator.concatNames(this.mNames) + ")";
        }

        static final String concatNames(PrefixedName[] names) {
            StringBuffer sb = new StringBuffer();
            int len = names.length;
            for (int i = 0; i < len; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(names[i].toString());
            }
            return sb.toString();
        }
    }
}

