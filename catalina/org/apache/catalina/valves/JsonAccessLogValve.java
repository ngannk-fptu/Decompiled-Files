/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.json.JSONFilter
 */
package org.apache.catalina.valves;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.tomcat.util.json.JSONFilter;

public class JsonAccessLogValve
extends AccessLogValve {
    private static final Map<Character, String> PATTERNS;
    private static final Map<Character, String> SUB_OBJECT_PATTERNS;

    private boolean addSubkeyedItems(ListIterator<AbstractAccessLogValve.AccessLogElement> iterator, List<JsonWrappedElement> elements, String patternAttribute) {
        if (!elements.isEmpty()) {
            iterator.add(new AbstractAccessLogValve.StringElement("\"" + patternAttribute + "\": {"));
            for (JsonWrappedElement element : elements) {
                iterator.add(element);
                iterator.add(new CharElement(','));
            }
            iterator.previous();
            iterator.remove();
            iterator.add(new AbstractAccessLogValve.StringElement("},"));
            return true;
        }
        return false;
    }

    @Override
    protected AbstractAccessLogValve.AccessLogElement[] createLogElements() {
        HashMap subTypeLists = new HashMap();
        for (Character pattern : SUB_OBJECT_PATTERNS.keySet()) {
            subTypeLists.put(pattern, new ArrayList());
        }
        boolean hasSub = false;
        ArrayList<AbstractAccessLogValve.AccessLogElement> logElements = new ArrayList<AbstractAccessLogValve.AccessLogElement>(Arrays.asList(super.createLogElements()));
        ListIterator<AbstractAccessLogValve.AccessLogElement> lit = logElements.listIterator();
        lit.add(new CharElement('{'));
        while (lit.hasNext()) {
            AbstractAccessLogValve.AccessLogElement logElement = (AbstractAccessLogValve.AccessLogElement)lit.next();
            if (!(logElement instanceof JsonWrappedElement)) {
                lit.remove();
                continue;
            }
            JsonWrappedElement wrappedLogElement = (JsonWrappedElement)logElement;
            AbstractAccessLogValve.AccessLogElement ale = wrappedLogElement.getDelegate();
            if (ale instanceof AbstractAccessLogValve.HeaderElement) {
                ((List)subTypeLists.get(Character.valueOf('i'))).add(wrappedLogElement);
                lit.remove();
                continue;
            }
            if (ale instanceof AbstractAccessLogValve.ResponseHeaderElement) {
                ((List)subTypeLists.get(Character.valueOf('o'))).add(wrappedLogElement);
                lit.remove();
                continue;
            }
            if (ale instanceof AbstractAccessLogValve.RequestAttributeElement) {
                ((List)subTypeLists.get(Character.valueOf('r'))).add(wrappedLogElement);
                lit.remove();
                continue;
            }
            if (ale instanceof AbstractAccessLogValve.SessionAttributeElement) {
                ((List)subTypeLists.get(Character.valueOf('s'))).add(wrappedLogElement);
                lit.remove();
                continue;
            }
            if (ale instanceof AbstractAccessLogValve.CookieElement) {
                ((List)subTypeLists.get(Character.valueOf('c'))).add(wrappedLogElement);
                lit.remove();
                continue;
            }
            lit.add(new CharElement(','));
        }
        for (Character pattern : SUB_OBJECT_PATTERNS.keySet()) {
            if (!this.addSubkeyedItems(lit, (List)subTypeLists.get(pattern), SUB_OBJECT_PATTERNS.get(pattern))) continue;
            hasSub = true;
        }
        lit.previous();
        lit.remove();
        if (hasSub) {
            lit.add(new AbstractAccessLogValve.StringElement("}}"));
        } else {
            lit.add(new CharElement('}'));
        }
        return logElements.toArray(new AbstractAccessLogValve.AccessLogElement[0]);
    }

    @Override
    protected AbstractAccessLogValve.AccessLogElement createAccessLogElement(String name, char pattern) {
        AbstractAccessLogValve.AccessLogElement ale = super.createAccessLogElement(name, pattern);
        return new JsonWrappedElement(pattern, name, true, ale);
    }

    @Override
    protected AbstractAccessLogValve.AccessLogElement createAccessLogElement(char pattern) {
        AbstractAccessLogValve.AccessLogElement ale = super.createAccessLogElement(pattern);
        return new JsonWrappedElement(pattern, true, ale);
    }

    static {
        HashMap<Character, String> pattern2AttributeName = new HashMap<Character, String>();
        pattern2AttributeName.put(Character.valueOf('a'), "remoteAddr");
        pattern2AttributeName.put(Character.valueOf('A'), "localAddr");
        pattern2AttributeName.put(Character.valueOf('b'), "size");
        pattern2AttributeName.put(Character.valueOf('B'), "byteSentNC");
        pattern2AttributeName.put(Character.valueOf('D'), "elapsedTime");
        pattern2AttributeName.put(Character.valueOf('F'), "firstByteTime");
        pattern2AttributeName.put(Character.valueOf('h'), "host");
        pattern2AttributeName.put(Character.valueOf('H'), "protocol");
        pattern2AttributeName.put(Character.valueOf('I'), "threadName");
        pattern2AttributeName.put(Character.valueOf('l'), "logicalUserName");
        pattern2AttributeName.put(Character.valueOf('m'), "method");
        pattern2AttributeName.put(Character.valueOf('p'), "port");
        pattern2AttributeName.put(Character.valueOf('q'), "query");
        pattern2AttributeName.put(Character.valueOf('r'), "request");
        pattern2AttributeName.put(Character.valueOf('s'), "statusCode");
        pattern2AttributeName.put(Character.valueOf('S'), "sessionId");
        pattern2AttributeName.put(Character.valueOf('t'), "time");
        pattern2AttributeName.put(Character.valueOf('T'), "elapsedTimeS");
        pattern2AttributeName.put(Character.valueOf('u'), "user");
        pattern2AttributeName.put(Character.valueOf('U'), "path");
        pattern2AttributeName.put(Character.valueOf('v'), "localServerName");
        pattern2AttributeName.put(Character.valueOf('X'), "connectionStatus");
        PATTERNS = Collections.unmodifiableMap(pattern2AttributeName);
        pattern2AttributeName = new HashMap();
        pattern2AttributeName.put(Character.valueOf('c'), "cookies");
        pattern2AttributeName.put(Character.valueOf('i'), "requestHeaders");
        pattern2AttributeName.put(Character.valueOf('o'), "responseHeaders");
        pattern2AttributeName.put(Character.valueOf('r'), "requestAttributes");
        pattern2AttributeName.put(Character.valueOf('s'), "sessionAttributes");
        SUB_OBJECT_PATTERNS = Collections.unmodifiableMap(pattern2AttributeName);
    }

    private static class JsonWrappedElement
    implements AbstractAccessLogValve.AccessLogElement,
    AbstractAccessLogValve.CachedElement {
        private CharSequence attributeName;
        private boolean quoteValue;
        private AbstractAccessLogValve.AccessLogElement delegate;

        private CharSequence escapeJsonString(CharSequence nonEscaped) {
            return JSONFilter.escape((CharSequence)nonEscaped);
        }

        JsonWrappedElement(char pattern, String key, boolean quoteValue, AbstractAccessLogValve.AccessLogElement delegate) {
            this.quoteValue = quoteValue;
            this.delegate = delegate;
            String patternAttribute = (String)PATTERNS.get(Character.valueOf(pattern));
            if (patternAttribute == null) {
                patternAttribute = "other-" + Character.toString(pattern);
            }
            this.attributeName = key != null && !"".equals(key) ? (SUB_OBJECT_PATTERNS.containsKey(Character.valueOf(pattern)) ? this.escapeJsonString(key) : this.escapeJsonString(patternAttribute + "-" + key)) : this.escapeJsonString(patternAttribute);
        }

        JsonWrappedElement(char pattern, boolean quoteValue, AbstractAccessLogValve.AccessLogElement delegate) {
            this(pattern, null, quoteValue, delegate);
        }

        public AbstractAccessLogValve.AccessLogElement getDelegate() {
            return this.delegate;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append('\"').append(this.attributeName).append('\"').append(':');
            if (this.quoteValue) {
                buf.append('\"');
            }
            this.delegate.addElement(buf, date, request, response, time);
            if (this.quoteValue) {
                buf.append('\"');
            }
        }

        @Override
        public void cache(Request request) {
            if (this.delegate instanceof AbstractAccessLogValve.CachedElement) {
                ((AbstractAccessLogValve.CachedElement)((Object)this.delegate)).cache(request);
            }
        }
    }

    protected static class CharElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final char ch;

        public CharElement(char ch) {
            this.ch = ch;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.write(this.ch);
        }
    }
}

