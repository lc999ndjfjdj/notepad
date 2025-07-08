package util;

import java.awt.*;


public class TestLine extends javax.swing.JComponent {

    /**
	 * �����ʾ�к�
	 */
	private final  Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    public final Color DEFAULT_BACKGROUD = new Color(228, 228, 228);
    public final Color DEFAULT_FOREGROUD = Color.red;
    public final int nHEIGHT = Integer.MAX_VALUE - 1000000;
    public final int MARGIN = 5;//�̶�����
    private int lineHeight;
    private int fontLineHeight;
    private int currentRowWidth;
    private FontMetrics fontMetrics;

    public TestLine() {
        setFont(DEFAULT_FONT);
        setForeground(DEFAULT_FOREGROUD);
        setBackground(DEFAULT_BACKGROUD);
        setPreferredSize(new Dimension(9999, 30)); // �����к�
    }


    public void setPreferredSize(int row) {//��̬�����к����Ŀ��
        int width = fontMetrics.stringWidth(String.valueOf(row));//���һ�У���������������
        if (currentRowWidth < width) {
            currentRowWidth = width;
            setPreferredSize(new Dimension(2 * MARGIN + width + 1, nHEIGHT));
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());//��ȡ����
        fontLineHeight = fontMetrics.getHeight();//�и�
    }

    public int getLineHeight() {
        if (lineHeight == 0) {
            return fontLineHeight;//Ĭ��
        }
        return lineHeight;
    }

    public int getStartOffset() {
        return 4;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int nlineHeight = getLineHeight();
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();//�ػ�����
        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
        g.setColor(getForeground());
        int startLineNum = (drawHere.y / nlineHeight) + 1;//��һ�����
        int endLineNum = startLineNum + (drawHere.height / nlineHeight);
        int start =startLineNum * nlineHeight - startOffset;//yΪ����λ�ã���ȥ�ĸ�����
        for (int i = startLineNum; i <= endLineNum; ++i) {//ѭ������
            String lineNum = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNum);//�����Ҽ��
            g.drawString(lineNum + " ", MARGIN + currentRowWidth - width -1, start);//�ܿ��-�Ҽ��=������
            start += nlineHeight;
        }
        setPreferredSize(endLineNum);
    }
}
