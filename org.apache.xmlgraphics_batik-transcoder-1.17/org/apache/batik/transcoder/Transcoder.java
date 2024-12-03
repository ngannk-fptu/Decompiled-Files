/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

import java.util.Map;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;

public interface Transcoder {
    public void transcode(TranscoderInput var1, TranscoderOutput var2) throws TranscoderException;

    public TranscodingHints getTranscodingHints();

    public void addTranscodingHint(TranscodingHints.Key var1, Object var2);

    public void removeTranscodingHint(TranscodingHints.Key var1);

    public void setTranscodingHints(Map var1);

    public void setTranscodingHints(TranscodingHints var1);

    public void setErrorHandler(ErrorHandler var1);

    public ErrorHandler getErrorHandler();
}

