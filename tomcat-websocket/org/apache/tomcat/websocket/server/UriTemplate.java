/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DeploymentException
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.websocket.DeploymentException;
import org.apache.tomcat.util.res.StringManager;

public class UriTemplate {
    private static final StringManager sm = StringManager.getManager(UriTemplate.class);
    private final String normalized;
    private final List<Segment> segments = new ArrayList<Segment>();
    private final boolean hasParameters;

    public UriTemplate(String path) throws DeploymentException {
        if (path == null || path.length() == 0 || !path.startsWith("/") || path.contains("/../") || path.contains("/./") || path.contains("//")) {
            throw new DeploymentException(sm.getString("uriTemplate.invalidPath", new Object[]{path}));
        }
        StringBuilder normalized = new StringBuilder(path.length());
        HashSet<String> paramNames = new HashSet<String>();
        String[] segments = path.split("/", -1);
        int paramCount = 0;
        int segmentCount = 0;
        for (int i = 0; i < segments.length; ++i) {
            String segment = segments[i];
            if (segment.length() == 0) {
                if (i == 0 || i == segments.length - 1 && paramCount == 0) continue;
                throw new DeploymentException(sm.getString("uriTemplate.emptySegment", new Object[]{path}));
            }
            normalized.append('/');
            int index = -1;
            if (segment.startsWith("{") && segment.endsWith("}")) {
                index = segmentCount;
                segment = segment.substring(1, segment.length() - 1);
                normalized.append('{');
                normalized.append(paramCount++);
                normalized.append('}');
                if (!paramNames.add(segment)) {
                    throw new DeploymentException(sm.getString("uriTemplate.duplicateParameter", new Object[]{segment}));
                }
            } else {
                if (segment.contains("{") || segment.contains("}")) {
                    throw new DeploymentException(sm.getString("uriTemplate.invalidSegment", new Object[]{segment, path}));
                }
                normalized.append(segment);
            }
            this.segments.add(new Segment(index, segment));
            ++segmentCount;
        }
        this.normalized = normalized.toString();
        this.hasParameters = paramCount > 0;
    }

    public Map<String, String> match(UriTemplate candidate) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (candidate.getSegmentCount() != this.getSegmentCount()) {
            return null;
        }
        Iterator<Segment> targetSegments = this.segments.iterator();
        for (Segment candidateSegment : candidate.getSegments()) {
            Segment targetSegment = targetSegments.next();
            if (targetSegment.getParameterIndex() == -1) {
                if (targetSegment.getValue().equals(candidateSegment.getValue())) continue;
                return null;
            }
            result.put(targetSegment.getValue(), candidateSegment.getValue());
        }
        return result;
    }

    public boolean hasParameters() {
        return this.hasParameters;
    }

    public int getSegmentCount() {
        return this.segments.size();
    }

    public String getNormalizedPath() {
        return this.normalized;
    }

    private List<Segment> getSegments() {
        return this.segments;
    }

    private static class Segment {
        private final int parameterIndex;
        private final String value;

        Segment(int parameterIndex, String value) {
            this.parameterIndex = parameterIndex;
            this.value = value;
        }

        public int getParameterIndex() {
            return this.parameterIndex;
        }

        public String getValue() {
            return this.value;
        }
    }
}

