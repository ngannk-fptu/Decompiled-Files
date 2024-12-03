/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Pair
 *  io.atlassian.fugue.Pair
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.mail.embed;

import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Maybe;
import io.atlassian.fugue.Pair;
import java.util.Optional;
import java.util.concurrent.Callable;
import javax.activation.DataSource;

public interface MimeBodyPartRecorder {
    @Deprecated
    public <T> com.atlassian.fugue.Pair<Maybe<T>, Iterable<MimeBodyPartReference>> record(Callable<T> var1) throws Exception;

    default public <T> Pair<Optional<T>, Iterable<MimeBodyPartReference>> startRecording(Callable<T> callback) throws Exception {
        com.atlassian.fugue.Pair<Maybe<T>, Iterable<MimeBodyPartReference>> recordResult = this.record(callback);
        return Pair.pair(FugueConversionUtil.toOptional((Maybe)recordResult.left()), (Object)((Iterable)recordResult.right()));
    }

    public boolean isRecording();

    @Deprecated
    default public Maybe<MimeBodyPartReference> track(DataSource source) {
        return FugueConversionUtil.toComOption(this.trackSource(source));
    }

    public Optional<MimeBodyPartReference> trackSource(DataSource var1);
}

