package ie.itsakettle.piccolo.Settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by wtr on 28/03/15.
 * Got this from here:
 * http://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 */
public class SettingsTimeDialog extends DialogPreference{

    private TimePicker timepicker;
    private int prevHour;
    private int prevMinute;

    public static int getHour(String t)
    {
        String[] pieces  = t.split(":");
        return(Integer.parseInt(pieces[1]));
    }

    public static int getMinute(String t)
    {
        String[] pieces  = t.split(":");
        return(Integer.parseInt(pieces[1]));
    }

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

        timepicker.setCurrentHour(prevHour);
        timepicker.setCurrentMinute(prevMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            prevHour=timepicker.getCurrentHour();
            prevMinute=timepicker.getCurrentMinute();

            String time=String.valueOf(prevHour)+":"+String.valueOf(prevMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("20:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        prevHour=getHour(time);
        prevMinute=getMinute(time);
    }

}
