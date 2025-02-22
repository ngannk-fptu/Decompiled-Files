/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.jssrc.internal;

import com.google.template.soy.shared.internal.AbstractGenerateSoyEscapingDirectiveCode;
import com.google.template.soy.shared.internal.DirectiveDigest;
import com.google.template.soy.shared.restricted.EscapingConventions;
import com.google.template.soy.shared.restricted.TagWhitelist;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class GenerateSoyUtilsEscapingDirectiveCode
extends AbstractGenerateSoyEscapingDirectiveCode {
    private static final Pattern NAMED_CLASS = Pattern.compile("(?<!\\\\)(\\\\{2})*\\\\p\\{");

    @Override
    protected EscapingConventions.EscapingLanguage getLanguage() {
        return EscapingConventions.EscapingLanguage.JAVASCRIPT;
    }

    @Override
    protected String getLineCommentSyntax() {
        return "//";
    }

    @Override
    protected String getLineEndSyntax() {
        return ";";
    }

    @Override
    protected String getRegexStart() {
        return "/";
    }

    @Override
    protected String getRegexEnd() {
        return "/g";
    }

    @Override
    protected String convertFromJavaRegex(Pattern javaPattern) {
        String body = javaPattern.pattern().replace("\r", "\\r").replace("\n", "\\n").replace("\u2028", "\\u2028").replace("\u2029", "\\u2029").replace("\\A", "^").replace("\\z", "$").replaceAll("(?<!\\\\)(?:\\\\{2})*/", "\\\\/");
        if (body.contains("(?<")) {
            throw new IllegalArgumentException("Pattern " + javaPattern + " uses lookbehind.");
        }
        if ((javaPattern.flags() & 0x20) != 0) {
            throw new IllegalArgumentException("Pattern " + javaPattern + " uses DOTALL.");
        }
        if (NAMED_CLASS.matcher(body).find()) {
            throw new IllegalArgumentException("Pattern " + javaPattern + " uses named characer classes.");
        }
        StringBuilder buffer = new StringBuilder(body.length() + 4);
        buffer.append('/').append(body).append('/');
        if ((javaPattern.flags() & 2) != 0) {
            buffer.append('i');
        }
        if ((javaPattern.flags() & 8) != 0) {
            buffer.append('m');
        }
        return buffer.toString();
    }

    @Override
    protected void generateCharacterMapSignature(StringBuilder outputCode, String mapName) {
        outputCode.append('\n').append("/**\n").append(" * Maps characters to the escaped versions for the named escape directives.\n").append(" * @type {Object.<string, string>}\n").append(" * @private\n").append(" */\n").append("soy.esc.$$ESCAPE_MAP_FOR_").append(mapName).append("_");
    }

    @Override
    protected void generateMatcher(StringBuilder outputCode, String name, String matcher) {
        outputCode.append('\n').append("/**\n").append(" * Matches characters that need to be escaped for the named directives.\n").append(" * @type RegExp\n").append(" * @private\n").append(" */\n").append("soy.esc.$$MATCHER_FOR_").append(name).append("_ = ").append(matcher).append(";\n");
    }

    @Override
    protected void generateFilter(StringBuilder outputCode, String name, String filter) {
        outputCode.append('\n').append("/**\n").append(" * A pattern that vets values produced by the named directives.\n").append(" * @type RegExp\n").append(" * @private\n").append(" */\n").append("soy.esc.$$FILTER_FOR_").append(name).append("_ = ").append(filter).append(";\n");
    }

    @Override
    protected void generateCommonConstants(StringBuilder outputCode) {
        outputCode.append('\n').append("/**\n").append(" * Matches all tags, HTML comments, and DOCTYPEs in tag soup HTML.\n").append(" * By removing these, and replacing any '<' or '>' characters with\n").append(" * entities we guarantee that the result can be embedded into a\n").append(" * an attribute without introducing a tag boundary.\n").append(" *\n").append(" * @type {RegExp}\n").append(" * @private\n").append(" */\n").append("soy.esc.$$HTML_TAG_REGEX_ = ").append(this.convertFromJavaRegex(EscapingConventions.HTML_TAG_CONTENT)).append("g;\n").append("\n").append("/**\n").append(" * Matches all occurrences of '<'.\n").append(" *\n").append(" * @type {RegExp}\n").append(" * @private\n").append(" */\n").append("soy.esc.$$LT_REGEX_ = /</g;\n");
        outputCode.append('\n').append("/**\n").append(" * Maps lower-case names of innocuous tags to 1.\n").append(" *\n").append(" * @type {Object.<string,number>}\n").append(" * @private\n").append(" */\n").append("soy.esc.$$SAFE_TAG_WHITELIST_ = ").append(GenerateSoyUtilsEscapingDirectiveCode.toJsStringSet(TagWhitelist.FORMATTING.asSet())).append(";\n");
    }

    @Override
    protected void generateReplacerFunction(StringBuilder outputCode, String mapName) {
        outputCode.append('\n').append("/**\n").append(" * A function that can be used with String.replace.\n").append(" * @param {string} ch A single character matched by a compatible matcher.\n").append(" * @return {string} A token in the output language.\n").append(" * @private\n").append(" */\n").append("soy.esc.$$REPLACER_FOR_").append(mapName).append("_ = function(ch) {\n").append("  return soy.esc.$$ESCAPE_MAP_FOR_").append(mapName).append("_[ch];\n").append("};\n");
    }

    @Override
    protected void useExistingLibraryFunction(StringBuilder outputCode, String identifier, String existingFunction) {
        outputCode.append('\n').append("/**\n").append(" * @type {function (*) : string}\n").append(" */\n").append("soy.esc.$$").append(identifier).append("Helper = function(v) {\n").append("  return ").append(existingFunction).append("(String(v));\n").append("};\n");
    }

    @Override
    protected void generateHelperFunction(StringBuilder outputCode, DirectiveDigest digest) {
        String name = digest.getDirectiveName();
        outputCode.append('\n').append("/**\n").append(" * A helper for the Soy directive |").append(name).append('\n').append(" * @param {*} value Can be of any type but will be coerced to a string.\n").append(" * @return {string} The escaped text.\n").append(" */\n").append("soy.esc.$$").append(name).append("Helper = function(value) {\n").append("  var str = String(value);\n");
        if (digest.getFilterName() != null) {
            String filterName = digest.getFilterName();
            outputCode.append("  if (!soy.esc.$$FILTER_FOR_").append(filterName).append("_.test(str)) {\n");
            if (this.availableIdentifiers.apply((Object)"goog.asserts.fail")) {
                outputCode.append("    goog.asserts.fail('Bad value `%s` for |").append(name).append("', [str]);\n");
            }
            outputCode.append("    return '").append(digest.getInnocuousOutput()).append("';\n").append("  }\n");
        }
        if (digest.getNonAsciiPrefix() != null) {
            throw new UnsupportedOperationException("Non ASCII prefix escapers not implemented yet.");
        }
        if (digest.getEscapesName() != null) {
            String escapeMapName = digest.getEscapesName();
            String matcherName = digest.getMatcherName();
            outputCode.append("  return str.replace(\n").append("      soy.esc.$$MATCHER_FOR_").append(matcherName).append("_,\n").append("      soy.esc.$$REPLACER_FOR_").append(escapeMapName).append("_);\n");
        } else {
            outputCode.append("  return str;\n");
        }
        outputCode.append("};\n");
    }

    private static String toJsStringSet(Iterable<? extends String> strings) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        sb.append('{');
        for (String string : strings) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            GenerateSoyUtilsEscapingDirectiveCode.writeStringLiteral(string, sb);
            sb.append(": 1");
        }
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        GenerateSoyUtilsEscapingDirectiveCode generator = new GenerateSoyUtilsEscapingDirectiveCode();
        generator.configure(args);
        generator.execute();
    }
}

