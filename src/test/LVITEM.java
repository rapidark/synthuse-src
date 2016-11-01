package test;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

/**
 *  
 * @author Darkness
 * @date 2016年10月31日 下午5:56:12
 * @version V1.0
 */
public  class LVITEM extends Structure {
    public WinDef.UINT mask;
    public int iItem; 
    public int iSubItem; 
    public WinDef.UINT state; 
    public WinDef.UINT stateMask; 
    public Pointer pszText;
    public int cchTextMax; 
    public int iImage; 
    public WinDef.LPARAM lParam; 
    public int iIndent; 
    public int iGoupId; 
    public WinDef.UINT cColumns; 
    public WinDef.UINT puColumns; 

    @Override
	protected List getFieldOrder() {
		return Arrays.asList(
				new String[] { "mask", "iItem", "iSubItem", "state", "stateMask", "pszText", "cchTextMax", "iImage", "lParam", "iIndent", "iGoupId", "cColumns", "puColumns" });
	}

    //Constructor
    public LVITEM() { 
        Memory m = new Memory(260); 
        mask = new WinDef.UINT((long)1); //code for LVIF_TEXT
        iItem = 0; 
        iSubItem = 0; //no subitem
        pszText = m.getPointer(0); 
        cchTextMax = 260; 
        iImage = 0; 
        lParam = new WinDef.LPARAM(0); 
        iIndent = 0; 
    }
}
