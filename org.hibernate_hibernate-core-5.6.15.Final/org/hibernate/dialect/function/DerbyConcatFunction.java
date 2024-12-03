/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.Iterator;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class DerbyConcatFunction
implements SQLFunction {
    private static final StringTransformer CAST_STRING_TRANSFORMER = new StringTransformer(){

        @Override
        public String transform(String string) {
            return "cast( ? as varchar(32672) )";
        }
    };
    private static final StringTransformer NO_TRANSFORM_STRING_TRANSFORMER = new StringTransformer(){

        @Override
        public String transform(String string) {
            return string;
        }
    };

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
        return StandardBasicTypes.STRING;
    }

    @Override
    public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        boolean areAllArgumentsDynamic = true;
        for (Object arg1 : args) {
            String arg = (String)arg1;
            if ("?".equals(arg)) continue;
            areAllArgumentsDynamic = false;
            break;
        }
        if (areAllArgumentsDynamic) {
            return DerbyConcatFunction.join(args.iterator(), CAST_STRING_TRANSFORMER, new StringJoinTemplate(){

                @Override
                public String getBeginning() {
                    return "varchar( ";
                }

                @Override
                public String getSeparator() {
                    return " || ";
                }

                @Override
                public String getEnding() {
                    return " )";
                }
            });
        }
        return DerbyConcatFunction.join(args.iterator(), NO_TRANSFORM_STRING_TRANSFORMER, new StringJoinTemplate(){

            @Override
            public String getBeginning() {
                return "(";
            }

            @Override
            public String getSeparator() {
                return "||";
            }

            @Override
            public String getEnding() {
                return ")";
            }
        });
    }

    private static String join(Iterator elements, StringTransformer elementTransformer, StringJoinTemplate template) {
        StringBuilder buffer = new StringBuilder(template.getBeginning());
        while (elements.hasNext()) {
            String element = (String)elements.next();
            buffer.append(elementTransformer.transform(element));
            if (!elements.hasNext()) continue;
            buffer.append(template.getSeparator());
        }
        return buffer.append(template.getEnding()).toString();
    }

    private static interface StringJoinTemplate {
        public String getBeginning();

        public String getSeparator();

        public String getEnding();
    }

    private static interface StringTransformer {
        public String transform(String var1);
    }
}

