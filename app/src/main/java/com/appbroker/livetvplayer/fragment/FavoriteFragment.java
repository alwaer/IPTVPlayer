package com.appbroker.livetvplayer.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.livetvplayer.MainActivity;
import com.appbroker.livetvplayer.R;
import com.appbroker.livetvplayer.adapter.ChannelListRecyclerViewAdapter;
import com.appbroker.livetvplayer.listener.ParserListener;
import com.appbroker.livetvplayer.model.Channel;
import com.appbroker.livetvplayer.util.Constants;
import com.appbroker.livetvplayer.util.DialogUtils;
import com.appbroker.livetvplayer.util.Enums;
import com.appbroker.livetvplayer.util.M3UParser;

import java.io.File;
import java.util.List;
import java.util.Random;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class FavoriteFragment extends Fragment {
    ChannelListRecyclerViewAdapter channelListRecyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView=view.findViewById(R.id.fragment_channel_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        channelListRecyclerViewAdapter=new ChannelListRecyclerViewAdapter(this, Constants.CATEGORY_ID_FAV);
        recyclerView.setAdapter(channelListRecyclerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel_list,container,false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_favorite_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.fragment_favorite_export_action){
            PermissionGen.with(FavoriteFragment.this)
                    .addRequestCode(101)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE).request();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @PermissionSuccess(requestCode = 101)
    public void onSuccessRead(){
        PermissionGen.with(FavoriteFragment.this)
                .addRequestCode(102)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).request();
    }
    @PermissionSuccess(requestCode = 102)
    public void onSuccessWrite(){
        proceedToParse();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }


    private void proceedToParse(){
        if (Build.VERSION.SDK_INT>=23){
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ((MainActivity)getActivity()).snackbar(getString(R.string.storage_permission_required),null,null);
                return;
            }

        }
        M3UParser m3UParser=new M3UParser(getContext());
        File dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+File.separator+"MicroPlaylists");
        if (!dir.exists()){
            dir.mkdir();
        }
        m3UParser.generateM3UPlaylist("favorites_" + new Random().nextInt(100), channelListRecyclerViewAdapter.getChannels(), dir, new ParserListener() {
            @Override
            public void onFinish(Enums.ParseResult parseResult, List<Channel> channelList, String message) {
                //empty
            }

            @Override
            public void onCreateFile(File f) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.showShareDialog(getActivity(),f);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                //todo:show
            }
        });
    }

}
