package be.kahosl.whatsrecent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class FilterDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private String[] type = { "Geen", "Mededeling", "Inhoud", "Taak" };
    private int selected = 0;
    private final int ID;

    public static FilterDialog newInstance(int id) {
    	FilterDialog dialog = new FilterDialog(id);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private FilterDialog(int id) {
    	this.ID = id;
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
        builder.setNegativeButton("Cancel", this);
        builder.setSingleChoiceItems(type, selected, new SingleChoiceListener());
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
    }

    private class SingleChoiceListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
        	selected = item;
        	
        	OnCloseListDialogListener act = (OnCloseListDialogListener) getFragmentManager().findFragmentById(ID);
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