package com.example.codeplayer.utils;

/**
 * descreption:
 * company:
 *
 * @author vince
 * @date 15/10/9
 */
public class Constant {

    public static final String SP_NAME = "CodingMusic";  //私有属性文件名
    public static final String DB_NAME = "CodingkePlayer.db"; //数据库名
    public static final int PLAY_RECORD_NUM = 5;//最近播放显示的最大条数

    //百度音乐地址
    public static final String BAIDU_URL = "http://music.baidu.com";
    //热歌榜
    public static final String BAIDU_DAYHOT = "/top/dayhot/?pst=shouyeTop";
    //搜索
    public static final String BAIDU_SEARCH = "/search/song";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.22 Safari/537.36";

    //成功标记
    public static final int SUCCESS = 1;
    //失败标记
    public static final int FAILED = 2;


    public static final String DIR_MUSIC = "/codeplayer_music/music";
    public static final String DIR_LRC = "/codeplayer_music/lrc";
}
