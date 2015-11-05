package com.mycompany.calendar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.time.DateUtils;

/**
 * Hello world!
 *
 */
public class App {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat monthFormat = new SimpleDateFormat("MMMMM", Locale.US);
    private static final DateFormat dayFormat = new SimpleDateFormat("d");
    private static final String[] IDX = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H"};
    
    /**
     * 休日 (内閣府発表 http://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html)
     * 平成28年（2016）の国民の祝日
     * 3月21は休日となります。
     * 名称        月日
     * 元日	  1月1日
     * 成人の日	  1月11日
     * 建国記念の日 2月11日
     * 春分の日	  3月20日
     * 昭和の日	  4月29日
     * 憲法記念日	  5月3日
     * みどりの日	  5月4日
     * こどもの日	  5月5日
     * 海の日	  7月18日
     * 山の日	  8月11日
     * 敬老の日	  9月19日
     * 秋分の日	  9月22日
     * 体育の日	  10月10日
     * 文化の日	  11月3日
     * 勤労感謝の日 11月23日
     * 天皇誕生日	  12月23日
     */
    private static final String[] holidays = {
        "2016-01-01", "2016-01-02", "2016-01-03", "2016-01-11", "2016-02-11", "2016-03-20", "2016-04-29",
        "2016-05-03", "2016-05-04", "2016-05-05", "2016-07-18", "2016-08-11", "2016-09-19", "2016-09-22",
        "2016-10-10", "2016-11-03", "2016-11-23", "2016-12-23",
        "2017-01-01", "2017-01-02", "2017-01-03", "2017-01-09", "2017-02-11", "2017-03-20"
    };

    private static byte[] template = null;
    
    public static void main(String[] args) {

        template = readTemplate();
        
        Calendar cal = Calendar.getInstance();
        int maxWeek;
        
        cal.set(Calendar.YEAR, 2016);
        maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1;
        for (int week = 1; week <= maxWeek; week++) {
            createSvgCal(2016,week);
        }
        
        cal.set(Calendar.YEAR, 2017);
        //maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1;
        for (int week = 1; week <= 14; week++) {
            createSvgCal(2017,week);
        }
    }
    private static void createSvgCal(int year, int week) {        
        Map<String, String> valMap = new LinkedHashMap<>();
        valMap.put("#Start", getDate(year, week, Calendar.MONDAY));
        valMap.put("#End", getDate(year, week + 1, Calendar.SUNDAY));

        valMap.put("#MonClass", getClass(year, week, Calendar.MONDAY));
        valMap.put("#TueClass", getClass(year, week, Calendar.TUESDAY));
        valMap.put("#WedClass", getClass(year, week, Calendar.WEDNESDAY));
        valMap.put("#ThuClass", getClass(year, week, Calendar.THURSDAY));
        valMap.put("#FriClass", getClass(year, week, Calendar.FRIDAY));
        
        valMap.put("#Mon", getDate(year, week, Calendar.MONDAY));
        valMap.put("#Tue", getDate(year, week, Calendar.TUESDAY));
        valMap.put("#Wed", getDate(year, week, Calendar.WEDNESDAY));
        valMap.put("#Thu", getDate(year, week, Calendar.THURSDAY));
        valMap.put("#Fri", getDate(year, week, Calendar.FRIDAY));
        valMap.put("#Sat", getDate(year, week, Calendar.SATURDAY));
        valMap.put("#Sun", getDate(year, week + 1, Calendar.SUNDAY));
        
        valMap.putAll(getCalendar(year, week, -1));
        valMap.putAll(getCalendar(year, week, 0));
        valMap.putAll(getCalendar(year, week, +1));

        valMap.put(
                "<!-- CURRENT WEEK RECT -->",
                String.format(
                    "<rect width=\"190\" height=\"11\" x=\"1037\" y=\"%d\"/>", 
                    getRow(year, week)));
        
        String calFile = String.format("%s/Desktop/Cal/%d-%02d.svg", System.getProperty("user.home"), year, week);
        System.out.println(calFile);
        File calSvg = new File(calFile);
        if (!calSvg.getParentFile().isDirectory()) {
            calSvg.getParentFile().mkdirs();
        }
        try (
                PrintWriter pw = new PrintWriter(calSvg, "UTF-8");
                ){
            String calString = new String(template, "UTF-8");
            for (Map.Entry<String, String> entry : valMap.entrySet()) {
                calString = calString.replaceAll(entry.getKey(), entry.getValue());
            }
            // System.out.println(calString);
            pw.print(calString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int getRow(int year, int week) {
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, week, Calendar.WEDNESDAY);
        int wednesday = cal.get(Calendar.DAY_OF_MONTH);
        int row = 1;
        for (int day = 1; day <= wednesday; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            if (Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                row += 1;
            }
        }
        
        switch(row){
            case 1:
                return -245;
            case 2:
                return -233;
            case 3:
                return -221;
            case 4:
                return -210;
            case 5:
                return -198;
            default:
                return -187;
        }
    }
    
    private static String getDate(int year, int week, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, week, day);
        return dateFormat.format(cal.getTime());        
    }

    private static String getClass(int year, int week, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, week, day);
        Date date = cal.getTime();
        return (isHoliday(date) ? "sunday" : "");
    }

