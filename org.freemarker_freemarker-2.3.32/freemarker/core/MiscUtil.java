/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Expression;
import freemarker.core.ParentheticalExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class MiscUtil {
    static final String C_FALSE = "false";
    static final String C_TRUE = "true";

    private MiscUtil() {
    }

    static List sortMapOfExpressions(Map map) {
        ArrayList res = new ArrayList(map.entrySet());
        Collections.sort(res, new Comparator(){

            public int compare(Object o1, Object o2) {
                Map.Entry ent1 = (Map.Entry)o1;
                Expression exp1 = (Expression)ent1.getValue();
                Map.Entry ent2 = (Map.Entry)o2;
                Expression exp2 = (Expression)ent2.getValue();
                int res = exp1.beginLine - exp2.beginLine;
                if (res != 0) {
                    return res;
                }
                res = exp1.beginColumn - exp2.beginColumn;
                if (res != 0) {
                    return res;
                }
                if (ent1 == ent2) {
                    return 0;
                }
                return ((String)ent1.getKey()).compareTo((String)ent1.getKey());
            }
        });
        return res;
    }

    static Expression peelParentheses(Expression exp) {
        while (exp instanceof ParentheticalExpression) {
            exp = ((ParentheticalExpression)exp).getNestedExpression();
        }
        return exp;
    }
}

