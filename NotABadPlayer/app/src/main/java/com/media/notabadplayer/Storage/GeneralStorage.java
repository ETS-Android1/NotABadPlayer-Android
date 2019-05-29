package com.media.notabadplayer.Storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.media.notabadplayer.Audio.AudioPlayOrder;
import com.media.notabadplayer.Audio.AudioPlayer;
import com.media.notabadplayer.Audio.AudioPlaylist;
import com.media.notabadplayer.Audio.AudioTrack;
import com.media.notabadplayer.Constants.AppSettings;
import com.media.notabadplayer.Controls.ApplicationAction;
import com.media.notabadplayer.Controls.ApplicationInput;
import com.media.notabadplayer.R;
import com.media.notabadplayer.Utilities.Serializing;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralStorage
{
    private static GeneralStorage singleton;
    
    private Application ___context;
    
    private SharedPreferences ___preferences;
    
    private boolean _firstTimeLaunch;
    
    private HashMap<ApplicationInput, ApplicationAction> _keyBinds = new HashMap<>();
    
    private GeneralStorage()
    {
        _firstTimeLaunch = true;
    }

    synchronized public static GeneralStorage getShared()
    {
        if (singleton == null)
        {
            singleton = new GeneralStorage();
        }
        
        return singleton;
    }
    
    public void init(@NonNull Application context)
    {
        ___context = context;
        
        ___preferences = context.getSharedPreferences(GeneralStorage.class.getCanonicalName(), Context.MODE_PRIVATE);
        
        detectFirstTimeLaunch();

        detectVersionChange();
    }
    
    private @NonNull Application getContext()
    {
        if (___context == null)
        {
            throw new UncheckedExecutionException(new Exception("GeneralStorage cannot be used before being initialized, initialize() has never been called"));
        }
        
        return ___context;
    }
    
    private @NonNull SharedPreferences getSharedPreferences()
    {
        if (___context == null)
        {
            throw new UncheckedExecutionException(new Exception("GeneralStorage cannot be used before being initialized, initialize() has never been called"));
        }
        
        return ___preferences;
    }
    
    private void detectFirstTimeLaunch()
    {
        this._firstTimeLaunch = getSharedPreferences().getBoolean("firstTime", true);
        
        if (this._firstTimeLaunch)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "First time launching the program! Setting app settings to their default values");
            
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            
            resetDefaultSettingsValues();
        }
    }
    
    private void detectVersionChange()
    {
        Application context = getContext();
        
        String preferencesVersion = getStorageVersion();
        String currentVersion = context.getResources().getString(R.string.storage_version);
        
        if (preferencesVersion.equals(currentVersion))
        {
            return;
        }

        SharedPreferences preferences = getSharedPreferences();
        
        saveStorageVersion(currentVersion);
        
        // Migrate from version to version, one by one
        // Migrate to 1.0
        if (preferencesVersion.equals(""))
        {
            String version = "1.0";
            
            Log.v(GeneralStorage.class.getCanonicalName(), "Migrating settings from first version to version " + version);
            
            Object result = Serializing.deserializeObject(preferences.getString("user_playlists", ""));
            
            if (result != null)
            {
                ArrayList<AudioPlaylist> playlistsArray = objectToPlaylistsArray(result);

                if (playlistsArray != null)
                {
                    saveUserPlaylists(playlistsArray);
                }
            }

            preferencesVersion = version;
        }

        // Migrate to 1.1
        if (preferencesVersion.equals("1.0"))
        {
            String version = "1.1";
            
            Log.v(GeneralStorage.class.getCanonicalName(), "Migrating settings from version " + preferencesVersion + " to version " + version);
            
            saveSettingsAction(ApplicationInput.PLAYER_SWIPE_LEFT, ApplicationAction.PREVIOUS);
            saveSettingsAction(ApplicationInput.PLAYER_SWIPE_RIGHT, ApplicationAction.NEXT);

            preferencesVersion = version;
        }

        // Migrate to 1.2
        if (preferencesVersion.equals("1.1"))
        {
            String version = "1.2";
            
            Log.v(GeneralStorage.class.getCanonicalName(), "Migrating settings from version " + preferencesVersion + " to version " + version);

            SharedPreferences.Editor editor = getSharedPreferences().edit();

            editor.putString("player_play_order", AudioPlayOrder.FORWARDS.name());

            editor.apply();

            preferencesVersion = version;
        }

        // Migrate to 1.3
        if (preferencesVersion.equals("1.2"))
        {
            String version = "1.3";
            
            Log.v(GeneralStorage.class.getCanonicalName(), "Migrating settings from version " + preferencesVersion + " to version " + version);

            SharedPreferences.Editor editor = getSharedPreferences().edit();

            editor.putString("open_player_on_play", AppSettings.OpenPlayerOnPlay.NO.name());

            editor.apply();
            
            preferencesVersion = version;
        }
        
        Log.v(GeneralStorage.class.getCanonicalName(), "Successfully migrated settings values!");
    }

    synchronized public void resetDefaultSettingsValues()
    {
        savePlayerPlayedHistoryCapacity(50);
        saveAppThemeValue(AppSettings.AppTheme.LIGHT);
        saveAlbumSortingValue(AppSettings.AlbumSorting.TITLE);
        saveTrackSortingValue(AppSettings.TrackSorting.TRACK_NUMBER);
        saveShowStarsValue(AppSettings.ShowStars.NO);
        saveShowVolumeBarValue(AppSettings.ShowVolumeBar.NO);
        saveOpenPlayerOnPlayValue(AppSettings.OpenPlayerOnPlay.NO);

        saveSettingsAction(ApplicationInput.PLAYER_VOLUME_UP_BUTTON, ApplicationAction.VOLUME_UP);
        saveSettingsAction(ApplicationInput.PLAYER_VOLUME_DOWN_BUTTON, ApplicationAction.VOLUME_DOWN);
        saveSettingsAction(ApplicationInput.PLAYER_PLAY_BUTTON, ApplicationAction.PAUSE_OR_RESUME);
        saveSettingsAction(ApplicationInput.PLAYER_RECALL, ApplicationAction.RECALL);
        saveSettingsAction(ApplicationInput.PLAYER_NEXT_BUTTON, ApplicationAction.NEXT);
        saveSettingsAction(ApplicationInput.PLAYER_PREVIOUS_BUTTON, ApplicationAction.PREVIOUS);
        saveSettingsAction(ApplicationInput.PLAYER_SWIPE_LEFT, ApplicationAction.PREVIOUS);
        saveSettingsAction(ApplicationInput.PLAYER_SWIPE_RIGHT, ApplicationAction.NEXT);
        saveSettingsAction(ApplicationInput.QUICK_PLAYER_VOLUME_UP_BUTTON, ApplicationAction.VOLUME_UP);
        saveSettingsAction(ApplicationInput.QUICK_PLAYER_VOLUME_DOWN_BUTTON, ApplicationAction.VOLUME_DOWN);
        saveSettingsAction(ApplicationInput.QUICK_PLAYER_PLAY_BUTTON, ApplicationAction.PAUSE_OR_RESUME);
        saveSettingsAction(ApplicationInput.QUICK_PLAYER_NEXT_BUTTON, ApplicationAction.FORWARDS_8);
        saveSettingsAction(ApplicationInput.QUICK_PLAYER_PREVIOUS_BUTTON, ApplicationAction.BACKWARDS_8);
        saveSettingsAction(ApplicationInput.EARPHONES_UNPLUG, ApplicationAction.PAUSE);
        saveSettingsAction(ApplicationInput.EXTERNAL_PLAY, ApplicationAction.PAUSE);

        saveCachingPolicy(AppSettings.TabCachingPolicies.ALBUMS_ONLY);
    }
    
    synchronized public boolean isFirstApplicationLaunch()
    {
        return _firstTimeLaunch;
    }

    private void saveStorageVersion(@NonNull String version)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("storage_version", version);

        editor.apply();
    }

    private @NonNull String getStorageVersion()
    {
        String storageVersion = getSharedPreferences().getString("storage_version", "");
        
        if (storageVersion == null)
        {
            return "";
        }
        
        return storageVersion;
    }
    
    synchronized public void savePlayerState()
    {
        AudioPlayer player = AudioPlayer.getShared();
        
        if (!player.hasPlaylist())
        {
            return;
        }
        
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        
        editor.putString("player_current_playlist", Serializing.serializeObject(player.getPlaylist()));
        editor.putString("player_play_order", player.getPlayOrder().name());
        editor.putInt("player_current_position", player.getCurrentPositionMSec());
        
        editor.apply();
    }
    
    synchronized public void restorePlayerState()
    {
        SharedPreferences preferences = getSharedPreferences();
        AudioPlayer player = AudioPlayer.getShared();

        String playOrderData = preferences.getString("player_play_order", "");
        String playlistData = preferences.getString("player_current_playlist", "");
        
        if (playlistData == null || playOrderData == null)
        {
            return;
        }

        // Restore play order state
        Log.v(GeneralStorage.class.getCanonicalName(), "Restoring audio player state...");
        
        AudioPlayOrder playOrder;
        
        try {
            playOrder = AudioPlayOrder.valueOf(playOrderData);
        } catch (Exception e) {
            Log.v(GeneralStorage.class.getCanonicalName(), "Failed to restore audio player state, " + e.toString());
            return;
        }
        
        player.setPlayOrder(playOrder);
        
        // Restore playlist
        Object result = Serializing.deserializeObject(playlistData);

        if (!(result instanceof AudioPlaylist))
        {
            return;
        }
        
        AudioPlaylist playlist = (AudioPlaylist)result;
        
        try {
            player.playPlaylist(playlist);
        } catch (Exception e) {
            Log.v(GeneralStorage.class.getCanonicalName(), "Failed to restore audio player state, " + e.toString());
            return;
        }
        
        int positionMSec = preferences.getInt("player_current_position", 0);
        
        player.seekTo(positionMSec);
        
        // Always pause by default when restoring state from storage
        player.pause();
        
        // Success
        Log.v(GeneralStorage.class.getCanonicalName(), "Successfully restored audio player state!");
    }

    synchronized public void savePlayerPlayHistoryState()
    {
        SharedPreferences preferences = getSharedPreferences();
        AudioPlayer player = AudioPlayer.getShared();

        if (!player.hasPlaylist())
        {
            return;
        }
        
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("player_play_history", Serializing.serializeObject(player.playHistory.getPlayHistory()));
        
        editor.apply();
        
        Log.v(GeneralStorage.class.getCanonicalName(), "Saved audio player state to storage.");
    }

    synchronized public void restorePlayerPlayHistoryState(@NonNull Application application)
    {
        SharedPreferences preferences = getSharedPreferences();
        AudioPlayer player = AudioPlayer.getShared();
        
        String data = preferences.getString("player_play_history", "");

        if (data == null)
        {
            return;
        }
        
        ArrayList<AudioTrack> playHistory = objectToTracksArray(Serializing.deserializeObject(data));
        
        if (playHistory == null)
        {
            return;
        }
        
        player.playHistory.setList(playHistory);
    }

    synchronized public void saveSearchQuery(String searchQuery)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putString("searchQuery", searchQuery);
        
        editor.apply();
    }

    synchronized public String retrieveSearchQuery()
    {
        return getSharedPreferences().getString("searchQuery", "");
    }

    synchronized public void saveSettingsAction(ApplicationInput input, ApplicationAction action)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putString(input.name(), action.name());
        
        editor.apply();
        
        _keyBinds.put(input, action);
    }
    
    synchronized public ApplicationAction getSettingsAction(ApplicationInput input)
    {
        SharedPreferences preferences = getSharedPreferences();
        ApplicationAction cachedAction = _keyBinds.get(input);
        
        if (cachedAction != null)
        {
            return cachedAction;
        }
        
        String actionStr = preferences.getString(input.name(), "");
        
        try {
            ApplicationAction action = ApplicationAction.valueOf(actionStr);
            _keyBinds.put(input, action);
            return action;
        }
        catch (Exception e) {
            
        }
        
        return ApplicationAction.DO_NOTHING;
    }
    
    synchronized public int getPlayerPlayedHistoryCapacity()
    {
        return getSharedPreferences().getInt("player_history_capacity", 1);
    }
    
    synchronized public void savePlayerPlayedHistoryCapacity(int value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("player_history_capacity", value >= 0 ? value : 0);
        editor.apply();
    }

    synchronized public ArrayList<AudioPlaylist> getUserPlaylists()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            Object result = Serializing.deserializeObject(preferences.getString("user_playlists", ""));
            
            ArrayList<AudioPlaylist> playlistsArray = objectToPlaylistsArray(result);

            if (playlistsArray == null)
            {
                Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not deserialize user playlists");
                return null;
            }
            
            return playlistsArray;
        }
        catch (Exception e)
        {
            
        }
        
        return null;
    }

    synchronized public void saveUserPlaylists(@NonNull ArrayList<AudioPlaylist> playlists)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_playlists", Serializing.serializeObject(playlists));
        editor.apply();
    }

    synchronized public AppSettings.AppTheme getAppThemeValue()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            return AppSettings.AppTheme.valueOf(preferences.getString("app_theme_value", ""));
        } 
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read AppThemeUtility value from storage");
        }
        
        return AppSettings.AppTheme.LIGHT;
    }

    synchronized public void saveAppThemeValue(AppSettings.AppTheme value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("app_theme_value", value.name());
        editor.apply();
    }

    synchronized public AppSettings.AlbumSorting getAlbumSortingValue()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            return AppSettings.AlbumSorting.valueOf(preferences.getString("album_sorting", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read AlbumSorting value from storage");
        }

        return AppSettings.AlbumSorting.TITLE;
    }

    synchronized public void saveAlbumSortingValue(AppSettings.AlbumSorting value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("album_sorting", value.name());
        editor.apply();
    }

    synchronized public AppSettings.TrackSorting getTrackSortingValue()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            return AppSettings.TrackSorting.valueOf(preferences.getString("track_sorting", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read TrackSorting value from storage");
        }

        return AppSettings.TrackSorting.TITLE;
    }

    synchronized public void saveTrackSortingValue(AppSettings.TrackSorting value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("track_sorting", value.name());
        editor.apply();
    }

    synchronized public AppSettings.ShowStars getShowStarsValue()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            return AppSettings.ShowStars.valueOf(preferences.getString("show_stars", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read ShowStars value from storage");
        }

        return AppSettings.ShowStars.NO;
    }

    synchronized public void saveShowStarsValue(AppSettings.ShowStars value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("show_stars", value.name());
        editor.apply();
    }

    synchronized public AppSettings.ShowVolumeBar getShowVolumeBarValue()
    {
        SharedPreferences preferences = getSharedPreferences();
        
        try {
            return AppSettings.ShowVolumeBar.valueOf(preferences.getString("show_volume_bar", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read ShowVolumeBar value from storage");
        }

        return AppSettings.ShowVolumeBar.NO;
    }

    synchronized public void saveShowVolumeBarValue(AppSettings.ShowVolumeBar value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("show_volume_bar", value.name());
        editor.apply();
    }

    synchronized public AppSettings.OpenPlayerOnPlay getOpenPlayerOnPlayValue()
    {
        SharedPreferences preferences = getSharedPreferences();

        try {
            return AppSettings.OpenPlayerOnPlay.valueOf(preferences.getString("open_player_on_play", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read OpenPlayerOnPlay value from storage");
        }

        return AppSettings.OpenPlayerOnPlay.NO;
    }

    synchronized public void saveOpenPlayerOnPlayValue(AppSettings.OpenPlayerOnPlay value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("open_player_on_play", value.name());
        editor.apply();
    }
    
    synchronized public AppSettings.TabCachingPolicies getCachingPolicy()
    {
        SharedPreferences preferences = getSharedPreferences();

        try {
            return AppSettings.TabCachingPolicies.valueOf(preferences.getString("caching_policy", ""));
        }
        catch (Exception e)
        {
            Log.v(GeneralStorage.class.getCanonicalName(), "Error: could not read TabCachingPolicies value from storage");
        }

        return AppSettings.TabCachingPolicies.NO_CACHING;
    }
    
    synchronized public void saveCachingPolicy(AppSettings.TabCachingPolicies value)
    {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("caching_policy", value.name());
        editor.apply();
    }
    
    public static ArrayList<AudioPlaylist> objectToPlaylistsArray(Object object)
    {
        if (object instanceof ArrayList)
        {
            ArrayList array = (ArrayList)object;
            
            if (array.size() > 0)
            {
                if (array.get(0) instanceof AudioPlaylist)
                {
                    @SuppressWarnings("unchecked")
                    ArrayList<AudioPlaylist> playlistsArray = (ArrayList<AudioPlaylist>)object;
                    return playlistsArray;
                }
            }
        }
        
        return null;
    }

    public static ArrayList<AudioTrack> objectToTracksArray(Object object)
    {
        if (object instanceof ArrayList)
        {
            ArrayList array = (ArrayList)object;

            if (array.size() > 0)
            {
                if (array.get(0) instanceof AudioTrack)
                {
                    @SuppressWarnings("unchecked")
                    ArrayList<AudioTrack> tracksArray = (ArrayList<AudioTrack>)object;
                    return tracksArray;
                }
            }
        }

        return null;
    }
}
