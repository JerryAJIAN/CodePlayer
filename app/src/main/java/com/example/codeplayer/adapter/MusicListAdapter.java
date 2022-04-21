package com.example.codeplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andraskindler.quickscroll.Scrollable;
import com.example.codeplayer.R;
import com.example.codeplayer.utils.MediaUtils;
import com.example.codeplayer.vo.Mp3Info;

import java.util.ArrayList;

/**
 * descreption:
 * company:
 *
 * @author vince
 * @date 15/9/24
 */
public class MusicListAdapter extends BaseAdapter implements Scrollable {

    private Context ctx;
    private ArrayList<Mp3Info> mp3Infos;

    public MusicListAdapter(Context ctx, ArrayList<Mp3Info> mp3Infos) {
        this.ctx = ctx;
        this.mp3Infos = mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_music_list,null);
            vh = new ViewHolder();
            vh.tv_title = (TextView) convertView.findViewById(R.id.textView1_title);
            vh.tv_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
            vh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        Mp3Info mp3Info = mp3Infos.get(position);
        vh.tv_title.setText(mp3Info.getTitle());
        vh.tv_singer.setText(mp3Info.getArtist());
        vh.tv_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        return convertView;
    }

    @Override
    public String getIndicatorForPosition(int childposition, int groupposition) {
        return Character.toString((mp3Infos.get(childposition)).getTitle().charAt(0));
    }

    @Override
    public int getScrollPosition(int childposition, int groupposition) {
        return childposition;
    }

    static class ViewHolder{
        TextView tv_title;
        TextView tv_singer;
        TextView tv_time;

    }
}
