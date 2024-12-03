/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInForLoopVariable;
import freemarker.core.Environment;
import freemarker.core.IteratorBlock;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

class BuiltInsForLoopVariables {
    BuiltInsForLoopVariables() {
    }

    static class item_cycleBI
    extends BuiltInForLoopVariable {
        item_cycleBI() {
        }

        @Override
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new BIMethod(iterCtx);
        }

        private class BIMethod
        implements TemplateMethodModelEx {
            private final IteratorBlock.IterationContext iterCtx;

            private BIMethod(IteratorBlock.IterationContext iterCtx) {
                this.iterCtx = iterCtx;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                item_cycleBI.this.checkMethodArgCount(args, 1, Integer.MAX_VALUE);
                return args.get(this.iterCtx.getIndex() % args.size());
            }
        }
    }

    static class item_parity_capBI
    extends BuiltInForLoopVariable {
        private static final SimpleScalar ODD = new SimpleScalar("Odd");
        private static final SimpleScalar EVEN = new SimpleScalar("Even");

        item_parity_capBI() {
        }

        @Override
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
        }
    }

    static class item_parityBI
    extends BuiltInForLoopVariable {
        private static final SimpleScalar ODD = new SimpleScalar("odd");
        private static final SimpleScalar EVEN = new SimpleScalar("even");

        item_parityBI() {
        }

        @Override
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
        }
    }

    static class is_even_itemBI
    extends BooleanBuiltInForLoopVariable {
        is_even_itemBI() {
        }

        @Override
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() % 2 != 0;
        }
    }

    static class is_odd_itemBI
    extends BooleanBuiltInForLoopVariable {
        is_odd_itemBI() {
        }

        @Override
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() % 2 == 0;
        }
    }

    static class is_firstBI
    extends BooleanBuiltInForLoopVariable {
        is_firstBI() {
        }

        @Override
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.getIndex() == 0;
        }
    }

    static class is_lastBI
    extends BooleanBuiltInForLoopVariable {
        is_lastBI() {
        }

        @Override
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return !iterCtx.hasNext();
        }
    }

    static class has_nextBI
    extends BooleanBuiltInForLoopVariable {
        has_nextBI() {
        }

        @Override
        protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
            return iterCtx.hasNext();
        }
    }

    static abstract class BooleanBuiltInForLoopVariable
    extends BuiltInForLoopVariable {
        BooleanBuiltInForLoopVariable() {
        }

        @Override
        final TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return this.calculateBooleanResult(iterCtx, env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }

        protected abstract boolean calculateBooleanResult(IteratorBlock.IterationContext var1, Environment var2);
    }

    static class counterBI
    extends BuiltInForLoopVariable {
        counterBI() {
        }

        @Override
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new SimpleNumber(iterCtx.getIndex() + 1);
        }
    }

    static class indexBI
    extends BuiltInForLoopVariable {
        indexBI() {
        }

        @Override
        TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
            return new SimpleNumber(iterCtx.getIndex());
        }
    }
}

