package ie.itsakettle.piccolo.Settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by wtr on 28/03/15.
 */
public class SettingsTimeDialog extends DialogPreference{

    private TimePicker timepicker;
    private 

    public SettingsTimeDialog(Context c, AttributeSet att)
    {
        super(c, att);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

    }


    @Override
    protected View onCreateDialogView() {
        timepicker=new TimePicker(getContext());

        return(timepicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        timepicker.setCurrentHour(lastHour);
        timepicker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }



}
