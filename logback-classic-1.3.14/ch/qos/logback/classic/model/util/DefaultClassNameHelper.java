/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.util.ParentTag_Tag_Class_Tuple
 *  ch.qos.logback.core.model.ImplicitModel
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.util.TagUtil
 */
package ch.qos.logback.classic.model.util;

import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.core.joran.util.ParentTag_Tag_Class_Tuple;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.util.TagUtil;
import java.util.List;

public class DefaultClassNameHelper {
    List<ParentTag_Tag_Class_Tuple> tupleList = LogbackClassicDefaultNestedComponentRules.TUPLES_LIST;

    public void injectDefaultComponentClasses(Model aModel, Model parent) {
        this.applyInjectionRules(aModel, parent);
        for (Model sub : aModel.getSubModels()) {
            this.injectDefaultComponentClasses(sub, aModel);
        }
    }

    private void applyInjectionRules(Model aModel, Model parent) {
        ImplicitModel implicitModel;
        String className;
        if (parent == null) {
            return;
        }
        String parentTag = TagUtil.unifiedTag((Model)parent);
        String modelTag = TagUtil.unifiedTag((Model)aModel);
        if (aModel instanceof ImplicitModel && ((className = (implicitModel = (ImplicitModel)aModel).getClassName()) == null || className.isEmpty())) {
            for (ParentTag_Tag_Class_Tuple ruleTuple : this.tupleList) {
                if (!ruleTuple.parentTag.equals(parentTag) || !ruleTuple.tag.equals(modelTag)) continue;
                implicitModel.setClassName(ruleTuple.className);
                break;
            }
        }
    }
}

