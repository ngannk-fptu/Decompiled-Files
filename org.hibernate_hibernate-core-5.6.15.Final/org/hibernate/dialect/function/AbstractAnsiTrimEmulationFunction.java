/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public abstract class AbstractAnsiTrimEmulationFunction
implements SQLFunction {
    @Override
    public final boolean hasArguments() {
        return true;
    }

    @Override
    public final boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public final Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
        return StandardBasicTypes.STRING;
    }

    @Override
    public final String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        String trimSource;
        String trimCharacter;
        if (args.size() == 1) {
            return this.resolveBothSpaceTrimFunction().render(argumentType, args, factory);
        }
        if ("from".equalsIgnoreCase((String)args.get(0))) {
            return this.resolveBothSpaceTrimFromFunction().render(argumentType, args, factory);
        }
        boolean leading = true;
        boolean trailing = true;
        int potentialTrimCharacterArgIndex = 1;
        String firstArg = (String)args.get(0);
        if ("leading".equalsIgnoreCase(firstArg)) {
            trailing = false;
        } else if ("trailing".equalsIgnoreCase(firstArg)) {
            leading = false;
        } else if (!"both".equalsIgnoreCase(firstArg)) {
            potentialTrimCharacterArgIndex = 0;
        }
        String potentialTrimCharacter = (String)args.get(potentialTrimCharacterArgIndex);
        if ("from".equalsIgnoreCase(potentialTrimCharacter)) {
            trimCharacter = "' '";
            trimSource = (String)args.get(potentialTrimCharacterArgIndex + 1);
        } else if (potentialTrimCharacterArgIndex + 1 >= args.size()) {
            trimCharacter = "' '";
            trimSource = potentialTrimCharacter;
        } else {
            trimCharacter = potentialTrimCharacter;
            trimSource = "from".equalsIgnoreCase((String)args.get(potentialTrimCharacterArgIndex + 1)) ? (String)args.get(potentialTrimCharacterArgIndex + 2) : (String)args.get(potentialTrimCharacterArgIndex + 1);
        }
        ArrayList<String> argsToUse = new ArrayList<String>();
        argsToUse.add(trimSource);
        argsToUse.add(trimCharacter);
        if (trimCharacter.equals("' '")) {
            if (leading && trailing) {
                return this.resolveBothSpaceTrimFunction().render(argumentType, argsToUse, factory);
            }
            if (leading) {
                return this.resolveLeadingSpaceTrimFunction().render(argumentType, argsToUse, factory);
            }
            return this.resolveTrailingSpaceTrimFunction().render(argumentType, argsToUse, factory);
        }
        if (leading && trailing) {
            return this.resolveBothTrimFunction().render(argumentType, argsToUse, factory);
        }
        if (leading) {
            return this.resolveLeadingTrimFunction().render(argumentType, argsToUse, factory);
        }
        return this.resolveTrailingTrimFunction().render(argumentType, argsToUse, factory);
    }

    protected abstract SQLFunction resolveBothSpaceTrimFunction();

    protected abstract SQLFunction resolveBothSpaceTrimFromFunction();

    protected abstract SQLFunction resolveLeadingSpaceTrimFunction();

    protected abstract SQLFunction resolveTrailingSpaceTrimFunction();

    protected abstract SQLFunction resolveBothTrimFunction();

    protected abstract SQLFunction resolveLeadingTrimFunction();

    protected abstract SQLFunction resolveTrailingTrimFunction();
}

