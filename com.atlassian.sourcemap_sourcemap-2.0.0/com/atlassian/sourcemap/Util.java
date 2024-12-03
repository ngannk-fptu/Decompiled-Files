/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.SourceMapJoiner;
import com.atlassian.sourcemap.WritableSourceMap;
import com.atlassian.sourcemap.WritableSourceMapImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Util {
    public static Set<String> JS_TYPES_AND_CONTENT_TYPES = new HashSet<String>(Arrays.asList("js", "text/javascript", "application/javascript", "application/x-javascript"));
    public static Set<String> CSS_TYPES_AND_CONTENT_TYPES = new HashSet<String>(Arrays.asList("css", "text/css"));

    public static boolean isSourceMapSupportedBy(String typeOrContentType) {
        return JS_TYPES_AND_CONTENT_TYPES.contains(typeOrContentType) || CSS_TYPES_AND_CONTENT_TYPES.contains(typeOrContentType);
    }

    public static String generateSourceMapComment(String sourceMapUrl, String typeOrContentType) {
        if (JS_TYPES_AND_CONTENT_TYPES.contains(typeOrContentType)) {
            return "//# sourceMappingURL=" + sourceMapUrl;
        }
        if (CSS_TYPES_AND_CONTENT_TYPES.contains(typeOrContentType)) {
            return "/*# sourceMappingURL=" + sourceMapUrl + " */";
        }
        throw new RuntimeException("source map not supported for " + typeOrContentType);
    }

    public static WritableSourceMap create1to1SourceMap(CharSequence source, String sourceUrl) {
        return Util.create1to1SourceMap(Util.countLines(source), sourceUrl);
    }

    public static WritableSourceMap create1to1SourceMap(int linesCount, String sourceUrl) {
        WritableSourceMap map = new WritableSourceMapImpl.Builder().withSources(Collections.singletonList(sourceUrl)).build();
        for (int i = 0; i < linesCount; ++i) {
            map.addMapping(i, 0, i, 0, sourceUrl);
        }
        return map;
    }

    public static int countLines(InputStream stream) {
        try {
            int c = stream.read();
            int counter = 0;
            while (c != -1) {
                if (c == 10) {
                    ++counter;
                }
                c = stream.read();
            }
            return counter + 1;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countLines(CharSequence stream) {
        int counter = 0;
        for (int i = 0; i < stream.length(); ++i) {
            if (counter == 0) {
                ++counter;
            }
            if (stream.charAt(i) != '\n') continue;
            ++counter;
        }
        return counter;
    }

    public static WritableSourceMap rebase(ReadableSourceMap sourceMap, ReadableSourceMap previousSourceMap) {
        WritableSourceMap rebasedMap = new WritableSourceMapImpl.Builder().withSourcesAndSourcesContent(sourceMap.getSources(), sourceMap.getSourcesContent()).build();
        sourceMap.eachMapping(mapping -> {
            Mapping rebasedMapping = previousSourceMap.getMapping(mapping.getSourceLine(), mapping.getSourceColumn());
            if (rebasedMapping != null) {
                rebasedMap.addMapping(mapping.getGeneratedLine(), mapping.getGeneratedColumn(), rebasedMapping.getSourceLine(), rebasedMapping.getSourceColumn(), rebasedMapping.getSourceFileName(), rebasedMapping.getSourceSymbolName());
            }
        });
        return rebasedMap;
    }

    public static SourceMapJoiner joiner() {
        return new SourceMapJoiner();
    }
}

