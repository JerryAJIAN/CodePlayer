package com.example.codeplayer;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.codeplayer.adapter.NetMusicAdapter;
import com.example.codeplayer.vo.SearchResult;

import java.util.ArrayList;

;

/**
 * @author ASUS_JAJIAN
 */
public class NetMusicListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private MainActivity mainActivity;
    private ListView listView_net_music;
    private LinearLayout load_layout;
    private LinearLayout ll_search_btn_container;
    private LinearLayout ll_search_container;
    private ImageButton ib_search_btn;
    private EditText et_search_content;
    private ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
    private NetMusicAdapter netMusicAdapter;
    private int page = 1;//搜索音乐的页码

    public static NetMusicListFragment newInstance() {
        NetMusicListFragment net = new NetMusicListFragment();
        return net;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
