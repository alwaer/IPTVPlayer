package com.appbroker.livetvplayer.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.appbroker.livetvplayer.listener.DataBaseJobListener;
import com.appbroker.livetvplayer.model.Channel;
import com.appbroker.livetvplayer.util.Constants;

import java.util.Date;
import java.util.List;

public class ChannelService {
    private ChannelDAO channelDAO;

    public ChannelService(Application application) {
        LiveTVDatabase liveTVDatabase=LiveTVDatabase.getInstance(application);
        channelDAO=liveTVDatabase.channelDAO();
    }

    public LiveData<List<Channel>> getAllOf(int categoryId) {
        return channelDAO.getAllOf(categoryId);
    }

    public LiveData<List<Channel>> getFavoriteChannels() {
        return channelDAO.getFavorites();
    }

    public LiveData<List<Channel>> getAllChannels() {
        return channelDAO.getAll();
    }

    public void addChannel(Channel channel){
        new Thread(){
            @Override
            public void run() {
                channelDAO.addChannel(channel);
            }
        }.start();
    }
    public void deleteChannel(int id){
        new Thread(){
            @Override
            public void run() {
                Channel channel=new Channel();
                channel.setId(id);
                channelDAO.deleteChannel(channel);
            }
        }.start();
    }
    public void updateChannel(Channel channel){
        new Thread(){
            @Override
            public void run() {
                channelDAO.updateChannel(channel);
            }
        }.start();
    }

    public void updateChannel(Channel channel, DataBaseJobListener dataBaseJobListener){
        new Thread(){
            @Override
            public void run() {
                channelDAO.updateChannel(channel);
                dataBaseJobListener.onFinish(channel);
            }
        }.start();
    }

    public void addTempChannels(int categoryId) {
        new Thread(){
            @Override
            public void run() {
                channelDAO.addTempChannels(categoryId);
                channelDAO.deleteChannelsInCategory(Constants.CATEGORY_ID_TEMP);
            }
        }.start();
    }

    public void addMultipleChannels(List<Channel> channels, DataBaseJobListener dataBaseJobListener) {
        new Thread(){
            @Override
            public void run() {
                channelDAO.addChannelBatch(channels);
                dataBaseJobListener.onFinish(null);
            }
        }.start();
    }

    public void updateTempChannelsChecked(boolean isChecked) {
        new Thread(){
            @Override
            public void run() {
                channelDAO.updateTempChannelsChecked(isChecked);
            }
        }.start();
    }
    public void dismissTempChannels(){
        new Thread(){
            @Override
            public void run() {
                channelDAO.deleteChannelsInCategory(Constants.CATEGORY_ID_TEMP);
            }
        }.start();
    }

    public LiveData<Channel> getChannelById(int id, boolean update) {
        new Thread(){
            @Override
            public void run() {
                if (update){
                    channelDAO.updateChannelDate(id,new Date().getTime());
                }
            }
        }.start();
        return channelDAO.getChannelById(id);
    }
}
