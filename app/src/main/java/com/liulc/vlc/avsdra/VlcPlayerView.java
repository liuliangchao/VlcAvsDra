package com.liulc.vlc.avsdra;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

/**
 * @author Liulc
 * @version 1.0
 * @date 2019/11/25
 */

public class VlcPlayerView extends SurfaceView implements IVLCVout.OnNewVideoLayoutListener {
	private MediaPlayer mMediaPlayer;

	private View.OnLayoutChangeListener onLayoutChangeListener = null;
	private Handler handler = new Handler();

	private int mVideoHeight = 0;
	private int mVideoWidth = 0;
	private int mVideoVisibleHeight = 0;
	private int mVideoVisibleWidth = 0;
	private int mVideoSarNum = 0;
	private int mVideoSarDen = 0;

	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_SCREEN = 1;
	private static final int SURFACE_FILL = 2;
	private static final int SURFACE_16_9 = 3;
	private static final int SURFACE_4_3 = 4;
	private static final int SURFACE_ORIGINAL = 5;
	private static int CURRENT_SIZE = SURFACE_BEST_FIT;

	public VlcPlayerView(Context context) {
		super(context);
	}

	public VlcPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VlcPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void attach(MediaPlayer mediaPlayer){
		mMediaPlayer = mediaPlayer;
		
		getHolder().setKeepScreenOn(true);
		IVLCVout ivlcVout = mMediaPlayer.getVLCVout();
		ivlcVout.setVideoView(this);
		ivlcVout.attachViews();

		if (onLayoutChangeListener == null) {
			onLayoutChangeListener = new View.OnLayoutChangeListener() {
				private final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						updateVideoSurfaces();
					}
				};

				@Override
				public void onLayoutChange(View view, int left, int top, int right,
				                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
						handler.removeCallbacks(runnable);
						handler.post(runnable);
					}
				}
			};
		}
		addOnLayoutChangeListener(onLayoutChangeListener);
	}

	private void changeMediaPlayerLayout(int displayW, int displayH) {
        /* Change the video placement using the MediaPlayer API */
		switch (CURRENT_SIZE) {
			case SURFACE_BEST_FIT:
				mMediaPlayer.setAspectRatio(null);
				mMediaPlayer.setScale(0);
				break;
			case SURFACE_FIT_SCREEN:
			case SURFACE_FILL: {
				Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
				if (vtrack == null)
					return;
				final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
						|| vtrack.orientation == Media.VideoTrack.Orientation.RightTop;
				if (CURRENT_SIZE == SURFACE_FIT_SCREEN) {
					int videoW = vtrack.width;
					int videoH = vtrack.height;

					if (videoSwapped) {
						int swap = videoW;
						videoW = videoH;
						videoH = swap;
					}
					if (vtrack.sarNum != vtrack.sarDen)
						videoW = videoW * vtrack.sarNum / vtrack.sarDen;

					float ar = videoW / (float) videoH;
					float dar = displayW / (float) displayH;

					float scale;
					if (dar >= ar)
						scale = displayW / (float) videoW; /* horizontal */
					else
						scale = displayH / (float) videoH; /* vertical */
					mMediaPlayer.setScale(scale);
					mMediaPlayer.setAspectRatio(null);
				} else {
					mMediaPlayer.setScale(0);
					mMediaPlayer.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
							: "" + displayH + ":" + displayW);
				}
				break;
			}
			case SURFACE_16_9:
				mMediaPlayer.setAspectRatio("16:9");
				mMediaPlayer.setScale(0);
				break;
			case SURFACE_4_3:
				mMediaPlayer.setAspectRatio("4:3");
				mMediaPlayer.setScale(0);
				break;
			case SURFACE_ORIGINAL:
				mMediaPlayer.setAspectRatio(null);
				mMediaPlayer.setScale(1);
				break;
		}
	}

	private void updateVideoSurfaces() {
		int sw = getWidth();
		int sh = getHeight();

		// sanity check
		if (sw * sh == 0) {
			return;
		}

		mMediaPlayer.getVLCVout().setWindowSize(sw, sh);
		ViewGroup.LayoutParams lp = getLayoutParams();
		if (mVideoWidth * mVideoHeight == 0) {
            /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
			lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
			lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
			setLayoutParams(lp);
			changeMediaPlayerLayout(sw, sh);
			return;
		}

		if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            /* We handle the placement of the video using Android View LayoutParams */
			mMediaPlayer.setAspectRatio(null);
			mMediaPlayer.setScale(0);
		}

		double dw = sw, dh = sh;
		final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

		if (sw > sh && isPortrait || sw < sh && !isPortrait) {
			dw = sh;
			dh = sw;
		}

		// compute the aspect ratio
		double ar, vw;
		if (mVideoSarDen == mVideoSarNum) {
            /* No indication about the density, assuming 1:1 */
			vw = mVideoVisibleWidth;
			ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
		} else {
            /* Use the specified aspect ratio */
			vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
			ar = vw / mVideoVisibleHeight;
		}

		// compute the display aspect ratio
		double dar = dw / dh;

		switch (CURRENT_SIZE) {
			case SURFACE_BEST_FIT:
				if (dar < ar)
					dh = dw / ar;
				else
					dw = dh * ar;
				break;
			case SURFACE_FIT_SCREEN:
				if (dar >= ar)
					dh = dw / ar; /* horizontal */
				else
					dw = dh * ar; /* vertical */
				break;
			case SURFACE_FILL:
				break;
			case SURFACE_16_9:
				ar = 16.0 / 9.0;
				if (dar < ar)
					dh = dw / ar;
				else
					dw = dh * ar;
				break;
			case SURFACE_4_3:
				ar = 4.0 / 3.0;
				if (dar < ar)
					dh = dw / ar;
				else
					dw = dh * ar;
				break;
			case SURFACE_ORIGINAL:
				dh = mVideoVisibleHeight;
				dw = vw;
				break;
		}

		// set display size
		lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
		lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
		setLayoutParams(lp);

		invalidate();
	}
	
	@Override
	public void onNewVideoLayout(IVLCVout vlcVout, int width, int height,
	                             int visibleWidth, int visibleHeight, int sarNum, int sarDen){
		mVideoWidth = width;
		mVideoHeight = height;
		mVideoVisibleWidth = visibleWidth;
		mVideoVisibleHeight = visibleHeight;
		mVideoSarNum = sarNum;
		mVideoSarDen = sarDen;
		updateVideoSurfaces();
	}
}
