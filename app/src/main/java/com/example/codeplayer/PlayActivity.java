package com.example.codeplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codingke.codingkeplayer.utils.Constant;
import com.codingke.codingkeplayer.utils.DownloadUtils;
import com.codingke.codingkeplayer.utils.MediaUtils;
import com.codingke.codingkeplayer.utils.SearchMusicUtils;
import com.codingke.codingkeplayer.vo.Mp3Info;
import com.codingke.codingkeplayer.vo.SearchResult;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import douzi.android.view.DefaultLrcBuilder;
import douzi.android.view.ILrcBuilder;
import douzi.android.view.ILrcView;
import douzi.android.view.LrcRow;
import douzi.android.view.LrcView;

/**
 * descreption:
 * company:
 * Created by vince on 15/9/28.
 */
public class PlayActivity extends BaseActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener {

    private TextView textView1_title, textView1_end_time, textView1_start_time;
    private ImageView imageView1_album, imageView1_play_mode, imageView3_previous, imageView2_play_pause, imageView1_next, ImageView1_favorite;
    private SeekBar seekBar1;
    private ViewPager viewPager;
    private LrcView lrcView;
    private ArrayList<View> views = new ArrayList<>();
    //private ArrayList<Mp3Info> mp3Infos;
    private static final int UPDATE_TIME = 0x10;//更新播放时间的标记
    private static final int UPDATE_LRC = 0x20; //更新歌词

    private CodePlayerApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        app = (CodePlayerApp) getApplication();

//        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
//        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        imageView1_play_mode = (ImageView) findViewById(R.id.ImageView1_play_mode);
        imageView3_previous = (ImageView) findViewById(R.id.ImageView3_previous);
        imageView2_play_pause = (ImageView) findViewById(R.id.ImageView2_play_pause);
        imageView1_next = (ImageView) findViewById(R.id.ImageView1_next);
   //     ImageView1_favorite = (ImageView) findViewById(R.id.ImageView1_favorite);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        initViewPager();

