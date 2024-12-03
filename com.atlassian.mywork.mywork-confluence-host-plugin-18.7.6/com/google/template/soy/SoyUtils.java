/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.io.CharSource
 *  com.google.common.io.Files
 */
package com.google.template.soy;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.data.internalutils.InternalValueUtils;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.PrimitiveData;
import com.google.template.soy.exprparse.ExpressionParser;
import com.google.template.soy.exprparse.ParseException;
import com.google.template.soy.exprparse.TokenMgrError;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.internal.base.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoyUtils {
    private static final Pattern COMPILE_TIME_GLOBAL_LINE = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9.]*) \\s* = \\s* (.+)", 4);

    private SoyUtils() {
    }

    public static void generateCompileTimeGlobalsFile(Map<String, ?> compileTimeGlobalsMap, Appendable output) throws IOException {
        ImmutableMap<String, PrimitiveData> compileTimeGlobals = InternalValueUtils.convertCompileTimeGlobalsMap(compileTimeGlobalsMap);
        for (Map.Entry entry : compileTimeGlobals.entrySet()) {
            String valueSrcStr = InternalValueUtils.convertPrimitiveDataToExpr((PrimitiveData)entry.getValue()).toSourceString();
            output.append((CharSequence)entry.getKey()).append(" = ").append(valueSrcStr).append("\n");
        }
    }

    public static void generateCompileTimeGlobalsFile(Map<String, ?> compileTimeGlobalsMap, File file) throws IOException {
        BufferedWriter writer = Files.newWriter((File)file, (Charset)Charsets.UTF_8);
        SoyUtils.generateCompileTimeGlobalsFile(compileTimeGlobalsMap, writer);
        writer.close();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static ImmutableMap<String, PrimitiveData> parseCompileTimeGlobals(CharSource charSource) throws IOException, SoySyntaxException {
        ImmutableMap.Builder compileTimeGlobalsBuilder = ImmutableMap.builder();
        ArrayList errors = Lists.newArrayListWithCapacity((int)0);
        BufferedReader reader = charSource.openBufferedStream();
        String line = reader.readLine();
        while (line != null) {
            block16: {
                if (!line.startsWith("//") && line.trim().length() != 0) {
                    Matcher matcher = COMPILE_TIME_GLOBAL_LINE.matcher(line);
                    if (!matcher.matches()) {
                        errors.add(Pair.of(CompileTimeGlobalsFileError.INVALID_FORMAT, line));
                    } else {
                        String name = matcher.group(1);
                        String valueText = matcher.group(2).trim();
                        try {
                            Node valueExpr = new ExpressionParser(valueText).parseExpression().getChild(0);
                            if (valueExpr instanceof OperatorNodes.NegativeOpNode) {
                                ExprNode childExpr = ((OperatorNodes.NegativeOpNode)valueExpr).getChild(0);
                                if (childExpr instanceof IntegerNode) {
                                    compileTimeGlobalsBuilder.put((Object)name, (Object)IntegerData.forValue(-((IntegerNode)childExpr).getValue()));
                                    break block16;
                                }
                                if (childExpr instanceof FloatNode) {
                                    compileTimeGlobalsBuilder.put((Object)name, (Object)FloatData.forValue(-((FloatNode)childExpr).getValue()));
                                    break block16;
                                }
                            }
                            if (!(valueExpr instanceof ExprNode.PrimitiveNode)) {
                                if (valueExpr instanceof GlobalNode || valueExpr instanceof VarRefNode) {
                                    errors.add(Pair.of(CompileTimeGlobalsFileError.INVALID_VALUE, line));
                                    break block16;
                                } else {
                                    errors.add(Pair.of(CompileTimeGlobalsFileError.NON_PRIMITIVE_VALUE, line));
                                }
                                break block16;
                            }
                            compileTimeGlobalsBuilder.put((Object)name, (Object)InternalValueUtils.convertPrimitiveExprToData((ExprNode.PrimitiveNode)valueExpr));
                        }
                        catch (TokenMgrError tme) {
                            errors.add(Pair.of(CompileTimeGlobalsFileError.INVALID_VALUE, line));
                        }
                        catch (ParseException pe) {
                            errors.add(Pair.of(CompileTimeGlobalsFileError.INVALID_VALUE, line));
                        }
                    }
                }
            }
            line = reader.readLine();
        }
        if (errors.size() <= 0) {
            return compileTimeGlobalsBuilder.build();
        }
        StringBuilder errorMsgSb = new StringBuilder("Compile-time globals file contains the following errors:\n");
        Iterator iterator = errors.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                throw SoySyntaxException.createWithoutMetaInfo(errorMsgSb.toString());
            }
            Pair error = (Pair)iterator.next();
            errorMsgSb.append("[").append(String.format("%-19s", ((CompileTimeGlobalsFileError)((Object)error.first)).toString())).append("] ").append((String)error.second).append("\n");
        }
    }

    private static enum CompileTimeGlobalsFileError {
        INVALID_FORMAT("Invalid line format"),
        INVALID_VALUE("Invalid value"),
        NON_PRIMITIVE_VALUE("Non-primitive value");

        private final String errorString;

        private CompileTimeGlobalsFileError(String errorString) {
            this.errorString = errorString;
        }

        public String toString() {
            return this.errorString;
        }
    }
}

