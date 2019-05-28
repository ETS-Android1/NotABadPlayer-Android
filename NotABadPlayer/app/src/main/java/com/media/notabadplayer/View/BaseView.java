package com.media.notabadplayer.View;

import android.support.annotation.NonNull;

import com.media.notabadplayer.Audio.AudioAlbum;
import com.media.notabadplayer.Audio.AudioInfo;
import com.media.notabadplayer.Presenter.BasePresenter;
import com.media.notabadplayer.Audio.AudioPlaylist;
import com.media.notabadplayer.Audio.AudioTrack;
import com.media.notabadplayer.Constants.AppSettings;

import java.util.ArrayList;

public interface BaseView {
    void setPresenter(@NonNull BasePresenter presenter);

    void enableInteraction();
    void disableInteraction();

    void openPlaylistScreen(@NonNull AudioInfo audioInfo, @NonNull AudioPlaylist playlist);
    
    void onMediaAlbumsLoad(@NonNull ArrayList<AudioAlbum> albums);

    void onPlaylistLoad(@NonNull AudioPlaylist playlist);
    
    void openPlayerScreen(@NonNull AudioPlaylist playlist);
    void updatePlayerScreen(@NonNull AudioPlaylist playlist);
    
    void searchQueryResults(@NonNull String searchQuery, @NonNull ArrayList<AudioTrack> songs);
    
    void appSettingsReset();
    void appThemeChanged(AppSettings.AppTheme appTheme);
    void appSortingChanged(AppSettings.AlbumSorting albumSorting, AppSettings.TrackSorting trackSorting);
    void appAppearanceChanged(AppSettings.ShowStars showStars, AppSettings.ShowVolumeBar showVolumeBar);
    
    void onPlayerErrorEncountered(@NonNull Exception error);
}
