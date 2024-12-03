/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BreakOrContinueException;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ListElseContainer;
import freemarker.core.LocalContext;
import freemarker.core.NonExtendedHashException;
import freemarker.core.NonSequenceOrCollectionException;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core.TemplateNullModel;
import freemarker.core._CoreStringUtils;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

final class IteratorBlock
extends TemplateElement {
    private final Expression listedExp;
    private final String loopVar1Name;
    private final String loopVar2Name;
    private final boolean hashListing;
    private final boolean forEach;

    IteratorBlock(Expression listedExp, String loopVar1Name, String loopVar2Name, TemplateElements childrenBeforeElse, boolean hashListing, boolean forEach) {
        this.listedExp = listedExp;
        this.loopVar1Name = loopVar1Name;
        this.loopVar2Name = loopVar2Name;
        this.setChildren(childrenBeforeElse);
        this.hashListing = hashListing;
        this.forEach = forEach;
        listedExp.enableLazilyGeneratedResult();
    }

    boolean isHashListing() {
        return this.hashListing;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        this.acceptWithResult(env);
        return null;
    }

    boolean acceptWithResult(Environment env) throws TemplateException, IOException {
        TemplateModel listedValue = this.listedExp.eval(env);
        if (listedValue == null) {
            if (env.isClassicCompatible()) {
                listedValue = Constants.EMPTY_SEQUENCE;
            } else {
                this.listedExp.assertNonNull(null, env);
            }
        }
        return env.visitIteratorBlock(new IterationContext(listedValue, this.loopVar1Name, this.loopVar2Name));
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(this.getNodeTypeSymbol());
        buf.append(' ');
        if (this.forEach) {
            buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar1Name));
            buf.append(" in ");
            buf.append(this.listedExp.getCanonicalForm());
        } else {
            buf.append(this.listedExp.getCanonicalForm());
            if (this.loopVar1Name != null) {
                buf.append(" as ");
                buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar1Name));
                if (this.loopVar2Name != null) {
                    buf.append(", ");
                    buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar2Name));
                }
            }
        }
        if (canonical) {
            buf.append(">");
            buf.append(this.getChildrenCanonicalForm());
            if (!(this.getParentElement() instanceof ListElseContainer)) {
                buf.append("</");
                buf.append(this.getNodeTypeSymbol());
                buf.append('>');
            }
        }
        return buf.toString();
    }

    @Override
    int getParameterCount() {
        return 1 + (this.loopVar1Name != null ? 1 : 0) + (this.loopVar2Name != null ? 1 : 0);
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.listedExp;
            }
            case 1: {
                if (this.loopVar1Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVar1Name;
            }
            case 2: {
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVar2Name;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.LIST_SOURCE;
            }
            case 1: {
                if (this.loopVar1Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            }
            case 2: {
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.forEach ? "#foreach" : "#list";
    }

    @Override
    boolean isNestedBlockRepeater() {
        return this.loopVar1Name != null;
    }

    class IterationContext
    implements LocalContext {
        private static final String LOOP_STATE_HAS_NEXT = "_has_next";
        private static final String LOOP_STATE_INDEX = "_index";
        private Object openedIterator;
        private boolean hasNext;
        private TemplateModel loopVar1Value;
        private TemplateModel loopVar2Value;
        private int index;
        private boolean alreadyEntered;
        private Collection<String> localVarNames = null;
        private String loopVar1Name;
        private String visibleLoopVar1Name;
        private String loopVar2Name;
        private final TemplateModel listedValue;

        public IterationContext(TemplateModel listedValue, String loopVar1Name, String loopVar2Name) {
            this.listedValue = listedValue;
            this.loopVar1Name = loopVar1Name;
            this.loopVar2Name = loopVar2Name;
        }

        boolean accept(Environment env) throws TemplateException, IOException {
            return this.executeNestedContent(env, IteratorBlock.this.getChildBuffer());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void loopForItemsElement(Environment env, TemplateElement[] childBuffer, String loopVarName, String loopVar2Name) throws TemplateException, IOException {
            try {
                if (this.alreadyEntered) {
                    throw new _MiscTemplateException(env, "The #items directive was already entered earlier for this listing.");
                }
                this.alreadyEntered = true;
                this.loopVar1Name = loopVarName;
                this.loopVar2Name = loopVar2Name;
                this.executeNestedContent(env, childBuffer);
            }
            finally {
                this.loopVar1Name = null;
                this.loopVar2Name = null;
            }
        }

        private boolean executeNestedContent(Environment env, TemplateElement[] childBuffer) throws TemplateException, IOException {
            return !IteratorBlock.this.hashListing ? this.executedNestedContentForCollOrSeqListing(env, childBuffer) : this.executedNestedContentForHashListing(env, childBuffer);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean executedNestedContentForCollOrSeqListing(Environment env, TemplateElement[] childBuffer) throws IOException, TemplateException {
            boolean listNotEmpty;
            if (this.listedValue instanceof TemplateCollectionModel) {
                TemplateCollectionModel collModel = (TemplateCollectionModel)this.listedValue;
                TemplateModelIterator iterModel = this.openedIterator == null ? collModel.iterator() : (TemplateModelIterator)this.openedIterator;
                listNotEmpty = iterModel.hasNext();
                if (listNotEmpty) {
                    if (this.loopVar1Name != null) {
                        do {
                            this.loopVar1Value = iterModel.next();
                            this.hasNext = iterModel.hasNext();
                            try {
                                this.visibleLoopVar1Name = this.loopVar1Name;
                                env.visit(childBuffer);
                            }
                            catch (BreakOrContinueException br) {
                                if (br == BreakOrContinueException.BREAK_INSTANCE) {
                                    break;
                                }
                            }
                            finally {
                                this.visibleLoopVar1Name = null;
                            }
                            ++this.index;
                        } while (this.hasNext);
                        this.openedIterator = null;
                    } else {
                        this.openedIterator = iterModel;
                        env.visit(childBuffer);
                    }
                }
            } else if (this.listedValue instanceof TemplateSequenceModel) {
                TemplateSequenceModel seqModel = (TemplateSequenceModel)this.listedValue;
                int size = seqModel.size();
                boolean bl = listNotEmpty = size != 0;
                if (listNotEmpty) {
                    if (this.loopVar1Name != null) {
                        this.index = 0;
                        while (this.index < size) {
                            block33: {
                                this.loopVar1Value = seqModel.get(this.index);
                                this.hasNext = size > this.index + 1;
                                try {
                                    this.visibleLoopVar1Name = this.loopVar1Name;
                                    env.visit(childBuffer);
                                }
                                catch (BreakOrContinueException br) {
                                    if (br != BreakOrContinueException.BREAK_INSTANCE) break block33;
                                    break;
                                }
                                finally {
                                    this.visibleLoopVar1Name = null;
                                }
                            }
                            ++this.index;
                        }
                    } else {
                        env.visit(childBuffer);
                    }
                }
            } else if (env.isClassicCompatible()) {
                listNotEmpty = true;
                if (this.loopVar1Name != null) {
                    this.loopVar1Value = this.listedValue;
                    this.hasNext = false;
                }
                try {
                    this.visibleLoopVar1Name = this.loopVar1Name;
                    env.visit(childBuffer);
                }
                catch (BreakOrContinueException breakOrContinueException) {
                }
                finally {
                    this.visibleLoopVar1Name = null;
                }
            } else {
                if (this.listedValue instanceof TemplateHashModelEx && !NonSequenceOrCollectionException.isWrappedIterable(this.listedValue)) {
                    throw new NonSequenceOrCollectionException(env, new _ErrorDescriptionBuilder("The value you try to list is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(this.listedValue)), ", thus you must specify two loop variables after the \"as\"; one for the key, and another for the value, like ", "<#... as k, v>", ")."));
                }
                throw new NonSequenceOrCollectionException(IteratorBlock.this.listedExp, this.listedValue, env);
            }
            return listNotEmpty;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean executedNestedContentForHashListing(Environment env, TemplateElement[] childBuffer) throws IOException, TemplateException {
            boolean hashNotEmpty;
            if (this.listedValue instanceof TemplateHashModelEx) {
                TemplateHashModelEx listedHash = (TemplateHashModelEx)this.listedValue;
                if (listedHash instanceof TemplateHashModelEx2) {
                    TemplateHashModelEx2.KeyValuePairIterator kvpIter = this.openedIterator == null ? ((TemplateHashModelEx2)listedHash).keyValuePairIterator() : (TemplateHashModelEx2.KeyValuePairIterator)this.openedIterator;
                    hashNotEmpty = kvpIter.hasNext();
                    if (hashNotEmpty) {
                        if (this.loopVar1Name != null) {
                            do {
                                TemplateHashModelEx2.KeyValuePair kvp = kvpIter.next();
                                this.loopVar1Value = kvp.getKey();
                                this.loopVar2Value = kvp.getValue();
                                this.hasNext = kvpIter.hasNext();
                                try {
                                    this.visibleLoopVar1Name = this.loopVar1Name;
                                    env.visit(childBuffer);
                                }
                                catch (BreakOrContinueException br) {
                                    if (br == BreakOrContinueException.BREAK_INSTANCE) {
                                        break;
                                    }
                                }
                                finally {
                                    this.visibleLoopVar1Name = null;
                                }
                                ++this.index;
                            } while (this.hasNext);
                            this.openedIterator = null;
                        } else {
                            this.openedIterator = kvpIter;
                            env.visit(childBuffer);
                        }
                    }
                } else {
                    TemplateModelIterator keysIter = listedHash.keys().iterator();
                    hashNotEmpty = keysIter.hasNext();
                    if (hashNotEmpty) {
                        if (this.loopVar1Name != null) {
                            do {
                                block26: {
                                    this.loopVar1Value = keysIter.next();
                                    if (!(this.loopVar1Value instanceof TemplateScalarModel)) {
                                        throw _MessageUtil.newKeyValuePairListingNonStringKeyExceptionMessage(this.loopVar1Value, (TemplateHashModelEx)this.listedValue);
                                    }
                                    this.loopVar2Value = listedHash.get(((TemplateScalarModel)this.loopVar1Value).getAsString());
                                    this.hasNext = keysIter.hasNext();
                                    try {
                                        this.visibleLoopVar1Name = this.loopVar1Name;
                                        env.visit(childBuffer);
                                    }
                                    catch (BreakOrContinueException br) {
                                        if (br != BreakOrContinueException.BREAK_INSTANCE) break block26;
                                        break;
                                    }
                                    finally {
                                        this.visibleLoopVar1Name = null;
                                    }
                                }
                                ++this.index;
                            } while (this.hasNext);
                        } else {
                            env.visit(childBuffer);
                        }
                    }
                }
            } else {
                if (this.listedValue instanceof TemplateCollectionModel || this.listedValue instanceof TemplateSequenceModel) {
                    throw new NonSequenceOrCollectionException(env, new _ErrorDescriptionBuilder("The value you try to list is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(this.listedValue)), ", thus you must specify only one loop variable after the \"as\" (there's no separate key and value)."));
                }
                throw new NonExtendedHashException(IteratorBlock.this.listedExp, this.listedValue, env);
            }
            return hashNotEmpty;
        }

        boolean hasVisibleLoopVar(String visibleLoopVarName) {
            String visibleLoopVar1Name = this.visibleLoopVar1Name;
            if (visibleLoopVar1Name == null) {
                return false;
            }
            return visibleLoopVarName.equals(visibleLoopVar1Name) || visibleLoopVarName.equals(this.loopVar2Name);
        }

        @Override
        public TemplateModel getLocalVariable(String name) {
            String visibleLoopVar1Name = this.visibleLoopVar1Name;
            if (visibleLoopVar1Name == null) {
                return null;
            }
            if (name.startsWith(visibleLoopVar1Name)) {
                switch (name.length() - visibleLoopVar1Name.length()) {
                    case 0: {
                        return this.loopVar1Value != null ? this.loopVar1Value : (IteratorBlock.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE);
                    }
                    case 6: {
                        if (!name.endsWith(LOOP_STATE_INDEX)) break;
                        return new SimpleNumber(this.index);
                    }
                    case 9: {
                        if (!name.endsWith(LOOP_STATE_HAS_NEXT)) break;
                        return this.hasNext ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
                    }
                }
            }
            if (name.equals(this.loopVar2Name)) {
                return this.loopVar2Value != null ? this.loopVar2Value : (IteratorBlock.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE);
            }
            return null;
        }

        @Override
        public Collection<String> getLocalVariableNames() {
            String visibleLoopVar1Name = this.visibleLoopVar1Name;
            if (visibleLoopVar1Name != null) {
                if (this.localVarNames == null) {
                    this.localVarNames = new ArrayList<String>(3);
                    this.localVarNames.add(visibleLoopVar1Name);
                    this.localVarNames.add(visibleLoopVar1Name + LOOP_STATE_INDEX);
                    this.localVarNames.add(visibleLoopVar1Name + LOOP_STATE_HAS_NEXT);
                }
                return this.localVarNames;
            }
            return Collections.emptyList();
        }

        boolean hasNext() {
            return this.hasNext;
        }

        int getIndex() {
            return this.index;
        }
    }
}

