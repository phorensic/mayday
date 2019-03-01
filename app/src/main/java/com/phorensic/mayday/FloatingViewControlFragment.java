package com.phorensic.mayday;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

// FloatingView main fragment
public class FloatingViewControlFragment extends Fragment {

    // TAG for debugging purposes
    // Never used??
    private static final String TAG = "FloatingViewControl";

    // Permission code for the overlay
    private static final int CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE = 100;

    // Find and omit all references to the CUSTOM_OVERLAY
    //private static final int CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE = 101;

    // Generate the FloatingViewControlFragment
    public static FloatingViewControlFragment newInstance() {
        final FloatingViewControlFragment fragment = new FloatingViewControlFragment();
        return fragment;
    }

    // Constructor
    public FloatingViewControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_floating_view_control, container, false);
        // View the demo, in this case this is the real production thing,
        // change references everywhere to replace word "demo"
        rootView.findViewById(R.id.show_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloatingView(getActivity(), true, false);
            }
        });
        
        // 設定画面の表示
        rootView.findViewById(R.id.show_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, FloatingViewSettingsFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rootView;
    }

    /**
     * オーバレイ表示の許可を処理します。
     */
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE) {
            showFloatingView(getActivity(), false, false);
        } else if (requestCode == CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE) {
            showFloatingView(getActivity(), false, true);
        }
    }

    /**
     * FloatingViewの表示
     *
     * @param context                 Context
     * @param isShowOverlayPermission 表示できなかった場合に表示許可の画面を表示するフラグ
     * @param isCustomFloatingView    If true, it launches CustomFloatingViewService.
     */
    @SuppressLint("NewApi")
    private void showFloatingView(Context context, boolean isShowOverlayPermission, boolean isCustomFloatingView) {
        // API22以下かチェック
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startFloatingViewService(getActivity(), isCustomFloatingView);
            return;
        }

        // 他のアプリの上に表示できるかチェック
        if (Settings.canDrawOverlays(context)) {
            startFloatingViewService(getActivity(), isCustomFloatingView);
            return;
        }

        // オーバレイパーミッションの表示
        if (isShowOverlayPermission) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, isCustomFloatingView ? CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE : CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Start floating view service
     *
     * @param activity             {@link Activity}
     * @param isCustomFloatingView If true, it launches CustomFloatingViewService.
     */
    private static void startFloatingViewService(Activity activity, boolean isCustomFloatingView) {
        // *** You must follow these rules when obtain the cutout(FloatingViewManager.findCutoutSafeArea) ***
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 1. 'windowLayoutInDisplayCutoutMode' do not be set to 'never'
            if (activity.getWindow().getAttributes().layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
                throw new RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to 'never'");
            }
            // 2. Do not set Activity to landscape
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                throw new RuntimeException("Do not set Activity to landscape");
            }
        }

        // launch service
        final Class<? extends Service> service;
        final String key;
        if (isCustomFloatingView) {
            service = CustomFloatingViewService.class;
            key = CustomFloatingViewService.EXTRA_CUTOUT_SAFE_AREA;
        } else {
            service = ChatHeadService.class;
            key = ChatHeadService.EXTRA_CUTOUT_SAFE_AREA;
        }
        final Intent intent = new Intent(activity, service);
        intent.putExtra(key, FloatingViewManager.findCutoutSafeArea(activity));
        ContextCompat.startForegroundService(activity, intent);
    }
}
