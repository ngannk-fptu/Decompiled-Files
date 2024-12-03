/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.util.ResourceLoader
 *  org.apache.lucene.analysis.util.ResourceLoaderAware
 *  org.apache.lucene.analysis.util.TokenizerFactory
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.UserDictionary;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class JapaneseTokenizerFactory
extends TokenizerFactory
implements ResourceLoaderAware {
    private static final String MODE = "mode";
    private static final String USER_DICT_PATH = "userDictionary";
    private static final String USER_DICT_ENCODING = "userDictionaryEncoding";
    private static final String DISCARD_PUNCTUATION = "discardPunctuation";
    private UserDictionary userDictionary;
    private final JapaneseTokenizer.Mode mode;
    private final boolean discardPunctuation;
    private final String userDictionaryPath;
    private final String userDictionaryEncoding;

    public JapaneseTokenizerFactory(Map<String, String> args) {
        super(args);
        this.mode = JapaneseTokenizer.Mode.valueOf(this.get(args, MODE, JapaneseTokenizer.DEFAULT_MODE.toString()).toUpperCase(Locale.ROOT));
        this.userDictionaryPath = args.remove(USER_DICT_PATH);
        this.userDictionaryEncoding = args.remove(USER_DICT_ENCODING);
        this.discardPunctuation = this.getBoolean(args, DISCARD_PUNCTUATION, true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public void inform(ResourceLoader loader) throws IOException {
        if (this.userDictionaryPath != null) {
            InputStream stream = loader.openResource(this.userDictionaryPath);
            String encoding = this.userDictionaryEncoding;
            if (encoding == null) {
                encoding = "UTF-8";
            }
            CharsetDecoder decoder = Charset.forName(encoding).newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            InputStreamReader reader = new InputStreamReader(stream, decoder);
            this.userDictionary = new UserDictionary(reader);
        } else {
            this.userDictionary = null;
        }
    }

    public JapaneseTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new JapaneseTokenizer(factory, input, this.userDictionary, this.discardPunctuation, this.mode);
    }
}

