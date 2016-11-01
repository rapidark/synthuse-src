package test;

import java.awt.Point;

import org.synthuse.Api;
import org.synthuse.Api.Kernel32Ex;
import org.synthuse.Api.User32Ex;
import org.synthuse.Api.WinDefEx.LVITEM_VISTA;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 *  
 * @author Darkness
 * @date 2016年10月31日 下午2:30:51
 * @version V1.0
 */
public class WindowsMonitorTest {

	HWND hwnd;   //窗口句柄
	HANDLE process;//进程句柄
    IntByReference pLVI;
    IntByReference pBuffer;
    private static int LVM_FIRST = 0x1000;
    private static int LVM_GETITEMCOUNT = LVM_FIRST + 4;//获取列表行数
    private static int LVM_GETHEADER = LVM_FIRST + 31;
    private static int LVM_GETITEMTEXT = LVM_FIRST + 45;//获取列表内的内容
    private static int LVM_GETITEMW = LVM_FIRST + 75;
//
    private static int HDM_GETITEMCOUNT = 0x1200;//获取列表列数
//
//    private const uint PROCESS_VM_OPERATION = 0x0008;//允许函数VirtualProtectEx使用此句柄修改进程的虚拟内存
//    private const uint PROCESS_VM_READ = 0x0010;//允许函数访问权限
//    private const uint PROCESS_VM_WRITE = 0x0020;//允许函数写入权限
//
//    private const uint MEM_COMMIT = 0x1000;//为特定的页面区域分配内存中或磁盘的页面文件中的物理存储
//    private const uint MEM_RELEASE = 0x8000;
//    private const uint MEM_RESERVE = 0x2000;//保留进程的虚拟地址空间,而不分配任何物理存储
//
//    private const uint PAGE_READWRITE = 4;

    private int LVIF_TEXT = 0x0001;
//
//    [DllImport("user32.dll")]//查找窗口
//    private static extern int FindWindow(
//                                        string strClassName,    //窗口类名
//                                        string strWindowName    //窗口标题
//    );
//
//    [DllImport("user32.dll")]//在窗口列表中寻找与指定条件相符的第一个子窗口
//    private static extern int FindWindowEx(
//                                          int hwndParent, // handle to parent window
//                                        int hwndChildAfter, // handle to child window
//                                          string className, //窗口类名            
//                                          string windowName // 窗口标题
//    );
//    [DllImport("user32.DLL")]
//    private static extern int SendMessage(int hWnd, uint Msg, int wParam, int lParam);
//    [DllImport("user32.dll")]//找出某个窗口的创建者(线程或进程),返回创建者的标志符
//    private static extern int GetWindowThreadProcessId(int hwnd, out int processId);
//    [DllImport("kernel32.dll")]//打开一个已存在的进程对象,并返回进程的句柄
//    private static extern int OpenProcess(uint dwDesiredAccess, bool bInheritHandle, int processId);
//    [DllImport("kernel32.dll")]//为指定的进程分配内存地址:成功则返回分配内存的首地址
//    private static extern int VirtualAllocEx(int hProcess, IntPtr lpAddress, uint dwSize, uint flAllocationType, uint flProtect);
//    [DllImport("kernel32.dll")]//从指定内存中读取字节集数据
//    private static extern bool ReadProcessMemory(
//                                        int hProcess, //被读取者的进程句柄
//                                        int lpBaseAddress,//开始读取的内存地址
//                                        IntPtr lpBuffer, //数据存储变量
//                                        int nSize, //要写入多少字节
//                                        ref uint vNumberOfBytesRead//读取长度
//    );
//    [DllImport("kernel32.dll")]//将数据写入内存中
//    private static extern bool WriteProcessMemory(
//                                        int hProcess,//由OpenProcess返回的进程句柄
//                                        int lpBaseAddress, //要写的内存首地址,再写入之前,此函数将先检查目标地址是否可用,并能容纳待写入的数据
//                                        IntPtr lpBuffer, //指向要写的数据的指针
//                                        int nSize, //要写入的字节数
//                                        ref uint vNumberOfBytesRead
//    );
//    [DllImport("kernel32.dll")]
//    private static extern bool CloseHandle(int handle);
//    [DllImport("kernel32.dll")]//在其它进程中释放申请的虚拟内存空间
//    private static extern bool VirtualFreeEx(
//                                int hProcess,//目标进程的句柄,该句柄必须拥有PROCESS_VM_OPERATION的权限
//                                int lpAddress,//指向要释放的虚拟内存空间首地址的指针
//                                uint dwSize,
//                                uint dwFreeType//释放类型
//    );
    /// <summary>
    /// LVITEM结构体,是列表视图控件的一个重要的数据结构
    /// 占空间：4(int)x7=28个byte
    /// </summary>
//    private struct LVITEM  //结构体
//    {
//        //public int a;
//        public int mask;//说明此结构中哪些成员是有效的
//        public int iItem;//项目的索引值(可以视为行号)从0开始
//        public int iSubItem; //子项的索引值(可以视为列号)从0开始
//        public int state;//子项的状态
//        public int stateMask; //状态有效的屏蔽位
//        public IntPtr pszText;  //主项或子项的名称
//        public int cchTextMax;//pszText所指向的缓冲区大小
//    }
//    public Form1()
//    {
//        InitializeComponent();
//    }

