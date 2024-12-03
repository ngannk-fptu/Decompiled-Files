/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.internal.lang.reflect.TypePatternImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;

public class DeclarePrecedenceImpl
implements DeclarePrecedence {
    private AjType<?> declaringType;
    private TypePattern[] precedenceList;
    private String precedenceString;

    public DeclarePrecedenceImpl(String precedenceList, AjType declaring) {
        this.declaringType = declaring;
        this.precedenceString = precedenceList;
        String toTokenize = precedenceList;
        if (toTokenize.startsWith("(")) {
            toTokenize = toTokenize.substring(1, toTokenize.length() - 1);
        }
        StringTokenizer strTok = new StringTokenizer(toTokenize, ",");
        this.precedenceList = new TypePattern[strTok.countTokens()];
        for (int i = 0; i < this.precedenceList.length; ++i) {
            this.precedenceList[i] = new TypePatternImpl(strTok.nextToken().trim());
        }
    }

    @Override
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public TypePattern[] getPrecedenceOrder() {
        return this.precedenceList;
    }

    public String toString() {
        return "declare precedence : " + this.precedenceString;
    }
}

