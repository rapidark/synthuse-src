package test2;

import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

/**
 *  
 * @author Darkness
 * @date 2016年11月1日 上午10:00:29
 * @version V1.0
 */
public class WindowsMonitor {
	
	public interface User32 extends W32APIOptions {
		User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);
//		//[DllImport("user32.dll")]//查找窗口
        int FindWindow(
                                            String strClassName,    //窗口类名
                                            String strWindowName    //窗口标题
        );

//        //[DllImport("user32.dll")]//在窗口列表中寻找与指定条件相符的第一个子窗口
        int FindWindowEx(
                                              int hwndParent, // handle to parent window
                                            int hwndChildAfter, // handle to child window
                                            String className, //窗口类名            
                                            String windowName // 窗口标题
        );
        //[DllImport("user32.DLL")]
        int SendMessage(int hWnd, UINT Msg, int wParam, int lParam);
        //[DllImport("user32.dll")]//找出某个窗口的创建者(线程或进程),返回创建者的标志符
        int GetWindowThreadProcessId(int hwnd, IntByReference processId);
	}
	
	public interface Kernel32 extends W32APIOptions {
		Kernel32 instance = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS);

        //[DllImport("kernel32.dll")]//打开一个已存在的进程对象,并返回进程的句柄
        int OpenProcess(UINT dwDesiredAccess, boolean bInheritHandle, int processId);
        //[DllImport("kernel32.dll")]//为指定的进程分配内存地址:成功则返回分配内存的首地址
        int VirtualAllocEx(int hProcess, Pointer lpAddress, UINT dwSize, UINT flAllocationType, UINT flProtect);
        //[DllImport("kernel32.dll")]//从指定内存中读取字节集数据
        boolean ReadProcessMemory(
                                            int hProcess, //被读取者的进程句柄
                                            int lpBaseAddress,//开始读取的内存地址
                                            Pointer lpBuffer, //数据存储变量
                                            int nSize, //要写入多少字节
                                            UINTByReference vNumberOfBytesRead//读取长度
        );
        //[DllImport("kernel32.dll")]//将数据写入内存中
