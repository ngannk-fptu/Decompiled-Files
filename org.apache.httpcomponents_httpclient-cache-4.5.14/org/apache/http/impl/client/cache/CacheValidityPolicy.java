/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpRequest
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.utils.DateUtils
 */
package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.utils.DateUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class CacheValidityPolicy {
    public static final long MAX_AGE = 0x80000000L;

    CacheValidityPolicy() {
    }

    public long getCurrentAgeSecs(HttpCacheEntry entry, Date now) {
        return this.getCorrectedInitialAgeSecs(entry) + this.getResidentTimeSecs(entry, now);
    }

    public long getFreshnessLifetimeSecs(HttpCacheEntry entry) {
        long maxage = this.getMaxAge(entry);
        if (maxage > -1L) {
            return maxage;
        }
        Date dateValue = entry.getDate();
        if (dateValue == null) {
            return 0L;
        }
        Date expiry = this.getExpirationDate(entry);
        if (expiry == null) {
            return 0L;
        }
        long diff = expiry.getTime() - dateValue.getTime();
        return diff / 1000L;
    }

    public boolean isResponseFresh(HttpCacheEntry entry, Date now) {
        return this.getCurrentAgeSecs(entry, now) < this.getFreshnessLifetimeSecs(entry);
    }

    public boolean isResponseHeuristicallyFresh(HttpCacheEntry entry, Date now, float coefficient, long defaultLifetime) {
        return this.getCurrentAgeSecs(entry, now) < this.getHeuristicFreshnessLifetimeSecs(entry, coefficient, defaultLifetime);
    }

    public long getHeuristicFreshnessLifetimeSecs(HttpCacheEntry entry, float coefficient, long defaultLifetime) {
        Date dateValue = entry.getDate();
        Date lastModifiedValue = this.getLastModifiedValue(entry);
        if (dateValue != null && lastModifiedValue != null) {
            long diff = dateValue.getTime() - lastModifiedValue.getTime();
            if (diff < 0L) {
                return 0L;
            }
            return (long)(coefficient * (float)(diff / 1000L));
        }
        return defaultLifetime;
    }

    public boolean isRevalidatable(HttpCacheEntry entry) {
        return entry.getFirstHeader("ETag") != null || entry.getFirstHeader("Last-Modified") != null;
    }

    public boolean mustRevalidate(HttpCacheEntry entry) {
        return this.hasCacheControlDirective(entry, "must-revalidate");
    }

    public boolean proxyRevalidate(HttpCacheEntry entry) {
        return this.hasCacheControlDirective(entry, "proxy-revalidate");
    }

    public boolean mayReturnStaleWhileRevalidating(HttpCacheEntry entry, Date now) {
        for (Header h : entry.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"stale-while-revalidate".equalsIgnoreCase(elt.getName())) continue;
                try {
                    int allowedStalenessLifetime = Integer.parseInt(elt.getValue());
                    if (this.getStalenessSecs(entry, now) > (long)allowedStalenessLifetime) continue;
                    return true;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return false;
    }

    public boolean mayReturnStaleIfError(HttpRequest request, HttpCacheEntry entry, Date now) {
        long stalenessSecs = this.getStalenessSecs(entry, now);
        return this.mayReturnStaleIfError(request.getHeaders("Cache-Control"), stalenessSecs) || this.mayReturnStaleIfError(entry.getHeaders("Cache-Control"), stalenessSecs);
    }

    private boolean mayReturnStaleIfError(Header[] headers, long stalenessSecs) {
        boolean result = false;
        block2: for (Header h : headers) {
            for (HeaderElement elt : h.getElements()) {
                if (!"stale-if-error".equals(elt.getName())) continue;
                try {
                    int staleIfErrorSecs = Integer.parseInt(elt.getValue());
                    if (stalenessSecs > (long)staleIfErrorSecs) continue;
                    result = true;
                    continue block2;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return result;
    }

    @Deprecated
    protected Date getDateValue(HttpCacheEntry entry) {
        return entry.getDate();
    }

    protected Date getLastModifiedValue(HttpCacheEntry entry) {
        Header dateHdr = entry.getFirstHeader("Last-Modified");
        if (dateHdr == null) {
            return null;
        }
        return DateUtils.parseDate((String)dateHdr.getValue());
    }

    protected long getContentLengthValue(HttpCacheEntry entry) {
        Header cl = entry.getFirstHeader("Content-Length");
        if (cl == null) {
            return -1L;
        }
        try {
            return Long.parseLong(cl.getValue());
        }
        catch (NumberFormatException ex) {
            return -1L;
        }
    }

    protected boolean hasContentLengthHeader(HttpCacheEntry entry) {
        return null != entry.getFirstHeader("Content-Length");
    }

    protected boolean contentLengthHeaderMatchesActualLength(HttpCacheEntry entry) {
        return !this.hasContentLengthHeader(entry) || entry.getResource() != null && this.getContentLengthValue(entry) == entry.getResource().length();
    }

    protected long getApparentAgeSecs(HttpCacheEntry entry) {
        Date dateValue = entry.getDate();
        if (dateValue == null) {
            return 0x80000000L;
        }
        long diff = entry.getResponseDate().getTime() - dateValue.getTime();
        if (diff < 0L) {
            return 0L;
        }
        return diff / 1000L;
    }

    protected long getAgeValue(HttpCacheEntry entry) {
        long ageValue = 0L;
        for (Header hdr : entry.getHeaders("Age")) {
            long hdrAge;
            try {
                hdrAge = Long.parseLong(hdr.getValue());
                if (hdrAge < 0L) {
                    hdrAge = 0x80000000L;
                }
            }
            catch (NumberFormatException nfe) {
                hdrAge = 0x80000000L;
            }
            ageValue = hdrAge > ageValue ? hdrAge : ageValue;
        }
        return ageValue;
    }

    protected long getCorrectedReceivedAgeSecs(HttpCacheEntry entry) {
        long ageValue;
        long apparentAge = this.getApparentAgeSecs(entry);
        return apparentAge > (ageValue = this.getAgeValue(entry)) ? apparentAge : ageValue;
    }

    protected long getResponseDelaySecs(HttpCacheEntry entry) {
        long diff = entry.getResponseDate().getTime() - entry.getRequestDate().getTime();
        return diff / 1000L;
    }

    protected long getCorrectedInitialAgeSecs(HttpCacheEntry entry) {
        return this.getCorrectedReceivedAgeSecs(entry) + this.getResponseDelaySecs(entry);
    }

    protected long getResidentTimeSecs(HttpCacheEntry entry, Date now) {
        long diff = now.getTime() - entry.getResponseDate().getTime();
        return diff / 1000L;
    }

    protected long getMaxAge(HttpCacheEntry entry) {
        long maxage = -1L;
        for (Header hdr : entry.getHeaders("Cache-Control")) {
            for (HeaderElement elt : hdr.getElements()) {
                if (!"max-age".equals(elt.getName()) && !"s-maxage".equals(elt.getName())) continue;
                try {
                    long currMaxAge = Long.parseLong(elt.getValue());
                    if (maxage != -1L && currMaxAge >= maxage) continue;
                    maxage = currMaxAge;
                }
                catch (NumberFormatException nfe) {
                    maxage = 0L;
                }
            }
        }
        return maxage;
    }

    protected Date getExpirationDate(HttpCacheEntry entry) {
        Header expiresHeader = entry.getFirstHeader("Expires");
        if (expiresHeader == null) {
            return null;
        }
        return DateUtils.parseDate((String)expiresHeader.getValue());
    }

    public boolean hasCacheControlDirective(HttpCacheEntry entry, String directive) {
        for (Header h : entry.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!directive.equalsIgnoreCase(elt.getName())) continue;
                return true;
            }
        }
        return false;
    }

    public long getStalenessSecs(HttpCacheEntry entry, Date now) {
        long freshness;
        long age = this.getCurrentAgeSecs(entry, now);
        if (age <= (freshness = this.getFreshnessLifetimeSecs(entry))) {
            return 0L;
        }
        return age - freshness;
    }
}

