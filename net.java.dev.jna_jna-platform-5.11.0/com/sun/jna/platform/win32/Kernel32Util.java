/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.LastErrorException
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.W32APITypeMapper
 */
package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Kernel32Util
implements WinDef {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final String VOLUME_GUID_PATH_PREFIX = "\\\\?\\Volume{";
    public static final String VOLUME_GUID_PATH_SUFFIX = "}\\";

    public static String getComputerName() {
        char[] buffer = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
        IntByReference lpnSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString((char[])buffer);
    }

    public static void freeLocalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.LocalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static void freeGlobalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.GlobalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static void closeHandleRefs(WinNT.HANDLEByReference ... refs) {
        Win32Exception err = null;
        for (WinNT.HANDLEByReference r : refs) {
            try {
                Kernel32Util.closeHandleRef(r);
            }
            catch (Win32Exception e) {
                if (err == null) {
                    err = e;
                    continue;
                }
                err.addSuppressedReflected((Throwable)((Object)e));
            }
        }
        if (err != null) {
            throw err;
        }
    }

    public static void closeHandleRef(WinNT.HANDLEByReference ref) {
        Kernel32Util.closeHandle(ref == null ? null : ref.getValue());
    }

    public static void closeHandles(WinNT.HANDLE ... handles) {
        Win32Exception err = null;
        for (WinNT.HANDLE h : handles) {
            try {
                Kernel32Util.closeHandle(h);
            }
            catch (Win32Exception e) {
                if (err == null) {
                    err = e;
                    continue;
                }
                err.addSuppressedReflected((Throwable)((Object)e));
            }
        }
        if (err != null) {
            throw err;
        }
    }

    public static void closeHandle(WinNT.HANDLE h) {
        if (h == null) {
            return;
        }
        if (!Kernel32.INSTANCE.CloseHandle(h)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String formatMessage(int code) {
        PointerByReference buffer = new PointerByReference();
        int nLen = Kernel32.INSTANCE.FormatMessage(4864, null, code, 0, buffer, 0, null);
        if (nLen == 0) {
            throw new LastErrorException(Native.getLastError());
        }
        Pointer ptr = buffer.getValue();
        try {
            String s = ptr.getWideString(0L);
            String string = s.trim();
            return string;
        }
        finally {
            Kernel32Util.freeLocalMemory(ptr);
        }
    }

    public static String formatMessage(WinNT.HRESULT code) {
        return Kernel32Util.formatMessage(code.intValue());
    }

    public static String formatMessageFromLastErrorCode(int code) {
        return Kernel32Util.formatMessage(W32Errors.HRESULT_FROM_WIN32(code));
    }

    public static String getLastErrorMessage() {
        return Kernel32Util.formatMessageFromLastErrorCode(Kernel32.INSTANCE.GetLastError());
    }

    public static String getTempPath() {
        WinDef.DWORD nBufferLength = new WinDef.DWORD(260L);
        char[] buffer = new char[nBufferLength.intValue()];
        if (Kernel32.INSTANCE.GetTempPath(nBufferLength, buffer).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString((char[])buffer);
    }

    public static void deleteFile(String filename) {
        if (!Kernel32.INSTANCE.DeleteFile(filename)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static List<String> getLogicalDriveStrings() {
        WinDef.DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new WinDef.DWORD(0L), null);
        if (dwSize.intValue() <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        char[] buf = new char[dwSize.intValue()];
        int bufSize = (dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf)).intValue();
        if (bufSize <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toStringList((char[])buf, (int)0, (int)bufSize);
    }

    public static int getFileAttributes(String fileName) {
        int fileAttributes = Kernel32.INSTANCE.GetFileAttributes(fileName);
        if (fileAttributes == -1) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return fileAttributes;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static int getFileType(String fileName) throws FileNotFoundException {
        int n;
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException(fileName);
        }
        WinNT.HANDLE hFile = null;
        Win32Exception err = null;
        try {
            hFile = Kernel32.INSTANCE.CreateFile(fileName, Integer.MIN_VALUE, 1, new WinBase.SECURITY_ATTRIBUTES(), 3, 128, new WinNT.HANDLEByReference().getValue());
            if (WinBase.INVALID_HANDLE_VALUE.equals((Object)hFile)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            int type = Kernel32.INSTANCE.GetFileType(hFile);
            block4 : switch (type) {
                case 0: {
                    int rc = Kernel32.INSTANCE.GetLastError();
                    switch (rc) {
                        case 0: {
                            break block4;
                        }
                    }
                    throw new Win32Exception(rc);
                }
            }
            n = type;
        }
        catch (Win32Exception e) {
            try {
                err = e;
                throw err;
            }
            catch (Throwable throwable) {
                block20: {
                    try {
                        Kernel32Util.closeHandle(hFile);
                    }
                    catch (Win32Exception e2) {
                        if (err == null) {
                            err = e2;
                            break block20;
                        }
                        err.addSuppressedReflected((Throwable)((Object)e2));
                    }
                }
                if (err != null) {
                    throw err;
                }
                throw throwable;
            }
        }
        try {
            Kernel32Util.closeHandle(hFile);
        }
        catch (Win32Exception e) {
            if (err == null) {
                err = e;
            }
            err.addSuppressedReflected((Throwable)((Object)e));
        }
        if (err != null) {
            throw err;
        }
        return n;
    }

    public static int getDriveType(String rootName) {
        return Kernel32.INSTANCE.GetDriveType(rootName);
    }

    public static String getEnvironmentVariable(String name) {
        int size = Kernel32.INSTANCE.GetEnvironmentVariable(name, null, 0);
        if (size == 0) {
            return null;
        }
        if (size < 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        char[] buffer = new char[size];
        if ((size = Kernel32.INSTANCE.GetEnvironmentVariable(name, buffer, buffer.length)) <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString((char[])buffer);
    }

    public static Map<String, String> getEnvironmentVariables() {
        Pointer lpszEnvironmentBlock = Kernel32.INSTANCE.GetEnvironmentStrings();
        if (lpszEnvironmentBlock == null) {
            throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        }
        try {
            Map<String, String> map = Kernel32Util.getEnvironmentVariables(lpszEnvironmentBlock, 0L);
            return map;
        }
        finally {
            if (!Kernel32.INSTANCE.FreeEnvironmentStrings(lpszEnvironmentBlock)) {
                throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
            }
        }
    }

    public static Map<String, String> getEnvironmentVariables(Pointer lpszEnvironmentBlock, long offset) {
        String nvp;
        int len;
        if (lpszEnvironmentBlock == null) {
            return null;
        }
        TreeMap<String, String> vars = new TreeMap<String, String>();
        boolean asWideChars = Kernel32Util.isWideCharEnvironmentStringBlock(lpszEnvironmentBlock, offset);
        long stepFactor = asWideChars ? 2L : 1L;
        long curOffset = offset;
        while ((len = (nvp = Kernel32Util.readEnvironmentStringBlockEntry(lpszEnvironmentBlock, curOffset, asWideChars)).length()) != 0) {
            int pos = nvp.indexOf(61);
            if (pos < 0) {
                throw new IllegalArgumentException("Missing variable value separator in " + nvp);
            }
            String name = nvp.substring(0, pos);
            String value = nvp.substring(pos + 1);
            vars.put(name, value);
            curOffset += (long)(len + 1) * stepFactor;
        }
        return vars;
    }

    public static String readEnvironmentStringBlockEntry(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        long endOffset = Kernel32Util.findEnvironmentStringBlockEntryEnd(lpszEnvironmentBlock, offset, asWideChars);
        int dataLen = (int)(endOffset - offset);
        if (dataLen == 0) {
            return "";
        }
        int charsLen = asWideChars ? dataLen / 2 : dataLen;
        char[] chars = new char[charsLen];
        long curOffset = offset;
        long stepSize = asWideChars ? 2L : 1L;
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        int index = 0;
        while (index < chars.length) {
            byte b = lpszEnvironmentBlock.getByte(curOffset);
            if (asWideChars) {
                byte x = lpszEnvironmentBlock.getByte(curOffset + 1L);
                chars[index] = ByteOrder.LITTLE_ENDIAN.equals(byteOrder) ? (char)(x << 8 & 0xFF00 | b & 0xFF) : (char)(b << 8 & 0xFF00 | x & 0xFF);
            } else {
                chars[index] = (char)(b & 0xFF);
            }
            ++index;
            curOffset += stepSize;
        }
        return new String(chars);
    }

    public static long findEnvironmentStringBlockEntryEnd(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        long stepSize;
        long curOffset = offset;
        long l = stepSize = asWideChars ? 2L : 1L;
        byte b;
        while ((b = lpszEnvironmentBlock.getByte(curOffset)) != 0) {
            curOffset += stepSize;
        }
        return curOffset;
    }

    public static boolean isWideCharEnvironmentStringBlock(Pointer lpszEnvironmentBlock, long offset) {
        byte b0 = lpszEnvironmentBlock.getByte(offset);
        byte b1 = lpszEnvironmentBlock.getByte(offset + 1L);
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
            return Kernel32Util.isWideCharEnvironmentStringBlock(b1);
        }
        return Kernel32Util.isWideCharEnvironmentStringBlock(b0);
    }

    private static boolean isWideCharEnvironmentStringBlock(byte charsetIndicator) {
        return charsetIndicator == 0;
    }

    public static final int getPrivateProfileInt(String appName, String keyName, int defaultValue, String fileName) {
        return Kernel32.INSTANCE.GetPrivateProfileInt(appName, keyName, defaultValue, fileName);
    }

    public static final String getPrivateProfileString(String lpAppName, String lpKeyName, String lpDefault, String lpFileName) {
        char[] buffer = new char[1024];
        Kernel32.INSTANCE.GetPrivateProfileString(lpAppName, lpKeyName, lpDefault, buffer, new WinDef.DWORD(buffer.length), lpFileName);
        return Native.toString((char[])buffer);
    }

    public static final void writePrivateProfileString(String appName, String keyName, String string, String fileName) {
        if (!Kernel32.INSTANCE.WritePrivateProfileString(appName, keyName, string, fileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation() {
        Memory memory;
        int sizePerStruct = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION().size();
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(sizePerStruct));
        while (!Kernel32.INSTANCE.GetLogicalProcessorInformation((Pointer)(memory = new Memory((long)bufferSize.getValue().intValue())), bufferSize)) {
            int err = Kernel32.INSTANCE.GetLastError();
            if (err == 122) continue;
            throw new Win32Exception(err);
        }
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION firstInformation = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION((Pointer)memory);
        int returnedStructCount = bufferSize.getValue().intValue() / sizePerStruct;
        return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[])firstInformation.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[returnedStructCount]);
    }

    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] getLogicalProcessorInformationEx(int relationshipType) {
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX information;
        Memory memory;
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(1L));
        while (!Kernel32.INSTANCE.GetLogicalProcessorInformationEx(relationshipType, (Pointer)(memory = new Memory((long)bufferSize.getValue().intValue())), bufferSize)) {
            int err = Kernel32.INSTANCE.GetLastError();
            if (err == 122) continue;
            throw new Win32Exception(err);
        }
        ArrayList<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX> procInfoList = new ArrayList<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX>();
        for (int offset = 0; offset < bufferSize.getValue().intValue(); offset += information.size) {
            information = WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.fromPointer(memory.share((long)offset));
            procInfoList.add(information);
        }
        return procInfoList.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[0]);
    }

    public static final String[] getPrivateProfileSection(String appName, String fileName) {
        char[] buffer = new char[32768];
        if (Kernel32.INSTANCE.GetPrivateProfileSection(appName, buffer, new WinDef.DWORD(buffer.length), fileName).intValue() == 0) {
            int lastError = Kernel32.INSTANCE.GetLastError();
            if (lastError == 0) {
                return EMPTY_STRING_ARRAY;
            }
            throw new Win32Exception(lastError);
        }
        return new String(buffer).split("\u0000");
    }

    public static final String[] getPrivateProfileSectionNames(String fileName) {
        char[] buffer = new char[65536];
        if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new WinDef.DWORD(buffer.length), fileName).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new String(buffer).split("\u0000");
    }

    public static final void writePrivateProfileSection(String appName, String[] strings, String fileName) {
        StringBuilder buffer = new StringBuilder();
        for (String string : strings) {
            buffer.append(string).append('\u0000');
        }
        buffer.append('\u0000');
        if (!Kernel32.INSTANCE.WritePrivateProfileSection(appName, buffer.toString(), fileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static final List<String> queryDosDevice(String lpszDeviceName, int maxTargetSize) {
        char[] lpTargetPath = new char[maxTargetSize];
        int dwSize = Kernel32.INSTANCE.QueryDosDevice(lpszDeviceName, lpTargetPath, lpTargetPath.length);
        if (dwSize == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toStringList((char[])lpTargetPath, (int)0, (int)dwSize);
    }

    public static final List<String> getVolumePathNamesForVolumeName(String lpszVolumeName) {
        char[] lpszVolumePathNames = new char[261];
        IntByReference lpcchReturnLength = new IntByReference();
        if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr != 234) {
                throw new Win32Exception(hr);
            }
            int required = lpcchReturnLength.getValue();
            lpszVolumePathNames = new char[required];
            if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
        int bufSize = lpcchReturnLength.getValue();
        return Native.toStringList((char[])lpszVolumePathNames, (int)0, (int)bufSize);
    }

    public static final String extractVolumeGUID(String volumeGUIDPath) {
        if (volumeGUIDPath == null || volumeGUIDPath.length() <= VOLUME_GUID_PATH_PREFIX.length() + VOLUME_GUID_PATH_SUFFIX.length() || !volumeGUIDPath.startsWith(VOLUME_GUID_PATH_PREFIX) || !volumeGUIDPath.endsWith(VOLUME_GUID_PATH_SUFFIX)) {
            throw new IllegalArgumentException("Bad volume GUID path format: " + volumeGUIDPath);
        }
        return volumeGUIDPath.substring(VOLUME_GUID_PATH_PREFIX.length(), volumeGUIDPath.length() - VOLUME_GUID_PATH_SUFFIX.length());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final String QueryFullProcessImageName(int pid, int dwFlags) {
        String string;
        WinNT.HANDLE hProcess = null;
        Win32Exception we = null;
        try {
            hProcess = Kernel32.INSTANCE.OpenProcess(1040, false, pid);
            if (hProcess == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            string = Kernel32Util.QueryFullProcessImageName(hProcess, dwFlags);
        }
        catch (Win32Exception e) {
            try {
                we = e;
                throw we;
            }
            catch (Throwable throwable) {
                block13: {
                    try {
                        Kernel32Util.closeHandle(hProcess);
                    }
                    catch (Win32Exception e2) {
                        if (we == null) {
                            we = e2;
                            break block13;
                        }
                        we.addSuppressed((Throwable)((Object)e2));
                    }
                }
                if (we != null) {
                    throw we;
                }
                throw throwable;
            }
        }
        try {
            Kernel32Util.closeHandle(hProcess);
        }
        catch (Win32Exception e) {
            if (we == null) {
                we = e;
            }
            we.addSuppressed((Throwable)((Object)e));
        }
        if (we != null) {
            throw we;
        }
        return string;
    }

    public static final String QueryFullProcessImageName(WinNT.HANDLE hProcess, int dwFlags) {
        int size = 260;
        IntByReference lpdwSize = new IntByReference();
        do {
            char[] lpExeName = new char[size];
            lpdwSize.setValue(size);
            if (Kernel32.INSTANCE.QueryFullProcessImageName(hProcess, dwFlags, lpExeName, lpdwSize)) {
                return new String(lpExeName, 0, lpdwSize.getValue());
            }
            size += 1024;
        } while (Kernel32.INSTANCE.GetLastError() == 122);
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getResource(String path, String type, String name) {
        byte[] results;
        Win32Exception err;
        block16: {
            WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, 2);
            if (target == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            err = null;
            Pointer start = null;
            int length = 0;
            results = null;
            try {
                Pointer t = null;
                try {
                    t = new Pointer(Long.parseLong(type));
                }
                catch (NumberFormatException e) {
                    t = new Memory((long)(Native.WCHAR_SIZE * (type.length() + 1)));
                    t.setWideString(0L, type);
                }
                Pointer n = null;
                try {
                    n = new Pointer(Long.parseLong(name));
                }
                catch (NumberFormatException e) {
                    n = new Memory((long)(Native.WCHAR_SIZE * (name.length() + 1)));
                    n.setWideString(0L, name);
                }
                WinDef.HRSRC hrsrc = Kernel32.INSTANCE.FindResource(target, n, t);
                if (hrsrc == null) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                WinNT.HANDLE loaded = Kernel32.INSTANCE.LoadResource(target, hrsrc);
                if (loaded == null) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                length = Kernel32.INSTANCE.SizeofResource(target, hrsrc);
                if (length == 0) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                start = Kernel32.INSTANCE.LockResource(loaded);
                if (start == null) {
                    throw new IllegalStateException("LockResource returned null.");
                }
                results = start.getByteArray(0L, length);
            }
            catch (Win32Exception we) {
                err = we;
                return err;
            }
            finally {
                if (target == null || Kernel32.INSTANCE.FreeLibrary(target)) break block16;
                we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (err != null) {
                    we.addSuppressedReflected((Throwable)((Object)err));
                }
                throw we;
            }
        }
        if (err != null) {
            throw err;
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, List<String>> getResourceNames(String path) {
        Win32Exception err;
        LinkedHashMap<String, List<String>> result;
        block12: {
            WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, 2);
            if (target == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            final ArrayList types = new ArrayList();
            result = new LinkedHashMap<String, List<String>>();
            WinBase.EnumResTypeProc ertp = new WinBase.EnumResTypeProc(){

                @Override
                public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer lParam) {
                    if (Pointer.nativeValue((Pointer)type) <= 65535L) {
                        types.add(Pointer.nativeValue((Pointer)type) + "");
                    } else {
                        types.add(type.getWideString(0L));
                    }
                    return true;
                }
            };
            WinBase.EnumResNameProc ernp = new WinBase.EnumResNameProc(){

                @Override
                public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer name, Pointer lParam) {
                    String typeName = "";
                    typeName = Pointer.nativeValue((Pointer)type) <= 65535L ? Pointer.nativeValue((Pointer)type) + "" : type.getWideString(0L);
                    if (Pointer.nativeValue((Pointer)name) < 65535L) {
                        ((List)result.get(typeName)).add(Pointer.nativeValue((Pointer)name) + "");
                    } else {
                        ((List)result.get(typeName)).add(name.getWideString(0L));
                    }
                    return true;
                }
            };
            err = null;
            try {
                if (!Kernel32.INSTANCE.EnumResourceTypes(target, ertp, null)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                for (String typeName : types) {
                    boolean callResult;
                    result.put(typeName, new ArrayList());
                    Pointer pointer = null;
                    try {
                        pointer = new Pointer(Long.parseLong(typeName));
                    }
                    catch (NumberFormatException e) {
                        pointer = new Memory((long)(Native.WCHAR_SIZE * (typeName.length() + 1)));
                        pointer.setWideString(0L, typeName);
                    }
                    if (callResult = Kernel32.INSTANCE.EnumResourceNames(target, pointer, ernp, null)) continue;
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            }
            catch (Win32Exception e) {
                err = e;
                return err;
            }
            finally {
                if (target == null || Kernel32.INSTANCE.FreeLibrary(target)) break block12;
                Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (err != null) {
                    we.addSuppressedReflected((Throwable)((Object)err));
                }
                throw we;
            }
        }
        if (err != null) {
            throw err;
        }
        return result;
    }

    public static List<Tlhelp32.MODULEENTRY32W> getModules(int processID) {
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD(processID));
        if (snapshot == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Win32Exception we = null;
        try {
            Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();
            if (!Kernel32.INSTANCE.Module32FirstW(snapshot, first)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            ArrayList<Tlhelp32.MODULEENTRY32W> modules = new ArrayList<Tlhelp32.MODULEENTRY32W>();
            modules.add(first);
            Tlhelp32.MODULEENTRY32W next = new Tlhelp32.MODULEENTRY32W();
            while (Kernel32.INSTANCE.Module32NextW(snapshot, next)) {
                modules.add(next);
                next = new Tlhelp32.MODULEENTRY32W();
            }
            int lastError = Kernel32.INSTANCE.GetLastError();
            if (lastError != 0 && lastError != 18) {
                throw new Win32Exception(lastError);
            }
            ArrayList<Tlhelp32.MODULEENTRY32W> arrayList = modules;
            return arrayList;
        }
        catch (Win32Exception e) {
            we = e;
            throw we;
        }
        finally {
            try {
                Kernel32Util.closeHandle(snapshot);
            }
            catch (Win32Exception e) {
                if (we == null) {
                    we = e;
                }
                we.addSuppressedReflected((Throwable)((Object)e));
            }
            if (we != null) {
                throw we;
            }
        }
    }

    public static String expandEnvironmentStrings(String input) {
        if (input == null) {
            return "";
        }
        int resultChars = Kernel32.INSTANCE.ExpandEnvironmentStrings(input, null, 0);
        if (resultChars == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Memory resultMemory = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? new Memory((long)(resultChars * Native.WCHAR_SIZE)) : new Memory((long)(resultChars + 1));
        resultChars = Kernel32.INSTANCE.ExpandEnvironmentStrings(input, (Pointer)resultMemory, resultChars);
        if (resultChars == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            return resultMemory.getWideString(0L);
        }
        return resultMemory.getString(0L);
    }
}

