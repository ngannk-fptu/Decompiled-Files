/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;

public class NOPSiftModelHandler
extends ModelHandlerBase {
    public NOPSiftModelHandler(Context context) {
        super(context);
    }

    public static NOPSiftModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new NOPSiftModelHandler(context);
    }

    protected Class<SiftModel> getSupportedModelClass() {
        return SiftModel.class;
    }

    @Override
    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
    }
}

