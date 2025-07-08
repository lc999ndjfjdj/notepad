package util;

import java.awt.*;


public class TestLine extends javax.swing.JComponent {

    /**
	 * 左侧显示行号
	 */
	private final  Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    public final Color DEFAULT_BACKGROUD = new Color(228, 228, 228);
    public final Color DEFAULT_FOREGROUD = Color.red;
    public final int nHEIGHT = Integer.MAX_VALUE - 1000000;
    public final int MARGIN = 5;//固定像素
    private int lineHeight;
    private int fontLineHeight;
    private int currentRowWidth;
    private FontMetrics fontMetrics;

    public TestLine() {
        setFont(DEFAULT_FONT);
        setForeground(DEFAULT_FOREGROUD);
        setBackground(DEFAULT_BACKGROUD);
        setPreferredSize(new Dimension(9999, 30)); // 设置行号
    }


    public void setPreferredSize(int row) {//动态控制行号栏的宽度
        int width = fontMetrics.stringWidth(String.valueOf(row));//最后一行，即行数最大的像素
        if (currentRowWidth < width) {
            currentRowWidth = width;
            setPreferredSize(new Dimension(2 * MARGIN + width + 1, nHEIGHT));
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());//获取字体
        fontLineHeight = fontMetrics.getHeight();//行高
    }

    public int getLineHeight() {
        if (lineHeight == 0) {
            return fontLineHeight;//默认
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
        Rectangle drawHere = g.getClipBounds();//重绘区域
        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
        g.setColor(getForeground());
        int startLineNum = (drawHere.y / nlineHeight) + 1;//第一行起点
        int endLineNum = startLineNum + (drawHere.height / nlineHeight);
        int start =startLineNum * nlineHeight - startOffset;//y为基线位置，减去四个像素
        for (int i = startLineNum; i <= endLineNum; ++i) {//循环绘制
            String lineNum = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNum);//控制右间距
            g.drawString(lineNum + " ", MARGIN + currentRowWidth - width -1, start);//总宽度-右间距=紧贴右
            start += nlineHeight;
        }
        setPreferredSize(endLineNum);
    }
}
