/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

public enum ProgressEventType {
    BYTE_TRANSFER_EVENT,
    REQUEST_CONTENT_LENGTH_EVENT,
    RESPONSE_CONTENT_LENGTH_EVENT,
    REQUEST_BYTE_TRANSFER_EVENT,
    RESPONSE_BYTE_TRANSFER_EVENT,
    RESPONSE_BYTE_DISCARD_EVENT,
    CLIENT_REQUEST_STARTED_EVENT,
    HTTP_REQUEST_STARTED_EVENT,
    HTTP_REQUEST_COMPLETED_EVENT,
    HTTP_REQUEST_CONTENT_RESET_EVENT,
    CLIENT_REQUEST_RETRY_EVENT,
    HTTP_RESPONSE_STARTED_EVENT,
    HTTP_RESPONSE_COMPLETED_EVENT,
    HTTP_RESPONSE_CONTENT_RESET_EVENT,
    CLIENT_REQUEST_SUCCESS_EVENT,
    CLIENT_REQUEST_FAILED_EVENT,
    TRANSFER_PREPARING_EVENT,
    TRANSFER_STARTED_EVENT,
    TRANSFER_COMPLETED_EVENT,
    TRANSFER_FAILED_EVENT,
    TRANSFER_CANCELED_EVENT,
    TRANSFER_PART_STARTED_EVENT,
    TRANSFER_PART_COMPLETED_EVENT,
    TRANSFER_PART_FAILED_EVENT;


    public boolean isTransferEvent() {
        switch (this) {
            case TRANSFER_CANCELED_EVENT: 
            case TRANSFER_COMPLETED_EVENT: 
            case TRANSFER_FAILED_EVENT: 
            case TRANSFER_PART_COMPLETED_EVENT: 
            case TRANSFER_PART_FAILED_EVENT: 
            case TRANSFER_PART_STARTED_EVENT: 
            case TRANSFER_PREPARING_EVENT: 
            case TRANSFER_STARTED_EVENT: {
                return true;
            }
        }
        return false;
    }

    public boolean isRequestCycleEvent() {
        return !this.isTransferEvent();
    }

    public boolean isByteCountEvent() {
        switch (this) {
            case BYTE_TRANSFER_EVENT: 
            case HTTP_REQUEST_CONTENT_RESET_EVENT: 
            case HTTP_RESPONSE_CONTENT_RESET_EVENT: 
            case REQUEST_BYTE_TRANSFER_EVENT: 
            case RESPONSE_BYTE_TRANSFER_EVENT: 
            case RESPONSE_BYTE_DISCARD_EVENT: 
            case REQUEST_CONTENT_LENGTH_EVENT: 
            case RESPONSE_CONTENT_LENGTH_EVENT: {
                return true;
            }
        }
        return false;
    }
}

