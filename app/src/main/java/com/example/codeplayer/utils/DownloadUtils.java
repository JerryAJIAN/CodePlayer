package com.example.codeplayer.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.example.codeplayer.vo.SearchResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

/**
 * descreption:
 * company: codingke.com
 *
 * @author vince
 * @date 15/10/14
 */
public class DownloadUtils {

    private static final String DOWNLOAD_URL = "/download?__o=%2Fsearch%2Fsong";
    public static final int SUCCESS_LRC = 1;  //下载歌词成功
    public static final int FAILED_LRC = 2;  //下载歌词失败
    private static final int SUCCESS_MP3 = 3;  //下载MP3成功
    private static final int FAILED_MP3 = 4;  //下载MP3失败
    private static final int GET_MP3_URL = 5; //获取MP3 URL成功
    private static final int GET_FAILED_MP3_URL = 6;//获取MP3 URL失败
    private static final int MUSIC_EXISTS = 7;  //音乐已存在

    private static DownloadUtils sInstance;
    private OnDownloadListener mListener;

    private ExecutorService mThreadPool;

    /**
     * 设置回调的监听器对象
     * @param mListener
     * @return
     */
    public DownloadUtils setListener(OnDownloadListener mListener) {
        this.mListener = mListener;
        return this;
    }
    //获取下载工具的实例
    public synchronized static DownloadUtils getInstance() {
        if (sInstance == null) {
            try {
                sInstance = new DownloadUtils();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

    private DownloadUtils() throws ParserConfigurationException {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    /**
     * 下载的具体业务方法
     * @param searchResult
     */
    public void download(final SearchResult searchResult){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESS_LRC:
                        if(mListener != null) mListener.onDownload("歌词下载成功");
                        break;
                    case FAILED_LRC:
                        if(mListener != null) mListener.onFailed("歌词下载失败");
                        break;
                    case GET_MP3_URL:
                        System.out.println(msg.obj);
                        downloadMusic(searchResult,(String)msg.obj,this);
                        break;
                    case GET_FAILED_MP3_URL:
                        if(mListener!=null)mListener.onFailed("下载失败，该歌曲为收费VIP类型或不存在");
                        break;
                    case SUCCESS_MP3:
                        if(mListener!=null)mListener.onDownload(searchResult.getMusicName()+"已下载");
                        String url = Constant.BAIDU_URL + searchResult.getUrl();
                        System.out.println("download lrc:"+url);
                        downloadLRC(url,searchResult.getMusicName(),this);
                        break;
                    case FAILED_MP3:
                        if (mListener !=null)mListener.onFailed(searchResult.getMusicName()+"下载失败");
                        break;
                    case MUSIC_EXISTS:
                        if (mListener !=null)mListener.onFailed("音乐已存在");
                        break;
                }
            }
        };

        getDownloadMusicURL(searchResult, handler);
    }

    /**
     * 下载MP3音乐的具体方法
     * @param searchResult
     * @param url
     * @param handler
     */
    private void downloadMusic(final SearchResult searchResult,final String url,final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File musicDirFile = new File(Environment.getExternalStorageDirectory()+Constant.DIR_MUSIC);
                if(!musicDirFile.exists()){
                    musicDirFile.mkdirs();
                }
                //http://music.baidu.com/data/music/file?link=&song_id=239896
                String mp3url = Constant.BAIDU_URL+url;
                String target = musicDirFile + "/" + searchResult.getMusicName() + ".mp3";
//                System.out.println(mp3url);
//                System.out.println(target);
                File fileTarget = new File(target);
                if(fileTarget.exists()){
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                    return;
                }else {
                    //使用OKHttpClient
                    //HttpUtils httpUtils = new HttpUtils();
                    //httpUtils.d
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(mp3url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_MP3).sendToTarget();
                    }
                }
            }
        });

    }

    //获取下载音乐的URL
    private void getDownloadMusicURL(final SearchResult searchResult, final Handler handler){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //
                    String url = Constant.BAIDU_URL + "/song/"+searchResult.getUrl().substring(searchResult.getUrl().lastIndexOf("/")+1) + DOWNLOAD_URL;
//                    System.out.println(url);
                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();

                    Elements targetElements = doc.select("a[data-btndata]");
                    if(targetElements.size() <= 0) {
                        handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                        return;
                    }
                    for(Element e : targetElements) {
                        if(e.attr("href").contains(".mp3")) {
                            String result = e.attr("href");
                            Message msg = handler.obtainMessage(GET_MP3_URL, result);
                            msg.sendToTarget();
                            return;
                        }

                        if(e.attr("href").startsWith("/vip")) {
                            targetElements.remove(e);
                        }
                    }
                    if(targetElements.size() <= 0) {
                        handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                        return;
                    }
                    String result = targetElements.get(0).attr("href");
                    Message msg = handler.obtainMessage(GET_MP3_URL, result);
                    msg.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                }
            }
        });
    }


    //下载歌词的具体方法
    public void downloadLRC(final String url,final String musicName, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();
                    Elements lrcTag = doc.select("div.lyric-content");
                    String lrcURL = lrcTag.attr("data-lrclink");
//                    System.out.println(lrcURL);
                    File lrcDirFile = new File(Environment.getExternalStorageDirectory() + Constant.DIR_LRC);
                    if (!lrcDirFile.exists()) {
                        lrcDirFile.mkdirs();
                    }

                    lrcURL = Constant.BAIDU_URL + lrcURL;
                    String target = lrcDirFile + "/" + musicName + ".lrc";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(lrcURL).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(new File(target));
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_LRC, target).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_LRC).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 自定义下载事件监听器
     */
    public interface OnDownloadListener{
        public void onDownload(String mp3Url);
        public void onFailed(String error);
    }
}