    /// <summary>  
    /// LV列表总行数
    /// </summary>
    private long ListView_GetItemRows(HWND handle)
    {
        return Api.User32Ex.instance.SendMessage(handle, Api.LVM_GETITEMCOUNT, new WPARAM(0), 0).longValue();
    }
    /// <summary>  
    /// LV列表总列数
    /// </summary>
    private long ListView_GetItemCols(HWND handle)
    {
        return Api.User32Ex.instance.SendMessage(handle, HDM_GETITEMCOUNT, new WPARAM(0), 0).longValue();
    }
    
    private static HWND FindWindowEx(HWND parent, HWND afterChild, String className, String title) {
    	return User32Ex.instance.FindWindowEx(parent, afterChild, className, title);
    }

    private void button1_Click_1()
    {
        int headerhwnd; //listview控件的列头句柄
        long rows, cols;  //listview控件中的行列数
        int processId; //进程pid  
        
        User32 user32 = User32.INSTANCE;
		 hwnd = user32.FindWindow("#32770", "Windows 任务管理器");//2497228
		
//        hwnd = FindWindow("#32770", "Windows 任务管理器");
        hwnd = FindWindowEx(hwnd, null, "#32770", null);
      
        hwnd = FindWindowEx(hwnd, null, "SysListView32", null);//进程界面窗口的句柄,通过SPY获取
        System.out.println(hwnd);
        headerhwnd = User32Ex.instance.SendMessage(hwnd, LVM_GETHEADER, new WPARAM(0), 0).intValue();//listview的列头句柄
//
        rows = ListView_GetItemRows(hwnd);//总行数，即进程的数量
        HWND header = new HWND(new Pointer(headerhwnd));
        cols = ListView_GetItemCols(header);//列表列数
        System.out.println("rows:" + rows + ",cols:" + cols);
        PointerByReference reference = new PointerByReference();
        int resultValue = User32Ex.instance.GetWindowThreadProcessId(hwnd, reference);
        System.out.println(resultValue);
        System.out.println(Pointer.nativeValue(reference.getValue()));
//
//        //打开并插入进程
//        process = User32Ex.instance.OpenProcess(Api.PROCESS_VM_OPERATION | Api.PROCESS_VM_READ | Api.PROCESS_VM_WRITE, false,reference.getPointer());// processId);
         Pointer processPointer = Kernel32Ex.instance.OpenProcess(Api.PROCESS_VM_OPERATION | Api.PROCESS_VM_READ | Api.PROCESS_VM_WRITE, false,reference.getValue());// processId);
         process = new HANDLE(processPointer);
//        //申请代码的内存区,返回申请到的虚拟内存首地址
         // IntByReference VirtualAllocEx(HANDLE hProc, IntByReference addr, SIZE_T size, int allocType, int prot);
         IntByReference zero = new IntByReference();
         SIZE_T bufferSize = new SIZE_T(4096);
//         pointer = Kernel32Ex.instance.VirtualAllocEx(new HANDLE(processPointer), zero, bufferSize, Api.MEM_RESERVE | Api.MEM_COMMIT, Api.PAGE_READWRITE);
         pLVI = Kernel32Ex.instance.VirtualAllocEx(process, new Pointer(0), bufferSize, Api.MEM_RESERVE | Api.MEM_COMMIT, Api.PAGE_READWRITE);
          pBuffer = Kernel32Ex.instance.VirtualAllocEx(process, new Pointer(0), bufferSize, Api.MEM_RESERVE | Api.MEM_COMMIT, Api.PAGE_READWRITE);
        String[][] tempStr;//二维数组
        int colsInt = (int)cols;
        String[] temp = new String[colsInt];
//
        tempStr = GetListViewItmeValue((int)rows, (int)cols);//将要读取的其他程序中的ListView控件中的文本内容保存到二维数组中
//
//        listView1.Items.Clear();//清空LV控件信息
//        //输出数组中保存的其他程序的LV控件信息
//        for (int i = 0; i < rows; i++)
//        {
//            for (int j = 0; j < cols; j++)
//            {
//                temp[j] = tempStr[i, j];
//            }
//            ListViewItem lvi = new ListViewItem(temp);
//            listView1.Items.Add(lvi);
//        }
    }

