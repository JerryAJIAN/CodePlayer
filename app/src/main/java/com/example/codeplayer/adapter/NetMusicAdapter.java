package com.example.codeplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.codeplayer.R;
import com.example.codeplayer.vo.SearchResult;

import java.util.ArrayList;

/**
 * descreption: Net Music Data Result Adapter
 * company: codingke.com
 *
 * @author vince
 * @date 15/10/12
 */
public class NetMusicAdapter extends BaseAdapter{
    private Context ctx;
    private ArrayList<SearchResult> searchResults;
    public NetMusicAdapter(Context ctx,ArrayList<SearchResult> searchResults) {
        this.ctx = ctx;
        this.searchResults = searchResults;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.net_item_music_list,null);
            vh = new ViewHolder();
            vh.net_tv_title = (TextView) convertView.findViewById(R.id.net_tv_title);
            vh.net_tv_singer = (TextView) convertView.findViewById(R.id.net_tv_singer);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        SearchResult result = searchResults.get(position);
        vh.net_tv_title.setText(result.getMusicName());
        vh.net_tv_singer.setText(result.getArtist());
        return convertView;
    }

    static class ViewHolder{
        TextView net_tv_title;
        TextView net_tv_singer;
    }

}
