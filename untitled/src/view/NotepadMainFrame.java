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
    //�������
    private JPanel contentPane;
    //�༭��
    private JTextArea textArea;
    //�򿪲˵���
    private JMenuItem itemOpen;
    //����˵���
    private JMenuItem itemSave;
    
    //1���½� 
    //2���޸Ĺ�
    //3���������
    int flag=0;//��־123

    //��ǰ�ļ���
    String currentFileName=null;
    
     PrintJob  p=null;//����һ��Ҫ��ӡ�Ķ���
     Graphics  g=null;//Ҫ��ӡ�Ķ���
    
    //��ǰ�ļ�·��
    String currentPath=null;
    
    //������ɫ                             ���Կص�ɫ��
    JColorChooser jcc1=null;
    Color color=Color.BLACK;
    
    //�ı�������������
    int linenum = 1;
    int columnnum = 1;
    
    //����������
    public UndoManager undoMgr = new UndoManager(); 
    
    //ʹ��ϵͳ������
    public Clipboard clipboard =  Toolkit.getDefaultToolkit().getSystemClipboard();
    
    private JMenuItem itemSaveAs;              //���Ϊ
    private JMenuItem itemNew;				   //�½�
    private JMenuItem itemPage;				   //ҳ������
    private JSeparator separator;			   //�ָ���
    private JMenuItem itemPrint;			   //��ӡ
    private JMenuItem itemExit;				   //�˳�
    private JSeparator separator_1;			   //�ָ���
    private JMenu itemEdit;					   //�༭
    private JMenu itFormat;					   //��ʽ
    private JMenu itemCheck;				   //�鿴
    private JMenu itemHelp;					   //����
    private JMenuItem itemSearchForHelp;	   //�鿴����
    private JMenuItem itemAboutNotepad;		   //���ڼ��±�
    private JMenuItem itemUndo;				   //����
    private JMenuItem itemCut;				   //����
    private JMenuItem itemCopy;				   //����
    private JMenuItem itemPaste;			   //ճ��
    private JMenuItem itemDelete;			   //ɾ��
    private JMenuItem itemFind;				   //����
    private JMenuItem itemFindNext;			   //������һ��
    private JMenuItem itemReplace;			   //�滻
    private JMenuItem itemTurnTo;			   //ת��
    private JMenuItem itemSelectAll;		   //ȫѡ
    private JMenuItem itemTime;				   //����/ʱ��
    private JMenuItem itemFont;				   //����
    private JMenuItem itemColor;			   //������ɫ
    private JMenuItem itemFontColor;		   //������ɫ
    private JCheckBoxMenuItem itemNextLine;	   //�Զ�����
    private JScrollPane scrollPane;			   //������
    private JCheckBoxMenuItem itemStatement;   //״̬��
    private JToolBar toolState;
    //״̬����ʱ�䣬���λ�ã�������
    public static JLabel label1;
    private JLabel label2;
    private JLabel label3;
    int length=0;
    int sum=0;
    
    /**
     *  	������
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {//�첽UI������������̳߳�ͻ
            try {
                NotepadMainFrame frame = new NotepadMainFrame();
                frame.setVisible(true);//��ʾ����UI����
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    GregorianCalendar c=new GregorianCalendar();//״̬����ʱ���ȡ
    int hour=c.get(Calendar.HOUR_OF_DAY);
    int min=c.get(Calendar.MINUTE);
    int second=c.get(Calendar.SECOND);    
    private JPopupMenu popupMenu;         //�Ҽ������˵�
    private JMenuItem popM_Undo;		  //����
    private JMenuItem popM_Cut;			  //����
    private JMenuItem popM_Copy;		  //����
    private JMenuItem popM_Paste;		  //ճ��
    private JMenuItem popM_Delete;		  //ɾ��
    private JMenuItem popM_SelectAll;	  //ȫѡ
    private JMenuItem popM_toLeft;		  //���ҵ�����Ķ�˳��
    private JMenuItem popM_showUnicode;   //��ʾUnicode�����ַ�
    private JMenuItem popM_closeIMe;      //�ر�IME
    private JMenuItem popM_InsertUnicode; //����Unicode�����ַ�
    private JMenuItem popM_RestartSelect; //������ѡ
    private JSeparator separator_2;       //�ָ���
    private JSeparator separator_3;		  //�ָ���
    private JSeparator separator_4;       //�ָ���
    private JSeparator separator_5;       //�ָ���
    private JMenuItem itemRedo;			  //�ָ�
    private JSeparator separator_6;		  //�ָ���
    private JSeparator separator_7;		  //�ָ���
    private JSeparator separator_8;		  //�ָ���
    private JMenuItem popM_Redo;		  //�ָ�

    /**
     * Create the frame.
     * ���캯��
     */
    public NotepadMainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//�޸ĳɵ�ǰϵͳ���Windows��
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�رպ�java�����Զ��˳�
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        setTitle("���±�");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//Ϊ�������Ĺر���ʾ�Ƿ񱣴���׼��
        setBounds(100, 100, 721, 772);//��ʼλ�úʹ�С
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);//�󶨲˵���
        
        JMenu itemFile = new JMenu("�ļ�(F)");
        itemFile.setMnemonic('F');	//���ÿ�ݼ�Alt+"F"
        menuBar.add(itemFile);
        
        itemNew = new JMenuItem("�½�(N)",'N');
        itemNew.setActionCommand(Source.NEW_FILE.name());//��ö�ٱ���
        itemNew.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"N"
        itemNew.addActionListener(this);
        itemFile.add(itemNew);
        
        itemOpen = new JMenuItem("��(O)",'O');
        itemOpen.setActionCommand(Source.OPEN.name());
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"O"
        itemOpen.addActionListener(this);
        itemFile.add(itemOpen);
        
        itemSave = new JMenuItem("����(S)");
        itemSave.setActionCommand(Source.SAVE.name());
        itemSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                CTRL_DOWN_MASK));   //���ÿ�ݼ�Ctrl+"S"
        itemSave.addActionListener(this);
        itemFile.add(itemSave);
        
        itemSaveAs = new JMenuItem("���Ϊ");
        itemSaveAs.setActionCommand(Source.SAVE_AS.name());
        itemSaveAs.addActionListener(this);
        itemFile.add(itemSaveAs);
        
        separator = new JSeparator();  //��ӷָ���
        itemFile.add(separator);
        
        itemPage = new JMenuItem("ҳ������(U)",'U');
        itemPage.setActionCommand(Source.PAGE_SETUP.name());
        itemPage.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U,
                CTRL_DOWN_MASK));   //���ÿ�ݼ�Ctrl+"U"
        itemPage.addActionListener(this);
        itemFile.add(itemPage);
        
        itemPrint = new JMenuItem("��ӡ(P)...",'P');
        itemPrint.setActionCommand(Source.PRINT.name());
        itemPrint.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,
                CTRL_DOWN_MASK));   //�ÿ�ݼ�Ctrl+"P"
        itemPrint.addActionListener(this);
        itemFile.add(itemPrint);
        
        separator_1 = new JSeparator();  //��ӷָ���
        itemFile.add(separator_1);
        
        itemExit = new JMenuItem("�˳�");
        itemExit.setActionCommand(Source.EXIT.name());
        itemExit.addActionListener(this);
        itemFile.add(itemExit);
        
        itemEdit = new JMenu("�༭(E)");
        itemEdit.setMnemonic('E');//���ÿ�ݼ�Alt+"E"
        menuBar.add(itemEdit);
        
        itemUndo = new JMenuItem("����(Z)",'Z');
        itemUndo.setActionCommand(Source.UNDO.name());
        itemUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"Z"
        itemUndo.addActionListener(this);
        itemEdit.add(itemUndo);
        
        itemRedo = new JMenuItem("�ָ�(R)",'R');
        itemRedo.setActionCommand(Source.REDO.name());
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z,
                CTRL_DOWN_MASK | SHIFT_DOWN_MASK
        ));  // ���ÿ�ݼ�Ϊ Ctrl+Shift+Z
        itemRedo.addActionListener(this);
        itemEdit.add(itemRedo);
        
        itemCut = new JMenuItem("����(T)",'T');
        itemCut.setActionCommand(Source.CUT.name());
        itemCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"X"
        itemCut.addActionListener(this);
        
        separator_6 = new JSeparator();
        itemEdit.add(separator_6);
        itemEdit.add(itemCut);
        
        itemCopy = new JMenuItem("����(C)",'C');
        itemCopy.setActionCommand(Source.COPY.name());
        itemCopy.addActionListener(this);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"C"
        itemEdit.add(itemCopy);
        
        itemPaste = new JMenuItem("ճ��(P)",'P');
        itemPaste.setActionCommand(Source.PASTE.name());
        itemPaste.addActionListener(this);
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"V"
        itemEdit.add(itemPaste);
        
        itemDelete = new JMenuItem("ɾ��(L)",'L');
        itemDelete.setActionCommand(Source.DELETE.name());
        itemDelete.addActionListener(this);
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                CTRL_DOWN_MASK));    //���ÿ�ݼ�Ctrl+"D"
        itemEdit.add(itemDelete);
        
        separator_7 = new JSeparator();
        itemEdit.add(separator_7);

        itemFind = new JMenuItem("����(F)",'F');
        itemFind.setActionCommand(Source.FIND.name());
        itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                CTRL_DOWN_MASK));   //���ÿ�ݼ�Ctrl+"F"
        itemFind.addActionListener(this);
        itemEdit.add(itemFind);
        
        itemFindNext = new JMenuItem("������һ��(N)",'N');
        itemFindNext.setActionCommand(Source.FIND_NEXT.name());
        itemFindNext.setAccelerator(KeyStroke.getKeyStroke("F3"));
        itemFindNext.addActionListener(this);
        itemEdit.add(itemFindNext);
        
        itemReplace = new JMenuItem("�滻(R)",'R');
        itemReplace.setActionCommand(Source.REPLACE.name());
        itemReplace.addActionListener(this);
        itemReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"H"
        itemEdit.add(itemReplace);
        
        itemTurnTo = new JMenuItem("ת��(G)",'G');
        itemTurnTo.setActionCommand(Source.TURN_TO.name());
        itemTurnTo.addActionListener(this);
        itemTurnTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"G"
        itemEdit.add(itemTurnTo);
        
        itemSelectAll = new JMenuItem("ȫѡ(A)",'A');
        itemSelectAll.setActionCommand(Source.SELECT_ALL.name());
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                CTRL_DOWN_MASK));  //���ÿ�ݼ�Ctrl+"A"
        itemSelectAll.addActionListener(this);
        
        separator_8 = new JSeparator();
        itemEdit.add(separator_8);
        itemEdit.add(itemSelectAll);
        
        itemTime = new JMenuItem("ʱ��/����(D)",'D');
        itemTime.setActionCommand(Source.INSERT_TIME.name());
        itemTime.addActionListener(this);
        itemTime.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemEdit.add(itemTime);
        
        itFormat = new JMenu("��ʽ(O)");
        itFormat.setMnemonic('O');//���ÿ�ݼ�Alt+"O"
        menuBar.add(itFormat);
        
        itemNextLine = new JCheckBoxMenuItem("�Զ�����(W)");
        itemNextLine.setActionCommand(Source.TOGGLE_WRAP.name());
        itemNextLine.addActionListener(this);
        itFormat.add(itemNextLine);
        
        itemFont = new JMenuItem("�����С...");
        itemFont.setActionCommand(Source.FONT.name());
        itemFont.addActionListener(this);
        itFormat.add(itemFont);
        
        itemColor = new JMenuItem("������ɫ...");
        itemColor.setActionCommand(Source.BG_COLOR.name());
        itemColor.addActionListener(this);
        itFormat.add(itemColor);
        
        itemFontColor = new JMenuItem("������ɫ...");
        itemFontColor.setActionCommand(Source.FONT_COLOR.name());
        itemFontColor.addActionListener(this);
        itFormat.add(itemFontColor);
        
        itemCheck = new JMenu("�鿴(V)");
        itemCheck.setMnemonic('V');//���ÿ�ݼ�Alt+"V"
        menuBar.add(itemCheck);
        
        itemStatement = new JCheckBoxMenuItem("״̬��(S)");
        itemStatement.setActionCommand(Source.TOGGLE_STATUS.name());
        itemStatement.addActionListener(this);
        itemCheck.add(itemStatement);
        
        itemHelp = new JMenu("����(H)");
        itemHelp.setMnemonic('H');//���ÿ�ݼ�Alt+"H"
        menuBar.add(itemHelp);
        
        itemSearchForHelp = new JMenuItem("�鿴����(H)",'H');
        itemSearchForHelp.setActionCommand(Source.HELP.name());
        itemSearchForHelp.addActionListener(this);
        itemHelp.add(itemSearchForHelp);
        
        itemAboutNotepad = new JMenuItem("���ڼ��±�(A)",'A');
        itemAboutNotepad.setActionCommand(Source.ABOUT.name());
        itemAboutNotepad.addActionListener(this);
        itemHelp.add(itemAboutNotepad);

        //�Զ���߿�
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));//͸����
        contentPane.setLayout(new BorderLayout(0, 0));//���������������
        setContentPane(contentPane);

        textArea = new JTextArea();
        
        //VERTICAL��ֱalways    HORIZONTALˮƽas_need
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TestLine view = new TestLine();
        //����к�
        scrollPane.setRowHeaderView(view);
        //��ӵ����
        contentPane.add(scrollPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        addPopup(textArea, popupMenu);
        
        popM_Undo = new JMenuItem("����(Z)");
        popM_Undo.setActionCommand(Source.UNDO.name());
        popM_Undo.addActionListener(this);
        popupMenu.add(popM_Undo);
        
        popM_Redo = new JMenuItem("�ָ�(R)");
        popM_Redo.setActionCommand(Source.REDO.name());
        popM_Redo.addActionListener(this);
        popupMenu.add(popM_Redo);
        
        separator_2 = new JSeparator();
        popupMenu.add(separator_2);
        
        popM_Cut = new JMenuItem("����(T)");
        popM_Cut.setActionCommand(Source.CUT.name());
        popM_Cut.addActionListener(this);
        popupMenu.add(popM_Cut);
        
        popM_Copy = new JMenuItem("����(C)");
        popM_Copy.setActionCommand(Source.COPY.name());
        popM_Copy.addActionListener(this);
        popupMenu.add(popM_Copy);
        
        popM_Paste = new JMenuItem("ճ��(P)");
        popM_Paste.setActionCommand(Source.PASTE.name());
        popM_Paste.addActionListener(this);
        popupMenu.add(popM_Paste);
        
        popM_Delete = new JMenuItem("ɾ��(D)");
        popM_Delete.setActionCommand(Source.DELETE.name());
        popM_Delete.addActionListener(this);
        popupMenu.add(popM_Delete);
        
        separator_3 = new JSeparator();
        popupMenu.add(separator_3);
        
        popM_SelectAll = new JMenuItem("ȫѡ(A)");
        popM_SelectAll.setActionCommand(Source.SELECT_ALL.name());
        popM_SelectAll.addActionListener(this);
        popupMenu.add(popM_SelectAll);

        separator_4 = new JSeparator();
        popupMenu.add(separator_4);

        //��ӳ���������
        textArea.getDocument().addUndoableEditListener(undoMgr);
                
        //����״̬��
        toolState = new JToolBar();
        toolState.setSize(textArea.getSize().width, 10);
        label1 = new JLabel("    ��ǰϵͳʱ�䣺" + hour + ":" + min + ":" + second+" ");
        toolState.add(label1);  //���ϵͳʱ��
        toolState.addSeparator();//�ָ���
        label2 = new JLabel("    �� " + linenum + " ��, �� " + columnnum+" ��  ");
        toolState.add(label2);  //�����������
        toolState.addSeparator();
        
        label3 = new JLabel("    һ�� " +length+" ��  ");
        toolState.add(label3);  //�������ͳ��
        //��¼������������ʵʱˢ��
        textArea.addCaretListener(e -> {
            JTextArea editArea = (JTextArea)e.getSource();
            try {
                int caretpos = editArea.getCaretPosition();//��ȡ���λ��
                linenum = editArea.getLineOfOffset(caretpos);//��ȡ�������
                columnnum = caretpos - textArea.getLineStartOffset(linenum);//��ȡ�������
                label2.setText("    �� " + (linenum+1) + " ��, �� " + (columnnum+1)+" ��  ");
                length=NotepadMainFrame.this.textArea.getText().length();
                label3.setText("    һ�� " +length+" ��  ");
            }
            catch(Exception ex) { }
        });
        
        contentPane.add(toolState, BorderLayout.SOUTH);  //��״̬����ӵ����ײ�
        toolState.setVisible(true);//�ɼ�
        toolState.setFloatable(true);//�����϶�
        Clock clock=new Clock();
        clock.start();//����ʱ���߳�
        
        
        
        // ���������˵�
        final JPopupMenu jp=new JPopupMenu();    //��������ʽ�˵������������ǲ˵���
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3)//ֻ��Ӧ����Ҽ������¼�
                {
                    jp.show(e.getComponent(),e.getX(),e.getY());//�����λ����ʾ����ʽ�˵�
                }
            }
        });
        isChanged();//����flag��־

        this.MainFrameWidowListener();
    }

    
    /*===============================1====================================*/
    /**
     * �Ƿ��б仯
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
     * �½��Ļ򱣴�����˳�ֻ������ѡ��
     */
    private void MainFrameWidowListener() {
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                if(flag==2 && currentPath==null){
                    //���ǵ���С����
                    //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽�ޱ���?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if(result==JOptionPane.OK_OPTION){
                        NotepadMainFrame.this.saveAs();
                    }else if(result==JOptionPane.NO_OPTION){
                        NotepadMainFrame.this.dispose();
                        NotepadMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }else if(flag==2){
                    //���ǵ���С����
                    //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽"+currentPath+"?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if(result==JOptionPane.OK_OPTION){
                        NotepadMainFrame.this.save();
                    }else if(result==JOptionPane.NO_OPTION){
                        NotepadMainFrame.this.dispose();
                        NotepadMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }else{
                    //���ǵ���С����
                    int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "ȷ���رգ�", "ϵͳ��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
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
     * ��Ϊ����
     */
    public void actionPerformed(ActionEvent e) {
        Source source = Source.valueOf(e.getActionCommand());
        switch (source) {
            // �ļ�����
            case Source.OPEN -> openFile();
            case Source.SAVE -> save();
            case Source.SAVE_AS -> saveAs();
            case Source.NEW_FILE -> newFile();
            case Source.EXIT -> exit();

            // ��ӡ��ҳ������
            case Source.PAGE_SETUP -> {
                PageFormat pf = new PageFormat();
                PrinterJob.getPrinterJob().pageDialog(pf);
            }
            case Source.PRINT -> Print();

            // ����/�ָ�
            case Source.UNDO -> {
                if (undoMgr.canUndo()) undoMgr.undo();
            }
            case Source.REDO -> {
                if (undoMgr.canRedo()) undoMgr.redo();
            }

            // �༭����
            case Source.CUT -> cut();
            case Source.COPY -> copy();
            case Source.PASTE -> paste();
            case Source.DELETE -> {
                String tem = textArea.getText();
                textArea.setText(tem.substring(0, textArea.getSelectionStart()));
            }

            // ���ң�ת�к��滻
            case Source.FIND, Source.FIND_NEXT, Source.REPLACE -> mySearch();
            case Source.TURN_TO -> turnTo();
            case Source.SELECT_ALL -> textArea.selectAll();

            // ʱ�����Զ���������
            case Source.INSERT_TIME -> textArea.append(hour + ":" + min + " " + c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH));
            case Source.TOGGLE_WRAP -> textArea.setLineWrap(itemNextLine.isSelected());

            // ��������ɫ����
            case Source.FONT -> {
                MQFontChooser fontChooser = new MQFontChooser(textArea.getFont());//Ĭ������
                fontChooser.showFontDialog(this);//���öԻ���
                textArea.setFont(fontChooser.getSelectFont());//��������
            }
            case Source.BG_COLOR -> {
                jcc1 = new JColorChooser();
                JOptionPane.showMessageDialog(this, jcc1, "ѡ�񱳾���ɫ", JOptionPane.PLAIN_MESSAGE);
                textArea.setBackground(jcc1.getColor());
            }
            case Source.FONT_COLOR -> {
                jcc1 = new JColorChooser();
                JOptionPane.showMessageDialog(this, jcc1, "ѡ��������ɫ", JOptionPane.PLAIN_MESSAGE);
                textArea.setForeground(jcc1.getColor());
            }

            // ״̬�����
            case Source.TOGGLE_STATUS -> toolState.setVisible(itemStatement.isSelected());//����״̬���ɼ�
            case Source.HELP -> JOptionPane.showMessageDialog(this, "�ѹ�����������˵�ټ�", "����һ�׸��ʱ��", JOptionPane.INFORMATION_MESSAGE);
            case Source.ABOUT -> JOptionPane.showMessageDialog(this, "һ���򵥵ļ��±�~", "���˵��", JOptionPane.INFORMATION_MESSAGE);
            default -> throw new IllegalStateException("Unexpected value: " + source);
        }
    }

    private void turnTo() {
        final JDialog gotoDialog = new JDialog(this, "ת��������");
        JLabel gotoLabel = new JLabel("����(L):");
        final JTextField linenum = new JTextField(5);//���5���ַ�
        linenum.setText("1");//��ʼֵ
        linenum.selectAll();

        JButton okButton = new JButton("ȷ��");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int totalLine = textArea.getLineCount();
                int[] lineNumber = new int[totalLine + 1];
                String s = textArea.getText();
                int pos = 0, t = 0;//pos����λ�ã�t����

                while (true) {//���һ��з�����λ��
                    pos = s.indexOf('\n', pos);
                    if (pos == -1)
                        break;
                    lineNumber[t++] = pos++;
                }

                int gt = 1;
                try {
                    gt = Integer.parseInt(linenum.getText());
                } catch (NumberFormatException efe) {
                    JOptionPane.showMessageDialog(null, "����������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
                    linenum.requestFocusInWindow();//���������
                    return;
                }

                if (gt < 2 || gt >= totalLine) {
                    if (gt < 2)
                        textArea.setCaretPosition(0);//���С��1�����
                    else
                        textArea.setCaretPosition(s.length());//�������������������
                } else
                    textArea.setCaretPosition(lineNumber[gt - 2] + 1);//�������

                gotoDialog.dispose();//�رմ���
            }

        });

        JButton cancelButton = new JButton("ȡ��");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gotoDialog.dispose();
            }
        });

        //�������ӵ�������
        Container con = gotoDialog.getContentPane();
        con.setLayout(new FlowLayout());
        con.add(gotoLabel);
        con.add(linenum);
        con.add(okButton);
        con.add(cancelButton);

        gotoDialog.setSize(200, 100);
        gotoDialog.setResizable(false);//���ɿ��ƿ��С
        gotoDialog.setLocation(300, 280);
        gotoDialog.setVisible(true);
    }
    
    
    /*===============================8====================================*/
    /**
     * �˳���ť���ʹ��ڵĺ��ʵ��һ���Ĺ���
     */
    private void exit() {
        if(flag==2 && currentPath==null){
            //���ǵ���С����
            //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽�ޱ���?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.saveAs();
            }else if(result==JOptionPane.NO_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }else if(flag==2){
            //���ǵ���С����
            //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽"+currentPath+"?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.save();
            }else if(result==JOptionPane.NO_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }else{
            //���ǵ���С����
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "ȷ���رգ�", "ϵͳ��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                NotepadMainFrame.this.dispose();
            }
        }
    }
    /*===================================================================*/


    /*===============================4====================================*/
    /**
     * �½��ļ���ֻ�иĹ��ĺͱ��������Ҫ����
     */
    private void newFile() {
        //���������±�Ϊ0�����½��ĵ�Ϊ1
        if(flag==0 || flag==1) return;
        else if(flag==2 && this.currentPath==null){        //�޸ĺ�
            //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽�ޱ���?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.saveAs();        //���Ϊ                
            }else if(result==JOptionPane.NO_OPTION){
                this.textArea.setText("");
                this.setTitle("�ޱ���");
                flag=1;
            }
        }else if(flag==2){
            //2����������ļ�Ϊ3���������޸ĺ�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽"+this.currentPath+"?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.save();        //ֱ�ӱ��棬��·��
            }else if(result==JOptionPane.NO_OPTION){
                this.textArea.setText("");
                this.setTitle("�ޱ���");
                flag=1;
            }
        }else if(flag==3){        //������ļ�
            this.textArea.setText("");
            this.setTitle("�ޱ���");
            flag=1;
        }
    }
    /*===================================================================*/
    
    
    /*===============================5====================================*/
    /**
     * ���Ϊ
     */
    private void saveAs() {
        //�򿪱����
        JFileChooser choose=new JFileChooser();
        //ѡ���ļ�
        int result=choose.showSaveDialog(this);
        if(result==JFileChooser.APPROVE_OPTION){
            //ȡ��ѡ����ļ�[�ļ������Լ������]
            File file=choose.getSelectedFile();
            FileWriter fw=null;
            //����
            try {
                fw=new FileWriter(file);
                fw.write(textArea.getText());
                currentFileName=file.getName();
                currentPath=file.getAbsolutePath();
                fw.flush();//��֤��������
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
     * ����
     */
    private void save() {
        if(this.currentPath==null){
            this.saveAs();
            if(this.currentPath==null){//ȡ��
                return;
            }
        }
        FileWriter fw=null;
        //����
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
     * ���ļ�
     */
    private void openFile() {
        if(flag==2 && this.currentPath==null){
            //1�������������±�Ϊ0�����½��ĵ�Ϊ1���������޸ĺ�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽�ޱ���?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.saveAs();
            }
        }else if(flag == 2){
            //2�����򿪵��ļ�2��������ļ�3���������޸�
            int result=JOptionPane.showConfirmDialog(NotepadMainFrame.this, "�Ƿ񽫸��ı��浽"+this.currentPath+"?", "���±�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result==JOptionPane.OK_OPTION){
                this.save();
            }
        }
        //���ļ�ѡ���
        JFileChooser choose=new JFileChooser();
        //ѡ���ļ�
        int result=choose.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION){
            //ȡ��ѡ����ļ�
            File file=choose.getSelectedFile();
            //���Ѵ��ڵ��ļ�����ǰ���ļ���������
            currentFileName=file.getName();
            //�����ļ�ȫ·��
            currentPath=file.getAbsolutePath();
            flag=3;
            this.setTitle(this.currentPath);//��������Ϊ·��
            BufferedReader br=null;
            try {
                //�����ļ���[�ַ���]
                InputStreamReader isr=new InputStreamReader(new FileInputStream(file),"GBK");
                br=new BufferedReader(isr);//��װ��ʹ��readLine
                //��ȡ����
                StringBuilder sb=new StringBuilder();
                String line=null;
                while((line=br.readLine())!=null){
                    sb.append(line)
                            .append(SystemParam.LINE_SEPARATOR);//readLine()���������У���Ҫ�ֶ����
                }
                //��ʾ���ı���
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
        PrinterJob job = PrinterJob.getPrinterJob();  // ������ӡ����
        job.setJobName("��ӡ���±�����");
        // ���ô�ӡ����Ϊ textArea��ʵ�� Printable �ӿڣ�
        job.setPrintable((graphics, pageFormat, pageIndex) -> {//Ŀ�꣬��ʽ��ҳ��
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());//���ƿ�ʼ����

            // ���ñ���ɫ
            g2d.setColor(textArea.getBackground());
            g2d.fillRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

            // ���������ǰ��ɫ
            g2d.setFont(textArea.getFont());
            g2d.setColor(textArea.getForeground());

            //��ȡ�о�
            FontMetrics metrics = g2d.getFontMetrics();
            int lineHeight = metrics.getHeight();

            String[] lines = textArea.getText().split("\n");
            int linesPerPage = (int)(pageFormat.getImageableHeight() / lineHeight);//һҳ������
            int totalPages = (int)Math.ceil((double) lines.length / linesPerPage);//��ӡ����Ҫ��ҳ��

            if (pageIndex >= totalPages) return Printable.NO_SUCH_PAGE;

            int start = pageIndex * linesPerPage;
            int end = Math.min(start + linesPerPage, lines.length);//����������������Խ�硣��ʣ����������һҳ�������һҳ������ȡʵ��������Ϊ����λ��

            for (int i = start, y = 0; i < end; i++, y++) {
                g2d.drawString(lines[i], 0, (y + 1) * lineHeight);
            }

            return Printable.PAGE_EXISTS;
        });



        // ������ӡ�Ի���
        if (job.printDialog()) {
            try {
                job.print();  // ��ʽִ�д�ӡ
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "��ӡʧ�ܣ�" + e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*================================================================*/
    
    
    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {//Windows��Linux
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
        //��ǿ�ʼλ��
        int start = this.textArea.getSelectionStart();
        //��ǽ���λ��
        int end = this.textArea.getSelectionEnd();
        //ɾ����ѡ��
        this.textArea.replaceRange("", start, end);
        
    }
    
    public void copy(){
        //�϶�ѡȡ�ı�
        String temp = this.textArea.getSelectedText();
        //�ѻ�ȡ�����ݸ��Ƶ������ַ����������̳��˼�����ӿ�
        StringSelection text = new StringSelection(temp);
        //�����ݷ��ڼ�����
        this.clipboard.setContents(text, null);//����������Ȩ
    }
    //ճ��
     public void paste(){
         //Transferable�ӿڣ��Ѽ����������ת��������
         Transferable contents = this.clipboard.getContents(this);
         //DataFalvor���ж��Ƿ��ܰѼ����������ת����������������
         DataFlavor flavor = DataFlavor.stringFlavor;
         //�������ת��
         if(contents != null && contents.isDataFlavorSupported(flavor)){
             String str;
             try {//��ʼת��
                str=(String)contents.getTransferData(flavor);
                //���Ҫճ��ʱ������Ѿ�ѡ����һЩ�ַ�
                if(this.textArea.getSelectedText() != null){
                    //��λ��ѡ���ַ��Ŀ�ʼλ��
                    int start = this.textArea.getSelectionStart();
                    //��λ��ѡ���ַ���ĩβλ��
                    int end = this.textArea.getSelectionEnd();
                    //��ճ���������滻�ɱ�ѡ�е�����
                    this.textArea.replaceRange(str, start, end);
                }else{
                    //��ȡ�������TextArea��λ��
                    int mouse = this.textArea.getCaretPosition();
                    //��������ڵ�λ��ճ������
                    this.textArea.insert(str, mouse);
                }
             } catch (UnsupportedFlavorException e) {
                 // ���ݸ�ʽ��ƥ�䣨���ı�����ͼƬ֮���
                 System.err.println("���������ݸ�ʽ����: " + e.getMessage());
             } catch (IOException e) {
                 // ���ݶ�ȡʧ��
                 System.err.println("��ȡ������ʧ��: " + e.getMessage());
             } catch (IllegalArgumentException e) {
                 // �������������λ����Ч��
                 System.err.println("ճ��λ����Ч: " + e.getMessage());
             } catch (Exception e) {
                 // ����δ֪�쳣
                 System.err.println("ճ��ʱ����δ֪����: " + e.getMessage());
                 e.printStackTrace();
             }
         }
     }
     
    public void mySearch() {
        final JDialog findDialog = new JDialog(this, "�������滻",true);
        Container con = findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchContentLabel = new JLabel("��������(N) :");
        JLabel replaceContentLabel = new JLabel("�滻Ϊ(P)�� :");
        final JTextField findText = new JTextField(22);
        final JTextField replaceText = new JTextField(22);
        final JCheckBox matchcase = new JCheckBox("���ִ�Сд");
        ButtonGroup bGroup = new ButtonGroup();
        final JRadioButton up = new JRadioButton("����(U)");
        final JRadioButton down = new JRadioButton("����(D)");
        down.setSelected(true);
        bGroup.add(up);
        bGroup.add(down);
        JButton searchNext = new JButton("������һ��(F)");
        JButton replace = new JButton("�滻(R)");
        final JButton replaceAll = new JButton("ȫ���滻(A)");
        searchNext.setPreferredSize(new Dimension(110, 22));
        replace.setPreferredSize(new Dimension(110, 22));
        replaceAll.setPreferredSize(new Dimension(110, 22));
        // "�滻"��ť���¼�����
        replace.addActionListener(e -> {
            if (textArea.getSelectedText() != null)
                textArea.replaceSelection(replaceText.getText());
        });

        // "�滻ȫ��"��ť���¼�����
        replaceAll.addActionListener(e -> {
            textArea.setCaretPosition(0); // �����ŵ��༭����ͷ
            int a = 0, b, replaceCount = 0;

            if (findText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(findDialog, "����д��������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(findDialog, "�Ҳ��������ҵ�����!", "���±�", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(findDialog, "�ɹ��滻" + replaceCount + "��ƥ������!", "�滻�ɹ�", JOptionPane.INFORMATION_MESSAGE);
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
        }); /* "�滻ȫ��"��ť���¼�������� */

        // "������һ��"��ť�¼�����
        searchNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String content = textArea.getText();
                String keyword = findText.getText();
                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(findDialog, "�������������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
                    findText.requestFocusInWindow();
                    return;
                }
                boolean matchCaseSelected = matchcase.isSelected();
                boolean isUp = up.isSelected();

                String source = matchCaseSelected ? content : content.toLowerCase();
                String target = matchCaseSelected ? keyword : keyword.toLowerCase();

                int caretPos = textArea.getCaretPosition();//��ǰ���λ��
                int index = -1;

                // ����������
                int searchStart;
                if (isUp) {
                    searchStart = (textArea.getSelectedText() == null) ? caretPos - 1 : caretPos - keyword.length() - 1;//��ѡ���ѡ��ǰ����
                    if (searchStart < 0) searchStart = source.length() - 1;//ת��ĩβ
                    index = source.lastIndexOf(target, searchStart);//IndexOf������

                    if (index == -1 && searchStart != source.length() - 1) {
                        index = source.lastIndexOf(target); // ѭ������
                    }
                } else {
                    searchStart = (textArea.getSelectedText() == null) ? caretPos : caretPos - keyword.length() + 1;
                    if (searchStart >= source.length()) searchStart = 0;
                    index = source.indexOf(target, searchStart);

                    if (index == -1 && searchStart != 0) {
                        index = source.indexOf(target); // ѭ������
                    }
                }

                // ƥ��ɹ�
                if (index != -1) {
                    textArea.setCaretPosition(index);
                    textArea.select(index, index + keyword.length());//ѡ��
                } else {
                    JOptionPane.showMessageDialog(findDialog, "�Ҳ��������ҵ�����!", "���±�", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        /* "������һ��"��ť�¼�������� */
        // "ȡ��"��ť���¼�����
        JButton cancel = new JButton("ȡ��");
        cancel.setPreferredSize(new Dimension(110, 22));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findDialog.dispose();
            }
        });

        // ����"�������滻"�Ի���Ľ���
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel direction = new JPanel();
        direction.setBorder(BorderFactory.createTitledBorder("���� "));
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

        // ����"�������滻"�Ի���Ĵ�С�����ɸ��Ĵ�С��λ�úͿɼ���
        findDialog.setSize(410, 210);
        findDialog.setResizable(false);
        findDialog.setLocation(230, 280);
        findDialog.setVisible(true);
    }
}