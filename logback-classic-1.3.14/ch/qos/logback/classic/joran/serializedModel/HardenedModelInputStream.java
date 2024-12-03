/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.model.AppenderModel
 *  ch.qos.logback.core.model.AppenderRefModel
 *  ch.qos.logback.core.model.ComponentModel
 *  ch.qos.logback.core.model.DefineModel
 *  ch.qos.logback.core.model.EventEvaluatorModel
 *  ch.qos.logback.core.model.ImplicitModel
 *  ch.qos.logback.core.model.ImportModel
 *  ch.qos.logback.core.model.IncludeModel
 *  ch.qos.logback.core.model.InsertFromJNDIModel
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.NamedComponentModel
 *  ch.qos.logback.core.model.NamedModel
 *  ch.qos.logback.core.model.ParamModel
 *  ch.qos.logback.core.model.PropertyModel
 *  ch.qos.logback.core.model.SequenceNumberGeneratorModel
 *  ch.qos.logback.core.model.SerializeModelModel
 *  ch.qos.logback.core.model.ShutdownHookModel
 *  ch.qos.logback.core.model.SiftModel
 *  ch.qos.logback.core.model.StatusListenerModel
 *  ch.qos.logback.core.model.TimestampModel
 *  ch.qos.logback.core.model.conditional.ElseModel
 *  ch.qos.logback.core.model.conditional.IfModel
 *  ch.qos.logback.core.model.conditional.ThenModel
 *  ch.qos.logback.core.net.HardenedObjectInputStream
 */
package ch.qos.logback.classic.joran.serializedModel;

import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.classic.model.LoggerContextListenerModel;
import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.ComponentModel;
import ch.qos.logback.core.model.DefineModel;
import ch.qos.logback.core.model.EventEvaluatorModel;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.ImportModel;
import ch.qos.logback.core.model.IncludeModel;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.NamedComponentModel;
import ch.qos.logback.core.model.NamedModel;
import ch.qos.logback.core.model.ParamModel;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.model.SequenceNumberGeneratorModel;
import ch.qos.logback.core.model.SerializeModelModel;
import ch.qos.logback.core.model.ShutdownHookModel;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.StatusListenerModel;
import ch.qos.logback.core.model.TimestampModel;
import ch.qos.logback.core.model.conditional.ElseModel;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.conditional.ThenModel;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HardenedModelInputStream
extends HardenedObjectInputStream {
    public static List<String> getWhilelist() {
        ArrayList<String> whitelist = new ArrayList<String>();
        whitelist.add(Model.class.getName());
        whitelist.add(Model.class.getName());
        whitelist.add(IncludeModel.class.getName());
        whitelist.add(InsertFromJNDIModel.class.getName());
        whitelist.add(RootLoggerModel.class.getName());
        whitelist.add(ImportModel.class.getName());
        whitelist.add(AppenderRefModel.class.getName());
        whitelist.add(ComponentModel.class.getName());
        whitelist.add(StatusListenerModel.class.getName());
        whitelist.add(ShutdownHookModel.class.getName());
        whitelist.add(NamedComponentModel.class.getName());
        whitelist.add(AppenderModel.class.getName());
        whitelist.add(EventEvaluatorModel.class.getName());
        whitelist.add(DefineModel.class.getName());
        whitelist.add(SequenceNumberGeneratorModel.class.getName());
        whitelist.add(ImplicitModel.class.getName());
        whitelist.add(ReceiverModel.class.getName());
        whitelist.add(LoggerContextListenerModel.class.getName());
        whitelist.add(ThenModel.class.getName());
        whitelist.add(IfModel.class.getName());
        whitelist.add(NamedModel.class.getName());
        whitelist.add(ContextNameModel.class.getName());
        whitelist.add(ParamModel.class.getName());
        whitelist.add(TimestampModel.class.getName());
        whitelist.add(PropertyModel.class.getName());
        whitelist.add(ElseModel.class.getName());
        whitelist.add(ConfigurationModel.class.getName());
        whitelist.add(SiftModel.class.getName());
        whitelist.add(LoggerModel.class.getName());
        whitelist.add(SerializeModelModel.class.getName());
        return whitelist;
    }

    public HardenedModelInputStream(InputStream is) throws IOException {
        super(is, HardenedModelInputStream.getWhilelist());
    }
}

