package be.kahosl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

	public AlertDialogFragment() {
	}

	public static AlertDialogFragment newInstance(String message) {
		AlertDialogFragment d = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        d.setArguments(args);
        return d;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Fout")
                .setMessage(message)
                .create();
    }
}