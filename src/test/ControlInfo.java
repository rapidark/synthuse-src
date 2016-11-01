package test;
import java.util.ArrayList;
import java.util.List;

import org.synthuse.Api;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;


/**
 *  
 * @author Darkness
 * @date 2016年10月26日 下午1:49:48
 * @version V1.0
 */
public class ControlInfo {

	int level;
	HWND hwnd;
	long hwndValue;
	long controlId;
	String text;
	String className;
	List<ControlInfo> children;
	
	public ControlInfo(HWND hwnd, int level,String className, String text) {
		this.hwnd = hwnd;
		this.hwndValue = Pointer.nativeValue(hwnd.getPointer());
		this.controlId = Api.User32Ex.instance.GetDlgCtrlID(hwnd);
		this.level = level;
		this.className = className;
		this.text = text.trim();
		this.children = new ArrayList<>();
	}
	
	public void addChild(ControlInfo child) {
		this.children.add(child);
	}
	
	@Override
	public String toString() {
		String temp = temp(level) + hwndValue + ",className:" + className +  ",text:" + this.text+ ",controlId:" +controlId;
		return temp  + toString(children);
	}
	
	public long getHwndValue() {
		return hwndValue;
	}
	
	private String toString(List<ControlInfo> children2) {
		String result = "";
		for (ControlInfo controlInfo : children2) {
			result += controlInfo.toString();
		}
		return result;
	}

	private String temp(int level) {
		String result = "\n";
		for (int i = 0; i < level; i++) {
			result += "\t";
		}
		return result + "";
	}

	public void setChildren(List<ControlInfo> children) {
		this.children = children;
	}
}
