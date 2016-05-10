package ua.in.devapp.ussd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
    final String LOG_TAG = "myLogs";
    ExpandableListView elvMain;
    DB db;
    boolean useDualSim;
    SharedPreferences sp;
    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
// получаем SharedPreferences, которое работает с файлом настроек
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // подключаемся к БД
        db = new DB(this);
        db.open();

        // готовим данные по группам для адаптера
        Cursor cursor = db.getOperatorData();
        startManagingCursor(cursor);
        // сопоставление данных и View для групп
        String[] groupFrom = {DB.OPERATOR_COLUMN_NAME};
        int[] groupTo = {android.R.id.text1};
        // сопоставление данных и View для элементов
        String[] childFrom = {DB.COMAND_COLUMN_COMMAND, DB.COMAND_COLUMN_COMMENT};
        int[] childTo = {android.R.id.text1, android.R.id.text2};

        // создаем адаптер и настраиваем список
        SimpleCursorTreeAdapter sctAdapter = new MyAdapter(this, cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_2, childFrom,
                childTo);
        elvMain = (ExpandableListView) findViewById(R.id.elvMain);
        elvMain.setAdapter(sctAdapter);

        // нажатие на элемент
        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition,   int childPosition, long id) {
                Log.d(LOG_TAG, "onChildClick groupPosition = " + groupPosition +
                        " childPosition = " + childPosition +
                        " id = " + id);
                TextView trTemp = (TextView) ((ViewGroup) v).getChildAt(0);

                String txtOperation = trTemp.getText().toString();
                procColl(txtOperation);
                //tvInfo.setText(ah.getGroupChildText(groupPosition, childPosition));
                return false;
            }
        });


    }

    protected void onResume() {
        useDualSim = sp.getBoolean("useDualSim", false);

        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.PreferencesTitle);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Intent intentPreferences = new Intent(this, PrefActivity.class);

                startActivity(intentPreferences);
                // int item_Id = item.getGroupId();
                break;

            default:
                break;
        }

        // Log.d(LOG_TAG, "item_Id - "+item_Id);

        // strMess = (String) item.getTitle();
        // CreateMessage().show();

        return super.onOptionsItemSelected(item);
    }
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void procColl(String Contacts_Phone) {

        Log.d(LOG_TAG, "Contacts_Phone: " + Contacts_Phone);

        // String encodedHash = Uri.encode(В«#В»);
        String encodeContacts_Phone = Uri.encode(Contacts_Phone);

        if (!useDualSim) {

            Intent intent = new Intent("android.intent.action.CALL");
            //intent.putExtra("simId", imeiSIM2);
//            intent.setFlags(0x10000000);
//            intent.putExtra("com.android.phone.extra.slot", imeiSIM2);
//            intent.putExtra("subscription", imeiSIM2);
            // intent.setData(Uri.parse((new
            // StringBuilder("tel://")).append(e()).toString()));
            intent.setData(Uri.parse("tel:" + encodeContacts_Phone));
            startActivity(intent);

            // getResources()
            // intent.getStringExtra("simId")

            // startActivity(new Intent(Intent.ACTION_CALL,
            // Uri.parse("tel:"+encodeContacts_Phone))); //РїСЂРѕРіСЂР°РјРЅС‹Р№ РІС‹Р·РѕРІ РЅР°
            // РїРµСЂРІСѓСЋ РєР°СЂС‚РѕС‡РєСѓ
        } else {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                    + encodeContacts_Phone)));// РґРёР°Р»РѕРі РІС‹Р·РѕРІР°
        }
    }

    class MyAdapter extends SimpleCursorTreeAdapter {

        public MyAdapter(Context context, Cursor cursor, int groupLayout,
                         String[] groupFrom, int[] groupTo, int childLayout,
                         String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // получаем курсор по элементам для конкретной группы
            int idColumn = groupCursor.getColumnIndex(DB.OPERATOR_COLUMN_ID);
            int operatorID = groupCursor.getInt(idColumn);
            return db.getCommandData(operatorID);
        }
    }
}