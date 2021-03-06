/*
 *  Copyright (C) 2013 Robert Siebert
 *  Copyright (C) 2011 John Törnblom
 *
 * This file is part of TVHGuide.
 *
 * TVHGuide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TVHGuide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TVHGuide.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tvheadend.tvhclient;

import org.tvheadend.tvhclient.interfaces.ActionBarInterface;
import org.tvheadend.tvhclient.model.Channel;
import org.tvheadend.tvhclient.model.Program;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class ProgramDetailsActivity extends ActionBarActivity implements ActionBarInterface {

    @SuppressWarnings("unused")
    private final static String TAG = ProgramDetailsActivity.class.getSimpleName();

    private ActionBar actionBar = null;
    private Program program;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(Utils.getThemeId(this));
    	super.onCreate(savedInstanceState);

    	// Get the selected channel
    	final long channelId = getIntent().getLongExtra("channelId", 0);
        TVHClientApplication app = (TVHClientApplication) getApplication();
        Channel channel = app.getChannel(channelId);
        if (channel == null) {
            finish();
            return;
        }
        // Get the selected program from the current channel
        final long eventId = getIntent().getLongExtra("eventId", 0);
        for (Program p : channel.epg) {
            if (p.id == eventId) {
                program = p;
                break;
            }
        }
        // Exit if no program was found.
        if (program == null) {
            finish();
            return;
        }

        // Setup the action bar and show the title
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    	// Show the fragment
        Bundle args = new Bundle();
        args.putLong("channelId", channelId);
        args.putLong("eventId", eventId);
        Fragment fragment = Fragment.instantiate(this, ProgramDetailsFragment.class.getName());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void setActionBarTitle(final String title, final String tag) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setActionBarSubtitle(final String subtitle, final String tag) {
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public void setActionBarIcon(Channel channel, String tag) {
        if (actionBar != null && channel != null) {
            // Show or hide the channel icon if required
            boolean showIcon = Utils.showChannelIcons(this);
            actionBar.setDisplayUseLogoEnabled(showIcon);
            if (showIcon) {
                actionBar.setIcon(new BitmapDrawable(getResources(), channel.iconBitmap));
            }
        }
    }
}