    /// <summary>
    /// 从内存中读取指定的LV控件的文本内容
    /// </summary>
    /// <param name="rows">要读取的LV控件的行数</param>
    /// <param name="cols">要读取的LV控件的列数</param>
    /// <returns>取得的LV控件信息</returns>
//    private String[][] GetListViewItemValue() {
//return null;
//def _readListViewItems(hwnd, column_index=0):
//    # Allocate virtual memory inside target process
//    pid = ctypes.create_string_buffer(4)
//    p_pid = ctypes.addressof(pid)
//    GetWindowThreadProcessId(hwnd, p_pid)  # process owning the given hwnd
//    hProcHnd = OpenProcess(win32con.PROCESS_ALL_ACCESS, False, struct.unpack("i", pid)[0])
//    pLVI = VirtualAllocEx(hProcHnd, 0, 4096, win32con.MEM_RESERVE | win32con.MEM_COMMIT, win32con.PAGE_READWRITE)
//    pBuffer = VirtualAllocEx(hProcHnd, 0, 4096, win32con.MEM_RESERVE | win32con.MEM_COMMIT, win32con.PAGE_READWRITE)
//
//    # Prepare an LVITEM record and write it to target process memory
//    lvitem_str = struct.pack('iiiiiiiii', *[0, 0, column_index, 0, 0, pBuffer, 4096, 0, 0])
//    lvitem_buffer = ctypes.create_string_buffer(lvitem_str)
//    copied = ctypes.create_string_buffer(4)
//    p_copied = ctypes.addressof(copied)
//    WriteProcessMemory(hProcHnd, pLVI, ctypes.addressof(lvitem_buffer), ctypes.sizeof(lvitem_buffer), p_copied)
//
//    # iterate items in the SysListView32 control
//    num_items = win32gui.SendMessage(hwnd, commctrl.LVM_GETITEMCOUNT)
//    item_texts = []
//    for item_index in range(num_items):
//        win32gui.SendMessage(hwnd, commctrl.LVM_GETITEMTEXT, item_index, pLVI)
//        target_buff = ctypes.create_string_buffer(4096)
//        ReadProcessMemory(hProcHnd, pBuffer, ctypes.addressof(target_buff), 4096, p_copied)
//        item_texts.append(target_buff.value)
//
//    VirtualFreeEx(hProcHnd, pBuffer, 0, win32con.MEM_RELEASE)
//    VirtualFreeEx(hProcHnd, pLVI, 0, win32con.MEM_RELEASE)
//    win32api.CloseHandle(hProcHnd)
//    return item_texts
//    }
    
