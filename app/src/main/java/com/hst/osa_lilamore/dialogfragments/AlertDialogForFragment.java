package com.hst.osa_lilamore.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.utils.OSAConstants;


/**
 * Created by Admin on 17-10-2017.
 */

public class AlertDialogForFragment extends DialogFragment {

    private int tag;
    DialogClickListener dialogActions;

    public static AlertDialogForFragment newInstance(int title, String message) {
        AlertDialogForFragment frag = new AlertDialogForFragment();
        Bundle args = new Bundle();
        args.putInt(OSAConstants.ALERT_DIALOG_TITLE, title);
        args.putString(OSAConstants.ALERT_DIALOG_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    public static AlertDialogForFragment newInstance(int title, String message, int tag) {
        AlertDialogForFragment frag = new AlertDialogForFragment();
        Bundle args = new Bundle();
        args.putInt(OSAConstants.ALERT_DIALOG_TITLE, title);
        args.putString(OSAConstants.ALERT_DIALOG_MESSAGE, message);
        args.putInt(OSAConstants.ALERT_DIALOG_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    public void setDialogListener(DialogClickListener dialogActions) {
        this.dialogActions = dialogActions;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String message = args.getString(OSAConstants.ALERT_DIALOG_MESSAGE, "");
        int title = args.getInt(OSAConstants.ALERT_DIALOG_TITLE);
        tag = args.getInt(OSAConstants.ALERT_DIALOG_TAG, 0);

        return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.alertDialogueTheme))
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_button_ok, mListener).create();
    }

    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                dialog.cancel();
                dialogActions.onAlertPositiveClicked(tag);

            } else {
                dialog.cancel();
                dialogActions.onAlertNegativeClicked(tag);
            }
        }

    };
}
