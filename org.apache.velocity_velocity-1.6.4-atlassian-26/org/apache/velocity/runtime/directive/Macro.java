/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.MacroParseException;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;

public class Macro
extends Directive {
    private static boolean debugMode = false;

    @Override
    public String getName() {
        return "macro";
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        return true;
    }

    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
    }

    public static void processAndRegister(RuntimeServices rs, Token t, Node node, String sourceTemplate) throws IOException, ParseException {
        int numArgs = node.jjtGetNumChildren();
        if (numArgs < 2) {
            rs.getLog().error("#macro error : Velocimacro must have name as 1st argument to #macro(). #args = " + numArgs);
            throw new MacroParseException("First argument to #macro() must be  macro name.", sourceTemplate, t);
        }
        int firstType = node.jjtGetChild(0).getType();
        if (firstType != 9) {
            throw new MacroParseException("First argument to #macro() must be a token without surrounding ' or \", which specifies the macro name.  Currently it is a " + ParserTreeConstants.jjtNodeName[firstType], sourceTemplate, t);
        }
        String[] argArray = Macro.getArgArray(node, rs);
        rs.addVelocimacro(argArray[0], node.jjtGetChild(numArgs - 1), argArray, sourceTemplate);
    }

    private static String[] getArgArray(Node node, RuntimeServices rsvc) {
        int numArgs = node.jjtGetNumChildren();
        String[] argArray = new String[--numArgs];
        for (int i = 0; i < numArgs; ++i) {
            argArray[i] = node.jjtGetChild((int)i).getFirstToken().image;
            if (i <= 0 || !argArray[i].startsWith("$")) continue;
            argArray[i] = argArray[i].substring(1, argArray[i].length());
        }
        if (debugMode) {
            StringBuffer msg = new StringBuffer("Macro.getArgArray() : nbrArgs=");
            msg.append(numArgs).append(" : ");
            Macro.macroToString(msg, argArray);
            rsvc.getLog().debug(msg);
        }
        return argArray;
    }

    public static final StringBuffer macroToString(StringBuffer buf, String[] argArray) {
        StringBuffer ret = buf == null ? new StringBuffer() : buf;
        ret.append('#').append(argArray[0]).append("( ");
        for (int i = 1; i < argArray.length; ++i) {
            ret.append(' ').append(argArray[i]);
        }
        ret.append(" )");
        return ret;
    }
}

