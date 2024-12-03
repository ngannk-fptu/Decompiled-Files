/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor.conditional;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.conditional.Condition;
import ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;

public class IfModelHandler
extends ModelHandlerBase {
    public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
    public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
    IfModel ifModel = null;

    public IfModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new IfModelHandler(context);
    }

    protected Class<IfModel> getSupportedModelClass() {
        return IfModel.class;
    }

    @Override
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.ifModel = (IfModel)model;
        if (!EnvUtil.isJaninoAvailable()) {
            this.addError(MISSING_JANINO_MSG);
            this.addError(MISSING_JANINO_SEE);
            return;
        }
        mic.pushModel(this.ifModel);
        Condition condition = null;
        int lineNum = model.getLineNumber();
        String conditionStr = this.ifModel.getCondition();
        if (!OptionHelper.isNullOrEmpty(conditionStr)) {
            try {
                conditionStr = OptionHelper.substVars(conditionStr, mic, this.context);
            }
            catch (ScanException e) {
                this.addError("Failed to parse input [" + conditionStr + "] on line " + lineNum, e);
                this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
                return;
            }
            try {
                PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(mic);
                pesb.setContext(this.context);
                condition = pesb.build(conditionStr);
            }
            catch (Exception | NoClassDefFoundError e) {
                this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
                this.addError("Failed to parse condition [" + conditionStr + "] on line " + lineNum, e);
                return;
            }
            if (condition != null) {
                boolean boolResult = condition.evaluate();
                this.addInfo("Condition [" + conditionStr + "] evaluated to " + boolResult + " on line " + lineNum);
                this.ifModel.setBranchState(boolResult);
            } else {
                this.addError("The condition variable is null. This should not occur.");
                this.ifModel.setBranchState(IfModel.BranchState.IN_ERROR);
                return;
            }
        }
    }

    @Override
    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (mic.isModelStackEmpty()) {
            this.addError("Unexpected unexpected empty model stack.");
            return;
        }
        Model o = mic.peekModel();
        if (o != this.ifModel) {
            this.addWarn("The object [" + o + "] on the top the of the stack is not the expected [" + this.ifModel);
        } else {
            mic.popModel();
        }
    }

    static enum Branch {
        IF_BRANCH,
        ELSE_BRANCH;

    }
}

