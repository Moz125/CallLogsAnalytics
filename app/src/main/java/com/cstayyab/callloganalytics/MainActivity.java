package com.cstayyab.callloganalytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button dialed, recieved, talked, missed;
    TextView list_heading;

    ListView detailed_numbers_list;

    EditText date_picker;

    String dateflag;
    final Calendar myCalendar = Calendar.getInstance();
    CallLogUtils logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRuntimePermission();

        dialed = findViewById(R.id.dailed);
        recieved = findViewById(R.id.received);
        talked = findViewById(R.id.talked);
        missed = findViewById(R.id.missed);

        list_heading = findViewById(R.id.list_heading);
        detailed_numbers_list = findViewById(R.id.detailed_numbers_list);


        date_picker = (EditText) findViewById(R.id.date);
        dateflag = date_picker.getText().toString();
        logs = CallLogUtils.getInstance(this);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        dialed.setOnClickListener(this);
        recieved.setOnClickListener(this);
        talked.setOnClickListener(this);
        missed.setOnClickListener(this);

        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        date_picker.setText(strDate);
        CallLogUtils.getInstance(this).readCallLogs();
        setDialedCalls();

    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date_picker.setText(sdf.format(myCalendar.getTime()));
        setDialedCalls();
    }




    private boolean getRuntimePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG},123);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //setUpViewPager();
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG);
            }else{
                finish();
            }
        }
    }

    private void setDialedCalls() {
        list_heading.setText("Dialed Numbers");
        ArrayList<CallLogInfo> d = logs.getOutgoingCalls();

        d = CallLogUtils.unifyList(d, myCalendar.getTime());
        Toast.makeText(this, "Total Logs: " + d.size(), Toast.LENGTH_LONG).show();
        int total = 0;
        if(d.size() >= 5) {
            total = 5;
        } else {
            total = d.size();
        }
        String[] details = new String[total];
        String[] durations = new String[total];
        String[] phones = new String[total];
        for(int i =0;i<total;i++) {
            details[i] = d.get(i).getNumber();
            if(d.get(i).getName() == null) {
                details[i] = "Unknown Contact";
            } else {
                details[i] = d.get(i).getName();
            }
            durations[i] = Utils.formatSeconds(d.get(i).getDuration());
            phones[i] = d.get(i).getNumber();
        }
        CustomList l = new CustomList(this, details, durations, phones);
        detailed_numbers_list.setAdapter(l);
    }
    private void setTalked() {
        list_heading.setText("Talked Numbers");
        ArrayList<CallLogInfo> d = logs.getTalkedCalls();
        d = CallLogUtils.unifyList(d, myCalendar.getTime());
        Toast.makeText(this, "Total Logs: " + d.size(), Toast.LENGTH_LONG).show();
        int total = 0;
        if(d.size() >= 5) {
            total = 5;
        } else {
            total = d.size();
        }
        String[] details = new String[total];
        String[] durations = new String[total];
        String[] phones = new String[total];
        for(int i =0;i<total;i++) {
            details[i] = d.get(i).getNumber();if(d.get(i).getName()==null) {
                details[i] = "Unknown Contact";
            } else {
                details[i] = d.get(i).getName();
            }
            durations[i] = Utils.formatSeconds(d.get(i).getDuration());
            phones[i] = d.get(i).getNumber();
        }
        CustomList l = new CustomList(this, details, durations, phones);
        detailed_numbers_list.setAdapter(l);
    }

    private void setReceived() {
        list_heading.setText("Recieved Numbers");
        ArrayList<CallLogInfo> d = logs.getIncommingCalls();
        d = CallLogUtils.unifyList(d, myCalendar.getTime());
        Toast.makeText(this, "Total Logs: " + d.size(), Toast.LENGTH_LONG).show();
        int total = 0;
        if(d.size() >= 5) {
            total = 5;
        } else {
            total = d.size();
        }
        String[] details = new String[total];
        String[] durations = new String[total];
        String[] phones = new String[total];
        for(int i =0;i<total;i++) {
            details[i] = d.get(i).getNumber();if(d.get(i).getName() == null) {
                details[i] = "Unknown Contact";
            } else {
                details[i] = d.get(i).getName();
            }
            durations[i] = Utils.formatSeconds(d.get(i).getDuration());
            phones[i] = d.get(i).getNumber();
        }
        CustomList l = new CustomList(this, details, durations, phones);
        detailed_numbers_list.setAdapter(l);
    }
    private void setMissed() {
        list_heading.setText("Missed Numbers");
        ArrayList<CallLogInfo> d = logs.getMissedCalls();
        d = CallLogUtils.unifyList(d, myCalendar.getTime());
        Toast.makeText(this, "Total Logs: " + d.size(), Toast.LENGTH_LONG).show();
        int total = 0;
        if(d.size() >= 5) {
            total = 5;
        } else {
            total = d.size();
        }
        String[] details = new String[total];
        String[] durations = new String[total];
        String[] phones = new String[total];
        for(int i =0;i<total;i++) {
            details[i] = d.get(i).getNumber();
            if(d.get(i).getName() == null) {
                details[i] = "Unknown Contact";
            } else {
                details[i] = d.get(i).getName();
            }
            durations[i] = Utils.formatSeconds(d.get(i).getDuration());
            phones[i] = d.get(i).getNumber();
        }
        CustomList l = new CustomList(this, details, durations, phones);
        detailed_numbers_list.setAdapter(l);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == dialed.getId()) {
            setDialedCalls();
        } else if(v.getId() == recieved.getId()) {
            setReceived();

        } else if(v.getId() == talked.getId()) {
            setTalked();


        } else if(v.getId() == missed.getId()) {
            setMissed();
        }

    }

    public class CustomList extends ArrayAdapter<String> {
        private String[] names;
        private String[] durations;
        private String[] numbers;

        private Activity context;

        public CustomList(Activity context, String[] names, String[] durations, String[] numbers) {
            super(context, R.layout.advance_list, names);//sending R.layout file and one array is necessary here
            this.context = context;

            this.names = names;
            this.durations = durations;
            this.numbers = numbers;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.advance_list, null, true);

            //TextView index = listViewItem.findViewById(R.id.index);
            TextView name = listViewItem.findViewById(R.id.name);
            TextView duration = listViewItem.findViewById(R.id.duration);
            TextView number = listViewItem.findViewById(R.id.number);

            ImageView image = listViewItem.findViewById(R.id.picture);

            //index.setText(position+1 + "");
            name.setText(names[position]);
            duration.setText(durations[position]);
            number.setText(numbers[position]);
            image.setImageResource(R.drawable.u2);

            return listViewItem;
        }
    }
}
