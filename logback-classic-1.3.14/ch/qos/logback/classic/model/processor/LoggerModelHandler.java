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
import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;

public class LoggerModelHandler
extends ModelHandlerBase {
    Logger logger;
    boolean inError = false;

    public LoggerModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext mic) {
        return new LoggerModelHandler(context);
    }

    protected Class<LoggerModel> getSupportedModelClass() {
        return LoggerModel.class;
    }

    public void handle(ModelInterpretationContext mic, Model model) throws ModelHandlerException {
        String additivityStr;
        this.inError = false;
        LoggerModel loggerModel = (LoggerModel)model;
        String finalLoggerName = mic.subst(loggerModel.getName());
        LoggerContext loggerContext = (LoggerContext)this.context;
        this.logger = loggerContext.getLogger(finalLoggerName);
        String levelStr = mic.subst(loggerModel.getLevel());
        if (!OptionHelper.isNullOrEmpty((String)levelStr)) {
            if ("INHERITED".equalsIgnoreCase(levelStr) || "NULL".equalsIgnoreCase(levelStr)) {
                if ("ROOT".equalsIgnoreCase(finalLoggerName)) {
                    this.addError("The level for the ROOT logger cannot be set to NULL or INHERITED. Ignoring.");
                } else {
                    this.addInfo("Setting level of logger [" + finalLoggerName + "] to null, i.e. INHERITED");
                    this.logger.setLevel(null);
                }
            } else {
                Level level = Level.toLevel(levelStr);
                this.addInfo("Setting level of logger [" + finalLoggerName + "] to " + level);
                this.logger.setLevel(level);
            }
        }
        if (!OptionHelper.isNullOrEmpty((String)(additivityStr = mic.subst(loggerModel.getAdditivity())))) {
            boolean additive = OptionHelper.toBoolean((String)additivityStr, (boolean)true);
            this.addInfo("Setting additivity of logger [" + finalLoggerName + "] to " + additive);
            this.logger.setAdditive(additive);
        }
        mic.pushObject((Object)this.logger);
    }

    public void postHandle(ModelInterpretationContext mic, Model model) {
        if (this.inError) {
            return;
        }
        Object o = mic.peekObject();
        if (o != this.logger) {
            LoggerModel loggerModel = (LoggerModel)model;
            this.addWarn("The object [" + o + "] on the top the of the stack is not the expected logger named " + loggerModel.getName());
        } else {
            mic.popObject();
        }
    }
}

