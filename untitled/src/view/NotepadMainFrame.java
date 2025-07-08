package view;

import util.Clock;
import util.MQFontChooser;
import util.SystemParam;
import util.TestLine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;


enum Source{
    OPEN, SAVE, SAVE_AS, NEW_FILE, EXIT,
    PAGE_SETUP, PRINT,
    UNDO, REDO,
    CUT, COPY, PASTE, DELETE,
    FIND, FIND_NEXT, REPLACE, TURN_TO, SELECT_ALL,
    INSERT_TIME, TOGGLE_WRAP,
    FONT, BG_COLOR, FONT_COLOR,
    TOGGLE_STATUS, HELP, ABOUT

}
public class NotepadMainFrame extends JFrame implements ActionListener{
    //内容面板
    private JPanel contentPane;
    //编辑区
    private JTextArea textArea;
    //打开菜单项
    private JMenuItem itemOpen;
    //保存菜单项
    private JMenuItem itemSave;
    
    //1：新建 
    //2：修改过
    //3：保存过的
    int flag=0;//标志123

    //当前文件名
    String currentFileName=null;
    
     PrintJob  p=null;//声明一个要打印的对象
     Graphics  g=null;//要打印的对象
    
    //当前文件路径
    String currentPath=null;
    
    //背景颜色                             ，自控调色板
    JColorChooser jcc1=null;
    Color color=Color.BLACK;
    
    //文本的行数和列数
    int linenum = 1;
    int columnnum = 1;
    
    //撤销管理器
    public UndoManager undoMgr = new UndoManager(); 
    
    //使用系统剪贴板
    public Clipboard clipboard =  Toolkit.getDefaultToolkit().getSystemClipboard();
    
    private JMenuItem itemSaveAs;              //另存为
    private JMenuItem itemNew;				   //新建
    private JMenuItem itemPage;				   //页面设置
    private JSeparator separator;			   //分隔线
    private JMenuItem itemPrint;			   //打印
    private JMenuItem itemExit;				   //退出
    private JSeparator separator_1;			   //分隔线
    private JMenu itemEdit;					   //编辑
    private JMenu itFormat;					   //格式
    private JMenu itemCheck;				   //查看
    private JMenu itemHelp;					   //帮助
    private JMenuItem itemSearchForHelp;	   //查看帮助
    private JMenuItem itemAboutNotepad;		   //关于记事本
    private JMenuItem itemUndo;				   //撤销
    private JMenuItem itemCut;				   //剪切
    private JMenuItem itemCopy;				   //复制
    private JMenuItem itemPaste;			   //粘贴
    private JMenuItem itemDelete;			   //删除
    private JMenuItem itemFind;				   //查找
    private JMenuItem itemFindNext;			   //查找下一个
    private JMenuItem itemReplace;			   //替换
    private JMenuItem itemTurnTo;			   //转到
    private JMenuItem itemSelectAll;		   //全选
    private JMenuItem itemTime;				   //日期/时间
    private JMenuItem itemFont;				   //字体
    private JMenuItem itemColor;			   //字体颜色
    private JMenuItem itemFontColor;		   //背景颜色
    private JCheckBoxMenuItem itemNextLine;	   //自动换行
    private JScrollPane scrollPane;			   //滚动栏
    private JCheckBoxMenuItem itemStatement;   //状态栏
    private JToolBar toolState;
    //状态栏（时间，鼠标位置，人数）
    public static JLabel label1;
    private JLabel label2;
    private JLabel label3;
    int length=0;
    int sum=0;
    
