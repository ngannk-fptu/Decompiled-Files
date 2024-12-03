/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.collation;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.collation.CollationKeyFilter;
import org.apache.lucene.util.IOUtils;

@Deprecated
public class CollationKeyFilterFactory
extends TokenFilterFactory
implements MultiTermAwareComponent,
ResourceLoaderAware {
    private Collator collator;
    private final String custom;
    private final String language;
    private final String country;
    private final String variant;
    private final String strength;
    private final String decomposition;

    public CollationKeyFilterFactory(Map<String, String> args) {
        super(args);
        this.custom = args.remove("custom");
        this.language = args.remove("language");
        this.country = args.remove("country");
        this.variant = args.remove("variant");
        this.strength = args.remove("strength");
        this.decomposition = args.remove("decomposition");
        if (this.custom == null && this.language == null) {
            throw new IllegalArgumentException("Either custom or language is required.");
        }
        if (this.custom != null && (this.language != null || this.country != null || this.variant != null)) {
            throw new IllegalArgumentException("Cannot specify both language and custom. To tailor rules for a built-in language, see the javadocs for RuleBasedCollator. Then save the entire customized ruleset to a file, and use with the custom parameter");
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        this.collator = this.language != null ? this.createFromLocale(this.language, this.country, this.variant) : this.createFromRules(this.custom, loader);
        if (this.strength != null) {
            if (this.strength.equalsIgnoreCase("primary")) {
                this.collator.setStrength(0);
            } else if (this.strength.equalsIgnoreCase("secondary")) {
                this.collator.setStrength(1);
            } else if (this.strength.equalsIgnoreCase("tertiary")) {
                this.collator.setStrength(2);
            } else if (this.strength.equalsIgnoreCase("identical")) {
                this.collator.setStrength(3);
            } else {
                throw new IllegalArgumentException("Invalid strength: " + this.strength);
            }
        }
        if (this.decomposition != null) {
            if (this.decomposition.equalsIgnoreCase("no")) {
                this.collator.setDecomposition(0);
            } else if (this.decomposition.equalsIgnoreCase("canonical")) {
                this.collator.setDecomposition(1);
            } else if (this.decomposition.equalsIgnoreCase("full")) {
                this.collator.setDecomposition(2);
            } else {
                throw new IllegalArgumentException("Invalid decomposition: " + this.decomposition);
            }
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new CollationKeyFilter(input, this.collator);
    }

    private Collator createFromLocale(String language, String country, String variant) {
        if (language != null && country == null && variant != null) {
            throw new IllegalArgumentException("To specify variant, country is required");
        }
        Locale locale = language != null && country != null && variant != null ? new Locale(language, country, variant) : (language != null && country != null ? new Locale(language, country) : new Locale(language));
        return Collator.getInstance(locale);
    }

    private Collator createFromRules(String fileName, ResourceLoader loader) throws IOException {
        RuleBasedCollator ruleBasedCollator;
        InputStream input = null;
        try {
            input = loader.openResource(fileName);
            String rules = this.toUTF8String(input);
            ruleBasedCollator = new RuleBasedCollator(rules);
        }
        catch (ParseException e) {
            try {
                throw new IOException("ParseException thrown while parsing rules", e);
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{input});
                throw throwable;
            }
        }
        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{input});
        return ruleBasedCollator;
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }

    private String toUTF8String(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        Reader r = IOUtils.getDecodingReader((InputStream)in, (Charset)IOUtils.CHARSET_UTF_8);
        int len = 0;
        while ((len = r.read(buffer)) > 0) {
            sb.append(buffer, 0, len);
        }
        return sb.toString();
    }
}

