/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski7@gmail.com
*/


// MsgHook.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"


LRESULT CALLBACK CwpHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{   
	COPYDATASTRUCT  CDS;
	HEVENT          Event;

	CDS.dwData = 0;
	CDS.cbData = sizeof(Event);
	CDS.lpData = &Event;

	//if (nCode == HC_ACTION)
	{
		//For WH_CALLWNDPROC hook a pointer to a CWPSTRUCT structure that contains details about the message. 
		CWPSTRUCT *cwps = (CWPSTRUCT *)lParam; 
		Event.hWnd = cwps->hwnd;
		Event.lParam = cwps->lParam;
		Event.wParam = cwps->wParam;
		Event.nCode = cwps->message;
		Event.dwHookType = WH_CALLWNDPROC;
		memset((void *)&Event.wParamStr, '\0', sizeof(TCHAR) * 25);
		memset((void *)&Event.lParamStr, '\0', sizeof(TCHAR) * 25);
		//if (cwps->message == WM_SETTEXT && cwps->lParam != 0)
		//	_tcscpy_s(Event.lParamStr, 25, (const wchar_t*)Event.lParam);

		BOOL bRes = (BOOL)SendMessage(pData->g_hWnd, WM_COPYDATA, 0, (LPARAM)(VOID*)&CDS); // ask the controlling program if the hook should be passed
	}
	return CallNextHookEx(pData->g_CwpHook, nCode, wParam, lParam);  // pass hook to next handler
	//return bRes;  // Don't tell the other hooks about this message.
}

LRESULT CALLBACK MsgHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{   
	COPYDATASTRUCT  CDS;
	HEVENT          Event;

	CDS.dwData = 0;
	CDS.cbData = sizeof(Event);
	CDS.lpData = &Event;

	//if (nCode == HC_ACTION)
	{
		//For WH_GETMESSAGE hook a pointer to a MSG structure that contains details about the message. 
		MSG *msg = (MSG *)lParam; 
		Event.hWnd = msg->hwnd;
		Event.lParam = msg->lParam;
		Event.wParam = msg->wParam;
		Event.nCode = msg->message;
		Event.dwHookType = WH_GETMESSAGE;
		memset((void *)&Event.wParamStr, '\0', sizeof(TCHAR) * 25);
		memset((void *)&Event.lParamStr, '\0', sizeof(TCHAR) * 25);
		//if (msg->message == WM_SETTEXT && msg->lParam != 0)
		//	_tcscpy_s(Event.lParamStr, 25, (const wchar_t*)Event.lParam);
		//if (msg->message == WM_COMMAND || msg->message == WM_MENUCOMMAND) //infinite loop?
		BOOL bRes = (BOOL)SendMessage(pData->g_hWnd, WM_COPYDATA, 0, (LPARAM)(VOID*)&CDS); // ask the controlling program if the hook should be passed
	}
	
	return CallNextHookEx(pData->g_MsgHook, nCode, wParam, lParam);  // pass hook to next handler
	//return bRes;  // Don't tell the other hooks about this message.
}

extern "C" __declspec(dllexport) BOOL SetMsgHook(HWND callerHWnd, DWORD threadId)
{
	if(bStartingProcess) // if we're just starting the DLL for the first time,
	{
		pData->g_hWnd   = callerHWnd; // remember the windows and hook handle for further instances
		pData->g_CwpHook  = SetWindowsHookEx(WH_CALLWNDPROC, (HOOKPROC)CwpHookProc, (HINSTANCE)pData->g_hInstance, threadId);
		//pData->g_MsgHook  = SetWindowsHookEx(WH_GETMESSAGE, (HOOKPROC)MsgHookProc, (HINSTANCE)pData->g_hInstance, threadId);   

		return (pData->g_CwpHook != NULL); //pData->g_CwpHook != NULL && 
	}
	else 
		return false;
}

extern "C" __declspec(dllexport) BOOL RemoveHook()
{
	if (pData == NULL)
		return false;
	if(pData->g_MsgHook)       // if the hook is defined
	{
		UnhookWindowsHookEx(pData->g_MsgHook);
		pData->g_MsgHook = NULL;
	}
	if(pData->g_CwpHook)       // if the hook is defined
	{
		BOOL ret = UnhookWindowsHookEx(pData->g_CwpHook);
		pData->g_hWnd = NULL;  // reset data
		pData->g_CwpHook = NULL;
		return ret;
	}
	return false;
}