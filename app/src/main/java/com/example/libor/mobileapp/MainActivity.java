package com.example.libor.mobileapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText name, number;
    Spinner network;
    ImageButton add, delete;
    public String getNetwork = "SUN";
    ArrayAdapter adapterContacts;
    ListView lstviewContacts;
    Animation onClick;
    public int valueID[];
    public String str;
    public String txtDuplicate = "0";
    public String split[];
    public int cntrContacts;
    public String provider[][];
    public int position,ctrl, cellNum;
    public boolean update = false;

    private DataBaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        network = (Spinner) findViewById(R.id.network);
        add = (ImageButton) findViewById(R.id.add);
        delete = (ImageButton) findViewById(R.id.delete);
        lstviewContacts = (ListView) findViewById(R.id.ListViewContacts);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.network, R.layout.spinner_item);
        network.setAdapter(adapter);

        onClick = AnimationUtils.loadAnimation(this, R.anim.alpha);

        dbhelper = new DataBaseHelper(MainActivity.this, "ContactDatabase", 2);

        provider = new String[][] {{"0922","0923","0924"},{"0955","0956","0965"},{"0928","0929","0930"}};

        refresh();
        reload();
        network();
        name();
        number();
        add();
        delete();
        longClick();
    }

    private void longClick()
    {
        final SQLiteDatabase db = dbhelper.getWritableDatabase();

        lstviewContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    cntrContacts=valueID[i];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                alertDialog.setTitle("Warning!");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("UPDATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        update = true;
                        number.setText("");
                        number.setHint("Update contact number");
                        add.setImageResource(R.drawable.save);
                        delete.setImageResource(R.drawable.cancel);

                        try
                        {
                            String query = "Select * FROM tblcontact WHERE contactID = '" + cntrContacts + "'";
                            Cursor cursor = db.rawQuery(query, null);

                            if (cursor.moveToFirst()) {
                                cursor.moveToFirst();
                                name.setText(cursor.getString(2));
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.setNeutralButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String sqlStr = "DELETE from tblcontact where contactID = '" + cntrContacts + "'";
                        db.execSQL(sqlStr);
                        refresh();
                        reload();
                    }
                }); alertDialog.show();
                return false;
            }
        });
    }

    private void delete()
    {
        final SQLiteDatabase dbDelete = dbhelper.getWritableDatabase();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                delete.startAnimation(onClick);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                if(update = true)
                {
                    refresh();
                    reload();
                }
                else
                {
                    if(ctrl > 0)
                    {
                        alertDialog.setTitle("Confirm Delete");
                        alertDialog.setMessage("Are you sure you want delete all records?");
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                String sqlStr = "DELETE from tblcontact";
                                dbDelete.execSQL(sqlStr);
                                refresh();
                                reload();
                            }
                        });
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }); alertDialog.show();
                    }
                    else
                    {
                        alertDialog.setTitle("No record found!");
                        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }); alertDialog.show();
                    }
                }
            }
        });
    }

    private void refresh()
    {
        name.setText("");
        network.setSelection(0);
        number.setText("");
        number.setHint("Contact number");
        add.setImageResource(R.drawable.add);
        delete.setImageResource(R.drawable.delete);
        getNetwork = "SUN";
    }

    private void add()
    {
        final SQLiteDatabase db = dbhelper.getWritableDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        try{
                add.startAnimation(onClick);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                if ((name.length() == 0) && (number.length() == 0))
                {
                    alertDialog.setTitle("Missing field!");
                    alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }); alertDialog.show();
                }
                else if (name.length() != 0 && number.length() != 0)
                {
                    try {
                        String query = "Select * FROM tblcontact WHERE contacts = '" + cellNum + "'";
                        Cursor cursor = db.rawQuery(query, null);

                        if (cursor.moveToFirst()) {
                            cursor.moveToFirst();
                            txtDuplicate = cursor.getString(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (Integer.parseInt(txtDuplicate) == cellNum) {

                        alertDialog.setTitle("Contact number already exist!");
                        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }); alertDialog.show();
                    }
                    else
                        {

                        for (int x = 0; x <= 2; x++) {

                            if (split[0].equals(provider[position][x])) {

//                                if (update = true) {
//
//                                    update = false;
//
//                                    try {
//
//                                        String sqlStr = "UPDATE tblcontacts SET name = '" + name.getText().toString()
//                                                + "', network = '" + getNetwork + "', contact = '" + cellNum
//                                                + "' where id = '" + cntrContacts + "'";
//                                        db.execSQL(sqlStr);
//                                        refresh();
//                                        reload();
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                }
//                                else {
//                                    try {
//                                        String sqlStr = "INSERT INTO tblcontacts (name,contact,network) VALUES ('"
//                                                + name.getText().toString() + "', '" + cellNum
//                                                + "', '" + getNetwork + "')";
//                                        db.execSQL(sqlStr);
//                                        refresh();
//                                        reload();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                }
                                try {
                                    String sqlStr = "INSERT INTO tblcontact (Contactname,contactNum,networkProv) VALUES ('"
                                            + name.getText().toString() + "', '" + cellNum
                                            + "', '" + getNetwork + "')";
                                    db.execSQL(sqlStr);
                                    refresh();
                                    reload();
                                } catch (Exception e) {
                                    e.printStackTrace();Toast.makeText(MainActivity.this,""+e,Toast.LENGTH_LONG).show();
                                }
                                break;

                            } else if (x == 2) {
                                alertDialog.setTitle("Invalid contact number!");
                                alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });alertDialog.show();
                            }
                        }
                    }
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            }
        });
    }

    private void network()
    {
        network.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getNetwork = network.getItemAtPosition(i).toString();
                position = i;
                reload();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void number()
    {
        number.addTextChangedListener(new TextWatcher() {

            int len=0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                str = number.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(number.length() == 12) {
                    split = str.split("-");
                    cellNum = Integer.parseInt(split[0]+""+split[1]);
                } else {
                    number.setError("This field cannot be blank!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                str = number.getText().toString();
                if(str.length() == 4 && len < str.length())
                {
                    number.append("-");
                }
            }
        });
    }

    private void name()
    {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(name.length() != 0) {
                } else {
                    name.setError("This field cannot be blank!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void reload()
    {
        SQLiteDatabase dbContacts = dbhelper.getWritableDatabase();

        Cursor contacts = dbContacts.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tblcontact'", null);
        contacts.moveToNext();

        if (contacts.getCount() == 0) {
            SQLite.FITCreateTable("ContactDatabase", this, "tblcontact",
                    "contactID INTEGER PRIMARY KEY AUTOINCREMENT, " + "Contactname VARCHAR(90),contactNum VARCHAR(MAX),networkProv VARCHAR(90)");
        }
        else {
            contacts = dbContacts.rawQuery("SELECT contactID, Contactname,contactNum,networkProv FROM tblcontact WHERE networkProv = '"+ getNetwork
                    +"' order by Contactname asc", null);

            String valueContacts[] = new String[contacts.getCount()];
            int valueCurrentId[] = new int[contacts.getCount()];

            ctrl = 0;
            while (contacts.moveToNext()) {

                String strFor = "";
                int strId;

                strFor += "Name : " + contacts.getString(contacts.getColumnIndex("Contactname"));
                strFor += System.lineSeparator() + "Contact # : " + contacts.getString(contacts.getColumnIndex("contactNum"));
                strFor += System.lineSeparator() + "Network : " + contacts.getString(contacts.getColumnIndex("networkProv"));
                strId = contacts.getInt(contacts.getColumnIndex("contactID"));

                valueContacts[ctrl] = strFor;
                valueCurrentId[ctrl] = strId;

                ctrl++;
            }
            valueID = Arrays.copyOf(valueCurrentId, contacts.getCount());

            adapterContacts = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valueContacts);

            try {
                lstviewContacts.setAdapter(adapterContacts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}