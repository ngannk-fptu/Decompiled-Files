/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.document.DateTools$Resolution
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 */
package org.apache.lucene.queryparser.flexible.standard;

import java.util.Locale;
import java.util.TimeZone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.MultiTermQuery;

public interface CommonQueryParserConfiguration {
    public void setLowercaseExpandedTerms(boolean var1);

    public boolean getLowercaseExpandedTerms();

    public void setAllowLeadingWildcard(boolean var1);

    public void setEnablePositionIncrements(boolean var1);

    public boolean getEnablePositionIncrements();

    public void setMultiTermRewriteMethod(MultiTermQuery.RewriteMethod var1);

    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod();

    public void setFuzzyPrefixLength(int var1);

    public void setLocale(Locale var1);

    public Locale getLocale();

    public void setTimeZone(TimeZone var1);

    public TimeZone getTimeZone();

    public void setPhraseSlop(int var1);

    public Analyzer getAnalyzer();

    public boolean getAllowLeadingWildcard();

    public float getFuzzyMinSim();

    public int getFuzzyPrefixLength();

    public int getPhraseSlop();

    public void setFuzzyMinSim(float var1);

    public void setDateResolution(DateTools.Resolution var1);
}

