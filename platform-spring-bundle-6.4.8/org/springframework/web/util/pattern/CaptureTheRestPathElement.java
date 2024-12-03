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
            for (int i2 = pathIndex; i2 < matchingContext.pathLength; ++i2) {
                MultiValueMap<String, String> parameters;
                PathContainer.Element element = matchingContext.pathElements.get(i2);
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
        StringBuilder sb = new StringBuilder();
        int max = pathElements.size();
        for (int i2 = fromSegment; i2 < max; ++i2) {
            PathContainer.Element element = pathElements.get(i2);
            if (element instanceof PathContainer.PathSegment) {
                sb.append(((PathContainer.PathSegment)element).valueToMatch());
                continue;
            }
            sb.append(element.value());
        }
        return sb.toString();
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public char[] getChars() {
        return ("/{*" + this.variableName + "}").toCharArray();
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
}

