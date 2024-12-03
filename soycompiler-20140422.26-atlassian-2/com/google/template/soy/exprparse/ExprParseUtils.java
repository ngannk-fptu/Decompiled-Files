/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprparse;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExpressionParser;
import com.google.template.soy.exprparse.ParseException;
import com.google.template.soy.exprparse.TokenMgrError;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.VarNode;
import java.util.List;

public class ExprParseUtils {
    private ExprParseUtils() {
    }

    public static List<ExprRootNode<?>> parseExprListElseThrowSoySyntaxException(String exprListText, String errorMsg) {
        try {
            return new ExpressionParser(exprListText).parseExpressionList();
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, tme);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, pe);
        }
    }

    public static ExprRootNode<?> parseExprElseThrowSoySyntaxException(String exprText, String errorMsg) {
        try {
            return new ExpressionParser(exprText).parseExpression();
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, tme);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, pe);
        }
    }

    public static ExprRootNode<?> parseExprElseNull(String exprText) {
        try {
            return new ExpressionParser(exprText).parseExpression();
        }
        catch (TokenMgrError tme) {
            return null;
        }
        catch (ParseException pe) {
            return null;
        }
    }

    public static String parseVarNameElseThrowSoySyntaxException(String exprText, String errorMsg) {
        try {
            return ((VarNode)new ExpressionParser(exprText).parseVariable().getChild(0)).getName();
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, tme);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, pe);
        }
    }

    public static ExprRootNode<ExprNode> parseDataRefElseThrowSoySyntaxException(String exprText, String errorMsg) {
        try {
            return new ExpressionParser(exprText).parseDataReference();
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, tme);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, pe);
        }
    }

    public static ExprRootNode<GlobalNode> parseGlobalElseThrowSoySyntaxException(String exprText, String errorMsg) {
        try {
            return new ExpressionParser(exprText).parseGlobal();
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, tme);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo(errorMsg, pe);
        }
    }
}

