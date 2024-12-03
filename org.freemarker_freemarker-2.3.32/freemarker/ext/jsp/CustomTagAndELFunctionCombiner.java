/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ClassUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

class CustomTagAndELFunctionCombiner {
    CustomTagAndELFunctionCombiner() {
    }

    static TemplateModel combine(TemplateModel customTag, TemplateMethodModelEx elFunction) {
        if (customTag instanceof TemplateDirectiveModel) {
            return (TemplateModel)((Object)(elFunction instanceof SimpleMethodModel ? new TemplateDirectiveModelAndSimpleMethodModel((TemplateDirectiveModel)customTag, (SimpleMethodModel)elFunction) : new TemplateDirectiveModelAndTemplateMethodModelEx((TemplateDirectiveModel)customTag, elFunction)));
        }
        if (customTag instanceof TemplateTransformModel) {
            return (TemplateModel)((Object)(elFunction instanceof SimpleMethodModel ? new TemplateTransformModelAndSimpleMethodModel((TemplateTransformModel)customTag, (SimpleMethodModel)elFunction) : new TemplateTransformModelAndTemplateMethodModelEx((TemplateTransformModel)customTag, elFunction)));
        }
        throw new BugException("Unexpected custom JSP tag class: " + ClassUtil.getShortClassNameOfObject(customTag));
    }

    static boolean canBeCombinedAsCustomTag(TemplateModel tm) {
        return (tm instanceof TemplateDirectiveModel || tm instanceof TemplateTransformModel) && !(tm instanceof CombinedTemplateModel);
    }

    static boolean canBeCombinedAsELFunction(TemplateModel tm) {
        return tm instanceof TemplateMethodModelEx && !(tm instanceof CombinedTemplateModel);
    }

    private static class TemplateTransformModelAndSimpleMethodModel
    extends CombinedTemplateModel
    implements TemplateTransformModel,
    TemplateMethodModelEx,
    TemplateSequenceModel,
    _UnexpectedTypeErrorExplainerTemplateModel {
        private final TemplateTransformModel templateTransformModel;
        private final SimpleMethodModel simpleMethodModel;

        public TemplateTransformModelAndSimpleMethodModel(TemplateTransformModel templateTransformModel, SimpleMethodModel simpleMethodModel) {
            this.templateTransformModel = templateTransformModel;
            this.simpleMethodModel = simpleMethodModel;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return this.simpleMethodModel.exec(arguments);
        }

        @Override
        public Object[] explainTypeError(Class[] expectedClasses) {
            return this.simpleMethodModel.explainTypeError(expectedClasses);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return this.simpleMethodModel.get(index);
        }

        @Override
        public int size() throws TemplateModelException {
            return this.simpleMethodModel.size();
        }

        @Override
        public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
            return this.templateTransformModel.getWriter(out, args);
        }
    }

    private static class TemplateTransformModelAndTemplateMethodModelEx
    extends CombinedTemplateModel
    implements TemplateTransformModel,
    TemplateMethodModelEx {
        private final TemplateTransformModel templateTransformModel;
        private final TemplateMethodModelEx templateMethodModelEx;

        public TemplateTransformModelAndTemplateMethodModelEx(TemplateTransformModel templateTransformModel, TemplateMethodModelEx templateMethodModelEx) {
            this.templateTransformModel = templateTransformModel;
            this.templateMethodModelEx = templateMethodModelEx;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return this.templateMethodModelEx.exec(arguments);
        }

        @Override
        public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
            return this.templateTransformModel.getWriter(out, args);
        }
    }

    private static class TemplateDirectiveModelAndTemplateMethodModelEx
    extends CombinedTemplateModel
    implements TemplateDirectiveModel,
    TemplateMethodModelEx {
        private final TemplateDirectiveModel templateDirectiveModel;
        private final TemplateMethodModelEx templateMethodModelEx;

        public TemplateDirectiveModelAndTemplateMethodModelEx(TemplateDirectiveModel templateDirectiveModel, TemplateMethodModelEx templateMethodModelEx) {
            this.templateDirectiveModel = templateDirectiveModel;
            this.templateMethodModelEx = templateMethodModelEx;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return this.templateMethodModelEx.exec(arguments);
        }

        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            this.templateDirectiveModel.execute(env, params, loopVars, body);
        }
    }

    private static class TemplateDirectiveModelAndSimpleMethodModel
    extends CombinedTemplateModel
    implements TemplateDirectiveModel,
    TemplateMethodModelEx,
    TemplateSequenceModel,
    _UnexpectedTypeErrorExplainerTemplateModel {
        private final TemplateDirectiveModel templateDirectiveModel;
        private final SimpleMethodModel simpleMethodModel;

        public TemplateDirectiveModelAndSimpleMethodModel(TemplateDirectiveModel templateDirectiveModel, SimpleMethodModel simpleMethodModel) {
            this.templateDirectiveModel = templateDirectiveModel;
            this.simpleMethodModel = simpleMethodModel;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return this.simpleMethodModel.exec(arguments);
        }

        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            this.templateDirectiveModel.execute(env, params, loopVars, body);
        }

        @Override
        public Object[] explainTypeError(Class[] expectedClasses) {
            return this.simpleMethodModel.explainTypeError(expectedClasses);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return this.simpleMethodModel.get(index);
        }

        @Override
        public int size() throws TemplateModelException {
            return this.simpleMethodModel.size();
        }
    }

    private static class CombinedTemplateModel {
        private CombinedTemplateModel() {
        }
    }
}

