package com.phorensic.mayday;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.DataOutputStream;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class ChatHeadService extends Service implements FloatingViewListener {

    // TAG for debugging purposes
    private static final String TAG = "ChatHeadService";

    // Intent key (Cutout safe area)
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";

    // Notification ID
    public static final int NOTIFICATION_ID = 9083150;

    // FloatingViewManager
    private FloatingViewManager mFloatingViewManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do nothing if Manager already exists
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        // Important 5 lines below
        // Setting the View to be displayed in the FloatingView
        final LayoutInflater inflater = LayoutInflater.from(this);
        final ImageView iconView = (ImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, getString(R.string.chathead_click_message));
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    dataOutputStream.writeBytes("settings put global airplane_mode_on 1\n");
                    dataOutputStream.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE\n");
                    Log.d(TAG, "Airplane mode ON");
                    dataOutputStream.writeBytes("settings put global airplane_mode_on 0\n");
                    dataOutputStream.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE\n");
                    Log.d(TAG, "Airplane mode OFF");
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    process.waitFor();
                }
                catch (Exception e) {
                    Log.d(TAG, "Exception: "+e);
                }
            }
        });

        // Use the FloatingViewManager, make the setting of FloatingView
        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);
        mFloatingViewManager.addViewToWindow(iconView, options);

        // Activation, startup
        startForeground(NOTIFICATION_ID, createNotification(this));

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();
        Log.d(TAG, getString(R.string.finish_deleted));
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (isFinishing) {
            Log.d(TAG, getString(R.string.deleted_soon));
        } else {
            Log.d(TAG, getString(R.string.touch_finished_position, x, y));
        }
    }

    // Kill View
    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }

    /**
     * Display Notification
     * There is no action when you click
     */
    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_floatingview_channel_id));
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.chathead_content_title));
        builder.setContentText(context.getString(R.string.content_text));
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN); // Backwards compat 7.1 and lower
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }
}
