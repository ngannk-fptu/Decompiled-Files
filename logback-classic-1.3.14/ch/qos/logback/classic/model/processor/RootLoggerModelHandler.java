/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.processor.ModelHandlerBase
 *  ch.qos.logback.core.model.processor.ModelHandlerException
 *  ch.qos.logback.core.model.processor.ModelInterpretationContext
 *  ch.qos.logback.core.util.OptionHelper
 */
package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;

public class RootLoggerModelHandler
extends ModelHandlerBase {
    Logger root;
    boolean inError = false;

    public RootLoggerModelHandler(Context context) {
        super(context);
    }

    public static RootLoggerModelHandler makeInstance(Context context, ModelInterpretationContext ic) {
        return new RootLoggerModelHandler(context);
    }

    protected Class<RootLoggerModel> getSupportedModelClass() {
        return RootLoggerModel.class;
    }

    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        this.inError = false;
        RootLoggerModel rootLoggerModel = (RootLoggerModel)model;
        LoggerContext loggerContext = (LoggerContext)this.context;
        this.root = loggerContext.getLogger("ROOT");
        String levelStr = mic.subst(rootLoggerModel.getLevel());
        if (!OptionHelper.isNullOrEmpty((String)levelStr)) {
            Level level = Level.toLevel(levelStr);
            this.addInfo("Setting level of ROOT logger to " + level);
            this.root.setLevel(level);
        }
        mic.pushObject((Object)this.root);
    }

    public void postHandle(ModelInterpretationContext mic, Model model) {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.root) {
            this.addWarn("The object [" + o + "] on the top the of the stack is not the root logger");
        } else {
            mic.popObject();
        }
    }
}

