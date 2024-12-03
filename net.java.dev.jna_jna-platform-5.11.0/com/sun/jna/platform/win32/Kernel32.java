/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.LastErrorException
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Kernel32
extends StdCallLibrary,
WinNT,
Wincon {
    public static final Kernel32 INSTANCE = (Kernel32)Native.load((String)"kernel32", Kernel32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int LOAD_LIBRARY_AS_DATAFILE = 2;
    public static final int MAX_PIPE_NAME_LENGTH = 256;

    public boolean ReadFile(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

    public Pointer LocalFree(Pointer var1);

    public Pointer GlobalFree(Pointer var1);

    public WinDef.HMODULE GetModuleHandle(String var1);

    public void GetSystemTime(WinBase.SYSTEMTIME var1);

    public boolean SetSystemTime(WinBase.SYSTEMTIME var1);

    public void GetLocalTime(WinBase.SYSTEMTIME var1);

    public boolean SetLocalTime(WinBase.SYSTEMTIME var1);

    public boolean GetSystemTimes(WinBase.FILETIME var1, WinBase.FILETIME var2, WinBase.FILETIME var3);

    public int GetTickCount();

    public long GetTickCount64();

    public int GetCurrentThreadId();

    public WinNT.HANDLE GetCurrentThread();

    public int GetCurrentProcessId();

    public WinNT.HANDLE GetCurrentProcess();

    public int GetProcessId(WinNT.HANDLE var1);

    public int GetProcessVersion(int var1);

    public boolean GetProcessAffinityMask(WinNT.HANDLE var1, BaseTSD.ULONG_PTRByReference var2, BaseTSD.ULONG_PTRByReference var3);

    public boolean SetProcessAffinityMask(WinNT.HANDLE var1, BaseTSD.ULONG_PTR var2);

    public boolean GetExitCodeProcess(WinNT.HANDLE var1, IntByReference var2);

    public boolean TerminateProcess(WinNT.HANDLE var1, int var2);

    public int GetLastError();

    public void SetLastError(int var1);

    public int GetDriveType(String var1);

    public int FormatMessage(int var1, Pointer var2, int var3, int var4, PointerByReference var5, int var6, Pointer var7);

    public WinNT.HANDLE CreateFile(String var1, int var2, int var3, WinBase.SECURITY_ATTRIBUTES var4, int var5, int var6, WinNT.HANDLE var7);

    public boolean CopyFile(String var1, String var2, boolean var3);

    public boolean MoveFile(String var1, String var2);

    public boolean MoveFileEx(String var1, String var2, WinDef.DWORD var3);

    public boolean CreateDirectory(String var1, WinBase.SECURITY_ATTRIBUTES var2);

    public WinNT.HANDLE CreateIoCompletionPort(WinNT.HANDLE var1, WinNT.HANDLE var2, Pointer var3, int var4);

    public boolean GetQueuedCompletionStatus(WinNT.HANDLE var1, IntByReference var2, BaseTSD.ULONG_PTRByReference var3, PointerByReference var4, int var5);

    public boolean PostQueuedCompletionStatus(WinNT.HANDLE var1, int var2, Pointer var3, WinBase.OVERLAPPED var4);

    public int WaitForSingleObject(WinNT.HANDLE var1, int var2);

    public int WaitForMultipleObjects(int var1, WinNT.HANDLE[] var2, boolean var3, int var4);

    public boolean DuplicateHandle(WinNT.HANDLE var1, WinNT.HANDLE var2, WinNT.HANDLE var3, WinNT.HANDLEByReference var4, int var5, boolean var6, int var7);

    public boolean CloseHandle(WinNT.HANDLE var1);

    public boolean ReadDirectoryChangesW(WinNT.HANDLE var1, WinNT.FILE_NOTIFY_INFORMATION var2, int var3, boolean var4, int var5, IntByReference var6, WinBase.OVERLAPPED var7, WinNT.OVERLAPPED_COMPLETION_ROUTINE var8);

    public int GetShortPathName(String var1, char[] var2, int var3);

    public Pointer LocalAlloc(int var1, int var2);

    public boolean WriteFile(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

    public boolean FlushFileBuffers(WinNT.HANDLE var1);

    public WinNT.HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES var1, boolean var2, boolean var3, String var4);

    public WinNT.HANDLE OpenEvent(int var1, boolean var2, String var3);

    public boolean SetEvent(WinNT.HANDLE var1);

    public boolean ResetEvent(WinNT.HANDLE var1);

    public boolean PulseEvent(WinNT.HANDLE var1);

    public WinNT.HANDLE CreateFileMapping(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, int var4, int var5, String var6);

    public WinNT.HANDLE OpenFileMapping(int var1, boolean var2, String var3);

    public Pointer MapViewOfFile(WinNT.HANDLE var1, int var2, int var3, int var4, int var5);

    public boolean UnmapViewOfFile(Pointer var1);

    public boolean GetComputerName(char[] var1, IntByReference var2);

    public boolean GetComputerNameEx(int var1, char[] var2, IntByReference var3);

    public WinNT.HANDLE OpenThread(int var1, boolean var2, int var3);

    public boolean CreateProcess(String var1, String var2, WinBase.SECURITY_ATTRIBUTES var3, WinBase.SECURITY_ATTRIBUTES var4, boolean var5, WinDef.DWORD var6, Pointer var7, String var8, WinBase.STARTUPINFO var9, WinBase.PROCESS_INFORMATION var10);

    public boolean CreateProcessW(String var1, char[] var2, WinBase.SECURITY_ATTRIBUTES var3, WinBase.SECURITY_ATTRIBUTES var4, boolean var5, WinDef.DWORD var6, Pointer var7, String var8, WinBase.STARTUPINFO var9, WinBase.PROCESS_INFORMATION var10);

    public WinNT.HANDLE OpenProcess(int var1, boolean var2, int var3);

    public boolean QueryFullProcessImageName(WinNT.HANDLE var1, int var2, char[] var3, IntByReference var4);

    public WinDef.DWORD GetTempPath(WinDef.DWORD var1, char[] var2);

    public WinDef.DWORD GetVersion();

    public boolean GetVersionEx(WinNT.OSVERSIONINFO var1);

    public boolean GetVersionEx(WinNT.OSVERSIONINFOEX var1);

    public boolean VerifyVersionInfoW(WinNT.OSVERSIONINFOEX var1, int var2, long var3);

    public long VerSetConditionMask(long var1, int var3, byte var4);

    public void GetSystemInfo(WinBase.SYSTEM_INFO var1);

    public void GetNativeSystemInfo(WinBase.SYSTEM_INFO var1);

    public boolean IsWow64Process(WinNT.HANDLE var1, IntByReference var2);

    public boolean GetLogicalProcessorInformation(Pointer var1, WinDef.DWORDByReference var2);

    public boolean GetLogicalProcessorInformationEx(int var1, Pointer var2, WinDef.DWORDByReference var3);

    public boolean GlobalMemoryStatusEx(WinBase.MEMORYSTATUSEX var1);

    public boolean GetFileInformationByHandleEx(WinNT.HANDLE var1, int var2, Pointer var3, WinDef.DWORD var4);

    public boolean SetFileInformationByHandle(WinNT.HANDLE var1, int var2, Pointer var3, WinDef.DWORD var4);

    public boolean GetFileTime(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4);

    public int SetFileTime(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4);

    public boolean SetFileAttributes(String var1, WinDef.DWORD var2);

    public WinDef.DWORD GetLogicalDriveStrings(WinDef.DWORD var1, char[] var2);

    public boolean GetDiskFreeSpace(String var1, WinDef.DWORDByReference var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

    public boolean GetDiskFreeSpaceEx(String var1, WinNT.LARGE_INTEGER var2, WinNT.LARGE_INTEGER var3, WinNT.LARGE_INTEGER var4);

    public boolean DeleteFile(String var1);

    public boolean CreatePipe(WinNT.HANDLEByReference var1, WinNT.HANDLEByReference var2, WinBase.SECURITY_ATTRIBUTES var3, int var4);

    public boolean CallNamedPipe(String var1, byte[] var2, int var3, byte[] var4, int var5, IntByReference var6, int var7);

    public boolean ConnectNamedPipe(WinNT.HANDLE var1, WinBase.OVERLAPPED var2);

    public WinNT.HANDLE CreateNamedPipe(String var1, int var2, int var3, int var4, int var5, int var6, int var7, WinBase.SECURITY_ATTRIBUTES var8);

    public boolean DisconnectNamedPipe(WinNT.HANDLE var1);

    public boolean GetNamedPipeClientComputerName(WinNT.HANDLE var1, char[] var2, int var3);

    public boolean GetNamedPipeClientProcessId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

    public boolean GetNamedPipeClientSessionId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

    public boolean GetNamedPipeHandleState(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5, char[] var6, int var7);

    public boolean GetNamedPipeInfo(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5);

    public boolean GetNamedPipeServerProcessId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

    public boolean GetNamedPipeServerSessionId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

    public boolean PeekNamedPipe(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6);

    public boolean SetNamedPipeHandleState(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4);

    public boolean TransactNamedPipe(WinNT.HANDLE var1, byte[] var2, int var3, byte[] var4, int var5, IntByReference var6, WinBase.OVERLAPPED var7);

    public boolean WaitNamedPipe(String var1, int var2);

    public boolean SetHandleInformation(WinNT.HANDLE var1, int var2, int var3);

    public int GetFileAttributes(String var1);

    public int GetFileType(WinNT.HANDLE var1);

    public boolean DeviceIoControl(WinNT.HANDLE var1, int var2, Pointer var3, int var4, Pointer var5, int var6, IntByReference var7, Pointer var8);

    public WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD var1, WinDef.DWORD var2);

    public boolean Process32First(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32 var2);

    public boolean Process32Next(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32 var2);

    public boolean Thread32First(WinNT.HANDLE var1, Tlhelp32.THREADENTRY32 var2);

    public boolean Thread32Next(WinNT.HANDLE var1, Tlhelp32.THREADENTRY32 var2);

    public boolean SetEnvironmentVariable(String var1, String var2);

    public int GetEnvironmentVariable(String var1, char[] var2, int var3);

    public Pointer GetEnvironmentStrings();

    public boolean FreeEnvironmentStrings(Pointer var1);

    public WinDef.LCID GetSystemDefaultLCID();

    public WinDef.LCID GetUserDefaultLCID();

    public int GetPrivateProfileInt(String var1, String var2, int var3, String var4);

    public WinDef.DWORD GetPrivateProfileString(String var1, String var2, String var3, char[] var4, WinDef.DWORD var5, String var6);

    public boolean WritePrivateProfileString(String var1, String var2, String var3, String var4);

    public WinDef.DWORD GetPrivateProfileSection(String var1, char[] var2, WinDef.DWORD var3, String var4);

    public WinDef.DWORD GetPrivateProfileSectionNames(char[] var1, WinDef.DWORD var2, String var3);

    public boolean WritePrivateProfileSection(String var1, String var2, String var3);

    public boolean FileTimeToLocalFileTime(WinBase.FILETIME var1, WinBase.FILETIME var2);

    public boolean SystemTimeToTzSpecificLocalTime(WinBase.TIME_ZONE_INFORMATION var1, WinBase.SYSTEMTIME var2, WinBase.SYSTEMTIME var3);

    public boolean SystemTimeToFileTime(WinBase.SYSTEMTIME var1, WinBase.FILETIME var2);

    public boolean FileTimeToSystemTime(WinBase.FILETIME var1, WinBase.SYSTEMTIME var2);

    @Deprecated
    public WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, WinBase.FOREIGN_THREAD_START_ROUTINE var4, Pointer var5, WinDef.DWORD var6, Pointer var7);

    public WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, Pointer var4, Pointer var5, int var6, WinDef.DWORDByReference var7);

    public boolean WriteProcessMemory(WinNT.HANDLE var1, Pointer var2, Pointer var3, int var4, IntByReference var5);

    public boolean ReadProcessMemory(WinNT.HANDLE var1, Pointer var2, Pointer var3, int var4, IntByReference var5);

    public BaseTSD.SIZE_T VirtualQueryEx(WinNT.HANDLE var1, Pointer var2, WinNT.MEMORY_BASIC_INFORMATION var3, BaseTSD.SIZE_T var4);

    public boolean DefineDosDevice(int var1, String var2, String var3);

    public int QueryDosDevice(String var1, char[] var2, int var3);

    public WinNT.HANDLE FindFirstFile(String var1, Pointer var2);

    public WinNT.HANDLE FindFirstFileEx(String var1, int var2, Pointer var3, int var4, Pointer var5, WinDef.DWORD var6);

    public boolean FindNextFile(WinNT.HANDLE var1, Pointer var2);

    public boolean FindClose(WinNT.HANDLE var1);

    public WinNT.HANDLE FindFirstVolumeMountPoint(String var1, char[] var2, int var3);

    public boolean FindNextVolumeMountPoint(WinNT.HANDLE var1, char[] var2, int var3);

    public boolean FindVolumeMountPointClose(WinNT.HANDLE var1);

    public boolean GetVolumeNameForVolumeMountPoint(String var1, char[] var2, int var3);

    public boolean SetVolumeLabel(String var1, String var2);

    public boolean SetVolumeMountPoint(String var1, String var2);

    public boolean DeleteVolumeMountPoint(String var1);

    public boolean GetVolumeInformation(String var1, char[] var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6, char[] var7, int var8);

    public boolean GetVolumePathName(String var1, char[] var2, int var3);

    public boolean GetVolumePathNamesForVolumeName(String var1, char[] var2, int var3, IntByReference var4);

    public WinNT.HANDLE FindFirstVolume(char[] var1, int var2);

    public boolean FindNextVolume(WinNT.HANDLE var1, char[] var2, int var3);

    public boolean FindVolumeClose(WinNT.HANDLE var1);

    public boolean GetCommState(WinNT.HANDLE var1, WinBase.DCB var2);

    public boolean GetCommTimeouts(WinNT.HANDLE var1, WinBase.COMMTIMEOUTS var2);

    public boolean SetCommState(WinNT.HANDLE var1, WinBase.DCB var2);

    public boolean SetCommTimeouts(WinNT.HANDLE var1, WinBase.COMMTIMEOUTS var2);

    public boolean ProcessIdToSessionId(int var1, IntByReference var2);

    public WinDef.HMODULE LoadLibraryEx(String var1, WinNT.HANDLE var2, int var3);

    public WinDef.HRSRC FindResource(WinDef.HMODULE var1, Pointer var2, Pointer var3);

    public WinNT.HANDLE LoadResource(WinDef.HMODULE var1, WinDef.HRSRC var2);

    public Pointer LockResource(WinNT.HANDLE var1);

    public int SizeofResource(WinDef.HMODULE var1, WinNT.HANDLE var2);

    public boolean FreeLibrary(WinDef.HMODULE var1);

    public boolean EnumResourceTypes(WinDef.HMODULE var1, WinBase.EnumResTypeProc var2, Pointer var3);

    public boolean EnumResourceNames(WinDef.HMODULE var1, Pointer var2, WinBase.EnumResNameProc var3, Pointer var4);

    public boolean Module32FirstW(WinNT.HANDLE var1, Tlhelp32.MODULEENTRY32W var2);

    public boolean Module32NextW(WinNT.HANDLE var1, Tlhelp32.MODULEENTRY32W var2);

    public int SetErrorMode(int var1);

    public Pointer GetProcAddress(WinDef.HMODULE var1, int var2) throws LastErrorException;

    public int SetThreadExecutionState(int var1);

    public int ExpandEnvironmentStrings(String var1, Pointer var2, int var3);

    public boolean GetProcessTimes(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4, WinBase.FILETIME var5);

    public boolean GetProcessIoCounters(WinNT.HANDLE var1, WinNT.IO_COUNTERS var2);

    public WinNT.HANDLE CreateMutex(WinBase.SECURITY_ATTRIBUTES var1, boolean var2, String var3);

    public WinNT.HANDLE OpenMutex(int var1, boolean var2, String var3);

    public boolean ReleaseMutex(WinNT.HANDLE var1);

    public void ExitProcess(int var1);

    public Pointer VirtualAllocEx(WinNT.HANDLE var1, Pointer var2, BaseTSD.SIZE_T var3, int var4, int var5);

    public boolean GetExitCodeThread(WinNT.HANDLE var1, IntByReference var2);

    public boolean VirtualFreeEx(WinNT.HANDLE var1, Pointer var2, BaseTSD.SIZE_T var3, int var4);

    public WinNT.HRESULT RegisterApplicationRestart(char[] var1, int var2);

    public WinNT.HRESULT UnregisterApplicationRestart();

    public WinNT.HRESULT GetApplicationRestartSettings(WinNT.HANDLE var1, char[] var2, IntByReference var3, IntByReference var4);
}

