/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.Mf2DataModel;
import com.ibm.icu.message2.Mf2Parser;
import java.util.ArrayList;
import java.util.List;

class Mf2Serializer
implements Mf2Parser.EventHandler {
    private String input;
    private final List<Token> tokens = new ArrayList<Token>();

    Mf2Serializer() {
    }

    @Override
    public void reset(CharSequence input) {
        this.input = input.toString();
        this.tokens.clear();
    }

    @Override
    public void startNonterminal(String name, int begin) {
        this.tokens.add(new Token(Token.Kind.NONTERMINAL_START, name, begin, -1, this.input));
    }

    @Override
    public void endNonterminal(String name, int end) {
        this.tokens.add(new Token(Token.Kind.NONTERMINAL_END, name, -1, end, this.input));
    }

    @Override
    public void terminal(String name, int begin, int end) {
        this.tokens.add(new Token(Token.Kind.TERMINAL, name, begin, end, this.input));
    }

    @Override
    public void whitespace(int begin, int end) {
    }

    Mf2DataModel build() {
        Token firstToken;
        if (!this.tokens.isEmpty() && Token.Type.MESSAGE.equals((Object)(firstToken = this.tokens.get(0)).type) && firstToken.isStart()) {
            return this.parseMessage();
        }
        return null;
    }

    private Mf2DataModel parseMessage() {
        Mf2DataModel.Builder result = Mf2DataModel.builder();
        block8: for (int i = 0; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case MESSAGE: {
                    if (token.isStart() && i == 0) continue block8;
                    if (token.isEnd() && i == this.tokens.size() - 1) {
                        String leftover;
                        if (token.end != this.input.length() && !(leftover = this.input.substring(token.end).replace("\n", "").replace("\r", "").replace(" ", "").replace("\t", "")).isEmpty()) {
                            throw new IllegalArgumentException("Parse error: Content detected after the end of the message: '" + this.input.substring(token.end) + "'");
                        }
                        return result.build();
                    }
                    throw new IllegalArgumentException("Parse error: Extra tokens at the end of the message");
                }
                case PATTERN: {
                    ParseResult<Mf2DataModel.Pattern> patternResult = this.parsePattern(i);
                    i = patternResult.skipLen;
                    result.setPattern((Mf2DataModel.Pattern)patternResult.resultValue);
                    continue block8;
                }
                case DECLARATION: {
                    Declaration declaration = new Declaration();
                    i = this.parseDeclaration(i, declaration);
                    result.addLocalVariable(declaration.variableName, declaration.expr);
                    continue block8;
                }
                case SELECTOR: {
                    ParseResult<List<Mf2DataModel.Expression>> selectorResult = this.parseSelector(i);
                    result.addSelectors((List)selectorResult.resultValue);
                    i = selectorResult.skipLen;
                    continue block8;
                }
                case VARIANT: {
                    ParseResult<Variant> variantResult = this.parseVariant(i);
                    i = variantResult.skipLen;
                    Variant variant = (Variant)variantResult.resultValue;
                    result.addVariant(variant.getSelectorKeys(), variant.getPattern());
                    continue block8;
                }
                case IGNORE: {
                    continue block8;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseMessage UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing MessageFormatter");
    }

    private ParseResult<Variant> parseVariant(int startToken) {
        Variant.Builder result = Variant.builder();
        block9: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case VARIANT: {
                    if (token.isStart() || !token.isEnd()) continue block9;
                    return new ParseResult<Variant>(i, result.build());
                }
                case LITERAL: {
                    result.addSelectorKey(this.input.substring(token.begin + 1, token.end - 1));
                    continue block9;
                }
                case NMTOKEN: {
                    result.addSelectorKey(this.input.substring(token.begin, token.end));
                    continue block9;
                }
                case DEFAULT: {
                    result.addSelectorKey("*");
                    continue block9;
                }
                case PATTERN: {
                    ParseResult<Mf2DataModel.Pattern> patternResult = this.parsePattern(i);
                    i = patternResult.skipLen;
                    result.setPattern((Mf2DataModel.Pattern)patternResult.resultValue);
                    continue block9;
                }
                case VARIANTKEY: {
                    continue block9;
                }
                case IGNORE: {
                    continue block9;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseVariant UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing Variant");
    }

    private ParseResult<List<Mf2DataModel.Expression>> parseSelector(int startToken) {
        ArrayList result = new ArrayList();
        block5: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case SELECTOR: {
                    if (token.isStart() || !token.isEnd()) continue block5;
                    return new ParseResult<List<Mf2DataModel.Expression>>(i, result);
                }
                case EXPRESSION: {
                    ParseResult<Mf2DataModel.Expression> exprResult = this.parseExpression(i);
                    i = exprResult.skipLen;
                    result.add(exprResult.resultValue);
                    continue block5;
                }
                case IGNORE: {
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseSelector UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing selectors");
    }

    private int parseDeclaration(int startToken, Declaration declaration) {
        block6: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case DECLARATION: {
                    if (token.isStart() || !token.isEnd()) continue block6;
                    return i;
                }
                case VARIABLE: {
                    declaration.variableName = this.input.substring(token.begin + 1, token.end);
                    continue block6;
                }
                case EXPRESSION: {
                    ParseResult<Mf2DataModel.Expression> exprResult = this.parseExpression(i);
                    i = exprResult.skipLen;
                    declaration.expr = (Mf2DataModel.Expression)exprResult.resultValue;
                    continue block6;
                }
                case IGNORE: {
                    continue block6;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseDeclaration UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing Declaration");
    }

    private ParseResult<Mf2DataModel.Pattern> parsePattern(int startToken) {
        Mf2DataModel.Pattern.Builder result = Mf2DataModel.Pattern.builder();
        block7: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case TEXT: {
                    Mf2DataModel.Text text = new Mf2DataModel.Text(this.input.substring(token.begin, token.end));
                    result.add(text);
                    continue block7;
                }
                case PLACEHOLDER: {
                    continue block7;
                }
                case EXPRESSION: {
                    ParseResult<Mf2DataModel.Expression> exprResult = this.parseExpression(i);
                    i = exprResult.skipLen;
                    result.add((Mf2DataModel.Part)exprResult.resultValue);
                    continue block7;
                }
                case IGNORE: 
                case VARIABLE: {
                    continue block7;
                }
                case PATTERN: {
                    if (token.isStart() && i == startToken || !token.isEnd()) continue block7;
                    return new ParseResult<Mf2DataModel.Pattern>(i, result.build());
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parsePattern UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing Pattern");
    }

    private ParseResult<Mf2DataModel.Expression> parseExpression(int startToken) {
        Mf2DataModel.Expression.Builder result = Mf2DataModel.Expression.builder();
        block9: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case EXPRESSION: 
                case PLACEHOLDER: {
                    if (token.isStart() && i == startToken || !token.isEnd()) continue block9;
                    return new ParseResult<Mf2DataModel.Expression>(i, result.build());
                }
                case FUNCTION: {
                    result.setFunctionName(this.input.substring(token.begin + 1, token.end));
                    continue block9;
                }
                case LITERAL: {
                    result.setOperand(Mf2DataModel.Value.builder().setLiteral(this.input.substring(token.begin + 1, token.end - 1)).build());
                    continue block9;
                }
                case VARIABLE: {
                    result.setOperand(Mf2DataModel.Value.builder().setVariableName(this.input.substring(token.begin + 1, token.end)).build());
                    continue block9;
                }
                case OPTION: {
                    Option option = new Option();
                    i = this.parseOptions(i, option);
                    result.addOption(option.name, option.value);
                    continue block9;
                }
                case OPERAND: {
                    continue block9;
                }
                case IGNORE: {
                    continue block9;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseExpression UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing Expression");
    }

    private int parseOptions(int startToken, Option option) {
        block8: for (int i = startToken; i < this.tokens.size(); ++i) {
            Token token = this.tokens.get(i);
            switch (token.type) {
                case OPTION: {
                    if (token.isStart() && i == startToken || !token.isEnd()) continue block8;
                    return i;
                }
                case NAME: {
                    option.name = this.input.substring(token.begin, token.end);
                    continue block8;
                }
                case LITERAL: {
                    option.value = Mf2DataModel.Value.builder().setLiteral(this.input.substring(token.begin + 1, token.end - 1)).build();
                    continue block8;
                }
                case NMTOKEN: {
                    option.value = Mf2DataModel.Value.builder().setLiteral(this.input.substring(token.begin, token.end)).build();
                    continue block8;
                }
                case VARIABLE: {
                    option.value = Mf2DataModel.Value.builder().setVariableName(this.input.substring(token.begin + 1, token.end)).build();
                    continue block8;
                }
                case IGNORE: {
                    continue block8;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: parseOptions UNEXPECTED TOKEN: '" + token + "'");
                }
            }
        }
        throw new IllegalArgumentException("Parse error: Error parsing Option");
    }

    static String dataModelToString(Mf2DataModel dataModel) {
        return dataModel.toString();
    }

    static class ParseResult<T> {
        final int skipLen;
        final T resultValue;

        public ParseResult(int skipLen, T resultValue) {
            this.skipLen = skipLen;
            this.resultValue = resultValue;
        }
    }

    static class Variant {
        private final Mf2DataModel.SelectorKeys selectorKeys;
        private final Mf2DataModel.Pattern pattern;

        private Variant(Builder builder) {
            this.selectorKeys = builder.selectorKeys.build();
            this.pattern = builder.pattern;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Mf2DataModel.SelectorKeys getSelectorKeys() {
            return this.selectorKeys;
        }

        public Mf2DataModel.Pattern getPattern() {
            return this.pattern;
        }

        public static class Builder {
            private final Mf2DataModel.SelectorKeys.Builder selectorKeys = Mf2DataModel.SelectorKeys.builder();
            private Mf2DataModel.Pattern pattern = Mf2DataModel.Pattern.builder().build();

            private Builder() {
            }

            public Builder setSelectorKeys(Mf2DataModel.SelectorKeys selectorKeys) {
                this.selectorKeys.addAll(selectorKeys.getKeys());
                return this;
            }

            public Builder addSelectorKey(String selectorKey) {
                this.selectorKeys.add(selectorKey);
                return this;
            }

            public Builder setPattern(Mf2DataModel.Pattern pattern) {
                this.pattern = pattern;
                return this;
            }

            public Variant build() {
                return new Variant(this);
            }
        }
    }

    static class Declaration {
        String variableName;
        Mf2DataModel.Expression expr;

        Declaration() {
        }
    }

    static class Option {
        String name;
        Mf2DataModel.Value value;

        Option() {
        }
    }

    static class Token {
        final String name;
        final int begin;
        final int end;
        final Kind kind;
        private final Type type;
        private final String input;

        Token(Kind kind, String name, int begin, int end, String input) {
            this.kind = kind;
            this.name = name;
            this.begin = begin;
            this.end = end;
            this.input = input;
            switch (name) {
                case "Message": {
                    this.type = Type.MESSAGE;
                    break;
                }
                case "Pattern": {
                    this.type = Type.PATTERN;
                    break;
                }
                case "Text": {
                    this.type = Type.TEXT;
                    break;
                }
                case "Placeholder": {
                    this.type = Type.PLACEHOLDER;
                    break;
                }
                case "Expression": {
                    this.type = Type.EXPRESSION;
                    break;
                }
                case "Operand": {
                    this.type = Type.OPERAND;
                    break;
                }
                case "Variable": {
                    this.type = Type.VARIABLE;
                    break;
                }
                case "Function": {
                    this.type = Type.FUNCTION;
                    break;
                }
                case "Option": {
                    this.type = Type.OPTION;
                    break;
                }
                case "Annotation": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "Name": {
                    this.type = Type.NAME;
                    break;
                }
                case "Nmtoken": {
                    this.type = Type.NMTOKEN;
                    break;
                }
                case "Literal": {
                    this.type = Type.LITERAL;
                    break;
                }
                case "Selector": {
                    this.type = Type.SELECTOR;
                    break;
                }
                case "Variant": {
                    this.type = Type.VARIANT;
                    break;
                }
                case "VariantKey": {
                    this.type = Type.VARIANTKEY;
                    break;
                }
                case "Declaration": {
                    this.type = Type.DECLARATION;
                    break;
                }
                case "Markup": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "MarkupStart": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "MarkupEnd": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'['": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "']'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'{'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'}'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'='": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'match'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'when'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'let'": {
                    this.type = Type.IGNORE;
                    break;
                }
                case "'*'": {
                    this.type = Type.DEFAULT;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Parse error: Unknown token \"" + name + "\"");
                }
            }
        }

        boolean isStart() {
            return Kind.NONTERMINAL_START.equals((Object)this.kind);
        }

        boolean isEnd() {
            return Kind.NONTERMINAL_END.equals((Object)this.kind);
        }

        boolean isTerminal() {
            return Kind.TERMINAL.equals((Object)this.kind);
        }

        public String toString() {
            int from = this.begin == -1 ? 0 : this.begin;
            String strval = this.end == -1 ? this.input.substring(from) : this.input.substring(from, this.end);
            return String.format("Token(\"%s\", [%d, %d], %s) // \"%s\"", new Object[]{this.name, this.begin, this.end, this.kind, strval});
        }

        static enum Type {
            MESSAGE,
            PATTERN,
            TEXT,
            PLACEHOLDER,
            EXPRESSION,
            OPERAND,
            VARIABLE,
            IGNORE,
            FUNCTION,
            OPTION,
            NAME,
            NMTOKEN,
            LITERAL,
            SELECTOR,
            VARIANT,
            DECLARATION,
            VARIANTKEY,
            DEFAULT;

        }

        static enum Kind {
            TERMINAL,
            NONTERMINAL_START,
            NONTERMINAL_END;

        }
    }
}

