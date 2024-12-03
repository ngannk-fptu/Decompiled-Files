/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.SyncProgressListener;
import com.amazonaws.event.request.Progress;
import com.amazonaws.event.request.ProgressSupport;

public class ProgressTracker
extends SyncProgressListener {
    public static final ProgressTracker NOOP = new ProgressTracker(){

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
        }
    };
    private final Progress progress = new ProgressSupport();

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        if (bytes <= 0L) {
            return;
        }
        switch (progressEvent.getEventType()) {
            case REQUEST_CONTENT_LENGTH_EVENT: {
                this.progress.addRequestContentLength(bytes);
                break;
            }
            case RESPONSE_CONTENT_LENGTH_EVENT: {
                this.progress.addResponseContentLength(bytes);
                break;
            }
            case REQUEST_BYTE_TRANSFER_EVENT: {
                this.progress.addRequestBytesTransferred(bytes);
                break;
            }
            case RESPONSE_BYTE_TRANSFER_EVENT: {
                this.progress.addResponseBytesTransferred(bytes);
                break;
            }
            case HTTP_REQUEST_CONTENT_RESET_EVENT: {
                this.progress.addRequestBytesTransferred(0L - bytes);
                break;
            }
            case HTTP_RESPONSE_CONTENT_RESET_EVENT: 
            case RESPONSE_BYTE_DISCARD_EVENT: {
                this.progress.addResponseBytesTransferred(0L - bytes);
                break;
            }
        }
    }

    public Progress getProgress() {
        return this.progress;
    }
}