    private String[][] GetListViewItmeValue(int rows, int cols)
    {
        String[][] tempStr = new String[rows][ cols];//二维数组:保存LV控件的文本信息
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
//            	lngMemVar2 = Kernel32.VirtualAllocEx(lngProcHandle, 0, lngMemLen2, Kernel32.MEM_RESERVE|Kernel32.MEM_COMMIT, Kernel32.PAGE_READWRITE);        
            	IntByReference zero = new IntByReference();
                SIZE_T bufferSize = new SIZE_T(4096);
                IntByReference lngMemVar2 = pBuffer;//Kernel32Ex.instance.VirtualAllocEx(process, zero, bufferSize, Api.MEM_RESERVE | Api.MEM_COMMIT, Api.PAGE_READWRITE);
            	
            	LVITEM_VISTA item = new LVITEM_VISTA();
            	item.mask = LVIF_TEXT;
            	item.iItem = i;
            	item.iSubItem = j;
            	item.pszText = lngMemVar2.getPointer();
            	int size = item.size();
//                int size = 40;// Marshal.SizeOf(typeof(LVITEM));
//                byte[] vBuffer = new byte[256];//定义一个临时缓冲区
//                LVITEM[] vItem = new LVITEM[1];
//                vItem[0].mask = LVIF_TEXT;//说明pszText是有效的
//               // vItem[0].iItem = i;     //行号
//               // vItem[0].iSubItem = j;  //列号
//                vItem[0].cchTextMax = vBuffer.Length;//所能存储的最大的文本为256字节
//                vItem[0].pszText = (IntPtr)((int)pointer + size);
//                uint vNumberOfBytesRead = 0;
//
//                //把数据写到vItem中
//                //pointer为申请到的内存的首地址
//                //UnsafeAddrOfPinnedArrayElement:获取指定数组中指定索引处的元素的地址
//                IntPtr intPtr = Marshal.UnsafeAddrOfPinnedArrayElement(vItem, 0);
            	//boolean WriteProcessMemory(HANDLE hProcess, IntByReference lpBaseAddress, Pointer lpBuffer, int len, IntByReference bytesWritten);
            	IntByReference vNumberOfBytesRead = new IntByReference();
            	Kernel32Ex.instance.WriteProcessMemory(process, pLVI, item, item.size(), vNumberOfBytesRead);
//
//                //发送LVM_GETITEMW消息给hwnd,将返回的结果写入pointer指向的内存空间
               User32Ex.instance.SendMessage(hwnd, LVM_GETITEMW, new WPARAM(i),pLVI);
//
//                //从pointer指向的内存地址开始读取数据,写入缓冲区vBuffer中
              
               //boolean ReadProcessMemory(Pointer hProcess, long inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);
               int strSize = 255;
               Memory lngVarPtr1 = new Memory(strSize + 1);
               Kernel32Ex.instance.ReadProcessMemory(process,  pBuffer, lngVarPtr1, strSize + 1, vNumberOfBytesRead);
//
               System.out.println("==" + lngVarPtr1.getString(0));
//                string vText = Encoding.Unicode.GetString(vBuffer, 0, (int)vNumberOfBytesRead); ;
//                tempStr[i, j] = vText;
            }
        }
//        VirtualFreeEx(process, pointer, 0, MEM_RELEASE);//在其它进程中释放申请的虚拟内存空间,MEM_RELEASE方式很彻底,完全回收
//        CloseHandle(process);//关闭打开的进程对象
        return tempStr;
    }
    
    public static void main(String[] args) {
		new WindowsMonitorTest().button1_Click_1();
	}
    
}
