package com.example.codeplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.example.codeplayer.utils.DownloadUtils;
import com.example.codeplayer.vo.SearchResult;

/**
 * descreption:
 * company: codingke.com
 *
 * @author vince
 * @date 15/10/13
 */
public class DownloadDialogFragment extends DialogFragment{

    private SearchResult searchResult;  //当前要下载的歌曲对象
    private MainActivity mainActivity;
    public static DownloadDialogFragment newInstance(SearchResult searchResult){
        DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
        downloadDialogFragment.searchResult = searchResult;
        return downloadDialogFragment;
    }

    private String[] items;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
        items = new String[]{getString(R.string.download),getString(R.string.cancel)};
    }

    //创建对话框的事件方法
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //执行下载
                        downloadMusic();
                        break;
                    case 1://取消
                        dialog.dismiss();
                        break;
                }
            }
        });
        return builder.show();
    }


    //下载音乐
    private void downloadMusic() {
        Toast.makeText(mainActivity, "正在下载：" + searchResult.getMusicName(), Toast.LENGTH_SHORT).show();
        DownloadUtils.getInstance().setListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownload(String mp3Url) {  //下载成功
                Toast.makeText(mainActivity, "歌曲下载成功", Toast.LENGTH_SHORT).show();
                //扫描新下载的歌曲
//                Uri contentUri = Uri.fromFile(new File(mp3Url));
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
//                getContext().sendBroadcast(mediaScanIntent);
            }
            @Override
            public void onFailed(String error) {  //下载失败
                Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
            }
        }).download(searchResult);
    }
}
