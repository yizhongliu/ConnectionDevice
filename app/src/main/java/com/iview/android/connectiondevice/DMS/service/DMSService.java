package com.iview.android.connectiondevice.DMS.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.iview.android.connectiondevice.DMS.presenter.FragmentAudioPre;
import com.iview.android.connectiondevice.DMS.presenter.FragmentImagePre;
import com.iview.android.connectiondevice.DMS.presenter.FragmentVideoPre;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.upnp.std.av.server.object.format.GIFFormat;
import org.cybergarage.upnp.std.av.server.object.format.ID3Format;
import org.cybergarage.upnp.std.av.server.object.format.JPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.MPEGFormat;
import org.cybergarage.upnp.std.av.server.object.format.PNGFormat;

public class DMSService extends Service {
    private final static String TAG = "DMSService";

    private MediaServer mediaServer;

    private static DMSService sDMSService = null;

    public static DMSService getInstance() {
        if (sDMSService == null) {
            synchronized (DMSService.class) {
                if (sDMSService == null) {
                    sDMSService = new DMSService();
                }
            }
        }
        return sDMSService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unInit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMediaServer();
        return super.onStartCommand(intent, flags, startId);

    }

    private void init() {

    }

    private void unInit() {
        stopMediaServer();
    }

    public void startMediaServer() {
        new Thread() {
            public void run() {
                try {
                    mediaServer = new MediaServer(getApplication());

                    mediaServer.addPlugIn(new JPEGFormat());
                    mediaServer.addPlugIn(new PNGFormat());
                    mediaServer.addPlugIn(new GIFFormat());
                    mediaServer.addPlugIn(new MPEGFormat());
                    mediaServer.addPlugIn(new ID3Format());
                 //   mediaServer.addPlugIn(new MP3Format());

                    mediaServer.addContentDirectory(
                            new FileDirectory("Image",
                                    FragmentImagePre.getImagePathList(getApplication())));
                    mediaServer.addContentDirectory(
                            new FileDirectory("Video",
                                    FragmentVideoPre.getVideoPathList(getApplication())));
                    mediaServer.addContentDirectory(
                            new FileDirectory("Audio",
                                    FragmentAudioPre.getAudioPathList(getApplication())));

                    mediaServer.start();
                } catch (InvalidDescriptionException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void stopMediaServer() {
        new Thread() {
            public void run() {
                if(null != mediaServer) {
                    mediaServer.stop();
                }
            }
        }.start();
    }
}
