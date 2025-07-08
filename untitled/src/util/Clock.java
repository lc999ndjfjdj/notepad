package util;

import view.NotepadMainFrame;

import java.util.Calendar;
import java.util.GregorianCalendar;
  
public class Clock extends Thread{//新线程进行状态栏显示
    public void run() {
        do {
            GregorianCalendar time = new GregorianCalendar();
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int min = time.get(Calendar.MINUTE);
            int second = time.get(Calendar.SECOND);
            NotepadMainFrame.label1.setText("    当前时间：" + hour + ":" + min + ":" + second);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        } while (true);
    }  
}  
