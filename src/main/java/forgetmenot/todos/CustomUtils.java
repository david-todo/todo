package forgetmenot.todos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderInstTable;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.database.ListItemInstTable;
import forgetmenot.todos.database.ListItemTable;

/**
 * Created by SherrelD on 3/7/2017.
 */

public class CustomUtils {
    public static int compare4charTime(String time1, String time2) {
        //compare 4 character time strings.  Need to take into account leading zeros

        if (time1==time2) {
            return 0; //equal
        }else {
            int t1 = convert4charTimetoInt(time1);
            int t2 = convert4charTimetoInt(time2);

            if (t1>t2) {
                return 1; //time 1 is greater
            }else {
                return 2; //time 2 is greater
            }
        }
    }

    public static int getMinIntfrom4charTime(String time) {
        //check for 0 as first char of min
        if (time.charAt(2) == '0') {
            return Integer.parseInt(time.substring(3,4));
        }else{
            return Integer.parseInt(time.substring(2,4));
        }
    }

    public static int getHrIntfrom4charTime(String time) {
        //check for 0 as first character of Hr;
        if (time.charAt(0) == '0') {
            return Integer.parseInt(time.substring(1,2));
        }else {
            return Integer.parseInt(time.substring(0,2));
        }
    }
    public static int convert4charTimetoInt(String time) {
        if (time.charAt(0) == '0') {
            return Integer.parseInt(time.substring(1,4));
        } else {
            return Integer.parseInt(time);
        }
    }

    public static int getIdfromUri(Uri uri) {
        int uriId = 0;
        int pos = 0;

        if (uri != null) {
            String texturi = uri.toString();
            String stringid = null;
            Boolean found = false;
            for (int i=(texturi.length()-1);i>0;i--) {
                if (texturi.charAt(i) == '/') { //reached
                    pos = i+1;
                    found = true;
                    break;
                }
            }
            if (found) {
                stringid = texturi.substring(pos,texturi.length());
                uriId = Integer.parseInt(stringid);
            }
        }

        return uriId;
    }

    public static ListInfo getActiveList(Cursor c, String currday, String nowtime) {
        //get correct column name for day
        String dayCol = getDayColumn(currday);

        //get list of listheaders active on current day
        ArrayList<ListInfo> listheads = getTodayLists(c, dayCol, currday);

        if (listheads.size() == 0) {  //no lists valid today
            return null;
        }else {  //at least 1 list valid today
            //sort array list by startime and then iterate through
            Collections.sort(listheads, new Comparator<ListInfo>() {
                public int compare(ListInfo o1, ListInfo o2) {
                    return o1.startTime.compareTo(o2.startTime);
                }
            });

            ListInfo returnItem = new ListInfo();

            for (ListInfo l : listheads) {
                if (CustomUtils.compare4charTime(l.startTime, nowtime)==2) { //starttime is less
                    if (CustomUtils.compare4charTime(l.deadlineTime,
                            nowtime) == 1) {//deadline has not passed
                        returnItem.listid = l.listid;
                        returnItem.startTime = l.startTime;
                        returnItem.startDay = l.startDay;
                        returnItem.listName = l.listName;
                        returnItem.deadlineTime = l.deadlineTime;
                        break;
                    }
                }
            }

            if (returnItem.listid ==0) {
                return null;
            }else{
                return returnItem;
            }
        }

    }

    public static ListInfo getNextListForDay(Cursor c, String selectday, String selecttime) {
        //get correct column name for day
        String dayCol = getDayColumn(selectday);

        //get list of listheaders active on current day
        ArrayList<ListInfo> listheads = getTodayLists(c, dayCol, selectday);

        if (listheads.size() == 0) { //no lists valid today
            return null;
        }else {
            //sort array list by deadline and then iterate through
            Collections.sort(listheads, new Comparator<ListInfo>() {
                public int compare(ListInfo o1, ListInfo o2) {
                    return o1.startTime.compareTo(o2.startTime);
                }
            });

            ListInfo returnItem = new ListInfo();

            for (ListInfo l : listheads) {
                if (CustomUtils.compare4charTime(l.startTime, selecttime)==1) { //starttime is greater
                    returnItem.listid = l.listid;
                    returnItem.startTime = l.startTime;
                    returnItem.startDay = l.startDay;
                    returnItem.listName = l.listName;
                    returnItem.deadlineTime = l.deadlineTime;
                    break;
                }
            }

            if (returnItem.listid != 0) {
                return returnItem;
            }else {
                return null;
            }
        }
    }
    public static ListInfo getNextList(Cursor c, String currday, String nowtime) {
        Boolean todaylist = false;

        ListInfo nextlist = getNextListForDay(c, currday, nowtime);

        if (nextlist==null) {
            //no list found for today, find next list on remaining days.
            Boolean listfound = false;
            String slidingDay = getNextDay(currday);
            if (!todaylist) {
                //execute this until we find a next valid list or until we run out of days
                do {
                    nextlist = getNextListForDay(c, slidingDay,"0000");
                    if (nextlist!=null) {
                        listfound = true;
                    }
                    slidingDay = getNextDay(slidingDay);
                } while (!listfound && !slidingDay.equals(currday));
            }

        }
        return nextlist;
    }


