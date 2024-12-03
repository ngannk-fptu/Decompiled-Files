/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermBracketedIdents;
import cz.vutbr.web.css.TermCalc;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermExpression;
import cz.vutbr.web.css.TermFrequency;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.css.TermOperator;
import cz.vutbr.web.css.TermPair;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.css.TermRect;
import cz.vutbr.web.css.TermResolution;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.css.TermTime;
import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.css.TermUnicodeRange;
import java.net.URL;
import java.util.List;

public interface TermFactory {
    public TermAngle createAngle(Float var1);

    public TermAngle createAngle(String var1, TermNumeric.Unit var2, int var3);

    public TermCalc createCalc(List<Term<?>> var1);

    public TermColor createColor(TermIdent var1);

    public TermColor createColor(String var1);

    public TermColor createColor(int var1, int var2, int var3);

    public TermColor createColor(int var1, int var2, int var3, int var4);

    public TermColor createColor(TermFunction var1);

    public TermFrequency createFrequency(Float var1);

    public TermFrequency createFrequency(String var1, TermNumeric.Unit var2, int var3);

    public TermExpression createExpression(String var1);

    public TermFunction createFunction(String var1);

    public TermFunction createFunction(String var1, List<Term<?>> var2);

    public TermIdent createIdent(String var1);

    public TermIdent createIdent(String var1, boolean var2);

    public TermBracketedIdents createBracketedIdents();

    public TermBracketedIdents createBracketedIdents(int var1);

    public TermInteger createInteger(Integer var1);

    public TermInteger createInteger(String var1, int var2);

    public TermLength createLength(Float var1);

    public TermLength createLength(Float var1, TermNumeric.Unit var2);

    public TermLength createLength(String var1, TermNumeric.Unit var2, int var3);

    public TermList createList();

    public TermList createList(int var1);

    public TermNumber createNumber(Float var1);

    public TermNumber createNumber(String var1, int var2);

    public TermNumeric<?> createNumeric(String var1, int var2);

    public TermNumeric<Float> createDimension(String var1, int var2);

    public <K, V> TermPair<K, V> createPair(K var1, V var2);

    public TermPercent createPercent(Float var1);

    public TermPercent createPercent(String var1, int var2);

    public TermRect createRect(TermFunction var1);

    public TermRect createRect(TermLength var1, TermLength var2, TermLength var3, TermLength var4);

    public TermResolution createResolution(Float var1);

    public TermResolution createResolution(String var1, TermNumeric.Unit var2, int var3);

    public TermString createString(String var1);

    public <V> Term<V> createTerm(V var1);

    public TermTime createTime(Float var1);

    public TermTime createTime(Float var1, TermNumeric.Unit var2);

    public TermTime createTime(String var1, TermNumeric.Unit var2, int var3);

    public TermUnicodeRange createUnicodeRange(String var1);

    public TermURI createURI(String var1);

    public TermURI createURI(String var1, URL var2);

    public TermOperator createOperator(char var1);
}

