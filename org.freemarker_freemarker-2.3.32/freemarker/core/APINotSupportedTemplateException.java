/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedShortClassName;
import freemarker.core._DelayedToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template._VersionInts;

class APINotSupportedTemplateException
extends TemplateException {
    APINotSupportedTemplateException(Environment env, Expression blamedExpr, TemplateModel model) {
        super(null, env, blamedExpr, APINotSupportedTemplateException.buildDescription(env, blamedExpr, model));
    }

    protected static _ErrorDescriptionBuilder buildDescription(Environment env, Expression blamedExpr, TemplateModel tm) {
        _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("The value doesn't support ?api. See requirements in the FreeMarker Manual. (FTL type: ", new _DelayedFTLTypeDescription(tm), ", TemplateModel class: ", new _DelayedShortClassName(tm.getClass()), ", ObjectWapper: ", new _DelayedToString(env.getObjectWrapper()), ")").blame(blamedExpr);
        if (blamedExpr.isLiteral()) {
            desc.tip("Only adapted Java objects can possibly have API, not values created inside templates.");
        } else {
            ObjectWrapper ow = env.getObjectWrapper();
            if (ow instanceof DefaultObjectWrapper && (tm instanceof SimpleHash || tm instanceof SimpleSequence)) {
                DefaultObjectWrapper dow = (DefaultObjectWrapper)ow;
                if (!dow.getUseAdaptersForContainers()) {
                    desc.tip("In the FreeMarker configuration, \"", "object_wrapper", "\" is a DefaultObjectWrapper with its \"useAdaptersForContainers\" property set to false. Setting it to true might solves this problem.");
                    if (dow.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_22) {
                        desc.tip("Setting DefaultObjectWrapper's \"incompatibleImprovements\" to 2.3.22 or higher will change the default value of \"useAdaptersForContainers\" to true.");
                    }
                } else if (tm instanceof SimpleSequence && dow.getForceLegacyNonListCollections()) {
                    desc.tip("In the FreeMarker configuration, \"", "object_wrapper", "\" is a DefaultObjectWrapper with its \"forceLegacyNonListCollections\" property set to true. If you are trying to access the API of a non-List Collection, setting the \"forceLegacyNonListCollections\" property to false might solves this problem.");
                }
            }
        }
        return desc;
    }
}