    /**
     *  	主函数
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {//异步UI操作，避免多线程冲突
            try {
                NotepadMainFrame frame = new NotepadMainFrame();
                frame.setVisible(true);//显示所有UI操作
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    GregorianCalendar c=new GregorianCalendar();//状态栏的时间获取
    int hour=c.get(Calendar.HOUR_OF_DAY);
    int min=c.get(Calendar.MINUTE);
    int second=c.get(Calendar.SECOND);    
    private JPopupMenu popupMenu;         //右键弹出菜单
    private JMenuItem popM_Undo;		  //撤销
    private JMenuItem popM_Cut;			  //剪切
    private JMenuItem popM_Copy;		  //复制
    private JMenuItem popM_Paste;		  //粘贴
    private JMenuItem popM_Delete;		  //删除
    private JMenuItem popM_SelectAll;	  //全选
    private JMenuItem popM_toLeft;		  //从右到左的阅读顺序
    private JMenuItem popM_showUnicode;   //显示Unicode控制字符
    private JMenuItem popM_closeIMe;      //关闭IME
    private JMenuItem popM_InsertUnicode; //插入Unicode控制字符
    private JMenuItem popM_RestartSelect; //汉字重选
    private JSeparator separator_2;       //分隔线
    private JSeparator separator_3;		  //分隔线
    private JSeparator separator_4;       //分隔线
    private JSeparator separator_5;       //分隔线
    private JMenuItem itemRedo;			  //恢复
    private JSeparator separator_6;		  //分隔线
    private JSeparator separator_7;		  //分隔线
    private JSeparator separator_8;		  //分隔线
    private JMenuItem popM_Redo;		  //恢复

    /**
     * Create the frame.
     * 构造函数
     */
    public NotepadMainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//修改成当前系统风格（Windows）
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭后java程序自动退出
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        setTitle("记事本");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//为接下来的关闭提示是否保存做准备
        setBounds(100, 100, 721, 772);//初始位置和大小
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);//绑定菜单栏
        
        JMenu itemFile = new JMenu("文件(F)");
        itemFile.setMnemonic('F');	//设置快捷键Alt+"F"
        menuBar.add(itemFile);
        
        itemNew = new JMenuItem("新建(N)",'N');
        itemNew.setActionCommand(Source.NEW_FILE.name());//绑定枚举变量
        itemNew.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"N"
        itemNew.addActionListener(this);
        itemFile.add(itemNew);
        
        itemOpen = new JMenuItem("打开(O)",'O');
        itemOpen.setActionCommand(Source.OPEN.name());
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"O"
        itemOpen.addActionListener(this);
        itemFile.add(itemOpen);
        
        itemSave = new JMenuItem("保存(S)");
        itemSave.setActionCommand(Source.SAVE.name());
        itemSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                CTRL_DOWN_MASK));   //设置快捷键Ctrl+"S"
        itemSave.addActionListener(this);
        itemFile.add(itemSave);
        
        itemSaveAs = new JMenuItem("另存为");
        itemSaveAs.setActionCommand(Source.SAVE_AS.name());
        itemSaveAs.addActionListener(this);
        itemFile.add(itemSaveAs);
        
        separator = new JSeparator();  //添加分隔线
        itemFile.add(separator);
        
        itemPage = new JMenuItem("页面设置(U)",'U');
        itemPage.setActionCommand(Source.PAGE_SETUP.name());
        itemPage.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U,
                CTRL_DOWN_MASK));   //设置快捷键Ctrl+"U"
        itemPage.addActionListener(this);
        itemFile.add(itemPage);
        
        itemPrint = new JMenuItem("打印(P)...",'P');
        itemPrint.setActionCommand(Source.PRINT.name());
        itemPrint.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,
                CTRL_DOWN_MASK));   //置快捷键Ctrl+"P"
        itemPrint.addActionListener(this);
        itemFile.add(itemPrint);
        
        separator_1 = new JSeparator();  //添加分隔线
        itemFile.add(separator_1);
        
        itemExit = new JMenuItem("退出");
        itemExit.setActionCommand(Source.EXIT.name());
        itemExit.addActionListener(this);
        itemFile.add(itemExit);
        
        itemEdit = new JMenu("编辑(E)");
        itemEdit.setMnemonic('E');//设置快捷键Alt+"E"
        menuBar.add(itemEdit);
        
        itemUndo = new JMenuItem("撤销(Z)",'Z');
        itemUndo.setActionCommand(Source.UNDO.name());
        itemUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"Z"
        itemUndo.addActionListener(this);
        itemEdit.add(itemUndo);
        
        itemRedo = new JMenuItem("恢复(R)",'R');
        itemRedo.setActionCommand(Source.REDO.name());
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z,
                CTRL_DOWN_MASK | SHIFT_DOWN_MASK
        ));  // 设置快捷键为 Ctrl+Shift+Z
        itemRedo.addActionListener(this);
        itemEdit.add(itemRedo);
        
        itemCut = new JMenuItem("剪切(T)",'T');
        itemCut.setActionCommand(Source.CUT.name());
        itemCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"X"
        itemCut.addActionListener(this);
        
        separator_6 = new JSeparator();
        itemEdit.add(separator_6);
        itemEdit.add(itemCut);
        
        itemCopy = new JMenuItem("复制(C)",'C');
        itemCopy.setActionCommand(Source.COPY.name());
        itemCopy.addActionListener(this);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"C"
        itemEdit.add(itemCopy);
        
        itemPaste = new JMenuItem("粘贴(P)",'P');
        itemPaste.setActionCommand(Source.PASTE.name());
        itemPaste.addActionListener(this);
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"V"
        itemEdit.add(itemPaste);
        
        itemDelete = new JMenuItem("删除(L)",'L');
        itemDelete.setActionCommand(Source.DELETE.name());
        itemDelete.addActionListener(this);
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                CTRL_DOWN_MASK));    //设置快捷键Ctrl+"D"
        itemEdit.add(itemDelete);
        
        separator_7 = new JSeparator();
        itemEdit.add(separator_7);

        itemFind = new JMenuItem("查找(F)",'F');
        itemFind.setActionCommand(Source.FIND.name());
        itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                CTRL_DOWN_MASK));   //设置快捷键Ctrl+"F"
        itemFind.addActionListener(this);
        itemEdit.add(itemFind);
        
        itemFindNext = new JMenuItem("查找下一个(N)",'N');
        itemFindNext.setActionCommand(Source.FIND_NEXT.name());
        itemFindNext.setAccelerator(KeyStroke.getKeyStroke("F3"));
        itemFindNext.addActionListener(this);
        itemEdit.add(itemFindNext);
        
        itemReplace = new JMenuItem("替换(R)",'R');
        itemReplace.setActionCommand(Source.REPLACE.name());
        itemReplace.addActionListener(this);
        itemReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"H"
        itemEdit.add(itemReplace);
        
        itemTurnTo = new JMenuItem("转到(G)",'G');
        itemTurnTo.setActionCommand(Source.TURN_TO.name());
        itemTurnTo.addActionListener(this);
        itemTurnTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"G"
        itemEdit.add(itemTurnTo);
        
        itemSelectAll = new JMenuItem("全选(A)",'A');
        itemSelectAll.setActionCommand(Source.SELECT_ALL.name());
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                CTRL_DOWN_MASK));  //设置快捷键Ctrl+"A"
        itemSelectAll.addActionListener(this);
        
        separator_8 = new JSeparator();
        itemEdit.add(separator_8);
        itemEdit.add(itemSelectAll);
        
        itemTime = new JMenuItem("时间/日期(D)",'D');
        itemTime.setActionCommand(Source.INSERT_TIME.name());
        itemTime.addActionListener(this);
        itemTime.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemEdit.add(itemTime);
        
        itFormat = new JMenu("格式(O)");
        itFormat.setMnemonic('O');//设置快捷键Alt+"O"
        menuBar.add(itFormat);
        
        itemNextLine = new JCheckBoxMenuItem("自动换行(W)");
        itemNextLine.setActionCommand(Source.TOGGLE_WRAP.name());
        itemNextLine.addActionListener(this);
        itFormat.add(itemNextLine);
        
        itemFont = new JMenuItem("字体大小...");
        itemFont.setActionCommand(Source.FONT.name());
        itemFont.addActionListener(this);
        itFormat.add(itemFont);
        
        itemColor = new JMenuItem("背景颜色...");
        itemColor.setActionCommand(Source.BG_COLOR.name());
        itemColor.addActionListener(this);
        itFormat.add(itemColor);
        
        itemFontColor = new JMenuItem("字体颜色...");
        itemFontColor.setActionCommand(Source.FONT_COLOR.name());
        itemFontColor.addActionListener(this);
        itFormat.add(itemFontColor);
        
        itemCheck = new JMenu("查看(V)");
        itemCheck.setMnemonic('V');//设置快捷键Alt+"V"
        menuBar.add(itemCheck);
        
        itemStatement = new JCheckBoxMenuItem("状态栏(S)");
        itemStatement.setActionCommand(Source.TOGGLE_STATUS.name());
        itemStatement.addActionListener(this);
        itemCheck.add(itemStatement);
        
        itemHelp = new JMenu("帮助(H)");
        itemHelp.setMnemonic('H');//设置快捷键Alt+"H"
        menuBar.add(itemHelp);
        
        itemSearchForHelp = new JMenuItem("查看帮助(H)",'H');
        itemSearchForHelp.setActionCommand(Source.HELP.name());
        itemSearchForHelp.addActionListener(this);
        itemHelp.add(itemSearchForHelp);
        
        itemAboutNotepad = new JMenuItem("关于记事本(A)",'A');
        itemAboutNotepad.setActionCommand(Source.ABOUT.name());
        itemAboutNotepad.addActionListener(this);
        itemHelp.add(itemAboutNotepad);

        //自定义边框
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));//透明框
        contentPane.setLayout(new BorderLayout(0, 0));//紧密排列相邻组件
        setContentPane(contentPane);

        textArea = new JTextArea();
        
        //VERTICAL垂直always    HORIZONTAL水平as_need
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TestLine view = new TestLine();
        //添加行号
        scrollPane.setRowHeaderView(view);
        //添加到面板
        contentPane.add(scrollPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        addPopup(textArea, popupMenu);
        
        popM_Undo = new JMenuItem("撤销(Z)");
        popM_Undo.setActionCommand(Source.UNDO.name());
        popM_Undo.addActionListener(this);
        popupMenu.add(popM_Undo);
        
        popM_Redo = new JMenuItem("恢复(R)");
        popM_Redo.setActionCommand(Source.REDO.name());
        popM_Redo.addActionListener(this);
        popupMenu.add(popM_Redo);
        
        separator_2 = new JSeparator();
        popupMenu.add(separator_2);
        
        popM_Cut = new JMenuItem("剪切(T)");
        popM_Cut.setActionCommand(Source.CUT.name());
        popM_Cut.addActionListener(this);
        popupMenu.add(popM_Cut);
        
        popM_Copy = new JMenuItem("复制(C)");
        popM_Copy.setActionCommand(Source.COPY.name());
        popM_Copy.addActionListener(this);
        popupMenu.add(popM_Copy);
        
        popM_Paste = new JMenuItem("粘贴(P)");
        popM_Paste.setActionCommand(Source.PASTE.name());
        popM_Paste.addActionListener(this);
        popupMenu.add(popM_Paste);
        
        popM_Delete = new JMenuItem("删除(D)");
        popM_Delete.setActionCommand(Source.DELETE.name());
        popM_Delete.addActionListener(this);
        popupMenu.add(popM_Delete);
        
        separator_3 = new JSeparator();
        popupMenu.add(separator_3);
        
        popM_SelectAll = new JMenuItem("全选(A)");
        popM_SelectAll.setActionCommand(Source.SELECT_ALL.name());
        popM_SelectAll.addActionListener(this);
        popupMenu.add(popM_SelectAll);

        separator_4 = new JSeparator();
        popupMenu.add(separator_4);

        //添加撤销管理器
        textArea.getDocument().addUndoableEditListener(undoMgr);
                
        //设置状态栏
        toolState = new JToolBar();
        toolState.setSize(textArea.getSize().width, 10);
        label1 = new JLabel("    当前系统时间：" + hour + ":" + min + ":" + second+" ");
        toolState.add(label1);  //添加系统时间
        toolState.addSeparator();//分割线
        label2 = new JLabel("    第 " + linenum + " 行, 第 " + columnnum+" 列  ");
        toolState.add(label2);  //添加行数列数
        toolState.addSeparator();
        
        label3 = new JLabel("    一共 " +length+" 字  ");
        toolState.add(label3);  //添加字数统计
        //记录行数和列数，实时刷新
        textArea.addCaretListener(e -> {
            JTextArea editArea = (JTextArea)e.getSource();
            try {
                int caretpos = editArea.getCaretPosition();//获取光标位置
                linenum = editArea.getLineOfOffset(caretpos);//获取光标行数
                columnnum = caretpos - textArea.getLineStartOffset(linenum);//获取光标列数
                label2.setText("    第 " + (linenum+1) + " 行, 第 " + (columnnum+1)+" 列  ");
                length=NotepadMainFrame.this.textArea.getText().length();
                label3.setText("    一共 " +length+" 字  ");
            }
            catch(Exception ex) { }
        });
        
        contentPane.add(toolState, BorderLayout.SOUTH);  //将状态栏添加到面板底部
        toolState.setVisible(true);//可见
        toolState.setFloatable(true);//可以拖动
        Clock clock=new Clock();
        clock.start();//开启时钟线程
        
        
        
        // 创建弹出菜单
        final JPopupMenu jp=new JPopupMenu();    //创建弹出式菜单，下面三项是菜单项
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3)//只响应鼠标右键单击事件
                {
                    jp.show(e.getComponent(),e.getX(),e.getY());//在鼠标位置显示弹出式菜单
                }
            }
        });
        isChanged();//设置flag标志

        this.MainFrameWidowListener();
    }

    
    /*===============================1====================================*/
    /**
     * 是否有变化
     */
    private void isChanged() {
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                Character c=e.getKeyChar();
                if(!textArea.getText().isEmpty()){
                    flag=2;
                }
            }
        });
    }
    /*===================================================================*/
    
    
    /*===============================2====================================*/
    /**
     * 新建的或保存过的退出只有两个选择
     */
    private void MainFrameWidowListener() {
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                if(flag==2 && currentPath==null){
                    //这是弹出小窗口
                    //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到无标题?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if(result==JOptionPane.OK_OPTION){
                        NotepadMainFrame.this.saveAs();
                    }else if(result==JOptionPane.NO_OPTION){
                        NotepadMainFrame.this.dispose();
                        NotepadMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }else if(flag==2){
                    //这是弹出小窗口
                    //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到"+currentPath+"?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if(result==JOptionPane.OK_OPTION){
                        NotepadMainFrame.this.save();
                    }else if(result==JOptionPane.NO_OPTION){
                        NotepadMainFrame.this.dispose();
                        NotepadMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }else{
                    //这是弹出小窗口
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "确定关闭？", "系统提示", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if(result==JOptionPane.OK_OPTION){
                        NotepadMainFrame.this.dispose();
                        NotepadMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }
            }
        });
    }
    /*===================================================================*/
    
    
    /*==============================3=====================================*/
    /**
     * 行为动作
     */
    public void actionPerformed(ActionEvent e) {
        Source source = Source.valueOf(e.getActionCommand());
        switch (source) {
            // 文件操作
            case Source.OPEN -> openFile();
            case Source.SAVE -> save();
            case Source.SAVE_AS -> saveAs();
            case Source.NEW_FILE -> newFile();
            case Source.EXIT -> exit();

            // 打印与页面设置
            case Source.PAGE_SETUP -> {
                PageFormat pf = new PageFormat();
                PrinterJob.getPrinterJob().pageDialog(pf);
            }
            case Source.PRINT -> Print();

            // 撤销/恢复
            case Source.UNDO -> {
                if (undoMgr.canUndo()) undoMgr.undo();
            }
            case Source.REDO -> {
                if (undoMgr.canRedo()) undoMgr.redo();
            }

            // 编辑操作
            case Source.CUT -> cut();
            case Source.COPY -> copy();
            case Source.PASTE -> paste();
            case Source.DELETE -> {
                String tem = textArea.getText();
                textArea.setText(tem.substring(0, textArea.getSelectionStart()));
            }

            // 查找，转行和替换
            case Source.FIND, Source.FIND_NEXT, Source.REPLACE -> mySearch();
            case Source.TURN_TO -> turnTo();
            case Source.SELECT_ALL -> textArea.selectAll();

            // 时间与自动换行设置
            case Source.INSERT_TIME -> textArea.append(hour + ":" + min + " " + c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH));
            case Source.TOGGLE_WRAP -> textArea.setLineWrap(itemNextLine.isSelected());

            // 字体与颜色设置
            case Source.FONT -> {
                MQFontChooser fontChooser = new MQFontChooser(textArea.getFont());//默认字体
                fontChooser.showFontDialog(this);//调用对话框
                textArea.setFont(fontChooser.getSelectFont());//更新字体
            }
            case Source.BG_COLOR -> {
                jcc1 = new JColorChooser();
                JOptionPane.showMessageDialog(this, jcc1, "选择背景颜色", JOptionPane.PLAIN_MESSAGE);
                textArea.setBackground(jcc1.getColor());
            }
            case Source.FONT_COLOR -> {
                jcc1 = new JColorChooser();
                JOptionPane.showMessageDialog(this, jcc1, "选择字体颜色", JOptionPane.PLAIN_MESSAGE);
                textArea.setForeground(jcc1.getColor());
            }

            // 状态与帮助
            case Source.TOGGLE_STATUS -> toolState.setVisible(itemStatement.isSelected());//控制状态栏可见
            case Source.HELP -> JOptionPane.showMessageDialog(this, "把故事听到最后才说再见", "给我一首歌的时间", JOptionPane.INFORMATION_MESSAGE);
            case Source.ABOUT -> JOptionPane.showMessageDialog(this, "一个简单的记事本~", "软件说明", JOptionPane.INFORMATION_MESSAGE);
            default -> throw new IllegalStateException("Unexpected value: " + source);
        }
    }

    private void turnTo() {
        final JDialog gotoDialog = new JDialog(this, "转到下列行");
        JLabel gotoLabel = new JLabel("行数(L):");
        final JTextField linenum = new JTextField(5);//宽度5个字符
        linenum.setText("1");//初始值
        linenum.selectAll();

        JButton okButton = new JButton("确定");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int totalLine = textArea.getLineCount();
                int[] lineNumber = new int[totalLine + 1];
                String s = textArea.getText();
                int pos = 0, t = 0;//pos换行位置，t个数

                while (true) {//查找换行符和其位置
                    pos = s.indexOf('\n', pos);
                    if (pos == -1)
                        break;
                    lineNumber[t++] = pos++;
                }

                int gt = 1;
                try {
                    gt = Integer.parseInt(linenum.getText());
                } catch (NumberFormatException efe) {
                    JOptionPane.showMessageDialog(null, "请输入行数!", "提示", JOptionPane.WARNING_MESSAGE);
                    linenum.requestFocusInWindow();//允许光标放置
                    return;
                }

                if (gt < 2 || gt >= totalLine) {
                    if (gt < 2)
                        textArea.setCaretPosition(0);//解决小于1的情况
                    else
                        textArea.setCaretPosition(s.length());//解决大于最大行数的情况
                } else
                    textArea.setCaretPosition(lineNumber[gt - 2] + 1);//正常情况

                gotoDialog.dispose();//关闭窗体
            }

        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gotoDialog.dispose();
            }
        });

        //将组件添加到容器里
        Container con = gotoDialog.getContentPane();
        con.setLayout(new FlowLayout());
        con.add(gotoLabel);
        con.add(linenum);
        con.add(okButton);
        con.add(cancelButton);

        gotoDialog.setSize(200, 100);
        gotoDialog.setResizable(false);//不可控制框大小
        gotoDialog.setLocation(300, 280);
        gotoDialog.setVisible(true);
    }
    
    
    /*===============================8====================================*/
    /**
     * 退出按钮，和窗口的红叉实现一样的功能
     */
    private void exit() {
        if(flag==2 && currentPath==null){
            //这是弹出小窗口
            //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到无标题?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.saveAs();
            }else if(result==JOptionPane.NO_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }else if(flag==2){
            //这是弹出小窗口
            //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到"+currentPath+"?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.save();
            }else if(result==JOptionPane.NO_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }else{
            //这是弹出小窗口
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "确定关闭？", "系统提示", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }
    }
    /*===================================================================*/


    /*===============================4====================================*/
    /**
     * 新建文件，只有改过的和保存过的需要处理
     */
    private void newFile() {
        //刚启动记事本为0，刚新建文档为1
        if(flag==0 || flag==1) return;
        else if(flag==2 && this.currentPath==null){        //修改后
            //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到无标题?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.saveAs();        //另存为                
            }else if(result==JOptionPane.NO_OPTION){
                this.textArea.setText("");
                this.setTitle("无标题");
                flag=1;
            }
        }else if(flag==2){
            //2、（保存的文件为3）条件下修改后
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到"+this.currentPath+"?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.save();        //直接保存，有路径
            }else if(result==JOptionPane.NO_OPTION){
                this.textArea.setText("");
                this.setTitle("无标题");
                flag=1;
            }
        }else if(flag==3){        //保存的文件
            this.textArea.setText("");
            this.setTitle("无标题");
            flag=1;
        }
    }
    /*===================================================================*/
    
    
    /*===============================5====================================*/
    /**
     * 另存为
     */
    private void saveAs() {
        //打开保存框
        JFileChooser choose=new JFileChooser();
        //选择文件
        int result=choose.showSaveDialog(this);
        if(result==JFileChooser.APPROVE_OPTION){
            //取得选择的文件[文件名是自己输入的]
            File file=choose.getSelectedFile();
            FileWriter fw=null;
            //保存
            try {
                fw=new FileWriter(file);
                fw.write(textArea.getText());
                currentFileName=file.getName();
                currentPath=file.getAbsolutePath();
                fw.flush();//保证保存数据
                this.flag=3;
                this.setTitle(currentPath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }finally{
                try {
                    if(fw!=null) fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    /*===================================================================*/
    

    /*===============================6====================================*/
    /**
     * 保存
     */
    private void save() {
        if(this.currentPath==null){
            this.saveAs();
            if(this.currentPath==null){//取消
                return;
            }
        }
        FileWriter fw=null;
        //保存
        try {
            fw=new FileWriter(new  File(currentPath));
            fw.write(textArea.getText());
            fw.flush();
            flag=3;
            this.setTitle(this.currentPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }finally{
            try {
                if(fw!=null) fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    /*===================================================================*/
    
    
    /*================================7===================================*/
    /**
     * 打开文件
     */
    private void openFile() {
        if(flag==2 && this.currentPath==null){
            //1、（刚启动记事本为0，刚新建文档为1）条件下修改后
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到无标题?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.saveAs();
            }
        }else if(flag == 2){
            //2、（打开的文件2，保存的文件3）条件下修改
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "是否将更改保存到"+this.currentPath+"?", "记事本", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.save();
            }
        }
        //打开文件选择框
        JFileChooser choose=new JFileChooser();
        //选择文件
        int result=choose.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION){
            //取得选择的文件
            File file=choose.getSelectedFile();
            //打开已存在的文件，提前将文件名存起来
            currentFileName=file.getName();
            //存在文件全路径
            currentPath=file.getAbsolutePath();
            flag=3;
            this.setTitle(this.currentPath);//标题设置为路径
            BufferedReader br=null;
            try {
                //建立文件流[字符流]
                InputStreamReader isr=new InputStreamReader(new FileInputStream(file),"GBK");
                br=new BufferedReader(isr);//包装以使用readLine
                //读取内容
                StringBuilder sb=new StringBuilder();
                String line=null;
                while((line=br.readLine())!=null){
                    sb.append(line)
                            .append(SystemParam.LINE_SEPARATOR);//readLine()不包含换行，需要手动添加
                }
                //显示在文本框
                textArea.setText(sb.toString());
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally{
                try {
                    if(br!=null) br.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    /*================================================================*/

    
    /*=============================9===================================*/
    public void Print() {
        PrinterJob job = PrinterJob.getPrinterJob();  // 创建打印任务
        job.setJobName("打印记事本内容");
        // 设置打印内容为 textArea（实现 Printable 接口）
        job.setPrintable((graphics, pageFormat, pageIndex) -> {//目标，格式，页码
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());//绘制开始坐标

            // 设置背景色
            g2d.setColor(textArea.getBackground());
            g2d.fillRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

            // 设置字体和前景色
            g2d.setFont(textArea.getFont());
            g2d.setColor(textArea.getForeground());

            //获取行距
            FontMetrics metrics = g2d.getFontMetrics();
            int lineHeight = metrics.getHeight();

            String[] lines = textArea.getText().split("\n");
            int linesPerPage = (int)(pageFormat.getImageableHeight() / lineHeight);//一页的行数
            int totalPages = (int)Math.ceil((double) lines.length / linesPerPage);//打印所需要的页数

            if (pageIndex >= totalPages) return Printable.NO_SUCH_PAGE;

            int start = pageIndex * linesPerPage;
            int end = Math.min(start + linesPerPage, lines.length);//结束行索引，避免越界。若剩余行数不足一页（如最后一页），则取实际行数作为结束位置

            for (int i = start, y = 0; i < end; i++, y++) {
                g2d.drawString(lines[i], 0, (y + 1) * lineHeight);
            }

            return Printable.PAGE_EXISTS;
        });



        // 弹出打印对话框
        if (job.printDialog()) {
            try {
                job.print();  // 正式执行打印
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "打印失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*================================================================*/
    
    
    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {//Windows和Linux
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }
            public void mouseReleased(MouseEvent e) {//macOS
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }
            private void showMenu(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
    
    public void cut(){
        copy();
        //标记开始位置
        int start = this.textArea.getSelectionStart();
        //标记结束位置
        int end = this.textArea.getSelectionEnd();
        //删除所选段
        this.textArea.replaceRange("", start, end);
        
    }
    
    public void copy(){
        //拖动选取文本
        String temp = this.textArea.getSelectedText();
        //把获取的内容复制到连续字符器，这个类继承了剪贴板接口
        StringSelection text = new StringSelection(temp);
        //把内容放在剪贴板
        this.clipboard.setContents(text, null);//不考虑所有权
    }
    //粘贴
     public void paste(){
         //Transferable接口，把剪贴板的内容转换成数据
         Transferable contents = this.clipboard.getContents(this);
         //DataFalvor类判断是否能把剪贴板的内容转换成所需数据类型
         DataFlavor flavor = DataFlavor.stringFlavor;
         //如果可以转换
         if(contents != null && contents.isDataFlavorSupported(flavor)){
             String str;
             try {//开始转换
                str=(String)contents.getTransferData(flavor);
                //如果要粘贴时，鼠标已经选中了一些字符
                if(this.textArea.getSelectedText() != null){
                    //定位被选中字符的开始位置
                    int start = this.textArea.getSelectionStart();
                    //定位被选中字符的末尾位置
                    int end = this.textArea.getSelectionEnd();
                    //把粘贴的内容替换成被选中的内容
                    this.textArea.replaceRange(str, start, end);
                }else{
                    //获取鼠标所在TextArea的位置
                    int mouse = this.textArea.getCaretPosition();
                    //在鼠标所在的位置粘贴内容
                    this.textArea.insert(str, mouse);
                }
             } catch (UnsupportedFlavorException e) {
                 // 数据格式不匹配（非文本），图片之类的
                 System.err.println("剪贴板内容格式错误: " + e.getMessage());
             } catch (IOException e) {
                 // 数据读取失败
                 System.err.println("读取剪贴板失败: " + e.getMessage());
             } catch (IllegalArgumentException e) {
                 // 参数错误（如插入位置无效）
                 System.err.println("粘贴位置无效: " + e.getMessage());
             } catch (Exception e) {
                 // 其他未知异常
                 System.err.println("粘贴时发生未知错误: " + e.getMessage());
                 e.printStackTrace();
             }
         }
     }
     
    public void mySearch() {
        final JDialog findDialog = new JDialog(this, "查找与替换",true);
        Container con = findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchContentLabel = new JLabel("查找内容(N) :");
        JLabel replaceContentLabel = new JLabel("替换为(P)　 :");
        final JTextField findText = new JTextField(22);
        final JTextField replaceText = new JTextField(22);
        final JCheckBox matchcase = new JCheckBox("区分大小写");
        ButtonGroup bGroup = new ButtonGroup();
        final JRadioButton up = new JRadioButton("向上(U)");
        final JRadioButton down = new JRadioButton("向下(D)");
        down.setSelected(true);
        bGroup.add(up);
        bGroup.add(down);
        JButton searchNext = new JButton("查找下一个(F)");
        JButton replace = new JButton("替换(R)");
        final JButton replaceAll = new JButton("全部替换(A)");
        searchNext.setPreferredSize(new Dimension(110, 22));
        replace.setPreferredSize(new Dimension(110, 22));
        replaceAll.setPreferredSize(new Dimension(110, 22));
        // "替换"按钮的事件处理
        replace.addActionListener(e -> {
            if (textArea.getSelectedText() != null)
                textArea.replaceSelection(replaceText.getText());
        });

        // "替换全部"按钮的事件处理
        replaceAll.addActionListener(e -> {
            textArea.setCaretPosition(0); // 将光标放到编辑区开头
            int a = 0, b, replaceCount = 0;

            if (findText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(findDialog, "请填写查找内容!", "提示", JOptionPane.WARNING_MESSAGE);
                findText.requestFocus(true);
                return;
            }
            while (a > -1) {

                int FindStartPos = textArea.getCaretPosition();
                String str1, str2, str3, str4, strA, strB;
                str1 = textArea.getText();
                str2 = str1.toLowerCase();
                str3 = findText.getText();
                str4 = str3.toLowerCase();

                if (matchcase.isSelected()) {
                    strA = str1;
                    strB = str3;
                } else {
                    strA = str2;
                    strB = str4;
                }

                if (up.isSelected()) {
                    if (textArea.getSelectedText() == null) {
                        a = strA.lastIndexOf(strB, FindStartPos - 1);
                    } else {
                        a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                    }
                } else if (down.isSelected()) {
                    if (textArea.getSelectedText() == null) {
                        a = strA.indexOf(strB, FindStartPos);
                    } else {
                        a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                    }

                }

                if (a > -1) {
                    if (up.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    } else if (down.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    }
                } else {
                    if (replaceCount == 0) {
                        JOptionPane.showMessageDialog(findDialog, "找不到您查找的内容!", "记事本", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(findDialog, "成功替换" + replaceCount + "个匹配内容!", "替换成功", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                if (replaceText.getText().isEmpty() && textArea.getSelectedText() != null) {
                    textArea.replaceSelection("");
                    replaceCount++;
                }
                if (!replaceText.getText().isEmpty() && textArea.getSelectedText() != null) {
                    textArea.replaceSelection(replaceText.getText());
                    replaceCount++;
                }
            }// end while
        }); /* "替换全部"按钮的事件处理结束 */

        // "查找下一个"按钮事件处理
        searchNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String content = textArea.getText();
                String keyword = findText.getText();
                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(findDialog, "请输入查找内容!", "提示", JOptionPane.WARNING_MESSAGE);
                    findText.requestFocusInWindow();
                    return;
                }
                boolean matchCaseSelected = matchcase.isSelected();
                boolean isUp = up.isSelected();

                String source = matchCaseSelected ? content : content.toLowerCase();
                String target = matchCaseSelected ? keyword : keyword.toLowerCase();

                int caretPos = textArea.getCaretPosition();//当前光标位置
                int index = -1;

                // 计算查找起点
                int searchStart;
                if (isUp) {
                    searchStart = (textArea.getSelectedText() == null) ? caretPos - 1 : caretPos - keyword.length() - 1;//有选择从选择前查找
                    if (searchStart < 0) searchStart = source.length() - 1;//转至末尾
                    index = source.lastIndexOf(target, searchStart);//IndexOf的逆置

                    if (index == -1 && searchStart != source.length() - 1) {
                        index = source.lastIndexOf(target); // 循环查找
                    }
                } else {
                    searchStart = (textArea.getSelectedText() == null) ? caretPos : caretPos - keyword.length() + 1;
                    if (searchStart >= source.length()) searchStart = 0;
                    index = source.indexOf(target, searchStart);

                    if (index == -1 && searchStart != 0) {
                        index = source.indexOf(target); // 循环查找
                    }
                }

                // 匹配成功
                if (index != -1) {
                    textArea.setCaretPosition(index);
                    textArea.select(index, index + keyword.length());//选中
                } else {
                    JOptionPane.showMessageDialog(findDialog, "找不到您查找的内容!", "记事本", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        /* "查找下一个"按钮事件处理结束 */
        // "取消"按钮及事件处理
        JButton cancel = new JButton("取消");
        cancel.setPreferredSize(new Dimension(110, 22));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findDialog.dispose();
            }
        });

        // 创建"查找与替换"对话框的界面
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel direction = new JPanel();
        direction.setBorder(BorderFactory.createTitledBorder("方向 "));
        direction.add(up);
        direction.add(down);
        direction.setPreferredSize(new Dimension(170, 60));
        JPanel replacePanel = new JPanel();
        replacePanel.setLayout(new GridLayout(2, 1));
        replacePanel.add(replace);
        replacePanel.add(replaceAll);

        topPanel.add(searchContentLabel);
        topPanel.add(findText);
        topPanel.add(searchNext);
        centerPanel.add(replaceContentLabel);
        centerPanel.add(replaceText);
        centerPanel.add(replacePanel);
        bottomPanel.add(matchcase);
        bottomPanel.add(direction);
        bottomPanel.add(cancel);

        con.add(topPanel);
        con.add(centerPanel);
        con.add(bottomPanel);

        // 设置"查找与替换"对话框的大小、不可更改大小、位置和可见性
        findDialog.setSize(410, 210);
        findDialog.setResizable(false);
        findDialog.setLocation(230, 280);
        findDialog.setVisible(true);
    }
}