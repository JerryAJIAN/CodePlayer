package com.example.codeplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andraskindler.quickscroll.QuickScroll;
import com.codingke.codingkeplayer.adapter.MusicListAdapter;
import com.codingke.codingkeplayer.utils.MediaUtils;
import com.codingke.codingkeplayer.vo.Mp3Info;
import com.example.codeplayer.adapter.MusicListAdapter;
import com.example.codeplayer.utils.MediaUtils;
import com.example.codeplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

/**
 * descreption:
 * company:
 * Created by vince on 15/9/24.
 */
public class MyMusicListFragment extends Fragment implements OnItemClickListener,View.OnClickListener{

    private ListView listView_my_music;
    private ImageView imageView_album;
    private TextView textView_songName,textView2_singer;
    private ImageView imageView2_play_pause,imageView3_next;
    private QuickScroll quickscroll;
    private ArrayList<Mp3Info> mp3Infos;
    private MusicListAdapter myMusicListAdapter;

    private MainActivity mainActivity;

    private CodePlayerApp app;

    private int position = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        return my;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        listView_my_music = (ListView) view.findViewById(R.id.lv_my_music);
        imageView_album = (ImageView) view.findViewById(R.id.iv_album);
        imageView2_play_pause = (ImageView) view.findViewById(R.id.list_iv_play_pause);
        imageView3_next = (ImageView) view.findViewById(R.id.lit_iv_next);
        textView_songName = (TextView) view.findViewById(R.id.list_tv_songName);
        textView2_singer = (TextView) view.findViewById(R.id.list_tv_singer);

        quickscroll = (QuickScroll) view.findViewById(R.id.my_music_quickscroll);
        listView_my_music.setOnItemClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_next.setOnClickListener(this);
        imageView_album.setOnClickListener(this);
        //loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //绑定播放服务
//        System.out.println("myMusiclistFragment onresume...");
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        //解除绑定播放服务
        mainActivity.unbindPlayService();
//        System.out.println("myMusiclistFragment    onPause...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    /**
     * 加载本地音乐列表
     */
    public void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        //mp3Infos = mainActivity.playService.mp3Infos;
        myMusicListAdapter = new MusicListAdapter(mainActivity,mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);

        initQuickscroll();

    }

    private void initQuickscroll() {
        quickscroll.init(QuickScroll.TYPE_POPUP_WITH_HANDLE, listView_my_music, myMusicListAdapter, QuickScroll.STYLE_HOLO);
        quickscroll.setFixedSize(1);
        quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
//        quickscroll.setIndicatorColor(bgColor, bgColor, Color.WHITE);
//        quickscroll.setHandlebarColor(bgColor, bgColor, bgColor);
//        quickscroll.setPopupColor(bgColor,0,0,Color.WHITE,0);
        quickscroll.setPopupColor(QuickScroll.BLUE_LIGHT, QuickScroll.BLUE_LIGHT_SEMITRANSPARENT, 1, Color.WHITE, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mainActivity.playService.getChangePlayList()!=PlayService.MY_MUSIC_LIST){
            mainActivity.playService.setMp3Infos(mp3Infos);
            mainActivity.playService.setChangePlayList(PlayService.MY_MUSIC_LIST);
        }
        mainActivity.playService.play(position);

        //保存播放时间
        savePlayRecord();
    }

    //保存播放记录
    private void savePlayRecord() {
        Mp3Info mp3Info = mainActivity.playService.getMp3Infos().get(mainActivity.playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = mainActivity.app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
            if(playRecordMp3Info==null){
                mp3Info.setMp3InfoId(mp3Info.getId());
                mp3Info.setPlayTime(System.currentTimeMillis());//设置当前播放时间
                mainActivity.app.dbUtils.save(mp3Info);
            }else{
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                mainActivity.app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //回调播放状态下的UI设置
    public void changeUIStatusOnPlay(int position){
        if(position>=0 && position<mainActivity.playService.mp3Infos.size()){
            Mp3Info mp3Info = mainActivity.playService.mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTitle());
            textView2_singer.setText(mp3Info.getArtist());
            if (mainActivity.playService.isPlaying()){
                imageView2_play_pause.setImageResource(R.mipmap.pause);
            }else{
                imageView2_play_pause.setImageResource(R.mipmap.play);
            }


            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView_album.setImageBitmap(albumBitmap);
            this.position = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView2_play_pause: {
                if (mainActivity.playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
                    mainActivity.playService.pause();
                } else {
                    if (mainActivity.playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView3_next:{
                mainActivity.playService.next();
                break;
            }
            case R.id.imageView_album: {
                Intent intent = new Intent(mainActivity,PlayActivity.class);
                startActivity(intent);
                break;
            }

            default:
                break;

        }
    }
}
