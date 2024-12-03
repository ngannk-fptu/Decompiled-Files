/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.processor.ModelHandlerBase
 *  ch.qos.logback.core.model.processor.ModelHandlerException
 *  ch.qos.logback.core.model.processor.ModelInterpretationContext
 *  ch.qos.logback.core.spi.LifeCycle
 *  ch.qos.logback.core.util.OptionHelper
 */
package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;

public class ReceiverModelHandler
extends ModelHandlerBase {
    private ReceiverBase receiver;
    private boolean inError;

    public ReceiverModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new ReceiverModelHandler(context);
    }

    protected Class<ReceiverModel> getSupportedModelClass() {
        return ReceiverModel.class;
    }

    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        ReceiverModel receiverModel = (ReceiverModel)model;
        String className = receiverModel.getClassName();
        if (OptionHelper.isNullOrEmpty((String)className)) {
            this.addError("Missing class name for receiver. ");
            this.inError = true;
            return;
        }
        className = mic.getImport(className);
        try {
            this.addInfo("About to instantiate receiver of type [" + className + "]");
            this.receiver = (ReceiverBase)((Object)OptionHelper.instantiateByClassName((String)className, ReceiverBase.class, (Context)this.context));
            this.receiver.setContext(this.context);
            mic.pushObject((Object)this.receiver);
        }
        catch (Exception ex) {
            this.inError = true;
            this.addError("Could not create a receiver of type [" + className + "].", ex);
            throw new ModelHandlerException((Throwable)ex);
        }
    }

    public void postHandle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.receiver) {
            this.addWarn("The object at the of the stack is not the receiver pushed earlier.");
        } else {
            mic.popObject();
            this.addInfo("Registering receiver with context.");
            mic.getContext().register((LifeCycle)this.receiver);
            this.receiver.start();
        }
    }
}

