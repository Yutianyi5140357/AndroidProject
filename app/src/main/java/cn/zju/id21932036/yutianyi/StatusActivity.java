package cn.zju.id21932036.yutianyi;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.iipc.android.tweetlib.SubmitProgram;
import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher{

    private EditText editStatus;
    private Button btnPost;
    private TextView PkgName;
    private TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editStatus = (EditText) findViewById(R.id.editContent);
        btnPost = (Button) findViewById(R.id.btnPost);
        txtCount = (TextView) findViewById(R.id.txtCount);

        PkgName = (TextView)findViewById(R.id.txtPkgName);
        PkgName.setText(getPackageName());

        btnPost.setOnClickListener(this);
        editStatus.addTextChangedListener(this);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            new SubmitProgram().submit(this, "D2");
            return true;
        }
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final String TAG = "StatusActivity";
    @Override
    public void onClick(View view) {

        String status = editStatus.getText().toString();

        Log.d(TAG, "onClicked with status: " + status);
        new PostTask().execute(status);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        int count = 140 - editStatus.length();
        txtCount.setText(Integer.toString(count));

        txtCount.setTextColor(Color.GREEN);
        if(count <= 10) txtCount.setTextColor(Color.YELLOW);
        if(count <= 0 ) txtCount.setTextColor(Color.RED);
    }

    private final class PostTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            YambaClient yambaCloud = new YambaClient("tennie", "yty970129");
            try{
                yambaCloud.postStatus(strings[0]);
                return "Successfully posted: " + strings[0].length() + "chars";
            } catch (YambaClientException e) {
                e.printStackTrace();
                return "Failed to post to yamba service";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(StatusActivity.this, s, Toast.LENGTH_LONG).show();
            PkgName.setText(s + "BY:" + getPackageName());
            if(s.startsWith("Successfully")){
                editStatus.setText("");
            }
        }
    }
}
