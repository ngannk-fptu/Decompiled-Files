/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.psl;

import java.net.IDN;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hc.client5.http.psl.DomainType;
import org.apache.hc.client5.http.psl.PublicSuffixList;
import org.apache.hc.client5.http.utils.DnsUtils;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher {
    private final Map<String, DomainType> rules;
    private final Map<String, DomainType> exceptions;

    public PublicSuffixMatcher(Collection<String> rules, Collection<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }

    public PublicSuffixMatcher(DomainType domainType, Collection<String> rules, Collection<String> exceptions) {
        Args.notNull(domainType, "Domain type");
        Args.notNull(rules, "Domain suffix rules");
        this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
        for (String rule : rules) {
            this.rules.put(rule, domainType);
        }
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        if (exceptions != null) {
            for (String exception : exceptions) {
                this.exceptions.put(exception, domainType);
            }
        }
    }

    public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
        Args.notNull(lists, "Domain suffix lists");
        this.rules = new ConcurrentHashMap<String, DomainType>();
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        for (PublicSuffixList list : lists) {
            DomainType domainType = list.getType();
            List<String> rules = list.getRules();
            for (String rule : rules) {
                this.rules.put(rule, domainType);
            }
            List<String> exceptions = list.getExceptions();
            if (exceptions == null) continue;
            for (String exception : exceptions) {
                this.exceptions.put(exception, domainType);
            }
        }
    }

    private static DomainType findEntry(Map<String, DomainType> map, String rule) {
        if (map == null) {
            return null;
        }
        return map.get(rule);
    }

    private static boolean match(DomainType domainType, DomainType expectedType) {
        return domainType != null && (expectedType == null || domainType.equals((Object)expectedType));
    }

    public String getDomainRoot(String domain) {
        return this.getDomainRoot(domain, null);
    }

    public String getDomainRoot(String domain, DomainType expectedType) {
        if (domain == null) {
            return null;
        }
        if (domain.startsWith(".")) {
            return null;
        }
        String segment = DnsUtils.normalize(domain);
        String result = null;
        while (segment != null) {
            DomainType wildcardDomainRule;
            String nextSegment;
            String key = IDN.toUnicode(segment);
            DomainType exceptionRule = PublicSuffixMatcher.findEntry(this.exceptions, key);
            if (PublicSuffixMatcher.match(exceptionRule, expectedType)) {
                return segment;
            }
            DomainType domainRule = PublicSuffixMatcher.findEntry(this.rules, key);
            if (PublicSuffixMatcher.match(domainRule, expectedType)) {
                if (domainRule == DomainType.PRIVATE) {
                    return segment;
                }
                return result;
            }
            int nextdot = segment.indexOf(46);
            String string = nextSegment = nextdot != -1 ? segment.substring(nextdot + 1) : null;
            if (nextSegment != null && PublicSuffixMatcher.match(wildcardDomainRule = PublicSuffixMatcher.findEntry(this.rules, "*." + IDN.toUnicode(nextSegment)), expectedType)) {
                if (wildcardDomainRule == DomainType.PRIVATE) {
                    return segment;
                }
                return result;
            }
            result = segment;
            segment = nextSegment;
        }
        if (expectedType == null || expectedType == DomainType.UNKNOWN) {
            return result;
        }
        return null;
    }

    public boolean matches(String domain) {
        return this.matches(domain, null);
    }

    public boolean matches(String domain, DomainType expectedType) {
        if (domain == null) {
            return false;
        }
        String domainRoot = this.getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
        return domainRoot == null;
    }
}

