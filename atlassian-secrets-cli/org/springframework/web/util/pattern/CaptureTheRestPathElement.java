/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import java.util.List;
import org.springframework.http.server.PathContainer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class CaptureTheRestPathElement
extends PathElement {
    private final String variableName;

    CaptureTheRestPathElement(int pos, char[] captureDescriptor, char separator) {
        super(pos, separator);
        this.variableName = new String(captureDescriptor, 2, captureDescriptor.length - 3);
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
        }
        if (matchingContext.extractingVariables) {
            LinkedMultiValueMap<String, String> parametersCollector = null;
            for (int i = pathIndex; i < matchingContext.pathLength; ++i) {
                MultiValueMap<String, String> parameters;
                PathContainer.Element element = matchingContext.pathElements.get(i);
                if (!(element instanceof PathContainer.PathSegment) || (parameters = ((PathContainer.PathSegment)element).parameters()).isEmpty()) continue;
                if (parametersCollector == null) {
                    parametersCollector = new LinkedMultiValueMap<String, String>();
                }
                parametersCollector.addAll(parameters);
            }
            matchingContext.set(this.variableName, this.pathToString(pathIndex, matchingContext.pathElements), (MultiValueMap<String, String>)(parametersCollector == null ? NO_PARAMETERS : parametersCollector));
        }
        return true;
    }

    private String pathToString(int fromSegment, List<PathContainer.Element> pathElements) {
        StringBuilder buf = new StringBuilder();
        int max = pathElements.size();
        for (int i = fromSegment; i < max; ++i) {
            PathContainer.Element element = pathElements.get(i);
            if (element instanceof PathContainer.PathSegment) {
                buf.append(((PathContainer.PathSegment)element).valueToMatch());
                continue;
            }
            buf.append(element.value());
        }
        return buf.toString();
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public int getWildcardCount() {
        return 0;
    }

    @Override
    public int getCaptureCount() {
        return 1;
    }

    public String toString() {
        return "CaptureTheRest(/{*" + this.variableName + "})";
    }

    @Override
    public char[] getChars() {
        return ("/{*" + this.variableName + "}").toCharArray();
    }
}

