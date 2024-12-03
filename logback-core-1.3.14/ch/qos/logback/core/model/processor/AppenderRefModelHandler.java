/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.AppenderAttachable;
import java.util.Map;

public class AppenderRefModelHandler
extends ModelHandlerBase {
    boolean inError = false;

    public AppenderRefModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new AppenderRefModelHandler(context);
    }

    protected Class<? extends AppenderRefModel> getSupportedModelClass() {
        return AppenderRefModel.class;
    }

    @Override
    public void handle(ModelInterpretationContext interpContext, Model model) throws ModelHandlerException {
        Object o = interpContext.peekObject();
        if (!(o instanceof AppenderAttachable)) {
            this.inError = true;
            String errMsg = "Could not find an AppenderAttachable at the top of execution stack. Near " + model.idString();
            this.addError(errMsg);
            return;
        }
        AppenderRefModel appenderRefModel = (AppenderRefModel)model;
        AppenderAttachable appenderAttachable = (AppenderAttachable)o;
        this.attachRefencedAppenders(interpContext, appenderRefModel, appenderAttachable);
    }

    void attachRefencedAppenders(ModelInterpretationContext mic, AppenderRefModel appenderRefModel, AppenderAttachable<?> appenderAttachable) {
        String appenderName = mic.subst(appenderRefModel.getRef());
        Map appenderBag = (Map)mic.getObjectMap().get("APPENDER_BAG");
        Appender appender = (Appender)appenderBag.get(appenderName);
        if (appender == null) {
            this.addError("Failed to find appender named [" + appenderName + "]");
        } else {
            this.addInfo("Attaching appender named [" + appenderName + "] to " + appenderAttachable);
            appenderAttachable.addAppender(appender);
        }
    }
}

