/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Arrays;

public class UnexpectedTypeException
extends TemplateException {
    public UnexpectedTypeException(Environment env, String description) {
        super(description, env);
    }

    UnexpectedTypeException(Environment env, _ErrorDescriptionBuilder description) {
        super(null, env, null, description);
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, UnexpectedTypeException.newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env));
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, String tip, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, UnexpectedTypeException.newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env).tip(tip));
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, UnexpectedTypeException.newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env).tips(tips));
    }

    UnexpectedTypeException(String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
        super(null, env, null, UnexpectedTypeException.newDescriptionBuilder(null, blamedAssignmentTargetVarName, model, expectedTypesDesc, expectedTypes, env).tips(tips));
    }

    private static _ErrorDescriptionBuilder newDescriptionBuilder(Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
        Object[] tip;
        if (model == null) {
            throw InvalidReferenceException.getInstance(blamed, env);
        }
        _ErrorDescriptionBuilder errorDescBuilder = new _ErrorDescriptionBuilder(UnexpectedTypeException.unexpectedTypeErrorDescription(expectedTypesDesc, blamed, blamedAssignmentTargetVarName, model)).blame(blamed).showBlamer(true);
        if (model instanceof _UnexpectedTypeErrorExplainerTemplateModel && (tip = ((_UnexpectedTypeErrorExplainerTemplateModel)model).explainTypeError(expectedTypes)) != null) {
            errorDescBuilder.tip(tip);
        }
        if (model instanceof TemplateCollectionModel && (Arrays.asList(expectedTypes).contains(TemplateSequenceModel.class) || Arrays.asList(expectedTypes).contains(TemplateCollectionModelEx.class))) {
            errorDescBuilder.tip("As the problematic value contains a collection of items, you could convert it to a sequence like someValue?sequence. Be sure though that you won't have a large number of items, as all will be held in memory the same time.");
        }
        return errorDescBuilder;
    }

    private static Object[] unexpectedTypeErrorDescription(String expectedTypesDesc, Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model) {
        Object object;
        Object[] objectArray = new Object[7];
        objectArray[0] = "Expected ";
        objectArray[1] = new _DelayedAOrAn(expectedTypesDesc);
        objectArray[2] = ", but ";
        if (blamedAssignmentTargetVarName == null) {
            object = blamed != null ? "this" : "the expression";
        } else {
            Object[] objectArray2 = new Object[2];
            objectArray2[0] = "assignment target variable ";
            object = objectArray2;
            objectArray2[1] = new _DelayedJQuote(blamedAssignmentTargetVarName);
        }
        objectArray[3] = object;
        objectArray[4] = " has evaluated to ";
        objectArray[5] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(model));
        objectArray[6] = blamed != null ? ":" : ".";
        return objectArray;
    }
}

