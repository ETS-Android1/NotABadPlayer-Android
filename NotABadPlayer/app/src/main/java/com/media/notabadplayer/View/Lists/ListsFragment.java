package com.media.notabadplayer.View.Lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import com.google.common.base.Function;

import com.media.notabadplayer.Audio.AudioAlbum;
import com.media.notabadplayer.Audio.AudioPlayer;
import com.media.notabadplayer.Audio.AudioPlaylist;
import com.media.notabadplayer.Audio.AudioTrack;
import com.media.notabadplayer.Constants.AppSettings;
import com.media.notabadplayer.Presenter.Albums.AlbumPresenter;
import com.media.notabadplayer.Presenter.BasePresenter;
import com.media.notabadplayer.R;
import com.media.notabadplayer.Storage.GeneralStorage;
import com.media.notabadplayer.Utilities.UIAnimations;
import com.media.notabadplayer.View.Albums.AlbumFragment;
import com.media.notabadplayer.View.Albums.AlbumsFragment;
import com.media.notabadplayer.View.BaseView;

public class ListsFragment extends Fragment implements BaseView {
    private ArrayList<AudioPlaylist> _playlists;
    
    private Button _createPlaylistButton;
    private Button _deletePlaylistButton;
    private Button _donePlaylistButton;
    private ListView _playlistsList;
    private PlaylistListAdapter _playlistsAdapter;
    
    public ListsFragment()
    {

    }

    public static ListsFragment newInstance()
    {
        return new ListsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlists, container, false);
        
        _createPlaylistButton = root.findViewById(R.id.createPlaylistButton);
        _deletePlaylistButton = root.findViewById(R.id.deletePlaylistButton);
        _donePlaylistButton = root.findViewById(R.id.donePlaylistButton);
        _playlistsList = root.findViewById(R.id.playlistsList);
        
        // Setup UI
        initUI();
        
        return root;
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        updateListAdapters();
    }
    
    private void initUI()
    {
        _createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreatePlaylistScreen();
            }
        });
        
        _deletePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _playlistsAdapter.enterEditMode();
                _deletePlaylistButton.setVisibility(View.GONE);
                _donePlaylistButton.setVisibility(View.VISIBLE);
            }
        });

        _donePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _playlistsAdapter.leaveEditMode();
                _donePlaylistButton.setVisibility(View.GONE);
                _deletePlaylistButton.setVisibility(View.VISIBLE);
            }
        });
        
        _playlistsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!_playlistsAdapter.isInEditMode())
                {
                    UIAnimations.animateAlbumItemTAP(getContext(), view);
                    openPlaylistScreen(_playlists.get(position));
                }
            }
        });

        updateListAdapters();
    }
    
    private void updateListAdapters()
    {
        _playlists = GeneralStorage.getShared().getPlaylists(getContext());

        if (_playlists == null)
        {
            _playlists = new ArrayList<>();
        }
        
        ArrayList<AudioTrack> history = AudioPlayer.getShared().getPlayHistory();
        
        if (history.size() > 0)
        {
            AudioPlaylist historyPlaylist = new AudioPlaylist("Recently played", history);
            _playlists.add(0, historyPlaylist);
        }
        
        Function<Integer, Void> onRemoveButtonClick = new Function<Integer, Void>() {
            @Override
            public Void apply(Integer integer) {
                if (integer != 0)
                {
                    deletePlaylistOnIndex(integer);
                }
                
                return null;
            }
        };
        
        _playlistsAdapter = new PlaylistListAdapter(getContext(), _playlists, onRemoveButtonClick);
        _playlistsList.setAdapter(_playlistsAdapter);
        _playlistsList.invalidateViews();
    }
    
    private void openCreatePlaylistScreen()
    {
        Activity a = getActivity();

        if (a == null)
        {
            return;
        }

        Intent intent = new Intent(a, CreatePlaylistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void deletePlaylistOnIndex(int position)
    {
        if (position < _playlists.size())
        {
            _playlists.remove(position);
            
            if (_playlists.size() > 0)
            {
                ArrayList<AudioPlaylist> playlists = new ArrayList<>(_playlists);

                playlists.remove(0);
                
                GeneralStorage.getShared().savePlaylists(getContext(), playlists);
            }

            _playlistsAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void setPresenter(@NonNull BasePresenter presenter)
    {

    }

    @Override
    public void openAlbumScreen(@NonNull AudioAlbum album) {
        
    }

    @Override
    public void openPlaylistScreen(@NonNull AudioPlaylist playlist) 
    {
        AlbumFragment f = AlbumFragment.newInstance();
        f.setPresenter(new AlbumPresenter(f, playlist));
        FragmentActivity a = getActivity();
        FragmentManager manager = a.getSupportFragmentManager();
        
        FragmentTransaction transaction = manager.beginTransaction().replace(R.id.mainLayout, f);
        transaction.addToBackStack(AlbumsFragment.class.getCanonicalName()).commit();
    }

    @Override
    public void onMediaAlbumsLoad(@NonNull ArrayList<AudioAlbum> albums)
    {

    }

    @Override
    public void onAlbumSongsLoad(@NonNull ArrayList<AudioTrack> songs)
    {

    }

    @Override
    public void onPlaylistLoad(@NonNull AudioPlaylist playlist, boolean sortTracks)
    {

    }
    
    @Override
    public void openPlayerScreen(@NonNull AudioPlaylist playlist)
    {

    }

    @Override
    public void searchQueryResults(@NonNull String searchQuery, @NonNull ArrayList<AudioTrack> songs)
    {

    }

    @Override
    public void appSettingsReset()
    {

    }

    @Override
    public void appThemeChanged(AppSettings.AppTheme appTheme)
    {

    }

    @Override
    public void appSortingChanged(AppSettings.AlbumSorting albumSorting, AppSettings.TrackSorting trackSorting)
    {

    }

    @Override
    public void appAppearanceChanged(AppSettings.ShowStars showStars, AppSettings.ShowVolumeBar showVolumeBar)
    {

    }
}
