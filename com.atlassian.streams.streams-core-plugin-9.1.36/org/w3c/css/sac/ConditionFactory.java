/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

public interface ConditionFactory {
    public CombinatorCondition createAndCondition(Condition var1, Condition var2) throws CSSException;

    public CombinatorCondition createOrCondition(Condition var1, Condition var2) throws CSSException;

    public NegativeCondition createNegativeCondition(Condition var1) throws CSSException;

    public PositionalCondition createPositionalCondition(int var1, boolean var2, boolean var3) throws CSSException;

    public AttributeCondition createAttributeCondition(String var1, String var2, boolean var3, String var4) throws CSSException;

    public AttributeCondition createIdCondition(String var1) throws CSSException;

    public LangCondition createLangCondition(String var1) throws CSSException;

    public AttributeCondition createOneOfAttributeCondition(String var1, String var2, boolean var3, String var4) throws CSSException;

    public AttributeCondition createBeginHyphenAttributeCondition(String var1, String var2, boolean var3, String var4) throws CSSException;

    public AttributeCondition createClassCondition(String var1, String var2) throws CSSException;

    public AttributeCondition createPseudoClassCondition(String var1, String var2) throws CSSException;

    public Condition createOnlyChildCondition() throws CSSException;

    public Condition createOnlyTypeCondition() throws CSSException;

    public ContentCondition createContentCondition(String var1) throws CSSException;
}

