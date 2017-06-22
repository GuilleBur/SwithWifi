package com.ar.gab.switchwifi.activity;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


import com.ar.gab.switchwifi.R;

import utils.ServiceUtil;

/**
 * Created by Guille on 7/6/2017.
 */

public class AboutFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.message_about)
                .setTitle(R.string.title_about)
                .setPositiveButton(R.string.dismiss_about, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ServiceUtil.showToast("Bye, good luck!", getActivity());
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}