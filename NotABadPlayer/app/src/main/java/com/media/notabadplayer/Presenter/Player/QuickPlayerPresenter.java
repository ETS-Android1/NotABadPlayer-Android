package com.media.notabadplayer.Presenter.Player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.media.notabadplayer.Audio.AudioAlbum;
import com.media.notabadplayer.Audio.AudioInfo;
import com.media.notabadplayer.Audio.AudioPlayer;
import com.media.notabadplayer.Audio.AudioPlaylist;
import com.media.notabadplayer.Constants.AppSettings;
import com.media.notabadplayer.Controls.ApplicationAction;
import com.media.notabadplayer.Controls.ApplicationInput;
import com.media.notabadplayer.Controls.KeyBinds;
import com.media.notabadplayer.Presenter.BasePresenter;
import com.media.notabadplayer.View.BaseView;

public class QuickPlayerPresenter implements BasePresenter
{
    private @NonNull BaseView _view;
    private @NonNull BaseView _applicationRootView;

    private @NonNull AudioInfo _audioInfo;

    public QuickPlayerPresenter(@NonNull BaseView view, @NonNull BaseView applicationRootView,
                                @NonNull AudioInfo audioInfo)
    {
        this._view = view;
        this._applicationRootView = applicationRootView;
        this._audioInfo = audioInfo;
    }
    
    @Override
    public void start() 
    {
        
    }

    @Override
    public void onAlbumItemClick(int index)
    {

    }

    @Override
    public void onPlaylistItemClick(int index)
    {

    }

    @Override
    public void onOpenPlayer(@Nullable AudioPlaylist playlist)
    {
        AudioPlaylist currentlyPlayingPlaylist = AudioPlayer.getShared().getPlaylist();

        if (currentlyPlayingPlaylist == null)
        {
            return;
        }

        _view.openPlaylistScreen(_audioInfo, currentlyPlayingPlaylist);
    }

    @Override
    public void onPlayerButtonClick(ApplicationInput input)
    {
        KeyBinds.getShared().evaluateInput(input);
    }
    
    @Override
    public void onOpenPlaylistButtonClick()
    {
        AudioPlaylist playlist = AudioPlayer.getShared().getPlaylist();
        
        if (playlist != null)
        {
            _applicationRootView.openPlaylistScreen(_audioInfo, playlist);
        }
    }

    @Override
    public void onPlayOrderButtonClick()
    {
        KeyBinds.getShared().performAction(ApplicationAction.CHANGE_PLAY_ORDER);
    }

    @Override
    public void onSearchResultClick(int index)
    {

    }

    @Override
    public void onSearchQuery(@NonNull String searchValue)
    {

    }

    @Override
    public void onAppSettingsReset() 
    {

    }

    @Override
    public void onAppThemeChange(AppSettings.AppTheme themeValue) 
    {

    }

    @Override
    public void onAppSortingChange(AppSettings.AlbumSorting albumSorting, AppSettings.TrackSorting trackSorting) 
    {

    }

    @Override
    public void onAppAppearanceChange(AppSettings.ShowStars showStars, AppSettings.ShowVolumeBar showVolumeBar) {

    }

    @Override
    public void onKeybindChange(ApplicationAction action, ApplicationInput input) {

    }
}
