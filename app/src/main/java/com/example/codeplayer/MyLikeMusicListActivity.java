package com.example.codeplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codingke.codingkeplayer.adapter.MusicListAdapter;
import com.codingke.codingkeplayer.vo.Mp3Info;
import com.example.codeplayer.adapter.MusicListAdapter;
import com.example.codeplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * descreption:
 * company:
 * Created by vince on 15/10/10.
 */
public class MyLikeMusicListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private ListView listView_like;
    private CodingkePlayerApp app;
    private ArrayList<Mp3Info> likeMp3Infos;
    private MusicListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         app = (CodingkePlayerApp) getApplication();
        setContentView(R.layout.activity_like_music_list);
        listView_like = (ListView) findViewById(R.id.listView_like);
        listView_like.setOnItemClickListener(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    private void initData() {
        try {
            List<Mp3Info> list =  app.dbUtils.findAll(Selector.from(Mp3Info.class).where("isLike","=","1"));
            if(list==null || list.size()==0){
                return;
            }
            likeMp3Infos = (ArrayList<Mp3Info>)list;
            adapter = new MusicListAdapter(this,likeMp3Infos);
            listView_like.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(playService.getChangePlayList()!=PlayService.LIKE_MUSIC_LIST){
            playService.setMp3Infos(likeMp3Infos);
            playService.setChangePlayList(PlayService.LIKE_MUSIC_LIST);
        }
        playService.play(position);
        //保存播放时间
        savePlayRecord();
    }


    //保存播放记录
    private void savePlayRecord() {
        //获取当前正在播放的音乐对象
        Mp3Info mp3Info = playService.getMp3Infos().get(playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getMp3InfoId()));
            if(playRecordMp3Info==null){
                mp3Info.setPlayTime(System.currentTimeMillis());
                app.dbUtils.save(mp3Info);
            }else{
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
