/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.BugException;
import freemarker.core.BuiltIn;
import freemarker.core.BuiltInForSequence;
import freemarker.core.BuiltInWithDirectCallOptimization;
import freemarker.core.CollectionAndSequence;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.IntermediateStreamOperationLikeBuiltIn;
import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.core.LazilyGeneratedCollectionModelWithSameSizeCollEx;
import freemarker.core.LazilyGeneratedCollectionModelWithSameSizeSeq;
import freemarker.core.LazilyGeneratedCollectionModelWithUnknownSize;
import freemarker.core.LazyCollectionTemplateModelIterator;
import freemarker.core.NonSequenceOrCollectionException;
import freemarker.core.RightUnboundedRangeModel;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedGetMessage;
import freemarker.core._DelayedGetMessageWithoutStackTop;
import freemarker.core._DelayedJQuote;
import freemarker.core._MessageUtil;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.Constants;
import freemarker.template.utility.StringUtil;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class BuiltInsForSequences {
    private static boolean isBuggySeqButGoodCollection(TemplateModel model) {
        return model instanceof CollectionModel ? !((CollectionModel)model).getSupportsIndexedAccess() : false;
    }

    private static void checkNotRightUnboundedNumericalRange(TemplateModel model) throws TemplateModelException {
        if (model instanceof RightUnboundedRangeModel) {
            throw new _TemplateModelException("The input sequence is a right-unbounded numerical range, thus, it's infinitely long, and can't processed with this built-in.");
        }
    }

    private static boolean modelsEqual(int seqItemIndex, TemplateModel seqItem, TemplateModel searchedItem, Environment env) throws TemplateModelException {
        try {
            return EvalUtil.compare(seqItem, null, 1, null, searchedItem, null, null, false, true, true, true, env);
        }
        catch (TemplateException ex) {
            throw new _TemplateModelException((Throwable)ex, "This error has occurred when comparing sequence item at 0-based index ", seqItemIndex, " to the searched item:\n", new _DelayedGetMessage(ex));
        }
    }

    private BuiltInsForSequences() {
    }

    static class drop_whileBI
    extends FilterLikeBI {
        drop_whileBI() {
        }

        @Override
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!this.isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                ArrayList<TemplateModel> resultList = new ArrayList<TemplateModel>();
                while (lhoIterator.hasNext()) {
                    TemplateModel element = lhoIterator.next();
                    if (this.elementMatches(element, elementTransformer, env)) continue;
                    resultList.add(element);
                    while (lhoIterator.hasNext()) {
                        resultList.add(lhoIterator.next());
                    }
                    break block0;
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator(){
                boolean dropMode = true;
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override
                public TemplateModel next() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                private void ensurePrefetchDone() throws TemplateModelException {
                    if (this.prefetchDone) {
                        return;
                    }
                    if (this.dropMode) {
                        boolean foundElement = false;
                        while (lhoIterator.hasNext()) {
                            TemplateModel element = lhoIterator.next();
                            try {
                                if (drop_whileBI.this.elementMatches(element, elementTransformer, env)) continue;
                                this.prefetchedElement = element;
                                foundElement = true;
                                break;
                            }
                            catch (TemplateException e) {
                                throw new _TemplateModelException((Throwable)e, env, "Failed to transform element");
                            }
                        }
                        this.dropMode = false;
                        if (!foundElement) {
                            this.prefetchedEndOfIterator = true;
                            this.prefetchedElement = null;
                        }
                    } else if (lhoIterator.hasNext()) {
                        TemplateModel element;
                        this.prefetchedElement = element = lhoIterator.next();
                    } else {
                        this.prefetchedEndOfIterator = true;
                        this.prefetchedElement = null;
                    }
                    this.prefetchDone = true;
                }
            }, lhoIsSequence);
        }
    }

    static class mapBI
    extends IntermediateStreamOperationLikeBuiltIn {
        mapBI() {
        }

        @Override
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!this.isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                ArrayList<TemplateModel> resultList = new ArrayList<TemplateModel>();
                while (lhoIterator.hasNext()) {
                    resultList.add(this.fetchAndMapNextElement(lhoIterator, elementTransformer, env));
                }
                return new TemplateModelListSequence(resultList);
            }
            TemplateModelIterator mappedLhoIterator = new TemplateModelIterator(){

                @Override
                public TemplateModel next() throws TemplateModelException {
                    try {
                        return mapBI.this.fetchAndMapNextElement(lhoIterator, elementTransformer, env);
                    }
                    catch (TemplateException e) {
                        throw new _TemplateModelException((Throwable)e, env, "Failed to transform element");
                    }
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    return lhoIterator.hasNext();
                }
            };
            if (lho instanceof TemplateCollectionModelEx) {
                return new LazilyGeneratedCollectionModelWithSameSizeCollEx(mappedLhoIterator, (TemplateCollectionModelEx)lho, lhoIsSequence);
            }
            if (lho instanceof TemplateSequenceModel) {
                return new LazilyGeneratedCollectionModelWithSameSizeSeq(mappedLhoIterator, (TemplateSequenceModel)lho);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(mappedLhoIterator, lhoIsSequence);
        }

        private TemplateModel fetchAndMapNextElement(TemplateModelIterator lhoIterator, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
            TemplateModel transformedElement = elementTransformer.transformElement(lhoIterator.next(), env);
            if (transformedElement == null) {
                throw new _TemplateModelException(this.getElementTransformerExp(), env, "The element mapper function has returned no return value (has returned null).");
            }
            return transformedElement;
        }
    }

    static class take_whileBI
    extends FilterLikeBI {
        take_whileBI() {
        }

        @Override
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!this.isLazilyGeneratedResultEnabled()) {
                TemplateModel element;
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                ArrayList<TemplateModel> resultList = new ArrayList<TemplateModel>();
                while (lhoIterator.hasNext() && this.elementMatches(element = lhoIterator.next(), elementTransformer, env)) {
                    resultList.add(element);
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator(){
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override
                public TemplateModel next() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                private void ensurePrefetchDone() throws TemplateModelException {
                    if (this.prefetchDone) {
                        return;
                    }
                    if (lhoIterator.hasNext()) {
                        boolean elementMatched;
                        TemplateModel element = lhoIterator.next();
                        try {
                            elementMatched = take_whileBI.this.elementMatches(element, elementTransformer, env);
                        }
                        catch (TemplateException e) {
                            throw new _TemplateModelException((Throwable)e, env, "Failed to transform element");
                        }
                        if (elementMatched) {
                            this.prefetchedElement = element;
                        } else {
                            this.prefetchedEndOfIterator = true;
                            this.prefetchedElement = null;
                        }
                    } else {
                        this.prefetchedEndOfIterator = true;
                        this.prefetchedElement = null;
                    }
                    this.prefetchDone = true;
                }
            }, lhoIsSequence);
        }
    }

    static class filterBI
    extends FilterLikeBI {
        filterBI() {
        }

        @Override
        protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
            if (!this.isLazilyGeneratedResultEnabled()) {
                if (!lhoIsSequence) {
                    throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
                }
                ArrayList<TemplateModel> resultList = new ArrayList<TemplateModel>();
                while (lhoIterator.hasNext()) {
                    TemplateModel element = lhoIterator.next();
                    if (!this.elementMatches(element, elementTransformer, env)) continue;
                    resultList.add(element);
                }
                return new TemplateModelListSequence(resultList);
            }
            return new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator(){
                boolean prefetchDone;
                TemplateModel prefetchedElement;
                boolean prefetchedEndOfIterator;

                @Override
                public TemplateModel next() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    if (this.prefetchedEndOfIterator) {
                        throw new IllegalStateException("next() was called when hasNext() is false");
                    }
                    this.prefetchDone = false;
                    return this.prefetchedElement;
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    this.ensurePrefetchDone();
                    return !this.prefetchedEndOfIterator;
                }

                private void ensurePrefetchDone() throws TemplateModelException {
                    if (this.prefetchDone) {
                        return;
                    }
                    boolean conclusionReached = false;
                    do {
                        if (lhoIterator.hasNext()) {
                            boolean elementMatched;
                            TemplateModel element = lhoIterator.next();
                            try {
                                elementMatched = filterBI.this.elementMatches(element, elementTransformer, env);
                            }
                            catch (TemplateException e) {
                                throw new _TemplateModelException((Throwable)e, env, "Failed to transform element");
                            }
                            if (!elementMatched) continue;
                            this.prefetchedElement = element;
                            conclusionReached = true;
                            continue;
                        }
                        this.prefetchedEndOfIterator = true;
                        this.prefetchedElement = null;
                        conclusionReached = true;
                    } while (!conclusionReached);
                    this.prefetchDone = true;
                }
            }, lhoIsSequence);
        }
    }

    private static abstract class FilterLikeBI
    extends IntermediateStreamOperationLikeBuiltIn {
        private FilterLikeBI() {
        }

        protected final boolean elementMatches(TemplateModel element, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
            TemplateModel transformedElement = elementTransformer.transformElement(element, env);
            if (!(transformedElement instanceof TemplateBooleanModel)) {
                if (transformedElement == null) {
                    throw new _TemplateModelException(this.getElementTransformerExp(), env, "The filter expression has returned no value (has returned null), rather than a boolean.");
                }
                throw new _TemplateModelException(this.getElementTransformerExp(), env, "The filter expression had to return a boolean value, but it returned ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(transformedElement)), " instead.");
            }
            return ((TemplateBooleanModel)transformedElement).getAsBoolean();
        }
    }

    static class minBI
    extends MinOrMaxBI {
        public minBI() {
            super(3);
        }
    }

    static class maxBI
    extends MinOrMaxBI {
        public maxBI() {
            super(4);
        }
    }

    private static abstract class MinOrMaxBI
    extends BuiltIn {
        private final int comparatorOperator;

        protected MinOrMaxBI(int comparatorOperator) {
            this.comparatorOperator = comparatorOperator;
        }

        @Override
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateCollectionModel) {
                BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
                return this.calculateResultForCollection((TemplateCollectionModel)model, env);
            }
            if (model instanceof TemplateSequenceModel) {
                return this.calculateResultForSequence((TemplateSequenceModel)model, env);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private TemplateModel calculateResultForCollection(TemplateCollectionModel coll, Environment env) throws TemplateException {
            TemplateModel best = null;
            TemplateModelIterator iter = coll.iterator();
            while (iter.hasNext()) {
                TemplateModel cur = iter.next();
                if (cur == null || best != null && !EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env)) continue;
                best = cur;
            }
            return best;
        }

        private TemplateModel calculateResultForSequence(TemplateSequenceModel seq, Environment env) throws TemplateException {
            TemplateModel best = null;
            for (int i = 0; i < seq.size(); ++i) {
                TemplateModel cur = seq.get(i);
                if (cur == null || best != null && !EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env)) continue;
                best = cur;
            }
            return best;
        }
    }

    static class sequenceBI
    extends BuiltIn {
        private boolean lazilyGeneratedResultEnabled;

        sequenceBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return model;
            }
            if (!(model instanceof TemplateCollectionModel)) {
                throw new NonSequenceOrCollectionException(this.target, model, env);
            }
            TemplateCollectionModel coll = (TemplateCollectionModel)model;
            if (!this.lazilyGeneratedResultEnabled) {
                SimpleSequence seq = coll instanceof TemplateCollectionModelEx ? new SimpleSequence(((TemplateCollectionModelEx)coll).size(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER) : new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                TemplateModelIterator iter = coll.iterator();
                while (iter.hasNext()) {
                    seq.add(iter.next());
                }
                return seq;
            }
            return coll instanceof LazilyGeneratedCollectionModel ? ((LazilyGeneratedCollectionModel)coll).withIsSequenceTrue() : (coll instanceof TemplateCollectionModelEx ? new LazilyGeneratedCollectionModelWithSameSizeCollEx(new LazyCollectionTemplateModelIterator(coll), (TemplateCollectionModelEx)coll, true) : new LazilyGeneratedCollectionModelWithUnknownSize(new LazyCollectionTemplateModelIterator(coll), true));
        }

        @Override
        void enableLazilyGeneratedResult() {
            this.lazilyGeneratedResultEnabled = true;
        }

        @Override
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }
    }

    static class sortBI
    extends BuiltInForSequence {
        static final int KEY_TYPE_NOT_YET_DETECTED = 0;
        static final int KEY_TYPE_STRING = 1;
        static final int KEY_TYPE_NUMBER = 2;
        static final int KEY_TYPE_DATE = 3;
        static final int KEY_TYPE_BOOLEAN = 4;

        sortBI() {
        }

        static TemplateModelException newInconsistentSortKeyTypeException(int keyNamesLn, String firstType, String firstTypePlural, int index, TemplateModel key) {
            String valuesInMsg;
            String valueInMsg;
            if (keyNamesLn == 0) {
                valueInMsg = "value";
                valuesInMsg = "values";
            } else {
                valueInMsg = "key value";
                valuesInMsg = "key values";
            }
            return new _TemplateModelException(sortBI.startErrorMessage(keyNamesLn, index), "All ", valuesInMsg, " in the sequence must be ", firstTypePlural, ", because the first ", valueInMsg, " was that. However, the ", valueInMsg, " of the current item isn't a ", firstType, " but a ", new _DelayedFTLTypeDescription(key), ".");
        }

        static TemplateSequenceModel sort(TemplateSequenceModel seq, String[] keyNames) throws TemplateModelException {
            int i;
            int ln = seq.size();
            if (ln == 0) {
                return seq;
            }
            ArrayList<Object> res = new ArrayList<Object>(ln);
            int keyNamesLn = keyNames == null ? 0 : keyNames.length;
            int keyType = 0;
            Comparator keyComparator = null;
            block18: for (i = 0; i < ln; ++i) {
                TemplateModel item;
                TemplateModel key = item = seq.get(i);
                for (int keyNameI = 0; keyNameI < keyNamesLn; ++keyNameI) {
                    try {
                        key = ((TemplateHashModel)key).get(keyNames[keyNameI]);
                    }
                    catch (ClassCastException e) {
                        if (!(key instanceof TemplateHashModel)) {
                            throw new _TemplateModelException(sortBI.startErrorMessage(keyNamesLn, i), keyNameI == 0 ? "Sequence items must be hashes when using ?sort_by. " : "The " + StringUtil.jQuote(keyNames[keyNameI - 1]), " subvariable is not a hash, so ?sort_by ", "can't proceed with getting the ", new _DelayedJQuote(keyNames[keyNameI]), " subvariable.");
                        }
                        throw e;
                    }
                    if (key != null) continue;
                    throw new _TemplateModelException(sortBI.startErrorMessage(keyNamesLn, i), "The " + StringUtil.jQuote(keyNames[keyNameI]), " subvariable was null or missing.");
                }
                if (keyType == 0) {
                    if (key instanceof TemplateScalarModel) {
                        keyType = 1;
                        keyComparator = new LexicalKVPComparator(Environment.getCurrentEnvironment().getCollator());
                    } else if (key instanceof TemplateNumberModel) {
                        keyType = 2;
                        keyComparator = new NumericalKVPComparator(Environment.getCurrentEnvironment().getArithmeticEngine());
                    } else if (key instanceof TemplateDateModel) {
                        keyType = 3;
                        keyComparator = new DateKVPComparator();
                    } else if (key instanceof TemplateBooleanModel) {
                        keyType = 4;
                        keyComparator = new BooleanKVPComparator();
                    } else {
                        throw new _TemplateModelException(sortBI.startErrorMessage(keyNamesLn, i), "Values used for sorting must be numbers, strings, date/times or booleans.");
                    }
                }
                switch (keyType) {
                    case 1: {
                        try {
                            res.add(new KVP(((TemplateScalarModel)key).getAsString(), item));
                            continue block18;
                        }
                        catch (ClassCastException e) {
                            if (!(key instanceof TemplateScalarModel)) {
                                throw sortBI.newInconsistentSortKeyTypeException(keyNamesLn, "string", "strings", i, key);
                            }
                            throw e;
                        }
                    }
                    case 2: {
                        try {
                            res.add(new KVP(((TemplateNumberModel)key).getAsNumber(), item));
                            continue block18;
                        }
                        catch (ClassCastException e) {
                            if (key instanceof TemplateNumberModel) continue block18;
                            throw sortBI.newInconsistentSortKeyTypeException(keyNamesLn, "number", "numbers", i, key);
                        }
                    }
                    case 3: {
                        try {
                            res.add(new KVP(((TemplateDateModel)key).getAsDate(), item));
                            continue block18;
                        }
                        catch (ClassCastException e) {
                            if (key instanceof TemplateDateModel) continue block18;
                            throw sortBI.newInconsistentSortKeyTypeException(keyNamesLn, "date/time", "date/times", i, key);
                        }
                    }
                    case 4: {
                        try {
                            res.add(new KVP(((TemplateBooleanModel)key).getAsBoolean(), item));
                            continue block18;
                        }
                        catch (ClassCastException e) {
                            if (key instanceof TemplateBooleanModel) continue block18;
                            throw sortBI.newInconsistentSortKeyTypeException(keyNamesLn, "boolean", "booleans", i, key);
                        }
                    }
                    default: {
                        throw new BugException("Unexpected key type");
                    }
                }
            }
            try {
                Collections.sort(res, keyComparator);
            }
            catch (Exception exc) {
                throw new _TemplateModelException((Throwable)exc, sortBI.startErrorMessage(keyNamesLn), "Unexpected error while sorting:" + exc);
            }
            for (i = 0; i < ln; ++i) {
                res.set(i, ((KVP)res.get(i)).value);
            }
            return new TemplateModelListSequence(res);
        }

        static Object[] startErrorMessage(int keyNamesLn) {
            return new Object[]{keyNamesLn == 0 ? "?sort" : "?sort_by(...)", " failed: "};
        }

        static Object[] startErrorMessage(int keyNamesLn, int index) {
            return new Object[]{keyNamesLn == 0 ? "?sort" : "?sort_by(...)", " failed at sequence index ", index, index == 0 ? ": " : " (0-based): "};
        }

        @Override
        TemplateModel calculateResult(TemplateSequenceModel seq) throws TemplateModelException {
            return sortBI.sort(seq, null);
        }

        private static class NumericalKVPComparator
        implements Comparator {
            private ArithmeticEngine ae;

            private NumericalKVPComparator(ArithmeticEngine ae) {
                this.ae = ae;
            }

            public int compare(Object arg0, Object arg1) {
                try {
                    return this.ae.compareNumbers((Number)((KVP)arg0).key, (Number)((KVP)arg1).key);
                }
                catch (TemplateException e) {
                    throw new ClassCastException("Failed to compare numbers: " + e);
                }
            }
        }

        private static class LexicalKVPComparator
        implements Comparator {
            private Collator collator;

            LexicalKVPComparator(Collator collator) {
                this.collator = collator;
            }

            public int compare(Object arg0, Object arg1) {
                return this.collator.compare(((KVP)arg0).key, ((KVP)arg1).key);
            }
        }

        private static class KVP {
            private Object key;
            private Object value;

            private KVP(Object key, Object value) {
                this.key = key;
                this.value = value;
            }
        }

        private static class DateKVPComparator
        implements Comparator,
        Serializable {
            private DateKVPComparator() {
            }

            public int compare(Object arg0, Object arg1) {
                return ((Date)((KVP)arg0).key).compareTo((Date)((KVP)arg1).key);
            }
        }

        private static class BooleanKVPComparator
        implements Comparator,
        Serializable {
            private BooleanKVPComparator() {
            }

            public int compare(Object arg0, Object arg1) {
                boolean b0 = (Boolean)((KVP)arg0).key;
                boolean b1 = (Boolean)((KVP)arg1).key;
                if (b0) {
                    return b1 ? 0 : 1;
                }
                return b1 ? -1 : 0;
            }
        }
    }

    static class sort_byBI
    extends sortBI {
        sort_byBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateSequenceModel seq) {
            return new BIMethod(seq);
        }

        class BIMethod
        implements TemplateMethodModelEx {
            TemplateSequenceModel seq;

            BIMethod(TemplateSequenceModel seq) {
                this.seq = seq;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                String[] subvars;
                if (args.size() < 1) {
                    throw _MessageUtil.newArgCntError("?" + sort_byBI.this.key, args.size(), 1);
                }
                Object obj = args.get(0);
                if (obj instanceof TemplateScalarModel) {
                    subvars = new String[]{((TemplateScalarModel)obj).getAsString()};
                } else if (obj instanceof TemplateSequenceModel) {
                    TemplateSequenceModel seq = (TemplateSequenceModel)obj;
                    int ln = seq.size();
                    subvars = new String[ln];
                    for (int i = 0; i < ln; ++i) {
                        TemplateModel item = seq.get(i);
                        try {
                            subvars[i] = ((TemplateScalarModel)item).getAsString();
                            continue;
                        }
                        catch (ClassCastException e) {
                            if (item instanceof TemplateScalarModel) continue;
                            throw new _TemplateModelException("The argument to ?", sort_byBI.this.key, "(key), when it's a sequence, must be a sequence of strings, but the item at index ", i, " is not a string.");
                        }
                    }
                } else {
                    throw new _TemplateModelException("The argument to ?", sort_byBI.this.key, "(key) must be a string (the name of the subvariable), or a sequence of strings (the \"path\" to the subvariable).");
                }
                return sortBI.sort(this.seq, subvars);
            }
        }
    }

    static class seq_index_ofBI
    extends BuiltInWithDirectCallOptimization {
        private boolean findFirst;

        @Override
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        seq_index_ofBI(boolean findFirst) {
            this.findFirst = findFirst;
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            return new BIMethod(env);
        }

        private class BIMethod
        implements TemplateMethodModelEx {
            protected final TemplateSequenceModel m_seq;
            protected final TemplateCollectionModel m_col;
            protected final Environment m_env;

            private BIMethod(Environment env) throws TemplateException {
                TemplateModel model = seq_index_ofBI.this.target.eval(env);
                this.m_seq = model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model) ? (TemplateSequenceModel)model : null;
                TemplateCollectionModel templateCollectionModel = this.m_col = this.m_seq == null && model instanceof TemplateCollectionModel ? (TemplateCollectionModel)model : null;
                if (this.m_seq == null && this.m_col == null) {
                    throw new NonSequenceOrCollectionException(seq_index_ofBI.this.target, model, env);
                }
                this.m_env = env;
            }

            @Override
            public final Object exec(List args) throws TemplateModelException {
                int foundAtIdx;
                int argCnt = args.size();
                seq_index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
                TemplateModel searched = (TemplateModel)args.get(0);
                if (argCnt > 1) {
                    int startIndex = seq_index_ofBI.this.getNumberMethodArg(args, 1).intValue();
                    foundAtIdx = this.m_seq != null ? this.findInSeq(searched, startIndex) : this.findInCol(searched, startIndex);
                } else {
                    foundAtIdx = this.m_seq != null ? this.findInSeq(searched) : this.findInCol(searched);
                }
                return foundAtIdx == -1 ? Constants.MINUS_ONE : new SimpleNumber(foundAtIdx);
            }

            int findInCol(TemplateModel searched) throws TemplateModelException {
                return this.findInCol(searched, 0, Integer.MAX_VALUE);
            }

            protected int findInCol(TemplateModel searched, int startIndex) throws TemplateModelException {
                if (seq_index_ofBI.this.findFirst) {
                    return this.findInCol(searched, startIndex, Integer.MAX_VALUE);
                }
                return this.findInCol(searched, 0, startIndex);
            }

            protected int findInCol(TemplateModel searched, int allowedRangeStart, int allowedRangeEnd) throws TemplateModelException {
                if (allowedRangeEnd < 0) {
                    return -1;
                }
                TemplateModelIterator it = this.m_col.iterator();
                int foundAtIdx = -1;
                for (int idx = 0; it.hasNext() && idx <= allowedRangeEnd; ++idx) {
                    TemplateModel current = it.next();
                    if (idx < allowedRangeStart || !BuiltInsForSequences.modelsEqual(idx, current, searched, this.m_env)) continue;
                    foundAtIdx = idx;
                    if (seq_index_ofBI.this.findFirst) break;
                }
                return foundAtIdx;
            }

            int findInSeq(TemplateModel searched) throws TemplateModelException {
                int seqSize = this.m_seq.size();
                int actualStartIndex = seq_index_ofBI.this.findFirst ? 0 : seqSize - 1;
                return this.findInSeq(searched, actualStartIndex, seqSize);
            }

            private int findInSeq(TemplateModel searched, int startIndex) throws TemplateModelException {
                int seqSize = this.m_seq.size();
                if (seq_index_ofBI.this.findFirst) {
                    if (startIndex >= seqSize) {
                        return -1;
                    }
                    if (startIndex < 0) {
                        startIndex = 0;
                    }
                } else {
                    if (startIndex >= seqSize) {
                        startIndex = seqSize - 1;
                    }
                    if (startIndex < 0) {
                        return -1;
                    }
                }
                return this.findInSeq(searched, startIndex, seqSize);
            }

            private int findInSeq(TemplateModel target, int scanStartIndex, int seqSize) throws TemplateModelException {
                if (seq_index_ofBI.this.findFirst) {
                    for (int i = scanStartIndex; i < seqSize; ++i) {
                        if (!BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), target, this.m_env)) continue;
                        return i;
                    }
                } else {
                    for (int i = scanStartIndex; i >= 0; --i) {
                        if (!BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), target, this.m_env)) continue;
                        return i;
                    }
                }
                return -1;
            }
        }
    }

    static class seq_containsBI
    extends BuiltInWithDirectCallOptimization {
        seq_containsBI() {
        }

        @Override
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return new BIMethodForSequence((TemplateSequenceModel)model, env);
            }
            if (model instanceof TemplateCollectionModel) {
                return new BIMethodForCollection((TemplateCollectionModel)model, env);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private class BIMethodForSequence
        implements TemplateMethodModelEx {
            private TemplateSequenceModel m_seq;
            private Environment m_env;

            private BIMethodForSequence(TemplateSequenceModel seq, Environment env) {
                this.m_seq = seq;
                this.m_env = env;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                seq_containsBI.this.checkMethodArgCount(args, 1);
                TemplateModel arg = (TemplateModel)args.get(0);
                int size = this.m_seq.size();
                for (int i = 0; i < size; ++i) {
                    if (!BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), arg, this.m_env)) continue;
                    return TemplateBooleanModel.TRUE;
                }
                return TemplateBooleanModel.FALSE;
            }
        }

        private class BIMethodForCollection
        implements TemplateMethodModelEx {
            private TemplateCollectionModel m_coll;
            private Environment m_env;

            private BIMethodForCollection(TemplateCollectionModel coll, Environment env) {
                this.m_coll = coll;
                this.m_env = env;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                seq_containsBI.this.checkMethodArgCount(args, 1);
                TemplateModel arg = (TemplateModel)args.get(0);
                TemplateModelIterator it = this.m_coll.iterator();
                int idx = 0;
                while (it.hasNext()) {
                    if (BuiltInsForSequences.modelsEqual(idx, it.next(), arg, this.m_env)) {
                        return TemplateBooleanModel.TRUE;
                    }
                    ++idx;
                }
                return TemplateBooleanModel.FALSE;
            }
        }
    }

    static class reverseBI
    extends BuiltInForSequence {
        reverseBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateSequenceModel tsm) {
            if (tsm instanceof ReverseSequence) {
                return ((ReverseSequence)tsm).seq;
            }
            return new ReverseSequence(tsm);
        }

        private static class ReverseSequence
        implements TemplateSequenceModel {
            private final TemplateSequenceModel seq;

            ReverseSequence(TemplateSequenceModel seq) {
                this.seq = seq;
            }

            @Override
            public TemplateModel get(int index) throws TemplateModelException {
                return this.seq.get(this.seq.size() - 1 - index);
            }

            @Override
            public int size() throws TemplateModelException {
                return this.seq.size();
            }
        }
    }

    static class lastBI
    extends BuiltInForSequence {
        lastBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
            int size = tsm.size();
            if (size == 0) {
                return null;
            }
            return tsm.get(size - 1);
        }
    }

    static class joinBI
    extends BuiltInWithDirectCallOptimization {
        joinBI() {
        }

        @Override
        protected void setDirectlyCalled() {
            this.target.enableLazilyGeneratedResult();
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateCollectionModel) {
                BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
                return new BIMethodForCollection(env, (TemplateCollectionModel)model);
            }
            if (model instanceof TemplateSequenceModel) {
                return new BIMethodForCollection(env, new CollectionAndSequence((TemplateSequenceModel)model));
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private class BIMethodForCollection
        implements TemplateMethodModelEx {
            private final Environment env;
            private final TemplateCollectionModel coll;

            private BIMethodForCollection(Environment env, TemplateCollectionModel coll) {
                this.env = env;
                this.coll = coll;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                joinBI.this.checkMethodArgCount(args, 1, 3);
                String separator = joinBI.this.getStringMethodArg(args, 0);
                String whenEmpty = joinBI.this.getOptStringMethodArg(args, 1);
                String afterLast = joinBI.this.getOptStringMethodArg(args, 2);
                StringBuilder sb = new StringBuilder();
                TemplateModelIterator it = this.coll.iterator();
                int idx = 0;
                boolean hadItem = false;
                while (it.hasNext()) {
                    TemplateModel item = it.next();
                    if (item != null) {
                        if (hadItem) {
                            sb.append(separator);
                        } else {
                            hadItem = true;
                        }
                        try {
                            sb.append(EvalUtil.coerceModelToStringOrUnsupportedMarkup(item, null, null, this.env));
                        }
                        catch (TemplateException e) {
                            throw new _TemplateModelException((Throwable)e, "\"?", joinBI.this.key, "\" failed at index ", idx, " with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---");
                        }
                    }
                    ++idx;
                }
                if (hadItem) {
                    if (afterLast != null) {
                        sb.append(afterLast);
                    }
                } else if (whenEmpty != null) {
                    sb.append(whenEmpty);
                }
                return new SimpleScalar(sb.toString());
            }
        }
    }

    static class firstBI
    extends BuiltIn {
        firstBI() {
        }

        @Override
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
                return this.calculateResultForSequence((TemplateSequenceModel)model);
            }
            if (model instanceof TemplateCollectionModel) {
                return this.calculateResultForColletion((TemplateCollectionModel)model);
            }
            throw new NonSequenceOrCollectionException(this.target, model, env);
        }

        private TemplateModel calculateResultForSequence(TemplateSequenceModel seq) throws TemplateModelException {
            if (seq.size() == 0) {
                return null;
            }
            return seq.get(0);
        }

        private TemplateModel calculateResultForColletion(TemplateCollectionModel coll) throws TemplateModelException {
            TemplateModelIterator iter = coll.iterator();
            if (!iter.hasNext()) {
                return null;
            }
            return iter.next();
        }
    }

    static class chunkBI
    extends BuiltInForSequence {
        chunkBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
            return new BIMethod(tsm);
        }

        private static class ChunkedSequence
        implements TemplateSequenceModel {
            private final TemplateSequenceModel wrappedTsm;
            private final int chunkSize;
            private final TemplateModel fillerItem;
            private final int numberOfChunks;

            private ChunkedSequence(TemplateSequenceModel wrappedTsm, int chunkSize, TemplateModel fillerItem) throws TemplateModelException {
                this.wrappedTsm = wrappedTsm;
                this.chunkSize = chunkSize;
                this.fillerItem = fillerItem;
                this.numberOfChunks = (wrappedTsm.size() + chunkSize - 1) / chunkSize;
            }

            @Override
            public TemplateModel get(final int chunkIndex) throws TemplateModelException {
                if (chunkIndex >= this.numberOfChunks) {
                    return null;
                }
                return new TemplateSequenceModel(){
                    private final int baseIndex;
                    {
                        this.baseIndex = chunkIndex * ChunkedSequence.this.chunkSize;
                    }

                    @Override
                    public TemplateModel get(int relIndex) throws TemplateModelException {
                        int absIndex = this.baseIndex + relIndex;
                        if (absIndex < ChunkedSequence.this.wrappedTsm.size()) {
                            return ChunkedSequence.this.wrappedTsm.get(absIndex);
                        }
                        return absIndex < ChunkedSequence.this.numberOfChunks * ChunkedSequence.this.chunkSize ? ChunkedSequence.this.fillerItem : null;
                    }

                    @Override
                    public int size() throws TemplateModelException {
                        return ChunkedSequence.this.fillerItem != null || chunkIndex + 1 < ChunkedSequence.this.numberOfChunks ? ChunkedSequence.this.chunkSize : ChunkedSequence.this.wrappedTsm.size() - this.baseIndex;
                    }
                };
            }

            @Override
            public int size() throws TemplateModelException {
                return this.numberOfChunks;
            }
        }

        private class BIMethod
        implements TemplateMethodModelEx {
            private final TemplateSequenceModel tsm;

            private BIMethod(TemplateSequenceModel tsm) {
                this.tsm = tsm;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                chunkBI.this.checkMethodArgCount(args, 1, 2);
                int chunkSize = chunkBI.this.getNumberMethodArg(args, 0).intValue();
                if (chunkSize < 1) {
                    throw new _TemplateModelException("The 1st argument to ?", chunkBI.this.key, " (...) must be at least 1.");
                }
                return new ChunkedSequence(this.tsm, chunkSize, args.size() > 1 ? (TemplateModel)args.get(1) : null);
            }
        }
    }
}

