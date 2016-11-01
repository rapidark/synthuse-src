package test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.synthuse.Api;
import org.synthuse.Api.User32Ex;
import org.synthuse.WindowInfo;
import org.synthuse.WindowsEnumeratedXml;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 *  
 * @author Darkness
 * @date 2016年10月31日 上午11:34:16
 * @version V1.0
 */
public class Ht {
	
	public static void main(String[] args) {
		String title = "网上股票交易系统5.0";//"Windows 任务管理器"
		User32 user32 = User32.INSTANCE;
		HWND topWindow = user32.FindWindow(null, title);
		
		System.out.println(children(topWindow).size());
		System.out.println(topWindow);
		WindowInfo wi = new WindowInfo(topWindow, false);
		System.out.println(wi);
//		System.out.println(WindowsEnumeratedXml.getWin32XmlMap(topWindow));
		Map<String, WindowInfo> children = EnumerateWin32ChildWindows(topWindow);
		int i = 0;
		for (String string : children.keySet()) {
//			System.out.println((i++) + "==>" + children.get(string));
		}
	}
	
	private static List<HWND> children(HWND parent) {
		List<HWND> result = new ArrayList<>();
		
		HWND child = null;
		while(true) {
			child = User32Ex.instance.FindWindowExA(parent, child, null, null);
			if(child != null) {
				result.add(child);
			} else {
				break;
			}
		}
		
		return result;
	}
	public static Map<String, WindowInfo> EnumerateWin32ChildWindows(HWND parentHwnd)
	{
		final Map<String, WindowInfo> infoList = new LinkedHashMap<String, WindowInfo>();
		
	    class ChildWindowCallback implements WinUser.WNDENUMPROC {
			@Override
			public boolean callback(HWND hWnd, Pointer lParam) {
				WindowInfo wi = new WindowInfo(hWnd, true);
				infoList.put(wi.hwndStr, wi);
				return true;
			}
	    }
	    
		Api.User32Ex.instance.EnumChildWindows(parentHwnd, new ChildWindowCallback(), new Pointer(0));
	    
		return infoList;
	}
	
//	public static LONG GetWindowLong(HWND hWnd,
//			int nIndex) throws NativeException, IllegalAccessException
//	{
//		
//		JNative GetWindowLong = new JNative(DLL_NAME, "GetWindowLongA");
//		GetWindowLong.setRetVal(Type.INT);
//		int pos = 0;
//		GetWindowLong.setParameter(pos++, hWnd.getValue());
//		GetWindowLong.setParameter(pos++, nIndex);
//		GetWindowLong.invoke();
//		pos = GetWindowLong.getRetValAsInt();
//		GetWindowLong.dispose();
//		return new LONG(pos);
//	}
	
}
