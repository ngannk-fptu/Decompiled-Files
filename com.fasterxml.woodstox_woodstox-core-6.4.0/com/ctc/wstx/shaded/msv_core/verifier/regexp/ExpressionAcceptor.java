/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionPrinter;
import com.ctc.wstx.shaded.msv_core.grammar.util.IDContextProviderWrapper;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AnyElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeRecoveryToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.CombinedChildContentExpCreator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.OptimizationTag;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringCareLevelCalculator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringRecoveryToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.Token;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public abstract class ExpressionAcceptor
implements Acceptor {
    private Expression expression;
    protected final REDocumentDeclaration docDecl;
    protected final boolean ignoreUndeclaredAttributes;

    public Expression getExpression() {
        return this.expression;
    }

    public ExpressionAcceptor(REDocumentDeclaration docDecl, Expression exp, boolean ignoreUndeclaredAttributes) {
        this.docDecl = docDecl;
        this.expression = exp;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }

    public Acceptor createChildAcceptor(StartTagInfo tag, StringRef errRef) {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        CombinedChildContentExpCreator.ExpressionPair e = cccc.get(this.expression, tag);
        if (e.content != Expression.nullSet) {
            if (Debug.debug) {
                System.out.println("accept start tag <" + tag.qName + ">. combined content pattern is");
                System.out.println(ExpressionPrinter.printContentModel(e.content));
                if (e.continuation != null) {
                    System.out.println("continuation is:\n" + ExpressionPrinter.printContentModel(e.continuation));
                } else {
                    System.out.println("no continuation");
                }
            }
            return this.createAcceptor(e.content, e.continuation, cccc.getMatchedElements(), cccc.numMatchedElements());
        }
        if (errRef == null) {
            return null;
        }
        errRef.str = this.diagnoseBadTagName(tag);
        if (errRef.str == null) {
            errRef.str = this.docDecl.localizeMessage("Diagnosis.BadTagName.Generic", tag.qName);
        }
        return this.createRecoveryAcceptors();
    }

    protected abstract Acceptor createAcceptor(Expression var1, Expression var2, ElementExp[] var3, int var4);

    public final boolean onAttribute(String namespaceURI, String localName, String qName, String value, IDContextProvider context, StringRef refErr, DatatypeRef refType) {
        return this.onAttribute2(namespaceURI, localName, qName, value, IDContextProviderWrapper.create(context), refErr, refType);
    }

    public final boolean onAttribute2(String namespaceURI, String localName, String qName, String value, IDContextProvider2 context, StringRef refErr, DatatypeRef refType) {
        this.docDecl.attToken.reinit(namespaceURI, localName, qName, new StringToken(this.docDecl, value, context, refType));
        return this.onAttribute(this.docDecl.attToken, refErr);
    }

    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        Expression r = this.docDecl.attFeeder.feed(this.expression, token, this.ignoreUndeclaredAttributes);
        if (r != Expression.nullSet) {
            this.expression = r;
            if (Debug.debug) {
                System.out.println("-- residual after :" + ExpressionPrinter.printContentModel(r));
            }
            return true;
        }
        if (refErr == null) {
            return false;
        }
        AttributeRecoveryToken rtoken = token.createRecoveryAttToken();
        r = this.docDecl.attFeeder.feed(this.expression, rtoken, this.ignoreUndeclaredAttributes);
        if (r == Expression.nullSet) {
            refErr.str = this.expression == Expression.nullSet ? this.docDecl.localizeMessage("Diagnosis.ContentModelIsNullset", null) : this.docDecl.localizeMessage("Diagnosis.UndeclaredAttribute", token.qName);
            return true;
        }
        refErr.str = this.diagnoseBadAttributeValue(rtoken);
        if (refErr.str == null) {
            refErr.str = this.docDecl.localizeMessage("Diagnosis.BadAttributeValue.Generic", token.qName);
        }
        this.expression = r;
        return true;
    }

    public boolean onEndAttributes(StartTagInfo sti, StringRef refErr) {
        Expression r = this.docDecl.attPruner.prune(this.expression);
        if (r != Expression.nullSet) {
            this.expression = r;
            return true;
        }
        if (refErr == null) {
            return false;
        }
        if (this.expression == Expression.nullSet) {
            refErr.str = this.docDecl.localizeMessage("Diagnosis.ContentModelIsNullset", null);
        } else {
            refErr.str = this.diagnoseMissingAttribute(sti);
            if (refErr.str == null) {
                refErr.str = this.docDecl.localizeMessage("Diagnosis.MissingAttribute.Generic", sti.qName);
            }
        }
        this.expression = this.expression.visit(this.docDecl.attRemover);
        return true;
    }

    protected boolean stepForward(Token token, StringRef errRef) {
        Expression residual = this.docDecl.resCalc.calcResidual(this.expression, token);
        if (Debug.debug) {
            System.out.println("residual of stepForward(" + token + ")");
            System.out.print(ExpressionPrinter.printContentModel(this.expression));
            System.out.print("   ->   ");
            System.out.println(ExpressionPrinter.printContentModel(residual));
        }
        if (residual == Expression.nullSet) {
            if (errRef != null && token instanceof StringToken) {
                errRef.str = this.diagnoseUnexpectedLiteral((StringToken)token);
            }
            return false;
        }
        this.expression = residual;
        return true;
    }

    public final boolean onText(String literal, IDContextProvider context, StringRef refErr, DatatypeRef refType) {
        return this.onText2(literal, IDContextProviderWrapper.create(context), refErr, refType);
    }

    public boolean onText2(String literal, IDContextProvider2 provider, StringRef refErr, DatatypeRef refType) {
        return this.stepForward(new StringToken(this.docDecl, literal, provider, refType), refErr);
    }

    public final boolean stepForwardByContinuation(Expression continuation, StringRef errRef) {
        if (continuation != Expression.nullSet) {
            if (Debug.debug) {
                System.out.println("stepForwardByCont. :  " + ExpressionPrinter.printContentModel(continuation));
            }
            this.expression = continuation;
            return true;
        }
        if (errRef == null) {
            return false;
        }
        return false;
    }

    public boolean isAcceptState(StringRef errRef) {
        if (errRef == null) {
            return this.expression.isEpsilonReducible();
        }
        if (this.expression.isEpsilonReducible()) {
            return true;
        }
        errRef.str = this.diagnoseUncompletedContent();
        return false;
    }

    public int getStringCareLevel() {
        OptimizationTag ot = (OptimizationTag)this.expression.verifierTag;
        if (ot == null) {
            ot = new OptimizationTag();
            this.expression.verifierTag = ot;
        }
        if (ot.stringCareLevel == -1) {
            ot.stringCareLevel = StringCareLevelCalculator.calc(this.expression);
        }
        return ot.stringCareLevel;
    }

    private final Expression mergeContinuation(Expression exp1, Expression exp2) {
        if (exp1 == null && exp2 == null) {
            return null;
        }
        if (exp1 == null || exp1 == Expression.nullSet) {
            return exp2;
        }
        if (exp2 == null || exp2 == Expression.nullSet) {
            return exp1;
        }
        return this.docDecl.pool.createChoice(exp1, exp2);
    }

    private final Acceptor createRecoveryAcceptors() {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        CombinedChildContentExpCreator.ExpressionPair combinedEoC = cccc.get(this.expression, null, false);
        Expression eocr = this.docDecl.resCalc.calcResidual(this.expression, AnyElementToken.theInstance);
        Expression continuation = this.docDecl.pool.createChoice(this.expression, eocr);
        Expression contentModel = combinedEoC.content;
        if (Debug.debug) {
            System.out.println("content model of recovery acceptor:" + ExpressionPrinter.printContentModel(contentModel));
            System.out.println("continuation of recovery acceptor:" + ExpressionPrinter.printSmallest(continuation));
        }
        return this.createAcceptor(contentModel, continuation, null, 0);
    }

    private String concatenateMessages(List items, boolean more, String separatorStr, String moreStr) {
        String r = "";
        String sep = this.docDecl.localizeMessage(separatorStr, null);
        Collections.sort(items, new Comparator(){

            public int compare(Object o1, Object o2) {
                return ((String)o1).compareTo((String)o2);
            }
        });
        for (int i = 0; i < items.size(); ++i) {
            if (r.length() != 0) {
                r = r + sep;
            }
            r = r + items.get(i);
        }
        if (more) {
            r = r + this.docDecl.localizeMessage(moreStr, null);
        }
        return r;
    }

    private String concatenateMessages(Set items, boolean more, String separatorStr, String moreStr) {
        return this.concatenateMessages(new Vector(items), more, separatorStr, moreStr);
    }

    private String getDiagnosisFromTypedString(DataOrValueExp exp, StringToken value) {
        try {
            exp.getType().checkValid(value.literal, value.context);
            return null;
        }
        catch (DatatypeException e) {
            return e.getMessage();
        }
    }

    private String diagnoseBadTagName(StartTagInfo sti) {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        Expression r = cccc.get((Expression)this.expression, (StartTagInfo)sti, (boolean)false).content;
        if (r == Expression.nullSet) {
            return this.docDecl.localizeMessage("Diagnosis.ElementNotAllowed", sti.qName);
        }
        if (cccc.isComplex()) {
            return null;
        }
        HashSet<String> s = new HashSet<String>();
        boolean more = false;
        String wrongNamespace = null;
        ElementExp[] eocs = cccc.getMatchedElements();
        int len = cccc.numMatchedElements();
        for (int i = 0; i < len; ++i) {
            NameClass ncc;
            if (eocs[i].contentModel.getExpandedExp(this.docDecl.pool) == Expression.nullSet) continue;
            NameClass nc = eocs[i].getNameClass();
            if (nc instanceof SimpleNameClass) {
                SimpleNameClass snc = (SimpleNameClass)nc;
                if (snc.localName.equals(sti.localName)) {
                    wrongNamespace = snc.namespaceURI;
                }
                s.add(this.docDecl.localizeMessage("Diagnosis.SimpleNameClass", nc.toString()));
                continue;
            }
            if (nc instanceof NamespaceNameClass) {
                s.add(this.docDecl.localizeMessage("Diagnosis.NamespaceNameClass", ((NamespaceNameClass)nc).namespaceURI));
                continue;
            }
            if (nc instanceof NotNameClass && (ncc = ((NotNameClass)nc).child) instanceof NamespaceNameClass) {
                s.add(this.docDecl.localizeMessage("Diagnosis.NotNamespaceNameClass", ((NamespaceNameClass)ncc).namespaceURI));
                continue;
            }
            more = true;
        }
        if (s.size() == 0) {
            return null;
        }
        if (wrongNamespace != null) {
            if (s.size() == 1) {
                return this.docDecl.localizeMessage("Diagnosis.BadTagName.WrongNamespace", sti.localName, wrongNamespace);
            }
            return this.docDecl.localizeMessage("Diagnosis.BadTagName.ProbablyWrongNamespace", sti.localName, wrongNamespace);
        }
        return this.docDecl.localizeMessage("Diagnosis.BadTagName.WrapUp", sti.qName, this.concatenateMessages(s, more, "Diagnosis.BadTagName.Separator", "Diagnosis.BadTagName.More"));
    }

    private String diagnoseBadAttributeValue(AttributeRecoveryToken rtoken) {
        Expression constraint = rtoken.getFailedExp();
        if (constraint instanceof DataOrValueExp) {
            DataOrValueExp tse = (DataOrValueExp)((Object)constraint);
            if (tse.getType() == NoneType.theInstance) {
                return this.docDecl.localizeMessage("Diagnosis.UndeclaredAttribute", rtoken.qName);
            }
            String dtMsg = this.getDiagnosisFromTypedString(tse, rtoken.value);
            if (dtMsg == null) {
                return null;
            }
            return this.docDecl.localizeMessage("Diagnosis.BadAttributeValue.DataType", rtoken.qName, dtMsg);
        }
        if (constraint instanceof ChoiceExp) {
            HashSet<String> items = new HashSet<String>();
            boolean more = false;
            ChoiceExp ch = (ChoiceExp)constraint;
            Expression[] children = ch.getChildren();
            for (int i = 0; i < children.length; ++i) {
                if (children[i] instanceof ValueExp) {
                    items.add(((ValueExp)children[i]).value.toString());
                    continue;
                }
                more = true;
            }
            if (items.size() == 0) {
                return null;
            }
            return this.docDecl.localizeMessage("Diagnosis.BadAttributeValue.WrapUp", rtoken.qName, this.concatenateMessages(items, more, "Diagnosis.BadAttributeValue.Separator", "Diagnosis.BadAttributeValue.More"));
        }
        return null;
    }

    private String diagnoseMissingAttribute(StartTagInfo sti) {
        Expression e = this.expression.visit(this.docDecl.attPicker);
        if (e.isEpsilonReducible()) {
            throw new Error();
        }
        HashSet<String> s = new HashSet<String>();
        boolean more = false;
        while (e instanceof ChoiceExp) {
            ChoiceExp ch = (ChoiceExp)e;
            NameClass nc = ((AttributeExp)ch.exp2).nameClass;
            if (nc instanceof SimpleNameClass) {
                s.add(nc.toString());
            } else {
                more = true;
            }
            e = ch.exp1;
        }
        if (e == Expression.nullSet) {
            return null;
        }
        if (!(e instanceof AttributeExp)) {
            throw new Error(e.toString());
        }
        NameClass nc = ((AttributeExp)e).nameClass;
        if (nc instanceof SimpleNameClass) {
            s.add(nc.toString());
        } else {
            more = true;
        }
        if (s.size() == 0) {
            return null;
        }
        if (s.size() == 1 && !more) {
            return this.docDecl.localizeMessage("Diagnosis.MissingAttribute.Simple", sti.qName, s.iterator().next());
        }
        return this.docDecl.localizeMessage("Diagnosis.MissingAttribute.WrapUp", sti.qName, this.concatenateMessages(s, more, "Diagnosis.MissingAttribute.Separator", "Diagnosis.MissingAttribute.More"));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private String diagnoseUnexpectedLiteral(StringToken token) {
        StringRecoveryToken srt = new StringRecoveryToken(token);
        Expression recoveryResidual = this.docDecl.resCalc.calcResidual(this.expression, srt);
        if (recoveryResidual == Expression.nullSet) {
            return this.docDecl.localizeMessage("Diagnosis.StringNotAllowed", token.literal.trim());
        }
        this.expression = this.docDecl.pool.createChoice(this.expression, recoveryResidual);
        if (srt.failedExps.size() == 1) {
            DataOrValueExp texp = (DataOrValueExp)srt.failedExps.iterator().next();
            try {
                texp.getType().checkValid(srt.literal, srt.context);
                if (!(texp instanceof ValueExp)) return null;
                ValueExp vexp = (ValueExp)texp;
                if (vexp.dt.sameValue(vexp.value, vexp.dt.createValue(srt.literal, srt.context))) return null;
                return this.docDecl.localizeMessage("Diagnosis.BadLiteral.IncorrectValue", vexp.value.toString(), token.literal.trim());
            }
            catch (DatatypeException de) {
                if (de.getMessage() == null) return this.docDecl.localizeMessage("Diagnosis.BadLiteral.Generic", token.literal.trim());
                return de.getMessage();
            }
        } else {
            HashSet<String> items = new HashSet<String>();
            boolean more = false;
            for (DataOrValueExp texp : srt.failedExps) {
                if (texp instanceof ValueExp) {
                    items.add(((ValueExp)texp).value.toString());
                    continue;
                }
                more = true;
            }
            if (items.size() != 0) return this.docDecl.localizeMessage("Diagnosis.BadLiteral.WrapUp", this.concatenateMessages(items, more, "Diagnosis.BadLiteral.Separator", "Diagnosis.BadLiteral.More"), token.literal.trim());
            return null;
        }
    }

    protected String diagnoseUncompletedContent() {
        CombinedChildContentExpCreator cccc = this.docDecl.cccec;
        cccc.get(this.expression, null, false);
        HashSet<String> s = new HashSet<String>();
        boolean more = false;
        ElementExp[] eocs = cccc.getMatchedElements();
        int len = cccc.numMatchedElements();
        for (int i = 0; i < len; ++i) {
            NameClass ncc;
            NameClass nc = eocs[i].getNameClass();
            if (nc instanceof SimpleNameClass) {
                s.add(this.docDecl.localizeMessage("Diagnosis.SimpleNameClass", nc.toString()));
                continue;
            }
            if (nc instanceof NamespaceNameClass) {
                s.add(this.docDecl.localizeMessage("Diagnosis.NamespaceNameClass", ((NamespaceNameClass)nc).namespaceURI));
                continue;
            }
            if (nc instanceof NotNameClass && (ncc = ((NotNameClass)nc).child) instanceof NamespaceNameClass) {
                s.add(this.docDecl.localizeMessage("Diagnosis.NotNamespaceNameClass", ((NamespaceNameClass)ncc).namespaceURI));
                continue;
            }
            more = true;
        }
        if (s.size() == 0) {
            return null;
        }
        return this.docDecl.localizeMessage("Diagnosis.UncompletedContent.WrapUp", null, this.concatenateMessages(s, more, "Diagnosis.UncompletedContent.Separator", "Diagnosis.UncompletedContent.More"));
    }
}

