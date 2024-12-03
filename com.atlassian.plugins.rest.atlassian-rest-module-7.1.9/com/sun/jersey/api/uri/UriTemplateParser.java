/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UriTemplateParser {
    static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Set<Character> RESERVED_REGEX_CHARACTERS = UriTemplateParser.initReserved();
    private static final Pattern TEMPLATE_VALUE_PATTERN = Pattern.compile("[^/]+?");
    private final String template;
    private final StringBuffer regex = new StringBuffer();
    private final StringBuffer normalizedTemplate = new StringBuffer();
    private final StringBuffer literalCharactersBuffer = new StringBuffer();
    private final Pattern pattern;
    private final List<String> names = new ArrayList<String>();
    private final List<Integer> groupCounts = new ArrayList<Integer>();
    private final Map<String, Pattern> nameToPattern = new HashMap<String, Pattern>();
    private int numOfExplicitRegexes;
    private int literalCharacters;

    private static Set<Character> initReserved() {
        char[] reserved = new char[]{'.', '^', '&', '!', '?', '-', ':', '<', '(', '[', '$', '=', ')', ']', ',', '>', '*', '+', '|'};
        HashSet<Character> s = new HashSet<Character>(reserved.length);
        for (char c : reserved) {
            s.add(Character.valueOf(c));
        }
        return s;
    }

    public UriTemplateParser(String template) throws IllegalArgumentException {
        if (template == null || template.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.template = template;
        this.parse(new StringCharacterIterator(template));
        try {
            this.pattern = Pattern.compile(this.regex.toString());
        }
        catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template expression '" + this.regex + "'", ex);
        }
    }

    public final String getTemplate() {
        return this.template;
    }

    public final Pattern getPattern() {
        return this.pattern;
    }

    public final String getNormalizedTemplate() {
        return this.normalizedTemplate.toString();
    }

    public final Map<String, Pattern> getNameToPattern() {
        return this.nameToPattern;
    }

    public final List<String> getNames() {
        return this.names;
    }

    public final List<Integer> getGroupCounts() {
        return this.groupCounts;
    }

    public final int[] getGroupIndexes() {
        int i;
        if (this.names.isEmpty()) {
            return EMPTY_INT_ARRAY;
        }
        int[] indexes = new int[this.names.size() + 1];
        indexes[0] = 1;
        for (i = 1; i < indexes.length; ++i) {
            indexes[i] = indexes[i - 1] + this.groupCounts.get(i - 1);
        }
        for (i = 0; i < indexes.length; ++i) {
            if (indexes[i] == i + 1) continue;
            return indexes;
        }
        return EMPTY_INT_ARRAY;
    }

    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }

    public final int getNumberOfLiteralCharacters() {
        return this.literalCharacters;
    }

    protected String encodeLiteralCharacters(String characters) {
        return characters;
    }

    private void parse(CharacterIterator ci) {
        try {
            while (ci.hasNext()) {
                char c = ci.next();
                if (c == '{') {
                    this.processLiteralCharacters();
                    this.parseName(ci);
                    continue;
                }
                this.literalCharactersBuffer.append(c);
            }
            this.processLiteralCharacters();
        }
        catch (NoSuchElementException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template, \"" + this.template + "\". Check if a path parameter is terminated with a '}'.", ex);
        }
    }

    private void processLiteralCharacters() {
        if (this.literalCharactersBuffer.length() > 0) {
            this.literalCharacters += this.literalCharactersBuffer.length();
            String s = this.encodeLiteralCharacters(this.literalCharactersBuffer.toString());
            this.normalizedTemplate.append(s);
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (RESERVED_REGEX_CHARACTERS.contains(Character.valueOf(c))) {
                    this.regex.append("\\");
                }
                this.regex.append(c);
            }
            this.literalCharactersBuffer.setLength(0);
        }
    }

    private void parseName(CharacterIterator ci) {
        char c = this.consumeWhiteSpace(ci);
        StringBuilder nameBuffer = new StringBuilder();
        if (!Character.isLetterOrDigit(c) && c != '_') {
            throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not as the start of a name");
        }
        nameBuffer.append(c);
        String nameRegexString = "";
        while (Character.isLetterOrDigit(c = ci.next()) || c == '_' || c == '-' || c == '.') {
            nameBuffer.append(c);
        }
        if (c == ':') {
            nameRegexString = this.parseRegex(ci);
        } else if (c != '}') {
            if (c == ' ') {
                c = this.consumeWhiteSpace(ci);
                if (c == ':') {
                    nameRegexString = this.parseRegex(ci);
                } else if (c != '}') {
                    throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not allowed after a name");
                }
            } else {
                throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not allowed as part of a name");
            }
        }
        String name = nameBuffer.toString();
        this.names.add(name);
        try {
            Pattern namePattern;
            if (nameRegexString.length() > 0) {
                ++this.numOfExplicitRegexes;
            }
            Pattern pattern = namePattern = nameRegexString.length() == 0 ? TEMPLATE_VALUE_PATTERN : Pattern.compile(nameRegexString);
            if (this.nameToPattern.containsKey(name)) {
                if (!this.nameToPattern.get(name).equals(namePattern)) {
                    throw new IllegalArgumentException("The name '" + name + "' is declared more than once with different regular expressions");
                }
            } else {
                this.nameToPattern.put(name, namePattern);
            }
            Matcher m = namePattern.matcher("");
            int g = m.groupCount();
            this.groupCounts.add(g + 1);
            this.regex.append('(').append(namePattern).append(')');
            this.normalizedTemplate.append('{').append(name).append('}');
        }
        catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the expression '" + nameRegexString + "' associated with the name '" + name + "'", ex);
        }
    }

    private String parseRegex(CharacterIterator ci) {
        StringBuilder regexBuffer = new StringBuilder();
        int braceCount = 1;
        while (true) {
            char c;
            if ((c = ci.next()) == '{') {
                ++braceCount;
            } else if (c == '}' && --braceCount == 0) break;
            regexBuffer.append(c);
        }
        return regexBuffer.toString().trim();
    }

    private char consumeWhiteSpace(CharacterIterator ci) {
        char c;
        while (Character.isWhitespace(c = ci.next())) {
        }
        return c;
    }

    private static final class StringCharacterIterator
    implements CharacterIterator {
        private int pos;
        private String s;

        public StringCharacterIterator(String s) {
            this.s = s;
            this.pos = 0;
        }

        @Override
        public boolean hasNext() {
            return this.pos < this.s.length();
        }

        @Override
        public char next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.s.charAt(this.pos++);
        }

        @Override
        public char peek() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.s.charAt(this.pos);
        }

        @Override
        public int pos() {
            if (this.pos == 0) {
                throw new IllegalStateException("Iterator not used yet.");
            }
            return this.pos - 1;
        }
    }

    private static interface CharacterIterator {
        public boolean hasNext();

        public char next();

        public char peek();

        public int pos();
    }
}

