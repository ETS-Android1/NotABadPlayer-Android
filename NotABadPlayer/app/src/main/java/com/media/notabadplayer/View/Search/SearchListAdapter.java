package com.media.notabadplayer.View.Search;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.media.notabadplayer.Audio.AudioTrack;
import com.media.notabadplayer.R;

import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter
{
    private Context _context;
    private ArrayList<AudioTrack> _tracks;
    
    public SearchListAdapter(@NonNull Context context, ArrayList<AudioTrack> tracks)
    {
        this._context = context;
        this._tracks = tracks;
    }
    
    public int getCount()
    {
        return _tracks.size();
    }

    public Object getItem(int position)
    {
        return _tracks.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) 
    {
        // Item update
        AudioTrack item = (AudioTrack) getItem(position);
        
        View listItem = LayoutInflater.from(_context).inflate(R.layout.item_search_result, parent, false);

        ImageView cover = listItem.findViewById(R.id.albumCover);
        
        if (!item.artCover.isEmpty())
        {
            cover.setImageURI(Uri.parse(Uri.decode(item.artCover)));
        }
        else
        {
            cover.setImageDrawable(parent.getResources().getDrawable(R.drawable.cover_art_none));
        }
        
        TextView title = listItem.findViewById(R.id.title);
        title.setText(item.title);

        TextView albumTitle = listItem.findViewById(R.id.albumTitle);
        albumTitle.setText(item.albumTitle);

        TextView duration = listItem.findViewById(R.id.duration);
        duration.setText(item.duration);
        
        return listItem;
    }
}