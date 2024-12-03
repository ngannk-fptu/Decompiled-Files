/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.JoinPoint$StaticPart
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.weaver.tools.PointcutParser
 *  org.aspectj.weaver.tools.PointcutPrimitive
 */
package org.springframework.aop.aspectj;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.springframework.aop.aspectj.AspectJProxyUtils;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class AspectJAdviceParameterNameDiscoverer
implements ParameterNameDiscoverer {
    private static final String THIS_JOIN_POINT = "thisJoinPoint";
    private static final String THIS_JOIN_POINT_STATIC_PART = "thisJoinPointStaticPart";
    private static final int STEP_JOIN_POINT_BINDING = 1;
    private static final int STEP_THROWING_BINDING = 2;
    private static final int STEP_ANNOTATION_BINDING = 3;
    private static final int STEP_RETURNING_BINDING = 4;
    private static final int STEP_PRIMITIVE_ARGS_BINDING = 5;
    private static final int STEP_THIS_TARGET_ARGS_BINDING = 6;
    private static final int STEP_REFERENCE_PCUT_BINDING = 7;
    private static final int STEP_FINISHED = 8;
    private static final Set<String> singleValuedAnnotationPcds = new HashSet<String>();
    private static final Set<String> nonReferencePointcutTokens = new HashSet<String>();
    @Nullable
    private final String pointcutExpression;
    private boolean raiseExceptions;
    @Nullable
    private String returningName;
    @Nullable
    private String throwingName;
    private Class<?>[] argumentTypes = new Class[0];
    private String[] parameterNameBindings = new String[0];
    private int numberOfRemainingUnboundArguments;

    public AspectJAdviceParameterNameDiscoverer(@Nullable String pointcutExpression) {
        this.pointcutExpression = pointcutExpression;
    }

    public void setRaiseExceptions(boolean raiseExceptions) {
        this.raiseExceptions = raiseExceptions;
    }

    public void setReturningName(@Nullable String returningName) {
        this.returningName = returningName;
    }

    public void setThrowingName(@Nullable String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    @Nullable
    public String[] getParameterNames(Method method) {
        this.argumentTypes = method.getParameterTypes();
        this.numberOfRemainingUnboundArguments = this.argumentTypes.length;
        this.parameterNameBindings = new String[this.numberOfRemainingUnboundArguments];
        int minimumNumberUnboundArgs = 0;
        if (this.returningName != null) {
            ++minimumNumberUnboundArgs;
        }
        if (this.throwingName != null) {
            ++minimumNumberUnboundArgs;
        }
        if (this.numberOfRemainingUnboundArguments < minimumNumberUnboundArgs) {
            throw new IllegalStateException("Not enough arguments in method to satisfy binding of returning and throwing variables");
        }
        try {
            int algorithmicStep = 1;
            block11: while (this.numberOfRemainingUnboundArguments > 0 && algorithmicStep < 8) {
                switch (algorithmicStep++) {
                    case 1: {
                        if (this.maybeBindThisJoinPoint()) continue block11;
                        this.maybeBindThisJoinPointStaticPart();
                        continue block11;
                    }
                    case 2: {
                        this.maybeBindThrowingVariable();
                        continue block11;
                    }
                    case 3: {
                        this.maybeBindAnnotationsFromPointcutExpression();
                        continue block11;
                    }
                    case 4: {
                        this.maybeBindReturningVariable();
                        continue block11;
                    }
                    case 5: {
                        this.maybeBindPrimitiveArgsFromPointcutExpression();
                        continue block11;
                    }
                    case 6: {
                        this.maybeBindThisOrTargetOrArgsFromPointcutExpression();
                        continue block11;
                    }
                    case 7: {
                        this.maybeBindReferencePointcutParameter();
                        continue block11;
                    }
                }
                throw new IllegalStateException("Unknown algorithmic step: " + (algorithmicStep - 1));
            }
        }
        catch (IllegalArgumentException | AmbiguousBindingException ex) {
            if (this.raiseExceptions) {
                throw ex;
            }
            return null;
        }
        if (this.numberOfRemainingUnboundArguments == 0) {
            return this.parameterNameBindings;
        }
        if (this.raiseExceptions) {
            throw new IllegalStateException("Failed to bind all argument names: " + this.numberOfRemainingUnboundArguments + " argument(s) could not be bound");
        }
        return null;
    }

    @Override
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        if (this.raiseExceptions) {
            throw new UnsupportedOperationException("An advice method can never be a constructor");
        }
        return null;
    }

    private void bindParameterName(int index, @Nullable String name) {
        this.parameterNameBindings[index] = name;
        --this.numberOfRemainingUnboundArguments;
    }

    private boolean maybeBindThisJoinPoint() {
        if (this.argumentTypes[0] == JoinPoint.class || this.argumentTypes[0] == ProceedingJoinPoint.class) {
            this.bindParameterName(0, THIS_JOIN_POINT);
            return true;
        }
        return false;
    }

    private void maybeBindThisJoinPointStaticPart() {
        if (this.argumentTypes[0] == JoinPoint.StaticPart.class) {
            this.bindParameterName(0, THIS_JOIN_POINT_STATIC_PART);
        }
    }

    private void maybeBindThrowingVariable() {
        if (this.throwingName == null) {
            return;
        }
        int throwableIndex = -1;
        for (int i2 = 0; i2 < this.argumentTypes.length; ++i2) {
            if (!this.isUnbound(i2) || !this.isSubtypeOf(Throwable.class, i2)) continue;
            if (throwableIndex == -1) {
                throwableIndex = i2;
                continue;
            }
            throw new AmbiguousBindingException("Binding of throwing parameter '" + this.throwingName + "' is ambiguous: could be bound to argument " + throwableIndex + " or argument " + i2);
        }
        if (throwableIndex == -1) {
            throw new IllegalStateException("Binding of throwing parameter '" + this.throwingName + "' could not be completed as no available arguments are a subtype of Throwable");
        }
        this.bindParameterName(throwableIndex, this.throwingName);
    }

    private void maybeBindReturningVariable() {
        if (this.numberOfRemainingUnboundArguments == 0) {
            throw new IllegalStateException("Algorithm assumes that there must be at least one unbound parameter on entry to this method");
        }
        if (this.returningName != null) {
            if (this.numberOfRemainingUnboundArguments > 1) {
                throw new AmbiguousBindingException("Binding of returning parameter '" + this.returningName + "' is ambiguous, there are " + this.numberOfRemainingUnboundArguments + " candidates.");
            }
            for (int i2 = 0; i2 < this.parameterNameBindings.length; ++i2) {
                if (this.parameterNameBindings[i2] != null) continue;
                this.bindParameterName(i2, this.returningName);
                break;
            }
        }
    }

    private void maybeBindAnnotationsFromPointcutExpression() {
        ArrayList<String> varNames = new ArrayList<String>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i2 = 0; i2 < tokens.length; ++i2) {
            PointcutBody body2;
            String toMatch = tokens[i2];
            int firstParenIndex = toMatch.indexOf(40);
            if (firstParenIndex != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            }
            if (singleValuedAnnotationPcds.contains(toMatch)) {
                body2 = this.getPointcutBody(tokens, i2);
                i2 += body2.numTokensConsumed;
                String varName = this.maybeExtractVariableName(body2.text);
                if (varName == null) continue;
                varNames.add(varName);
                continue;
            }
            if (!tokens[i2].startsWith("@args(") && !tokens[i2].equals("@args")) continue;
            body2 = this.getPointcutBody(tokens, i2);
            i2 += body2.numTokensConsumed;
            this.maybeExtractVariableNamesFromArgs(body2.text, varNames);
        }
        this.bindAnnotationsFromVarNames(varNames);
    }

    private void bindAnnotationsFromVarNames(List<String> varNames) {
        if (!varNames.isEmpty()) {
            int numAnnotationSlots = this.countNumberOfUnboundAnnotationArguments();
            if (numAnnotationSlots > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " potential annotation variable(s), and " + numAnnotationSlots + " potential argument slots");
            }
            if (numAnnotationSlots == 1) {
                if (varNames.size() == 1) {
                    this.findAndBind(Annotation.class, varNames.get(0));
                } else {
                    throw new IllegalArgumentException("Found " + varNames.size() + " candidate annotation binding variables but only one potential argument binding slot");
                }
            }
        }
    }

    @Nullable
    private String maybeExtractVariableName(@Nullable String candidateToken) {
        if (AspectJProxyUtils.isVariableName(candidateToken)) {
            return candidateToken;
        }
        return null;
    }

    private void maybeExtractVariableNamesFromArgs(@Nullable String argsSpec, List<String> varNames) {
        if (argsSpec == null) {
            return;
        }
        String[] tokens = StringUtils.tokenizeToStringArray(argsSpec, ",");
        for (int i2 = 0; i2 < tokens.length; ++i2) {
            tokens[i2] = StringUtils.trimWhitespace(tokens[i2]);
            String varName = this.maybeExtractVariableName(tokens[i2]);
            if (varName == null) continue;
            varNames.add(varName);
        }
    }

    private void maybeBindThisOrTargetOrArgsFromPointcutExpression() {
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at this(),target(),args() binding stage, with no way to determine between them");
        }
        ArrayList<String> varNames = new ArrayList<String>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i2 = 0; i2 < tokens.length; ++i2) {
            PointcutBody body2;
            if (tokens[i2].equals("this") || tokens[i2].startsWith("this(") || tokens[i2].equals("target") || tokens[i2].startsWith("target(")) {
                body2 = this.getPointcutBody(tokens, i2);
                i2 += body2.numTokensConsumed;
                String varName = this.maybeExtractVariableName(body2.text);
                if (varName == null) continue;
                varNames.add(varName);
                continue;
            }
            if (!tokens[i2].equals("args") && !tokens[i2].startsWith("args(")) continue;
            body2 = this.getPointcutBody(tokens, i2);
            i2 += body2.numTokensConsumed;
            ArrayList<String> candidateVarNames = new ArrayList<String>();
            this.maybeExtractVariableNamesFromArgs(body2.text, candidateVarNames);
            for (String varName : candidateVarNames) {
                if (this.alreadyBound(varName)) continue;
                varNames.add(varName);
            }
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate this(), target() or args() variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; ++j) {
                if (!this.isUnbound(j)) continue;
                this.bindParameterName(j, (String)varNames.get(0));
                break;
            }
        }
    }

    private void maybeBindReferencePointcutParameter() {
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at reference pointcut binding stage, with no way to determine between them");
        }
        ArrayList<String> varNames = new ArrayList<String>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i2 = 0; i2 < tokens.length; ++i2) {
            String varName;
            String nextToken;
            int firstParenIndex;
            String toMatch = tokens[i2];
            if (toMatch.startsWith("!")) {
                toMatch = toMatch.substring(1);
            }
            if ((firstParenIndex = toMatch.indexOf(40)) != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            } else if (tokens.length < i2 + 2 || (nextToken = tokens[i2 + 1]).charAt(0) != '(') continue;
            PointcutBody body2 = this.getPointcutBody(tokens, i2);
            i2 += body2.numTokensConsumed;
            if (nonReferencePointcutTokens.contains(toMatch) || (varName = this.maybeExtractVariableName(body2.text)) == null) continue;
            varNames.add(varName);
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate reference pointcut variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; ++j) {
                if (!this.isUnbound(j)) continue;
                this.bindParameterName(j, (String)varNames.get(0));
                break;
            }
        }
    }

    private PointcutBody getPointcutBody(String[] tokens, int startIndex) {
        int numTokensConsumed = 0;
        String currentToken = tokens[startIndex];
        int bodyStart = currentToken.indexOf(40);
        if (currentToken.charAt(currentToken.length() - 1) == ')') {
            return new PointcutBody(0, currentToken.substring(bodyStart + 1, currentToken.length() - 1));
        }
        StringBuilder sb = new StringBuilder();
        if (bodyStart >= 0 && bodyStart != currentToken.length() - 1) {
            sb.append(currentToken.substring(bodyStart + 1));
            sb.append(' ');
        }
        int currentIndex = startIndex + ++numTokensConsumed;
        while (currentIndex < tokens.length) {
            if (tokens[currentIndex].equals("(")) {
                ++currentIndex;
                continue;
            }
            if (tokens[currentIndex].endsWith(")")) {
                sb.append(tokens[currentIndex], 0, tokens[currentIndex].length() - 1);
                return new PointcutBody(numTokensConsumed, sb.toString().trim());
            }
            String toAppend = tokens[currentIndex];
            if (toAppend.startsWith("(")) {
                toAppend = toAppend.substring(1);
            }
            sb.append(toAppend);
            sb.append(' ');
            ++currentIndex;
            ++numTokensConsumed;
        }
        return new PointcutBody(numTokensConsumed, null);
    }

    private void maybeBindPrimitiveArgsFromPointcutExpression() {
        int numUnboundPrimitives = this.countNumberOfUnboundPrimitiveArguments();
        if (numUnboundPrimitives > 1) {
            throw new AmbiguousBindingException("Found '" + numUnboundPrimitives + "' unbound primitive arguments with no way to distinguish between them.");
        }
        if (numUnboundPrimitives == 1) {
            int i2;
            ArrayList<String> varNames = new ArrayList<String>();
            String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
            for (i2 = 0; i2 < tokens.length; ++i2) {
                if (!tokens[i2].equals("args") && !tokens[i2].startsWith("args(")) continue;
                PointcutBody body2 = this.getPointcutBody(tokens, i2);
                i2 += body2.numTokensConsumed;
                this.maybeExtractVariableNamesFromArgs(body2.text, varNames);
            }
            if (varNames.size() > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " candidate variable names but only one candidate binding slot when matching primitive args");
            }
            if (varNames.size() == 1) {
                for (i2 = 0; i2 < this.argumentTypes.length; ++i2) {
                    if (!this.isUnbound(i2) || !this.argumentTypes[i2].isPrimitive()) continue;
                    this.bindParameterName(i2, (String)varNames.get(0));
                    break;
                }
            }
        }
    }

    private boolean isUnbound(int i2) {
        return this.parameterNameBindings[i2] == null;
    }

    private boolean alreadyBound(String varName) {
        for (int i2 = 0; i2 < this.parameterNameBindings.length; ++i2) {
            if (this.isUnbound(i2) || !varName.equals(this.parameterNameBindings[i2])) continue;
            return true;
        }
        return false;
    }

    private boolean isSubtypeOf(Class<?> supertype, int argumentNumber) {
        return supertype.isAssignableFrom(this.argumentTypes[argumentNumber]);
    }

    private int countNumberOfUnboundAnnotationArguments() {
        int count = 0;
        for (int i2 = 0; i2 < this.argumentTypes.length; ++i2) {
            if (!this.isUnbound(i2) || !this.isSubtypeOf(Annotation.class, i2)) continue;
            ++count;
        }
        return count;
    }

    private int countNumberOfUnboundPrimitiveArguments() {
        int count = 0;
        for (int i2 = 0; i2 < this.argumentTypes.length; ++i2) {
            if (!this.isUnbound(i2) || !this.argumentTypes[i2].isPrimitive()) continue;
            ++count;
        }
        return count;
    }

    private void findAndBind(Class<?> argumentType, String varName) {
        for (int i2 = 0; i2 < this.argumentTypes.length; ++i2) {
            if (!this.isUnbound(i2) || !this.isSubtypeOf(argumentType, i2)) continue;
            this.bindParameterName(i2, varName);
            return;
        }
        throw new IllegalStateException("Expected to find an unbound argument of type '" + argumentType.getName() + "'");
    }

    static {
        singleValuedAnnotationPcds.add("@this");
        singleValuedAnnotationPcds.add("@target");
        singleValuedAnnotationPcds.add("@within");
        singleValuedAnnotationPcds.add("@withincode");
        singleValuedAnnotationPcds.add("@annotation");
        Set pointcutPrimitives = PointcutParser.getAllSupportedPointcutPrimitives();
        for (PointcutPrimitive primitive : pointcutPrimitives) {
            nonReferencePointcutTokens.add(primitive.getName());
        }
        nonReferencePointcutTokens.add("&&");
        nonReferencePointcutTokens.add("!");
        nonReferencePointcutTokens.add("||");
        nonReferencePointcutTokens.add("and");
        nonReferencePointcutTokens.add("or");
        nonReferencePointcutTokens.add("not");
    }

    public static class AmbiguousBindingException
    extends RuntimeException {
        public AmbiguousBindingException(String msg) {
            super(msg);
        }
    }

    private static class PointcutBody {
        private int numTokensConsumed;
        @Nullable
        private String text;

        public PointcutBody(int tokens, @Nullable String text) {
            this.numTokensConsumed = tokens;
            this.text = text;
        }
    }
}

