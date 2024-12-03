/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DependencyDefinition;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;

@PhaseIndicator(phase=ProcessingPhase.DEPENDENCY_ANALYSIS)
public class AppenderRefDependencyAnalyser
extends ModelHandlerBase {
    public AppenderRefDependencyAnalyser(Context context) {
        super(context);
    }

    protected Class<AppenderRefModel> getSupportedModelClass() {
        return AppenderRefModel.class;
    }

    @Override
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        Model depender;
        AppenderRefModel appenderRefModel = (AppenderRefModel)model;
        String ref = mic.subst(appenderRefModel.getRef());
        if (mic.isModelStackEmpty()) {
            depender = appenderRefModel;
        } else {
            Model parentModel = mic.peekModel();
            depender = parentModel;
        }
        DependencyDefinition dd = new DependencyDefinition(depender, ref);
        mic.addDependencyDefinition(dd);
    }
}

