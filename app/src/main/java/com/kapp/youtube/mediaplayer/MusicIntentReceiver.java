package com.kapp.youtube.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import com.kapp.youtube.service.PlaybackService;

import org.videolan.libvlc.util.JNILib;

/**
 * Receives broadcasted intents. In particular, we are interested in the
 * android.media.AUDIO_BECOMING_NOISY and android.intent.action.MEDIA_BUTTON intents, which is
 * broadcast, for example, when the user disconnects the headphones. This class works because we are
 * declaring it in a &lt;receiver&gt; tag in AndroidManifest.xml.
 */
public class MusicIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!JNILib.checkJNILibs()) {
            Toast.makeText(context,
                    "Media codec file not found, open App to download it.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent service = new Intent(context, PlaybackService.class);
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            //Toast.makeText(context, "Headphones disconnected.", Toast.LENGTH_SHORT).show();
            // send an intent to our MusicService to telling it to pause the audio
            service.setAction(PlaybackService.ACTION_PAUSE);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    service.setAction(PlaybackService.ACTION_TOGGLE_PLAYBACK);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    service.setAction(PlaybackService.ACTION_PLAY);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    service.setAction(PlaybackService.ACTION_PAUSE);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    service.setAction(PlaybackService.ACTION_STOP);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    service.setAction(PlaybackService.ACTION_SKIP);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    service.setAction(PlaybackService.ACTION_PREVIOUS);
                    break;
                default:
                    return;
            }
        } else
            return;
        context.startService(service);
    }
}
