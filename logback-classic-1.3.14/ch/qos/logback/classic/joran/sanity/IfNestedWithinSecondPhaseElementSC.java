/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.sanity.Pair
 *  ch.qos.logback.core.joran.sanity.SanityChecker
 *  ch.qos.logback.core.model.AppenderModel
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.conditional.IfModel
 *  ch.qos.logback.core.spi.ContextAwareBase
 */
package ch.qos.logback.classic.joran.sanity;

import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.joran.sanity.SanityChecker;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.ArrayList;
import java.util.List;

public class IfNestedWithinSecondPhaseElementSC
extends ContextAwareBase
implements SanityChecker {
    public static final String NESTED_IF_WARNING_URL = "http://logback.qos.ch/codes.html#nested_if_element";

    public void check(Model model) {
        if (model == null) {
            return;
        }
        ArrayList secondPhaseModels = new ArrayList();
        this.deepFindAllModelsOfType(AppenderModel.class, secondPhaseModels, model);
        this.deepFindAllModelsOfType(LoggerModel.class, secondPhaseModels, model);
        this.deepFindAllModelsOfType(RootLoggerModel.class, secondPhaseModels, model);
        List nestedPairs = this.deepFindNestedSubModelsOfType(IfModel.class, secondPhaseModels);
        if (nestedPairs.isEmpty()) {
            return;
        }
        this.addWarn("<if> elements cannot be nested within an <appender>, <logger> or <root> element");
        this.addWarn("See also http://logback.qos.ch/codes.html#nested_if_element");
        for (Pair pair : nestedPairs) {
            Model p = (Model)pair.first;
            int pLine = p.getLineNumber();
            Model s = (Model)pair.second;
            int sLine = s.getLineNumber();
            this.addWarn("Element <" + p.getTag() + "> at line " + pLine + " contains a nested <" + s.getTag() + "> element at line " + sLine);
        }
    }

    public String toString() {
        return "IfNestedWithinSecondPhaseElementSC";
    }
}

