package com.phorensic.mayday;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// FloatingView is a fragment to delete the sample service.
public class DeleteActionFragment extends Fragment {

    /**
     * Creates a DeleteActionFragment.
     *
     * @return DeleteActionFragment
     */
    public static DeleteActionFragment newInstance() {
        final DeleteActionFragment fragment = new DeleteActionFragment();
        return fragment;
    }

    // Constructor
    public DeleteActionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delete_action, container, false);
        // Delete button
        final View clearFloatingButton = rootView.findViewById(R.id.clearDemo);
        clearFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Easy way to delete a service
                final Activity activity = getActivity();
                activity.stopService(new Intent(activity, ChatHeadService.class));
            }
        });
        return rootView;
    }
}
