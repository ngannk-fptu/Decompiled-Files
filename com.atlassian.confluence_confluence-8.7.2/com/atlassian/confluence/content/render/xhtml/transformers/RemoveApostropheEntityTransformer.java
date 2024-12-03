/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveApostropheEntityTransformer
implements Transformer {
    private static final Pattern APOS_ENTITY_PATTERN = Pattern.compile("\\&apos;");

    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        BufferedReader reader = new BufferedReader(input);
        StringBuilder result = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = APOS_ENTITY_PATTERN.matcher(line);
                result.append(matcher.replaceAll("'")).append("\n");
                line = reader.readLine();
            }
        }
        catch (IOException ex) {
            throw new XhtmlException("Could not read the input to remove apostrophes from.");
        }
        if (result.length() > 0 && result.charAt(result.length() - 1) == '\n') {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
}

