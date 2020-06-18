package cn.zju.id21932036.yutianyi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.iipc.android.tweetlib.SubmitProgram;

public class MusicActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private TextView text1;
    private ListView listMusic;
    private ArrayList<Music> list = new ArrayList<>();
    private void getList(){
        ContentResolver provider=getContentResolver();
        Cursor cursor=provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"title","duration","artist",MediaStore.Audio.Media.DATA},
                null, null, null);
        String s;
        while(cursor.moveToNext()){
            int t=Integer.parseInt(cursor.getString(1))/1000;
            s=String.format("%d:%d",t/60, t%60);
//            s=String.format("作者：%s    曲名：%s    时间：%d(秒)",
//                    cursor.getString(0),
//                    cursor.getString(1),
//                    Integer.parseInt(cursor.getString(2))/1000);
            list.add(new Music(cursor.getString(0), s, cursor.getString(2),cursor.getString(3)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        text1 = (TextView)findViewById(R.id.pkgName);
        text1.setText(getPackageName());
        listMusic=(ListView)findViewById(R.id.listMusic);
        getList();
//        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, list);
        MusicAdapter adapter=new MusicAdapter(this,R.layout.music_item,list);
        listMusic.setAdapter(adapter);
        listMusic.setOnItemClickListener(this);

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
            new SubmitProgram().submit(this, "G1");
            return true;
        }
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private MediaPlayer player=new MediaPlayer();
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        player.reset();
        try{
            player.setDataSource(list.get(position).uri);
            player.prepare();
            player.start();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player.isPlaying()) player.stop();
        player.release();
    }

    public class Music{
        public String name, duration, author, uri;

        public Music(String name, String duration, String author, String uri) {
            this.name = name;
            this.duration = duration;
            this.author = author;
            this.uri=uri;
        }
    }

    public class MusicAdapter extends ArrayAdapter<Music>{
        private int rowResource;
        public MusicAdapter(Context context, int resource, List<Music> objs) {
            super(context, resource, objs);
            this.rowResource = resource;
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Music music=getItem(position);
            View view;
            if(convertView==null){
                view= LayoutInflater.from(getContext()).inflate(rowResource,parent,false);
            }else view=convertView;
            TextView txtName, txtDuration, txtAuthor;
            txtName=(TextView)view.findViewById(R.id.txtName);
            txtDuration=(TextView)view.findViewById(R.id.txtDuration);
            txtAuthor=(TextView)view.findViewById(R.id.txtAuthor);
            txtName.setText(music.name);
            txtAuthor.setText(music.author);
            txtDuration.setText(music.duration);
            return view;
        }
    }
}
