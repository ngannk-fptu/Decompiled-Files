/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Wevtapi;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Winevt;
import com.sun.jna.ptr.IntByReference;

public abstract class WevtapiUtil {
    public static String EvtGetExtendedStatus() {
        IntByReference buffUsed = new IntByReference();
        int errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, null, buffUsed);
        if (errorCode != 0 && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        if (buffUsed.getValue() == 0) {
            return "";
        }
        char[] mem = new char[buffUsed.getValue()];
        errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(mem.length, mem, buffUsed);
        if (errorCode != 0) {
            throw new Win32Exception(errorCode);
        }
        return Native.toString((char[])mem);
    }

    public static Memory EvtRender(Winevt.EVT_HANDLE context, Winevt.EVT_HANDLE fragment, int flags, IntByReference propertyCount) {
        IntByReference buffUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, 0, null, buffUsed, propertyCount);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory mem = new Memory((long)buffUsed.getValue());
        result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, (int)mem.size(), (Pointer)mem, buffUsed, propertyCount);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return mem;
    }

    public static String EvtFormatMessage(Winevt.EVT_HANDLE publisherMetadata, Winevt.EVT_HANDLE event, int messageId, int valueCount, Winevt.EVT_VARIANT[] values, int flags) {
        IntByReference bufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, 0, null, bufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        char[] buffer = new char[bufferUsed.getValue()];
        result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, buffer.length, buffer, bufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString((char[])buffer);
    }

    public static Winevt.EVT_VARIANT EvtGetChannelConfigProperty(Winevt.EVT_HANDLE channelHandle, int propertyId) {
        IntByReference propertyValueBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, 0, null, propertyValueBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory propertyValueBuffer = new Memory((long)propertyValueBufferUsed.getValue());
        result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, (int)propertyValueBuffer.size(), (Pointer)propertyValueBuffer, propertyValueBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Winevt.EVT_VARIANT resultEvt = new Winevt.EVT_VARIANT((Pointer)propertyValueBuffer);
        resultEvt.read();
        return resultEvt;
    }

    public static String EvtNextPublisherId(Winevt.EVT_HANDLE publisherEnum) {
        IntByReference publisherIdBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, 0, null, publisherIdBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        char[] publisherIdBuffer = new char[publisherIdBufferUsed.getValue()];
        result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, publisherIdBuffer.length, publisherIdBuffer, publisherIdBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString((char[])publisherIdBuffer);
    }

    public static Memory EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE PublisherMetadata, int PropertyId, int Flags) {
        IntByReference publisherMetadataPropertyBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 0, null, publisherMetadataPropertyBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory publisherMetadataPropertyBuffer = new Memory((long)publisherMetadataPropertyBufferUsed.getValue());
        result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, (int)publisherMetadataPropertyBuffer.size(), (Pointer)publisherMetadataPropertyBuffer, publisherMetadataPropertyBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return publisherMetadataPropertyBuffer;
    }
}