//        boolean WriteProcessMemory(
//                int hProcess,//由OpenProcess返回的进程句柄
//                int lpBaseAddress, //要写的内存首地址,再写入之前,此函数将先检查目标地址是否可用,并能容纳待写入的数据
//                IntByReference lpBuffer, //指向要写的数据的指针
//                int nSize, //要写入的字节数
//                UINTByReference vNumberOfBytesRead
//);
        boolean WriteProcessMemory(
                int hProcess,//由OpenProcess返回的进程句柄
                int lpBaseAddress, //要写的内存首地址,再写入之前,此函数将先检查目标地址是否可用,并能容纳待写入的数据
                Pointer lpBuffer, //指向要写的数据的指针
                int nSize, //要写入的字节数
                UINTByReference vNumberOfBytesRead
);
        //[DllImport("kernel32.dll")]
        boolean CloseHandle(int handle);
        //[DllImport("kernel32.dll")]//在其它进程中释放申请的虚拟内存空间
        boolean VirtualFreeEx(
                                    int hProcess,//目标进程的句柄,该句柄必须拥有PROCESS_VM_OPERATION的权限
                                    int lpAddress,//指向要释放的虚拟内存空间首地址的指针
                                    UINT dwSize,
                                    UINT dwFreeType//释放类型
        );
	}
	
	/// <summary>
    /// LVITEM结构体,是列表视图控件的一个重要的数据结构
    /// 占空间：4(int)x7=28个byte
    /// </summary>
    public static class LVITEM extends Structure  //结构体
    {
    	private static final int MEMSIZE = 260;
        
        //public int a;
        public int mask;//说明此结构中哪些成员是有效的
        public int iItem;//项目的索引值(可以视为行号)从0开始
        public int iSubItem; //子项的索引值(可以视为列号)从0开始
        public int state;//子项的状态
        public int stateMask; //状态有效的屏蔽位
        public Pointer pszText = new Memory(MEMSIZE);;  //主项或子项的名称
        public int cchTextMax = MEMSIZE;//pszText所指向的缓冲区大小
        
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(
					new String[] { 
							"mask", "iItem", "iSubItem", "state", "stateMask", 
							"pszText", "cchTextMax" 
//							,"iImage", "lParam", "iIndent", 
//							"iGoupId", "cColumns", "puColumns" 
				});
		}
    }
    
	int hwnd;   //窗口句柄
    int process;//进程句柄
    int pointer;
    int pointer2;
    private static final UINT LVM_FIRST = new UINT(0x1000);
    private static final UINT LVM_GETITEMCOUNT = new UINT(LVM_FIRST.longValue() + 4);;//获取列表行数
    private static final UINT LVM_GETHEADER = new UINT(LVM_FIRST.longValue() + 31);;
    private static final UINT LVM_GETITEMTEXT = new UINT(LVM_FIRST.longValue() + 45);;//获取列表内的内容
    private static final UINT LVM_GETITEMW = new UINT(LVM_FIRST.longValue() + 75);;

    private static final UINT HDM_GETITEMCOUNT = new UINT(0x1200);;//获取列表列数

    private static final UINT PROCESS_VM_OPERATION = new UINT(0x0008);;//允许函数VirtualProtectEx使用此句柄修改进程的虚拟内存
    private static final UINT PROCESS_VM_READ = new UINT(0x0010);;//允许函数访问权限
    private static final UINT PROCESS_VM_WRITE = new UINT(0x0020);;//允许函数写入权限

    private static final UINT MEM_COMMIT = new UINT(0x1000);;//为特定的页面区域分配内存中或磁盘的页面文件中的物理存储
    private static final UINT MEM_RELEASE = new UINT(0x8000);;
    private static final UINT MEM_RESERVE = new UINT(0x2000);;//保留进程的虚拟地址空间,而不分配任何物理存储

    private static final UINT PAGE_READWRITE = new UINT(4);;

    private int LVIF_TEXT = 0x0001;
    
    private User32 user32 = User32.instance;
    private Kernel32 kernel32 = Kernel32.instance;
    
  /// <summary>  
    /// LV列表总行数
    /// </summary>
    private int ListView_GetItemRows(int handle)
    {
        return user32.SendMessage(handle, LVM_GETITEMCOUNT, 0, 0);
    }
    /// <summary>  
    /// LV列表总列数
    /// </summary>
    private int ListView_GetItemCols(int handle)
    {
        return user32.SendMessage(handle, HDM_GETITEMCOUNT, 0, 0);
    }

    private void displayGridInfo()
    {
        int headerhwnd; //listview控件的列头句柄
        int rows, cols;  //listview控件中的行列数
        int processId; //进程pid  

        hwnd = user32.FindWindow("#32770", "Windows 任务管理器");
        hwnd = user32.FindWindowEx(hwnd, 0, "#32770", null);
        hwnd = user32.FindWindowEx(hwnd, 0, "SysListView32", null);//进程界面窗口的句柄,通过SPY获取
        headerhwnd = user32.SendMessage(hwnd, LVM_GETHEADER, 0, 0);//listview的列头句柄

        rows = ListView_GetItemRows(hwnd);//总行数，即进程的数量
        cols = ListView_GetItemCols(headerhwnd);//列表列数
        IntByReference intByReference = new IntByReference();
        user32.GetWindowThreadProcessId(hwnd, intByReference);
        processId = intByReference.getValue();

        //打开并插入进程
        UINT power = new UINT(PROCESS_VM_OPERATION.intValue() | PROCESS_VM_READ.intValue() | PROCESS_VM_WRITE.intValue());
        process = kernel32.OpenProcess(power, false, processId);
        //申请代码的内存区,返回申请到的虚拟内存首地址
        pointer = kernel32.VirtualAllocEx(process, new Pointer(0), new UINT(4096), new UINT(MEM_RESERVE.intValue() | MEM_COMMIT.intValue()), PAGE_READWRITE);
        
        pointer2 = kernel32.VirtualAllocEx(process, new Pointer(0), new UINT(4096), new UINT(MEM_RESERVE.intValue() | MEM_COMMIT.intValue()), PAGE_READWRITE);
        
        String[][] tempStr;//二维数组
        String[] temp = new String[cols];

        tempStr = GetListViewItmeValue(1, 1);//将要读取的其他程序中的ListView控件中的文本内容保存到二维数组中
//        tempStr = GetListViewItmeValue(rows, cols);//将要读取的其他程序中的ListView控件中的文本内容保存到二维数组中

//        listView1.Items.Clear();//清空LV控件信息
        //输出数组中保存的其他程序的LV控件信息
//        for (int i = 0; i < rows; i++)
//        {
//            for (int j = 0; j < cols; j++)
//            {
//                temp[j] = tempStr[i][j];
//            }
//            ListViewItem lvi = new ListViewItem(temp);
//            listView1.Items.Add(lvi);
//        }
    }
    
    private void displayMemory1(String info) {
    	Memory memory = readMemory(process, pointer, 4096);
        System.out.println(info + ", m1["+memory.toString()+"]" +Arrays.toString(memory.getByteArray(0, 4096)));
    }
    private void displayMemory2(String info) {
    	Memory memory = readMemory(process, pointer2, 4096);
        System.out.println(info + ", m2["+memory.toString()+"]" +Arrays.toString(memory.getByteArray(0, 4096)));
    }
    
    private void displayMemory(Memory memory) {
    	System.out.println("memory:" + Arrays.toString(memory.getByteArray(0, 256)));
    }
    
    private void displayMemory(String info) {
    	displayMemory1(info);
    	displayMemory2(info);
    }

    /// <summary>
    /// 从内存中读取指定的LV控件的文本内容
    /// </summary>
    /// <param name="rows">要读取的LV控件的行数</param>
    /// <param name="cols">要读取的LV控件的列数</param>
    /// <returns>取得的LV控件信息</returns>
    private String[][] GetListViewItmeValue(int rows, int cols)
    {
    	String[][] tempStr = new String[rows][cols];//二维数组:保存LV控件的文本信息
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                int size = 40;// Marshal.SizeOf(typeof(LVITEM));
//                byte[] vBuffer = new byte[256];//定义一个临时缓冲区
                Memory vBuffer = new Memory(256);
                LVITEM vItem = new LVITEM();
                vItem.mask = LVIF_TEXT;//说明pszText是有效的
               // vItem[0].iItem = i;     //行号
               // vItem[0].iSubItem = j;  //列号
//                vItem.cchTextMax = (int)vBuffer.size();//所能存储的最大的文本为256字节
                Memory memory =(Memory) vItem.pszText;
//                vItem.pszText = new Pointer(pointer2);//memory;//new Pointer(pointer2);// ((int)pointer + size);
                displayMemory(memory);
                UINTByReference vNumberOfBytesRead = new UINTByReference(new UINT(0));
                Pointer tempPointer = vItem.pszText;
                System.out.println("temppointer:" + Pointer.nativeValue(tempPointer));
                
//                Pointer pointerTest = pointer2;
//                System.out.println("pointerTest:" + pointerTest);
//                System.out.println("pointerTest:" + Pointer.nativeValue(pointerTest));
                //把数据写到vItem中
                //pointer为申请到的内存的首地址
                //UnsafeAddrOfPinnedArrayElement:获取指定数组中指定索引处的元素的地址
                
                //IntPtr intPtr = Marshal.UnsafeAddrOfPinnedArrayElement(vItem, 0);
                long pa = Pointer.nativeValue(vItem.getPointer());
                IntByReference intPtr = new IntByReference((int)pa);
               
                displayMemory("before write");
                kernel32.WriteProcessMemory(process, pointer, vItem.getPointer(), size,  vNumberOfBytesRead);
                displayMemory("after write");
                
//                displayMemory("before send");
                //发送LVM_GETITEMW消息给hwnd,将返回的结果写入pointer指向的内存空间
                int isSuccess = user32.SendMessage(hwnd, new UINT(4171), i, pointer);
                System.out.println("isSuccess:" + isSuccess);
                displayMemory("after send");
                displayMemory(memory);
                //从pointer指向的内存地址开始读取数据,写入缓冲区vBuffer中
               // ReadProcessMemory(process, ((int)pointer + size), Marshal.UnsafeAddrOfPinnedArrayElement(vBuffer, 0), vBuffer.Length, ref vNumberOfBytesRead);
                Pointer bufferPointer = vBuffer.getPointer(0);
//                bufferPointer = Marshal.UnsafeAddrOfPinnedArrayElement(vBuffer, 0);
                 tempPointer = vItem.pszText;
                kernel32.ReadProcessMemory(process, pointer2, bufferPointer, (int)vBuffer.size(),  vNumberOfBytesRead);
                kernel32.ReadProcessMemory(process, (int)Pointer.nativeValue(tempPointer), bufferPointer, (int)vBuffer.size(),  vNumberOfBytesRead);
                String vText = "";
				try {
					vText = new String(vBuffer.getByteArray(0, vNumberOfBytesRead.getValue().intValue()), "gbk");
					System.out.println(vText);
					System.out.println(memory.getWideString(0));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//System.out.println("["+i+", "+j+"]" +vText.trim());
//                String vText = vBuffer.getWideString(0, vNumberOfBytesRead.getValue());
//                string vText = Encoding.Unicode.GetString(vBuffer, 0, (int)vNumberOfBytesRead); ;
                tempStr[i][j] = vText;
            }
        }
        kernel32.VirtualFreeEx(process, pointer, new UINT( 0), MEM_RELEASE);//在其它进程中释放申请的虚拟内存空间,MEM_RELEASE方式很彻底,完全回收
        kernel32.CloseHandle(process);//关闭打开的进程对象
        return tempStr;
    }
    
    public static Memory readMemory(int process, int address, int bytesToRead) {
        Memory output = new Memory(bytesToRead);
        UINTByReference readedBytes = new UINTByReference();
        Kernel32.instance.ReadProcessMemory(process, address, output, bytesToRead, readedBytes);
        return output;
    }
    
    public static void main(String[] args) {
		new WindowsMonitor().displayGridInfo();
	}
}
