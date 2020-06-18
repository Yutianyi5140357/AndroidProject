package cn.zju.id21932036.yutianyi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.iipc.android.tweetlib.SubmitProgram;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener{
    Button zero,one,two,three,four,five,six,seven,eight,nine;
    Button multi,div,add,sub;
    Button cls,equal;
    EditText result;
    boolean clr_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        zero= (Button) findViewById(R.id.zero);
        one= (Button) findViewById(R.id.one);
        two= (Button) findViewById(R.id.two);
        three= (Button) findViewById(R.id.three);
        four= (Button) findViewById(R.id.four);
        five= (Button) findViewById(R.id.five);
        six= (Button) findViewById(R.id.six);
        seven= (Button) findViewById(R.id.seven);
        eight= (Button) findViewById(R.id.eight);
        nine= (Button) findViewById(R.id.nine);
        add= (Button) findViewById(R.id.add);
        sub= (Button) findViewById(R.id.sub);
        multi= (Button) findViewById(R.id.multi);
        div= (Button) findViewById(R.id.divide);
        cls= (Button) findViewById(R.id.clear);
        equal= (Button) findViewById(R.id.equal);
        result= (EditText) findViewById(R.id.content);

        zero.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        add.setOnClickListener(this);
        sub.setOnClickListener(this);
        multi.setOnClickListener(this);
        div.setOnClickListener(this);
        cls.setOnClickListener(this);
        equal.setOnClickListener(this);

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

        //noinspection SimplifiableIfStatement
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
    public void onClick(View view) {
        String str=result.getText().toString();
        switch (view.getId()){
            case   R.id.zero:
            case   R.id.one:
            case   R.id.two:
            case   R.id.three:
            case   R.id.four:
            case   R.id.five:
            case   R.id.six:
            case   R.id.seven:
            case   R.id.eight:
            case   R.id.nine:
                if(clr_flag){
                    clr_flag=false;
                    str="";
                    result.setText("");
                }
                result.setText(str+((Button)view).getText());
                break;
            case R.id.add:
            case R.id.sub:
            case R.id.multi:
            case R.id.divide:
                if(clr_flag){
                    clr_flag=false;
                    str="";
                    result.setText("");
                }
                if(str.contains("+")||str.contains("-")||str.contains("*")||str.contains("/")) {
                    str=str.substring(0,str.indexOf(" "));
                }
                result.setText(str+" "+((Button)view).getText()+" ");
                break;
            case R.id.clear:
                if(clr_flag)
                    clr_flag=false;
                str="";
                result.setText("");
                break;
            case R.id.equal:
                getResult();
                break;
        }
    }

    private void getResult() {
        String exp=result.getText().toString();
        if(exp==null||exp.equals("")) return ;
        if(!exp.contains(" ")){
            return ;
        }
        if(clr_flag){
            clr_flag=false;
            return;
        }
        clr_flag=true;
        String s1=exp.substring(0,exp.indexOf(" "));
        String op=exp.substring(exp.indexOf(" ")+1,exp.indexOf(" ")+2);
        String s2=exp.substring(exp.indexOf(" ")+3);
        double cnt=0;
        if(!s1.equals("")&&!s2.equals("")){
            double d1=Double.parseDouble(s1);
            double d2=Double.parseDouble(s2);
            if(op.equals("+")){
                cnt=d1+d2;
            }
            if(op.equals("-")){
                cnt=d1-d2;
            }
            if(op.equals("*")){
                cnt=d1*d2;
            }
            if(op.equals("/")){
                if(d2==0) cnt=0;
                else cnt=d1/d2;
            }
            if(!s1.contains(".")&&!s2.contains(".")&&!op.equals("/")) {
                int res = (int) cnt;
                result.setText(res+"");
            }else {
                result.setText(cnt+"");}
        }
        else if(!s1.equals("")&&s2.equals("")){
            double d1=Double.parseDouble(s1);
            if(op.equals("+")){
                cnt=d1;
            }
            if(op.equals("-")){
                cnt=d1;
            }
            if(op.equals("*")){
                cnt=0;
            }
            if(op.equals("/")){
                cnt=0;
            }
            if(!s1.contains(".")) {
                int res = (int) cnt;
                result.setText(res+"");
            }else {
                result.setText(cnt+"");}
        }
        else if(s1.equals("")&&!s2.equals("")){
            double d2=Double.parseDouble(s2);
            if(op.equals("+")){
                cnt=d2;
            }
            if(op.equals("-")){
                cnt=0-d2;
            }
            if(op.equals("*")){
                cnt=0;
            }
            if(op.equals("/")){
                cnt=0;
            }
            if(!s2.contains(".")) {
                int res = (int) cnt;
                result.setText(res+"");
            }else {
                result.setText(cnt+"");}
        }
        else {
            result.setText("");
        }
    }
}