    private static ArrayList<ListInfo> getTodayLists(Cursor c, String dayCol, String currDay) {
        //iterate through cursor and pull all lists with current day checkbox checked

        //Define local variables
        ArrayList<ListInfo> lists = new ArrayList<ListInfo>();
        int listid;
        String startDay;
        String startTime;
        String deadline;
        String listName;

        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
            if (c.getInt(c.getColumnIndexOrThrow(dayCol))==1) {  //means checkbox is checked
                //add list to string array
                listid = (c.getInt(c.getColumnIndexOrThrow
                        (ListHeaderTable.COLUMN_LISTID)));
                startDay = currDay;
                startTime = (c.getString(c.getColumnIndexOrThrow
                        (ListHeaderTable.COLUMN_STARTTIME)));
                listName = (c.getString(c.getColumnIndexOrThrow
                        (ListHeaderTable.COLUMN_LISTNAME)));
                deadline = (c.getString(c.getColumnIndex
                        (ListHeaderTable.COLUMN_DEADLINE)));
                lists.add(new ListInfo(listid, listName, startDay,startTime,deadline));
            }
        }

        return lists;
    }
    private static String getNextDay(String currday) {
        String nextday;
        switch (currday) {
            case "Mon":
                nextday = "Tue";
                break;
            case "Tue":
                nextday = "Wed";
                break;
            case "Wed":
                nextday = "Thu";
                break;
            case "Thu":
                nextday = "Fri";
                break;
            case "Fri":
                nextday = "Sat";
                break;
            case "Sat":
                nextday = "Sun";
                break;
            case "Sun":
                nextday = "Mon";
                break;
            default:
                return null;
        }
        return nextday;
    }
    private static String getDayColumn(String day) {
        String colname;
        switch (day) {
            case "Mon":
                colname = ListHeaderTable.COLUMN_DUE_MON;
                break;
            case "Tue":
                colname = ListHeaderTable.COLUMN_DUE_TUE;
                break;
            case "Wed":
                colname = ListHeaderTable.COLUMN_DUE_WED;
                break;
            case "Thu":
                colname = ListHeaderTable.COLUMN_DUE_THU;
                break;
            case "Fri":
                colname = ListHeaderTable.COLUMN_DUE_FRI;
                break;
            case "Sat":
                colname = ListHeaderTable.COLUMN_DUE_SAT;
                break;
            case "Sun":
                colname = ListHeaderTable.COLUMN_DUE_SUN;
                break;
            default:
                return null;
        }
        return colname;
    }

    public static Uri addListInstance(Context context, int listid) {
    /* This method takes a list header id and creates a new list
       header instance and new list item instance records for each
       item in the list */

        String selection = ListHeaderTable.COLUMN_LISTID + "=?";
        String[] selectionArgs = {String.valueOf(listid)};
        String status = "open";

       //get header row for list id
        Cursor c = context.getContentResolver().query(MyTodoContentProvider.LISTHEADER_URI,
                ListHeaderTable.PROJECTION, selection, selectionArgs, null, null);

        Uri headInstUri = null;

        if (c != null) {
            ContentValues headinstvalues = new ContentValues();
            String user = getUser();
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEE");
            SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currDay = sdfDay.format(cal.getTime());
            String currDateTime = sdfDateTime.format(cal.getTime());
            c.moveToFirst();
            String listname = c.getString(c.getColumnIndexOrThrow(ListHeaderTable.COLUMN_LISTNAME));


            headinstvalues.put(ListHeaderInstTable.COLUMN_USERID, user);
            headinstvalues.put(ListHeaderInstTable.COLUMN_LISTID, c.getColumnIndexOrThrow(
                    ListHeaderTable.COLUMN_LISTID));
            headinstvalues.put(ListHeaderInstTable.COLUMN_STARTDATETIME, currDateTime);
            headinstvalues.put(ListHeaderInstTable.COLUMN_STARTDAY, currDay);
            headinstvalues.put(ListHeaderInstTable.COLUMN_OVERALLSTAT, status);

            headInstUri = context.getContentResolver().insert(
                    MyTodoContentProvider.LISTHEADERINST_URI, headinstvalues);

            //create list item instance
            if (headInstUri != null) {
                int headInstId = CustomUtils.getIdfromUri(headInstUri);

                selection = ListItemTable.COLUMN_LISTID + "=?";
                Cursor citem = context.getContentResolver().query(MyTodoContentProvider.LISTITEM_URI,
                        ListItemTable.PROJECTION, selection, selectionArgs, null, null);

                if (citem != null) {
                    ContentValues[] iteminstvalues = new ContentValues[citem.getCount()];
                    citem.moveToFirst();
                    status = "open";
                    for (int i=0;i<citem.getCount();i++) {
                        ContentValues cv = new ContentValues();
                        cv.put(ListItemInstTable.COLUMN_HEADINST_ID,headInstId);
                        cv.put(ListItemInstTable.COLUMN_LISTID,listid);
                        cv.put(ListItemInstTable.COLUMN_ITEMNO,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_ITEMNO)));
                        cv.put(ListItemInstTable.COLUMN_ITEMDESC,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_ITEMDESC)));
                        cv.put(ListItemInstTable.COLUMN_CONFIRM,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_CONFIRM)));
                        cv.put(ListItemInstTable.COLUMN_NOTIF_TYPE,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_NOTIF_TYPE)));
                        cv.put(ListItemInstTable.COLUMN_DIST_LIST,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_DIST_LIST)));
                        cv.put(ListItemInstTable.COLUMN_STATUS,status);
                        cv.put(ListItemInstTable.COLUMN_COMPDATETIME, "00000000 00:00:00");

                        iteminstvalues[i] = cv;
                        citem.moveToNext();
                    }

                    int itemCount = context.getContentResolver().bulkInsert(
                            MyTodoContentProvider.LISTITEMINST_URI,iteminstvalues);

                }

            }
        }

        c.close();
        return headInstUri;
    }

    public static String getUser() {
        return "sherreld";
    }
}
