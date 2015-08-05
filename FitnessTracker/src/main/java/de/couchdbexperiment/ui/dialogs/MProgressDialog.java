package de.couchdbexperiment.ui.dialogs;

import de.couchdbexperiment.MainCouchDbActivity;

/**
 * Created by skip on 05.06.2015.
 */
public class MProgressDialog extends android.app.ProgressDialog{

    public MProgressDialog(MainCouchDbActivity context, String message){
        super(context);
        this.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        this.setMessage(message);
        this.setIndeterminate(true);
        this.setCanceledOnTouchOutside(false);
    }
}