        imageView1_play_mode.setOnClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView1_next.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        ImageView1_favorite.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);

        //mp3Infos = MediaUtils.getMp3Infos(this);

        myHandler = new MyHandler(this);
    }

    private void initViewPager() {
        View album_image_layout = getLayoutInflater().inflate(R.layout.album_image_layout, null);
        imageView1_album = (ImageView) album_image_layout.findViewById(R.id.imageView1_album);
        textView1_title = (TextView) album_image_layout.findViewById(R.id.textView1_title);
        views.add(album_image_layout);
        View lrc_layout = getLayoutInflater().inflate(R.layout.lrc_layout, null);
        lrcView = (LrcView) lrc_layout.findViewById(R.id.lrcView);
        //设置滚动事件
        lrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (playService.isPlaying()) {
                    playService.seekTo((int) row.time);
                }
            }
        });
        lrcView.setLoadingTipText("正在加载歌词");
        lrcView.setBackgroundResource(R.mipmap.jb_bg);
        lrcView.getBackground().setAlpha(150);
        views.add(lrc_layout);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.addOnPageChangeListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private static MyHandler myHandler;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && playService.isPlaying()) {
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    //加载歌词
    private void loadLRC(File lrcFile) {
        StringBuffer buf = new StringBuffer(1024 * 10);
        char[] chars = new char[1024];
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
            int len = -1;
            while ((len = in.read(chars)) != -1) {
                buf.append(chars, 0, len);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(buf.toString());
        lrcView.setLrc(rows);
//            long id = mp3Info.getMp3InfoId()==0?mp3Info.getId():mp3Info.getMp3InfoId();
//            Bitmap bg = MediaUtils.getArtwork(this, id, mp3Info.getAlbumId(), false, false);
//            if(bg!=null){
//                lrcView.setBackground(new BitmapDrawable(getResources(),bg));
//                lrcView.getBackground().setAlpha(120);
//            }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class MyHandler extends android.os.Handler {


        private PlayActivity playActivity;
        private WeakReference<PlayActivity> weak;

        public MyHandler(PlayActivity playActivity) {
            weak = new WeakReference<PlayActivity>(playActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playActivity = weak.get();
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime((int) msg.obj));
                        break;
                    case UPDATE_LRC:
                        playActivity.lrcView.seekLrcToTime((int) msg.obj);
                        break;
                    case DownloadUtils.SUCCESS_LRC:
                        playActivity.loadLRC(new File((String) msg.obj));
                        break;
                    case DownloadUtils.FAILED_LRC:
                        Toast.makeText(playActivity, "歌词下载失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void publish(int progress) {
//        textView1_start_time.setText(MediaUtils.formatTime(progress));
        myHandler.obtainMessage(UPDATE_TIME, progress).sendToTarget();
        seekBar1.setProgress(progress);
        myHandler.obtainMessage(UPDATE_LRC, progress).sendToTarget();
    }

    @Override
    public void change(int position) {
        //if(this.playService.isPlaying()) {
        Mp3Info mp3Info = playService.mp3Infos.get(position);
        textView1_title.setText(mp3Info.getTitle());
        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        imageView1_album.setImageBitmap(albumBitmap);
        textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        seekBar1.setProgress(0);
        seekBar1.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            imageView2_play_pause.setImageResource(R.mipmap.pause);
        } else {
            imageView2_play_pause.setImageResource(R.mipmap.play);
        }
        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.order);
                imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.random);
                imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.single);
                imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                break;
        }
        long id = getId(mp3Info);

        try {
            Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", id));
            if (likeMp3Info != null && likeMp3Info.getIsLike() == 1) {
                ImageView1_favorite.setImageResource(R.mipmap.xin_hong);
            } else {
                ImageView1_favorite.setImageResource(R.mipmap.xin_bai);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        //歌词
        String songName = mp3Info.getTitle();
        String lrcPath = Environment.getExternalStorageDirectory() + Constant.DIR_LRC + "/" + songName + ".lrc";
        File lrcFile = new File(lrcPath);
        if (!lrcFile.exists()) {
            //下载
            SearchMusicUtils.getInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
                @Override
                public void onSearchResult(ArrayList<SearchResult> results) {
                    SearchResult searchResult = results.get(0);
//                    System.out.println(searchResult);
                    String url = Constant.BAIDU_URL+searchResult.getUrl();
                    DownloadUtils.getInstance().downloadLRC(url,searchResult.getMusicName(),myHandler);
                }
            }).search(songName+" "+mp3Info.getArtist(),1);

        }else{
            loadLRC(lrcFile);
        }

    }


    private long getId(Mp3Info mp3Info) {
        //初始收藏状态
        long id = 0;
        switch (playService.getChangePlayList()) {
            case PlayService.MY_MUSIC_LIST:
                id = mp3Info.getId();
                break;
            case PlayService.LIKE_MUSIC_LIST:
                id = mp3Info.getMp3InfoId();
                break;
            default:
                break;
        }
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImageView2_play_pause: {
                if (playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.play);
                    playService.pause();
                } else {
                    if (playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.pause);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.ImageView1_next: {
                playService.next();
                break;
            }
            case R.id.ImageView3_previous: {
                playService.prev();
                break;
            }
            case R.id.ImageView1_play_mode: {
                int mode = (int) imageView1_play_mode.getTag();
                switch (mode) {
                    case PlayService.ORDER_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.random);
                        imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.single);
                        imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.SINGLE_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.order);
                        imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            case R.id.ImageView1_favorite: {
                Mp3Info mp3Info = playService.mp3Infos.get(playService.getCurrentPosition());
                System.out.println(mp3Info);
                try {
                    Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));
                    System.out.println(likeMp3Info);
                    if (likeMp3Info == null) {
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        mp3Info.setIsLike(1);
                        System.out.println(mp3Info);
                        app.dbUtils.save(mp3Info);
                        System.out.println("save");
                        ImageView1_favorite.setImageResource(R.mipmap.xin_hong);
                    } else {
                        int isLike = likeMp3Info.getIsLike();
                        if (isLike == 1) {
                            likeMp3Info.setIsLike(0);
                            ImageView1_favorite.setImageResource(R.mipmap.xin_bai);
                        } else {
                            likeMp3Info.setIsLike(1);
                            ImageView1_favorite.setImageResource(R.mipmap.xin_hong);
                        }
                        System.out.println("update");
                        app.dbUtils.update(likeMp3Info, "isLike");

                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
        }
    }

//适配器
class MyPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return views.size();
    }

    //实例化选项卡
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = views.get(position);
        container.addView(v);
        return v;
    }

    //删除选项卡
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    //判断视图是否为返回的对象
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}


}
