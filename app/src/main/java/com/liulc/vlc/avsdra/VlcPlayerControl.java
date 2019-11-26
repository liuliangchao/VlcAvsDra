package com.liulc.vlc.avsdra;

import android.content.Context;
import android.net.Uri;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import static com.liulc.vlc.LogUtils.logld;

/**
 * @author Liulc
 * @version 1.0
 * @date 2019/11/25
 */

public class VlcPlayerControl {
	private static final String TAG = "liulc";

	private LibVLC libvlc;
	private MediaPlayer mMediaPlayer;

	public VlcPlayerControl(Context context) {
		ArrayList<String> options = new ArrayList<>();
		options.add("--aout=opensles");
		options.add("--audio-time-stretch");
		options.add("--audio-resampler=soxr");
		options.add("--avcodec-skiploopfilter=1");
		options.add("--avcodec-skip-frame=0");
		options.add("--avcodec-skip-idct=0");
		options.add("--udp-timeout=5");
		options.add("-vv");

		libvlc = new LibVLC(context, options);
		mMediaPlayer = new MediaPlayer(libvlc);
		mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
			@Override
			public void onEvent(MediaPlayer.Event event) {
				onEventNative(event);
			}
		});
	}
	
	public void attachView(VlcPlayerView vlcPlayerView){
		vlcPlayerView.attach(mMediaPlayer);
	}

	public void load(final String url) {
		Media media = new Media(libvlc, Uri.parse(url));
		mMediaPlayer.setMedia(media);
		media.release();
	}
	
	public void play() {
		mMediaPlayer.play();
	}

	public void stop(){
		mMediaPlayer.stop();
	}

	public void release(){
		mMediaPlayer.stop();
		libvlc.release();
	}

	private void onEventNative(final MediaPlayer.Event event) {
		switch (event.type) {
			case MediaPlayer.Event.Stopped:
				logld(TAG, "Stopped");
				break;
			case MediaPlayer.Event.EndReached:
				logld(TAG, "EndReached");
				break;
			case MediaPlayer.Event.EncounteredError:
				logld(TAG, "EncounteredError");
				break;
			case MediaPlayer.Event.Opening:
				logld(TAG, "Opening");
				break;
			case MediaPlayer.Event.Playing:
				logld(TAG, "Playing");
				break;
			case MediaPlayer.Event.Paused:
				logld(TAG, "Paused");
				break;
			case MediaPlayer.Event.TimeChanged:
				// logld(TAG, "TimeChanged: " + event.getTimeChanged());
				break;
			case MediaPlayer.Event.PositionChanged:
				// logld(TAG, "PositionChanged: " + event.getPositionChanged());
				break;
			case MediaPlayer.Event.Vout:
				logld(TAG, "Vout Count: " + event.getVoutCount());
				break;
			case MediaPlayer.Event.ESAdded:
				logld(TAG, "ESAdded: " + event.getEsChangedType());
				break;
			case MediaPlayer.Event.ESDeleted:
				logld(TAG, "ESDeleted: " + event.getEsChangedType());
				break;
			case MediaPlayer.Event.SeekableChanged:
				logld(TAG, "SeekableChanged");
				break;
			case MediaPlayer.Event.PausableChanged:
				logld(TAG, "PausableChanged");
				break;
			case MediaPlayer.Event.Buffering:
				if (event.getBuffering() == 100f) {
					logld(TAG, "MediaPlayer.Event.Buffering: " + event.getBuffering());
				}
				break;
			case MediaPlayer.Event.MediaChanged:
				logld(TAG, "MediaChanged: " + event.getEsChangedType());
				break;
			default:
				logld(TAG, "event.type: " + event.type);
				break;
		}
	}
	
}