    private static String getDay(Date date) {
        String dayString = dayFormat.format(date);
        int day = Integer.parseInt(dayString);
        if (day < 10) {
            dayString = "&#160;" + dayString;
        }
        
        return (isHoliday(date) ? " class=\"sunday\">" : ">") + dayString;
    }

    private static boolean isHoliday(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int wday = cal.get(Calendar.DAY_OF_WEEK);
        if (Calendar.SATURDAY == wday || Calendar.SUNDAY == wday) {
            return false;
        }

        for (String holiday : holidays) {
            try {
                if (DateUtils.isSameDay(date, dateFormat.parse(holiday))) {
                    return true;
                }
            } catch (ParseException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
        return false;
    }

    private static byte[] readTemplate() {
        try (
                InputStream in = App.class.getResourceAsStream("/OneWeek.svg");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();) {

            int size;
            byte[] buf = new byte[1024];
            while ((size = in.read(buf)) > 0) {
                bout.write(buf, 0, size);
            }
            return bout.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static Date getMonth(int year, int week, int diff) {
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, week, Calendar.WEDNESDAY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DateUtils.addMonths(cal.getTime(), diff);
    }

    private static Map<String, String> getCalendar(int year, int week, int diff) {
        Date date = getMonth(year, week, diff);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int row = 0;
        String[][] contents = new String[6][7];
        for (int day = 1; day <= maxDate; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);

            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    contents[row][0] = getDay(cal.getTime());
                    break;
                case Calendar.TUESDAY:
                    contents[row][1] = getDay(cal.getTime());
                    break;
                case Calendar.WEDNESDAY:
                    contents[row][2] = getDay(cal.getTime());
                    break;
                case Calendar.THURSDAY:
                    contents[row][3] = getDay(cal.getTime());
                    break;
                case Calendar.FRIDAY:
                    contents[row][4] = getDay(cal.getTime());
                    break;
                case Calendar.SATURDAY:
                    contents[row][5] = getDay(cal.getTime());
                    break;
                case Calendar.SUNDAY:
                    contents[row][6] = getDay(cal.getTime());
                    row += 1;
                    break;
            }
        }

        Map<String, String> valMap = new LinkedHashMap<>();

        if (-1 == diff) {
            valMap.put("#Cal1", monthFormat.format(date));
            for (row = 0; row < 6; row++) {
                for (int col = 0; col < 7; col++) {
                    String key = String.format(">#%s%d", IDX[row], col);
                    String val = null == contents[row][col] ? ">" : contents[row][col];
                    valMap.put(key, val);
                }
            }
        } else if (0 == diff) {
            valMap.put("#Cal2", monthFormat.format(date));
            for (row = 0; row < 6; row++) {
                for (int col = 0; col < 7; col++) {
                    String key = String.format(">#%s%d", IDX[row + 6], col);
                    String val = null == contents[row][col] ? ">" : contents[row][col];
                    valMap.put(key, val);
                }
            }
        } else {
            valMap.put("#Cal3", monthFormat.format(date));
            for (row = 0; row < 6; row++) {
                for (int col = 0; col < 7; col++) {
                    String key = String.format(">#%s%d", IDX[row + 12], col);
                    String val = null == contents[row][col] ? ">" : contents[row][col];
                    valMap.put(key, val);
                }
            }
        }

        return valMap;
    }
}
