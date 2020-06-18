package cn.zju.id21932036.yutianyi;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.iipc.android.tweetlib.SubmitProgram;

public class MemoActivity extends AppCompatActivity implements TextWatcher{
    private EditText textMsg;
    private TextView pkgName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        textMsg = (EditText) findViewById(R.id.Content);

        pkgName=(TextView)findViewById(R.id.txtHint);
        pkgName.setText(this.getPackageName());
        File file = new File(getFilesDir(), "memo.txt");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //textMsg.setText("Create Error:"+file.getPath().toString());
            }
        }
        fileTestRead(file);
        textMsg.addTextChangedListener(this);
    }

    private void fileTestRead(File file) {
        String fn = file.getPath();
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(fn));
            String line = bfr.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = bfr.readLine();
            }
            bfr.close();
            Log.d("buffer", "bufferRead: " + sb.toString());
            textMsg.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fileTestWrite(File file) {
        String fn = file.getPath();
        String content = textMsg.getText().toString();

        try{
            PrintWriter o = new PrintWriter(new BufferedWriter(new FileWriter(fn)));
            o.println(content);
            o.close();
        }catch(Exception e){
            textMsg.setText("Write Error");
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        File file = new File(getFilesDir(), "memo.txt");
        fileTestWrite(file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_submit:
                new SubmitProgram().submit(this,"E1");
                return super.onOptionsItemSelected(item);
            case R.id.action_close:
                finish();
                break;
        }
        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        pkgName.setBackgroundColor(Color.RED);
    }
}
