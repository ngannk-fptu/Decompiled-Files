/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.StringLiteral;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.ext.beans._MethodUtil;
import freemarker.log.Logger;
import freemarker.template.Template;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class _ErrorDescriptionBuilder {
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private final String description;
    private final Object[] descriptionParts;
    private Expression blamed;
    private boolean showBlamer;
    private Object tip;
    private Object[] tips;
    private Template template;

    public _ErrorDescriptionBuilder(String description) {
        this.description = description;
        this.descriptionParts = null;
    }

    public _ErrorDescriptionBuilder(Object ... descriptionParts) {
        this.descriptionParts = descriptionParts;
        this.description = null;
    }

    public String toString() {
        return this.toString(null, true);
    }

    public String toString(TemplateElement parentElement, boolean showTips) {
        int i;
        if (this.blamed == null && this.tips == null && this.tip == null && this.descriptionParts == null) {
            return this.description;
        }
        StringBuilder sb = new StringBuilder(200);
        if (parentElement != null && this.blamed != null && this.showBlamer) {
            try {
                Blaming blaming = this.findBlaming(parentElement, this.blamed, 0);
                if (blaming != null) {
                    sb.append("For ");
                    String nss = blaming.blamer.getNodeTypeSymbol();
                    char q = nss.indexOf(34) == -1 ? (char)'\"' : '`';
                    sb.append(q).append(nss).append(q);
                    sb.append(" ").append(blaming.roleOfblamed).append(": ");
                }
            }
            catch (Throwable e) {
                LOG.error("Error when searching blamer for better error message.", e);
            }
        }
        if (this.description != null) {
            sb.append(this.description);
        } else {
            this.appendParts(sb, this.descriptionParts);
        }
        String extraTip = null;
        if (this.blamed != null) {
            char lastChar;
            for (int idx = sb.length() - 1; idx >= 0 && Character.isWhitespace(sb.charAt(idx)); --idx) {
                sb.deleteCharAt(idx);
            }
            char c = lastChar = sb.length() > 0 ? sb.charAt(sb.length() - 1) : (char)'\u0000';
            if (lastChar != '\u0000') {
                sb.append('\n');
            }
            if (lastChar != ':') {
                sb.append("The blamed expression:\n");
            }
            String[] lines = this.splitToLines(this.blamed.toString());
            for (i = 0; i < lines.length; ++i) {
                sb.append(i == 0 ? "==> " : "\n    ");
                sb.append(lines[i]);
            }
            sb.append("  [");
            sb.append(this.blamed.getStartLocation());
            sb.append(']');
            if (this.containsSingleInterpolatoinLiteral(this.blamed, 0)) {
                extraTip = "It has been noticed that you are using ${...} as the sole content of a quoted string. That does nothing but forcably converts the value inside ${...} to string (as it inserts it into the enclosing string). If that's not what you meant, just remove the quotation marks, ${ and }; you don't need them. If you indeed wanted to convert to string, use myExpression?string instead.";
            }
        }
        if (showTips) {
            Object[] allTips;
            int allTipsLen = (this.tips != null ? this.tips.length : 0) + (this.tip != null ? 1 : 0) + (extraTip != null ? 1 : 0);
            if (this.tips != null && allTipsLen == this.tips.length) {
                allTips = this.tips;
            } else {
                allTips = new Object[allTipsLen];
                int dst = 0;
                if (this.tip != null) {
                    allTips[dst++] = this.tip;
                }
                if (this.tips != null) {
                    for (int i2 = 0; i2 < this.tips.length; ++i2) {
                        allTips[dst++] = this.tips[i2];
                    }
                }
                if (extraTip != null) {
                    allTips[dst++] = extraTip;
                }
            }
            if (allTips != null && allTips.length > 0) {
                sb.append("\n\n");
                for (i = 0; i < allTips.length; ++i) {
                    if (i != 0) {
                        sb.append('\n');
                    }
                    sb.append("----").append('\n');
                    sb.append("Tip: ");
                    Object tip = allTips[i];
                    if (!(tip instanceof Object[])) {
                        sb.append(allTips[i]);
                        continue;
                    }
                    this.appendParts(sb, (Object[])tip);
                }
                sb.append('\n').append("----");
            }
        }
        return sb.toString();
    }

    private boolean containsSingleInterpolatoinLiteral(Expression exp, int recursionDepth) {
        if (exp == null) {
            return false;
        }
        if (recursionDepth > 20) {
            return false;
        }
        if (exp instanceof StringLiteral && ((StringLiteral)exp).isSingleInterpolationLiteral()) {
            return true;
        }
        int paramCnt = exp.getParameterCount();
        for (int i = 0; i < paramCnt; ++i) {
            boolean result;
            Object paramValue = exp.getParameterValue(i);
            if (!(paramValue instanceof Expression) || !(result = this.containsSingleInterpolatoinLiteral((Expression)paramValue, recursionDepth + 1))) continue;
            return true;
        }
        return false;
    }

    private Blaming findBlaming(TemplateObject parent, Expression blamed, int recursionDepth) {
        if (recursionDepth > 50) {
            return null;
        }
        int paramCnt = parent.getParameterCount();
        for (int i = 0; i < paramCnt; ++i) {
            Blaming blaming;
            Object paramValue = parent.getParameterValue(i);
            if (paramValue == blamed) {
                blaming = new Blaming();
                blaming.blamer = parent;
                blaming.roleOfblamed = parent.getParameterRole(i);
                return blaming;
            }
            if (!(paramValue instanceof TemplateObject) || (blaming = this.findBlaming((TemplateObject)paramValue, blamed, recursionDepth + 1)) == null) continue;
            return blaming;
        }
        return null;
    }

    private void appendParts(StringBuilder sb, Object[] parts) {
        Template template = this.template != null ? this.template : (this.blamed != null ? this.blamed.getTemplate() : null);
        for (int i = 0; i < parts.length; ++i) {
            Object partObj = parts[i];
            if (partObj instanceof Object[]) {
                this.appendParts(sb, (Object[])partObj);
                continue;
            }
            String partStr = _ErrorDescriptionBuilder.tryToString(partObj);
            if (partStr == null) {
                partStr = "null";
            }
            if (template != null) {
                if (partStr.length() > 4 && partStr.charAt(0) == '<' && (partStr.charAt(1) == '#' || partStr.charAt(1) == '@' || partStr.charAt(1) == '/' && (partStr.charAt(2) == '#' || partStr.charAt(2) == '@')) && partStr.charAt(partStr.length() - 1) == '>') {
                    if (template.getActualTagSyntax() == 2) {
                        sb.append('[');
                        sb.append(partStr.substring(1, partStr.length() - 1));
                        sb.append(']');
                        continue;
                    }
                    sb.append(partStr);
                    continue;
                }
                sb.append(partStr);
                continue;
            }
            sb.append(partStr);
        }
    }

    public static String toString(Object partObj) {
        return _ErrorDescriptionBuilder.toString(partObj, false);
    }

    public static String tryToString(Object partObj) {
        return _ErrorDescriptionBuilder.toString(partObj, true);
    }

    private static String toString(Object partObj, boolean suppressToStringException) {
        if (partObj == null) {
            return null;
        }
        String partStr = partObj instanceof Class ? ClassUtil.getShortClassName((Class)partObj) : (partObj instanceof Method || partObj instanceof Constructor ? _MethodUtil.toString((Member)partObj) : (suppressToStringException ? StringUtil.tryToString(partObj) : partObj.toString()));
        return partStr;
    }

    private String[] splitToLines(String s) {
        s = StringUtil.replace(s, "\r\n", "\n");
        s = StringUtil.replace(s, "\r", "\n");
        String[] lines = StringUtil.split(s, '\n');
        return lines;
    }

    public _ErrorDescriptionBuilder template(Template template) {
        this.template = template;
        return this;
    }

    public _ErrorDescriptionBuilder blame(Expression blamedExpr) {
        this.blamed = blamedExpr;
        return this;
    }

    public _ErrorDescriptionBuilder showBlamer(boolean showBlamer) {
        this.showBlamer = showBlamer;
        return this;
    }

    public _ErrorDescriptionBuilder tip(String tip) {
        this.tip((Object)tip);
        return this;
    }

    public _ErrorDescriptionBuilder tip(Object ... tip) {
        this.tip((Object)tip);
        return this;
    }

    private _ErrorDescriptionBuilder tip(Object tip) {
        if (tip == null) {
            return this;
        }
        if (this.tip == null) {
            this.tip = tip;
        } else if (this.tips == null) {
            this.tips = new Object[]{tip};
        } else {
            int origTipsLen = this.tips.length;
            Object[] newTips = new Object[origTipsLen + 1];
            for (int i = 0; i < origTipsLen; ++i) {
                newTips[i] = this.tips[i];
            }
            newTips[origTipsLen] = tip;
            this.tips = newTips;
        }
        return this;
    }

    public _ErrorDescriptionBuilder tips(Object ... tips) {
        if (tips == null || tips.length == 0) {
            return this;
        }
        if (this.tips == null) {
            this.tips = tips;
        } else {
            int i;
            int origTipsLen = this.tips.length;
            int additionalTipsLen = tips.length;
            Object[] newTips = new Object[origTipsLen + additionalTipsLen];
            for (i = 0; i < origTipsLen; ++i) {
                newTips[i] = this.tips[i];
            }
            for (i = 0; i < additionalTipsLen; ++i) {
                newTips[origTipsLen + i] = tips[i];
            }
            this.tips = newTips;
        }
        return this;
    }

    private static class Blaming {
        TemplateObject blamer;
        ParameterRole roleOfblamed;

        private Blaming() {
        }
    }
}

