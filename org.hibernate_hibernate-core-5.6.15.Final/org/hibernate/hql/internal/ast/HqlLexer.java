/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.Token
 */
package org.hibernate.hql.internal.ast;

import antlr.Token;
import java.io.Reader;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.antlr.HqlBaseLexer;
import org.hibernate.hql.internal.ast.HqlToken;

class HqlLexer
extends HqlBaseLexer {
    private boolean possibleID;

    public HqlLexer(Reader in) {
        super(in);
    }

    public void setTokenObjectClass(String cl) {
        this.tokenObjectClass = HqlToken.class;
    }

    @Override
    protected void setPossibleID(boolean possibleID) {
        this.possibleID = possibleID;
    }

    protected Token makeToken(int i) {
        HqlToken token = (HqlToken)super.makeToken(i);
        token.setPossibleID(this.possibleID);
        this.possibleID = false;
        return token;
    }

    public void panic() {
        this.panic("CharScanner: panic");
    }

    public void panic(String s) {
        throw new QueryException(s);
    }
}

