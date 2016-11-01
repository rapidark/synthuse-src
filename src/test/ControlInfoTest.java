package test;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.synthuse.Api;
import org.synthuse.Api.User32Ex;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * 
 * @author Darkness
 * @date 2016年10月26日 下午12:21:42
 * @version V1.0
 */
public class ControlInfoTest {

	public static void main(String[] args) throws Exception {
		// System.setProperty("jna.encoding","GBK");
		// HWND hWnd = User32.FindWindow("TXGuiFoundation", "StockAuto");
		// System.out.println(hWnd);
		// HWND hWndd = User32.FindWindow(null, "StockAuto");
		// System.out.println(hWndd);
		// Afx:400000:b:10003:6:78522cb
		// "Afx:400000:b:10003:6:38fb2193"

		String title = "网上股票交易系统5.0";
		User32 user32 = User32.INSTANCE;
		HWND top_hwnd = user32.FindWindow(null, title);//2497228
		System.out.println(top_hwnd);
		HWND Frame = Api.User32Ex.instance.GetDlgItem(top_hwnd, 59648);
		System.out.println(Frame);

		// List<Object[]> windows = dumpWindow(top_hwnd,null,null);
		// for (Object[] objects : windows) {
		// System.out.println("=========================");
		// System.out.println(Arrays.toString(objects));
		//
		// List<Object[]> child = dumpWindow((HWND)objects[0],null,null);
		// for (Object[] objects2 : child) {
		// System.out.println(Arrays.toString(objects2));
		// }
		// }
		if (top_hwnd==null) {
			System.out.println("没有找到程序");
			return;
		}
		List<ControlInfo> windows = dumpWindow(top_hwnd, 0, null, null);
		for (ControlInfo controlInfo : windows) {
			System.out.println(controlInfo);
		}

		System.out.println("===========================");
//		List<ControlInfo> result = dumpWindow(new HWND(2039936), 1, null, null);
//		for (ControlInfo controlInfo : result) {
//			System.out.println(controlInfo);
//		}
	}
	
	private static String getText(HWND hwnd) {
		 byte[] buffer = new byte[1024];
	        User32Ex.instance.GetWindowTextA(hwnd, buffer, buffer.length);
				try {
					String text = new String(buffer, "gbk");
					return text;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return "";
				
	}
	
	private static String getClassName(HWND hwnd) {
		
		 char[] buffer2 = new char[1026];
			User32Ex.instance.GetClassName(hwnd, buffer2, 1026);
			String className = Native.toString(buffer2);
			return className;
	}
	
	private static List<ControlInfo> dumpWindow(HWND hwnd, int level, String className, String windowName) {
		// """
		// :param hwnd: 窗口句柄
		// :param wantedText: 指定子窗口名
		// :param wantedClass: 指定子窗口类名
		// :return: 返回父窗口下所有子窗体的句柄
		// """
		List<ControlInfo> windows = new ArrayList<>();
		try {
			HWND hwndChild = null;
			while (true) {
//				hwndChild = User32.FindWindowEx(hwnd, hwndChild, className, windowName);
				hwndChild = User32Ex.instance.FindWindowExA(hwnd, hwndChild, null, null);
				if (hwndChild != null) {// && hwndChild.getValue() != 0) {
					String textName = getText(hwndChild);
					boolean isVisiable = User32Ex.instance.IsWindowVisible(hwndChild);
					if(!isVisiable) {
						continue;
					}
					// String className2 = User32.get(hwndChild,
					// hwndChild.getPointer(), 1);
//					Pointer pointer = Pointer.createPointer(100);
//					User32.GetClassName(hwndChild, pointer, 100);
					String className1 = getClassName(hwndChild);// pointer.getAsString();

					ControlInfo controlInfo = new ControlInfo(hwndChild, level, className1, textName);

					List<ControlInfo> children = dumpWindow(hwndChild, level + 1, className, windowName);
					controlInfo.setChildren(children);
					windows.add(controlInfo);
				} else {
					return windows;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return windows;
	}
}
