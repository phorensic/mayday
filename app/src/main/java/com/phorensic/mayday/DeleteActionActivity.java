package com.phorensic.mayday;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Screen to perform delete action after starting from notification
public class DeleteActionActivity extends AppCompatActivity {

    // Tag of setting fragment
    private static final String FRAGMENT_TAG_DELETE_ACTION = "delete_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_action);

        if (savedInstanceState == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, DeleteActionFragment.newInstance(), FRAGMENT_TAG_DELETE_ACTION);
            ft.commit();
        }

    }
}
