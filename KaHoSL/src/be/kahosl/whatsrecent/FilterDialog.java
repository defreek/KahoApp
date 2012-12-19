package be.kahosl.whatsrecent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class FilterDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private String[] type = { "Geen", "Mededeling", "Inhoud", "Taak" };
    private int selected = 0;
    private WhatsRecentListFragment fragment;

    public static FilterDialog newInstance(WhatsRecentListFragment fragment) {
    	FilterDialog dialog = new FilterDialog(fragment);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private FilterDialog(WhatsRecentListFragment fragment) {
    	this.fragment = fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter");
        builder.setNegativeButton("Annuleren", this);
        builder.setSingleChoiceItems(type, selected, new SingleChoiceListener());
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
    }

    private class SingleChoiceListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
        	selected = item;
        	
        	OnCloseListDialogListener act = (OnCloseListDialogListener) fragment;
            act.onDialogListSelection();
            dismiss();
        }
    }
    
    public interface OnCloseListDialogListener {
        public void onDialogListSelection();
    }
    
    public String getSelectedType() {
    	return type[selected];
    }
}