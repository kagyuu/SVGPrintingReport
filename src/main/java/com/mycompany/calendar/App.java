package com.mycompany.calendar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
     * 休日 (内閣府発表 https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html) から
     * syukujitsu.csv をダウンロードして /src/main/resources に配置する
     * $ wget https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv
     */
    private static List<Date> holidays;

    private static byte[] template = null;

    public static void main(String[] args) {

        holidays = readSyukujitsu();
        template = readTemplate();

        Calendar cal = Calendar.getInstance();
        int maxWeek;

        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1;
        for (int week = 0; week <= maxWeek; week++) {
            createSvgCal(cal.get(Calendar.YEAR), week);
        }

//        for (int week = 1; week <= 14; week++) {
//            createSvgCal(cal.get(Calendar.YEAR) + 1, week);
//        }
        
        // copy shells
        Set<PosixFilePermission> perm = new HashSet<>();
        perm.add(PosixFilePermission.OWNER_READ);
        perm.add(PosixFilePermission.OWNER_WRITE);
        perm.add(PosixFilePermission.OWNER_EXECUTE);
        
        for (String file : new String[]{"A3toA4.sh", "svg2cvs.sh", "createBook.sh", "blanksheet-a4-portrait.pdf"}){
            File src = new File(String.format("src/main/resources/%s", file));
            File dest = new File(String.format("%s/Desktop/Cal/%s", System.getProperty("user.home"), file));
            try {
                Files.copy(src.toPath(), dest.toPath());
                Files.setPosixFilePermissions(dest.toPath(), perm);
                System.out.println(file);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                PrintWriter pw = new PrintWriter(calSvg, "UTF-8");) {
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

        switch (row) {
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

        for (Date holiday : holidays) {
            if (DateUtils.isSameDay(date, holiday)) {
                return true;
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

    private static List<Date> readSyukujitsu() {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("src/main/resources/syukujitsu.csv"),"UTF-8"))) {

                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    List<Date> holidayArray = new ArrayList<>();
                    String line;
                    while ((line = r.readLine()) != null) {
                        String[] part = line.split(",");
                        try {
                            holidayArray.add(df.parse(part[0]));
                        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
                        }
                    }

                    Collections.sort(holidayArray);

                    System.out.println("HOLIDAYS");
                    holidayArray.stream().forEach((d) -> {
                        System.out.println(dateFormat.format(d));
                    });

                    return holidayArray;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
    }
}
