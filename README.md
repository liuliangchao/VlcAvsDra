### VlcAvsDra

Support AVS+ and DRA decoding

支持AVS+和DRA解码

使用[vlc-android](https://code.videolan.org/videolan/vlc-android) 重新编译打包，可以进行二次开发。

### 所使用的vlc-android版本

1. git clone https://github.com/videolan/vlc-android.git
2. git checkout 3b370d9

### 如何使用

```
<com.liulc.vlc.VlcPlayerView
	android:id="@+id/vlcPlayerView"
	android:layout_width="wrap_content"
	android:layout_height="wrap_content" />
	
	VlcPlayerView vlcPlayerView = (VlcPlayerView) findViewById(R.id.vlcPlayerView);
	VlcPlayerControl vlcPlayerControl = new VlcPlayerControl(this);

	vlcPlayerControl.attachView(vlcPlayerView);
//	vlcPlayerControl.load("http://192.168.9.222/dra.ts");
	vlcPlayerControl.load("file:///" + Environment.getExternalStorageDirectory().getPath() +"/dra.ts");
	vlcPlayerControl.play();
```

### DRA测试TS文件

![dra.ts](others/dra.ts)

### 注意

##### so库是提取而来，另附提取的APK。

![VLC-Android-3.0.11-ARMv7_avs+dra.apk](others/apk/VLC-Android-3.0.11-ARMv7_avs+dra.apk)

![VLC-Android-3.0.11-ARMv8_avs+dra.apk](others/apk/VLC-Android-3.0.11-ARMv8_avs+dra.apk)
